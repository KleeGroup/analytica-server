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

		$("#"+this[value].id).d3Sparkline(this[value].sparklineValues,this[value].id);

		//$("#" + this[value].id).d3Sparkline(this[value].sparklineValues,this[value].id);
		//$("#" + this[value].id).innerHTML = this[value].sparklineValues;
		//$("#" + this[value].id).sparkline(this[value].sparklineValues);
		
	});

Handlebars.registerHelper('paginateTable',function(value,options){
	
	$('table.paginated').each(function() {
	    var currentPage = 0;
	    var numPerPage = 10;
	    var $table = $(this);
	    $table.bind('repaginate', function() {
	        $table.find('tbody tr').hide().slice(currentPage * numPerPage, (currentPage + 1) * numPerPage).show();
	    });
	    $table.trigger('repaginate');
	    var numRows = $table.find('tbody tr').length;
	    var numPages = Math.ceil(numRows / numPerPage);
	    var $pager = $('<div class="pager"></div>');
	    for (var page = 0; page < numPages; page++) {
	        $('<span class="page-number"></span>').text(page + 1).bind('click', {
	            newPage: page
	        }, function(event) {
	            currentPage = event.data['newPage'];
	            $table.trigger('repaginate');
	            $(this).addClass('active').siblings().removeClass('active');
	        }).appendTo($pager).addClass('clickable');
	    }
	    $pager.insertAfter($table).find('span.page-number:first').addClass('active');
	});
});

});



