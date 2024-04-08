package net.kissenpvp.core.user.playersetting;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter @RequiredArgsConstructor
public class IllegalSettingException extends IllegalArgumentException
{
    private final String settingsKey;
}
