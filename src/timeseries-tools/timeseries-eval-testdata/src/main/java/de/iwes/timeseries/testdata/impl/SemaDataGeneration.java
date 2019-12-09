package de.iwes.timeseries.testdata.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.model.ResourceList;
import org.ogema.core.model.schedule.AbsoluteSchedule;
import org.ogema.core.model.simple.StringResource;
import org.ogema.core.recordeddata.RecordedData;
import org.ogema.core.recordeddata.RecordedDataConfiguration;
import org.ogema.core.recordeddata.RecordedDataConfiguration.StorageType;
import org.ogema.core.tools.SerializationManager;
import org.ogema.model.gateway.remotesupervision.GatewayTransferInfo;
import org.ogema.recordeddata.RecordedDataStorage;
import org.ogema.tools.timeseries.api.MemoryTimeSeries;
import org.ogema.tools.timeseries.implementations.FloatTreeTimeSeries;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.LoggerFactory;
import org.smartrplace.ogema.recordeddata.slotsdb.CloseableDataRecorder;
import org.smartrplace.ogema.recordeddata.slotsdb.SlotsDbFactory;

import de.iwes.sema.pattern.SemaPointsPattern;

public class SemaDataGeneration {

	static void importLogdata(final boolean importData, ApplicationManager am) throws Exception, NoClassDefFoundError {
		final Bundle b = FrameworkUtil.getBundle(SemaDataGeneration.class);
		final Path targetFolder = Paths.get(System.getProperty("org.smartrplace.tools.upload.folder", "ogemaCollect"));
		if (slotsDataExistsAndIsUptoDate(targetFolder, am)) {
			generateGatewayResourcesOnly(targetFolder, am, b);
			LoggerFactory.getLogger(TimeSeriesData.class).info("SlotsDb data already exists, skipping generation. Folder: {}", targetFolder);
			return;
		} else {
			retryDelete(targetFolder.resolve("rest").toFile(), 1);
		}
		final BundleContext ctx = FrameworkUtil.getBundle(SemaDataGeneration.class).getBundleContext();
		ServiceReference<SlotsDbFactory> sref =null;
		for (int i=0; i<30;i++) {
			sref = ctx.getServiceReference(SlotsDbFactory.class);
			if (sref != null)
				break;
			Thread.sleep(1000);
		}
		final SlotsDbFactory slots = sref != null ? ctx.getService(sref) : null;
		if (slots == null)
			throw new NullPointerException("SlotsDbFactory service not available");
		final Enumeration<URL> e = b.findEntries("serverdata/", "*", true);
		// Maps <gateway id, zip file>
		final Map<String, URL> backupFiles = new HashMap<>();
		final Map<String, URL> logdataFiles = new HashMap<>();
		final Map<String, URL> pointsInfos = new HashMap<>();
		while (e.hasMoreElements()) {
			URL u = e.nextElement();
			final String uStr = u.toString();
			if (uStr.endsWith("/")) { // directory
				continue;
			}
			final String[] gwId = extractGatewayId(uStr);
			if (gwId == null)
				continue;
			if (gwId[1].startsWith("generalBackup"))
				backupFiles.put(gwId[0], u);
			else if (gwId[1].startsWith("slotsdb"))
				logdataFiles.put(gwId[0], u);
			else if (gwId[1].equalsIgnoreCase("semaPointsInfo.xml")) 
				pointsInfos.put(gwId[0], u);
		}
		for (String id : backupFiles.keySet()) {
			if (!logdataFiles.containsKey(id))
				continue;
			try {
				generateGatewayResource(id, am, pointsInfos.get(id));
				importGatewayData(id, backupFiles.get(id), logdataFiles.get(id), am, slots, targetFolder);
			} catch (Exception ee) {
				LoggerFactory.getLogger(TimeSeriesData.class).warn("Error generating gateway data: {}",id,e);
			}
		}

	}
	
	private static void generateGatewayResourcesOnly(final Path base, final ApplicationManager am, final Bundle bundle) throws UnsupportedEncodingException, IOException {
		if (!Files.isDirectory(base.resolve("rest")))
			return;
		for (File f : base.resolve("rest").toFile().listFiles()) {
			if (!f.isDirectory())
				continue;
			final Path slots = f.toPath().resolve("slotsdb");
			if (Files.isDirectory(slots)) {
				URL url = bundle.getResource("serverdata/" + f.getName() + "/semaPointsInfo.xml");
				generateGatewayResource(f.getName(), am, url);
			}
		}
	}
	
