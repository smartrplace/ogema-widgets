package org.ogema.model.gateway;

import org.ogema.core.model.ResourceList;
import org.ogema.core.model.array.StringArrayResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;
import org.ogema.model.extended.alarming.AlarmGroupData;
import org.ogema.model.prototypes.Data;

public interface CustomerMessageData extends Data {
	StringResource destination();
	StringResource title();
	StringResource messageBeforeActionTable();
	StringArrayResource deviceActions();
	StringResource messageAfterActionTable();
	
	/** If not existing or zero the message is still a draft and not SENT */
	IntegerResource sendCounter();
	/** Comment on success of sending etc. */
	StringResource sendComment();
	
	ResourceList<AlarmGroupData> source();
}
