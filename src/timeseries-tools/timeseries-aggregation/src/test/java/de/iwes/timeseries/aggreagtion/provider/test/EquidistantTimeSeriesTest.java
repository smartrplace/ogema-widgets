package de.iwes.timeseries.aggreagtion.provider.test;

import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;
import org.ogema.core.channelmanager.measurements.FloatValue;
import org.ogema.core.channelmanager.measurements.Quality;
import org.ogema.core.channelmanager.measurements.SampledValue;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.tools.timeseries.api.FloatTimeSeries;
import org.ogema.tools.timeseries.implementations.FloatTreeTimeSeries;
import org.ogema.tools.widgets.test.base.WidgetsTestBase;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import de.iwes.timeseries.aggregation.api.StandardIntervalTimeseriesBuilder;

@ExamReactorStrategy(PerClass.class)
public class EquidistantTimeSeriesTest extends WidgetsTestBase {
		
	@Override
	public Option widgets() {
		return CoreOptions.composite(super.widgets(),
				CoreOptions.mavenBundle("de.iwes.tools", "timeseries-eval-base", widgetsVersion));
	}
	
	private static void verifyPoint(Iterator<SampledValue> it, float value) {
		Assert.assertTrue("Point missing in iterator",it.hasNext());
		final SampledValue sv = it.next();
		Assert.assertEquals("Unexpected bad quality in iterator", Quality.GOOD, sv.getQuality());
		Assert.assertEquals("Unexpected value in iterator", value, sv.getValue().getFloatValue(), 0.1F);
	}
	
	@Test
	public void equidistantTimeseriesWorks1() {
		final FloatTimeSeries ts = new FloatTreeTimeSeries();
		ts.setInterpolationMode(InterpolationMode.LINEAR);
		SampledValue sv0 = new SampledValue(FloatValue.ZERO, 0, Quality.GOOD);
		SampledValue sv1 = new SampledValue(new FloatValue(10), 10, Quality.GOOD);
		ts.addValue(sv0);
		ts.addValue(sv1);
		final ReadOnlyTimeSeries ts2 
			= StandardIntervalTimeseriesBuilder.newBuilder(ts).setInterval(5, ChronoUnit.MILLIS).setIgnoreGaps(true).build();
		Assert.assertEquals("Incorrect time series size", 2, ts2.size());
		for (SampledValue sv: ts2.getValues(Long.MIN_VALUE))
			System.out.println("   ttt sv: " + sv.getTimestamp() + ": " + sv.getValue().getFloatValue() + " : " + sv.getQuality());
		Assert.assertEquals("Incorrect time series size", 2, ts2.getValues(Long.MIN_VALUE).size());
		final Iterator<SampledValue> it = ts2.iterator();
		verifyPoint(it, 2.5F);
		verifyPoint(it, 7.5F);
		Assert.assertFalse("Too many points in iterator",it.hasNext());
	}
	
	@Test
	public void equidistantTimeseriesWorksWithUnalignedEndpoint() {
		final FloatTimeSeries ts = new FloatTreeTimeSeries();
		ts.setInterpolationMode(InterpolationMode.LINEAR);
		SampledValue sv0 = new SampledValue(FloatValue.ZERO, 0, Quality.GOOD);
		SampledValue sv1 = new SampledValue(new FloatValue(7), 7, Quality.GOOD);
		ts.addValue(sv0);
		ts.addValue(sv1);
		final ReadOnlyTimeSeries ts2 
			= StandardIntervalTimeseriesBuilder.newBuilder(ts).setInterval(5, ChronoUnit.MILLIS).setIgnoreGaps(true).build();
		Assert.assertEquals("Incorrect time series size", 2, ts2.size());
		for (SampledValue sv: ts2.getValues(Long.MIN_VALUE))
			System.out.println("   ttt sv: " + sv.getTimestamp() + ": " + sv.getValue().getFloatValue() + " : " + sv.getQuality());
		Assert.assertEquals("Incorrect time series size", 2, ts2.getValues(Long.MIN_VALUE).size());
		final Iterator<SampledValue> it = ts2.iterator();
		verifyPoint(it, 2.5F);
		verifyPoint(it, 6F);
		Assert.assertFalse("Too many points in iterator",it.hasNext());
	}
	
