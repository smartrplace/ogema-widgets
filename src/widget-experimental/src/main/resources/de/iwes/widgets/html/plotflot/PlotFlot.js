PlotFlot.prototype = new GenericWidget();
PlotFlot.prototype.constructor = PlotFlot;

function PlotFlot( servletPath, widgetID ) {
	GenericWidget.call( this, servletPath, widgetID );
	this.plot = null;
	this.sendGET();
	var file = null;
	this.createCsv = function() {
		var data = this.getData();
	    if (file !== null) {
		    window.URL.revokeObjectURL(file);
		}
		var data = new Blob([data], {type: 'text/plain'});
		file = window.URL.createObjectURL(data);
		return file;
	}

}

PlotFlot.prototype.update = function( resp ) {

	// console.log("Flot widget updating... ",resp)

	if (!resp.hasOwnProperty("data")) {
		console.error("Unexpected data format");
		return;
	}
	var data = resp.data;
	var options = resp.options;
	options.xaxis = {
    		tickLength: 5,
            mode: resp.options.xtype
        };
	options.selection = { mode: "x" };
	if (options.legendOutsidePlot) {
		var cont = $(this.element).find("#legendContainer");
		options.legend = { container: cont };
	}

/*   var options = {
        xaxis: {
    		tickLength: 5,
            mode: "time"
        },
        selection: {
            mode: "x"
        }
    }; */

    var plotContainer = $("#" + this.widgetID + ".ogema-widget");
	var yaxis = undefined;
	var xaxis = undefined;
	if (resp.hasOwnProperty("yAxis"))
		yaxis = resp.yAxis;
	if (resp.hasOwnProperty("xAxis"))
		xaxis = resp.xAxis;
	var axisLabelY = plotContainer.find(">div>#label-y").get(0);
	if (yaxis) {
		axisLabelY.innerText=yaxis;
		axisLabelY.style.display="block";
	} else {
		axisLabelY.innerText="";
		axisLabelY.style.display="none";
	}
	var axisLabelX = plotContainer.find(">div>#label-x").get(0);
	if (xaxis) {
		axisLabelX.innerText=xaxis;
		axisLabelX.style.display="block";
	} else {
		axisLabelX.innerText="";
		axisLabelX.style.display="none";
	}

    var chart = plotContainer.find(">div>#chart");

	this.plot = $.plot(chart,data,options );
	var interactionsEnabled = resp.interactionsEnabled;
	if (!interactionsEnabled) return;
    var showOverview = resp.enableOverviewPlot && interactionsEnabled;
    var overview;
    var overviewElement = plotContainer.find("#overview");

    if (showOverview) {
    	overviewElement[0].style.display="block";
	    if (resp.hasOwnProperty("overviewHeight")) {
	    	overviewElement.height(resp.overviewHeight);
	    }
	    var overviewOptions = {
	            xaxis: {
	                    ticks: [],
	                    mode: resp.options.xtype
	            },
	            yaxis: {
	                    ticks: [],
	                    autoscaleMargin: 0.1
	            },
	            selection: {
	                    mode: "x"
	            }
	    };

	    overview = $.plot(overviewElement, data, overviewOptions);
    }
    else {
    	overviewElement[0].style.display="none";
    }
    var gw = this;
    chart.bind("plotselected", function (event, ranges) {

        // do the zooming
        $.each(gw.plot.getXAxes(), function(_, axis) {
                var opts = axis.options;
                opts.min = ranges.xaxis.from;
                opts.max = ranges.xaxis.to;
        });
        gw.plot.setupGrid();
        gw.plot.draw();
        gw.plot.clearSelection();

        // don't fire event on the overview to prevent infinite loop
        if (showOverview)
        	overview.setSelection(ranges, true);
    });

    if (showOverview) {
		overviewElement.bind("plotselected", function (event, ranges) {
		        gw.plot.setSelection(ranges);
		});
    }

}

// prints current data into a csv format
PlotFlot.prototype.getData = function() {
	var result = "";
	var data = this.plot.getData();  // array
	if (!data)
		return result;
	result = "t";
	for (var i=0;i<data.length;i++) {
		result += ";" + data[i].label;  // label should not contain a semicolon
	}
	result += "\n";
	var c = [];  // c[i] is the next time index to process for timeseries i  // -1 when timeseries i is finished
	var t = []; // t[i] is the next timestamp for timeseries i (the one with index c[i]) // Number.POSITIVE_INFINITY when timeseries is finished
	for (var i=0;i<data.length;i++) {
		c.push(0);
		t.push(data[i].datapoints.points[0]);
	}
	var line ="";
	while (Math.max.apply(null, c) >= 0) {
		var t0 = Math.min.apply(null, t);
		line = "" + t0;
		for (var i=0;i<data.length;i++) {
			line += ";";
			if (c[i] >= 0 && t[i] === t0) {
				line += data[i].datapoints.points[c[i]+1];
				if (c[i]+1 === data[i].datapoints.points.length-1) {
					c[i] = -1;
					t[i] = Number.POSITIVE_INFINITY;
				}
				else {
					c[i] = c[i] + 2;
					t[i] = data[i].datapoints.points[c[i]];
				}
			}
		}
		line += "\n";
		result += line;
	}
	return result;
}

// nicer solution would involve the download attribute, but it is not widely supported // <a href="your_link" download> file_name </a>
// see http://stackoverflow.com/questions/3749231/download-file-using-javascript-jquery
PlotFlot.prototype.download = function() {
	// $(this.element).find("#downloadFrame").get(0).src = this.createCsv(); // not working
	var a = $(this.element).find("#download").get(0);
	a.href = this.createCsv();
	try {
		a.download = "data.csv";  // HTML5 attribute
	} catch (e) {}
	a.click();
}

/**
 * Set line type client-side
 */
PlotFlot.prototype.setPlotType = function(type) {
//	console.log("Set plot type called with arguments ",type);
	if (this.plot == null)
		return;
	// TODO individual types?
	var showBars=false;
	var showLines=false;
	var showSteps=false;
	var showPoints=false;
	switch(type) {
	case "bar":
	case "barStacked": // not supported
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
	case "lineStacked": // not supported
		showLines=true;
		break;
	default:
		console.log("Invalid plot type",type);
		return;
	}
	this.plot.getData().forEach(function(data){
		data.bars.show = showBars;
		data.lines.show = showLines;
		data.lines.steps = showSteps;
		data.points.show = showPoints;
	});
	this.plot.draw();
}

/**
 * Set height client-side
 */
PlotFlot.prototype.setHeight = function(nrPixels) {
//	console.log("setting height:",nrPixels);
	if (this.plot == null)
		return;
	var data = this.plot.getData();
	var options = this.plot.getOptions();
	this.plot.destroy();
	var plotContainer = $("#" + this.widgetID + ".ogema-widget");
	var axisLabelY = plotContainer.find(">div>#label-y").get(0).innerText;
	plotContainer.find(">div>#chart").height(nrPixels);
	var resp = {data: data, options: options};
	if (axisLabelY)
		resp.yAxis=axisLabelY;
	var axisLabelX = plotContainer.find(">div>#label-x").get(0).innerText;
	if (axisLabelX)
		resp.xAxis=axisLabelX;
	this.update(resp);
}
