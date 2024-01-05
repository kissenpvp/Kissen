package net.kissenpvp.core.user.usersettings;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter @RequiredArgsConstructor
public class IllegalSettingException extends IllegalArgumentException
{
    private final String settingsKey;
}
