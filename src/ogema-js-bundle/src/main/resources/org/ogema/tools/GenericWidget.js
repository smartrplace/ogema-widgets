/**
 * This file is part of the OGEMA widgets framework.
 *
 * OGEMA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OGEMA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OGEMA. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2014 - 2018
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
 */
function GenericWidget(servletPath, widgetID, pollingInterval) {  // constructor of abstract class GenericWidget

    this.widgetID = widgetID;
    this.servletPath = servletPath;
    this.element = $("#" + widgetID + ".ogema-widget")[0];
    this.groups = [];
    this.pollingInterval = -1;
    var triggeredActionGET = {};    // use widgetID as key, object with keys 'id': function to be executed as value (function name), 'args': arguments (object) 
    var triggeredActionPOST = {};
    this.governingWidget = false;
    var triggeredActionPrePOST = {};
    var triggeredGroupActionGET = {};    // use widgetGroupID as key, object with keys 'id': function to be executed as value (function name), 'args': arguments (object) 
    var triggeredGroupActionPOST = {};
    var triggeredGroupActionPrePOST = {};
    var gw = this;
    var contentType = "application/json";
    var isPolling = false;
    var artificialAttributes = [];
    var cssMap = {};
    var styles = {};
    var requestPending = false;
    var waitForPendingRequest = false;
    var initialGroupUpdateSet = false;
    this.isDynamicWidget = false;
    this.initialUpdated = false;        // is used to see how the widgets should be updated (through bundled information or own its own via ajax request)
    this.lastGetRequest = {};		  // used to decide whether subwidgets need to be reloaded; only relevant if this.isDynamicWidget == true
    // map<event type: array of listeners>
    this.customEventListeners = {}; 

    /**** public methods that cannot be changed	*****/

    //handle widget update in seperate function due to possible update via bundled information on startup
    this.handleWidgetGET = function (text, isPollRequest, ignoreTriggerActions, excludedTriggers, excludedGroupTriggers) {
        //console.log("GET result for widget " + widgetID, text, textStatus);
        //if (!gw.element) gw.element = document.getElementById(widgetID);
//        var widgetHtmlElement = document.getElementById(widgetID);
        gw.element.style.visibility = "visible";
        //$("#" + widgetID).animate( { opacity : 1 } , 500);
        if (!text) {   // handle empty strings
            text = "{}";
        }
        var result = {};
        if (text.constructor === "X".constructor) {
            result = JSON.parse(text);
        } else {
            result = text;
        }
        if (result.hasOwnProperty("expired") && result.expired) {
    		try {	// currently only works for WidgetPageSimple
    			ogema.widgets.rootWidget.showExpiredMessage();
    		} catch (e) {}
    		return;
    	}
    	if (result.hasOwnProperty("waitForPendingRequest")) {
    		waitForPendingRequest = result.waitForPendingRequest;
    	}
    	
    	if (result.hasOwnProperty("widgetGroups")) {
    		gw.groups = result.widgetGroups;
    	} else {
    		gw.groups = [];
    	}
    	if (result.hasOwnProperty("preloadGroup") && result.preloadGroup && !initialGroupUpdateSet) {
			initialGroupUpdateSet = true;
			if (ogema.widgetLoader.groupsToBePreloaded === undefined)
				ogema.widgetLoader.groupsToBePreloaded = [];
			ogema.widgetLoader.groupsToBePreloaded.push(this.widgetID);	
		}
    	// clean up
    	Object.keys(gw.customEventListeners).forEach(function(key) {
    		var arr = gw.customEventListeners[key];
    		for (var i=0;i<arr.length;i++) {
    			gw.element.removeEventListener(key,arr[i]);
    		}
    	});
    	gw.customEventListeners = {};
//    	var otherEvents = {};
        if (result.hasOwnProperty("connectWidgets")) {
            //console.log("Connecting widgets!",result.connectWidgets);
            for (var i = 0; i < result.connectWidgets.length; i++) {
                var cnc = result.connectWidgets[i];
                if (cnc.triggeringAction === 'GET') {
              		  triggeredActionGET[cnc.widgetID2] = cnc;
                }
                else if (cnc.triggeringAction === 'POST') {
					  triggeredActionPOST[cnc.widgetID2] = cnc;
                }
                else if (cnc.triggeringAction === 'prePOST') {
                	  triggeredActionPrePOST[cnc.widgetID2] = cnc;
                }
                else {    // here we expect e.g. cnc.triggeringAction === 'click'    (or 'change', 'mouseover', etc.)
                	// this funny construction helps circumvent some extremely counterintuitive 
                	// properties of closures in javascript; passing an anonymous function as event listener which refers 
                	// to cnc.widgetID2 does not work (the action for the last passed widget is executed multiple times).
                	var evt = (function(widget,action){
                		var w = widget;
                		var a = action;
                		return {
                			trigger: function(){ogema.widgets[w][a]()}
                		}
                	})(cnc.widgetID2,cnc.triggeredAction);
//                	console.log("Adding event trigger " + cnc.triggeringAction + " for " + cnc.widgetID2);
                	gw.element.addEventListener(cnc.triggeringAction, evt.trigger);
                	if (!gw.customEventListeners.hasOwnProperty(cnc.triggeringAction)) 
                		gw.customEventListeners[cnc.triggeringAction] = [];
                	gw.customEventListeners[cnc.triggeringAction].push(evt.trigger);
                }
            }
        }
        if (result.hasOwnProperty("connectGroups")) {
            //console.log("Connecting widgets!",result.connectGroups);
            for (var i = 0; i < result.connectGroups.length; i++) {
                var cnc = result.connectGroups[i];
                if (cnc.triggeringAction === 'GET') {
                // TODO test
                	  triggeredGroupActionGET[cnc.groupID2] = cnc;  // not working for groupds
                }
                else if (cnc.triggeringAction === 'POST') {
					  triggeredGroupActionPOST[cnc.groupID2] = cnc;
                }
                else if (cnc.triggeringAction === 'prePOST') {
                	triggeredGroupActionPrePOST[cnc.groupID2] = cnc;
                }
                // TODO not yet implemented
                else {    // here we expect e.g. cnc.triggeringAction === 'click'    (or 'change', 'mouseover', etc.)
                    gw.element.addEventListener(cnc.triggeringAction, function () {  // TODO ensure compatibility with ind. widgets listeners
                        // widgets[cnc.groupID2][cnc.triggeredAction]();  // call triggered function // TODO need to iterate over group widgets!
                    	console.error("Triggering custom group actions not yet implemented");
                    });
                }
            }
        }
        if (result.hasOwnProperty("dynamicWidget")) {
            gw.isDynamicWidget = result.dynamicWidget;
        }

        if (result.hasOwnProperty("pollingInterval")) {
            gw.pollingInterval = result.pollingInterval;
        }
        for (var i = 0; i < artificialAttributes.length; i++) {    // brute force... remove all attributes, and reset those that are sent later. TODO To be improved...
            gw.element.removeAttribute(artificialAttributes[i]);
        }
        artificalAttributes = [];
        if (result.hasOwnProperty("attributes")) {
            Object.keys(result.attributes).forEach(function (attr) {
                gw.element.setAttribute(attr, result.attributes[attr]);
                artificalAttributes.push(attr);
            });
        }
/*        if (result.hasOwnProperty('params')) {  // TODO required?
            var params = ''; // FIXME here params is a local variable, in 'res' block below it is global -> ?
            var isInit = false;
            Object.keys(result.params).forEach(function (key) {
                if (isInit) {
                    params = params + "&";
                } else {
                    params = '?';
                    isInit = true;  
                }
                params = params + key + "=" + result.params[key];
            });
        }
        if (result.hasOwnProperty('res')) {
            if (isInit) {
                params = params + "&";
            } else {
                params = '?';
                isInit = true;  
            }
            params = params + "res=" + result.params['res'];
        } */ 
        if (result.hasOwnProperty('governingWidget')) {
            gw.governingWidget = result.governingWidget;
        }

        // css stuff after calling the update function!
        if (result.hasOwnProperty("cssMap") && result.cssMap != cssMap) {
            try {
                var css = $("#globalWidgetsCSSEl");
                if (css.length > 0) {
                    cssMap = result.cssMap;
                    var sheet = css[0].sheet;
                    // first remove all CSS rules for this particular widget, otherwise they'd accumulate
                    var arr = sheet.cssRules;
                    var N = arr.length;
                    for (var i = 0; i < N; i++) {
                        if (arr[N - i - 1].cssText.indexOf("#" + gw.widgetID + ".ogema-widget") === 0) {
                            sheet.deleteRule(N - i - 1);
                        }
                    }

                    Object.keys(result.cssMap).forEach(function (className) {
                        var propList = "{";
                        Object.keys(result.cssMap[className]).forEach(function (key) {
                            var prop = result.cssMap[className][key];
                            propList = propList + key + ": " + prop + "; ";
                        });
                        propList = propList + "}";
                        sheet.insertRule("#" + gw.widgetID + ".ogema-widget " + className + " " + propList, 0);
                    });
                }
            } catch (e) {
                console.log("Error updating style sheets for widget " + gw.widgetID, e);
            }
        }
        
        //  widget update must be done before triggeringActions, so that a widget can reasonably trigger actions on itself (e.g. FileDownload: set File first, then download)
        var subwidgetsToBeRemoved = [];  // this variable should be updated by the widget-specific update function; only relevant for dynamicWidgets (set flag dynamicWidget = true)	
        var updateSubwidgets = gw.update(result, gw.lastGetRequest, subwidgetsToBeRemoved);	  // updateSubwidgets is ignored for non-dynamic widgets
        
        // set classes of subobjects // must be called after gw.update, since otherwise elements to be styled may not yet be available
        if (result.hasOwnProperty("styles") && result.styles != styles) {
        	gw.setStyles(result.styles);
            styles = result.styles;
        }
        if (result.hasOwnProperty("visible")) { // must be executed before triggering other actions, since they might influence the visibility as well
            if (!result.visible) {
                gw.hideWidget();
            } else {
                gw.showWidget();
            }
        }
  //
        
        if (typeof ignoreTriggerActions === "undefined") {
        	ignoreTriggerActions = false;
        }
        if (!ignoreTriggerActions) {
/*
	        Object.keys(triggeredActionGET).forEach(function (triggerWidget) {
	            try {
	                var widget2 = ogema.widgets[triggerWidget];                           //note: widgets is a global variable defined in loadWidgets.js script
	                console.log("Triggering action on GET of " + widgetID + " for widget " + triggerWidget);
	                widget2[triggeredActionGET[triggerWidget].triggeredAction].apply(widget2, triggeredActionGET[triggerWidget].args);           // call triggered function   //TODO test
	            } catch (e) {
	            }
	        });
	        Object.keys(triggeredGroupActionGET).forEach(function (groupId) {
	            try {
	            	// need to treat differently case of triggered GET and POST/other requests?		
	            	console.log("Triggering action on GET of group ", groupId);
	            	ogema.widgetGroups[triggeredGroupActionGET[groupId].triggeredAction].apply(ogema.widgetGroups, triggeredGroupActionGET[groupId].args);
	            } catch (e) {
	            	console.error("Error triggering widget group GET",groupId,e);
	            }
	        });
*/
        	this.trigger(triggeredActionGET, triggeredGroupActionGET, 0, excludedTriggers, excludedGroupTriggers);
	    }
        if (gw.pollingInterval > 0 && !ogema.widgetLoader.pollingStopped && (!isPolling || isPollRequest)) {
            //if (!isPolling) {
                //console.log("Starting to poll " + gw.widgetID);
            //}
            isPolling = true;
            setTimeout(function () {
//                console.log("Polling", gw.pollingInterval);
//                gw.sendGET(undefined,true,ignoreTriggerActions);
                gw.sendGET(undefined,undefined,undefined,gw.widgetID,true,false); // poll requests must not ignore triggers (TODO tbc) // in any case it doesn't work to put here ignoreTriggerActions,
                						// since this is always false in the first request, and hence will prevent any triggers from working
            }, gw.pollingInterval);
        }
        else if ((gw.pollingInterval <= 0 || ogema.widgetLoader.pollingStopped) && isPollRequest) {
        	isPolling = false;
        }

//        if (gw.isDynamicWidget && gw.dataChanged(result)) {  
	      if (result.composition && result.composition.init) {
			  const comp = result.composition;
			  const initData  = comp.init.filter(data => !ogema.widgets[data[0]]);
			  // TODO make sure this data is not appended to initialWidgetData requests?
			  Object.assign(ogema.widgetLoader.initialWidgetInformation, comp.sub);
			  // FIXME there is a potential race condition here if the loader is already running
			  // this is not so easy to overcome without a migration to a promise based widget loader impl
			  ogema.widgetLoader.loadUniqueWidgetData(initData, true);
		  }
		  if (gw.isDynamicWidget) {
		   	var nrDeleteWidgets = subwidgetsToBeRemoved.length;
		   	
            var interval = 0; //Initial waiting-time
            var attemptCount = 0;
            if (nrDeleteWidgets > 0) {
	            var waitForLoading = setInterval(function () {	
	                if (attemptCount > 150)
	                {
	                    console.error("Timeout while waiting for loadWidgets() in " + gw.widgetID);
	                    clearInterval(waitForLoading);
	                }
	                else {
	                    attemptCount++;
	                    interval = 100;
	                    if (!ogema.widgetLoader.isLoadWidgetsRunning) {
	                        if (gw.hasOwnProperty("subWidgets")) {
	                            gw.invalidateSubWidgets(subwidgetsToBeRemoved);  
	                        }
	                        if (result.hasOwnProperty("subWidgets")) {		
	        				    gw.subWidgets = result.subWidgets;
	     				    }
	                        if (updateSubwidgets && !gw.composition) 
	                        	ogema.reloadWidgets();
	                        clearInterval(waitForLoading);
	                    }
	                }
	
	            }, interval);
	       } else if (updateSubwidgets) {    // if no widgets are deleted, but new ones to be loaded
	       		if (!gw.composition)
	       			ogema.reloadWidgets(); 
	       		if (result.hasOwnProperty("subWidgets")) {		
				    gw.subWidgets = result.subWidgets;
			    }	
	       }       
        }
//        if (result.hasOwnProperty('triggerPost')) {  // FIXME check whether this is used so commonly that it justifies a parallel implementation to triggerAction
//            sendPOST();
//        }
        if (gw.isDynamicWidget) {
    		gw.lastGetRequest = result;
    	}
        /*     
         if(gw.isDynamicWidget) {
         console.log("widget " + widgetID + " is dynamic");
         } else {
         console.log("widget " + widgetID + " is NOT dynamic");
         }
         */
        //	gw.blockLoadWidgets = false;  // reset
    };


    //Delete all sub-widgets from global widget-variable so that reloadWidgets() can rebuild them
    // widgetsList can be an array of strings, OR a single string "all"
    this.invalidateSubWidgets = function (widgetsList) {
    	var deleteAll = false;
    	if (widgetsList === "all") {
    		deleteAll = true;
    	}
        $.each(gw.subWidgets, function (key, value) {
        	if (!deleteAll && widgetsList.indexOf(value) < 0) return;
//        	console.log("  trying to eliminate subwidget ",value);
            try {
                if (ogema.widgets[value].hasOwnProperty("subWidgets")) {
                    ogema.widgets[value].invalidateSubWidgets("all");
                }
                delete ogema.widgets[value];						
//                console.log("          deleted ",value);
            } catch (e) {
                console.error("Widget not found", value);
            }
        });
        //delete gw.subWidgets;
    };
    var failedInitialisations = 0;
    
    // note: this function should always be called without arguments when triggered in an implementing widget (isPollRequest & ignoreTriggerActions default to false)
    // TODO return a future, make it possible to wait for result
    /**
     * 
     * @param deferred allows to wait for the request to finish
     * @param triggeredBy an optional string... the widget that triggered the request 
     */
    this.sendGET = function (deferred, excludedTriggers, excludedGroupTriggers, triggeredBy, isPollRequest, ignoreTriggerActions) {
    	//	console.log('sendGET for ',gw.widgetID, ', requestPending && wait',(requestPending && waitForPendingRequest),', getWidgetUpdateInformation(widgetID) ',getWidgetUpdateInformation(widgetID),', getWidgetUI ',getWidgetUpdateInformation  );
		if (requestPending && waitForPendingRequest) {
//			console.log("Too many requests...");
			if (typeof deferred !== "undefined") {
            	deferred.resolve();
            }
			return;
		}
        if (typeof isPollRequest === "undefined") {
            isPollRequest = false; 
        }
        if (typeof ignoreTriggerActions === "undefined") {
            ignoreTriggerActions = false; 
        }

        if (!gw.initialUpdated) {
        	if (typeof ogema.widgetLoader.getWidgetUpdateInformation(widgetID) !== "undefined") {  
	            var jsonUpdateObject = ogema.widgetLoader.getWidgetUpdateInformation(widgetID);
	            gw.initialUpdated = true;
	            var jsonString = JSON.stringify(jsonUpdateObject[0]);
	            gw.handleWidgetGET(jsonString, isPollRequest, true, excludedTriggers, excludedGroupTriggers);
	            //console.log('delete iwi:' + widgetID);
	            delete ogema.widgetLoader.initialWidgetInformation[widgetID]; // not needed anymore
	            if (typeof deferred !== "undefined") {
	            	deferred.resolve();
	            }
        	}
        	// there are two options how to deal with the case that the initial update info is not yet available...
        	// ignore it, or wait. It turns out that usually ignoring it is better, otherwise one may need to wait for a
        	// long time, and in the end the information may not be available anyway. However, in a bad case this may lead to
        	// an inconsistent state. Commented out: the waiting option
        	/*
        	else  { 
	        	if (failedInitialisations < 40) {   // wait time max. 8s... too long?
		        	setTimeout(function() {
		        		// console.error("Resending request for widget ",widgetID, ", attempt ",failedInitialisations);
		        		failedInitialisations++;
		                gw.sendGET(isPollRequest, ignoreTriggerActions);  		// try again...
		        	},200); 
	        	} else {
	        		console.error("Waiting in vain for initial widget update information... giving up now. ",widgetID);
	        		gw.initialUpdated = true;
	        		failedInitialisation = 0;
	        		gw.sendGET(isPollRequest, ignoreTriggerActions);
	        	}
        	} */
        	else {
        		gw.initialUpdated = true;
        		gw.sendGET(deferred, excludedTriggers,excludedGroupTriggers); 
        	}
        }  
        else {
            //console.log('send get on ' + widgetID + " params:" + getParameters());
            requestPending = true; 
            if (ogema.showOverlay) {
            	try {
            		$("#" + widgetID + ".ogema-widget").overlay(); 
            	} catch(err) {}
            }
            if (typeof ogema.pageInstance === "undefined" || ogema.pageInstance === "") {
            	requestPending=false;
            	setTimeout(function() { gw.sendGET(deferred, excludedTriggers,excludedGroupTriggers, undefined, isPollRequest, ignoreTriggerActions) },500);
            	return;
            }
            var servletPath = gw.servletPath + ogema.getParameters();
            if (typeof triggeredBy !== "undefined")
            	servletPath += "&triggeredBy=" + triggeredBy; 
            $.ajax({
                type: "GET",
                url: servletPath,
                contentType: contentType
            }).done(function (result) {
                gw.handleWidgetGET(result, isPollRequest, ignoreTriggerActions, excludedTriggers,excludedGroupTriggers);
            }).always(function() {
            	if (ogema.showOverlay) {
            		try {
                		$("#" + widgetID + ".ogema-widget").overlayout(); 
            		} catch(err) {}
            	}
            	requestPending = false;
            	if (typeof deferred !== "undefined") {
            		deferred.resolve();
            	}
            }).fail(function(jqXHR, textStatus, errorThrown) {
            	if (!isPollRequest) 
            		gw.handleAjaxError(jqXHR, textStatus, errorThrown, "GET");
            });
        }
    };

	/**
	* @param deferred allows to wait for the request to be done
	* @param triggeredBy an optional string... the widget that triggered the request 
	*/
    this.sendPOST = function (deferred, excludedTriggers, excludedGroupTriggers, triggeredBy) {
        //console.log("sendPOST for " + widgetID);
        if (requestPending && waitForPendingRequest) {
//        	console.log("Too many requests... ");
        	return;
        }
        requestPending = true;
        if (ogema.showOverlay) {
        	try {
        		$("#" + widgetID + ".ogema-widget").overlay(); 
        	} catch(err) {}
        }
        var prePostData = {};
        Object.keys(triggeredActionPrePOST).forEach(function (triggerWidget) {
            var widget2 = ogema.widgets[triggerWidget];    		     //note: widgets is a global variable defined in loadWidgets.js script
            var action = triggeredActionPrePOST[triggerWidget].triggeredAction;
            if (action === "sendPOST") {	// POST actions need to be submitted with the same request
            	action === "getSubmitData";
            }
            var postresult =  widget2[action].apply(widget2, triggeredActionPrePOST[triggerWidget].args);     // call triggered function   
            if (action === "getSubmitData") {
	            prePostData[triggerWidget] = {};
	            prePostData[triggerWidget].data = postresult;
            }
        });
        Object.keys(triggeredGroupActionPrePOST).forEach(function (groupId) {
            try {
            	// need to treat differently case of triggered GET and POST/other requests?		
            	var action = triggeredGroupActionPrePOST[groupId].triggeredAction;
//               	console.log("Triggering action on PrePOST of group ", groupId + ", action: " + action);
            	var gatherData = false;
            	if (action === "sendPOST" || action === "getSubmitData") {  // POST actions need to be submitted with the same request
            		action = "getSubmitData";
            		gatherData = true;
            	}
            	var postdata = ogema.widgetGroups[action].apply(ogema.widgetGroups, [groupId].concat(triggeredGroupActionPrePOST[groupId].args));  
            	if (gatherData) {
           			jQuery.extend(prePostData, postdata);    // merges postdata into prePostData
            	}
            	
            } catch (e) {
            	console.error("Error triggering widget group PrePOST",groupId,e);
            }
        });

        var data = gw.getSubmitData();
        prePostData.data = data;
        var strData = JSON.stringify(prePostData);
        if (typeof ogema.pageInstance === "undefined" || ogema.pageInstance === "") {
        	requestPending=false;
            console.log("page instance undefined!");
        	return;
        }
        var servletPath = gw.servletPath + ogema.getParameters();
        if (typeof triggeredBy !== "undefined")
        	servletPath += "&triggeredBy=" + triggeredBy; 
        $.ajax({
            type: "POST",
            url: servletPath,
            contentType: contentType,
            data: strData
        })
        .done(function (text, textStatus) {
        	var data;
            //console.log("Finished post for widget " + widgetID);
        	if (typeof text === "object") {
        		data = text;
        	}
        	else if (typeof text === "string") {    	
	        	try {
	        	    data = JSON.parse(text);
	        	} catch (e) {
	        		console.error("POST answer received not JSON compliant"); 
	        		data = {};
	        	}
        	}
    	    if (data.hasOwnProperty("expired") && data.expired) {  // FIXME move to error part
        		try {	// currently only works for WidgetPageSimple
        			ogema.widgets.rootWidget.showExpiredMessage();
        		} catch (e) {}
                //console.log("Is expired!");
        		return;
        	}
        
 //       	if (text.length === 0) text = "{}";
                // TODO unified treatment of governing and non-governing widgets    
                if (gw.governingWidget) {
                    //console.log("Is governing widget");
                    try {
                    	gw.processPOSTResponse(data.onPOSTReply);  
                    } catch (e) {
               			 console.error("Could not process POST response, probably due to wrong format", text, e);
         		    }                       
                    Object.keys(data).forEach(function (triggerWidget) {		
                        if (ogema.widgets.hasOwnProperty(triggerWidget)) {      	  
                            try {
                                var widget2 = ogema.widgets[triggerWidget];         //note: widgets is a global variable defined in loadWidgets.js script
//                              console.log("Dep.widget:",triggerWidget," Value", data[widget2.widgetID][0]);
                                widget2['handleWidgetGET'].call(widget2, data[widget2.widgetID][0], false);
                            } catch (e) {
                            	console.error("Error triggering subwidget",triggerWidget,e);
                            }
                        }
                    }); 
                } else {
                	try {
                    	gw.processPOSTResponse(data);
                    } catch (e) {
               			 console.error("Could not process POST response, probably due to wrong format", text, e);
         		    }    
/*
                    Object.keys(triggeredActionPOST).forEach(function (triggerWidget) {
                        try {
                            var widget2 = ogema.widgets[triggerWidget];    		     //note: widgets is a global variable defined in loadWidgets.js script
                            widget2[triggeredActionPOST[triggerWidget].triggeredAction].apply(widget2, triggeredActionPOST[triggerWidget].args);           // call triggered function   
                        } catch (e) {
                            console.error("Error triggering subwidget",triggerWidget,e);
                        }
                    });
                    Object.keys(triggeredGroupActionPOST).forEach(function (groupId) {
			            try {	
//			            	console.log("Triggering action on POST of group ", groupId);
			            	ogema.widgetGroups[triggeredGroupActionPOST[groupId].triggeredAction].apply(ogema.widgetGroups, triggeredGroupActionPOST[groupId].args); 
			            } catch (e) {
			            	console.error("Error triggering widget group POST",groupId,e);
			            }
                    });  
*/                  
					gw.trigger(triggeredActionPOST,triggeredGroupActionPOST,0, excludedTriggers, excludedGroupTriggers);
                }

        }).always(function() {
        	 if (ogema.showOverlay) {
            	 try {
               		 $("#" + widgetID + ".ogema-widget").overlayout(); 
             	} catch(err) {}
             }
             requestPending = false;
             if (typeof deferred !== "undefined") {
             	deferred.resolve();
             }
        }).fail(function(jqXHR, textStatus, errorThrown) {
            gw.handleAjaxError(jqXHR, textStatus, errorThrown, "POST");
        });
    };

    //args  => widget2[triggeredActionPOST[triggerWidgetID]].args

    /*
     this.getPageParameters = function () {
     var params = {};
     var paramString = window.location.search.replace("?","");
     var paramList = paramString.split("&");
     for (var i=0;i<paramList.length;i++) {
     var pair = paramList[i].split("=");
     var id = pair[0];
     var value = pair[1];
     params[id] = value;
     }
     return params;
     }
     */
    this.handleAjaxError = function (jqXHR, textStatus, errorThrown, type) {
    	if (jqXHR.readyState === 4 && jqXHR.status === 404) { // widget not found; typically a session-specific widget that has been removed
    		return;
    	}
        var reason = "unknown";
        try {
        	var data = JSON.parse(jqXHR.responseText);
    	    if (data.hasOwnProperty("expired") && data.expired) { 
    	    	reason = "Session expired.";
    	    	ogema.widgets.rootWidget.showExpiredMessage();
        		return;
           	}
    	    else if (data.hasOwnProperty("exception")) {
    	    	ogema.widgets.rootWidget.showErrorHandlingMessage(data.exception); 
    	    	return
    	    }
        } catch (e) {} // above only works for WidgetPageSimple
        if (jqXHR.readyState === 4 && reason === "unknown") {
            reason = "http-error " + jqXHR.status + ", " + jqXHR.statusText;
            // HTTP error (can be checked by XMLHttpRequest.status and XMLHttpRequest.statusText)
        }
        else if (jqXHR.readyState === 0) {
            reason = "Network-error, cannot reach server! Is it running?";
            // Network error (i.e. connection refused, access denied due to CORS, etc.)
        }
        var msg = "Ajax-error (" + type + "): " + reason;
        try {
        	ogema.widgets.rootWidget.showErrorHandlingMessage(msg);
        } catch (e) {
    		alert(msg);   // fallback: use ugly intrinsic alert
    	}
    };
}

