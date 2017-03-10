function completeDataTableQuery(dataColumns, dataQuery) {
	var datas = new Array();
	for(var i = 0; i < dataColumns.length; i++) {
		dataColumns[i].label;
		datas.push(dataColumns[i].data);
	}
	dataQuery.datas = datas.join(";");
}

function showDataTable(elem, datas, dataColumns, dataQuery) {
	var dataTableDatas = toDataTableDatas(datas, dataColumns);
	var table = $('<table/>').appendTo(elem);
	var thead = $('<thead/>').appendTo(table);
	var trthead = $('<tr/>').appendTo(thead);
	
	for(var i = 0; i < dataColumns.length; i++) {
		$('<th/>').append(dataColumns[i].label).appendTo(trthead);
	}
	var tbody = $('<tbody/>').appendTo(table);
	for(var i = 0; i < dataTableDatas.length; i++) {
		var tr = $('<tr/>').appendTo(tbody);
		for(var j = 0; j < dataColumns.length; j++) {
			var value = dataTableDatas[i][dataColumns[j].data];
			
			var content;
			if(dataColumns[j].content) {
				//on remplace les variables ($name) par la valeur de la ligne 
				content = dataColumns[j].content.replace(/\$([a-z:]+)/gi, function(match, v) {
					return dataTableDatas[i][v] || v;
				});
				var td = $('<td/>').append(content).appendTo(tr);
				$('.inlinebar', td).sparkline('html', {type: 'bar'} );
			} else {
				content = value;
				$('<td/>', {"class":"right"}).append(content).appendTo(tr);
			}
			
		}
	}
	
	$(table).dataTable({
		"sPaginationType": "full_numbers",
		"bLengthChange": false,
		"bFilter": false,
		/** From official site : */
		"oLanguage": {
		    "sProcessing":     "Traitement en cours...",
		    "sSearch":         "Rechercher&nbsp;:",
		    "sLengthMenu":     "Afficher _MENU_ &eacute;l&eacute;ments",
		    "sInfo":           "Affichage de l'&eacute;lement _START_ &agrave; _END_ sur _TOTAL_ &eacute;l&eacute;ments",
		    "sInfoEmpty":      "Affichage de l'&eacute;lement 0 &agrave; 0 sur 0 &eacute;l&eacute;ments",
		    "sInfoFiltered":   "(filtr&eacute; de _MAX_ &eacute;l&eacute;ments au total)",
		    "sInfoPostFix":    "",
		    "sLoadingRecords": "Chargement en cours...",
		    "sZeroRecords":    "Aucun &eacute;l&eacute;ment &agrave; afficher",
		    "sEmptyTable":     "Aucune donn&eacute;e disponible dans le tableau",
		    "oPaginate": {
		        "sFirst":      "Premier",
		        "sPrevious":   "Pr&eacute;c&eacute;dent",
		        "sNext":       "Suivant",
		        "sLast":       "Dernier"
		    },
		    "oAria": {
		        "sSortAscending":  ": activer pour trier la colonne par ordre croissant",
		        "sSortDescending": ": activer pour trier la colonne par ordre décroissant"
		    }
		}
	});	
}


/** Conversion de données servers List<date, Map<NomMetric, value>> en données DataTable.*/
function toDataTableDatas(datas, metrics) {
	var newDatas = new Array();
	for(var i = 0; i < datas.length; i++) {
		var serie = new Object();
		
		for(var j = 0; j < metrics.length; j++) {
			if(metrics[j].data == 'name') {
				serie.name = datas[i].time?datas[i].time:datas[i].category;
			} else {
				serie[metrics[j].data] = datas[i].values[metrics[j].data];
			}
		}
		newDatas.push(serie);
	}
	return newDatas;
}

