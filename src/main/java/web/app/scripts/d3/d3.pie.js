var drawPieChart = function(datas,id) {
	var data = datas.data;
	var unit = datas.unit;
	//-----------------------------------------------------
	var defaultColors = d3.scale.category20c();
	var width = 600;
	var height = 500;
	var radius = 200;
	var innerradius = 100;
	var textOffset = 24;
	var tweenDuration = 1050;

	var lines, valueLabels, nameLabels;
	var pieData = [];
	var filteredPieData = [];

	var total = 0;
	//D3 helper function to populate pie slice parameters from array data
	var donut = d3.layout.pie().value(function(d) {
		total += d.itemValue;
		return d.itemValue;
	}).sort(null);

	//D3 helper function to create colors from an ordinal scale
	var color = defaultColors;

	//D3 helper function to draw arcs, populates parameter "d" in path object
	var arc = d3.svg.arc()
		.startAngle(function(d) {
		return d.startAngle;
	})
		.endAngle(function(d) {
		return d.endAngle;
	})
		.innerRadius(innerradius)
		.outerRadius(radius);

	///////////////////////////////////////////////////////////
	// CREATE VIS & GROUPS ////////////////////////////////////
	///////////////////////////////////////////////////////////

	var vis = d3.select("#" + id).append("svg:svg")
		.attr("width", width)
		.attr("height", height);

	//GROUP FOR ARCS/PATHS
	var arc_group = vis.append("svg:g")
		.attr("class", "arc")
		.attr("transform", "translate(" + (width / 2) + "," + (height / 2) + ")");

	//GROUP FOR LABELS
	var label_group = vis.append("svg:g")
		.attr("class", "label_group")
		.attr("transform", "translate(" + (width / 2) + "," + (height / 2) + ")");

	//GROUP FOR CENTER TEXT  
	var center_group = vis.append("svg:g")
		.attr("class", "center_group")
		.attr("transform", "translate(" + (width / 2) + "," + (height / 2) + ")");

	//PLACEHOLDER GRAY CIRCLE
	// var paths = arc_group.append("svg:circle")
	//     .attr("fill", "#EFEFEF")
	//     .attr("r", r);

	///////////////////////////////////////////////////////////
	// CENTER TEXT ////////////////////////////////////////////
	///////////////////////////////////////////////////////////

	//WHITE CIRCLE BEHIND LABELS
	var whiteCircle = center_group.append("svg:circle")
		.attr("fill", "white")
		.attr("r", innerradius);

	// "TOTAL" LABEL
	var totalLabel = center_group.append("svg:text")
		.attr("class", "label")
		.attr("dy", -15)
		.attr("text-anchor", "middle") // text-align: right
	.text("TOTAL");

	//TOTAL TRAFFIC VALUE
	var totalValue = center_group.append("svg:text")
		.attr("class", "total")
		.attr("dy", 7)
		.attr("text-anchor", "middle") // text-align: right
	.text(".......");

	//UNITS LABEL
	var totalUnits = center_group.append("svg:text")
		.attr("class", "units")
		.attr("dy", 21)
		.attr("text-anchor", "middle") // text-align: right
	.text(unit);

	///////////////////////////////////////////////////////////
	// STREAKER CONNECTION ////////////////////////////////////
	///////////////////////////////////////////////////////////

	// to draw the chart

	function draw() {

		pieData = donut(data);

		var sliceProportion = 0; //size of this slice
		filteredPieData = pieData.filter(filterData);
		var totalOctets = total;

		function filterData(element, index, array) {
			element.name = data[index].itemLabel;
			element.value = data[index].itemValue;
			element.color = data[index].itemColor;
			sliceProportion += element.value;
			return (element.value > 0);
		}


		//REMOVE PLACEHOLDER CIRCLE

		totalValue.text(function() {
			return totalOctets; // à recalculer
		});


		//DRAW ARC PATHS
		paths = arc_group.selectAll("path").data(filteredPieData);
		paths.enter().append("svg:path")
			.attr("stroke", "white")
			.attr("stroke-width", 0.5)
			.attr("fill", function(d, i) {
			console.log(d);
			if (d.color === undefined) {
				return color(i);
			}
			return d.color;
		})
			.transition()
			.duration(tweenDuration)
			.attrTween("d", pieTween);


		//DRAW TICK MARK LINES FOR LABELS
		lines = label_group.selectAll("line").data(filteredPieData);
		lines.enter().append("svg:line")
			.attr("x1", 0)
			.attr("x2", 0)
			.attr("y1", -radius - 3)
			.attr("y2", -radius - 8)
			.attr("stroke", "gray")
			.attr("transform", function(d) {
			return "rotate(" + (d.startAngle + d.endAngle) / 2 * (180 / Math.PI) + ")";
		});
		lines.transition()
			.duration(tweenDuration)
			.attr("transform", function(d) {
			return "rotate(" + (d.startAngle + d.endAngle) / 2 * (180 / Math.PI) + ")";
		});
		lines.exit().remove();

		//DRAW LABELS WITH PERCENTAGE VALUES
		valueLabels = label_group.selectAll("text.value").data(filteredPieData)
			.attr("dy", function(d) {
			if ((d.startAngle + d.endAngle) / 2 > Math.PI / 2 && (d.startAngle + d.endAngle) / 2 < Math.PI * 1.5) {
				return 5;
			} else {
				return -7;
			}
		})
			.attr("text-anchor", function(d) {
			if ((d.startAngle + d.endAngle) / 2 < Math.PI) {
				return "beginning";
			} else {
				return "end";
			}
		})
			.text(function(d) {
			var percentage = (d.value / totalOctets) * 100;
			return percentage.toFixed(1) + "%";
		});

		valueLabels.enter().append("svg:text")
			.attr("class", "value")
			.attr("transform", function(d) {
			return "translate(" + Math.cos(((d.startAngle + d.endAngle - Math.PI) / 2)) * (radius + textOffset) + "," + Math.sin((d.startAngle + d.endAngle - Math.PI) / 2) * (radius + textOffset) + ")";
		})
			.attr("dy", function(d) {
			if ((d.startAngle + d.endAngle) / 2 > Math.PI / 2 && (d.startAngle + d.endAngle) / 2 < Math.PI * 1.5) {
				return 5;
			} else {
				return -7;
			}
		})
			.attr("text-anchor", function(d) {
			if ((d.startAngle + d.endAngle) / 2 < Math.PI) {
				return "beginning";
			} else {
				return "end";
			}
		}).text(function(d) {
			var percentage = (d.value / totalOctets) * 100;
			return percentage.toFixed(1) + "%";
		});

		valueLabels.transition().duration(tweenDuration).attrTween("transform", textTween);

		valueLabels.exit().remove();


		//DRAW LABELS WITH ENTITY NAMES
		nameLabels = label_group.selectAll("text.units").data(filteredPieData)
			.attr("dy", function(d) {
			if ((d.startAngle + d.endAngle) / 2 > Math.PI / 2 && (d.startAngle + d.endAngle) / 2 < Math.PI * 1.5) {
				return 17;
			} else {
				return 5;
			}
		})
			.attr("text-anchor", function(d) {
			if ((d.startAngle + d.endAngle) / 2 < Math.PI) {
				return "beginning";
			} else {
				return "end";
			}
		}).text(function(d) {
			return d.name;
		});

		nameLabels.enter().append("svg:text")
			.attr("class", "units")
			.attr("transform", function(d) {
			return "translate(" + Math.cos(((d.startAngle + d.endAngle - Math.PI) / 2)) * (radius + textOffset) + "," + Math.sin((d.startAngle + d.endAngle - Math.PI) / 2) * (radius + textOffset) + ")";
		})
			.attr("dy", function(d) {
			if ((d.startAngle + d.endAngle) / 2 > Math.PI / 2 && (d.startAngle + d.endAngle) / 2 < Math.PI * 1.5) {
				return 17;
			} else {
				return 5;
			}
		})
			.attr("text-anchor", function(d) {
			if ((d.startAngle + d.endAngle) / 2 < Math.PI) {
				return "beginning";
			} else {
				return "end";
			}
		}).text(function(d) {
			return d.name;
		});

		nameLabels.transition().duration(tweenDuration).attrTween("transform", textTween);

		nameLabels.exit().remove();

	}

	///////////////////////////////////////////////////////////
	// FUNCTIONS //////////////////////////////////////////////
	///////////////////////////////////////////////////////////

	// Interpolate the arcs in data space.

	function pieTween(d, i) {
		var s0 = 0;
		var e0 = 0;
		var i = d3.interpolate({
			startAngle: s0,
			endAngle: e0
		}, {
			startAngle: d.startAngle,
			endAngle: d.endAngle
		});
		return function(t) {
			var b = i(t);
			return arc(b);
		};
	}

	function textTween(d, i) {
		var a = 0;

		var b = (d.startAngle + d.endAngle - Math.PI) / 2;

		var fn = d3.interpolateNumber(a, b);
		return function(t) {
			var val = fn(t);
			return "translate(" + Math.cos(val) * (radius + textOffset) + "," + Math.sin(val) * (radius + textOffset) + ")";
		};
	}



	draw();
}