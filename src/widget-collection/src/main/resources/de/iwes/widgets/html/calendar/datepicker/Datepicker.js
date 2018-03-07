Datepicker.prototype = new GenericWidget();
Datepicker.prototype.constructor = Datepicker;

function Datepicker(servletPath, widgetID) {
    this.daysOfWeekDisabled=[];
    this.disabledDates=[];
	this.attributes = {};
    this.datePickerID = widgetID + "_datetimepicker";
    this.datePickerFieldID = widgetID + "_datetimepickerField";

    $("#" + widgetID).find("#datetimepicker").removeAttr("id").attr("id", this.datePickerID);
    $("#" + widgetID).find("#datetimepickerField").removeAttr("id").attr("id", this.datePickerFieldID);
    this.inputField = null;
 
    GenericWidget.call(this, servletPath, widgetID);
    this.sendGET();

}

Datepicker.prototype.update = function( json ) {
	var dp = this;
 	if (json.hasOwnProperty("daysOfWeekDisabled")) {
        this.daysOfWeekDisabled = json.daysOfWeekDisabled;
    }
    if (json.hasOwnProperty("disabledDates")) {
        this.disabledDates = json.disabledDates;
    }
    if (json.hasOwnProperty("attributes")) { 
        this.attributes = json.attributes;
    }
    if (this.inputField == null) {
	    $('#' + this.datePickerID).datetimepicker({
	        locale: this.attributes.locale,
	        format: this.attributes.format,
	        defaultDate: this.attributes.defaultDate,
	        disabledDates: this.disabledDates,
	        viewMode: this.attributes.viewMode,
	        daysOfWeekDisabled: this.daysOfWeekDisabled
	    }).on("dp.change", function(attributes) {
	       	dp.sendPOST();
	    });;
	    this.inputField = $('#' + this.datePickerID).find("input");
    } else {
    	this.inputField.val(this.attributes.defaultDate);
    }
}

Datepicker.prototype.getSubmitData = function () {
    var datePickerFieldID = this.widgetID + "_datetimepickerField";
    var data = $("#" + this.widgetID).find("#" + datePickerFieldID).val();
//    console.log("Sending POST data", data);
    return data;
}
