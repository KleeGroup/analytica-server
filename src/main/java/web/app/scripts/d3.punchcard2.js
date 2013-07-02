function drawPunchcard2(id, data, days){
/* var data = [
    [1, 0, 0, 0, 1, 1, 4, 5, 5, 1, 1, 1, 1, 1, 1, 2, 5, 5, 4, 1, 1, 1, 1, 0],
    [1, 0, 0, 0, 1, 1, 4, 5, 5, 1, 1, 1, 1, 1, 1, 2, 5, 5, 4, 1, 1, 1, 1, 0],
    [1, 0, 0, 0, 1, 1, 4, 5, 5, 1, 1, 1, 1, 1, 1, 2, 5, 5, 4, 1, 1, 1, 1, 0],
    [1, 0, 0, 0, 1, 1, 4, 5, 5, 1, 1, 1, 1, 1, 1, 2, 5, 5, 4, 1, 1, 1, 1, 0],
    [1, 0, 0, 0, 1, 1, 4, 5, 5, 1, 1, 1, 1, 1, 1, 2, 5, 5, 4, 1, 1, 1, 1, 0],
    [1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 0],
    [0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 0, 0]
  ];
    var days = ["lundi", "mardi", "mercredi", "jeudi", "vendredi", "samedi", "dimanche"];
*/
	
//However we draw from the bottom up.
data = data.reverse();
days = days.reverse();
	
var pane_left = 120
  , width =  $(id).width()
  , pane_right = width - pane_left
  , height = 400
  , margin = 10
  , i
  , j
  , tx
  , ty
  , max = 0;


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
for (d in y.ticks(7)) {
	(function (d){
		punchcard.
		    append("g").
		    selectAll("line").
		    data([0]).
		    enter().
		    append("line").
			    attr("x1", margin).
			    attr("x2", width - 3 * margin).
			    attr("y1", height - 3 * margin - y(d)).
			    attr("y2", height - 3 * margin - y(d)).
			    style("stroke-width", 1).
			    style("stroke", "#888");
		
		  punchcard.
		    append("g").
		    selectAll(".rule").
		    data([0]).
		    enter().
		    append("text")
			    .attr("x", margin)
			    .attr("y", height - 3 * margin - y(d) - 5)
			    .attr("text-anchor", "left")
			    .attr("class", "day"+d)
			    .style("fill", "#888")
			    .text(days[d])
			    .on("mouseover",  function() {
			        d3.selectAll(".day"+d)
			          	.transition()
			          	.style("fill", "#33B5E5"); //blue
			  		})   
		  		.on("mouseout", function() {
			       	d3.selectAll(".day"+d)
			          	.transition()
			          	.style("fill", "#888");
				      });

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
	})(d);
}

// Hour text markers.
punchcard
  .selectAll(".rule")
  .data(x.ticks(24))
  .enter()
  .append("text")
  .attr("class", "rule")
  .attr("class", function(h) { return "hour"+h;})
  .attr("x", function(h) { return pane_left - 2 * margin + x(h); })
  .attr("y", height - 3 * margin)
  .attr("text-anchor", "middle")
  .style("fill", "#888")
  .on("mouseover",  function(h) {
	  d3.selectAll(".hour"+h)
      	.transition()
      	.style("fill", "#33B5E5"); //blue
  	})
  .on("mouseout", function(h) {
	  d3.selectAll(".hour"+h)
	  	.transition()
	   	.style("fill", "#888");
	 })
  .text(function(h) {
    if (h === 0) {
      return "12a";
    } else if (h > 0 && h < 12) {
      return h;
    } else if (h === 12) {
      return "12p";
    } else if (h > 12 && h < 25) {
      return h - 12;
    }
  });


// Find the max value to normalize the size of the circles.
for (i = 0; i < data.length; i++) {
  max = Math.max(max, Math.max.apply(null, data[i]));
}

// Show the circles on the punchcard.
for (i = 0; i < data.length; i++) {
	//i is a day
	(function (i){
		for (j = 0; j < data[i].length; j++) {
			//j is an hour
			(function (j){
			 punchcard
		      .append("g")
		      .selectAll("circle")
		      .data([data[i][j]])
		      .enter()
		      .append("circle")
			 	  .style("fill", "#888")
			      .attr("class", "day"+i+" hour"+j)
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
			      .attr("r", function(d) { return  Math.sqrt(d / max) * 14; })
			      .attr("transform", function() {
			          tx = pane_left - 2 * margin + x(j);
			          ty = height - 3 * margin - y(i) -14 - margin;
			          return "translate(" + tx + ", " + ty + ")";
			        })
			       //svg tooltip 
			 	  .append("title").text(function(d) { return d});
			})(j);
	  }
	})(i);
}
};