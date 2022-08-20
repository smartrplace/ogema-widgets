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
package org.smartrplace.internal.resadmin;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.application.Timer;
import org.ogema.core.application.TimerListener;
import org.ogema.core.logging.OgemaLogger;
import org.ogema.core.model.Resource;
import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.model.simple.TimeResource;
import org.ogema.core.resourcemanager.AccessPriority;
import org.ogema.core.resourcemanager.InvalidResourceTypeException;
import org.ogema.core.resourcemanager.NoSuchResourceException;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;
import org.ogema.model.action.Action;
import org.ogema.model.gateway.LocalGatewayInformation;
import org.ogema.model.gateway.init.InitStatus;
//import org.ogema.model.gateway.InitStatus;
import org.ogema.model.locations.Room;
import org.ogema.model.stakeholders.LegalEntity;
import org.ogema.tools.resourcemanipulator.timer.CountDownDelayedExecutionTimer;
import org.smartrplace.internal.resadmin.gui.ZipUtil;
import org.smartrplace.internal.resadmin.logic.TopLevelResTypePattern;
import org.smartrplace.internal.resadmin.pattern.BackupConfigPattern;
import org.smartrplace.internal.resadmin.patternlistener.BackupConfigListener;
import org.smartrplace.resadmin.config.BackupConfig;
import org.smartrplace.resadmin.config.ResAdminConfig;

import de.iwes.util.format.StringFormatHelper;
import de.iwes.util.logconfig.LogHelper;
import de.iwes.util.performanceeval.ExecutionTimeLogger;
import de.iwes.util.resource.ResourceHelper;
import de.iwes.util.resource.ValueResourceHelper;
import de.iwes.util.resourcelist.ResourceListHelper;

// here the controller logic is implemented
public class ResAdminController {

	//TODO: From OGEMA-Fileinstall, should be moved to utils
    private static final String JSON_EXTENSION1 = ".ogj";
    private static final String XML_EXTENSION1 = ".ogx";
    private static final String JSON_EXTENSION2 = ".json";
    private static final String XML_EXTENSION2 = ".xml";
	public OgemaLogger log;
    public final ApplicationManager appMan;
    private final ResourcePatternAccess advAcc;

	public final ResAdminConfig appConfigData;
	//private final ResourceList<?> specialDeviceList;
	private List<ResourceList<?>> specialDeviceList() {
		String resListSpecial = System.getProperty("org.smartrplace.internal.resadmin.show_special_resourcelistelements");
		if(resListSpecial != null) {
			String[] resListSpecials = resListSpecial.split(",");
			List<ResourceList<?>> result = new ArrayList<>();
			for(String resString: resListSpecials) {
				result.add(ResourceHelper.getResource(resString.trim(),
						ResourceList.class, appMan.getResourceAccess()));
			}
			return result ;
		} else
			return null;		
	}
	// FIXME global variables are accessed from multiple session threads, synchronization missing
	public List<TopLevelResTypePattern> typePatterns = new ArrayList<>(); 
	public TopLevelResTypePattern patternForResource = null;
	private final Timer timer;
	private final BackupConfigListener backupConfigListener = new BackupConfigListener();
	 
    String stdDir = null;
	
