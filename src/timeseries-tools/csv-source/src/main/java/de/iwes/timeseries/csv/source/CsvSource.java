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
package de.iwes.timeseries.csv.source;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.ogema.core.recordeddata.RecordedData;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.tools.timeseriesimport.api.ImportConfiguration;
import org.ogema.tools.timeseriesimport.api.ImportConfigurationBuilder;
import org.ogema.tools.timeseriesimport.api.TimeseriesImport;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;
import de.iwes.widgets.html.selectiontree.TerminalOption;
import de.iwes.timeseries.csv.source.options.DirectoryOption;
import de.iwes.timeseries.csv.source.options.FileLeaf;
import de.iwes.timeseries.csv.source.options.DirectoryOption.FileItem;
import de.iwes.timeseries.eval.api.DataProvider;
import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationInputImpl;
import de.iwes.timeseries.eval.base.provider.utils.TimeSeriesDataImpl;

@Component(service=DataProvider.class)
public class CsvSource implements DataProvider<RecordedData> {
	
	private volatile LinkingOption[] options;
	private volatile FileLeaf resources;
	
	@Reference
	private TimeseriesImport timeseriesImport;
	
	@Activate
	protected void activate(BundleContext ctx, Map<String, ?> options) {
		final DirectoryOption folders = new DirectoryOption(parseConfigurations(ctx, options));
		this.resources = new FileLeaf(timeseriesImport, folders);
		this.options = new LinkingOption[]{folders, resources};
	}
	
	@Deactivate
	protected void deactivate() {
		this.options = null;
		this.resources = null;
	}

	@Override
	public String description(OgemaLocale arg0) {
		return label(arg0);
	}

	@Override
	public String id() {
		return "csv_provider";
	}

	@Override
	public String label(OgemaLocale arg0) {
		return "CSV data Provider";
	}

	@Override
	public EvaluationInput getData(List<SelectionItem> items) {
		Objects.requireNonNull(items);
		final List<TimeSeriesData> timeSeriesData = new ArrayList<>(items.size());
		for (SelectionItem item : items) {
			if (!(item instanceof FileItem)) {
				throw new IllegalArgumentException("Unexpected type " + item);
			}
			final FileItem fileItem = (FileItem) item;
			final ReadOnlyTimeSeries ts;
			try {
				ts = AccessController.doPrivileged(new PrivilegedAction<ReadOnlyTimeSeries>() {

					@Override
					public ReadOnlyTimeSeries run() {
						try {
							return timeseriesImport.parseCsv(fileItem.getPath(), fileItem.getConfig());
						} catch (IOException e) {
							throw new UncheckedIOException(e);
						}
					}
				});
			} catch (UncheckedIOException e) {
				LoggerFactory.getLogger(FileLeaf.class).error("Error extracting time series from {}", fileItem.getPath(),e.getCause() );
				return null;
			}
			TimeSeriesDataImpl dataImpl = new TimeSeriesDataImpl(ts, fileItem.label(OgemaLocale.ENGLISH), // TODO? 
					fileItem.label(OgemaLocale.ENGLISH), null);
			timeSeriesData.add(dataImpl);
		}
		return new EvaluationInputImpl(timeSeriesData);
	}

	@Override
	public TerminalOption<? extends ReadOnlyTimeSeries> getTerminalOption() {
		return resources;
	}

	@Override
	public LinkingOption[] selectionOptions() {
		final LinkingOption[] opts = this.options;
		return opts != null ? opts.clone() : null;
	}
	
	private static Map<Path,ImportConfiguration> parseConfigurations(final BundleContext ctx, final Map<String, ?> options) {
		final Map<Path, ImportConfiguration> results = new HashMap<>();
		final String folders0 = (String) options.get("folders");
		final String separators0 = (String) options.get("separators");
		final String formats0 = (String) options.get("formats");
		final String decSeps0 = (String) options.get("decimalSeparators");
		add(folders0, separators0, formats0, decSeps0, results);
		final String folders1 = ctx.getProperty("de.iwes.tools.csv.source.folders");
		final String separators1 = ctx.getProperty("de.iwes.tools.csv.source.separators");
		final String formats1 = ctx.getProperty("de.iwes.tools.csv.source.formats");
		final String decSeps1 = (String) ctx.getProperty("de.iwes.tools.csv.source.decimalSeparators");
		add(folders1, separators1, formats1, decSeps1, results);
		LoggerFactory.getLogger(CsvSource.class).info("Starting CSV source service with directories {}",results.keySet());
		return results;
	}
	
