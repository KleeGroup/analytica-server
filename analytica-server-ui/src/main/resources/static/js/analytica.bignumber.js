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
		var tendanceArrow = lastValue[1] > firstValue[1]*1.05 ? 'fa fa-arrow-up' : 
							lastValue[1] < firstValue[1]*0.95 ? 'fa fa-arrow-down' : 
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
