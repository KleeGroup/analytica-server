
function showFlotChart(elem, datas, dataMetrics, dataQuery, dataLabels, dataColors) {
	var allMetrics = dataQuery.datas.split(';');
	var timedSeries = datas[0].time;
	var flotDatas = toFlotData(datas, dataMetrics, allMetrics, dataLabels, timedSeries);
	var defaultChartOptions = createDefaultChartOptions(allMetrics, dataQuery, datas, timedSeries, dataColors);
  	var chartOptions;
	if (elem.hasClass ("barchart")) {
		chartOptions = getBarOptions(dataQuery, datas, timedSeries, dataColors);
	} else if (elem.hasClass ("linechart")) {
		chartOptions = getLineOptions(dataQuery, timedSeries, dataColors);
	} else if (elem.hasClass ("stakedareachart")) {
		chartOptions = getStakedAreaOptions(dataQuery, timedSeries, dataColors);
	} else if (elem.hasClass ("sparkbar")) {
		chartOptions = getSparkBarOptions(dataQuery, datas, timedSeries, dataColors);
	} else if (elem.hasClass ("sparkline")) {
		chartOptions = getSparkLineOptions(dataQuery, datas, timedSeries, dataColors);
	}
	var options = $.extend({}, defaultChartOptions, chartOptions);
	var plot = $.plot(elem, flotDatas, options);
	elem.bind("plothover", options.tooltipsFunction(plot));
}


function createDefaultChartOptions(allMetrics, dataQuery, datas, timedSeries, dataColors) {
	var options = {
			series: {
				//specific
			},
			grid: { hoverable: true, borderWidth:1	},
			xaxis: {
				
			},
			yaxis: {
				min:0
			},
			legend : {
				show : false /*on cache la legend, en attendant de la placer mieux */
			},
			tooltipsFunction : function(plot) {
				var previousPoint = null;
				return showTooltipsFunction(previousPoint, plot, false, true);
			}
		};
		if(timedSeries) {
			options.xaxis = {
				min: datas[0].time,
			    max: datas[datas.length-1].time,
				mode: "time",
				timezone : "browser",
				timeformat: analyticaTools.getTimeFormat(dataQuery.timeDim)
			};	
		} else {
			options.xaxis = {
					mode: "categories",
					tickFormatter : function(value, axis) {
				    	return axis.ticks[Math.round(value)].label;
					}
			};			
		}
		
		if(dataColors) {
			options.colors = analyticaTools.getColors(dataColors, allMetrics.length);		
		}
		
		return options;
}

function getBarOptions(dataQuery, datas, timedSeries, dataColors) {
	var options = {
			series: {
				bars: {
					show: true,
					barWidth: timedSeries?analyticaTools.getTimeDimStep(dataQuery.timeDim):0.8,
					align: "center",
				}
			},
			tooltipsFunction : function(plot) {
				var previousPoint = null;
				return showTooltipsFunction(previousPoint, plot, true, true);
			}
	};
	return options;
}

function getSparkBarOptions(dataQuery, datas, timedSeries, dataColors) {
	var options = {
		series: {
			bars: {
				show: true,
				barWidth: analyticaTools.getTimeDimStep(dataQuery.timeDim),
				align: "center",
			}
		},
		xaxis: {
			min: datas[0].time,
		    max: datas[datas.length-1].time+analyticaTools.getTimeDimStep(dataQuery.timeDim)/2,
			mode: "time",
			timezone : "browser",
			timeformat: analyticaTools.getTimeFormat(dataQuery.timeDim)
		},
		grid: {
			show: false,
			hoverable: false,
		},
		legend: {
		    show: false,
		}};
		return options;
}

function getLineOptions(dataQuery, timedSeries, dataColors) {
	var options = {
		series: {
			lines: {
				show: true,
			},
			points: { 
				show:true,
				radius:1,
				fill:false
			}
		}};
	return options;
}

function getSparkLineOptions(dataQuery, datas, timedSeries, dataColors) {
	var options = {
		series: {
			lines: {
				show: true,
				fill: true
				/*fillColor: {colors: [ { opacity: 1.0 }, { opacity: 1.0 } ]},*/
			},
			points: { 
				show:true,
				radius:1,
				fill:false
			}
		},
		xaxis: {
			ticks : [],
		},yaxis: {
			ticks : [],
		},
		grid: {
			show: false,
			hoverable: false,
			borderWidth:1
		},
		legend: {
		    show: false
		}};
		return options;
}

function getStakedAreaOptions(dataQuery, timedSeries, dataColors) {
	var options = {
		series: {
			lines: {
				show: true,
				fill: true,
				fillColor: {colors: [ { opacity: 1.0 }, { opacity: 1.0 } ]},
	            lineWidth: 0
			},
			stack: true
		},
		tooltipsFunction : function(plot) {
			var previousPoint = null;
			return showTooltipsFunction(previousPoint, plot, false, false);
		}};
		return options;
}


/** Conversion de données servers Map<NomMetric, <date, value>> en données Flot.
 * function toFlotData(datas, dataLabels) {
	
	var newSeries = new Array();
	for(var metric in datas) {
		var serie = new Object();
		if(dataLabels && dataLabels[metric]) {
			serie.label = dataLabels[metric];
		}
		serie.data = new Array();
		for(var i = 0 ; i<datas[metric].length; i++) {
			serie.data[i] = [datas[metric][i].x, datas[metric][i].y];		
		}
		
		newSeries.push(serie);
	}
	return newSeries;
}*/


