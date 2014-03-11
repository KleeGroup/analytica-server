/*
 * Flot plugin to place bars side by side. 
 * This is improved version of Benjamin BUFFET work originally from http://en.benjaminbuffet.com/labs/flot/.
 * 
 * Released under the MIT license by KleeGroup, 20-Nov-2013.
 * 
 * This plugin is active by default (because it should be :)) but for stacked bars.
 * You could desactivate this plugin, by specify the parameter "sidebyside" for the specific serie :
 * 
 * $.plot($("#placeholder"), [{ data: [ ... ], bars : { sidebyside = false }])
 *
 *
 * The plugin adjust the point by adding a value depanding of the barwidth
 * Exemple for 3 series (barwidth : 0.1) :
 *    first bar : x-0.15
 *    second bar : x-0.05
 *    third bar : x+0.05
 *
 */

(function($){
    
	function translateBarsForAxis(series, axis, datapoints, serieIndex, seriesCount) {
        // go through the points, translating them
        var points = datapoints.points,
            ps = datapoints.pointsize,
            barsWidth = series.bars.barWidth,
            newBarsWidth = barsWidth/seriesCount;

        for (var i = 0; i < points.length; i += ps) {
            if (points[i] == null)
                continue;
            points[i] = points[i] - barsWidth/2 + newBarsWidth/2 + serieIndex*newBarsWidth;
        }
        series.bars.barWidth = newBarsWidth; 
    }

    function processDatapoints(plot, series, datapoints) {
        if(series.bars.show && series.bars.sidebyside && !series.stack) {
        	seriesCount = plot.getData().length;
        	serieIndex = plot.getData().indexOf(series);
        	if(series.bars.horizontal) {
        		translateBarsForAxis(series, "yaxis", datapoints, serieIndex, seriesCount);
        	} else {
        		translateBarsForAxis(series, "xaxis", datapoints, serieIndex, seriesCount);
        	}
        }
    }

    	
    function init(plot) {
    	plot.hooks.processDatapoints.push(processDatapoints);
    }

    var options = {
        series : {
            bars : {
            	sidebyside : true
            }
        }
    };
    
    $.plot.plugins.push({
        init: init,
        options: options,
        name: 'sideBySideBars',
        version: '1.0'
    });

})(jQuery)
