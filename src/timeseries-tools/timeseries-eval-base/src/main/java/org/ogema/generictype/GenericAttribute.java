/**
 * ﻿Copyright 2014-2018 Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ogema.generictype;

import de.iwes.widgets.template.LabelledItem;

/** An attribute provides additional information compared to the mere OGEMA resource type
 * information. If a potential attribute is already implied by the specification of a certain
 * OGEMA resource type it does not need to be added explicitly even if it would apply. It can
 * still be added in case of doubt.<br>
 * Note that for now it just contains a specification, id and label, but it could be extended
 * in the future, so we define it as a separate interface.
 */
public interface GenericAttribute extends LabelledItem {
}
