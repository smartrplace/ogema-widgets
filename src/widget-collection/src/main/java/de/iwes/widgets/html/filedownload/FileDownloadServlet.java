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
