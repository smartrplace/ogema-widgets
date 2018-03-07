DataTable.prototype = new GenericWidget();
DataTable.prototype.constructor = DataTable;

/**
* Relies on DataTables library (http://datatables.net/)
*/
function DataTable(servletPath, widgetID) {

    GenericWidget.call(this, servletPath, widgetID);
    this.table = $("#" + this.widgetID).find("#datatable");
    this.resultingObject = null;
	this.data = [];
	this.currentRows = [];
    this.sendGET();
}

DataTable.prototype.update = function (response) {
	if (!response.hasOwnProperty("data") || response.data.length === 0 || response.data == this.data) return; // FIXME last condition not working
	console.log("new data table: ",response); // FIXME
	if (this.resultingObject) {
		this.resultingObject.destroy(); // FIXME avoid resetting paging & ordering options
	}
	this.resultingObject = this.table.DataTable( response );
	this.data = response.data;
	var tableObject = this.resultingObject;
	var thisObj = this;
	tableObject.off( 'select' );
		tableObject
		        .on( 'select', function ( e, dt, type, indexes ) {
		            thisObj.currentRows = tableObject.rows( indexes ).data().toArray();  
		            thisObj.sendPOST();
		            console.log("row selected", thisObj.currentRows);
		        } );
	
	
/*	if (response.hasOwnProperty("connectWidgets")) { 
		var triggerWidgets = [];
		var triggerActions = [];
		for (var i=0;i<response.connectWidgets.length;i++){
		    var obj = response.connectWidgets[i];
			var actiontype = obj.triggeringAction;
			if (actiontype !== "row_selected") continue;
			try {
				var widget2 = widgets[obj.widgetID2];
				if (typeof widget2 !== "undefined") {
					triggerWidgets.push(widget2);
					triggerActions.push(obj.triggeredAction); // TODO add arguments!
				}
			} catch (e) {}
		}
		tableObject.off( 'select' );
		tableObject
		        .on( 'select', function ( e, dt, type, indexes ) {
		            thisObj.currentRows = tableObject.rows( indexes ).data().toArray();  
		            for (var i=0;i<triggerWidgets.length;i++) {
		            	try {
			            	var w = triggerWidgets[i];
			            	var a = triggerActions[i];
			            	w[a]();
			            } catch (e) {}
		            }
		            console.log("row selected", thisObj.currentRows);
		        } );
	}*/
}

DataTable.prototype.getSubmitData = function () {
    // override; adding content to data
    var data = {};
    data.currentRows = this.currentRows;
    return data;
};
