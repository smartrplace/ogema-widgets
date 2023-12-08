Dropdown.prototype = new GenericWidget();
Dropdown.prototype.constructor = Dropdown;


function Dropdown(servletPath, widgetID) {
 
    GenericWidget.call(this, servletPath, widgetID);
    this.dropdown = $("#" + widgetID + ".ogema-widget").find(">#dropdown");
    this.dropdownOptions = this.dropdown.find(">#list");
    
    this.sendValueOnChange = true;
    this.syncParam = undefined;
    var tmp = this;
    this.dropdownOptions.change(function () {
		if (tmp.syncParam)
			tmp.syncParams();
    	if(tmp.sendValueOnChange) {
    		tmp.sendPOST();
    	}
    });
    this.syncParams = function() {
		var val = tmp.dropdown[0].querySelector("select").value;
		var url = new URL(window.location);
		url.searchParams.set(tmp.syncParam, val);
		window.history.pushState(null, "", url);
	};
    this.sendGET();  
}

Dropdown.prototype.update = function (data) {
    var html = "";

    if (data.hasOwnProperty("css")) {
        this.dropdownOptions.css(data.css);
    }
    if (data.hasOwnProperty("sendValueOnChange")) {
    	this.sendValueOnChange = data.sendValueOnChange;
    }
    var disabled = data.hasOwnProperty("disabled") && data.disabled;
   	this.dropdownOptions.attr("disabled",disabled);
   	this.syncParam = data.syncParam || undefined;

    if (data.hasOwnProperty("options")) {
        var hasSelected = false;
        var options = data.options;
        var html = "";
        var optGroupsActive = data.optGroupsActive === true;
        if (optGroupsActive) {
            var allOptGroups = [];
            var isSorted = true;
            for (var i = 0; i < options.length; i++) {
                var opt = options[i];
            	var group = opt.optGroup ? opt.optGroup : "__none__";
            	var currentIdx = allOptGroups.indexOf(group);
            	if (currentIdx < 0)
            	    allOptGroups.push(group);
            	else if (currentIdx != allOptGroups.length - 1) {
            	    isSorted = false;
            	    break;
            	}
            }
            if (!isSorted) {
            	var optsByGroup = {};
                for (var i = 0; i < options.length; i++) {
                    var opt = options[i];
		    var group = opt.optGroup ? opt.optGroup : "__none__";
		    if (Object.keys(optsByGroup).indexOf(group) < 0)
		    	optsByGroup[group] = [];
		    optsByGroup[group].push(opt);
            	}
            	options = [];
            	var keys = Object.keys(optsByGroup);
            	for (var i = 0; i < keys.length; i++) {
            	    var value = optsByGroup[keys[i]];
            	    for (var j = 0; j < value.length; j++) {
            	        options.push(value[j]);
            	    }
		}
            }
        }
        var previousOptGroup = "";
        for (var i = 0; i < options.length; i++) {
            var opt = options[i];
            var selected = opt.selected;
            if (selected)
            	hasSelected = true;
            var value = opt.value;
            var label = opt.label;
	    if (optGroupsActive) {
                var group = opt.optGroup ? opt.optGroup : "Other";
                if (group !== previousOptGroup) {
                    if (previousOptGroup !== "")
                        html += "</optgroup>";
                    html += "<optgroup label=\"" + group + "\">"
                    previousOptGroup = group;
              	}
	    }
            html += "<option ";
            if (selected === true) {
                html += "selected=\"selected\" ";
            }
            if (opt.tooltip)
                html += "title=\"" + opt.tooltip +  "\" ";
            html += "value=\'" + value + "\'>" + label + "</option>";

        }
	if (optGroupsActive && previousOptGroup !== "")
	    html += "</optgroup>";
        this.dropdownOptions.html(html);
        if (this.syncParam) {
	    	if (!hasSelected || data.defaultSelected) {
				var params = new URLSearchParams(window.location.search);
				var value = params.get(this.syncParam);
				if (value !== null) {
					value = value.toLowerCase();
					var optValueFound;
					var optLabelFound;
					for (var i = 0; i < options.length; i++) {
						var opt = options[i];
						if (opt.value.toLowerCase() === value) {
							optValueFound = opt;
							break;
						}
						if (opt.label.toLowerCase() === value)
							optLabelFound = opt;
					}
					var option = optValueFound || optLabelFound;
					if (!option && options.length > 0 && !data.defaultSelected) {
						option = options[0];
						hasSelected = true;
					}
					if (option) {
						this.dropdown[0].querySelector("select").value = option.value; // should trigger a POST request, but doesn't
						if (this.sendValueOnChange)
							this.sendPOST();
					}
				}
			} 
			if (hasSelected)
				this.syncParams();
		}
    }
};

Dropdown.prototype.getSubmitData = function () {
    var data = [];

    $("#" + this.widgetID + " option:selected").each(function () {
      //  data = "" + $(this).attr("value");
      data.push($(this)[0].value);
    });
    return data;
};
