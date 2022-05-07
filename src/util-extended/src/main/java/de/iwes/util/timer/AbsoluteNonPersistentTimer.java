package de.iwes.util.timer;

import org.ogema.core.application.ApplicationManager;
import org.ogema.core.application.Timer;
import org.ogema.core.application.TimerListener;
import org.ogema.tools.resourcemanipulator.timer.CountDownAbsoluteTimer;

import de.iwes.util.logconfig.LogHelper;

public class AbsoluteNonPersistentTimer implements TimerListener {
	CountDownAbsoluteTimer absoluteTimer;
	private final ApplicationManager appMan;
	private final int intervalType;
	private final long stepOffsetTime;
	private final TimerListener listener;
	
	public AbsoluteNonPersistentTimer(int intervalType, long stepOffsetTime,
			TimerListener listener, ApplicationManager appMan) {
		this.appMan = appMan;
		this.intervalType = intervalType;
		this.stepOffsetTime = stepOffsetTime;
		this.listener = listener;
		
		startNewTimer(true);
	}
	
	protected void startNewTimer(boolean init) {
		long baseInstant;
		if(init)
			baseInstant = appMan.getFrameworkTime()-5*LogHelper.MINUTE_MILLIS;
		else
			baseInstant = appMan.getFrameworkTime()+1;
		long nextTime = AbsoluteTimeHelper.getNextStepTime(baseInstant, intervalType, stepOffsetTime);
		absoluteTimer = new CountDownAbsoluteTimer(appMan, nextTime, true, this);
	}

	@Override
	public void timerElapsed(Timer arg0) {
		listener.timerElapsed(arg0);
		startNewTimer(false);
	}
	
	public void destroy() {
		if(absoluteTimer != null) {
			absoluteTimer.destroy();
			absoluteTimer = null;
		}
	}
}
