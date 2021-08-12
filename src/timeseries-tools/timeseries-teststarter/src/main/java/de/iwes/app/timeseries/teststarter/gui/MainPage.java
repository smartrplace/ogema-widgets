package de.iwes.app.timeseries.teststarter.gui;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;

import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.app.timeseries.teststarter.TeststarterController;
import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationInstance;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationProvider;
import de.iwes.timeseries.eval.api.extended.util.MultiEvaluationUtils;
import de.iwes.timeseries.eval.api.semaextension.GatewayDataExportUtil;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationInputImpl;
import de.iwes.timeseries.eval.base.provider.utils.TimeSeriesDataImpl;
import de.iwes.timeseries.eval.garo.api.base.GaRoPreEvaluationProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoStdPreEvaluationProvider;
import de.iwes.timeseries.eval.garo.api.base.GaRoSuperEvalResult;
import de.iwes.timeseries.eval.garo.helper.jaxb.GaRoEvalHelperJAXB;
import de.iwes.timeseries.eval.garo.helper.jaxb.GaRoTestStarter;
import de.iwes.timeseries.multi.provider.garoBase.GaRoBaseMultiResult;
import de.iwes.timeseries.multi.provider.garoWinHeat.GaRoWinMultiResult;
import de.iwes.timeseries.provider.genericcollection.ComfortTempRB_OverallProvider;
import de.iwes.timeseries.provider.genericcollection.ComfortTempRealBasedEvalProvider;
import de.iwes.timeseries.provider.genericcollection.ComfortTemperatureGenericEvalProvider;
import de.iwes.timeseries.provider.genericcollection.HeatingTimeEvaluationProvider;
import de.iwes.timeseries.provider.genericcollection.PresenceCorrelationEvaluationProvider;
import de.iwes.timeseries.provider.genericcollection.PresenceEvalProvider;
import de.iwes.timeseries.provider.genericcollection.WinHeatGenericEvalProvider;
import de.iwes.timeseries.provider.heatingloss.HeatLossEvalProvider;
import de.iwes.timeseries.provider.outsideTemperature.OutsideTemperatureEvalProvider;
import de.iwes.timeseries.provider.outsideTemperature.OutsideTemperatureMultiResult;
import de.iwes.timeseries.winopen.provider.WinHeatEvalProvider;
import de.iwes.widgets.api.widgets.WidgetPage;
import de.iwes.widgets.api.widgets.html.StaticTable;
import de.iwes.widgets.api.widgets.sessionmanagement.OgemaHttpRequest;
import de.iwes.widgets.html.form.button.Button;
import de.iwes.widgets.html.form.button.RedirectButton;
import de.iwes.widgets.html.form.label.Header;
import de.iwes.widgets.html.form.label.HeaderData;

/**
 * An HTML page, generated from the Java code.
 */
public class MainPage {
    static long[] startEnd = {1483228800000l, 1485907200000l, 1488326400000l,
            1491004800000l, 1493596800000l, 1496275200000l};
    //static long[] startEndTotal = {startEnd[0] + 600000, startEnd[startEnd.length - 1]};
    //February - April
    static long[] startEndTotal = {startEnd[1] + 600000, startEnd[startEnd.length - 2]-(24*3600*1000l)};
    static long[] startEndYear = {startEnd[1] + 600000, startEnd[1] + 600000+(356*24*3600*1000l)};
    private final String FILE_PATH = de.iwes.timeseries.eval.generic.gatewayBackupAnalysis.GaRoTestStarter.FILE_PATH;
    public final long UPDATE_RATE = 5 * 1000;
    private final WidgetPage<?> page;

    private final TeststarterController app;
    //Win heat
    public final Button gaRoButton1;
    //Comfort temperature
	public final Button gaRoButton4;
	public final Button gaRoButton4heattime;
	public final Button gaRoButton4ct_rt;
	public final Button gaRoButton4ct_rt_overall;
	public final Button gaRoButton4winvalve;
	public final Button gaRoButton4presence;
	public final Button gaRoButton4presenceBase;
	
	//Outside temperature
	public final Button gaRoButton5;
	//Heat loss
	public final Button gaRoButton6;
	

