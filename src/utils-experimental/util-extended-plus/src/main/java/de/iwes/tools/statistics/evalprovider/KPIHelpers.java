/**
 * Copyright 2009 - 2016
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES
 *
 * All Rights reserved
 */
package de.iwes.tools.statistics.evalprovider;

import org.ogema.core.model.simple.FloatResource;

public class KPIHelpers {
	public static float getPvSelfConsumptionQuote(FloatResource pvImport, FloatResource gridExport, FloatResource gridNetImport) {
		float ePV = -pvImport.getValue();
		float eGE = -gridExport.getValue();
		return 1-eGE/ePV;
	}
	
	public static float getAutarkyQuote(FloatResource pvImport, FloatResource gridExport, FloatResource gridNetImport) {
		float ePV = -pvImport.getValue();
		float eGE = -gridExport.getValue();
		float eGI = gridNetImport.getValue() + eGE;
		return 1-eGI/(ePV-eGE+eGI);
	}

}
