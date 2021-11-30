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
package de.iwes.widgets.reswidget.schedulecsvdownload;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.core.recordeddata.RecordedData;
import org.ogema.core.security.WebAccessManager;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.humread.valueconversion.HumanReadableValueConverter;
import org.ogema.humread.valueconversion.HumanReadableValueConverter.LinearTransformation;
import org.ogema.humread.valueconversion.SchedulePresentationData;
import org.ogema.model.chartexportconfig.ChartExportConfig;
import org.ogema.tools.resource.util.ResourceUtils;
import org.ogema.tools.timeseries.implementations.TreeTimeSeries;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.LoggerFactory;

import de.iwes.util.timer.AbsoluteTimeHelper;
import de.iwes.util.timer.AbsoluteTiming;
import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.services.NameService;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.calendar.datepicker.Datepicker;
import de.iwes.widgets.html.emptywidget.EmptyData;
import de.iwes.widgets.html.emptywidget.EmptyWidget;
import de.iwes.widgets.html.filedownload.FileDownload;
import de.iwes.widgets.html.filedownload.FileDownloadData;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.button.RedirectButton;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.schedulemanipulator.ScheduleRowTemplate;
import de.iwes.widgets.resource.timeseries.OnlineTimeSeries;
import de.iwes.widgets.reswidget.scheduleviewer.api.TimeSeriesFilterExtended;

// FIXME avoid creation of temp files?
public class ScheduleCsvDownload<T extends ReadOnlyTimeSeries> extends PageSnippet {
	public static final char[] ILLEGAL_CHARACTERS = { '/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':' };

	protected static final long serialVersionUID = 1L;
	// may be null
	protected final Alert alert; 
	// we need this, because the ScheduleCsvDonwload itself is necessarily global (why?)
	protected final DataWidget dataWidget;
	//may be null
	protected final ChartExportConfig configRes;
	
	public final Button downloadCSVButton;
	public final Button downloadJSONButton;
	public final Button csvConfigButton;
	public final Label csvConfigLabel;
	protected final Label nrItemsLabel;
	protected final Datepicker startPicker;
	protected final Datepicker endPicker;
	protected final FileDownload download;
	protected final boolean showUserInput;
	protected File tempFolder;
	protected static final String JSON = "JSON";
	protected static final String CSV = "CSV";

	/** Maximum samples that are chosen per minimum distance, beyound averaging is used*/
	public static final int MAX_PER_MINDISTANCE = 3;
	
	public ScheduleCsvDownload(WidgetPage<?> page, String id, WebAccessManager wam) {
		this(page, id, wam, null, true, null, null, null);
	}
	public ScheduleCsvDownload(WidgetPage<?> page, String id, WebAccessManager wam, ChartExportConfig configRes) {
		this(page, id, wam, null, true, null, null, configRes);
	}
	
	public ScheduleCsvDownload(WidgetPage<?> page, String id, WebAccessManager wam, Alert alert) {
		this(page, id, wam, null, true, null, null, null);
	}
	
	public ScheduleCsvDownload(WidgetPage<?> page, String id, WebAccessManager wam, Alert alert, 
			boolean showUserInput, Datepicker startPicker, Datepicker endPicker,
			ChartExportConfig configRes) {
		super(page, id, true);
		AccessController.doPrivileged(new PrivilegedAction<Void>() {
			@Override
			public Void run() {
				tempFolder = FrameworkUtil.getBundle(ScheduleCsvDownload.class).getDataFile("temp_download_" + id);
				if (tempFolder.exists()) {
					try {
						if (tempFolder.isDirectory())
							FileUtils.deleteDirectory(tempFolder);
						else
							tempFolder.delete();
					} catch (IOException | IllegalArgumentException | SecurityException e) {
						LoggerFactory.getLogger(ScheduleCsvDownload.class).warn("Temp folder {} could not be deleted",tempFolder,e);
					}
				}
				tempFolder.mkdirs();
				return null;
			}
		});
		this.alert = alert;
		this.configRes = configRes;
		this.dataWidget = new DataWidget(page, id + "_dataWidget");
		this.showUserInput = showUserInput;
		this.nrItemsLabel = new Label(page, id + "_nrItemsLabel") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(String.valueOf(size(req)), req);
			}
			
		};
		
