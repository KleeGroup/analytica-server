var initializeApplication = function initializeApplication() {
	//Sample to initialize your app.
};

//Parameters which defines One graph with both datas and ui.
var sampleGraph = {
	data: {
		url: undefined, // home/datas
		type: undefined, // Mono or multi series.	
		filters: {
			//Object to filter data.
		},
		parse: undefined // By default it is parseDataResult. function parseData() {} // Function which transforms all the data received from the server.
	},
	ui: {
		id: undefined, //Id of the graph.
		icon: undefined, // bootstrap name of the icon.
		labels: "label", //list of labels according to the datas defined in the filters the number of labels should be equals to the number of datas in filters
		type: "Graph type ", //Panel type
		options: undefined //
	},
	html: {
		title: "Default Titlee ", // Title of the panel.
		container: undefined // id of the container.
	},
	options: {

	} //General options for the graph
};

var generateGraph = function generateGraph(graph) {

	//Insert the html in the dom in order to be able to render data.
	loadPanel(graph);

//Load the callback to draw data.
	var drawGraphCallbackName = getDrawFunction(graph.data.type, graph.ui.type); //todo:determine the graph to draw.
	var parse = graph.data.parse || parseDataResult; // loads the parsing function for the corresponding type of graph
	$.ajax({
		type: "GET",
		url: generateUrl([graph.data.url], graph.data.filters), //generates the url used to call the web service
		dataType: 'json',
		success: function(response, text) {
			console.log('response', response, 'text', text);
			var labels = graph.ui.labels.split(";");
			var data = parse(response, labels);

			if (document.getElementById(graph.html.container)) {
				var graphtype = graph.ui.type;
				if (graphtype === "table") {
					data.htmlIcon = graph.ui.icon;
					data.htmlTitle = graph.html.title;
					/*Loading the datatable in the DOM*/
					$('div#' + graph.html.container).html(Handlebars.templates.table(data));
					//Drawing the sparklines after the table have been loaded in the DOM
					/*$('.line-spark').sparkline('html', {
						type: 'line',
						width: 80,
						lineColor: '#33B5E5',
						minSpotColor: 'black',
						maxSpotColor: false,
						spotColor: '#FF4444'
						//spotRadius: 3
					});
					$('.bar-spark').sparkline('html', {
						type: 'bar',
						barColor: '#33B5E5',
						//minSpotColor: false, 
						//maxSpotColor: false, 
						spotColor: '#FF4444',
						spotRadius: 3
					});*/
					$('.bar-spark').d3Sparkline(undefined,undefined);
				} else if (graphtype === "bigValue") {
					data.icon = graph.ui.icon;
					data.title = graph.html.title;
					//Loading the bigValue HandleBars template in the DOM
					$('div#' + graph.html.container).html(Handlebars.templates.bigvalue(data));

				} else {
			//Loading the other charts template in the dom
			//We have to do a callback with the name defined in the plugin because the function has to be registered in jquery.
					$('#' + graph.ui.id)[drawGraphCallbackName](data);
				}
			}
		},
		error: function(request, status, error) {
			console.error("request", request.responseText, "status", status, "error", error);
		}
	});
};



var generateGraphs = function generateGraphs(graphs) {
	for (var i = 0, graphNumber = graphs.length; i < graphNumber; i++) {
		generateGraph(graphs[i]);
	}
};

function getDrawFunction(dataType, uiType) {
	//todo check if the graph type is correct with the data type.
	// load the functio which corresponds to the dataType.

	switch (uiType) {
		case "bigValue":
			return 'drawBigValue';
			break;

		case "line":
			return 'drawlineChartWithNvd3';
			break;

		case "simpleBar":
			return 'drawbarChartWithNvd3';
			break;

		case "bar":
			return 'drawMultiBarChartWithNvd3';
			break;

		case "linebar":
			return 'drawlinePlusBarWithNvd3';
			break;

		case "pie":
			return 'drawpieChartWithNvd3';
			break;

		case "clock":
			return 'MyDigitClock';
			break;

		case "stack":
			return 'drawStackedAreaChartWithNvd3';
			break;

		case "punchcard":
			return 'drawD3PunchCard';
			break;

	}
}


