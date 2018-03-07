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

package de.iwes.widgets.html.listselect;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class ListSelect extends OgemaWidgetBase<ListSelectData> {

	private static final long serialVersionUID = 550753654103033620L;    
    protected String[] defaultHeader = {}; 
    protected final Map<String,String[]> arrayValues;
    /**
     * default: true
     */
    protected boolean responsive;

	/************************** constructors ***********************/
    
    /** Default: session dependent */
    public ListSelect(WidgetPage<?> page, String id) {
	        this(page, id, new String[0]);
	}
    
	public ListSelect(WidgetPage<?> page, String id, String[] header) {
        super(page, id);
        this.defaultHeader = header;
        this.arrayValues = null;
	}


	/** session-independent versions */
    public ListSelect(WidgetPage<?> page, String id, String[] header, Map<String,String[]> arrayValues) {
		super(page, id, true);
        this.arrayValues = new HashMap<>(arrayValues); // copy map to avoid external changes. Changes have to be made via the public methods provided.
        this.defaultHeader = header;
        // check that header is correct size?
	}
    
    /******* Inherited methods ******/
    
	@Override
	public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
		return ListSelect.class;
	}
	
	@Override
	public ListSelectData createNewSession() {
		ListSelectData opt = new ListSelectData(this, defaultHeader);
		return opt;
	}
    
	@Override
	protected void setDefaultValues(ListSelectData opt) {
		if (arrayValues != null) opt.arrayValues = arrayValues;
		else opt.arrayValues = new LinkedHashMap<String, String[]>();		
		super.setDefaultValues(opt);
	}
	
	
	/************************** public methods ***********************/
	
	public List<String> getSelectedIds(OgemaHttpRequest req) {
		return getData(req).getSelectedIds();	
	}
	
	public Map<String,String[]> getSelectedItems(OgemaHttpRequest req) {		
		return getData(req).getSelectedItems();	
	}
	
	
	public Map<String,String[]> getArrayValues(OgemaHttpRequest req) {
		return getData(req).getArrayValues();
	}
	
	public void addArrayValues(Map<String,String[]> newEntry, OgemaHttpRequest req) {
		getData(req).addArrayValues(newEntry);
	}
	
	public void removeArrayValues(List<String> keys, OgemaHttpRequest req) {
		getData(req).removeArrayValues(keys);
	}
	
	public void clearValues(OgemaHttpRequest req) {
		getData(req).clearValues();
	}

	public void setArrayValues(Map<String,String[]> arrayValues, String[] header, OgemaHttpRequest req) {
		getData(req).setArrayValues(arrayValues, header);
	}

	public String[] getHeader(OgemaHttpRequest req) {
		return getData(req).getHeader();
	}

	public boolean isResponsive(OgemaHttpRequest req) {
		return getData(req).isResponsive();
	}
	
    public String[] getCss(OgemaHttpRequest req) {
		return getData(req).getCss();
	}
    
    @Deprecated
	public void setCss(String[] css, OgemaHttpRequest req) {
    	getData(req).setCss(css);
	}
	
}