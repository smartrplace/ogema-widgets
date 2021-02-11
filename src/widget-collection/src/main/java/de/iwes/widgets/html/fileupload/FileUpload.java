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

import org.apache.commons.fileupload.FileItem;
import org.ogema.core.application.ApplicationManager;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/** 
 * Opens dialog that allows the user to upload a file. When the upload is finished the onFinished
 * method is called.
 * The widget itself provides a button to select a file for upload, but none to actually start the upload.
 * Triggering the upload itself must be done by a separate button or other widget action like in FileDownload.
 * <br>
 * The upload functionality can be disabled and re-enabled using the methods {@link #disable(OgemaHttpRequest)} and
 * {@link #enable(OgemaHttpRequest)}, for instance in the {@link #onPrePOST(String, OgemaHttpRequest)} callback, or the 
 * {@link #onGET(OgemaHttpRequest)} callback. 
 * Note that disabling/enabling in {@link #onPOSTComplete(String, OgemaHttpRequest)}, on the other hand, 
 * will not affect the currently processed upload.
 * 
 * @author pzuehlcke
 *
 */
public class FileUpload extends OgemaWidgetBase<FileUploadData> {

    private static final long serialVersionUID = 1L;
    private final ApplicationManager am;
    final String servletPathExisting;

    /**
     * ********* Constructor
     *
     *********
     * @param page
     * @param id
     * @param appMan
     * @param globalWidget
     */
    public FileUpload(WidgetPage<?> page, String id, ApplicationManager appMan, boolean globalWidget) {
    	this(page, id, appMan, globalWidget, null);
    }
    public FileUpload(WidgetPage<?> page, String id, ApplicationManager appMan, boolean globalWidget,
    		String servletPathExisting) {
        super(page, id, globalWidget);
        this.am = appMan;
        this.servletPathExisting = servletPathExisting;
    }

    public FileUpload(WidgetPage<?> page, String id, ApplicationManager appMan) {
        this(page, id, appMan, false);
    }

    /**
     * ***** Inherited methods
     *
     ****
     */
    @Override
    public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
        return FileUpload.class;
    }

    @Override
    public FileUploadData createNewSession() {
        return new FileUploadData(this, am);
    }

    @Override
    protected void setDefaultValues(FileUploadData opt) {
        super.setDefaultValues(opt);
    }

    /**
     * ***** Public methods *******
     */
    
    public FileItem getCurrentFile(OgemaHttpRequest req) {
    	return getData(req).getCurrentFileItem();
    }
    
//    public void onStarted() {
//    }

    public void onFinished(FileItem item, OgemaHttpRequest req) {}
    
    public <T> void registerListener(FileUploadListener<T> listener, T context, OgemaHttpRequest req) {
    	getData(req).registerListener(listener, context);
	}

	public void removeListener(FileUploadListener<?> listener, OgemaHttpRequest req) {
	    getData(req).removeListener(listener);
	}
}
