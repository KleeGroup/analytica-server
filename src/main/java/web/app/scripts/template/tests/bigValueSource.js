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
      parse: parseHitsCountDatas, //function parseData() {} // Function which transforms all the data received from the server.
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
      container: "idgraphique0", // id of the container.
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
        timeTo: "NOW%2B2h", //ProblÃ¨me ici avec le +
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
        timeTo: "NOW%2B2h", //ProblÃ¨me ici avec le +
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
      container: "idgraphique", // id of the container.
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
        url: '/home/agregatedDatasByCategory/PAGE', // home/datas
        type: 'Mono', // Mono or multi series.   
        filters: {
          timeFrom: "NOW-1h", //
          timeTo: "NOW%2B3h", //ProblÃ¨me ici avec le +
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

  bigValuegraph.ui.type = "bignumbercount";
  //Other graphs
 var graphique1 = generateDefaultGraph();
 graphique1.ui.type = "uptime";
 graphique1.data.parse = parseHitsCountDatas;

 var graphique2 = generateDefaultGraph();
 graphique2.ui.type = "showchange";
 graphique2.data.parse = parseHitsCountDatas;

 var graphique3 = generateDefaultGraph();
 graphique3.ui.type = "tablelisting";
 graphique3.data.parse = parseHitsCountDatas;
 graphique3.html.title = "Response Time (ms)";

  
  

  var graphs = [
    bigValuegraph,
    graphique1,
    graphique2,
    graphique3
  ];

  KLEE.Analytica.generateGraphs(graphs);
}
