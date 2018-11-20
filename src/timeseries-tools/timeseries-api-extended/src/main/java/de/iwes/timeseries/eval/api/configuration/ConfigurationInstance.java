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
package de.iwes.timeseries.eval.api.configuration;

import java.util.Objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import de.iwes.timeseries.eval.base.provider.utils.DummyConfigurationInstance;

@JsonDeserialize(as = DummyConfigurationInstance.class)
public interface ConfigurationInstance {
	
	Configuration<?> getConfigurationType();

	public static class GenericFloatConfiguration implements ConfigurationInstance {
		
		private final Configuration<? extends GenericFloatConfiguration> config;
		private final float value;
		
		public GenericFloatConfiguration(float value, Configuration<? extends GenericFloatConfiguration> config) {
			Objects.requireNonNull(config);
			this.value = value;
			this.config = config;
		}
		
		public final float getValue() {
			return value;
		}
		
		@Override
		public final Configuration<?> getConfigurationType() {
			return config;
		}
		
	}
	
	public static class GenericStringConfiguration implements ConfigurationInstance {
	
		private final Configuration<? extends GenericStringConfiguration> config;
		private final String value;
		
		public GenericStringConfiguration(String value, Configuration<? extends GenericStringConfiguration> config) {
			Objects.requireNonNull(config);
			this.value = value;
			this.config = config;
		}
		
		public final String getValue() {
			return value;
		}
		
		@Override
		public final Configuration<?> getConfigurationType() {
			return config;
		}
		
	}
	
	public static class GenericIntConfiguration implements ConfigurationInstance {
		
		private final Configuration<? extends GenericIntConfiguration> config;
		private final int value;
		
		public GenericIntConfiguration(int value, Configuration<? extends GenericIntConfiguration> config) {
			Objects.requireNonNull(config);
			this.value = value;
			this.config = config;
		}
		
		public final int getValue() {
			return value;
		}
		
		@Override
		public final Configuration<?> getConfigurationType() {
			return config;
		}
		
	}
	
	public static class DateConfiguration implements ConfigurationInstance {
		
		private final Configuration<? extends DateConfiguration> config;
		private final long value;
		
		public DateConfiguration(long value, Configuration<? extends DateConfiguration> config) {
			Objects.requireNonNull(config);
			this.value = value;
			this.config = config;
		}
		
		public final long getValue() {
			return value;
		}
		
		@Override
		public final Configuration<?> getConfigurationType() {
			return config;
		}
		
	}
	
	public class GenericDurationConfiguration implements ConfigurationInstance {

		private final Configuration<? extends GenericDurationConfiguration> config;
		private final long duration;
		private final String unit;
		
		/**
		 * @param duration
		 * @param unit
		 * 		must be one of the java.time.ChronoUnit unit names (taking a String
		 * 		for Java 7 compatibility)
		 */
		public GenericDurationConfiguration(long duration, String unit, Configuration<? extends GenericDurationConfiguration> config) {
			Objects.requireNonNull(config);
			Objects.requireNonNull(unit);
			this.duration = duration;
			this.unit = unit;
			this.config = config;
		}
		
		public final long getDuration() {
			return duration;
		}
		
		public final String getUnit() {
			return unit;
		}
		
		@Override
		public final Configuration<?> getConfigurationType() {
			return config;
		}
		
	}
	
	public static class GenericObjectConfiguration<T> implements ConfigurationInstance {
		
		private final Configuration<? extends GenericObjectConfiguration<T>> config;
		private final T value;
		
		public GenericObjectConfiguration(T value, Configuration<? extends GenericObjectConfiguration<T>> config) {
			Objects.requireNonNull(config);
			this.value = value;
			this.config = config;
		}
		
		public final T getValue() {
			return value;
		}
		
		@Override
		public final Configuration<?> getConfigurationType() {
			return config;
		}
		
	}

	
}
