package net.kissenpvp.core.api.command.exception;

public class ArgumentMissingException extends NullPointerException
{
    public ArgumentMissingException() {
    }

    public ArgumentMissingException(String message) {
        super(message);
    }
}
