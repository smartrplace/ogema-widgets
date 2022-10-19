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
/**
 * Default Widget-loader script. It has to be included into a html-site which wants
 * to use Widgets.
 *
 * Function calls in this file:
 *
 * loadWidgets() (called from main script)
 *		--> loadUniqueWidgetData(), once
 * loadUniqueWidgetData()
 *		--> getUniqueTypeUrls(), once
 *		--> extractScripts(), once per missing widget html file
 *		--> preloadInitGroups(), once, as soon as all extractScripts calls have returned
 * extractScripts()
 *		--> createScript(), once per missing javascript file
 *		--> callback to loadUniqueWidgetData().checkLoadingFinished() for each html file that has all scripts loaded
 * createScript()
 *		--> callback to extractScripts().checkScriptsFinished() for each javascript file that has been loaded
 * preloadInitGroups()
 *      --> load widgets init data for widgets that are not part of the initial preloading but shall be loaded together
 *		--> createWidgets(), once all groups have been loaded
 * createWidgets()
 *		--> restarts loadWidgets() if required
 */

var ogema;
if (typeof ogema === "undefined") {// should be defined already, if loaded from a WidgetPageSimple
//	console.error("ogema var missing");
	ogema = {};
	ogema.widgetLoader = {};
}

ogema.widgetLoader.ID_DETECT_ATTEMPTS = 25;
ogema.widgetLoader.backupScripts = [];
ogema.widgetLoader.newBackups = 0;
ogema.widgetLoader.iterationCounter = 0;
ogema.widgetLoader.failCounter = 0;
ogema.widgetLoader.loadedWidgetTypes = []; // FIXME used?
ogema.widgets = {};  // keys: widgetIDs; values: instances of GenericWidget
ogema.widgetLoader.htmlObj = {};  // keys: widgetIDs; values: widget HTML
ogema.widgetLoader.isLoadWidgetsRunning = false;
ogema.widgetLoader.runAgainLoadWidgets = false;
ogema.widgetLoader.initialWidgetInformation = {};
// this array of group ids is filled by widgets registering a group of subwidgets which shall be preloaded together
// these are different groups from the WidgetGroups
ogema.widgetLoader.groupsToBePreloaded = undefined; // Array<string>|undefined

ogema.pageInstance = '';
ogema.widgetLoader.pollingStopped = false;
//ogema.configId = ''; // what is this?
//ogema.widgetLoader.allscripts;
if (typeof ogema.widgetLoader.scriptsLoadedInit === "undefined")
	ogema.widgetLoader.allscripts = []; // contains a list of loaded javascript files (their urls)
else
	ogema.widgetLoader.allscripts = ogema.widgetLoader.scriptsLoadedInit;
//var params = '';
//ogema.widgetLoader.auxParams = '';
ogema.locale = 'en'; // FIXME
ogema.widgetGroups = {};

//read the widget information for initial update
ogema.widgetLoader.init = function() {
	$.ajax({
	    type: "GET",
	    url: "/ogema/widget/servlet?initialWidgetInformation=" + window.location.toString().replace("#","") + "&user=" + otusr + "&pw=" + otpwd,
	    contentType: "application/json"
	}).done(function (responseAsJSON) {
		ogema.widgetLoader.initialWidgetInformation = responseAsJSON;
	//    console.log("initialWInfo", responseAsJSON);
	    ogema.pageInstance = ogema.widgetLoader.initialWidgetInformation.pageInstance;
	//    params = '&pageInstance=' + pageInstance;  // handled by getParameters() function
	//    if(ogema.configId.length > 0) {
	//     	params = params + "&configId="+configId;
	//   	ogema.widgetLoader.auxParams = ogema.widgetLoader.auxParams + "&configId="+ogema.configId;
	//    }
	    ogema.reloadWidgets(); // once pageInstance is set, page-specific widgets can be loaded
	});
}

ogema.widgetLoader.getWidgetUpdateInformation = function(widgetID) {
    return ogema.widgetLoader.initialWidgetInformation[widgetID];
}

/**
* @param doAppend: append query parameters from page URL? default (no parameter): false
*/
ogema.getParameters = function(doAppend) {
	var res = '?pageInstance=' + ogema.pageInstance + '&locale=' + ogema.locale  + '&user=' + otusr + '&pw=' + otpwd;
	if (doAppend) {
		var str = window.location.search;
		if (str)
			res = res + '&' + str.substring(1); // assuming that str starts with '?'
	}
	return res;
}

