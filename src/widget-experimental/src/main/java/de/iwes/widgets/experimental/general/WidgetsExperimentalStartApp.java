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

package de.iwes.widgets.experimental.general;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.security.WebAccessManager;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.html.appbox.AppBox;
import de.iwes.widgets.html.chart.C3Chart;
import de.iwes.widgets.html.geomap.GeoMap;
import de.iwes.widgets.html.listselect.ListSelect;
import de.iwes.widgets.html.plotc3.PlotC3;
import de.iwes.widgets.html.plotflot.PlotFlot;
import de.iwes.widgets.html.plotmorris.PlotMorris;
import de.iwes.widgets.html.plotnvd3.PlotNvd3;
import de.iwes.widgets.html.tilearea.TileArea;
import de.iwes.widgets.html.tree.Tree;
import de.iwes.widgets.html.urlredirect.UrlRedirect;

@Component(specVersion = "1.2", immediate = true)
@Service(Application.class)
public class WidgetsExperimentalStartApp implements Application {

	private WebAccessManager wam;

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public void start(ApplicationManager am) {
    	this.wam = am.getWebAccessManager();
    	try {
    		final org.ogema.webadmin.AdminWebAccessManager wam = (org.ogema.webadmin.AdminWebAccessManager) this.wam;;
        //register widgets here
        //WidgetsHtmlServlet widgetHtmlServlet = new WidgetsHtmlServlet();
	        wam.registerBasicResource("/ogema/widget/tree", getPackageResourceReferenz((Class) Tree.class));
	        wam.registerBasicResource("/ogema/widget/appbox", getPackageResourceReferenz(AppBox.class));
	        wam.registerBasicResource("/ogema/widget/tilearea", getPackageResourceReferenz(TileArea.class));
	        wam.registerBasicResource("/ogema/widget/plotlibs", "de/iwes/widgets/html/plot/libs");
	        wam.registerBasicResource("/ogema/widget/chart", getPackageResourceReferenz(C3Chart.class));
	        wam.registerBasicResource("/ogema/widget/c3chart", getPackageResourceReferenz(C3Chart.class)); // experimental
	        wam.registerBasicResource("/ogema/widget/plotc3", getPackageResourceReferenz(PlotC3.class)); // experimental
	        wam.registerBasicResource("/ogema/widget/plotmorris", getPackageResourceReferenz(PlotMorris.class)); // experimental
	        wam.registerBasicResource("/ogema/widget/plotflot", getPackageResourceReferenz(PlotFlot.class)); // experimental
	        wam.registerBasicResource("/ogema/widget/plotnvd3", getPackageResourceReferenz(PlotNvd3.class)); // experimental
	        wam.registerBasicResource("/ogema/widget/listselect", getPackageResourceReferenz(ListSelect.class));
	        wam.registerBasicResource("/ogema/widget/urlredirect", getPackageResourceReferenz(UrlRedirect.class));
	        wam.registerBasicResource("/ogema/widget/geomap", getPackageResourceReferenz(GeoMap.class));
    	} catch (NoClassDefFoundError | ClassCastException e) { // fallback for OGEMA v < 2.1.2
    		wam.registerWebResource("/ogema/widget/tree", getPackageResourceReferenz((Class) Tree.class));
 	        wam.registerWebResource("/ogema/widget/appbox", getPackageResourceReferenz(AppBox.class));
 	        wam.registerWebResource("/ogema/widget/tilearea", getPackageResourceReferenz(TileArea.class));
 	        wam.registerWebResource("/ogema/widget/plotlibs", "de/iwes/widgets/html/plot/libs");
 	        wam.registerWebResource("/ogema/widget/chart", getPackageResourceReferenz(C3Chart.class));
 	        wam.registerWebResource("/ogema/widget/c3chart", getPackageResourceReferenz(C3Chart.class)); // experimental
 	        wam.registerWebResource("/ogema/widget/plotc3", getPackageResourceReferenz(PlotC3.class)); // experimental
 	        wam.registerWebResource("/ogema/widget/plotmorris", getPackageResourceReferenz(PlotMorris.class)); // experimental
 	        wam.registerWebResource("/ogema/widget/plotflot", getPackageResourceReferenz(PlotFlot.class)); // experimental
 	        wam.registerWebResource("/ogema/widget/plotnvd3", getPackageResourceReferenz(PlotNvd3.class)); // experimental
 	        wam.registerWebResource("/ogema/widget/listselect", getPackageResourceReferenz(ListSelect.class));
 	        wam.registerWebResource("/ogema/widget/urlredirect", getPackageResourceReferenz(UrlRedirect.class));
 	        wam.registerWebResource("/ogema/widget/geomap", getPackageResourceReferenz(GeoMap.class));
    	}
        wam.registerStartUrl(null); 
    }

    @Override
    public void stop(AppStopReason asr) {
    	wam.unregisterWebResource("/ogema/widget/tree");
    	wam.unregisterWebResource("/ogema/widget/appbox");
    	wam.unregisterWebResource("/ogema/widget/tilearea");
    	wam.unregisterWebResource("/ogema/widget/chart");
        wam.unregisterWebResource("/ogema/widget/c3chart"); 
        wam.unregisterWebResource("/ogema/widget/plotc3"); 
        wam.unregisterWebResource("/ogema/widget/plotmorris");
        wam.unregisterWebResource("/ogema/widget/plotflot");
        wam.unregisterWebResource("/ogema/widget/plotnvd3");
        wam.unregisterWebResource("/ogema/widget/listselect");
        wam.unregisterWebResource("/ogema/widget/urlredirect");
        wam.unregisterWebResource("/ogema/widget/geomap");
    }

    private static String getPackageResourceReferenz(Class<? extends OgemaWidgetBase<?>> claze) {
        return claze.getPackage().getName().replace(".", "/");
    }
}
