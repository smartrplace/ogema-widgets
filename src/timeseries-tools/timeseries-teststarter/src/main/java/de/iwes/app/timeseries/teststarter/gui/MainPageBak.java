package de.iwes.app.timeseries.teststarter.gui;

import java.io.FileOutputStream;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import org.ogema.core.model.simple.FloatResource;
import org.ogema.core.timeseries.InterpolationMode;
import org.ogema.core.timeseries.ReadOnlyTimeSeries;

import de.iwes.app.timeseries.teststarter.TeststarterController;
import de.iwes.timeseries.eval.api.DataProviderType;
import de.iwes.timeseries.eval.api.EvaluationInput;
import de.iwes.timeseries.eval.api.EvaluationInstance;
import de.iwes.timeseries.eval.api.ResultType;
import de.iwes.timeseries.eval.api.TimeSeriesData;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance;
import de.iwes.timeseries.eval.api.configuration.ConfigurationInstance.DateConfiguration;
import de.iwes.timeseries.eval.api.configuration.StartEndConfiguration;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInputGeneric;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationInstance;
import de.iwes.timeseries.eval.api.extended.MultiEvaluationProvider;
import de.iwes.timeseries.eval.api.extended.util.AbstractSuperMultiResult;
import de.iwes.timeseries.eval.api.extended.util.MultiEvaluationUtils;
import de.iwes.timeseries.eval.api.helper.EvalHelperExtended;
import de.iwes.timeseries.eval.api.semaextension.GatewayDataExportUtil;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationInputImpl;
import de.iwes.timeseries.eval.base.provider.utils.EvaluationUtils;
import de.iwes.timeseries.eval.base.provider.utils.TimeSeriesDataImpl;
import de.iwes.timeseries.eval.garo.api.base.GaRoDataType;
import de.iwes.timeseries.eval.garo.api.base.GaRoMultiEvaluationInput;
import de.iwes.timeseries.eval.garo.api.base.GaRoTimeSeriesId;
import de.iwes.timeseries.eval.garo.api.jaxb.GaRoMultiEvalDataProviderJAXB;
import de.iwes.timeseries.multi.provider.garoWinHeat.GaRoWinMultiResult;
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
public class MainPageBak {

    public final long UPDATE_RATE = 5 * 1000;
    private final WidgetPage<?> page;
    private final String FILE_PATH = System.getProperty("de.iwes.tools.timeseries-multieval.resultpath", "../evaluationresults");
    private final TeststarterController app;
    //public final RedirectButton basicSmartHomeButton2;
    //private final Button basicSmartHomeButton3;
    public final Button gaRoButton1;

