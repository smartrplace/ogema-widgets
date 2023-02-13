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
package de.iwes.widgets.html.form.dropdown;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.template.LabelledItem;

public class DropdownData extends WidgetData {
	
	// TODO add predefined styles
	
    protected final List<DropdownOption> options = new LinkedList<>();
    public final static String EMPTY_OPT_ID = "___EMPTY_OPT___";
    protected boolean addEmptyOpt = false;
    protected String emptyOptLabel = "";
    
	/*********** Constructor **********/
	
	public DropdownData(Dropdown dropdown) {
		super(dropdown);
	}
	
	/******* Inherited methods ******/
	
	@Override
    public JSONObject retrieveGETData(OgemaHttpRequest req) {
        JSONObject result = new JSONObject();

        JSONArray array = new JSONArray();
        writeLock(); // sorting... FIXME
        try {
        	final Comparator<DropdownOption> comparator = ((Dropdown) widget).comparator;
        	if (comparator != null)
        		Collections.sort(options, comparator);
	        List<DropdownOption> optionLoc;
//	        if(req.widgetObject != null) {
//	        	optionLoc = (List<DropdownOption>) req.widgetObject;
//	        } else {
        	optionLoc = options;
//	        }
	        for (DropdownOption o : optionLoc) {
	            array.put(o.getJSON(req.getLocale()));
	        }
        } finally {
        	writeUnlock();
        }

        result.put("options", array);
        return result;
    }

    @Override
    public JSONObject onPOST(String json, OgemaHttpRequest req) {
        JSONObject obj = new JSONObject(json);
        if (obj.has("data") ) {
        	JSONArray arr = obj.getJSONArray("data");
        	List<String> selectedOptions = new ArrayList<String>();
        	for (int i=0;i<arr.length();i++) {
				String value = arr.getString(i);
        		selectedOptions.add(unescapeHtmlAttributeValue(value));
//	        		setSelectionStatus(value, true); 	// does not unselect previously selected options;
        	}
//        	selectMultipleOptions(selectedOptions);
        	if (selectedOptions.isEmpty()) {
        		if (!getOptions().isEmpty()) {
        			LoggerFactory.getLogger(getClass()).warn("No item selected in dropdown, although options are available. This will lead to ill-defined behaviour");
        		}
        		return obj;
        	}
        	String option1 = selectedOptions.get(0);
        	selectSingleOption(option1);
    		JSONObject result = new JSONObject();
    		result.put("data", option1);
    		return result;
        }
        return obj;
    }
    
    @Override
    protected String getWidthSelector() {
    	return ">div";
    }
	
	/********** Public methods **********/
    
    public List<DropdownOption> getOptions() {
    	readLock();
    	try {
    		return new LinkedList<DropdownOption>(options);
    	} finally {
    		readUnlock();
    	}
    }

    public void setOptions(Collection<DropdownOption> options) {
    	boolean selectedFound = false;
//   		boolean emptyFound = false;
//    		for (DropdownOption opt: options) {
//    			if (opt.getValue().equals(EMPTY_OPT_ID)) 
//    				emptyFound = true;
//    			if (opt.isSelected())
//    				selectedFound = true;
//    		}
//    	if (!emptyFound && addEmptyOpt) {
//    		options.add(new DropdownOption(EMPTY_OPT_ID, "", !selectedFound));
//    		selectedFound = true;
//    	}
//    	if (!selectedFound && !options.isEmpty()) 
//    		options.iterator().next().select(true);
//    	writeLock();
//    	try {
//	    	this.options.clear();
//	        this.options.addAll(options);
//    	} finally {
//    		writeUnlock();
//    	}
    	boolean emptyFound = false;
    	writeLock();
		try {
	    	this.options.clear();
			for (DropdownOption opt: options) {
				if (opt.id().equals(EMPTY_OPT_ID)) 
					emptyFound = true;
				if (opt.isSelected())
					selectedFound = true;
				try {
					this.options.add(opt.clone());
				} catch (CloneNotSupportedException e) {
					throw new RuntimeException(e);
				}
			}
			if (!emptyFound && addEmptyOpt) {
				this.options.add(new DropdownOption(EMPTY_OPT_ID, emptyOptLabel, !selectedFound));
				selectedFound = true;
			}
			if (!selectedFound && !options.isEmpty()) 
				this.options.get(0).select(true);
		} finally {
			writeUnlock();
		}
    }

    public void addOption(String label, String value, boolean selected) {
    	writeLock();
    	try {
    		if (options.isEmpty())
    			selected = true;
    		options.add(new DropdownOption(value, label, selected));
    	} finally {
    		writeUnlock();
    	}
    }
    
    protected void addOption(LabelledItem item, boolean selected) {
    	writeLock();
    	try {
    		if (options.isEmpty())
    			selected = true;
    		options.add(new ProxyDropdownOption(item, selected));
    	} finally {
    		writeUnlock();
    	}
    }
    
    
    public DropdownOption getSelected() {
    	readLock();
    	try {
	    	Iterator<DropdownOption> it = options.iterator();
	    	while (it.hasNext()) {
	    		DropdownOption op = it.next();
	    		if (op.isSelected()) return op;
	    	}
	    	return null;
    	} finally {
    		readUnlock();
    	}
    }
    
    public String getSelectedValue() {
    	DropdownOption selected = getSelected();
    	if (selected == null) return null;
    	return selected.id();
    }
    
