(function() {
  var template = Handlebars.template, templates = Handlebars.templates = Handlebars.templates || {};
Handlebars.partials['_tableLine'] = template(function (Handlebars,depth0,helpers,partials,data) {
  this.compilerInfo = [4,'>= 1.0.0'];
helpers = this.merge(helpers, Handlebars.helpers); data = data || {};
  var buffer = "", stack1, functionType="function", escapeExpression=this.escapeExpression;


  buffer += "<tr id=\"";
  if (stack1 = helpers.lineId) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.lineId; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "\">\r\n  <td class = \"table-labels\">\r\n\r\n	<a href=\"pages/?value=";
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
    + "</div>\r\n</td>\r\n  <td>";
  if (stack1 = helpers.value2) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.value2; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</td>\r\n  <td>";
  if (stack1 = helpers.value3) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.value3; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</td>\r\n  <td>";
  if (stack1 = helpers.value4) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.value4; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</td>\r\n  <td>";
  if (stack1 = helpers.value5) { stack1 = stack1.call(depth0, {hash:{},data:data}); }
  else { stack1 = depth0.value5; stack1 = typeof stack1 === functionType ? stack1.apply(depth0) : stack1; }
  buffer += escapeExpression(stack1)
    + "</td>\r\n</tr>";
  return buffer;
  });
})();