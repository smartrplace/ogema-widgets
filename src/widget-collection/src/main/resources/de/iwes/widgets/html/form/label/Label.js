Label.prototype = new GenericWidget();
Label.prototype.constructor = Label;

function Label(servletPath, widgetID) {
    GenericWidget.call(this, servletPath, widgetID);
    this.sendGET();
}

Label.prototype.update = function (data) {
    if (data.hasOwnProperty("css")) {
        $("#" + this.widgetID).find("#labelText").removeAttr("class").addClass(data.css);
    }
    if (data.hasOwnProperty("text")) {
        $("#" + this.widgetID).find("#labelText").html(data.text);
    }
    else {
    	$("#" + this.widgetID).find("#labelText").html("");
    }
};




  