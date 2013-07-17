<!--Script for bar sparklines-->
var splitter = function(value){
	//si data est un tableau retourne le tableau
	//Si data est une string alors ion constitue un tableau en splittant 
	if ((typeof value) === "string") {
		return  value.split(";");
	}
	return value;
}
			
var drawBarSparklines = function (inData, id){
	var spacing = 4;// space between 2 ticks
	var height = 15;
	//---
	var space = 1; // space between the bars
	var color = "#C00";
	var lastColor = "#09C";
	//----------------------------------------
	var barWidth = spacing - space; 
	var data = splitter(inData);
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
			.append("title")
				.text(function(d) {return d;});
}
<!--Script for line sparklines-->
var drawLineSparklines = function (inData,id){
	var spacing = 4; // space between 2 ticks
	var height = 15;
	//--------------------------------------------------------------------------------------
	var data = splitter(inData);
	var width = spacing*data.length;
	var graph = d3.select("#"+id).append("svg")
			.attr("width", width)
			.attr("height", height);
	
	// X scale will fit values from 0-10 within pixels 0-100
	var x = d3.scale.linear()
		.domain([0, data.length])
		.range([0, width]); 
	
	// Y scale will fit values from 0-10 within pixels 0-100
	var y = d3.scale.linear()
		.domain([0, d3.max(data)])
		.range([0, height]);

	// create a line object that represents the SVN line we're creating
	var line = d3.svg.line()
		.x(function(d,i) { return x(i);})
		.y(function(d) { return height-y(d);})

	var area = d3.svg.area()
		.x(function(d,i) { return x(i);})
		.y0(height)
		.y1(function(d) { return height-y(d);})

	// display the line by appending an svg:path element with the data line we created above


	graph.append("path").attr("d", line(data));
	graph.append("path").datum(data).attr("class", "area").attr("d", area);
	
	graph.selectAll("dot")
		.data(data)
		.enter().append("circle")
			.filter(function(d,i) { return i ==data.length-1;}) // <== This line
			.style("fill", "red") // <== and this one
			.attr("r", 3.5)
			.attr("cx", function(d,i) { return width; })
			.attr("cy", function(d) { return height-y(d); });
}

