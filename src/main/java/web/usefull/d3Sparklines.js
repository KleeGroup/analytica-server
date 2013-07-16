<!--Script for bar sparklines-->
var drawBarSparklines = function (data, id){
	var space = 1; // space between the bars
	var barWidth = 3; //width of bars 
	var height = 10;
	var color = "#C00";
	var lastColor = "#09C";
	//----------------------------------------
	var	width = (barWidth+space) * data.length;
	var y = d3.scale.linear()
		.domain([0, d3.max(data)])
		.range([0, height]);

	var rectangle = d3.selectAll("#"+id).append("svg")
		.attr("width", width)
		.attr("height", height)
		.attr("class","bar"); 

	var rectangle = rectangle.selectAll("rect")
		.data(data)
		.enter().append("rect")
			.attr("class", "bar")
			.attr("width", barWidth)
			.attr("height", y)
			.attr("y", function(d) {return height-y(d) ;})
			.attr("x", function(d, i) {return i * (barWidth+space);})
			.style("fill", function(d, i) { return i==(data.length-1)? color : lastColor;})
			.append("title").text(function(d) {return d;});
}
