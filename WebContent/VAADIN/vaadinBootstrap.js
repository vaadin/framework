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
		
		//document.write("<script type='text/javascript' src='"+url+"'></script>");
		
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
			log("Got defaults", defaults)
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
			}
			
			if (getConfig("debug")) {
				// TODO debug state is now global for the entire page, but should somehow only be set for the current application  
				window.vaadin.debug = true;
			}
			
			//Export public data
			var app = {
				'getConfig': getConfig
			};
			apps[appId] = app;
			
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