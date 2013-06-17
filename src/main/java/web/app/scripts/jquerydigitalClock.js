	
(function($) {

    var _options = {};
	var _container = {};

	jQuery.fn.MyDigitClock = function(options) {


		var idC = $(this).get(0).id;
		var container = document.getElementById(idC);
		var clockContainer = document.createElement('div');
		clockContainer.setAttribute('id','cockId');
		var id = clockContainer.id;


		_options[id] = $.extend({}, $.fn.MyDigitClock.defaults, options);
		
		return this.each(function()
		{
			_container[id] = $(this);
			showClock(id);
		
			
		
		});
		
		function showClock(id)
		{
			var d = new Date;
			var h = d.getHours();
			var m = d.getMinutes();
			var s = d.getSeconds();

			var day = d.toLocaleString();
			var date = day.split(" ");
			var ampm = "";
			if (_options[id].bAmPm)
			{
				if (h>12)
				{
					h = h-12;
					ampm = " PM";
				}
				else
				{
					ampm = " AM";
				}
			}
			
			var templateStr = _options[id].timeFormat + ampm;
			templateStr = templateStr.replace("{HH}", getDD(h));
			templateStr = templateStr.replace("{MM}", getDD(m));
			templateStr = templateStr.replace("{SS}", getDD(s));
			
			var obj = $("#"+id);
			//obj.css("fontSize", _options[id].fontSize);
			obj.css("margin-top",'auto');
			obj.css("top",'50%');
			obj.css("top",'50%');

			obj.css("margin",'0 auto');
			obj.css("text-align",'center');
			obj.css("fontSize",'200%');
			obj.css("fontFamily", _options[id].fontFamily);
			obj.css("color", _options[id].fontColor);
			obj.css("background", _options[id].background);
			obj.css("fontWeight", _options[id].fontWeight);
			//obj.css("width","90%");*/

		
			//change reading
			obj.html(templateStr)
			
			//toggle hands
			if (_options[id].bShowHeartBeat)
			{
				obj.find("#ch1").fadeTo(800, 0.1);
				obj.find("#ch2").fadeTo(800, 0.1);
			}
			setTimeout(function(){showClock(id)}, 1000);
			container.appendChild(clockContainer);
		}
		
		function getDD(num)
		{
			return (num>=10)?num:"0"+num;
		}
		
		function refreshClock()
		{
			setupClock();
		}
	}

	//default values
	jQuery.fn.MyDigitClock.defaults = {
		fontSize:100, 
		fontColor:"grey",
		bAmPm:true,
		background:"#fff",
		fontWeight:"bold",
		timeFormat: '{HH}<span id="ch1">:</span>{MM}'
	};

})(jQuery);