    public MainPageBak(final WidgetPage<?> page, final TeststarterController app) {
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
        /*EvalOpenButton basicSmartHomeButton = new EvalOpenButton(page, "basicSmartHomeButton", "Start Basic Sema Eval") {
            private static final long serialVersionUID = 1L;

            // FIXME fails with other data, in particular the one generated by the test data generation app!
            @Override
            protected EvaluationInstance startEvaluation() {
                //From 01.01.2017 00:00:00
                //long startTime = 1483228800000l + 2*60*60000;
                //From 01.03.2017 00:00:00
                long startTime = 1488326400000l + 2 * 60 * 60000;
                // to 01.02.2017 00:00:00
                //long endTime = 1485907200000l + 2*60*60000;
                // to 01.04.2017 00:00:00
                long endTime = 1491004800000l + 2 * 60 * 60000;
                // to 01.05.2017 00:00:00
                //long endTime = 1493596800000l + 2*60*60000;
                return MainPage.this.startSmarHomeEvaluation(startTime, endTime, true);
            }

            @Override
            protected EvaluationProvider getProvider() {
                return getSmartHomeProvider();
            }

        };
        class EvaluationTestStarter implements Callable<Void> {

            boolean doExportCSV;

            public EvaluationTestStarter(boolean doExportCSV) {
                super();
                this.doExportCSV = doExportCSV;
            }

            @Override
            public Void call() throws Exception {
                //first two weeks
                //long[] startEnd = {1483228800000l, 1483833600000l, 1484438400000l};
                //first two months
                //long[] startEnd = {1483228800000l, 1485907200000l, 1488326400000l};
                long[] startEnd = {1483228800000l, 1485907200000l, 1488326400000l,
                    1491004800000l, 1493596800000l, 1496275200000l};
                long[] startEndTotal = {startEnd[0] + 600000, startEnd[startEnd.length - 1]};
                Map<DataType, SpecialGaRoEvalResult> resultsTotal = new HashMap<>();
                List<SuperEvalResultTotal> serTotal = new ArrayList<>();
                Map<DataType, SpecialGaRoEvalResult> resultsPerGwTotal = new HashMap<>();
                Set<TimeSeriesEvaluated> timeSeriesEvalAll = new HashSet<>();

                for (int i = 0; i < startEnd.length - 1; i++) {
                    long startTime = startEnd[i] + 60 * 60000;
                    long endTime = startEnd[i + 1] + 60 * 60000;
                    BasicSmartHomeFieldTestEvaluation eval
                            = MainPage.this.startSmarHomeEvaluation(startTime, endTime, false);

                    for (Entry<DataType, SpecialGaRoEvalResult> entry : eval.results.entrySet()) {
                        BasicSmartHomeFieldTestEvaluation.reportSingleTimeSeriesEvaluation(entry.getValue(),
                                EvalHelperExtended.getOrCreateResult(resultsTotal, entry.getKey()));
                    }
                    for (Entry<DataType, SpecialGaRoEvalResult> entry : eval.resultsPerGw.entrySet()) {
                        BasicSmartHomeFieldTestEvaluation.reportSingleTimeSeriesEvaluationMaxMode(entry.getValue(),
                                EvalHelperExtended.getOrCreateResult(resultsPerGwTotal, entry.getKey()));
                    }
                    SuperEvalResultTotal resTotal = new SuperEvalResultTotal();
                    resTotal.results = eval.results;
                    resTotal.resultsPerGw = eval.resultsPerGw;
                    resTotal.resultsPerRoomType = eval.resultsPerRoomType;
                    resTotal.ser = eval.ser;
                    resTotal.startEnd = eval.startEnd;
                    serTotal.add(resTotal);
                    timeSeriesEvalAll.addAll(eval.timeSeriesEvaluated);
                    
                    System.out.printf("evaluation runs done: %d/%d%n", i+1, startEnd.length - 1);
                }
                
                GaRoEvalUtil.exportSuperResultCSV(BasicSmartHomeFieldTestEvaluation.evalOutputPath,
                        serTotal, startEndTotal);
                //EvalHelperExtended.exportResultCSV(BasicSmartHomeFieldTestEvaluation.evalOutputPath,
                //		startEndTotal, serTotal, resultsTotal, resultsPerGwTotal);

                Map<String, List<String>> timeSeriesSelection = new HashMap<>();
                timeSeriesEvalAll.forEach(tse -> {
                    timeSeriesSelection.computeIfAbsent(tse.gwId,
                            __ -> {
                                return new ArrayList<>();
                            }).add(tse.timeSeriesId);
                });

                if (doExportCSV) {
                    System.out.printf("evaluation done, exporting %d time series%n", timeSeriesEvalAll.size());
                    System.out.printf("selected %s time series from %s gateways%n", timeSeriesEvalAll.size(), timeSeriesSelection.size());
                    //writeGatewayDataArchive(timeSeriesSelection, null, null, Period.ZERO, null);
                    String outputFileName = "evaluation-output-test.zip";
                    try (FileOutputStream output = new FileOutputStream(outputFileName)) {
                        DateTime start = new DateTime("2017-04-01T00:00:00Z");
                        DateTime end = new DateTime("2017-06-01T00:00:00Z");
                        Period step = new Period("PT60s");
                        app.gde.writeGatewayDataArchive(timeSeriesSelection.keySet(),
                            (gwId, rdId) -> {return timeSeriesSelection.getOrDefault(gwId, Collections.EMPTY_LIST).contains(rdId);},
                            start, end, step, output);
                        Path of = Paths.get(outputFileName);
                        System.out.printf("export done, %dkb in %s%n", Files.size(of)/1024, of);
                    }
                } else {
                    System.out.printf("evaluation done, evaluated %d time series%n", timeSeriesEvalAll.size());                	
                }
                return null;
            }
        }

        basicSmartHomeButton2 = new RedirectButton(page, "basicSmartHomeButton2", "Start Stepped Sema Eval") {
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
                Executors.newSingleThreadExecutor().submit(new EvaluationTestStarter(false));
            }
        };
        basicSmartHomeButton3 = new Button(page, "basicSmartHomeButton3", "Start Stepped Sema Eval and Export CSV") {
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
                Executors.newSingleThreadExecutor().submit(new EvaluationTestStarter(true));
            }
        };
        */
        class GaRoTestStarter implements Callable<Void> {

            boolean doExportCSV;

            public GaRoTestStarter(boolean doExportCSV) {
                super();
                this.doExportCSV = doExportCSV;
            }

            @Override
            public Void call() throws Exception {
            	try {
                //first two weeks
                //long[] startEnd = {1483228800000l, 1483833600000l, 1484438400000l};
                //first two months
                //long[] startEnd = {1483228800000l, 1485907200000l, 1488326400000l};
                long[] startEnd = {1483228800000l, 1485907200000l, 1488326400000l,
                    1491004800000l, 1493596800000l, 1496275200000l};
                long[] startEndTotal = {startEnd[0] + 600000, startEnd[startEnd.length - 1]};
                //Map<GaRoDataType, SpecialGaRoEvalResult> resultsTotal = new HashMap<>();
                //List<SuperEvalResultTotal> serTotal = new ArrayList<>();
                //Map<DataType, EvalResult> resultsPerGwTotal = new HashMap<>();
                //Set<GaRoTimeSeriesId> timeSeriesEvalAll = new HashSet<>();

                long startTime = startEndTotal[0] + 60 * 60000;
                long endTime = startEndTotal[1] + 60 * 60000;
                MultiEvaluationInstance<GaRoWinMultiResult> eval
                	= MainPageBak.this.startGaRoEvaluation(startTime, endTime);

                /*for (Entry<DataType, EvalResult> entry : eval.results.entrySet()) {
                    BasicSmartHomeFieldTestEvaluation.reportSingleTimeSeriesEvaluation(entry.getValue(),
                            EvalHelperExtended.getOrCreateResult(resultsTotal, entry.getKey()));
                }
                for (Entry<DataType, EvalResult> entry : eval.resultsPerGw.entrySet()) {
                    BasicSmartHomeFieldTestEvaluation.reportSingleTimeSeriesEvaluationMaxMode(entry.getValue(),
                            EvalHelperExtended.getOrCreateResult(resultsPerGwTotal, entry.getKey()));
                }
                SuperEvalResultTotal resTotal = new SuperEvalResultTotal();
                resTotal.results = eval.results;
                resTotal.resultsPerGw = eval.resultsPerGw;
                resTotal.resultsPerRoomType = eval.resultsPerRoomType;
                resTotal.ser = eval.ser;
                resTotal.startEnd = eval.startEnd;
                serTotal.add(resTotal);*/
                //timeSeriesEvalAll.addAll(eval.getResult().timeSeriesEvaluated);
                Set<GaRoTimeSeriesId> timeSeriesEvalAll = new HashSet<>();
                AbstractSuperMultiResult<GaRoWinMultiResult> result = eval.getResult();
                for(GaRoWinMultiResult res: result.intervalResults) {
                	timeSeriesEvalAll.addAll(res.timeSeriesEvaluated);
                }
                
                System.out.printf("evaluation runs done: %d/%d%n", 1, startEnd.length - 1);

                //GaRoEvalUtil.exportSuperResultCSV(BasicSmartHomeFieldTestEvaluation.evalOutputPath,
                //        serTotal, startEndTotal);
                
                MultiEvaluationUtils.exportToJSONFile(FILE_PATH+"/GaRoWinResult.json", result);
                if (doExportCSV) {
                	GatewayDataExportUtil.writeGatewayDataArchive(new FileOutputStream(FILE_PATH+"/evaluation-output-test.zip"),
                			timeSeriesEvalAll, startEndTotal[0], startEndTotal[1], app.gde);
                } else {
                    System.out.printf("evaluation done, evaluated %d time series%n", result.intervalResults.size(), timeSeriesEvalAll.size());                	
                }
                return null;
            	} catch(Exception e) {
            		e.printStackTrace();
            		throw e;
            	}
            }
        }
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

            @Override
            public void onPrePOST(String data, OgemaHttpRequest req) {
                Executors.newSingleThreadExecutor().submit(new GaRoTestStarter(false));
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
                Executors.newSingleThreadExecutor().submit(new GaRoTestStarter(true));
            }
        };

