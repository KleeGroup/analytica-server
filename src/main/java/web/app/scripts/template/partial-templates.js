(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
Handlebars.partials['_tableLine'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, options, functionType="function", escapeExpression=this.escapeExpression, helperMissing=helpers.helperMissing;


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
    + "</div>\r\n</td>\r\n  <td>\r\n  <p>\r\n	";
  if (stack1 = helpers.value2) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.value2; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</p>\r\n  </td>\r\n  <td>\r\n	<p>";
  if (stack1 = helpers.value3) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.value3; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</p>\r\n  </td>  \r\n  <td id=\""
    + escapeExpression(((stack1 = ((stack1 = depth0.value4),stack1 == null || stack1 === false ? stack1 : stack1.id)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "\" class=\"text-center\">\r\n  <span class = \"line-spark text-center\"> "
    + escapeExpression(((stack1 = ((stack1 = depth0.value4),stack1 == null || stack1 === false ? stack1 : stack1.sparklineValues)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "</span> <span class =\"spark-text\"> "
    + escapeExpression(((stack1 = ((stack1 = depth0.value4),stack1 == null || stack1 === false ? stack1 : stack1.lastValue)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "</span>\r\n  </td>\r\n  <td id=\""
    + escapeExpression(((stack1 = ((stack1 = depth0.value5),stack1 == null || stack1 === false ? stack1 : stack1.id)),typeof stack1 === functionType ? stack1.apply(depth0) : stack1))
    + "\" class = \"text-center\">\r\n</td>\r\n</tr>\r\n\r\n"
    + "\r\n<script type=\"text/javascript\">\r\n  $(document).ready(function () {\r\n   "
    + "\r\n    ";
  options = {hash:{},data:data};
  buffer += escapeExpression(((stack1 = helpers.drawSparkline || depth0.drawSparkline),stack1 ? stack1.call(depth0, "value5", options) : helperMissing.call(depth0, "drawSparkline", "value5", options)))
    + "\r\n  });\r\n</script>\r\n";
  return buffer;
  });
})();