'use strict';

Handlebars.registerHelper('sanitize', function(html) {
    // Strip the script tags from the html, and return it as a Handlebars.SafeString
    html = html.replace(/<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi, '');
    return new Handlebars.SafeString(html);
});

Handlebars.registerHelper('renderTextParam', function(param) {
    var result, type = 'text';
    var isArray = param.type.toLowerCase() === 'array' || param.allowMultiple;
    var defaultValue = isArray && Array.isArray(param.default) ? param.default.join('\n') : param.default;

    if (typeof defaultValue === 'undefined') {
        defaultValue = '';
    }

    if(param.format && param.format === 'password') {
      type = 'password';
    }

    if(isArray) {
        result = '<textarea class=\'body-textarea' + (param.required ? ' required' : '') + '\' name=\'' + param.name + '\'';
        result += ' placeholder=\'Provide multiple values in new lines' + (param.required ? ' (at least one required).' : '.') + '\'>';
        result += defaultValue + '</textarea>';
    } else {
        var parameterClass = 'parameter';
        if(param.required) {
          parameterClass += ' required';
        }
        result = '<input class=\'' + parameterClass + '\' minlength=\'' + (param.required ? 1 : 0) + '\'';
        result += ' name=\'' + param.name +'\' placeholder=\'' + (param.required ? '(required)' : '') + '\'';
        result += ' type=\'' + type + '\' value=\'' + defaultValue + '\'/>';
    }
    return new Handlebars.SafeString(result);
});

Handlebars.registerHelper('prettifyDate', function(timestamp) {
    var date = new Date(timestamp);
    var options = { year: 'numeric', month: 'numeric', day: 'numeric', hour: '2-digit', minute: '2-digit'};
    return date.toLocaleDateString('en-US', options);
});
