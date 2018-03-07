/**
 * The js Function has to be named as the Widget-java File, otherwise it will not be 
 * instantiated on client-side
 * 
 * The servletPath is unique for each widgetId, so every Widget is (per default)
 * knowing where its Java counterpart (Servlet) is running.
 * 
 * @param {type} servletPath
 * @returns {undefined}
 */
DynamicTable2.prototype = new GenericWidget();			// inherit from GenericWidget
DynamicTable2.prototype.constructor = DynamicTable2;	// have to reset constructor for inheriting class

function DynamicTable2(servletPath, widgetID) {     // constructor
	GenericWidget.call(this,servletPath,widgetID);
	var clw = this;
	//console.log("running ComplexTableWidget under "+servletPath);
	this.plotContainer = $("#"+widgetID);
	this.table = this.plotContainer.find("#table").get()[0];
	var isPolling = false;
//	this.html = {};
	this.rows = [];
	this.cols = [];
//	this.evaluateParameters = function(object) {}; 
//	this.setRows = function(object,rows,cols) {};  
//	this.getRows = function(object) {};
	this.sendGET();
}

//@Override
DynamicTable2.prototype.update = function(data, oldRequest, subwidgetsToBeRemoved) {
	//console.log("Updating ComplexTableWidget",data);
//	if (data.hasOwnProperty('options')) {
//		this.evaluateParameters(data.options);
//	}
	var changed = false;
	if (data.hasOwnProperty('html') && data.hasOwnProperty('rows') && data.hasOwnProperty('cols')) {
		console.log("Received new set of rows",data.html);
		changed = this.setRows(data.html,data.rows,data.cols,data.options);
//		ogema.reloadWidgets(); 
	}
	return changed;
}

/**
* @return: boolean:
* 	true: changed
*	false: unchanged
*/
DynamicTable2.prototype.setRows = function(html, rows, cols, options) {
	var changed = false;
	var oldRows = this.rows;
	var oldCols = this.cols;
	for (var i=0;i<oldRows.length;i++) {
		var oldRow = oldRows[i];
		if (rows.indexOf(oldRow) < 0) { // remove row
			var row = this.plotContainer.find("#" + oldRow);
			row.detach();  // FIXME use row.remove(); ?
//			var oldHtml = this.html[oldRow];
			//it doesn't seem to be necessary to explicity mark any subwidgets for removal
			changed = true;
		}
	}	
	for (var i=0;i<oldCols.length;i++) {
		var oldCol = oldCols[i];
		if (cols.indexOf(oldCol) < 0) { // remove col
			var col = this.plotContainer.find("#" + oldCol);
			col.detach();
			//it doesn't seem to be necessary to explicity mark any subwidgets for removal
			changed = true;
		}
	}
	for (var i=0;i<rows.length;i++) {
		var row = rows[i];
		if (oldRows.indexOf(row)<0) { // add row
			var r = this.table.insertRow();
			r.id = row;
			r.classList.add("row");
			for (var j=0;j<cols.length;j++) {
				var col = cols[j];
				var c = r.insertCell(j);
				c.innerHTML = html[row][col];
				c.classList.add("complex-table-cell");
				c.classList.add("col");
				c.id = col;
				try {
					c.classList.add(options.colClass[col]);
				} catch (e) {}
			}
			changed = true;
		}
	}
	this.rows = rows;
	this.cols = cols;
//	this.html = html;  
	return changed;
}