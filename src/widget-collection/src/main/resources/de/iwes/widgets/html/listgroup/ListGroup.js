ListGroup.prototype = new GenericWidget();
ListGroup.prototype.constructor = ListGroup;

function ListGroup(servletPath, widgetID) {
    GenericWidget.call(this, servletPath, widgetID);
    this.sendGET();
}

// FIXME use document fragment, detach old elements and re-append -> this does not work properly in a dynamic situation yet

// we do not know the subwidgets explicitly (since they can be recursive), hence
// we need to trigger a complete new update cycle (unless the entries did not change at all)
ListGroup.prototype.update = function (data, oldRequest, subwidgetsToBeRemoved) {
    var el = $(document.querySelectorAll("#" + this.widgetID + ".ogema-widget")[0]).find("#listBody");
    var entries = data.data;

	var existingEntries = el.find(">li");
	var ids = [];
	for (var j=0;j<existingEntries.length;j++) {
		const entry = existingEntries[j];
		if (entry.firstChild.classList.contains("ogema-widget")) {
			var id = entry.firstChild.id;
			//var id = entry.id;
			if (entries.filter(en => en.type === 2).map(en => en.content).indexOf(id) < 0) {
				subwidgetsToBeRemoved.push(id);
				entry.remove();  // does not change the list size, no "ConcurrentModificationException"
			}
			else 
				ids.push(id);
		} else {  // FIXME retain elements that shall not be deleted
			entry.querySelectorAll(".ogema-widget").forEach(wel => {
				const id = wel.id;
				subwidgetsToBeRemoved.push(id);
			});
			entry.remove();
		}
	}

    for (var i=0;i<entries.length;i++) {
    	var entry = entries[i];
    	if (entry.type === 2) {
    		if (ids.indexOf(entry.content) < 0) {
	    		// add new entry
	    		var newListEntry = document.createElement("li");
	    		var widgetDiv = document.createElement("div");
	    		newListEntry.appendChild(widgetDiv);
	    		widgetDiv.id = entry.content;
	    		widgetDiv.classList.add("ogema-widget");
	    		el.append(newListEntry);
	    		//newListEntry.innerHtml="<div id=\"" + entry + "\" class=\"ogema-widget\"></div>";
    		}
    	} else {
    		// add new entry
    		const newListEntry = document.createElement("li");
    		if (entry.type === 1)
    			newListEntry.innerHTML = entry.content;
    		else
    			newListEntry.innerText = entry.content;
    		el.append(newListEntry);
    	}
    }
    
    return true; // load new subwidgets and remove obsolete ones.
};

//ListGroup.prototype.getSubmitData = function() {
//	var textFieldValue = $("#" + this.widgetID).find("#textareaTag").val();
//    return textFieldValue;
//}




  