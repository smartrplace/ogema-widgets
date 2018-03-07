ConfigButtonRow.prototype = new GenericWidget();
ConfigButtonRow.prototype.constructor = ConfigButtonRow;

function ConfigButtonRow(servletPath, widgetID) {
    GenericWidget.call(this,servletPath,widgetID);
    
    var bt = this;
    $("#" + widgetID).find("#buttonOK")[0].addEventListener('click', function () {
         bt.sendPOST();
        setTimeout(function() {
        	window.close();
        }, 1000);      
    });
    $("#" + widgetID).find("#buttonCancel")[0].addEventListener('click', function () {
        window.close();
    });
    $("#" + widgetID).find("#buttonSave")[0].addEventListener('click', function () {
        bt.sendPOST();
    });

    //this.servletPath = this.servletPath + window.location.search;
    this.sendGET()
}

