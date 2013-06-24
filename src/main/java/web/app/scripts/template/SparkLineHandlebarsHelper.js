$(document).ready(function() {
	Handlebars.registerHelper('drawSparkline', function(value, options) {
		if (this[value] === undefined) {
			throw "drawSparkline  error:" + value + " value is not defined in your JSON.";
		}
		if (this[value].id === undefined) {
			throw "drawSparkline  error:" + value + " value.id is not defined in your JSON.";
		}
		if (this[value].values === undefined) {
			throw "drawSparkline  error:" + value + " value.values is not defined in your JSON.";
		}
		//$("#" + this[value].id).innerHTML = this[value].sparklineValues;
		$("#" + this[value].id).sparkline(this[value].sparklineValues);
	});
});