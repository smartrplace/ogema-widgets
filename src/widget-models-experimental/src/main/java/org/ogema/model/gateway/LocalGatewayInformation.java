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
package org.ogema.model.gateway;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.chartexportconfig.ChartExportConfig;
import org.ogema.model.prototypes.PhysicalElement;
import org.smartrplace.apps.eval.timedjob.TimedJobConfig;

/** 
 * Information on the OGEMA Gateway
 * Use {@link PhysicalElement#name()} for a human readable name 
 */
public interface LocalGatewayInformation extends PhysicalElement {
	
	/**
	 * Id of the OGEMA gateway / instance. The id should be unique within all OGEMA gateways in a project
	 * and within all gateways connected to common cloud services.
	 */
	StringResource id();
	
	/** Base URL of gateway to connect to via internet, e.g. https://customer.manufacturer.de:2000
	 * Use element in SubCustomerSuperiorData instead<br>
	 * Note: Shall be switched to SubcustomerSuperiorData#gatewayBaseUrl() in the future*/
	StringResource gatewayBaseUrl();
	
	/** Url to gateway installation and operation documentation
	 * Use element in SubCustomerSuperiorData instead*/
	@Deprecated
	StringResource gatewayOperationDatabaseUrl();
	
	/** Url to overview on gateway documentation sources. If this is existing then the {@link #gatewayOperationDatabaseUrl()} may 
	 * not be used as the link overview should usually contain this.
	 * Use element in SubCustomerSuperiorData instead*/
	@Deprecated
	StringResource gatewayLinkOverviewUrl();

	/** ID of system default locale (obtained by OgemaLocale.getLocale().getLanguage() ), which can
	 * be used to obtain OgemaLocale object by OgemaLocale#getLocale
	 * Use element in SubCustomerSuperiorData instead*/
	@Deprecated
	StringResource systemLocale();
	
	ResourceList<TimedJobConfig> timedJobs();
	
	StringResource initDoneStatus();
	
	ChartExportConfig chartExportConfig();
}