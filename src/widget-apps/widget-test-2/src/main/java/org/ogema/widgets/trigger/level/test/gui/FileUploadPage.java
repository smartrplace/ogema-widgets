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
package org.ogema.widgets.trigger.level.test.gui;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;
import org.ogema.core.application.ApplicationManager;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.buttonconfirm.ButtonConfirm;
import de.iwes.widgets.html.buttonconfirm.ButtonConfirmData;
import de.iwes.widgets.html.fileupload.FileUpload;
import de.iwes.widgets.html.fileupload.FileUploadData;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.button.ButtonData;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.listlabel.ListLabel;

public class FileUploadPage {
	
	private final static long MAX_FILE_SIZE = 1024*1024; // 1MB
	private final static String DIR = "tempUploadedFiles";
	private final WidgetPage<?> page;
	private final Header header;
	private final Alert alert;
	private final FileUpload fileUpload;
	private final Button uploadTrigger;
	private final ListLabel uploadedFiles;
	private final ButtonConfirm deleteButton;
	private final Path directory;
	
	public FileUploadPage(final WidgetPage<?> page, final ApplicationManager appMan) {
		this.page = page;
		this.header = new Header(page, "header", "File Upload Page");
		header.addDefaultStyle(WidgetData.TEXT_ALIGNMENT_CENTERED);
		header.setDefaultColor("blue");
		this.alert = new Alert(page, "alert", "");
		alert.setDefaultVisibility(false);
		this.uploadedFiles = new ListLabel(page, "uploadedFiles") {

			private static final long serialVersionUID = 1L;
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				if (directory == null || !Files.isDirectory(directory)) {
					setValues(Collections.<String> emptyList(), req);
					return;
				}
				final List<String> values = new ArrayList<>();
				try {
					Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
						
						@Override
						public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
							values.add(directory.relativize(file).toString());
							return super.visitFile(file, attrs);
						}
						
					});
				} catch (IOException e) { 
					appMan.getLogger().warn("Error parsing files",e);
				}
				setValues(values, req);
			}
			
		};
		
		this.fileUpload = new FileUpload(page, "fileUpload", appMan) {

			private static final long serialVersionUID = 1L;
			
			@Override
			public void onFinished(FileItem item, OgemaHttpRequest req) {
				if (item == null) {
					alert.showAlert("File upload failed for unknown reason", false, req);
					return;
				}
				try {
					// TODO upload widget needs a configuration option to reject files above a certain threshold size;
					// here the complete file has been transferred already
					if (item.getSize() > MAX_FILE_SIZE) {
						alert.showAlert("Uploaded file is too large... max size accepted is 1MB.", false, req);
						return;
					}
					// here we simply copy the file to our temp folder
					try (final InputStream in = new BufferedInputStream(item.getInputStream())) {
						Files.copy(in, directory.resolve(item.getName()), StandardCopyOption.REPLACE_EXISTING);
						alert.showAlert("File successfully uploaded: " + item.getName(), true, req);
					} catch (IOException e) {
						alert.showAlert("Could not copy uploaded file to target location: " + e, false, req);
					}
				} finally {
					try {
						item.delete();
					} catch (Exception ignore) {}
				}
			}
			
		};
		this.uploadTrigger = new Button(page, "uploadTrigger", "Upload file");
		uploadTrigger.addDefaultStyle(ButtonData.BOOTSTRAP_LIGHT_BLUE);
		
		this.deleteButton = new ButtonConfirm(page, "deleteButton", "Delete all files") {

			private static final long serialVersionUID = 1L;
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				if (isDirectoryEmpty(directory))
					disable(req);
				else
					enable(req);
			}

			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				if (directory == null || !Files.isDirectory(directory)) {
					return;
				}
				try {
					FileUtils.cleanDirectory(directory.toFile());
					alert.showAlert("All files deleted", true, req);
				} catch (IOException e) {
					alert.showAlert("File deletion failed: " + e, false, req);
				}
			}
			
		};
		deleteButton.setDefaultConfirmBtnMsg("Delete");
		deleteButton.setDefaultCancelBtnMsg("Cancel");
		deleteButton.setDefaultConfirmPopupTitle("Confirm deletion");
		deleteButton.setDefaultConfirmMsg("Do you really want to clear the folder and delete all uploaded files?");
		deleteButton.addDefaultStyle(ButtonConfirmData.CANCEL_BLUE);
		deleteButton.addDefaultStyle(ButtonConfirmData.CONFIRM_RED);
		deleteButton.addDefaultStyle(ButtonData.BOOTSTRAP_ORANGE);
		
		buildPage();
		setDependencies();
		Path path = null;
		try {
			path = createUploadFolder(appMan);
		} catch (IOException e) {
			appMan.getLogger().error("Folder for temporary files could not be created",e);
		}
		this.directory = path;
	}
	
	private final void buildPage() {
		int row = 0;
		page.append(header).linebreak().append(alert).linebreak()
			.append(new StaticTable(4, 2, new int[] {2,4})
					.setContent(row, 0, "Uploaded files").setContent(row++, 1, uploadedFiles)
					.setContent(row++, 1, deleteButton)
					.setContent(row, 0, "Upload new file").setContent(row++, 1, fileUpload)
					.setContent(row++, 1, uploadTrigger));
	}
	
	private final void setDependencies() {
		uploadTrigger.triggerAction(fileUpload, TriggeringAction.POST_REQUEST, TriggeredAction.POST_REQUEST); // start upload
		fileUpload.triggerAction(alert, FileUploadData.UPLOAD_COMPLETED, TriggeredAction.GET_REQUEST); // display message that file has been uploaded
		fileUpload.triggerAction(uploadedFiles, FileUploadData.UPLOAD_COMPLETED, TriggeredAction.GET_REQUEST); // update view of uploaded files
		fileUpload.triggerAction(deleteButton, FileUploadData.UPLOAD_COMPLETED, TriggeredAction.GET_REQUEST); // update view of uploaded files
		deleteButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST); // show deletion message
		deleteButton.triggerAction(uploadedFiles, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST); // update view of uploaded files
		deleteButton.triggerAction(deleteButton, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST); // update view of uploaded files
	}
	
	private final Path createUploadFolder(ApplicationManager appMan) throws IOException {
		final Path path = appMan.getDataFile(DIR).toPath();
		Files.createDirectories(path);
		return path;
	}
	
	private static boolean isDirectoryEmpty(final Path dir) {
		if (dir == null || !Files.isDirectory(dir))
			return true;
		try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(dir)) {
	        return !dirStream.iterator().hasNext();
	    } catch (IOException e) {
			e.printStackTrace(); //TODO
			return true;
		}
		
	}
	
}
