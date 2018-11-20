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
package de.iwes.widgets.html.multiselect;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.template.DisplayTemplate;

@SuppressWarnings({"rawtypes", "unchecked"})
public class EnumMultiselect<E extends Enum> extends TemplateMultiselect<E> {

	private static final long serialVersionUID = 1L;
	private final Class<E> type;
	
	public EnumMultiselect(WidgetPage<?> page, String id, boolean globalWidget, Class<E> type) {
		super(page, id, globalWidget);
		this.type = Objects.requireNonNull(type);
		selectDefaultItems(Arrays.asList(getAllElements()));
		setComparator(new EnumComparator(type));
		setTemplate((DisplayTemplate) ENUM_TEMPlATE);
	}

	public EnumMultiselect(OgemaWidget parent, String id, OgemaHttpRequest req, Class<E> type) {
		super(parent, id, req);
		this.type = Objects.requireNonNull(type);
		selectDefaultItems(Arrays.asList(getAllElements()));
		setComparator(new EnumComparator(type));
		setTemplate((DisplayTemplate) ENUM_TEMPlATE);
	}
	
	protected E[] getAllElements() {
		return type.getEnumConstants();
	}
	
	protected static class EnumComparator implements Comparator<Enum> {
		
		private final Class<? extends Enum> type;
		
		public EnumComparator(Class<? extends Enum> type) {
			this.type = type;
		}

		@Override
		public int compare(Enum o1, Enum o2) {
			if (o1 == o2)
				return 0;
			for (Enum e: type.getEnumConstants()) {
				if (e == o1)
					return -1;
				else if (e == o2)
					return 1;
			}
			return 0;
		}

	}
	
	protected static final DisplayTemplate<Enum> ENUM_TEMPlATE = new DisplayTemplate<Enum>() {

		@Override
		public String getId(Enum object) {
			return object.name();
		}

		@Override
		public String getLabel(Enum object, OgemaLocale locale) {
			return object.toString();
		}
		
	};
	
}
