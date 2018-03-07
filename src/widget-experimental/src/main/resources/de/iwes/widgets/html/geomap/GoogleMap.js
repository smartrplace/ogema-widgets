GoogleMap.prototype = new Map();	// inherit from Map // see GeoMap
GoogleMap.prototype.constructor = GoogleMap;	// have to reset constructor for inheriting class

// TODO backup info window when this is hidden
function GoogleMap(geoMap, jqueryElement) {
	Map.call(this,geoMap, jqueryElement);
	this.mapObject = null;
	this.markers = {};
	this.selectedMarker = null;
	this.markerIdCnt = 0;
}

GoogleMap.prototype.getId = function() {
	return "google";
}

// @Override 
// only provider Google supported
GoogleMap.prototype.setDisplayStyle = function(provider, style, key) {
	this.mapObject.setMapTypeId(style);
}

//@Override
GoogleMap.prototype.getDisplayStyle = function() {
	return this.mapObject.getMapTypeId();
}

//@Override
GoogleMap.prototype.getMapProvider = function() {
	return "google";
}


// @Override
GoogleMap.prototype.initialized = function() {
	return this.mapObject != null;
}

//@Override
GoogleMap.prototype.getSelectedMarker = function() {
	return this.selectedMarker;
}

//@Override
GoogleMap.prototype.hideAllInfoWindows = function() {
	this.backupInfoWindow();
	var widget = this;
	Object.keys(this.markers).forEach(function(key) {
		var marker = widget.markers[key];
		if (marker.hasOwnProperty("infoWindow")) {
			marker.infoWindow.close(); 
			marker.infoWindow.opened = false;
		}
	});
}

GoogleMap.prototype.backupInfoWindow = function() {
	var infoContainers = this.jqueryElement.find(".info-container");
	for (var i = 0;i<infoContainers.length;i++) {
		var c = infoContainers.get(i);
		// closed ones should have count 0
		if (c.childElementCount === 1) {
			var child = c.childNodes[0];
			this.geoMap.infoWindowBackupNode = c.removeChild(child);
			break;
		}
	}
}

// TODO case that infoWindowWidget is set
GoogleMap.prototype.showInfoWindow = function(markerId) {
	this.hideAllInfoWindows();
	var set  = false;
	if (this.markers.hasOwnProperty(markerId)) {
		var marker = this.markers[markerId];
		if (marker.hasOwnProperty("infoWindow")) {
			marker.infoWindow.open(this.mapObject, marker);
			if (marker.hasOwnProperty("containerId")) {
				 var container = map.jqueryElement.find("#infoContainer" + marker.containerId + ".info-container").get(0);
				 var infoNode = map.geoMap.getInfoWindowNode();
				 if (infoNode != null) {
					container.appendChild(infoNode);
				 }  	
			}
		    marker.infoWindow.opened = true;
		    this.selectedMarker = key;
		    set  =true;
		}
	}
	if (!set) {
		this.selectedMarker = null;
	}
}

GoogleMap.prototype.hideInfoWindow = function(markerId) {
	if (this.selectedMarker = markerId) {
		this.backupInfoWindow();
		if (this.markers.hasOwnProperty(markerId)) {
			var marker = this.markers[markerId];
			if (marker.hasOwnProperty("infoWindow")) {
				marker.infoWindow.close();
			    marker.infoWindow.opened =false;
			}
		}
		this.selectedMarker = null;
	}
}


GoogleMap.prototype.hideMarkers = function() {
	var map = this;
	Object.keys(map.markers).forEach(function(key) {
		map.markers[key].setMap(null);
	});
}

//@Override
GoogleMap.prototype.showMarkers = function() {
	var map = this;
	Object.keys(map.markers).forEach(function(key) {
		map.markers[key].setMap(map.mapObject);
	});
}

// @Override
GoogleMap.prototype.getData = function() {
	if (this.mapObject == null)
		return {};
	var data = {};
	data.center = {lat: this.mapObject.center.lat(), lng: this.mapObject.center.lng()};
	data.zoom = this.mapObject.zoom;
	return data;
}

