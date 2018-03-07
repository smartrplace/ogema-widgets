PlotC3.prototype = new GenericWidget();
PlotC3.prototype.constructor = PlotC3;

function PlotC3( servletPath, widgetID ) {
	GenericWidget.call( this, servletPath, widgetID );
	this.chart = undefined;
	this.ids = [];
	this.sendGET();	
}

PlotC3.prototype.update = function( resp ) {
	
	if (!resp.hasOwnProperty("data")) {
		console.error("Unexpected data format");
		return;
	}
	
//	resp.bindTo="#" + this.widgetID + "#chart"; // not working 
	// FIXME
	console.log("C3 chart data: ", resp);
	var newId =  this.widgetID + "_chart";
//	if (typeof this.chart !== "undefined")
//		this.chart.destroy();
	if (typeof this.chart === "undefined") {
		document.getElementById(this.widgetID).innerHTML="<div id=\""+ newId + "\"></div>"; // ugly, overwrites old chart
		resp.bindto="#" + newId;
		this.chart = c3.generate( resp ); 		// for required format see http://c3js.org/samples/chart_combination.html
	}
	else {
		this.chart.unload({ids: this.ids});
		this.ids = [];
		// this.chart.destroy(); // TODO how to reload widget?
		this.chart.load( resp );
	}
//	$("#" + this.widgetID).find("#chart").append(chart.element);
	var thisRef = this;
	Object.keys(resp.data.xs).forEach(function(key) {
		thisRef.ids.push(key);
		thisRef.ids.push(resp.data.xs[key]);
	});
	
}
