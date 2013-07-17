var areaChart = function (datas,id){
var defaultColors = ["#33B5E5", "#FF4444"];
	var defaultHeight = 300;
	var defaultWidth = 600;
	var barWidth = 50;
	var space = 10;
	var axisSpacing = 3; // spacing between ticks
	//--------------------------
	var margin = {
		top: 20,
		right: 50,
		bottom: 50,
		left: 50
	}; 
	var width = undefined; // width - margin.left - margin.right
	var height = undefined; // height -margin.top - margin.bottom
	var color = null;
	var x, y1, y2;


    var container = document.getElementById(id);
    var xAxis,
      yLeftAxis,
      line,
      area,
      legend;

    //-------------------------------------------------------
	if (datas[0].color === undefined) {
		datas[0].color = defaultColors[0];
	}

    // taille par défaut 
	if ((height === undefined) || (height === 0)) {
		height = defaultHeight;
	}
	if ((width === undefined) || (width === 0)) {
		width = defaultWidth;
	}

	var axisSpacing = 3; // spacing between ticks
	var param = 0;
    if (width < 300) {
      axisSpacing = 6;
      param = 7 / 10;
      //space = 5;
      space = width / 60;
      height = 155;
      margin = {
        top: 20,
        right: 30,
        bottom: 50,
        left: 30
      }
    } else if (width < 600) {
      axisSpacing = 4
      param = 8 / 10;
      //space = 5;
      space = width / 60;
      height = 155;
    } else {
      param = 9 / 10;
      space = width / 60;
    }
    width = param * width;
    space = param * space;



    x = d3.time.scale().range([0, width]);
    yL = d3.scale.linear().range([height, 0]);

    /*var minValue =  // find min and max value of the two ranges and use them for the x domain below
                  ,maxValeu =*/

    x.domain([new Date(d3.min(datas[0].values, function(d) {
        return d[0];
      }) - 3600000), d3.time.day.offset(new Date(d3.max(datas[0].values, function(d) {
        return d[0];
      }) + 3600000), 0)]);

    yL.domain([0, d3.max(datas[0].values, function(d) {
        return d[1];
      })])

    xAxis = d3.svg.axis().scale(x).orient("bottom").tickFormat(d3.time.format.utc("%d/%m-%H:%M")).ticks(d3.time.hours, axisSpacing);
    yLeftAxis = d3.svg.axis().scale(yL).orient("left").ticks(5);

	var div = d3.select("#" + id).append("div")
		.attr("class", "tooltip")
		.style("opacity", 1e-6);

	var toolTip = d3.select("#" + id).append("div")
		.attr("class", "tooltip")
		.style("opacity", 1e-6);

	var parseToDate = function(d) {
		var format = d3.time.format.utc("%d/%m-%H:%M");
		return format(new Date(d[0]));
	};

	var parseToDate = function(d) {
		var format = d3.time.format.utc("%d/%m-%H:%M");
		return format(new Date(d[0]));
	};

	if (id === undefined) {
		id = container[0].id;
	}

    var getTextY = function(d) {
      if ((d === undefined) || ((d.key === undefined) && (d.unit === undefined))) {
        throw "Labels and units not defined";
      }
      var text = "";
      if (d.key === undefined) {
        text += d.unit;
      } else if (d.unit === undefined) {
        text += d.key;
      } else {
        text = d.key + " ( " + d.unit + " )";
      }

      return text;

    }


	var chart = d3.select('#' + id).append("svg")
		.attr("width", width + margin.left + margin.right)
		.attr("height", height + margin.top + margin.bottom)
		.append("g")
		.attr("transform", "translate(" + margin.left + "," + margin.top + ")"); // Draw X-axis grid lines

	chart.selectAll("line.x")
		.data(x.ticks(10))
		.enter().append("line")
			.attr("class", "x")
			.attr("x1", x)
			.attr("x2", x)
			.attr("y1", 0)
			.attr("y2", height)
			.style("stroke", "#ccc");

    // Draw Y-axis grid lines
	chart.selectAll("line.y")
		.data(yL.ticks(10))
		.enter().append("line")
			.attr("class", "y")
			.attr("x1", 0)
			.attr("x2", width)
			.attr("y1", yL)
			.attr("y2", yL)
			.style("stroke", "#ccc");

	chart.append("g") // Add the X Axis
		.attr("class", "x axis")
		.attr("transform", "translate(0," + height + ")")
		.call(xAxis);

	chart.append("g") // Add the Y Axis
		.attr("class", "y axis")
		.call(yLeftAxis)
		.append("text")
			.attr("transform", "rotate(-90)")
			.attr("y", 2)
			.attr("dy", ".71em")
			.style("text-anchor", "end")
			.text(getTextY(datas[0]));



    var getToolTipText = function(d, datas) {
      if (datas.unit === undefined) {
        return  d[1] + " " + datas.key + " Le " + parseToDate(d);
      } else {
        return  d[1] + " " + datas.unit + " Le " + parseToDate(d);
      }
    }

    line = d3.svg.line().x(function(d) {
      return x(d[0]);
    }).y(function(d) {
      return yL(d[1])
    });
	var area = d3.svg.area()
		.x(function(d) {return x(d[0]);})
		.y0(height)
		.y1(function(d) {return yL(d[1]);});

	chart.append("path")
		.datum(datas[0].values)
		.attr("d", area)
		.attr("class", "area")
		.style("stroke", datas[0].color);
	  
    var c = d3.scale.linear()
		.domain([0, 1])
		.range(["hsl(250, 50%, 50%)", "hsl(350, 100%, 50%)"])
		.interpolate(d3.interpolateHsl);

	chart.selectAll("circle")
		.data(datas[0].values)
		.enter().append("circle")
			.attr("cx", function(d) { return x(d[0]) })
			.attr("cy", function(d) { return yL(d[1])})
			.attr("stroke-width", "none")
			.attr("fill", function() { return c(Math.random())})
			.attr("fill-opacity", .5)
			.attr("r", 3)
			.style("display", "inline")
			.on("mouseover", function(d) {
				div.transition().duration(200).style("opacity", 1);
				d3.select(this).transition()
					.duration(200)
					.attr("fill-opacity", .5)
			})
			.on("mousemove", function(d) {
				div.text(getToolTipText(d, datas[0]))
					.style("left", (d3.event.pageX - 34) + "px")
					.style("top", (d3.event.pageY - 70) + "px");
			})
			.on("mouseout", function() {
				div.transition().duration(200).style("opacity", 1e-6);
				d3.select(this).transition()
					.duration(200)
					.attr("fill-opacity", .5)
			});

    var legend = chart.append("g")
      .attr("class", "legend")
      .attr("transform", "translate(" + 0 + " ," + (-margin.top) + ")");
    var color_hash = {
      0: ["Response Time", "#09C"],
      1: ["Hits", "black"]
    };
    var val = height + 3 * margin.bottom / 4;
    var legendHeight = margin.bottom / 2;
    var legendWidth = width / 2;
	
	legend.append("rect")
		.attr("height", legendHeight)
		.attr("width", legendWidth)
		.attr("rx", 5)
		.attr("ry", 5)
		.attr("transform", "translate(0," + val + ")")
		.style("fill", "#EEE");

	legend.selectAll('legend')
		.data(datas)
		.enter().append("rect")
			.attr("y", height + margin.bottom / 2 + margin.top)
			.attr("x", function(d, i) { return i * 150 + 25; })
			.attr("width", legendHeight / 3)
			.attr("height", legendHeight / 3)
			.style("fill", function(d, i) { return datas[datas.indexOf(d)].color; });

	legend.selectAll('legend')
		.data(datas)
		.enter().append("text")
			.attr("y", height + margin.bottom)
			.attr("x", function(d, i) { return i * 150 + 25 + 20;})
			.text(function(d) { return datas[datas.indexOf(d)].key; });
}