	private static void generateGatewayResource(String gatewayId, ApplicationManager am, URL pointsInfoUrl) throws UnsupportedEncodingException, IOException {
		@SuppressWarnings("unchecked")
		ResourceList<GatewayTransferInfo> list = am.getResourceManagement().createResource("controlledTestGateways",
				ResourceList.class);
		list.setElementType(GatewayTransferInfo.class);
		GatewayTransferInfo gateway = getOrCreateGatewayInfo(gatewayId, list, am, pointsInfoUrl);
	}
	
	private static boolean slotsDataExistsAndIsUptoDate(final Path base, ApplicationManager am) {
		final long current = am.getFrameworkTime();
		if (!Files.isDirectory(base.resolve("rest")))
			return false;
		for (File f : base.resolve("rest").toFile().listFiles()) {
			if (!f.isDirectory())
				continue;
			final Path slots = f.toPath().resolve("slotsdb");
			if (Files.isDirectory(slots)) {
				int latest = 0;
				for (File slotsDayFolder : slots.toFile().listFiles()) {
					if (!slotsDayFolder.isDirectory())
						continue;
					try {
						int local = Integer.parseInt(slotsDayFolder.getName());
						if (local > latest)
							latest = local;
					} catch (NumberFormatException ok) {}
				}
				if (latest > 0) {
					try {
						Date d = new SimpleDateFormat("yyyyMMdd").parse(String.valueOf(latest));
						if (current - d.getTime() < 48 * 60 * 60 * 1000)
							return true;
					} catch (Exception e) {};
				}
			}
		}
		return false;
	}

	/**
	 * @param url
	 * @return either null, or length-2 array: 0: gateway id 1: filename
	 */
	private static String[] extractGatewayId(String url) {
		int idxE = url.lastIndexOf('/');
		if (idxE < 0)
			return null;
		int idxS = url.substring(0, idxE).lastIndexOf('/');
		if (idxS < 0)
			return null;
		return new String[] { url.substring(idxS + 1, idxE), url.substring(idxE + 1) };
	}

	// copies resource backup and recorded data, moving the latter such that the
	// last timestamps are now
	private static void importGatewayData(String gatewayId, URL backupFile, URL logdataFile, ApplicationManager am,
			SlotsDbFactory slots, Path uploadFolder) throws IOException {
		LoggerFactory.getLogger(TimeSeriesData.class).info("Importing test data for {}", gatewayId);
		final Path targetFolder = uploadFolder.resolve("rest").resolve(gatewayId);
		Files.createDirectories(targetFolder);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			return;
		}
		String backup = backupFile.toString();
		int idx = backup.lastIndexOf('/');
		backup = backup.substring(idx + 1);
		final Path target = targetFolder.resolve(backup);

		try (final InputStream is = backupFile.openStream()) {
			Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
		}

		final Path tempFolder = Files.createTempDirectory(am.getDataFile("").toPath(), "temp_");
		CloseableDataRecorder sourceDb = null;
		CloseableDataRecorder targetDb = null;
		try {
			final Path zipFile = tempFolder.resolve("slotsdb.zip");
			try (final InputStream is = logdataFile.openStream()) {
				Files.copy(is, zipFile, StandardCopyOption.REPLACE_EXISTING);
			}
			final Path slotsFolder = tempFolder.resolve("slotsTemp");
			Files.createDirectories(slotsFolder);
			unzip(zipFile, slotsFolder);
	
			if (!Files.exists(slotsFolder) || !Files.isDirectory(slotsFolder))
				return;
			sourceDb = slots.getInstance(slotsFolder);
			targetDb = slots.getInstance(targetFolder.resolve("slotsdb"));
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) {
				return;
			}
			
