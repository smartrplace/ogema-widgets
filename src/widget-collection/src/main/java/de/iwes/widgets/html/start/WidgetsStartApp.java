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

package de.iwes.widgets.html.start;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.security.WebAccessManager;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.html.accordion.Accordion;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.autocomplete.Autocomplete;
import de.iwes.widgets.html.buttonconfirm.ButtonConfirm;
import de.iwes.widgets.html.buttonrow.ConfigButtonRow;
import de.iwes.widgets.html.calendar.datepicker.Datepicker;
import de.iwes.widgets.html.complextable.DynamicTable;
import de.iwes.widgets.html.complextable2.DynamicTable2;
import de.iwes.widgets.html.datatable.DataTable;
import de.iwes.widgets.html.dragdropassign.DragDropAssign;
import de.iwes.widgets.html.emptywidget.EmptyWidget;
import de.iwes.widgets.html.filedownload.FileDownload;
import de.iwes.widgets.html.fileupload.FileUpload;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.checkbox.Checkbox;
import de.iwes.widgets.html.form.dropdown.Dropdown;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.slider.Slider;
import de.iwes.widgets.html.form.textfield.TextField;
import de.iwes.widgets.html.html5.Meter;
import de.iwes.widgets.html.icon.Icon;
import de.iwes.widgets.html.listgroup.ListGroup;
import de.iwes.widgets.html.multiselect.Multiselect;
import de.iwes.widgets.html.popup.Popup;
import de.iwes.widgets.html.textarea.TextArea;

@Component(specVersion = "1.2", immediate = true)
@Service(Application.class)
public class WidgetsStartApp implements Application {

	private WebAccessManager wam;

    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public void start(ApplicationManager am) {
    	this.wam = am.getWebAccessManager();
        //register widgets here
        try {
        	final org.ogema.webadmin.AdminWebAccessManager wam = (org.ogema.webadmin.AdminWebAccessManager) this.wam;
//        WidgetsHtmlServlet widgetHtmlServlet = new WidgetsHtmlServlet();
//        wam.registerBasicResource("/ogema/widget/html", widgetHtmlServlet);
	        wam.registerBasicResource("/ogema/widget/alert", getPackageResourceReferenz(Alert.class));
	        wam.registerBasicResource("/ogema/widget/button", getPackageResourceReferenz(Button.class));
	        wam.registerBasicResource("/ogema/widget/label", getPackageResourceReferenz(Label.class));
	        wam.registerBasicResource("/ogema/widget/slider", getPackageResourceReferenz(Slider.class));
	        wam.registerBasicResource("/ogema/widget/datepicker", getPackageResourceReferenz(Datepicker.class));
	        wam.registerBasicResource("/ogema/widget/textfield", getPackageResourceReferenz(TextField.class));
	        wam.registerBasicResource("/ogema/widget/dropdown", getPackageResourceReferenz(Dropdown.class));
	        wam.registerBasicResource("/ogema/widget/checkbox", getPackageResourceReferenz(Checkbox.class));
	        wam.registerBasicResource("/ogema/widget/complextable", getPackageResourceReferenz((Class) DynamicTable.class));
	        wam.registerBasicResource("/ogema/widget/complextable2", getPackageResourceReferenz((Class) DynamicTable2.class));
	       
	        wam.registerBasicResource("/ogema/widget/accordion", getPackageResourceReferenz(Accordion.class));
	        wam.registerBasicResource("/ogema/widget/buttonrow", getPackageResourceReferenz(ConfigButtonRow.class));
	        wam.registerBasicResource("/ogema/widget/popup", getPackageResourceReferenz(Popup.class));
	        wam.registerBasicResource("/ogema/widget/filedownload", getPackageResourceReferenz(FileDownload.class));
	        wam.registerBasicResource("/ogema/widget/fileupload", getPackageResourceReferenz(FileUpload.class));
	        wam.registerBasicResource("/ogema/widget/dragdropassign", getPackageResourceReferenz(DragDropAssign.class));
	        wam.registerBasicResource("/ogema/widget/datatable", getPackageResourceReferenz(DataTable.class));
	        wam.registerBasicResource("/ogema/widget/textarea", getPackageResourceReferenz(TextArea.class));
	        wam.registerBasicResource("/ogema/widget/multiselect", getPackageResourceReferenz(Multiselect.class));
	        wam.registerBasicResource("/ogema/widget/buttonconfirm", getPackageResourceReferenz(ButtonConfirm.class));
	        wam.registerBasicResource("/ogema/widget/emptywidget", getPackageResourceReferenz(EmptyWidget.class)); // experimental
	        wam.registerBasicResource("/ogema/widget/icon", getPackageResourceReferenz(Icon.class)); // experimental
	        wam.registerBasicResource("/ogema/widget/autocomplete", getPackageResourceReferenz(Autocomplete.class)); // experimental
	        wam.registerBasicResource("/ogema/widget/html5", getPackageResourceReferenz(Meter.class));
	        wam.registerBasicResource("/ogema/widget/listgroup", getPackageResourceReferenz((Class) ListGroup.class));
	       	wam.registerBasicResource("/ogema/widget/minified", "de/iwes/widgets/html/minified");
        } catch (NoClassDefFoundError | ClassCastException e) {  // fallback for OGEMA v < 2.1.2
        	wam.registerWebResource("/ogema/widget/alert", getPackageResourceReferenz(Alert.class));
 	        wam.registerWebResource("/ogema/widget/button", getPackageResourceReferenz(Button.class));
 	        wam.registerWebResource("/ogema/widget/label", getPackageResourceReferenz(Label.class));
 	        wam.registerWebResource("/ogema/widget/slider", getPackageResourceReferenz(Slider.class));
 	        wam.registerWebResource("/ogema/widget/datepicker", getPackageResourceReferenz(Datepicker.class));
 	        wam.registerWebResource("/ogema/widget/textfield", getPackageResourceReferenz(TextField.class));
 	        wam.registerWebResource("/ogema/widget/dropdown", getPackageResourceReferenz(Dropdown.class));
 	        wam.registerWebResource("/ogema/widget/checkbox", getPackageResourceReferenz(Checkbox.class));
 	        wam.registerWebResource("/ogema/widget/complextable", getPackageResourceReferenz((Class) DynamicTable.class));
 	        wam.registerWebResource("/ogema/widget/complextable2", getPackageResourceReferenz((Class) DynamicTable2.class));
 	       
 	        wam.registerWebResource("/ogema/widget/accordion", getPackageResourceReferenz(Accordion.class));
 	        wam.registerWebResource("/ogema/widget/buttonrow", getPackageResourceReferenz(ConfigButtonRow.class));
 	        wam.registerWebResource("/ogema/widget/popup", getPackageResourceReferenz(Popup.class));
 	        wam.registerWebResource("/ogema/widget/filedownload", getPackageResourceReferenz(FileDownload.class));
 	        wam.registerWebResource("/ogema/widget/fileupload", getPackageResourceReferenz(FileUpload.class));
 	        wam.registerWebResource("/ogema/widget/dragdropassign", getPackageResourceReferenz(DragDropAssign.class));
 	        wam.registerWebResource("/ogema/widget/datatable", getPackageResourceReferenz(DataTable.class));
 	        wam.registerWebResource("/ogema/widget/textarea", getPackageResourceReferenz(TextArea.class));
 	        wam.registerWebResource("/ogema/widget/multiselect", getPackageResourceReferenz(Multiselect.class));
 	        wam.registerWebResource("/ogema/widget/buttonconfirm", getPackageResourceReferenz(ButtonConfirm.class));
 	        wam.registerWebResource("/ogema/widget/emptywidget", getPackageResourceReferenz(EmptyWidget.class)); // experimental
 	        wam.registerWebResource("/ogema/widget/icon", getPackageResourceReferenz(Icon.class)); // experimental
 	        wam.registerWebResource("/ogema/widget/autocomplete", getPackageResourceReferenz(Autocomplete.class)); // experimental
 	        wam.registerWebResource("/ogema/widget/html5", getPackageResourceReferenz(Meter.class));
 	        wam.registerWebResource("/ogema/widget/listgroup", getPackageResourceReferenz((Class) ListGroup.class));
 	       
 	       	wam.registerWebResource("/ogema/widget/minified", "de/iwes/widgets/html/minified");
        }
        
        wam.registerStartUrl(null); 
    }

