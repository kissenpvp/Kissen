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
