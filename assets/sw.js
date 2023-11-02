
console.log('loaded sw.js');

var appVersion = '0.0.1-0';
var urlsToCache = [];

self.addEventListener('install', function(event) {
  // Perform install steps
  event.waitUntil(
    caches.open(appVersion).then(function(cache) {
      console.log('Opened cache');
      return cache.addAll(urlsToCache);
    }));
});

self.addEventListener('activate', function (event) {
  console.log('new version', appVersion);
  event.waitUntil(
    caches.keys().then(function (cacheNames) {
      return Promise.all(
        cacheNames.filter(function (cacheName) {
          return cacheName !== appVersion
        }).map(function (cacheName) {
          console.log('delete cacheName', cacheName);
          return caches.delete(cacheName)
        })
      )
    })
  )
});

self.addEventListener('fetch', function(event) {
  // console.log('fetching:', event.request);
  if (event.request.url.match(/\.html/)) {
    event.respondWith(fetch(event.request).catch(function(error) {
      console.log('not found HTML', error)
      return caches.match(event.request);
    }));
  } else {
    event.respondWith(
      caches.match(event.request).then(function(response) {
        if (response) {
          caches.open(appVersion).then(function(cache) {
            return cache.put(event.request, response.clone());
          });
          return response;
        } else {
          return fetch(event.request);
        }
      })
    );
  }
});
