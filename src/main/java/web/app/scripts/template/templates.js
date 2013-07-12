(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
templates['header'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  


  return "      <div class=\"hero-unit\">\r\n      <div class=\"navbar navbar-inverse navbar-fixed-top\">\r\n            <div class=\"navbar-inner\">\r\n              <div class=\"container\">\r\n                <button type=\"button\" class=\"btn btn-navbar\" data-toggle=\"collapse\" data-target=\".nav-collapse\">\r\n                  <span class=\"icon-bar\"></span>\r\n                  <span class=\"icon-bar\"></span>\r\n                  <span class=\"icon-bar\"></span>\r\n                </button>\r\n                <a class=\"brand\" href=\"#\">Analytica</a>\r\n                <div class=\"nav-collapse collapse\">\r\n                  <ul class=\"nav\">\r\n                    <li class=\"active\"><a href=\"index.html\">Home</a></li>\r\n                    <li class=\"dropdown\">\r\n                      <a href=\"#\" class=\"dropdown-toggle\" data-toggle=\"dropdown\">Dashboards <b class=\"caret\"></b></a>\r\n                      <ul class=\"dropdown-menu\">\r\n                        <li><a href=\"#\">DashboardFicen</a></li>\r\n                        <li><a href=\"http://localhost:8083/web/app/html/dashboard.html\">Dashboard1</a></li>\r\n                        <li><a href=\"http://localhost:8083/web/app/html/mixedChart.html\">MixedChart</a></li>\r\n                        <li><a href=\"http://localhost:8083/web/app/html/bigValue.html\">Big Value Chart</a></li>\r\n                      </ul>\r\n                    </li>\r\n                  </ul>\r\n                </div><!--/.nav-collapse -->\r\n              </div>\r\n            </div>\r\n      </div>      ";
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
  


  return "<ul class=\"breadcrumb\">\r\n  <li><a href=\"http://localhost:8083/web/app/html/dashboard.html\">Home</a> <span class=\"divider\">/</span></li>\r\n  <li><a href=\"#\">Library</a> <span class=\"divider\">/</span></li>\r\n  <li class=\"active\">Data</li>\r\n</ul>";
  });
