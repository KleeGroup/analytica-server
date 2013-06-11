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