/**
* extracts unique widget names and corresponding html urls.
*/
ogema.widgetLoader.getUniqueTypeUrls = function(widgetScripts) {
	var types = [];
	var urls = {};
	for (var i=0;i<widgetScripts.length;i++) {
		var tp = widgetScripts[i][1];
		if (types.indexOf(tp) < 0 && !ogema.widgetLoader.htmlObj.hasOwnProperty(tp)) {
			types.push(tp);
			urls[tp] = widgetScripts[i][2];
		}
	}
	return urls;
}

/**
* appends javascript script to document
*/
ogema.widgetLoader.createScript = function(scriptItem, url, callbackFct) {
    var targetScript = document.createElement("script");
    document.head.appendChild(targetScript);
    targetScript.onload = callbackFct;
    targetScript.onerror = callbackFct;

    if (scriptItem.src.length > 0) {
        targetScript.src = scriptItem.getAttribute("src");
    }
    else if (scriptItem.innerHTML.length > 0) {
        targetScript.innerHTML = scriptItem.innerHTML;
        targetScript.onload();
    }
    else {
        targetScript.onload();
    }
}

/**
* extracts javascript tags from Html string and loads them
*/
ogema.widgetLoader.extractScripts = function(html, callbackFct) {	// FIXME not respecting order of scripts??
	var items = $(html).get();  // an array of tag elements
	var locScripts = {};
	var locUrls = {};
	var indices = [];
	for (var i=0; i < items.length; i++) {
		var item = items[i];
		if (item.tagName !== "SCRIPT") continue;
		var url = item.src;
		if (!url || ogema.widgetLoader.allscripts.indexOf(url) >= 0) {
			//console.log("Trying to reload " + url + ", skip this one");
			continue;
		}
		if (url.indexOf("http") === 0 && url.lastIndexOf("/") > 7) {
			var idx = url.indexOf("/",8);
			var locUrl = url.substring(idx);
			//console.log(" locUrl ",locUrl);
			if (ogema.widgetLoader.allscripts.indexOf(locUrl) >= 0) {
				//console.log("Trying to reload " + locUrl + " (original url: )" + url + ", skip this one");
				continue;
			}
		}
		ogema.widgetLoader.allscripts.push(url);
		locScripts[i] = item;
		locUrls[i] = url;
		indices.push(i);
	}
	var cnt = 0;
	var loadNext = function() {
		if (cnt === indices.length) {
			callbackFct();
			return;
		}
		var idx = indices[cnt++];
		var url = locUrls[idx];
		var script = locScripts[idx];
		ogema.widgetLoader.createScript(script,url, loadNext);
	};
	loadNext();
}

ogema.widgetLoader.preloadInitGroups = function(widgetScripts) {
	var groups = ogema.widgetLoader.groupsToBePreloaded;
	ogema.widgetLoader.groupsToBePreloaded = undefined;
	if (groups !== undefined && groups.length > 0) {
		var promises = [];
		for (var group of groups) {
			var promise = $.ajax({
		        type: "GET",
		         url: "/ogema/widget/servlet?initialWidgetInformation=" + window.location.toString().replace("#","") + "&initGroup=" + group +
		         	 "&pageInstance=" + ogema.pageInstance + "&user=" + otusr + "&pw=" + otpwd,
		        contentType: "text/plain"
	        });
	        promises.push(promise.done(function(response) {
				Object.assign(ogema.widgetLoader.initialWidgetInformation, response);
				scripts = [];
				// remove response widgets from widget scripts
				for (var widgetId of Object.keys(response)) {
					var idx = widgetScripts.findIndex(script => script[0] === widgetId);
					if (idx >= 0)
						scripts.push(widgetScripts.splice(idx, 1)[0]);
				}
				ogema.widgetLoader.createWidgets(scripts, true);
			}));
		}
		$.when(...promises).done(function() {
			ogema.widgetLoader.createWidgets(widgetScripts, false);
		});
	} else {
		ogema.widgetLoader.createWidgets(widgetScripts, false);
	}
}