	private static void add(final String folders, final String separators, final String formats, final String decSeps,
				final Map<Path, ImportConfiguration> results) {
		if (folders == null || folders.isEmpty())
			return;
		final List<String> foldersList = Arrays.stream(folders.split(","))
			.map(str -> str.trim())
			.filter(str -> !str.isEmpty())
			.collect(Collectors.toList());
		if (foldersList.isEmpty())
			return;
		final List<Character> separatorsList = new ArrayList<>(foldersList.size());
		if (separators == null) {
			foldersList.forEach(folder -> separatorsList.add(';'));
		} else if (separators.trim().isEmpty()) {
			foldersList.forEach(folder -> separatorsList.add(' '));
		} else {
			final String[] sepArr = separators.split(",");
			if (sepArr.length == 1) {
				final String sep0 = separators.trim();
				if (sep0.length() != 1) {
					LoggerFactory.getLogger(CsvSource.class).warn("Invalid separator " + sep0);
					return;
				}
				foldersList.forEach(folder -> separatorsList.add(sep0.charAt(0)));
			} else {
				if (sepArr.length != foldersList.size()) {
					LoggerFactory.getLogger(CsvSource.class).warn("Size does not match... " + folders + " : " + separators);
					return;
				}
				final Optional<String> mismatch = Arrays.stream(sepArr)
					.filter(str -> str.trim().length() != 1)
					.findAny();
				if (mismatch.isPresent()) {
					LoggerFactory.getLogger(CsvSource.class).warn("Invalid separator " + mismatch.get());
					return;
				}
				final AtomicInteger cnt = new AtomicInteger(0);
				foldersList.forEach(folder -> separatorsList.add(sepArr[cnt.getAndIncrement()].trim().charAt(0)));
			}
		}
		final List<String> timeFormatsList = new ArrayList<>(foldersList.size());
		if (formats == null || formats.isEmpty()) {
			foldersList.forEach(folder -> timeFormatsList.add(null));
		} else {
			final String[] timeFormats = formats.split(",");
			if (timeFormats.length == 1) {
				final String timeFormat = formats.trim();
				if (!isTimeFormatAdmissible(timeFormat)) {
					LoggerFactory.getLogger(CsvSource.class).warn("Invalid time format " + timeFormat);
					return;
				}
				foldersList.forEach(folder -> timeFormatsList.add(timeFormat));
			}
			else {
				if (timeFormats.length != foldersList.size()) {
					LoggerFactory.getLogger(CsvSource.class).warn("Size does not match... " + folders + " : " + formats);
					return;
				}
				final Optional<String> mismatch = Arrays.stream(timeFormats)
					.filter(str -> !isTimeFormatAdmissible(str.trim()))
					.findAny();
				if (mismatch.isPresent()) {
					LoggerFactory.getLogger(CsvSource.class).warn("Invalid time format " + mismatch.get());
					return;
				}
				final AtomicInteger cnt = new AtomicInteger(0);
				foldersList.forEach(folder -> timeFormatsList.add(timeFormats[cnt.getAndIncrement()].trim()));
			}
		} 
		final List<Character> decSepsList = new ArrayList<>(foldersList.size());
		if (decSeps == null || decSeps.isEmpty()) 
			foldersList.forEach(folder -> decSepsList.add(null));
		else {
			final char[] seps = decSeps.toCharArray();
			if (seps.length == 1) {
				foldersList.forEach(folder -> decSepsList.add(seps[0]));
			} else {
				if (seps.length != foldersList.size()) {
					LoggerFactory.getLogger(CsvSource.class).warn("Size does not match... " + folders + " : " + decSeps);
					return;
				}
				final AtomicInteger cnt = new AtomicInteger(0);
				foldersList.forEach(folder -> decSepsList.add(seps[cnt.getAndIncrement()]));
			}
		}
		IntStream.range(0, foldersList.size())
			.forEach(i -> {
				final char sep = separatorsList.get(i);
				final String format = timeFormatsList.get(i);
				final Character decSep = decSepsList.get(i);
				final ImportConfigurationBuilder builder = ImportConfigurationBuilder.newInstance()
						.setDelimiter(sep);
				if (decSep != null)
					builder.setDecimalSeparator(decSep);
				if (format != null) {
					try {
						final TimeUnit unit = TimeUnit.valueOf(format.toUpperCase());
						builder.setTimeUnit(unit);
					} catch (IllegalArgumentException expected) {
						final SimpleDateFormat df = new SimpleDateFormat(format, Locale.ENGLISH);
						builder.setDateTimeFormat(df);
					}
				}
				results.put(Paths.get(foldersList.get(i)), builder.build());
			});
	}
	
	private static boolean isTimeFormatAdmissible(String format) {
		try {
			final TimeUnit unit = TimeUnit.valueOf(format.toUpperCase());
			return true;
		} catch (IllegalArgumentException expected) {
			try {
				new SimpleDateFormat(format, Locale.ENGLISH);
				return true;
			} catch (IllegalArgumentException ee) {
				return false;
			}
		}
	}
	
}
