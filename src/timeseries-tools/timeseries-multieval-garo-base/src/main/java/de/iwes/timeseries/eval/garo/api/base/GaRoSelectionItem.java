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
package de.iwes.timeseries.eval.garo.api.base;

import java.io.UncheckedIOException;
import java.util.List;

import de.iwes.timeseries.eval.api.extended.util.HierarchySelectionItemGeneric;
import de.iwes.timeseries.eval.garo.api.helper.base.GaRoEvalHelper;

/** Selection item for {@link GaRoEvalDataProviderGateway}
 * @param id if this is a terminal option the id must contain the String specified for the
 * respective {@link GaRoDataType} in {@link GaRoEvalHelper#getDataType(String)}. Otherwise
 * GaRoMultiEvalDataProvider.GW_LINKINGOPTION_ID or
 * GaRoMultiEvalDataProvider.ROOM_LINKINGOPTION_ID.
 */
public abstract class GaRoSelectionItem extends HierarchySelectionItemGeneric {
	public GaRoSelectionItem(int level, String id) {
		super(level, id);
	}

	//public R resource;
	//private final ApplicationManager appMan;

	//only relevant for level GW_LEVEL
	//private String gwId;
	
	//only relevant for level GW_ROOM
	protected List<String> devicePath;
	
	//only relevant for level ROOM_LEVEL, TS_LEVEL
	protected GaRoSelectionItem gwSelectionItem;
	
	//only relevant for level TS_LEVEL
	protected GaRoSelectionItem roomSelectionItem;	
	//private String tsId;
	
	public List<String> getDevicePaths() {
		if(level == 0)
			throw new IllegalStateException("Device paths are not available on GW_LEVEL");
		if(level == 1) {
			if(devicePath == null) try {
				this.devicePath = getDevicePaths(this);
			} catch (UncheckedIOException e) {
				throw new IllegalStateException(e);
			}
			return devicePath;
		} else {
			if(roomSelectionItem.devicePath == null) try {
				roomSelectionItem.devicePath = getDevicePaths(roomSelectionItem);
			} catch (UncheckedIOException e) {
				throw new IllegalStateException(e);
			}
			return roomSelectionItem.devicePath;
		}
	}
	
	//TODO: Not used yet
	public GaRoDataType getTypeForTerminalOption() {
		return GaRoEvalHelper.getDataType(id());
	}
	
	protected abstract List<String> getDevicePaths(GaRoSelectionItem roomSelItem);

	//TODO: This shall replace getResource and make typing superflucious
	public abstract Integer getRoomType();
	public abstract String getRoomName();
	public abstract String getPath();

}
