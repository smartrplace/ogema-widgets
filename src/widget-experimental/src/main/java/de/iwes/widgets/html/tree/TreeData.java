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

import org.json.JSONObject;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class TreeData<T> extends WidgetData {

    private NodeDO<T> root;
    
    /*********** Constructor **********/

    public TreeData(Tree<T> tree) {
        super(tree);
    }
    
    /******* Inherited methods ******/

    @Override
    public JSONObject retrieveGETData(OgemaHttpRequest req) {
        JSONObject result = new JSONObject();
        readLock();
        try {
	        if (root != null)
	        	result.put("root", getJSON(root));
        } finally {
        	readUnlock();
        }
        return result;
    }

    @Override
    public JSONObject onPOST(String data, OgemaHttpRequest req) {
        throw new UnsupportedOperationException();
    }
    
    /********** Public methods **********/
    
    public NodeDO<T> getTreeRoot(){
    	readLock();
    	try {
    		return root;
    	} finally {
    		readUnlock();
    	}
    }
   
    public void setTreeRoot(NodeDO<T> root) {
    	writeLock();
    	try {
    		this.root = root;
    	} finally {
    		writeUnlock();
    	}
    }

    @SuppressWarnings("unchecked")
	public JSONObject getJSON(TreeElement<T> node) {
        JSONObject result = new JSONObject();
        if (node instanceof NodeDO) {
	        JSONObject jsonChilds = new JSONObject();
	        JSONObject jsonLeaf = new JSONObject();
	
	        for (NodeDO<T> child : ((NodeDO<T>) node).getChilds()) {
	            jsonChilds.put(child.id(), getJSON(child));
	//            jsonChilds.put("glyphicon", child.getGlyphicon()); 
	//            jsonChilds.put("css", child.getCss());
	        }
	
	        for (LeafDO<T> leaf : ((NodeDO<T>) node).getData()) {
	            jsonLeaf.put(leaf.id(), getJSON(leaf));
	//            jsonLeaf.put("glyphicon", leaf.getGlyphicon());
	//            jsonLeaf.put("css", leaf.getCss());
	        }
	
	        if (jsonChilds.length() != 0) {
	            result.put("childs", jsonChilds);
	        }
	
	        if (jsonLeaf.length() != 0) {
	            result.put("data", jsonLeaf);
	        }
        }
        else if (node instanceof LeafDO) {
        	result.put("value", ((LeafDO<T>) node).getValue());
        }
        T o = node.getObject();
        Map<String,String> properties = (o == null ? null : ((Tree<T>) widget).getObjectRepresentation(o));
        if (properties != null) 
        	result.put("objectRepresentation", properties);
        result.put("glyphicon", node.getGlyphicon());
        result.put("name", node.getName());
        result.put("css", node.getCss());
        return result;
    }
    
    
}
