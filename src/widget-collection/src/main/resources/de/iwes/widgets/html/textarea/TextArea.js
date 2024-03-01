TextArea.prototype = new GenericWidget();
TextArea.prototype.constructor = TextArea;

function TextArea(servletPath, widgetID) {
    GenericWidget.call(this, servletPath, widgetID);
    this.el = $("#" + this.widgetID).find("#textareaTag");
    this.sendGET();
}

TextArea.prototype.update = function (data) {
    if (data.hasOwnProperty("text")) {
        this.el.val(data.text);
    }
    if (data.rows > 0)
		this.el[0].rows = data.rows;
	if (data.cols > 0)
		this.el[0].cols = data.cols;
    this.el.off("change");
    if (!data.hasOwnProperty("sendValueOnChange") || data.sendValueOnChange) {
    	const tmp = this;
    	tmp.el.change(function() { tmp.sendPOST(); });
    }
    if (data.hasOwnProperty("selected")) {
		this.el[0].select();
		this.el[0].focus();
	}
    
};

TextArea.prototype.getSubmitData = function() {
	var textFieldValue = $("#" + this.widgetID).find("#textareaTag").val();
    return textFieldValue;
}




  
