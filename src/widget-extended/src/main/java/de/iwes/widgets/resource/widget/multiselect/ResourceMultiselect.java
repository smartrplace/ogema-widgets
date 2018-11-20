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
package de.iwes.widgets.resource.widget.multiselect;

import java.util.Collection;
import java.util.Objects;
import org.ogema.core.model.Resource;
import org.ogema.core.resourcemanager.ResourceAccess;

import de.iwes.widgets.api.extended.mode.UpdateMode;
import de.iwes.widgets.api.extended.resource.DefaultResourceTemplate;
import de.iwes.widgets.api.extended.resource.ResourceMultiSelector;
import de.iwes.widgets.api.extended.resource.ResourceWidget;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.multiselect.MultiselectData;
import de.iwes.widgets.html.multiselect.TemplateMultiselect;

/**
 * Allows the user to select multiple {@link Resource}s from a list. The options can 
 * be specified either through the resource type (see {@link #setType(Class, OgemaHttpRequest)},
 * or can be set explicitly (see {@link #update(Collection, OgemaHttpRequest)}). <br>
 * 
 * This is similar to a ResourceSelector, but allows to select more than one resource at a time.
 * @param <R>
 */
// TODO listener based updates
public class ResourceMultiselect<R extends Resource> extends TemplateMultiselect<R> implements ResourceWidget<R>, ResourceMultiSelector<R> {

	private static final long serialVersionUID = 1L;
	private final ResourceAccess ra;
	private Class<? extends R> defaultType;
	protected UpdateMode updateMode; 
	
    /*
     ******** Constructors **********
     */

	/**
	 * Constructor for default update mode AUTO_ON_GET.
	 * @param page
	 * @param id
	 * @param ra
	 * @param baseType
	 */
	public ResourceMultiselect(WidgetPage<?> page, String id,ResourceAccess ra, Class<R> baseType) {
		this(page, id, false, ra, UpdateMode.AUTO_ON_GET, baseType);
	}
	/**
	 * Constructor for update mode MANUAL. With this constructor update mode AUTO_ON_GET is not
	 * supported.
	 * @param page
	 * @param id
	 */
	public ResourceMultiselect(WidgetPage<?> page, String id) {
		this(page, id, false, null, UpdateMode.MANUAL, null);
	}

	/**
	 * @param page
	 * @param id
	 * @param ra
	 * 		may be null if and only if updateMode is MANUAL
	 * @param updateMode
	 * @param baseType
	 * 		may be null if and only if updateMode is MANUAL
	 */
	public ResourceMultiselect(WidgetPage<?> page, String id, boolean globalWidget, ResourceAccess ra, UpdateMode updateMode, Class<R> baseType) {
		super(page, id, globalWidget);
		this.ra=ra;
		this.updateMode = updateMode;
		if (updateMode != UpdateMode.MANUAL) {
			Objects.requireNonNull(ra);
			Objects.requireNonNull(baseType);
		}
		this.defaultType = baseType;
		setTemplate(new DefaultResourceTemplate<R>());
	}
	
	/**
	 * Constructor for session-specific widget with default update mode AUTO_ON_GET
	 * @param parent
	 * @param id
	 * @param ra
	 * @param baseType
	 * @param req
	 */
    public ResourceMultiselect(OgemaWidget parent, String id, ResourceAccess ra, Class<R> baseType, OgemaHttpRequest req) {
        this(parent, id, ra, UpdateMode.AUTO_ON_GET, baseType, req);
    }
    
    /**
     * 
     * @param parent
     * @param id
     * @param ra
     * 		may be null if and only if updateMode is MANUAL
     * @param updateMode
     * @param baseType
     * 	 	may be null if and only if updateMode is MANUAL
     * @param req
     */
    public ResourceMultiselect(OgemaWidget parent, String id, ResourceAccess ra, UpdateMode updateMode, Class<R> baseType, OgemaHttpRequest req) {
        super(parent, id, req);
        this.ra=ra;
        this.updateMode = updateMode;
        this.defaultType = baseType;
        if (updateMode != UpdateMode.MANUAL) {
			Objects.requireNonNull(ra);
			Objects.requireNonNull(baseType);
		}
        setTemplate(new DefaultResourceTemplate<R>());
    }

    /*
     ******** Inherited methods **********
     */
    
    @Override
    public ResourceMultiselectData<R> createNewSession() {
    	return new ResourceMultiselectData<R>(this,ra);
    }
    
	@Override
    public ResourceMultiselectData<R> getData(OgemaHttpRequest req) {
    	return (ResourceMultiselectData<R>) super.getData(req);
    }
    
    @Override
    protected void setDefaultValues(MultiselectData opt) {
    	super.setDefaultValues(opt);
    	@SuppressWarnings("unchecked")
		ResourceMultiselectData<R> opt2 = (ResourceMultiselectData<R>) opt;
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
	public void selectDefaultItems(Collection<R> items) {
		if (updateMode != UpdateMode.MANUAL)
			throw new UnsupportedOperationException("setDefaultItems only supported in manual update mode");
		super.selectDefaultItems(items);
	}
	
    /**
     * Use only in manual update mode
     */
	@Override
	public void update(Collection<R> items, OgemaHttpRequest req) {
		if (updateMode != UpdateMode.MANUAL)
			throw new UnsupportedOperationException("update only supported in manual update mode");
		super.update(items, req);
	}
	
    /**
     * Use only in manual update mode
     */
	@Override
	public boolean addItem(R item, OgemaHttpRequest req) {
		if (updateMode != UpdateMode.MANUAL)
			throw new UnsupportedOperationException("addItem only supported in manual update mode");
		return super.addItem(item, req);
	}
	
    /**
     * Use only in manual update mode
     */
	@Override
	public boolean removeItem(R item, OgemaHttpRequest req) {
		if (updateMode != UpdateMode.MANUAL)
			throw new UnsupportedOperationException("removeItem only supported in manual update mode");
		return super.removeItem(item, req);
	}
	
	
	public void setDefaultType(Class<? extends R> defaultType) {
		this.defaultType = defaultType;
	}
    
	public void setType(Class<? extends R> type, OgemaHttpRequest req) {
		getData(req).setType(type);
	}
	
	public Class<? extends R> getType(OgemaHttpRequest req) {
		return getData(req).getType();
	}
	
	@Override
	public UpdateMode getUpdateMode() {
		return updateMode;
	}

	public void setUpdateMode(UpdateMode updateMode) {
		if(updateMode == UpdateMode.AUTO_ON_GET) {
			if((ra == null)||(defaultType == null)) throw new IllegalStateException("AUTO_ON_GET needs ResourceAccess/Class information!");
		}
		this.updateMode = updateMode;
	}
}
