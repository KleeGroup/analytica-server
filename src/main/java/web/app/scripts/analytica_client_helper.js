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
		parse: undefined// By default it is parseDataResult. function parseData() {} // Function which transforms all the data received from the server.
	},
	ui: {
		id: undefined, //Id of the graph.
		icon: undefined, // bootstrap name of the icon.
		title: "Default title ", // Title of the panel.
		type: "Graph type ", //Panel type
		options: undefined //
	},
	html: {
		container: undefined // id of the container.
	},
	options: {} //General options for the graph
};


//Function to generat a graph.
var generateGraph = function generateGraph(graph) {

	//Insert the html in the dom in order to be able to render data.
	loadPanel(graph.ui, graph.html.container)
	//Load the callback to draw data.

	var drawGraphCallbackName = getDrawFunction(graph.data.type, graph.ui.type); //todo:determine the graph to draw.
	var parse = graph.data.parse || parseDataResult;
	$.ajax({
		type: "GET",
		url: generateUrl([graph.data.url], graph.data.filters),
		dataType: 'json',
		success: function(response, text) {
			console.log('response', response, 'text', text);
			var data = parse(response);
			//We have to do a callback with the name defined in the plugin because the function has to be registered in jquery.
			$('#' + graph.ui.id)[drawGraphCallbackName](data);
		},
		error: function(request, status, error) {
			console.error("request", request.responseText, "status", status, "error", error);
		}
	});
};

var generateGraphs = function generateGraphs(graphs){
	for(var i=0,graphNumber = graphs.length; i< graphNumber;i++){
		generateGraph(graphs[i]);
	}
};

function getDrawFunction(dataType, uiType) {
	//todo check if the graph type is correct with the data type.
	// load the functio which corresponds to the dataType.
	return 'drawlineChartWithNvd3';
}

// Generate an url with all the parameters where route is the default route and params is the url parameters
function generateUrl(route, params) {
	var url = '',
		SEP = '/',
		PARAM = '?',
		ET = '&';
	for (var i = 0, routeLength = route.length; i < routeLength; i++) {
		url += (route[i] + SEP);
	}
	url += PARAM;
	for (var propt in params) {
		url += (propt + '=' + params[propt] + ET);
	}
	return url.slice(0, -1); //Remove the last ET.
};

//Parse the results.
function parseDataResult(dataResult) {
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
};

//Load the dom structure for a panel.
//todo: a mettre dans le plugin jquery. Il faut que le plugin soit auto suffisant.
function loadPanel(config, htmlContainerId) {
	var container = document.getElementById(htmlContainerId),
		title = config['title'],
		icon = config['icon'],
		panelId = config['id'];
	var titleDiv = document.createElement("div");
	titleDiv.setAttribute('class', "ui-widget-header");
	var iconElt = document.createElement("i");
	iconElt.setAttribute('class', icon);
	var hTitle = document.createElement("h");
	hTitle.innerHTML = title;
	var widgetContent = document.createElement("div");
	widgetContent.setAttribute('id', panelId);
	widgetContent.innerHTML = "<svg></svg>";
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