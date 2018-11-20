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
package de.iwes.widgets.html.filedownload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;

public class FileDownloadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private FileInputStream fileStream;
    private final File file;
    private final boolean forceDownload;
    private final boolean deleteFileAfterSingleDownload;
    private String mimeType;
    private volatile boolean active = true;

    FileDownloadServlet(File file, boolean forceDownload) {
    	this(file, forceDownload,false);
    }

    FileDownloadServlet(File file) {
        this(file, false);
    }
    
    public FileDownloadServlet(File file, boolean forceDownload, boolean deleteFileAfterSingleDownload) {
        this.file = file;
        this.forceDownload = forceDownload;
        this.mimeType = "application/octet-stream";
        this.deleteFileAfterSingleDownload = deleteFileAfterSingleDownload;
	}

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	if (!active) {
    		response.setStatus(200);
    		return;
    	}
    	OutputStream out = null;
    	try {
            fileStream = new FileInputStream(file);       
	        if (!forceDownload) {
	            ServletContext context = getServletContext();
	            String mimeType = context.getMimeType(file.getAbsolutePath());
	            if (mimeType != null) {
	                this.mimeType = mimeType;
	            }
	        }
	
	        // modify response to inform browser about file-type and length
	        response.setContentType(mimeType);
	        response.setContentLength((int) file.length());
	
	        out = response.getOutputStream();
	        byte[] buffer = new byte[8192];
	        int bytesRead = -1;
	
	        while ((bytesRead = fileStream.read(buffer)) != -1) {
	            out.write(buffer, 0, bytesRead);
	        }
	        out.flush();
	        out.close();
        } catch (FileNotFoundException ex) {
            LoggerFactory.getLogger(FileDownloadServlet.class).error(ex.toString());
            response.setStatus(500);
            return;
        } finally {
        	try {
        		fileStream.close();
        	} catch (Exception e) {}
        	if (out != null) {
	        	try {
	        		out.close();
	        	} catch (Exception e) {}
        	}
        }       
    	if (deleteFileAfterSingleDownload) {
    		try {    			
    			file.delete();  // FIXME -> security issues?
    		} catch (Exception e) {
    			LoggerFactory.getLogger(getClass()).error("Could not delete file " + e);
    		} finally {
    			active = false;
    		}
    	}
    	response.setStatus(200);
    }
}
