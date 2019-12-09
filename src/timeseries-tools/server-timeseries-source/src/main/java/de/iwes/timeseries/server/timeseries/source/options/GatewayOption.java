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
package de.iwes.timeseries.server.timeseries.source.options;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.smartrplace.analysis.backup.parser.api.GatewayBackupAnalysis;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.html.selectiontree.LinkingOption;
import de.iwes.widgets.html.selectiontree.SelectionItem;
import de.iwes.widgets.html.selectiontree.samples.SelectionItemImpl;

public class GatewayOption extends LinkingOption {
	
	private final GatewayBackupAnalysis gatewayParser;
	
	/**
	 * Pass null to indicate that the type must not be offered as selection
	 * @param rto
	 */
	public GatewayOption(GatewayBackupAnalysis gatewayParser) {
		this.gatewayParser=gatewayParser;
	}

	@Override
	public LinkingOption[] dependencies() {
		return null;
	}

	@Override
	public List<SelectionItem> getOptions(List<Collection<SelectionItem>> items) {
		return gatewayParser.getGatewayIds().stream()
			.map(id -> new SelectionItemImpl(id, "GW " + id))
			.collect(Collectors.toList());
	}

	@Override
	public String id() {
		return "gateways";
	}

	@Override
	public String label(OgemaLocale arg0) {
		return "Select gateways";
	}

}
