package de.iwes.widgets.reswidget.schedulecsvdownload;

import org.ogema.core.security.WebAccessManager;
import org.ogema.humread.valueconversion.SchedulePresentationData;
import org.ogema.model.chartexportconfig.ChartExportConfig;

import de.iwes.widgets.api.widgets.WidgetPage;

@SuppressWarnings("serial")
public class ScheduleCsvDownloadSchedPres extends ScheduleCsvDownload<SchedulePresentationData> {

	public ScheduleCsvDownloadSchedPres(WidgetPage<?> page, String id, WebAccessManager wam, ChartExportConfig configRes) {
		super(page, id, wam, configRes);
	}

}
