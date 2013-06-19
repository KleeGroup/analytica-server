(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['container'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  


  return "<!-- Graphs.-->\r\n      <!-- ROW 1-->\r\n      <div id = 'row1' class=\"row\">\r\n        <div class=\"span3\"></div>\r\n        <div class=\"span3\"></div>\r\n        <div class=\"span3\"></div>\r\n        <div class=\"span3\"></div>\r\n      </div>\r\n      <hr>\r\n      <!-- ROW 2-->\r\n      <div id = 'row2' class=\"row\">\r\n         <div class=\"span12\"></div>\r\n      </div>\r\n      <div id = 'row3' class=\"row\">\r\n        <div class=\"span12\"></div>\r\n      </div>\r\n  ";
  });
templates['header'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  


  return "      <div class=\"hero-unit\">\r\n      <div class=\"navbar navbar-inverse navbar-fixed-top\">\r\n            <div class=\"navbar-inner\">\r\n              <div class=\"container\">\r\n                <button type=\"button\" class=\"btn btn-navbar\" data-toggle=\"collapse\" data-target=\".nav-collapse\">\r\n                  <span class=\"icon-bar\"></span>\r\n                  <span class=\"icon-bar\"></span>\r\n                  <span class=\"icon-bar\"></span>\r\n                </button>\r\n                <a class=\"brand\" href=\"#\">Analytica</a>\r\n                <div class=\"nav-collapse collapse\">\r\n                  <ul class=\"nav\">\r\n                    <li class=\"active\"><a href=\"#\">Home</a></li>\r\n                    <li class=\"dropdown\">\r\n                      <a href=\"#\" class=\"dropdown-toggle\" data-toggle=\"dropdown\">Dashboards <b class=\"caret\"></b></a>\r\n                      <ul class=\"dropdown-menu\">\r\n                        <li><a href=\"#\">DashboardFicen</a></li>\r\n                        <li><a href=\"#\">Dashboard1</a></li>\r\n                      </ul>\r\n                    </li>\r\n                  </ul>\r\n                </div><!--/.nav-collapse -->\r\n              </div>\r\n            </div>\r\n      </div>      ";
  });
templates['footer'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", escapeExpression=this.escapeExpression;


  buffer += "<div id =\"container\">\r\n	<p class = \"muted credit\">\r\n		<p class=\"version\">";
  if (stack1 = helpers.version) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.version; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "du ";
  if (stack1 = helpers.date) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.date; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</p>\r\n		<p class=\"contact\">\r\n			Pour toutes remarques ou suggestions, merci de nous envoyer un email a <a href=";
  if (stack1 = helpers.contact) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.contact; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + ">";
  if (stack1 = helpers.contact) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.contact; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</a>\r\n		</p>\r\n	</p>\r\n</div>\r\n";
  return buffer;
  });
templates['breadcrumb'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  


  return "<ul class=\"breadcrumb\">\r\n  <li><a href=\"#\">Home</a> <span class=\"divider\">/</span></li>\r\n  <li><a href=\"#\">Library</a> <span class=\"divider\">/</span></li>\r\n  <li class=\"active\">Data</li>\r\n</ul>";
  });
templates['bigvalue'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", escapeExpression=this.escapeExpression;


  buffer += "<!-- Big Value Graph.-->\r\n      <div>\r\n	      <div class = \"bigValue\">";
  if (stack1 = helpers.data) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.data; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</div>\r\n	      <div class = \"bigValuelabel\">";
  if (stack1 = helpers.label) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.label; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</div>\r\n      </div>\r\n";
  return buffer;
  });
templates['table'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); partials = this.merge(partials, Handlebars.partials); data = data || {};
  var buffer = "", stack1, functionType="function", escapeExpression=this.escapeExpression, self=this;

function program1(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n        <th>";
  if (stack1 = helpers.value) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.value; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</th>\r\n      ";
  return buffer;
  }

function program3(depth0,data) {
  
  var buffer = "", stack1;
  buffer += "\r\n    "
    + "\r\n        ";
  stack1 = self.invokePartial(partials._tableLine, '_tableLine', depth0, helpers, partials, data);
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n   ";
  return buffer;
  }

  buffer += " <div>\r\n <div class=\"ui-widget-header \">\r\n    <i class=\"";
  if (stack1 = helpers.htmlIcon) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.htmlIcon; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "\"></i>\r\n    <h>";
  if (stack1 = helpers.htmlTitle) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.htmlTitle; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</h>\r\n  </div>\r\n  <div>\r\n<table class=\"table table-hover\">\r\n  <caption>";
  if (stack1 = helpers.title) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.title; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</caption>\r\n  <thead>\r\n    <tr>\r\n      ";
  stack1 = helpers.each.call(depth0, depth0.headers, {hash:{},inverse:self.noop,fn:self.program(1, program1, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n      "
    + "\r\n    </tr>\r\n  </thead>\r\n  <tbody>\r\n    ";
  stack1 = helpers.each.call(depth0, depth0.collection, {hash:{},inverse:self.noop,fn:self.program(3, program3, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n  </tbody>\r\n</table>\r\n</div>\r\n</div>\r\n";
  return buffer;
  });
templates['graph'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", escapeExpression=this.escapeExpression;


  buffer += "<div class=\""
    + escapeExpression(((stack1 = ((stack1 = depth0.html),stack1 == null || stack1 === false ? stack1 : stack1.gridSize)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "\"> \r\n	<div class=\"ui-widget-header \">\r\n		<i class=\""
    + escapeExpression(((stack1 = ((stack1 = depth0.ui),stack1 == null || stack1 === false ? stack1 : stack1.icon)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "\"></i>\r\n		<h>"
    + escapeExpression(((stack1 = ((stack1 = depth0.html),stack1 == null || stack1 === false ? stack1 : stack1.title)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "</h>\r\n	</div>\r\n	<div id=\""
    + escapeExpression(((stack1 = ((stack1 = depth0.ui),stack1 == null || stack1 === false ? stack1 : stack1.id)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "\">\r\n		<svg></svg>\r\n	</div>\r\n</div>\r\n\r\n"
    + "\r\n  ";
  return buffer;
  });
})();