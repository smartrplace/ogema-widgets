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
package de.iwes.widgets.resource.widget.arrays;

import java.util.Objects;

import org.ogema.core.model.array.ArrayResource;
import org.ogema.core.model.array.BooleanArrayResource;
import org.ogema.core.model.array.ByteArrayResource;
import org.ogema.core.model.array.FloatArrayResource;
import org.ogema.core.model.array.IntegerArrayResource;
import org.ogema.core.model.array.StringArrayResource;
import org.ogema.core.model.array.TimeArrayResource;
import org.ogema.core.model.simple.OpaqueResource;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.arrays.ArrayManipulatorConfiguration;
import de.iwes.widgets.html.form.checkbox.Checkbox2;
import de.iwes.widgets.html.form.textfield.ValueInputField;
import de.iwes.widgets.html.html5.FlexboxData;

public class ResourceArrayManipulatorData<A extends ArrayResource> extends FlexboxData {

	private final ArrayManipulatorConfiguration config;
	private A resource;
	
	protected ResourceArrayManipulatorData(ResourceArrayManipulator<A> flexbox) {
		super(flexbox);
		this.config = flexbox.getConfig();
	}

	protected void setResource(A resource, OgemaHttpRequest req) {
		if (Objects.equals(this.resource, resource))
			return;
		this.resource = resource;
		clear();
		if (resource == null)
			return;
		final int length = getLength(resource);
		for (int i=0; i< length; i++) {
			final OgemaWidget in = createWidget(resource, i, widget, req);
			if (in instanceof ValueInputField)
				((ValueInputField<?>) in).setDefaultNrDecimals(config.getNrDecimals());
			in.setMargin("0.5em", false, false, true, false, req);
			// TODO if delete is allowed, add a delete button
			in.triggerAction(in, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST, req);
			addItem(in);
		}
	}
	
	protected A getResource() {
		return resource;
	}

	@SuppressWarnings("deprecation")
	private static int getLength(final ArrayResource a) {
		if (a instanceof FloatArrayResource) {
			final float[] values = ((FloatArrayResource) a).getValues();
			return values == null ? 0 : values.length;
		}
		if (a instanceof IntegerArrayResource) {
			final int[] values = ((IntegerArrayResource) a).getValues();
			return values == null ? 0 : values.length;
		}
		if (a instanceof TimeArrayResource) {
			final long[] values = ((TimeArrayResource) a).getValues();
			return values == null ? 0 : values.length;
		}
		if (a instanceof ByteArrayResource) {
			final byte[] values = ((ByteArrayResource) a).getValues();
			return values == null ? 0 : values.length;
		}
		if (a instanceof BooleanArrayResource) {
			final boolean[] values = ((BooleanArrayResource) a).getValues();
			return values == null ? 0 : values.length;
		}
		if (a instanceof OpaqueResource) {
			final byte[] values = ((OpaqueResource) a).getValue();
			return values == null ? 0 : values.length;
		}
		if (a instanceof StringArrayResource) {
			final String[] values = ((StringArrayResource) a).getValues();
			return values == null ? 0 : values.length;
		}
		throw new IllegalArgumentException("Unknown array type " + a);
	}
	
	@SuppressWarnings("serial")
	private static OgemaWidget createWidget(final ArrayResource a, final int i,
			final OgemaWidget parent, final OgemaHttpRequest req) {
		if (a instanceof FloatArrayResource) {
			final ValueInputField<Float> in= new ValueInputField<Float>(parent, parent.getId() + "_" + i, Float.class, req) {
				
				@Override
				public void onGET(OgemaHttpRequest req) {
					final float[] values = ((FloatArrayResource) a).getValues();
					if (values == null || i > values.length - 1) {
						setNumericalValue(null, req);
					} else {
						setNumericalValue(values[i], req);
					}
				}
				
				@Override
				public void onPOSTComplete(String data, OgemaHttpRequest req) {
					final Float newVal = getNumericalValue(req);
					if (newVal == null)
						return;
					final float[] values = ((FloatArrayResource) a).getValues();
					if (values != null && i <= values.length - 1) {
						values[i] = newVal;
						((FloatArrayResource) a).setValues(values);
					}
				}
				
			};
			return in;
		}
		if (a instanceof IntegerArrayResource) {
			final ValueInputField<Integer> in= new ValueInputField<Integer>(parent, parent.getId() + "_" + i, Integer.class, req) {
				
				@Override
				public void onGET(OgemaHttpRequest req) {
					final int[] values = ((IntegerArrayResource) a).getValues();
					if (values == null || i > values.length - 1) {
						setNumericalValue(null, req);
					} else {
						setNumericalValue(values[i], req);
					}
				}
				
				@Override
				public void onPOSTComplete(String data, OgemaHttpRequest req) {
					final Integer newVal = getNumericalValue(req);
					if (newVal == null)
						return;
					final int[] values = ((IntegerArrayResource) a).getValues();
					if (values != null && i <= values.length - 1) {
						values[i] = newVal;
						((IntegerArrayResource) a).setValues(values);
					}
				}
				
			};
			return in;
		}
		if (a instanceof BooleanArrayResource) {
			final Checkbox2 in= new Checkbox2(parent, parent.getId() + "_" + i, req) {
				
				@Override
				public void onGET(OgemaHttpRequest req) {
					final boolean[] values = ((BooleanArrayResource) a).getValues();
					if (values == null || i > values.length - 1) {
						setState("", false, req);
					} else {
						setState("", values[i], req);
					}
				}
				
				@Override
				public void onPOSTComplete(String data, OgemaHttpRequest req) {
					final boolean newVal = isChecked("", req);
					final boolean[] values = ((BooleanArrayResource) a).getValues();
					if (values != null && i <= values.length - 1) {
						values[i] = newVal;
						((BooleanArrayResource) a).setValues(values);
					}
				}
				
			};
			return in;
		}
		if (a instanceof TimeArrayResource) {
			final ValueInputField<Long> in= new ValueInputField<Long>(parent, parent.getId() + "_" + i, Long.class, req) {
				
				@Override
				public void onGET(OgemaHttpRequest req) {
					final long[] values = ((TimeArrayResource) a).getValues();
					if (values == null || i > values.length - 1) {
						setNumericalValue(null, req);
					} else {
						setNumericalValue(values[i], req);
					}
				}
				
				@Override
				public void onPOSTComplete(String data, OgemaHttpRequest req) {
					final Long newVal = getNumericalValue(req);
					if (newVal == null)
						return;
					final long[] values = ((TimeArrayResource) a).getValues();
					if (values != null && i <= values.length - 1) {
						values[i] = newVal;
						((TimeArrayResource) a).setValues(values);
					}
				}
				
			};
			return in;
		} 
		throw new IllegalArgumentException("Unsupported type " + a);
	}
	
}
