package org.ogema.model.recplay.testing;

import org.ogema.core.model.ResourceList;
import org.ogema.model.prototypes.Data;

public interface RecReplayData extends Data {
	ResourceList<RecReplayObserverData> observerData();
}
