

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
		var width = $(this).width();
		var height = $(this).height();
		
		var container = d3.select("#"+$(this).attr("id")).append("svg")
		.attr("width", width)
	    .attr("height", height)
	    .append("g");
		
		var x = d3.time.scale()
		.domain([d3.min(datas, function(d) { return new Date(d.x);}), d3.max(datas, function(d) { return new Date(d.x);})])
		.range([0, width]);
		
		var y = d3.scale.linear()
		.domain([0, d3.max(datas, function(d) { return d.y;})])
		.range([height, 0]);
			
		container.selectAll(".bar")
	      .data(datas)
	    .enter().append("rect")
	      .attr("class", "bar")
	      .attr("x", function(d) { return x(new Date(d.x)); })
	      .attr("width", function(d) { return 10; })
	      .attr("y", function(d) { return y(d.y); })
	      .attr("height", function(d) { return height-y(d.y); });
	};
})(jQuery);