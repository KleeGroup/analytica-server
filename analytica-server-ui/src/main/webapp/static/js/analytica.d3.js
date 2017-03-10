

(function($) {
	$.fn.drawlineChart = function(datas) {
		var defaults = {}, options = $.extend(defaults, datas);
		var container = $(this);

		return this.each(function() {
			nv.addGraph(function() {
				var chart = nv.models.lineChart().x(function(d) { // CumulativeLineChart++discreteBarChart++stackedAreaChart++multiBarChart
					return d[0]
				}).y(function(d) {
					return d[1]
				}) // adjusting, 100% is 1.00, not 100 as it is in the data
				.color(d3.scale.category10().range());

				chart.xAxis.rotateLabels(-45).tickFormat(function(d) {
					return d3.time.format("%x:%H:%M")(new Date(d))
				});

				// chart.yAxis.tickFormat(d3.format(',f'));

				d3.select('#' + container[0].id + ' svg').datum(datas)
						.transition().duration(500).call(chart);

				nv.utils.windowResize(chart.update);

				return chart;
			});

		});
	};

	$.fn.drawStackedAreaChart = function(datas) {
		var defaults = {}, options = $.extend(defaults, datas);
		var container = $(this);

		return this.each(function() {
			nv.addGraph(function() {
				var chart = nv.models.stackedAreaChart().x(function(d) { // CumulativeLineChart++discreteBarChart++stackedAreaChart++multiBarChart
					return d[0]
				}).y(function(d) {
					return d[1]
				}) // adjusting, 100% is 1.00, not 100 as it is in the data
				.color(d3.scale.category10().range());

				chart.xAxis.rotateLabels(-45).tickFormat(function(d) {
					return d3.time.format("%x:%H:%M")(new Date(d))
				});

				// chart.yAxis.tickFormat(d3.format(',f'));

				d3.select('#' + container[0].id + ' svg').datum(datas)
						.transition().duration(500).call(chart);

				nv.utils.windowResize(chart.update);

				return chart;
			});

		});
	};
	$.fn.drawMultiBarChart = function(datas) {
		var defaults = {}, options = $.extend(defaults, datas);
		var container = $(this);

		return this.each(function() {
			nv.addGraph(function() {
				var chart = nv.models.multiBarChart().x(function(d) { // CumulativeLineChart++discreteBarChart++stackedAreaChart++multiBarChart
					return d[0]
				}).y(function(d) {
					return d[1]
				}) // adjusting, 100% is 1.00, not 100 as it is in the data
				.color(d3.scale.category10().range());

				chart.xAxis.rotateLabels(-45).tickFormat(function(d) {
					return d3.time.format("%x:%H:%M")(new Date(d))
				});

				// chart.yAxis.tickFormat(d3.format(',f'));

				d3.select('#' + container[0].id + ' svg').datum(datas)
						.transition().duration(500).call(chart);

				nv.utils.windowResize(chart.update);

				return chart;
			});

		});
	};

	$.fn.drawbarChart = function(datas) {
		var defaults = {}, options = $.extend(defaults, datas);	
		var margin = {top: 20, right: 20, bottom: 20, left: 30},
	    width = $(this).width() - margin.left - margin.right,
	    height = $(this).height() - margin.top - margin.bottom;
		
		var container = d3.select("#"+$(this).attr("id")).append("svg")
		.attr("width", width + margin.left + margin.right)
		.attr("height", height + margin.top + margin.bottom)
		.append("g")
		.attr("transform", "translate(" + margin.left + "," + margin.top + ")");
		
		var x = d3.time.scale()
		.domain([d3.min(datas, function(d) { return new Date(d.x+5);}), d3.max(datas, function(d) { return new Date(d.x+5);})])
		.rangeRound([0, width]);
		
		var y = d3.scale.linear()
		.domain([0, d3.max(datas, function(d) { return d.y;})])
		.range([height, 0]);
		
		var xAxis = d3.svg.axis()
	    .scale(x)
	    .orient("bottom")
	    .ticks(2)
	    .tickFormat(d3.time.format("%H"));
	
		var yAxis = d3.svg.axis()
	    .scale(y)
	    .ticks(2)
	    .orient("left");	
		
		container.append("g").selectAll(".bar")
	      .data(datas)
	    .enter().append("rect")
	      .attr("class", "bar")
	      .attr("x", function(d) { return x(new Date(d.x))-10/2; })
	      .attr("width", function(d) { return 10; })
	      .attr("y", function(d) { return y(d.y); })
	      .attr("height", function(d) { return height-y(d.y); });
		
		container.append("g")
		  .attr("class", "x axis")
	      .attr("transform", "translate(0," + height + ")")
	      .call(xAxis);
		
		container.append("g")
		  .attr("class", "y axis")
	      .call(yAxis);
		
	};
})(jQuery);