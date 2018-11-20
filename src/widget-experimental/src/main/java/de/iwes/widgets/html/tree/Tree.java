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