			List<String> ids = sourceDb.getAllRecordedDataStorageIDs();
			for (String id : ids) {
				try {
					RecordedData data = sourceDb.getRecordedDataStorage(id);
					SampledValue last = data.getPreviousValue(Long.MAX_VALUE);
					if (last == null)
						continue;
					long tend = last.getTimestamp();
					long now = am.getFrameworkTime();
					final long diff = now - tend;
					MemoryTimeSeries copy = new FloatTreeTimeSeries();
					List<SampledValue> values = new ArrayList<>();
					Iterator<SampledValue> it = data.iterator();
					data = null;
					SampledValue sv;
					while (it.hasNext()) {
						sv = it.next();
						values.add(new SampledValue(sv.getValue(), sv.getTimestamp() + diff, sv.getQuality()));
					}
					copy.addValues(values, now);
					RecordedDataStorage targetData = targetDb.getRecordedDataStorage(id);
					RecordedDataConfiguration config = new RecordedDataConfiguration();
					config.setStorageType(StorageType.ON_VALUE_UPDATE);
					if (targetData == null) {
						targetData = targetDb.createRecordedDataStorage(id, config);
					} else if (targetData.getConfiguration() == null
							|| targetData.getConfiguration().getStorageType() == null) {
						targetData.setConfiguration(config);
					}
					targetData.insertValues(values);
				} catch (Exception e) {
					LoggerFactory.getLogger(TimeSeriesData.class).warn("RecordedData copy transaction failed for id {}", id, e);
				}
			}
		} finally {
			if (sourceDb != null) {
				try {
					sourceDb.close();
				} catch (Exception e) {
					LoggerFactory.getLogger(TimeSeriesData.class).warn("Could not close temporary SlotsDb instance {}", e);
				}
			}
			// XXX must release sourceDB file locks, but they are not cached; in the worst case, temporary data cannot be deleted
			// FIXME not working either...
			System.gc();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			retryDelete(tempFolder.toFile(),5);
		}
	}
	
	private static void retryDelete(File directory, int retries) {
		if (!directory.exists())
			return;
		IOException e = null;
		for (int i=0; i<retries; i++) {
			try {
				FileUtils.deleteDirectory(directory);
				return;
			} catch (IOException iox) {
				e = iox;
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException ie) {
				break;
			}
		}
		LoggerFactory.getLogger(TimeSeriesData.class).warn("Could not delete temp folder {}", directory, e);
	}

	private static GatewayTransferInfo getOrCreateGatewayInfo(String id, ResourceList<GatewayTransferInfo> gateways,
			ApplicationManager am, URL pointsInfoUrl) throws UnsupportedEncodingException, IOException {
		for (GatewayTransferInfo g : gateways.getAllElements()) {
			if (g.id().getValue().equals(id))
				return g;
		}
		GatewayTransferInfo g = gateways.add();
		g.id().<StringResource>create().setValue(id);
		SemaPointsPattern points = am.getResourcePatternAccess().addDecorator(g, "semaPayload",	SemaPointsPattern.class);
		am.getResourcePatternAccess().createOptionalResourceFields(points, SemaPointsPattern.class, false);
		points.gatewayId.setValue(id);
		g.activate(true);
		if (pointsInfoUrl != null) {
			final SerializationManager sman = am.getSerializationManager(10, false, true);
			try (final Reader reader = new InputStreamReader(pointsInfoUrl.openStream(), "UTF-8")) {
				sman.applyXml(reader, g, true);
			}
			final long now = am.getFrameworkTime();
			final SampledValue lastTotal = points.totalPointsHistorical.getPreviousValue(Long.MAX_VALUE);
			final long diff;
			if (lastTotal == null) {
				diff = 0;
			} else {
				diff = now-lastTotal.getTimestamp();
			}
			for (AbsoluteSchedule schedule : g.getSubResources(AbsoluteSchedule.class, true)) {
				moveScheduleValuesToNow(schedule, diff);
			}
		}
		return g;
	}
	
	private static void moveScheduleValuesToNow(AbsoluteSchedule schedule, long diff) {
		final SampledValue sv = schedule.getPreviousValue(Long.MAX_VALUE);
		if (sv == null)
			return;
		final List<SampledValue> newValues = new ArrayList<>();
		for (SampledValue old : schedule.getValues(Long.MIN_VALUE)) {
			newValues.add(new SampledValue(old.getValue(), old.getTimestamp()+diff, old.getQuality()));
		}
		schedule.replaceValues(Long.MIN_VALUE, Long.MAX_VALUE, newValues);
	}

	private static void unzip(final Path zip, final Path targetDir) throws IOException {
		try (FileSystem zipFileSystem = FileSystems.newFileSystem(zip, null)) {
			final Path root = zipFileSystem.getPath("/");
			// walk the zip file tree and copy files to the destination
			Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
				
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					String dirStr = file.toString();
					if (dirStr.startsWith("\\") || dirStr.startsWith("/"))
						dirStr = dirStr.substring(1);
					final Path destFile = targetDir.resolve(dirStr);
					Files.copy(file, destFile, StandardCopyOption.REPLACE_EXISTING);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
//					if (dir.toString().equals("/"))
//						return FileVisitResult.CONTINUE;
					String dirStr = dir.toString();
					if (dirStr.startsWith("\\") || dirStr.startsWith("/"))
						dirStr = dirStr.substring(1);
					final Path dirToCreate = targetDir.resolve(dirStr);
					if (Files.notExists(dirToCreate)) {
						Files.createDirectories(dirToCreate);
					}
					return FileVisitResult.CONTINUE;
				}
			});
		}
	}

}
