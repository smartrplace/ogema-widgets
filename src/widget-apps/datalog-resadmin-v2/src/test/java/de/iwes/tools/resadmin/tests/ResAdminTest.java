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
package de.iwes.tools.resadmin.tests;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ogema.core.model.Resource;
import org.ogema.core.tools.SerializationManager;
import org.ogema.model.locations.Room;
import org.ogema.model.sensors.TemperatureSensor;
import org.ogema.tools.widgets.test.base.WidgetsTestBase;
import org.ops4j.io.FileUtils;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;


@ExamReactorStrategy(PerClass.class)
public class ResAdminTest extends WidgetsTestBase {
	
	private final static String REPLAY_DIR = "replay-on-clean";
	private final static Path REPLAY_PATH = Paths.get(REPLAY_DIR);
	
	public ResAdminTest() {
		super(false);
		FileUtils.delete(new File("data"));
	}
	
	@Before
	public void clearReplayOnClean() throws IOException, BundleException {
		if (Files.exists(REPLAY_PATH))
			FileUtils.delete(REPLAY_PATH.toFile());
		Files.createDirectories(REPLAY_PATH);
		final Bundle b = findResadminApp();
		Assert.assertNotNull(b);
		if (b.getState() == Bundle.ACTIVE)
			b.stop();
		// deleting the file and all resources will make the ResAdmin app think it is started clean. 
		// this is a bit of hack, though...
		final File uncleanMarker = b.getDataFile("clean");
		if (uncleanMarker.exists())
			uncleanMarker.delete();
		for (Resource r : getApplicationManager().getResourceAccess().getToplevelResources(null)) {
			r.delete();
		}
	}
	
	@Override
	public Option[] frameworkBundles() {
		Option[] opt = super.frameworkBundles();
		Option[] options = new Option[opt.length + 2];
		System.arraycopy(opt, 0, options, 0, opt.length);
		// must not start immediately
		options[opt.length] = CoreOptions.mavenBundle("org.ogema.tools", "datalog-resadmin-v2", widgetsVersion).noStart();
		options[opt.length+1] = CoreOptions.systemProperty("org.ogema.app.resadmin.replay_oncleanstart_path").value(REPLAY_DIR);
		return options;
	}
	
	private final Bundle findResadminApp() {
		for (Bundle b : ctx.getBundles()) {
			final String symbName = b.getSymbolicName();
			if (symbName != null && symbName.equals("org.ogema.tools.datalog-resadmin-v2"))
				return b;
		}
		return null;
	}
	
	private final SerializationManager getSerializationManager() {
		return getSerializationManager(1000, false, true);
	}
	
	private final SerializationManager getSerializationManager(final int maxDepth, final boolean followReferences, final boolean writeSchedules) {
		return getApplicationManager().getSerializationManager(maxDepth, followReferences, writeSchedules);
	}
	
	private final void serializeResource(final Resource r, final SerializationManager sman, final String filePrefix, final boolean jsonOrXml) throws IOException {
		final String result = jsonOrXml ? sman.toJson(r) : sman.toXml(r);
		Files.write(REPLAY_PATH.resolve(filePrefix + (jsonOrXml ? ".json" : ".xml")), result.getBytes(StandardCharsets.UTF_8));
	}
	
	@Test
	public void resourceImportWorks() throws IOException, BundleException {
		final Bundle b = findResadminApp();
		Assert.assertNotNull(b);
		Assert.assertNotEquals(Bundle.ACTIVE, b.getState());
		final Resource r = getApplicationManager().getResourceManagement().createResource(newResourceName(), TemperatureSensor.class);
		final String path = r.getLocation();
		r.addDecorator("dec", Room.class);
		serializeResource(r, getSerializationManager(), "res", false);
		r.delete();
		Assert.assertFalse(r.exists());
		b.start();
		Assert.assertEquals(Bundle.ACTIVE, b.getState());
		final Resource r2 = getApplicationManager().getResourceAccess().getResource(path);
		Assert.assertNotNull("Resource not found",r2);
		assertExists(r2);
		assertExists(r2.getSubResource("dec"));
		r2.delete();
		b.stop();
	}
	
	// requires 2 imports
	@Test
	public void referenceImportWorks() throws IOException, BundleException {
		final Bundle b = findResadminApp();
		Assert.assertNotNull(b);
		Assert.assertNotEquals(Bundle.ACTIVE, b.getState());
		final Resource r0 = getApplicationManager().getResourceManagement().createResource(newResourceName(), TemperatureSensor.class);
		final String path0 = r0.getLocation();
		final Resource r1 = getApplicationManager().getResourceManagement().createResource(newResourceName(), Room.class);
		final String path1 = r1.getLocation();
		r1.addDecorator("dec", r0);
		serializeResource(r0, getSerializationManager(), "A", false);
		serializeResource(r1, getSerializationManager(), "B", false);
		r0.delete();
		r1.delete();
		Assert.assertFalse(r0.exists());
		Assert.assertFalse(r1.exists());
		b.start();
		Assert.assertEquals(Bundle.ACTIVE, b.getState());
		final Resource r02 = getApplicationManager().getResourceAccess().getResource(path0);
		final Resource r12 = getApplicationManager().getResourceAccess().getResource(path1);
		Assert.assertNotNull("Resource not found",r02);
		assertExists(r02);
		Assert.assertNotNull("Resource not found",r12);
		assertExists(r12);
		final Resource dec = r12.getSubResource("dec");
		Assert.assertNotNull("Reference got lost in import",dec);
		assertLocationsEqual(r02, dec);
		r02.delete();
		r12.delete();
		b.stop();
	}
	
