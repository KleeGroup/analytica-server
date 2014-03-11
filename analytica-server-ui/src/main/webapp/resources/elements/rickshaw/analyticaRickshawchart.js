/**
 * 
 * 
 * Construire un grahique avec legende et slider à partir d'un tableau d'objets
 * Json sous sous le format suivant: var param = { dataSeries: data, type:
 * "line" };
 * 
 * Revoir les paramètres d'entrée, pour indiquer le type de associé à chaque
 * jeu de donnée, le type sera un tableau de String de même taille que le
 * tableau dataSeries
 * 
 * Indiquer également les bibliothèques avec les quelles utiliser le plugin
 * 
 * 
 * 
 * 
 */
(function($) {
	$.fn.drawRickshawChart = function(parameters) {
		var defaults = {
			'type' : "line",
			'dataSerie' : []
		}, options = $.extend(defaults, parameters), types = options.type
		labels = parameters.labels;
		return this.each(function() {

			var datas = options.dataSeries;
			var container = $(this), serie = buildRickShawSerie(datas, labels);
			var cont = "#" + container[0].id;
			var balisegraph = document.createElement("div");
			balisegraph.setAttribute("id", "g" + container[0].id);
			container[0].appendChild(balisegraph);

			var graph = new Rickshaw.Graph({
				element : document.querySelector("#g" + container[0].id),
				renderer : types,
				series : serie
			});
			var x_axis = new Rickshaw.Graph.Axis.Time({
				graph : graph
			});
			var x_axis = new Rickshaw.Graph.Axis.Time({
				graph : graph
			});

			var y_axis = new Rickshaw.Graph.Axis.Y({
				graph : graph,
				orientation : 'left',
				tickFormat : Rickshaw.Fixtures.Number.formatKMBT,
				element : document.getElementById('y_axis'),
			});
			// Construire un div pour la legende
			var baliseLedgend = document.createElement("div");
			baliseLedgend.setAttribute("id", "l" + container[0].id);
			container[0].appendChild(baliseLedgend);
			var legend = new Rickshaw.Graph.Legend({
				graph : graph,
				element : baliseLedgend,

			});

			// Construire un div pour le slider
			/*
			 * Revoir pourquoi problème d'attribut style (Debug chrome) ???
			 */

			var baliseSlider = document.createElement("div");
			baliseSlider.setAttribute("id", "s" + container[0].id);
			container[0].appendChild(baliseSlider);
			var slider = new Rickshaw.Graph.RangeSlider({
				graph : graph,
				element : $('#s' + container[0].id)
			});

			graph.render();

		});
	};

	function buildRickShawSerie() {
		var s = [];
		var labels = [];
		labels = arguments[arguments.length - 1];
		if (labels.length === (arguments.length - 1)) {
			for ( var i = 0; i < arguments.length - 1; i++) {
				arguments[i] = parseData(arguments[i]);
				s.push({
					name : labels[i],
					data : arguments[i],
					color : '#'
							+ Math.floor(Math.random() * 16777215).toString(16)
				});
			}
		} else {
			// throw new Exception("The number of Labels should same as the
			// numer of series to plot ");
		}
		return s;
	}

	function parseData(d) {
		/* Corriger les valeurs undefined dans le tableau de donnee */
		for ( var i = 0; i < d.length; i++) {
			var value = d[i];
			if (typeof value.y === 'undefined') {
				value.y = null;
				d[i] = value;
			}
		}
		return d;
	}

})(jQuery);