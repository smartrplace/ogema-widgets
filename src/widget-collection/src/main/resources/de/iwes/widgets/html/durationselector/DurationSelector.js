DurationSelector.prototype = new GenericWidget();
DurationSelector.prototype.constructor = DurationSelector;

function DurationSelector(servletPath, widgetID) {
    GenericWidget.call(this,servletPath,widgetID);
    this.zeroAllowed = true;
    this.sendValueOnChange = true;
    this.sendGET();
}

DurationSelector.prototype.update = function (data) {
  
	var baseEl  = $("#" + this.widgetID);
	var elmt = baseEl.find("#textField");
	var typeSelector = baseEl.find("#durationTypeSelect");
    elmt.off("change");
    typeSelector.off("change");
	
    var tmp = this;

    var types = data.types; 
    var typeHtml = "";
    if (data.hasOwnProperty("sendValueOnChange")) 
    	this.sendValueOnChange = data.sendValueOnChange;
    else
    	this.sendValueOnChange = true;
    for (var i = 0; i<types.length; i++) {
    	typeHtml += "<option>" + types[i] + "</option>";
    }
    typeSelector.html(typeHtml);
    if (data.hasOwnProperty("selectedType")) {
    	var selected = data.selectedType;
        typeSelector.val(selected); 

    }
    elmt.val(1);
    this.zeroAllowed = data.zeroAllowed;
    var min = this.zeroAllowed ? 0 : 1;
    elmt.attr("min", min); 
    
    elmt.change(function () {
    	if(tmp.sendValueOnChange) {
    		tmp.sendPOST();
    	}
    });
    typeSelector.change(function () {
    	if(tmp.sendValueOnChange) {
    		tmp.sendPOST();
    	}
    });
}

DurationSelector.prototype.getSubmitData = function() {
	var baseEl  = $("#" + this.widgetID);
	var elmt = baseEl.find("#textField");
	var typeSelector = baseEl.find("#durationTypeSelect");
	var value = parseInt(elmt.val());
	var tp = typeSelector.val(); // TODO check
	var durationObj = "{}";
	var result = "";
	try {
		if (isNaN(value)) 
			throw new Error("not a value");
		durationObj = moment.duration(value,tp);
		result = durationObj.asMilliseconds();
		// FIXME
		console.log("New duration; value, type, object, millis: ",value,tp,durationObj,result );
	} catch (e) {
		console.err("Invalid input ", value, tp, e);
	}
    return result;
}

