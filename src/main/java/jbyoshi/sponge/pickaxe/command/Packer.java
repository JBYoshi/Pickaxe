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

import com.google.common.base.Throwables;
import jbyoshi.sponge.pickaxe.ToArrayCollector;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.Optional;

public interface Packer {
    Type getType(Type declaredType);

    CommandElement wrap(CommandElement element);

    Object pack(Collection<Object> raw);

    boolean allowMultipleValues();

    Packer SINGLE = new Packer() {
        @Override
        public Type getType(Type declaredType) {
            return declaredType;
        }

        @Override
        public CommandElement wrap(CommandElement element) {
            return GenericArguments.onlyOne(element);
        }

        @Override
        public Object pack(Collection<Object> raw) {
            return raw.iterator().next();
        }

        @Override
        public boolean allowMultipleValues() {
            return false;
        }
    };

    Packer OPTIONAL = new Packer() {
        @Override
        public Type getType(Type declaredType) {
            if (declaredType instanceof ParameterizedType) {
                return ((ParameterizedType) declaredType).getActualTypeArguments()[0];
            }
            throw new UnsupportedOperationException("type must be parameterized (got " + declaredType + ")");
        }

        @Override
        public CommandElement wrap(CommandElement element) {
            return GenericArguments.optional(GenericArguments.onlyOne(element));
        }

        @Override
        public Object pack(Collection<Object> raw) {
            return raw.stream().findFirst();
        }

        @Override
        public boolean allowMultipleValues() {
            return false;
        }
    };

    static <E> Packer array(Class<E> elementType) {
        return new Packer() {
            @Override
            public Type getType(Type declaredType) {
                if (declaredType instanceof GenericArrayType) {
                    return ((GenericArrayType) declaredType).getGenericComponentType();
                }
                if (declaredType instanceof Class<?>) {
                    return ((Class<?>) declaredType).getComponentType();
                }
                throw new IllegalArgumentException("type must be an array");
            }

            @Override
            public CommandElement wrap(CommandElement element) {
                return element;
            }

            @Override
            public Object pack(Collection<Object> raw) {
                return raw.stream().map(elementType::cast).collect(ToArrayCollector.of(elementType));
            }

            @Override
            public boolean allowMultipleValues() {
                return true;
            }
        };
    }

    static Packer collection(Class<? extends Collection<Object>> collectionType) {
        Constructor<? extends Collection<Object>> constructor;
        try {
            constructor = collectionType.getConstructor();
            constructor.newInstance();
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalArgumentException("Type must have a public no-arg constructor");
        } catch (InstantiationException e) {
            throw new IllegalArgumentException("Type must be non-abstract");
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException("Type's no-arg constructor must not throw an exception");
        }
        return new Packer() {
            @Override
            public Type getType(Type declaredType) {
                if (declaredType instanceof ParameterizedType) {
                    return ((ParameterizedType) declaredType).getActualTypeArguments()[0];
                }
                throw new UnsupportedOperationException("type must be parameterized (got " + declaredType + ")");
            }

            @Override
            public CommandElement wrap(CommandElement element) {
                return element;
            }

            @Override
            public Object pack(Collection<Object> raw) {
                try {
                    Collection<Object> collection = constructor.newInstance();
                    collection.addAll(raw);
                    return collection;
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new AssertionError(e);
                } catch (InvocationTargetException e) {
                    throw Throwables.propagate(e.getCause());
                }
            }

            @Override
            public boolean allowMultipleValues() {
                return true;
            }
        };
    }

    static Packer of(Type type) {
        if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            if (componentType instanceof ParameterizedType) {
                componentType = ((ParameterizedType) componentType).getOwnerType();
            }
            return Packer.array((Class<?>) componentType);
        }
        if (type instanceof ParameterizedType) {
            Class<?> clazz = (Class<?>) ((ParameterizedType) type).getOwnerType();
            if (clazz.isArray()) {
                return Packer.array(clazz.getComponentType());
            } else if (Collection.class.isAssignableFrom(clazz)) {
                return Packer.collection(clazz.asSubclass((Class<Collection<Object>>) (Class<?>) Collection.class));
            } else if (Optional.class.isAssignableFrom(clazz)) {
                return Packer.OPTIONAL;
            }
        }
        return Packer.SINGLE;
    }

    static Packer allRemaining(Packer packer) {
        if (!packer.allowMultipleValues()) {
            throw new IllegalArgumentException("Packer only allows one value, may not have @AllRemaining");
        }
        return new Packer() {
            @Override
            public Type getType(Type declaredType) {
                return packer.getType(declaredType);
            }

            @Override
            public CommandElement wrap(CommandElement element) {
                return GenericArguments.allOf(element);
            }

            @Override
            public Object pack(Collection<Object> raw) {
                return packer.pack(raw);
            }

            @Override
            public boolean allowMultipleValues() {
                return true;
            }
        };
    }
}
