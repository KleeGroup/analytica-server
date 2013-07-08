(function($){
    $.fn.linePlusBarChart = function(){
         //Public Variables with default settings
              var margin = {top: 20, right: 50, bottom: 100, left: 50}
                  , width = null // width - margin.left - margin.right
                  , height = null // height -margin.top - margin.bottom
                  , color = null
                  , x
                  , y1
                  , y2
                  ,barWidth=20,
                  space = 5;

                var xAxis,
                  yRightAxis,
                  yLeftAxis,
                  line,
                  bars,
                  legend;

                var defaultColors = ["#33B5E5","#FF4444"];
                  //-------------------------------------------------------

                  if(datas[0].color ===undefined){
                    datas[0].color = defaultColors[0];
                  }
                  if(datas[1].color ===undefined){
                    datas[1].color = defaultColors[1];
                  }

                  width = 600,height = 300;
                  barWidth = width/(datas[0].values).length - space;

                  x = d3.time.scale().range([0, width]);
                  yL = d3.scale.linear().range([height, 0]);
                  yR = d3.scale.linear().range([height, 0]);

                  /*var minValue =  // find min and max value of the two ranges and use them for the x domain below
                  ,maxValeu =*/

                  x.domain([new Date(d3.min(datas[1].values, function(d) {
                      return d[0];
                    })-3600000),new Date(d3.max(datas[1].values, function(d) {
                      return d[0];
                    })+3600000)]);

                  yL.domain([0, d3.max(datas[0].values, function(d) {
                      return d[1];
                    })])
                  yR.domain([0, d3.max(datas[1].values, function(d) {
                      return d[1];
                    })])

                  xAxis = d3.svg.axis().scale(x).orient("bottom").ticks(5);
                  yRightAxis = d3.svg.axis().scale(yR).orient("right").ticks(5);
                  yLeftAxis = d3.svg.axis().scale(yL).orient("left").ticks(5);

                  line = d3.svg.line().x(function(d){return x(d[0]);}).y(function(d){return yR(d[1])}).interpolate("basis");

                  var div = d3.select("#"+id).append("div")
                    .attr("class", "tooltip")
                    .style("opacity", 1e-6);

                  var parseToDate = function(d){
                        var format = d3.time.format.utc("%d/%m-%H:%M");
                        
                        return format(new Date(d[0]));
                  };

                   var parseToDate = function(d){
                        var format = d3.time.format.utc("%d/%m-%H:%M");
                        
                        return format(new Date(d[0]));
                  };
                  

                  
                  var chart = d3.select('#'+id)
                    .append("svg")
                    .attr("width", width + margin.left + margin.right)
                    .attr("height", height + margin.top + margin.bottom)
                    .append("g")
                    .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

                    chart.append("path")      // Add the valueline path.
                      .attr("d", line(datas));

                    // Draw X-axis grid lines

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
                     






                    chart.append("g")       // Add the X Axis
                        .attr("class", "x axis")
                        .attr("transform", "translate(0," + height + ")")
                        .call(xAxis);

                    chart.append("g")         // Add the Y Axis
                        .attr("class", "y axis")
                        .call(yLeftAxis)
                        .append("text")
                        .attr("transform", "rotate(-90)")
                        .attr("y", 2)
                        .attr("dy", ".71em")
                        .style("text-anchor", "end")
                        .text(datas[0].key);

                    chart.append("g")             
                        .attr("class", "y axis")    
                        .attr("transform", "translate(" + width + " ,0)")   
                        .call(yRightAxis)
                        .append("text")
                        .attr("transform", "rotate(-90)")
                        .attr("y", 2)
                        .attr("dy", ".71em")
                        .style("text-anchor", "end")
                        .text(datas[1].key);;

                    bars = chart.selectAll("rect")
                      .data(datas[0].values)
                      .enter().append("rect")
                      .attr("height", function(d) {return height-yL(d[1]);})
                      .attr("y", function(d){return yL(d[1])})
                      .attr("x", function(d, i) {return x((new Date(d[0])))-15;})
                      .attr("width",barWidth)
                      .style("fill",datas[0].color)
                      .style("stroke","white")
                      .style("stroke-width",1)
                      .on("mouseover", function(d){div.transition().duration(500).style("opacity", 1)})
                      .on("mousemove", function(d){div.text(d[1] +" at "+parseToDate(d)).style("left", (d3.event.pageX - 34) + "px").style("top", (d3.event.pageY - 50) + "px");})
                      .on("mouseout", function(d){div.transition().duration(500).style("opacity", 1e-6);}); ;

                    chart.append("svg:path")
                      .datum(datas[1].values)
                      .attr("d", line)
                      .attr("class","line")
                      .style("stroke",datas[1].color);             

                    var c = d3.scale.linear().domain([0,1]).range(["hsl(250, 50%, 50%)", "hsl(350, 100%, 50%)"]).interpolate(d3.interpolateHsl);
                    chart.selectAll("circle")
                        .data(datas[1].values)
                        .enter().append("svg:circle")
                        .attr("cx", function(d) { return x(d[0]) })
                        .attr("cy", function(d) { return yR(d[1]) })
                        .attr("stroke-width", "none")
                        .attr("fill", function() { return c(Math.random()) })
                        .attr("fill-opacity", 0)
                        .attr("r", 5)
                        .style("display","inline")
                        .on("mouseover", function(d) {
                            div.transition().duration(500).style("opacity", 1);
                            d3.select(this).transition().duration(200).attr("fill-opacity", .5)
                        })
                        .on("mousemove", function(d){div.text(d[1] +" at "+parseToDate(d)).style("left", (d3.event.pageX - 34) + "px").style("top", (d3.event.pageY - 70) + "px");})
                        .on("mouseout",function(){
                            div.transition().duration(500).style("opacity", 1e-6);
                            d3.select(this).transition().duration(200)
                            .attr("fill-opacity", 0)
                        });


                var legend = chart.append("g")
                .attr("class", "legend")
                .attr("transform", "translate(" + (width/2 + 100) + " ,"+ (-margin.top)+")");
                var color_hash = {  
                0 : ["Response Time", "#09C"],
                1 : ["Hits", "black"]
             };  var val = height+margin.bottom/2;
                legend.append("rect")
                    .attr("height", 40)
                    .attr("width", 180)
                    .attr("rx", 6)
                    .attr("ry", 6)
                    .attr("transform", "translate(20,"+val+")")
                    .style("fill", "#EEE");

                legend.selectAll('legend')
                    .data(datas)
                    .enter().append("rect")
                        .attr("y", height +margin.bottom/2 + margin.top)
                        .attr("x", function(d, i){return i * 100+25;})
                        .attr("width", 10)
                        .attr("height", 10)
                        .style("fill", function(d, i) {return datas[datas.indexOf(d)].color;});

                legend.selectAll('legend')
                    .data(datas)
                    .enter().append("text")
                        .attr("y", height +margin.bottom/2 + margin.top+8)
                        .attr("x", function(d, i){return i *100 + 25+20;})
                        .text(function(d) {return datas[datas.indexOf(d)].key;});

    }
})(jQuery)