// @Override
GoogleMap.prototype.init = function(data, callback) {
	console.log("GoogleMap init called: ")
	if (!data.hasOwnProperty("apiKey")) {
		console.log("API key missing, cannot load Google map");
		return false;
	}
	var height = data.hasOwnProperty("height") ? data.height : null;
	var map = this;

	var initMap = function() {
		// https://stackoverflow.com/questions/12410062/check-if-infowindow-is-opened-google-maps-v3
		if (!google.maps.InfoWindow.prototype.hasOwnProperty("opened"))
			google.maps.InfoWindow.prototype.opened = false;
		map.show();
		if (map.mapObject === null)
			map.mapObject = new google.maps.Map(map.domElement, data.options);
//		else
		// https://stackoverflow.com/questions/4064275/how-to-deal-with-google-map-inside-of-a-hidden-div-updated-picture
		if (height == null)
			map.domElement.style.height=window.outerHeight + "px";
		else
			map.domElement.style.height=height;
		google.maps.event.trigger(map.mapObject, 'resize');
		if (typeof callback !== "undefined")
			callback();
//		setTimeout(function() {
//			google.maps.event.trigger(map.mapObject, 'resize'); 
//			if (typeof callback !== "undefined")
//				callback();
//		}, 100);
	}
	// might as well have been loaded by another widget
	if (typeof google === "undefined" || typeof google.maps === "undefined") {
		var scr = document.createElement("script");
		scr.src = "https://maps.googleapis.com/maps/api/js?key=" + data.apiKey;
		scr.onload = initMap;
		document.body.appendChild(scr);
	} else {
		initMap();
	}
	return true;
}

GoogleMap.prototype.update = function(data) {
	this.mapObject.setCenter(data.options.center);
	this.mapObject.setZoom(data.options.zoom);
	this.mapObject.setMapTypeId(data.type.displayStyle.id);
	var map = this;
	Object.keys(map.markers).forEach(function(key) {
		if (!data.markers.hasOwnProperty(key)) {
			// FIXME ?
			map.markers[key].setMap(null);
			map.markers[key] = undefined; 
		}
	});
	Object.keys(data.markers).forEach(function(key) {
		var markerData = data.markers[key];
		var id = markerData.id;
		if (!map.markers.hasOwnProperty(id)) {
			var props = {};
			props.position = markerData.position;
			props.map = map.mapObject;
			if (markerData.hasOwnProperty("icon")) {
				var icon = {};
				icon.url = markerData.icon;
				if (markerData.hasOwnProperty("iconSize")) {
					// FIXME
//					var scale = markerData.iconScale*100;
					var size = markerData.iconSize; 
					icon.scaledSize = new google.maps.Size(size[0], size[1]);
				}
				props.icon = icon;
			}
			if (markerData.hasOwnProperty("title"))
				props.title = markerData.title;
			var marker = new google.maps.Marker(props);
			map.markers[key] = marker;
			var iw = true;
			if (markerData.hasOwnProperty("infoWindow")) {
				var infoProp = {};
				infoProp.content = markerData.infoWindow;
				if (markerData.hasOwnProperty("infoWindowSize"))
					infoProp.maxWidth = markerData.infoWindowSize;
				marker.infoWindow = new google.maps.InfoWindow(infoProp);
				marker.addListener('click', function() {
					var open = marker.infoWindow.opened;
					map.hideAllInfoWindows();
					if (!open) {
					    marker.infoWindow.open(map.mapObject, marker);
					    marker.infoWindow.opened = true;
					    map.selectedMarker = key;
					} else {
						map.selectedMarker = null;
					}
					if (map.geoMap.trackMarkers) {
						map.geoMap.sendPOST();
					}
				});
			} else if (data.hasOwnProperty("infoWidget")) {
				var infoProp = {};
				var icIf = map.markerIdCnt++;
				marker.containerId = icIf;
				infoProp.content = "<div class=\"info-container\" id=\"infoContainer" + icIf + "\"></div>"; // TODO?
				marker.infoWindow = new google.maps.InfoWindow(infoProp);
				marker.addListener('click', function() {
					var open = marker.infoWindow.opened;
					map.hideAllInfoWindows(); 
					if (!open) {
					    marker.infoWindow.open(map.mapObject, marker);
					    marker.infoWindow.opened = true;
					    map.selectedMarker = key;
					    var container = map.jqueryElement.find("#infoContainer" + icIf + ".info-container").get(0);
					    var node = map.geoMap.getInfoWindowNode();
					    if (node != null) {
							container.appendChild(node);
						} else { // FIXME should not happen
							container.innerHtml = data.infoWidget.tag;
							ogema.reloadWidgets();
						}
						marker.infoWindow.setContent
					} else {
						map.selectedMarker = null;
					}
					if (map.geoMap.trackMarkers) {
						map.geoMap.sendPOST();
					}
				});
			} else {
				iw = false;
			}
			// FIXME would be easier to use a single infoWindow that is moved to the marker position when needed
			if (iw) {
				marker.infoWindow.addListener("closeclick", function() {
					marker.infoWindow.open(map.mapObject,marker);
					map.backupInfoWindow(); 
					marker.infoWindow.close();
					marker.infoWindow.opened = false;
				});
			}
		}
	});
}
