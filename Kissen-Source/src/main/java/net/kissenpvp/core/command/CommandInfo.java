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
                commandData.name(),
                commandData.aliases(),
                commandData.description(),
                commandData.usage(),
                commandData.permission().isBlank() ? "kissen.command." + commandData.name() : commandData.permission(),
                commandData.permissionRequired(),
                commandData.target(),
                commandData.runAsync()
        );
    }

}
