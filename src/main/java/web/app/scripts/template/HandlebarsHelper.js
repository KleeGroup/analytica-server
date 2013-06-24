$(document).ready(function() {
	Handlebars.registerHelper('drawSparklineWithNvd3', function(value, options) {
		if (this[value] === undefined) {
			throw "drawSparklineWithNvd3  error:" + value + " value is not defined in your JSON.";
		}
		if (this[value].id === undefined) {
			throw "drawSparklineWithNvd3  error:" + value + " value.id is not defined in your JSON.";
		}
		if (this[value].values === undefined) {
			throw "drawSparklineWithNvd3  error:" + value + " value.values is not defined in your JSON.";
		}
		$("#" + this[value].id).drawSparklineWithNvd3(this[value].values);
	});
});