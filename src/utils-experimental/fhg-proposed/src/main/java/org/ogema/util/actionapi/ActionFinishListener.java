package org.ogema.util.actionapi;

/**DN: TODO: Does this have any use? Documentation?
 */
@Deprecated
public interface ActionFinishListener {
	/**may throw exception, result in general is null if not specified otherwise by the providing application*/
	void actionFinished(Object result);
}
