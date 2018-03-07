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

package de.iwes.widgets.reswidget.scheduleviewer.api;

import java.util.Collection;
import java.util.List;

import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.widgets.api.widgets.html.PageSnippetI;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.multiselect.TemplateMultiselect;
import de.iwes.widgets.html.plotflot.FlotConfiguration;
import de.iwes.widgets.reswidget.scheduleplot.flot.SchedulePlotFlot;

/**
 * A schedule viewer, consisting of a Multiselect widget that lets the user choose
 * from a set of schedules/time series, Datepickers for the start and end time, and
 * a SchedulePlot widget, that displays the selected time series. Optionally, 
 * a schedule manipulator can shown as well, allowing the user to manipulate schedule values.<br>
 * 
 * Note: this is always a global widget, in particular it cannot be added as a subwidget
 * to a non-global widget. Several of the subwidgets are non-global, though, such as the
 * time series selector.<br>
 * 
 * Besides the {{@link #getSchedules(OgemaHttpRequest)} and {@link #getSelectedSchedules(OgemaHttpRequest)}
 * methods, the basic implementation ScheduleViewerBasic also possesses a methods 
 * <code>setSchedules(Collection,OgemaHttpRequest)</code> and
 * <code>setDefaultSchedules(Collection)</code>.
 * These are not supported by all implementations, however, because some widgets determine the schedules
 * to be displayed autonomously. This applies in particular to the ResourceScheduleViewer and the 
 * PatternScheduleViewer, plus its variants.
 * 
 * @param <T> the type of time series to be displayed.
 */
public interface ScheduleViewer<T extends ReadOnlyTimeSeries> extends PageSnippetI {

	/**
	 * Get a reference to the schedule selector widget. 
	 * @return
	 */
	TemplateMultiselect<T> getScheduleSelector();
	
	/**
	 * Get a reference to the schedule plot widget
	 * @return
	 */
	SchedulePlotFlot getSchedulePlot();
	
	/**
	 * Get all schedules for a particular session.
	 * @param req
	 * @return
	 */
	List<T> getSchedules(OgemaHttpRequest req);
	
	/**
	 * Get all selected schedules in a particular session.
	 * @param req
	 * @return
	 */
	List<T> getSelectedSchedules(OgemaHttpRequest req);
	
	/**
	 * Set selected time series server-side
	 * @param selected
	 * @param req
	 */
	void selectSchedules(Collection<T> selected, OgemaHttpRequest req);
	
	/**
	 * Set the start time for a particular session
	 * @param start
	 * @param req
	 */
	void setStartTime(long start, OgemaHttpRequest req);
	
	/**
	 * Set the end time for a particular session
	 * @param end
	 * @param req
	 */
	void setEndTime(long end, OgemaHttpRequest req);
	
	/**
	 * Get the selected time series
	 * @param req
	 * @return
	 */
	List<T> getSelectedItems(OgemaHttpRequest req);
	
	/**
	 * Set default plot configurations
	 * @return
	 */
	FlotConfiguration getDefaultPlotConfiguration();
	
	/**
	 * Set session-specific plot configurations
	 * @param req
	 * @return
	 */
	FlotConfiguration getPlotConfiguration(OgemaHttpRequest req);
	
}
