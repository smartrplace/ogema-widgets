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

package de.iwes.widgets.html.tree;

import java.util.Map;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class Tree<T> extends OgemaWidgetBase<TreeData<T>> {

    private static final long serialVersionUID = 550713654103033621L;
    private final NodeDO<T> defaultRoot;
    
    /*
     * ********** Constructor ********
     */

    public Tree(WidgetPage<?> page, String id, NodeDO<T> root) {
        super(page, id);
        this.defaultRoot = root;
    }
    
     public Tree(WidgetPage<?> page, String id) {
        this(page, id, null);
    }
    
    /*
     *********** Override if required ****
     */
     
     /**
      * GUI representation of object
      * @return
      */
     protected Map<String,String> getObjectRepresentation(T object) {
    	 return null;
     }
     
     /*
      * ****** Inherited methods *****
      */

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
        return (Class) Tree.class;
    }
    
    @Override
	public TreeData<T> createNewSession() {
    	TreeData<T> opt = new TreeData<T>(this);
    	opt.setTreeRoot(defaultRoot);
//    	if (defaultRoot != null) opt = new TreeData(this, defaultRoot);
//    	else opt = new TreeData(this, new NodeDO("fill the Tree"));
    	return opt;
    }
    
    /*
     * ********* Public methods *********
     */

    public NodeDO<T> getTreeRoot(OgemaHttpRequest req){
        return getData(req).getTreeRoot();
    }
    
    public void setTreeRoot(NodeDO<T> root, OgemaHttpRequest req) {
    	getData(req).setTreeRoot(root);
    }
    

}
