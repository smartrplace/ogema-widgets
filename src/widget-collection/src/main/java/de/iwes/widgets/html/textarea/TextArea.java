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
package de.iwes.widgets.html.textarea;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class TextArea extends OgemaWidgetBase<TextAreaData>  {
    
    private static final long serialVersionUID = 7367326133405921539L;
    private String defaultText = "Lorem ipsum";
    private int defaultRows = 5;
    private int defaultCols = 30;

    /*********** Constructors **********/
    
    public TextArea(WidgetPage<?> page, String id) {
    	this(page,id,false);
    }
   
    public TextArea(WidgetPage<?> page, String id, String defaultText) {
    	this(page,id,false);
    	this.defaultText = defaultText;
    }
    
    public TextArea(WidgetPage<?> page, String id, OgemaHttpRequest req) {
    	super(page, id, req);
    }
    
    public TextArea(WidgetPage<?> page, String id, boolean globalWidget) {
    	super(page, id, globalWidget);
    }
   
    public TextArea(OgemaWidget parent, String id, OgemaHttpRequest req) {
    	super(parent,id,req);
    }
    
    /******* Inherited methods ******/
    

    @Override
    public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
        return TextArea.class;
    }

	@Override
	public TextAreaData createNewSession() {
		return new TextAreaData(this);
	}
	
	@Override
	protected void setDefaultValues(TextAreaData opt) {
		opt.setText(defaultText);	
		opt.setCols(defaultCols);
		opt.setRows(defaultRows);
		super.setDefaultValues(opt);
	}

    /******* Public methods ******/
	
	public void setDefaultText(String defaultText) {
		this.defaultText = defaultText;
	}
	
	/**Number of text lines displayed after loading the page. Note that the user can resize the text area
	 * in the browser
	 * @param defaultRows number of lines of text that fit in the text area initially
	 */
	public void setDefaultRows(int defaultRows) {
		this.defaultRows = defaultRows;
	}
	
	/**Number of characters per line displayed after loading the page. Note that the user can resize the text area
	 * in the browser
	 * @param defaultCols number of characters per line that fit in the text area initially
	 */
	public void setDefaultCols(int defaultCols) {
		this.defaultCols = defaultCols;
	}

    public void setText(String text, OgemaHttpRequest req) {
    	getData(req).setText(text);
    }
    
    public String getText(OgemaHttpRequest req) {
    	return getData(req).getText();
    }
    
    public int getCols(OgemaHttpRequest req) {
 		return getData(req).getCols();
 	}

 	public void setCols(int cols, OgemaHttpRequest req) {
 		getData(req).setCols(cols);
 	}

 	public int getRows(OgemaHttpRequest req) {
 		return getData(req).getRows();
 	}

 	public void setRows(int rows, OgemaHttpRequest req) {
 		getData(req).setRows(rows);
 	}
 	
 	public boolean isSelected(OgemaHttpRequest req) {
 		return getData(req).isSelected();
 	}
 	
 	public void setSelected(boolean selected, OgemaHttpRequest req) {
 		getData(req).setSelected(selected);
 	}
    
}
