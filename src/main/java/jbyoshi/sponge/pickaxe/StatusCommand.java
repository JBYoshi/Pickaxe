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

import jbyoshi.sponge.pickaxe.command.Command;
import jbyoshi.sponge.pickaxe.command.Source;
import jbyoshi.sponge.pickaxe.ore.OrePluginInstallation;
import jbyoshi.sponge.pickaxe.ore.OreVersion;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class StatusCommand {
    private final PluginInstaller installer;
    StatusCommand(PluginInstaller installer) {
        this.installer = installer;
    }

    @Command(aliases = "status", permission = "pickaxe.command")
    public void status(@Source CommandSource src) {
        Map<String, OreVersion> toInstall = installer.getToInstall().values().stream()
                .collect(Collectors.toMap(e -> e.getProject().getPluginId(), Function.identity()));
        Map<String, OrePluginInstallation> toUninstall = installer.getToUninstall().stream()
                .collect(Collectors.toMap(e -> e.getProject().getPluginId(), Function.identity()));

        Map<OrePluginInstallation, OreVersion> toUpgrade = new HashMap<>();
        for (OreVersion version : new HashSet<>(toInstall.values())) {
            OrePluginInstallation currentVersion = toUninstall.remove(version.getProject().getPluginId());
            if (currentVersion != null) {
                toInstall.remove(version.getProject().getPluginId());
                toUpgrade.put(currentVersion, version);
            }
        }

        if (toInstall.isEmpty() && toUninstall.isEmpty() && toUpgrade.isEmpty()) {
            src.sendMessage(Messages.STATUS_NO_CHANGES);
            return;
        }
        Text ending = Messages.UNINSTALL_RESTART;
        if (!toInstall.isEmpty()) {
            src.sendMessage(Messages.TO_INSTALL);
            ending = Messages.INSTALL_RESTART;
            for (OreVersion version : toInstall.values()) {
                src.sendMessage(Messages.statusInstall(version));
            }
        }
        if (!toUpgrade.isEmpty()) {
            src.sendMessage(Messages.TO_UPGRADE);
            ending = Messages.INSTALL_RESTART;
            for (Map.Entry<OrePluginInstallation, OreVersion> upgrade : toUpgrade.entrySet()) {
                src.sendMessage(Messages.statusUpgrade(upgrade.getKey(), upgrade.getValue()));
            }
        }
        if (!toUninstall.isEmpty()) {
            src.sendMessage(Messages.TO_UNINSTALL);
            for (OrePluginInstallation plugin : toUninstall.values()) {
                src.sendMessage(Messages.statusUninstall(plugin));
            }
        }
        src.sendMessage(ending);
    }
}
