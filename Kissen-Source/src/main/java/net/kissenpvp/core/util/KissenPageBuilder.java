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

package net.kissenpvp.core.util;

import lombok.Getter;
import lombok.Setter;
import net.kissenpvp.core.api.util.PageBuilder;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class KissenPageBuilder<T> implements PageBuilder<T>
{

    private final List<T> entries;

    @Getter @Setter private int splitter;

    /**
     * Creates a page manager and fills its list with the transferred list.
     *
     * @param entries The list to be set.
     */
    public KissenPageBuilder(List<T> entries)
    {
        this();
        this.entries.addAll(entries);
    }

    /**
     * Create a page manager and set all standard values. The list is empty.
     */
    public KissenPageBuilder()
    {
        this.entries  = new ArrayList<>();
        this.splitter = 5;
    }

    /**
     * Get the values on one site, if the site is too big,
     * it'll return the content of the last page.
     * <p>
     * IMPORTANT The fist page is 1, not 0.
     *
     * @param currentPage The page whose values you want to have.
     * @return The values it contains.
     */
    @Override public List<T> getEntries(int currentPage)
    {
        if (this.entries.isEmpty())
        {
            throw new IllegalArgumentException("List of entries cannot be empty!");
        }

        List<T> pageEntries = new ArrayList<>();

        currentPage = normalize(currentPage);

        int startIndex = (currentPage - 1) * this.splitter;
        int endIndex = Math.min(startIndex + this.splitter, this.entries.size());

        for (int i = startIndex; i < endIndex; i++)
        {
            pageEntries.add(this.entries.get(i));
        }

        return pageEntries;
    }

    @Override
    public int normalize(int page)
    {
        if(page < 1)
        {
            return 1;
        }

        if(page > getLastPage())
        {
            return getLastPage();
        }

        return page;
    }

    @Override
    public @NotNull Component getHeader(@NotNull Component title, int page)
    {
        Component[] args = {
                title,
                Component.text(normalize(page)),
                Component.text(getLastPage())
        };

        return Component.translatable("server.command.general.header.paged", args);
    }

    @Override
    public @NotNull Component getFooter(@NotNull Component title, int page)
    {
        Component[] args = {
                title,
                Component.text(normalize(page)),
                Component.text(getLastPage())
        };

        return Component.translatable("server.command.general.footer.paged", args);
    }

    /**
     * Get the maximum page you have.
     *
     * @return The maximum of pages available.
     */
    @Override public int getLastPage()
    {
        return (int) Math.ceil(this.entries.size() / ((float) this.splitter));
    }

}
