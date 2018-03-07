UrlRedirect.prototype = new GenericWidget();
UrlRedirect.prototype.constructor = UrlRedirect;

function UrlRedirect( servletPath, widgetID ) {
	GenericWidget.call( this, servletPath, widgetID );
	this.sendGET();	
}

UrlRedirect.prototype.update = function( data ) {
	if (data.hasOwnProperty("disabled") && data.disabled)
		return;
	if (data.hasOwnProperty("url")) {
		if (data.url === "CURRENT_LOCATION")
			window.location.reload(true); // note: true is important here
		else
			window.location.replace(data.url);
	}
}
