package net.kissenpvp.core.api.networking.client.entitiy;

public class UnknownPlayerException extends NullPointerException {

    private final String player;

    public UnknownPlayerException(String player) {
        this.player = player;
    }

    public UnknownPlayerException(String message, String player) {
        super(message);
        this.player = player;
    }

    public String getPlayer() {
        return player;
    }
}