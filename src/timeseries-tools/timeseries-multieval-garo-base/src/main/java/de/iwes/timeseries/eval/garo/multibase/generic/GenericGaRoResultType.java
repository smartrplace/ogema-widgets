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
package de.iwes.timeseries.eval.garo.multibase.generic;

import java.util.Collections;
import java.util.List;

import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.generictype.GenericAttribute;

import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.base.provider.utils.ResultTypeDefault;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataTypeI;

public abstract class GenericGaRoResultType extends ResultTypeDefault implements GaRoDataTypeI { //GenericDataTypeDeclaration {
	
	private final Class<? extends Resource> representingResourceType;
	private final TypeCardinality typeCardinality;
	private final Level level;
	private final String providerId;

	public GenericGaRoResultType(String label, String eval) {
		this(label, FloatResource.class, eval);
	}
	public GenericGaRoResultType(String label, Level level, String eval) {
		this(label, FloatResource.class, level, eval);
	}
	public GenericGaRoResultType(String label, Class<? extends Resource> representingResourceType,
			String eval) {
		this(label, label, representingResourceType, eval);
	}
	public GenericGaRoResultType(String label, Class<? extends Resource> representingResourceType,
			Level level, String eval) {
		this(label, label, representingResourceType, level, eval);
	}
	public GenericGaRoResultType(String label, String description, String eval) {
		this(label, description, FloatResource.class, eval);
	}
	public GenericGaRoResultType(String label, String description, Level level,
			String eval) {
		this(label, description, FloatResource.class, level, eval);
	}
	public GenericGaRoResultType(String label, Class<? extends Resource> representingResourceType,
			TypeCardinality typeCardinality, String eval) {
		this(label, label, representingResourceType, typeCardinality, eval);
	}
	public GenericGaRoResultType(String label, Class<? extends Resource> representingResourceType,
			TypeCardinality typeCardinality, Level level, String eval) {
		this(label, label, representingResourceType, typeCardinality, level, eval);
	}
	public GenericGaRoResultType(String label, String description,
			Class<? extends Resource> representingResourceType,
			String eval) {
		this(label, description, ResultStructure.COMBINED, representingResourceType, eval);
	}
	public GenericGaRoResultType(String label, String description, Class<? extends Resource> representingResourceType,
			Level level, String eval) {
		this(label, description, ResultStructure.COMBINED, representingResourceType, level, eval);
	}
	public GenericGaRoResultType(String label, String description, Class<? extends Resource> representingResourceType,
			TypeCardinality typeCardinality, String eval) {
		this(label, description, ResultStructure.COMBINED, representingResourceType, typeCardinality, eval);
	}
	public GenericGaRoResultType(String label, String description, Class<? extends Resource> representingResourceType,
			TypeCardinality typeCardinality, Level level,
			String eval) {
		this(label, description, ResultStructure.COMBINED, representingResourceType, typeCardinality, level, eval);
	}
	public GenericGaRoResultType(String label, ResultStructure resultStructure,
			Class<? extends Resource> representingResourceType,
			String eval) {
		this(label, label, resultStructure, representingResourceType,
				eval);
	}
	public GenericGaRoResultType(String label, String description, ResultStructure resultStructure,
			Class<? extends Resource> representingResourceType, String eval) {
		this(label, description, resultStructure, representingResourceType, TypeCardinality.SINGLE_VALUE, eval);
	}
	public GenericGaRoResultType(String label, String description, ResultStructure resultStructure,
			Class<? extends Resource> representingResourceType, Level level,
			String eval) {
		this(label, description, resultStructure, representingResourceType, TypeCardinality.SINGLE_VALUE, level, eval);
	}
	public GenericGaRoResultType(String label, String description, ResultStructure resultStructure,
			Class<? extends Resource> representingResourceType,
			TypeCardinality typeCardinality,
			String eval) {
		this(label, description, resultStructure, representingResourceType, typeCardinality, Level.ROOM, eval);
	}
	public GenericGaRoResultType(String label, String description, ResultStructure resultStructure,
				Class<? extends Resource> representingResourceType,
				TypeCardinality typeCardinality, Level level,
				String eval) {
		super(label, description, resultStructure);
		this.representingResourceType = representingResourceType;
		this.typeCardinality = typeCardinality;
		this.level = level;
		this.providerId = eval;
	}

	public abstract SingleEvaluationResult getEvalResult(GenericGaRoEvaluationCore evalContainer, ResultType rt, List<TimeSeriesData> inputData);

	@Override
	public List<GenericAttribute> attributes() {
		return Collections.emptyList();
	}
	
	@Override
	public Class<? extends Resource> representingResourceType() {
		return representingResourceType;
	}
	
	@Override
	public TypeCardinality typeCardinality() {
		return typeCardinality;
	}
	
	@Override
	public Level getLevel() {
		return this.level;
	}
	
	@Override
	public String primaryEvalProvider() {
		return providerId;
	}
}
