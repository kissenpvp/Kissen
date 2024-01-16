package net.kissenpvp.core.command.confirmation;

import net.kissenpvp.core.api.base.plugin.KissenPlugin;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.base.KissenImplementation;
import net.kissenpvp.core.message.localization.KissenLocalizationImplementation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class KissenConfirmationImplementation implements KissenImplementation
{
    private final Set<Confirmation> confirmations;
    private final ScheduledExecutorService cleanupScheduler;

    public KissenConfirmationImplementation()
    {
        this.confirmations = ConcurrentHashMap.newKeySet();
        this.cleanupScheduler = Executors.newScheduledThreadPool(1);
        this.cleanupScheduler.scheduleAtFixedRate(this::cleanUp, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public boolean postStart()
    {
        KissenLocalizationImplementation localize = KissenCore.getInstance().getImplementation(
                KissenLocalizationImplementation.class);
        localize.register("server.command.confirm.required", new MessageFormat(
                "Are you sure you want to execute this task? Type /confirm to execute it, or /cancel to cancel it. This confirmation will expire after {0} seconds."));
        localize.register("server.command.confirm.cancel",
                new MessageFormat("The confirmation request has not been executed."));
        localize.register("server.command.confirm.no.request", new MessageFormat("You do not have any task to confirm."));
        localize.register("server.command.confirm.already.request", new MessageFormat("You already have a task to confirm. Please first confirm or cancel the other task."));
        return KissenImplementation.super.postStart();
    }

    @Override
    public void stop()
    {
        this.cleanupScheduler.shutdown();
        KissenImplementation.super.stop();
    }

    private @NotNull @Unmodifiable Set<PlayerConfirmationNode> getPlayerConfirmation()
    {
        return confirmations.stream().filter(confirmation -> confirmation instanceof PlayerConfirmationNode).map(
                confirmation -> (PlayerConfirmationNode) confirmation).collect(Collectors.toUnmodifiableSet());
    }

    public boolean execute(@NotNull ServerEntity sender, boolean cancel)
    {
        if (sender instanceof PlayerClient<?, ?, ?> player)
        {
            Predicate<PlayerConfirmationNode> isFromPlayer = confirmation -> confirmation.equals(player);
            return getPlayerConfirmation().stream().filter(isFromPlayer).anyMatch(executePlayer(cancel));
        }
        return false;
    }

    @Contract(pure = true)
    private @NotNull Predicate<PlayerConfirmationNode> executePlayer(boolean cancel)
    {
        return confirmation ->
        {
            confirmations.remove(confirmation);
            if (cancel)
            {
                confirmation.confirmationNode().cancel().run();
                return true;
            }
            confirmation.confirmationNode().task().run();
            return true;
        };
    }

    public boolean requestConfirmation(@Nullable KissenPlugin plugin, @NotNull ServerEntity sender, @NotNull Instant time, @NotNull Runnable runnable, @NotNull Runnable onCancel, @NotNull Runnable onTime)
    {
        if (sender instanceof PlayerClient<?, ?, ?> player)
        {
            return requestPlayerConfirmation(plugin, player, time, runnable, onCancel, onTime);
        }
        return false;
    }

    private boolean requestPlayerConfirmation(@Nullable KissenPlugin plugin, @NotNull PlayerClient<?, ?, ?> player, @NotNull Instant time, @NotNull Runnable runnable, @NotNull Runnable onCancel, @NotNull Runnable onTime)
    {
        Predicate<Confirmation> is = task -> task instanceof PlayerConfirmationNode confirm && confirm.equals(player);
        if (confirmations.stream().anyMatch(is))
        {
            return false;
        }

        ConfirmationNode confirm = new ConfirmationNode(plugin, time, runnable, onCancel, onTime);
        PlayerConfirmationNode playerConfirm = new PlayerConfirmationNode(player.getUniqueId(), confirm);
        return confirmations.add(playerConfirm);
    }


    /**
     * Performs cleanup tasks, including removing invalid confirmations and canceling associated player tasks.
     * After the cleanup, it checks if the list of confirmations is empty. If so, it shuts down the cleanup scheduler.
     */
    private void cleanUp()
    {
        Stream<Confirmation> confirmationStream = Collections.unmodifiableSet(confirmations).stream();
        confirmations.removeAll(confirmationStream.filter(cleanFilter()).collect(Collectors.toSet()));
    }

    /**
     * Creates a pure predicate for filtering out invalid confirmation objects during the cleanup process.
     * The predicate checks if a confirmation is invalid, and if so, cancels associated tasks for player confirmations.
     *
     * @return A predicate that evaluates to true for invalid confirmation objects, and false otherwise
     */
    @Contract(pure = true, value = "-> new")
    private @NotNull Predicate<Confirmation> cleanFilter()
    {
        return confirmation ->
        {
            if (!confirmation.isValid())
            {
                if (confirmation instanceof PlayerConfirmationNode confirmationNode)
                {
                    cancelPlayerTask(confirmationNode);
                }
                return true;
            }
            return false;
        };
    }

    /**
     * Cancels the task associated with a player confirmation node. If the confirmation node has a plugin assigned,
     * the task is executed within the context of that plugin. Additionally, a task with the name "Cancel" is scheduled
     * in the KissenCore instance to handle the cancellation.
     *
     * @param confirmationNode The player confirmation node containing the confirmation and associated tasks
     * @throws NullPointerException If the provided confirmationNode is null
     */
    private void cancelPlayerTask(@NotNull PlayerConfirmationNode confirmationNode)
    {
        Runnable time = confirmationNode.confirmationNode().expire();
        if (confirmationNode.confirmationNode().plugin() != null)
        {
            KissenCore.getInstance().runTask(confirmationNode.confirmationNode().plugin(), time);
        }
        KissenCore.getInstance().runTask(time, 0, "Cancel");
    }

    protected abstract boolean isServerRunning();
}