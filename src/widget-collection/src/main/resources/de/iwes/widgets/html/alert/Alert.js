Alert.prototype = new GenericWidget();
Alert.prototype.constructor = Alert;


function Alert(servletPath, widgetID) {

    GenericWidget.call(this, servletPath, widgetID);

    this.alert = $("#" + this.widgetID).find("#alertBody");
    this.aDismiss = -1;

    this.timeoutVar = null;

    this.sendGET();
}

Alert.prototype.update = function (data) {
 
    if (data.hasOwnProperty("css")) {
        this.alert.removeAttr("style");
        this.alert.css(data.css);
    }

    if (data.hasOwnProperty("autoDismiss")) {
        this.aDismiss = data.autoDismiss;
    }

    if (data.hasOwnProperty("allowDismiss")) { //This is lacking remove functionality
        var w = this;
        if (data.allowDismiss) {
            //Only add button once
            if (this.alert.has(".close").length === 0) {
               var dismissButton = "<button type=\"button\" class=\"close\"><span class=\"glyphicon glyphicon-remove\" aria-hidden=\"true\"></span></button>";      
                this.alert.append(dismissButton);
                //Hide when dismiss-button is pressed
                this.alert.find(".close").on('click', function (e) {
                    w.hideWidget();
                });
            }
        }
    }

    if (data.hasOwnProperty("htmlClass")) {
        this.alert.attr("class", data.htmlClass);
    }

    if (data.hasOwnProperty("text")) {
        this.setText(data.text);
    }
};

Alert.prototype.setText = function (text) {
    if (this.alert.has("#alertText").length === 0) {
        this.alert.append("<div id=\"alertText\"></div>");
    }
    this.alert.find("#alertText").html(text);
};

Alert.prototype.autoDismiss = function (delay) {
    var w = this;
    if (this.timeoutVar) {  // remove old timeout
    	clearTimeout(this.timeoutVar);
    }
    this.timeoutVar = setTimeout(function() {
        w.hideWidget();
        w.timeoutVar = null;
    },delay);
};

//Override method to support auto-dismiss
Alert.prototype.showWidget = function () {
    this.element.style.display = "inline";
    if (this.aDismiss !== -1) {
            this.autoDismiss(this.aDismiss);
    }
};
