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

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.ogema.tools.timeseriesimport.api.ImportConfiguration;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;

public class DirectoryOption extends LinkingOption {
	
	private final List<SelectionItem> options;
	
	/**
	 * @param folders
	 *  	map<path -> delimiter
	 */
	public DirectoryOption(Map<Path, ImportConfiguration> folders) {
		this.options = Collections.unmodifiableList(folders.entrySet().stream()
//			.filter(folder -> Files.isDirectory(folder))
			.map(folder -> new FileItem(folder.getKey(), folder.getValue()))
			.collect(Collectors.toList()));
	}
	
	@Override
	public LinkingOption[] dependencies() {
		return null;
	}

	@Override
	public List<SelectionItem> getOptions(List<Collection<SelectionItem>> items) {
		return options;
	}

	@Override
	public String id() {
		return "dir";
	}

	@Override
	public String label(OgemaLocale arg0) {
		return "Select directories";
	}
	
	public static class FileItem implements SelectionItem {
		
		private final Path path;
		private final ImportConfiguration config;
		
		FileItem(Path path, ImportConfiguration config) {
			this.path = path;
			this.config = config;
		}

		@Override
		public String id() {
			return path.toString();
		}
		
		@Override
		public String label(OgemaLocale locale) {
			return path.toString();
		}
		
		public Path getPath() {
			return path;
		}
		
		public ImportConfiguration getConfig() {
			return config;
		}
		
		@Override
		public String toString() {
			return "FileItem[" + path  + "]";
		}
		
	}

}
