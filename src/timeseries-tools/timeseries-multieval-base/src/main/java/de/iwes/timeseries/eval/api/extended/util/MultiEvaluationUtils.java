package de.iwes.timeseries.eval.api.extended.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance.DateConfiguration;
import de.iwes.timeseries.eval.api.configuration.StartEndConfiguration;
import de.iwes.timeseries.eval.api.extended.MultiResult;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationUtils;

public abstract class MultiEvaluationUtils {
	/** Export result of MultiEvaluationInstance to json
	 * 
	 * @param file
	 * @param multiResult all public fields are serialized into a JSON (recursive)
	 */
	public static void exportToJSONFile(FileOutputStream file, MultiResult multiResult) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(file, multiResult);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	public static void exportToJSONFile(String fileName, MultiResult multiResult) {
		File file = new File(fileName);
		file.getParentFile().mkdirs();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.setSerializationInclusion(Include.NON_NULL);
		try {
			mapper.writeValue(file, multiResult);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	/**Import data from CSV into a given MultiResult class. Data in the file that does not fit
	 * into the result object is omitted. Elements that are not found in the file shall be null
	 * or default if null is not allowed.
	 * @param file
	 * @param structure
	 * @return object with result
	 */
	public static <M extends MultiResult> M importFromJSON(FileInputStream file, Class<M> structure) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(file, structure);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public static <M extends MultiResult> M importFromJSON(String fileName, Class<M> structure) {
		File file = new File(fileName);

		JacksonNonBlockingObjectMapperFactory factory = new JacksonNonBlockingObjectMapperFactory();
		factory.setJsonDeserializers(Arrays.asList(new StdDeserializer[]{
		    // StdDeserializer, here, comes from Jackson (org.codehaus.jackson.map.deser.StdDeserializer)
		    new NumberDeserializers.ShortDeserializer(Short.class, null),
		    new NumberDeserializers.IntegerDeserializer(Integer.class, null),
		    new NumberDeserializers.CharacterDeserializer(Character.class, null),
		    new NumberDeserializers.LongDeserializer(Long.class, null),
		    new NumberDeserializers.FloatDeserializer(Float.class, null),
		    new NumberDeserializers.DoubleDeserializer(Double.class, null),
		    new NumberDeserializers.NumberDeserializer(),
		    new NumberDeserializers.BigDecimalDeserializer(),
		    new NumberDeserializers.BigIntegerDeserializer()
		    //new StdDeserializer.CalendarDeserializer()
		}));
		ObjectMapper mapper = factory.createObjectMapper();
		//ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
		mapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);

		mapper.setSerializationInclusion(Include.NON_NULL);
		try {
			return mapper.readValue(file, structure);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	/** Export to CSV structure flattening everything into a matrix structure. For single values and arrays
	 * up to 255 values the path elements of the respective JSON representation are given in the initial
	 * six columns of a row, afterwards the values. If more than six path elements are required, the
	 * sixth column shall contain the entire remaining path with slashes as separators.
	 * Time series are all given in a column last with common time stamps.
	 * Note: Import from such CSV is not foreseen currently.
	 * @param file
	 * @param multiResult
	 * @return number of top-level elements serialized
	 */
	public static int exportToCSVFile(FileOutputStream file, MultiResult multiResult) {
		//TODO
		throw new UnsupportedOperationException();
	}
	
	public static
	<T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
	  List<T> list = new ArrayList<T>(c);
	  java.util.Collections.sort(list);
	  return list;
	}

    /**
     * Add the mandatory start/end time information to the list of
     * configurations or create list with this information if not yet existing
     *
     * @param tsList list of input time series from which start/end shall be
     * determined
     * @param configurations may be null if no other configurations shall be set
     * for the evaluation
     * @return list of configurations with start and end time
     */
    public static Collection<ConfigurationInstance> addStartEndTime(final List<ReadOnlyTimeSeries> tsList, Collection<ConfigurationInstance> configurations) {
        if (configurations == null) {
            configurations = new ArrayList<>();
        }
        long startTime = EvaluationUtils.getDefaultStartEndTimeForInput(tsList, true);
        ConfigurationInstance config = new DateConfiguration(startTime, StartEndConfiguration.START_CONFIGURATION);
        configurations.add(config);
        long endTime = EvaluationUtils.getDefaultStartEndTimeForInput(tsList, false);
        config = new DateConfiguration(endTime, StartEndConfiguration.END_CONFIGURATION);
        configurations.add(config);
        return configurations;
    }
    
    public static class JacksonNonBlockingObjectMapperFactory {

        /**
         * Deserializer that won't block if value parsing doesn't match with target type
         * @param <T> Handled type
         */
        private static class NonBlockingDeserializer<T> extends JsonDeserializer<T> {
            private StdDeserializer<T> delegate;

            public NonBlockingDeserializer(StdDeserializer<T> _delegate){
                this.delegate = _delegate;
            }

            @Override
            public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                try {
                    return delegate.deserialize(jp, ctxt);
                }catch (JsonMappingException e){
                    // If a JSON Mapping occurs, simply returning null instead of blocking things
                    return null;
                }
            }
        }

        @SuppressWarnings("rawtypes")
		private List<StdDeserializer> jsonDeserializers = new ArrayList<StdDeserializer>();

        @SuppressWarnings({ "unchecked", "rawtypes"})
		public ObjectMapper createObjectMapper(){
            ObjectMapper objectMapper = new ObjectMapper();

            SimpleModule customJacksonModule = new SimpleModule("customJacksonModule", new Version(0, 0, 0, null, null, null));
            //SimpleModule customJacksonModule = new SimpleModule("customJacksonModule", new Version(1, 0, 0, null));
            for(StdDeserializer jsonDeserializer : jsonDeserializers){
                // Wrapping given deserializers with NonBlockingDeserializer
                customJacksonModule.addDeserializer(jsonDeserializer.handledType(), new NonBlockingDeserializer(jsonDeserializer));
            }

            objectMapper.registerModule(customJacksonModule);
            return objectMapper;
        }

        @SuppressWarnings("rawtypes")
		public JacksonNonBlockingObjectMapperFactory setJsonDeserializers(List<StdDeserializer> _jsonDeserializers){
            this.jsonDeserializers = _jsonDeserializers;
            return this;
        }
    }
 }
