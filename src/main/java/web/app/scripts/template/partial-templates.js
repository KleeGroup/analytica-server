(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
Handlebars.partials['_tableLine'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", escapeExpression=this.escapeExpression;


  buffer += "<tr > "
    + "\r\n  <td>\r\n\r\n	<a href=\"#/";
  if (stack1 = helpers.value1) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.value1; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "\">";
  if (stack1 = helpers.value1) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.value1; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</a>  	\r\n	<div>";
  if (stack1 = helpers.unit) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.unit; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</div>\r\n</td>\r\n  <td>\r\n	<p class=\"text-right\">";
  if (stack1 = helpers.value2) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.value2; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</p>\r\n  </td>\r\n  <td>\r\n	<p class=\"text-right\">";
  if (stack1 = helpers.value3) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.value3; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</p>\r\n  </td>  \r\n  <td>\r\n  "
    + " "
    + " <div id=\""
    + escapeExpression(((stack1 = ((stack1 = depth0.value4),stack1 == null || stack1 === false ? stack1 : stack1.id)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "\" class=\"text-center\" > <span class = \"line-spark text-center\"> "
    + escapeExpression(((stack1 = ((stack1 = depth0.value4),stack1 == null || stack1 === false ? stack1 : stack1.sparklineValues)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "</span> <span class =\"spark-text\"> "
    + escapeExpression(((stack1 = ((stack1 = depth0.value4),stack1 == null || stack1 === false ? stack1 : stack1.lastValue)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "</span></div>\r\n  </td>\r\n  <td><div id=\""
    + escapeExpression(((stack1 = ((stack1 = depth0.value5),stack1 == null || stack1 === false ? stack1 : stack1.id)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "\" class = \"bar-spark text-center\">"
    + escapeExpression(((stack1 = ((stack1 = depth0.value5),stack1 == null || stack1 === false ? stack1 : stack1.sparklineValues)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "</div>\r\n</td>\r\n</tr>\r\n\r\n"
    + "\r\n<script type=\"text/javascript\">\r\n\r\n$(document).ready(function () {\r\n "
    + "\r\n  "
    + "\r\n  "
    + "\r\n  "
    + "\r\n});\r\n</script>\r\n";
  return buffer;
  });
})();