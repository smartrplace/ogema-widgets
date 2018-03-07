OpenLayersMap.prototype = new Map();	// inherit from Map // see GeoMap
OpenLayersMap.prototype.constructor = OpenLayersMap;	// have to reset constructor for inheriting class

// TODO attributions etc
// TODO static info windows
function OpenLayersMap(geoMap, jqueryElement) {
	Map.call(this,geoMap, jqueryElement);
	this.mapObject = null;
	this.markers = {};
	this.selectedMarker = null;
	this.popup = null;
	// map types, such as openstreetmap, bing, ...
	//Map<provider id, Map<style id, layer>>
	this.layers = {};
	// contains markers; type ol.layers.Vector
	this.vectorLayer = null;
	// contains markers in OpenLayers ol.source.Vector element
	this.featureSource = null;
	
//	this.layersOSM = null;
//	this.layersBing = null;
	this.activeStyle = null;
	this.activeProvider = null;
	this.infoNode = null;
	this.popover = null;
}

OpenLayersMap.prototype.getId = function() {
	return "openlayers";
}

// @Override
OpenLayersMap.prototype.setDisplayStyle = function(provider, style, key) {
	if (provider === this.activeProvider && style === this.activeStyle)
		return;
	var layer;
	var layersGroup;
	var map = this;
	if (!this.layers.hasOwnProperty(provider) || !this.layers[provider].hasOwnProperty(style)) {
		var source;
		switch(provider) {
		case "openstreetmap":
			source = new ol.source.OSM();
			break;
		case "bing":
			source = new ol.source.BingMaps({key:key, imagerySet:style})
			break;
		case "opentopomap":
			source = new ol.source.XYZ({
		        url: 'https://{a-c}.tile.opentopomap.org/{z}/{x}/{y}.png'
		    })
		//{a|b|c}.tile.opentopomap.org/{z}/{x}/{y}.png
			break;
		default:
			throw new Error("Unsupported map provider " + provider);
		}
		if (!this.layers.hasOwnProperty(provider)) {
			this.layers[provider] = {};
		}
		layer = new ol.layer.Tile({
            source: source
        });
//		this.layers[provider][style] = new ol.layer.Group({
//		    layers: [ layer ]
//		});
		this.layers[provider][style] = layer;
		if (this.mapObject != null) {
			this.mapObject.getLayerGroup().getLayers().getArray().forEach(function(l){
				if (l === map.vectorLayer)
					return;
				l.setVisible(false);
			});
			this.mapObject.addLayer(layer);
			layersGroup = this.mapObject.getLayerGroup();
		} else {
			layersGroup = new ol.layer.Group({
			    layers: [ layer ]
			});
		}
	} else {
		layer = this.layers[provider][style];
		layer.setVisible(true);
		layersGroup = this.mapObject.getLayerGroup();
		layersGroup.getLayers().getArray().forEach(function(l){
			if (l !== layer && l !== map.vectorLayer)
				l.setVisible(false);
		});
	}
	this.activeStyle = style;
	this.activeProvider = provider;
	return layersGroup;
}

// @Override
OpenLayersMap.prototype.getMapProvider = function() {
	return this.activeProvider;
}

//@Override
OpenLayersMap.prototype.getDisplayStyle = function() {
	return this.activeStyle;
}

// @Override
OpenLayersMap.prototype.initialized = function() {
	return this.mapObject != null;
}

//@Override
OpenLayersMap.prototype.getSelectedMarker = function() {
	return this.selectedMarker;
}

//@Override
OpenLayersMap.prototype.hideAllInfoWindows = function() {
	if (this.infoNode != null) {
  	  var markerContainers =  $("#" + this.geoMap.widgetID +  ".ogema-widget").find("#openlayersMarker");
  	  if (markerContainers.length > 0) {
  	  	  this.geoMap.infoWindowBackupNode = markerContainers.get(0).removeChild(this.infoNode);
  	  	  this.infoNode = null;
  	  }
  	  var element = $("#" + this.geoMap.widgetID + ".ogema-widget #osmPopup");
  	  element.popover("destroy");
   }
}

