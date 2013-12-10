function showObjective(elem, datas, dataMetrics, dataQuery, dataLabels, dataIcons, dataColors) {
	colors = analyticaTools.getColors(dataColors, datas.length * dataMetrics.length/2 );
	var colorIndex = 0;
	for(var i = 0; i < datas.length; i++) {
		for(var j = 0; j < dataMetrics.length; j+=2) { //Les métrics vont 2 par deux
		var metricDiv = $('<div/>', {class:'gauge'}).appendTo(elem);
		var gaugeDiv = $('<div/>').appendTo(metricDiv);
		var guid = analyticaTools.guid();
		var processingPlaceholder = $('<canvas/>', {id:guid})
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
		
		Processing.loadSketchFromSources(guid, ['static/pde/circleGauge.pde']);
		
		//Processing.reload();
		function getSetValueFunction(current, objective, d3color) { return function(processing) {
			var surfaceElement = elem.width() * elem.height() / datas.length;
			var maxChartSize = Math.max(120, Math.min( Math.min(elem.width()-5, elem.height()-5), Math.sqrt(surfaceElement)));
			console.log(elem.width()+" * "+elem.height()+" nbData:"+datas.length+" = surfaceElement :"+surfaceElement +" sqrt="+Math.sqrt(surfaceElement)+")")
			console.log("maxChartSize :"+maxChartSize);
			var gap = Math.max(elem.width() % 120, elem.height() % (120));
			var widthPerChart = Math.ceil(elem.width() / datas.length);
			var nbRow = Math.ceil(elem.height() / (widthPerChart+20));
			if(nbRow > 2) {
				widthPerChart = Math.ceil(elem.width() / (datas.length / nbRow));
				var newNbRow = Math.ceil(elem.height() / (widthPerChart+20));
				if(newNbRow < nbRow) {
					nbRow--;
					widthPerChart = Math.ceil(elem.width() / (datas.length / nbRow));
					newNbRow = Math.ceil(elem.height() / (widthPerChart+20));
				}
				nbRow = newNbRow;
			}
			var nbChartByRow = Math.ceil(elem.width() / widthPerChart);
			//var chartSize = Math.max(120, Math.min((elem.width() - 10*nbChartByRow) / nbChartByRow, (elem.height()) / nbRow));
			processing.size(maxChartSize, maxChartSize);
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
