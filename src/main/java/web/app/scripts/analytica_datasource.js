function initializeTestHelperAnalytica() {
  //Parameters which defines One graph with both datas and ui.
  var bigValuegraph = {
    data: {
      url: '/home/agregatedDatasByCategory/PAGE', // home/datas
      type: 'Mono', // Mono or multi series.   
      filters: {
        timeFrom: "NOW-12h", //
        timeTo: "NOW%2B2h", //ProblÃ¨me ici avec le +
        timeDim: "Hour", //
        category: "PAGE", //
        datas: "duration:count" //  
      },
      parse: parseBigValue, //function parseData() {} // Function which transforms all the data received from the server.
    },
    ui: {
      id: 'graph0', //Id of the graph.
      icon: ' icon-picture', // bootstrap name of the icon.
      labels: "Max",
      type: "bigValue", //Panel type
      options: undefined //
    },
    html: {
      title: "Page Hits", // Title of the panel.
      container: "idbigvalue", // id of the container.
    },
    options: {
    } //General options for the graph
  };
  var graphique0 = {
    data: {
      url: '/home/timeLine/PAGE', // home/datas
      type: 'Mono', // Mono or multi series.   
      filters: {
        timeFrom: "NOW-12h", //
        timeTo: "NOW%2B2h", //ProblÃ¨me ici avec le +
        timeDim: "Hour", //
        category: "PAGE", //
        datas: "duration:count" //  
      },
      parse: undefined, //function parseData() {} // Function which transforms all the data received from the server.
    },
    ui: {
      id: 'graph0', //Id of the graph.
      icon: ' icon-picture', // bootstrap name of the icon.
      labels: "label",
      type: "clock", //Panel type
      options: undefined //
    },
    html: {
      title: "Current Time", // Title of the panel.
      container: 'idgraphique0', // id of the container.
    },
    options: {
    } //General options for the graph
  };
  var counter = 0;
  function generateDefaultGraph(i) {
    i = i || ++counter ;
    return {
      data: {
        url: '/home/timeLine/PAGE', // home/datas
        type: 'Mono', // Mono or multi series.   
        filters: {
          timeFrom: "NOW-12h", //
          timeTo: "NOW%2B2h", //ProblÃ¨me ici avec le +
          timeDim: "Hour", //
          category: "PAGE", //
          datas: "duration:count" //  
        },
        parse: undefined, //function parseData() {} // Function which transforms all the data received from the server.
      },
      ui: {
        id: 'graph'+i, //Id of the graph.
        icon: ' icon-picture', // bootstrap name of the icon.
        labels:"label"+i,
        type: "line", //Panel type
        options: undefined //
      },
      html: {
        title: "Temps Moyen "+i, // Title of the panel.
        container: 'idgraphique'+ i // id of the container.
      },
      options: {
      } //General options for the graph
    };
  }

  //Other graphs
 var graphique1 = generateDefaultGraph();
  graphique1.data.filters.timeFrom = 'NOW';
  graphique1.ui.type = "bigValue";
  graphique1.html.title = "Graphe MonoSerie1";
  var graphique2 = generateDefaultGraph();
  graphique2.html.title = "Pages Hits";
  graphique2.data.parse =parsePieDatas;
  graphique2.data.url = "/home/agregatedDatasByCategory/PAGE";
  graphique2.data.filters.category = "PAGE";
  graphique2.ui.type ="pie";

 var graphique3 = generateDefaultGraph();
  graphique3.html.title = "Graphe MonoSerie3";
  //graphique3.data.filters.timeDim = 'Hour';
  var graphique4 = generateDefaultGraph();
  graphique4.data.url = "/home/multitimeLine/PAGE";
  graphique4.data.filters.timeFrom = 'NOW-2h';
  graphique4.data.filters.timeTo = 'NOW%2B10h';
  graphique4.data.parse = parseMultiSeriesD3Datas;
  graphique4.data.filters.datas = "duration:mean";
  graphique4.ui.labels = "Temps Moyen";
  graphique4.html.title = "Graphe MultiSerie4";
  var graphique5 = generateDefaultGraph();//MultiSerie
  //graphique5.data.filters.timeTo = 'NOW%2B6h';
  graphique5.data.url = "/home/multitimeLine/PAGE";
  graphique5.data.filters.timeFrom = 'NOW';
  graphique5.data.filters.timeTo = 'NOW%2B8h';
  graphique5.data.parse = parseMultiSeriesD3Datas;
  graphique5.data.filters.datas = "duration:mean;duration:count";
  graphique5.ui.labels = "Response Time;Hits";
  graphique5.html.title = "Page hits and response time";
  graphique5.ui.type = "linebar";
  var graphique6 = generateDefaultGraph();//MultiSerie
  graphique6.data.url = "/home/dataTables/PAGE";
  graphique6.html.title = "DataTable Graph";
  graphique6.data.parse = parseDataTable;
  graphique6.ui.type = "table";
  graphique6.ui.labels = "Labels;Hits;Response Time(ms);SQL Time(ms);Metrics";

  
  var graphique7 = generateDefaultGraph();
  var graphique8 = generateDefaultGraph();
  var graphique9 = generateDefaultGraph();
  var graphs = [
    bigValuegraph,
    graphique2,
    graphique3,
    graphique4,
    graphique5,
    graphique6
  ];

  KLEE.Analytica.generateGraphs(graphs);
}