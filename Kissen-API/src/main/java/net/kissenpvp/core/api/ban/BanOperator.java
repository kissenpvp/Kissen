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
