/**
 * This file is part of the OGEMA widgets framework.
 *
 * OGEMA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OGEMA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OGEMA. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2014 - 2018
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
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
