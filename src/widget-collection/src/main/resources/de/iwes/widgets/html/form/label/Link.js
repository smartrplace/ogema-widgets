Link.prototype = new GenericWidget();
Link.prototype.constructor = Link;

function Link(servletPath, widgetID) {
    GenericWidget.call(this, servletPath, widgetID);
    this.sendGET();
}

Link.prototype.update = function (data) {
	const a = this.element.querySelector(":scope >a");
	if (!data.hasOwnProperty("text")) {
		a.innerText = "";
		a.href = "";
		a.target = "";
	} else {
		a.innerText = data.text;
		a.href = data.url;
		if (data.hasOwnProperty("newTab") && data.newTab)
			a.target = "_blank";
		else
			a.target = "";
	}
};




  