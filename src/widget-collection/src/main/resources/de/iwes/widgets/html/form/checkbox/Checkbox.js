Checkbox.prototype = new GenericWidget();
Checkbox.prototype.constructor = Checkbox;

function Checkbox(servletPath, widgetID) {
    GenericWidget.call(this, servletPath, widgetID);
    
    //this.sendValueOnChange = true;
    
    this.sendGET();
}

Checkbox.prototype.update = function (data) {


    var checkboxID = this.widgetID + "_checkbox";
    $("#" + this.widgetID + " #checkboxList").removeAttr("id").attr("id", checkboxID);

 /*   if (data.hasOwnProperty("title")) {
        var title = data.title;
        $("#" + this.widgetID + " #title").text(title);
    } */
    var tmp = this;
    if (data.hasOwnProperty("sendValueOnChange")) {
        tmp.sendValueOnChange = data.sendValueOnChange;
    } else {
    	tmp.sendValueOnChange = true;
    }
    var disabled = data.disabled ? "disabled" : "";
    if (data.hasOwnProperty("checkboxList")) {
        var checkboxList = data.checkboxList;
        var html = "<div class=\"checkbox\">";
        $.each(checkboxList, function (index, value) {

            var checked = "";
            if (value === true) {
                checked = "checked=checked";
            }

            	 html += "<label>\n\ <input type='checkbox'  name='" + data.title + "' value='" + index + "' " + checked + " " + disabled + ">"
             + index +
             "</label><br/>";
          
           
        });
        html += "</div>";
        $("#" + this.widgetID + " #" + checkboxID).html(html);

        $("#" + this.widgetID).find(":checkbox").change(function() {
        	if(tmp.sendValueOnChange) {
        		tmp.sendPOST();
        	}
        });
        
    }
};

Checkbox.prototype.getSubmitData = function () {

    var widgetID = this.widgetID;
    var checkboxID = widgetID + "_checkbox";
    var checkboxList = $("#" + checkboxID + " input");
    var data = "";

    $.each(checkboxList, function (index, value) {
        var input = value;
        data += input.value + "=" + input.checked + "&";
    });
    return data;
};