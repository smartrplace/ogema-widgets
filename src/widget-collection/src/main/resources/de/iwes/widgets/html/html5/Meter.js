Meter.prototype = new GenericWidget();
Meter.prototype.constructor = Meter;


function Meter(servletPath, widgetID) {

    GenericWidget.call(this, servletPath, widgetID);
    this.sendGET();
}

Meter.prototype.update = function (data) {
    
	var el = $(this.element).find(">meter").get(0); 
	
    if (data.hasOwnProperty("value"))
        el.value = data.value;
    else
    	delete el.value; // TODO check
    if (data.hasOwnProperty("min"))
    	el.min = data.min;
    if (data.hasOwnProperty("max"))
    	el.max = data.max;
    if (data.hasOwnProperty("low"))
    	el.low = data.low;
    if (data.hasOwnProperty("high"))
    	el.high = data.high;
    if (data.hasOwnProperty("optimum"))
    	el.optimum = data.optimum;
  

};


