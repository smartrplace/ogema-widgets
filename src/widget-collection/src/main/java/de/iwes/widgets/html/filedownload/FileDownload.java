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

import org.ogema.core.security.WebAccessManager;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/** 
 * Widget that allows to set a File object by the application and makes this file accessible for
 * download by the user. Use setFile to define the file, overwrite onGet to generate and set the file
 * "on the fly" directly before the download.
 * The widget itself provides no element visible to the user. Opening the dialog needs to be triggered
 * like this:
 * 		Button downloadButton = new Button(mainPage, "downloadButton", "Download File [Text]");
 *   	downloadButton.triggerAction(fileDownload1, TriggeringAction.POST_REQUEST, FileDownloadData.GET_AND_STARTDOWNLOAD);
 *   
 * @author pzuehlcke
 * @deprecated consider using {@link Download} instead, which is more generic; it supports not only downloads
 * of files from the server, but also other content. 
 */
@Deprecated
public class FileDownload extends OgemaWidgetBase<FileDownloadData> {

    private static final long serialVersionUID = 1L;
    private boolean defaultDeleteAfterDownload = false;
    private final WebAccessManager wam;

     /* ********* Constructor
     *
     *********
     * @param page
     * @param id
     * @param appMan
     * @param globalWidget
     */
    public FileDownload(WidgetPage<?> page, String id, WebAccessManager wam, boolean globalWidget) {
        super(page, id, globalWidget);
        this.wam = wam;
    }

    public FileDownload(WidgetPage<?> page, String id, WebAccessManager wam) {
        this(page, id, wam, false);
    }

    /**
     * ***** Inherited methods
     *
     ****
     */
    @Override
    public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
        return FileDownload.class;
    }

    @Override
    public FileDownloadData createNewSession() {
        return new FileDownloadData(this, wam);
    }

    @Override
    protected void setDefaultValues(FileDownloadData opt) {
        super.setDefaultValues(opt);
        opt.setDeleteFileAfterDownload(defaultDeleteAfterDownload);
    }

    /**
     * ***** Public methods *******
     */
    public boolean setFile(File file, String customFileName, boolean forceDownload, OgemaHttpRequest req) {
        return getData(req).setFile(file, customFileName, forceDownload);
    }

    //Usability
    public boolean setFile(File file, String customFileName, OgemaHttpRequest req) {
        return setFile(file, customFileName, false, req);
    }

    public boolean setFile(File file, boolean forceDownload, OgemaHttpRequest req) {
        return setFile(file, null, forceDownload, req);
    }

    public boolean setFile(File file, OgemaHttpRequest req) {
        return setFile(file, null, false, req);
    }
    
    public void setDefaultDeleteFileAfterDownload(boolean doDelete) { 
    	this.defaultDeleteAfterDownload = doDelete;
    }
    
    /**
     * Note: this method must be called before setting the file, otherwise it has no effect (FIXME)
     */
    public void setDeleteFileAfterDownload(boolean doDelete, OgemaHttpRequest req) { 
    	getData(req).setDeleteFileAfterDownload(doDelete);
    }
    
    //

    public final boolean setWebPath(String webPath, OgemaHttpRequest req) { //Can only be set if widget is global
        if (this.isGlobalWidget()) {
            getData(req).setWebPath(webPath);
            return true;
        } else {
            return false;
        }
    }

    public final String getURL(OgemaHttpRequest req) { //FIXME
        return getData(req).getURL();
    }

}
