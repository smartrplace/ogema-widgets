PlotC3.prototype = new GenericWidget();
PlotC3.prototype.constructor = PlotC3;

// TODO
//  * supports individual line types
//  * supports stacked lines
// http://c3js.org/samples/chart_combination.html
function PlotC3( servletPath, widgetID ) {
	GenericWidget.call( this, servletPath, widgetID );
	this.chart = this.element.querySelector("div");
	this.plot = null;
	this.type = null;
	this.lastResp = null;
	this.ids = [];
	this.sendGET();
}

PlotC3.prototype.update = function( resp ) {

	if (!resp.hasOwnProperty("data")) {
		console.error("Unexpected data format");
		return;
	}
	this.type = resp.data.type;
	// FIXME
	console.log("C3 chart data: ", resp);

	 // FIXME this is often a bad choice... but the C3 built-in timeseries type can only be applied if all
	// timeseries have the same t-values -> very annoying
	const format = t => new Date(t).toISOString();
	const x = {
		tick : {
			fit: true,
			format: format,
			multiline: true,
			culling: {max: 8}
		}
	};
	if (!resp.hasOwnProperty("axis"))
		resp.axis = {};
	resp.axis.x = x;
	if (this.plot !== null)
		this.plot.destroy(); // seems to be required... there is no safe way to update the line type, for instance
//	if (this.plot === null) {
		resp.bindto=this.chart;
		this.plot = c3.generate( resp ); 		// for required format see http://c3js.org/samples/chart_combination.html
//	}
//	else {
//		// TODO
//		this.plot.unload({ids: this.ids});
//		this.ids = [];
//		this.plot.load( resp );
//	}
	var thisRef = this;
	Object.keys(resp.data.xs).forEach(function(key) {
		thisRef.ids.push(key);
		thisRef.ids.push(resp.data.xs[key]);
	});
	this.lastResp = resp; // FIXME expensive to store the data
}

PlotC3.prototype.setPlotType = function(type) {
	if (this.plot === null)
		return;
	// TODO individual types supported! http://c3js.org/samples/chart_combination.html
	const lines = type.indexOf("line") >= 0;
	const bars = !lines && type.indexOf("bar") >= 0;
	const steps = !lines && !bars && type.indexOf("steps") >= 0;
	const points = !bars && !lines && !steps;
	const matches = lines && this.type === "line"
		|| bars && this.type === "bar"
		|| points && this.type === "scatter"
		|| steps && this.type === "step";
	const showPoints = type.toLowerCase().indexOf("point") >= 0;
//	if (!matches) { // TODO
		this.plot.destroy();
		const resp = this.lastResp;
		const newType = lines ? "line" : bars ? "bar" : steps ? "step" : "scatter";
		resp.data.type = newType;
		Object.keys(resp.data.types).forEach(key => resp.data.types[key] = newType);
		resp.point.show = showPoints;
		this.plot = c3.generate( resp );
//	}

}
