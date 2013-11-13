/**
 * Charge les données lors du chargement de la page.
 **/
function showCharts() {
	$('div.chart').each(function () {
		var elem = $(this);
		var dataURL = elem.attr('data-url');
		var dataQuery = jQuery.parseJSON( elem.attr('data-query') );
		var dataLabels =  elem.attr('data-labels');
		if(dataLabels) {
			dataLabels = jQuery.parseJSON( dataLabels );
		}
		var dataColors =  elem.attr('data-colors');
		if (elem.hasClass ("d3chart")) {
			showD3Chart(elem, dataURL, dataQuery, dataLabels, dataColors);
		} else if (elem.hasClass ("flotchart")) {
			showFlotChart(elem, dataURL, dataQuery, dataLabels, dataColors);
		}		
	});
}

function startClock() {
// Create two variable with the names of the months and days in an array
var monthNames = [ "Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre" ]; 
// Create a newDate() object
var newDate = new Date();
// Extract the current date from Date object
newDate.setDate(newDate.getDate());
// Output the day, date, month and year   


refreshClock = function() {
	// Create a newDate() object
	var newDate = new Date();
	// Extract the current date from Date object
	newDate.setDate(newDate.getDate());
	var seconds = newDate.getSeconds();
	var minutes = newDate.getMinutes();
	var hours = newDate.getHours();
	// Add a leading zero to seconds value
	$(".clock .seconds").html(( seconds < 10 ? "0" : "" ) + seconds);
	// Add a leading zero to the minutes value
	$(".clock .hours").html(( hours < 10 ? "0" : "" ) + hours + ":"+( minutes < 10 ? "0" : "" ) + minutes);
	$('.clock .date').html(newDate.getDate() + ' ' + monthNames[newDate.getMonth()] + ' ' + newDate.getFullYear());
};

setInterval( refreshClock ,1000);
refreshClock();

}
    

