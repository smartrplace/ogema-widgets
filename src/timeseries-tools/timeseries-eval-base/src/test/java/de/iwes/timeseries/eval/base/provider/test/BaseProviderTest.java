package de.iwes.timeseries.eval.base.provider.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.tools.timeseries.api.FloatTimeSeries;
import org.ogema.tools.timeseries.implementations.FloatTreeTimeSeries;
import org.ogema.tools.widgets.test.base.WidgetsTestBase;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationInstance;
import de.iwes.timeseries.eval.api.EvaluationManager;
import de.iwes.timeseries.eval.api.EvaluationResult;
import de.iwes.timeseries.eval.api.OnlineEvaluation;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.ResultType.ResultStructure;
import de.iwes.timeseries.eval.api.SingleEvaluationResult;
import de.iwes.timeseries.eval.api.SingleEvaluationResult.SingleValueResult;
import de.iwes.timeseries.eval.api.Status;
import de.iwes.timeseries.eval.api.Status.EvaluationStatus;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.base.provider.BasicEvaluationProvider;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationInputImpl;
import de.iwes.timeseries.eval.base.provider.utils.TimeSeriesDataImpl;
import de.iwes.widgets.resource.timeseries.OnlineIterator;
import de.iwes.widgets.resource.timeseries.OnlineTimeSeries;
import de.iwes.widgets.resource.timeseries.OnlineTimeSeriesCache;

@ExamReactorStrategy(PerClass.class)
public class BaseProviderTest extends WidgetsTestBase {

	@Inject
	private OnlineTimeSeriesCache timeSeriesCache;

	@Inject
	private EvaluationManager evalManager;

	// writes all values before iterator retrieves them
	@Test(timeout=15000)
	public void onlineIteratorWorks() throws InterruptedException {
		final FloatResource test = getApplicationManager().getResourceManagement().createResource(newResourceName(), FloatResource.class);
		test.setValue(1);
		test.activate(false);
		final OnlineIterator it = timeSeriesCache.getResourceValuesAsTimeSeries(test).onlineIterator(true);
		test.setValue(2);
		Thread.sleep(200); // ensure callback is issued with old value
		test.setValue(3);
		Thread.sleep(2000); // may need some time for callbacks to be executed
		for (int i=0;i<3;i++) {
			Assert.assertTrue(it.hasNext());
			SampledValue sv = it.next();
			Assert.assertEquals(i+1, sv.getValue().getFloatValue(),0.1F);
		}
		it.stop();
		test.delete();
	}

