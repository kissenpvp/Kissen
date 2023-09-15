/*
 * Copyright (C) 2023 KissenPvP
 *
 * This program is licensed under the Apache License, Version 2.0.
 *
 * This software may be redistributed and/or modified under the terms
 * of the Apache License as published by the Apache Software Foundation,
 * either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the Apache
 * License, Version 2.0 for the specific language governing permissions
 * and limitations under the License.
 *
 * You should have received a copy of the Apache License, Version 2.0
 * along with this program. If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
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