templates['bigvalue'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", escapeExpression=this.escapeExpression;


  buffer += "\r\n<div class = \"ui-widget-header \">\r\n	<i class=\"";
  if (stack1 = helpers.icon) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.icon; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "\"></i>\r\n	<h>";
  if (stack1 = helpers.title) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.title; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</h> </div>\r\n        <div id=\"bigValue\" class=\"analytica-tile\">\r\n          \r\n          <div class =\"analytica-tile-content\" >\r\n            <span >";
  if (stack1 = helpers.data) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.data; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "\r\n            </span>\r\n          </div>\r\n          <div class = \"analytica-brand\">\r\n          <span class = \"analytica-badge\">";
  if (stack1 = helpers.label) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.label; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</span></div>\r\n      </div>";
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
    + "</h>\r\n  </div>\r\n  <div>\r\n<table class=\"table table-striped table-bordered mixed-table\">\r\n  <caption>";
  if (stack1 = helpers.title) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.title; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</caption>\r\n  <thead>\r\n    <tr >\r\n      ";
  stack1 = helpers.each.call(depth0, depth0.headers, {hash:{},inverse:self.noop,fn:self.program(1, program1, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n      "
    + "\r\n    </tr>\r\n  </thead>\r\n  <tbody>\r\n    ";
  stack1 = helpers.each.call(depth0, depth0.collection, {hash:{},inverse:self.noop,fn:self.program(3, program3, data),data:data});
  if(stack1 || stack1 === 0) { buffer += stack1; }
  buffer += "\r\n  </tbody>\r\n</table>\r\n</div>\r\n</div>\r\n\r\n\r\n";
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
    + "\" class=\"graph\">\r\n		<!--<svg></svg>-->\r\n	</div>\r\n</div>\r\n\r\n\r\n\r\n"
    + "\r\n  ";
  return buffer;
  });
templates['dashboard'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  


  return "<div class=\"row show-grid\">\r\n        <!--This should be in a template file-->\r\n        <div class=\"span3 grey-bg\">\r\n        <div class = \"ui-widget-header \"><i class=\"icon-picture\"></i><h>Horloge</h> </div>\r\n        <div id=\"wallClock\">\r\n          <!--<div class=\"grey-bg\">-->\r\n          <div id=\"fullDate\"></div>\r\n          <div id=\"bigTime\"></div>\r\n          <div id=\"dayName\"></div>\r\n        <!--</div>-->\r\n        </div>\r\n        </div>\r\n        <!--This should be in a template file-->\r\n        <div class=\"span3\">\r\n        <div  class=\"grey-bg\">\r\n          <div id = \"idbigvalue\"></div>\r\n        </div>\r\n        </div>\r\n\r\n        <div class=\"span3\">\r\n          <div class=\"grey-bg\">\r\n            <div id=\"idgraphique0\" ></div>\r\n          </div>\r\n        </div>\r\n        <div class=\"span3\">\r\n          <div class=\"grey-bg\">\r\n            <div id=\"idgraphique1\" ></div>\r\n          </div>\r\n        </div>\r\n        <div class=\"span3\">\r\n          <div class=\"grey-bg\">\r\n            <div id=\"idgraphique2\" ></div>\r\n          </div>\r\n        </div>\r\n        <div class=\"span3\">\r\n          <div class=\"grey-bg\">\r\n            <div id=\"idgraphique3\" ></div>\r\n          </div>\r\n        </div>\r\n      </div>\r\n            \r\n      <div class=\"row show-grid\">\r\n        <div class=\"span6\">\r\n          <div class=\"grey-bg\">\r\n            <div id=\"idgraphique4\" ></div>\r\n          </div>\r\n        </div>\r\n        <div class=\"span6\">\r\n          <div class=\"grey-bg\">\r\n            <div id=\"idgraphique5\" ></div>\r\n          </div>\r\n        </div>\r\n      </div>\r\n\r\n      <div class=\"row show-grid\">\r\n        <div class=\"span12 grey-bg\">\r\n            <div id=\"idgraphique6\" ></div>\r\n        </div>\r\n      </div>\r\n\r\n       <div class=\"row show-grid\">\r\n        <div class=\" span12 grey-bg\">\r\n            <div id=\"idgraphique7\" ></div>\r\n        </div>\r\n      </div>";
  });
templates['dashboardtest'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  


  return "   \r\n        <!--This should be in a template file-->\r\n    \r\n        <!--This should be in a template file-->\r\n       \r\n        <div class=\"span12\">\r\n          <div class=\"grey-bg\">\r\n            <div id=\"idgraphique4\" class=\"test\" ></div>\r\n          </div>\r\n        </div>\r\n        \r\n";
  });