		if(startPicker == null) {
			this.startPicker = new Datepicker(page, id +  "_startPicker") {
	
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onGET(OgemaHttpRequest req) {
					List<T> schedules = getSchedules(req);
					long start = Long.MAX_VALUE;
					for (T schedule: schedules) {
						SampledValue sv = schedule.getNextValue(0);
						if (sv == null)
							continue;
						long t = sv.getTimestamp();
						if (t<start)
							start = t;
					}
					if (start == Long.MAX_VALUE) 
						start = System.currentTimeMillis();
					setDate(start, req);
				}			
			};
		}else {
			this.startPicker = startPicker;
		}
		
		if(endPicker == null) {
			this.endPicker = new Datepicker(page, id + "_endPicker") {
	
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onGET(OgemaHttpRequest req) {
					List<T> schedules = getSchedules(req);
					long end = Long.MIN_VALUE;
					for (T schedule: schedules) {
						SampledValue sv = schedule.getPreviousValue(Long.MAX_VALUE);
						if (sv == null)
							continue;
						long t = sv.getTimestamp();
						if (t>end)
							end = t;
					}
					if (end == Long.MIN_VALUE) 
						end = System.currentTimeMillis();
					setDate(end, req);
				}			
			};
		} else {
			this.endPicker = endPicker;
		}
		
		this.downloadCSVButton = new Button(page, id + "_downloadButtonCSV", "download CSV") {
			private static final long serialVersionUID = -6547975805917435860L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				if (dataWidget.getData(req).isEmpty()) 
					disable(req);
				else 
					enable(req);
			}
	
