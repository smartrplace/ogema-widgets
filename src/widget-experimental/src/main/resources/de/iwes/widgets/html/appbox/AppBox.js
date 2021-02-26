AppBox.prototype = new GenericWidget();
AppBox.prototype.constructor = AppBox;

function AppBox( servletPath, widgetID ) {
	GenericWidget.call( this, servletPath, widgetID );
	this.currentApps = [];
	this.sendGET();	
}

AppBox.prototype.update = function( data ) {
	
	var el = $("#" + this.widgetID + ".ogema-widget>div");
	el.html(""); // TODO improve
	var opener = function(t) {
		window.open(t,"_blank");
	};
	for (var i=0; i<data.apps.length; i++) {
		var appData = data.apps[i];
		var metainfo = appData.metainfo;
		var description;
		if (metainfo.hasOwnProperty("Bundle_Description"))
			description = metainfo.Bundle_Description;
		else
			description = "";
		var appHtml = 
			"<div style=\"flex:0 0 100px;\" id=\"" + appData.id + "\"><div class=\"app-container\">" +
				"<div class='wrapper_top_image_title'><center><img src=\"/ogema/widget/apps?action=getIcon&id=" + appData.id;
		if (typeof otusr !== "undefined")
			appHtml += "&user=" + otusr + "&pw=" + otpwd;
		appHtml += "\"/></center>" +
				"<b>" + metainfo.Bundle_Name + "</b></div>" +
				"<p style=\"font-size:80%;\">" + metainfo.Bundle_Version + "<p>" +
				"<p>" + description + "</p>" + 
			"</div></div>";
		el.append(appHtml);
		if (appData.hasOwnProperty("startPage")) {
			var target = opener.bind(null,appData.startPage);
			el.find(">div:last-child").on("click", target);
		}
		if (appData.hasOwnProperty("tooltip")) {
			el.find(">div:last-child").get(0).title=appData.tooltip;
		}
	}
	this.currentApps = data.apps;
}

// TODO better filter
AppBox.prototype.filterApps = function( filter, caseSensitive ) {
	if (!caseSensitive)
		filter = filter.toLowerCase();
	var el = $("#" + this.widgetID + ".ogema-widget>div");
	for (var i=0; i<this.currentApps.length;i++) {
		var app = this.currentApps[i];
		var name = app.name;
		var bundleName = app.metainfo.Bundle_Name;
		var descr = app.metainfo.Bundle_Description;
		if (!caseSensitive) {
			name = name.toLowerCase();
			bundleName = bundleName.toLowerCase();
			if (typeof descr !== "undefined")
				descr = descr.toLowerCase();
		}
		if (name.indexOf(filter) >=0 || bundleName.indexOf(filter) >=0 || (typeof descr !== "undefined" &&  descr.indexOf(filter) >=0 )) {
			el.find(">div#" + app.id).removeClass("app-filtered");
		}
		else {
			el.find(">div#" + app.id).addClass("app-filtered");
		}
	}
	
}
