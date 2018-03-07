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
    private final ApplicationManager am;

    FileUploadServlet(UploadState uploadState, boolean multiUpload, ApplicationManager am) {
        this.uploadState = uploadState;
        this.multiUpload = multiUpload;
        this.am = am;
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
	        File repository = am.getDataFile("uploads");
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