/**
* @param excludedTriggers may be undefined; an array of widget ids that must not be triggered
*/
GenericWidget.prototype.trigger = function(triggerObj,triggerGroupObj,level, excludedTriggers, excludedGroupTriggers) {
	if (typeof excludedTriggers === "undefined")
		excludedTriggers = [];
	if (typeof excludedGroupTriggers === "undefined")
		excludedGroupTriggers = [];
	var newExcludedTriggers = [].concat(excludedTriggers);
	var newExcludedGroupTriggers = [].concat(excludedGroupTriggers);
	Object.keys(triggerObj).forEach(function (triggerWidget) {
        try {
        	var obj = triggerObj[triggerWidget];
        	if (obj.level <= level) 
        		return;
            var action = obj.triggeredAction;
            newExcludedTriggers.push(triggerWidget + "__ACTION__" + action);
        } catch (e) {}
    });
	Object.keys(triggerGroupObj).forEach(function (groupId) {
        try {
        	var obj = triggerGroupObj[groupId];
        	if (obj.level <= level) 
        		return;
            var action = obj.triggeredAction;
            newExcludedGroupTriggers.push(groupId + "__ACTION__" + action);
        } catch (e) {}
    });	
	var gw = this;
	var id = gw.widgetID;
//	var newExcludedTriggers = Object.keys(triggerObj).concat(excludedTriggers);
//	var newExcludedGroupTriggers = Object.keys(triggerGroupObj).concat(excludedGroupTriggers);
	var deferreds = [];
    Object.keys(triggerObj).forEach(function (triggerWidget) {
        try {
        	var obj = triggerObj[triggerWidget];
        	if (obj.level !== level) 
        		return;
            var widget2 = ogema.widgets[triggerWidget];
            if (typeof widget2 === "undefined")
            	return;
            var action = obj.triggeredAction;
            if (excludedTriggers.indexOf(triggerWidget + "__ACTION__" + action) >= 0)
            	return;
//            console.log("Triggering action " + action + " for widget " + triggerWidget);
            if (action === "sendGET" || action === "sendPOST") {
            	var deferred = $.Deferred();
            	deferreds.push(deferred);
//            	widget2[action].apply(widget2, deferred, newExcludedTriggers, newExcludedGroupTriggers);
				widget2[action].call(widget2, deferred, newExcludedTriggers, newExcludedGroupTriggers, id);
            }	
            else 
            	widget2[action].apply(widget2, triggerObj[triggerWidget].args);           // call triggered function   
        } catch (e) {
        	console.error("Error triggering widget action",triggerWidget,e);
        }
    });
    // TODO wait for reply
    Object.keys(triggerGroupObj).forEach(function (groupId) {
        try {
       		var obj = triggerGroupObj[groupId];
       		// TODO
        	if (obj.level !== level) 
        		return;
	       	var action = obj.triggeredAction;
	       	if (excludedGroupTriggers.indexOf(groupId + "__ACTION__" + action) >= 0)
	       		return;
//            console.log("Triggering action " + action + " for widget group " + groupId);
            if (action === "sendGET" || action === "sendPOST") {
                var deferred = $.Deferred();
            	deferreds.push(deferred);
 //        		ogema.widgetGroups[obj.triggeredAction].apply(ogema.widgetGroups, groupId, deferred, excludedTriggers, excludedGroupTriggers);
        		ogema.widgetGroups[obj.triggeredAction].call(ogema.widgetGroups, groupId, deferred, newExcludedTriggers, newExcludedGroupTriggers, id);
        	}
        	else
        		ogema.widgetGroups[obj.triggeredAction].apply(ogema.widgetGroups, [groupId].concat(triggerGroupObj[groupId].args));
        } catch (e) {
        	console.error("Error triggering widget group action",groupId,e);
        }
    });
    // when this level is done, start the next one
    if (deferreds.length === 0)
    	return;
    $.when.apply($, deferreds).done(function() { 
    	gw.trigger(triggerObj,triggerGroupObj,level+1, excludedTriggers, excludedGroupTriggers);
    });
}


