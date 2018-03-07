/**
 * The js Function has to named as the Widget-java File, otherwise it will not be 
 * instantiated on client-side
 * 
 * The servletPath is unique for each widgetId, so every Widget is (per default)
 * knowing where its Java counterpart (Servlet) is running.
 * 
 * @param {type} servletPath
 * @returns {undefined}
 */
ListSelect.prototype = new GenericWidget();	// inherit from GenericWidget
ListSelect.prototype.constructor = ListSelect;	// have to reset constructor for inheriting class

function ListSelect(servletPath, widgetID) {
//	console.log("Creating ListSelectWidget class");
	GenericWidget.call(this,servletPath,widgetID);
	this.getSelected; // function defined below
	this.setRows; 
	this.generateRows;
	this.updateSelection;
	this.evaluateParameters;
	this.apply;
	this.currentRowChanged;
	var lw = this;
//	console.log("running ListSelectWidget under "+servletPath);
	var plotContainer = $("#"+widgetID);
	var el =plotContainer.find( "#listWidgetBody");
	

	   var angModId = widgetID;
	   angular.module('listSelWidget' + angModId,[])   
	    .factory('TableRows', function() {
	    // note: rows is an array of objects
	    var rows = [];
	    // header the array of keys of rows
	    var headers = [];
	    return {
	      getRows: function() {
	        return rows;
	      },
	      addRow: function(row) {
	        rows.push(row);
	      },
	      setRows: function(rowsIn) {
	        rows = rowsIn;
	      },
	      getHeaders: function() {
	      	 //console.log('Retrieving headers: ', headers);
	        return headers;
	      },
	      // note: objects is to be an array of objects (key-value pairs) 
	   	  generateRows: function(newHeaders,newRows) {
	   	    headers =  [];
	   	    
	   	    for (var i=0;i<newHeaders.length;i++) {
			  headers.push(newHeaders[i]);
    		}
	   	    for (var i=0;i<newRows.length;i++) {
	   	        var row = newRows[i];
   	    		for (var j=0;j<headers.length;j++) {
	   	    		if (!newRows[i].hasOwnProperty(headers[j].headerItem)) {
	   	    		  row[headers[j].headerItem] = '';
	   	    		} 
   	    		}
   	    		rows.push(row);
   	          } 
	   	   }
	    };
	  })
	    .controller('ListSelectCtrl', function($scope,$http,$timeout, TableRows) {
	 
	 //*********** variables declaration *****************
	 
	    	$scope.rowsSet = TableRows;
	    	$scope.path = '/ogema/widget/listselect';
	    	$scope.logging = {};
	    	$scope.message= '';
	    	$scope.temp = 0;
	    	$scope.test = 'Moinsen!!';
	    	$scope.selected = {};
	    	$scope.selectTableStyle={color:'red'};
	    	$scope.synchronize = true;
	    	$scope.css=[];
	    	$scope.currentRowChanged = {};
	    
	    	$scope.orderColumns = function(a) {
	    		if (a.indexOf('Location')) {
					return -1;	    		
	    		}
				return 0;	    		

	    	};
	    	
	    	$scope.rowSelected = function(row) {
	    	    $scope.selected[row.lWidgetId] = !$scope.selected[row.lWidgetId];
	    	    $scope.currentRowChanged = row;
	    	    //console.log('Toggled selection: ',row,$scope.selected);
	    	    if (!$scope.synchronize) {
	    	    	return;
	    	    }
	    	    /*var message = {};
	    	    Object.keys( $scope.selected).forEach(function(prop) {
	    	    	message[prop] = $scope.selected[prop];
	    	    });
	    	    message.changedRowZZ = row.lWidgetId;*/
	    	    lw.sendPOST();
	    	    
	   /* 	    $http.post(servletPath, message).then(function(response) {	    			
	    			console.log('post response',response.data);
	    			//$scope.selectTableStyle={color:'green'};	
	    			//console.log('Checked elements: ',$(".checkedList"));
	    			
	    		}); */ 
	    	};

	    		
	    	$scope.updateSelection = function() {			
				var selBak = $scope.selected;
				var auxRows = $scope.rowsSet.getRows();
				$scope.selected ={};
				var auxHeaders = $scope.rowsSet.getHeaders();
				for (var k=0;k<auxRows.length;k++) {
					$scope.selected[auxRows[k].lWidgetId] = false;
				}
				Object.keys(selBak).forEach(function(prop) {
					if (!selBak[prop]) { return; }
					for (var k=0;k<auxRows.length;k++) {
						if (auxRows[k].lWidgetId == prop) {
							$scope.selected[auxRows[k].lWidgetId] = true;
							return;
						}	
					}				
	    	  	 });
				//console.log('Selected: ',$scope.selected);
	    	};
	    	

	    	
	    	$scope.getStyleSheet = function() {
	    		var sheets = document.styleSheets;
    			var sheet = null;
    			for (var m=0;m<sheets.length;m++)  {
    				crSheet = document.styleSheets[m];
    				try {				
    					if (crSheet.ownerNode.parentElement.id == widgetID && crSheet.href.indexOf('widget.css')>=0) {
    						sheet = crSheet;
    						break;
    					}
    				} catch(e) {}
    			}
    			return sheet;
	    	};
	    	
	    	$scope.updateCSS = function() {
	    		if ($scope.css.length==0) {
	    			return;
	    		}
	    		console.log('Updating css ',$scope.css)
	    		var sheet= $scope.getStyleSheet();
	    		if (sheet == null) {return;}
	    		for (var n=0;n<$scope.css.length;n++) {
	    			var ruleExists =false;
	    			for (var p=0;p<sheet.cssRules.length;p++) {
	    				if (sheet.cssRules[p] == $scope.css[n]) {
	    					ruleExists = true;
	    					break;
	    				}
	    			}
	    			if (!ruleExists) {
	    				sheet.insertRule($scope.css[n],sheet.cssRules.length);
	    			}
    			}
	    		//console.log('New sheet ', sheet);
	    	};
	    	
	    	$( document ).ready(function() {
	    		$scope.updateCSS();
    		});
	    	
	    	$scope.evaluateParameters = function(parameters) {
	    		if (parameters.hasOwnProperty('pollingInterval')) {
	    			$scope.pollingInterval = parameters.pollingInterval; 
	    		}
	    		if (parameters.hasOwnProperty('responsive')) {
	    			$scope.synchronize = parameters.responsive;
	    		}
	    		if (parameters.hasOwnProperty('css')) {
	    			$scope.css = parameters.css;
	    			$scope.updateCSS();
	    		}	   
	    		else {
	    			$scope.css = [];
	    		}
	    		//console.log('Parameters received: ', parameters);
	    	};
	    	 
	 		   	
	    	// GET list
	    	// PARAM pollRequest: indicates whether the request potentially belongs to a series of polls; must always be false when triggered externally
	    	$scope.getList = function() {
	    		lw.sendGET();
	    	/*	$http.get(servletPath).then(function(response) {
	    			$scope.rowsSet.setRows([]);
					//console.log('Get response:');
					//console.log(response.data);
					var headersReceived = response.data.headers;
					var rowsReceived = response.data.rows;
					var params =  {};
					if (response.data.hasOwnProperty('params')) {
						params = response.data.params;
					}
					$scope.rowsSet.generateRows(headersReceived,rowsReceived);
					$scope.updateSelection();
					$scope.evaluateParameters(params);
	    		}); */	
	    	};
	    	
	    	//  javascript 'interfaces' offered by the widget. 
	    	//    retrieveData: get the list of rows as json objects, and ids of selected items
	    	//    submitSelection: trigger a POST request (synchronize Java model to Javascript one)
	    	//	  sendGET: trigger a GET request (synchronize Javascript view to Java)
	    	// Call e.g. (replace WIDGETID by the div-id of the widget):
	    	//    var contain = {};
	    	//	  $("#WIDGETID").find("#listWidgetBody").trigger('retrieveData',[contain]);
	    	//    var selected = contain.selected;
     	   	//    var rows = contain.rows;	    	
	/*	    el.bind('retrieveData',function(event,container) {
		    	container.selected = $scope.selected;
		    	container.rows = $scope.rowsSet.getRows();
		    });  
		    el.bind('submitSelection',function() {
		    	var message = {};
	    	    Object.keys( $scope.selected).forEach(function(prop) {
	    	    	message[prop] = $scope.selected[prop];
	    	    }); 
	    	    $http.post(servletPath, message).then(function(response) {
	    	    	//console.log('Submit response: ', response.data);
	    	    	$scope.getList();
	    	    });
		    }); 
		    el.bind('sendGET',function() {
		    	//console.log('sendGET executing');
	    	    $scope.getList();
		    }); 
		    el.bind('apply',function() {
		    	$scope.$apply();
		    });  */
		    
		    // expose $scope properties outside angular module
		    lw.getSelected = function() {
		    	return $scope.selected;
		    }
		    lw.setRows = function(rw) {
		    	$scope.rowsSet.setRows(rw);
		    }
		    lw.generateRows = function(hd,rw) {
		    	$scope.rowsSet.generateRows(hd,rw);
		    }
		    lw.updateSelection = function() {
		    	$scope.updateSelection();
		    }
		    lw.evaluateParameters = function(params) {
		    	$scope.evaluateParameters(params);
		    }
		    lw.currentRowChanged = function() {
		    	return $scope.currentRowChanged;
		    }
		    lw.apply = function() {
		    	$scope.$apply();
		    }

	    }); 

//	    console.log('listselect trying to bootstrap... ', el);
	    angular.bootstrap(el, ['listSelWidget'   + angModId]);  // mol equivalent to ng-app tag...
//	    angular.bootstrap(angDivs[domCounter], ['listSelWidget'  + angModId]);
		this.sendGET(); // init
	   
};

// @Override
ListSelect.prototype.update = function(data) {
	this.setRows([]);
//	console.log('Get response:',data);
	var headersReceived = data.headers;
	var rowsReceived = data.rows;
	var params =  {};
	if (data.hasOwnProperty('params')) {
		params = data.params;
	}
	this.generateRows(headersReceived,rowsReceived);
	this.updateSelection();
	this.evaluateParameters(params);
	this.apply();
}

//@Override
ListSelect.prototype.getSubmitData = function() {
	var message = {};
	var sel = this.getSelected();
//	console.log('Get submit data:',sel);
    Object.keys(sel).forEach(function(prop) {
    	message[prop] = sel[prop];
    });
    message.changedRowZZ = this.currentRowChanged().lWidgetId;
    return message;
}

