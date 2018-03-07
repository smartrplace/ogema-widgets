// RedirectButton.prototype = new Button();	     // problematic, because we would have to ensure that Button.js is loaded before this script is
RedirectButton.prototype = new GenericWidget();
RedirectButton.prototype.constructor = RedirectButton;	// have to reset constructor for inheriting class

/*function RedirectButton(servletPath, widgetID) {           // constructor
    Button.call(this, servletPath, widgetID);	// call superconstructor
}*/

function RedirectButton(servletPath, widgetID) {           // constructor
    GenericWidget.call(this, servletPath, widgetID);	// call superconstructor
    if (!widgetID) { // required so other widgets can inherit from this
    	return;
    }
    var bt = this;
    $("#" + widgetID).find("#ogemaButton")[0].addEventListener('click', function () {
        bt.sendPOST();
    });
    this.sendGET();
}

// @Override
RedirectButton.prototype.update = function (data) {
    if (data.hasOwnProperty("text")) {
        var html = "<span id='buttonText'></span>" + data.text;
        $("#" + this.widgetID).find("#ogemaButton").html(html);
    }
    if (data.hasOwnProperty("glyphicon")) {
        $("#" + this.widgetID).find("#buttonText").removeAttr("class").addClass(data.glyphicon);
    }
    if (data.hasOwnProperty("disabled")) {
        $("#" + this.widgetID).find("#ogemaButton").prop('disabled', data.disabled);
    	if (data.disabled) console.log("Disabled Button");
    }
};

RedirectButton.prototype.processPOSTResponse = function (data) {
//	var paramStr = '';
//	var isInit = false;
	if (!data.hasOwnProperty('url') || !data.url) return;
	var openNewTab = data.newTab;
	// url contains parameters; page parameters are set by widget loader on init
	var url = data.url;
//	if (data.hasOwnProperty('params')) {
//		Object.keys(data.params).forEach(function(key) {
//			if(isInit) {
//				paramStr = paramStr + "&";
//			} else {
//				paramStr = '?';
//				isInit = true;
//			}
//			paramStr = paramStr + key + "=" + data.params[key];
//			if(key === 'configId') {
//				ogema.configId = data.params[key];
//		        params = '?pageInstance=' + ogema.pageInstance;
//		        if(ogema.configId.length > 0) {
//		        	params = params + "&configId="+ogema.configId;
//		        }
//			}
//		});
//	}
//	window.open(url + paramStr);
	if(openNewTab) {
		window.open(url);
	} else {
		location.href = url;
	}
}