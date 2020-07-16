Multiselect.prototype = new GenericWidget();
Multiselect.prototype.constructor = Multiselect;


function Multiselect(servletPath, widgetID) {
 
    GenericWidget.call(this, servletPath, widgetID);
    this.dropdown = $("#" + widgetID).find("#multiselect");
    this.dropdownOptions = this.dropdown.find("#list");
    this.sendValueOnChange = true;
    this.sendGET();    
}

Multiselect.prototype.update = function (data) {

    if (data.hasOwnProperty("css")) {
        this.dropdownOptions.css(data.css);
    }
    if (data.hasOwnProperty("sendValueOnChange")) {
    	this.sendValueOnChange = data.sendValueOnChange;
    }
    var html = "<select class=\"chosen-select\"  multiple=\"\" id=\"list\">";
    if (data.hasOwnProperty("options")) {
        var options = data.options;
        for (var i = 0; i < options.length; i++) {
            var selected = options[i].selected;
            var value = options[i].value;
            var label = options[i].label;
            html += "<option ";

            if (selected === true) {
                html += "selected=\"selected\" ";
            }
            html += "value=\'" + value + "\'>" + label + "</option>";

        }
//        this.dropdownOptions.html(html);
    }
    html += "</select>";
    this.dropdown.html(html);
    this.dropdownOptions = this.dropdown.find("#list");
    var disabled = data.hasOwnProperty("disabled") && data.disabled;
    this.dropdownOptions.prop("disabled", disabled);
    this.dropdownOptions.trigger("chosen:updated");
    var chosenObj;
    if (data.hasOwnProperty("width")) { 
    	chosenObj = this.dropdownOptions.chosen({width: data.width});
    } else {
//    	chosenObj = this.dropdownOptions.chosen({width:"100%;"}); -> 100% means it will expand indefinitely in horizontal direction
    	chosenObj = this.dropdownOptions.chosen();
    	var el  = this.dropdown.find(".chosen-container");
    	if (el.width() < 20) { // happens if element is originally hidden
    		el.width(200); // TODO would be better to adjust width to surrounding element; not so easy...
    	}
    }
    if (this.sendValueOnChange) {
        var multi = this;
		chosenObj.change(function() {multi.sendPOST();});
    }
};

Multiselect.prototype.getSubmitData = function () {
    var data = [];

    $("#" + this.widgetID + " option:selected").each(function () {
      //  data = "" + $(this).attr("value");
      data.push($(this)[0].value);
    });
//	console.log("Multiselect POST response",data);
    return data;
};