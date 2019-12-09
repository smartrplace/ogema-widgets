package de.iee.sema.remote.user.administration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ogema.core.application.ApplicationManager;
import org.osgi.service.condpermadmin.ConditionalPermissionAdmin;
import org.osgi.service.condpermadmin.ConditionalPermissionInfo;
import org.osgi.service.condpermadmin.ConditionalPermissionUpdate;

import de.iwes.widgets.api.extended.WidgetData;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.alert.Alert;
import de.iwes.widgets.html.alert.AlertData;
import de.iwes.widgets.html.fileupload.FileUpload;
import de.iwes.widgets.html.fileupload.FileUploadData;
import de.iwes.widgets.html.fileupload.FileUploadListener;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.textarea.TextArea;

class CreatePageInit {

	private final WidgetPage<?> page;
	private final Header header;
	private final Alert alert;
	private final Alert infoAlert;
	private final TextArea usersField;
	private final TextArea permsField;
	private final Label userLabel;
	private final Label permLabel;
	private final FileUpload usersUpload;
	private final FileUpload permsUpload;
	private final Button uploadButton;
	private final Button installButton;
	
	private final ConditionalPermissionAdmin cpa;
	
	CreatePageInit(final WidgetPage<?> page, final ApplicationManager appMan, final ConditionalPermissionAdmin cpa) {
		this.page = page;
		this.cpa = cpa;
		this.header = new Header(page, "header", "Remote User Administation");
		header.addDefaultStyle(WidgetData.TEXT_ALIGNMENT_CENTERED);
		this.alert = new Alert(page, "alert", "");
		alert.setDefaultVisibility(false);
		
		this.infoAlert = new Alert(page, "infoAlert", "You can enter the usernames and permissions in the two text ares below "
				+ "manually or upload files containing these information. A permission could look like this "
				+ "(You have to replace the <value> entries with your needed information, it's also possible to enter "
				+ "multiple permissions, just add a new '()' after the example ResourcePermission with the needed "
				+ "information): "
				+ "allow { [<usertype (machine/natural)> \"{$user}\"] (org.ogema.accesscontrol.ResourcePermission \"path=<pathToResource>/*\""
				+ " \"<needed permissions (read,write,addsub,create,activity)>)}");
		infoAlert.setDefaultVisibility(true);
		infoAlert.addDefaultStyle(AlertData.BOOTSTRAP_INFO);

		this.usersField = new TextArea(page, "usersField") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				setToolTip("You can enter the multiple usernames manually or upload a file", req);
			}
		};
		usersField.setDefaultWidth("100%");
		usersField.setDefaultText("");
		
		this.permsField = new TextArea(page, "permsField") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onGET(OgemaHttpRequest req) {
				setToolTip("You can enter the permissions manually or upload a file", req);
			}
		};
		permsField.setDefaultWidth("100%");
		permsField.setDefaultText("");
		
		this.userLabel = new Label(page, "userLabel", "Users: ");
		this.permLabel = new Label(page, "permLabel", "Permissions: ");
		this.usersUpload = new FileUpload(page, "upload", appMan);
		this.permsUpload = new FileUpload(page, "policies", appMan);		
		
		this.uploadButton = new Button(page, "uploadButton") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				
				final FileUploadListener<Float> userL = (fileItem, context, req2) -> {
					try {
						usersField.setText(fileItem.getString("UTF-8"), req);
					} catch (IOException e) {
						alert.showAlert("User upload Failure: Reading in the userfile '" + fileItem.getName() + "' failed", false, req);
					}
				};
				
				final FileUploadListener<Float> permL = (fileItem, context, req2) -> {
					try {
						permsField.setText(fileItem.getString("UTF-8"), req);
					} catch (IOException e) {
						alert.showAlert("Policy upload Failure: Reading in the file '" + fileItem.getName() + "' failed", false, req);
					}
				};
				// TODO ensure listeners are unregistered / not called multiple times
				usersUpload.registerListener(userL, 1.5f, req);
				permsUpload.registerListener(permL, 1.5f, req);
			}

		};
		uploadButton.setDefaultText("Upload selected files");
		
		
		this.installButton = new Button(page, "installButton", true) {

			private static final long serialVersionUID = 1L;

			@Override
			public void onPOSTComplete(String data, OgemaHttpRequest req) {
				final String users = usersField.getText(req).trim();
				final String perms = permsField.getText(req).trim();
				if (users.isEmpty() || perms.isEmpty()) {
					alert.showAlert("Please upload files first", false, req);
					return;
				}
				final List<String> usersList = new ArrayList<>();
				try (final BufferedReader reader = new BufferedReader(new StringReader(users))) {
					String line;
					while ((line = reader.readLine()) != null) {
						line = line.trim();
						if (isEmptyOrCommentLine(line))
							continue;
						usersList.add(line);
					}
				} catch (IOException e) {
					alert.showAlert("Unexpected error " + e, false, req);
					return;
				}
				if (usersList.isEmpty()) {
					alert.showAlert("No users selected", false, req);
					return;
				}
				final List<String> permsList;
				try {
					permsList = parseUserPolicies(perms, usersList);
				} catch (IOException e) {
					alert.showAlert("Unexpected error " + e, false, req);
					return;
				}
				if (permsList.isEmpty()) {
					alert.showAlert("No permissions selected", false, req);
					return;
				}
				createRestUsers(usersList.stream());
				installPolicies(permsList.stream(), cpa);
				alert.showAlert(permsList.size() + " policies installed", true, req);
			}

			private void createRestUsers(Stream<String> stream) {
				stream.forEach(gw -> {
					List<String> users = new ArrayList<>();
					appMan.getAdministrationManager().getAllUsers().forEach(user -> users.add(user.getName()));
					if (!users.contains(gw))
						appMan.getAdministrationManager().createUserAccount(gw, false);
				});
			}

		};
		installButton.setDefaultText("Install user permissions");
		buildPage();
		setDependencies();
	}
	
	private final void buildPage() {
		final StaticTable uploadTable = new StaticTable(2, 2);
		uploadTable.setContent(0, 0, userLabel).setContent(0, 1, usersUpload)
			.setContent(1, 0, permLabel).setContent(1, 1, permsUpload);

		page.append(header).linebreak().append(infoAlert).append(alert)
			.append(uploadTable).append(uploadButton).append(usersField).append(permsField)
			.append(installButton);
	}
	
	private final void setDependencies() {
		uploadButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		uploadButton.triggerAction(usersUpload, TriggeringAction.POST_REQUEST, TriggeredAction.POST_REQUEST);
		uploadButton.triggerAction(permsUpload, TriggeringAction.POST_REQUEST, TriggeredAction.POST_REQUEST);
		uploadButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		usersUpload.triggerAction(usersField, FileUploadData.UPLOAD_COMPLETED, TriggeredAction.GET_REQUEST);
		permsUpload.triggerAction(permsField, FileUploadData.UPLOAD_COMPLETED, TriggeredAction.GET_REQUEST);
		installButton.triggerAction(alert, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
	}
	
	static boolean isEmptyOrCommentLine(final String line) {
		return line.isEmpty() || line.charAt(0) == '#' || line.startsWith("//");
	}

	private List<String> parseUserPolicies(String fileData, List<String> userList) throws IOException {
		try (BufferedReader reader = new BufferedReader(new StringReader(fileData))) {
			final List<String> permInfos = new ArrayList<>();
			String gwSpecificUserPolicy = null;
			String line;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				// If the line is a comment or an empty line skip it.
				if (isEmptyOrCommentLine(line))
					continue;
				for (String gw : userList) {
					// Replacing generic user entry with specific rest username
					gwSpecificUserPolicy = line.replace("{$user}", gw);

					// replace user data with the appropriate condition information
					gwSpecificUserPolicy = gwSpecificUserPolicy.replaceFirst("(.*machine)[ \t]*\"([a-zA-Z_0-9]*)\"",
							"$1 \"urp:$2\"");
					gwSpecificUserPolicy = gwSpecificUserPolicy.replaceFirst("(.*\\[)[ \t]*machine(.*)",
							"$1org.osgi.service.condpermadmin.BundleLocationCondition$2");
					gwSpecificUserPolicy = gwSpecificUserPolicy + " " + EditPageInit.getValidPolicyID(getPolicyNames(cpa), gw);
					
					permInfos.add(gwSpecificUserPolicy);
				}
			}
			return permInfos;
		}
	}

	// See StaticPoliciesImpl.java from permission-admin
	static void installPolicies(final Stream<String> permInfos, final ConditionalPermissionAdmin cpa) {
		// First get the permissions table
		ConditionalPermissionUpdate cpu = cpa.newConditionalPermissionUpdate();
		List<ConditionalPermissionInfo> permInfoList = cpu.getConditionalPermissionInfos();
		permInfos
			.map(permInfo -> {
				// Create new permission info object each new entry
				// Multiple entries with same name are not permitted.
				ConditionalPermissionInfo cpi;
				try {
					cpi = cpa.newConditionalPermissionInfo(permInfo);
				} catch (Exception e) {
						org.slf4j.LoggerFactory.getLogger(RemoteUserAdministration.class)
								.error(String.format("Error setting permission '%s'", permInfo), e);
					return null;
				}
				return cpi;
			})
			.filter(Objects::nonNull)
			.forEach(permInfoList::add);
		cpu.commit();
	}
	
	static List<String> getPolicyNames(final ConditionalPermissionAdmin cpa) {
		return cpa.newConditionalPermissionUpdate().getConditionalPermissionInfos().stream()
			.map(ConditionalPermissionInfo::getName)
			.collect(Collectors.toList());
	}
	
}
