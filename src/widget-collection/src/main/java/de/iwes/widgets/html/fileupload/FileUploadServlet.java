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
package de.iwes.widgets.html.fileupload;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.ogema.core.application.ApplicationManager;
import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class FileUploadServlet extends HttpServlet {
    private static final long serialVersionUID = -8133771713368604348L;

    private final UploadState uploadState;
    private final boolean multiUpload;
    //private final ApplicationManager am;

    protected final File repository;

    FileUploadServlet(UploadState uploadState, boolean multiUpload, ApplicationManager am) {
        this.uploadState = uploadState;
        this.multiUpload = multiUpload;
        //this.am = am;
        repository = am.getDataFile("uploads");
    }
    public FileUploadServlet(UploadState uploadState, boolean multiUpload, File destinationFolder, ApplicationManager am) {
        this.uploadState = uploadState;
        this.multiUpload = multiUpload;
        //this.am = am;
        repository = destinationFolder;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	try {
    		// use onPrePOST instead
//	        uploadState.started();
	
	        // Create a factory for disk-based file items
	        DiskFileItemFactory factory = new DiskFileItemFactory();//MaxMemorySize, TempDirectory
	
	        // Configure a repository (to ensure a secure temp location is used)
	//        ServletContext servletContext = this.getServletConfig().getServletContext();
	//        File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
	        repository.mkdirs();
	        factory.setRepository(repository);
	
	        // Create a new file upload handler
	        ServletFileUpload upload = new ServletFileUpload(factory);
	        OgemaHttpRequest oreq = new OgemaHttpRequest(request, false);
       
            // Parse the request
            List<FileItem> items = upload.parseRequest(request);

            if (multiUpload) {
                // Process the uploaded items
                Iterator<FileItem> iter = items.iterator();

                while (iter.hasNext()) {
                    FileItem item = iter.next();

                    if (!item.isFormField()) {
                        if (item.getSize() > 0) { //Failsafe for empty file or no file selected in html form (usually caught client-side)
                            uploadState.finished(item,oreq);
                        }
                    }
                }
            } else {
                FileItem item = items.get(0);
                if (!item.isFormField()) {
                    uploadState.finished(item,oreq);
                }
            }
        } catch (Exception ex) {
            LoggerFactory.getLogger(FileUploadServlet.class).error("File upload failed",ex);
            throw new RuntimeException(ex);
        }
    }
}
