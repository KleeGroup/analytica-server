function initializeTestHelperAnalytica() {
  //Parameters which defines One graph with both datas and ui.
  var graphique0 = {
    data: {
      url: '/home/timeLine/PAGE', // home/datas
      type: 'Mono', // Mono or multi series.   
      filters: {
        timeFrom: "NOW-8h", //
        timeTo: "NOW%2B4h", //ProblÃ¨me ici avec le +
        timeDim: "Hour", //
        category: "PAGE", //
        datas: "duration:mean" //  
      },
      parse: undefined, //function parseData() {} // Function which transforms all the data received from the server.
    },
    ui: {
      id: 'graph0', //Id of the graph.
      icon: ' icon-picture', // bootstrap name of the icon.
      title: "Temps Moyen6", // Title of the panel.
      type: "Graph type ", //Panel type
      options: undefined //
    },
    html: {
      container: 'idgraphique0' // id of the container.
    },
    options: {
      labels: "label"
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
          timeFrom: "NOW-1h", //
          timeTo: "NOW%2B4h", //ProblÃ¨me ici avec le +
          timeDim: "Hour", //
          category: "PAGE", //
          datas: "duration:mean" //  
        },
        parse: undefined, //function parseData() {} // Function which transforms all the data received from the server.
      },
      ui: {
        id: 'graph'+i, //Id of the graph.
        icon: ' icon-picture', // bootstrap name of the icon.
        title: "Temps Moyen "+i, // Title of the panel.
        type: "Graph type ", //Panel type
        options: undefined //
      },
      html: {
        container: 'idgraphique'+ i // id of the container.
      },
      options: {
        labels:"label"+i
      } //General options for the graph
    };
  }


  //Other graphs
  var graphique1 = generateDefaultGraph();
 // graphique1.data.filters.timeFrom = 'NOW';
  var graphique2 = generateDefaultGraph();
  var graphique3 = generateDefaultGraph();
//  graphique3.data.filters.timeDim = 'Day';
  var graphique4 = generateDefaultGraph();
  //graphique4.data.filters.timeFrom = 'NOW-1h';
  //graphique4.data.filters.timeTo = 'NOW';
  //graphique4.data.filters.timeDim = 'Minute';
  var graphique5 = generateDefaultGraph();
  //graphique5.data.filters.timeTo = 'NOW%2B6h';
  var graphique6 = generateDefaultGraph();
  //graphique6.data.filters.timeFrom = 'NOW';
//  graphique6.data.filters.timeTo = 'NOW%2B6h';

  var graphique7 = generateDefaultGraph();
  var graphique8 = generateDefaultGraph();
  var graphique9 = generateDefaultGraph();
  var graphs = [
    graphique0,
    graphique1,
    graphique2,
    graphique3,
    graphique4,
    graphique5,
  ];

  KLEE.Analytica.generateGraphs(graphs);
}