GenericWidget.prototype.setStyles = function (styles) {
	var gw = this;
	var idClasses = ["ogema-widget"]; // these must not be removed... they are used as identifiers
	function extractIdClasses(styleObjects, isBaseClass) {
		Object.keys(styleObjects).forEach(function(identifier) {
			var spaceIdx = identifier.indexOf(" ");
			if (isBaseClass) {
				if (spaceIdx < 0) {
		        	idClasses.push(identifier);
		        }
		        else {
		        	idClasses.push(identifier.substring(0,spaceIdx));
		        }
			}
			while (spaceIdx > 0) {
				identifier = identifier.substring(spaceIdx+1);
	        	spaceIdx = identifier.indexOf(" ");
	        	var subClass = identifier;
	        	if (spaceIdx > 0) {
	        		subClass = identifier.substring(0,spaceIdx);
	        	}
	        	
	        	if (subClass.startsWith(".") && idClasses.indexOf(subClass) < 0) {
	        		idClasses.push(subClass.substring(1));
	        	}
	        }
		});
	}
	extractIdClasses(styles.classStyles,true);
	extractIdClasses(styles.idStyles,false);
	extractIdClasses(styles.childTagStyles,false);
	extractIdClasses(styles.tagStyles,false);
	
	function removeOldClasses(styleObjects,selector) {
		Object.keys(styleObjects).forEach(function (idClass) {
	         var crValues = styleObjects[idClass];
	         var styleEl = $("#" + gw.widgetID + ".ogema-widget").find(selector + idClass);
	         var elementsArray = styleEl.get();
	         for (var i=0;i<elementsArray.length;i++) {
	         	var mmm = elementsArray[i];
	         	var clList = Array.from(mmm.classList);
	         	for (var j=0;j<clList.length;j++) {
	         		// TODO remove nonId classes
	         		var nnn = clList[j];
	         		if (idClasses.indexOf(nnn) >= 0)
	         			continue;
	         		mmm.classList.remove(nnn);
	         	}
	         }
		});
	}
	removeOldClasses(styles.classStyles,".");
	removeOldClasses(styles.idStyles,"#");
	removeOldClasses(styles.childTagStyles,">");
	removeOldClasses(styles.tagStyles,"");
	$("#" + gw.widgetID + ".ogema-widget").get().forEach(function(domEl){
		var clList = domEl.classList;
     	for (var j=0;j<clList.length;j++) {
     		var nnn = clList[j];
     		if (idClasses.indexOf(nnn) >= 0)
     			continue;
     		domeEl.classList.remove(nnn);
     	}
	});
	
	function addNewClasses(styleObjects,selector) {
		Object.keys(styleObjects).forEach(function (styleId) {
	        var crValues = styleObjects[styleId];
	        var styleEl = $("#" + gw.widgetID + ".ogema-widget").find(selector + styleId);
	        var listL = crValues.length;          
	        for (var nn = 0; nn < listL; nn++) {
	            styleEl.addClass(crValues[nn]);
	        }
	    });
	}
	addNewClasses(styles.classStyles,".");
	addNewClasses(styles.idStyles,"#");
	addNewClasses(styles.childTagStyles,">");
	addNewClasses(styles.tagStyles,"");
	
	styles.widgetStyles.forEach(function(styleId) {
		var styleEl = $("#" + gw.widgetID + ".ogema-widget");
		styleEl.addClass(styleId);
	});

} 

