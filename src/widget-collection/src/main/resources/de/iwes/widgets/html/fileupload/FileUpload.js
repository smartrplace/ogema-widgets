/* global GenericWidget */

FileUpload.prototype = new GenericWidget();
FileUpload.prototype.constructor = FileUpload;


function FileUpload(servletPath, widgetID) {
    GenericWidget.call(this, servletPath, widgetID);

    this.fileupload = $("#" + this.widgetID);
    this.fileuploadform = this.fileupload.find("form");
    this.filebox = this.fileuploadform.find("input");
    this.base_url = window.location.origin; //Get base-url for form-action // required?
//    this.servletPath = "";
    this.url = "";
    this.disabled = false;

    this.sendGET();
}

FileUpload.prototype.update = function (data) {
    if (data.hasOwnProperty("servletPath")) {
//        if (this.servletPath != data.servletPath) {
//            this.servletPath = data.servletPath;
            this.url = this.base_url + data.servletPath;
//        }
    }
    if (data.hasOwnProperty("multiUpload")) {
        this.filebox.attr("multiple", "");
    }
    else {
        this.filebox.removeAttr("multiple");
    }
};


//FileUpload.prototype.getSubmitData = function() {
FileUpload.prototype.processPOSTResponse = function(data) {
	var disabled;
    if (data.hasOwnProperty("disabled")) 
    	disabled = data.disabled;
    else
    	disabled = false;
	if (!disabled)
		this.submitUpload();
	return {};
}

FileUpload.prototype.submitUpload = function () {  
    if (this.filebox.val() == "") {
        //alert("Please select a file!"); // not a problem
    	return;
    }
    else {
    	var el = this;
        var formData = new FormData();
        var fileCount = this.filebox[0].files.length;

        for (var x = 0; x < fileCount; x++)
        {
            formData.append("file[]", this.filebox[0].files[x]);
        }
        
        console.log("filebox ",this.filebox);
        console.log("File count",fileCount,this.filebox[0].files);

        $.ajax({
            url: this.url + ogema.getParameters(),
            type: 'post',
            data: formData,
            contentType: false,
            processData: false,
            success: function (data, status) {
                //alert("File/s uploaded! " + url);  // avoid annyoing the user... use customized Alert widget message in apps instead
                el.uploadCompleted(true);
            },
            error: function (xhr, desc, err) {
            	// alert("Error while uploading file/s! Check console for details.");
                console.error(xhr, desc, err);
                el.uploadCompleted(false, desc);
            }
        });
    }
};

FileUpload.prototype.uploadCompleted = function(success,message,err) {
	var detail = {success:success};
	if (typeof message !== "undefined") 
		detail.message = message;
	if (typeof err !== "undefined") 
		detail.error = err;
	var event = new CustomEvent("uploadCompleted", {
		detail: detail
	});
	this.element.dispatchEvent(event);
}