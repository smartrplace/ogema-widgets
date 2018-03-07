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
 * Copyright 2014 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
 */
package com.example.app.template.patternlistener;

import java.util.ArrayList;
import java.util.List;

import org.ogema.core.resourcemanager.pattern.PatternListener;

import com.example.app.template.TemplateController;
import com.example.app.template.pattern.TemplatePattern;

public class TemplateSensorListener implements PatternListener<TemplatePattern> {
	
	private final TemplateController app;
	public final List<TemplatePattern> availablePatterns = new ArrayList<>();
	
 	public TemplateSensorListener(TemplateController templateProcess) {
		this.app = templateProcess;
	}
	
	@Override
	public void patternAvailable(TemplatePattern pattern) {
		availablePatterns.add(pattern);
		
		//TODO: work on pattern
		app.processInterdependies();
	}
	@Override
	public void patternUnavailable(TemplatePattern pattern) {
		// TODO process remove
		
		availablePatterns.remove(pattern);
		app.processInterdependies();
	}
	
	
}
