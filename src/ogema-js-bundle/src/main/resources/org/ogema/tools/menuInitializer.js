/*
 * Must be loaded after widgetLoader
 */
ogema.menuIdentifier = {}; // only indicates that the script has been loaded
/*
* FIX ME : Library ddslick calls onSelected on startup
*/
ogema.menuIdentifierFirstCallThrownAway = false;

// keep a list of active ajax requests so we can abort them on logout.
var xhrPool = [];
var loggedOut = false;
$(document).ajaxSend(function (e, jqXHR, options) {
    xhrPool.push(jqXHR);
    //console.log("new ajax: " + options.url);
    if (loggedOut) {
        jqXHR.abort();
    }
});
$(document).ajaxComplete(function (e, jqXHR, options) {
    xhrPool = $.grep(xhrPool, function (x) {
        return x !== jqXHR;
    });
    // console.log("open ajax requests " + xhrPool.length);
});
var abortAjax = function () {
    console.log("logout, aborting ajax requests: " + xhrPool.length);
    $.each(xhrPool, function (idx, jqXHR) {
        jqXHR.abort();
    });
};

(function() {  // anonymous function hides variables
	var messagesTitle = "Messages";
	var setMenuLanguage = function() {
		var address = window.location.pathname;
		$.ajax({
	        type: "GET",
	        url: address + ogema.getParameters() + "&menu=true"
	    }).done(function(resp){
	    	var modal = $("#menu-modal");
    		var logout = resp.logout;
	    	modal.find("#ModalLabel").text(logout.title);
	    	modal.find("#ModalBody").text(logout.msg);
	    	modal.find("#logoutBtn").text(logout.logoutBtn);
	    	modal.find("#cancelBtn").text(logout.cancelBtn);
	    	var logoutField = $("#logoutField");
	    	logoutField.html(logout.logoutHtml);
	    	messagesTitle = resp.messages;
	    });
	};

	function loadLanguageParameters(languageCallback, skipWidgetsLoading) {
		 $.ajax({
	        type: "GET",
	        url: "/ogema/widget/servlet" + ogema.getParameters() + "&getConfigurationParameters=true",
	        contentType: "application/json"
	    }).done(function(responseText){
    		languageCallback(responseText.languages, skipWidgetsLoading);
	    });
	}
	var languageCallback = function(input, skipWidgetsLoading) {
		var lang =  $("#languageSelector");
		if (lang.length === 0) return;
//		console.log("language tag",lang);
		lang.ddslick({
		    data:input,
		    width:"100%",
		    selectText: "Select language",
		    imagePosition:"left",
		    onSelected: function(selectedData){
		        if(!ogema.menuIdentifierFirstCallThrownAway) {
		        	ogema.menuIdentifierFirstCallThrownAway = true;
		        	return;
		        }
		        console.log("selectedData: ",selectedData);
		        if (ogema.locale) {
		        	ogema.locale = selectedData.selectedData.text;
		        	// update widgets
//		        	if (!skipWidgetsLoading) // note: the skipWidgets parameter is onyl set at construction time, and is always true
		        		ogema.widgetGroups.sendGET("all", undefined,undefined,undefined, true); // in particular leads to an initial request, besides the initialWidgetInformation request that is sent anyway...
		        									  // somewhat dangerous; sometimes this request is actually sent before the pageInstanceId is set, in which case it is ignored -> FIXME
		         //	Object.keys(widgets).forEach(function(id) {
		         //		widgets[id].sendGET(false,true); // do not trigger dependent widget updates
		         //	});
		         	setMenuLanguage();
		        }
		        else {
		        	console.error("Variable 'locale' has been removed. Cannot change language.");
		        }
		     }
		});
	};
	//https://stackoverflow.com/questions/5499078/fastest-method-to-escape-html-tags-as-html-entities
	function escapeHTML(html) {
		if (typeof html !== "string")
			return "";
	    var fn=function(tag) {
	        var charsToReplace = {
	            '&': '&amp;',
	            '<': '&lt;',
	            '>': '&gt;',
	            '"': '&#34;'
	        };
	        return charsToReplace[tag] || tag;
	    }
	    return html.replace(/[&<>"]/g, fn);
	}
	// caching fails here...
//    cache:true,
//    headers: {"Cache-Control": "max-age:60", "Pragma": "cache"}
	function loadAppsParameters(appsCbk) {
		 $.ajax({
	        type: "GET",
	        url: "/ogema/widget/apps?action=listAll" + "&user=" + otusr + "&pw=" + otpwd,
	        contentType: "application/json"
	    }).done(function(responseText){
	    	//var json=JSON.parse(responseText);
	    	var json=responseText;
	    	for (var i=0;i<json.length;i++) {
	    		json[i].imageSrc="/ogema/widget/apps?action=getIcon&id=" + json[i].id + "&user=" + otusr + "&pw=" + otpwd;
	    		json[i].description=escapeHTML(json[i].metainfo.Bundle_Description);
	    		if (!json[i].description) json[i].description=" "; // hack... empty field leads to problem with ddslick
	    		json[i].text=escapeHTML(json[i].metainfo.Bundle_Name);
	    		json[i].value=i;
	    		json[i].selected=false;
	    	}
	    	appsCbk(json);
	    });
	}
	var appsCallback = function(input) {
		var appsSel =  $("#appsSelector");
		if (appsSel.length === 0) return;
		appsSel.ddslick({
		    data:input,
		    width:"100%",
		    selectText: "Select App",
		    imagePosition:"left",
		    onSelected: function(selectedData){
		        try {
		        	window.location.href = selectedData.selectedData.webResourcePaths[0];
		        } catch (e) {
		        	console.error("Could not load page",e);
		        }
		    }
		});
	};
	loadLanguageParameters(languageCallback, true);
	loadAppsParameters(appsCallback);

	setMenuLanguage();

	var messageText=$(".messageText");   // TODO handle case that locale changes
	var messageIcon=$(".messageIcon");
	if (messageText.length>0 && messageIcon.length>0) {

		var msgBox = document.getElementById("msgBox");
		var textf = document.getElementById("messageTitle");
		var texttext = document.getElementById("messageFullText");
		var sendingapp = document.getElementById("sendingAppId");
		var msgLink = document.getElementById("msgLink");
		var msgNrBox = document.getElementById("msgNrBox");
		var messageAppLinkBox = document.getElementById("messageAppLinkBox");
		messageAppLinkBox.href = "/de/iwes/ogema/apps/message/reader/index.html";
		$(msgBox).hide();
		$(msgNrBox).hide();
		var messageVisible=false;
		var activeBak = false;
		var msgTime = -1;
		var nullMessage = {};

		nullMessage.link = "#";
		nullMessage.prio = 0;
		function setNullMessageTitle() {
			nullMessage.title = messagesTitle;
		}
		setNullMessageTitle();
		var crMessageForDisplay = nullMessage;
		function setMsgStatusToRead() {
			if (msgTime < 0) return;
			var body  = {};
			body.timestamp = msgTime;
			body.oldStatus = "SENT";
			body.newStatus = "READ";
			$.ajax({
			        type: "POST",
			        url: "/ogema/messaging/service?user=" + otusr + "&pw=" + otpwd,
			        contentType: "application/json",
			        data: JSON.stringify(body)
			    });
		}
		function toggleMessageVisibility() {
			if (messageVisible) {
				$(msgBox).hide();
				setMsgStatusToRead();
			}
			else {
			 	$(msgBox).show();
			}
			messageVisible = !messageVisible;
		}

		function enableMessageDisplay(element) {
			/*element.mouseenter(function(e){
				console.log("hover!");
				$(textf).show();
			});
			element.mouseleave(function(e){
				console.log("Leaving");
				$(textf).hide();
			}); */
			element.click(function(e) {
				 toggleMessageVisibility();
			});
		}

		function disableMessageDisplay(element) {
			/*element.off("mouseenter");
			element.off("mouseleave");*/
			element.off("click");
			$(msgBox).hide();
		}


		//var lastRequest=0; // time of last request for messages
		var nrOfUnreadMsg = 0;
		messageText[0].value=crMessageForDisplay.title;
		var setFieldActive = function(active) {
			 var el = messageText[0];
			 var el2= messageIcon[0];
			 if (active) {
			 	el.classList.remove("inactive");
			 	el.classList.add("active");
			 	el2.classList.remove("inactive");
			 	el2.classList.add("active");
			 	enableMessageDisplay(messageText);
			 }
			 else {
			 	el.classList.remove("active");
			 	el.classList.add("inactive");
			 	el2.classList.remove("active");
			 	el2.classList.add("inactive");
			 	disableMessageDisplay(messageText);
			 }
		};
		var pollInterval = 3000;
		var pollForMessage = function() {
			var interval = pollInterval; // this way interval is final, whereas pollInterval is not
			if (!interval || interval <= 0)
				return;
			setTimeout(function(){
				var loc;
				if (ogema.locale)
					loc = ogema.locale;
				else
					loc = "en";
				$.ajax({
			        type: "GET",
			        url: "/ogema/messaging/service?locale=" + loc + "&lastHighPriority=true&user=" + otusr + "&pw=" + otpwd,
			        contentType: "application/json"
			    }).done(function(responseText){
			    	try {
			    		var json;
			    		if (typeof responseText === "object") {
			    			json = responseText;
			    		}
			    		else if (typeof responseText === "string") {
			    			json = JSON.parse(responseText);
			    		}
			    		crMessageForDisplay=nullMessage;
			    		var active = false;
			    		msgTime = -1;
			    		Object.keys(json).forEach(function(key) { // should contain either 1 or 2 entries, where one of them indicates nr of unread messages
			    			if (key==="-1") return;
			    			crMessageForDisplay = json[key];
			    			active = true;
			    			msgTime = key;
			    		});
			    		nrOfUnreadMsg = json["-1"].nrMsg;
			    		if (active != activeBak) {
			    			activeBak=active;
			    			setFieldActive(active);
			    		}
			    		if (!active) {
			    			$(textf).text("");
			    			$(texttext).text("");
			    			$(sendingapp).text("");
			    			$(msgNrBox).text("");
			    			$(msgNrBox).hide();
			    			msgLink.href="#";
			    			setNullMessageTitle();
			    		}
			    		else {
			    			$(textf).text(crMessageForDisplay.title);
			    			$(texttext).text(crMessageForDisplay.msg);
			    			$(sendingapp).text(crMessageForDisplay.app);
			    			$(msgNrBox).show();
			    			$(msgNrBox).text(nrOfUnreadMsg);
			    			msgLink.href=crMessageForDisplay.link;
			    		}
			    	} catch (e) {}
			    	messageText[0].value=crMessageForDisplay.title + (crMessageForDisplay.app ? "\n" + crMessageForDisplay.app : "");
			    	pollForMessage();
			    });

			}, interval);
		};
		pollForMessage();
		ogema.stopMessagePolling = function() {
			pollInterval = 0;
		};
		ogema.startMessagePolling = function(interval) {
			var wasActive = pollInterval && pollInterval > 0;
			pollInterval = interval;
			if (!wasActive) {
				pollForMessage();
            }
		};
	}
	ogema.logout = function() {
        var onLogoutSuccess = function(data){ 
              loggedOut = true;
              if (ogema.stopMessagePolling) { ogema.stopMessagePolling(); }
              abortAjax();
              window.location.assign(data);
        };
        // logout should happen synchronously, otherwise abortAjax() does not catch all requests(?)  
        $.ajax({
            url: "/ogema/widget/apps?action=logout&user=" + otusr + "&pw=" + otpwd,
            data: "",
            success: onLogoutSuccess,
            dataType: "text",
            async: false
        });        
	};
})();