    @Override
    public void stop(AppStopReason asr) {
    	wam.unregisterWebResource("/ogema/widget/html");
    	wam.unregisterWebResource("/ogema/widget/button");
    	wam.unregisterWebResource("/ogema/widget/label");
    	wam.unregisterWebResource("/ogema/widget/datepicker");
    	wam.unregisterWebResource("/ogema/widget/slider");
    	wam.unregisterWebResource("/ogema/widget/textfield");
    	wam.unregisterWebResource("/ogema/widget/dropdown");
    	wam.unregisterWebResource("/ogema/widget/checkbox");
    	wam.unregisterWebResource("/ogema/widget/alert");
    	wam.unregisterWebResource("/ogema/widget/complextable");
    	wam.unregisterWebResource("/ogema/widget/complextable2");
        wam.unregisterWebResource("/ogema/widget/accordion");
        wam.unregisterWebResource("/ogema/widget/buttonrow");
        wam.unregisterWebResource("/ogema/widget/popup");
        wam.unregisterWebResource("/ogema/widget/dragdropassign");
        wam.unregisterWebResource("/ogema/widget/datatable");
        wam.unregisterWebResource("/ogema/widget/textarea");
        wam.unregisterWebResource("/ogema/widget/multiselect");
        wam.unregisterWebResource("/ogema/widget/buttonconfirm");
        wam.unregisterWebResource("/ogema/widget/emptywidget");
        wam.unregisterWebResource("/ogema/widget/icon");
        wam.unregisterWebResource("/ogema/widget/autocomplete");
        wam.unregisterWebResource("/ogema/widget/html5");
        wam.unregisterWebResource("/ogema/widget/listgroup");
        wam.unregisterWebResource("/ogema/widget/minified");
    }

    private static final String getPackageResourceReferenz(Class<? extends OgemaWidgetBase<?>> claze) {
        return claze.getPackage().getName().replace(".", "/");
    }
}
