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

package net.kissenpvp.core.api.networking;

import com.google.gson.JsonObject;
import net.kissenpvp.core.api.base.Implementation;
import net.kissenpvp.core.api.networking.client.entitiy.UnknownPlayerException;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;

public interface APIRequestImplementation extends Implementation {

    String getName(UUID uuid) throws UnknownPlayerException;

    UUID getUUID(String name) throws UnknownPlayerException;

    /**
     * Fetch user data from the Mojang api using the uuid.
     * <p>
     * WARNING when this method is called to often the mojang api is closing the requests sent.
     * This is for security and fully understandable, so only use this method when absolutely needed.
     *
     * @param uuid the user to get the data from.
     * @return the json response gotten from the mojang api.
     * @throws UnknownPlayerException when the user could not be resolved.
     */
    JsonObject getData(UUID uuid) throws UnknownPlayerException;

    /**
     * Fetches json from a website.
     *
     * @param url the url to receive json from.
     * @return the downloaded json object.
     * @throws IOException           when something went wrong while connection or downloading.
     * @throws IllegalStateException when the received stuff is not json.
     */
    JsonObject fetchJsonData(URL url) throws IOException, IllegalStateException;

}
