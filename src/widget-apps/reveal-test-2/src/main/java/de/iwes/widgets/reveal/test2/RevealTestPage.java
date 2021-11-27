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
package de.iwes.widgets.reveal.test2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ogema.core.administration.AdminApplication;
import org.ogema.core.application.Application;
import org.ogema.core.application.ApplicationManager;
import org.ogema.core.model.Resource;
import org.ogema.core.model.ValueResource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.SingleValueResource;
import org.ogema.core.model.units.TemperatureResource;
import org.ogema.core.resourcemanager.ResourceAccess;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;
import org.ogema.model.actors.Actor;
import org.ogema.model.connections.ElectricityConnection;
import org.ogema.model.devices.buildingtechnology.ElectricLight;
import org.ogema.model.devices.buildingtechnology.Thermostat;
import org.ogema.model.devices.connectiondevices.ElectricityConnectionBox;
import org.ogema.model.devices.sensoractordevices.SingleSwitchBox;
import org.ogema.model.locations.Building;
import org.ogema.model.locations.BuildingPropertyUnit;
import org.ogema.model.locations.Room;
import org.ogema.model.sensors.DoorWindowSensor;
import org.ogema.model.sensors.PowerSensor;
import org.ogema.model.sensors.Sensor;
import org.ogema.model.sensors.TemperatureSensor;
import org.ogema.tools.resource.util.LoggingUtils;
import org.ogema.tools.resource.util.MultiTimeSeriesUtils;
import org.ogema.tools.resource.util.ResourceUtils;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import de.iwes.widgets.api.services.IconService;
import de.iwes.widgets.api.services.NameService;
import de.iwes.widgets.api.widgets.LazyWidgetPage;
import de.iwes.widgets.api.widgets.OgemaWidget;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.dynamics.TriggeredAction;
import de.iwes.widgets.api.widgets.dynamics.TriggeringAction;
import de.iwes.widgets.api.widgets.html.HtmlItem;
import de.iwes.widgets.api.widgets.html.PageSnippetI;
import de.iwes.widgets.api.widgets.html.StaticHeader;
import de.iwes.widgets.api.widgets.html.StaticLink;
import de.iwes.widgets.api.widgets.html.StaticList;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.checkbox.Checkbox2;
import de.iwes.widgets.html.form.checkbox.DefaultCheckboxEntry;
import de.iwes.widgets.html.form.dropdown.TemplateDropdown;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.Label;
import de.iwes.widgets.html.form.label.Link;
import de.iwes.widgets.html.html5.Flexbox;
import de.iwes.widgets.html.html5.SimpleGrid;
import de.iwes.widgets.html.html5.flexbox.AlignContent;
import de.iwes.widgets.html.html5.flexbox.AlignItems;
import de.iwes.widgets.html.html5.flexbox.FlexWrap;
import de.iwes.widgets.html.html5.flexbox.JustifyContent;
import de.iwes.widgets.html.icon.Icon;
import de.iwes.widgets.html.icon.IconType;
import de.iwes.widgets.html.listgroup.ListGroup;
import de.iwes.widgets.html.plot.api.PlotType;
import de.iwes.widgets.reswidget.scheduleplot.api.TimeSeriesPlot;
import de.iwes.widgets.reswidget.scheduleplot.plotlyjs.SchedulePlotlyjs;
import de.iwes.widgets.reswidget.scheduleviewer.DefaultSchedulePresentationData;
import org.ogema.humread.valueconversion.SchedulePresentationData;
import de.iwes.widgets.reveal.base.ColumnTemplate;
import de.iwes.widgets.reveal.base.RevealWidgetPage;
import de.iwes.widgets.template.DisplayTemplate;
import de.iwes.widgets.template.WidgetTemplate;

/*
 * TODO
 *   - window sensors, power sensors: print state (open/closed resp. Watts) in overview slide
 *   - Room: details (in separate app?)
 *   - power generation units; part of PowerSensors?
 */
// TODO aggregate all global polling requests into a widget group?
@Component(
		service=LazyWidgetPage.class,
		property= {
				LazyWidgetPage.BASE_URL + "=/de/iee/widgets/reveal-test2",
				LazyWidgetPage.RELATIVE_URL + "=index.html",
				LazyWidgetPage.START_PAGE + "=true",
				LazyWidgetPage.MENU_ENTRY + "=Reveal.js test page",
				LazyWidgetPage.CUSTOM_PAGE_TYPE + "=true"
		}
)
public class RevealTestPage implements LazyWidgetPage {

	static final String DEVICE_MNGMT_PROP = "org.ogema.apps.device.mgmt";
	private final Map<String, List<ComponentServiceObjects<?>>> managementApps = new ConcurrentHashMap<>();
	
	@Reference(
			target="(" + DEVICE_MNGMT_PROP + "=*)",
			cardinality=ReferenceCardinality.MULTIPLE,
			service=LazyWidgetPage.class,
			policy=ReferencePolicy.DYNAMIC,
			policyOption=ReferencePolicyOption.GREEDY,
			bind="addApp0",
			unbind="removeApp0"
	)
	protected void addApp0(ComponentServiceObjects<LazyWidgetPage> page) {
		addApp(page);
	}
	
	protected void removeApp0(ComponentServiceObjects<LazyWidgetPage> page) {
		removeApp(page);
	}
	
	@Reference(
			target="(" + DEVICE_MNGMT_PROP + "=*)",
			cardinality=ReferenceCardinality.MULTIPLE,
			service=Application.class,
			policy=ReferencePolicy.DYNAMIC,
			policyOption=ReferencePolicyOption.GREEDY,
			bind="addApp1",
			unbind="removeApp1"
	)
	protected void addApp1(ComponentServiceObjects<Application> app) {
		addApp(app);
	}
	
	protected void removeApp1(ComponentServiceObjects<Application> app) {
		removeApp(app);
	}
	
