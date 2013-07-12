function initializeTestHelperAnalytica() {
  //Parameters which defines One graph with both datas and ui.
  var bigValuegraph = {
    data: {
      url: '/home/agregatedDatasByCategory/PAGE', // home/datas
      type: 'Mono', // Mono or multi series.   
      filters: {
        timeFrom: "NOW-12h", //
        timeTo: "NOW%2B12h", //ProblÃ¨me ici avec le +
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
      title: "Visitors", // Title of the panel.
      container: "idbigvalue", // id of the container.
    },
    options: {
    } //General options for the graph
  };

var graph3 = {
    data: {
      url: '/home/agregatedDatasByCategory/PAGE', // home/datas
      type: 'Mono', // Mono or multi series.   
      filters: {
        timeFrom: "NOW-12h", //
        timeTo: "NOW%2B12h", //ProblÃ¨me ici avec le +
        timeDim: "Hour", //
        category: "PAGE", //
        datas: "duration:count" //  
      },
      parse: parseBigValue, //function parseData() {} // Function which transforms all the data received from the server.
    },
    ui: {
      id: 'graph3', //Id of the graph.
      icon: ' icon-picture', // bootstrap name of the icon.
      labels: "Max",
      type: "bigValue", //Panel type
      options: undefined //
    },
    html: {
      title: "Page Hits", // Title of the panel.
      container: "idgraphique3", // id of the container.
    },
    options: {
    } //General options for the graph
  };


  var graph4 = {
    data: {
      url: '/home/agregatedDatasByCategory/PAGE', // home/datas
      type: 'Mono', // Mono or multi series.   
      filters: {
        timeFrom: "NOW-12h", //
        timeTo: "NOW%2B12h", //ProblÃ¨me ici avec le +
        timeDim: "Hour", //
        category: "PAGE", //
        datas: "duration:count" //  
      },
      parse: parseBigValue, //function parseData() {} // Function which transforms all the data received from the server.
    },
    ui: {
      id: 'graph4', //Id of the graph.
      icon: ' icon-picture', // bootstrap name of the icon.
      labels: "Max",
      type: "bigValue", //Panel type
      options: undefined //
    },
    html: {
      title: "Page Hits", // Title of the panel.
      container: "idgraphique4", // id of the container.
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
        timeTo: "NOW%2B12h", //ProblÃ¨me ici avec le +
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
          timeTo: "NOW%2B12h", //ProblÃ¨me ici avec le +
          timeDim: "Hour", //
          category: "PAGE", //
          datas: "duration:mean" //  
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
  graphique1.data.filters.timeFrom = 'NOW-12h';
  graphique1.ui.type = "bigValue";
  graphique1.html.title = "Graphe MonoSerie1";
  var graphique2 = generateDefaultGraph();
  graphique2.html.title = "Pages Hits";
  graphique2.data.parse =parsePieDatas;
  graphique2.data.url = "/home/agregatedDatasByCategory/PAGE";
  graphique2.data.filters.category = "PAGE";
  graphique2.ui.type ="pie";

  var graphique3 = generateDefaultGraph();
  graphique3.ui.type = "simpleBar";
  graphique3.html.title = "Graphe MonoSerie3";
  //graphique3.data.filters.timeDim = 'Hour';
  var graphique4 = generateDefaultGraph();
  graphique4.data.filters.timeFrom = 'NOW-12h';
  graphique4.data.filters.timeTo = 'NOW%2B12h';
  graphique4.data.url = "/home/stackedDatas/PAGE";

  graphique4.data.parse = parseDataResult;
  graphique4.ui.type ="stack";
  graphique4.data.filters.datas = "duration:mean";
  graphique4.ui.labels = "Response Time";
  graphique4.html.title = "Pages Response Time";
  var graphique5 = generateDefaultGraph();//MultiSerie
  //graphique5.data.filters.timeTo = 'NOW%2B6h';
  graphique5.data.url = "/home/multitimeLine/PAGE";
  graphique5.data.filters.timeFrom = 'NOW-12h';
  graphique5.data.filters.timeTo = 'NOW%2B12h';
  graphique5.data.parse = parseMultiSeriesD3Datas;
  graphique5.data.filters.datas = "duration:mean;duration:count";
  graphique5.ui.labels = "Response Time;Hits";
  graphique5.html.title = "Page hits and response time";
  graphique5.ui.type = "linebar";


  var graphique6 = generateDefaultGraph();//MultiSerie
  graphique6.data.url = "/home/tableSparkline/PAGE";
  graphique6.html.title = "DataTable Graph";
  graphique6.data.parse = parseDataTable;
  graphique6.ui.type = "table";
  graphique6.ui.labels = "Labels;Hits;Response Time(ms);Response Time;Activity";
  var graphique7 = generateDefaultGraph();//MultiSerie
  graphique7.data.url = "/home/faketablePunchcard/PAGE";
  graphique7.html.title = "Pages Activity";
  graphique7.data.parse = parsePunchCard;
  graphique7.ui.type = "punchcard";
  

 bigValuegraph.ui.type = "bignumbercount";
  //Other graphs
 graph3.ui.type = "tablelisting";
 graph3.html.title = "Hits per hour";
 graph3.ui.type = "tablelisting";
 graph3.data.parse = parseHitsCountDatas;
bigValuegraph.data.parse = parseHitsCountDatas;

 graph4.ui.type = "showchange";
 
  var graphs = [
    bigValuegraph,
    graph3,
    graph4,
    graphique5,
    graphique6
  ];
  KLEE.Analytica.generateGraphs(graphs);
}
