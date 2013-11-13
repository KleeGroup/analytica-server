
function showFlotChart(elem, dataUrl, dataQuery, dataLabels, dataColors) {
	$.getJSON(dataUrl, dataQuery)      
	.done(
	  function( datas ) {
		  	var flotDatas = toFlotData(datas, dataQuery.datas.split(';'), dataLabels);
		  	var defaultChartOptions = createDefaultChartOptions(dataQuery, datas, dataColors);
		  	var chartOptions;
			if (elem.hasClass ("barchart")) {
				chartOptions = getBarOptions(dataQuery, dataColors);
			} else if (elem.hasClass ("linechart")) {
				chartOptions = getLineOptions(dataQuery, dataColors);
			} else if (elem.hasClass ("stakedareachart")) {
				chartOptions = getStakedAreaOptions(dataQuery, dataColors);
			}
			var options = $.extend({}, defaultChartOptions, chartOptions);
			var plot = $.plot(elem, flotDatas, options);
			var previousPoint = null;
			elem.bind("plothover", showTooltipsFunction(previousPoint, plot));
	  });
}

function createDefaultChartOptions(dataQuery, datas, dataColors) {
	var options = {
		series: {
			//specific
		},
		grid: { hoverable: true, borderWidth:1	},
		xaxis: {
			min: datas[0].time,
		    max: datas[datas.length-1].time,
			mode: "time",
			timezone : "browser",
			timeformat: getTimeFormat(dataQuery.timeDim)
		},
		yaxis: {
			min:0
		}};
		if(dataColors) {
			options.colors = colorTools.getColors(dataColors, dataQuery.datas.split(';').length);		
		}
		return options;
}

function getBarOptions(dataQuery, flotDatas, dataColors) {
	var options = {
		series: {
			bars: {
				show: true,
				barWidth: getBarWidth(dataQuery.timeDim)
			}
		}};
		return options;
}

function getLineOptions(dataQuery, flotDatas, dataColors) {
	var options = {
		series: {
			lines: {
				show: true
			}
		}};
	return options;
}

function getStakedAreaOptions(dataQuery, flotDatas, dataColors) {
	var options = {
		series: {
			lines: {
				show: true,
				fill: true,
				fillColor: {colors: [ { opacity: 1.0 }, { opacity: 1.0 } ]},
	            lineWidth: 0
			},
			stack: true
		}};
		return options;
}


function showTooltip(x, y, contents, serieColor) {
	$("<div id='tooltip'>" + contents + "</div>").css({
		display: "none",
		top: y + 5,
		left: x + 5,
		"border-color":serieColor,
	}).appendTo("body").fadeIn(200);
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


function showMultiTooltipsFunction(previousPoint, plot) {
	/** Fonction de tooltip Flot.*/
	showTooltips = function (event, pos, item) {
		if (item) {
			if (previousPoint != item.seriesIndex+':'+item.dataIndex) {
				previousPoint = item.seriesIndex+':'+item.dataIndex;
				$("#tooltip").remove();
				var x = item.datapoint[0];
				var content = "<span class='xvalue'>" +item.series.xaxis.tickFormatter(x,item.series.xaxis) + "</span><br/> ";
				for(var i=0; i<plot.getData().length;i++) {
					var serie = plot.getData()[i];
					content += "<div>";
					var y;
					for(var j=0; j < serie.data.length; j++) {
						if(serie.data[j][0] == x) {
							y = serie.data[j][1];
							break;
						}
					}
					if(y > 0) {
						if(serie.label) {
							content += "<span class='serie' style='color:"+serie.color+"'>"+serie.label+"</span> : ";
						} else {
							content += "<span class='serie'>valeur</span> : ";
						}
						content += "<span class='yvalue'>" + serie.yaxis.tickFormatter(y,serie.yaxis) + "</span>";
					}
					content += "</div>";
				}
				showTooltip(item.pageX, item.pageY, content, item.series.color);
			}
		}
	}
	return showTooltips;
}
function showTooltipsFunction(previousPoint, plot) {
	/** Fonction de tooltip Flot.*/
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
				} else {
					content += "<span class='serie'>valeur</span> : ";
				}
				content += "<span class='yvalue'>" + serie.yaxis.tickFormatter(y,serie.yaxis) + "</span>";
				showTooltip(item.pageX, item.pageY, content, item.series.color);
			}
		}
	}
	return showTooltips;
}

/** Conversion de données servers List<date, Map<NomMetric, value>> en données Flot.*/
function toFlotData(datas, metrics, dataLabels) {
	var newSeries = new Array();
	for(var i = 0 ; i<metrics.length; i++) {
		var metric = metrics[i];
		var serie = new Object();
		if(dataLabels && dataLabels[metric]) {
			serie.label = dataLabels[metric];
		}
		serie.data = new Array();
		for(var j = 0 ; j<datas.length; j++) {
			serie.data[j] = [datas[j].time, datas[j].values[metric]];			
		}
		newSeries.push(serie);
	}
	return newSeries;
}

function getBarWidth(timeDim) {
	if(timeDim == 'Year') {
		return 364*24*60*60*1000;
	} else if(timeDim == 'Month') {
		return 28*24*60*60*1000;
	} else if(timeDim == 'Day') {
		return 24*60*60*1000;
	} else if(timeDim == 'Hour') {
		return 60*60*1000;
	} else if(timeDim == 'Minute') {
		return 60*1000;
	}
	return 60*1000;
}

function getTimeFormat(timeDim) {
	if(timeDim == 'Year') {
		return "%Y";
	} else if(timeDim == 'Month') {
		return "%m/%y";
	} else if(timeDim == 'Day') {
		return "%e/%m";
	} else if(timeDim == 'Hour') {
		return "%Hh";
	} else if(timeDim == 'Minute') {
		return "%H:%M";
	}
	return "%H:%M";
}