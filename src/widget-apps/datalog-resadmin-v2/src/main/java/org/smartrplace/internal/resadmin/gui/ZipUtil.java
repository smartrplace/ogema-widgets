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
package org.smartrplace.internal.resadmin.gui;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartrplace.internal.resadmin.ResAdminApp;

//based on http://www.oracle.com/technetwork/articles/java/compress-1565076.html
public class ZipUtil {
	static final int BUFFER = 2048;
	
	public static void compressEntireDirectory(Path destination, Path source) {
		compressEntireDirectory(destination, source, null);
	}
	
	public static void compressEntireDirectory(Path destination, Path source, FilenameFilter filter) {
		Collection<File> files2zip = FileUtils.listFiles(source.toFile(), null, true);
		ZipUtil.compress(destination, files2zip, source, filter);
	}
	
	public static void compress(Path destination, Collection<File> inputFiles, Path topPath, FilenameFilter filter) {

		try {
			String destDir = FilenameUtils.getPathNoEndSeparator(destination.toString());
			Path f = Paths.get(destDir);
			if(Files.notExists(f)) {
				Files.createDirectories(f);
			}
			final FileOutputStream dest = new FileOutputStream(destination.toString());
			try (final ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest))) {
				out.setMethod(ZipOutputStream.DEFLATED);
				byte data[] = new byte[BUFFER];
				
				// get a list of files from current directory
				//File f = new File(".");
				//String files[] = f.list();
				final Logger logger = LoggerFactory.getLogger(ResAdminApp.class);
				for (File input: inputFiles) {
					if (filter != null && !filter.accept(f.toFile(), input.getName()))
						continue;
					logger.trace("Adding: {}",input);
					try (final BufferedInputStream origin = new BufferedInputStream(new FileInputStream(input.getPath()), BUFFER)) {
						ZipEntry entry;
						if (topPath == null)
							entry = new ZipEntry(input.getPath());
						else
							entry = new ZipEntry(topPath.relativize(input.toPath()).toString());
						out.putNextEntry(entry);
						int count;
						while ((count = origin.read(data, 0,	BUFFER)) != -1) {
							out.write(data, 0, count);
						}
					} catch(FileNotFoundException e) {
						logger.warn("Could not find input file: {}",input);
						throw e;
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public interface ZipEntryProcessingListener {
		boolean useFile(ZipEntry entry);
	}
	/**
	 * 
	 * @param source
	 * @param destination
	 * @param listener may be null if all files from zip shall be written to destination
	 */
	public static void deCompress(Path source, Path destination, ZipEntryProcessingListener listener) {
	      try {
	          ZipEntry entry;
	          try (ZipFile zipfile = new ZipFile(source.toString())) {
		          Enumeration<? extends ZipEntry> e = zipfile.entries();
		          while(e.hasMoreElements()) {
		             entry = (ZipEntry) e.nextElement();
		             if ((listener != null) && (!listener.useFile(entry))) 
		            	 continue;
		             try (BufferedInputStream is = new BufferedInputStream(zipfile.getInputStream(entry))) {
			             int count;
			             byte data[] = new byte[BUFFER];
			             try (BufferedOutputStream dest 
			            		 = new BufferedOutputStream(new FileOutputStream(destination + File.separator + entry.getName()), BUFFER)) {
				             while ((count = is.read(data, 0, BUFFER)) != -1) {
				                dest.write(data, 0, count);
				             }
				             dest.flush();
			             }
		             }
		          }
	          }
	       } catch(Exception e) {
	          e.printStackTrace();
	       }		
	}
}

