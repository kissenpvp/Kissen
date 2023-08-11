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

package net.kissenpvp.core.database.file;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class KissenObjectFile {
    private final Map<String, Serializable> stringSerializableMap;
    private final File cacheFile;

    public KissenObjectFile(java.io.File cachefile) throws IOException, ClassNotFoundException {
        this.cacheFile = cachefile;
        this.stringSerializableMap = new HashMap<>();

        if (!cacheFile.exists()) {
            write();
        }
        read();
    }

    public Map<String, Serializable> getStringSerializableMap() {
        return stringSerializableMap;
    }

    public void write() throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(cacheFile);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(new HashMap<>(stringSerializableMap));
            objectOutputStream.flush();
        }
    }

    public void read() throws IOException, ClassNotFoundException {
        try (FileInputStream fileInputStream = new FileInputStream(cacheFile); ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            Object raw = objectInputStream.readObject();
            if (raw instanceof Map<?, ?> map) {
                this.stringSerializableMap.putAll((Map<? extends String, ? extends Serializable>) map);
            }
        }
    }
}
