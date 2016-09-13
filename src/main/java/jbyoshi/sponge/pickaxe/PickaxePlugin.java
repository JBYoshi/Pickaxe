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

import jbyoshi.sponge.pickaxe.command.CommandHelper;
import jbyoshi.sponge.pickaxe.ore.CombinedOreRepository;
import jbyoshi.sponge.pickaxe.ore.OreRepository;
import jbyoshi.sponge.pickaxe.ore.local.LocalOreRepository;
import jbyoshi.sponge.pickaxe.ore.remote.OreRepositoryImpl;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.dispatcher.SimpleDispatcher;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import javax.inject.Inject;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@Plugin(id = "pickaxe", name = "Pickaxe", version = "1.1",
        description = "A Sponge plugin manager and unofficial Ore client.", authors = "JBYoshi",
        url = "https://ore-staging.spongepowered.org/JBYoshi/Pickaxe")
public final class PickaxePlugin {
    private static final OreRepository ORE = new CombinedOreRepository(
            new OreRepositoryImpl("https://ore-staging.spongepowered.org"),
            LocalOreRepository.INSTANCE);

    @Inject
    private Logger logger;
    private PluginInstaller installer;

    @Listener
    public void initialize(GameInitializationEvent event) {
        installer = new PluginInstaller(Sponge.getPluginManager()
                .fromInstance(this).orElseThrow(() -> new AssertionError("Apparently I'm not a plugin?!"))
                .getSource().filter(Files::isRegularFile).map(Path::getParent).orElseGet(() -> {
                    // Try reflection to access the Sponge configuration.
                    try {
                        return (Path) Class.forName("org.spongepowered.common.launch.SpongeLaunch")
                                .getDeclaredMethod("getPluginsDir").invoke(null);
                    } catch (NoSuchMethodException | ClassNotFoundException | InvocationTargetException
                            | IllegalAccessException e) {
                        throw new AssertionError("Can't find the plugins directory", e);
                    }
                }));
        SimpleDispatcher dispatcher = new SimpleDispatcher();
        CommandHelper.register(new StatusCommand(installer), dispatcher);
        CommandHelper.register(new InstallCommand(this, ORE, installer), dispatcher);
        CommandHelper.register(new UninstallCommand(this, ORE, installer), dispatcher);
        CommandHelper.register(new UpdateCommand(this, ORE, installer), dispatcher);
        CommandHelper.register(new YesCommand(this), dispatcher);
        Sponge.getCommandManager().register(this, dispatcher, "pickaxe", "ore");
    }

    @Listener
    public void finishPluginInstallation(GameStoppedEvent event) {
        try {
            installer.execute();
        } catch (IOException e) {
            logger.error("Could not finalize plugin installation and uninstallation: " + e);
        }
    }

    @Listener
    public void sendSupersededMessage(GameStartedServerEvent event) {
        sendSupersededMessage(Sponge.getServer().getConsole());
    }

    @Listener
    public void maybeSendSupersededMessage(ClientConnectionEvent.Join event) {
        if (event.getTargetEntity().hasPermission("pickaxe.command")) {
            sendSupersededMessage(event.getTargetEntity());
        }
    }

    private void sendSupersededMessage(CommandSource target) {
        try {
            target.sendMessage(Text.of(TextColors.BLUE, "Pickaxe has been superseded by an ",
                    Text.of(TextActions.openUrl(new URL("https://ore-staging.spongepowered.org/windy/Ore")),
                            TextActions.showText(Text.of("View on Ore")),
                            TextColors.AQUA, TextStyles.UNDERLINE, "official Ore client plugin"), "."));
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
        target.sendMessage(Text.of(TextColors.BLUE, "The official plugin works with the ",
                TextColors.AQUA, "same commands as Pickaxe", TextColors.BLUE, " (except you need to use /ore instead of /pickaxe), but has ",
                TextColors.AQUA, "many more features", TextColors.BLUE, " and is mantained by the ",
                TextColors.AQUA, "same developers as the Ore website", TextColors.BLUE, "."));
        target.sendMessage(Text.of(TextColors.BLUE, "To install it, run the following commands:"));
        target.sendMessage(Text.of(TextColors.AQUA, "    /pickaxe install ore"));
        target.sendMessage(Text.of(TextColors.AQUA, "    /pickaxe uninstall pickaxe"));
        target.sendMessage(Text.of(TextColors.BLUE, "Then restart the server."));
        target.sendMessage(Text.of());
        target.sendMessage(Text.of(TextColors.BLUE, "Thanks for using Pickaxe, and here's to a great future with a standard, official Ore plugin!"));
    }

    void printException(MessageReceiver receiver, IOException e) {
        Throwable cause = e;
        while (cause.getCause() != null) cause = cause.getCause();
        if ("unable to find valid certification path to requested target".equals(cause.getMessage())) {
            receiver.sendMessage(Text.of(TextColors.RED, "Could not connect to Ore: SSL certificate not registered."));
            receiver.sendMessage(Text.of(TextColors.RED, "Look up 'java unable to find valid certification path to requested target' for details on how to solve this."));
        } else {
            receiver.sendMessage(Text.of(TextColors.RED, "Could not connect to Ore: " + e.getMessage()));
            logger.error("Could not connect to Ore", e);
        }
    }
}
