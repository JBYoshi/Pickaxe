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
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public final class YesCommand {
    private static final Map<CommandSource, Runnable> tasks = Collections.synchronizedMap(new WeakHashMap<>());
    private static PickaxePlugin plugin;

    YesCommand(PickaxePlugin plugin) {
        YesCommand.plugin = plugin;
    }

    @Command(aliases = "yes", permission = "pickaxe.command")
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
