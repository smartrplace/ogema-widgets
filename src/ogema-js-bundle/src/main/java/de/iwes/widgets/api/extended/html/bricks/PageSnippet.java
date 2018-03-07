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

package de.iwes.widgets.api.extended.html.bricks;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.WidgetPageBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.html.HtmlItem;
import de.iwes.widgets.api.widgets.html.PageSnippetI;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/** This widget can be used to create (a part of) an Html page */
public class PageSnippet extends OgemaWidgetBase<PageSnippetData> implements PageSnippetI {

	private static final long serialVersionUID = 1L;

    /*********** Constructor **********/
	
	/** Default: session-dependent page */
	public PageSnippet(WidgetPage<?> page, String id) {
		this(page, id,false);
	}
	
	/** Create session-independent page by setting globalPage = false	 */
	public PageSnippet(WidgetPage<?> page, String id, boolean globalPage) {
		super(page, id, globalPage);
		super.setDynamicWidget(true);
	}
	
	/** Create session-specific subwidget */
	public PageSnippet(OgemaWidget widget, String id, OgemaHttpRequest req) { // or use page instead of widget?
		super(widget, id, req);
		super.setDynamicWidget(true);
	}
	
    /******* Inherited methods *****/
	    
    @Override
    public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {  // must be overridden by derived class
    	return PageSnippet.class;
    }

	@Override
	public PageSnippetData createNewSession() {	// must be overridden by derived class if generics parameter S differs from PageSnippetOptions
		return new PageSnippetData(this);
	}
	
	@Override
	protected void registerJsDependencies() {
		this.registerLibrary(true, "PageSnippet", "/ogema/widget/bricks/PageSnippet.js");
	}
    
    /******* Public methods ********/

	@Override
	public PageSnippet append (String html,OgemaHttpRequest req) {
		getData(req).append(html);
		return this;
	}
	
	@Override
	public PageSnippet append (HtmlItem item,OgemaHttpRequest req) {
		getData(req).append(item);
		return this;
	}
	
	@Override
	public PageSnippet append (OgemaWidget widget,OgemaHttpRequest req) {
		getData(req).append(widget);
		return this;
	}
	
	@Override
	public PageSnippet linebreak(OgemaHttpRequest req) {
		getData(req).linebreak();
		return this;
	}
	
	@Override
	public WidgetPageBase<?> getPage() {	// needs to be public for PatternPage 
		return super.getPage();
	}
	
	public String getBackgroundImg(OgemaHttpRequest req) {
		return getData(req).getBackgroundImg();
	}

	public void setBackgroundImg(String backgroundImg, OgemaHttpRequest req) {
		getData(req).setBackgroundImg(backgroundImg);
	}
	
	public void clear(OgemaHttpRequest req) {
		getData(req).removeSubWidgets();
	}

	/******* Internal methods ********/
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (req.getParameterMap().containsKey("expired")) {
        	JSONObject results = new JSONObject();
        	results.put("html", getExpiredMsg());
            resp.getWriter().write(results.toString());
            resp.setStatus(200);
			return;
		}
		else super.doGet(req, resp);
	}
	
	private String getExpiredMsg() {
		return "<br><br><div id=\"alert\" class=\"alert alert-success\"><div>Your session has expired. Please reload.</div></div>";
	}
	
}
