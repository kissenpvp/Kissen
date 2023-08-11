package net.kissenpvp.core.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UnauthorizedException extends UnsupportedOperationException {
    public String permission;
}
