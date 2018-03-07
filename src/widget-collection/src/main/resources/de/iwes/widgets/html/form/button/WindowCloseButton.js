WindowCloseButton.prototype = new GenericWidget();	// inherit from GenericWidget
WindowCloseButton.prototype.constructor = WindowCloseButton;	// have to reset constructor for inheriting class

function WindowCloseButton(servletPath, widgetID) {           // constructor
    GenericWidget.call(this, servletPath, widgetID);	// call superconstructor
    if (!widgetID) { // required so other widgets can inherit from this
    	return;
    }
    var bt = this;
    $("#" + widgetID).find("#ogemaButton")[0].addEventListener('click', function () {
        bt.sendPOST();
        setTimeout(function() {
        	window.close();
        }, 1000);      
    });
    this.sendGET();
}

// @Override
WindowCloseButton.prototype.update = function (data) {
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

WindowCloseButton.prototype.processPOSTResponse = function (data) {
	var paramStr = '';
	var isInit = false;
	if (!data.hasOwnProperty('url') || !data.url) return;
	var url = data.url;
	if (data.hasOwnProperty('params')) {
		Object.keys(data.params).forEach(function(key) {
			if(isInit) {
				paramStr = paramStr + "&";
			} else {
				paramStr = '?';
				inInit = true;
			}
			paramStr = paramStr + key + "=" + data.params[key];
			if(key === 'configId') {
				ogema.configId = data.params[key];
		        params = '?pageInstance=' + ogema.pageInstance;
		        if(ogema.configId.length > 0) {
		        	params = params + "&configId="+ogema.configId;
		        }
			}
		});
	}
	window.open(url + paramStr);
}