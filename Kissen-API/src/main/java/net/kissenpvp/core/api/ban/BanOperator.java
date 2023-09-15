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

package net.kissenpvp.core.api.ban;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code BanOperator} interface represents an operator responsible for banning players.
 * It provides methods to retrieve the banner's ID and display name.
 */
public interface BanOperator {

    /**
     * Returns the display name of the ban operator.
     *
     * @return a {@link Component} representing the display name of the ban operator.
     * The display name is typically a formatted string or a localized text that identifies the operator.
     * @implNote The display name is used to provide information about the ban operator,
     * such as showing it in ban messages or admin panels.
     * @since 1.0
     */
    @NotNull Component displayName();
}
