Plot2D.prototype = new GenericWidget();
Plot2D.prototype.constructor = Plot2D;

function Plot2D( servletPath, widgetID ) {
	GenericWidget.call( this, servletPath, widgetID );
//	this.sendGET();	 // must be called in implementing widget
}

/**
 * @param
 *		xmin, xmax mandatory, ymin, ymax optional
 */
Plot2D.prototype.getRestrictedDataParameters = function( xmin,xmax,ymin,ymax ) {
	var params = "&xmin=" + xmin + "&xmax=" + xmax;
	if (typeof ymin !== "undefined" && typeof ymax !== "undefined")
		params += "&ymin=" + ymin + "&ymax=" + ymax;
	return params;
}
