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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.model.schedule.Schedule;
import org.ogema.core.recordeddata.RecordedData;
import org.ogema.core.security.WebAccessManager;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.tools.timeseries.implementations.TreeTimeSeries;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.extended.html.bricks.PageSnippetData;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.calendar.datepicker.Datepicker;
import de.iwes.widgets.html.filedownload.FileDownload;
import de.iwes.widgets.html.filedownload.FileDownloadData;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.button.ButtonData;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.resource.timeseries.OnlineTimeSeries;
import de.iwes.widgets.reswidget.scheduleviewer.api.SchedulePresentationData;

// FIXME avoid creation of temp files?
public class ScheduleCsvDownload<T extends ReadOnlyTimeSeries> extends PageSnippet {

	private static final long serialVersionUID = 1L;
	// may be null
	private final Alert alert; 
	public final Button downloadCSVButton;
	public final Button downloadJSONButton;
	private final Label nrItemsLabel;
	private final Datepicker startPicker;
	private final Datepicker endPicker;
	private final FileDownload download;
	private final boolean showUserInput;
	private File tempFolder;
	private static final String JSON = "JSON";
	private static final String CSV = "CSV";
	
	public ScheduleCsvDownload(WidgetPage<?> page, String id, WebAccessManager wam) {
		this(page, id, wam, null, true, null, null);
	}
	
	public ScheduleCsvDownload(WidgetPage<?> page, String id, WebAccessManager wam, Alert alert) {
		this(page, id, wam, null, true, null, null);
	}
	
