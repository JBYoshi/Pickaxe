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
package jbyoshi.sponge.pickaxe.command;

import com.flowpowered.math.vector.Vector3d;
import jbyoshi.sponge.pickaxe.ToArrayCollector;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.*;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.dispatcher.SimpleDispatcher;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.storage.WorldProperties;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public final class CommandHelper {
    private CommandHelper() {
    }

    public static void register(Object plugin, Object obj) {
        register(obj, (command, aliases) -> Sponge.getCommandManager().register(plugin, command, aliases));
    }

    public static void register(Object obj, SimpleDispatcher dispatcher) {
        register(obj, dispatcher::register);
    }

    public static void register(Object obj, BiConsumer<CommandCallable, String[]> register) {
        for (Method method : obj.getClass().getMethods()) {
            Command command = method.getAnnotation(Command.class);
            if (command != null) {
                try {
                    String[] aliases = command.value();
                    List<ParameterElement> arguments = new ArrayList<>();
                    for (Parameter param : method.getParameters()) {
                        try {
                            if (param.getAnnotation(Source.class) != null && param.getType() == CommandSource.class) {
                                arguments.add(new ParameterElement(null, Packer.SINGLE));
                            } else {
                                Alias alias = param.getAnnotation(Alias.class);
                                String name;
                                if (alias == null) {
                                    throw new IllegalArgumentException("Missing alias");
                                }
                                arguments.add(getArgument(param, Text.of(alias.value())));
                            }
                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException("Bad parameter " + (arguments.size() + 1) + ": " + e);
                        }
                    }
                    register.accept(CommandSpec.builder()
                            .arguments(arguments.stream().map(e -> e.element).filter(e -> e != null)
                                    .collect(ToArrayCollector.of(CommandElement.class)))
                            .executor((src, args) -> {
                                try {
                                    Object out = method.invoke(obj, arguments.stream().map(arg -> arg.getValue(src, args))
                                            .collect(ToArrayCollector.of(Object.class)));
                                    if (out == null) {
                                        return CommandResult.success();
                                    }
                                    if (out instanceof Boolean) {
                                        return (Boolean) out ? CommandResult.success() : CommandResult.empty();
                                    }
                                    if (out instanceof CommandResult) {
                                        return (CommandResult) out;
                                    }
                                    if (out instanceof Number) {
                                        return CommandResult.builder().successCount(((Number) out).intValue()).build();
                                    }
                                    return CommandResult.empty();
                                } catch (IllegalAccessException e) {
                                    throw new AssertionError(e);
                                } catch (InvocationTargetException e) {
                                    Throwable cause = e.getCause();
                                    if (cause instanceof CommandException) {
                                        throw (CommandException) cause;
                                    }
                                    if (cause instanceof RuntimeException) {
                                        throw (RuntimeException) cause;
                                    }
                                    if (cause instanceof Error) {
                                        throw (Error) cause;
                                    }
                                    throw new InvocationCommandException(Text.of(e), e);
                                }
                            }).build(), aliases);
                } catch (IllegalArgumentException e) {
                    StringBuilder message = new StringBuilder().append("Bad method ").append(method.getName());
                    message.append('(').append(String.join(", ", Arrays.stream(method.getParameterTypes())
                            .<CharSequence>map(Class::getSimpleName)::iterator)).append("): ").append(e);
                    throw new IllegalArgumentException(message.toString());
                }
            }
        }
    }

    private static ParameterElement getArgument(Parameter parameter, Text key) {
        Type type = parameter.getParameterizedType();
        Packer packer = Packer.of(type);
        if (parameter.getAnnotation(AllRemaining.class) != null) {
            packer = Packer.allRemaining(packer);
        }
        return new ParameterElement(getElement(packer.getType(type), key, parameter, packer), packer);
    }

    private static CommandElement getElement(Type type, Text key, Parameter parameter, Packer packer) {
        Class<?> clazz;
        if (type instanceof Class<?>) {
            clazz = (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getRawType();
            if (type instanceof Class) {
                clazz = (Class<?>) type;
            } else {
                throw new AssertionError("raw type should be a class, got " + type.getClass().getName() + "(" + type + ")");
            }
        } else {
            throw new IllegalArgumentException("element type " + type + " may not be an array");
        }

        if (clazz == CommandSource.class) {
            if (parameter.getAnnotation(Source.class) != null) {
                return null;
            }
            throw new IllegalArgumentException("CommandSource parameter must have @Source");
        }
        if (clazz == Player.class) {
            if (parameter.getAnnotation(Source.class) != null) {
                return GenericArguments.playerOrSource(key);
            }
            return GenericArguments.player(key);
        }
        if (clazz == User.class) {
            return GenericArguments.user(key);
        }
        if (clazz == WorldProperties.class) {
            return GenericArguments.world(key);
        }
        if (CatalogType.class.isAssignableFrom(clazz)) {
            return GenericArguments.catalogedElement(key, clazz.asSubclass(CatalogType.class));
        }
        if (clazz == Vector3d.class) {
            return GenericArguments.vector3d(key);
        }
        if (clazz == Location.class) {
            return GenericArguments.location(key);
        }
        // TODO flags
        // TODO choices
        // TODO firstParsing
        // TODO allOf
        if (clazz == String.class) {
            // TODO remainingJoinedStrings
            return GenericArguments.string(key);
        }
        if (clazz == int.class || clazz == Integer.class) {
            return GenericArguments.integer(key);
        }
        if (clazz == long.class || clazz == Long.class) {
            return GenericArguments.longNum(key);
        }
        if (clazz == double.class || clazz == Double.class) {
            return GenericArguments.doubleNum(key);
        }
        if (clazz == boolean.class || clazz == Boolean.class) {
            return GenericArguments.bool(key);
        }
        if (Enum.class.isAssignableFrom(clazz)) {
            return GenericArguments.enumValue(key, clazz.asSubclass(Enum.class));
        }
        // TODO literal
        // TODO requiringPermission
        if (clazz == Entity.class) {
            if (parameter.getAnnotation(Source.class) != null) {
                return GenericArguments.entityOrSource(key);
            }
            return GenericArguments.entity(key);
        }
        if (clazz == PluginContainer.class) {
            return GenericArguments.plugin(key);
        }

        throw new UnsupportedOperationException("Parameter type " + clazz.getName() + " is not supported!");
    }
}
