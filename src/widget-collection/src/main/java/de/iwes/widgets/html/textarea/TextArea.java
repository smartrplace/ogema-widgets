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
    
}
