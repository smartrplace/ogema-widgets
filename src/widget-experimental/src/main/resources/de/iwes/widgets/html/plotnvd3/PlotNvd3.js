PlotNvd3.prototype = new GenericWidget();
PlotNvd3.prototype.constructor = PlotNvd3;

/**
 * TODO
 *  - line type (poorly supported, but at least bars shoud be possible: http://nvd3.org/examples/multiBar.html; points with tricks... )
 *  - set height
 *  - download csv data
 *  - ...
 * 
 * @param servletPath
 * @param widgetID
 * @returns
 */
function PlotNvd3( servletPath, widgetID ) {
	GenericWidget.call( this, servletPath, widgetID );
	this.plot = null;
	this.chart = null;
	this.sendGET();	
}

// TODO update case
PlotNvd3.prototype.update = function( resp ) {
	
	// FIXME
	console.log("Nvd3 widget updating... ",resp)
	
	if (!resp.hasOwnProperty("data")) {
		console.error("Unexpected data format");
		return;
	}
	var data = resp.data;	
//    var chartDiv = $("#" + this.widgetID + ".ogema-widget").find("#chart");

    // see http://nvd3.org/examples/line.html
    this.chart = nv.models.lineChart()
	    .margin({left: 100})  //Adjust chart margins to give the x-axis some breathing room.
	    .useInteractiveGuideline(true)  //We want nice looking tooltips and a guideline! // FIXME disable in case of many points?
	    // FIXME not available
//	    .transitionDuration(350)  //how fast do you want the lines to transition?
	    .showLegend(true)       //Show the legend, allowing users to turn on/off line series.
	    .showYAxis(true)        //Show the y-axis
	    .showXAxis(true);        //Show the x-axis

    this.chart.xAxis     //Chart x-axis settings
//	    .axisLabel('Time')
//	    .tickFormat(function(d) { return d3.time.format('%b %d')(new Date(d)); })
	    .tickFormat(function(d) {
	    	return d3.time.format('%y-%m-%d %H:%M')(new Date(d))
	    });

   this.chart.xScale(d3.time.scale()); //fixes misalignment of timescale with line graph

   var yaxis = undefined;
	if (resp.hasOwnProperty("yAxis")) {
		this.chart.yAxis     //Chart y-axis settings
	    	.axisLabel(resp.yAxis) // Voltage (V)
//	    .tickFormat(d3.format('.02f')); // done automatically
	}
    
//    d3.select('#chart svg') 
//    d3.select(chartDiv)    //Select the <svg> element you want to render the chart in.   
//	    .datum(data)         //Populate the <svg> element with chart data...
//	    // FIXME throws exception
//	    .call(chart); 
//    
    d3.select("#" + this.widgetID + ".ogema-widget>svg")    //Select the <svg> element you want to render the chart in.   
	    .datum(data)         //Populate the <svg> element with chart data...
	    .call(this.chart); 
    var gw = this;
    nv.utils.windowResize(function() { gw.chart.update() });
}