			@Override
			public void onPrePOST(String data, OgemaHttpRequest req) {
				dataWidget.getData(req).buttonPressed = ScheduleCsvDownload.CSV;
			}
		};
		this.downloadJSONButton = new Button(page, id + "_downloadButtonJSON", "download JSON") {
			private static final long serialVersionUID = -6547975805437435860L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				if (dataWidget.getData(req).isEmpty()) 
					disable(req);
				else 
					enable(req);
			}
			
			@Override
			public void onPrePOST(String data, OgemaHttpRequest req) {
				dataWidget.getData(req).buttonPressed = ScheduleCsvDownload.JSON;
			}
	
		};
		this.download = new FileDownload(page, id + "_download", wam);
		this.csvConfigButton = new RedirectButton(page, id+"csvConfigButton", "CSV Configuration",
				"/de/iwes/tools/schedule/viewer-basic-example/chartconfigPage.html") {
			private static final long serialVersionUID = 1502462320343388254L;
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				if((configRes == null) || (!configRes.showConfigButton().getValue()))
					setWidgetVisibility(false, req);
				else 
					setWidgetVisibility(true, req);
			}
			
			@Override
			public void onPrePOST(String data, OgemaHttpRequest req) {
				dataWidget.getData(req).buttonPressed = ScheduleCsvDownload.JSON;
			}
		};
		this.csvConfigLabel = new Label(page, id+"csvConfigLabel", "CSV Download Configuration Page") {
			private static final long serialVersionUID = -2738979445650418439L;
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				if((configRes == null) || (!configRes.showConfigButton().getValue()))
					setWidgetVisibility(false, req);
				else 
					setWidgetVisibility(true, req);
			}
		};
		buildWidget();
		setDependencies();
	}
	
	public void buildWidget() {
		if(showUserInput) {
			StaticTable tab = new StaticTable(5, 2, new int[] {3,3});
			try {
				AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
					public Void run() throws Exception {;
					tab.setContent(0, 0, System.getProperty("org.ogema.app.timeseries.viewer.expert.gui.csvdownloadschednumselected", "Schedules selected")).setContent(0, 1, nrItemsLabel)
						.setContent(1, 0, System.getProperty("org.ogema.app.timeseries.viewer.expert.gui.csvdownloadstarttime", "Select start time")).setContent(1, 1, startPicker)
						.setContent(2, 0, System.getProperty("org.ogema.app.timeseries.viewer.expert.gui.csvdownloadendtime", "Select end time")).setContent(2, 1, endPicker)
						.setContent(3, 0, downloadCSVButton).setContent(3, 1, downloadJSONButton);
					ScheduleCsvDownload.this.append(tab, null).linebreak(null).append(download, null);
					tab.setContent(4,  0, csvConfigLabel).setContent(4, 1, csvConfigButton);
					return null;
					}
				});
			} catch (PrivilegedActionException e) {
				throw new IllegalStateException(e);
			}
		} else {
			this.append(downloadCSVButton, null).append(download, null);
		}
		this.linebreak(null).append(dataWidget, null);
	}
		
	protected final void setDependencies() {
		this.triggerAction(downloadCSVButton, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		this.triggerAction(downloadJSONButton, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);

		this.triggerAction(nrItemsLabel, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST,1);
		this.triggerAction(startPicker, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST,1);
		this.triggerAction(endPicker, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST,1);
		
		downloadCSVButton.triggerAction(dataWidget, TriggeringAction.POST_REQUEST, TriggeredAction.POST_REQUEST);
		downloadJSONButton.triggerAction(dataWidget, TriggeringAction.POST_REQUEST, TriggeredAction.POST_REQUEST);

		dataWidget.triggerAction(download, TriggeringAction.POST_REQUEST, FileDownloadData.GET_AND_STARTDOWNLOAD);
		if (alert != null)
			dataWidget.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
	}

	public void setSchedules(Collection<T> schedules, OgemaHttpRequest req) {
		dataWidget.getData(req).setSchedules(schedules, null);
	}
	public void setSchedules(Collection<T> schedules, Collection<? extends TimeSeriesFilterExtended> filters,
			OgemaHttpRequest req) {
		dataWidget.getData(req).setSchedules(schedules, filters);
	}
	
	public List<T> getSchedules(OgemaHttpRequest req) {
		return dataWidget.getData(req).getSchedules();
	}

	/*public void addSchedule(T schedule, OgemaHttpRequest req) {
		dataWidget.getData(req).addSchedule(schedule);
	}*/

	public boolean isEmpty(OgemaHttpRequest req) {
		return dataWidget.getData(req).isEmpty();
	}

	public int size(OgemaHttpRequest req) {
		return dataWidget.getData(req).size();
	}	

	protected class DataWidget extends EmptyWidget {

		private static final long serialVersionUID = 1L;

		DataWidget(WidgetPage<?> page, String id) {
			super(page, id);
		}
		
		@Override
		public ScheduleCsvDownloadData<T> createNewSession() {
			return new ScheduleCsvDownloadData<T>(this, ScheduleCsvDownload.this , startPicker, endPicker, download, tempFolder, alert);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public ScheduleCsvDownloadData<T> getData(OgemaHttpRequest req) {
			return (ScheduleCsvDownloadData<T>) super.getData(req);
		}
		
	}

	protected static class ScheduleCsvDownloadData<T extends ReadOnlyTimeSeries> extends EmptyData {
		
		private final ScheduleCsvDownload<?> master;
		// illegal in file names; not necessarily comlete
		// https://stackoverflow.com/questions/893977/java-how-to-find-out-whether-a-file-name-is-valid
		final List<T> schedules = new ArrayList<>();
		private final Datepicker downloadStartPicker;
		private final Datepicker downloadEndPicker;
		private final Path tempFolder;
		private final FileDownload download;
		// may be null
		private final Alert alert;
		public String buttonPressed = "";
		private final List<TimeSeriesFilterExtended> filters = new ArrayList<>();;

		protected ScheduleCsvDownloadData(EmptyWidget snippet, ScheduleCsvDownload<?> master,
				Datepicker start, Datepicker end, FileDownload download, File tempFolder, Alert alert) {
			super(snippet);
			this.downloadStartPicker = start;
			this.downloadEndPicker = end;
			this.tempFolder = tempFolder.toPath();
			this.download = download; 
			this.alert = alert;
			this.master = master;
		}
		
		@Override
		public JSONObject onPOST(String data, OgemaHttpRequest req) {
			try {
				final Path path = generateFile(req, filters);
				download.enable(req);
				download.setFile(path.toFile(), req);
				download.setDeleteFileAfterDownload(true, req);
				if (alert != null)
					alert.setWidgetVisibility(false, req);
			} catch (IOException e) {
				LoggerFactory.getLogger(ScheduleCsvDownload.class).error("Error generating temp file",e);
				if (alert != null)
					alert.showAlert("Error generating CSV file: " + e, false, req);
				download.setFile(null, req);
				download.disable(req);
			}
			return new JSONObject();
		}
		
		protected Path generateFile(OgemaHttpRequest req, List<TimeSeriesFilterExtended> filters) throws IOException {
			
			final List<T> schedules = getSchedules();
			final long start = downloadStartPicker.getDateLong(req); 
			final long end = downloadEndPicker.getDateLong(req);

			boolean exportJSON = buttonPressed.equals(ScheduleCsvDownload.JSON);
			return exportFile(start, end, schedules, tempFolder, "schedules_", exportJSON, filters, null, master.configRes);
		}

		public void setSchedules(Collection<T> schedules, Collection<? extends TimeSeriesFilterExtended> filters) {
			writeLock();
			try {
				this.schedules.clear();
				this.schedules.addAll(schedules);
				if(filters != null) {
					this.filters.clear();
					this.filters.addAll(filters);
				}
			} finally {
				writeUnlock();
			}
		}
		
		public List<T> getSchedules() {
			readLock();
			try {
				return new ArrayList<>(schedules);
			} finally {
				readUnlock();
			}
		}
		
		public boolean isEmpty() {
			readLock();
			try {
				return schedules.isEmpty();
			} finally {
				readUnlock();
			}
		}

		public int size() {
			readLock();
			try {
				return schedules.size();
			} finally {
				readUnlock();
			}
		}

		// XXX this does not work... moved to Buttons' onGET method
		/*
		@Override
		public JSONObject retrieveGETData(OgemaHttpRequest req) {
			if (master.isEmpty(req) && master.showUserInput) {
				master.downloadCSVButton.disable(req);
				master.downloadJSONButton.disable(req);
			}
			else {
				master.downloadCSVButton.enable(req);
				master.downloadJSONButton.enable(req);
			}
			return super.retrieveGETData(req);
		}
		*/
	}	
	
	/**
	 * 
	 * @param start
	 * @param end
	 * @param schedules
	 * @param tempFolder
	 * @param filters may be null
	 * @param exportJSON if false CSV is exported
	 * @return
	 * @throws IOException 
	 */
	public static <S extends ReadOnlyTimeSeries> Path exportFile(long start, long end,
			List<S> schedules, Path tempFolder, String zipBaseName, boolean exportJSON,
			List<TimeSeriesFilterExtended> filters,
			Integer schedCountReportInterval,
			ChartExportConfig configRes) throws IOException {
		if (!Files.exists(tempFolder))
			Files.createDirectories(tempFolder);
		final Path base = Files.createTempDirectory(tempFolder, zipBaseName);

		if (start > end) {
			return null;
		}
		final Path zipFile = tempFolder.resolve(base.getFileName() + ".zip");
        final URI uri = URI.create("jar:" + zipFile.toUri());
        int i = 0;
        try (final FileSystem zipfs = FileSystems.newFileSystem(uri, Collections.singletonMap("create", "true"))) {
			int schedCount = 0;
			int nextReportCount = (schedCountReportInterval!=null)?schedCountReportInterval:-1;
        	
			List<String> ids = null;
			if((!exportJSON) && (configRes != null) && configRes.performFixedStepExport().getValue()) {
				ids = new ArrayList<>();
			}
			for (S rd : schedules) {
				final String id;
				if (rd instanceof RecordedData)
					id = ((RecordedData) rd).getPath();
				else if (rd instanceof Schedule)
					id = ((Schedule) rd).getPath();
				else if (rd instanceof SchedulePresentationData)
					id = ((SchedulePresentationData) rd).getLabel(OgemaLocale.ENGLISH);
				else if (rd instanceof OnlineTimeSeries)
					id = ((OnlineTimeSeries) rd).getResource().getPath();
				else if (rd instanceof TreeTimeSeries)
					id = "TreeTimeSeries_" + i++;
				else
					id = "_" + new BigInteger(65, new Random()).toString(32);
				if(ids != null) {
					ids.add(id);
					continue;
				}
			
				String formatId = System.getProperty("org.ogema.widgets.schedulecsvdownload.formatid");
				if(formatId != null && formatId.contains("HUMREAD")) {
					if(id.contains("TEMPERATURE")|| id.contains("temperatureSensor") || ScheduleRowTemplate.isTemperatureSchedule(rd))
						formatId += "CELSIUS";
				}
				
				String fileFormat = System.getProperty("org.ogema.widgets.schedulecsvdownload.fileformat");
				String filename;
				if(filters != null && fileFormat != null && fileFormat.equals("FULL")) {
					filename = getShortLabel(rd, filters, null, null);
					filename = filename.replace("TemperatureRoomSensor", "TempRS");
				} else
					filename = id.replace("/", "%2F");
				for (char c : ILLEGAL_CHARACTERS) {
					filename = filename.replace(c, '_');
				}
				
				if(exportJSON) {
					writeValuesToFile(base, start, end, zipfs, rd, filename+".json", formatId, null);
				} else {
					writeValuesToFile(base, start, end, zipfs, rd, filename+".csv", formatId, configRes);
				}
				schedCount++;
				if(schedCountReportInterval != null && schedCount > nextReportCount) {
					System.out.println("Exported "+schedCount+" files, now:"+id);
					nextReportCount = schedCount + schedCountReportInterval;
				}
			}
			if(ids != null) {
				writeMultiValuesToFile(base, start, end, zipfs, schedules, ids, "MultiRowExport.csv", configRes);				
			}
		}
		FileUtils.deleteDirectory(base.toAbsolutePath().toFile());
        return zipFile;			
	}
	
	/** Get short Label for display, file names etc.
	 * 
	 * @param schedule
	 * @param filters filters to use. Usually a single filter should provide the short name
	 * @param nameService may be null
	 * @param locale may be null
	 * @return
	 */
	public static String getShortLabel(ReadOnlyTimeSeries schedule,
			List<TimeSeriesFilterExtended> filters, NameService nameService, OgemaLocale locale) {
		for(TimeSeriesFilterExtended filter : filters) {
			if(filter.accept(schedule)) {
				return filter.shortName(schedule);
			}
		}
		
		if(schedule instanceof SchedulePresentationData) {
			return ((SchedulePresentationData) schedule).getLabel(locale);
		}
		
		if (schedule instanceof Schedule) {
			if (nameService != null) {
				String name = nameService.getName((Schedule) schedule, locale);
				if (name != null) {
					return name;
				}
			}
			return ResourceUtils.getHumanReadableName((Schedule) schedule);
		}
		if (schedule instanceof RecordedData) {
			return ((RecordedData) schedule).getPath();
		}
		for(TimeSeriesFilterExtended filter : filters) {
			if(filter.accept(schedule)) {
				return filter.longName(schedule);
			}
		}
		
		throw new IllegalArgumentException("Could not determine schedule label for time series " + schedule +
				". Please provide a Long Name.");
		
	}
	
	protected static <S extends ReadOnlyTimeSeries> void writeValuesToFile(final Path base, final long start, final long end,
			final FileSystem zipfs, S rd,
			String filename, String formatId,
			ChartExportConfig configRes) throws IOException {
		Path file = base.resolve(filename);
		if(filename.endsWith(".csv")) {
			LinearTransformation trans = null;
			if(configRes != null && (!configRes.exportOriginalOGEMAValues().getValue()) &&
					(rd instanceof SchedulePresentationData))
				trans = HumanReadableValueConverter.getTransformationIfNonTrivial((SchedulePresentationData) rd);
			else if((formatId != null) && formatId.contains("CELSIUS"))
				trans = HumanReadableValueConverter.getTransformation(TemperatureResource.class, null);
			toCsvFile(rd.iterator(start,end), file, formatId, configRes, trans);
		} else if(filename.endsWith(".json")) {
			toJSONFile(rd.iterator(start,end), file, filename.replace(".json", ""));
		} else if(filename.endsWith(".python")) {
			//TODO: Does this option make sense?
			return;
		}
		Path pathInZipfile = zipfs.getPath("/" + filename);          
		// copy a file into the zip file
		Files.move(file, pathInZipfile, StandardCopyOption.REPLACE_EXISTING );
	}
	
	protected static class TSConvertData {
		LinearTransformation trans;
		Iterator<SampledValue> it;
		SampledValue lastVal;
		SampledValue nextVal;
		boolean isFinished = false;
		String nanValue;
	}
	/**
	 * 
	 * @param <S>
	 * @param base
	 * @param startIn
	 * @param end
	 * @param zipfs
	 * @param schedules
	 * @param ids
	 * @param filename
	 * @param configRes must be non-null here
	 * @throws IOException
	 */
	protected static <S extends ReadOnlyTimeSeries> void writeMultiValuesToFile(Path base, long startIn, long end,
			FileSystem zipfs, List<S> schedules,
			List<String> ids, String filename, ChartExportConfig configRes) throws IOException {
		Path path = base.resolve(filename);
		List<TSConvertData> tsData = new ArrayList<>();

		long start = AbsoluteTimeHelper.getIntervalStart(startIn, AbsoluteTiming.MINUTE);
		long stepSize = (long) (configRes.fixedTimeStepSeconds().getValue()*1000);
		long halfStep = stepSize/2;
		if(Float.isNaN(stepSize) || stepSize <= 0)
			stepSize = 60000;
		long maxDistance = (long) (configRes.maxValidValueIntervalSeconds().getValue()*1000);
		GeneralConfigCSVExport pconfig = getGeneralConfig(null, configRes);
		
		List<String> labels = new ArrayList<>();
		try (final Writer writer = new BufferedWriter(new FileWriter(path.toFile()))) {
			for(S rd: schedules) {
				LinearTransformation trans = null;
				if((!configRes.exportOriginalOGEMAValues().getValue()) &&
						(rd instanceof SchedulePresentationData))
					trans = HumanReadableValueConverter.getTransformationIfNonTrivial((SchedulePresentationData) rd);
				TSConvertData td = new TSConvertData();
				td.it = rd.iterator(start-stepSize/2,end);
				td.trans = trans;
				if(!td.it.hasNext())
					td.isFinished = true;
				else
					td.lastVal = td.nextVal = td.it.next();
				td.nanValue = configRes.naNValue().getValue();
				tsData.add(td);
				if(rd instanceof SchedulePresentationData)
					labels.add(((SchedulePresentationData)rd).getID());
				else
					labels.add("");
			}
			writeCSVMultiLine(writer, "Label", pconfig.date, ids);
			if(configRes.addIDLine().getValue())
				writeCSVMultiLine(writer, "ID", pconfig.date, labels);
	
			long curTime = start;
			long nextTime = start + stepSize;
			long limitStart;
			long limitEnd = curTime - halfStep;
			while(curTime <= end) {
				limitStart = limitEnd;
				limitEnd = curTime + halfStep;
				List<String> vals = new ArrayList<>();
				for(TSConvertData td: tsData) {
					if(td.isFinished) {
						vals.add(td.nanValue);
						continue;
					}
					float sum = 0;
					int count = 0;
					SampledValue closest = null;
					long closestDist = -1;
					while(td.lastVal.getTimestamp() < nextTime) {
						if(td.lastVal.getTimestamp() >= limitStart && td.lastVal.getTimestamp() <= limitEnd) {
							count++;
							sum += td.lastVal.getValue().getFloatValue();
						}
						if(count <= MAX_PER_MINDISTANCE) {
							long dist = Math.abs(td.lastVal.getTimestamp() - curTime);
							if(closest == null || (dist < closestDist)) {
								closest = td.lastVal;
								closestDist = dist;
							}
							if(td.nextVal.getTimestamp() >= nextTime)
								break;
						} else if(td.nextVal.getTimestamp() > limitEnd)
							break;
						
						td.lastVal = td.nextVal;
						if(td.it.hasNext())
							td.nextVal = td.it.next();
						else
							break;
					}
					float value;
					if(count > MAX_PER_MINDISTANCE) {
						value = sum / count;
					} else if(closest != null) {
						value = closest.getValue().getFloatValue();
					} else if(Math.abs(td.lastVal.getTimestamp() - curTime) < maxDistance)
						value = td.lastVal.getValue().getFloatValue();
					else {
						value = Float.NaN;
					}
					String val;
					if(Float.isNaN(value))
						val = td.nanValue;
					else {
						if(td.trans != null)
							value = HumanReadableValueConverter.getHumanValue(value, td.trans);
						if(pconfig.locale != null)
								val = String.format(pconfig.locale, "%.3f", value);
							else
								val = String.format("%.3f", value);
					}
					vals.add(val);
				}
				writeCSVMultiLine(writer, curTime, pconfig.date, vals);
				curTime = nextTime;
				nextTime += stepSize;
			}
			writer.flush();
		}
		
		Path pathInZipfile = zipfs.getPath("/" + filename);          
		// copy a file into the zip file
		Files.move(path, pathInZipfile, StandardCopyOption.REPLACE_EXISTING );
	}

	protected static class GeneralConfigCSVExport {
		Locale locale = null;
		SimpleDateFormat date = null;
		boolean fixStepMinute;
	}
	protected static GeneralConfigCSVExport getGeneralConfig(String formatId,
			ChartExportConfig configRes) {
		GeneralConfigCSVExport result = new GeneralConfigCSVExport();
		if(configRes != null && configRes.exportGermanExcelCSV().getValue())
			result.locale = Locale.GERMANY;
		else if(formatId != null && formatId.contains("DE"))
			result.locale = Locale.GERMANY;
		else result.locale = null;
		//TODO: Support also other fixed steps and interpolation / averaging
		result.fixStepMinute = (formatId != null) && formatId.contains("FIXmm");
		
		//SimpleDateFormat date = null;
		if(configRes != null && configRes.timeStampFormat().getValue() != null && (!configRes.timeStampFormat().getValue().isEmpty())) {
			if(result.locale != null)
				result.date = new SimpleDateFormat(configRes.timeStampFormat().getValue(), result.locale);
			else
				result.date = new SimpleDateFormat(configRes.timeStampFormat().getValue());			
		} else if(formatId != null && formatId.contains("#TS#")) {
			String[] els = formatId.split("#TS#");
			if(els.length == 3) {
				String dateFormat = els[1];
				if(result.locale != null)
					result.date = new SimpleDateFormat(dateFormat, result.locale);
				else
					result.date = new SimpleDateFormat(dateFormat);
			}
		}
		return result;
	}
	
	protected static void toCsvFile(Iterator<SampledValue> values, Path path, String formatId,
			ChartExportConfig configRes,
			LinearTransformation trans) throws IOException {
		GeneralConfigCSVExport pconfig = getGeneralConfig(formatId, configRes);
		/*final Locale locale;
		//final boolean celsius = (formatId != null) && formatId.contains("CELSIUS");
		if(configRes != null && configRes.exportGermanExcelCSV().getValue())
			locale = Locale.GERMANY;
		else if(formatId != null && formatId.contains("DE"))
			locale = Locale.GERMANY;
		else locale = null;
		//TODO: Support also other fixed steps and interpolation / averaging
		final boolean fixStepMinute = (formatId != null) && formatId.contains("FIXmm");
		
		SimpleDateFormat date = null;
		if(configRes != null && configRes.timeStampFormat().getValue() != null && (!configRes.timeStampFormat().getValue().isEmpty())) {
			if(locale != null)
				date = new SimpleDateFormat(configRes.timeStampFormat().getValue(), locale);
			else
				date = new SimpleDateFormat(configRes.timeStampFormat().getValue());			
		} else if(formatId != null && formatId.contains("#TS#")) {
			String[] els = formatId.split("#TS#");
			if(els.length == 3) {
				String dateFormat = els[1];
				if(locale != null)
					date = new SimpleDateFormat(dateFormat, locale);
				else
					date = new SimpleDateFormat(dateFormat);
			}
		}*/
		try (final Writer writer = new BufferedWriter(new FileWriter(path.toFile()))) {
			SampledValue sv;
			String lastVal = null;
			Long writeTimeStamp = null;
			long nextTimeStamp = -1;
			while (values.hasNext()) {
				try {
					sv = values.next();
				} catch (NoSuchElementException e) { // does in fact occur, because slots database may be corrupt (not ordered). We better skip those.
					LoggerFactory.getLogger(ScheduleCsvDownload.class).error("Trying to read corrupt time series data. Maybe timestamps are not ordered: " + path);
					return;
				}
				final float value;
				if(trans != null)
					value = HumanReadableValueConverter.getHumanValue(sv.getValue().getFloatValue(), trans);
				else
					value = sv.getValue().getFloatValue();
				final String val;
				if(pconfig.locale != null) val = String.format(pconfig.locale, "%.3f", value);
				else val = String.format("%.3f", value);
				
				if(pconfig.fixStepMinute) {
					if(writeTimeStamp == null) {
						writeTimeStamp = sv.getTimestamp()-60000l;
						lastVal = val;
					}
					nextTimeStamp = sv.getTimestamp();
					while(writeTimeStamp < nextTimeStamp) {
						writeCSVLine(writer, writeTimeStamp, pconfig.date, lastVal);
						writeTimeStamp += 60000l;
					}
					lastVal = val;
				} else {
					writeCSVLine(writer, sv.getTimestamp(), pconfig.date, val);
				}
			}
			writer.flush();
		}
	}
	
	protected static void writeCSVLine(Writer writer, long timeStamp, SimpleDateFormat date, String val)
			throws IOException {
		if(date == null)
			writer.write(timeStamp + ";" + val + "\n");
		else
			writer.write(date.format(new Date(timeStamp)) + ";" + val + "\n");			
	}
	protected static void writeCSVMultiLine(Writer writer, long timeStamp, SimpleDateFormat date, List<String> vals)
			throws IOException {
		final String line;
		if(timeStamp < 0)
			line = "Time";
		else if(date == null)
			line = ""+timeStamp;
		else
			line = date.format(new Date(timeStamp));
		writeCSVMultiLine(writer, line, date, vals);
	}
	protected static void writeCSVMultiLine(Writer writer, String timeStamp, SimpleDateFormat date, List<String> vals)
			throws IOException {
		String line = timeStamp;
		for(String val: vals) {
			line += ";" + val;
		}
		writer.write(line+"\n");
	}
	
	protected static void toJSONFile(Iterator<SampledValue> values, Path path,String name) throws IOException {
		try (final Writer writer = new BufferedWriter(new FileWriter(path.toFile()))) {
			SampledValue sv;
			SortedMap<Long, Float> map = new TreeMap<>();
			while (values.hasNext()) {
				try {
					sv = values.next();
				} catch (NoSuchElementException e) { // does in fact occur, because slots database may be corrupt (not ordered). We better skip those.
					LoggerFactory.getLogger(ScheduleCsvDownload.class).error("Trying to read corrupt time series data. Maybe timestamps are not ordered: " + path);
					return;
				}
				map.put(sv.getTimestamp(), sv.getValue().getFloatValue());
			}
			final JSONObject json = new JSONObject();
			json.put(name, map);
			writer.write(json.toString()); 
			writer.flush();
		}
	}
}