	private void addApp(ComponentServiceObjects<?> app) {
		final ServiceReference<?> ref = app.getServiceReference();
		final Object prop = ref.getProperty(DEVICE_MNGMT_PROP);
		final List<ComponentServiceObjects<?>> pages = managementApps.computeIfAbsent((String) prop, key -> Collections.synchronizedList(new ArrayList<>()));
		pages.add(app);
	}
	
	private void removeApp(ComponentServiceObjects<?> app) {
		final ServiceReference<?> ref = app.getServiceReference();
		final Object prop = ref.getProperty(DEVICE_MNGMT_PROP);
		final List<ComponentServiceObjects<?>> pages = managementApps.get((String) prop);
		if (pages != null)
			pages.remove(app);
	}
	
	@Override
	public void init(ApplicationManager appMan, WidgetPage<?> page) {
		new RevealPageInit((RevealWidgetPage<?>) page, appMan, managementApps);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Class<? extends WidgetPage> getPageType() {
		return RevealWidgetPage.class;
	}
	
	private static class RevealPageInit {
	
		private static final Comparator<ComponentServiceObjects<?>> SERVICE_COMPARATOR_INVERSE = new Comparator<ComponentServiceObjects<?>>() {

			@Override
			public int compare(ComponentServiceObjects<?> o1, ComponentServiceObjects<?> o2) {
				final Object rank1 = o1.getServiceReference().getProperty(Constants.SERVICE_RANKING);
				final Object rank2 = o2.getServiceReference().getProperty(Constants.SERVICE_RANKING);
				final int r1 = rank1 instanceof Integer ? ((Integer) rank1).intValue() : 0;
				final int r2 = rank2 instanceof Integer ? ((Integer) rank2).intValue() : 0;
				return -Integer.compare(r1, r2);
			}
			
		};
		private final RevealWidgetPage<?> page;
		// avg temperatures
		private final Label avTempInside;
		private final Label avTempOutside;
		private final Label powerConsumption;
		private final Flexbox ecoFlex;
		private final Button switchOffEverything;
		
		// nr sensors
		private final Label nrTempSensors;
		private final Label nrPowerSensor;
		private final Label nrSensor;
		private final Label nrSwitchBoxes;
		private final Label nrThermostats;
		private final Label nrActors;
		private final Icon tempIcon;
		private final Icon powerIcon;
		private final Icon sensorIcon;
		private final Icon switchBoxIcon;
		private final Icon actorsIcon;
		private final Icon thermostatsIcon;
		
//		private final ListLabel rooms;
		private final ListGroup<Room> rooms;
		private final ColumnTemplate roomsTemplate;
		
		private final ListGroup<SingleSwitchBox> switchboxes;
		private final SimpleGrid switchboxesGrid;
		private final Label switchboxesOn;
		private final Label switchboxesOverallConsumption;
		private final Button switchboxesAllOff;
		private final ColumnTemplate switchboxesTemplate;
	
		private final TemplateDropdown<Class<? extends Sensor>> sensorDropdown;
		private final Checkbox2 aggregateSensors;
		// temperature plots
		private final TimeSeriesPlot<?, ?, ?> temperaturePlots;
		
		private final ListGroup<ElectricLight> lights;
		private final Label lightsOn;
		private final Button lightsAllOff;
		private final SimpleGrid lightsGrid;
		private final ColumnTemplate lightsTemplate;
		
		private final ListGroup<DoorWindowSensor> windows;
		private final Label windowsOpen;
		private final SimpleGrid windowsGrid;
		private final ColumnTemplate windowsTemplate;
		
		private final ListGroup<ElectricityConnection> powerSensors;
		private final ColumnTemplate powerSensorsTemplate;

		private final ListGroup<ComponentServiceObjects<?>> appsList; 
		
		@SuppressWarnings("serial")
		RevealPageInit(final RevealWidgetPage<?> page, final ApplicationManager appMan, final Map<String, List<ComponentServiceObjects<?>>> managementApps) {
			this.page = page;
			this.avTempInside = new Label(page, "avTempIn") {
				
				@Override
				public void onGET(OgemaHttpRequest req) {
					final OptionalDouble opt = appMan.getResourceAccess().getResources(TemperatureSensor.class).stream()
						.filter(sensor -> !isOutsideSensor(sensor))
						.filter(sensor -> sensor.reading().isActive())
						.mapToDouble(sensor -> sensor.reading().getCelsius())
						.filter(value -> value > -50) // filter out nonsense values...
						.average();
					if (!opt.isPresent())
						setText("Inside temperature: n.a.", req);
					else {
						final double temp = opt.getAsDouble();
						final String tempHtml = getTempHtml(temp, true);
						setHtml("Inside temperature: " + tempHtml, req);
//						setHtml(String.format(Locale.ENGLISH, "Inside temperature: %.2f°C", opt.getAsDouble()), req);
					}
					
				}
				
			};
			this.avTempOutside = new Label(page, "avTempOut") {
				
				@Override
				public void onGET(OgemaHttpRequest req) {
					final OptionalDouble opt = appMan.getResourceAccess().getResources(TemperatureSensor.class).stream()
						.filter(sensor -> isOutsideSensor(sensor))
						.filter(sensor -> sensor.reading().isActive())
						.mapToDouble(sensor -> sensor.reading().getCelsius())
						.filter(value -> value > -50) // filter out nonsense values...
						.average();
					if (!opt.isPresent())
						setText("Outside temperature: n.a.", req);
					else {
						final double temp = opt.getAsDouble();
						final String tempHtml = getTempHtml(temp, false);
						setHtml("Outside temperature: " + tempHtml, req);
//						setText(String.format(Locale.ENGLISH, "Outside temperature: %.2f°C", opt.getAsDouble()), req);
					}
				}
				
			};
			this.powerConsumption = new Label(page, "powerConsumption") {
				
				public void onGET(OgemaHttpRequest req) {
					Optional<ElectricityConnection> connOpt = appMan.getResourceAccess().getResources(Building.class).stream()
							.map(building -> building.electricityConnectionBox().connection())
							.filter(conn -> conn.powerSensor().reading().isActive())
							.findAny();
					if (!connOpt.isPresent()) {
						connOpt = appMan.getResourceAccess().getResources(BuildingPropertyUnit.class).stream()
							.map(unit -> unit.electricityConnectionBox().connection())
							.filter(conn -> conn.powerSensor().reading().isActive())
							.findAny();
					}
					if (!connOpt.isPresent()) {
						connOpt = appMan.getResourceAccess().getResources(ElectricityConnectionBox.class).stream()
							.map(box -> box.connection())
							.filter(conn -> conn.powerSensor().reading().isActive())
							.findAny();
					}
					if (!connOpt.isPresent()) {
						setText("Electric power consumption: n.a.", req);
						setPollingInterval(-1, req);
					}
					else {
						setText(String.format(Locale.ENGLISH, "Electric power consumption: %.2f W", connOpt.get().powerSensor().reading().getValue()), req);
						setPollingInterval(15000, req);
					}
				}
				
			};
			this.switchOffEverything = new Button(page, "switchOffEverything", "Start") {
				
				private final ThreadLocal<EcoModeState> ecoMode = new ThreadLocal<EcoModeState>() {
					
					@Override
					protected EcoModeState initialValue() {
						return new EcoModeState();
					}
					
				};
				
				@Override
				public void onGET(OgemaHttpRequest req) {
					final EcoModeState eco = ecoMode.get();
					synchronized (eco) {
						setText(eco.active ? "End" : "Start", req);
					}
				}
				
				@Override
				public void onPOSTComplete(String data, OgemaHttpRequest req) {
					final EcoModeState eco = ecoMode.get();
					synchronized (eco) {
						eco.toggle(appMan);
					}
				}
				
			};
			switchOffEverything.addDefaultCssItem(">button", Collections.singletonMap("vertical-align", "middle"));
			this.ecoFlex = new Flexbox(page, "ecoFlex", true);
			ecoFlex.setDefaultJustifyContent(JustifyContent.CENTER);
			final Label ecoLabel = new Label(page, "ecoLabel", true);
			ecoLabel.setDefaultText("Eco mode (switch devices off):");
			ecoLabel.setDefaultMargin("0.4em", false, false, false, true);
			ecoFlex.addItem(ecoLabel, null).addItem(switchOffEverything, null);
			
			// there are too many widgets on this page, must avoid polling very often...
			avTempInside.setDefaultPollingInterval(60000);
			avTempOutside.setDefaultPollingInterval(60000);
			powerConsumption.setDefaultPollingInterval(60000);
			
			
			this.nrTempSensors = new NrResourcesLabel(page, "nrTempSens", appMan.getResourceAccess(), TemperatureSensor.class);
			this.nrPowerSensor = new NrResourcesLabel(page, "nrPowerSens", appMan.getResourceAccess(), PowerSensor.class);
			this.nrSwitchBoxes = new NrResourcesLabel(page, "nrSwitchBoxes", appMan.getResourceAccess(), SingleSwitchBox.class);
			this.nrThermostats = new NrResourcesLabel(page, "nrThermostats", appMan.getResourceAccess(), Thermostat.class);
			this.nrSensor = new NrResourcesLabel(page, "nrSensors", appMan.getResourceAccess(), Sensor.class);
			this.nrActors = new NrResourcesLabel(page, "nrActors", appMan.getResourceAccess(), Actor.class);
			
			this.tempIcon = new ResTypeIcon(page, "tempIcon", TemperatureSensor.class);
			this.powerIcon = new ResTypeIcon(page, "powerIcon", PowerSensor.class);
			this.switchBoxIcon = new ResTypeIcon(page, "swithcboxIocn", SingleSwitchBox.class);
			this.sensorIcon = new ResTypeIcon(page, "sensorIcon", Sensor.class);
			this.actorsIcon = new ResTypeIcon(page, "actorsIcon", Actor.class);
			this.thermostatsIcon = new ResTypeIcon(page, "thermostatsIcon", Thermostat.class);
			
			this.sensorDropdown = new TemplateDropdown<Class<? extends Sensor>>(page, "snesorDropdown") {

				@SuppressWarnings("unchecked")
				private Class<? extends Sensor> getType(final Sensor sensor) {
					Class<? extends Sensor> previous = (Class<? extends Sensor>) sensor.getResourceType();
					for (Class<?> type = previous; type != null; type = type.getSuperclass()) {
						if (type == Sensor.class || !Sensor.class.isAssignableFrom(type))
							return previous;
						if (type.getName().startsWith("org.ogema."))
							return (Class<? extends Sensor>) type;
					}
					return previous;
				}
				
				public void onGET(OgemaHttpRequest req) {
					update(appMan.getResourceAccess().getResources(Sensor.class).stream()
						.map(sensor -> getType(sensor))
						.distinct()
						.collect(Collectors.toList()), req);
				}
				
			};
			sensorDropdown.selectDefaultItem(TemperatureSensor.class);
			final DisplayTemplate<Class<? extends Sensor>> sensorTemplate = new DisplayTemplate<Class<? extends Sensor>>() {
				
				@Override
				public String getLabel(Class<? extends Sensor> object, OgemaLocale locale) {
					String simple = object.getSimpleName();
					final StringBuilder sb = new StringBuilder();
					int cnt = 0;
					int lastUpper = -1;
					for (char c: simple.toCharArray()) {
						if (cnt++ > 1 && Character.isUpperCase(c) && cnt > lastUpper + 1) {
							sb.append(' ');
							lastUpper = cnt;
						}
						sb.append(c);
					}
					return sb.toString();
				}
				
				@Override
				public String getId(Class<? extends Sensor> object) {
					return object.getName();
				}
			};
			sensorDropdown.setTemplate(sensorTemplate);
			
			this.aggregateSensors = new Checkbox2(page, "aggregateSensors");
			aggregateSensors.setDefaultCheckboxList(Collections.singletonList(
					new DefaultCheckboxEntry("", "Aggregate values", false)
			));
			aggregateSensors.addDefaultCssStyle("font-size", "medium");
			aggregateSensors.addDefaultCssStyle("padding-top", "1em");
			
			
			this.temperaturePlots = new SchedulePlotlyjs(page, "temperaturePlots", true) {
				
				private final long TWO_DAYS = 2 * 24 * 60* 60* 1000;
				
				/**
				 * @param r
				 * @return
				 * 		null: undetermined, true: inside, false: outside
				 */
				private Boolean isInsideOrOutside(final Resource r) {
					try {
						final Room room = ResourceUtils.getDeviceLocationRoom(r);
						if (room == null || !room.type().isActive())
							return null;
						return room.type().getValue() != 0;
					} catch (SecurityException e) {
						return null;
					}
				}
				
				public void onGET(final OgemaHttpRequest req) {
					final Class<? extends Sensor> type = sensorDropdown.getSelectedItem(req);
					if (type == null) {
						getScheduleData(req).setSchedules(Collections.emptyMap());
						return;
					}
					final Stream<? extends Sensor> sensors = appMan.getResourceAccess().getResources(type).stream()
							.filter(sensor -> {
								final ValueResource val = sensor.reading();
								return val instanceof SingleValueResource && LoggingUtils.isLoggingEnabled((SingleValueResource) val);
							});
					final boolean doAggregate = aggregateSensors.isChecked("", req);
					final long now = appMan.getFrameworkTime();
					final long start = now - TWO_DAYS;
					final long end = now + TWO_DAYS;
					if (doAggregate) {
						final List<Sensor> list = sensors.collect(Collectors.toList());
						final boolean containsInsideAndOutsideSensors = list.stream()
									.map(sensor -> isInsideOrOutside(sensor))
									.filter(val -> val != null)
									.distinct().count() > 1;
						if (containsInsideAndOutsideSensors) {
							final List<ReadOnlyTimeSeries> timeSeriesInside = list.stream()
									.filter(sensor -> {
										final Boolean bool = isInsideOrOutside(sensor);
										return bool == null || bool.booleanValue();
									})
									.map(sensor -> LoggingUtils.getHistoricalData((SingleValueResource) sensor.reading()))
									.collect(Collectors.toList());
							final List<ReadOnlyTimeSeries> timeSeriesOutside = list.stream()
									.filter(sensor -> {
										final Boolean bool = isInsideOrOutside(sensor);
										return bool != null && !bool.booleanValue();
									})
									.map(sensor -> LoggingUtils.getHistoricalData((SingleValueResource) sensor.reading()))
									.collect(Collectors.toList());
							final ReadOnlyTimeSeries tsInside = MultiTimeSeriesUtils.getAverageTimeSeriesLazy(timeSeriesInside, start, end, true, InterpolationMode.STEPS, false);
							final ReadOnlyTimeSeries tsOutside = MultiTimeSeriesUtils.getAverageTimeSeriesLazy(timeSeriesOutside, start, end, true, InterpolationMode.STEPS, false);
							final String label = "Average " + sensorTemplate.getLabel(type, req.getLocale()).toLowerCase().replace(" sensor", "");
							final SchedulePresentationData dataInside = new DefaultSchedulePresentationData(tsInside, Float.class, label + " inside");
							final SchedulePresentationData dataOutside = new DefaultSchedulePresentationData(tsOutside, Float.class, label + " outside");
							final Map<String, SchedulePresentationData> map = new HashMap<>(4);
							map.put(label + " inside", dataInside);
							map.put(label + " outside", dataOutside);
							getScheduleData(req).setSchedules(map);
						} else {
							final List<ReadOnlyTimeSeries> timeSeries = list.stream()
								.map(sensor -> LoggingUtils.getHistoricalData((SingleValueResource) sensor.reading()))
								.collect(Collectors.toList());
							final ReadOnlyTimeSeries ts = MultiTimeSeriesUtils.getAverageTimeSeriesLazy(timeSeries, start, end, true, InterpolationMode.STEPS, false);
							final String label = "Average " + sensorTemplate.getLabel(type, req.getLocale()).toLowerCase().replace(" sensor", "");
							final SchedulePresentationData data = new DefaultSchedulePresentationData(ts, Float.class, label);
							getScheduleData(req).setSchedules(Collections.singletonMap(label, data));
						}
					} else {
						final Map<String, SchedulePresentationData> map = sensors
							.collect(Collectors.<Sensor, String,SchedulePresentationData> toMap(s -> getName(s, req.getLocale()), 
									s -> new DefaultSchedulePresentationData(LoggingUtils.getHistoricalData((SingleValueResource) s.reading()), 
											s instanceof TemperatureSensor ? TemperatureResource.class : Float.class, getName(s, req.getLocale()))));
						getScheduleData(req).setSchedules(map);
					}
					getScheduleData(req).setStartTime(start);
					getScheduleData(req).setEndTime(end); // there should not be any newer data anyway...
				}
				
				private final String getName(final Sensor sensor, final OgemaLocale locale) {
					final NameService names = getNameService();
					final String n = names == null ? null : names.getName(sensor, locale);
					return n != null ? n : ResourceUtils.getHumanReadableName(sensor);
				}
				
			};
			temperaturePlots.getDefaultConfiguration().setPlotType(PlotType.LINE);
			temperaturePlots.setDefaultPollingInterval(30000);
			
			// TODO name service
			final WidgetTemplate<Room> roomsTemplate0 = (room, parent, req) -> new ResourceLink(room, null, req);
			this.rooms = new ListGroup<Room>(page, "rooms", roomsTemplate0) {
				
				public void onGET(OgemaHttpRequest req) {
					update(appMan.getResourceAccess().getResources(Room.class).stream()
						.filter(Resource::isActive)
						.collect(Collectors.toList()), req);
							
				}
				
			};
			
			this.roomsTemplate = new RoomsTemplate(appMan);
			final WidgetTemplate<SingleSwitchBox> switchboxesTemplate = (box, parent, req) -> new ResourceLink(box, null, req);
			this.switchboxes = new ListGroup<SingleSwitchBox>(page, "switchboxes", switchboxesTemplate) {
				
				public void onGET(OgemaHttpRequest req) {
					update(appMan.getResourceAccess().getResources(SingleSwitchBox.class).stream()
							.filter(Resource::isActive)
							.collect(Collectors.toList()), req);
				}
				
			};
			this.switchboxesTemplate = new SwitchBoxTemplate(appMan);
			this.switchboxesOn = new Label(page, "switchboxesOn") {
				
				public void onGET(OgemaHttpRequest req) {
					final long nr = appMan.getResourceAccess().getResources(SingleSwitchBox.class).stream()
						.filter(box -> box.onOffSwitch().stateFeedback().isActive() && box.onOffSwitch().stateFeedback().getValue())
						.count();
					setText(String.valueOf(nr), req);
				}
				
			};
			this.switchboxesOverallConsumption = new Label(page, "switchboxesOverallConsumption") {
				
				public void onGET(OgemaHttpRequest req) {
					 final double watts = appMan.getResourceAccess().getResources(SingleSwitchBox.class).stream()
					 	.map(box -> box.electricityConnection().powerSensor().reading())
					 	.filter(reading -> reading.isActive())
					 	.mapToDouble(reading -> reading.getValue())
					 	.sum();
					 setText(String.format(Locale.ENGLISH, "%.2f W", watts), req);
				}
				
			};
			switchboxesOverallConsumption.setDefaultPollingInterval(30000);
			this.switchboxesAllOff = new Button(page, "switchboxesAllOff", "All off" ) {
				
				public void onPOSTComplete(String data, OgemaHttpRequest req) {
					appMan.getResourceAccess().getResources(SingleSwitchBox.class).stream()
					    .map(box -> box.onOffSwitch().stateControl())
					    .filter(Resource::exists)
					    .forEach(r -> setBooleanSafe(r, false));
				}
				
			};
			this.switchboxesGrid = new SimpleGrid(page, "switchboxesGrid", true)
					.addItem("Currently on: ", false, null).addItem(switchboxesOn, false, null)
					.addItem("Switch off: ", true, null).addItem(switchboxesAllOff, false, null)
					.addItem("Overall consumption: ", true, null).addItem(switchboxesOverallConsumption, false, null);
			switchboxesGrid.setPrependFillColumn(true, null);
			switchboxesGrid.setAppendFillColumn(true, null);
			switchboxesGrid.setRowGap("0", null);
			switchboxesGrid.addCssItem(">div>span", Collections.singletonMap("text-align", "left"), null);
			
			final WidgetTemplate<ElectricLight> lightsTemplate = (light, parent, req) -> new ResourceLink(light, null, req);
			this.lights = new ListGroup<ElectricLight>(page, "lights", lightsTemplate) {
				
				public void onGET(OgemaHttpRequest req) {
					update(appMan.getResourceAccess().getResources(ElectricLight.class).stream()
							.filter(Resource::isActive)
							.collect(Collectors.toList()), req);
				}
				
			};
			this.lightsOn = new Label(page, "lightsOn") {
				
				public void onGET(OgemaHttpRequest req) {
					final long nr = appMan.getResourceAccess().getResources(ElectricLight.class).stream()
						.map(light -> light.onOffSwitch().stateFeedback())
						.filter(fb -> fb.isActive() && fb.getValue())
						.count();
					setText(String.valueOf(nr), req);
				}
				
			};
			this.lightsAllOff = new Button(page, "lightsAllOff", "All off") {
				
				public void onPOSTComplete(String data, OgemaHttpRequest req) {
					appMan.getResourceAccess().getResources(ElectricLight.class).stream()
						.map(light -> light.onOffSwitch().stateControl())
						.filter(Resource::exists)
						.forEach(r -> setBooleanSafe(r, false));
				}
				
			};
			this.lightsGrid = new SimpleGrid(page, "lightsGrid", true)
					.addItem("Currently on: ", false, null).addItem(lightsOn, false, null)
					.addItem("Switch off: ", true, null).addItem(lightsAllOff, false, null);
			lightsGrid.setPrependFillColumn(true, null);
			lightsGrid.setAppendFillColumn(true, null);
			lightsGrid.setRowGap("0", null);
			lightsGrid.addCssItem(">div>span", Collections.singletonMap("text-align", "left"), null);
			
			this.lightsTemplate = new LightsTemplate(appMan);
			
			final WidgetTemplate<DoorWindowSensor> windowsTemplate = (window, parent, req) -> new ResourceLink(window, null, req);
			this.windows = new ListGroup<DoorWindowSensor>(page, "windows", windowsTemplate) {
				
				public void onGET(OgemaHttpRequest req) {
					update(appMan.getResourceAccess().getResources(DoorWindowSensor.class).stream()
							.filter(Resource::isActive)
							.collect(Collectors.toList()), req);
				}
				
			};
			this.windowsOpen = new Label(page, "windowsOpen") {
				
				public void onGET(OgemaHttpRequest req) {
					final long nr = appMan.getResourceAccess().getResources(DoorWindowSensor.class).stream()
						.map(sensor -> sensor.reading())
						.filter(reading -> reading.isActive() && reading.getValue())
						.count();
					setText(String.valueOf(nr), req);
				}
				
			};
			this.windowsGrid = new SimpleGrid(page, "windowsGrid", true)
					.addItem("Currently open: ", false, null).addItem(windowsOpen, false, null);
			windowsGrid.setPrependFillColumn(true, null);
			windowsGrid.setAppendFillColumn(true, null);
			windowsGrid.setRowGap("0", null);
			windowsGrid.addCssItem(">div>span", Collections.singletonMap("text-align", "left"), null);
			
			this.windowsTemplate =new WindowsTemplate(appMan);
			
			// TODO show also power consumption: use a custom template here 
			final WidgetTemplate<ElectricityConnection> powerSensorsTemplate = (powerSensor, parent, req) -> new ResourceLink(powerSensor, null, req);
			this.powerSensors = new ListGroup<ElectricityConnection>(page, "powerSensors", powerSensorsTemplate) {
				
				public void onGET(OgemaHttpRequest req) {
					update(PowerSensorTemplate.getConnections(appMan)
							.collect(Collectors.toList()), req);
				}
				
			};
			this.powerSensorsTemplate = new PowerSensorTemplate(appMan);
			
			
			final AtomicLong appsCnt = new AtomicLong(0);
			final WidgetTemplate<ComponentServiceObjects<?>> appsTemplate = new WidgetTemplate<ComponentServiceObjects<?>>() {

				@Override
				public Object getItem(ComponentServiceObjects<?> app, OgemaWidget parent, OgemaHttpRequest req) {
					return new AppWithIconFlex(appsList, "appFlex_" + appsCnt.getAndIncrement(), req, app, appMan);
				}
			};
			this.appsList = new ListGroup<ComponentServiceObjects<?>>(page, "appsList", appsTemplate) {
				
				public void onGET(OgemaHttpRequest req) {
					// TODO check for user permission to access some app?
					update(managementApps.values().stream()
						.map(list -> list.stream().sorted(SERVICE_COMPARATOR_INVERSE).findFirst().orElse(null))
						.filter(service -> service != null)
						.collect(Collectors.toList()), req);
				}
				
			};
			
			buildPage();
			setDependencies();
			
		}
		
		private final void buildPage() {
			
			final PageSnippetI slide1 = page.addSlide("summary");
			slide1.append(new StaticHeader(2, "System overview"), null)
				.append(avTempInside, null)
				.append(avTempOutside, null)
				.append(powerConsumption, null)
				.append(ecoFlex, null);
			final Flexbox flex0 = new Flexbox(page, "sensorPlotFlex", true)
					.addItem(sensorDropdown, null)
					.addItem(aggregateSensors, null);
			flex0.setDefaultJustifyContent(JustifyContent.SPACE_AROUND);
			flex0.setDefaultFlexWrap(FlexWrap.WRAP);
			final StaticList contentList = new StaticList();
			contentList.newEntry().addSubItem(new StaticLink("#sensorPlots", "Sensor data plot"));
			contentList.newEntry().addSubItem(new StaticLink("#sensact", "Sensor/Actor overview"));
			contentList.newEntry().addSubItem(new StaticLink("#rooms", "Rooms"));
			contentList.newEntry().addSubItem(new StaticLink("#switchboxes", "Switch boxes"));
			contentList.newEntry().addSubItem(new StaticLink("#lights", "Electric lights"));
			contentList.newEntry().addSubItem(new StaticLink("#windows", "Window sensors"));
			contentList.newEntry().addSubItem(new StaticLink("#powerSensors", "Power sensors"));
			contentList.newEntry().addSubItem(new StaticLink("#apps", "More apps")); 
			page.addSlide("content")
				.append(new StaticHeader(2, "Content"), null)
				.append(contentList, null);
			page.addSlide("sensorPlots")
				.append(new StaticHeader(2, "Sensor data"), null)
				.append(temperaturePlots, null)
				.append(flex0, null);
			final StaticList list = new StaticList();
			list.newEntry().addSubItem(tempIcon).addSubItem(HtmlItem.EMPTY_SPACE).addSubItem("Temperature sensors: ").addSubItem(nrTempSensors);
			list.newEntry().addSubItem(powerIcon).addSubItem(HtmlItem.EMPTY_SPACE).addSubItem("Power sensors: ").addSubItem(nrPowerSensor);
			list.newEntry().addSubItem(sensorIcon).addSubItem(HtmlItem.EMPTY_SPACE).addSubItem("All sensors: ").addSubItem(nrSensor);
			list.newEntry().addSubItem(switchBoxIcon).addSubItem(HtmlItem.EMPTY_SPACE).addSubItem("Switch boxes: ").addSubItem(nrSwitchBoxes);
			list.newEntry().addSubItem(thermostatsIcon).addSubItem(HtmlItem.EMPTY_SPACE).addSubItem("Thermostats: ").addSubItem(nrThermostats);
			list.newEntry().addSubItem(actorsIcon).addSubItem(HtmlItem.EMPTY_SPACE).addSubItem("All actors: ").addSubItem(nrActors);
			page.addSlide("sensact") // TODO
				.append(new StaticHeader(2, "Sensor/Actor overview"), null)
				.append(list, null);
			final PageSnippetI roomsSlide = page.addSlide("rooms")
				.append(new StaticHeader(2, "Rooms"), null)
				.append(rooms, null);
			page.setColumnsTemplate(roomsSlide, roomsTemplate);
			
			final HeaderFlexWithIcon switchboxHeader = new HeaderFlexWithIcon(page, "Switchboxes", SingleSwitchBox.class);
			final PageSnippetI boxesSlide = page.addSlide("switchboxes")
				.append(switchboxHeader, null)
				.append(switchboxesGrid, null)
				.linebreak(null)
				.append(switchboxes, null);
			final Collection<OgemaWidget> swTriggers = Collections.singleton(switchboxesAllOff);
			final Collection<OgemaWidget> swTriggered = Arrays.asList(switchboxesOn, switchboxesOverallConsumption);
			page.setColumnsTemplate(boxesSlide, switchboxesTemplate, swTriggers, swTriggered);
			
			final HeaderFlexWithIcon lightsHeader = new HeaderFlexWithIcon(page, "Electric lights", ElectricLight.class);
			final PageSnippetI lightsSlide = page.addSlide("lights")
					.append(lightsHeader, null)
					.append(lightsGrid, null)
					.linebreak(null)
					.append(lights, null);
			final Collection<OgemaWidget> lightsTriggers = Collections.singleton(lightsAllOff);
			final Collection<OgemaWidget> lightsTriggered = Arrays.asList(lightsOn);			
			page.setColumnsTemplate(lightsSlide, lightsTemplate, lightsTriggers, lightsTriggered);
			
			final HeaderFlexWithIcon windowsHeader = new HeaderFlexWithIcon(page, "Window sensors", DoorWindowSensor.class);			
			final PageSnippetI windowsSlide = page.addSlide("windows")
					.append(windowsHeader, null)
					.append(windowsGrid, null)
					.linebreak(null)
					.append(windows, null);
			final Collection<OgemaWidget> windowsTriggered = Arrays.asList(windowsOpen);			
			page.setColumnsTemplate(windowsSlide, windowsTemplate, null, windowsTriggered);
			
			final HeaderFlexWithIcon powerSensorHeader = new HeaderFlexWithIcon(page, "Power sensors", PowerSensor.class);
			final PageSnippetI powerSensorsSlide = page.addSlide("powerSensors")
					.append(powerSensorHeader, null)
					.linebreak(null)
					.append(powerSensors, null);
			page.setColumnsTemplate(powerSensorsSlide, powerSensorsTemplate);
			
			final PageSnippetI appsSlide = page.addSlide("apps")
					.append(new StaticHeader(2, "More apps..."), null)
					.append(appsList, null);
			
		}
		
		private final void setDependencies() {
			sensorDropdown.triggerAction(temperaturePlots, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			aggregateSensors.triggerAction(temperaturePlots, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			switchboxesAllOff.triggerAction(switchboxesOn, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			switchboxesAllOff.triggerAction(switchboxesOverallConsumption, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			lightsAllOff.triggerAction(lightsOn, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);

			switchOffEverything.triggerAction(switchOffEverything, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			switchOffEverything.triggerAction(switchboxesOn, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
			switchOffEverything.triggerAction(lightsOn, TriggeringAction.POST_REQUEST, TriggeredAction.GET_REQUEST);
		}
		
		private static boolean isOutsideSensor(final Sensor sensor) {
			final Room location = ResourceUtils.getDeviceRoom(sensor);
			if (location == null || !location.type().isActive())
				return false;
			return location.type().getValue() == 0;
		}
		
		@SuppressWarnings("serial")
		private static class NrResourcesLabel extends Label {

			private final ResourceAccess ra;
			private final Class<? extends Resource> type;
			
			NrResourcesLabel(WidgetPage<?> page, String id, ResourceAccess ra, Class<? extends Resource> type) {
				super(page, id);
				addDefaultCssStyle("display", "inline-block");
				this.type = type;
				this.ra = ra;
			}
			
			@Override
			public void onGET(OgemaHttpRequest req) {
				setText(String.format(Locale.ENGLISH, " %d ", ra.getResources(type).size()), req);
			}
			
		}
		
		@SuppressWarnings("serial")
		private static class ResTypeIcon extends Icon {
			
//			private static final WidgetStyle<Icon> iconTextAlignmentStyle = new Wid
			private static final Map<String, String> CSS_ICON_TEXT_ALIGNMENT = Arrays.stream(new String[] {
					"max-height=1em",
					"vertical-align=-0.6em"
				})
				.map(string -> string.split("="))
				.collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
			
			ResTypeIcon(WidgetPage<?> page, String id, Class<? extends Resource> type) {
				super(page, id);
				addDefaultCssStyle("display", "inline-block");
				addDefaultCssItem(">div#icon>img", CSS_ICON_TEXT_ALIGNMENT);
				final IconService service = getIconService();
				if (service != null) {
					final String url = service.getIcon(type);
					if (url != null && !url.isEmpty())
						setDefaultIconType(new IconType(url));
				}
			}
			
		}
		
		private static class ResourceLink extends StaticLink {
			
			private static final String getName(final Resource room, final NameService names, final OgemaLocale locale) {
				final String n = names == null ? null : names.getName(room, locale);
				return n != null ? n : ResourceUtils.getHumanReadableName(room);
			}

			// FIXME is the link correct in all cases?
			ResourceLink(Resource room, NameService names, OgemaHttpRequest req) {
				super("#/" + ResourceUtils.getValidResourceName(room.getPath()), getName(room, names, req.getLocale()));
			}
			
		}
		
		@SuppressWarnings("serial")
		private static class HeaderFlexWithIcon extends Flexbox {

			public HeaderFlexWithIcon(final WidgetPage<?> page, final String text, final Class<? extends Resource> deviceType) {
				super(page, text.replace(" ", "").toLowerCase() + "_headerflex", true);
				final String id = getId();
				final Icon icon = new Icon(page, id + "_icon") {
					
					@Override
					public void onGET(OgemaHttpRequest req) {
						setIconType(new IconType(getIconService().getIcon(deviceType)), req);
					}
					
				};
				final Header header = new Header(page, id + "_header", text, true);
				header.setDefaultHeaderType(2);
				this.addItem(header, null)
					.addItem(icon, null);
				setJustifyContent(JustifyContent.CENTER, null);
				setAlignItems(AlignItems.CENTER, null);
				icon.setDefaultPadding("1em", false, true, false, false);
				icon.setDefaultMaxWidth("2em");
			}
			
		}
		
		@SuppressWarnings("serial")
		private static class AppWithIconFlex extends Flexbox {
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			private static String[] getLabelAndLink(final ComponentServiceObjects service, final ApplicationManager appMan) {
				final ServiceReference<?> ref = service.getServiceReference();
				String url = null;
				String label = null;
				final Object baseUrl = ref.getProperty(LazyWidgetPage.BASE_URL);
				final Object relativeUrl = ref.getProperty(LazyWidgetPage.RELATIVE_URL);
				if (baseUrl instanceof String && relativeUrl instanceof String) {
					String base = (String) baseUrl;
					String rel = (String) relativeUrl;
					if (!base.endsWith("/"))
						base = base + "/";
					if (!rel.isEmpty() && rel.charAt(0) == '/')
						rel = rel.substring(1);
					url = base + rel;
				} 
				Object prop = ref.getProperty("label");
				if (prop instanceof String)
					label = (String) prop;
				if (label == null) {
					try {
						label = service.getServiceReference().getBundle().getHeaders().get(Constants.BUNDLE_NAME);
						if (label != null && label.isEmpty())
							label = null;
					} catch (SecurityException expected) {
						// FIXME
						expected.printStackTrace();
					}
				}
				if (url == null) {
					final Object object = service.getService();
					try {
						if (object instanceof Application) {
							try {
								final Optional<AdminApplication> opt = appMan.getAdministrationManager().getAllApps().stream()
									.filter(app -> app.getID().getApplication() == object)
									.findAny();
								if (opt.isPresent())
									url = opt.get().getWebAccess().getStartUrl(); // may still be null
							} catch (SecurityException expected) {
								// FIXME
								expected.printStackTrace();
							}
						}
					} finally {
						service.ungetService(object);
					}
				}
				if (label == null && url != null)
					label = url;
				return new String[]{label, url};
			}

			public AppWithIconFlex(OgemaWidget parent, String id, OgemaHttpRequest req, ComponentServiceObjects<?> service, ApplicationManager appMan) {
				super(parent, id, req);
				String deviceType = (String) service.getServiceReference().getProperty(DEVICE_MNGMT_PROP);
				final int lastDot = deviceType.lastIndexOf('.');
				if (lastDot >= 0 && lastDot < deviceType.length()-1)
					deviceType = deviceType.substring(lastDot + 1);
				if (!deviceType.endsWith("s"))
					deviceType = deviceType + "s";
				final Label typeLabel = new Label(parent, id + "_devtype", req);
				typeLabel.setDefaultText(deviceType + ":");
				typeLabel.setDefaultPadding("0.5em", false, false, false, true);
				addItem(typeLabel, req);
				final String[] labelAndUrl = getLabelAndLink(service, appMan);
				final String label = labelAndUrl[0];
				final String url = labelAndUrl[1];
				if (url !=null) {  
					final Link link = new Link(parent, id + "_link", req);
					link.setDefaultText(label);
					link.setDefaultUrl(url);
					link.setDefaultNewTab(true);
					addItem(link, req);
				} else if (label != null) {
					final Label lab = new Label(parent, id + "_label", req);
					lab.setDefaultText(label);
					addItem(lab, req);
				}
				try {
					final long bid = service.getServiceReference().getBundle().getBundleId();
					final String iconUrl = "/ogema/widget/apps?action=getIcon&id=" + bid;
					final Icon icon = new Icon(parent, id + "_icon", req);
					icon.setDefaultIconType(new IconType(iconUrl));
					icon.setDefaultPadding("1em", false, true, false, false);
					icon.setDefaultMaxWidth("1.5em");
					icon.setOgemaServletSource(true, req);
					addItem(icon, req);
				} catch (SecurityException expected) {
					// FIXME
					expected.printStackTrace();
				}
				setAlignItems(AlignItems.CENTER, req);
			}
		}
		
		private static String getTempHtml(final double celsius, final boolean indoorOrOutdoor) {
			final String color = getColorForTemp(celsius, indoorOrOutdoor);
			final StringBuilder sb = new StringBuilder();
			sb.append("<span style=\"font-weight:bold;color:").append(color).append(";\">")
			  .append(String.format(Locale.ENGLISH, "%.2f°C", celsius))
			  .append("</span>");
			return sb.toString();
		}
		
		private static String getColorForTemp(double celsius, final boolean indoorOrOutdoor) {
			final double lower = indoorOrOutdoor ? 10 : -20;
			final double upper = indoorOrOutdoor ? 30 : 35;
			if (celsius > upper)
				celsius = upper;
			else if (celsius < lower)
				celsius = lower;
			final double comfy = indoorOrOutdoor ? 21 : 17;
			final double gettingCold = indoorOrOutdoor ? 19 : 7;
			final double cold = indoorOrOutdoor ? 17 : 2;
			// see hsl color scheme, e.g. https://en.wikipedia.org/wiki/HSL_and_HSV
			final int hue;
			if (celsius >= comfy)
				hue = (int) (45 * (upper - celsius) / (upper - comfy));
			else if (celsius >= gettingCold)
				hue = 45 + (int) (85 * (comfy - celsius) / (comfy - gettingCold));
			else if (celsius >= cold)
				hue = 130 + (int) (60 * (gettingCold - celsius) / (gettingCold - cold));
			else
				hue = 190 + (int) (70 * (cold - celsius) / (cold - lower));
			return "hsl(" + hue + ", 100%, 50%)";
		}
		
	}
	
	static void setBooleanSafe(final BooleanResource resource, boolean value) {
		try {
			if ("ecoModeActive".equals(resource.getName())) // hack for eco mode resource as opposed to swithces
				value = !value;
			resource.setValue(value);
		} catch (SecurityException ignore) {}
	}
}
