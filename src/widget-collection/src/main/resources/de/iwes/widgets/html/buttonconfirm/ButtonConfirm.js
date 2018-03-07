ButtonConfirm.prototype = new GenericWidget();	// inherit from GenericWidget
ButtonConfirm.prototype.constructor = ButtonConfirm;	// have to reset constructor for inheriting class

function ButtonConfirm(servletPath, widgetID) {           // constructor
    GenericWidget.call(this, servletPath, widgetID);	// call superconstructor
    if (!widgetID) { // required so other widgets can inherit from this
    	return;
    }
    this.modal = $("#" + widgetID + " #modal");
    var bt = this;
    var el = $("#" + this.widgetID + ".ogema-widget");
    el.find("#confirmButton")[0].addEventListener('click', function () {
    	bt.modal.modal("hide");
        bt.sendPOST();
    });
    el.find("#cancelButton")[0].addEventListener('click', function () {
    	bt.modal.modal("hide");
    });
    el.find("#ogemaButton")[0].addEventListener('click', function () {
        bt.modal.modal("show");
    });
    this.sendGET();
}

// @Override
ButtonConfirm.prototype.update = function (data) {
	var el = $("#" + this.widgetID + ".ogema-widget");
	var html="<span id='buttonText'></span>";
	var confirmBtnMsg = "";
    if (data.hasOwnProperty("text")) {
        html += data.text;
        confirmBtnMsg = data.text;
    }
    el.find("#ogemaButton").html(html);
    if (data.hasOwnProperty("confirmMsg")) {
    	el.find("#confirmMsg").html(data.confirmMsg);
    }
    if (data.hasOwnProperty("confirmBtnMsg")) {
    	confirmBtnMsg = data.confirmBtnMsg;
    }
    el.find("#confirmButtonText").html(confirmBtnMsg);
    var cancelMsg = "Cancel";
    if (data.hasOwnProperty("cancelBtnMsg")) {
    	cancelMsg = data.cancelBtnMsg;
    }
    el.find("#cancelButtonText").html(cancelMsg);
    var title = "";
    if (data.hasOwnProperty("confirmPopupTitle")) {
    	title = data.confirmPopupTitle;
    }
    el.find("#ModalLabel").html(title);
    if (data.hasOwnProperty("disabled")) {
   	 	el.find("#ogemaButton").prop('disabled', data.disabled);
   }
};
