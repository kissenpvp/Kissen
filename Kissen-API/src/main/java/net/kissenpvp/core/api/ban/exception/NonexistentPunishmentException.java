package net.kissenpvp.core.api.ban.exception;

/**
 * Exception thrown when attempting to perform an operation on a nonexistent ban.
 * This exception extends the {@code IllegalStateException} class.
 */
public class NonexistentPunishmentException extends IllegalStateException {

    private final String punishment;

    /**
     * Creates a new instance of NonexistentBanException with the given ban.
     *
     * @param punishment the ban that caused the exception
     */
    public NonexistentPunishmentException(String punishment) {
        this.punishment = punishment;
    }

    /**
     * Retrieves the value of the "ban" property.
     *
     * @return the value of the "ban" property
     */
    public String getPunishment() {
        return punishment;
    }
}