// Generate an url with all the parameters where route is the default route and params is the url parameters

function generateUrl(route, params) {
	var url = '',
		SEP = '/',
		PARAM = '?',
		AND = '&';
	for (var i = 0, routeLength = route.length; i < routeLength; i++) {
		url += (route[i] + SEP);
	}
	url += PARAM;
	for (var propt in params) {
		url += (propt + '=' + params[propt] + AND);
	}
	return url.slice(0, -1); //Remove the last AND.
};


//Parse data for a mono serie graph.
/*
dataResult is an array of json objects like {x:xValue,y:yValue} and label is a String;
the result will be a json object like below
*/
function parseDataResult(dataResult, label) {
	var reconstructedData = [];
	for (var i = 0, responseLength = dataResult.length; i < responseLength; i++) {
		var r = dataResult[i];
		reconstructedData.push([r.x, r.y]);
	}
	var data = [{
			key: label,
			values: reconstructedData
		}
	];
	return data;
};

// Parse function for d3 punchCard
function parsePunchCard(response, labels) {
	var series = [],
		text = [];
	for (var cle in response) {
		if (response.hasOwnProperty(cle)) {
			var array = response[cle];
			series.push(array);
			text.push(cle);
		} else { /*throw an exception here*/ }
	};
	var datas = {};
	datas.data = series;
	datas.labels = text;

	return datas;
}


/* Parsing datas for Nvd3 sparklines the datas should be an array of json objects like {x:--, y:--}
 dataResult is an array of DataPoints ({x:--,y:--, ...} ie a json with x,y and some other properties)
 */

function parseSparkLines(dataResult, label) {
	var reconstructedData = [];
	for (var i = 0, responseLength = dataResult.length; i < responseLength; i++) {
		var r = dataResult[i];
		var obj = {};
		obj.x = r.x,
		obj.y = r.y
		reconstructedData.push(obj);
	}
	var data = [{
			key: label,
			values: reconstructedData
		}
	];
	return data;

}

// Parsing a collection of string into Nvd3 sparkline datasource . 2,3,0,1 to [{x:1,y:2},{x:2,y:3},{x:3,y:0},{x:4,y:1}]

function parseStringValuesToJsonSparklines(data) {
	var data = data.split(",");
	var result = [];
	for (var i = 0; i < data.length; i++) {
		var obj = {};
		obj.x = i,
		obj.y = data[i]
		result.push(obj);
	}
	return result;
}


//Parse data for a mutli serie graph
// response is a json object
// the returned data is an array of json objects like  {key: label,	values: reconstructedData}
//for more details , look parseDataResult function
function parseMultiSeriesD3Datas(response, labels) {
	var series = [];
	var i = 0;
	for (var cle in response) {
		if (response.hasOwnProperty(cle)) {
			var jsonObject = parseDataResult(response[cle], labels[i++])[0];
			series.push(jsonObject);
		} else { /*throw an exception here*/ }
	};
	return series;
}


function parseStackedSeriesD3Datas(response, labels) {
	var series = [];
	var i = 0;
	for (var cle in response) {
		var label = cle.split("::");
		if (response.hasOwnProperty(cle)) {
			var jsonObject = parseDataResult(response[cle], label[1])[0];
			series.push(jsonObject);
		} else { /*throw an exception here*/ }
	};
	return series;
}



//sorting function : sorts a json array according to parameters :
//field is the field to sort,
//reverse is a boolean: true= reverse sorting, false=...
//primer = type of values to be sorted it could be for example; parseFloat/parseInt/parseDouble...
var sort_by = function(field, reverse, primer) {

	var key = function(x) {
		return primer ? primer(x[field]) : x[field]
	};

	return function(a, b) {
		var A = key(a),
			B = key(b);
		return ((A < B) ? -1 : (A > B) ? +1 : 0) * [-1, 1][+ !! reverse];
	}
};