templates['dashboardAnalytica'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  


  return "<div class=\"row show-grid\">\r\n        <!--This should be in a template file-->\r\n        <div class=\"span3 grey-bg\">\r\n          <div class = 'widget-inner widget-size-1x1'>\r\n        <div class = \"ui-widget-header \"><i class=\"icon-picture\"></i><h>Horloge</h> </div>\r\n        <div id=\"wallClock\" class = 'clock-content'>\r\n          <!--<div class=\"grey-bg\">-->\r\n          <div id=\"fullDate\"></div>\r\n          <div id=\"bigTime\"></div>\r\n          <div id=\"dayName\"></div>\r\n        <!--</div>-->\r\n        </div>\r\n        </div>\r\n        </div>\r\n        <!--This should be in a template file-->\r\n        <div class=\"span3\">\r\n        <div  class=\"grey-bg\">\r\n          <div id = \"idbigvalue\"></div>\r\n        </div>\r\n        </div>\r\n\r\n        <div class=\"span3\">\r\n          <div class=\"grey-bg\">\r\n            <div id=\"idgraphique3\" ></div>\r\n          </div>\r\n        </div>\r\n        <div class=\"span3\">\r\n          <div class=\"grey-bg\">\r\n            <div id=\"idgraphique4\" ></div>\r\n          </div>\r\n        </div>\r\n        <!--\r\n        <div class=\"span3\">\r\n          <div class=\"grey-bg\">\r\n            <div id=\"idgraphique2\" ></div>\r\n          </div>\r\n        </div>\r\n        <div class=\"span3\">\r\n          <div class=\"grey-bg\">\r\n            <div id=\"idgraphique3\" ></div>\r\n          </div>\r\n        </div>\r\n        -->\r\n      </div>\r\n            \r\n      <div class=\"row show-grid\">\r\n        <div class=\"span12\">\r\n          <div class=\"grey-bg\">\r\n            <div id=\"idgraphique5\" ></div>\r\n          </div>\r\n        </div>\r\n        <!--\r\n        <div class=\"span6\">\r\n          <div class=\"grey-bg\">\r\n            <div id=\"idgraphique5\" ></div>\r\n          </div>\r\n        </div>\r\n        -->\r\n      </div>\r\n      \r\n      <div class=\"row show-grid\">\r\n        <div class=\"span12 grey-bg\">\r\n            <div id=\"idgraphique6\" ></div>\r\n        </div>\r\n      </div>\r\n\r\n       <div class=\"row show-grid\">\r\n        <div class=\" span12 grey-bg\">\r\n            <div id=\"idgraphique7\" ></div>\r\n        </div>\r\n      </div>";
  });
templates['mixedChart'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  


  return "<div class=\"row show-grid\">\r\n        <!--This should be in a template file-->\r\n        \r\n        <!--This should be in a template file-->\r\n        <div id = \"idgraphique1\" class=\"grey-bg span3\">\r\n        \r\n        </div>\r\n        <div id=\"idgraphique2\" class=\"grey-bg span6\">\r\n          \r\n        </div>\r\n        <div id = \"idgraphique\" class=\"grey-bg span3\">\r\n        \r\n        </div>\r\n      </div>\r\n            \r\n      \r\n      \r\n      <div class=\"row show-grid\">\r\n        <div id=\"idgraphique3\" class=\"span12 grey-bg\">\r\n        </div>\r\n      </div>";
  });
templates['bigValueDash'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  


  return "<div class = 'row'>\r\n\r\n        <div class = 'span3 grey-bg'>\r\n          <div id = 'idgraphique0'></div>\r\n          \r\n        </div>\r\n        <div class = 'span3 grey-bg'>\r\n          <div id='idgraphique1'></div>  \r\n        </div>\r\n        <div class = 'span3 grey-bg'>\r\n          <div id = 'idgraphique2'></div>\r\n        </div>\r\n        <div class = 'span3 grey-bg'>\r\n          <div id = 'idgraphique3'></div>\r\n        </div>\r\n      </div>";
  });
templates['uptime'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  


  return "<div class=\"widget-inner  widget-size-1x1\">\r\n  <div class=\"ui-widget-header \">\r\n    <i class=\"icon-picture\"></i>\r\n    <h>Up Time</h>\r\n  </div> \r\n  <a class=\"led success\" href=\"#\"></a>\r\n  <section class=\"widget-body monitoring-widget\">\r\n    <div class=\"widget-canvas\">\r\n      <div class=\"status positive text-center\">\r\n        <div class=\"sparkline-wrapper\">\r\n          Sparkline Here(YES)\r\n        </div>\r\n      </div>\r\n      <div class=\"server-times\">\r\n        <div class=\"server-time\">\r\n          <div class=\"label t-size-x10\">Last downtime</div>\r\n          <div class=\"t-size-x24 \">12d 22h </div>\r\n        </div>\r\n        <div class=\"server-time\">\r\n          <div class=\"label t-size-x10\">Response time</div>\r\n          <div class=\"t-size-x24 \">181ms</div>\r\n        </div>\r\n      </div>\r\n    </div><!-- .widget-canvas -->\r\n  </section>    \r\n</div>";
  });