    public MainPage(final WidgetPage<?> page, final TeststarterController app) {
        this.page = page;
        this.app = app;

        Header header = new Header(page, "header", "Evaluation Testing Page");
        header.addDefaultStyle(HeaderData.TEXT_ALIGNMENT_CENTERED);

        Button startElEval = new Button(page, "startElEval", "Start Electricity Evaluation") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onGET(OgemaHttpRequest req) {
                if (app.serviceAccess.basicEvalProvider == null) {
                    disable(req);
                } else {
                    enable(req);
                }
            }

            @Override
            public void onPOSTComplete(String data, OgemaHttpRequest req) {
                startEnergyEvaluation();
            }
        };
        RedirectButton startElEvalOpenResult = new RedirectButton(page, "startElEvalOpenResult", "Start Electricity Evaluation and open Result Page") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onGET(OgemaHttpRequest req) {
                if (app.serviceAccess.basicEvalProvider == null) {
                    disable(req);
                } else {
                    enable(req);
                }
            }

            @Override
            public void onPrePOST(String data, OgemaHttpRequest req) {
                EvaluationInstance eval = startEnergyEvaluation();
                int counter = 0;
                while (!eval.isDone() || counter > 10) {
                    try {
                        Thread.sleep(1000);
                        counter++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                String url = "https://localhost:8443/de/iwes/tools/timeseries/analysis/results.html?provider=";
                url += app.serviceAccess.basicEvalProvider.id() + "&instance=" + eval.id();//"EnergyEvaluation_0";
                setUrl(url, req);

            }
        };

        gaRoButton1 = new Button(page, "basicSmartHomeButton3", "Start Stepped Sema Window Heat Eval") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onGET(OgemaHttpRequest req) {
                if (getSmartHomeProvider() == null) {
                    disable(req);
                } else {
                    enable(req);
                }
            }

            /*@Override
            public void onPrePOST(String data, OgemaHttpRequest req) {
                Executors.newSingleThreadExecutor().submit(
                		new GaRoEvalHelper.GaRoTestStarter<GaRoWinMultiResult>(
                		app.serviceAccess.garoWinEval, app.serviceAccess.gatewayParser,
                		startEndTotal[0], startEndTotal[1], ChronoUnit.MONTHS, "GaRoWinResult.json", null));
            }*/
            /*@Override
            public void onPrePOST(String data, OgemaHttpRequest req) {
                GaRoEvalHelper.performGenericMultiEvalOverAllData(RoomBaseEvalProvider.class,
                		app.serviceAccess.gatewayParser,
                		startEndTotal[0], startEndTotal[1],
                		//startEnd[1], startEnd[1]+(startEnd[2]-startEnd[1]),
                		ChronoUnit.DAYS,
                		null, true);
            }*/
            
            @Override
            public void onPrePOST(String data, OgemaHttpRequest req) {
                GaRoEvalHelperJAXB.performGenericMultiEvalOverAllData(WinHeatEvalProvider.class,
                		app.serviceAccess.gatewayParser,
                		startEndTotal[0], startEndTotal[1],
                		//startEnd[1], startEnd[1]+(startEnd[2]-startEnd[1])/3,
                		ChronoUnit.DAYS,
                		null, true);
             }

       };
        Button gaRoButton2 = new Button(page, "basicSmartHomeButton4", "Start Stepped Sema Window Heat Eval and Export CSV") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onGET(OgemaHttpRequest req) {
                if (getSmartHomeProvider() == null) {
                    disable(req);
                } else {
                    enable(req);
                }
            }

