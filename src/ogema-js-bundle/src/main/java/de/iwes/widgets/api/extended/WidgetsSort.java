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

package de.iwes.widgets.api.extended;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.LoggerFactory;

import de.iwes.widgets.api.extended.xxx.ConfiguredWidget;
import de.iwes.widgets.api.widgets.OgemaWidget;

class WidgetsSort {
	
	/**
	 * It is not assumed that the comparator is transitive.
	 * @param list
	 * @param comparator
	 */
	public static final List<ConfiguredWidget<?>> sort(List<ConfiguredWidget<?>> list, Comparator<ConfiguredWidget<?>> comparator) {
		int sz = list.size();
		if (sz == 0)
			return Collections.emptyList();
		List<ConfiguredWidget<?>> newList = new ArrayList<>();
		newList.add(list.get(0));
//		List<ConfiguredWidget> copy = new ArrayList<>(initialWidgets);
		for (int i=1; i<sz; i++) {
			ConfiguredWidget<?> cw = list.get(i);
			if (cw.widget.globalConnectElements.isEmpty() && cw.widget.globalConnectGroups.isEmpty()) {
				newList.add(cw);
				continue;
			}
			addEntry(newList, cw, comparator);
		}
		return newList;
	}
	
	public static void addEntry(List<ConfiguredWidget<?>> list, ConfiguredWidget<?> entry, Comparator<ConfiguredWidget<?>> comp) {
		int low = -1; // highest index with compare(entry, X) > 0
		int high = -1; // lowest index with compare(entry, X) < 0
		for (int k=0; k<list.size(); k++) {
			int result = comp.compare(entry, list.get(k));
			if (result == 0)
				continue;
			//	System.out.println("    dependency detected " + entry.getWidget().getId() + " -> " + list.get(k).getWidget().getId() + ": " + comp.compare(entry,list.get(k)));
			if (result > 0) {  // entry must be sorted behind the existing list entry
				low = k;
			}
			else if (high == -1) { // entry must be sorted before the existing list entry
				high = k;
			}
		}
		if (low >= 0 && high >= 0 && low > high) {
			// now we need to perform some reshuffling of the existing list entries
			int moved= moveRight(low-high, list, high, comp);
			if (moved > 0) { // ordering successful
				addEntry(list, entry, comp);
				return;
			}
			else {
				OgemaWidget w = entry.getWidget();
				LoggerFactory.getLogger(w.getPage().getClass())
					.warn("Inconsistent widget dependency chain detected. This may lead to malfunctioning of the page. Widget: " + w + " on page " + w.getPage().getFullUrl());
				list.add(high,entry);
			}

		}
		else {
			if (high >= 0) {
				list.add(high, entry);
			}
			else { // low > 0, high = -1
				list.add(entry);
			}
		}
		
	}
	
	
	/**
	 * Tries to move a widget nrSteps steps to the right in the list of widgets, while maintaining compatibility
	 * with the comparator. This may involve moving other widgets sitting between the currently active one and its
	 * target position.
	 * 
	 * @param nrSteps
	 * @param list
	 * @param idx
	 * @param comp
	 * @return
	 * 		nr of elements moved beyond target
	 */
	private static int moveRight(int nrSteps, List<ConfiguredWidget<?>> list, int idx, Comparator<ConfiguredWidget<?>> comp) {
		if (nrSteps <= 0)
			throw new IllegalArgumentException("Inconsistent arguments... this is a bug in the widget core. Got nrSteps argument " + nrSteps + ", must be greater 0.");
		ConfiguredWidget<?> widget = list.remove(idx);
		// System.out.println("    xxx  moving right... " + widget.getWidget().getId() + ", " + nrSteps + " steps; current position " + idx + ", total: " + list.size());
		int target = idx;
		for (int i=0; i<nrSteps; i++ ) {
			ConfiguredWidget<?> cw = list.get(idx + i);
			int result = comp.compare(widget, cw);
			if (result != 0) {
				list.add(target, widget);
				if (i < nrSteps -1) {
					int nrMoved = moveRight(nrSteps - i - 1, list, target + 1, comp);
					if (nrMoved > 0) {
						int newMoved = moveRight(nrSteps - nrMoved - i, list, target, comp);
						if (newMoved == 0)
							return 0;
						else 
							return newMoved + nrMoved;
					}
					else
						return 0;
				}
				else 
					return 0;
			}
			target++;
		}
		list.add(target, widget);
		return 1;
	}
	
	
	
	

}
