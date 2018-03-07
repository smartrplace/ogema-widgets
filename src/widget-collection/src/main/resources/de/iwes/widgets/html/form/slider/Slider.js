Slider.prototype = new GenericWidget();
Slider.prototype.constructor = Slider;

function Slider(servletPath, widgetID) {
    var sliderIdOutput = widgetID + "Output"; 
    var sendValueOnChange = true;
    
    $("#" + widgetID).find("#slider").removeAttr("onchange").attr("onchange", "nextElementSibling.value = value");  
    $("#" + widgetID).find("#rangePrimary").removeAttr("id").attr("id", sliderIdOutput);  

    GenericWidget.call(this, servletPath, widgetID);
    
    this.sendValueOnChange = true;
    var tmp = this;
    $("#" + this.widgetID).find("#slider").change(function () {
    	if(tmp.sendValueOnChange) {
    		tmp.sendPOST();
    	}
    });

    this.sendGET();
}

Slider.prototype.update = function (data) {

    if (data.hasOwnProperty("min")) {
        $("#" + this.widgetID).find("#slider").removeAttr("min").attr("min", data.min);
    }
    if (data.hasOwnProperty("max")) {
        $("#" + this.widgetID).find("#slider").removeAttr("max").attr("max", data.max);
    }
    if (data.hasOwnProperty("sendValueOnChange")) {
    	this.sendValueOnChange = data.sendValueOnChange;
    }
    if (data.hasOwnProperty("value")) {
        var sliderIdOutput = this.widgetID + "Output";
    
        $("#" + this.widgetID).find("#slider")[0].value = data.value;
        $("#" + this.widgetID).find("#"+sliderIdOutput).val(data.value);
    }
    
    if (data.hasOwnProperty("disabled")) {
    	if (data.disabled) {
    		$("#" + this.widgetID).find("#slider").attr("disabled",true);
    		$("#" + this.widgetID).find("#ogemaSlider").removeAttr("class").addClass("range");
    	}
    	else {
    		$("#" + this.widgetID).find("#slider").attr("disabled",false);
    		if (!data.hasOwnProperty("css")) {
    			$("#" + this.widgetID).find("#ogemaSlider").removeAttr("class").addClass("range range-primary");
    		}
    		
    	}
    }
    if (data.hasOwnProperty("css")) { 	// TODO move to parent
        $("#" + this.widgetID).find("#ogemaSlider").removeAttr("class").addClass(data.css);
    }
}

Slider.prototype.getSubmitData = function () {
    var data =  $("#" + this.widgetID).find("#slider")[0].value;
    return data;
}