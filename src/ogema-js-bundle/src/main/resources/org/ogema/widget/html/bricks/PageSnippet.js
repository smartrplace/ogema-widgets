PageSnippet.prototype = new GenericWidget();
PageSnippet.prototype.constructor = PageSnippet;


function PageSnippet(servletPath, widgetID) {
 
    GenericWidget.call(this, servletPath, widgetID);
    this.snippet = $("#" + widgetID).find("#bodyDiv")[0];
    this.sendValueOnChange = true;
    // Array<[int|string, int, string, HtmlElement>, 
	//   where [object hash code or string value, type (0=widget, 1=html, 2=string), HTML/string value/widget id] 
    this.items = undefined;
    var tmp = this;
//    this.html = "";
    this.sendGET();  
//    console.log("Initializing page snippet!!");
    
}

PageSnippet.prototype.update = function (data, oldRequest, subwidgetsToBeRemoved) {
    
    if (data.hasOwnProperty("sendValueOnChange")) {
    	this.sendValueOnChange = data.sendValueOnChange;
    }
    if (!data.html && !data.items) {
    	console.error("Page snippet has no html!");
    	return;
    }
    
    var update = this.hasDataChanged(data,oldRequest,subwidgetsToBeRemoved);
     
    if (update && (oldRequest.html || data.html)) { 
    	this.snippet.innerHTML= data.html || "";	
    }
    if (update && data.items) {
		// update mode 1: update items carefully, without destroying existing subwidgets
		const oldItems /*: Array<[int|string, int, string, HtmlElement> */ = this.items || [];
		const forRemoval = oldItems.filter(item => data.items.find(i => i[0] === item[0] && i[1] === item[1]) === undefined);
		forRemoval.forEach(item => {
			oldItems.splice(oldItems.indexOf(item), 1);
			const oldEl = item[3];
			if (item[1] < 2) { // remove subwidgets
				const subwidgets = Array.from(oldEl.querySelectorAll("div.ogema-widget"));
				if (item[1] === 0)
					subwidgets.push(oldEl);
				subwidgets.forEach(w => delete ogema.widgets[w.id]);  // destroy subwidgets
			}
			oldEl?.remove(); 
		});
		let previousOldIdx = -1;
		const newItems /*: Array<[int, str, HtmlElement]> */= [];
		let previousElement = undefined;
		for (let idx=0; idx<data.items.length; idx++) {
			const item = data.items[idx];
			const oldIdx = oldItems.findIndex(i => i[0] === item[0] && i[1] === item[1]);
			if (oldIdx >= 0) {
				const oldItem = oldItems[oldIdx];
				const itemEl = oldItem[3];
				if (oldIdx !== previousOldIdx + 1) {
					// reappend at new position
					this.element.insertBefore(itemEl, previousElement?.nextElementSibling);
				} else {
					previousOldIdx++;
				}
				newItems.push(oldItem);
				previousElement = itemEl;
			} else {
				const type = item[1];
				const newElement = document.createElement("div");
				if (type === 0) { // widget
					newElement.classList.add("ogema-widget");
					newElement.id = item[2];
				} else if (type === 1) { // html
					newElement.innerHTML = item[2]; // XXX ugly wrapper
				} else { // string
					newElement.innerText = item[2];
				}
				item.push(newElement);
		        newItems.push(item);
		        this.element.insertBefore(newElement, previousElement?.nextElementSibling);
				previousElement = newElement;
			}
		}
		this.items = newItems;
	} else {
		this.items = undefined;
	}
    if (data.hasOwnProperty("background-img")) {
    	this.snippet.style["background-image"]="url('" + data["background-img"] + "')";
    }
    
//    console.log("Updating page snippet!");
	return update;
};

PageSnippet.prototype.hasDataChanged = function(newRequest,oldRequest,subwidgetsToBeRemoved) {
	if (!oldRequest)
		return true;
	if (!!oldRequest.html !== !!newRequest.html) // mode changed from html to items or v.v.
		return true;
	if (oldRequest.html && (oldRequest.html.length !== newRequest.html.length || oldRequest.html !== newRequest.html)) {	
		if (oldRequest.hasOwnProperty("subWidgets")) {
			subwidgetsToBeRemoved.push.apply(subwidgetsToBeRemoved, oldRequest.subWidgets);
		}
		return true;
	}
	if (oldRequest.items) // in this case we run the update procedure in any case, but we take care not to destroy subwidgets unnecessarily
		return true;
	return false;
}

PageSnippet.prototype.showExpiredMessage = function() { // FIXME remove subwidgets?
	 var snippet = this;
	 $.ajax({
        type: "GET",
        url: this.servletPath + "?expired=true",
        contentType: "application/json"
    }).done(function (result) {
    	var data = JSON.parse(result);
    	snippet.snippet.innerHTML= data.html;
    });
}

PageSnippet.prototype.showErrorHandlingMessage = function(msg) { // FIXME remove subwidgets?
	var html = "<br><button class=\"btn btn-primary\" onclick=\"window.location.reload(true);\">Reload page</button><br>" +
			"<br><div id=\"alert\" class=\"alert alert-success\"><div>" + msg +"</div></div>"; 
   	this.snippet.innerHTML= html;	
}

