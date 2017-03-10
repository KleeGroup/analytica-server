function showObjective(elem, datas, dataMetrics, dataQuery, dataLabels, dataIcons, dataColors) {
	colors = analyticaTools.getColors(dataColors, datas.length * dataMetrics.length/2 );
	var colorIndex = 0;
	for(var i = 0; i < datas.length; i++) {
		for(var j = 0; j < dataMetrics.length; j+=2) { //Les métrics vont 2 par deux
		var divGauge = $('<div/>', {class:'gauge'}).appendTo(elem);
		var guid = analyticaTools.guid();
		var processingPlaceholder = $('<canvas/>', {id:guid})
		.appendTo(divGauge);
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
		
		function getSetValueFunction(current, objective, d3color, divGauge) { return function(processing) {
			var nbGauge = datas.length * (dataMetrics.length/2); //Les métrics vont 2 par deux
			var maxChartSize = 135;
			var nbRow = 1;
			var nbColumn = nbGauge;
			for(var testedNbRow = 1; testedNbRow < nbGauge; testedNbRow++) {
				var testedNbColumn = Math.ceil(nbGauge / testedNbRow);
				var newChartSize = Math.min(Math.min(elem.width() / testedNbColumn, elem.height() / testedNbRow-(5*testedNbRow-1)));
				if(newChartSize < maxChartSize) {
					break;
				}
				maxChartSize = newChartSize;
				nbRow = testedNbRow;
				nbColumn = testedNbColumn;
			}
			processing.size(maxChartSize, maxChartSize);
			processing.initColor(d3color.r, d3color.g, d3color.b);
			processing.setValue(current, objective);
			
			divGauge.removeClass();
			divGauge.addClass('gauge');
			var showedNbColumn = Math.floor(elem.width() / maxChartSize);
			divGauge.addClass('col-'+showedNbColumn);
			
		}};
		analyticaTools.doWhenProcessingReady(guid, getSetValueFunction(current, objective, d3.rgb(colors[colorIndex]), divGauge));
		
		var divNumber = $('<div/>', {class:'number', style:'color:'+colors[colorIndex]}).appendTo(divGauge);
		if(icon) {
			divNumber.append($('<i/>', {class:icon}));
		}
		divNumber.append('<br/>'+Math.round(percent)+'&nbsp;% ');
		
		var divTitle = $('<div/>', {class:'title'}).appendTo(divGauge);
		divTitle.append(label);
		analyticaTools.toggleVisibilityOnMouseOver(divGauge, divTitle);
		
		colorIndex++;
		
		}
	}	
}