/**
* creates widgets, assuming that the required div tag is available (otherwise widget is stored in backupScripts),
* and html and js have been loaded
*/
ogema.widgetLoader.createWidgets = function(widgetScripts, skipFinish) {
	for (var i=0;i<widgetScripts.length;i++) {
		var widgetID = widgetScripts[i][0];
		var type = widgetScripts[i][1];
//		var widgetHtmlElement = document.getElementById(widgetID);
//		if (widgetHtmlElement === null) {
//			ogema.widgetLoader.backupScripts.push(widgetScripts[i]);
//			continue;
//		}
		var widgetHtmlElement;
		var elements = $("#" + widgetID + ".ogema-widget");
		var size = elements.length;
		if (size === 0) {
			ogema.widgetLoader.backupScripts.push(widgetScripts[i]);
			continue;
		}
		else {
			widgetHtmlElement = elements.get(0);
			if (size > 1) {
				console.warn("Widget ID " + widgetID + " is not unique on this page. This may lead to unexpected behaviour.");
			}
		}
		widgetHtmlElement.style.visibility = "hidden";
		widgetHtmlElement.innerHTML = ogema.widgetLoader.htmlObj[type];
		try { // Browser support: http://caniuse.com/#search=classlist
			var classes = widgetHtmlElement.classList;
			if (!classes.contains("ogema-widget"))
				classes.add("ogema-widget");
		} catch (e) {}
			try {
				var servletPath = window.location.pathname.replace('.html', '').replace('.htm', '') + '/' + widgetID;
		    var widget = new window[type](servletPath, widgetID);
		    ogema.widgets[widgetID] = widget;
		} catch (exception) {
		    console.log("There was an error creating an instance for widgetID: " + widgetID + " of type " + type + " on servletPath: " + servletPath);
		    console.error(exception);
		}
	}
	if (!skipFinish) {
		// finished!
		if (ogema.widgetLoader.runAgainLoadWidgets || ogema.widgetLoader.backupScripts.length > 0) {
			ogema.widgetLoader.loadWidgets();
		}
		else {
			ogema.widgetLoader.isLoadWidgetsRunning = false;
			ogema.widgetLoader.failCounter = 0;
			ogema.widgetLoader.iterationCounter = 0;
		}
	}
}

/**
* ensures that all required html and javascript is loaded, then creates widgets
*/
ogema.widgetLoader.loadUniqueWidgetData = function(widgetScripts) {
	var urls = ogema.widgetLoader.getUniqueTypeUrls(widgetScripts);
	var counter = Object.keys(urls).length;
	var checkLoadingFinished = function() {
		counter = counter - 1;
		//console.log("loadUniqueWidgetData counter =",counter);
		if (counter === 0) {
			//console.log("loadUniqueWidgetData done");
			ogema.widgetLoader.preloadInitGroups(widgetScripts);
		}
	}
	if (counter === 0) {	// if all html and js has been loaded already
		counter = 1;
		checkLoadingFinished();
	}
	Object.keys(urls).forEach(function(widgetType) {
		var widgetHtmlURL = urls[widgetType];
		$.ajax({
	        type: "GET",
	        url: widgetHtmlURL,
	        contentType: "text/html"
	    })
	    	.done(function(htmlText) {
	    		// console.log("  Initial loader for",widgetType);
	    		ogema.widgetLoader.htmlObj[widgetType] = htmlText;
	    		ogema.widgetLoader.extractScripts(htmlText, checkLoadingFinished);
	    	});
	});
}

