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
				chart.xAxis.rotateLabels(-45)
				.tickFormat(function(d) {
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
				.color(d3.scale.category10().range()).showLegend(true);

				chart.xAxis.rotateLabels(-45).tickFormat(function(d) {
					return d3.time.format("%H:%M")(new Date(d))
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
					return d3.time.format("%H:%M")(new Date(d))
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
					return d3.time.format("%H:%M")(new Date(d))
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
				.showLabels(false)
				.showLegend(true);

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


	$.fn.drawlinePlusBarWithNvd3 = function(data) {
		//The parseDatas function will need to clean the datas,
		//by eliminating the undefined values
		//var datas =parseDatas(data)||data;
		var datas = [];
		
		


		var values1t = data[0].values;
		var reconstructedData1 = [];
		for (var i = 0, responseLength = values1t.length; i < responseLength; i++) {
		var r = values1t[i];
		reconstructedData1.push({
			x:r[0],
			y:r[1]
		});
		}

		var values2t = data[1].values;
		var reconstructedData2 = [];
		for (var i = 0, responseLength = values2t.length; i < responseLength; i++) {
		var r = values2t[i];
		reconstructedData2.push({
			x:r[0],
			y:r[1]
		});
		}

		var val1 = {
			bar : true,
			key:data[0].key + " (left axis)",
			originalKey: data[0].key,
			values:reconstructedData1
		},
		val2 = {
			key:data[1].key + " (right axis)",
			originalKey: data[1].key,
			values:reconstructedData2
		};
		datas.push(val1);
		datas.push(val2);


		//définir les options par défaut ici
		var defaults = {}, options = $.extend(defaults, datas);
		//Récupérer le conteneur sur lequel la fonction est appelée
		var container = $(this);

		return this.each(function() {
			//appel de la fonction graphique de nvd3
			nv.addGraph(function() {
				//paramétrage des axes


				chart = nv.models.linePlusBarChart()
					.margin({
					top: 30,
					right: 60,
					bottom: 50,
					left: 70
				})
					.x(function(d, i) {
					return i
				})
					.color(d3.scale.category10().range());
				chart.xAxis.tickFormat(function(d) {
					var dx = datas[0].values[d] && datas[0].values[d].x || 0;
					return dx ? d3.time.format('%H:%M')(new Date(dx)) : '';
				});

				chart.y1Axis
					.tickFormat(d3.format(',f'));

				chart.y2Axis
					.tickFormat(function(d) {
					return d3.format(',.2f')(d)
				});

				chart.bars.forceY([0]);
				//chart.lines.forceY([0]);

				d3.select('#' + container[0].id + ' svg')
					.datum(datas)
					.transition().duration(500).call(chart);

				nv.utils.windowResize(chart.update);

				chart.dispatch.on('stateChange', function(e) {
					nv.log('New State:', JSON.stringify(e));
				});

				return chart;



			});

		});
	};
	
	$.fn.drawSparklineWithNvd3 = function(data){
		var datas = [];
		var defaults = {}, options = $.extend(defaults, datas);
		var container = $(this);
		nv.addGraph(function() {
			var chart = nv.models.sparklinePlus()
			.width(70)
            .height(30)

			chart
				.margin({
				top: 5, right: 0, bottom: 10, left: 0
			})
				.x(function(d, i) {
				return i
			})
				.xTickFormat(function(d) {
				return d.x
			})

			d3.select(container.selector)
				.datum(data)
				.transition().duration(250)
				.call(chart);


			return chart;
		});
	};
})(jQuery);