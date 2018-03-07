/** 
 * Only one instance should be created (think of a static class). Actual groups are
 * identified by their groupId. Every page has one special group, with id "all".
 * 
 */
function WidgetGroups(servletPath, boundPagePath) {   

	/** think of a subclass */
	function WidgetGroup(groupId) {
	
		this.groupId = groupId;
		this.widgetsList = [];
	
	}

    this.servletPath = servletPath;
    this.boundPagePath = boundPagePath;
    var wg = this;
    var pollingGroups = {};

    /**** public methods *****/

	/**
	* returns a promise for a group; use as
	* widgetGroups.getGroupPromise(groupId).done(function(group) {
	*   for (var i=0;i<group.widgetsList.length;i++) {
	*		var widget = widgets[group.widgetsList[i]];
	*		// do something with group widget
	*	}
	* });
	*/
	this.getGroupPromise = function(id) {
		var deferred = $.Deferred();
		var self = this;
		if (typeof ogema.pageInstance === "undefined") {
        	return;
        }
		$.ajax({
	        type: "GET",
	        url: this.servletPath + ogema.getParameters() + "&boundPagePath=" + this.boundPagePath +  "&getGroup=" + id, 
	        contentType: "text/plain"
    	}).done(function(response) {
    		var group = new WidgetGroup(id);
    		group.widgetsList = response;
    		deferred.resolveWith(self,[group]);
    	}).fail(function(response) {
    		deferred.rejectWith(self,[response]);
    	});
    	return deferred;	
	}
	
	// content-type?
	this.sendGET = function(groupId, deferred, excludedTriggers, excludedGroupTriggers, triggeredBy, ignoreTriggerActions, isPollingRequest) {
		if (typeof ogema.pageInstance === "undefined" || ogema.pageInstance === "") {
			setTimeout(function() { wg.sendGET(groupId, deferred, excludedTriggers, excludedGroupTriggers, triggeredBy, ignoreTriggerActions, isPollingRequest) },500);
        	return;
        }	
		var servletPath = this.servletPath + ogema.getParameters()+ "&groupId=" + groupId + "&boundPagePath=" + this.boundPagePath;
		if (typeof triggeredBy !== "undefined")
			servletPath += "&triggeredBy=" + triggeredBy;
		$.ajax({
	        type: "GET",
	        url: servletPath,
	        contentType: "text/plain"
    	}).done(function(response) {
    		Object.keys(response).forEach(function(widgetId) {
    			if (widgetId === "polling") return; // filter
    			try {
    				ogema.widgets[widgetId].handleWidgetGET(response[widgetId][0], false, ignoreTriggerActions, excludedTriggers, excludedGroupTriggers);
    			} catch (e) {
    				//console.error("Widget not found",widgetId,e); // happens particularly at load time
    			}
    		});
    		if (typeof deferred !== "undefined")
    			deferred.resolve();
    		var poll = response.polling;
    		if (typeof poll !== "undefined" && poll > 0) {
    			if (!pollingGroups[groupId] || isPollingRequest)  { // either not polling yet, or this was a poll request
    				pollingGroups[groupId] = true;
    				// TODO send poll request
    				setTimeout(function() {
    					wg.sendGET(groupId, undefined, undefined, undefined, undefined, false, true);
    				}, poll);
    			}    			
    		}
    		else {
    			pollingGroups[groupId]  = false;
    		}
    		
    		
    	})
	};
	
	// FIXME send common POST!
	this.sendPOST = function(groupId, deferred, excludedTriggers, excludedGroupTriggers, triggeredBy) {  
			// FIXME requests still executed sequentially // -> ok, if triggered by prePOST, this is replaced by getSubmitData 
			 // -> potential problem: if triggered by prePOST, actions triggered by POST are not executed anymore.
		var groupPromise = this.getGroupPromise(groupId);
		groupPromise.done(function(group) {
			var deferreds = [];
			for (var i=0;i<group.widgetsList.length;i++) {
				try {
					var widget = ogema.widgets[group.widgetsList[i]];
					var widgetDeferred = $.Deferred(); 
					deferreds.push(widgetDeferred);
					widget.sendPOST(widgetDeferred, excludedTriggers, excludedGroupTriggers, triggeredBy);
				} catch (e) {
					console.error("Error sending POST for widget ",group.widgetsList[i],e);
				}				
			}
			if (typeof deferred !== "undefined") {
				$.when.apply($, deferreds).done(function() {  // TODO fail
			    	deferred.resolve();
			    });
		    }
		});
	}; 
	
	// here widgets need to be aware of their group membership themselves, contrary to the other
	// functions defined 
	this.getSubmitData = function(groupId) {	
		var data = {};
		Object.keys(ogema.widgets).forEach(function(widgetId) {
			var widget = ogema.widgets[widgetId];
			if (widget.groups.indexOf(groupId) < 0) return;
			data[widgetId] = {};
			data[widgetId].data = widget.getSubmitData();
		});
		return data;
	}
	
	
	this.showWidget = function (groupId) {
		var groupPromise = this.getGroupPromise(groupId);
		groupPromise.done(function(group) {
				for (var i=0;i<group.widgetsList.length;i++) {
				try {
					var widget = ogema.widgets[group.widgetsList[i]];
					widget.showWidget();
				} catch (e) {
					console.error("Error sending POST for widget",e);
				}				
			}
		});
	};

	this.hideWidget = function (groupId) {
		var groupPromise = this.getGroupPromise(groupId);
		groupPromise.done(function(group) {
			for (var i=0;i<group.widgetsList.length;i++) {
				try {
					var widget = ogema.widgets[group.widgetsList[i]];
					widget.hideWidget();
				} catch (e) {
					console.error("Error sending POST for widget",e);
				}				
			}
		});
	};


}