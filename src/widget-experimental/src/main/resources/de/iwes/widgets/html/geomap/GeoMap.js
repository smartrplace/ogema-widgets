GeoMap.prototype = new GenericWidget();	// inherit from GenericWidget
GeoMap.prototype.constructor = GeoMap;	// have to reset constructor for inheriting class

/**
 * Abstract class that must be implemented to wrap concrete map libaries,
 * such as Google maps, OpenLayers maps, Leaflet maps, etc.
 * Instances are never deleted, instead they may be hidden and
 * displayed again.
 * @param geoMap
 * @param domElement
 * @returns
 */
function Map(geoMap, jqueryElement) {
	this.geoMap = geoMap;
	this.jqueryElement = jqueryElement;
	this.domElement = typeof jqueryElement !== "undefined" ? jqueryElement.get(0) : null;
	/**
	 * Most map libraries support different display styles,
	 * such as road view, satellite or terrain. type is an 
	 * identifier for these display types.
	 */
	this.show = function() {
		this.domElement.style.display="block"; // ?
	}
	this.hide = function() {
		this.domElement.style.display="none";
	}

}

// override these in derived class
Map.prototype.getId = function() {throw new Error("Abstract method called");};
/**
 * Override in derived class -> should load any required
 * libraries, check for necessary API keys, etc. If any required
 * resource is not available, return false.
 */
Map.prototype.init = function(data, callback) {throw new Error("Abstract method called");}
/**
 * Override in subclass
 * TODO check; maybe this is better done in update function only? 
 */
Map.prototype.setDisplayStyle = function(provider, style, apiKey) {throw new Error("Abstract method called");}
Map.prototype.getMapProvider = function() {throw new Error("Abstract method called");}
Map.prototype.getDisplayStyle = function() {throw new Error("Abstract method called");}
/**
 * Override in derived class
 */
Map.prototype.update = function(data) {throw new Error("Abstract method called");}
/**
 * Override in derived class
 */
Map.prototype.initialized = function() {throw new Error("Abstract method called");}
/**
 * Override in derived class.
 * Return an object of the type
 * { center: { lat: 59.212, lng: 10.32 }, zoom: 7  }
 */
Map.prototype.getData = function() {throw new Error("Abstract method called");}
/**
 * Override in derived class
 */
Map.prototype.hideAllInfoWindows = function() {throw new Error("Abstract method called");}
/**
 * Override in derived class; return a marker id or null
 */
Map.prototype.getSelectedMarker = function() {throw new Error("Abstract method called");}
Map.prototype.showInfoWindow = function(markerId) {throw new Error("Abstract method called");}
Map.prototype.hideInfoWindow = function(markerId) {throw new Error("Abstract method called");}
Map.prototype.showMarkers = function() {throw new Error("Abstract method called");}
Map.prototype.hideMarkers = function() {throw new Error("Abstract method called");}

function GeoMap(servletPath, widgetID) {
	GenericWidget.call(this,servletPath,widgetID);
	// keys: type ids, values Map objects (libraries)
	this.maps = {};
	// the active Map object; all others must be hidden
	this.activeMap = null;
	// object of the form 
	// { center: { lat: 59.212, lng: 10.32 }, zoom: 7  }
	this.dataBak = {};
	this.trackMarkers = false;
	this.infoWindowBackupNode = null;
	this.infoWindowInitialized = false;
	// TODO change backupSnippet id to infoWidget id, if set
	this.sendGET();
};

// @Override
GeoMap.prototype.update = function(data) {
	if (this.infoWindowBackupNode == null && data.hasOwnProperty("infoWidget")) {
		var backupSnippets = $(this.element).find("#infoWindowBackupSnippet").get(0);
		var node = document.createElement("div");
		node.id = data.infoWidget.id;
		node.classList.add("ogema-widget");
		backupSnippets.appendChild(node);
		this.infoWindowBackupNode = node;
		ogema.reloadWidgets();
	}
	var lib = data.lib.id;
	if (this.activeMap == null || this.activeMap.getId() !== lib) {
		this.destroy();
		if (this.dataBak.hasOwnProperty("zoom")) {
			data.options.zoom = this.dataBak.zoom;
			this.dataBak.zoom = undefined;
		}
		if (this.dataBak.hasOwnProperty("center")) {
			data.options.center = this.dataBak.center;
			this.dataBak.center = undefined;
		}
	} 
	// TODO case that provider type changes and/or display style
	else if (this.activeMap != null &&
			(this.activeMap.getMapProvider() !== data.type.id ||
			this.activeMap.getDisplayStyle() !== data.type.displayStyle.id)) {
		// keep current position if display style changed (as a heuristic rule)
		var dataBak = this.activeMap.getData();
		data.options.center = dataBak.center;
		data.options.zoom = dataBak.zoom;
	}
	var widget = this;
	// XXX callback hell
	var callback = function() {
		if (widget.activeMap != null) {
			widget.activeMap.update(data);
			widget.activeMap.show();
		}
	}
	if (this.activeMap == null) {
		this.trackMarkers = data.trackMarkers;
		this.loadMap(lib, data.lib.jsClassName, data, callback);
	}
	else {
		callback();
	}
}

GeoMap.prototype.getInfoWindowNode = function() {
	if (this.infoWindowBackupNode == null)
		return null;
	if (this.infoWindowInitialized) {
		var copy = this.infoWindowBackupNode;
		this.infoWindowBackupNode = null;
		return copy;
	}
	this.infoWindowInitialized = true;
	var backupSnippets = $(this.element).find("#infoWindowBackupSnippet").get(0);
	return backupSnippets.removeChild(this.infoWindowBackupNode);
}

/**
 * @param type: e.g. google, openlayers
 * @param className: e.g. GoogleMap, OpenLayersMap
 */
GeoMap.prototype.loadMap = function(type, className, data, callback) {
	var widget = this;
	if (!this.maps.hasOwnProperty(type)) {
		var jqueryElement = $("#" + widget.widgetID + ".ogema-widget>#" + type + "Map");
		var createInstance = function() {
			var map = new window[className](widget,jqueryElement);
			widget.maps[type] = map;
			widget.activeMap = map;
			if (!map.init(data, callback)) {
				console.error("Could not load map of type " + type);
				widget.maps[type] = undefined;
				widget.activeMap = null;
				return;
			}
		}
		// load js file
		var scr = document.createElement("script");
		scr.src = "/ogema/widget/geomap/" + className + ".js";
		scr.onload = createInstance;
		widget.element.appendChild(scr);
	} else {
		this.activeMap = widget.maps[type];
		callback();
	}
}

//
GeoMap.prototype.destroy = function() {
	if (this.activeMap == null)
		return;
	this.dataBak = this.activeMap.getData();
	this.activeMap.hideAllInfoWindows();
	this.activeMap.hide();
	// TODO hide markers? Such as in Google, marker.setMap(null)?
	this.activeMap = null;
	// TODO set active marker to null?
}

//@Override
GeoMap.prototype.getSubmitData = function() {
	var data = {};
	if (this.activeMap != null) {
		var marker = this.activeMap.getSelectedMarker();
		if (marker != null) 
			data.marker = marker;
	}
	return data;
}