function showTooltipsFunction(previousPoint, plot, showAllValues, showSameValue) {
	/** Fonction de tooltip Flot.*/
	showTooltips = function (event, pos, item) {
		if (item) {
			if (previousPoint != item.seriesIndex+':'+item.dataIndex) {
				previousPoint = item.seriesIndex+':'+item.dataIndex;
				$("#tooltip").remove();
				var x = item.datapoint[0];
				var y = item.datapoint.length>2?item.datapoint[1]-item.datapoint[2]:item.datapoint[1];
				var content = "<span class='xvalue'>" +item.series.xaxis.tickFormatter(x,item.series.xaxis) + "</span><br/> ";
				
				for(var i=0; i<plot.getData().length;i++) {
					var serie = plot.getData()[i];
					var serieDatapoints = serie.datapoints;
					content += "<div>";
					var otherY = null;
					for(var j=0; j < serieDatapoints.points.length; j+=serieDatapoints.pointsize) {
						if(serieDatapoints.points[j] == x) {
							otherY = serieDatapoints.pointsize>2?serieDatapoints.points[j+1]-serieDatapoints.points[j+2]:serieDatapoints.points[j+1];
							break;
						}
					}
					//if we found a data at the same x
					//and we draw all series in tooltip or the data get the same Y (ie : one hide the other in chart)
					//then we render all datas
					if(otherY && (showAllValues || ((showSameValue || item.seriesIndex == i ) && otherY == y))) {
						if(serie.label) {
							content += "<span class='serie' style='color:"+serie.color+"'>"+serie.label+"</span> : ";
						}
						content += "<span class='yvalue'>" + serie.yaxis.tickFormatter(otherY,serie.yaxis) + "</span>";
					}
					content += "</div>";
				}
				analyticaTools.showTooltip(item.pageX, item.pageY, content, item.series.color);
			}
		} else {
			$("#tooltip").remove();
			previousPoint = null;
		}
	}
	return showTooltips;
}
/*function showTooltipsFunction(previousPoint, plot) {
	// Fonction de tooltip Flot.
	console.log('showTooltipsFunction');
	showTooltips = function (event, pos, item) {
		if (item) {
			if (previousPoint != item.seriesIndex+':'+item.dataIndex) {
				previousPoint = item.seriesIndex+':'+item.dataIndex;
				$("#tooltip").remove();
				var x = item.datapoint[0];
				var y = item.datapoint.length>2?item.datapoint[1]-item.datapoint[2]:item.datapoint[1];
				var serie = item.series;
				
				var content = "<span class='xvalue'>" +serie.xaxis.tickFormatter(x,item.series.xaxis) + "</span><br/> ";
				if(serie.label) {
					content += "<span class='serie' style='color:"+serie.color+"'>"+serie.label+"</span> : ";
				}
				content += "<span class='yvalue'>" + serie.yaxis.tickFormatter(y,serie.yaxis) + "</span>";
				analyticaTools.showTooltip(item.pageX, item.pageY, content, item.series.color);
			}
		} else {
			$("#tooltip").remove();
			previousPoint = null;
		}
	}
	return showTooltips;
}*/

/** Conversion de données servers List<date, Map<NomMetric, value>> en données Flot.*/
function toFlotData(datas, metrics, allMetrics, dataLabels, timedSeries) {
	_endsWith = function(string, suffix) {
	    return string.indexOf(suffix, string.length - suffix.length) !== -1;
	};
	var categorieIndex = new Array();
	
	var newSeries = new Array();
	for(var i = 0 ; i< metrics.length; i++) {
		var metric = metrics[i];
		var serie = new Object();
		if(dataLabels && dataLabels[metric]) {
			serie.label = dataLabels[metric];
		}
		serie.data = new Array();
		for(var j = 0 ; j<datas.length; j++) {
			var x = timedSeries ? datas[j].time : datas[j].category; // timed series by default, else categories 
			var y = datas[j].values[metric];
			serie.data[j]=([x, y]);
		}
		var index = allMetrics.indexOf(metric);
		serie.color = index>=0 ? index : i;
		
		if(!serie.label) {
			if(_endsWith(metric, 'count')) {
				serie.label = "Quantit&eacute;";
			} else if(_endsWith(metric, 'mean')) {
				serie.label = "Moyenne";
			} else if(_endsWith(metric, 'min')) {
				serie.label = "Minimum";
			} else if(_endsWith(metric, 'max')) {
				serie.label = "Maximum";
			}
		}
		
		newSeries.push(serie);
	}
	return newSeries;
}


/*function removeGap(flotDatas) {
	var newSeries = new Array();
	for(var i = 0 ; i<flotDatas.length; i++) {
		var serie = flotDatas[i];
		for(var j = 0 ; j<serie.data.length; j++) {
			serie.data.splice(j, 0, [serie.data[j][0]+timeStep, serie.data[j][1]])
			serie.data[j]=([datas[j].time, datas[j].values[metric]]);
		}		
	}
	return newSeries;
}*/

