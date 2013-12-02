/**
 * Charge les données lors du chargement de la page.
 **/
function showCharts() {
	$('div.chart').each(function () {
		var elem = $(this);
		var dataUrl = getDataUrl(elem); 
		var dataQuery = jQuery.parseJSON( elem.attr('data-query') );
		var dataLabels =  elem.attr('data-labels');
		if(dataLabels) {
			dataLabels = jQuery.parseJSON( dataLabels );
		}
		var dataIcons =  elem.attr('data-icons');
		if(dataIcons) {
			dataIcons = jQuery.parseJSON( dataIcons );
		}
		var dataColors =  elem.attr('data-colors');
		
		$.getJSON(dataUrl, dataQuery)      
		.done(
		  function( datas ) {
			  if(notEmpty(datas)) {
				  var dataMetrics = dataQuery.datas.split(';');
				  if (elem.hasClass ("bignumber")) {
					  showBigNumber(elem, datas, dataMetrics, dataQuery, dataLabels, dataIcons, dataColors);
				  } else if (elem.hasClass ("objective")) {
					  showObjective(elem, datas, dataMetrics, dataQuery, dataLabels, dataIcons, dataColors);
				  } else if (elem.hasClass ("healthMonitor")) {
					  showHealthMonitor(elem, datas, dataMetrics, dataQuery, dataLabels, dataIcons, dataColors);
				  } else if (elem.hasClass ("d3chart")) {
					  showD3Chart(elem, datas, dataMetrics, dataQuery, dataLabels, dataColors);
				  } else if (elem.hasClass ("flotchart")) {
					  showFlotChart(elem, datas, dataMetrics, dataQuery, dataLabels, dataColors);
				  }
			  }
		  });
		toggle = getZoomFunction(elem);
		elem.on("click", toggle);
		//elem.on("mouseout", function() { elem.removeClass('zoom'); });
	});
}

function notEmpty(datas) {
	for(var i = 0; i < datas.length; i++) {
		for(var value in datas[i].values) {
			return true;
		}
	}
	return false;
}
/**
 * Charge les données lors du chargement de la page.
 **/
function showTables() {
	$('div.datatable').each(function () {
		var elem = $(this);
		var dataUrl = getDataUrl(elem); 
		var dataQuery = jQuery.parseJSON( elem.attr('data-query') );
		var dataColumns =  elem.attr('data-columns');
		if(dataColumns) {
			dataColumns = jQuery.parseJSON( dataColumns );
		}
		completeDataTableQuery(dataColumns, dataQuery);
		$.getJSON(dataUrl, dataQuery)      
		.done(
		  function( datas ) {
			  showDataTable(elem, datas, dataColumns, dataQuery);			  
		  });
		//toggle = getZoomFunction(elem);
		//elem.on("click", toggle);
		//elem.on("mouseout", function() { elem.removeClass('zoom'); });
	});
	
}

getDataUrl = function(elem) {
	var dataUrl = elem.attr('data-url');
	if(dataUrl.indexOf('$')>-1) {			
		dataUrl = dataUrl.replace(/\$([a-z:]+)/gi, function(match, v) {
			return analyticaTools.getUrlVar(v);
		});
	}
	return dataUrl;
} 

getZoomFunction = function(elem) {
	var toggle = function() {
		var parent = elem.parent().parent();
		if(parent.hasClass('zoom')) {
			$("#overlay").remove();
			parent.removeClass('zoom');
		} else {
			$("<div/>", {"id":"overlay", "class":"modal-backdrop fade in"}).appendTo($("body"));
			parent.addClass('zoom');
		}
	};
	return toggle;
} 

function startClock() {
//Create elements :
$('<span/>', {class: 'hours'}).appendTo('.clock');
$('<span/>', {class: 'seconds'}).appendTo('.clock');
$('<span/>', {class: 'date'}).appendTo('.clock');

// Create two variable with the names of the months and days in an array
var monthNames = [ "Janvier", "F&eacute;vrier", "Mars", "Avril", "Mai", "Juin", "Juillet", "Ao&ucirc;t", "Septembre", "Octobre", "Novembre", "D&eacute;cembre" ]; 
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
setInterval(refreshClock,1000);
refreshClock();
}


