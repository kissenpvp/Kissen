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

package net.kissenpvp.core.api.util;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public interface PageBuilder<T>
{

    /**
     * Get the values on one site, if the site is too big,
     * it'll return the content of the last page.
     * <p>
     * IMPORTANT The fist page is 1, not 0.
     *
     * @param currentPage The page whose values you want to have.
     * @return The values it contains.
     */
    List<T> getEntries(int currentPage);

    int normalize(int page);

    @NotNull Component getHeader(@NotNull Component title, int page);

    @NotNull Component getFooter(@NotNull Component title, int page);

    /**
     * Get the maximum page you have.
     *
     * @return The maximum of pages available.
     */
    int getLastPage();

    int getSplitter();

    void setSplitter(int splitter);
}
