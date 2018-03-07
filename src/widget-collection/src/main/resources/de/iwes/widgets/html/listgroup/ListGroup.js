ListGroup.prototype = new GenericWidget();
ListGroup.prototype.constructor = ListGroup;

function ListGroup(servletPath, widgetID) {
    GenericWidget.call(this, servletPath, widgetID);
    this.sendGET();
}

// we do not know the subwidgets explicitly (since they can be recursive), hence
// we need to trigger a complete new update cycle (unless the entries did not change at all)
ListGroup.prototype.update = function (data, oldRequest, subwidgetsToBeRemoved) {
    var el = $(document.querySelectorAll("#" + this.widgetID + ".ogema-widget")[0]).find("#listBody");
    var entries = data.data;

	var existingEntries = el.find(">li");
	var ids = [];
	for (var j=0;j<existingEntries.length;j++) {
		var entry = existingEntries[j];
		var id = entry.firstChild.id;
		//var id = entry.id;
		if (entries.indexOf(id) < 0) 
			entry.remove();  // does not change the list size, no "ConcurrentModificationException"
		else 
			ids.push(id);
	}

    for (var i=0;i<entries.length;i++) {
    	var entry = entries[i];
    	if (ids.indexOf(entry) < 0) {
    		// TODO add new entry
    		var newListEntry = document.createElement("li");
    		el.append(newListEntry);
    		var widgetDiv = document.createElement("div");
    		newListEntry.appendChild(widgetDiv);
    		widgetDiv.id = entry;
    		widgetDiv.classList.add("ogema-widget");
    		//newListEntry.innerHtml="<div id=\"" + entry + "\" class=\"ogema-widget\"></div>";
    	}
    }
    
    return true; // load new subwidgets and remove obsolete ones.
};

//ListGroup.prototype.getSubmitData = function() {
//	var textFieldValue = $("#" + this.widgetID).find("#textareaTag").val();
//    return textFieldValue;
//}




  