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
DynamicTable.prototype = new GenericWidget();				// inherit from GenericWidget
DynamicTable.prototype.constructor = DynamicTable;	// have to reset constructor for inheriting class

function DynamicTable(servletPath, widgetID) {     // constructor
	GenericWidget.call(this,servletPath,widgetID);
	var clw = this;
	//console.log("running ComplexTableWidget under "+servletPath);
	var plotContainer = $("#"+widgetID);
	var el = plotContainer.find("#ComplexTableWidgetBody");
	var isPolling = false;
	this.evaluateParameters = function(object) {}; // function defined in angular controller below
	this.setRows = function(object,rows,cols) {};  // function defined in angular controller below
	this.getRows = function(object) {};

	//********* angular stuff ***********/
    angular.module('ComplexTableWidget' + widgetID,[])   
	    .factory('ComplexRows', function($sce) {
	    var html = {};
	    var plainObj = {};
	    var rows = [];
	    var cols = [];
	    return {
	    	  getHtml: function() {
	    		  return html;
	    	  },
	    	  getRows: function() {
	    		  return rows;
	    	  },
	    	  getCols: function() {
	    		  return cols;
	    	  },
	    	  setHtml: function(objIn,rowsIn,colsIn) {
	    		rows = rowsIn;
	    		cols = colsIn;
	    		if (objIn.length == 0) {
	    			html = {};
	    			rows = [];
	    			cols = [];
	    			plainObj = objIn;
	    			return;
	    		}  
//	    		rows = Object.keys(objIn);
//	    		firstRow = rows[0];
//	    		console.log("firstRow: ",firstRow,"keys:", Object.keys(objIn[firstRow]));
//	    		cols = Object.keys(objIn[firstRow]);
	    		html = {};
	    		for (var i=0;i<rows.length;i++) {
	    			html[rows[i]] = {};
	    			for (var j=0;j<cols.length;j++) {
	    				var cont = objIn[rows[i]][cols[j]];
	    				if (typeof cont === "undefined") {
	    					html[rows[i]][cols[j]] = "";
	    				} else {
	    					html[rows[i]][cols[j]] =  $sce.trustAsHtml(cont);
	    				}				
	    			}	
	    		}
	    		// delete old cells that are no longer available
	    		Object.keys(plainObj).forEach(function(oldRow) {
	    			if (!objIn.hasOwnProperty(oldRow)) {
	    				delete html[oldRow];
	    				return;
	    			}	    			
	    			Object.keys(plainObj[oldRow]).forEach(function(oldCol) {
	    				if (!objIn[oldRow].hasOwnProperty(oldCol)) {
	    					delete html[oldRow][oldCol];
	    				}
	    			});
	    		});
	    		plainObj = objIn;
	    		// $scope.html[row][col] = $sce.trustAsHtml($scope.options.cellHTML[row][col]);
	    	  }
	    };
	  })
	    .controller('ComplexTableCtrl', function($scope,$http,$timeout, ComplexRows) {
	 //*********** variables declaration *****************
	    	$scope.entriesSet = ComplexRows;
	    	$scope.options = {};
	    	clw.evaluateParameters = function(options) {
	    		$scope.options = options;
	    	};
	    	// make setRows function available outside angular controller (access as 'this.setRows(...)')
	    	clw.setRows = function(rw,rows,cols){ 
	    		$scope.entriesSet.setHtml(rw,rows,cols); 
	    		$scope.$apply();
//	    		console.log("Set rows... new status ",$scope.entriesSet.getRows(),"columns:",$scope.entriesSet.getCols(),"html:",$scope.entriesSet.getHtml());
    		};
    		clw.getRows = function() {
    			return $scope.entriesSet.getRows();
    		};
	    }); 

	    //console.log('complextable trying to bootstrap... ', el);
	    angular.bootstrap(el, ['ComplexTableWidget'   + widgetID]); 
	    var counter = 0;
	    var tmout = 10;
	    var tic = function() {
	    	setTimeout(function() {
	    		counter = counter+1;
	    		//clw.blockLoadWidgets = true; // note: this is only valid for a single GET request
	    		clw.sendGET();
	    		tmout=200;
	    		//try {
		    		//if (clw.getRows().length == 0 && counter < 25) {
		    			//tic();
		    		//}
		    	//} catch (e) {}
	    	}, tmout);
	    };  
	    tic();
	    
};

//@Override
DynamicTable.prototype.update = function(data) {
	//console.log("Updating ComplexTableWidget",data);
	if (data.hasOwnProperty('options')) {
		this.evaluateParameters(data.options);
	}
	if (data.hasOwnProperty('html') && data.hasOwnProperty('rows') && data.hasOwnProperty('cols')) {
		//console.log("Received new set of rows",data.html);
		this.setRows(data.html,data.rows,data.cols);
		ogema.reloadWidgets(); // updates subwidgets
	}
/*	if (data.hasOwnProperty('css')) {
		var el = $("#" + this.widgetID).find( "#ComplexTableWidgetBody");
		if (data.css != el.css()) {
			el.removeAttr("style");
	        el.css(data.css);
	    }
	} */
}

