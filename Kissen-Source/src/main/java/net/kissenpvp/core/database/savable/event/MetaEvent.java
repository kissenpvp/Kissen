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

package net.kissenpvp.core.database.savable.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kissenpvp.core.api.database.savable.Savable;
import net.kissenpvp.core.api.event.EventClass;

@Getter
@AllArgsConstructor
public abstract class MetaEvent<T extends Savable, K> implements EventClass {
    private final T savable;
    private final String key;
    private final K before, after;
}
