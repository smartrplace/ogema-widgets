/* global GenericWidget */

FileDownload.prototype = new GenericWidget();
FileDownload.prototype.constructor = FileDownload;


function FileDownload(servletPath, widgetID) {
    GenericWidget.call(this, servletPath, widgetID);

    this.filedownload = $("#" + this.widgetID);
    this.url = "";
    this.base_url = window.location.origin; //Get base-url for download-tab // FIXME does this always work as expected?
    this.waitingForDownload = false;

    this.sendGET();
}

FileDownload.prototype.update = function (data) {
    if (data.hasOwnProperty("url")) {
        this.url = data.url;
    }
    var waiting = this.waitingForDownload;
    this.waitingForDownload = false;
    if (waiting) {
        this.download();
    }
};

FileDownload.prototype.download = function () {

    try {
        if (this.url === "") {
//            console.error(this.widgetID + ": Download-URL empty!"); // this case is no longer considered an error, but a feature to deactivate the FileDownload
        }
        else {
            window.open(this.base_url + this.url + ogema.getParameters());
        }
    }
    catch (e) {
        console.error(this.widgetID + ": Error while triggering download!", e);
    }
};

FileDownload.prototype.getAndDownload = function () {
   this.waitingForDownload = true;
   this.sendGET();
};


