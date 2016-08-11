package jbyoshi.sponge.pickaxe;

import jbyoshi.sponge.pickaxe.command.Alias;
import jbyoshi.sponge.pickaxe.command.Command;
import jbyoshi.sponge.pickaxe.command.Source;
import jbyoshi.sponge.pickaxe.ore.Download;
import jbyoshi.sponge.pickaxe.ore.OreProject;
import jbyoshi.sponge.pickaxe.ore.OreRepository;
import jbyoshi.sponge.pickaxe.ore.OreVersion;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandSource;

import java.io.IOException;
import java.util.Optional;

public final class InstallCommand {
    private final PickaxePlugin plugin;
    private final OreRepository ore;
    private final PluginInstaller installer;

    InstallCommand(PickaxePlugin plugin, OreRepository ore, PluginInstaller installer) {
        this.plugin = plugin;
        this.ore = ore;
        this.installer = installer;
    }

    @Command("install")
    public void install(@Source CommandSource src, @Alias("plugin") String plugin) throws CommandException {
        String[] parts = plugin.split("/");
        if (parts.length > 2) {
            throw new CommandException(Messages.invalidPluginId(plugin));
        }
        src.sendMessage(Messages.CONNECTING);
        Sponge.getScheduler().createTaskBuilder().async().execute(() -> {
            try {
                Optional<? extends OreProject> projectOpt;
                switch (parts.length) {
                    case 1:
                        projectOpt = ore.findProjectById(parts[0]);
                        break;
                    case 2:
                        projectOpt = ore.findProjectByOwnerAndName(parts[0], parts[1]);
                        break;
                    default:
                        throw new AssertionError();
                }
                if (!projectOpt.isPresent()) {
                    src.sendMessage(Messages.notFound(plugin));
                    return;
                }
                OreProject project = projectOpt.get();
                OreVersion version = project.getRecommendedVersion();
                Optional<Download> download = version.download();
                if (download.isPresent()) {
                    src.sendMessage(Messages.preInstall(version));
                    YesCommand.put(src, () -> {
                        try {
                            src.sendMessage(Messages.downloading(version));
                            installer.installAsync(version, download.get());
                            src.sendMessage(Messages.install(version));
                            src.sendMessage(Messages.INSTALL_RESTART);
                        } catch (IOException e) {
                            this.plugin.printException(src, e);
                        }
                    });
                } else {
                    src.sendMessage(Messages.notOnOre(project));
                }
            } catch (IOException e) {
                this.plugin.printException(src, e);
            }
        }).submit(this.plugin);
    }
}
