function drawPunchcard2(id){
var pane_left = 120
  , width =  $(id).width()
  , pane_right = width - pane_left
  , height = 400
  , margin = 10
  , i
  , j
  , tx
  , ty
  , max = 0
  , data = [
    [1, 0, 0, 0, 1, 1, 4, 5, 5, 1, 1, 1, 1, 1, 1, 2, 5, 5, 4, 1, 1, 1, 1, 0],
    [1, 0, 0, 0, 1, 1, 4, 5, 5, 1, 1, 1, 1, 1, 1, 2, 5, 5, 4, 1, 1, 1, 1, 0],
    [1, 0, 0, 0, 1, 1, 4, 5, 5, 1, 1, 1, 1, 1, 1, 2, 5, 5, 4, 1, 1, 1, 1, 0],
    [1, 0, 0, 0, 1, 1, 4, 5, 5, 1, 1, 1, 1, 1, 1, 2, 5, 5, 4, 1, 1, 1, 1, 0],
    [1, 0, 0, 0, 1, 1, 4, 5, 5, 1, 1, 1, 1, 1, 1, 2, 5, 5, 4, 1, 1, 1, 1, 0],
    [1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 0],
    [0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 0, 0]
  ];

// X-Axis.
var x = d3.scale.linear().domain([0, 23]).
  range([pane_left + margin, pane_right - 2 * margin]);

// Y-Axis.
var y = d3.scale.linear().domain([0, 6]).
  range([2 * margin, height - 10 * margin]);

// The main SVG element.
var punchcard = d3.
  select(id).
  append("svg").
	  attr("width", width - 2 * margin).
	  attr("height", height - 2 * margin).
	  append("g");


var w = (pane_right- margin)/24;
//Adding a vertical zebra
punchcard
//	.append("g")
	.selectAll(".rule")
	.data(x.ticks(24))
	.enter()
	.append("rect")
	    .style("fill", function(d) { return d%2==0 ? "#ddd" : "#eee"})
	    .attr("width", w)
	    .attr("height", height)
	    .attr("x", function(d) { return pane_left  - 2 * margin+ x(d) - w/2; })
	    .attr("y", 0);


// Hour line markers by day.
for (i in y.ticks(7)) {
  punchcard.
    append("g").
    selectAll("line").
    data([0]).
    enter().
    append("line").
	    attr("x1", margin).
	    attr("x2", width - 3 * margin).
	    attr("y1", height - 3 * margin - y(i)).
	    attr("y2", height - 3 * margin - y(i)).
	    style("stroke-width", 1).
	    style("stroke", "#888");

  punchcard.
    append("g").
    selectAll(".rule").
    data([0]).
    enter().
    append("text")
	    .attr("x", margin)
	    .attr("y", height - 3 * margin - y(i) - 5)
	    .attr("text-anchor", "left")
	    .attr("class", "day"+i)
	    .style("fill", "#888")
	    .text(["Sunday", "Saturday", "Friday", "Thursday", "Wednesday", "Tuesday", "Monday"][i]);

/*  punchcard.
    append("g").
    selectAll("line").
    data(x.ticks(24)).
    enter().
    append("line")
	    .attr("x1", function(d) { return pane_left - 2 * margin + x(d); })
	    .attr("x2", function(d) { return pane_left - 2 * margin + x(d); })
	    .attr("y1", height - 4 * margin - y(i))
	    .attr("y2", height - 3 * margin - y(i))
	    .style("stroke-width", 1)
	    .style("stroke", "#888");*/
}

// Hour text markers.
punchcard
  .selectAll(".rule")
  .data(x.ticks(24))
  .enter()
  .append("text")
  .attr("class", "rule")
  .attr("class", function(d) { return "hour"+d;})
  .attr("x", function(d) { return pane_left - 2 * margin + x(d); })
  .attr("y", height - 3 * margin)
  .attr("text-anchor", "middle")
  .style("fill", "#888")
  .text(function(d) {
    if (d === 0) {
      return "12a";
    } else if (d > 0 && d < 12) {
      return d;
    } else if (d === 12) {
      return "12p";
    } else if (d > 12 && d < 25) {
      return d - 12;
    }
  });

// Data has array where indicy 0 is Monday and 6 is Sunday, however we draw
// from the bottom up.
data = data.reverse();

// Find the max value to normalize the size of the circles.
for (i = 0; i < data.length; i++) {
  max = Math.max(max, Math.max.apply(null, data[i]));
}

// Show the circles on the punchcard.
for (i = 0; i < data.length; i++) {
	(function (i){
		for (j = 0; j < data[i].length; j++) {
			(function (j){
			 punchcard
		      .append("g")
		      .selectAll("circle")
		      .data([data[i][j]])
		      .enter()
		      .append("circle")
			      .style("fill", "#888")
			      .on("mouseover",  function() {
			          d3.select(".hour"+j)
			          	.transition()
			          	.style("fill", "#33B5E5"); //blue
			         
			          d3.select(".day"+i)
			          	.transition()
			          	.style("fill", "#33B5E5"); //blue
			          
			          d3.select(this)
			          	.transition()
			          	.style("fill", "#33B5E5"); //blue
			        })
			        
			      .on("mouseout", function() {
			          d3.select(this)
			          	.transition()
			          	.style("fill", "#888")
	
		          	d3.select(".day"+i)
			          	.transition()
			          	.style("fill", "#888");
	
		          	d3.select(".hour"+j)
		          		.transition()
		          		.style("fill", "#888");
			      })
			      .attr("r", function(d) { return d / max * 14; })
			      .attr("transform", function() {
			          tx = pane_left - 2 * margin + x(j);
			          ty = height - 3 * margin - y(i) -14 - margin;
			          return "translate(" + tx + ", " + ty + ")";
			        });
			})(j);
	  }
	})(i);
}
};