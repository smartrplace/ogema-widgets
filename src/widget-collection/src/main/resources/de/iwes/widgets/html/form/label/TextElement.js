TextElement.prototype = new GenericWidget();
TextElement.prototype.constructor = TextElement;

function TextElement(servletPath, widgetID) {
    GenericWidget.call(this, servletPath, widgetID);
    this.sendGET();
}

TextElement.prototype.update = function (data) {
    if (data.hasOwnProperty("text")) {
        var html = "<span id='labelText'></span>" + data.text;
        $("#" + this.widgetID).find("#labelText").html(html);
    	//$("#" + this.widgetID).find("#labelText").text("");
        //$("#" + this.widgetID).find("#labelText").text(data.text);
        //$("#" + this.widgetID).find("#labelText").add(data.text);
    }
};




  