colorTools = function() {
	var analyticaColors = {
			"version" : "1.0.0"
	};
	
	analyticaColors.getColors = function (colorName, nbSeries) {
		if ("DEFAULT" == colorName) {
			//default on ne fait rien
		} else if ("RAINBOW" == colorName || "iRAINBOW" == colorName) {
			var mainColors = [ "#FF0000", "#FFA500", "#FFFF00", "#00FF00", "#00FF00", "rgb(75, 0, 130)", "rgb(238, 130, 238)" ];
			if(colorName.charAt(0) == 'i') { mainColors = mainColors.reverse(); }
			return _interpolateHsl(mainColors, nbSeries);
		} else if ("SPECTRUM" == colorName || "iSPECTRUM" == colorName) {
			var mainColors = [ "rgb(230, 31, 30)", "rgb(230, 230, 30)", "rgb(30, 230, 30)", "rgb(30, 230, 230)", "rgb(30, 30, 230)", "rgb(230, 30, 230)", "rgb(230, 30, 31)" ];
			if(colorName.charAt(0) == 'i') { mainColors = mainColors.reverse(); }
			return _interpolateCatmul(mainColors, nbSeries);
		} else if ("RED2GREEN" == colorName || "iRED2GREEN" == colorName) {
			var mainColors = [ "rgb(255, 51, 51)", "rgb(255, 255, 51)", "rgb(51, 153, 51)" ];
			if(colorName.charAt(0) == 'i') { mainColors = mainColors.reverse(); }
			return _interpolateHsl(mainColors, nbSeries);
		} else if ("HEAT" == colorName || "iHEAT" == colorName) {
			var mainColors = [ "rgb(255, 51, 51)", "rgb(255, 255, 51)", "rgb(51, 153, 51)", "rgb(51, 153, 255)" ];
			if(colorName.charAt(0) == 'i') { mainColors = mainColors.reverse(); }
			return _interpolateHsl(mainColors, nbSeries);
		} else if ("GREEN:INTENSITY" == colorName || "iGREEN:INTENSITY" == colorName) {
			var mainColors = [ "rgb(0, 170, 85)", "rgb(240, 240, 170)" ];
			if(colorName.charAt(0) == 'i') { mainColors = mainColors.reverse(); }
			return _interpolateLinear(mainColors, nbSeries);
		}
	}
	
	_interpolateHsl = function(mainColors, nbColors) {
		return _point2PointColors(mainColors, nbColors, function(t, c1, c2, c3, c4) {
			return d3.interpolateHsl(c2, c3)(t);
		}); 
	}
	
	_interpolateLinear = function(mainColors, nbColors) {
		return _point2PointColors(mainColors, nbColors, function(t, c1, c2, c3, c4) {
			return d3.interpolateRgb(c2, c3)(t);
		});
	}
	
	_interpolateCatmul = function(mainColors, nbColors) {
		return _point2PointColors(mainColors, nbColors, function(t, c1, c2, c3, c4) {
			var empty = {};
			empty.r = null;
			empty.g = null;
			empty.b = null;
			var nc1 = c1 ? d3.rgb(c1) : empty;
			var nc2 = d3.rgb(c2);
			var nc3 = d3.rgb(c3);
			var nc4 = c4 ? d3.rgb(c4) : empty;
			var red = ( Math.max(Math.min(Math.round(_catmull(t, nc1.r, nc2.r, nc3.r, nc4.r)), 255), 0));
			var green = ( Math.max(Math.min(Math.round(_catmull(t, nc1.g, nc2.g, nc3.g, nc4.g)), 255), 0));
			var blue = (Math.max(Math.min(Math.round(_catmull(t, nc1.b, nc2.b, nc3.b, nc4.b)), 255), 0));
			return d3.rgb(red,green,blue);
		});
	}
	
	_point2PointColors = function (mainColors, nbColors, colorInterpolation) {
		var startJ = 0;
		var interpolatedColor = new Array();
		var nbInterpolatedColor = mainColors.length;
		var nbInterpolatedColorDegree = 0;
		while ((nbInterpolatedColor - 1) % (nbColors - 1) != 0 && nbInterpolatedColorDegree < 10) {
			nbInterpolatedColorDegree++;
			nbInterpolatedColor = mainColors.length + nbInterpolatedColorDegree * (mainColors.length - 1);
		}
		nbInterpolatedColorDegree++;
		for (var i = 0; i < mainColors.length - 1; i++) {
			var c1 = i - 1 >= 0 ? mainColors[i - 1] : null;
			var c2 = mainColors[i];
			var c3 = mainColors[i + 1];
			var c4 = i + 2 < mainColors.length ? mainColors[i + 2] : null;
			for (var j = startJ; j < nbInterpolatedColorDegree + 1; j++) {
				var color = colorInterpolation(j / nbInterpolatedColorDegree, c1, c2, c3, c4);
				interpolatedColor.push(color);
			}
			startJ = 1; //on ne refait pas le premier point (dejà atteint)
		}
		var result = new Array();
		for (var i = 0; i < nbColors; i++) {
			var index = (interpolatedColor.length - 1) / (nbColors - 1) * i;
			result.push(interpolatedColor[index]);
		}
		return result;
	}
	
	//Catmull-Rom spline interpolation function
	//p0 et p3 servent a orienter le chemin entre p1 et p2
	//t est une fraction entre 
	_catmull = function(t, inP0, p1, p2, inP3) {
		 var delta = p2 - p1;
		 var p0 = inP0 != null ? inP0 : p1 - delta;
		 var p3 = inP3 != null ? inP3 : p2 + delta;
		return 0.5 * (2 * p1 + (-p0 + p2) * t + (2 * p0 - 5 * p1 + 4 * p2 - p3) * t * t + (-p0 + 3 * p1 - 3 * p2 + p3) * t * t * t);
	}
	return analyticaColors;
}();