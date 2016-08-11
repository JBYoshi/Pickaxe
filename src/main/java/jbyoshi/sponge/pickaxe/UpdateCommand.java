/*
 * Copyright (c) 2016 JBYoshi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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

    @Command(aliases = {"update", "upgrade"}, permission = "pickaxe.command")
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
