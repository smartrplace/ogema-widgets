package org.ogema.humread.valueconversion;

import org.ogema.core.model.units.PercentageResource;
import org.ogema.core.model.units.TemperatureResource;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public class HumanReadableValueConverter {
	public static class LinearTransformation {
		public float offset;
		public float scale;		
	}
	
	public static LinearTransformation getTransformationIfNonTrivial(SchedulePresentationData sched) {
		return getTransformationIfNonTrivial(sched.getScheduleType(), sched.getLabel(OgemaLocale.ENGLISH));
	}
	
	public static LinearTransformation getTransformationIfNonTrivial(Class<?> type, String label) {
		LinearTransformation result = new LinearTransformation();		
		if (type == TemperatureResource.class) {
			result.offset = -273.15F;
			result.scale = 1.0F;
		} else if (type == Float.class && (label != null) 
				&& label.toLowerCase().contains("temperature") &&
				(!label.toLowerCase().contains("setpreact"))) {
			result.offset = -273.15F;
			result.scale = 1.0F;
		} else if (type == PercentageResource.class) {
			result.offset = 0;
			result.scale = 100;
		} else {
			return null;
		}
		return result;
	}

	public static LinearTransformation getTransformation(SchedulePresentationData sched) {
		return getTransformation(sched.getScheduleType(), sched.getLabel(OgemaLocale.ENGLISH));
	}
	
	public static LinearTransformation getTransformation(Class<?> type, String label) {
		LinearTransformation result = getTransformationIfNonTrivial(type, label);
		if(result == null) {
			result = new LinearTransformation();
			result.offset = 0;
			result.scale = 1;
		}
		return result;
	}
	
	public static float getHumanValue(float inVal, SchedulePresentationData sched) {
		return getHumanValue(inVal, sched.getScheduleType(), sched.getLabel(OgemaLocale.ENGLISH));
	}
	public static float getHumanValue(float inVal, Class<?> type) {
		return getHumanValue(inVal, type, null);
	}
	public static float getHumanValue(float inVal, Class<?> type, String label) {
		LinearTransformation trans = getTransformationIfNonTrivial(type, label);
		return getHumanValue(inVal, trans);
		/*if (type == TemperatureResource.class)
			return inVal - 273.15F;
		else if(type == PercentageResource.class)
			return 100f*inVal;
		return inVal;*/
	}
	
	public static float getHumanValue(float inVal, LinearTransformation trans) {
		if(trans == null)
			return inVal;
		return trans.scale*inVal+trans.offset;
	}
}
