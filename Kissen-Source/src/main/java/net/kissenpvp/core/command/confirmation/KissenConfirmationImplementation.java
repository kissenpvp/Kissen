package net.kissenpvp.core.command.confirmation;

import lombok.SneakyThrows;
import net.kissenpvp.core.api.networking.client.entitiy.PlayerClient;
import net.kissenpvp.core.api.networking.client.entitiy.ServerEntity;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.base.KissenImplementation;
import net.kissenpvp.core.message.localization.KissenLocalizationImplementation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class KissenConfirmationImplementation implements KissenImplementation
{
    private final Set<Confirmation> confirmations;
    private volatile Thread cleanThread;

    public KissenConfirmationImplementation()
    {
        this.confirmations = ConcurrentHashMap.newKeySet();
    }

    @Override
    public boolean postStart()
    {
        KissenLocalizationImplementation localize = KissenCore.getInstance().getImplementation(
                KissenLocalizationImplementation.class);
        localize.register("server.command.confirm.required", new MessageFormat(
                "Are you sure you want to execute this task? Type /confirm to execute it, or /cancel to cancel it. This confirmation will expire after {0} seconds."));
        localize.register("server.command.confirm.cancelled",
                new MessageFormat("The confirmation request has not been executed."));
        localize.register("server.command.confirm.no.request",
                new MessageFormat("You do not have any task to confirm."));
        return KissenImplementation.super.postStart();
    }

    private @NotNull @Unmodifiable Set<PlayerConfirmationNode> getPlayerConfirmation()
    {
        return confirmations.stream().filter(confirmation -> confirmation instanceof PlayerConfirmationNode).map(
                confirmation -> (PlayerConfirmationNode) confirmation).collect(Collectors.toUnmodifiableSet());
    }

    public boolean action(@NotNull ServerEntity sender, boolean cancel)
    {
        if (sender instanceof PlayerClient<?, ?, ?> player)
        {
            Predicate<PlayerConfirmationNode> isFromPlayer = confirmation -> confirmation.equals(player);
            return getPlayerConfirmation().stream().filter(isFromPlayer).anyMatch(playerAction(cancel));
        }
        return false;
    }

    @Contract(pure = true)
    private @NotNull Predicate<PlayerConfirmationNode> playerAction(boolean cancel)
    {
        return confirmation ->
        {
            confirmations.remove(confirmation);
            if (cancel)
            {
                confirmation.confirmationNode().cancel();
                return true;
            }
            confirmation.confirmationNode().execute();
            return true;
        };
    }

    public boolean requestConfirmation(@NotNull ServerEntity sender, @NotNull Instant time, @NotNull Runnable runnable, @NotNull Runnable onCancel, @NotNull Runnable onTime)
    {
        if (sender instanceof PlayerClient<?, ?, ?> player)
        {
            return requestPlayerConfirmation(player, time, runnable, onCancel, onTime);
        }
        return false;
    }

    private boolean requestPlayerConfirmation(@NotNull PlayerClient<?, ?, ?> player, @NotNull Instant time, @NotNull Runnable runnable, @NotNull Runnable onCancel, @NotNull Runnable onTime)
    {
        if (confirmations.stream().anyMatch(
                confirmation -> confirmation instanceof PlayerConfirmationNode confirm && confirm.equals(player)))
        {
            return false;
        }

        ConfirmationNode confirm = new ConfirmationNode(time, runnable, onCancel, onTime);
        PlayerConfirmationNode playerConfirm = new PlayerConfirmationNode(player.getUniqueId(), confirm);
        if (confirmations.add(playerConfirm))
        {
            runThread();
            return true;
        }
        return false;
    }

    private void runThread()
    {
        if (cleanThread == null)
        {
            cleanThread = new Thread(this::cleanUp);
            cleanThread.setName("clean-up-1");
            cleanThread.setPriority(Thread.MIN_PRIORITY);
            cleanThread.start();
        }
    }

    private void cleanUp()
    {
        while (isServerRunning())
        {
            if (confirmations.isEmpty())
            {
                cleanThread.interrupt();
                cleanThread = null;
                return;
            }
            Stream<Confirmation> confirmationStream = Collections.unmodifiableSet(confirmations).stream();
            confirmations.removeAll(confirmationStream.filter(cleanFilter()).collect(Collectors.toSet()));
        }
    }

    @Contract(pure = true)
    private @NotNull Predicate<Confirmation> cleanFilter()
    {
        return confirmation ->
        {
            if (!confirmation.isValid())
            {
                if (confirmation instanceof PlayerConfirmationNode playerConfirmationNode)
                {
                    playerConfirmationNode.confirmationNode().cancel();
                    return true;
                }
                return true;
            }
            return false;
        };
    }

    protected abstract boolean isServerRunning();
}
