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

import com.google.common.collect.ImmutableMap;
import jbyoshi.sponge.pickaxe.ore.OreProject;
import jbyoshi.sponge.pickaxe.ore.OrePluginInstallation;
import jbyoshi.sponge.pickaxe.ore.OreVersion;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

final class Messages {
    private static final TextColor INSTALL_COLOR = TextColors.GREEN;
    private static final TextColor UNINSTALL_COLOR = TextColors.RED;
    private static final TextColor UPGRADE_COLOR = TextColors.BLUE;
    private static final TextColor ERROR_COLOR = TextColors.RED;
    private static final TextColor MESSAGE_COLOR = TextColors.RESET;
    private static final String PLATFORM = Sponge.getGame().getPlatform().getType().isServer() ? "the server"
            : "Minecraft";

    private static final TextTemplate STATUS_INSTALL = TextTemplate.of(INSTALL_COLOR, " + ",
            TextTemplate.arg("plugin").build());
    private static final TextTemplate STATUS_UPGRADE = TextTemplate.of(UPGRADE_COLOR, " * ",
            TextTemplate.arg("plugin").build(), " -> ", TextTemplate.arg("newVersion").build());
    private static final TextTemplate STATUS_UNINSTALL = TextTemplate.of(UNINSTALL_COLOR, " - ",
            TextTemplate.arg("plugin").build());
    private static final TextTemplate DOWNLOADING = TextTemplate.of(MESSAGE_COLOR, "Downloading ",
            TextTemplate.arg("plugin").build(), "...");
    private static final TextTemplate INSTALL = TextTemplate.of(MESSAGE_COLOR, TextTemplate.arg("plugin").build(),
            " is ready to ", Text.of(INSTALL_COLOR, "install"), ".");
    private static final TextTemplate UNINSTALL = TextTemplate.of(MESSAGE_COLOR, TextTemplate.arg("plugin").build(),
            " is ready to ", Text.of(UNINSTALL_COLOR, "uninstall"), ".");
    private static final TextTemplate PRE_INSTALL = TextTemplate.of(MESSAGE_COLOR, "Install ",
            TextTemplate.arg("plugin").build(), "?");
    private static final TextTemplate PRE_UNINSTALL = TextTemplate.of(MESSAGE_COLOR, "Uninstall ",
            TextTemplate.arg("plugin").build(), "?");

    private static final TextTemplate INVALID_PLUGIN_ID = TextTemplate.of(ERROR_COLOR, "Invalid plugin ID ",
            TextTemplate.arg("id").build(),
            ": must be a Sponge plugin ID, or an Ore project in the form <owner>/<name>");
    private static final TextTemplate NOT_FOUND = TextTemplate.of(ERROR_COLOR, "Could not find ",
            TextTemplate.arg("id").build(), ". Did you type it correctly?");
    private static final TextTemplate NOT_INSTALLED = TextTemplate.of(ERROR_COLOR, TextTemplate.arg("plugin").build(),
            " is not installed.");
    private static final TextTemplate NOT_ON_ORE = TextTemplate.of(ERROR_COLOR, TextTemplate.arg("plugin").build(),
            " is not published on Ore, so Pickaxe cannot update it.");
    private static final TextTemplate CANNOT_UNINSTALL = TextTemplate.of(ERROR_COLOR, TextTemplate.arg("plugin").build(),
            " cannot be uninstalled because it is not a proper plugin.");

    static final Text CONNECTING = Text.of(MESSAGE_COLOR, "Connecting to Ore...");
    static final Text INSTALL_RESTART = Text.of(MESSAGE_COLOR, "Restart ", PLATFORM, " to finish installation.");
    static final Text UNINSTALL_RESTART = Text.of(MESSAGE_COLOR, "Restart ", PLATFORM, " to finish uninstallation.");
    static final Text TO_INSTALL = Text.of(INSTALL_COLOR, "Plugins to be installed:");
    static final Text TO_UPGRADE = Text.of(UPGRADE_COLOR, "Plugins to be upgraded:");
    static final Text TO_UNINSTALL = Text.of(UNINSTALL_COLOR, "Plugins to be uninstalled:");
    static final Text STATUS_NO_CHANGES = Text.of(MESSAGE_COLOR, "No changes");
    static final Text YES_REQUIRED = Text.of(MESSAGE_COLOR, "Type /pickaxe yes to confirm.");
    static final Text INVALID_YES = Text.of(ERROR_COLOR, "This command is not active!");
    static final Text YES_CANCELLED = Text.of(MESSAGE_COLOR, "Action cancelled.");
    static final Text NO_UPDATES = Text.of(MESSAGE_COLOR, "No updates found.");
    static final Text UPDATE_COMPLETED = Text.of(MESSAGE_COLOR, "Update completed. ", INSTALL_RESTART);

    private Messages() {
    }

    static Text notInstalled(OreProject plugin) {
        return NOT_INSTALLED.apply(ImmutableMap.of("plugin", plugin)).build();
    }

    static Text cannotUninstall(OreProject plugin) {
        return CANNOT_UNINSTALL.apply(ImmutableMap.of("plugin", plugin)).build();
    }

    static Text notFound(String id) {
        return NOT_FOUND.apply(ImmutableMap.of("id", id)).build();
    }

    static Text notOnOre(OreProject plugin) {
        return NOT_ON_ORE.apply(ImmutableMap.of("plugin", plugin)).build();
    }

    static Text invalidPluginId(String id) {
        return INVALID_PLUGIN_ID.apply(ImmutableMap.of("id", id)).build();
    }

    static Text downloading(OreVersion plugin) {
        return DOWNLOADING.apply(ImmutableMap.of("plugin", plugin)).build();
    }

    static Text install(OreVersion plugin) {
        return INSTALL.apply(ImmutableMap.of("plugin", plugin)).build();
    }

    static Text uninstall(OrePluginInstallation plugin) {
        return UNINSTALL.apply(ImmutableMap.of("plugin", plugin)).build();
    }

    static Text statusInstall(OreVersion plugin) {
        return STATUS_INSTALL.apply(ImmutableMap.of("plugin", plugin)).build();
    }

    static Text statusUpgrade(OrePluginInstallation currentVersion, OreVersion newVersion) {
        return STATUS_UPGRADE.apply(ImmutableMap.of("plugin", currentVersion, "newVersion",
                newVersion.getVersion())).build();
    }

    static Text statusUninstall(OrePluginInstallation plugin) {
        return STATUS_UNINSTALL.apply(ImmutableMap.of("plugin", plugin)).build();
    }

    static Text preInstall(OreVersion plugin) {
        return PRE_INSTALL.apply(ImmutableMap.of("plugin", plugin)).build();
    }

    static Text preUninstall(OrePluginInstallation plugin) {
        return PRE_UNINSTALL.apply(ImmutableMap.of("plugin", plugin)).build();
    }
}
