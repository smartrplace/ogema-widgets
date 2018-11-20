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
package de.iwes.widgets.html.icon;

import java.net.URI;
import java.util.Arrays;

import org.json.JSONObject;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.WidgetStyle;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class IconData extends WidgetData {

	public static final WidgetStyle<Icon> LINK_CURSOR_STYLE = new WidgetStyle<Icon>("icon img", Arrays.asList("linkElement"),0);

	private IconType iconType = null;
	private float scale = Float.NaN;
	private boolean isOgemaServlet = false;

	/*
	 ********** Constructor *********
	 */

	public IconData(Icon icon) {
		super(icon);
	}

	/*
	 ******* Inherited methods *****
	 */

    @Override
    public JSONObject retrieveGETData(OgemaHttpRequest req) {
    	JSONObject obj = new JSONObject();
        if (iconType != null) {
        	obj.put("iconType", iconType.getBrowserPath());
        	if (!Float.isNaN(scale)) {
        		obj.put("scale", scale * 100);
        	}
        	if (isOgemaServlet && !isAbsolutePath(iconType.getBrowserPath()))
        		obj.put("ogemaServlet", true);
        }
        return obj;
    }

    // triggered when user clicks on the icon, if it is enabled
    @Override
    public JSONObject onPOST(String data, OgemaHttpRequest req) throws UnsupportedOperationException {
    	if (isDisabled())
    		throw new UnsupportedOperationException();
    	return new JSONObject();
    }

    /**
     * Try to ensure that we don't send the One-Time-Password to external servers!
     * @param path0
     * @return
     */
    // see https://stackoverflow.com/questions/10687099/how-to-test-if-a-url-string-is-absolute-or-relative
    private final static boolean isAbsolutePath(final String path0) {
    	final String path = path0.toLowerCase().trim();
    	if (path == null || path.isEmpty())
    		return true;
    	try {
    		if (new URI(path).isAbsolute()) // this is not sufficient, though: https://stackoverflow.com/questions/4390800/determine-if-a-string-is-absolute-url-or-relative-url-in-java
    			return true;
    	} catch (Exception ok) {}
    	if (path.indexOf("://") > 0)
    		return true;
    	if (path.startsWith("//")) // URL is protocol-relative (= absolute)
    		return true;
    	if (path.indexOf('.') == -1) // URL does not contain a dot, i.e. no TLD (= relative, possibly REST)
    		return false;
    	if (path.indexOf('/') == -1) // URL does not contain a single slash (= relative)
    		return false;
    	if (path.indexOf(':') > path.indexOf('/')) // The first colon comes after the first slash (= relative)
    		return false;
        if (path.indexOf("://") < path.indexOf('.')) // Protocol is defined before first dot (= absolute)
        	return true;
        return false; // Anything else must be relative
    }

	/*
	 ********** Public methods *********
	 */


	public IconType getIconType() {
		return iconType;
	}

	public void setIconType(IconType iconType) {
		this.iconType = iconType;
	}


	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	@Override
	protected String getWidthSelector() {
		return ">#icon>img";
	}

	protected void setOgemaServletSource(boolean isOgemaServlet) {
		this.isOgemaServlet = isOgemaServlet;
	}

}