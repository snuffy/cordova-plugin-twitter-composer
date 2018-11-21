'use strict'
var exec = require('cordova/exec');

var TWComposer = {
  compose: function(text, url) {
    return new Promise((resolve, reject) => {
      exec(response => {
        resolve(response);
      }, error => {
        reject(error);
      } , 'TWComposer', 'compose', [text, url]);
    });
  },
};

module.exports = TWComposer;