	// writes values in parallel to online iterator checks
	@Test(timeout=15000)
	public void onlineIteratorWorks2() throws InterruptedException {
		final FloatResource test = getApplicationManager().getResourceManagement().createResource(newResourceName(), FloatResource.class);
		test.setValue(1);
		test.activate(false);
		final OnlineIterator it = timeSeriesCache.getResourceValuesAsTimeSeries(test).onlineIterator(true);
		Assert.assertTrue(it.hasNext());
		it.next();
		final int N = 5;
		new Thread(new Runnable() {

			@Override
			public void run() {
				for (int i=0;i<N;i++) {
					test.setValue(i*i);
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {}
				}
			}
		}).start();
		for (int i=0;i<N;i++) {
			Assert.assertTrue(it.hasNext()); // blocks
			SampledValue sv = it.next();
			Assert.assertEquals(i*i, sv.getValue().getFloatValue(),0.5F);
			Assert.assertEquals(Quality.GOOD, sv.getQuality());
		}
		it.stop();
		Assert.assertFalse("Online iterator has been stopped and should not have any further elements",it.hasNext());
		test.delete();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void baseProviderWorksOffline() throws InterruptedException, ExecutionException, TimeoutException {
		final BasicEvaluationProvider baseProvider = new BasicEvaluationProvider();
		final FloatTimeSeries ts1 = new FloatTreeTimeSeries();
		ts1.addValue(0, new FloatValue(10F));
		ts1.addValue(10, new FloatValue(20F));
		ts1.addValue(30, new FloatValue(30F));
		ts1.setInterpolationMode(InterpolationMode.LINEAR);
		TimeSeriesData dataImpl = new TimeSeriesDataImpl(ts1, "test", "test", null);

		final List<EvaluationInput> input = new ArrayList<>();
		EvaluationInput in1 = new EvaluationInputImpl(Collections.singletonList(dataImpl));
		input.add(in1);
//		EvaluationInstance eval  = baseProvider.newEvaluation(input, baseProvider.resultTypes(), Collections.<ConfigurationInstance> emptyList());
		EvaluationInstance eval  = evalManager.newEvaluation(baseProvider, input, baseProvider.resultTypes(), Collections.<ConfigurationInstance> emptyList());
		Assert.assertFalse("Offline evaluation expected",eval.isOnlineEvaluation());
		final Status status = eval.get(10, TimeUnit.SECONDS);
		Assert.assertNotNull(status);
		Assert.assertEquals(EvaluationStatus.FINISHED, status.getStatus());
		final Map<ResultType, EvaluationResult> results = eval.getResults();
		Assert.assertNotNull(results);
		Assert.assertFalse(results.isEmpty());
		boolean maxContained = false;
		boolean minContained = false;
		float maxValue = 0;
		float minValue = 0;
		for (EvaluationResult result : results.values()) {
			for (SingleEvaluationResult singleResult : result.getResults()) {
				Assert.assertEquals(ResultStructure.PER_INPUT, singleResult.getResultType().resultStructure());
				if (singleResult.getResultType().equals(BasicEvaluationProvider.MAX_TYPE)) {
					maxContained = true;
					maxValue = ((SingleValueResult<Float>) singleResult).getValue();
				}
				else if (singleResult.getResultType().equals(BasicEvaluationProvider.MIN_TYPE)) {
					minContained = true;
					minValue = ((SingleValueResult<Float>) singleResult).getValue();
				}
			}
		}
		Assert.assertTrue("Maximum value evaluation missing",maxContained);
		Assert.assertTrue("Minimum value evaluation missing",minContained);
		Assert.assertEquals("Unexpected maximum value in timeseries",30, maxValue, 0.1F);
		Assert.assertEquals("Unexpected minimum value in timeseries",10, minValue, 0.1F);
	}

	@SuppressWarnings("unchecked")
//	@Ignore("Fails for unknown reason on CI server")
	@Test
	public void baseProviderWorksOnline() throws InterruptedException, ExecutionException, TimeoutException {
		final BasicEvaluationProvider baseProvider = new BasicEvaluationProvider();
		final FloatResource resource = getApplicationManager().getResourceManagement().createResource(newResourceName(), FloatResource.class);
		resource.setValue(10);
		resource.activate(false);
		OnlineTimeSeries timeSeries = timeSeriesCache.getResourceValuesAsTimeSeries(resource);
		TimeSeriesData dataImpl = new TimeSeriesDataImpl(timeSeries, "test", "test", null);

		final List<EvaluationInput> input = new ArrayList<>();
		EvaluationInput in1 = new EvaluationInputImpl(Collections.singletonList(dataImpl));
		input.add(in1);
//		EvaluationInstance eval  = baseProvider.newEvaluation(input,baseProvider.resultTypes(), Collections.<ConfigurationInstance> emptyList());
		EvaluationInstance eval  = evalManager.newEvaluation(baseProvider, input, baseProvider.resultTypes(), Collections.<ConfigurationInstance> emptyList());
		Assert.assertTrue("Online evaluation expected",eval.isOnlineEvaluation());
		Assert.assertTrue("Online evaulation expected", eval instanceof OnlineEvaluation);
		Thread.sleep(500);
		resource.setValue(27);
		Thread.sleep(500);
		resource.setValue(30);
		Thread.sleep(1000); //need to wait long enough here, so the value is not overwritten before being evaluated
		resource.setValue(20);
		resource.setValue(12);
		resource.setValue(11);
		Thread.sleep(100);
		OnlineEvaluation onlineEval = (OnlineEvaluation) eval; // XXX
		final Status status = onlineEval.finish();
		Assert.assertNotNull(status);
		Assert.assertEquals(EvaluationStatus.FINISHED, status.getStatus());
		final Map<ResultType, EvaluationResult> results = eval.getResults();
		Assert.assertNotNull(results);
		Assert.assertFalse(results.isEmpty());
		boolean maxContained = false;
		boolean minContained = false;
		float maxValue = 0;
		float minValue = 0;
		for (EvaluationResult result : results.values()) {
			for (SingleEvaluationResult singleResult : result.getResults()) {
				Assert.assertEquals(ResultStructure.PER_INPUT, singleResult.getResultType().resultStructure());
				if (singleResult.getResultType().equals(BasicEvaluationProvider.MAX_TYPE)) {
					maxContained = true;
					maxValue = ((SingleValueResult<Float>) singleResult).getValue();
				}
				else if (singleResult.getResultType().equals(BasicEvaluationProvider.MIN_TYPE)) {
					minContained = true;
					minValue = ((SingleValueResult<Float>) singleResult).getValue();
				}
			}
		}
		Assert.assertTrue("Maximum value evaluation missing",maxContained);
		Assert.assertTrue("Minimum value evaluation missing",minContained);
		Assert.assertEquals("Unexpected maximum value in timeseries",30, maxValue, 0.1F);
		Assert.assertEquals("Unexpected minimum value in timeseries",10, minValue, 0.1F);
		resource.delete();
	}

}