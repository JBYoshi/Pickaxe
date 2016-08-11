package jbyoshi.sponge.pickaxe;

import jbyoshi.sponge.pickaxe.command.Command;
import jbyoshi.sponge.pickaxe.command.Source;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public final class YesCommand {
    private static final Map<CommandSource, Runnable> tasks = Collections.synchronizedMap(new WeakHashMap<>());
    private static PickaxePlugin plugin;

    YesCommand(PickaxePlugin plugin) {
        YesCommand.plugin = plugin;
    }

    @Command("yes")
    public void command(@Source CommandSource source) {
        if (tasks.containsKey(source)) {
            tasks.remove(source).run();
        } else {
            source.sendMessage(Messages.INVALID_YES);
        }
    }

    static void put(CommandSource source, Runnable task) {
        source.sendMessage(Messages.YES_REQUIRED);
        tasks.put(source, task);
        Sponge.getScheduler().createTaskBuilder().execute(() -> {
            if (tasks.remove(source, task)) {
                source.sendMessage(Messages.YES_CANCELLED);
            }
        });
    }
}
