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
package de.iwes.timeseries.eval.base.provider.utils;

import org.ogema.tools.resource.util.ResourceUtils;

import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public class ResultTypeDefault implements ResultType {
	private final String id;
	private final String label;
	private final String description;
	private final ResultStructure resultStructure;
	private final ValueType valueType;
	private final boolean singleValueOrArray;
	
	public ResultTypeDefault(String id, String label, String description, ResultStructure resultStructure,
			ValueType valueType, boolean singleValueOrArray) {
		this.id = id;
		this.label = label;
		this.description = description;
		this.resultStructure = resultStructure;
		this.valueType = valueType;
		this.singleValueOrArray = singleValueOrArray;
	}
	
	public ResultTypeDefault(String label, String description) {
		this(ResourceUtils.getValidResourceName(label), label, description, ResultStructure.PER_INPUT,
				ValueType.NUMERIC, true);
	}
	public ResultTypeDefault(String label) {
		this(ResourceUtils.getValidResourceName(label), label, label, ResultStructure.PER_INPUT,
				ValueType.NUMERIC, true);
	}
	public ResultTypeDefault(String label, ResultStructure resultStructure) {
		this(ResourceUtils.getValidResourceName(label), label, label, resultStructure,
				ValueType.NUMERIC, true);
	}
	public ResultTypeDefault(String label, String description, ResultStructure resultStructure) {
		this(ResourceUtils.getValidResourceName(label), label, description, resultStructure,
				ValueType.NUMERIC, true);
	}


	@Override
	public String id() {
		return id;
	}

	@Override
	public String label(OgemaLocale locale) {
		return label;
	}

	@Override
	public String description(OgemaLocale locale) {
		return description;
	}

	@Override
	public ResultStructure resultStructure() {
		return resultStructure;
	}

	@Override
	public ValueType valueType() {
		return valueType;
	}

	@Override
	public Boolean isSingleValueOrArray() {
		return singleValueOrArray;
	}
}
