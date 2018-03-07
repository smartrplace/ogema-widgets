Button.prototype = new GenericWidget();	// inherit from GenericWidget
Button.prototype.constructor = Button;	// have to reset constructor for inheriting class

function Button(servletPath, widgetID) {           // constructor
    GenericWidget.call(this, servletPath, widgetID);	// call superconstructor
    if (!widgetID) { // required so other widgets can inherit from this
    	return;
    }
    var bt = this;
    $("#" + widgetID + ".ogema-widget").find("#ogemaButton")[0].addEventListener('click', function () {
        bt.sendPOST();
    });
    this.sendGET();
}

// @Override
Button.prototype.update = function (data) {
	var baseEl = $("#" + this.widgetID + ".ogema-widget");
    if (data.hasOwnProperty("css")) {
        baseEl.find("#ogemaButton").removeAttr("class").addClass(data.css);
    }
    if (data.hasOwnProperty("text")) {
        var html = "<span id='buttonText'></span>" + data.text;
        baseEl.find("#ogemaButton").html(html);
    }
    if (data.hasOwnProperty("glyphicon")) {
    	 baseEl.find("#buttonText").removeAttr("class").addClass(data.glyphicon);
    }
    if (data.hasOwnProperty("disabled")) {
    	 baseEl.find("#ogemaButton").prop('disabled', data.disabled);
    }
};

/*
 <button class="btn btn-lg btn-primary btn-block" id="ogemaButton">
 <span id="buttonText">blabla</span>
 </button>
 
 <button class="btn btn-lg btn-primary btn-block" id="ogemaButton">
 <span id="buttonText"></span>blabla
 </button>
 
 
 
 */