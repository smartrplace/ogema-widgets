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
package de.iwes.widgets.pattern.widget.multiselect;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.ogema.core.resourcemanager.pattern.ResourcePattern;
import org.ogema.core.resourcemanager.pattern.ResourcePatternAccess;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.extended.pattern.DefaultPatternTemplate;
import de.iwes.widgets.api.extended.pattern.PatternMultiSelector;
import de.iwes.widgets.api.extended.pattern.PatternWidget;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.multiselect.MultiselectData;
import de.iwes.widgets.html.multiselect.TemplateMultiselect;

/**
 * Allows the user to select multiple {@link ResourcePattern}s from a list. The options can 
 * be specified either through the pattern type (see {@link #setType(Class, OgemaHttpRequest)},
 * or can be set explicitly (see {@link #update(Collection, OgemaHttpRequest)}). <br>
 * 
 * This is similar to a ResourceSelector, but allows to select more than one resource at a time.
 * @param <P>
 */
// TODO listener based updates
public class PatternMultiselect<P extends ResourcePattern<?>> extends TemplateMultiselect<P> implements PatternWidget<P>, PatternMultiSelector<P> {

	private static final long serialVersionUID = 1L;
	protected final ResourcePatternAccess rpa;
	private Class<? extends P> defaultType;
	protected final UpdateMode updateMode; 
	
    /*
     ******** Constructors **********
     */

	/**
	 * Constructor for default update mode AUTO_ON_GET.
	 * @param page
	 * @param id
	 * @param rpa
	 * @param baseType
	 */
	public PatternMultiselect(WidgetPage<?> page, String id,ResourcePatternAccess rpa, Class<P> baseType) {
		this(page, id, rpa, UpdateMode.AUTO_ON_GET, baseType);
	}

	/**
	 * @param page
	 * @param id
	 * @param rpa
	 * 		may be null if and only if updateMode is MANUAL
	 * @param updateMode
	 * @param baseType
	 * 		may be null if and only if updateMode is MANUAL
	 */
	public PatternMultiselect(WidgetPage<?> page, String id,ResourcePatternAccess rpa, UpdateMode updateMode, Class<P> baseType) {
		super(page, id);
		this.rpa=rpa;
		this.updateMode = updateMode;
		if (updateMode != UpdateMode.MANUAL) {
			Objects.requireNonNull(rpa);
			Objects.requireNonNull(baseType);
		}
		this.defaultType = baseType;
		setTemplate(new DefaultPatternTemplate<P>());
	}
	
	/**
	 * Constructor for session-specific widget with default update mode AUTO_ON_GET
	 * @param parent
	 * @param id
	 * @param rpa
	 * @param baseType
	 * @param req
	 */
    public PatternMultiselect(OgemaWidget parent, String id, ResourcePatternAccess rpa, Class<P> baseType, OgemaHttpRequest req) {
        this(parent, id, rpa, UpdateMode.AUTO_ON_GET, baseType, req);
    }
    
    /**
     * 
     * @param parent
     * @param id
     * @param rpa
     * 		may be null if and only if updateMode is MANUAL
     * @param updateMode
     * @param baseType
     * 	 	may be null if and only if updateMode is MANUAL
     * @param req
     */
    public PatternMultiselect(OgemaWidget parent, String id, ResourcePatternAccess rpa, UpdateMode updateMode, Class<P> baseType, OgemaHttpRequest req) {
        super(parent, id, req);
        this.rpa=rpa;
        this.updateMode = updateMode;
        this.defaultType = baseType;
        if (updateMode != UpdateMode.MANUAL) {
			Objects.requireNonNull(rpa);
			Objects.requireNonNull(baseType);
		}
        setTemplate(new DefaultPatternTemplate<P>());
    }

    /*
     ******** Inherited methods **********
     */
    
    @Override
    public PatternMultiselectData<P> createNewSession() {
    	return new PatternMultiselectData<P>(this,rpa);
    }
    
	@Override
    public PatternMultiselectData<P> getData(OgemaHttpRequest req) {
    	return (PatternMultiselectData<P>) super.getData(req);
    }
    
    @Override
    protected void setDefaultValues(MultiselectData opt) {
    	super.setDefaultValues(opt);
    	@SuppressWarnings("unchecked")
		PatternMultiselectData<P> opt2 = (PatternMultiselectData<P>) opt;
    	if (defaultType != null) 
    		opt2.setType(defaultType);
    }
    
    /*
     ******** Public methods **********
     */
    
    /**
     * Use only in manual update mode
     */
	@Override
	public void selectDefaultItems(Collection<P> items) {
		if (updateMode != UpdateMode.MANUAL)
			throw new UnsupportedOperationException("setDefaultItems only supported in manual update mode");
		super.selectDefaultItems(items);
	}
	
    /**
     * Use only in manual update mode
     */
	@Override
	public void update(Collection<P> items, OgemaHttpRequest req) {
		if (updateMode != UpdateMode.MANUAL)
			throw new UnsupportedOperationException("update only supported in manual update mode");
		super.update(items, req);
	}
	
    /**
     * Use only in manual update mode
     */
	@Override
	public boolean addItem(P item, OgemaHttpRequest req) {
		if (updateMode != UpdateMode.MANUAL)
			throw new UnsupportedOperationException("addItem only supported in manual update mode");
		return super.addItem(item, req);
	}
	
    /**
     * Use only in manual update mode
     */
	@Override
	public boolean removeItem(P item, OgemaHttpRequest req) {
		if (updateMode != UpdateMode.MANUAL)
			throw new UnsupportedOperationException("removeItem only supported in manual update mode");
		return super.removeItem(item, req);
	}
	
	
	public void setDefaultType(Class<? extends P> defaultType) {
		this.defaultType = defaultType;
	}
	
	public void setType(Class<? extends P> type, OgemaHttpRequest req) {
		getData(req).setType(type);
	}
	
	public Class<? extends P> getType(OgemaHttpRequest req) {
		return getData(req).getType();
	}

	@Override
	public List<P> getItems(OgemaHttpRequest req) {
		return getData(req).getItems();
	}

	@Override
	public UpdateMode getUpdateMode() {
		return updateMode;
	}
	


}
