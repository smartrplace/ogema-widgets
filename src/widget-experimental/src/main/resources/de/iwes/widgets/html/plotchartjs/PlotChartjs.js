PlotChartjs.prototype = new GenericWidget();
PlotChartjs.prototype.constructor = PlotChartjs;

function PlotChartjs( servletPath, widgetID ) {
	GenericWidget.call( this, servletPath, widgetID );
	this.canvas = this.element.querySelector("canvas");
	this.ctx = this.canvas.getContext("2d");
	this.plot = null;
	this.type = null;
	this.lastOptions = null;
	this.sendGET();
}

PlotChartjs.prototype.update = function( resp ) {
	const data = resp.data;
	if (this.plot !== null) {
		this.plot.destroy();
	}
	this.plot = new Chart(this.ctx, resp);
	this.type = resp.type;
}

PlotChartjs.prototype.setPlotType = function(type) {
	if (this.plot === null)
		return;
	let showBars=false;
	let showLines=false;
	let showSteps=false;
	let showPoints=false;
	let stacked=false;
	switch(type) {
	case "bar":
		showBars=true;
		break;
	case "points":
		showPoints=true;
		break;
	case "linePoints":
		showPoints=true;
		showLines=true;
		break;
	case "steps":
		showSteps=true;
		showLines=true;
		break;
	case "line":
		showLines=true;
		break;
	case "lineStacked":
		showLines=true;
		stacked=true;
		break;
	case "barStacked":
		showBars=true;
		stacked=true;
		break;
	default:
		console.log("Invalid plot type",type);
		return;
	}
	const typeNew = showLines ? "line" : showBars ? "bar" : "scatter"; // TODO other types
	const oldType = this.type;
	const options = this.plot.options;
	if (stacked) {
		if (!options.hasOwnProperty("scales"))
			options.scales= {};
		if (!options.scales.hasOwnProperty("yAxes"))
			options.scales.yAxes = [{stacked : true}];
		else
			options.scales.yAxes.forEach(axis => axis.stacked = true);
	} else if (options.hasOwnProperty("scales") && options.scales.hasOwnProperty("yAxes")) {
		options.scales.yAxes.forEach(axis => axis.stacked = false);
	}
	if (oldType !== typeNew) {
		const data = this.plot.data;
		this.plot.destroy();
		data.datasets.forEach(data => {
			data.steppedLine = showSteps;
			data.showLine = showLines;
		});
		this.plot = new Chart(this.ctx, {type: typeNew, data, options});
	} else {
		this.plot.type = typeNew;
		this.plot.data.datasets.forEach(data => {
			data.steppedLine = showSteps;
			data.showLine = showLines;
		});
		this.plot.update();
	}
	this.type = typeNew;

}