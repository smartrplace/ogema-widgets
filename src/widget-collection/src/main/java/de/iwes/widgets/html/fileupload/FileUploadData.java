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

    final void init(boolean multiUpload) {
        this.multiUpload = multiUpload;
        servlet = new FileUploadServlet(this, multiUpload, appMan);
        servletPath = "/upload/" + nextSessionId();
        servletPath = appMan.getWebAccessManager().registerWebResourcePath(servletPath, servlet);
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
     * @param id
     */
    public FileUploadData(FileUpload upload, ApplicationManager am) {
        super(upload);
        this.appMan = am;
        this.logger = am.getLogger();
        init(multiUpload);
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
