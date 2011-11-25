(function() {
	var defaults;
	var apps = {};
	var themesLoaded = {};
	var widgetsetsRequested = {}
	var widgetsetApps = {};
	
	
	var log = function() {
		if (console && console.log) {
			console.log(arguments);
		}
	}
	
	var loadTheme = function(url) {
		log("loadTheme", url);
		if(!themesLoaded[url]) {
			var stylesheet = document.createElement('link');
			stylesheet.setAttribute('rel', 'stylesheet');
			stylesheet.setAttribute('type', 'text/css');
			stylesheet.setAttribute('href', url + "/styles.css");
			document.getElementsByTagName('head')[0].appendChild(stylesheet);
			themesLoaded[url] = true;
		}		
	}
	
	var isWidgetsetLoaded = function(widgetset) {
		var className = widgetset.replace(/\./g, "_");
		return (typeof window[className]) != "undefined";
	}
	
	var loadWidgetset = function(basePath, widgetset) {
		if (widgetsetsRequested[widgetset]) {
			//TODO Tell the widgetset to load another application
			return;
		}
		log("load widgetset", basePath, widgetset)
		setTimeout(function() {
			if (!isWidgetsetLoaded(widgetset)) {
				alert("Failed to load the widgetset: " + url);
			}
		}, 15000);
		
		var url = basePath + widgetset + "/" + widgetset + ".nocache.js?" + new Date().getTime();
		
		var scriptTag = document.createElement('script');
		scriptTag.setAttribute('type', 'text/javascript');
		scriptTag.setAttribute('src', url);
		document.getElementsByTagName('head')[0].appendChild(scriptTag);
		
		widgetsetsRequested[widgetset] = true;
	}
	
	window.vaadin = window.vaadin || {
		setDefaults: function(d) {
			if (defaults) {
				throw "Defaults already defined";
			}
			log("Got defaults", d)
			defaults = d;
		},
		initApplication: function(appId, config) {
			if (apps[appId]) {
				throw "Application " + appId + " already initialized";
			}
			log("init application", appId, config);
			var getConfig = function(name) {
				var value = config[name];
				if (value === undefined) {
					value = defaults[name];
				}
				return value;
			}
			
			var fetchRootConfig = function() {
				log('Fetching root config');
				var url = getConfig('appUri');
				// Root id
				url += ((/\?/).test(url) ? "&" : "?") + "browserDetails";
				url += '&rootId=' + getConfig('rootId');
				// Uri fragment
				url += '&f=' + encodeURIComponent(location.hash);
				// Timestamp to avoid caching
				url += '&' + (new Date()).getTime();
				
				var r = new XMLHttpRequest();
				r.open('POST', url, true);
				r.onreadystatechange = function (aEvt) {  
					if (r.readyState == 4) {  
						if (r.status == 200){
							log(r.responseText);
							// TODO Does this work in all supported browsers?
							var updatedConfig = JSON.parse(r.responseText);
							
							// Copy new properties to the config object
							for (var property in updatedConfig) {
								if (updatedConfig.hasOwnProperty(property)) {
									config[property] = updatedConfig[property];
								}
							}
							
							// Try bootstrapping again, this time without fetching missing info
							bootstrapApp(false);
						} else {
							log('Error', r.statusText);  
						}
					}  
				};
				r.send(null);
				
				log('sending request to ', url);
			};			
			
			//Export public data
			var app = {
				'getConfig': getConfig
			};
			apps[appId] = app;
			
			var bootstrapApp = function(mayDefer) {
				var themeUri = getConfig('themeUri');
				if (themeUri) {
					loadTheme(themeUri);
				}
				
				var widgetsetBase = getConfig('widgetsetBase');
				var widgetset = getConfig('widgetset');
				if (widgetset && widgetsetBase) {
					loadWidgetset(widgetsetBase, widgetset);
					if (widgetsetApps[widgetset]) {
						widgetsetApps[widgetset].push(appId);
					}  else {
						widgetsetApps[widgetset] = [appId];
					}
				} else if (mayDefer) {
					fetchRootConfig();
				} else {
					throw "Widgetset not defined";
				}
			}
			bootstrapApp(true);

			if (getConfig("debug")) {
				// TODO debug state is now global for the entire page, but should somehow only be set for the current application  
				window.vaadin.debug = true;
			}
			
			return app;
		},
		getApp: function(appId) {
			var app = apps[appId]; 
			return app;
		},
		popWidgetsetApp: function(widgetset) {
			if (widgetsetApps[widgetset]) {
				return widgetsetApps[widgetset].pop();
			} else {
				return null;
			}
		}
	};
	
	log('Vaadin bootstrap loaded');
})();