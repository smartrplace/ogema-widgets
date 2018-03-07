Flexbox.prototype = new GenericWidget();
Flexbox.prototype.constructor = Flexbox;


function Flexbox(servletPath, widgetID) {

    GenericWidget.call(this, servletPath, widgetID);
    this.sendGET();
}

/*
 * TODO this is implemented in a very inefficient way: destroy all widgets, then recreate them -> to be improved 
 */
Flexbox.prototype.update = function (data, oldRequest, subwidgetsToBeRemoved) {
    
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