            @Override
            public void onPrePOST(String data, OgemaHttpRequest req) {
                Executors.newSingleThreadExecutor().submit(
                		new GaRoTestStarter<GaRoWinMultiResult>(
                		app.serviceAccess.garoWinEval, app.serviceAccess.gatewayParser,
                		//startEndTotal[0], startEndTotal[1], "GaRoWinResult.json",
                   		startEnd[1], startEnd[2], ChronoUnit.MONTHS, "GaRoWinResultRedFeb.json",
                		new GatewayDataExportUtil.CSVArchiveExporterGDE(app.gde)));
            }
        };

        Button gaRoButton3 = new Button(page, "basicSmartHomeButton5", "Start Stepped Sema Basic Eval and Export CSV") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onGET(OgemaHttpRequest req) {
                if (getSmartHomeProvider() == null) {
                    disable(req);
                } else {
                    enable(req);
                }
            }

            @Override
            public void onPrePOST(String data, OgemaHttpRequest req) {
                Executors.newSingleThreadExecutor().submit(
                		new GaRoTestStarter<GaRoBaseMultiResult>(
                		app.serviceAccess.baseMultiEval, app.serviceAccess.gatewayParser,
                		startEndTotal[0], startEndTotal[1], ChronoUnit.MONTHS, "GaRoBaseResult.json",
                		//startEnd[1], startEnd[2], "GaRoBaseResultRedFeb.json",
                		new GatewayDataExportUtil.CSVArchiveExporterGDE(app.gde)));
            }
        };

        gaRoButton4 = new Button(page, "basicSmartHomeButton6", "Start Stepped Comfort Temp Eval") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onGET(OgemaHttpRequest req) {
                if (getSmartHomeProvider() == null) {
                    disable(req);
                } else {
                    enable(req);
                }
            }

            @Override
            public void onPrePOST(String data, OgemaHttpRequest req) {
                GaRoEvalHelperJAXB.performGenericMultiEvalOverAllData(ComfortTemperatureGenericEvalProvider.class,
                		app.serviceAccess.gatewayParser,
                		startEndTotal[0], startEndTotal[1],
                		//startEnd[1], startEnd[1]+(startEnd[2]-startEnd[1])/3,
                		ChronoUnit.DAYS,
                		null, false);
             }
        };
        gaRoButton4ct_rt = new Button(page, "gaRoButton4ct_rt", "Start Stepped Comfort Temp Eval (temperature measurement based)") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onGET(OgemaHttpRequest req) {
                if (getSmartHomeProvider() == null) {
                    disable(req);
                } else {
                    enable(req);
                }
            }

            @Override
            public void onPrePOST(String data, OgemaHttpRequest req) {
                GaRoEvalHelperJAXB.performGenericMultiEvalOverAllData(ComfortTempRealBasedEvalProvider.class,
                		app.serviceAccess.gatewayParser,
                		startEndYear[0], startEndYear[1],
                		//startEnd[1], startEnd[1]+(startEnd[2]-startEnd[1])/3,
                		ChronoUnit.DAYS,
                		null, false);
             }
        };
        gaRoButton4ct_rt_overall = new Button(page, "gaRoButton4ct_rt_overall", "Start Overall Comfort Temp Eval (temperature measurement based)") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onGET(OgemaHttpRequest req) {
                if (getSmartHomeProvider() == null) {
                    disable(req);
                } else {
                    enable(req);
                }
            }

            @Override
            public void onPrePOST(String data, OgemaHttpRequest req) {
             	GaRoPreEvaluationProvider comfortTempProvider = 
            			new GaRoStdPreEvaluationProvider<GaRoMultiResultDeser, GaRoSuperEvalResult<GaRoMultiResultDeser>>(
            					GaRoSuperEvalResultDeser.class, FILE_PATH+"/ComfortTempRealBasedEvalProviderResult.json");
               GaRoEvalHelperJAXB.performGenericMultiEvalOverAllData(ComfortTempRB_OverallProvider.class,
                		app.serviceAccess.gatewayParser,
                		startEndTotal[0], startEndTotal[1],
                		//startEnd[1], startEnd[1]+(startEnd[2]-startEnd[1])/3,
                		ChronoUnit.DAYS,
                		null, false,
                		new GaRoPreEvaluationProvider[] {comfortTempProvider});
             }
        };
       gaRoButton4heattime = new Button(page, "basicSmartHomeButton4heattime", "Start Stepped Heating Time Eval") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onGET(OgemaHttpRequest req) {
                if (getSmartHomeProvider() == null) {
                    disable(req);
                } else {
                    enable(req);
                }
            }

            @Override
            public void onPrePOST(String data, OgemaHttpRequest req) {
                GaRoEvalHelperJAXB.performGenericMultiEvalOverAllData(HeatingTimeEvaluationProvider.class,
                		app.serviceAccess.gatewayParser,
                		startEndTotal[0], startEndTotal[1],
                		//startEnd[1], startEnd[1]+(startEnd[2]-startEnd[1])/3,
                		ChronoUnit.DAYS,
                		null, false);
             }
        };
        gaRoButton4winvalve = new Button(page, "basicSmartHomeButton4winvalve", "Start Stepped Window/Valve Time Eval") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onGET(OgemaHttpRequest req) {
                if (getSmartHomeProvider() == null) {
                    disable(req);
                } else {
                    enable(req);
                }
            }

            @Override
            public void onPrePOST(String data, OgemaHttpRequest req) {
                GaRoEvalHelperJAXB.performGenericMultiEvalOverAllData(WinHeatGenericEvalProvider.class,
                		app.serviceAccess.gatewayParser,
                		startEndTotal[0], startEndTotal[1],
                		//startEnd[1], startEnd[1]+(startEnd[2]-startEnd[1])/3,
                		ChronoUnit.DAYS,
                		null, false);
             }
        };
        gaRoButton4presence = new Button(page, "gaRoButton4presence", "Start presence temperature correlation evaluation") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onGET(OgemaHttpRequest req) {
                if (getSmartHomeProvider() == null) {
                    disable(req);
                } else {
                    enable(req);
                }
            }

            @Override
            public void onPrePOST(String data, OgemaHttpRequest req) {
             	GaRoPreEvaluationProvider comfortTempProvider = 
            			new GaRoStdPreEvaluationProvider<GaRoMultiResultDeser, GaRoSuperEvalResult<GaRoMultiResultDeser>>(
            					GaRoSuperEvalResultDeser.class, FILE_PATH+"/ComfortTempRealBasedEvalProviderResult.json");
               GaRoEvalHelperJAXB.performGenericMultiEvalOverAllData(PresenceCorrelationEvaluationProvider.class,
                		app.serviceAccess.gatewayParser,
                		startEndTotal[0], startEndTotal[1],
                		//startEnd[1]+8*24*3600*1000, startEnd[2],
                		ChronoUnit.DAYS,
                		null, false,
                		new GaRoPreEvaluationProvider[] {comfortTempProvider});
             }
        };
        gaRoButton4presenceBase = new Button(page, "gaRoButton4presenceBase", "Start presence base evaluation") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onGET(OgemaHttpRequest req) {
                if (getSmartHomeProvider() == null) {
                    disable(req);
                } else {
                    enable(req);
                }
            }

            @Override
            public void onPrePOST(String data, OgemaHttpRequest req) {
               GaRoEvalHelperJAXB.performGenericMultiEvalOverAllData(PresenceEvalProvider.class,
                		app.serviceAccess.gatewayParser,
                		//startEndTotal[0], startEndTotal[1],
                		startEnd[1]+600000, startEnd[1]+1*24*3600*1000,
                		ChronoUnit.DAYS,
                		null, false);
             }
        };

        
        gaRoButton5 = new Button(page, "basicSmartHomeButton7", "Start Outside Temperature Eval (outside-selection input recommended)") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onGET(OgemaHttpRequest req) {
                if (getSmartHomeProvider() == null) {
                    disable(req);
                } else {
                    enable(req);
                }
            }

            @Override
            public void onPrePOST(String data, OgemaHttpRequest req) {
                GaRoEvalHelperJAXB.performGenericMultiEvalOverAllData(OutsideTemperatureEvalProvider.class,
                		app.serviceAccess.gatewayParser,
                		startEndTotal[0], startEndTotal[1],
                		//startEnd[1], startEnd[1]+(startEnd[2]-startEnd[1])/3,
                		ChronoUnit.DAYS,
                		null, false);
             }
        };

        gaRoButton6 = new Button(page, "basicSmartHomeButton8", "Start Heat-loss Eval (requires comfort and outside temperature as input)") {
            private static final long serialVersionUID = 1L;

//            @Override
//            public void onGET(OgemaHttpRequest req) {
//                if (getSmartHomeProvider() == null) {
//                    disable(req);
//                } else {
//                    enable(req);
//                }
//            }

            @Override
            public void onPrePOST(String data, OgemaHttpRequest req) {
             	GaRoPreEvaluationProvider outTemp =
            			new GaRoStdPreEvaluationProvider<OutsideTemperatureMultiResult, GaRoSuperEvalResult<OutsideTemperatureMultiResult>>(
            					GaRoSuperEvalResultOut.class, FILE_PATH+"/OutsideTemperatureEvalProviderResult.json");
            	//OutsideTemperatureMultiResult.class
             	GaRoPreEvaluationProvider comfortTempProvider = 
            			new GaRoStdPreEvaluationProvider<GaRoMultiResultDeser, GaRoSuperEvalResult<GaRoMultiResultDeser>>(
            					GaRoSuperEvalResultDeser.class, FILE_PATH+"/ComfortTemperatureEvalProviderResult.json");
            	//GaRoMultiResult.class
             	GaRoEvalHelperJAXB.performGenericMultiEvalOverAllData(HeatLossEvalProvider.class,
                		app.serviceAccess.gatewayParser,
                		//startEndTotal[0], startEndTotal[1],
                		startEnd[1], startEnd[1]+(startEnd[2]-startEnd[1])/3,
                		ChronoUnit.DAYS,
                		null, false,
                		new GaRoPreEvaluationProvider[] {outTemp, comfortTempProvider});
             }
        };

        //init all widgets
        page.append(header);
        StaticTable table1 = new StaticTable(13, 2);
        page.append(table1);
        int i= 0;
        table1.setContent(0, 0, "Start Eval");
        table1.setContent(0, 1, startElEval);
        table1.setContent(1, 0, "Start Eval Plus:");
        table1.setContent(1, 1, startElEvalOpenResult);
        table1.setContent(2, 0, "Start Window Heat Evaluation:");
        table1.setContent(2, 1, gaRoButton1);
        table1.setContent(3, 0, "Start Window Heat Evaluation:");
        table1.setContent(3, 1, gaRoButton2);
        table1.setContent(4, 0, "Start Base Evaluation:");
        table1.setContent(4, 1, gaRoButton3);
        table1.setContent(5, 0, "Start CT Evaluation:");
        table1.setContent(5, 1, gaRoButton4);
        i = 6;
        table1.setContent(i, 0, "Start CT-RB Evaluation:");
        table1.setContent(i, 1, gaRoButton4ct_rt);
        i++;
        table1.setContent(i, 0, "Start CT-RB Overall Evaluation:");
        table1.setContent(i, 1, gaRoButton4ct_rt_overall);
        i++;
        table1.setContent(i, 0, "Start Heat Time Evaluation:");
        table1.setContent(i, 1, gaRoButton4heattime);
        i++;
        table1.setContent(i, 0, "Start WinValve Time Evaluation:");
        table1.setContent(i, 1, gaRoButton4winvalve);
        i++;
        table1.setContent(i, 0, "Start PresenceCor Evaluation:");
        table1.setContent(i, 1, gaRoButton4presence);
        i++;
        table1.setContent(i, 0, "Start OutTemp Evaluation:");
        table1.setContent(i, 1, gaRoButton5);
        i++;
        table1.setContent(i, 0, "Start HeatLoss Evaluation:");
        table1.setContent(i, 1, gaRoButton6);
    }

    public WidgetPage<?> getPage() {
        return page;
    }

    public EvaluationInstance startEnergyEvaluation() {
        final List<EvaluationInput> allItems = new ArrayList<>();

        // * Generate a dataList and an input for each RequiredInput of the eval
        // * service
        // *
        final List<TimeSeriesData> dataList = new ArrayList<>();
        final List<ReadOnlyTimeSeries> tsList = new ArrayList<>();

        
        // * Generate a timeSeriesData and and entry in the dataList for each
        // * evaluation input option that shall be used (for testing this is
        // * usually just one)
        // *
        String label = "electricity points (TL)";
        FloatResource source = app.appMan.getResourceAccess().getResource(
                "controlledTestGateways/controlledTestGateways_0/semaPayload/electricityPoints");
        ReadOnlyTimeSeries schedule = source.historicalData();
        TimeSeriesData timeSeriesData = new TimeSeriesDataImpl(schedule, label, label,
                InterpolationMode.STEPS); // TODO offset, factor, etc
        dataList.add(timeSeriesData);
        tsList.add(schedule);
        final EvaluationInput input = new EvaluationInputImpl(dataList);
        allItems.add(input);

        //we want to get all
        List<ResultType> requestedResults = app.serviceAccess.basicEvalProvider.resultTypes();
        //requestedResults.remove(BasicEvaluationProvider.COUNTER);

        Collection<ConfigurationInstance> configurations = MultiEvaluationUtils.addStartEndTime(tsList, null);

        return app.serviceAccess.evalManager.newEvaluation(app.serviceAccess.basicEvalProvider,
                allItems, requestedResults, configurations);
        //app.serviceAccess.electrcityEvalProvider.newEvaluation(allItems, requestedResults, configurations );

    }

    public MultiEvaluationProvider<?> getSmartHomeProvider() {
        if (app.serviceAccess.garoWinEval == null) {
            return null;
        }
        if (app.serviceAccess.gatewayParser == null) {
            return null;
        }
        return app.serviceAccess.garoWinEval;
    }

}
