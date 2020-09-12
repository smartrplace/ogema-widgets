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
package de.iwes.util.logconfig;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.model.gateway.EvalCollection;

public class EvalHelper {
	public static EvalCollection getEvalCollection(ApplicationManager appMan) {
		Resource flist = appMan.getResourceAccess().getResource("EvalCollection");
		if(flist == null) {
			flist = appMan.getResourceManagement().createResource("EvalCollection", EvalCollection.class);
			flist.activate(false);
		}
		if(flist instanceof EvalCollection) {
			EvalCollection slist = (EvalCollection)flist;
			return slist;
		}
		throw new IllegalStateException("Toplevel-Resource EvalCollection of wrong type!");
	}

}
