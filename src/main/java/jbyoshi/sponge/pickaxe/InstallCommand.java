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

    @Command(aliases = "install", permission = "pickaxe.command")
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
