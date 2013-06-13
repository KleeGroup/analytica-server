//fonction d'initialisation de l'application.
var initializeApplication = function initializeApplication() {
    //loadAllPanels();
    //loadDataToDraw();
    initializeTestHelperAnalytica();
};
//Container pour analytica.
var Klee = {
    analytica: {
        initialize: function initialize() {
            initializeApplication();
        }
    }
}

//Ajouter les scripts dont on a besoin pour les graphiques
/*document.write('<scr'+'ipt type="text/javascript" src="" ></scr'+'ipt>');*/


var defaultParameters = {
    timeFrom: "NOW-2h", //
    timeTo: "NOW%2B9h", //Problème ici avec le +
    timeDim: "Hour", //
    category: "PAGE", //
    datas: "duration;count", //
    lang: "fr-FR" // Optionnel // }
}


    function getWidgetConfig() {
        //if () {
        return {
            title: "Analytica",
            icon: "icon-globe", // classe de l'icone dans le css bootstrap
            labels: "Temps Moyen", //
            route: "/home/timeLine  ",
            parameters: {
                timeFrom: "NOW-2h", //
                timeTo: "NOW%2B9h", //Problème ici avec le +
                timeDim: "Hour", //
                category: "PAGE", //
                datas: "duration;count" //
            }
        }
    }
    //Construire des options par défaut pour les graphes
    /*
    Période du tracée,
    Catégorie , unités temporelles, labels des mesures
*/


    // PARTIE GRAPHIQUE

    // Charge les données à dessiner.

    //  Old drawing function, works with monoserie datas

    function loadDataToDraw() {
        /*var panelConfig = {
            id: "panel6",
            title: "Pages Warnings",
            icon: "icon-globe", // classe de l'icone dans le css bootstrap
            labels: "Temps Moyen6", //
            route: "/home/timeLine",
            renderer: "line",
            parameters: {
                timeFrom: "NOW-4h", //
                timeTo: "NOW%2B6h", //ProblÃ¨me ici avec le +
                timeDim: "Hour", //
                category: "PAGE", //
                datas: "duration:mean" //        }
            }
        };*/

        var panelConfig = dashboardConfiguration['panels'][5],
            params = getParameters(panelConfig),
            category = params['category'],
            route = panelConfig['route'], //+ "/" + category,
            url = generateUrl([route, category], params);

        $.ajax({ // ajax call starts
            type: "GET",
            url: url, // JQuery loads serverside.php

            // data: JSON.stringify(defaultOptions),
            dataType: 'json', // Choosing a JSON datatype
            success: function(response, text) {
                console.log('response', response, 'text', text);
                // treat the json response received from the server to adapt it to the drawing library input values/
                // todo: faire cette partie côté serveur.
                var reconstructedData = [];
                for (var i = 0, responseLength = response.length; i < responseLength; i++) {
                    var r = response[i];
                    reconstructedData.push([r.x, r.y]);
                }
                var data = [{
                        values: reconstructedData
                    }
                ];
                // Appel la fonction qui dessine les graphs.
                drawGraphs(data);
            },
            error: function(request, status, error) {
                console.error("request", request.responseText, "status", status, "error", error);
            }
        });
    }

    //Nouvelle fonction de dessin: 

    function fetchAndDraw(panelConfig) {

        var panelId = panelConfig['id'],
            params = getParameters(panelConfig),
            category = params['category'],
            route = panelConfig['route'], //+ "/" + category,
            url = generateUrl([route, category], params),
            renderer = panelConfig['renderer'],
            labels = panelConfig['labels'],
            container = "#" + panelId;

        $.ajax({ // ajax call starts
            type: "GET",
            url: url, // JQuery loads serverside.php

            // data: JSON.stringify(defaultOptions),
            dataType: 'json', // Choosing a JSON datatype
            success: function(response, text) {
                var reconstructedData = [];
                for (var i = 0, responseLength = response.length; i < responseLength; i++) {
                    var r = response[i];
                    reconstructedData.push([r.x, r.y]);
                }
                var data = [{
                        values: reconstructedData
                    }
                ];
                console.log('response', response, 'text', text);
                // treat the json response received from the server to adapt it to the drawing library input values/
                // todo: faire cette partie côté serveur.
                /*
                if (renderer === "line") {
                    var data = parseD3Datas(response, labels);
                    $(container).drawlineChartWithNvd3(data);

                } else if (renderer === "bar") {
                    var data = parseD3Datas(response, labels);
                    $(container).drawMultiBarChartWithNvd3(data);

                } else {
                    console.log("Not line Nor barchart");
                }

                */

                //Choix du type de graphe à dessiner en fonction du type de vue indiqué dans le paramétrage

                switch (renderer) {
                    case "bigValue":
                        //    alert("bigValue");
                        break;
                    case "line":
                        var data = parseD3Datas(response, labels);
                        $('#' + panelId).drawlineChartWithNvd3(data);
                        break;

                    case "table":
                        // fillTable(panelId, response);
                        // alert("table");
                        break;

                    case "bar":
                        var data = parseD3Datas(response, labels);
                        $('#' + panelId).drawMultiBarChartWithNvd3(data);
                        break;

                    case "spline":
                        var data = parseD3Datas(response, labels);
                        $('#' + panelId).drawStackedAreaChartWithNvd3(data);
                        break;
                }
                // Appel la fonction qui dessine les graphs.
                // drawGraphs(data);
            },
            error: function(request, status, error) {
                console.error("request", request.responseText, "status", status, "error", error);
            }
        });
    }

    function parseD3Datas(response, labels) {
        var reconstructedData = [];
        for (var i = 0, responseLength = response.length; i < responseLength; i++) {
            var r = response[i];
            reconstructedData.push([r.x, r.y]);
        }
        var data = [{
                key: labels,
                values: reconstructedData
            }
        ];
        return data;
    }

    function parseMultiSeriesD3Datas(response, labels) {

        var series = [],i=0;
        for(var cle in response ){
            if(response.hasOwnProperty(cle)){
                series.push(parseD3Datas(response[cle],labels[i++]));
            }
        }
        return series;
    }



    function fillTable(panelId, datas) {


    }

    function getParameters(panelConfig) {
        var param = panelConfig['parameters'];
        return param;
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


var value = {
    lang: "Metallica",
    labels: "Fade To Black"
};
/*
function testFunction() {
    $.ajax({ // ajax call starts
        type: "post",
        url: '/home/test', // JQuery loads serverside.php
        data: JSON.stringify(value),
        dataType: 'jsonp', // Choosing a JSON datatype
        success: function(response, text) {
            console.log('response', response, 'text', text);
            alert(data);
        },
        error: function(request, status, error) {
            console.error("request", request.responseText, "status", status, "error", error);
        }
    });
}

// Dessine les graphs
*/

function drawGraphs(data) {
    $('#panel1').drawlineChartWithNvd3(data);
    $('#panel2').drawStackedAreaChartWithNvd3(data);
    $('#panel3').drawMultiBarChartWithNvd3(data);
    $('#panel4').drawlineChartWithNvd3(data);
    $('#panel5').drawbarChartWithNvd3(data);
}

function initializeTestHelperAnalytica() {

    //Parameters which defines One graph with both datas and ui.
    var graphiqueMono = {
        data: {
            url: '/home/multitimeLine/PAGE', // home/datas
            type: 'Multi', // Mono or multi series.   
            filters: {
                timeFrom: "NOW-6h", //
                timeTo: "NOW%2B4h", //ProblÃ¨me ici avec le +
                timeDim: "Hour", //
                category: "PAGE", //
                datas: "duration:mean;duration:count" //  
            },
            parse: parseMultiSeriesD3Datas //function parseData() {} // Function which transforms all the data received from the server.
        },
        ui: {
            id: 'monoGraph', //Id of the graph.
            icon: ' icon-picture', // bootstrap name of the icon.
            labels: "Temps Moyen;Nombre d'acces", // Title of the panel.
            type: "Graph type ", //Panel type
            options: undefined //
        },
        html: {
            title: "Graphique", 
            container: 'testResult' // id of the container.
        },
        options: {} //General options for the graph
    };
    var graphiqueMulti = {
        data: {
            url: '/home/timeLine/PAGE', // home/datas
            type: 'Multi', // Mono or multi series.   
            filters: {
                timeFrom: "NOW-6h", //
                timeTo: "NOW%2B4h", //ProblÃ¨me ici avec le +
                timeDim: "Hour", //
                category: "PAGE", //
                datas: "duration:mean;duration:count" //  
            },
            parse: undefined///parseMultiSeriesD3Datas //function parseData() {} // Function which transforms all the data received from the server.
        },
        ui: {
            id: 'monoGraph', //Id of the graph.
            icon: ' icon-picture', // bootstrap name of the icon.
            labels: "Temps Moyen;Nombre d'acces", // Title of the panel.
            type: "Graph type ", //Panel type
            options: undefined //
        },
        html: {
            title: "Graphique", 
            container: 'testResult' // id of the container.
        },
        options: {} //General options for the graph
    };
    KLEE.Analytica.generateGraph(graphiqueMulti);
}

//Découpage de l'interface principale en sous pannels : STATIC

function loadAllPanels() {

    var row1 = document.getElementById('row1');
    var row2 = document.getElementById('row2');
    var row3 = document.getElementById('row3');
    for (var i = 0; i < 4; i++) {
        var element = document.createElement("div");
        element.setAttribute('id', "chart" + i);
        element.setAttribute('class', "span3");
        row1.appendChild(element);
    }
    var element = document.createElement("div");
    element.setAttribute('id', "chart4");
    element.setAttribute('class', "span12");
    row2.appendChild(element);
    var element = document.createElement("div");
    element.setAttribute('id', "chart5");
    element.setAttribute('class', "span12");
    row3.appendChild(element);
    var panelList = dashboardConfiguration['panels'];
    for (var i = 0; i < 6; i++) {
        var chartId = "chart" + i;
        loadPanel(panelList[i], chartId);
    }
    //Dessiner remplir les pannels avec les graphes


    var datass = [{
            values: [
                [1370563200000, null],
                [1370566800000, null],
                [1370570400000, null],
                [1370574000000, null],
                [1370577600000, null],
                [1370581200000, 284.26666666666665],
                [1370584800000, 324.25],
                [1370588400000, 405.96],
                [1370592000000, 436.7142857142857],
                [1370595600000, 421.3],
                [1370599200000, 326.56],
                [1370602800000, 272.7]
            ]
        }
    ];
    //$("#panel5").drawlineChartWithNvd3(datass);
    /*for (var i = 0; i < 6; i++) {
        fetchAndDraw(panelList[i]);
    }*/
}

//construire un panel conformément aux paramètres qui lui sont passés et dans div dont l'id lui est donné

function loadPanel(config, id) {
    var container = document.getElementById(id),
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
}


/*
    Paramétrage des messages de bienvenue sur l'application
        
*/

function getWelcome() {
    lang = getLanguage();
    if (!lang || !phrases[lang]) {
        lang = 'nl';
    }
    document.getElementById('welcome').innerHTML = phrases[lang];
    document.getElementById('version').innerHTML = versionPhrases[lang];
    loadDataToDraw();
}

function getLanguage() {
    if (navigator.language) {
        lang = navigator.language;
    } else if (navigator.userLanguage) {
        lang = navigator.userLanguage;
    }

    if (lang && lang.length > 2) {
        lang = lang.substring(0, 2);
    }
    return lang;
}


//A remplacer avec un appel ajax poou récupérer le json
/*var dataOld = [{ values : [[1370563200000 , null],[1370566800000 , null],[1370570400000 , null],[1370574000000 , null],[1370577600000 , null],[1370581200000 , 284.26666666666665],[1370584800000 , 324.25],[1370588400000 , 405.96],[1370592000000 , 436.7142857142857],[1370595600000 , 421.3],[1370599200000 , 326.56],[1370602800000 , 272.7]]}];
 */



var phrases = { /* translation table for page */
    en: ["<h1>Welcome!</h1><p>Analytica Monitorig Application </p><p><a href='about.html' class='btn btn-primary btn-large'>Learn more &raquo;</a></p>"],
    fr: ["<h1>Welcome!</h1><p>Analytica: Outil de monitoring d'Application </p><p><a href='about.html' class='btn btn-primary btn-large'>Learn more &raquo;</a></p>"],
};

var versionPhrases = { /* translation table for page */
    en: ["<p class='version'>v1.1 du 16/01/2013</p> <p class='contact'>for all your remarques ou suggestions, thanks for sending us an email at <a href='mailto:npiedeloup@kleegroup.com'>npiedeloup@kleegroup.com</a></p>"],
    fr: ["<p class='version'>v1.1 du 16/01/2013</p> <p class='contact'>Pour toutes remarques ou suggestions, merci de nous envoyer un email a <a href='mailto:npiedeloup@kleegroup.com'>npiedeloup@kleegroup.com</a></p>"],
};

//todo: poru les parameters penser à prévoir des valeurs par défaut.
var dashboardConfiguration = {
    panels: [{
            id: "panel1",
            title: "users",
            icon: "icon-globe", // classe de l'icone dans le css bootstrap
            labels: "Min;Temps moyen;Max", //
            route: "/home/agregatedDatas",
            renderer: "bigValue",
            parameters: {
                timeFrom: "NOW-2h", //
                timeTo: "NOW%2B9h", //Problème ici avec le +
                timeDim: "Hour", //
                category: "PAGE", //
                datas: "duration:min;duration:mean;duration:max" //        }
            }
        }, {
            id: "panel2",
            title: "Hits",
            icon: "icon-globe", // classe de l'icone dans le css bootstrap
            labels: "%Hits", //
            route: "/home/agregatedDatas",
            renderer: "bigValue",
            parameters: {
                timeFrom: "NOW-2h", //
                timeTo: "NOW%2B9h", //Problème ici avec le +
                timeDim: "Hour", //
                category: "PAGE", //
                datas: "duration:min;duration:mean;duration:max" //        }
            }

        }, {
            id: "panel3",
            title: "Panel3",
            icon: "icon-globe", // classe de l'icone dans le css bootstrap
            labels: "Temps Moyen", //
            route: "/home/agregatedDatas",
            renderer: "bigValue",
            parameters: {
                timeFrom: "NOW-2h", //
                timeTo: "NOW%2B9h", //Problème ici avec le +
                timeDim: "Hour", //
                category: "PAGE", //
                datas: "duration:min;duration:mean;duration:max" //        }
            }


        }, {
            id: "panel4",
            title: "Panel4",
            icon: "icon-globe", // classe de l'icone dans le css bootstrap
            labels: "Temps Moyen", //
            route: "/home/agregatedDatas",
            renderer: "bigValue",
            parameters: {
                timeFrom: "NOW-2h", //
                timeTo: "NOW%2B9h", //Problème ici avec le +
                timeDim: "Hour", //
                category: "PAGE", //
                datas: "duration:min;duration:mean;duration:max" //        }
            }

        }, {
            id: "panel5",
            title: "Response Time",
            icon: "icon-globe", // classe de l'icone dans le css bootstrap
            labels: "Temps Moyen5", //
            route: "/home/timeLine",
            renderer: "line",
            parameters: {
                timeFrom: "NOW-15h", //
                timeTo: "NOW-4h", //Problème ici avec le +
                timeDim: "Hour", //
                category: "PAGE", //
                datas: "duration:mean;duration:count" //        }
            }

        }, {
            id: "panel6",
            title: "Pages Warnings",
            icon: "icon-globe", // classe de l'icone dans le css bootstrap
            labels: "Temps Moyen6", //
            route: "/home/timeLine",
            renderer: "line",
            parameters: {
                timeFrom: "NOW-4h", //
                timeTo: "NOW%2B6h", //ProblÃ¨me ici avec le +
                timeDim: "Hour", //
                category: "PAGE", //
                datas: "duration:mean" //        }
            }

        }
    ]
}