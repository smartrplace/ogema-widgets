PageSnippet.prototype = new GenericWidget();
PageSnippet.prototype.constructor = PageSnippet;


function PageSnippet(servletPath, widgetID) {
 
    GenericWidget.call(this, servletPath, widgetID);
    this.snippet = $("#" + widgetID).find("#bodyDiv")[0];
    this.sendValueOnChange = true;
    var tmp = this;
//    this.html = "";
    this.sendGET();  
//    console.log("Initializing page snippet!!");
    
}

PageSnippet.prototype.update = function (data, oldRequest, subwidgetsToBeRemoved) {
    
    if (data.hasOwnProperty("sendValueOnChange")) {
    	this.sendValueOnChange = data.sendValueOnChange;
    }
    if (!data.hasOwnProperty("html")) {
    	console.error("Page snippet has no html!");
    	return;
    }
    
    var update = this.hasDataChanged(data,oldRequest,subwidgetsToBeRemoved);
     
    if (update) { 
    	this.snippet.innerHTML= data.html;
    }
    if (data.hasOwnProperty("background-img")) {
    	this.snippet.style["background-image"]="url('" + data["background-img"] + "')";
    }
    
//    console.log("Updating page snippet!");
	return update;
};

PageSnippet.prototype.hasDataChanged = function(newRequest,oldRequest,subwidgetsToBeRemoved) {
	
	if (!oldRequest || !oldRequest.hasOwnProperty("html")) return true;
	if (oldRequest.html.length !== newRequest.html.length || oldRequest.html !== newRequest.html) {	
		if (oldRequest.hasOwnProperty("subWidgets")) {
			subwidgetsToBeRemoved.push.apply(subwidgetsToBeRemoved, oldRequest.subWidgets);
		}
		return true;
	}
	return false;
}

PageSnippet.prototype.showExpiredMessage = function() { // FIXME remove subwidgets?
	 var snippet = this;
	 $.ajax({
        type: "GET",
        url: this.servletPath + "?expired=true",
        contentType: "application/json"
    }).done(function (result) {
    	var data = JSON.parse(result);
    	snippet.snippet.innerHTML= data.html;
    });
}

PageSnippet.prototype.showErrorHandlingMessage = function(msg) { // FIXME remove subwidgets?
	var html = "<br><button class=\"btn btn-primary\" onclick=\"window.location.reload(true);\">Reload page</button><br>" +
			"<br><div id=\"alert\" class=\"alert alert-success\"><div>" + msg +"</div></div>"; 
   	this.snippet.innerHTML= html;	
}

