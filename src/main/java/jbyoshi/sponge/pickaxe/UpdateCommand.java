package jbyoshi.sponge.pickaxe;

import jbyoshi.sponge.pickaxe.command.Alias;
import jbyoshi.sponge.pickaxe.command.AllRemaining;
import jbyoshi.sponge.pickaxe.command.Command;
import jbyoshi.sponge.pickaxe.command.Source;
import jbyoshi.sponge.pickaxe.ore.Download;
import jbyoshi.sponge.pickaxe.ore.OreProject;
import jbyoshi.sponge.pickaxe.ore.OreRepository;
import jbyoshi.sponge.pickaxe.ore.OreVersion;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.plugin.PluginContainer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public final class UpdateCommand {
    private final PickaxePlugin plugin;
    private final OreRepository repository;
    private final PluginInstaller installer;

    UpdateCommand(PickaxePlugin plugin, OreRepository repository, PluginInstaller installer) {
        this.plugin = plugin;
        this.repository = repository;
        this.installer = installer;
    }

    @Command({"update", "upgrade"})
    public void update(@Source CommandSource source, @Alias("plugins") @AllRemaining Set<String> plugins)
            throws CommandException {
        Set<PluginContainer> toUpdate = new HashSet<>();
        if (!plugins.isEmpty()) {
            for (String plugin : plugins) {
                Optional<PluginContainer> container = Sponge.getPluginManager().getPlugin(plugin);
                if (container.isPresent()) {
                    toUpdate.add(container.get());
                } else {
                    throw new CommandException(Messages.notFound(plugin));
                }
            }
        } else {
            toUpdate.addAll(Sponge.getPluginManager().getPlugins());
        }
        Sponge.getScheduler().createTaskBuilder().async().execute(() -> {
            boolean updated = false;
            for (PluginContainer container : toUpdate) {
                try {
                    Optional<? extends OreProject> projectOpt = repository.findProjectById(container.getId());
                    if (projectOpt.isPresent()) {
                        OreProject project = projectOpt.get();
                        OreVersion version = project.getRecommendedVersion();
                        if (container.getVersion().filter(v -> !version.getVersion().equals(v)).isPresent()) {
                            Optional<Download> download = version.download();
                            if (download.isPresent()) {
                                source.sendMessage(Messages.install(version));
                                installer.installAsync(version, download.get());
                                updated = true;
                            }
                        }
                    }
                } catch (IOException e) {
                    UpdateCommand.this.plugin.printException(source, e);
                }
            }
            source.sendMessage(updated ? Messages.UPDATE_COMPLETED : Messages.NO_UPDATES);
        }).submit(this.plugin);
    }
}