        //init all widgets
        page.append(header);
        StaticTable table1 = new StaticTable(5, 2);
        page.append(table1);
        table1.setContent(0, 0, "Start Eval");
        table1.setContent(0, 1, startElEval);
        table1.setContent(1, 0, "Start Eval Plus:");
        table1.setContent(1, 1, startElEvalOpenResult);
        //table1.setContent(2, 0, "Start Sema Basic Evaluation:");
        //table1.setContent(2, 1, basicSmartHomeButton);
        //table1.setContent(3, 0, "Start Multi-Sema Basic Evaluation:");
        //table1.setContent(3, 1, basicSmartHomeButton2);
        //table1.setContent(4, 0, "Start Multi-Sema Basic Evaluation:");
        //table1.setContent(4, 1, basicSmartHomeButton3);
        table1.setContent(2, 0, "Start Window Heat Evaluation:");
        table1.setContent(2, 1, gaRoButton1);
        table1.setContent(2, 0, "Start Window Heat Evaluation:");
        table1.setContent(2, 1, gaRoButton2);
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

        Collection<ConfigurationInstance> configurations = addStartEndTime(tsList, null);

        return app.serviceAccess.evalManager.newEvaluation(app.serviceAccess.basicEvalProvider,
                allItems, requestedResults, configurations);
        //app.serviceAccess.electrcityEvalProvider.newEvaluation(allItems, requestedResults, configurations );

    }

    /*public BasicSmartHomeFieldTestEvaluation startSmarHomeEvaluation(long startTime, long endTime, boolean startSeparateThread) {
        //we want to get all
        List<ResultType> requestedResults = app.serviceAccess.smartHomeEval.resultTypes();
        //requestedResults.remove(BasicEvaluationProvider.COUNTER);

        Collection<ConfigurationInstance> configurations = addStartEndTime(startTime, endTime, null);

        if (startSeparateThread) {
            return (BasicSmartHomeFieldTestEvaluation) app.serviceAccess.evalManager.newEvaluationSelfOrganized(app.serviceAccess.smartHomeEval,
                    null, requestedResults, configurations);
        } else {
            return app.serviceAccess.smartHomeEval.newEvaluation(null, requestedResults, configurations, false);
        }
        //app.serviceAccess.electrcityEvalProvider.newEvaluation(allItems, requestedResults, configurations );

    }*/
    public MultiEvaluationInstance<GaRoWinMultiResult> startGaRoEvaluation(long startTime, long endTime) {

        Collection<ConfigurationInstance> configurations = EvalHelperExtended.addStartEndTime(startTime, endTime, null);

        List<DataProviderType> providerTypes = app.serviceAccess.garoWinEval.inputDataTypes();
        GaRoMultiEvalDataProviderJAXB dataProvider = new GaRoMultiEvalDataProviderJAXB(app.serviceAccess.gatewayParser);
        GaRoMultiEvaluationInput tempSensIn = new GaRoMultiEvaluationInput(
        		providerTypes.get(0), dataProvider, GaRoDataType.TemperatureMeasurementRoomSensor, null, null);
        GaRoMultiEvaluationInput windowIn = new GaRoMultiEvaluationInput(
        		providerTypes.get(1), dataProvider, GaRoDataType.WindowOpen, null, null);
        GaRoMultiEvaluationInput valveIn = new GaRoMultiEvaluationInput(
        		providerTypes.get(2), dataProvider, GaRoDataType.ValvePosition, null, null);
        List<MultiEvaluationInputGeneric> input = new ArrayList<>();
        input.add( tempSensIn);
        input.add( windowIn);
        input.add( valveIn);
 
        MultiEvaluationInstance<GaRoWinMultiResult> result = app.serviceAccess.garoWinEval.newEvaluation(input,
        		configurations, ChronoUnit.MONTHS, null);
        result.execute();
        return result;
        //app.serviceAccess.electrcityEvalProvider.newEvaluation(allItems, requestedResults, configurations );

    }

    /**
     * Add the mandatory start/end time information to the list of
     * configurations or create list with this information if not yet existing
     *
     * @param tsList list of input time series from which start/end shall be
     * determined
     * @param configurations may be null if no other configurations shall be set
     * for the evaluation
     * @return list of configurations with start and end time
     */
    public Collection<ConfigurationInstance> addStartEndTime(final List<ReadOnlyTimeSeries> tsList, Collection<ConfigurationInstance> configurations) {
        if (configurations == null) {
            configurations = new ArrayList<>();
        }
        long startTime = EvaluationUtils.getDefaultStartEndTimeForInput(tsList, true);
        ConfigurationInstance config = new DateConfiguration(startTime, StartEndConfiguration.START_CONFIGURATION);
        configurations.add(config);
        long endTime = EvaluationUtils.getDefaultStartEndTimeForInput(tsList, false);
        config = new DateConfiguration(endTime, StartEndConfiguration.END_CONFIGURATION);
        configurations.add(config);
        return configurations;
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
