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
      labels: "label",
      type: "Graph type ", //Panel type
      options: undefined //
    },
    html: {
      title: "Temps Moyen6", // Title of the panel.
      container: 'idgraphique0' // id of the container.
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
          timeFrom: "NOW-10h", //
          timeTo: "NOW%2B1h", //ProblÃ¨me ici avec le +
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
        type: "Graph type ", //Panel type
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
  graphique1.html.title = "Graphe MonoSerie1";
  var graphique2 = generateDefaultGraph();
  graphique2.html.title = "Graphe MonoSerie2";
  var graphique3 = generateDefaultGraph();
  graphique3.html.title = "Graphe MonoSerie3";
//  graphique3.data.filters.timeDim = 'Day';
  var graphique4 = generateDefaultGraph();
  graphique4.data.url = "/home/multitimeLine/PAGE";
  graphique4.data.filters.timeFrom = 'NOW-9h';
  graphique4.data.filters.timeTo = 'NOW%2B2h';
  graphique4.data.parse = parseMultiSeriesD3Datas;
  graphique4.data.filters.datas = "duration:mean;duration:count";
  graphique4.ui.labels = "Temps Moyen;Nombre d'acces";
  graphique4.html.title = "Graphe MultiSerie4";
  var graphique5 = generateDefaultGraph();//MultiSerie
  //graphique5.data.filters.timeTo = 'NOW%2B6h';
    graphique5.data.url = "/home/multitimeLine/PAGE";
  graphique5.data.filters.timeFrom = 'NOW-5h';
  graphique5.data.filters.timeTo = 'NOW';
  graphique5.data.parse = parseMultiSeriesD3Datas;
  graphique5.data.filters.datas = "duration:mean;duration:count";
  graphique5.ui.labels = "Temps Moyen;Nombre d'acces";
  graphique5.html.title = "Graphe MultiSerie5";
  var graphique6 = generateDefaultGraph();//MultiSerie
  


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