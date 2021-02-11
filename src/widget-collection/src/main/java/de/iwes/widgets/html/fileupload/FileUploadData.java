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

import java.math.BigInteger;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.fileupload.FileItem;
import org.json.JSONObject;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.logging.OgemaLogger;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

public class FileUploadData extends WidgetData implements UploadState {

	public final static TriggeringAction UPLOAD_COMPLETED = new TriggeringAction("uploadCompleted");
	/**
	 * @deprecated use {@link TriggeredAction#POST_REQUEST} instead
	 */
	@Deprecated
    public static final TriggeredAction STARTUPLOAD = new TriggeredAction("submitUpload");  
    private final ApplicationManager appMan;
    private final OgemaLogger logger;
    private String servletPath;
    private FileUploadServlet servlet;
    private static final Random random = new Random(System.currentTimeMillis());
    private boolean multiUpload = false;
    private FileItem fileItem = null;
    private final Map<FileUploadListener<?>,Object> listeners = new ConcurrentHashMap<FileUploadListener<?>, Object>();

    private static String nextSessionId() { //Own session-id for download-security
        return new BigInteger(130, random).toString(32);
    }

    final void init(boolean multiUpload, String servletPathExisting) {
        this.multiUpload = multiUpload;
        if(servletPathExisting == null) {
            servlet = new FileUploadServlet(this, multiUpload, appMan);
        	servletPath = "/upload/" + nextSessionId();
        	servletPath = appMan.getWebAccessManager().registerWebResourcePath(servletPath, servlet);
        } else
        	servletPath = servletPathExisting;
    }

    private void unregisterServlet() {
        if (servletPath != null) { //Only unregister if registered
            appMan.getWebAccessManager().unregisterWebResource(servletPath);
            servletPath = null;
        }
    }

    //When session "dies" unregister web-resource
    @Override
    protected void finalize() {
        unregisterServlet();
        try {
            super.finalize();
        } catch (Throwable ex) {
            logger.error(ex.toString());
        }
    }

    /**
     * ********* Constructor
     *
     **********
     * @param upload
     * @param am
     */
    public FileUploadData(FileUpload upload, ApplicationManager am) {
        super(upload);
        this.appMan = am;
        this.logger = am.getLogger();
        init(multiUpload, upload.servletPathExisting);
    }

    /**
     * ***** Inherited methods ******
     */
    @Override
    public JSONObject retrieveGETData(OgemaHttpRequest req) {
        JSONObject result;
        result = new JSONObject();
        if (servletPath != null) {
            result.put("servletPath", servletPath);
            if (multiUpload) {
                result.put("multiUpload", multiUpload);
            }
        }
        return result;
    }

    @Override
    public JSONObject onPOST(String json, OgemaHttpRequest req) {
    	JSONObject j = new JSONObject();
    	j.put("disabled", isDisabled());
        return j;
    }

    /**
     * ******** Public methods *********
     */
    public void setMultiUpload(boolean multiUpload) {
        this.multiUpload = multiUpload;
    }
    
    public FileItem getCurrentFileItem() {
    	return fileItem;
    }

//    @Override
//    public void started() {
//        ((FileUpload) widget).onStarted();
//    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public void finished(FileItem item, OgemaHttpRequest req) {
    	this.fileItem = item;
        ((FileUpload) widget).onFinished(item, req);
    	for (Map.Entry<FileUploadListener<?>,?> entry: listeners.entrySet()) {
    		try {
    			FileUploadListener listener = entry.getKey();
    			Object context = entry.getValue();
    			listener.fileUploaded(fileItem, context, req);
    		} catch (Exception e) {
    			logger.error("Error in FileUploadListener callback",e);
    		};
		}
    }
    
    public <T> void registerListener(FileUploadListener<T> listener, T context) {
        listeners.put(listener,context);
//    	if (fileItem != null) {
//   			listener.fileUploaded(fileItem, context);
//    	}
    }
    
    public void removeListener(FileUploadListener<?> listener) {
        listeners.remove(listener);
    }
}
