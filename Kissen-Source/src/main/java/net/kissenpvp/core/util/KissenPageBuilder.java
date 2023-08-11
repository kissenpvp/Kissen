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

package net.kissenpvp.core.util;

import lombok.Getter;
import lombok.Setter;
import net.kissenpvp.core.api.util.PageBuilder;

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
            throw new NullPointerException("List of entries cannot be empty!");
        }

        final List<T> PAGE_ENTRIES = new ArrayList<>();

        if (currentPage < 1)
        {
            currentPage = 1;
        }
        else
        {
            if (currentPage > this.getLastPage())
            {
                currentPage = this.getLastPage();
            }
        }

        for (int i = ((currentPage - 1) * this.splitter); i < (((currentPage - 1) * this.splitter) + this.splitter); i++)
        {
            try
            {
                PAGE_ENTRIES.add(this.entries.get(i));
            }
            catch (IndexOutOfBoundsException ignored)
            {
            }

        }

        return PAGE_ENTRIES;
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
