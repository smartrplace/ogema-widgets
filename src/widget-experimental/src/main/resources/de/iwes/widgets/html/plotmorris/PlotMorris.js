PlotMorris.prototype = new GenericWidget();
PlotMorris.prototype.constructor = PlotMorris;

function PlotMorris( servletPath, widgetID ) {
	GenericWidget.call( this, servletPath, widgetID );
	this.sendGET();	
}

PlotMorris.prototype.update = function( resp ) {
	
	if (!resp.hasOwnProperty("data")) {
		console.error("Unexpected data format");
		return;
	}
	
//	resp.bindTo="#" + this.widgetID + "#chart"; // not working 
	// FIXME
	console.log("Morris chart data: ", resp);
	var newId =  this.widgetID + "_chart";
	document.getElementById(this.widgetID).innerHTML="<div id=\""+ newId + "\"></div>"; // ugly, overwrites old chart
	resp.element = document.getElementById(newId);
	var interactionsEnabled = resp.interactionsEnabled;
	if (!interactionsEnabled) {
		resp.hideHover="always";
	}
	var chart = Morris.Line( resp ); 		// for required format see http://c3js.org/samples/chart_combination.html
//	$("#" + this.widgetID).find("#chart").append(chart.element);
	
}
