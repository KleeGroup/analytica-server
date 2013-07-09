/*Construction de graphes à partir de la bibliothèque Nvd3 
Les fonctions prennent en paramètre un objet json data de la structure suivante:(cfXXX)
*/
(function($) {
  $.fn.drawlineChartWithNvd3 = function(data, id) {
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


  $.fn.drawStackedAreaChartWithNvd3 = function(datas, id) {
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
  $.fn.drawMultiBarChartWithNvd3 = function(datas, id) {
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

  $.fn.drawbarChartWithNvd3 = function(datas, id) {
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

  $.fn.drawpieChartWithNvd3 = function(datas, id) {
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
        .showLegend(false);

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


  $.fn.drawlinePlusBarWithNvd3 = function(datas, id) {
    datas[0].unit = "ms"; // must be removed here

   /* var data1 = [
      [3625200000, 1214],
      [3628800000, 285],
      [3632400000, 455],
      [3636000000, 125],
      [3639600000, 1850],
      [3643200000, 789],
      [3646800000, 458],
      [3650400000, 999],
      [3654000000, 775],
      [3657600000, 328],
      [3661200000, 389],
      [3664800000, 552],
      [3668400000, 566],
      [3672000000, 398],
      [3675600000, 455],
      [3679200000, 566],
      [3682800000, 635],
      [3686400000, 985],
      [3690000000, 777],
      [3693600000, 1000],
      [3697200000, 989],
      [3700800000, 1245],
      [3704400000, 522],
      [3708000000, 300]
    ];

    var data2 = [
      [3625200000, 145],
      [3628800000, 285],
      [3632400000, 455],
      [3636000000, 15],
      [3639600000, 510],
      [3643200000, 89],
      [3646800000, 58],
      [3650400000, 99],
      [3654000000, 75],
      [3657600000, 28],
      [3661200000, 39],
      [3664800000, 52],
      [3668400000, 566],
      [3672000000, 398],
      [3675600000, 455],
      [3679200000, 566],
      [3682800000, 35],
      [3686400000, 85],
      [3690000000, 77],
      [3693600000, 100],
      [3697200000, 89],
      [3700800000, 125],
      [3704400000, 22],
      [3708000000, 30]
    ];
*/

    /*var datas = [{
        unit: "ms",
        key: "response Time",
        values: data1
      }, {
        key: "Hits",
        values: data2
      }
    ]*/




    var margin = {
      top: 20,
      right: 50,
      bottom: 50,
      left: 50
    }, width = null // width - margin.left - margin.right
      ,
      height = null // height -margin.top - margin.bottom
      ,
      color = null,
      x, y1, y2, barWidth = 50,
      space = 10;

    var container = document.getElementById(id);
    container = $(this);
    var xAxis,
      yRightAxis,
      yLeftAxis,
      line,
      bars,
      legend;

    var defaultColors = ["#33B5E5", "#FF4444"];
    //-------------------------------------------------------
    if (datas[0].color === undefined) {
      datas[0].color = defaultColors[0];
    }
    if (datas[1].color === undefined) {
      datas[1].color = defaultColors[1];
    }
    var defaultHeight = 300; // taille par défaut 
    //width = 600,height = 300;
    height = container.height();
    if ((height === undefined) || (height === 0)) {
      height = defaultHeight;
    }
    var axisSpacing = 3; // spacing between ticks
    var param = 0;
    width = container.width();
    if (width < 300) {
      axisSpacing = 6;
      param = 7 / 10;
      space = 5;
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
      space = 5;
      height = 155;
    } else {
      param = 9 / 10;
    }
    width = param * width;


    barWidth = Math.min(width / (datas[0].values).length - space,50);

    x = d3.time.scale().range([0, width]);
    yL = d3.scale.linear().range([height, 0]);
    yR = d3.scale.linear().range([height, 0]);

    /*var minValue =  // find min and max value of the two ranges and use them for the x domain below
                  ,maxValeu =*/

    x.domain([new Date(d3.min(datas[1].values, function(d) {
        return d[0];
      }) - 3600000), d3.time.day.offset(new Date(d3.max(datas[1].values, function(d) {
        return d[0];
      }) + 3600000), 0)]);

    yL.domain([0, d3.max(datas[0].values, function(d) {
        return d[1];
      })])
    yR.domain([0, d3.max(datas[1].values, function(d) {
        return d[1];
      })])

    xAxis = d3.svg.axis().scale(x).orient("bottom").tickFormat(d3.time.format.utc("%d/%m-%H:%M")).ticks(d3.time.hours, axisSpacing);
    yRightAxis = d3.svg.axis().scale(yR).orient("right").ticks(5);
    yLeftAxis = d3.svg.axis().scale(yL).orient("left").ticks(5);

    line = d3.svg.line().x(function(d) {
      return x(d[0]);
    }).y(function(d) {
      return yR(d[1])
    });

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


    var chart = d3.select('#' + id)
      .append("svg")
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


    chart.append("g")
      .attr("class", "y axis")
      .attr("transform", "translate(" + width + " ,0)")
      .call(yRightAxis)
      .append("text")
      .attr("transform", "rotate(-90)")
      .attr("y", 2)
      .attr("dy", ".71em")
      .style("text-anchor", "end")
      .text(getTextY(datas[1]));

    var getToolTipText = function(d, datas) {
      var text = "";
      if (datas.unit === undefined) {
        text = d[1] + " " + datas.key + " Le " + parseToDate(d);
      } else {
        text = d[1] + " " + datas.unit + " Le " + parseToDate(d);
      }
      return text;
    }

    bars = chart.selectAll("rect")
      .data(datas[0].values)
      .enter().append("rect")
      .attr("height", function(d) {
      return height - yL(d[1]);
    })
      .attr("y", function(d) {
      return yL(d[1])
    })
      .attr("x", function(d, i) {
      return x((new Date(d[0]))) - barWidth / 2;
    })
      .attr("width", barWidth)
      .style("fill", datas[0].color)
      .style("stroke", "white")
      .style("stroke-width", 1)
      .on("mouseover", function(d) {
      toolTip.transition().duration(500).style("opacity", 1)
    })
      .on("mousemove", function(d) {
      toolTip.text(d[1] + " at " + parseToDate(d)).style("left", (d3.event.pageX - 34) + "px").style("top", (d3.event.pageY - 50) + "px");
    })
      .on("mouseout", function(d) {
      toolTip.transition().duration(500).style("opacity", 1e-6);
    });;

    chart.append("svg:path")
      .datum(datas[1].values)
      .attr("d", line)
      .attr("class", "line")
      .style("stroke", datas[1].color);

    var c = d3.scale.linear().domain([0, 1]).range(["hsl(250, 50%, 50%)", "hsl(350, 100%, 50%)"]).interpolate(d3.interpolateHsl);
    chart.selectAll("circle")
      .data(datas[1].values)
      .enter().append("svg:circle")
      .attr("cx", function(d) {
      return x(d[0])
    })
      .attr("cy", function(d) {
      return yR(d[1])
    })
      .attr("stroke-width", "none")
      .attr("fill", function() {
      return c(Math.random())
    })
      .attr("fill-opacity", .5)
      .attr("r", 3)
      .style("display", "inline")
      .on("mouseover", function(d) {
      toolTip.transition().duration(500).style("opacity", 1);
      d3.select(this).transition().duration(200).attr("fill-opacity", .5)
    })
      .on("mousemove", function(d) {
      toolTip.text(getToolTipText(d, datas[1])).style("left", (d3.event.pageX - 34) + "px").style("top", (d3.event.pageY - 70) + "px");
    })
      .on("mouseout", function() {
      toolTip.transition().duration(500).style("opacity", 1e-6);
      d3.select(this).transition().duration(200)
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
      .attr("x", function(d, i) {
      return i * 150 + 25;
    })
      .attr("width", legendHeight / 3)
      .attr("height", legendHeight / 3)
      .style("fill", function(d, i) {
      return datas[datas.indexOf(d)].color;
    });

    legend.selectAll('legend')
      .data(datas)
      .enter().append("text")
      .attr("y", height + margin.bottom)
      .attr("x", function(d, i) {
      return i * 150 + 25 + 20;
    })
      .text(function(d) {
      return datas[datas.indexOf(d)].key;
    });
  };

  $.fn.drawSparklineWithNvd3 = function(data, id) {

    var datas = [];
    var defaults = {}, options = $.extend(defaults, datas);
    var container = $(this);
    nv.addGraph(function() {
      var chart = nv.models.sparklinePlus()
        .width(70)
        .height(30)

      chart
        .margin({
        top: 5,
        right: 0,
        bottom: 10,
        left: 0
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

  $.fn.d3Sparkline = function(datas, id) {
    var data = datas;
    var data = ["1", "2", "3", "4", "5", "10", "9", "8", "7", "6"];
    if (typeof data === 'String') {
      data = data.split(",");
    }

    return this.each(function() {
      var space = 1; // space between the bars
      var barWidth = 3;
      var height = 10;
      container = $(this);
      //----------------------------------------
      var width = (barWidth + space) * data.length;
      var y = d3.scale.linear()
        .domain([0, d3.max(data)])
        .range([0, height]);
      if (id === undefined) {
        id = container[0].id;
      }

      var rectangle = d3.selectAll(("." + id)).append("svg")
        .attr("width", width)
        .attr("height", height)
        .attr("class", "bar");

      var rectangle = rectangle.selectAll("rect")
        .data(data)
        .enter().append("rect")
        .attr("class", "bar")
        .attr("width", barWidth)
        .attr("height", y)
        .attr("y", function(d) {
        return height - y(d);
      })
        .attr("x", function(d, i) {
        return i * (barWidth + space);
      })
        .style("fill", function(d, i) {
        return i == (data.length - 1) ? "#C00" : "#09C";
      })
        .append("title").text(function(d) {
        return d;
      });
    });
  };

})(jQuery);