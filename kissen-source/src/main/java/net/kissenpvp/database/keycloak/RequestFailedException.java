package net.kissenpvp.database.keycloak;

public class RequestFailedException extends RuntimeException
{
    private final int statusCode;

    public RequestFailedException(int statusCode)
    {
        this.statusCode = statusCode;
    }

    public int getStatusCode()
    {
        return statusCode;
    }
}
