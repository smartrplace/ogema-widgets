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

import org.apache.commons.fileupload.FileItem;
import org.ogema.core.application.ApplicationManager;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.filedownload.FileDownload;

/** 
 * Opens dialog that allows the user to upload a file. When the upload is finished the onFinished
 * method is called.
 * The widget itself provides a button to select a file for upload, but none to actually start the upload.
 * Triggering the upload itself must be done by a separate button or other widget action like in {@link FileDownload}.
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
        super(page, id, globalWidget);
        this.am = appMan;
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