function showBigNumber(elem, datas, dataMetrics, dataQuery, dataLabels, dataIcons, dataColors) {
	_getFirstValue = function(datas, metric) {
		  for(var i = 0; i < datas.length; i++) {
			  var val = datas[i].values[metric];
			  if(datas[i].values[metric]) {
				  return [datas[i].time, val];
			  }
		  }
	  };
	  _getLastValue= function(datas, metric) {
		  for(var i = datas.length-1; i >= 0; i--) {
			  var val = datas[i].values[metric];
			  if(datas[i].values[metric]) {
				  return [datas[i].time, val];
			  }
		  }
	  };
		
	for(var i = 0; i < dataMetrics.length; i++) {
		var metric = dataMetrics[i];
		var firstValue = _getFirstValue(datas, metric);
		var lastValue = _getLastValue(datas, metric);
		var icon = dataIcons?dataIcons[metric]:undefined;
		
		var metricDiv = $('<div/>', {class:'metricLine'}).appendTo(elem);
		var flotPlaceholder = $('<div/>', {class:'sparkbar', width:((datas.length)*6)+'px'})
		.appendTo(metricDiv);
		showFlotChart(flotPlaceholder, datas, [metric], dataQuery, dataLabels, dataColors);
		var tendanceArrow = lastValue[1] > firstValue[1] ? 'fa fa-arrow-up' : 
							lastValue[1] < firstValue[1] ? 'fa fa-arrow-down' : 
							'fa fa-arrow-right';
		
		var divTitle = $('<div/>', {class:'title'})		  
		  .appendTo(metricDiv);
		
		var divNumber = $('<div/>', {class:'number'}).appendTo(divTitle);
		divNumber.append(Math.round(lastValue[1]))
		  .append($('<i/>', {class:tendanceArrow}));
		
		if(icon) {
			//On ajoute l'icon avant le libellé, il n'y a pas la place avant le nombre
			divTitle.append($('<i/>', {class:icon}));
			divTitle.append('&nbsp;');
		}
		
		divTitle.append(dataLabels[metric]);		
	  }
}

function showObjective(elem, datas, dataMetrics, dataQuery, dataLabels, dataIcons, dataColors) {
		
	colors = analyticaTools.getColors(dataColors, datas.length * dataMetrics.length/2 );
	var colorIndex = 0;
	for(var i = 0; i < datas.length; i++) {
		for(var j = 0; j < dataMetrics.length; j+=2) { //Les métrics vont 2 par deux
		var metricDiv = $('<div/>', {class:'gauge'}).appendTo(elem);
		var gaugeDiv = $('<div/>').appendTo(metricDiv);
		var guid = analyticaTools.guid();
		var processingPlaceholder = $('<canvas/>', {id:guid, 'data-processing-sources':'static/pde/circleGauge.pde'})
		.appendTo(gaugeDiv);
		var x = datas[i].category?datas[i].category:dataMetrics[j];
		var label = dataLabels[x]; //category become x
		if(!label) {
			label = datas[i].category;
		}
		var icon = dataIcons?dataIcons[x]:undefined;
		var current = datas[i].values[dataMetrics[j]]; //first metric mean the current value
		var objective = datas[i].values[dataMetrics[j+1]]; //second metric mean the objective value
		var percent = current * 100 / objective;
		
		Processing.reload();
		function getSetValueFunction(current, objective, d3color) { return function(processing) {
			var gap = Math.max(elem.width() % 120, elem.height() % (120+20));
			var nbRow = Math.ceil(elem.height() / (120+20));
			var nbChartByRow = Math.ceil(datas.length / nbRow);
			var chartSize = Math.max(120, Math.min((elem.width() - elem.width() % 120) / nbChartByRow, (elem.height() - elem.height() % (120+20)) / nbRow));
			processing.size(chartSize, chartSize);
			processing.initColor(d3color.r, d3color.g, d3color.b);
			processing.setValue(current, objective);
		}};
		analyticaTools.doWhenProcessingReady(guid, getSetValueFunction(current, objective, d3.rgb(colors[colorIndex])));
		
		var divTitle = $('<div/>', {class:'title'})		  
		  .appendTo(gaugeDiv);
		divTitle.append(label);
		
		var divNumber = $('<div/>', {class:'number', style:'color:'+colors[colorIndex]}).appendTo(gaugeDiv);
		if(icon) {
			divNumber.append($('<i/>', {class:icon}));
		}
		divNumber.append(Math.round(percent)+'&nbsp;% ');
		colorIndex++;
		}
	  }	
}