    public ResAdminController(ApplicationManager appMan) {
		this.appMan = appMan;
		this.log = appMan.getLogger();
		this.advAcc = appMan.getResourcePatternAccess();
		
		//check for clean start
		String name = getAppConfigName();
		// FIXME this would require dynamically generated file permissions... not feasible!
		stdDir = getPropertySecure("org.ogema.app.resadmin.backup_base_path","backup" + File.separator);
//		stdDir = System.getProperty("org.ogema.app.resadmin.backup_base_path","backup" + File.separator);
		final String replayDir = getPropertySecure("org.ogema.app.resadmin.replay_oncleanstart_path", null);
//		String replayDir = System.getProperty("org.ogema.app.resadmin.replay_oncleanstart_path");
		final ResAdminConfig appConfigData = appMan.getResourceAccess().getResource(name);
		
		/*String resListSpecial = System.getProperty("org.smartrplace.internal.resadmin.show_special_resourcelistelements");
		if(resListSpecial != null)
			specialDeviceList = ResourceHelper.getResource(resListSpecial,
				ResourceList.class, appMan.getResourceAccess());
		else
			specialDeviceList = null;*/
		
		if (replayDir != null) {
			if (appConfigData == null) {
				log.info("Replay after clean start from directory {}",replayDir);
				try {
					final ImportResult result = replayBackup(replayDir, null);
					final String message = result.getMessage();
					if (message != null)
						log.error(message);
					else if (log.isInfoEnabled())
						log.info("Initial resource import: imported " + result.getTotalNrOfImportedResources() + " resources.");
//					Integer replayNum = Integer.getInteger("org.ogema.resadmin.replaynum", 1);
//					for(int i=1; i<replayNum; i++)
//						replayBackup(replayDir, null);
//					if(replayNum >= 1) {
//						String mes = replayBackup(replayDir, null);
//						if (mes != null) 
//							log.error(mes);
//					}
/*					String createInitResource = getPropertySecure("org.ogema.app.resadmin.createInitResource", "false");
					if(Boolean.parseBoolean(createInitResource)) {
						appMan.getResourceManagement().createResource("initStatus", InitStatus.class);
					}*/
				} catch(SecurityException e) {
					log.error("Access denied",e);
				}
			} else {  // resource already exists (appears in case of non-clean start)
				log.warn("ResAdmin app detected non-clean start, directory would be: {}",replayDir);
			}
		} else {
			log.warn("ResAdmin: No replay directory configured");
		}

		LogHelper.resetStartup(appMan);

		if (appConfigData != null)
			this.appConfigData = appConfigData;
		else
			this.appConfigData = initConfigurationResource(name);
		
		resourceCleanups();
		
		String delayProp = System.getProperty("org.ogema.sim.simulationdelay");
		if(delayProp == null)
			setInit();
		else {
			long delay = Math.abs(Long.parseLong(delayProp));
			if(delay == 0l) setInit();
			else new CountDownDelayedExecutionTimer(appMan, delay) {
				@Override
				public void delayedExecution() {
					setInit();
				}
			};
		}
        initDemands();

        //reset application inits
		Resource mirrorList = appMan.getResourceAccess().getResource("serverMirror");
		if(mirrorList != null) {
			IntegerResource initStatus = mirrorList.getSubResource("initStatus", IntegerResource.class);
			initStatus.setValue(0);
		}
		
		//add overwriteExistingBackup to all Configs
		for(BackupConfig config: this.appConfigData.configList().getAllElements()) {
			if(config.overwriteExistingBackup().exists())
				continue;
			BooleanResource overwrite = config.overwriteExistingBackup().create();
					overwrite.setValue(isSpecialDir(config.destinationDirectory().getValue()));
		}
        
        //check for missing action elements
		for(BackupConfig config: this.appConfigData.configList().getAllElements()) {
			if(!config.run().isActive()) {
				addRunAction(config);
				config.run().activate(true);
			}
		}	
		
		//start timer
		timer = appMan.createTimer(60*60000, new TimerListener() {
			@Override
			public void timerElapsed(Timer timer) {
				for(BackupConfig config: ResAdminController.this.appConfigData.configList().getAllElements()) {
					if(checkIfNonNegative(config.nextBackupScheduled())) {
						if(config.nextBackupScheduled().getValue() >= ResAdminController.this.appMan.getFrameworkTime()) {
							runBackup(config);
							if(checkIfNonNegative(config.autoBackupInterval())) setNextAutoTimer(config);
						}
					} else {
						if(checkIfNonNegative(config.autoBackupInterval())) {
							setNextAutoTimer(config);
						}
					}
				}
				
			}
		});
	}
    
	private void setInit() {
		InitStatus init = appMan.getResourceManagement().createResource("initStatus", InitStatus.class);
		BooleanResource replayDone = init.replayOnClean().create();
		replayDone.setValue(true);
		init.activate(true);    	
		log.info("ReplayOnClean released.....\n\n");
    }

	private static boolean checkIfNonNegative(TimeResource tr) {
		return tr.exists() && tr.getValue() >= 0;
	}
	
	//requires prior checking of resources
	private void setNextAutoTimer(BackupConfig config) {
		config.nextBackupScheduled().create();
		long nextTime = appMan.getFrameworkTime() + config.autoBackupInterval().getValue();
		config.nextBackupScheduled().setValue(nextTime);
	}

    /*
     * This app uses a central configuration resource, which is accessed here
     */
    private ResAdminConfig initConfigurationResource(String name) {
    	final ResAdminConfig appConfigData = (ResAdminConfig) appMan.getResourceManagement().createResource(name, ResAdminConfig.class);
		appConfigData.name().create();
		//TODO provide different sample, provide documentation in code
		appConfigData.name().setValue("sampleName");
		appConfigData.configList().create();
		appConfigData.configList().setElementType(BackupConfig.class);
		appConfigData.activate(true);
		appMan.getLogger().debug("{} started with new config resource", getClass().getName());
		if(appConfigData.configList().size() <= 0) {
			addConfiguration(appConfigData, "Standard");
		}
		return appConfigData;
    }
//    
//    private String setAppConfigDataIfAvailable() {
//		String configResourceDefaultName = ResAdminConfig.class.getSimpleName().substring(0, 1).toLowerCase()+ResAdminConfig.class.getSimpleName().substring(1);
//		final String name = ResourceHelper.getUniqueResourceName(configResourceDefaultName);
//		appConfigData = appMan.getResourceAccess().getResource(name);
//		return name;
//    }
    
    private static final String getAppConfigName() {
    	return ResAdminConfig.class.getSimpleName().substring(0, 1).toLowerCase()+ResAdminConfig.class.getSimpleName().substring(1);
    }
    
