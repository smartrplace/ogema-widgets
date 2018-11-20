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

import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author jlapp
 */
public class ResultTypeBuilder {

    protected ResultType.ResultStructure resultStructure;
    protected ResultType.ValueType valueType;
    protected Boolean isSingleValueOrArray;
    protected String id;
    protected String defaultDescription;
    protected String defaultLabel;
    protected Map<OgemaLocale, String> descriptions;
    protected Map<OgemaLocale, String> labels;

    private static class BasicResultType implements ResultType {

        protected final ResultType.ResultStructure resultStructure;
        protected final ResultType.ValueType valueType;
        protected final Boolean isSingleValueOrArray;
        protected final String id;
        protected final String defaultDescription;
        protected final String defaultLabel;
        protected Map<OgemaLocale, String> descriptions;
        protected Map<OgemaLocale, String> labels;

        public BasicResultType(ResultType.ResultStructure resultStructure, ResultType.ValueType valueType, boolean isSingleValueOrArray, String id,
                String defaultDescription, Map<OgemaLocale, String> descriptions, String defaultLabel, Map<OgemaLocale, String> labels) {
            Objects.requireNonNull(resultStructure);
            Objects.requireNonNull(valueType);
            Objects.requireNonNull(id);
            if (defaultDescription == null) {
                Objects.requireNonNull(descriptions);
            }
            if (defaultLabel == null) {
                Objects.requireNonNull(labels);
            }
            this.resultStructure = resultStructure;
            this.valueType = valueType;
            this.isSingleValueOrArray = isSingleValueOrArray;
            this.id = id;
            this.defaultDescription = defaultDescription;
            this.defaultLabel = defaultLabel;
            this.descriptions = descriptions;
            this.labels = labels;
        }

        @Override
        public ResultType.ResultStructure resultStructure() {
            return resultStructure;
        }

        @Override
        public ResultType.ValueType valueType() {
            return valueType;
        }

		@Override
		public Boolean isSingleValueOrArray() {
			return isSingleValueOrArray;
		}

        @Override
        public String id() {
            return id;
        }

        @Override
        public String label(OgemaLocale locale) {
            String l = null;
            if (labels != null) {
                l = labels.get(locale);
            }
            return l == null ? defaultLabel : l;
        }

        @Override
        public String description(OgemaLocale locale) {
            String d = null;
            if (descriptions != null) {
                d = descriptions.get(locale);
            }
            return d == null ? defaultDescription : d;
        }

    }

    public ResultTypeBuilder withStructure(ResultType.ResultStructure structure) {
        this.resultStructure = structure;
        return this;
    }

    public ResultTypeBuilder withType(ResultType.ValueType type) {
        this.valueType = type;
        return this;
    }

    public ResultTypeBuilder withSingleValueOrArray(boolean isSingleValueOrArray) {
        this.isSingleValueOrArray = isSingleValueOrArray;
        return this;
    }

    public ResultTypeBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public ResultTypeBuilder withDescription(String desc) {
        this.defaultDescription = desc;
        return this;
    }

    public ResultTypeBuilder withLabel(String label) {
        this.defaultLabel = label;
        return this;
    }

    public ResultTypeBuilder withDescriptions(Map<OgemaLocale, String> desc) {
        this.descriptions = desc;
        return this;
    }

    public ResultTypeBuilder withLabels(Map<OgemaLocale, String> labels) {
        this.labels = labels;
        return this;
    }

    public ResultType build() {
        return new BasicResultType(resultStructure, valueType, isSingleValueOrArray, id, defaultDescription, descriptions, defaultLabel, labels);
    }

    public static ResultTypeBuilder newBuilder() {
        return new ResultTypeBuilder();
    }

}
