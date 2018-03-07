/**
 * This file is part of the OGEMA widgets framework.
 *
 * OGEMA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OGEMA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OGEMA. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2014 - 2018
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
 */

package de.iwes.widgets.html.dragdropassign;

import de.iwes.widgets.api.extended.OgemaWidgetBase;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;

/** Widget that allows to assign items to containers (see {@link DragDropAssignData} for details).
 *  Changes of the item assignments by the user are provided via the callback {@link onUpdate}.
 *  Changes from other sources can be inserted into the data structure 
 * @author pzuehlcke
 *
 */
public abstract class DragDropAssign extends OgemaWidgetBase<DragDropAssignData> {

    private static final long serialVersionUID = 1L;
    private DragDropData defaultData;

    /**
     * ********* Constructors 
     * 
     */
     
    public DragDropAssign(WidgetPage<?> page, String id, DragDropData data) {
        this(page, id, data, false);
    }

     /*********
     * @param page
     * @param id
     * @param data
     * @param globalWidget
     */
    public DragDropAssign(WidgetPage<?> page, String id, DragDropData data, boolean globalWidget) {
        super(page, id, globalWidget);
        setDefaultData(data);
    }

    /**
     * ***** Inherited methods
     *
     */
    @Override
    public Class<? extends OgemaWidgetBase<?>> getWidgetClass() {
        return DragDropAssign.class;
    }

    @Override
    public DragDropAssignData createNewSession() {
        return new DragDropAssignData(this);
    }

    @Override
    protected void setDefaultValues(DragDropAssignData opt) {
        opt.setAssignData(defaultData);
        super.setDefaultValues(opt);
    }

    /**
     * ***** Public methods *******
     */
    /** Callback from widget when an item is re-assigned by the user
     * 
     * @param item that has been moved
     * @param from previous container to which item was assigned
     * @param to new container to which item is assigned
     * @param req: http-request corresponding to action. Is null if widget is in global mode
     */
    public abstract void onUpdate(Item item, Container from, Container to, OgemaHttpRequest req);

    /** Get options data structure. If you write into this data object this is directly
     * used by the widget and visible to the user with the next update of the widget from
     * Javascript side
     */
    public DragDropData getAssignData(OgemaHttpRequest req) {
        return getData(req).getAssignData();
    }

    public void setAssignData(DragDropData data, OgemaHttpRequest req) {
        getData(req).setAssignData(data);
    }
    
    public final void setDefaultData(DragDropData data) {
        this.defaultData = data;
    }
}
