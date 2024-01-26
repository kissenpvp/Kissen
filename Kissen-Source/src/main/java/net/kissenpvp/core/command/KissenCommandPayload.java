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

package net.kissenpvp.core.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kissenpvp.core.api.base.plugin.KissenPlugin;
import net.kissenpvp.core.api.command.ArgumentParser;
import net.kissenpvp.core.api.command.CommandHandler;
import net.kissenpvp.core.api.command.CommandTarget;
import net.kissenpvp.core.api.networking.client.entitiy.MessageReceiver;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.api.permission.PermissionEntry;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.command.confirmation.KissenConfirmationImplementation;
import net.kissenpvp.core.command.handler.AbstractCommandHandler;
import net.kissenpvp.core.command.handler.PluginCommandHandler;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.time.Duration;
import java.time.Instant;

/**
 * The {@code KissenPaperCommandPayload} class represents the payload associated with a command execution in the KissenPvP plugin for the Paper platform.
 * It implements the {@link net.kissenpvp.core.api.command.CommandPayload} interface, providing information about the command label, sender, target, arguments, and the associated command holder.
 * Additionally, it includes methods for validation, confirmation requests, and default behaviors related to command confirmation cancellations.
 *
 * <p>This class is part of the KissenPvP command framework and is designed to handle command-related information and operations specific to the Paper platform.</p>
 *
 * @see net.kissenpvp.core.api.command.CommandPayload
 * @see CommandTarget
 * @see PermissionEntry
 * @see CommandHolder
 * @see Component
 */
@RequiredArgsConstructor
@Getter
public abstract class KissenCommandPayload<S extends ServerEntity> implements net.kissenpvp.core.api.command.CommandPayload<S>
{

    private final String label;
    private final S sender;
    private final CommandTarget target;
    private final String[] args;
    private final CommandHolder<?, ?> commandHolder;

    protected abstract @NotNull CommandHandler<S, ?> getHandler();

    @Override
    public @NotNull String getLabel()
    {
        return label;
    }

    @Override
    public @NotNull S getSender()
    {
        return sender;
    }

    @Override
    public @NotNull CommandTarget getTarget()
    {
        return target;
    }

    @Override
    public @NotNull String[] getArguments()
    {
        return args;
    }

    @Override
    public <T> @NotNull T[] getArgument(int from, int to, @NotNull Class<T> type) throws ArrayIndexOutOfBoundsException
    {

        CommandImplementation<S> command = KissenCore.getInstance().getImplementation(CommandImplementation.class);



        final ArgumentParser<?, ?> adapter = ((AbstractCommandHandler<S, ?>) getHandler()).getParser().get(type);
        final T[] instance = (T[]) Array.newInstance(type, to - from);

        for (int i = from; i <= to; i++)
        {
            instance[i - from] = (T) command.deserialize(getArgument(i).orElseThrow(IllegalArgumentException::new),
                    adapter);
        }

        return instance;
    }

    @Override
    public boolean validate(@NotNull ServerEntity serverEntity)
    {

        if (getCommandHolder().getCommandInfo().map(CommandInfo::isPermissionRequired).orElse(true) && serverEntity instanceof PermissionEntry<?> permissionEntry)
        {

            if (!permissionEntry.hasPermission(getCommandHolder().getCommandInfo().map(CommandInfo::getPermission).orElse(
                    "kissen.command" + getLabel())))
            {
                return false;
            }
        }

        CommandImplementation<S> command = KissenCore.getInstance().getImplementation(CommandImplementation.class);
        return command.getTargetValidator().validate(getTarget(), serverEntity);
    }

    @Override
    public boolean confirmRequest(@NotNull Runnable runnable)
    {
        return confirmRequest(runnable, defaultRunnable());
    }

    @Override
    public boolean confirmRequest(@NotNull Runnable runnable, @NotNull Runnable cancel)
    {
        return confirmRequest(runnable, cancel, defaultRunnable());
    }

    @Override
    public boolean confirmRequest(@NotNull Runnable runnable, @NotNull Runnable cancel, @NotNull Runnable timeRunOut)
    {
        return confirmRequest(runnable, cancel, timeRunOut, true);
    }

    @Override
    public boolean confirmRequest(@NotNull Runnable runnable, @NotNull Runnable cancel, @NotNull Runnable timeRunOut, boolean sendMessage)
    {
        return confirmRequest(Duration.ofSeconds(30), runnable, cancel, timeRunOut, sendMessage);
    }

    @Override
    public boolean confirmRequest(@NotNull Duration duration, @NotNull Runnable runnable)
    {
        return confirmRequest(duration, runnable, defaultRunnable());
    }

    @Override
    public boolean confirmRequest(@NotNull Duration duration, @NotNull Runnable runnable, @NotNull Runnable cancel)
    {
        return confirmRequest(duration, runnable, cancel, defaultRunnable());
    }

    @Override
    public boolean confirmRequest(@NotNull Duration duration, @NotNull Runnable runnable, @NotNull Runnable cancel, @NotNull Runnable timeRunOut)
    {
        return confirmRequest(duration, runnable, cancel, timeRunOut, true);
    }

    @Override
    public boolean confirmRequest(@NotNull Duration duration, @NotNull Runnable runnable, @NotNull Runnable cancel, @NotNull Runnable timeRunOut, boolean sendMessage)
    {
        if (getTarget().equals(CommandTarget.SYSTEM))
        {
            runnable.run();
            return true; // TODO implement confirmation for console as well
        }
        Class<KissenConfirmationImplementation> confirmClass = KissenConfirmationImplementation.class;
        KissenConfirmationImplementation implementation = KissenCore.getInstance().getImplementation(confirmClass);
        Instant now = Instant.now();

        KissenPlugin plugin = null;
        if (getCommandHolder() instanceof PluginCommandHandler<?, ?> pluginHandler)
        {
            plugin = pluginHandler.getPlugin();
        }

        if (implementation.requestConfirmation(plugin, getSender(), now.plus(duration), runnable, cancel, timeRunOut))
        {
            if (sendMessage)
            {
                Component time = Component.text(duration.toSecondsPart());
                sendInternalMessage(Component.translatable("server.command.confirm.required", time));
            }
            return true;
        }

        if (sendMessage)
        {
            sendInternalMessage(Component.translatable("server.command.confirm.already.request"));
        }
        return false;
    }

    /**
     * Returns a non-null {@code Runnable} that represents the default behavior when a command confirmation is cancel.
     * The returned {@code Runnable} is a lambda expression that sends a cancellation message to the command sender.
     *
     * <p>The cancellation message is constructed using the {@link Component#translatable(String)} method with the
     * translation key "server.command.confirm.cancel". The message is sent to the command sender using the
     * {@link #getSender()} method.</p>
     *
     * <p>Usage Example:</p>
     * <pre>
     * {@code
     * Runnable cancellationAction = defaultRunnable();
     * // Execute the cancellation action when needed
     * cancellationAction.task();
     * }
     * </pre>
     *
     * @return A non-null {@code Runnable} representing the default cancellation behavior for command confirmation.
     * @see Component#translatable(String)
     * @see #getSender()
     */
    @Contract(pure = true, value = "-> new")
    private @NotNull Runnable defaultRunnable()
    {
        return () -> sendInternalMessage(Component.translatable("server.command.confirm.cancel"));
    }

    private void sendInternalMessage(@NotNull Component component)
    {
        if (getSender() instanceof MessageReceiver messageReceiver)
        {
            messageReceiver.getKyoriAudience().sendMessage(component);
        }
    }
}
