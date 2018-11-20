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
/**
 * Copyright 2009 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package org.smartrplace.internal.resadmin.gui;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;
import org.ogema.core.model.Resource;
import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.action.SCPDataCollectionAction;
import org.ogema.util.action.ActionHelper;
import org.slf4j.Logger;
import org.smartrplace.internal.resadmin.ResAdminController;
import org.smartrplace.internal.resadmin.gui.ZipUtil.ZipEntryProcessingListener;
import org.smartrplace.internal.resadmin.logic.ResourceTypeTemplate;
import org.smartrplace.internal.resadmin.logic.TopLevelResTypePattern;
import org.smartrplace.resadmin.config.BackupConfig;
import org.smartrplace.resadmin.config.KnownBackups;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.extended.html.bricks.PageSnippet;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.complextable.RowTemplate;
import de.iwes.widgets.html.filedownload.FileDownload;
import de.iwes.widgets.html.filedownload.FileDownloadData;
import de.iwes.widgets.html.fileupload.FileUpload;
import de.iwes.widgets.html.fileupload.FileUploadListener;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.checkbox.SimpleCheckbox;
import de.iwes.widgets.html.form.checkbox.SimpleCheckboxI;
import de.iwes.widgets.html.form.dropdown.Dropdown;
import de.iwes.widgets.html.form.dropdown.TemplateDropdown;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.HeaderData;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.textfield.TextField;
import de.iwes.widgets.html.popup.Popup;
import de.iwes.widgets.resource.widget.dropdown.ResourceListDropdown;
import de.iwes.widgets.resource.widget.table.DefaultResourceRowTemplate;
import de.iwes.widgets.resource.widget.table.ResourceTable;
import de.iwes.widgets.resource.widget.textfield.BooleanResourceCheckbox;
import de.iwes.widgets.resource.widget.textfield.TimeResourceTextField;
import de.iwes.widgets.resource.widget.textfield.TimeResourceTextField.Interval;
import de.iwes.widgets.resource.widget.textfield.ValueResourceTextField;

public class MainPageImpl { // extends WidgetPage<LocaleDictionary> {

	// private final static long UPDATE_RATE = 5*1000;
	final ResourceTable<Resource> tableResources;
	final ResourceListDropdown<BackupConfig> dropProgram;
	private final Dropdown dropKnownBackups;
	private final static FilenameFilter NO_ZIPS_FILTER = new FilenameFilter() {

		@Override
		public boolean accept(File dir, String name) {
			return !name.toLowerCase().endsWith(".zip");
		}

	};

	/**
	 * Structure for registering the same set of dependencies for more than one
	 * governing widget
	 */
	private static abstract class WidgetRegisterer {
		public abstract void registerDependentWidgets(OgemaWidgetBase<?> parent);
	}

	public MainPageImpl(WidgetPage<?> page, final ResAdminController appData) {
		// public MainPageImpl(WidgetApp widgetApp, final ResAdminController
		// appData) {
		// super(widgetApp);
		// page.appData = appData;

		// init all widgets
		Header header = new Header(page, "header", "Resource Backup and Cleaner");
		header.addDefaultStyle(WidgetData.TEXT_ALIGNMENT_CENTERED);
		page.append(header);

		final Alert alert = new Alert(page, "alert", "");
		alert.setDefaultVisibility(false);
		page.append(alert);
		page.linebreak();

		// New Backup Config Popup
		final Label newBackupConfigNameLabel = new Label(page, "newBackupConfigNameLabel");
		newBackupConfigNameLabel.setDefaultText("Name of Backup Config");

		final TextField newBackupConfigName = new TextField(page, "newBackupConfigName");

		final Label newBackupConfigDestinationLabel = new Label(page, "newBackupConfigDestinationLabel");
		newBackupConfigDestinationLabel.setDefaultText("Destination Path");

		final TextField newBackupConfigDestination = new TextField(page, "newBackupConfigDestination");
		newBackupConfigDestination.setDefaultPlaceholder("'./myDirectory' or 'myDirectory'");

		final Label newBackupConfigIsSpecialLabel = new Label(page, "newBackupConfigIsSpecialLabel");
		newBackupConfigIsSpecialLabel.setDefaultText("Overwrite or ZipFile");

		final SimpleCheckboxI newBackupConfigIsSpecial = new SimpleCheckbox(page, "newBackupConfigIsSpecial",
				"Overwrite (activate checkbox) or generate Zip-File for each backup (deactivate checkbox)");

		final StaticTable newNameTable = new StaticTable(3, 2);
		newNameTable.setContent(0, 0, newBackupConfigNameLabel);
		newNameTable.setContent(0, 1, newBackupConfigName);
		newNameTable.setContent(1, 0, newBackupConfigDestinationLabel);
		newNameTable.setContent(1, 1, newBackupConfigDestination);
		newNameTable.setContent(2, 0, newBackupConfigIsSpecialLabel);
		newNameTable.setContent(2, 1, newBackupConfigIsSpecial);

		final Button acceptNewBackupConfigButton = new Button(page, "acceptNewBackupConfigButton") {
			public void onPOSTComplete(String data, OgemaHttpRequest req) {

				boolean nameAlreadyInUse = false;
				boolean destinationAlreadyInUse = false;
				ResourceList<BackupConfig> configs = appData.appConfigData.configList();
				String actName = newBackupConfigName.getValue(req);
				String destination = newBackupConfigDestination.getValue(req);
				boolean overwrite = newBackupConfigIsSpecial.getValue(req);

				if (actName.equals("")) {
					alert.showAlert("BackupConfig not created, no name was entered", false, req);
					return;
				}
				if (destination.equals("")) {
					destination = "basicBackupDirectory";
					alert.showAlert("Warning : You entered nothing in the 'Directory' Field, so your place where your backups will be stored is 'basicBackupDirectory' now", false, req);
				}
				for (BackupConfig bc : configs.getAllElements()) {
					if (bc.name().getValue().equals(actName)) {
						nameAlreadyInUse = true;
						alert.showAlert("Name '" + actName + "' is already used or empty", false, req);
						break;
					}
					if (bc.destinationDirectory().getValue().equals(destination)) {
						alert.showAlert("Warning : Destination  '" + destination + "' is already used, you now got more than one BackupConfig refering to the same Directory", false, req);
						break;
					}
				}
				
				if (!nameAlreadyInUse) {
					appData.saveNewBackupConfig(actName, destination, overwrite);
					alert.showAlert("Backup Config '" + actName + "' successfully created", true, req);
				}

			}
		};
		acceptNewBackupConfigButton.setDefaultText("accept");

		final PageSnippet newBackupConfigSnippet = new PageSnippet(page, "newBackupConfigSnippet", true);
		newBackupConfigSnippet.append(newNameTable, null);
		newBackupConfigSnippet.append(acceptNewBackupConfigButton, null);

		final Popup newBackupConfigPopup = new Popup(page, "newBackupConfigPopup", true);
		newBackupConfigPopup.setTitle("New Backup Config", null);
		newBackupConfigPopup.setBody(newBackupConfigSnippet, null);
		page.append(newBackupConfigPopup);
		acceptNewBackupConfigButton.triggerAction(newBackupConfigPopup, TriggeringAction.POST_REQUEST,
				TriggeredAction.HIDE_WIDGET);
		acceptNewBackupConfigButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);

		//
		final Button createNewBackupConfigButton = new Button(page, "createNewBackupConfigButton",
				"Create new Backup Config");
		createNewBackupConfigButton.triggerAction(newBackupConfigPopup, TriggeringAction.POST_REQUEST,
				TriggeredAction.SHOW_WIDGET);

		final TimeResourceTextField editBackupInterval = new TimeResourceTextField(page, "editBackupInterval",
				Interval.hours);

		final BooleanResourceCheckbox checkIncludeRefs = new BooleanResourceCheckbox(page, "checkIncludeRefs",
				"Include referenced rooms and users");

		final SimpleCheckboxI overwriteBackup = new SimpleCheckbox(page, "overwrite_backup", "Overwrite Backup ?") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				BackupConfig config = dropProgram.getSelectedItem(req);
				BooleanResource bool = config.overwriteExistingBackup();

				if (config != null && bool != null) {
					bool.setValue(this.getValue(req));
				}
			}
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				this.setToolTip("Checkbox active : Your Backup will be overwritten after "
				+ "each press on 'Backup to Directory'. \nCheckbox not active : After each press on 'Backup "
				+ "to Directory' it will creates a new Backup zip File from the selected Data in the table at "
				+ "the bottom of the page", req);
			}
		};


		final ValueResourceTextField<StringResource> editDirectory = new ValueResourceTextField<>(page,
				"editDirectory");
		final BooleanResourceCheckbox checkAllExcept = new BooleanResourceCheckbox(page, "checkAllExcept",
				"Include all resources except the selected");
		final BooleanResourceCheckbox checkWriteJSON = new BooleanResourceCheckbox(page, "checkWriteJSON",
				"WriteJSON (default:XML)");

		// XXX must be set explicitly in all widgets that can change the options
		// to be displayed... pretty annoying
		this.dropKnownBackups = new Dropdown(page, "dropKnownBackups");

		final Button buttonReplay = new Button(page, "buttonReplay", "Replay") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				if (dropKnownBackups.getSelected(req) != null) {
					String mes = appData.replayBackup(dropKnownBackups.getSelected(req).id(),
							dropProgram.getSelectedItem(req)).getMessage();
					if (mes != null)
						alert.showAlert(mes, false, req);
					else
						alert.showAlert("Successfully replayed", true, req);
				}
				// if(dropKnownBackups.getSelectedItem(req) != null) {
				// String mes =
				// appData.replayBackup(dropKnownBackups.getSelectedItem(req).fileDirectory().getValue());
				// if(mes != null) alert.setText(mes, req);
				// }
			}
		};
		newBackupConfigIsSpecialLabel.triggerAction(dropKnownBackups, TriggeringAction.POST_REQUEST,
				TriggeredAction.GET_REQUEST);

		/*
		 * We have to provide the current list everytime here as the list is
		 * re-built on every reload of the page. It might make sense to update
		 * the widget for page in the future
		 */
		final TemplateDropdown<TopLevelResTypePattern> dropResType = new TemplateDropdown<TopLevelResTypePattern>(page,
				"dropResType") {
			private static final long serialVersionUID = 1L;

			@Override
			public void updateDependentWidgets(OgemaHttpRequest req) {
				TopLevelResTypePattern pattern = getSelectedItem(req);
				// tableResources.selectItemList((List<Resource>)
				// pattern.resList, req);
				// FIXME ok?
				if (pattern == null)
					tableResources.updateRows(Collections.<Resource>emptyList(), req);
				else
					tableResources.updateRows((List<Resource>) pattern.resList, req);
			}
		};
		dropResType.setTemplate(new ResourceTypeTemplate());

		dropProgram = new ResourceListDropdown<BackupConfig>(page, "dropProgram", false,
				appData.appConfigData.configList()) {

			private static final long serialVersionUID = 8696145677385119466L;

			@Override
			public void updateDependentWidgets(OgemaHttpRequest req) {
				BackupConfig c = getSelectedItem(req);
				// System.out.println("dropProgram:updateDependingWidgets:"+c.getLocation());
				appData.upateTypeList();
				dropResType.update(appData.typePatterns, req);
				dropResType.selectItem(appData.patternForResource, req);
				dropResType.updateDependentWidgets(req);
				overwriteBackup.updateDependentWidgets(req);

				editBackupInterval.selectItem(c != null ? c.autoBackupInterval() : null, req);
				checkIncludeRefs.selectItem(c != null ? c.includeStandardReferences() : null, req);
				checkAllExcept.selectItem(c != null ? c.backupAllExceptResoucesIncluded() : null, req);
				checkWriteJSON.selectItem(c != null ? c.writeJSON() : null, req);
				editDirectory.selectItem(c != null ? c.destinationDirectory() : null, req);

				updateDropKnownBackups(c, req);

				overwriteBackup.setValue(c.overwriteExistingBackup().getValue(), req);

				// TODO: Adaptation of the checks for the resources did not work
				// at one observation,
				// but not reproducible. So we keep page idea for now
				/*
				 * for(StringResource resname:
				 * c.topLevelResourcesIncluded().getAllElements()) { Object o =
				 * tableResources.getCellContent(resname.getValue(), "1", req);
				 * if(o instanceof BooleanResourceCheckBox) {
				 * BooleanResourceCheckBox brc = (BooleanResourceCheckBox)o;
				 * //brc. } }
				 */
				// System.out.println("finish
				// dropProgram:updateDependingWidgets:"+c.getLocation());
			}
		};
		acceptNewBackupConfigButton.triggerAction(dropProgram, TriggeringAction.POST_REQUEST,
				TriggeredAction.GET_REQUEST);
		// dropProgram.makeInitWidget();

		final FileUpload upload = new FileUpload(page, "upload", appData.appMan, true);
		final FileUploadListener<Float> uploadAndKeepListener = new FileUploadListener<Float>() {

			@Override
			public void fileUploaded(FileItem fileItem, Float context, OgemaHttpRequest req) {
				try {
					if (fileItem.getName().endsWith(".ogx")) {
						File destFile = new File("replay-on-clean", fileItem.getName());
						Files.copy(fileItem.getInputStream(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
						// writeFile(fileItem.getInputStream(), destFile);
					} else if (fileItem.getName().endsWith(".zip")) {
						String destDirStr = dropProgram.getSelectedItem(req).destinationDirectory().getValue();
						File destFile = new File(destDirStr, fileItem.getName());
						Files.copy(fileItem.getInputStream(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
						// writeFile(fileItem.getInputStream(), destFile);
						updateReplayOnClean(destFile.toPath());
					}
					alert.showAlert("Uploaded!", true, req);
				} catch (IOException e) {
					appData.log.error("Error copying file", e);
					alert.showAlert("Upload failed!", false, req);
					return;
				}
			}

		};

		final FileUploadListener<Float> uploadAndDeleteListener = new FileUploadListener<Float>() {

			@Override
			public void fileUploaded(FileItem fileItem, Float context, OgemaHttpRequest req) {
				try {
					File destFile = null;
					File tempDir = new File("tempUpload"); // FIXME unnecessary to copy the file to the disk
					if (fileItem.getName().endsWith(".ogx") || fileItem.getName().endsWith(".zip"))
						destFile = new File("tempUpload", fileItem.getName());
					if(!destFile.exists())
						destFile.mkdirs();
					Files.copy(fileItem.getInputStream(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
					String mes = appData.replayBackup(destFile.getPath(), null).getMessage();
					FileUtils.deleteDirectory(tempDir);
					if (mes != null)
						alert.showAlert(mes, false, req);
					else
						alert.showAlert("Successfully replayed", true, req);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		};

		final Button buttonUploadReplay = new Button(page, "buttonUploadReplay", "Upload&Update ReplayOnClean") {
			// final Button buttonUploadReplay = new Button(page,
			// "buttonUploadReplay", "Upload&Reboot") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				upload.registerListener(uploadAndKeepListener, 1.5f, req);
				alert.showAlert("Starting Upload!", true, req);
			}
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				this.setToolTip("Puts your selected Backup into the 'replay-on-clean' folder which is loaded before Ogema starts clean.", req);
			}
		};
		buttonUploadReplay.triggerAction(upload, TriggeringAction.POST_REQUEST, TriggeredAction.POST_REQUEST);
		
		
		final Button uploadBackupFileButton = new Button(page, "uploadBackupFileButton", "Upload Backup File") {
			private static final long serialVersionUID = 1L;

			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				upload.registerListener(uploadAndDeleteListener, 1.5f, req);
				// FIXME what is this supposed to mean?
				alert.showAlert("Starting Upload, remember that you have to save your Backup Afterwards. Otherwise your Uploaded Backup will be gone!",	true, req);
			}
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				this.setToolTip("Upload your selected file and create or replace the respective OGEMA resources", req);
			}

		};
		uploadBackupFileButton.triggerAction(upload, TriggeringAction.POST_REQUEST, TriggeredAction.POST_REQUEST);
		uploadBackupFileButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		

		/*
		 * WidgetGroup baseGroup = page.registerWidgetGroup("baseGroup", null);
		 * baseGroup.addWidget(editBackupInterval);
		 * baseGroup.addWidget(checkIncludeRefs);
		 * baseGroup.addWidget(dropResType);
		 * 
		 * baseGroup.addWidget(editNewProgramName);
		 * baseGroup.addWidget(checkAllExcept);
		 * baseGroup.addWidget(checkWriteJSON);
		 * baseGroup.addWidget(editDirectory);
		 * baseGroup.addWidget(dropKnownBackups);
		 * 
		 * //dropProgram.registerDependentWidget(dropResType);
		 * //dropProgram.registerDependentWidget(baseGroup);
		 * dropProgram.triggerAction(baseGroup, TriggeringAction.POST_REQUEST,
		 * TriggeredAction.GET_REQUEST);
		 */

		/* makes sense theoretically, but not really required */
		// dropProgram.registerDependentWidget(dropResType);
		/*
		 * table is sub-widget of dropResType, but Javascript update is not
		 * added automatically
		 */

		final Button backupToDirectoryButton = new Button(page, "backupToDirectoryButton", "Backup to Directory") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onPrePOST(String data, OgemaHttpRequest req) {
				final BackupConfig config = dropProgram.getSelectedItem(req);
				File result = appData.runBackup(config);
				if (result == null || !result.exists())
					alert.showAlert("Something went wrong, backup could not be created", false, req);
				else
					alert.showAlert("Backup successfully created", true, req);
				updateDropKnownBackups(config, req);
			}
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				this.setToolTip("Creates a Backup which is stored in the directory of your "
						+ "selected Backup Configuration. It will be shown in the Backup Dropdown", req);
			}
		};
		

		final FileDownload download = new FileDownload(page, "download", appData.appMan.getWebAccessManager(), true);
		download.triggerAction(download, TriggeringAction.GET_REQUEST, FileDownloadData.STARTDOWNLOAD);
		page.append(download);

		final Button buttonDownloadProgram = new Button(page, "buttonDownloadProgram", "Download Backup") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onPrePOST(String data, OgemaHttpRequest req) {

				if (dropKnownBackups.getSelectedValue(req) == null)
					return;

				File backupDir;
				// this would download an current Backup, not one of the
				// selected ones
				// backupDir =
				// appData.runBackup(dropProgram.getSelectedItem(req));
				String selectedBackup = dropKnownBackups.getSelectedValue(req);
				if (selectedBackup.endsWith(".zip")) {
					backupDir = new File(
							dropProgram.getSelectedItem(req).destinationDirectory().getValue() + "/" + selectedBackup);
				} else {
					backupDir = new File(dropProgram.getSelectedItem(req).destinationDirectory().getValue());
				}
				String customName = backupDir.getName();
				if (customName.endsWith(".zip")) {
					download.setFile(backupDir, customName, req);
					download.setDeleteFileAfterDownload(false, req);
				} else {
					// creates an zipFile with all displayed ogx data, downloads
					// it and deletes it afterwards
					Path zipFile = backupDir.toPath().resolve(customName + ".zip");
					// TODO new method that takes a filter -> filter out zip
					// files
					ZipUtil.compressEntireDirectory(zipFile, backupDir.toPath(), NO_ZIPS_FILTER);
					download.setDeleteFileAfterDownload(true, req);
					download.setFile(zipFile.toFile(), customName+".zip", req);
				}

			}

			@Override
			public void onGET(OgemaHttpRequest req) {
				if (dropKnownBackups.getSelectedValue(req) == null) {
					disable(req);
				} else {
					enable(req);
				}
			}
		};
		dropKnownBackups.triggerAction(buttonDownloadProgram, TriggeringAction.POST_REQUEST,
				TriggeredAction.GET_REQUEST);
		buttonDownloadProgram.triggerAction(download, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST); // GET then triggers download start

		final Button buttonSendProgram = new Button(page, "buttonSendProgram", "Send Backup") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onPrePOST(String data, OgemaHttpRequest req) {
				ActionHelper.runExtendedActionNonBlocking(SCPDataCollectionAction.class, "backup-install",
						appData.appMan, 2, "No Backup sending action found!");
				alert.showAlert("Started sending backup to server", true, req);
			}
		};

		final Button buttonDeleteProgram = new Button(page, "buttonDeleteProgram", "Delete Program") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				//TODO > 1 or >=1 ? should it be possible to have 0 BackupConfigs ?
				if (appData.appConfigData.configList().size() > 1) {
					enable(req);
				} else {
					disable(req);
				}
			}

			@Override
			public void onPrePOST(String data, OgemaHttpRequest req) {
				if (appData.appConfigData.configList().size() > 1) {
					BackupConfig c = dropProgram.getSelectedItem(req);
					File configDir = new File(c.destinationDirectory().getValue());
					appData.deleteConfiguration(dropProgram.getSelectedItem(req));
					if(backupDirUnused(c.destinationDirectory().getValue())) {
						deleteBackupRec(configDir);
					}
					dropProgram.selectItem(appData.appConfigData.configList().getAllElements().get(0), req);
				}
				dropProgram.updateDependentWidgets(req);
			}

			public void deleteBackupRec(File f) {
				if (f.isDirectory()) {
					File[] children = f.listFiles();
					for (File file : children) {
						deleteBackupRec(file);
					}
					f.delete();
				} else {
					f.delete();
				}
			}
			
			public boolean backupDirUnused(String dirStr) {
				boolean unused = true;
				for(BackupConfig c : appData.appConfigData.configList().getAllElements()) {
					if(c.destinationDirectory().getValue().equals(dirStr)) {
						unused = false;
						break;
					}
				}
				return unused;
			}

		};
		buttonDeleteProgram.triggerAction(dropProgram, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
//		buttonDeleteProgram.triggerAction(dropKnownBackups, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);

		final Button downloadTempBackup = new Button(page, "downloadTempBackup", "Backup & Download") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onPrePOST(String data, OgemaHttpRequest req) {

				if (dropProgram.getSelectedItem(req) == null)
					return;

				File backupDir = appData.appMan.getDataFile("data/tempBackup");

				backupDir = appData.runBackup(dropProgram.getSelectedItem(req), backupDir.toString());

				// String customName =
				// dropProgram.getSelectedItem(req).name().getValue();
				String customName = backupDir.getName();
				if (backupDir.getName().endsWith(".zip")) {
					download.setFile(backupDir, customName, req);
					download.setDeleteFileAfterDownload(true, req);
				} else {
					// creates an zipFile with all displayed ogx data, downloads
					// it and deletes it afterwards
					customName += ".zip";
					Path zipFile = backupDir.toPath().resolve(customName);
					// TODO new method that takes a filter -> filter out zip
					// files
					ZipUtil.compressEntireDirectory(zipFile, backupDir.toPath(), NO_ZIPS_FILTER);
					download.setDeleteFileAfterDownload(true, req);
					download.setFile(zipFile.toFile(), customName, req);
				}

			}

			@Override
			public void onGET(OgemaHttpRequest req) {
				if (dropProgram.getSelectedItem(req) == null) {
					disable(req);
				} else {
					enable(req);
				}
				this.setToolTip("Creates a Backup of the current state of your Ogema Resources "
						+ "and downloads it. The downloaded Backup is deleted in Ogema afterwards", req);
			}

		};
		dropProgram.triggerAction(downloadTempBackup, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		downloadTempBackup.triggerAction(download, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		
		RowTemplate<Resource> rowTemplate = new DefaultResourceRowTemplate<Resource>() {
			@Override
			public Row addRow(final Resource listElement, OgemaHttpRequest req) {
				Row row = new Row();
				String lineId = getLineId(listElement);
				row.addCell(new Label(tableResources, "loc_" + lineId, listElement.getLocation(), req), 3);

				SimpleCheckboxI checkIncluded = new SimpleCheckbox(tableResources, "check_" + lineId, "", req) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onGET(OgemaHttpRequest req) {
						ResourceList<StringResource> list = dropProgram.getSelectedItem(req)
								.topLevelResourcesIncluded();
						// if(list.contains(listElement)) {
						if (isElementInList(list, listElement.getLocation())) {
							// System.out.println("True List:
							// "+list.getLocation()+"
							// listEl:"+listElement.getLocation());
							setValue(true, req);
						} else {
							// System.out.println("False List:
							// "+list.getLocation()+"
							// listEl:"+listElement.getLocation());
							setValue(false, req);
						}
						// return
						// dropProgram.getChoice(req).topLevelResourcesIncluded().contains(listElement);
					}

					private boolean isElementInList(ResourceList<StringResource> list, String resLoc) {
						for (StringResource tr : list.getAllElements()) {
							if (tr.getValue().equals(resLoc)) {
								return true;
							}
						}
						return false;
					}

					@Override
					public void onPOSTComplete(String data, OgemaHttpRequest req) {
						boolean newValue = getValue(req);
						if (newValue) {
							if (!isElementInList(dropProgram.getSelectedItem(req).topLevelResourcesIncluded(),
									listElement.getLocation())) {
								// if(!dropProgram.getChoice(req).topLevelResourcesIncluded().contains(listElement))
								// {
								StringResource sr = dropProgram.getSelectedItem(req).topLevelResourcesIncluded().add();
								sr.setValue(listElement.getLocation());
								sr.activate(true);
								// dropProgram.getChoice(req).topLevelResourcesIncluded().add(listElement);
							}
						} else {
							StringResource sr = getResListElement(
									dropProgram.getSelectedItem(req).topLevelResourcesIncluded(),
									listElement.getLocation());
							if (sr != null)
								dropProgram.getSelectedItem(req).topLevelResourcesIncluded().remove(sr);
						}
					}
				};
				dropProgram.registerDependentWidget(checkIncluded, req);
				row.addCell(checkIncluded, 2);

				final Resource elRes = listElement;
//				final List<? extends Resource> refList = elRes.getReferencingResources(null);
				final Button delBtn = new Button(tableResources, "delrefbut_" + lineId, "Delete", req) {
					private static final long serialVersionUID = 1L;

					public void onPOSTComplete(String data, OgemaHttpRequest req) {
						elRes.delete();
					};
				};
				delBtn.registerDependentWidget(tableResources);
				row.addCell(delBtn, 3);
//				if (refList.isEmpty()) {
//					row.addCell(new Button(tableResources, "delrefbut_" + lineId, "Delete", req) {
//						private static final long serialVersionUID = 1L;
//
//						public void onPOSTComplete(String data, OgemaHttpRequest req) {
//							elRes.delete();
//						};
//					}, 3);
//				} else {
//					row.addCell(new Button(tableResources, "delrefbut_" + lineId, "Get references", req) {
//						private static final long serialVersionUID = 1L;
//
//						public void onPOSTComplete(String data, OgemaHttpRequest req) {
//							// System.out.println(refList);
//						};
//					}, 3);
//				}
				return row;
			}

		};
		tableResources = new ResourceTable<Resource>(page, "tableResources", rowTemplate);

		WidgetRegisterer wr = new WidgetRegisterer() {
			@Override
			public void registerDependentWidgets(OgemaWidgetBase<?> parent) {
				parent.registerDependentWidget(editBackupInterval);
				parent.registerDependentWidget(checkIncludeRefs);
				parent.registerDependentWidget(checkAllExcept);
				parent.registerDependentWidget(checkWriteJSON);
				parent.registerDependentWidget(dropKnownBackups);
				parent.registerDependentWidget(editDirectory);
			}
		};
		wr.registerDependentWidgets(dropProgram);

		buttonDeleteProgram.registerDependentWidget(dropProgram);
		// buttonDeleteProgram.registerDependentWidget(buttonDeleteProgram);
		// wr.registerDependentWidgets(buttonDeleteProgram);
		// buttonStartProgram.registerDependentWidget(dropKnownBackups); // not
		// working
		backupToDirectoryButton.triggerAction(dropKnownBackups, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		backupToDirectoryButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);

		// buttonSaveAsNewProgram.registerDependentWidget(dropProgram);
		buttonReplay.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		buttonUploadReplay.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		buttonSendProgram.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);

		dropResType.registerDependentWidget(tableResources);
		// dropProgram.registerDependentWidget(tableResources);
		dropProgram.registerDependentWidget(overwriteBackup);

		StaticTable table1 = new StaticTable(1, 5);
		page.append(table1);
		table1.setContent(0, 0, createNewBackupConfigButton);

		StaticTable table2 = new StaticTable(1, 5);
		page.append(table2);
		table2.setContent(0, 0, dropProgram);
		table2.setContent(0, 1, backupToDirectoryButton);
		table2.setContent(0, 2, buttonSendProgram);
		table2.setContent(0, 3, buttonDeleteProgram);
		table2.setContent(0, 4, downloadTempBackup);

		StaticTable table3 = new StaticTable(1, 5);
		table3.setContent(0, 0, "Backup every (hours):").setContent(0, 1, editBackupInterval).setContent(0, 2,
				overwriteBackup);
		page.append(table3);

		StaticTable table4 = new StaticTable(1, 5);
		table4.setContent(0, 0, "Directory:").setContent(0, 1, editDirectory).setContent(0, 2, checkIncludeRefs);
		page.append(table4);

		StaticTable table5 = new StaticTable(1, 5);
		table5.setContent(0, 0, dropKnownBackups).setContent(0, 1, buttonReplay).setContent(0, 2,
				buttonDownloadProgram);
		page.append(table5);

		StaticTable table6 = new StaticTable(1, 5);
		table6.setContent(0, 0, upload).setContent(0, 1, buttonUploadReplay).setContent(0, 2, uploadBackupFileButton);
		page.append(table6);

		// StaticTable table7 = new StaticTable(1, 5);
		// table7.setContent(0, 0, checkAllExcept).setContent(0, 1,
		// checkWriteJSON);
		// page.append(table7);
		page.linebreak().linebreak().linebreak();

		StaticTable table7 = new StaticTable(1, 5);
		table7.setContent(0, 0, "Show:").setContent(0, 1, dropResType).setContent(0, 2, checkAllExcept).setContent(0, 3,
				checkWriteJSON); // FIXME missing... see index.html
		page.append(table7);

		page.append(tableResources);
	}

	public static StringResource getResListElement(ResourceList<StringResource> list, String loc) {
		for (StringResource sr : list.getAllElements()) {
			if (sr.getValue().equals(loc)) {
				return sr;
			}
		}
		return null;
	}

	private void updateDropKnownBackups(BackupConfig c, OgemaHttpRequest req) {

		// TODO update the current Backups from the old format into the new one
		Map<String, String> map = new HashMap<String, String>();
		File destDir = new File(c.destinationDirectory().getValue());

		File[] backups = destDir.listFiles();

		if (backups != null) {
			for (int i = 0; i < backups.length; i++) {
				final String fName = backups[i].getName();
				if (c.overwriteExistingBackup().getValue()) {
					if (fName.endsWith("ogx") || fName.endsWith("json") || fName.endsWith("xml")
							|| fName.endsWith("ogj")) {
						map.put(fName, fName);
					}
				} else {
					if (fName.endsWith("zip")) {
						map.put(fName, fName);
					}
				}

			}
		}
		dropKnownBackups.update(map, req);

	}

	// replaced by Files.copy()
	/*
	 * public static void writeFile(InputStream is, File fileOut) throws
	 * IOException { FileOutputStream outputStream = null; try { outputStream =
	 * new FileOutputStream(fileOut); int read = 0; byte[] bytes = new
	 * byte[1024];
	 * 
	 * while ((read = is.read(bytes)) != -1) { outputStream.write(bytes, 0,
	 * read); } } finally { try { if (outputStream != null)
	 * outputStream.close(); if (is != null) is.close(); } catch (Exception e)
	 * {} } }
	 */

	public static void updateReplayOnClean(Path zipFile) {
		Path replayOC = Paths.get("./replay-on-clean/");
		try {
			if (Files.exists(replayOC))
				FileUtils.cleanDirectory(replayOC.toFile());
			else
				Files.createDirectories(replayOC);
			ZipUtil.deCompress(zipFile, replayOC, new ZipEntryProcessingListener() {

				@Override
				public boolean useFile(ZipEntry entry) {
					final String filename = entry.getName();
					return filename.endsWith(".ogx") || filename.endsWith(".xml");
				}

			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
