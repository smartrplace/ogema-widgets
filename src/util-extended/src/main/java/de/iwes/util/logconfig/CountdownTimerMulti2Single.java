package de.iwes.util.logconfig;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.tools.resourcemanipulator.timer.CountDownDelayedExecutionTimer;

/** This timer works like a {@link CountDownDelayedExecutionTimer}, but each time newEvent()
 * is called the timer is reset and the timer is only started when new event is called. In
 * this way the timer can be used for retarded events from multiple sources where the action
 * shall be carried out with a delay to the latest action. Note that the action would not
 * take place if newEvent() is called always before the executionDelay.*/
public abstract class CountdownTimerMulti2Single {

	public abstract void delayedExecution();
	protected long getVariableTimerDuration() {
		return countdownTime;
	}
	
	protected final ApplicationManager appMan;
	protected final long countdownTime;
	protected final BooleanResource persistentFlagOpen;
	
	protected CountDownDelayedExecutionTimer timer;
	
	public CountdownTimerMulti2Single(ApplicationManager appMan, long countdownTime) {
		this(appMan, countdownTime, null);
	}
	/** 
	 * 
	 * @param appMan
	 * @param countdownTime if {@link #getVariableTimerDuration()} is overwritten this value is irrelevant
	 * @param persistentFlagOpen if not null the Resource must be created and activated by the application.
	 * If created true an inital retarded update will be triggered
	 */
	public CountdownTimerMulti2Single(ApplicationManager appMan, long countdownTime,
			BooleanResource persistentFlagOpen) {
		this.appMan = appMan;
		this.countdownTime = countdownTime;
		this.persistentFlagOpen = persistentFlagOpen;
		if(persistentFlagOpen != null && persistentFlagOpen.getValue()) {
			newEvent();
		}
	}

	public void newEvent() {
		if(timer != null) {
			timer.destroy();
		}
		if(persistentFlagOpen != null)
			persistentFlagOpen.setValue(true);
		long duration = getVariableTimerDuration();
		timer = new CountDownDelayedExecutionTimer(appMan, duration) {
			
			@Override
			public void delayedExecution() {
				stop();
				if(persistentFlagOpen != null)
					persistentFlagOpen.setValue(false);
				CountdownTimerMulti2Single.this.delayedExecution();
			}
		};
	}
	
	public void stop() {
		if(timer != null) {
			timer.destroy();
			timer = null;
		}
	}
	
	public void executeNow() {
		stop();
		delayedExecution();
	}
	
	public boolean isCounting() {
		return timer != null;
	}
	
	public long getNextRunTime() {
		if(!isCounting())
			return -1;
		return timer.getNextRunTime();
	}
	
	public long getRemainingTime() {
		if(!isCounting())
			return -1;
		return timer.getRemainingTime();
	}
}