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

package net.kissenpvp.core.command;

import lombok.*;
import net.kissenpvp.core.api.command.CommandTarget;
import net.kissenpvp.core.api.command.annotations.CommandData;
import org.jetbrains.annotations.NotNull;

@Getter
@Builder
@AllArgsConstructor
public class CommandInfo {

    @NonNull
    private final String name;

    @NonNull
    @Builder.Default
    private final String[] aliases = new String[0];

    @Setter
    @Builder.Default
    private String description = "";

    @Setter
    @Builder.Default
    private String usage = "";

    @Setter
    @Builder.Default
    private String permission = null;

    @Setter
    @Builder.Default
    private boolean permissionRequired = true;

    @Setter
    @NonNull
    @Builder.Default
    private CommandTarget target = CommandTarget.ALL;

    @Builder.Default
    private final boolean async = false;

    public CommandInfo(@NotNull CommandData commandData) {
        this(
                commandData.value(),
                commandData.aliases(),
                commandData.description(),
                commandData.usage(),
                commandData.permission().isBlank() ? "kissen.command." + commandData.value() : commandData.permission(),
                commandData.permissionRequired(),
                commandData.target(),
                commandData.runAsync()
        );
    }

}