	@Test
	public void equidistantTimeseriesWorks2() {
		final FloatTimeSeries ts = new FloatTreeTimeSeries();
		ts.setInterpolationMode(InterpolationMode.LINEAR);
		SampledValue sv0 = new SampledValue(FloatValue.ZERO, 0, Quality.GOOD);
		SampledValue sv1 = new SampledValue(new FloatValue(3), 3, Quality.GOOD);
		SampledValue sv2 = new SampledValue(new FloatValue(10), 10, Quality.GOOD);
		ts.addValue(sv0);
		ts.addValue(sv1);
		ts.addValue(sv2);
		final ReadOnlyTimeSeries ts2 
			= StandardIntervalTimeseriesBuilder.newBuilder(ts).setInterval(5, ChronoUnit.MILLIS).setIgnoreGaps(true).build();
		Assert.assertEquals("Incorrect time series size", 2, ts2.size());
		for (SampledValue sv: ts2.getValues(Long.MIN_VALUE))
			System.out.println("   ttt sv: " + sv.getTimestamp() + ": " + sv.getValue().getFloatValue() + " : " + sv.getQuality());
		Assert.assertEquals("Incorrect time series size", 2, ts2.getValues(Long.MIN_VALUE).size());
		final Iterator<SampledValue> it = ts2.iterator();
		verifyPoint(it, 2.5F);
		verifyPoint(it, 7.5F);
		Assert.assertFalse("Too many points in iterator",it.hasNext());
	}
	
	@Test
	public void equidistantTimeseriesWorks3() {
		final FloatTimeSeries ts = new FloatTreeTimeSeries();
		ts.setInterpolationMode(InterpolationMode.LINEAR);
		SampledValue sv0 = new SampledValue(FloatValue.ZERO, 0, Quality.GOOD);
		SampledValue sv1 = new SampledValue(new FloatValue(3), 3, Quality.GOOD);
		SampledValue sv2 = new SampledValue(new FloatValue(4), 4, Quality.GOOD);
		SampledValue sv3 = new SampledValue(new FloatValue(7), 7, Quality.GOOD);
		SampledValue sv4 = new SampledValue(new FloatValue(10), 10, Quality.GOOD);
		ts.addValue(sv0);
		ts.addValue(sv1);
		ts.addValue(sv2);
		ts.addValue(sv3);
		ts.addValue(sv4);
		final ReadOnlyTimeSeries ts2 
			= StandardIntervalTimeseriesBuilder.newBuilder(ts).setInterval(5, ChronoUnit.MILLIS).setIgnoreGaps(true).build();
		Assert.assertEquals("Incorrect time series size", 2, ts2.size());
		for (SampledValue sv: ts2.getValues(Long.MIN_VALUE))
			System.out.println("   ttt sv: " + sv.getTimestamp() + ": " + sv.getValue().getFloatValue() + " : " + sv.getQuality());
		Assert.assertEquals("Incorrect time series size", 2, ts2.getValues(Long.MIN_VALUE).size());
		final Iterator<SampledValue> it = ts2.iterator();
		verifyPoint(it, 2.5F);
		verifyPoint(it, 7.5F);
		Assert.assertFalse("Too many points in iterator",it.hasNext());
	}
	
	@Test
	public void equidistantTimeseriesWorks4() {
		final FloatTimeSeries ts = new FloatTreeTimeSeries();
		ts.setInterpolationMode(InterpolationMode.LINEAR);
		ts.addValue(new SampledValue(FloatValue.ZERO, 0, Quality.GOOD));
		ts.addValue(new SampledValue(new FloatValue(3), 3, Quality.GOOD));
		ts.addValue(new SampledValue(new FloatValue(4), 4, Quality.GOOD));
		ts.addValue(new SampledValue(new FloatValue(7), 7, Quality.GOOD));
		ts.addValue(new SampledValue(new FloatValue(9), 9, Quality.GOOD));
		ts.addValue(new SampledValue(new FloatValue(10), 10, Quality.GOOD));
		ts.addValue(new SampledValue(new FloatValue(17), 17, Quality.GOOD));
		ts.addValue(new SampledValue(new FloatValue(28), 28, Quality.GOOD));
		final ReadOnlyTimeSeries ts2 
			= StandardIntervalTimeseriesBuilder.newBuilder(ts).setInterval(5, ChronoUnit.MILLIS).setIgnoreGaps(true).build();
		Assert.assertEquals("Incorrect time series size", 6, ts2.size());
		for (SampledValue sv: ts2.getValues(Long.MIN_VALUE))
			System.out.println("   ttt sv: " + sv.getTimestamp() + ": " + sv.getValue().getFloatValue() + " : " + sv.getQuality());
		Assert.assertEquals("Incorrect time series size", 6, ts2.getValues(Long.MIN_VALUE).size());
		final Iterator<SampledValue> it = ts2.iterator();
		verifyPoint(it, 2.5F);
		verifyPoint(it, 7.5F);
		verifyPoint(it, 12.5F);
		verifyPoint(it, 17.5F);
		verifyPoint(it, 22.5F);
		Assert.assertTrue(it.hasNext());
		it.next();
		Assert.assertFalse("Too many points in iterator",it.hasNext());
	}
	
	
}