analyticaTools = function() {
	var analyticaTools = {
			"version" : "1.0.0"
	};
	
	//From https://gist.github.com/varemenos/2531765
	analyticaTools.getUrlVar =  function (key) {
		var result = new RegExp(key + "=([^&]*)", "i").exec(window.location.search); 
		return result && unescape(result[1]) || ""; 
	};
	
	analyticaTools.guid = function () {
		//http://stackoverflow.com/questions/105034/how-to-create-a-guid-uuid-in-javascript
		function s4() {
			  return Math.floor((1 + Math.random()) * 0x10000)
			             .toString(16)
			             .substring(1);
		};
		return s4() + s4() + '-' + s4() + '-' + s4() + '-' +
	           s4() + '-' + s4() + s4() + s4();		
	};
	
	analyticaTools.doWhenProcessingReady = function (guid, callbackFunction) {
		var timer = {key:null};
		timer.key = setInterval( getTimer(guid, callbackFunction, timer), 500);

		function getTimer(guid, callbackFunction, timer) {
			return function() { 
				if(Processing.getInstanceById(guid)) {
					callbackFunction(Processing.getInstanceById(guid));
					clearInterval(timer.key);
				}
			}
		}
	};
	
	analyticaTools.showTooltip = function (x, y, contents, serieColor) {
		var attrs = {display: "none", top: y + 5, left :  x + 5, "border-color":serieColor};
		var elem = $("<div id='tooltip'>" + contents + "</div>").appendTo("body");//we must appends in order to get the correct size
		if($( window ).width() - elem.width() - x -5 < 20) { //if no more space at right, we align a right of the cursor
			attrs.left = 'initial';
			attrs.right = $( window ).width() - x + 5;			
		}
		elem.css(attrs).fadeIn(200);
		
	}
	

	analyticaTools.getTimeDimStep = function(timeDim) {
		if(timeDim == 'Year') {
			return 364*24*60*60*1000.0;
		} else if(timeDim == 'Month') {
			return 28*24*60*60*1000.0;
		} else if(timeDim == 'Day') {
			return 24*60*60*1000.0;
		} else if(timeDim == 'Hour') {
			return 60*60*1000.0;
		} else if(timeDim == 'QuarterHour') {
			return 15*60*1000.0;
		} else if(timeDim == 'SixMinutes') {
			return 6*60*1000.0;
		} else if(timeDim == 'Minute') {
			return 60*1000.0;
		}
		return 60*1000.0;
	}
	
	analyticaTools.getTimeFormat = function (timeDim) {
		if(timeDim == 'Year') {
			return "%Y";
		} else if(timeDim == 'Month') {
			return "%m/%y";
		} else if(timeDim == 'Day') {
			return "%e/%m";
		} else if(timeDim == 'Hour') {
			return "%Hh";
		} else { //'QuarterHour' || 'SixMinutes' || 'Minute'
			return "%H:%M";
		}
	}
		
	analyticaTools.getColors = function (colorName, nbSeries) {
		if ("DEFAULT" == colorName) {
			//default on ne fait rien
			return;
		} 
		var resultColors;
		if ("RAINBOW" == colorName || "iRAINBOW" == colorName) {
			var mainColors = [ "#FF0000", "#FFA500", "#FFFF00", "#00FF00", "#00FF00", "rgb(75, 0, 130)", "rgb(238, 130, 238)" ];
			resultColors = _interpolateHsl(mainColors, nbSeries);
		} else if ("SPECTRUM" == colorName || "iSPECTRUM" == colorName) {
			var mainColors = [ "rgb(230, 31, 30)", "rgb(230, 230, 30)", "rgb(30, 230, 30)", "rgb(30, 230, 230)", "rgb(30, 30, 230)", "rgb(230, 30, 230)", "rgb(230, 30, 31)" ];
			resultColors = _interpolateCatmul(mainColors, nbSeries+1); //+1 pour ne pas reprendre la dernière couleur
		} else if ("RED2GREEN" == colorName || "iRED2GREEN" == colorName) {
			var mainColors = [ "rgb(255, 51, 51)", "rgb(250, 235, 0)", "rgb(51, 200, 51)" ];
			resultColors = _interpolateHsl(mainColors, nbSeries);
		} else if ("GREEN2BLUE" == colorName || "iGREEN2BLUE" == colorName) {
			var mainColors = [ "rgb(51, 153, 51)", "rgb(51, 153, 200)", "rgb(51, 51, 255)" ];
			resultColors = _interpolateHsl(mainColors, nbSeries);
		} else if ("HEAT" == colorName || "iHEAT" == colorName) {
			var mainColors = [ "rgb(255, 51, 51)", "rgb(255, 255, 51)", "rgb(51, 153, 51)", "rgb(51, 153, 255)" ];
			resultColors = _interpolateHsl(mainColors, nbSeries);
		} else if ("GREEN:INTENSITY" == colorName || "iGREEN:INTENSITY" == colorName) {
			var mainColors = [ "rgb(51, 153, 51)", "rgb(170, 250, 170)" ];
			resultColors = _interpolateLinear(mainColors, nbSeries);
		} else if ("ANDROID" == colorName || "iANDROID" == colorName) {
			var mainColors = [ "#0099CC", "#9933CC", "#CC0000", "#FF8800", "#669900"  ];
			//var mainColors = [ "#33B5E5", "#AA66CC", "#ff4444", "#ffbb33", "#99cc00" ];
			resultColors = _interpolateHsl(mainColors, nbSeries);
		} else if ("ANDROID:LIGHT" == colorName || "iANDROID:LIGHT" == colorName) {
			var mainColors = [ "#33B5E5", "#AA66CC", "#ff4444", "#ffbb33", "#99cc00" ];
			resultColors = _interpolateHsl(mainColors, nbSeries);
		}  
		if(colorName.charAt(0) == 'i') { 
			resultColors = resultColors.reverse(); 
		}
		return resultColors;
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
			var empty = {r:null,g : null,b : null };
			
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
	return analyticaTools;
}();