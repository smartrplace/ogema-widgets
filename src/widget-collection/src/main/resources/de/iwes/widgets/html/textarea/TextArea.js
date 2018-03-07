TextArea.prototype = new GenericWidget();
TextArea.prototype.constructor = TextArea;

function TextArea(servletPath, widgetID) {
    GenericWidget.call(this, servletPath, widgetID);
    this.el = $("#" + this.widgetID).find("#textareaTag");
    this.sendGET();
}

TextArea.prototype.update = function (data) {
    if (data.hasOwnProperty("text")) {
        this.el.html(data.text);
    }
};

TextArea.prototype.getSubmitData = function() {
	var textFieldValue = $("#" + this.widgetID).find("#textareaTag").val();
    return textFieldValue;
}




  