	public ScheduleCsvDownload(WidgetPage<?> page, String id, WebAccessManager wam, Alert alert, 
			boolean showUserInput, Datepicker startPicker, Datepicker endPicker) {
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
		this.showUserInput = showUserInput;
		this.nrItemsLabel = new Label(page, "nrItemsLabel_" + id) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(String.valueOf(size(req)), req);
			}
			
		};
		
		if(startPicker == null) {
			this.startPicker = new Datepicker(page, "startPicker_" + id) {
	
				private static final long serialVersionUID = 1L;
	
				@Override
				public void onGET(OgemaHttpRequest req) {
					List<T> schedules = getSchedules(req);
					long start = Long.MAX_VALUE;
					for (T schedule: schedules) {
						SampledValue sv = schedule.getNextValue(Long.MIN_VALUE);
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
			this.endPicker = new Datepicker(page, "endPicker_" + id) {
	
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
		
		this.downloadCSVButton = new Button(page, "downloadButtonCSV_" + id, "download CSV") {
			private static final long serialVersionUID = -6547975805917435860L;

	
			@Override
			public void onPrePOST(String data, OgemaHttpRequest req) {
				ScheduleCsvDownload.this.getData(req).buttonPressed = ScheduleCsvDownload.CSV;
			}
		};
		this.downloadJSONButton = new Button(page, "downloadButtonJSON_" + id, "download JSON") {
			private static final long serialVersionUID = -6547975805437435860L;

			@Override
			public void onPrePOST(String data, OgemaHttpRequest req) {
				ScheduleCsvDownload.this.getData(req).buttonPressed = ScheduleCsvDownload.JSON;
			}
	
		};
		this.download = new FileDownload(page, "download_" + id, wam);
		buildWidget();
		setDependencies();
	}
	
	public void buildWidget() {
		if(showUserInput) {
			StaticTable tab = new StaticTable(4, 2, new int[] {3,3});
			tab.setContent(0, 0, "Schedules selected").setContent(0, 1, nrItemsLabel)
				.setContent(1, 0, "Select start time").setContent(1, 1, startPicker)
				.setContent(2, 0, "Select end time").setContent(2, 1, endPicker)
				.setContent(3, 0, downloadCSVButton).setContent(3, 1, downloadJSONButton);
			this.append(tab, null).linebreak(null).append(download, null);		
		}else {
			this.append(downloadCSVButton, null).append(download, null);
		}
	}
		
	private final void setDependencies() {
		this.triggerAction(downloadCSVButton, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);
		this.triggerAction(downloadJSONButton, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST);

		this.triggerAction(nrItemsLabel, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST,1);
		this.triggerAction(startPicker, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST,1);
		this.triggerAction(endPicker, TriggeringAction.GET_REQUEST, TriggeredAction.GET_REQUEST,1);
		
		downloadCSVButton.triggerAction(this, TriggeringAction.POST_REQUEST, TriggeredAction.POST_REQUEST);
		downloadJSONButton.triggerAction(this, TriggeringAction.POST_REQUEST, TriggeredAction.POST_REQUEST);

		this.triggerAction(download, TriggeringAction.POST_REQUEST, FileDownloadData.GET_AND_STARTDOWNLOAD);
		if (alert != null)
			this.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ScheduleCsvDownloadData<T> getData(OgemaHttpRequest req) {
		return (ScheduleCsvDownloadData<T>) super.getData(req);
	}
	
	public void setSchedules(Collection<T> schedules, OgemaHttpRequest req) {
		getData(req).setSchedules(schedules);
	}
	
	public List<T> getSchedules(OgemaHttpRequest req) {
		return getData(req).getSchedules();
	}

	
	public void addSchedule(T schedule, OgemaHttpRequest req) {
		getData(req).addSchedule(schedule);
	}

	public boolean isEmpty(OgemaHttpRequest req) {
		return getData(req).isEmpty();
	}

	public int size(OgemaHttpRequest req) {
		return getData(req).size();
	}	

	@Override
	public ScheduleCsvDownloadData<T> createNewSession() {
		return new ScheduleCsvDownloadData<T>(this, startPicker, endPicker, download, tempFolder, alert);
	}

	static class ScheduleCsvDownloadData<T extends ReadOnlyTimeSeries> extends PageSnippetData {
		
		// illegal in file names; not necessarily comlete
		// https://stackoverflow.com/questions/893977/java-how-to-find-out-whether-a-file-name-is-valid
		private static final char[] ILLEGAL_CHARACTERS = { '/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':' };
		final List<T> schedules = new ArrayList<>();
		private final Datepicker downloadStartPicker;
		private final Datepicker downloadEndPicker;
		private final Path tempFolder;
		private final FileDownload download;
		// may be null
		private final Alert alert;
		public String buttonPressed = "";
		

		protected ScheduleCsvDownloadData(ScheduleCsvDownload<T> snippet,Datepicker start, Datepicker end, FileDownload download, File tempFolder, Alert alert) {
			super(snippet);
			
			this.downloadStartPicker = start;
			this.downloadEndPicker = end;
			this.tempFolder = tempFolder.toPath();
			this.download = download; 
			this.alert = alert;
		}
		

		@SuppressWarnings("deprecation")
		@Override
		public JSONObject onPOST(String data, OgemaHttpRequest req) {
			try {
				final Path path = generateFile(req);
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
		
		private Path generateFile(OgemaHttpRequest req) throws IOException {
			
			ScheduleCsvDownload<T> master = (ScheduleCsvDownload<T>) widget;
			ScheduleCsvDownloadData<T> data = master.getData(req);
		
			
			final List<T> schedules = getSchedules();
			if (!Files.exists(tempFolder))
				Files.createDirectories(tempFolder);
			final Path base = Files.createTempDirectory(tempFolder, "schedules_");

			final long start = downloadStartPicker.getDateLong(req); 
			final long end = downloadEndPicker.getDateLong(req);
			if (start > end) {
				return null;
			}
			final Path zipFile = tempFolder.resolve(base.getFileName() + ".zip");
	        final URI uri = URI.create("jar:" + zipFile.toUri());
	        int i = 0;
			try (final FileSystem zipfs = FileSystems.newFileSystem(uri, Collections.singletonMap("create", "true"))) {
				for (T rd : schedules) {
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
					
					String filename = id.replace("/", "%2F");
					for (char c : ILLEGAL_CHARACTERS) {
						filename = filename.replace(c, '_');
					}
					
					if(data.buttonPressed.equals(ScheduleCsvDownload.JSON)) {
						writeValuesToFile(base, start, end, zipfs, rd, filename+".json");
					}else if(data.buttonPressed.equals(ScheduleCsvDownload.CSV)){
						writeValuesToFile(base, start, end, zipfs, rd, filename+".csv");
					}
					
				}
				
			}
			FileUtils.deleteDirectory(base.toAbsolutePath().toFile());
	        return zipFile;
		}

		private void writeValuesToFile(final Path base, final long start, final long end, final FileSystem zipfs, T rd,
				String filename) throws IOException {
			Path file = base.resolve(filename);
			if(filename.endsWith(".csv")) {
				toCsvFile(rd.iterator(start,end), file);
			}else if(filename.endsWith(".json")) {
				toJSONFile(rd.iterator(start,end), file, filename.replace(".json", ""));
			}else if(filename.endsWith(".python")) {
				return;
			}
			Path pathInZipfile = zipfs.getPath("/" + filename);          
			// copy a file into the zip file
			Files.move(file, pathInZipfile, StandardCopyOption.REPLACE_EXISTING );
		}
		
		public void setSchedules(Collection<T> schedules) {
			writeLock();
			try {
				this.schedules.clear();
				this.schedules.addAll(schedules);
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
		
		public void addSchedule(T schedule) {
			writeLock();
			try {
				schedules.add(schedule);
			} finally {
				writeUnlock();
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
		
		private static void toCsvFile(Iterator<SampledValue> values, Path path) throws IOException {
			try (final Writer writer = new BufferedWriter(new FileWriter(path.toFile()))) {
				SampledValue sv;
				while (values.hasNext()) {
					try {
						sv = values.next();
					} catch (NoSuchElementException e) { // does in fact occur, because slots database may be corrupt (not ordered). We better skip those.
						LoggerFactory.getLogger(ScheduleCsvDownload.class).error("Trying to read corrupt time series data. Maybe timestamps are not ordered: " + path);
						return;
					}
					writer.write(sv.getTimestamp() + ";" + sv.getValue().getFloatValue() + "\n");
				}
				writer.flush();
			}
		}
		
		private static void toJSONFile(Iterator<SampledValue> values, Path path,String name) throws IOException {
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

		
		@Override
		public JSONObject retrieveGETData(OgemaHttpRequest req) {
			@SuppressWarnings("unchecked")
			ScheduleCsvDownload<T> master = (ScheduleCsvDownload<T>) widget;
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
		
	}	
}
