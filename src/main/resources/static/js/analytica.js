/**
 * Charge les données lors du chargement de la page.
 **/
function showCharts() {
	$('div.d3chart').each(function () {
		showChart($(this));
	});
}


function showChart(chartElement) {
	$.getJSON(chartElement.attr('data-url'), 
		      chartElement.attr('data-query')
	).done(
	  function( data ) {
		  chartElement.drawbarChart(data);
	});
}