/**** public methods that can be overridden	*****/

GenericWidget.prototype.showWidget = function () {
	try {
    	// this.element.style.display = "inline";
    	this.element.style.removeProperty("display");
    } catch (e) { console.log(e); }
};

GenericWidget.prototype.hideWidget = function () {
	try {
    	this.element.style.display = "none";
    } catch (e) { console.log(e); }
};

/**
 * to be overwritten by inheriting class;
 * called by sendGET method
 * @param data: JSON object returned by GET request
 * @return 
 * 		true: reload widgets after finishing GET method (for dynamic widgets, i.e. those that support subwidgets)<br>
 * 		false: do not reload widgets
 */
GenericWidget.prototype.update = function (data, oldRequest, subwidgetsToBeRemoved) {
//    console.log("Calling GenericWidget update function");
};
/**
 * to be overwritten by inheriting class;
 * called by sendPOST method
 */
GenericWidget.prototype.getSubmitData = function () {
    // override; adding content to data
    var data = {};
    return data;
};

/**
 * must return a string in the form "?a1=b1&a2=b2..."
 */
GenericWidget.prototype.getParametersGET = function () {
    return '';
};

/**
 * must return a string in the form "?a1=b1&a2=b2..."
 */
/*GenericWidget.prototype.getParametersPOST = function () {
 return '';
 };
 */
GenericWidget.prototype.processPOSTResponse = function (data) {
};
