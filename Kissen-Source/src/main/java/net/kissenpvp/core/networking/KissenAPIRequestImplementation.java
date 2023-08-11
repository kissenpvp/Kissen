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

package net.kissenpvp.core.networking;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kissenpvp.core.api.networking.APIRequestImplementation;
import net.kissenpvp.core.api.networking.client.entitiy.UnknownPlayerException;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.UUID;


public class KissenAPIRequestImplementation implements APIRequestImplementation {

    @Override
    public String getName(UUID uuid) throws UnknownPlayerException {
        try {
            return getData(uuid).get("name").getAsString();
        } catch (IllegalStateException illegalStateException) {
            throw new UnknownPlayerException(uuid.toString());
        }
    }

    @Override
    public UUID getUUID(String name) throws UnknownPlayerException {
        try {
            JsonObject jsonObject = fetchJsonData(URI.create("https://api.mojang.com/users/profiles/minecraft/" + name).toURL());

            if (jsonObject.has("errorMessage")) {
                throw new IOException();
            }

            //IllegalArgumentException when UUID is invalid.
            return UUID.fromString(jsonObject.get("id").getAsString().replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"));
        } catch (IOException | IllegalArgumentException | IllegalStateException ioException) {
            throw new UnknownPlayerException(name);
        }
    }

    @Override
    public JsonObject getData(@NotNull UUID uuid) throws UnknownPlayerException {
        try {
            JsonObject jsonObject = fetchJsonData(URI.create("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false").toURL());

            if (jsonObject.has("errorMessage")) {
                throw new IOException();
            }

            return jsonObject;
        } catch (IOException | IllegalStateException ioException) {
            throw new UnknownPlayerException(uuid.toString());
        }
    }

    @Override
    public JsonObject fetchJsonData(URL url) throws IOException, IllegalStateException {
        StringBuilder json = new StringBuilder();
        URLConnection urlConnection = url.openConnection();
        urlConnection.connect();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8));

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            json.append(line);
        }
        bufferedReader.close();
        return JsonParser.parseString(json.toString()).getAsJsonObject();
    }
}
