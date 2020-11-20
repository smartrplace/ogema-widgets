package org.smartrplace.router.model;

import org.ogema.core.model.ResourceList;
import org.ogema.model.prototypes.Data;

public interface GliNetRouters extends Data {
	ResourceList<GlitNetRouter> routers();
}
