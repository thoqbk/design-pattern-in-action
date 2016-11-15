angular.module( 'slideApp', [
    'ngRoute',
    'slideApp.filters',
    'slideApp.services',
    'slideApp.directives',
    'slideApp.controllers'
]);

var slideApp = angular.module( 'slideApp', [ ] );

slideApp.controller( 'SlideController', function( $scope ) {
  $scope.pages = [
    "connection-requirements.html",
    "connection-states.html",
    "connection-interface.html",
    "state-machine-diagram.html",
    "thank-you.html"
  ];
  var initialized = false;
  $scope.initialize = function() {
    if ( !initialized ) {
      Reveal.initialize({
        controls: true,
        progress: true,
        history: true,
        center: true
      });
      initialized = true;
    }
    return "";
  };
} );
