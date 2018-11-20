Plotlyjs.prototype = new GenericWidget();
Plotlyjs.prototype.constructor = Plotlyjs;

function Plotlyjs( servletPath, widgetID ) {
	GenericWidget.call( this, servletPath, widgetID );
	this.plot = null;
	this.lastOptions = null;
	this.sendGET();
}

Plotlyjs.prototype.update = function( resp ) {
	const container = this.element.querySelector(":scope >div");
	// https://plot.ly/javascript/plotlyjs-function-reference/
	// https://plot.ly/javascript/reference/
//	https://plot.ly/javascript/axes/
	const data = resp.data; // array
	const series = resp.options.series;
	const lines = series.hasOwnProperty("lines") && series.lines.show;
	const bars = series.hasOwnProperty("bars") && series.bars.show;
	const points = series.hasOwnProperty("points") && series.points.show;
	const steps = lines && series.lines.hasOwnProperty("steps") && series.lines.steps;
	let type = "scatter";
	let mode0 = null;
	let shape = null;
	if (bars) {
		type = "bar";
		// TODO support stacked chart
		// https://plot.ly/javascript/bar-charts/
	}
	else {
		mode0 = lines && points ? "lines+markers" : points ? "markers" : "lines";
		if (steps)
			shape = "hv";
		else
			shape = "linear";
	}
	const mode = mode0;
	const plotData = data.map(entry => {
		const obj = entry.data;
		obj.type = type;
		if (mode !== null)
			obj.mode = mode;
		if (shape != null)
			obj.line = {shape: shape};
		return obj;
	});
	this.lastOptions = resp.options;
	resp.options.legend = {orientation: "h"};
	this.plot = Plotly.react(container, plotData, resp.options);
}

Plotlyjs.prototype.getPlotTypes = function(type) {
	let showBars=false;
	let showLines=false;
	let showSteps=false;
	let showPoints=false;
	switch(type) {
	case "barStacked":
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
	case "lineStacked":
	case "line":
		showLines=true;
		break;
	default:
		showLines = true;
		console.log("Invalid plot type",type);
	}
	return [showLines, showBars, showSteps, showPoints];
};

Plotlyjs.prototype.setPlotType = function(type) {
	if (this.plot == null)
		return;
	// TODO individual types; supported! https://plot.ly/javascript/plotlyjs-function-reference/#plotlyrestyle
	const arr = this.getPlotTypes(type);
	const showLines=arr[0];
	const showBars=arr[1];
	const showSteps=arr[2];
	const showPoints=arr[3];
	if (type === "barStacked")
		this.lastOptions.barmode = "stack";
	else
		delete this.lastOptions.barmode;
	const typeNew = showBars ? "bar" : "scatter"; // TODO other types
	const update = { type: typeNew };
	if (showPoints || showLines ) {
		const mode = showPoints && showLines ? "lines+markers" : showPoints ? "markers" : "lines";
		update.mode = mode;
		if (showSteps)
			update.line = {shape: "hv"};
		else
			update.line = {shape: "linear"};
	}
	// FIXME this removes the time attribute from the x-axis... probably a bug
	//Plotly.restyle(this.element.querySelector(":scope >div"), update);
	Plotly.update(this.element.querySelector(":scope >div"), update, this.lastOptions);

}