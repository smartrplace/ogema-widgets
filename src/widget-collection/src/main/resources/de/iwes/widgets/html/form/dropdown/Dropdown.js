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
        for (var i = 0; i < options.length; i++) {
            var selected = options[i].selected;
            if (selected)
            	hasSelected = true;
            var value = options[i].value;
            var label = options[i].label;
            html += "<option ";

            if (selected === true) {
                html += "selected=\"selected\" ";
            }
            html += "value=\'" + value + "\'>" + label + "</option>";

        }

        this.dropdownOptions.html(html);
        if (!hasSelected && this.syncParam) {
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
				if (!option && options.length > 0) {
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
		if (this.syncParam && hasSelected)
			this.syncParams();
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