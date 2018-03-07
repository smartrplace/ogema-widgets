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

package de.iwes.widgets.html.icon;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/**
 * An icon, from a set of predefined options (see {@link IconType}). All icons provided are
 * in the public domain, custom types can be defined as well. <br>
 * It is possible to use the icon as a button, i.e. to trigger a POST request when the user clicks
 * the icon, and then trigger any kind of action.
 */
public class Icon extends OgemaWidgetBase<IconData> {

	private static final long serialVersionUID = 1L;
	private IconType defaultIconType = null;
	private float defaultScale = Float.NaN;
	private boolean defaultEnabled = false;
	private boolean defaultOgemaServletSource = false;

	/*********** Constructor **********/

    public Icon(WidgetPage<?> page, String id) {
        super(page, id);
    }

    public Icon(WidgetPage<?> page, String id, boolean globalWidget) {
        super(page, id, globalWidget);
    }

//    public Icon(WidgetPageI<?> page, String id, OgemaHttpRequest req) {
//        super(page, id, req);
//    }

    public Icon(OgemaWidget parent, String id, OgemaHttpRequest req) {
        super(parent, id, req);
    }

    /******* Inherited methods *****/

    @Override
    public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
        return Icon.class;
    }

	@Override
	public IconData createNewSession() {
		return new IconData(this);
	}

	@Override
	protected void setDefaultValues(IconData opt) {
		super.setDefaultValues(opt);
		opt.setIconType(defaultIconType);
		opt.setScale(defaultScale);
		if (defaultEnabled)
			opt.enable();
		else
			opt.disable();
		opt.setOgemaServletSource(defaultOgemaServletSource);
	}

    /******* Public methods ********/

	public void setDefaultIconType(IconType iconType) {
		this.defaultIconType = iconType;
	}

	public IconType getIconType(OgemaHttpRequest req) {
		return getData(req).getIconType();
	}

	public void setIconType(IconType iconType,OgemaHttpRequest req) {
		getData(req).setIconType(iconType);
	}

	/**
	 * Set the default scale of the icon. A value of 1 corresponds to the original
	 * size. Note that displayed size may differ from the original size, if this
	 * value has not been set. Reset to default by passing <code>Float.NaN</code>
	 * for the scale parameter.
	 * @param scale
	 */
	public void setDefaultScale(float scale) {
		this.defaultScale = scale;
	}

	/**
	 * Returns <code>Float.NaN</code> if this has not been set
	 * @param req
	 * @return
	 */
	public float getScale(OgemaHttpRequest req) {
		return getData(req).getScale();
	}

	/**
	 * Set the scale of the icon. A value of 1 corresponds to the original
	 * size. Note that displayed size may differ from the original size, if this
	 * value has not been set. Reset to default by passing <code>Float.NaN</code>
	 * for the scale parameter.
	 * @param scale
	 * @param req
	 */
	public void setScale(float scale, OgemaHttpRequest req) {
		getData(req).setScale(scale);
	}

	/**
	 * Call this once to trigger POST requests when the user clicks the icon.
	 */
	public void enable() {
		this.defaultEnabled = true;
	}

	/**
	 * See {@link #setOgemaServletSource(boolean, OgemaHttpRequest)}
	 * @param isOgemaServlet
	 */
	public void setDefaultOgemaServletSource(boolean isOgemaServlet) {
		this.defaultOgemaServletSource = isOgemaServlet;
	}

	/**
	 * Set this to true if the icon will be delivered through an OGEMA servlet. In this case,
	 * the request to retrieve the icon will include the OGEMA One-Time-Password; otherwise,
	 * the servlet request would be denied with a 403 (Forbidden) HTTP response.
	 * @param isOgemaServlet
	 * @param req
	 */
	public void setOgemaServletSource(boolean isOgemaServlet, OgemaHttpRequest req) {
		getData(req).setOgemaServletSource(isOgemaServlet);
	}

}
