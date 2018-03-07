ProgressBar.prototype = new GenericWidget();
ProgressBar.prototype.constructor = ProgressBar;

function ProgressBar(servletPath, widgetID) {

    GenericWidget.call(this, servletPath, widgetID);
    this.sendGET();
}

ProgressBar.prototype.update = function (data) {
    
	var el = $(this.element).find(">progress").get(0); 
	
    if (data.hasOwnProperty("value"))
        el.value = data.value;
    else
    	delete el.value; // TODO check
    if (data.hasOwnProperty("min"))
    	el.min = data.min;
    if (data.hasOwnProperty("max"))
    	el.max = data.max;

};


