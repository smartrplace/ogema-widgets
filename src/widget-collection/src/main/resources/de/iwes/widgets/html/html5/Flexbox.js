Flexbox.prototype = new GenericWidget();
Flexbox.prototype.constructor = Flexbox;


function Flexbox(servletPath, widgetID) {

    GenericWidget.call(this, servletPath, widgetID);
    this.lastData = undefined;
    this.sendGET();
}

/*
 * TODO this is implemented in a very inefficient way: destroy all widgets, then recreate them -> to be improved 
 * First step: if items did not change at all then we do not recreate them
 */
Flexbox.prototype.update = function (data, oldRequest, subwidgetsToBeRemoved) {
        var isUnchanged = false;     
        if (this.lastData !== undefined && data.items && this.lastData.items && data.items.length === this.lastData.items.length) {
        	isUnchanged = true;
		for (var i=0; i < data.items.length; i++) {
			if (data.items[i] !== this.lastData.items[i]) {
				isUnchanged = false;
				break;
			}
		}
        }    
        this.lastData = data;
        if (isUnchanged)
        	return false;
	var el = $(this.element).find(">div"); 	
	var oldSubwidgets = el.find(".ogema-widget"); // should be fine... finds even recursive subwidgets
	for (var i=0; i<oldSubwidgets.length; i++) {
		var subwidget = oldSubwidgets.get(i);
		subwidgetsToBeRemoved.push(subwidget.id);
		
	}
	
	var items = data.items;
	var str = "";
	for (var i=0; i < items.length; i++) {
		str += items[i];
	}
	
	el.html(str);
	return true;
};




