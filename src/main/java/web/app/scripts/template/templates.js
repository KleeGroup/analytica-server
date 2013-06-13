(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['container'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  


  return "<!-- Graphs.-->\r\n      <!-- ROW 1-->\r\n      <div id = 'row1' class=\"row\">\r\n        <div id=\"idgraphique0\" class=\"span3\"></div>\r\n        <div id=\"idgraphique1\" class=\"span3\"></div>\r\n        <div id=\"idgraphique2\" class=\"span3\"></div>\r\n        <div id=\"idgraphique3\" class=\"span3\"></div>\r\n      </div>\r\n      <hr>\r\n      <!-- ROW 2-->\r\n      <div id = 'row2' class=\"row\">\r\n         <div id=\"idgraphique4\" class=\"span12\"></div>\r\n      </div>\r\n      <hr>\r\n      <div id = 'row3' class=\"row\">\r\n        <div id=\"idgraphique5\" class=\"span12\"></div>\r\n      </div>\r\n    </div>";
  });
templates['graph'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  


  return "<h1>Graph</h1>";
  });
})();