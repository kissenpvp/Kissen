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
import net.kissenpvp.core.api.command.exception.CommandException;
import net.kissenpvp.core.api.command.exception.type.IllegalParameterException;
import net.kissenpvp.core.api.command.exception.type.IllegalReturnValueException;
import net.kissenpvp.core.api.command.executor.TabCompleterExecutor;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * The {@code KissenPaperCompleteExecutor} class implements the {@link TabCompleterExecutor} interface for the Paper platform.
 * It is responsible for providing tab completion suggestions by executing a method associated with a command on the KissenPvP server,
 * specifically tailored for Paper's tab completion handling.
 *
 * <p>This class is designed to work in conjunction with the KissenPvP command framework, providing an implementation
 * of the {@link TabCompleterExecutor} interface to handle tab completion for the Paper platform. The associated method is expected
 * to return a {@link Collection} of tab completion suggestions.</p>
 *
 * <p>The constructor enforces certain method signature requirements, throwing exceptions if the method's return type is not a Collection
 * or if the method has more than one parameter or a parameter that is not of type {@link CommandPayload}.</p>
 *
 * @see TabCompleterExecutor
 * @see CommandPayload
 */
public class KissenPaperCompleteExecutor<S extends ServerEntity> implements TabCompleterExecutor<S>
{

    private final Method method;
    private final Object holder;

    /**
     * Constructs a new instance of the {@code KissenPaperCompleteExecutor} class.
     *
     * @param method The method to be executed for tab completion.
     * @param holder The object instance that holds the method to be executed.
     * @throws IllegalReturnValueException If the return type of the method is not a Collection.
     * @throws IllegalParameterException   If the method takes more than one parameter or takes a parameter that is not a CommandPayload.
     */
    public KissenPaperCompleteExecutor(@NotNull Object holder, @NotNull Method method)
    {
        final Class<?> returnType = method.getReturnType();
        final Class<?>[] parameters = method.getParameterTypes();

        if (!Collection.class.isAssignableFrom(returnType))
        {
            throw new IllegalReturnValueException(returnType, Collection.class);
        }

        if (parameters.length > 1 || (parameters.length == 1 && !CommandPayload.class.isAssignableFrom(parameters[0])))
        {
            throw new IllegalParameterException(parameters[0], CommandPayload.class);
        }

        this.method = method;
        this.holder = holder;
    }

    @Override
    public @NotNull Collection<String> execute(@NotNull CommandPayload<S> context)
    {
        final Class<?>[] types = method.getParameterTypes();
        try
        {
            if (types.length == 0)
            {
                return (Collection<String>) method.invoke(holder);
            }

            if (types.length == 1 && types[0] == CommandPayload.class)
            {
                return (Collection<String>) method.invoke(holder, context);
            }

            throw new IllegalStateException(String.format(
                    "The method '%s' has an invalid configuration for being a tab completer.",
                    method.getName()));
        }
        catch (Exception exception)
        {
            throw new CommandException(String.format(
                    "An error occurred while processing the tab request for command '%s'.",
                    context.getLabel()), exception);
        }
    }
}
