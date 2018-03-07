Checkbox2.prototype = new GenericWidget();
Checkbox2.prototype.constructor = Checkbox2;

function Checkbox2(servletPath, widgetID) {
    GenericWidget.call(this, servletPath, widgetID);
    this.sendGET();
}

Checkbox2.prototype.update = function (data) {

    var tmp = this;
    if (data.hasOwnProperty("sendValueOnChange")) {
        tmp.sendValueOnChange = data.sendValueOnChange;
    } else {
    	tmp.sendValueOnChange = true;
    }
    var disabled = data.disabled ? "disabled" : "";
    var html = "<div>";
    var first = true;;
    $.each(data.items, function (index, value) {
    	if (!first)
    		html += "<br>";
    	first = false;
    	var checked = value.checked ? "checked='checked'" : "";
    	var tooltip = value.hasOwnProperty("tooltip") ? "title='" + value.tooltip + "'" : "";
    	html += "<label " + tooltip + "><input type='checkbox' value='" + value.id + "' " + checked + " " + disabled + ">" + value.label + "</label>";
    });
    html += "</div>";
    $("#" + this.widgetID + ".ogema-widget>#items").html(html);

    if (this.sendValueOnChange) { 
	    $("#" + this.widgetID + ".ogema-widget").find(":checkbox").change(function() {
	    		tmp.sendPOST();
	    });
    }
        
};

Checkbox2.prototype.getSubmitData = function () {

    var inputs =  $("#" + this.widgetID + ".ogema-widget input");
    var data = "";
    $.each(inputs, function (index, value) {
        data += value.value + "=" + value.checked + "&";
    });
    return data;
};