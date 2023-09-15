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
