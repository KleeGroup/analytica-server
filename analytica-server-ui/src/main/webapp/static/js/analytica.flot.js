
function showFlotChart(elem, datas, dataMetrics, dataQuery, dataLabels, dataColors) {
	var allMetrics = dataQuery.datas.split(';');
	var timedSeries = datas[0].time;
	var flotDatas = toFlotData(datas, dataMetrics, allMetrics, dataLabels, timedSeries);
	var defaultChartOptions = createDefaultChartOptions(allMetrics, dataQuery, datas, timedSeries);
	setColorOptions(defaultChartOptions, allMetrics.length, dataColors);
	
	var chartOptions;
	if (elem.hasClass ("barchart")) {
		chartOptions = getBarOptions(dataQuery, datas, timedSeries, dataColors);
	} else if (elem.hasClass ("linechart")) {
		chartOptions = getLineOptions(dataQuery, datas, timedSeries, dataColors);
	} else if (elem.hasClass ("stakedareachart")) {
		chartOptions = getStakedAreaOptions(dataQuery, timedSeries, dataColors);
	} else if (elem.hasClass ("sparkbar")) {
		chartOptions = getSparkBarOptions(dataQuery, datas, timedSeries, dataColors);		
	} else if (elem.hasClass ("sparkline")) {
		chartOptions = getSparkLineOptions(dataQuery, datas, timedSeries, dataColors);
	} else if (elem.hasClass ("donut")) {
		flotDatas = inverseFlotData(flotDatas, dataLabels);
		chartOptions = getDonutOptions(dataQuery, datas, timedSeries, dataColors);
		setColorOptions(chartOptions, flotDatas.length, dataColors);
	}
	var options = $.extend(defaultChartOptions, chartOptions);
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
		return options;
}

function setColorOptions(options, nbSeries, dataColors) {
	if(dataColors) {
		options.colors = analyticaTools.getColors(dataColors, nbSeries);
	}
	return options;
}


function getDonutOptions(dataQuery, datas, timedSeries, dataColors) {
	var options = {
		series: {
			pie: {
				show: true,
				radius :1,
				innerRadius: 0.5,
				label: {
	                show: false,
	            }
			}
		},
		tooltipsFunction : function(plot) {
			var previousPoint = null;
			return showPieTooltipsFunction(previousPoint, plot, false, true);
		}};
	return options;
}


function getBarOptions(dataQuery, datas, timedSeries, dataColors) {
	var options = {
			series: {
				bars: {
					//horizontal:true,
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
	if(timedSeries) {
		options.xaxis = {
				mode: "time",
				timezone : "browser",
				timeformat: analyticaTools.getTimeFormat(dataQuery.timeDim)
			};		
	}
	
	return options;
}

function getSparkBarOptions(dataQuery, datas, timedSeries, dataColors) {
	var options = {
		series: {
			bars: {
				show: true,
				fill:1,
				//barWidth: analyticaTools.getTimeDimStep(dataQuery.timeDim),
				lineWidth : 4, //on fixe la taille en pt
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
			hoverable: true,
		},
		legend: {
		    show: false,
		}};
		return options;
}

function getLineOptions(dataQuery,  datas, timedSeries, dataColors) {
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
		},
		legend: {
		    show: true,
		},
		crosshair: { mode: "x" }};
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
		crosshair: { mode: "xy" },
		grid: {
			show: false,
			hoverable: true,
			  autoHighlight: false,
			borderWidth:1
		},
		legend: {
		    show: true
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
		crosshair: { mode: "x" },
		grid: {
			show: true,
			hoverable: true
		},
		legend: {
		    show: true
		}
		};
		return options;
}


/** Conversion de donn�es servers Map<NomMetric, <date, value>> en donn�es Flot.
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
function isDifferentEnougth(previousPoint, item, pos) {
	if(!previousPoint) {
		return true;
	}
	var distance = Math.sqrt(Math.pow(previousPoint.pageX-pos.pageX,2)+Math.pow(previousPoint.pageY-pos.pageY,2));
	return previousPoint.seriesIndex != item.seriesIndex || previousPoint.dataIndex != item.dataIndex || distance > 25;
}

function getPreviousPoint(item, pos) {
	return {seriesIndex : item.seriesIndex,
		dataIndex: item.dataIndex,
		pageX : pos.pageX,
		pageY : pos.pageY};
}

function showPieTooltipsFunction(previousPoint, plot, showAllValues, showSameValue) {
	/** Fonction de tooltip Flot.*/
	showTooltips = function (event, pos, item) {
		if (item) {
			if (isDifferentEnougth(previousPoint, item, pos)) {
				previousPoint = getPreviousPoint(item,pos); 
				$("#tooltip").remove();
				var x = item.series.label;
				var percent = parseFloat(item.datapoint[0]).toFixed(2);
				var y = item.datapoint[1][0][1];
				var content = "<span class='xvalue'>" + x + "</span><br/> ";
				content += "<div>";
				//content += "<span class='serie' style='color:"+item.series.color+"'>"+item.series.metriclabel+"</span> : ";
				content += "<span class='yvalue'>" + y + " (" + percent +"%) </span>";
				content += "</div>";
				analyticaTools.showTooltip(pos.pageX, pos.pageY, content, item.series.color);
			}
		} else {
			$("#tooltip").remove();
			previousPoint = null;
		}
	}
	return showTooltips;
}

function showTooltipsFunction(previousPoint, plot, showAllValues, showSameValue) {
	/** Fonction de tooltip Flot.*/
	showTooltips = function (event, pos, item) {
		if (item) {
			if (isDifferentEnougth(previousPoint, item, pos)) {
				previousPoint = getPreviousPoint(item, pos);
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


/** Conversion de donn�es servers List<date, Map<NomMetric, value>> en donn�es Flot.*/
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

function inverseFlotData(flotData, dataLabels) {
	var newSeries = new Array();
	for(var i = 0 ; i< flotData[0].data.length; i++) {
		var serie = new Object();
		serie.label = flotData[0].data[i][0];
		if(dataLabels && dataLabels[flotData[0].data[i][0]]) {
			serie.label = dataLabels[flotData[0].data[i][0]];			
		}
		serie.data  = flotData[0].data[i][1];
		newSeries.push(serie);
	}
	return newSeries;
}