// TODO
OpenLayersMap.prototype.showInfoWindow = function(markerId) {throw new Error("Abstract method called");}
OpenLayersMap.prototype.hideInfoWindow = function(markerId) {throw new Error("Abstract method called");}


//@Override
OpenLayersMap.prototype.showMarkers = function() {
	if (this.vectorLayer != null)
		this.vectorLayer.setVisible(true);
}
//@Override
OpenLayersMap.prototype.hideMarkers = function() {
	if (this.vectorLayer != null)
		this.vectorLayer.setVisible(false);
}


// @Override
OpenLayersMap.prototype.getData = function() {
	if (this.mapObject == null)
		return {};
	var data = {};
	var c = ol.proj.toLonLat(this.mapObject.getView().getCenter());
	data.center = {lat: c[1], lng: c[0]};
	data.zoom = this.mapObject.getView().getZoom();
	// TODO remove markers?
	return data;
}

// @Override
OpenLayersMap.prototype.init = function(data, callback) {
	var height = data.hasOwnProperty("height") ? data.height : null;
	var map = this;
	var initMap = function() {
		map.show();
		if (map.mapObject === null) {
			var options = {};
			options.center = ol.proj.fromLonLat([data.options.center.lng, data.options.center.lat]);
			options.zoom = data.options.zoom;
			// TODO allow for non-standard tiles source
			var key = data.apiKey; // may be undefined
			var layersGroup = map.setDisplayStyle(data.type.id, data.type.displayStyle.id, key);
			console.log("Initializing OSM map!: " + map.mapObject);
			if (height == null)
				map.domElement.style.height=window.outerHeight + "px";
			else
				map.domElement.style.height=height;
			map.mapObject = new ol.Map({
		        target: map.domElement,
		        layers: layersGroup,
		        view: new ol.View(options)
		      });
		}
		if (map.popup == null) {
			var element = $("#" + map.geoMap.widgetID + ".ogema-widget #osmPopup").get()[0];
			map.popup = new ol.Overlay({
		        element: element,
		        positioning: 'bottom-center',
		        stopEvent: false,
		        offset: [0, -50] // FIXME
		      });
		      map.mapObject.addOverlay(map.popup);
		   // display popup on click
		      map.mapObject.on('click', function(evt) {
		        var feature = map.mapObject.forEachFeatureAtPixel(evt.pixel,
		            function(feature) {
		              return feature;
		            });
		        if (map.infoNode != null) {
		        	  var markerContainers =  $("#" + map.geoMap.widgetID +  ".ogema-widget").find("#openlayersMarker");
		        	  if (markerContainers.length > 0) {
		        	  	  map.geoMap.infoWindowBackupNode = markerContainers.get(0).removeChild(map.infoNode);
		        	  	  map.infoNode = null;
		        	  }
		        }
		        if (feature) {
		          var coordinates = feature.getGeometry().getCoordinates();
		          map.popup.setPosition(coordinates);
		          // TODO the positioning does not work; needs to adjust to dynamically loaded widgets!
		          // see http://getbootstrap.com/javascript/#popovers
		          map.popover = $(element).popover({
		            'placement': 'top',
		            'html': true,
		            'content': '<div id=\"openlayersMarker\"></div>'
		          });
		          $(element).popover('show');
		          map.selectedMarker = feature.id;
		          var node = map.geoMap.getInfoWindowNode();
		          if (node != null) {
		        	  $("#" + map.geoMap.widgetID +  ".ogema-widget").find("#openlayersMarker").get(0).appendChild(node);
		        	  map.infoNode  = node;
		          }
		          map.geoMap.sendPOST();
//		           TODO need to change position when widgets have been reloaded -> possibility to wait for reload?
		          var reposition = function() {
		        	  var p = map.jqueryElement.find(".popover");
		        	  if (p.length === 0)
		        		  return true;
		        	  var height = p.height();
		        	  var width = p.width();
		        	  if (width % 2 === 0)
		        		  width = width/2;
		        	  else 
		        		  width = (width+1)/2;
		        	  p.css({marginTop:-(height-25), marginLeft:-(width-10)}); // FIXME offset depending on icon size
		          };
		          setTimeout(reposition, 50); // ?
		        } else {
		          map.selectedMarker = null;
		          map.popover = null;
		          $(element).popover('destroy');
		        }
		      });
		}
		if (typeof callback !== "undefined")
			callback();
	}
	// might as well have been loaded by another widget
	if (typeof OPENLAYERS === "undefined") {
		var scr = document.createElement("script");
		scr.src = "/ogema/widget/geomap/lib/openlayers/ol.js";
		scr.onload = initMap;
		this.domElement.appendChild(scr);
	} else {
		initMap();
	}
	return true;
}

