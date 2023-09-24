package net.kissenpvp.core.api.ban.exception;

/**
 * Exception thrown when attempting to perform an operation on a nonexistent ban.
 * This exception extends the {@code IllegalStateException} class.
 */
public class NonexistentBanException extends IllegalStateException {

    private final String ban;

    /**
     * Creates a new instance of NonexistentBanException with the given ban.
     *
     * @param ban the ban that caused the exception
     */
    public NonexistentBanException(String ban) {
        this.ban = ban;
    }

    /**
     * Retrieves the value of the "ban" property.
     *
     * @return the value of the "ban" property
     */
    public String getBan() {
        return ban;
    }
}
