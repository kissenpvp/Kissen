package net.kissenpvp.core.ban;

import net.kissenpvp.core.api.ban.BanType;
import net.kissenpvp.core.api.database.DataImplementation;
import net.kissenpvp.core.api.message.Comment;
import net.kissenpvp.core.api.util.Container;
import net.kissenpvp.core.base.KissenCore;
import net.kissenpvp.core.database.DataWriter;
import net.kissenpvp.core.message.CommentNode;
import net.kissenpvp.core.message.KissenComment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public record KissenPunishmentNode(@NotNull String id, @NotNull String banName, @NotNull BanOperatorNode banOperator,
                                   @NotNull BanType banType, @NotNull Container<String> reason,
                                   @NotNull List<String> comments, long start, @NotNull Container<Long> duration,
                                   @NotNull Container<Long> end, @Nullable Long predictedEnd)
{

    public @NotNull @Unmodifiable List<Comment> translateComments() {
        return comments().stream().map(comment -> (Comment) new KissenComment(KissenCore.getInstance().getImplementation(DataImplementation.class).fromJson(comment, CommentNode.class), commentDataWriter())).toList();
    }

    public @NotNull DataWriter commentDataWriter()
    {
        DataImplementation dataImplementation = KissenCore.getInstance().getImplementation(DataImplementation.class);
        return commentRecord -> {
            for (int i = 0; i < comments.size(); i++) {
                CommentNode commentNode = dataImplementation.fromJson(comments.get(i), CommentNode.class);
                if(commentNode.equals(commentRecord))
                {
                    comments.set(i, dataImplementation.toJson(commentRecord));
                }
            }
        };
    }
}
