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
