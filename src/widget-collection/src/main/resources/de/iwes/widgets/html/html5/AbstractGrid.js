AbstractGrid.prototype = new GenericWidget();
AbstractGrid.prototype.constructor = AbstractGrid;


function AbstractGrid(servletPath, widgetID) {

    GenericWidget.call(this, servletPath, widgetID);
    this.cells = []; // an array of arrays; the indices should be viewed as keys of an object
    this.sendGET();
}

AbstractGrid.prototype.update = function (data0, oldRequest, subwidgetsToBeRemoved) {
	const data = data0.data;
	const el = this.element.querySelector("div");
	const directSubwidgets = Array.from(el.querySelectorAll(":scope > .ogema-widget"));
	// access individual widget using directSubwidgets.find(w => w.id === WIDGET_ID)
	try {
		directSubwidgets.filter(widget => {
			const id = widget.id;
			for (var i=0; i < data.length; i++) {
				const col = data[i];
				for (var j=0; j<col.length; j++) {
					const cell = col[j];
					if (cell !== null && cell.type === 2 && cell.content === id)
						return false;
				}
			}
			return true;
		}).forEach(widget => {
			// not foudn
			subwidgetsToBeRemoved.push(widget.id);
			// remove nested widgets
			Array.from(widget.querySelectorAll(".ogema-widget")).forEach(w => subwidgetsToBeRemoved.push(w.id));
			widget.remove();
		});
	} catch (e) {
		console.log("Error deleting subwidgets",e);
	}
	// clean TODO check for efficiency
	const range = document.createRange();
	range.selectNodeContents(el);
	// or detach?
	range.deleteContents();
	
	var widgetsChanged = subwidgetsToBeRemoved.length > 0;
	const fragment = document.createDocumentFragment();
	for (var i=0; i < data.length; i++) {
		const col = data[i];
		for (var j=0; j<col.length; j++) {
			const cell = col[j];
			const type = cell === null ? -1 : cell.type;
			let div;
			if (type === 2) {
				const id = cell.content;
				const existing = directSubwidgets.find(w => w.id === id);
				if (typeof existing !== "undefined") {
					div = existing;
				} else {
					div = document.createElement("div");
					div.classList.add("ogema-widget");
					div.id = cell.content;
					widgetsChanged = true;
				}
			}
			else if (type === 0 || type === 1) {
				div = document.createElement("span");
				if (cell.type === 0)
					div.innerText = cell.content;
				else 
					div.innerHTML = cell.content;
			} 
			else {
				div = document.createElement("div");
				div.dataset.empty = "true";
			}
			if (cell.hasOwnProperty("row"))
				div.dataset.row = cell.row;
			if (cell.hasOwnProperty("col"))
				div.dataset.col = cell.col;
			if (cell.hasOwnProperty("area")) {
				div.style.gridArea = cell.area;
				// required so we can use css selectors to target the cell
				div.dataset.area = cell.area;
			}
			else
				div.dataset.rowtype = (i % 2 === 0) ? "0" : "1"; // even / odd
			fragment.appendChild(div);
		}
	}
	el.appendChild(fragment);
	if (data0.hasOwnProperty("colTemplate"))
		el.style.gridTemplateColumns = data0.colTemplate;
	if (data0.hasOwnProperty("rowTemplate"))
		el.style.gridTemplateRows = data0.rowTemplate;
	if (data0.hasOwnProperty("colGap"))
		el.style.gridColumnGap = data0.colGap;
	if (data0.hasOwnProperty("rowGap"))
		el.style.gridRowGap = data0.rowGap;	
	return widgetsChanged;
};

/**
 * 
 */
AbstractGrid.prototype.findWidget = function(data, widgetId) {
	for (var i=0; i < data.length; i++) {
		var col = data[i];
		for (var j=0; j<col.length; j++) {
			var cell = col[j];
			if (cell !== null) {
				if (cell === "<div id=\"" +widgetId + "\" class=\"ogema-widget\"/>") {
					return 
				}
			}
		}
	}
}




