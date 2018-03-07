C3Chart.prototype = new GenericWidget();
C3Chart.prototype.constructor = C3Chart;

var c3chartMap = {};

function C3Chart( servletPath, widgetID ) {
	GenericWidget.call( this, servletPath, widgetID );
	this.sendGET();	
}

C3Chart.prototype.update = function( resp ) {
			
	if ( !c3chartMap.hasOwnProperty( this.widgetID ) ) {
		// create new chart and init
		c3chartMap[ this.widgetID ] = { 
				chart : c3.generate( parseData( resp ) ),
				cc	  : resp.cc
		};
		$( '#' + this.widgetID ).append( c3chartMap[ this.widgetID ].chart.element );
		
	} else {
		// update chart
		if ( resp.cc > c3chartMap[ this.widgetID ].cc ) {
			c3chartMap[ this.widgetID ].load( parseData( resp ) );
		} else {
			// do nothing
		}
	}

	function parseData( resp ) {

		var columns = [];
		var xs = {};
		var types = {};
		var data = {};
		var config = {};
		
		
		if ( resp.hasOwnProperty( 'type' ) ) {
			data[ 'type' ] = resp.type;
		}

		for ( var i in resp.rows ) {

			var row = resp.rows[ i ];

			if ( row.hasOwnProperty( 'xs' ) ) {
				columns.push( row.xs );
				xs[ row.ys[ 0 ] ] = row.xs[ 0 ];
				data[ 'xs' ] = xs;
			}

			if ( row.hasOwnProperty( 'ys' ) ) {
				columns.push( row.ys );
				data[ 'columns' ] = columns;
			}

			if ( row.hasOwnProperty( 'type' ) ) {
				types[ row.ys[ 0 ] ] = row.type;
				data[ 'types' ] = types;
			}

		}
		config[ 'data' ] = data;
		
		if ( resp.hasOwnProperty( 'conf' ) ) {
			for ( var j in resp.conf ) {
				var conf = resp.conf[ j ];
				jQuery.extend( config, conf );
			}
		}		
		
		return config;
	}
}
