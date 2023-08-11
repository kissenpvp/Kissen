/*
 * Copyright 2023 KissenPvP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.kissenpvp.core.database;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.kissenpvp.core.api.database.DataImplementation;
import net.kissenpvp.core.base.KissenCore;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class KissenDataImplementation implements DataImplementation {
    @Override
    public @NotNull String toJson(@NotNull Record record) {
        return new Gson().toJson(record, record.getClass());
    }

    @Override
    public @NotNull JsonElement toJsonElement(@NotNull Record record) {
        return JsonParser.parseString(new Gson().toJson(record, record.getClass()));
    }

    @Override
    public <T extends Record> @NotNull T fromJson(@NotNull String json, @NotNull Class<T> record) {
        return new GsonBuilder().registerTypeAdapterFactory(new RecordTypeAdapterFactory()).create().fromJson(json,
                record);
    }

    @Override
    public @NotNull String generateID() {
        return UUID.randomUUID().toString().split("-")[1];
    }

    static class RecordTypeAdapterFactory implements TypeAdapterFactory {

        @Override
        public <T> TypeAdapter<T> create(Gson gson, @NotNull TypeToken<T> type) {
            Class<T> clazz = (Class<T>) type.getRawType();
            if (!clazz.isRecord()) {
                return null;
            }
            TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);

            return new TypeAdapter<>() {
                @Override
                public void write(JsonWriter out, T value) throws IOException {
                    delegate.write(out, value);
                }

                @Override
                public T read(JsonReader reader) throws IOException {
                    if (reader.peek() == JsonToken.NULL) {
                        reader.nextNull();
                        return null;
                    } else {
                        RecordComponent[] recordComponents = clazz.getRecordComponents();
                        Map<String, TypeToken<?>> typeMap = new HashMap<>();
                        for (RecordComponent recordComponent : recordComponents) {
                            typeMap.put(recordComponent.getName(), TypeToken.get(recordComponent.getGenericType()));
                        }

                        Map<String, Object> argsMap = new HashMap<>();
                        reader.beginObject();
                        while (reader.hasNext()) {
                            String name = reader.nextName();
                            argsMap.put(name, gson.getAdapter(typeMap.get(name)).read(reader));
                        }
                        reader.endObject();

                        Class<?>[] argTypes = new Class<?>[recordComponents.length];
                        Object[] args = new Object[recordComponents.length];
                        for (int i = 0; i < recordComponents.length; i++) {
                            argTypes[i] = recordComponents[i].getType();
                            args[i] = argsMap.get(recordComponents[i].getName());
                        }

                        Constructor<T> constructor;
                        try {
                            constructor = clazz.getDeclaredConstructor(argTypes);
                            constructor.setAccessible(true);
                            return constructor.newInstance(args);
                        } catch (NoSuchMethodException | InstantiationException | SecurityException |
                                 IllegalAccessException | IllegalArgumentException |
                                 InvocationTargetException exception) {
                            KissenCore.getInstance().getLogger().error("Could not convert json to record.", exception);
                            throw new RuntimeException(exception);
                        }
                    }
                }
            };
        }
    }
}
