/*
 * Copyright (C) 2023 KissenPvP
 *
 * This program is licensed under the Apache License, Version 2.0.
 *
 * This software may be redistributed and/or modified under the terms
 * of the Apache License as published by the Apache Software Foundation,
 * either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the Apache
 * License, Version 2.0 for the specific language governing permissions
 * and limitations under the License.
 *
 * You should have received a copy of the Apache License, Version 2.0
 * along with this program. If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package net.kissenpvp.core.command.executor;

import net.kissenpvp.core.api.command.CommandPayload;
import net.kissenpvp.core.api.command.exception.ArgumentMissingException;
import net.kissenpvp.core.api.command.exception.CommandException;
import net.kissenpvp.core.api.command.executor.CommandExecutor;
import net.kissenpvp.core.api.networking.client.entitiy.MessageReceiver;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.command.CommandHolder;
import net.kissenpvp.core.command.argument.ArgumentEvaluator;
import net.kissenpvp.core.command.handler.AbstractCommandHandler;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.stream.Stream;

/**
 * The {@code KissenPaperCommandExecutor} class implements the {@link CommandExecutor} interface for the Paper platform.
 * It is responsible for executing commands on the KissenPvP server, specifically tailored for Paper's command handling.
 *
 * <p>This class is designed to work in conjunction with the KissenPvP command framework, providing an implementation
 * of the {@link CommandExecutor} interface to handle command execution for the Paper platform.</p>
 *
 * @see CommandExecutor
 * @see ArgumentEvaluator
 */
public abstract class KissenCommandExecutor<S extends ServerEntity> implements CommandExecutor<S>
{
    private final Method method;
    private final Object holder;
    private final ArgumentEvaluator<S> evaluator;
    private final boolean useMethodValue;
    private CommandHolder<S, ?> commandHolder;

    public KissenCommandExecutor(@NotNull AbstractCommandHandler<S, ?> handler, @NotNull Object holder, @NotNull Method method)
    {
        final Class<?> returnType = method.getReturnType();

        if (!returnType.equals(Void.TYPE) && !returnType.equals(Boolean.TYPE))
        {
            throw new IllegalArgumentException(String.format("The return type %s cannot be processed.",
                    returnType.getName()));
        }
        this.holder = holder;
        this.method = method;

        this.evaluator = new ArgumentEvaluator<>(handler.getEvaluator().evaluateMethod(method));
        useMethodValue = returnType.equals(Boolean.TYPE);
    }

    @Override
    public boolean execute(@NotNull CommandPayload<S> context)
    {
        if (evaluator == null)
        {
            NullPointerException nullPointerException = new NullPointerException("The evaluator cannot be null.");
            throw new IllegalStateException("This command cannot be executed", nullPointerException);
        }
        return invokeCommand(context);
    }

    public boolean invokeCommand(@NotNull CommandPayload<S> context)
    {
        try
        {
            if (method == null)
            {
                throw new ArgumentMissingException();
            }
            method.setAccessible(true);
            if (evaluator.arguments().isEmpty())
            {
                if (useMethodValue)
                {
                    return (boolean) method.invoke(holder);
                }
                method.invoke(holder);
                return true;
            }

            Object[] parameter = evaluator.parseArguments(context);

            if (useMethodValue)
            {
                return (boolean) method.invoke(holder, parameter);
            }
            method.invoke(holder, parameter);
            return true;
        }
        catch (Throwable throwable)
        {
            if (isException(throwable, ArgumentMissingException.class, EnumConstantNotPresentException.class))
            {
                if (context.getSender() instanceof MessageReceiver messageReceiver)
                {
                    messageReceiver.getKyoriAudience().sendMessage(getCommandHolder().getFormattedUsage(context));
                    return true;
                }
                return false;
            }

            if (!handleThrowable(context, throwable))
            {
                throw new CommandException(throwable);
            }
            return false;
        }
    }

    @SafeVarargs
    private boolean isException(@NotNull Throwable exception, @NotNull Class<? extends Throwable>... throwable)
    {
        return Stream.of(throwable).anyMatch(current -> current.isAssignableFrom(exception.getClass()) || current.isAssignableFrom(
                exception.getCause().getClass()));
    }

    public @NotNull ArgumentEvaluator<S> getEvaluator()
    {
        return evaluator;
    }

    public @NotNull CommandHolder<S, ?> getCommandHolder()
    {
        return commandHolder;
    }

    public void setCommandHolder(@NotNull CommandHolder<S, ?> commandHolder)
    {
        this.commandHolder = commandHolder;
    }

    protected abstract boolean handleThrowable(@NotNull CommandPayload<S> commandPayload, @NotNull Throwable throwable);
}
