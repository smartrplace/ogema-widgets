Dropdown.prototype = new GenericWidget();
Dropdown.prototype.constructor = Dropdown;


function Dropdown(servletPath, widgetID) {
 
    GenericWidget.call(this, servletPath, widgetID);
    this.dropdown = $("#" + widgetID + ".ogema-widget").find(">#dropdown");
    this.dropdownOptions = this.dropdown.find(">#list");
    
    this.sendValueOnChange = true;
    var tmp = this;
    this.dropdownOptions.change(function () {
    	if(tmp.sendValueOnChange) {
    		tmp.sendPOST();
    	}
    });
    this.sendGET();  
}

Dropdown.prototype.update = function (data) {
    var html = "";

    if (data.hasOwnProperty("css")) {
        this.dropdownOptions.css(data.css);
    }
    if (data.hasOwnProperty("sendValueOnChange")) {
    	this.sendValueOnChange = data.sendValueOnChange;
    }
    var disabled = data.hasOwnProperty("disabled") && data.disabled;
   	this.dropdownOptions.attr("disabled",disabled);

    if (data.hasOwnProperty("options")) {
        var options = data.options;
        var html = "";
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

        this.dropdownOptions.html(html);
    }
};

Dropdown.prototype.getSubmitData = function () {
    var data = [];

    $("#" + this.widgetID + " option:selected").each(function () {
      //  data = "" + $(this).attr("value");
      data.push($(this)[0].value);
    });
    return data;
};