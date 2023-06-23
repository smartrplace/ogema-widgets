package de.iwes.widgets.resource.widget.calendar;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import org.json.JSONObject;
import org.ogema.core.model.simple.TimeResource;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.textfield.TextField;
import de.iwes.widgets.html.form.textfield.TextFieldType;
import de.iwes.widgets.resource.widget.textfield.ResourceTextField;
import de.iwes.widgets.resource.widget.textfield.ResourceTextFieldData;

/**
 * A variant of DatepickerTimeResource that does not rely on a library but uses the browser native
 * Input field type "datetime-local". See https://developer.mozilla.org/en-US/docs/Web/HTML/Element/input/datetime-local.
 * It can save > 250kB of Javascript resources.
 */
@SuppressWarnings("serial")
public class DatepickerTimeResourceTextField extends ResourceTextField<TimeResource> {

	final static DateTimeFormatter DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
	final static DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	TextFieldType defaultType;
	
	public DatepickerTimeResourceTextField(WidgetPage<?> page, String id) {
		this(page, id, null);
	}
	
	public DatepickerTimeResourceTextField(WidgetPage<?> page, String id, TimeResource defaultResource) {
		super(page, id, defaultResource);
		setDefaultType(TextFieldType.DATETIME_LOCAL);
	}
	
	public DatepickerTimeResourceTextField(OgemaWidget parent, String id, OgemaHttpRequest req) {
		this(parent, id, req, null);
	}
	
	public DatepickerTimeResourceTextField(OgemaWidget parent, String id, OgemaHttpRequest req, TimeResource defaultResource) {
		super(parent, id, req);
		selectDefaultItem(defaultResource);
		setDefaultType(TextFieldType.DATETIME_LOCAL);
	}
	
	public void setDefaultType(TextFieldType type) {
		if (type != TextFieldType.DATETIME_LOCAL && type != TextFieldType.DATE)
			throw new IllegalArgumentException("Only datetime-local or date types supported");
		super.setDefaultType(type);
		this.defaultType = type;
	}
	
    public void setType(TextFieldType type,OgemaHttpRequest req) {
    	throw new IllegalArgumentException("Only default type supported");
    }
	
    DateTimeFormatter getFormatter() {
    	return defaultType == TextFieldType.DATETIME_LOCAL ? DATETIME : DATE;
    }
    
    @Override
    protected String format(TimeResource resource, Locale locale) {
    	if (resource == null || !resource.isActive())
    		return "";
    	final ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(resource.getValue()), ZoneId.systemDefault());
    	return getFormatter().format(zdt);
    }
    
    @Override
    protected String getEmptyLabel(Locale locale) {
    	return "";
    }
    
	@Override
	public DatepickerTimeResourceTextFieldData createNewSession() {
		return new DatepickerTimeResourceTextFieldData(this);
	}
    
    public static class DatepickerTimeResourceTextFieldData extends ResourceTextFieldData<TimeResource> {

    	public DatepickerTimeResourceTextFieldData(DatepickerTimeResourceTextField dtr) {
			super(dtr);
		}
    	
    	@Override
    	public JSONObject retrieveGETData(OgemaHttpRequest req) {
    		super.setValue(this.getValue());
    		return super.retrieveGETData(req);
        }
    	
    	@Override
    	public String getValue() {
    		final TimeResource t = getSelectedResource();
    		if (t == null || !t.isActive())
    			return null;
    		final LocalDateTime ldt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(t.getValue()), ZoneId.systemDefault()).toLocalDateTime();
    		final DateTimeFormatter formatter = ((DatepickerTimeResourceTextField) widget).getFormatter();
    		return ldt.format(formatter);
    	}
    	
    	@Override
    	public JSONObject onPOST(String data, OgemaHttpRequest req) {
    		JSONObject request = new JSONObject(data);
    		String value = request.getString("data");
    		final TimeResource t = getSelectedResource();
    		if (t == null) {
    			setValue(null);
    			return new JSONObject();
    		}
    		boolean set = false;
    		if (value != null) {
    			value = value.trim();
    			final DateTimeFormatter formatter = ((DatepickerTimeResourceTextField) widget).getFormatter();
    			try {
    				final ZonedDateTime zdt;
	    			if (TextFieldType.DATE.name().equals(getType())) {
	    				zdt = LocalDate.parse(value, formatter).atStartOfDay(ZoneId.systemDefault());
	    			} else {
	    				zdt = LocalDateTime.parse(value, formatter).atZone(ZoneId.systemDefault());
	    			}
	    			final long timestamp = zdt.toInstant().toEpochMilli();
	    			set = true;
	    			t.<TimeResource> create().setValue(timestamp);
    			} catch (DateTimeParseException ok) {}  
    			
    		}
    		if (!set) {
    			if (t.exists())
    				t.deactivate(false);
    			super.setValue(null);
    		}
    		else {
    			t.activate(false);
    			super.setValue(this.getValue());
    		}
    		return request; 
    	}
    	
    }
    
    

}
