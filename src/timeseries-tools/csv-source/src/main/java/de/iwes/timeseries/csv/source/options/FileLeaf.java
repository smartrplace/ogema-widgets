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
package de.iwes.timeseries.csv.source.options;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.tools.timeseriesimport.api.TimeseriesImport;
import org.slf4j.LoggerFactory;

import de.iwes.timeseries.csv.source.options.DirectoryOption.FileItem;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;
import de.iwes.widgets.html.selectiontree.TerminalOption;

public class FileLeaf extends TerminalOption<ReadOnlyTimeSeries> {
	
	private final static String[] ADMISSIBLE_FILE_ENDINGS = { ".csv", ".dat" };
	private final TimeseriesImport service;
	private final DirectoryOption dirOption;
	
	public FileLeaf(TimeseriesImport service, DirectoryOption dirOption) {
		this.service = service;
		this.dirOption = dirOption; 
	}

	@Override
	public LinkingOption[] dependencies() {
		return new LinkingOption[] {dirOption};
	}

	@Override
	public List<SelectionItem> getOptions(final List<Collection<SelectionItem>> items) {
		final boolean foldersSelected = items != null && !items.isEmpty() && !items.get(0).isEmpty();
		if (!foldersSelected)
			return Collections.emptyList();
		synchronized (this) { 
			final Collection<SelectionItem> selected = items.get(0);
			final List<SelectionItem> result = new ArrayList<>();
			for (SelectionItem dir : selected) {
				final FileItem folder = (FileItem) dir;
				try (final Stream<Path> stream = Files.list(folder.getPath())) {
					stream.filter(file -> isAdmissible(file))
						.map(file -> new FileItem(file, folder.getConfig()))
						.forEach(item -> result.add(item));
				} catch (IOException e) {
					continue;
				}
			}
			return result;
		}
		
	}

	@Override
	public String id() {
		return "csv_file_leaf";
	}

	@Override
	public String label(OgemaLocale arg0) {
		return "Select a CSV file";
	}

	@Override
	public ReadOnlyTimeSeries getElement(SelectionItem item) {
		if (!(item instanceof FileItem))
			throw new IllegalArgumentException("Invalid item type " + item);
		final FileItem item0 = (FileItem) item;
		final Path path = item0.getPath();
		try {
			return AccessController.doPrivileged(new PrivilegedAction<ReadOnlyTimeSeries>() {

				@Override
				public ReadOnlyTimeSeries run() {
					try {
						return service.parseCsv(path, item0.getConfig());
					} catch (IOException e) {
						throw new UncheckedIOException(e);
					}
				}
			});
		} catch (UncheckedIOException e) {
			LoggerFactory.getLogger(FileLeaf.class).error("Error parsing csv file at {}",path,e.getCause());
			return null;
		}
	}
	
	private static boolean isAdmissible(final Path file) {
		if (!Files.isRegularFile(file))
			return false;
		final String lower = file.getFileName().toString();
		return Arrays.stream(ADMISSIBLE_FILE_ENDINGS)
			.filter(ending -> lower.endsWith(ending))
			.findAny().isPresent();
	}

}

