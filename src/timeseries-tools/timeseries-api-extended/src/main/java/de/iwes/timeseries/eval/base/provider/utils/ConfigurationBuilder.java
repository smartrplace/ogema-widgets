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
import de.iwes.timeseries.eval.api.configuration.Configuration;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 *
 * @author jlapp
 */
public class ConfigurationBuilder<C extends ConfigurationInstance> {
    
    public static interface ConfigurationFilter<C> {
        void filter(C instance);
    }
    
    public static interface ConfigurationInstanceFactory<C extends ConfigurationInstance> {
        C newInstance(Configuration<C> cfg);
    }
    
    protected String id;
    protected String defaultDescription;
    protected String defaultLabel;
    private final Class<C> type;
    private final Collection<ResultType> resultTypes = new ArrayList<>();
    private boolean optional;
    private ConfigurationFilter<C> filter;
    private ConfigurationInstanceFactory<C> ifac;
    
    private ConfigurationBuilder(Class<C> type) {
        this.type = type;
    }
    
    public static <I extends ConfigurationInstance> ConfigurationBuilder<I> newBuilder(Class<I> type) {
        Objects.requireNonNull(type);
        return new ConfigurationBuilder<>(type);
    }
    
    public ConfigurationBuilder<C> withId(String id) {
        this.id = id;
        return this;
    }

    public ConfigurationBuilder<C> withDescription(String desc) {
        this.defaultDescription = desc;
        return this;
    }

    public ConfigurationBuilder<C> withLabel(String label) {
        this.defaultLabel = label;
        return this;
    }
    
    public ConfigurationBuilder<C> withFilter(ConfigurationFilter<C> filter) {
        this.filter = filter;
        return this;
    }
    
    public ConfigurationBuilder<C> withResultTypes(ResultType ... types) {
        this.resultTypes.addAll(Arrays.asList(types));
        return this;
    }
    
    public ConfigurationBuilder<C> withResultType(ResultType type) {
        Objects.requireNonNull(type);
        this.resultTypes.add(type);
        return this;
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ConfigurationBuilder<C> withDefaultFloat(final float f) {
        ifac = new ConfigurationInstanceFactory() {
            @Override
            public ConfigurationInstance newInstance(Configuration cfg) {
                return new ConfigurationInstance.GenericFloatConfiguration(f, cfg);
            }
        };
        return this;
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ConfigurationBuilder<C> withDefaultInt(final int value) {
        ifac = new ConfigurationInstanceFactory() {
            @Override
            public ConfigurationInstance newInstance(Configuration cfg) {
                return new ConfigurationInstance.GenericIntConfiguration(value, cfg);
            }
        };
        return this;
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ConfigurationBuilder<C> withDefaultString(final String value) {
        ifac = new ConfigurationInstanceFactory() {
            @Override
            public ConfigurationInstance newInstance(Configuration cfg) {
                return new ConfigurationInstance.GenericStringConfiguration(value, cfg);
            }
        };
        return this;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public ConfigurationBuilder<C> withDefaultObject(final Object value) {
        ifac = new ConfigurationInstanceFactory() {
            @Override
            public ConfigurationInstance newInstance(Configuration cfg) {
                return new ConfigurationInstance.GenericObjectConfiguration(value, cfg);
            }
        };
        return this;
    }
    
    public ConfigurationBuilder<C> withDefaultValues(ConfigurationInstanceFactory<C> ifac) {
        this.ifac = ifac;
        return this;
    }
    
    public ConfigurationBuilder<C> isOptional(boolean optional) {
        this.optional = optional;
        return this;
    }
    
    public Configuration<C> build() {
        Objects.requireNonNull(id);
        Objects.requireNonNull(defaultDescription);
        Objects.requireNonNull(defaultLabel);
        Objects.requireNonNull(type);
        if (resultTypes.isEmpty()) {
            throw new IllegalArgumentException("applicable result types are empty");
        }
        return new SimpleConfiguration<>(id, defaultDescription, defaultLabel, type, resultTypes, optional, filter, ifac);
    }
    
    private static class SimpleConfiguration<C extends ConfigurationInstance> implements Configuration<C> {
        
        protected final String id;
        protected final String defaultDescription;
        protected final String defaultLabel;
        private final Class<C> type;
        private final Collection<ResultType> resultTypes;
        private final boolean optional;
        private final ConfigurationFilter<C> filter;
        private final ConfigurationInstanceFactory<C> ifac;

        public SimpleConfiguration(String id, String defaultDescription, String defaultLabel,
                Class<C> type, Collection<ResultType> resultTypes, boolean optional,
                ConfigurationFilter<C> filter, ConfigurationInstanceFactory<C> ifac) {
            this.id = id;
            this.defaultDescription = defaultDescription;
            this.defaultLabel = defaultLabel;
            this.type = type;
            this.resultTypes = resultTypes;
            this.optional = optional;
            this.filter = filter;
            this.ifac = ifac;
        }

        @Override
        public String id() {
            return id;
        }

        @Override
        public String description(OgemaLocale locale) {
            return defaultDescription;
        }

        @Override
        public String label(OgemaLocale locale) {
            return defaultLabel;
        }

        @Override
        public boolean isOptional() {
            return optional;
        }
        
        @Override
        public void filter(C instance) throws IllegalArgumentException {
            if (filter != null) {
                filter.filter(instance);
            }
        }

        @Override
        public C defaultValues() {
            return ifac != null ? ifac.newInstance(this) : null;
        }        

        @Override
        public Collection<ResultType> getApplicableResultTypes() {
            return resultTypes;
        }

        @Override
        public Class<C> configurationType() {
            return type;
        }
        
        @Override
        public int hashCode() {
            return id.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            Objects.requireNonNull(obj);
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof SimpleConfiguration)) {
                return false;
            }
            SimpleConfiguration<?> other = (SimpleConfiguration)obj;
            return other.id.equals(this.id);
        }
        
    }
    
    
}
