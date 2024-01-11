package net.kissenpvp.core.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter @RequiredArgsConstructor
public class InvalidColorException extends NullPointerException
{
    private final String input;

}
