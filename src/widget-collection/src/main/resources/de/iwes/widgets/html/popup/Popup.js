Popup.prototype = new GenericWidget();
Popup.prototype.constructor = Popup;

function Popup(servletPath, widgetID) {
    GenericWidget.call(this, servletPath, widgetID);
    this.done = true;
    this.modal = $("#" + widgetID + " #modal");
    this.modalDialog = this.modal.find("#dialog");

    var tmp = this;
/*    this.modal.change(function () {
        tmp.sendPOST();
    }); */ 
    this.sendGET();  // required! (FIXME)
/*    setTimeout(function() { // hack
    	tmp.done = true;
    },3000); */
}

Popup.prototype.update = function (data, oldRequest, subwidgetsToBeRemoved) {
    this.addListener();
    
    if (data.hasOwnProperty("title")) {
        this.modalDialog.find("#ModalLabel").html(data.title);
    }

	var update = this.hasDataChanged(data,oldRequest,subwidgetsToBeRemoved);
	if (!update && data.hasOwnProperty("forceUpdate")) {
		update = data.forceUpdate;
	}
	
	if (update) {	
    	this.modalDialog.find("#ModalHeader").html(data.headerHTML);
    	this.currentHeader = data.headerHTML;
    	this.modalDialog.find("#ModalBody").html(data.bodyHTML);
    	this.currentBody = data.bodyHTML;
    	this.modalDialog.find("#ModalFooter").html(data.footerHTML);
    	this.currentFooter = data.footerHTML;
    }
    return update;
};

Popup.prototype.hasDataChanged = function(newRequest,oldRequest,subwidgetsToBeRemoved) {
	if (!oldRequest || !oldRequest.hasOwnProperty("bodyHTML")) {
		if (newRequest.hasOwnProperty("bodyHTML"))	return true;
		else return false;
	}
	if (!newRequest.hasOwnProperty("bodyHTML")) {	
		if (oldRequest.hasOwnProperty("subWidgets")) {
			subwidgetsToBeRemoved.push.apply(subwidgetsToBeRemoved, oldRequest.subWidgets);
		}
		return false;   
	}  
	// check whether old and new html are equal
	if (oldRequest.bodyHTML.length !== newRequest.bodyHTML.length
			|| oldRequest.headerHTML.length !== newRequest.headerHTML.length
			|| oldRequest.footerHTML.length !== newRequest.footerHTML.length 
			|| oldRequest.bodyHTML !== newRequest.bodyHTML
			|| oldRequest.headerHTML !== newRequest.headerHTML
			|| oldRequest.footerHTML !== newRequest.footerHTML) {
		if (oldRequest.hasOwnProperty("subWidgets")) {
			subwidgetsToBeRemoved.push.apply(subwidgetsToBeRemoved, oldRequest.subWidgets);
		}
		return true;
	}
	return false;
}

Popup.prototype.setVisibility = function (visible) {
//    if (this.done) {  // FIXME does not work reliably
        if (visible) {
            //this.sendGET();
            this.modal.modal("show");
        }
        else {
            this.modal.modal("hide");
        }
        this.done = false;
//    }
};

Popup.prototype.showWidget = function () {
	try {
    	this.element.style.display = "inline";
    	this.setVisibility(true);
    } catch (e) { console.log(e); }
};

Popup.prototype.hideWidget = function () {
	try {
    	this.element.style.display = "none";
    	this.setVisibility(false);
    } catch (e) { console.log(e); }
};

Popup.prototype.addListener = function () {
    var tmp = this;
//    this.modal.on('hide.bs.modal', function (e) {
//    });
//    this.modal.on('show.bs.modal', function (e) {
//    });

    this.modal.on('shown.bs.modal', function (e) {
        tmp.done = true;
    });
    this.modal.on('hidden.bs.modal', function (e) {
        tmp.done = true;
    });
};
