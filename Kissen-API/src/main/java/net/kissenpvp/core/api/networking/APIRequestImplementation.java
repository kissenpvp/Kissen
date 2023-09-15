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
