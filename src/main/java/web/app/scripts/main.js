//Ajouter les scripts dont on a besoin pour les graphiques
/*document.write('<scr'+'ipt type="text/javascript" src="" ></scr'+'ipt>');*/

function loadGraphs() {

}

function getParameters() {
    return {
        timeFrom: "NOW-2h", //
        timeTo: "NOW%2B9h", //Problème ici avec le +
        timeDim: "Hour", //
        category: "PAGE", //
        datas: "duration;count", //
        lang: "fr-FR" // Optionnel // 
    };
    //return getWidgetConfig()['parameters'];


}

function getWidgetConfig() {
    if () {
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
                datas: "duration;count", //
            }
            //home/myDatas/{category}?
        } else {

            return;
        }
    }

    //Construire des options par défaut pour les graphes
    /*
    Période du tracée,
    Catégorie , unités temporelles, labels des mesures
*/


    // PARTIE GRAPHIQUE

    // Charge les données à dessiner.


    function loadDataToDraw() {
        var category = getParameters()['category'],
            route = getWidgetConfig()['route'] + "/" + category,
            url = generateUrl(route.split("/"), getParameters());

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

    function drawGraphs(data) {
        $('#chart1').drawlineChartWithNvd3(data);
        $('#chart2').drawStackedAreaChartWithNvd3(data);
        $('#chart3').drawMultiBarChartWithNvd3(data);
        $('#chart4').drawlineChartWithNvd3(data);
        $('#chart5').drawbarChartWithNvd3(data);
    }



    //Découpage de l'interface principale en sous pannels : STATIC

    function loadAllPanels() {

        var row1 = document.getElementById('row1');
        var row2 = document.getElementById('row2');
        var row3 = document.getElementById('row3');
        for (var i = 0; i < 4; i++) {
            var element = document.createElement("div");
            element.setAttribute('id', "chart" + i);
            element.setAttribute('span', "span3");
            row1.appendChild(element);
        }
        var element = document.createElement("div");
        element.setAttribute('id', "chart4");
        element.setAttribute('span', "span12");
        row2.appendChild(element);
        var element = document.createElement("div");
        element.setAttribute('id', "chart5");
        element.setAttribute('span', "span12");
        row3.appendChild(element);
        var panelList = DashboardConfiguration['panels'];
        for (var i = 0; i < 6; i++) {
            var chartId = "chart" + i;
            loadPanel(panelList[i], chartId);
        }
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
        hTitle.value = title;
        var widgetContent = document.createElement("div");
        widgetContent.setAttribute('id', panelId);
        var svgWidget = document.createElement("svg");
        widgetContent.appendChild(svgWidget);
        titleDiv.appendChild(iconElt);
        titleDiv.appendChild(hTitle);
        container.appendChild(titleDiv);
        container.appendChild(widgetContent);

        //La construction des graphes se fera par un $('#'+panelId).drawbarChartWithNvd3(data);
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
    var DashboardConfiguration = {
        panels: [{
                id: "panel1",
                title: "users",
                icon: "icon-globe", // classe de l'icone dans le css bootstrap
                labels: "Temps Max", //
                route: "/home/agregatedDatas",
                parameters: {
                    timeFrom: "NOW-2h", //
                    timeTo: "NOW%2B9h", //Problème ici avec le +
                    timeDim: "Hour", //
                    category: "PAGE", //
                    datas: "count", //        }

                }
            }, {
                id: "panel2",
                title: "Hits",
                icon: "icon-globe", // classe de l'icone dans le css bootstrap
                labels: "%Hits", //
                route: "/home/agregatedDatas",
                parameters: {
                    timeFrom: "NOW-2h", //
                    timeTo: "NOW%2B9h", //Problème ici avec le +
                    timeDim: "Hour", //
                    category: "PAGE", //
                    datas: "count", //
                }

            }, {
                id: "panel3",
                title: "Panel3",
                icon: "icon-globe", // classe de l'icone dans le css bootstrap
                labels: "Temps Moyen", //
                route: "/home/agregatedDatas",
                parameters: {
                    timeFrom: "NOW-2h", //
                    timeTo: "NOW%2B9h", //Problème ici avec le +
                    timeDim: "Hour", //
                    category: "PAGE", //
                    datas: "duration;count", //
                }


            }, {
                id: "panel4",
                title: "Panel4",
                icon: "icon-globe", // classe de l'icone dans le css bootstrap
                labels: "Temps Moyen", //
                route: "/home/agregatedDatas",
                parameters: {
                    timeFrom: "NOW-2h", //
                    timeTo: "NOW%2B9h", //Problème ici avec le +
                    timeDim: "Hour", //
                    category: "PAGE", //
                    datas: "duration;count", //
                }

            }, {
                id: "panel5",
                title: "Response Time",
                icon: "icon-globe", // classe de l'icone dans le css bootstrap
                labels: "Temps Moyen", //
                route: "/home/timeLine",
                parameters: {
                    timeFrom: "NOW-2h", //
                    timeTo: "NOW%2B9h", //Problème ici avec le +
                    timeDim: "Hour", //
                    category: "PAGE", //
                    datas: "duration;count", //
                }

            }, {
                id: "panel6",
                title: "Pages Warnings",
                icon: "icon-globe", // classe de l'icone dans le css bootstrap
                labels: "Temps Moyen", //
                route: "/home/timeLine",
                parameters: {
                    timeFrom: "NOW-2h", //
                    timeTo: "NOW%2B9h", //Problème ici avec le +
                    timeDim: "Hour", //
                    category: "PAGE", //
                    datas: "duration;count", //
                }

            }
        ]
    }