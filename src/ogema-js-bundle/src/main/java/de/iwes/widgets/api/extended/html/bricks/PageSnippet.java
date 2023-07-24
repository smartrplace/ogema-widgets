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
package de.iwes.widgets.api.extended.html.bricks;

import java.io.IOException;
import java.util.Collection;

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
	private int defaultUpdateMode = 0;

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
	protected void setDefaultValues(PageSnippetData data) {
		super.setDefaultValues(data);
		data.setUpdateMode(this.defaultUpdateMode);
		
	}
	    
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

	/*
	@Override
	public PageSnippet remove(String html,OgemaHttpRequest req) {
		getData(req).remove(html);
		return this;
	}
	*/
	
	@Override
	public PageSnippet remove(HtmlItem item,OgemaHttpRequest req) {
		getData(req).remove(item);
		return this;
	}
	
	@Override
	public PageSnippet remove(OgemaWidget widget,OgemaHttpRequest req) {
		getData(req).remove(widget);
		return this;
	}
	
	public PageSnippet removeWidgets(Collection<OgemaWidget> widgets,OgemaHttpRequest req) {
		getData(req).removeWidgets(widgets);
		return this;
	}
	
	public PageSnippet removeItems(Collection<HtmlItem> items, OgemaHttpRequest req) {
		getData(req).removeItems(items);
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
	
	public int getUpdateMode(OgemaHttpRequest req) {
		return getData(req).getUpdateMode();
	}
	
	/**
	 * 
	 * @param mode 0: default legacy mode; 1: sub-content preserving mode; try not to destroy subwidgets and other content on update
	 * @param req
	 */
	public void setUpdateMode(int mode, OgemaHttpRequest req) {
		getData(req).setUpdateMode(mode);
	}
	
	/**
	 * 
	 * @param mode 0: default legacy mode; 1: sub-content preserving mode; try not to destroy subwidgets and other content on update
	 * @param req
	 */
	public void setDefaultUpdateMode(int mode) {
		if (mode != 0 && mode != 1)
			throw new IllegalArgumentException("Invalid update mode " + mode + ", expecting 0 or 1.");
		this.defaultUpdateMode = mode;
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
