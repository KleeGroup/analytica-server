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


//Function to generat a graph.
/*var generateGraph = function generateGraph(graph) {

	//Insert the html in the dom in order to be able to render data.
	loadPanel(graph.ui, graph.html)
	//Load the callback to draw data.

	var drawGraphCallbackName = getDrawFunction(graph.data.type, graph.ui.type); //todo:determine the graph to draw.
	var parse = graph.data.parse || parseDataResult;
	$.ajax({
		type: "GET",
		url: generateUrl([graph.data.url], graph.data.filters),
		dataType: 'json',
		success: function(response, text) {
			console.log('response', response, 'text', text);
			var data = parse(response,graph.ui.labels);
			//We have to do a callback with the name defined in the plugin because the function has to be registered in jquery.
			$('#' + graph.ui.id)[drawGraphCallbackName](data);
		},
		error: function(request, status, error) {
			console.error("request", request.responseText, "status", status, "error", error);
		}
	});
};*/


var generateGraph = function generateGraph(graph) {

	//Insert the html in the dom in order to be able to render data.
	loadPanel(graph.ui, graph.html)
	//Load the callback to draw data.

	var drawGraphCallbackName = getDrawFunction(graph.data.type, graph.ui.type); //todo:determine the graph to draw.
	var parse = graph.data.parse || parseDataResult;
	$.ajax({
		type: "GET",
		url: generateUrl([graph.data.url], graph.data.filters),
		dataType: 'json',
		success: function(response, text) {
			console.log('response', response, 'text', text);
			var labels = graph.ui.labels.split(";");
			var data = parse(response, labels);
			//We have to do a callback with the name defined in the plugin because the function has to be registered in jquery.
			$('#' + graph.ui.id)[drawGraphCallbackName](data);
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
			return 'drawMultiBarChartWithNvd3';

			break;

		case "table":
			// fillTable(panelId, response);
			// alert("table");
			break;

		case "bar":
			return 'drawMultiBarChartWithNvd3';

			break;

		case "pie":
			return 'drawpieChartWithNvd3';

		case "clock":
			return 'MyDigitClock';

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

//Parse the results.
/*function parseDataResult(dataResult) {
	var reconstructedData = [];
	for (var i = 0, responseLength = dataResult.length; i < responseLength; i++) {
		var r = dataResult[i];
		reconstructedData.push([r.x, r.y]);
	}
	var data = [{
			values: reconstructedData
		}
	];
	return data;
};*/
//Parse data for a mono serie graph.

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
//Parse data for a mutli serie graph

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

function parsePieDatas(dataResult, labels) {
	var reconstructedData = [];
	//for (var i = 0, responseLength = dataResult.length; i < responseLength; i++) {
	for (var r in dataResult) {
		//var r = dataResult[i];
		reconstructedData.push({
			label: r,
			value: dataResult[r]
		}

		);
	}
	var data = [{
			key: "label",
			values: reconstructedData
		}
	];
	return data;
};



//Load the dom structure for a panel.
//todo: a mettre dans le plugin jquery. Il faut que le plugin soit auto suffisant.

function loadPanel(config, htmlContainer) {
	var container = document.getElementById(htmlContainer.container),
		title = htmlContainer.title,
		icon = config['icon'],
		panelId = config['id'],
		type = config.type;
	var titleDiv = document.createElement("div");
	titleDiv.setAttribute('class', "ui-widget-header ");
	var iconElt = document.createElement("i");
	iconElt.setAttribute('class', icon);
	var hTitle = document.createElement("h");
	hTitle.innerHTML = title;
	var widgetContent = document.createElement("div");
	/*widgetContent.setAttribute('class',"white-bg");*/
	//widgetContent.setAttribute('class',"white-bg");
	widgetContent.setAttribute('id', panelId);
	if((type !== "clock")||(type !== "clock")){
	widgetContent.innerHTML = "<svg></svg>";}
	//var svgWidget = document.createElement("svg");
	//widgetContent.appendChild(svgWidget);
	titleDiv.appendChild(iconElt);
	titleDiv.appendChild(hTitle);
	container.appendChild(titleDiv);
	container.appendChild(widgetContent);
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