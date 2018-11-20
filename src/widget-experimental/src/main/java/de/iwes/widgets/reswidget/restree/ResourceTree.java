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
package de.iwes.widgets.reswidget.restree;

import org.ogema.core.model.Resource;

import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.html.tree.LeafDO;
import de.iwes.widgets.html.tree.NodeDO;
import de.iwes.widgets.html.tree.Tree;

/** Tree widget that allows to view and edit the contents of a resource and its subresources
 * TODO: Under development!!
 * @author dnestle
 *
 */
public class ResourceTree<T extends Resource> extends Tree<T> {
	private static final long serialVersionUID = -1687042308795879835L;
	
	public ResourceTree(WidgetPage<?> page, String id) {
		this(page, id, null);
	}
	public ResourceTree(WidgetPage<?> page, String id, NodeDO<T> root) {
		super(page, id, root);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public NodeDO<T> getResourceNode(Resource res) {
		NodeDO<T> result = new NodeDO(res.getName(), res);
		for(Resource r: res.getSubResources(false)) {
			if(r.getDirectSubResources(false).isEmpty()) {
				result.addData(new LeafDO(r.getName(), r.getName(), res));
			} else {
				result.add(getResourceNode(r));
			}
		}
		return null;
	}
}