templates['showchange'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  


  return "<div class=\"widget-inner  widget-size-1x1\">\r\n  <div class=\"ui-widget-header \">\r\n    <i class=\"icon-picture\"></i>\r\n    <h>Avg time on Pages</h>\r\n  </div>\r\n  <section class=\"widget-body number-widget represent\">\r\n    <div class=\"widget-canvas ass\">\r\n      <div class=\" t-size-x52\">\r\n        6<span class=\"unit\">m</span> 52<span class=\"unit\">s</span>\r\n      </div>\r\n      <div class=\"secondary-stat t-size-x48 negative \">\r\n        <span>-19<sup>.29%</sup></span>\r\n      </div>\r\n      <div class=\"show-spark text-center\">\r\n        1,2,4,58,95,62,14,52,44,55,22,5,0,14,23,9,32,20,14\r\n      </div>\r\n    </div><!-- .widget-canvas -->\r\n  </section>\r\n</div>";
  });
templates['tablelisting'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", escapeExpression=this.escapeExpression;


  buffer += "<div class=\"widget-size-1x1\">\r\n  <div class=\"ui-widget-header \">\r\n    <i class=\"";
  if (stack1 = helpers.icon) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.icon; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "\"></i><h>";
  if (stack1 = helpers.title) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.title; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</h>\r\n  </div>\r\n  <section class=\"widget-body rag-widget no-title\">\r\n    <div class = 'widget-canvas'>\r\n      <table>\r\n        <tbody>\r\n          <tr>\r\n            <td class = \"t-size-x48 number positive text-right\">"
    + escapeExpression(((stack1 = ((stack1 = depth0.max),stack1 == null || stack1 === false ? stack1 : stack1.data)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "</td>\r\n            <td class = \"t-secondary\">"
    + escapeExpression(((stack1 = ((stack1 = depth0.max),stack1 == null || stack1 === false ? stack1 : stack1.label)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "</td>\r\n          </tr>\r\n          <tr>\r\n            <td class = \"t-size-x48 number level text-right\">"
    + escapeExpression(((stack1 = ((stack1 = depth0.mean),stack1 == null || stack1 === false ? stack1 : stack1.data)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "</td>\r\n            <td class = \"t-secondary\">"
    + escapeExpression(((stack1 = ((stack1 = depth0.mean),stack1 == null || stack1 === false ? stack1 : stack1.label)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "</td>\r\n          </tr>\r\n          <tr>\r\n            <td class = \"t-size-x48 number negative text-right\">"
    + escapeExpression(((stack1 = ((stack1 = depth0.min),stack1 == null || stack1 === false ? stack1 : stack1.data)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "</td>\r\n            <td class = \"t-secondary\">"
    + escapeExpression(((stack1 = ((stack1 = depth0.min),stack1 == null || stack1 === false ? stack1 : stack1.label)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "</td>\r\n          </tr>\r\n        </tbody>\r\n      </table>\r\n    </div>\r\n  </section>\r\n</div>";
  return buffer;
  });
templates['bignumbercount'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", escapeExpression=this.escapeExpression;


  buffer += "<div class = 'widget-size-1x1'>\r\n  <div class=\"ui-widget-header \">\r\n    <i class=\"";
  if (stack1 = helpers.icon) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.icon; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "\"></i>\r\n  <h>";
  if (stack1 = helpers.title) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.title; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</h> \r\n  </div>\r\n  <section class=\"widget-body \">\r\n    <div class=\"widget-canvas ass\">\r\n      <div class=\" t-size-x72 represent text-center \">\r\n        <span class=\"text-center\">";
  if (stack1 = helpers.Total) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.Total; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</span>\r\n      </div>\r\n      <div class=\" t-size-x30 text-center\">Last 3 Hours</div>\r\n      <div class=\" t-size-x44 visitor-spark text-center\">\r\n        1,2,4,58,95,62,14,52,44,55,22,5,0,14,23,9,32,20,14\r\n      </div>\r\n    </div><!-- .widget-canvas -->\r\n  </section>\r\n</div>";
  return buffer;
  });
})();