	// requires 2 imports
	@Test
	public void cyclicReferenceImportWorks() throws IOException, BundleException {
		final Bundle b = findResadminApp();
		Assert.assertNotNull(b);
		Assert.assertNotEquals(Bundle.ACTIVE, b.getState());
		final Resource r0 = getApplicationManager().getResourceManagement().createResource(newResourceName(), Room.class);
		final Resource r1 = getApplicationManager().getResourceManagement().createResource(newResourceName(), Room.class);
		final Resource r2 = getApplicationManager().getResourceManagement().createResource(newResourceName(), Room.class);
		final Resource r3 = getApplicationManager().getResourceManagement().createResource(newResourceName(), Room.class);
		final String path0 = r0.getLocation();
		final String path1 = r1.getLocation();
		final String path2 = r2.getLocation();
		final String path3 = r3.getLocation();
		final String decorator = "dec";
		// create a tricky loop
		r3.addDecorator(decorator, r0);
		r2.addDecorator(decorator, r3.<Resource> getSubResource(decorator));
		r1.addDecorator(decorator, r2.<Resource> getSubResource(decorator));
		r0.addDecorator(decorator, r1.<Resource> getSubResource(decorator));
		serializeResource(r0, getSerializationManager(), "A", false);
		serializeResource(r1, getSerializationManager(), "B", false);
		serializeResource(r2, getSerializationManager(), "C", false);
		serializeResource(r3, getSerializationManager(), "D", false);
		r0.delete();
		r1.delete();
		r2.delete();
		r3.delete();
		b.start();
		Assert.assertEquals(Bundle.ACTIVE, b.getState());
		final Resource s0 = getApplicationManager().getResourceAccess().getResource(path0);
		final Resource s1 = getApplicationManager().getResourceAccess().getResource(path1);
		final Resource s2 = getApplicationManager().getResourceAccess().getResource(path2);
		final Resource s3 = getApplicationManager().getResourceAccess().getResource(path3);
		Assert.assertNotNull(s0.getSubResource(decorator));
		Assert.assertNotNull(s1.getSubResource(decorator));
		Assert.assertNotNull(s2.getSubResource(decorator));
		Assert.assertNotNull(s3.getSubResource(decorator));
		assertLocationsEqual(s3.getSubResource(decorator), s0);
		assertLocationsEqual(s2.getSubResource(decorator), s3.getSubResource(decorator));
		assertLocationsEqual(s1.getSubResource(decorator), s2.getSubResource(decorator));
		assertLocationsEqual(s0.getSubResource(decorator), s1.getSubResource(decorator));
		s0.delete();
		s1.delete();
		s2.delete();
		s3.delete();
		b.stop();
	}
	
	// requires 3 imports
	@Test
	public void cyclicReferenceImportWorks2() throws IOException, BundleException {
		final Bundle b = findResadminApp();
		Assert.assertNotNull(b);
		Assert.assertNotEquals(Bundle.ACTIVE, b.getState());
		final Resource r0 = getApplicationManager().getResourceManagement().createResource(newResourceName(), Room.class);
		final Resource r1 = getApplicationManager().getResourceManagement().createResource(newResourceName(), Room.class);
		final String path0 = r0.getLocation();
		final String path1 = r1.getLocation();
		final String decorator = "dec";
		// create a tricky loop
		final Resource d11 = r1.addDecorator(decorator, TemperatureSensor.class);
		final Resource d12 = d11.addDecorator(decorator, TemperatureSensor.class);
		final Resource d01a = r0.addDecorator(decorator + "a", d11);
		final Resource d01b = r0.addDecorator(decorator + "b", d01a.<Resource> getSubResource(decorator));
		serializeResource(r0, getSerializationManager(), "A", false);
		serializeResource(r1, getSerializationManager(), "B", false);
		r0.delete();
		r1.delete();
		b.start();
		Assert.assertEquals(Bundle.ACTIVE, b.getState());
		final Resource s0 = getApplicationManager().getResourceAccess().getResource(path0);
		final Resource s1 = getApplicationManager().getResourceAccess().getResource(path1);
		final Resource d01a2 = s0.getSubResource(decorator + "a");
		final Resource d01b2 = s0.getSubResource(decorator + "b");
		assertLocationsEqual(d01a2, s1.getSubResource(decorator));
		assertLocationsEqual(d01b2, d01a2.getSubResource(decorator));
		s0.delete();
		s1.delete();
		b.stop();
	}

	private static void assertExists(final Resource r) {
		Assert.assertTrue("Resource " + r + " does not exist", r != null && r.exists());
	}
	
	private static void assertLocationsEqual(Resource r1, Resource r2) {
		Assert.assertNotNull(r1);
		Assert.assertNotNull(r2);
		Assert.assertTrue("Resource " + r1.getPath() + " and " + r2.getPath() + " should have equal locations.", r1.equalsLocation(r2));
	}


}