    /*
     * register ResourcePatternDemands. The listeners will be informed about new and disappearing
     * patterns in the OGEMA resource tree
     */
    public void initDemands() {
		advAcc.addPatternDemand(BackupConfigPattern.class, backupConfigListener, AccessPriority.PRIO_LOWEST, this);
    }

	public void close() {
		advAcc.removePatternDemand(BackupConfigPattern.class, backupConfigListener);
		timer.destroy();
    }

	private class UpdateTypeListData {
		/** Main result, used in ResourceType dropDown*/
		List<TopLevelResTypePattern> newList = new ArrayList<>();
		/** Only used in addResourceType*/
		List<Class<? extends Resource>> classList= new ArrayList<>();
		/** Only used in addResourceType*/
		List<String> simpleNames = new ArrayList<>();
	}
	
	private TopLevelResTypePattern addResourceType(Class<? extends Resource> type, UpdateTypeListData data) {
		if(!data.classList.contains(type)) {
			data.classList.add(type);
			String simpleName = type.getSimpleName();
			@SuppressWarnings("unchecked")
			List<Resource> resList = (List<Resource>) appMan.getResourceAccess().getToplevelResources(type);
			TopLevelResTypePattern newPattern;
			if(data.simpleNames.contains(simpleName)) {
				newPattern = new TopLevelResTypePattern(type, true, resList);
				data.newList.add(newPattern);
				TopLevelResTypePattern p = getPatternByName(data.newList, simpleName);
				if(p != null) p.setNameIsFullClassName(true);
			} else {
				newPattern = new TopLevelResTypePattern(type, resList);
				data.newList.add(newPattern);
				data.simpleNames.add(simpleName);
			}
			return newPattern;
		}
		return null;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void upateTypeList() {
		UpdateTypeListData data = new UpdateTypeListData();
		patternForResource = addResourceType(Resource.class, data);
		if(patternForResource == null) {
			throw new IllegalStateException("New UpdateTypeListData cannot contain the element already!");
		}
		for(Resource topRes: appMan.getResourceAccess().getToplevelResources(null)) {
			addResourceType(topRes.getResourceType(), data);
		}
		List<ResourceList> lrl;
		List<ResourceList<?>> specialDevList = specialDeviceList();
		lrl = new ArrayList<>();
		if(specialDevList != null) for(ResourceList<?> resList: specialDevList) {
			lrl.add(resList);
		} else if(!Boolean.getBoolean("org.smartrplace.internal.resadmin.show_also_resourcelistelements")) {
			typePatterns = data.newList;
			return;
		} else
			lrl = appMan.getResourceAccess().getResources(ResourceList.class);
		for(ResourceList rl: lrl) {
			if(rl.size() <= 0) continue;
			TopLevelResTypePattern pattern = addResourceType(rl.getElementType(), data);
			if(pattern == null) {
				pattern = getPatternByClass(data.newList, rl.getElementType());
			}
			List<? extends Resource> sublist = rl.getAllElements();
			Iterator<? extends Resource> it = sublist.iterator();
			while(it.hasNext()) {
				Resource r = it.next();
				pattern.resList.add(r);
			}
			pattern.resList.addAll(rl.getAllElements());
		}
		typePatterns = data.newList;
	}
	
	private static TopLevelResTypePattern getPatternByName(List<TopLevelResTypePattern> list, String name) {
		for(TopLevelResTypePattern p: list) {
			if(p.getName().equals(name)) return p;
		}
		return null;
	}
	
	private static TopLevelResTypePattern getPatternByClass(List<TopLevelResTypePattern> list, Class<? extends Resource> type) {
		for(TopLevelResTypePattern p: list) {
			if(p.type.equals(type)) return p;
		}
		return null;
	}
	
	public BackupConfig addConfiguration(ResAdminConfig appConfigData, String name) {
		if(name == null) name = "new backup configuration";
		BackupConfig nc = ResourceListHelper.createNewNamedElement(appConfigData.configList(), name, false);
		nc.includeStandardReferences().create();
		nc.includeStandardReferences().setValue(true);
		nc.backupAllExceptResoucesIncluded().create();
		nc.backupAllExceptResoucesIncluded().setValue(false);
		nc.writeJSON().create();
		nc.writeJSON().setValue(false);
		nc.autoBackupInterval().create();
		nc.autoBackupInterval().setValue(-1);
		nc.topLevelResourcesIncluded().create();
		nc.destinationDirectory().create();
		if(stdDir != null) {
			nc.destinationDirectory().setValue(stdDir);
		}
		addRunAction(nc);
		nc.activate(true);		
		return nc;
	}
	
	private static void addRunAction(BackupConfig nc) {
		nc.run().controllingApplication().create();
		nc.run().controllingApplication().setValue(BackupConfigPattern.APP_NAME);
		nc.run().stateControl().create();
		nc.run().stateControl().setValue(false);
	}
	
	public BackupConfig saveNewBackupConfig(String newName, String newDestination, boolean overwrite) {
		BackupConfig bc = appConfigData.configList().add();
		bc.name().<StringResource> create().setValue(newName);
		bc.destinationDirectory().<StringResource> create().setValue(newDestination);
		bc.overwriteExistingBackup().<BooleanResource> create().setValue(overwrite);
		bc.autoBackupInterval().<TimeResource> create().setValue(-1);
		bc.backupAllExceptResoucesIncluded().<BooleanResource> create();
		bc.includeStandardReferences().<BooleanResource> create().setValue(true);
		bc.run().create();
		bc.run().controllingApplication().<StringResource> create().setValue(BackupConfigPattern.APP_NAME);
		bc.run().description().create().setAsReference(bc.name());
		bc.run().stateControl().<BooleanResource> create().setValue(false);
		bc.topLevelResourcesIncluded().create();
		bc.writeJSON().<BooleanResource> create().setValue(false);

		bc.activate(true);
//		OGEMAResourceCopyHelper.copySubResourceIntoResourceList(appConfigData.configList(), bc, appMan, false);
		return bc;
	}
	
//	public BackupConfig saveConfigurationWithNewName(BackupConfig source, String newName) {
//		BackupConfig bc = OGEMAResourceCopyHelper.copySubResourceIntoResourceList(appConfigData.configList(), source, appMan, false);
//		List<KnownBackups> list = bc.knownBackups().getAllElements();
//		for(KnownBackups kb: list) {
//			bc.knownBackups().remove(kb);
//		}
//		//Maybe rename the NewName if there is already one BackupConfig the this name.
//		bc.name().setValue(newName);
//		bc.activate(true);
//		return bc;
//	}
	
	public void deleteConfiguration(BackupConfig config) {
		appConfigData.configList().remove(config);
	}
	
	private static boolean removeLocationsFromList(List<Resource> allList, List<StringResource> exLocs) {
		boolean removedSomething = false;
		for(StringResource exResPath: exLocs) {
			for(Resource topRes: allList) {
				if(topRes.getLocation().equals(exResPath.getValue())) {
					allList.remove(topRes);
					removedSomething = true;
					break;
				}
			}
		}
		return removedSomething;
	}
	
	private static void copyFileIfExists(File source, String destDirStr) {
		if(source.exists()) {
			//copy file
			try {
				Files.copy(source.toPath(),
						new File(destDirStr, source.getName()).toPath(), REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void copyDirectoryIfExists(File source, File destDir) {
		if(!source.exists()) return;
		try {
			File destDirNew = new File(destDir, source.getName());
			destDirNew.mkdir();
			FileUtils.copyDirectory(source, destDirNew);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	final static boolean isSpecialDir(String destDirStr) {
		return destDirStr.contains("extBackup") || destDirStr.contains("replay-on-clean");
	}
	
	public File runBackup(BackupConfig config) {
		String destDirStr = config.destinationDirectory().getValue();
		return runBackup(config, destDirStr);
	}
	
	/**
	 * @param config
	 * @param destDirStr
	 * @return
	 * @throws SecurityException if access to some configured file is denied
	 */
	public File runBackup(final BackupConfig config, final String destDirStr) {
		return AccessController.doPrivileged(new PrivilegedAction<File>() {

			@Override
			public File run() {
				return runBackupUnprivileged(config, destDirStr);
			}
		});
		
	}
	
	public File runBackupUnprivileged(BackupConfig config, String destDirStr) {

		
		String strDate = StringFormatHelper.getCurrentDateForPath(appMan);

		File destDir = null;
		
		if(!config.overwriteExistingBackup().exists()) {
			BooleanResource overwrite = config.overwriteExistingBackup().create();
			overwrite.setValue(isSpecialDir(destDirStr));
			overwrite.activate(false);
		}
	
		final boolean overwrite = config.overwriteExistingBackup().getValue();
		if(!overwrite) {
			File configDir = new File(destDirStr);
			if(destDirStr.endsWith("generalBackup"))
				destDir = new File(configDir.getAbsolutePath() + strDate);
			else
				destDir = new File(configDir.getAbsolutePath() + "/" + strDate);
		} else {
			destDir = new File(destDirStr);
		}

		if(!destDir.exists()) {
			destDir.mkdirs();
		} 
		if(overwrite) {
			for( File f : destDir.listFiles()) {
				if(!f.getName().endsWith(".zip")) {
					if(f.isDirectory()) {
						try {
							FileUtils.deleteDirectory(f); // FIXME apache lib requires (java.lang.RuntimePermission "getClassLoader")
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						f.delete();
					}
				}
			}
//    		try{FileUtils.cleanDirectory(destDir);} catch (IOException e) {e.printStackTrace();}
		}
		
		RefData refData = new RefData(config.includeStandardReferences().getValue(), config.writeJSON().getValue());
		
		//first top-level resources
		if(config.backupAllExceptResoucesIncluded().getValue()) {
			List<Resource> allList = appMan.getResourceAccess().getToplevelResources(null);
			removeLocationsFromList(allList, config.topLevelResourcesIncluded().getAllElements());
			for(Resource topRes: allList) {
				if(topRes instanceof ResourceList) {
					ResourceList<?> rl = (ResourceList<?>)topRes;
					@SuppressWarnings("unchecked")
					List<Resource> allEls = (List<Resource>) rl.getAllElements();
					if(removeLocationsFromList(allEls, config.topLevelResourcesIncluded().getAllElements())) {
						for(Resource r: allEls) {
							//TODO: resource list and references still missing
							writeResourceAndReferences(destDir, r, refData);
						}
					} else {
						writeResourceAndReferences(destDir, topRes, refData);
					}
				} else {
					writeResourceAndReferences(destDir, topRes, refData);
				}
			}
		} else {
			for(StringResource topResPath: config.topLevelResourcesIncluded().getAllElements()) {
				Resource topRes = appMan.getResourceAccess().getResource(topResPath.getValue());
				if (topRes == null)
					continue;
				writeResourceAndReferences(destDir, topRes, refData);
			}
		}
		
		if(config.backupAllExceptResoucesIncluded().getValue()) {
			//backup also known xml files etc. from rundir
			//no automated playback is foreseen, though
			File bFile = new File("./config", "homematic.devices");
			copyFileIfExists(bFile, destDirStr);
			bFile = new File(".", "tunnels.sh");
			copyFileIfExists(bFile, destDirStr);
			bFile = new File("./config", "ParameterDefinition.xml");
			copyFileIfExists(bFile, destDirStr);
			
			//copy entire folder config
			copyDirectoryIfExists(new File("./config"), destDir);
			copyDirectoryIfExists(new File("./security"), destDir);
		}
		
		/*for(ResourceTypeConfigurations typeStr: config.resourceTypeConfigs().getAllElements()) {
			Class<? extends Resource> type;
			try {
				type = (Class<? extends Resource>) Class.forName(typeStr.resourceType().getValue());
				for(Resource topRes: appMan.getResourceAccess().getToplevelResources(type)) {
					for(Resource exRes: typeStr.excludedResources().getAllElements()) {
						if(exRes.equals(topRes)) {
							continue;
						}
					}
					writeResourceAndReferences(destDir, topRes, refData);
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}*/
		
		//add to known backups
//		KnownBackups kbu = config.knownBackups().add();
//		kbu.fileDirectory().create();
//		kbu.fileDirectory().setValue(destDirStr);
//		kbu.name().create();
//		kbu.name().setValue(strDate);
//		kbu.timeOfBackup().create();
//		kbu.timeOfBackup().setValue(appMan.getFrameworkTime());
//		if(userName != null) {
//			kbu.user().userName().create();
//			kbu.user().userName().setValue(userName);
//		}
		
		
//		zip the file
		if(!overwrite) {
			String customName = destDir.getName();
			Path zipFile = destDir.getParentFile().toPath().resolve(customName + ".zip");
			File zip = zipFile.toFile();
			ZipUtil.compressEntireDirectory(zipFile, destDir.toPath());
			try {
				FileUtils.deleteDirectory(destDir);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return zip;
		}
		return destDir;
	}
	
	private class RefData {
		@SuppressWarnings("rawtypes")
		Class[] refTypes = {Room.class, LegalEntity.class, Action.class, LocalGatewayInformation.class};
		/**If an element of a subRefName is found directly below a top-level resource it is considered a
		 * potential linking resource that has prefix "A"
		 */
		String[] subRefNames = {"run"};
		/**Top-level resources with such a name get prefix "Z"*/
		String[] finalResourceNames = {"OGEMASimulationConfiguration","HomeMatic", "RundirBackup"};
		List<Resource> refResources = new ArrayList<Resource>();
		int prefix = 0;
		
		final boolean writeJson;
		final boolean includeStdRefs;
		
		public RefData(boolean includeStdRefs, boolean writeJson) {
			this.includeStdRefs = includeStdRefs;
			this.writeJson = writeJson;
		}
	}
	
	private static boolean arrayContains(String val, String[] array) {
		for (String el: array) {
			if (el.equals(val)) return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private void writeResourceAndReferences(File parentDir, Resource res, RefData data) {
		//write rooms and users
//		log.debug("parentDir : " + parentDir + ", res : " + res + ", data : " + data);
		if(data.includeStdRefs) for(Class<? extends Resource> resType: data.refTypes) {
			try {
				for(Resource refRes: res.getSubResources(resType, true)) {
					Resource locRefRes = refRes.getLocationResource();
					if(locRefRes.isTopLevel()) {
						writeIfNotYetWrittenResource(locRefRes, parentDir, data, true);
					}
				}				
			} catch(NoSuchResourceException e) {
				//Should not occur, but sometimes required as quick fix
				continue;
			}
		}
		writeIfNotYetWrittenResource(res, parentDir, data, true);
	}
	
	@SuppressWarnings("unchecked")
	private void writeIfNotYetWrittenResource(Resource res, File parentDir, RefData data, boolean checkPrefix) {
		try {
		Resource locRefRes = res.getLocationResource();
		if(data.refResources.contains(locRefRes)) return;
		String plusPrefix = "";
		if(checkPrefix) {
			boolean found = false;
			if(arrayContains(res.getLocation(), data.finalResourceNames)) {
				plusPrefix = "Z";
				found = true;
			} else {
				for(Class<? extends Resource> resType: data.refTypes) {
if(resType == null) {
	log.error("   NULL AS RESOURCE_TYPE in "+data.refTypes.length+" elements");
	continue;
}
					if(resType.isAssignableFrom(locRefRes.getResourceType())) {
						plusPrefix = "";
						found = true;
						break;
					}
				}
			}
			if(!res.isTopLevel()) {
				int resListIndex = 0;
				List<ResourceList<?>> specialDevList = specialDeviceList();
				Resource parent = res.getParent();
				for(;resListIndex<specialDevList.size(); resListIndex++) {
					if(parent.equalsLocation(specialDevList.get(resListIndex)))
						break;
				}
				if(resListIndex >= specialDevList.size())
					resListIndex = 0;
				plusPrefix = "SR"+String.format("%02d_", resListIndex);
				found = true;				
			}
			if(!found) {
				for(Resource r: res.getSubResources(false)) {
					if(arrayContains(r.getName(), data.subRefNames)) {
						plusPrefix = "A";
						found = true;
						break;
					}
				}
				if(!found) {
					plusPrefix = "B";
				}
			}
		}
		if(data.writeJson) {
			writeJsonFile(parentDir, res, plusPrefix+String.format("%03d", data.prefix));
		} else {
			writeXmlFile(parentDir, res, plusPrefix+String.format("%03d", data.prefix));
		}
		data.prefix++;
		data.refResources.add(locRefRes);
		} catch(OutOfMemoryError e) {
			String message = "OutOfMemory In Resource:"+res.getLocation();
			System.out.println(message);
			log.error(message);
			throw e;
		}
	}
	
	/**
	 * This method is used to import resources from serialized files.
	 * 
	 * We distinguish three cases
	 * <ul>
	 * 	<li>c == null: in this case we assume that the parameter directoryName indeed is the path to a directory,
	 * 		which contains .ogx files, which we import into the system (on clean start)
	 * <li>c != null && c.overwriteExistingBackup == false: in this case directoryName denotes a zip file in c.destinationDirectory,
	 * 		containing again .ogx files (or .xml, .ogj, .json), which we import by means of the serialization manager
	 * <li>c != null && c.overwriteExistingBackup == true: in this case directoryName denotes a single .ogx file (or similar) 
	 * 		in c.destinationDirectory, which we import again.
	 * </ul>
	 * 
	 * @param directoryName	
	 * 		either a directory containing .ogx files (in case c == null) -> all files are imported
	 * 		or a filename of a zip file (if c != null and c.overwriteExistingBackup == false)
	 * 		or a filename of an ogx file (id != null and c.overwriteExistingBackup == true)
	 *  
	 * return null or error message
	 * @throws SecurityException if file access is not granted
	 */
	public ImportResult replayBackup(String directoryName, BackupConfig c) {
		final File folder;
		if (c != null) 
			folder = new File(c.destinationDirectory().getValue() + "/" + directoryName);
		else 
			folder = new File(directoryName);
		if(!folder.exists()) {
			final String message = "Replay directory "+folder.getPath()+" does not exist"; 
			log.debug(message);
			return new ImportResult(message);
		}
		int lastImported = -1;
		int thisImported = 0;
		ImportResult result = null;
		while (thisImported > lastImported) {
			lastImported = thisImported;
			result = replayBackup0(folder);
			if (result.getNrOfFiles() < 2)
				break;
			thisImported = result.getTotalNrOfImportedResources();
		}
		return result;
	}
	
	private ImportResult replayBackup0(final File folder) {
		ExecutionTimeLogger etl = new ExecutionTimeLogger("REPLAY_ON_CLEAN", appMan);
		final AtomicInteger fileCnt = new AtomicInteger(0);
	    String message = null;
	    final List<Resource> imported = new ArrayList<>();
	    //we need to filter the zips out here
	    if (folder.isDirectory()) {
		    File[] files = folder.listFiles(new FilenameFilter() {
			    public boolean accept(File dir, String name) {
			        return !name.toLowerCase().endsWith(".zip");
			    }
			});
		 
		    Arrays.sort(files);
		    for (final File fileEntry : files) {
		    	boolean xml = isXmlFile(fileEntry);
		    	boolean json = isJsonFile(fileEntry);
		    	//TODO add ogx and ogj
		    	boolean isSubResource = json && 
		    			(fileEntry.getName().startsWith("SR"));
		    	int resListIdx = -1;
		    	List<ResourceList<?>> specialDevList = null;
		    	if(isSubResource) {
					specialDevList = specialDeviceList();
		    		try {
		    			resListIdx = Integer.parseInt(fileEntry.getName().substring(2, 4));
		    		} catch(NumberFormatException e) {
		    			if(specialDevList != null && specialDevList.size() == 1)
		    				resListIdx = 0;
		    			else {
		    				log.error("SR<index> required if more than one replay-on-clean parent-list specified!");
		    				continue;
		    			}
		    		}
		    		if(resListIdx >= specialDevList.size()) {
	    				log.error("The following SR-file has invalid index:"+fileEntry.getName());		    			
		    		}
		    	}
		        if (!fileEntry.isDirectory() && (xml || json) ){
		        	log.debug("Replay of file {}",fileEntry.getPath());
		    		try {
		    			final Resource result;
		    			if (json) {
		    				if(isSubResource) {
		    					ResourceList<?> resList = specialDevList.get(resListIdx);
		    					if(resList != null)
		    						result = installJsonSubResource(fileEntry.toPath(), resList);
		    					else
		    						continue;
		    				} else
		    					result = installJson(fileEntry.toPath());
		    			} else 
		    				result = installXml(fileEntry.toPath());
		    			if (result != null) {
		    				imported.add(result);
			    			fileCnt.getAndIncrement();
		    			}
					} catch (Exception e) {
						if (message == null) {
							message = "Error reading XML/JSON file " + fileEntry.getPath() + ": " + e;
						} else {
							message.concat(", "+fileEntry.getPath());
						}
						log.error(message,e);
					}
		    		etl.intermediateStep(fileEntry.getName());
		        }
		    }
	    } else if(folder.getPath().endsWith(".zip")) { // import .zip file
	    	try (FileSystem fs = FileSystems.newFileSystem(folder.toPath(), (ClassLoader) null)) {
	    		final Path base = fs.getRootDirectories().iterator().next();
	    		// using somewhat cumbersome Java7 method here, Java8 is more convenient
	    		Files.walkFileTree(base, new FileVisitor<Path>() {

					@Override
					public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
						return FileVisitResult.CONTINUE;
//						return FileVisitResult.SKIP_SUBTREE;
					}

					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
	 	    			if (isXmlFile(file)) {
	 	    				try {
	 	    					final Resource result = installXml(file);
	 	    					if (result != null) {
	 	    						imported.add(result);
	 	    						fileCnt.getAndIncrement();
	 	    					}
	 	    				} catch (Exception e) {
	 	    					log.error("Could not import file {}: ",file,e);
	 	    				}
	 	    			}
		    			else if (isJsonFile(file)){
	 	    				try {
	 	    					final Resource result = installJson(file);
	 	    					if (result != null) {
	 	    						imported.add(result);
	 	    						fileCnt.getAndIncrement();
	 	    					}
	 	    				} catch (Exception e) {
	 	    					log.error("Could not import file {}: ",file,e);
	 	    				}
	 	    			}
	 	    			return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
						log.warn("Error accessing file {} in zip folder {}: ", file, folder, exc);
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
						return FileVisitResult.CONTINUE;
					}
				});
//	    		message = cnt.get() + " files successfully imported from zip file " + folder.getPath();
	    		log.info("{} files successfully imported from zip file {}", fileCnt.get(), folder.getPath());
	    	} catch (IOException e1) {
	    		message = "Could not access zip file "+ folder.getPath() + ": " + e1;
				log.error(message);
				return new ImportResult(message);
			}
	    } else { // import single .ogx file /* XML,json */
	    	try {
		    	if (isXmlFile(folder)) {
		    		final Resource result = installXml(folder.toPath());
		    		if (result != null) {
		    			imported.add(result);
		    			fileCnt.getAndIncrement();
		    		}
		    	}
		    	else if (isJsonFile(folder)) {
		    		final Resource result = installJson(folder.toPath());
		    		if (result != null) {
		    			imported.add(result);
		    			fileCnt.getAndIncrement();
		    		}
		    	}
	    	} catch (Exception e) {
	    		message = "Error reading XML/JSON file " + folder.getPath() + ": " + e;
	    		log.error(message,e);
	    	}
	    }
	    etl.finish();
	    return new ImportResult(message, imported, fileCnt.get());
	}
	
	private void writeJsonFile(File parentDir, Resource res, String namePrefix) {
		//System.out.println("Parent:"+parentDir+" prefix:"+namePrefix);
		File ownFile = new File(parentDir, namePrefix+"_"+res.getName()+JSON_EXTENSION1);
		//System.out.println("File:"+ namePrefix+"_"+res.getName()+JSON_EXTENSION);
		try (PrintWriter out = new PrintWriter(ownFile, "UTF-8")) {
			appMan.getSerializationManager(20, false, true).writeJson(out, res);
		} catch (IOException e) {
			log.error("serialization failed for resource {}", res.getPath(), e);
		}
	}
	
	private void writeXmlFile(File parentDir, Resource res, String namePrefix) {
		//System.out.println("Parent:"+parentDir+" prefix:"+namePrefix);
		File ownFile = new File(parentDir, namePrefix+"_"+res.getName()+XML_EXTENSION1);
		//System.out.println("File:"+ namePrefix+"_"+res.getName()+XML_EXTENSION);
		try (PrintWriter out = new PrintWriter(ownFile, "UTF-8")) {
            appMan.getSerializationManager(20, false, true).writeXml(out, res);
		} catch (IOException e) {
            log.error("serialization failed for resource {}", res.getPath(), e);
		}
	}
	
    private Resource installJson(Path file) throws Exception {
//        try (FileInputStream fis = new FileInputStream(file); InputStreamReader in = new InputStreamReader(fis, Charset.forName("UTF-8"))) {
    	try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
    		log.debug("importing JSON file {}",file);
            return appMan.getSerializationManager().createFromJson(reader);
        } catch(InvalidResourceTypeException e) {
        	log.warn("Resource type in file {} not found",file);
        	return null;
        }
    }

    private Resource installJsonSubResource(Path file, Resource parent) throws Exception {
    	try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
    		log.debug("importing JSON file {}",file);
    		return appMan.getSerializationManager().createFromJson(reader, parent);
    	} catch(InvalidResourceTypeException e) {
    		log.warn("Resource type in file {} not found",file);
    		return null;
    	}
  }

    
    private Resource installXml(Path file) throws Exception {
    	try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
//        try (FileInputStream fis = new FileInputStream(file); InputStreamReader in = new InputStreamReader(fis, Charset.forName("UTF-8"))) {
        	log.debug("importing XML file {}",file);
        	return appMan.getSerializationManager().createFromXml(reader);
        } catch(InvalidResourceTypeException e) {
        	log.warn("Resource type in file {} not found",file);
        	return null;
        }
    }
    
    private static final boolean isXmlFile(Path file) {
        return isXmlType(file.getFileName().toString().toLowerCase());
    }
    
    private static final boolean isXmlFile(File file) {
        return isXmlType(file.getName().toLowerCase());
    }
    
    private static boolean isJsonFile(File file) {
        return isJsonType(file.getName().toLowerCase());
    }
    
    static boolean isJsonFile(Path file) {
        return isJsonType(file.getFileName().toString().toLowerCase());
    }
    
    private static final boolean isXmlType(String filename) {
    	return filename.endsWith(XML_EXTENSION1) || filename.endsWith(XML_EXTENSION2);
    }
    
    private static final boolean isJsonType(String filename) {
    	return filename.endsWith(JSON_EXTENSION1) || filename.endsWith(JSON_EXTENSION2);
    }
    
    private static String getPropertySecure(String prop, String defaultVal) {
    	try {
    		return System.getProperty(prop, defaultVal);
    	} catch (SecurityException e) {
    		return defaultVal;
    	}
    }
    
    public static class ImportResult {
    	
    	private final String message;
    	private final List<Resource> resources;
    	private final int nrFiles;
    	
    	public ImportResult(String errorMessage) {
    		this(Objects.requireNonNull(errorMessage), null, 0);
		}
    	
    	ImportResult(String message, Collection<Resource> resources, int nrFiles) {
    		this.message = message;
    		this.resources = resources == null || resources.isEmpty() ? Collections.emptyList() : new ArrayList<>(resources);
    		this.nrFiles = nrFiles;
		}
    	
    	public String getMessage() {
    		return message;
    	}
    	
    	public List<Resource> getImportedResources() {
    		return resources;
    	}
    	
    	public int getTotalNrOfImportedResources() {
    		int cnt = 0;
    		for (Resource r: resources) {
    			cnt += r.getSubResources(true).size()+1;
    		}
    		return cnt;
    	}
    	
    	public int getNrOfFiles() {
    		return nrFiles;
    	}
    	
    	@Override
    	public String toString() {
    		if (message != null)
    			return message;
    		return "Imported " + resources.size() + " resources";
    	}
    	
    }
    
    private void resourceCleanups() {
		List<Room> topRooms = appMan.getResourceAccess().getToplevelResources(Room.class);
		ResourceList<Room> newRooms = appMan.getResourceAccess().getResource("rooms");
		if(newRooms == null)
			return;
		for(Room topRoom: topRooms) {
			Room newRoom = ResourceListHelper.getNamedElementFlex(topRoom.name().getValue(), newRooms);
			if(newRoom == null)
				continue;
			List<Resource> refs = topRoom.getReferencingNodes(false);
			for(Resource ref: refs) {
				ref.setAsReference(newRoom);
			}
			topRoom.delete();
		}
	}
}