OpenLayersMap.prototype.update = function(data) {
	this.mapObject.getView().setCenter(ol.proj.fromLonLat([data.options.center.lng,data.options.center.lat]));
	this.mapObject.getView().setZoom(data.options.zoom);
	this.setDisplayStyle(data.type.id, data.type.displayStyle.id, data.apiKey);
	var widget = this;
	var featureSource = this.featureSource; // may be null
	Object.keys(widget.markers).forEach(function(key) {
		if (!data.markers.hasOwnProperty(key)) {
			var feature = widget.markers[key];
			widget.markers[key] = undefined; 
			if (featureSource != null) {
				featureSource.removeFeature(feature);
			}
		}
	});
	// TODO treat update case
	var iconFeatures = [];
	Object.keys(data.markers).forEach(function(key) {
		var markerData = data.markers[key];
		var id = markerData.id;
		if (!widget.markers.hasOwnProperty(id)) {
			var location = new ol.geom.Point(ol.proj.fromLonLat([markerData.position.lng,markerData.position.lat]));
			var marker = new ol.Feature({
				  geometry: location,
				  name: markerData.title
			});
			var icon = {};
			icon.src = markerData.icon;
			// scale is relative, we have no idea of the actual icon size
			if (markerData.hasOwnProperty("iconSize")) {
//				icon.scale = markerData.iconScale;
				var arr = markerData.iconSize;
				marker.targetSize = arr;
			}
//			icon.imgSize = 50;
			var iconStyle = new ol.style.Style({
		        image: new ol.style.Icon(icon)
		     });			
			marker.setStyle(iconStyle);
			marker.id = id;
			widget.markers[key] = marker;
			iconFeatures.push(marker);
			if (featureSource != null) {
				featureSource.addFeature(marker);
			}
		}
	});
	// TODO see https://openlayers.org/en/v4.1.1/apidoc/ol.source.Vector.html for update case
	if (this.vectorLayer == null) {
		var vectorSource = new ol.source.Vector({
			  features: iconFeatures //add an array of features
		});
		this.vectorLayer = new ol.layer.Vector({
			  source: vectorSource
		});
		// in foreground, wrt. maps layers, which have z ind 0
		this.vectorLayer.setZIndex(1); 
		this.featureSource = vectorSource;
		this.mapObject.addLayer(this.vectorLayer);
	} 
	// XXX OL seems to lack the basic feature of rescaling an image of unknown size
	var rescale = function() {
		var newArr = [];
		iconFeatures.forEach(function(feature){
			if (!feature.hasOwnProperty("targetSize"))
				return;
			var img;
			try {
				img = feature.getStyle().getImage();
			} catch (e) {
				return;
			}
			var size = img.getSize();
			if (size == null) {
				newArr.push(feature);
				return;
			}
			var sz1 = size[0];
			img.setScale(feature.targetSize[0]/sz1); 
			feature.changed();
		});
		iconFeatures = newArr;
		return iconFeatures.length == 0;
	};
	var failCounter = 0;
	var timer = function t() {
		if (!rescale() && failCounter++ < 100)
			setTimeout(t,1000);
	}
	timer();
}
