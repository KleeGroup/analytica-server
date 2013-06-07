(function($) {
	$.fn.drawlineChartWithNvd3 = function(datas) {
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

	$.fn.drawStackedAreaChartWithNvd3 = function(datas) {
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
	$.fn.drawMultiBarChartWithNvd3 = function(datas) {
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

	$.fn.drawbarChartWithNvd3 = function(datas) {
		var defaults = {}, options = $.extend(defaults, datas);
		var container = $(this);

		return this.each(function() {
			nv.addGraph(function() {
				var chart = nv.models.discreteBarChart().x(function(d) { // CumulativeLineChart++discreteBarChart++stackedAreaChart++multiBarChart
					return d[0]
				}).y(function(d) {
					return d[1]
				}) // adjusting, 100% is 1.00, not 100 as it is in the data
				.color(d3.scale.category10().range());

				chart.xAxis.rotateLabels(-45).tickFormat(function(d) {
					return d3.time.format("%H")(new Date(d))
				});

				// chart.yAxis.tickFormat(d3.format(',f'));

				d3.select('#' + container[0].id + ' svg').datum(datas)
						.transition().duration(500).call(chart);

				nv.utils.windowResize(chart.update);

				return chart;
			});

		});
	};
})(jQuery);