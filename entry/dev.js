require('respo-ui');
require('./main.css');

var main = require('../target/app.main.js');

if (module.hot) {
  module.hot.accept('../target/app.main.js', function() {
    main = require('../target/app.main.js');
    main.reload_BANG_();
  });
}