function parsePieDatas(dataResult, labels) {
	var reconstructedData = [];
	//build the piechart only with significant values whet there are too many informations to plot
	//Max informations to plot = 4 = 3 +1OTHERS
	for (var r in dataResult) {
		var name = r.split("::");

		reconstructedData.push({
			label: name[1],
			value: dataResult[r]
		});
	}
	var maxElements = 4;

	reconstructedData.sort(sort_by('value', false, parseFloat));
	if (reconstructedData.length > maxElements) {
		var otherValues = {
			label: "Others",
			value: 0
		};

		for (var i = maxElements; i < reconstructedData.length; i++) {
			otherValues.value += reconstructedData[i].value;
		}
		var numberToSuppress = reconstructedData.length - maxElements;

		reconstructedData.splice(maxElements, numberToSuppress, otherValues);
	}

	var data = [{
			key: "label",
			values: reconstructedData
		}
	];

	return data;
};

//finds the highest value in the dataResult json object
function parseBigValue(dataResult, labels) {
	var data = {};
	var reconstructedData = [];

	for (var r in dataResult) {
		var name = r.split("::");

		reconstructedData.push({
			label: name[1],
			value: dataResult[r]
		});
	}
	reconstructedData.sort(sort_by('value', false, parseFloat));
	data.label = labels;
	data.data = reconstructedData[0].value;
	return data;
}

function parseDataTable(dataResult, labels) {

	var headers = [];
	for (var i = 0; i < labels.length; i++) {
		headers.push({
			value: labels[i]
		});
	}

	var collection = [];
	var compteur = 0;
	for (var r in dataResult) {
		var obj = dataResult[r][0];
		var responseSparks = dataResult[r][1];
		var activitySparks = dataResult[r][2];
		var hits, duration;
		var sqlTime = undefined;
		var metric = "";
		for (var i = 0; i < obj.length; i++) {
			if (obj[i].metricKey.id === "duration") {
				hits = obj[i].count;
				duration = (obj[i].sum) / (hits);
			}
			metric = metric + "," + obj[i].metricKey.id;
		}
		var responseObject = {}, activityObject = {};
		responseObject.id = "response" + compteur;
		var sparkresponse = responseSparks;
		responseSparks = parseStringValuesToJsonSparklines(responseSparks);
		responseObject.values = responseSparks;
		responseObject.lastValue = responseSparks[responseSparks.length - 1].y;
		responseObject.sparklineValues = sparkresponse;
		activityObject.id = "activity" + compteur;
		var sparkActivity = activitySparks;
		activitySparks = parseStringValuesToJsonSparklines(activitySparks);
		activityObject.values = activitySparks;
		activityObject.lastValue = activitySparks[activitySparks.length - 1].y;
		activityObject.sparklineValues = sparkActivity;
		var name = r.split("::");
		var collectionElement = {
			value1: name[1],
			value2: hits,
			value3: duration.toFixed(2),
			value4: responseObject, // Here weneed a json object with an array to plot the Activity sparkline
			value5: activityObject // Here a json object with array property to plot the response time sparkline
		};
		collection.push(collectionElement);
		compteur++;
	}

	var data = {
		title: '',
		headers: headers,
		collection: collection
	};
	return data;
}

//This function will just load the appropriate template for the graph to draw

function loadPanel(graph) {
	if (document.getElementById(graph.html.container)) {
		if (graph.ui.type === "table") {

		} else {
			var graphId = 'div#' + graph.html.container;
			$(graphId).html(Handlebars.templates.graph(graph));
		}
	}
};

//Container pour analytica.
var KLEE = {
	Analytica: {
		initialize: function initialize() {
			throw "You have to override Klee.analytica.initialize function in order to have your html client initialized.";
		},
		generateGraph: generateGraph,
		generateGraphs: generateGraphs
	}
};

//A chager utiliser un pattern module ou require js.
window.KLEE = KLEE;