    @Deprecated
    public String getSelectedLabel() {
    	DropdownOption selected = getSelected();
    	if (selected == null) return null;
    	return selected.getLabel();
    }

    public String getSelectedLabel(final OgemaLocale locale) {
    	DropdownOption selected = getSelected();
    	if (selected == null) return null;
    	return selected.label(locale);
    }
    
    public void selectSingleOption(String value) {
    	selectSingleOption(value, true);
    }
    
    public void selectSingleOption(String value, boolean checkForDouble) {
    	boolean found = false;
    	writeLock();
    	try {
	        for (DropdownOption o : options) {
	            if (o.id().equals(value)) {
	                o.select(true);
	                found = true;
	                break;
	            } 
//	            else {
//	                o.select(false);
//	            }
	        }
	        // avoid no selection at all
	        if (found && checkForDouble) {
	        	for (DropdownOption o : options) {
	                if (!o.id().equals(value)) 
	                    o.select(false);   
	            }
	        }
    	} finally {
    		writeUnlock();
    	}
        
    }
    
    /**
     * Here value = label for all entries
     * @param values
     */
    // name conflict with method in TemplateDropdown
//    public void update(Collection<String> values) {
//    	Map<String,String> map = new HashMap<>();
//    	for (String value: values) {
//    		map.put(value, value);
//    	}
//    	update(map);
//    }

    public void update(Map<String, String> values) {
    	update(values, null);
    }
    
    /**
     * Update the items, and select a specific one, in case the old
     * selected item is no longer included
     * @param values
     * 		Map&lt;value,label&gt;
     * @param select option to select if no option was selected before calling the method
     * 		or if the option previously selected is not in the set of options anymore.
     * 		If the previously selected option is till available, the parameter select
     * 		is not relevant. 
     */
    public void update(Map<String,String> values, String select) {
    	if (addEmptyOpt && !values.keySet().contains(EMPTY_OPT_ID))
    		values.put(EMPTY_OPT_ID, emptyOptLabel);
    	writeLock();
    	try {
	    	Iterator<DropdownOption> it = options.iterator();
	    	while (it.hasNext()) {	
	    		DropdownOption opt = it.next();
	    		if (!values.keySet().contains(opt.id())) { 
//	    			removeOption(opt.getValue());
	    			it.remove();
	    		}
	    	}
//	    	boolean empty = getOptions().size() == 0;
	    	boolean selectedFound = getSelected()  != null;
	    	for (Map.Entry<String, String> entry: values.entrySet()) {
	    		String newVal = entry.getKey();
	    		boolean found = false; 
	    		for (DropdownOption opt : options) {
	    			if (opt.id().equals(newVal)) {
	    				found = true;
	    				break;
	    			}
	    		}
	    		if (!found) {
//	    			addOption(newVal, entry.getValue(), false);
	    			options.add(new DropdownOption(newVal, entry.getValue(), false));
	    		}
	    	}
	    	if (!selectedFound) {
	    		if (select != null)
	    			selectSingleOption(select);
	    		if (select == null || getSelected()==null) {
		    		if (addEmptyOpt) {
		    			selectSingleOption(EMPTY_OPT_ID,false);
		    		}
		    		else {
			    		if (!options.isEmpty()) {
			    			options.get(0).select(true);
			    		}
		    		}
	    		}
	    	}
    	} finally {
    		writeUnlock();
    	}
    	
    }
      
    // XXX
    public void selectMultipleOptions(Collection<String> selectedOptions) {
    	writeLock();
    	try {
	    	Iterator<DropdownOption> it = options.iterator();
	    	while (it.hasNext()) {
	    		DropdownOption opt = it.next();
	    		opt.select(selectedOptions.contains(opt.id()));
	    	}
    	} finally {
			writeUnlock();
		}
    }

    public void changeSelection(String value, boolean newState) {
    	writeLock();
    	try {
	        for (DropdownOption o : options) {
	            if (o.id().equals(value)) {
	                o.select(newState);
	                return;
	            }
	        }
    	} finally {
			writeUnlock();
		}
    }

    public void removeOption(String value) {
    	writeLock();
    	try {
	    	Iterator<DropdownOption> it = options.iterator();
	    	while (it.hasNext()) {
	        	DropdownOption o = it.next();
	            if (o.id().equals(value)) {
	                it.remove();
	                break;
	            }
	        }
	    	DropdownOption selected = getSelected();
	    	if (selected == null && !options.isEmpty()) {
	    		options.get(0).select(true);
	    	}
    	} finally {
			writeUnlock();
		}
    }

    
    public boolean containsValue(String value) {
	    readLock();
	    try {
    	Iterator<DropdownOption> it = options.iterator();
    	while (it.hasNext()) {
        	DropdownOption o = it.next();
        	if (o.id().equals(value))
        		return true;
    	}
    	return false;
	    } finally {
			readUnlock();
		}
    }

	public boolean isAddEmptyOption() {
		return addEmptyOpt;
	}

	public void setAddEmptyOption(boolean addEmptyOpt) {
		this.addEmptyOpt = addEmptyOpt;
	}
	
	public void setAddEmptyOption(boolean addEmptyOpt, String emptyOptLabel) {
		this.addEmptyOpt = addEmptyOpt;
		this.emptyOptLabel = emptyOptLabel;
	}

    
    
}
