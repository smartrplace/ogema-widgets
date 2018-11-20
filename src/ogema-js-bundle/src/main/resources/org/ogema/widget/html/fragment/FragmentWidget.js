FragmentWidget.prototype = new GenericWidget();
FragmentWidget.prototype.constructor = FragmentWidget;

function FragmentWidget(servletPath, widgetID) {
    GenericWidget.call(this, servletPath, widgetID);
    this.elements = [];
    this.sendGET();  
}

FragmentWidget.prototype.update = function (data, oldRequest, subwidgetsToBeRemoved) {
	const subExists = this.elements.map(e => {
		const subWidgetExists = Array.from(e.querySelectorAll(".ogema-widget")).map(el => {
			delete ogema.widgets[el.id];
			el.remove();
            return true;
		}).reduce((a,b) => a || b, false);
		// TODO destroy toplevel widget itself
		return subWidgetExists;
	}).reduce((a,b) => a || b, false);
	const html = data.html;
	if (html.length === 0)
		return subExists;
	const frag = document.createDocumentFragment();
	this.elements = html.map(item => {
		const el = item.type === 0 ? document.createTextNode(item.content) : document.createElement(item.tag);
		if (item.type !== 0) {
			el.innerHTML = item.content;
			if (item.type === 2)
				el.classList.add("ogema-widget");
			else if (item.type === 1 && item.hasOwnProperty("attributes")) 
				Object.keys(item.attributes).forEach(key => el.setAttribute(key, item.attributes[key]));
		}
		frag.append(el);
		return el;
	});
	this.element.parentNode.insertBefore(frag, this.element.nextSibling);
	return subExists || this.elements.length > 0;
};


