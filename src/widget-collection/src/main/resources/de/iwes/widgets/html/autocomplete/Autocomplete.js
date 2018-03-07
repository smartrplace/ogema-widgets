/**
 * Based on Bootstrap-3-Typeahead, a port of the original 
 * Bootstrap2 typeahead item. It is recommended to use 
 * typeahead.js instead, but this does not work well 
 * with Bootstrap.
 * 
 * https://github.com/bassjobsen/Bootstrap-3-Typeahead
 * (Apache2 license)
 * 
 * TODO test with mobile browser
 */
Autocomplete.prototype = new GenericWidget();
Autocomplete.prototype.constructor = Autocomplete;


function Autocomplete(servletPath, widgetID) {

    GenericWidget.call(this, servletPath, widgetID);
    this.input = $("#" + this.widgetID).find("#textField");
    this.selected = null;
    this.sendGET();
}

//Autocomplete.prototype.triggerSubmit = function( selectedOption ) {
//	console.log("Selected an option!! " + selectedOption );
//	console.log("Same value directly read from input field: " + this.input.val());
//	this.selected = selectedOption;
//	this.sendPOST();
//}

Autocomplete.prototype.update = function (data) {
	
	var el2 = $('#' + this.widgetID + ' .dropdown-menu');
	this.input.remove(); // quite a drastic mehtod... otherwise it is impossible to remove the old autocomplete element
	el2.remove();
	
	$('#' + this.widgetID).append("<input class=\"typeahead\" type=\"text\" id=\"textField\" placeholder=\"Enter text\"></input>");
	this.input = $("#" + this.widgetID).find("#textField");
	
	var stateStrings = [];
	if (data.hasOwnProperty("states")) {
		stateStrings = data.states;
	}
	if (data.hasOwnProperty("value")) 
		this.input.val(data.value);
	else
		this.input.val("");
	
	// Bloodhound is the typeahead suggestion engine
	// instead of using a fixed array of allowed values, 
	// passed to the 'local' field, one could use 'prefetch',
	// and provide a URL
	// multiple datasets can be supported as well
	var states = new Bloodhound({
		  datumTokenizer: Bloodhound.tokenizers.whitespace,
		  queryTokenizer: Bloodhound.tokenizers.whitespace,
		  // `states` is an array of state 
		  local: stateStrings
	});
	
	//
	states.initialize();
	var ac = this;
	var callback = function() { // this funny construction is required to preserve the 'this' object in the function call... 
		ac.sendPOST.call(ac);
	};
	
	// name: array of Strings
	// source: substring matcher
	// limit: make it scrollable
	$('#' + this.widgetID + ' .typeahead').typeahead({
//		  hint: true,
//		  highlight: true,
//		  limit: 10,  
//		  minLength: 1
//		},
//		{
		  items:'all',
		  minLength: data.minLength,
		  afterSelect: callback ,
		  source: states.ttAdapter()
		}
	);
	// change default display style to inline, to be compatible with TextField
//	$('#' + this.widgetID + ' .twitter-typeahead').attr("style","position: relative; display: inline;")
	
};

/*
 * Copied from TextField
 */
Autocomplete.prototype.getSubmitData = function() {
	var textFieldValue = this.input.val();
    return textFieldValue;
}