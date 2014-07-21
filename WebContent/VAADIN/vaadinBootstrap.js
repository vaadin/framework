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
	
	var loadWidgetset = function(url, widgetset) {
		if (widgetsets[widgetset]) {
			return;
		}
		log("load widgetset", url, widgetset);
		setTimeout(function() {
			if (!isWidgetsetLoaded(widgetset)) {
				alert("Failed to load the widgetset: " + url);
			}
		}, 15000);
	
		var scriptTag = document.createElement('script');
		scriptTag.setAttribute('type', 'text/javascript');
		scriptTag.setAttribute('src', url);
		document.getElementsByTagName('head')[0].appendChild(scriptTag);
		
		widgetsets[widgetset] = {
			pendingApps: []
		};
	};

	var isInitializedInDom = function(appId) {
		var appDiv = document.getElementById(appId);
		if (!appDiv) {
			return false;
		}
		for ( var i = 0; i < appDiv.childElementCount; i++) {
			var className = appDiv.childNodes[i].className;
			// If the app div contains a child with the class
			// "v-app-loading" we have only received the HTML 
			// but not yet started the widget set
			// (UIConnector removes the v-app-loading div).
			if (className && className.indexOf("v-app-loading") != -1) {
				return false;
			}
		}
		return true;
	};

	window.vaadin = window.vaadin || {
		initApplication: function(appId, config) {
			var testbenchId = appId.replace(/-\d+$/, '');
			
			if (apps[appId]) {
				if (window.vaadin && window.vaadin.clients && window.vaadin.clients[testbenchId] && window.vaadin.clients[testbenchId].initializing) {
					throw "Application " + appId + " is already being initialized";
				}
				if (isInitializedInDom(appId)) {
					throw "Application " + appId + " already initialized";
				}
			}

			log("init application", appId, config);
			
			window.vaadin.clients[testbenchId] = {
					isActive: function() {
						return true;
					},
					initializing: true
			};
			
			var getConfig = function(name) {
				var value = config[name];
				return value;
			};
			
			var fetchRootConfig = function(callback) {
				log('Fetching root config');
				var url = getConfig('browserDetailsUrl');
				if (!url) {
					// No special url defined, use the same URL that loaded this page (without the fragment)
					url = window.location.href.replace(/#.*/,'');
				}
				// Timestamp to avoid caching
				url += ((/\?/).test(url) ? "&" : "?") + "v-" + (new Date()).getTime();		
				
				var params = "v-browserDetails=1";
				var rootId = getConfig("v-rootId");
				if (rootId !== undefined) {
					params += "&v-rootId=" + rootId;
				}

				// Tell the UI what theme it is configured to use
				var theme = getConfig('theme');
				if (theme !== undefined) {
					params += '&theme=' + encodeURIComponent(theme);
				}
				
				params += "&v-appId=" + appId;
				
				var extraParams = getConfig('extraParams')
				if (extraParams !== undefined) {
					params += extraParams;
				}
				
				params += '&' + vaadin.getBrowserDetailsParameters(appId); 
				
				var r;
				try {
					r = new XMLHttpRequest();
				} catch (e) {
					r = new ActiveXObject("MSXML2.XMLHTTP.3.0");
				}
				r.open('POST', url, true);
				r.onreadystatechange = function (aEvt) {  
					if (r.readyState == 4) {
						// Save responseStatus so as Offline Applications know what happened
						// when loading root configuration from server, and depending on the
						// error status display an error message or the offline UI.
						config.rootResponseStatus = r.status;
						config.rootResponseText = r.responseText;

						var text = r.responseText;
						if (r.status == 200){
							log("Got root config response", text);
							var updatedConfig = JSON.parse(text);
							
							// Copy new properties to the config object
							for (var property in updatedConfig) {
								if (updatedConfig.hasOwnProperty(property)) {
									config[property] = updatedConfig[property];
								}
							}
							
							// Try bootstrapping again, this time without fetching missing info
							bootstrapApp(false);
						} else {
							log('Error', r.statusText, text);
							
							//Let TB waitForVaadin work again
							delete window.vaadin.clients[testbenchId];
							
							// Show the error in the app's div
							var appDiv = document.getElementById(appId);
							appDiv.innerHTML = text;
							appDiv.style['overflow'] = 'auto';
						}

						// Run the fetchRootConfig callback if present.
						callback && callback(r);
					}  
				};
				// send parameters as POST data
				r.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
				r.send(params);
				
				log('sending request to ', url);
			};			
			
			//Export public data
			var app = {
				getConfig: getConfig,
				// Used when the app was started in offline, so as it is possible
				// to defer root configuration loading until network is available.
				fetchRootConfig: fetchRootConfig
			};
			apps[appId] = app;
			
			if (!window.name) {
				window.name =  appId + '-' + Math.random();
			}
			
			var bootstrapApp = function(mayDefer) {
				var vaadinDir = getConfig('vaadinDir');
				
				var themeUri = vaadinDir + 'themes/' + getConfig('theme');
				loadTheme(themeUri);
				
				var widgetset = getConfig('widgetset');
				var widgetsetUrl = getConfig('widgetsetUrl');
				if (!widgetsetUrl) {
					widgetsetUrl = vaadinDir + 'widgetsets/' + widgetset + "/" + widgetset + ".nocache.js?" + new Date().getTime();
				}
				loadWidgetset(widgetsetUrl, widgetset);
				
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
		getAppIds: function() {
			var ids = [ ];
			for (var id in apps) {
				if (apps.hasOwnProperty(id)) {
					ids.push(id);
				}
			}
			return ids;
		},
		getApp: function(appId) {
			return apps[appId];
		},
		loadTheme: loadTheme,
		registerWidgetset: function(widgetset, callback) {
			log("Widgetset registered", widgetset);
			var ws = widgetsets[widgetset];
			if (ws && ws.pendingApps) {
				ws.callback = callback;
				for(var i = 0; i < ws.pendingApps.length; i++) {
					var appId = ws.pendingApps[i];
					log("Starting from register widgetset", appId);
					callback(appId);
				}
				ws.pendingApps = null;
			}
		},
		getBrowserDetailsParameters: function(parentElementId) {
			// Screen height and width
			var params = 'v-sh=' + window.screen.height;
			params += '&v-sw=' + window.screen.width;
			
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
			params += '&v-cw=' + cw + '&v-ch=' + ch;
			

			var d = new Date();
			
			params += '&v-curdate=' + d.getTime();
			
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
			params += '&v-tzo=' + tzo1;
			
			// DST difference
			params += '&v-dstd=' + dstDiff;
			
			// Raw time zone offset
			params += '&v-rtzo=' + rtzo;
			
			// DST in effect?
			params += '&v-dston=' + (tzo1 != rtzo);
			
			var pe = document.getElementById(parentElementId);
			if (pe) {
				params += '&v-vw=' + pe.offsetWidth;
				params += '&v-vh=' + pe.offsetHeight;
			}
			
			// Location
			params += '&v-loc=' + encodeURIComponent(location.href);

			// Window name
			if (window.name) {
				params += '&v-wn=' + encodeURIComponent(window.name);
			}
			
			// Detect touch device support
			var supportsTouch = false;
			try {
				document.createEvent("TouchEvent");
				supportsTouch = true;
			} catch (e) {
				// Chrome and IE10 touch detection
				supportsTouch = 'ontouchstart' in window
						|| navigator.msMaxTouchPoints;
			}

			if (supportsTouch) {
				params += "&v-td=1";
			}
   	        
	        return params;
		}
	};
	
	log('Vaadin bootstrap loaded');
})();
