Icon.prototype = new GenericWidget();
Icon.prototype.constructor = Icon;

function Icon(servletPath, widgetID) {

    GenericWidget.call(this, servletPath, widgetID);
    this.iconDiv = $("#" + this.widgetID).find("#icon");
    this.sendGET();
}

Icon.prototype.update = function (data) {
   var html = "";
   var scaleExists = data.hasOwnProperty("scale");
   var enabled = data.hasOwnProperty("disabled") && data.disabled === false;
   if (data.hasOwnProperty("iconType") && data.iconType.length > 0) {
   		var style = "";
   		if (scaleExists || enabled) {
   			style = "style=\"";
   			if (scaleExists)
   				style = style + "height:" + data.scale + "%; width:" + data.scale + "%; ";
   			if (enabled)
   				style = style + "cursor: pointer;"
   			style = style + "\"";
   		}
   		html = "<img src=\"" + data.iconType;
   		// usually not needed, since resources do not require a one-time-password... but
   		// occasionally, they may be delivered via a servlet
   		if (data.ogemaServlet && typeof otusr === "string" && typeof otpwd === "string") {
   			var sep = data.iconType.indexOf("?") >= 0 ? "&" : "?";
   			html += sep + "user=" + otusr+ "&pw=" + otpwd;
   		}
   		html += "\" " + style + "></img>";

   }
   this.iconDiv.html(html);
   var sub = this.iconDiv.find(">img");  // important to trigger only on the image itself
   sub.off('click');
   var gw = this;
   if (enabled) {
   		sub.on('click', function() { gw.sendPOST(); });
   }
};
