TextField.prototype = new GenericWidget();
TextField.prototype.constructor = TextField;

function TextField(servletPath, widgetID) {
    var sendValueOnChange = true;

    GenericWidget.call(this,servletPath,widgetID);

    this.sendGET();
}

TextField.prototype.update = function (data) {

	var tfelement = $("#" + this.widgetID + ".ogema-widget").find("#textField");
    var valueAvailable = false;
    if (data.hasOwnProperty("value") && data.value !== "") {
    	valueAvailable = true;
    }
    if (data.hasOwnProperty("placeholder") && !valueAvailable) {
        //$("#" + this.widgetID).find("#textField").removeAttr("placeholder").removeAttr("value").attr("placeholder", data.placeholder);
        tfelement.val("");
        tfelement.removeAttr("placeholder").attr("placeholder", data.placeholder);
    }
    else if (valueAvailable) {
        //$("#" + this.widgetID).find("#textField").removeAttr("placeholder").attr("value", data.value);
    	tfelement.removeAttr("placeholder").val(data.value);
    }
    
    var tmp = this;
    if (data.hasOwnProperty("sendValueOnChange")) {
        tmp.sendValueOnChange = data.sendValueOnChange;
    } else {
    	tmp.sendValueOnChange = true;
    }

    if (data.hasOwnProperty("type")) {
    	tfelement.removeAttr("type").attr("type", data.type);
    }
    
    tfelement.removeAttr("min");
    if (data.hasOwnProperty("min")) {
    	tfelement.attr("min",data.min);
    }
    
    tfelement.removeAttr("max");
    if (data.hasOwnProperty("max")) {
    	tfelement.attr("max",data.max);
    }
    
    tfelement.removeAttr("step");
    if (data.hasOwnProperty("step")) {
    	tfelement.attr("step",data.step);
    }
 
    var disabled = data.hasOwnProperty("disabled") && data.disabled;
    if (disabled)
    	tfelement.attr("disabled",true);
    else
    	tfelement.removeAttr("disabled");

    tfelement.off("change");
    tfelement.change(function () {
    	if(tmp.sendValueOnChange) {
    		tmp.sendPOST();
    	}
    });


}

TextField.prototype.getSubmitData = function() {
	var textFieldValue = $("#" + this.widgetID + ".ogema-widget").find("#textField").val();
    return textFieldValue;
}