/**
* determines widgets to be loaded, by sending a request to the central widget service servlet, then
* calls loadUniqueWidgetData to actually load widgets. Also checks whether backup scripts need to be loaded.
*/
ogema.widgetLoader.loadWidgets = function() {
	ogema.widgetLoader.isLoadWidgetsRunning = true;
    //if (backupScripts.length===0) {
	ogema.widgetLoader.runAgainLoadWidgets = false;
    // }
    var widgetCounter = 0;
    var widgetScripts = [];
    ogema.widgetLoader.iterationCounter++;
    console.log('loadWidgets iteration ',ogema.widgetLoader.iterationCounter);

    //define callback for request to central servlet
    $.ajax({
        type: "GET",
        url: "/ogema/widget/servlet?boundPagePath=" + window.location.toString().replace("?","&").replace("#","")  + "&" + ogema.getParameters().substring(1),
        contentType: "text/plain"
    })
            .fail(function () {
                console.error("Widget-service-servlet not found!");
                ogema.widgetLoader.isLoadWidgetsRunning = false;
                if (ogema.widgetLoader.runAgainLoadWidgets) {
                	ogema.widgetLoader.loadWidgets();
                }
            })
            .done(function (responseText) {
                //get JSON with widget-script-urls to load from response
                var widgetScripts = [];
                var widgetScriptsAux;
                if (typeof responseText === "object") {
                	widgetScriptsAux = responseText;
                }
                else if (typeof responseText === "string") {
                	widgetScriptsAux = JSON.parse(responseText);
                }
                //console.log("widgetScriptsAux",widgetScriptsAux);
                for (var aux1 = 0; aux1 < widgetScriptsAux.length; aux1++) {
                    var arr = widgetScriptsAux[aux1];
                    //check if widget has been loaded and started in finishLoading
                    if (ogema.widgets.hasOwnProperty(arr[0])) {
                        continue;
                    }

                    widgetScripts.push(widgetScriptsAux[aux1]);
                }

                // delete widgets objects that are not no longer registered
                Object.keys(ogema.widgets).forEach(function (widgetID) {
                    var widgetFound = false;
                    for (var i = 0; i < widgetScriptsAux.length; i++) {
                        var array = widgetScriptsAux[i];
                        var widgetIdAux = array[0];
                        if (widgetID === widgetIdAux) {
                            widgetFound = true;
                            break;
                        }

                    }

                    if (!widgetFound) {
                        delete ogema.widgets[widgetID];
                    }
                });

                //console.log("widgetScripts",widgetScripts);
                //for each call includeJs()
                var scriptMap = [];
                scriptMap.script = [];
                //check if this iteration processes backup scripts and make sure
                //you are not iterating eternally
                if (ogema.widgetLoader.backupScripts.length > 0) {
                    //widgetScripts = ogema.widgetLoader.backupScripts;
                    ogema.widgetLoader.backupScripts = [];
                    if (ogema.widgetLoader.failCounter === 0)
                    	ogema.widgetLoader.runAgainLoadWidgets = true;
                }
                if (ogema.widgetLoader.iterationCounter > 1 && widgetScripts.length === ogema.widgetLoader.newBackups) {
                	ogema.widgetLoader.failCounter++;
                    if (ogema.widgetLoader.failCounter > ogema.widgetLoader.ID_DETECT_ATTEMPTS) {
                    	ogema.widgetLoader.isLoadWidgetsRunning = false;
                        ogema.widgetLoader.newBackups = 0;
                        ogema.widgetLoader.failCounter = 0;
                        if (ogema.widgetLoader.runAgainLoadWidgets) {
                        	ogema.widgetLoader.loadWidgets();
                        } else {
                       	        console.log("loadWidgets failed; were these widgets added to the page?", widgetScripts.map(function(arr) { return arr[0];}));
                        }
                        return;
                    }
                    //setTimeout(function () {
                    //}, 500);
                } else {
                	ogema.widgetLoader.failCounter = 0;
                }

                if (widgetScripts.length === 0) {
                	ogema.widgetLoader.isLoadWidgetsRunning = false;
                    if (ogema.widgetLoader.runAgainLoadWidgets) {
                    	ogema.widgetLoader.loadWidgets();
                    }
                    return;
                }
                ogema.widgetLoader.loadUniqueWidgetData(widgetScripts);
                ogema.widgetLoader.newBackups = widgetScripts.length;

            });
};

ogema.reloadWidgets = function() {
	if (ogema.widgetLoader.isLoadWidgetsRunning) {
		ogema.widgetLoader.runAgainLoadWidgets = true;
	} else {
		ogema.widgetLoader.loadWidgets();
	}
}

//immediately run the function if HTML is specified explicitly
if (!ogema.dynamicHtml) {
	// should not be seen normally
	console.log("Initializing widgets manually, since HTML is provided");
	ogema.widgetLoader.init();
}

try {
	ogema.widgetGroups = new WidgetGroups("/ogema/widget/servlet", window.location);
} catch (e) {
	console.error("Could not initialize WidgetGroups. Please include the WidgetGroups.js file in your sources.",e);
}

ogema.stopPolling = function() {
	ogema.widgetLoader.pollingStopped = true;
	ogema.stopMessagePolling();
}

ogema.startPolling = function() {
	ogema.widgetLoader.pollingStopped = false;
	ogema.startMessagePolling();
}

// independent of widgets loading:
function addCSSTag() {
    var css = $("#globalWidgetsCSSEl");
    if (css.length === 0) {
        var tag = document.createElement("style");
        tag.appendChild(document.createTextNode(""));
        tag.setAttribute("id", "globalWidgetsCSSEl");
        document.head.appendChild(tag);
        css = $("#globalWidgetsCSSEl");
    }
}

addCSSTag();
