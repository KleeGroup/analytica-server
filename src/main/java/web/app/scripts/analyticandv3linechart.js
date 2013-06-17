/*Construction de graphes à partir de la bibliothèque Nvd3 
Les fonctions prennent en paramètre un objet json data de la structure suivante:(cfXXX)
*/
(function($) {
	$.fn.drawlineChartWithNvd3 = function(data) {
		//The parseDatas function will need to clean the datas,
		//by eliminating the undefined values
		//var datas =parseDatas(data)||data;
		var datas = data;
		//définir les options par défaut ici
		var defaults = {}, options = $.extend(defaults, datas);
		//Récupérer le conteneur sur lequel la fonction est appelée
		var container = $(this);

		return this.each(function() {
			//appel de la fonction graphique de nvd3
			nv.addGraph(function() {
				//paramétrage des axes
				var chart = nv.models.lineChart().x(function(d) { 
					return d[0]
				}).y(function(d) {
					return d[1]
				}) // adjusting, 100% is 1.00, not 100 as it is in the data
				.color(d3.scale.category10().range());
				//Paramétrage des labels sur l'axe des abscisses
				chart.xAxis.rotateLabels(-45).tickFormat(function(d) {
					return d3.time.format("%H:%M")(new Date(d))
				});

				// chart.yAxis.tickFormat(d3.format(',f'));

				d3.select('#' + container[0].id + ' svg')
				.datum(datas)
				.transition()
				.duration(500)
				.call(chart);

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
				var chart = nv.models.stackedAreaChart().x(function(d) { 
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
				var chart = nv.models.multiBarChart().x(function(d) {
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
				var chart = nv.models.discreteBarChart().x(function(d) { 
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

	$.fn.drawpieChartWithNvd3 = function(datas) {
		var defaults = {}, options = $.extend(defaults, datas);
		var container = $(this);

		nv.addGraph(function() {
			var chart = nv.models.pieChart()
				.x(function(d) {
				return d.label
			})
				.y(function(d) {
				return d.value
			})
				.showLabels(true);

			d3.select('#' + container[0].id + ' svg')
				.datum(datas)
				.transition().duration(1200)
				.call(chart);

			return chart;
		});
	};
	
	$.fn.drawBigValue = function() {
		var container = $(this);
		var bigValueDiv = document.createElement("div");
		bigValueDiv.setAttribute('class', "bigValue");
		var bigValuelabel = document.createElement("div");
		bigValueDiv.setAttribute('class', "bigValuelabel");
		bigValueDiv.innerHTML = '90';
		bigValuelabel.innerHTML = 'Max';
		container.append(bigValueDiv);
		container.bigValuelabel(append);
	};

})(jQuery);