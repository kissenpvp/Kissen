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
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The {@code PageBuilder} class provides a way to paginate a list of entries of type {@link T}.
 * This class is designed to handle any type of list and provides methods to retrieve entries
 * for a specific page, as well as generate headers and footers for paginated views.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * List<String> entries = Arrays.asList("entry1", "entry2", "entry3", "entry4", "entry5", "entry6");
 * PageBuilder<String> pageBuilder = new PageBuilder<>(entries, 2);
 * System.out.println(pageBuilder.getEntries(1)) // out: ["entry1", "entry2"]
 * System.out.println(pageBuilder.getEntries(3)) // out: ["entry5", "entry6"]
 * }</pre>
 *
 * @param <T> the type of the entries in the list
 */
public class PageBuilder<T> {

    private final List<T> entries;
    private int splitter;

    /**
     * Constructs a {@code PageBuilder} with a list of entries and a specified number of entries per page.
     *
     * @param entries the list of entries to paginate
     * @param splitter the number of entries per page
     */
    public PageBuilder(@NotNull Collection<T> entries, int splitter) {
        this(splitter);
        this.entries.addAll(entries);
    }

    /**
     * Constructs a {@code PageBuilder} with a list of entries and a default of 5 entries per page.
     *
     * @param entries the list of entries to paginate
     */
    public PageBuilder(@NotNull Collection<T> entries) {
        this(5);
        this.entries.addAll(entries);
    }

    /**
     * Constructs an empty {@code PageBuilder} with a default of 5 entries per page.
     */
    public PageBuilder() {
        this(5);
    }

    /**
     * Constructs an empty {@code PageBuilder} with a specified number of entries per page.
     *
     * @param splitter the number of entries per page
     */
    public PageBuilder(int splitter) {
        this.entries = new ArrayList<>();
        this.splitter = splitter;
    }

    /**
     * Returns the current number of entries per page.
     *
     * @return the number of entries per page
     */
    public int getSplitter() {
        return splitter;
    }

    /**
     * Sets the number of entries per page.
     *
     * @param splitter the new number of entries per page
     */
    public void setSplitter(int splitter) {
        this.splitter = splitter;
    }

    /**
     * Returns an unmodifiable list of entries for the specified page.
     * <p>
     * If the specified page is less than 1, it returns the entries for the first page.
     * If the specified page is greater than the last page, it returns the entries for the last page.
     * The returned list is unmodifiable.
     *
     * @param currentPage the page number to retrieve
     * @return an unmodifiable list of entries for the specified page
     * @throws IllegalArgumentException if the entries list is empty
     */
    public @NotNull @Unmodifiable List<T> getEntries(int currentPage) {
        if (this.entries.isEmpty()) {
            throw new IllegalArgumentException("List of entries cannot be empty!");
        }

        List<T> pageEntries = new ArrayList<>();

        currentPage = normalize(currentPage);

        int startIndex = (currentPage - 1) * this.splitter;
        int endIndex = Math.min(startIndex + this.splitter, this.entries.size());

        for (int i = startIndex; i < endIndex; i++) {
            pageEntries.add(this.entries.get(i));
        }

        return Collections.unmodifiableList(pageEntries);
    }

    /**
     * Normalizes the page number to ensure it is within valid range.
     * <p>
     * If the page number is less than 1, it returns 1.
     * If the page number is greater than the last page, it returns the last page number.
     *
     * @param page the page number to normalize
     * @return the normalized page number
     */
    public int normalize(int page) {
        if (page < 1) {
            return 1;
        }

        return Math.min(page, getLastPage());
    }

    /**
     * Generates a header component for the paginated view.
     * <p>
     * The header includes the title, current page number, and total number of pages.
     *
     * @param title the title component for the header
     * @param page the current page number
     * @return a {@link Component} representing the header
     */
    public @NotNull Component getHeader(@NotNull Component title, int page) {
        Component[] args = {title, Component.text(normalize(page)), Component.text(getLastPage())};

        return Component.translatable("server.command.general.header.paged", args);
    }

    /**
     * Generates a footer component for the paginated view.
     * <p>
     * The footer includes the title, current page number, and total number of pages.
     *
     * @param title the title component for the footer
     * @param page the current page number
     * @return a {@link Component} representing the footer
     */
    public @NotNull Component getFooter(@NotNull Component title, int page) {
        Component[] args = {title, Component.text(normalize(page)), Component.text(getLastPage())};

        return Component.translatable("server.command.general.footer.paged", args);
    }

    /**
     * Calculates and returns the last page number based on the total number of entries and the splitter value.
     *
     * @return the last page number
     */
    public int getLastPage() {
        return (int) Math.ceil(this.entries.size() / ((float) this.splitter));
    }
}
