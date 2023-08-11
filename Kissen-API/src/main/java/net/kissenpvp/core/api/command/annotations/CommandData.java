package net.kissenpvp.core.api.command.annotations;

import net.kissenpvp.core.api.command.CommandTarget;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@java.lang.annotation.Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandData {

    @NotNull String name();

    @NotNull String[] aliases() default {};

    @NotNull String description() default "No description provided.";

    @NotNull String usage() default "";

    @NotNull String permission() default "";

    boolean permissionRequired() default true;

    @NotNull CommandTarget target() default CommandTarget.ALL;

    boolean runAsync() default false;
}
