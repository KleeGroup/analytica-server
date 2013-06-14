(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['graph'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  


  return "<h1>Graph</h1>";
  });
templates['container'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  


  return "<!-- Graphs.-->\r\n      <!-- ROW 1-->\r\n      <div id = 'row1' class=\"row\">\r\n        <div class=\"span3\"></div>\r\n        <div class=\"span3\"></div>\r\n        <div class=\"span3\"></div>\r\n        <div class=\"span3\"></div>\r\n      </div>\r\n      <hr>\r\n      <!-- ROW 2-->\r\n      <div id = 'row2' class=\"row\">\r\n         <div class=\"span12\"></div>\r\n      </div>\r\n      <hr>\r\n      <div id = 'row3' class=\"row\">\r\n        <div class=\"span12\"></div>\r\n      </div>\r\n    </div>";
  });
templates['header'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", escapeExpression=this.escapeExpression;


  buffer += "<h1>";
  if (stack1 = helpers.data) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.data; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</h1>\r\n      <div class=\"hero-unit\">\r\n      <div class=\"navbar navbar-inverse navbar-fixed-top\">\r\n            <div class=\"navbar-inner\">\r\n              <div class=\"container\">\r\n                <button type=\"button\" class=\"btn btn-navbar\" data-toggle=\"collapse\" data-target=\".nav-collapse\">\r\n                  <span class=\"icon-bar\"></span>\r\n                  <span class=\"icon-bar\"></span>\r\n                  <span class=\"icon-bar\"></span>\r\n                </button>\r\n                <a class=\"brand\" href=\"#\">Analytica</a>\r\n                <div class=\"nav-collapse collapse\">\r\n                  <ul class=\"nav\">\r\n                    <li class=\"active\"><a href=\"#\">Home</a></li>\r\n                    <li class=\"dropdown\">\r\n                      <a href=\"#\" class=\"dropdown-toggle\" data-toggle=\"dropdown\">Dashboards <b class=\"caret\"></b></a>\r\n                      <ul class=\"dropdown-menu\">\r\n                        <li><a href=\"#\">DashboardFicen</a></li>\r\n                        <li><a href=\"#\">Dashboard1</a></li>\r\n                      </ul>\r\n                    </li>\r\n                  </ul>\r\n                </div><!--/.nav-collapse -->\r\n              </div>\r\n            </div>\r\n      </div>      ";
  return buffer;
  });
templates['footer'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", escapeExpression=this.escapeExpression;


  buffer += "<p class=\"version\">";
  if (stack1 = helpers.version) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.version; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "du ";
  if (stack1 = helpers.date) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.date; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</p>\r\n<p class=\"contact\">\r\nPour toutes remarques ou suggestions, merci de nous envoyer un email a <a href=";
  if (stack1 = helpers.contact) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.contact; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + ">";
  if (stack1 = helpers.contact) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.contact; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</a>\r\n</p>\r\n\r\n";
  return buffer;
  });
templates['breadcrumb'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  


  return "<ul class=\"breadcrumb\">\r\n  <li><a href=\"#\">Home</a> <span class=\"divider\">/</span></li>\r\n  <li><a href=\"#\">Library</a> <span class=\"divider\">/</span></li>\r\n  <li class=\"active\">Data</li>\r\n</ul>";
  });
})();