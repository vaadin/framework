(function() {
	var apps = {};
	var themesLoaded = {};
	var widgetsets = {};
	
	
    var log;
    if (typeof console === "undefined" || !window.location.search.match(/[&?]debug(&|$)/)) {
    	//If no console.log present, just use a no-op
    	log = function() {};
    } else if (typeof console.log === "function") {
    	//If it's a function, use it with apply
		log = function() {
			console.log.apply(console, arguments);
		};
    } else {
    	//In IE, its a native function for which apply is not defined, but it works without a proper 'this' reference
    	log = console.log;
    }
	
	var loadTheme = function(url) {
		if(!themesLoaded[url]) {
			log("loadTheme", url);
			var stylesheet = document.createElement('link');
			stylesheet.setAttribute('rel', 'stylesheet');
			stylesheet.setAttribute('type', 'text/css');
			stylesheet.setAttribute('href', url + "/styles.css");
			document.getElementsByTagName('head')[0].appendChild(stylesheet);
			themesLoaded[url] = true;
		}		
	};
	
	var isWidgetsetLoaded = function(widgetset) {
		var className = widgetset.replace(/\./g, "_");
		return (typeof window[className]) != "undefined";
	};
	
	var loadWidgetset = function(basePath, widgetset) {
		if (widgetsets[widgetset]) {
			return;
		}
		log("load widgetset", basePath, widgetset);
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
		
		widgetsets[widgetset] = {
			pendingApps: []
		};
	};
	
	window.vaadin = window.vaadin || {
		initApplication: function(appId, config) {
			if (apps[appId]) {
				throw "Application " + appId + " already initialized";
			}
			log("init application", appId, config);
			
			var testbenchId = appId.replace(/-\d+$/, '');
			window.vaadin.clients[testbenchId] = {
					isActive: function() {
						return true;
					}
			};
			
			var getConfig = function(name) {
				var value = config[name];
				return value;
			};
			
			var fetchRootConfig = function() {
				log('Fetching root config');
				var url = getConfig('browserDetailsUrl');
				if (!url) {
					// No special url defined, use the same URL that loaded this page (without the fragment)
					url = window.location.href.replace(/#.*/,'');
				}
				url += ((/\?/).test(url) ? "&" : "?") + "browserDetails=1";
				var rootId = getConfig("rootId");
				if (rootId !== undefined) {
					url += "&rootId=" + rootId;
				}

				// Tell the UI what theme it is configured to use
				var theme = getConfig('theme');
				if (theme !== undefined) {
					url += '&theme=' + encodeURIComponent(theme);
				}
				
				url += '&' + vaadin.getBrowserDetailsParameters(appId); 
				
				// Timestamp to avoid caching
				url += '&' + (new Date()).getTime();
				
				var r = new XMLHttpRequest();
				r.open('POST', url, true);
				r.onreadystatechange = function (aEvt) {  
					if (r.readyState == 4) {
						if (r.status == 200){
							log("Got root config response", r.responseText);
							var updatedConfig = JSON.parse(r.responseText);
							
							// Copy new properties to the config object
							for (var property in updatedConfig) {
								if (updatedConfig.hasOwnProperty(property)) {
									config[property] = updatedConfig[property];
								}
							}
							
							// Try bootstrapping again, this time without fetching missing info
							bootstrapApp(false);
						} else if (r.status == 500) {
							document.write(r.responseText);
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
			
			if (!window.name) {
				window.name =  appId + '-' + Math.random();
			}
			
			var bootstrapApp = function(mayDefer) {
				var vaadinDir = getConfig('vaadinDir');
				
				var themeUri = vaadinDir + 'themes/' + getConfig('theme')
				loadTheme(themeUri);
				
				var widgetset = getConfig('widgetset');
				loadWidgetset(vaadinDir + 'widgetsets/', widgetset);
				
				if (getConfig('uidl') === undefined) {
					if (mayDefer) {
						fetchRootConfig();
					} else {
						throw "May not defer bootstrap any more";
					}
				} else {
					if (widgetsets[widgetset].callback) {
						log("Starting from bootstrap", appId);
						widgetsets[widgetset].callback(appId);
					}  else {
						log("Setting pending startup", appId);
						widgetsets[widgetset].pendingApps.push(appId);
					}
				}
			};
			bootstrapApp(true);

			if (getConfig("debug")) {
				// TODO debug state is now global for the entire page, but should somehow only be set for the current application  
				window.vaadin.debug = true;
			}
			
			return app;
		},
		clients: {},
		getApp: function(appId) {
			var app = apps[appId]; 
			return app;
		},
		loadTheme: loadTheme,
		registerWidgetset: function(widgetset, callback) {
			log("Widgetset registered", widgetset);
			widgetsets[widgetset].callback = callback;
			for(var i = 0; i < widgetsets[widgetset].pendingApps.length; i++) {
				var appId = widgetsets[widgetset].pendingApps[i];
				log("Starting from register widgetset", appId);
				callback(appId);
			}
			widgetsets[widgetset].pendingApps = null;
		},
		getBrowserDetailsParameters: function(parentElementId) {
			// Screen height and width
			var url = 'sh=' + window.screen.height;
			url += '&sw=' + window.screen.width;
			
			// Window height and width
			var cw = 0;
			var ch = 0;
			if(typeof(window.innerWidth) == 'number') {
				// Modern browsers
				cw = window.innerWidth;
				ch = window.innerHeight;
			} else {
				// IE 8
				cw = document.documentElement.clientWidth;
				ch = document.documentElement.clientHeight;
			}
			url += '&cw=' + cw + '&ch=' + ch;
			

			var d = new Date();
			
			url += '&curdate=' + d.getTime();
			
			var tzo1 = d.getTimezoneOffset(); // current offset
			var dstDiff = 0;
			var rtzo = tzo1;
			
			for (var m=12;m>0;m--) {
				d.setUTCMonth(m);
				var tzo2 = d.getTimezoneOffset();
				if (tzo1 != tzo2) {
					dstDiff =  (tzo1 > tzo2 ? tzo1-tzo2 : tzo2-tzo1); // offset w/o DST
					rtzo = (tzo1 > tzo2 ? tzo1 : tzo2); // offset w/o DST
					break;
				}
			}

			// Time zone offset
			url += '&tzo=' + tzo1;
			
			// DST difference
			url += '&dstd=' + dstDiff;
			
			// Raw time zone offset
			url += '&rtzo=' + rtzo;
			
			// DST in effect?
			url += '&dston=' + (tzo1 != rtzo);
			
			var pe = document.getElementById(parentElementId);
			if (pe) {
				url += '&vw=' + pe.offsetWidth;
				url += '&vh=' + pe.offsetHeight;
			}
			
			// Location
			url += '&loc=' + encodeURIComponent(location.href);

			// Window name
			if (window.name) {
				url += '&wn=' + encodeURIComponent(window.name);
			}
			
			// Detect touch device support
	        try { document.createEvent("TouchEvent"); url += "&td=1";} catch(e){};
	        
	        return url;
		}
	};
	
	log('Vaadin bootstrap loaded');
})();