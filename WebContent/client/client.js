
/** Creates new ITMillToolkit ajax client.
 *  @param windowElementNode Reference to element that will contain the 
 * 				application window.
 *  @param servletUrl Base URL to server-side ajax adapter.
 *  @param clientRoot Base URL to client-side ajax adapter resources.
 *  @constructor
 *
 *  @author Oy IT Mill Ltd / Sami Ekblad
 */
function ITMillToolkitClient(windowElementNode, servletUrl, clientRoot, waitElement) {

	// Store parameters
	this.mainWindowElement = windowElementNode;
	if (this.mainWindowElement == null) {
		alert("Invalid window element. Ajax client not properly initialized.");
	}
	this.itmtkMainWindow = window;
	
	this.ajaxAdapterServletUrl = servletUrl;
	if (this.ajaxAdapterServletUrl == null) {
		alert("Invalid servlet URL. Ajax client not properly initialized.");
	}

	// Wait element is shown during the ajax requests
	this.waitElement = waitElement;

	// Root of the client scripts
	if (clientRoot == null)
		this.clientRoot = "";
	else (clientRoot.length > 0)
		this.clientRoot = clientRoot + (clientRoot.match('/$') ? "" : "/" );	

	// Debugging is disabled by default
	this.debugEnabled = false;

	// Initialize variableChangeQueue
	this.variableStates = new Object();
	
	// Create empty renderers list
	this.renderers = new Object(); 

	// Create windows list
	this.documents = new Object(); 
	this.windows = new Object(); 
	
	// Remove all eventListeners on window.unload
	with (this) {
		addEventListener(window,"unload", function () {
			var removed =  removeAllEventListeners(document);
			if (window.eventMap) {
				for (var t in window.eventMap) {
					var i = window.eventMap[t].length;
					while (i--) {
						client.removeEventListener(window,t,window.eventMap[t][i]);
						removed++;
					}
				}
				window.eventMap = null;
			}
			
			debug("Removed " + removed + " event listeners.");
			// TODO close all windows
			debug("Removed " + unregisterAllLayoutFunctions()+ " layout functions.");
			
			window.png = null;
		});
		var client = this;
		var func = function() {
			client.resizeTimeout=null;
			client.processAllLayoutFunctions()
		};
		
		addEventListener(window,"resize", function () {	
			if (client.resizeTimeout) clearTimeout(client.resizeTimeout);				
			client.resizeTimeout = setTimeout(func,500);
		});
		
	}
	
	window.png = function(img) {
       var src = img.src;
        if (!src || src.indexOf("pixel.gif")>0) return;
        if (src.indexOf(".png")<1) return
        var ua = navigator.userAgent.toLowerCase();
        if (ua.indexOf("windows")<0) return;
        var msie = ua.indexOf("msie");
        if (msie < 0) return;
        var v = parseInt(ua.substring(msie+5,msie+6));
        if (!v || v < 5 || v > 6) return;
        
        var w = img.width||16; // def width 16, hidden icons fail otherwise
        var h = img.height||16;

        
        img.onload = null;
        img.src = clientRoot + "pixel.gif";
        img.style.height = h+"px";
        img.style.width = w+"px";
        img.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+src+"', sizingMethod='crop');";               
	}
	
}

/** Start the ajax client.
 *  Creates debug window and sends the initial request to server.
 *
 *  @author Oy IT Mill Ltd / Sami Ekblad
 */
ITMillToolkitClient.prototype.start = function() {
    
    if (this.debugEnabled) {
    	this.debug("Starting Ajax client");
	} 
    
    // Send initial request
	this.processVariableChanges(true);
}



/** Creates new debug window.
 *
 *  @return New debug window instance.
 *  @type Window
 *  @private
 *
 *  @author Oy IT Mill Ltd / Sami Ekblad
 */
ITMillToolkitClient.prototype.createDebugWindow = function() {

	var dw = window.open("","ITMillToolkitDebugWindow","width=500,height=700,scrollbars=1,menubar=0,status=0,titlebar=0,toolbar=0,resizable=1");
	if (dw != null) {
		with (dw.document) {
		
			if (dw.document.body != null) {
				dw.document.body.innerHTML = "";			
			}
			
			write("<html><head>");
			write("<title>IT Mill Toolkit Adapter Debug</title>");
			write("<link rel=\"stylesheet\" href=\""+this.clientRoot+"debug.css\" type=\"text/css\" >");
			write("</head>");
			write("<body><h2>Debug</h2>");
			write("</body></html>\n");
		}
	} else {
		return null;
	}    
	return dw;
}


ITMillToolkitClient.prototype.warn = function (message, folded, extraStyle, html) {

	// Check if we are in debug mode
	if (!this.debugEnabled)	{ return; }

	this.debug(message, folded, "warn "+(extraStyle?extraStyle:""), html);
}
/** Write debug message to debug window.
 *
 *  @param message The message to be written
 *  @param folded True if the message should be foldable and folded to default,
 *			false or missing otherwise.
 *
 *  @author Oy IT Mill Ltd / Sami Ekblad
 *
 */
ITMillToolkitClient.prototype.debug = function (message, folded, extraStyle, html) {

	// Check if we are in debug mode
	if (!this.debugEnabled)	{ return; }

	// Ensure we have a debug window
	if (this.debugwindow == null) {
		this.debugwindow = this.createDebugWindow();
	}

	// If window is closed disable debug
	if (this.debugwindow.closed) {
		alert("Debug window closed. Disabling debug.\n Press reload to re-enable debug window.");
    	this.debugEnabled = false;
		return;
	}
	
	// Apply the extra style given
	if (extraStyle != null) {
		extraStyle = " "+extraStyle;
	} else {
		extraStyle = "";
	}
	
	// Use folded or normal view
	if (folded) {
		this.debugwindow.document.write("<div onclick=\"if (this.className == 'folded"+extraStyle+"') this.className = 'unfolded"+extraStyle+"'; else this.className = 'folded"+extraStyle+"';\" class='folded"+extraStyle+"'>");
	} else {
		this.debugwindow.document.write("<div class='normal"+extraStyle+"'>");
	}

	// Print out as html or as preformatted	
	if (html) {
		this.debugwindow.document.write(message);
	} else {
		this.debugwindow.document.write("<xmp>"+message+"</xmp>");
	}
	this.debugwindow.document.write("</div>");
	
	// Scroll to end
	this.debugwindow.document.write("<script>window.scrollTo(0,document.body.scrollHeight);</script>");
}

/** Write object properties to debug window.
 *
 *  @param obj The object that is debugged.
 *  @param level The recursion level that the properties are inspected.
 *
 *  @author Oy IT Mill Ltd / Sami Ekblad
 *
 */
ITMillToolkitClient.prototype.debugObject = function (obj,level) {
	this.debug(this.printObject(obj,level),true,null,true);
}

/** Write error message to debug window.
 *
 *  @param message The message to be written
 *  @param causeException Exception that caused this error.
 *
 *  @author Oy IT Mill Ltd / Sami Ekblad
 *
 */
ITMillToolkitClient.prototype.error = function (message, causeException) {

	// Check if we are in debug mode
	if (!this.debugEnabled)	{ return; }

	if (causeException != null) {
		// If filename and line number is avaiable, append to output.
		if (causeException.fileName != null) {
			message += "\n\n  FILE= '" 
				+ causeException.fileName +"'"
				+ (causeException.lineNumber != null ? 
					" LINE="+causeException.lineNumber: 
					"");				
		}
		
		// If stack trace is available, append it to output.
		if (causeException.stack != null) {
			message += "\n\n" + causeException.stack;
		}

		// Dump all exception properties
		message += "\n\nException properties:\n";
		for (var prop in causeException) {
			if (prop != "stack") {
				message += "  " + prop + "=" +causeException[prop] + "\n";
			}
		}
	}
	this.debug(message,causeException != null, "error");
	
}

/** Creates new XMLHttpRequest object.
 *
 *  NOTE: The return type of this function is platform dependent.
 *
 *  @return New XMLHttpRequest or XMLHTTP (ActiveXObject) instance
 *  @type XMLHttpRequest | ActiveXObject
 *
 *  @author Oy IT Mill Ltd / Sami Ekblad
 */
ITMillToolkitClient.prototype.getXMLHttpRequest = function () {

	var req = false;
  	
	if(window.XMLHttpRequest) {
	
		// Native XMLHttpRequest object
		try {
			req = new XMLHttpRequest();
		} catch(e) {
			req = false;
		}
		    
    } else if(window.ActiveXObject) {
    	
    	// IE/Windows ActiveX version
		try {
			req = new ActiveXObject("Msxml2.XMLHTTP");
		} catch(e) {
			try {
				req = new ActiveXObject("Microsoft.XMLHTTP");
			} catch(e) {
				req = false;
			}
		}
	}
	return req;
}

/** Loads a document using XMLHttpRequest object and returns it as text.
 *
 *  @param url The URL of document.		
 *  @skipCache If true, does not use cached documents (or cache this result).
 *	
 *  @author Oy IT Mill Ltd / Sami Ekblad
 * 
 */
ITMillToolkitClient.prototype.loadDocument = function (url,skipCache) {

	if (!skipCache) {
		if (!this.loadcache) this.loadcache = new Object();
		var cached = this.loadcache["_"+url];
		if (cached != null) {
			this.debug(url + " loaded from cache.");
			return cached;
		}
	}
	
	var x = this.getXMLHttpRequest();
	x.open("GET",url, false);
	x.send(null);
	var response = x.responseText; 
	if (x.status != 200) {
		this.error("Could not load (status 200) " + url);
		return null;
	}
	delete x;
	
	if (!skipCache) {
		this.loadcache["_"+url] = response;
	}
	
	if (response) {
		this.debug(url + " loaded.");
	} else {
		this.debug("Could not load " + url);
	}
	return response;
}


/** Registers new renderer function to ajax client.
 *	
 *  @param theme Theme instance where the renderer belongs to.
 *  @param tag UIDL Tag-name that this renderer supports.
 *  @param componentStyle The style attribute of component that this renderer supports.
 *  @param renderFunction Function that is performs the rendering of the UIDL.
 *	@return Newly created renderer object instance.
 *  @type Object
 *
 *  @author Oy IT Mill Ltd / Sami Ekblad
 * 
 */
ITMillToolkitClient.prototype.registerRenderer = function (theme, tag, componentStyle, renderFunction) {

	if (renderFunction == null) {
		alert("Theme error: Invalid renderer function registered for '"+tag+(componentStyle == null ? "" : "." + componentStyle)+"'");
	}

	// Find previous (parent) renderer
	var parentRenderer = this.findRenderer(tag,componentStyle);
	
	// Create new renderer information object
	var renderer = new Object; 
	renderer.match = tag + (componentStyle == null ? "" : "__" + componentStyle);
	renderer.doc = document;
	renderer.client = this;
	renderer.theme = theme;
	renderer.tag = tag;
	renderer.componentStyle = componentStyle;
	renderer.renderFunction = renderFunction;
	renderer.parentRenderer = parentRenderer;
	
	// This replaces the previous (parent) renderer
	this.renderers[renderer.match] = renderer;

	// We return the created renderer object
	this.debug("Registered renderer for "+tag +(componentStyle == null ? "" : " (" + componentStyle+")")+"");
	return renderer;

}


/** Unregisters all renderers in client.
 *
 *  @author Oy IT Mill Ltd / Sami Ekblad
 * 
 */
ITMillToolkitClient.prototype.unregisterAllRenderers = function () {

	// We just create new, empty rederer map.
	this.renderers = new Object();

}

/** Create new response listener for the HTTPRequest object.
 *  This creates new function reference that is used to
 *  process the server response in httpRequest.onreadystatechange.
 *
 *  @param client Reference to this client instance.
 *
 *  @author Oy IT Mill Ltd / Sami Ekblad
 * 
 */
ITMillToolkitClient.prototype.createRequestChangeListener = function(client, req) {
	
	return (function()  {
		if (req.readyState != 4 || req.status == null) {
			return;
		}
		
		// Check status code
		if (req.status != 200) {
			alert("Server request failed: ("+req.status+", "+req.statusText+")");
			req.onreadystatechange = new Function();
			delete req;
			return;
		}
		
		// Get updates
		var updates = req.responseXML;
		if (updates == null) {
			alert("Server did not return anything: ");
			req.onreadystatechange = new Function();
			delete req;
			return;
		}
		
			// Debug request load time
		if (client.debugEnabled) {
			var loadedTime = (new Date()).getTime();
			client.debug("UIDL loaded in " + (loadedTime-client.requestStartTime) + "ms");
			client.debug("UIDL Changes: \n"+req.responseText,true);
		}
		
		// Clean up	
		client.variableStates = new Object();
		req.onreadystatechange = new Function();
		delete req;
		
		// Process the updates
		try {
			if (updates.normalize) updates.normalize();
		} catch (e) {
			if (client.debugEnabled) {
				client.debug("normalize() FAILED");
			}
		}
		client.processUpdates(updates);	
		client.requestStartTime = -1;	
	
	});
	
}

/** Send pending variable changes to server.
 *
 *  This function sends all pending (non-immediate) variable changes to the 
 *  server and registers callback to render process the server response.
 *
 *  @param repaintAll True if full window UIDL should be requested from server.
 *  @param nowait True if the wait-window should not be shown
 *
 *  @author Oy IT Mill Ltd / Sami Ekblad
 *
 */
ITMillToolkitClient.prototype.processVariableChanges = function (repaintAll,nowait) {
	
	if (this.waitElement&&!nowait) {
		this.waitElement.style.display = "inline";
	}
	
	// Request start time
	this.requestStartTime = (new Date()).getTime();
	
	// Build variable change query string
	var changes = "";
	for (var i in this.variableStates) {
		changes  += i + "=" + this.variableStates[i] + "&";
	}
	
	// Build up request URL
    var url = this.ajaxAdapterServletUrl + (repaintAll ? "?repaintAll=1" : "?") + "&requestid=" +this.requestStartTime;
    
     // Run the HTTP request
	this.debug("Send variable changes: " + url);
	var activeRequest = this.getXMLHttpRequest();
    // Create callback for request state changes
    var changeListener = this.createRequestChangeListener(this,activeRequest);  
	activeRequest.onreadystatechange = changeListener;
	activeRequest.open("POST",url, true);
	activeRequest.setRequestHeader('Content-Type', 
		     'application/x-www-form-urlencoded')
	activeRequest.send(changes);	
	
}

/** Get first child element in given parent.
 *
 *  @param parent The parent element
 *
 *  @author Oy IT Mill Ltd / Sami Ekblad
 *
 */
ITMillToolkitClient.prototype.getFirstChildElement = function (parent) {
	/*
	if (parent == null || parent.childNodes == null) {
		return null;
	}
	for (var j=0; j<parent.childNodes.length; j++) {  			
		var n = parent.childNodes.item(j);
		if (n.nodeType == Node.ELEMENT_NODE) {
			return n;
		}
	}
	*/
	try {
		var child = parent.firstChild;
		while (child) {
			if (child.nodeType == Node.ELEMENT_NODE) {
				return child;
			}
			child = child.nextSibling;
		}
	} catch (e) {
	}
	
	return null;
	
	
}


/** Initializes new window.
 *  Creates a document element and initializes it to
 *  to contain a window component.
 *
 *  @param win The window to be initialized.
 *  @param name IT Mill Toolkit name of the window to be initialized.
 *  @return reference to div in document that should contain the window.
 *  @author Oy IT Mill Ltd / Sami Ekblad
 *
 */
ITMillToolkitClient.prototype.initializeNewWindow = function (win,uidl,theme) {

	if (win == null) {
		return null;
	}
	
	// Special handling for framewindows
	var framewindow = uidl.nodeName == "framewindow";	
	var name = uidl.getAttribute("name");	
	var caption = uidl.getAttribute("caption")||"";	

	if (this.debugEnabled) {
			this.debug("Initializing new "+(framewindow?"frame-":"")+"window '"+name+"' (PID="+uidl.getAttribute("id")+")");
	}    

	// Create HTML content
	var html="";
	if (framewindow) {
		html = this.createFramesetHtml(uidl,theme)
	} else {
		html = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><HTML><HEAD id=\"html-head\"><TITLE>"+caption+"</TITLE></HEAD>"+"<BODY STYLE=\" overflow: hidden; border: none; margin: 0px; padding: 0px;\"><div id=\"itmtk-window\" class=\"window\"></div><\/BODY><\/HTML>";			
	}
    win.document.open();
    win.document.write(html);
    win.document.close();
    win.document.ownerWindow = win;
    win.document.renderUIDL = function(uidl,currentNode) {
    	this.client.renderUIDL(uidl,currentNode);
    }
    win.document.client = this;
    with (this) {
		addEventListener(win,"unload", function () {
			try {
				removeAllEventListeners(win.document);
				removeAllEventListeners(win);
				unregisterAllLayoutFunctions(win.document);
			} catch (e) {
				// IGNORED
			}
		});
		var client = this;
		addEventListener(win,"resize", function () {			
			try {
				setTimeout(function() {client.processAllLayoutFunctions()},1);
			} catch (e) {
				// IGNORED
			}
		});
		
	}
    // Add stylesheets
    if (!framewindow) {
		for (var si in this.mainDocument.styleSheets) {	
			var ss = this.mainDocument.styleSheets[si];	
			var nss = win.document.createElement('link');
			nss.rel = 'stylesheet';
			nss.type = 'text/css';
			nss.media = ss.media;
			nss.href = ss.href;
			win.document.getElementById('html-head').appendChild(nss);
		}
	}
		
    // Register it to client
	this.registerWindow(name, win, win.document);
	
	// Add unregister callback
	var client = this;
	win.onunload = function() { 
		client.unregisterWindow(name); 
		win.onunload = null;
	}
	
	// Ensure the name
	win.itmtkWindowName = name;
	
	// Assign the current node into that window
	var winElement = win.document.getElementById("itmtk-window");
	if (framewindow) {
		winElement = win.document.getElementById(uidl.getAttribute("id"));
	}	
	if (winElement == null && this.debugEnabled) {
			this.warn("Window element not found!");
	}
	win.document.itmtkWindowElement = winElement;
	
	
	if (!win.png) {
		var clientRoot = this.clientRoot;
		// PNG loading support in IE
		win.png = function(img) {
                var ua = navigator.userAgent.toLowerCase();
                if (ua.indexOf("windows")<0) return;
                var msie = ua.indexOf("msie");
                if (msie < 0) return;
                var v = parseInt(ua.substring(msie+5,msie+6));
                if (!v || v < 5 || v > 6) return;
                
                var src = img.src;
                var w = img.width;
                var h = img.height;
                
                if (src && src.indexOf(clientRoot+"pixel.gif")>0) return;
                
                img.onload = null;
                img.src = clientRoot + "pixel.gif";
                img.style.height = h+"px";
                img.style.width = w+"px";
                img.style.filter = "progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+src+"', sizingMethod='crop');";               
        }
	}
	
	// Return the content element
	return winElement;
}

/** Recursively create frameset html for FrameWindow initialization.
 *
 *  @param win The window to be initialized.
 *  @param name IT Mill Toolkit name of the window to be initialized.
 *  @return reference to div in document that should contain the window.
 *  @author Oy IT Mill Ltd / Sami Ekblad
 *
 */
ITMillToolkitClient.prototype.createFramesetHtml = function(uidl,theme) {

	if (uidl == null) {
		return "";
	}
	var cols = uidl.getAttribute("cols");
	var rows = uidl.getAttribute("rows");
	var caption = uidl.getAttribute("caption")||"";

	// Open frameset	
	var html = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html><head><title>"+caption+"</title></head><frameset ";
	if (cols) {
		html += "cols=\""+cols+"\"";
	} else if (rows) {
		html += "rows=\""+rows+"\"";
	}
	html += " id=\""+uidl.getAttribute("id")+"\"";
	html += " >";

	// Sub-frames / -framesets
    for (var i=0; i<uidl.childNodes.length; i++) {
		var n = uidl.childNodes.item(i); 
		if (n.nodeType == Node.ELEMENT_NODE) {
			if (n.nodeName == "frameset") {
				html += this.createFramesetHtml(n);
			} else if (n.nodeName == "frame") {
				var name =n.getAttribute("name");
				var src = n.getAttribute("src");
				html += "<frame id=\""+name+"\" name=\""+name+"\"";
				
				if (src && src.indexOf("theme://")==0) {
					src = (theme?theme.root:"themes/") + src.substring(8);
					html += " src=\""+src+"\" ";
				}
				
				html += "/>";
			}
		}
	}
	
	// Close frameset	
	html += "</frameset></html>";
	return html;
}

/** Unregisters and closes a window.
 
 *  @param windowName Name of the window
 *
 *  @author Oy IT Mill Ltd / Sami Ekblad
 *
 */
ITMillToolkitClient.prototype.unregisterWindow = function (windowName) {
	if (this.debugEnabled) {
		this.debug("Unregistering window '"+windowName+"'");
	}
	
	var doc = this.documents[windowName];
	var win = this.windows[windowName];
	
	if (doc) {
		this.documents[windowName] = null;
		try {
			//if (win.location) win.location.href = "about:blank";
			this.windows[windowName] = null;
			win.close();
			doc.ownerWindow = null;
		} catch (e) {
			if (this.debugEnabled) {
				this.error("Exception when closing window '"+windowName+"'. Continuing...",e);
			}
		}
	} else if (this.debugEnabled) {
		this.debug("Failed to unregister '"+windowName+"'. Window not found.");
	}
}

/** Registers new window .
 *  This enabled to client update the components by id in this window.
 
 *  @param windowName Name of the window
 *  @param doc The document element of window.
 *
 *  @author Oy IT Mill Ltd / Sami Ekblad
 *
 */
ITMillToolkitClient.prototype.registerWindow = function (windowName,win,doc) {
	if (win != null && doc != null && windowName != null) {	
		doc.itmtkWindowName = windowName;
		this.documents[windowName] = doc;
		this.windows[windowName] = win;
		if (this.debugEnabled) {
			this.debug("Registered new window '"+windowName+"'");
		}
	}
}

/** Find a paintable by id.
 *  Searcher all windows for given id and returns the element
 *  or null if not found.
 
 *  @param paintableId Id to look for.
 *
 *  @author Oy IT Mill Ltd / Sami Ekblad
 *
 */
ITMillToolkitClient.prototype.findPaintableById = function (paintableId) {

	if (this.documents == null) {
		return null;
	}
	for (var name in this.documents) {  			
		var d = this.documents[name];
		var win = this.windows[name];
		
		try {
			if (win && win.location && this.itmtkMainWindow.location.href != win.location.href) {
				// referencing external url, we cannot access, but no problem:
				// it can't contain the paitable either.
				if (this.debugEnabled) {
					this.debug("Window: '"+name+"' referencing external URL and can NOT contain Paintable '"+paintableId+"'");
				}	
				continue;
			}
		} catch (e) {
			this.debug("Exception while examining window.location, assuming ext url.");
			continue;
		}

		try {
			if (d != null && win && !win.closed) {
				var el  = d.getElementById(paintableId);
				if (el != null) {
					if (this.debugEnabled) {
						var isMain = el.ownerDocument == this.mainDocument? "main":" child";
						this.debug("Paintable '"+paintableId+"' found in "+isMain+"-window: '"+d.itmtkWindowName+"'");
					}			
					return el;
				} else {
					if (this.debugEnabled) {
						this.debug("Window: '"+d.itmtkWindowName+"' does NOT contain Paintable '"+paintableId+"'");
					}			
				}
			}
		} catch (e) {			
			if (this.debugEnabled) {
				this.error("Exception when accessing window '"+name+"'. Closing and continuing...",e);
			}
			this.unregisterWindow(name);
		}			
	}
	if (this.debugEnabled) {
		this.debug("Paintable '"+paintableId+"' NOT found in ANY of current windows.");
	}	
	return null;
}

/** Process UIDL updates from server.
 *
 *  Renders user interface changes. The registered renderers
 *  are then used to render the changes to correct location.
 *
 *  @param updates Updates UIDL updates from server.
 *
 *  @author Oy IT Mill Ltd / Sami Ekblad
 *
 */
ITMillToolkitClient.prototype.processUpdates = function (updates) {
	if (this.debugEnabled) {
		this.debug("Processing updates.");
	}

	try {
		// Iterate through the received changes
		var changes = updates.getElementsByTagName("change");
		var cLen = changes.length;		
		for (var i=0; i<cLen; i++) {
		
			// Render start time
			var renderStartTime = (new Date()).getTime();
			var change = changes.item(i);
			var paintableId = change.getAttribute("pid");
			var windowName = change.getAttribute("windowname");
			var invisible = (change.getAttribute("visible") == "false");
			var changeContent = this.getFirstChildElement(change);
			invisible = invisible || (changeContent && changeContent.getAttribute("invisible") == "true");
			var paintableName = (changeContent!= null?changeContent.nodeName:"(unknown)");
	
			if (this.debugEnabled) {
				this.debug(" ");
				this.debug("Change "+i+" Id='"+paintableId+"' Paintable='"+paintableName+"'");
			}
					
			// Get the containing element from all current windows
			var currentNode = this.findPaintableById(paintableId);
			
			// Use window element by default for windows
			if (currentNode == null && changeContent != null) {
			
				if (changeContent.nodeName == "window" || changeContent.nodeName == "framewindow") {
					var winName = changeContent.getAttribute("name");				
					
					if (this.mainDocument == null) {
						// Initialize the main document/window 
						currentNode = this.mainWindowElement;
						currentNode.ownerDocument.ownerWindow = window;
						this.registerWindow(winName, window, currentNode.ownerDocument);
						this.mainDocument = currentNode.ownerDocument;
						this.mainDocument.isMainDocument = "true";
					} else {						
						// Open a new window if no document was found						
						var limit = new Date().getTime() + (1000*3);
						var win = window.open("about:blank",winName);
						while (new Date().getTime() < limit) {
							try  {
								var url = win.location.href;
								break;
							} catch (e) {
								// IE slow sometimes, buzy-loop for permission ( TODO better solution? )
								this.debug("Permission denied for window "+winName+", retrying.");
							} 
						}
						try {
							var url = win.location.href;
						} catch (e) {
							alert("Could not open window.");
							win = window.open("about:blank",winName);
						}
						
						currentNode  = this.initializeNewWindow(win,changeContent);			
					}
				}
			}
			
			if (currentNode != null) {
			
				if (invisible) {
					// Special hiding procesedure for windows
					if (windowName != null) {
						this.unregisterWindow(windowName);					
					} else {
						// Hide invisble components
						currentNode.style.display = "none";
					}
					
				} else {
					// Make sure we are visible
					if (currentNode.style) currentNode.style.display = "";
				}
	
									
				// Process all uidl nodes inside a change
				var uidl = change.firstChild;
				while (uidl) {
					if (uidl.nodeType == Node.ELEMENT_NODE) {
						if (!currentNode) {
							currentNode = this.createPaintableElement(uidl);
						}
						this.warn("Removed " + this.removeAllEventListeners(currentNode) + " event listeners.");
						this.warn("Removed " + this.unregisterAllLayoutFunctions(currentNode) + " layout functions.");
						if (currentNode.ownerDocument.renderUIDL) {
							currentNode.ownerDocument.renderUIDL(uidl,currentNode);
						} else {
							this.renderUIDL(uidl,currentNode);
						}
					}
					uidl = uidl.nextSibling;
				}
			} else {
				this.error("Change " + i +" node not found. Id='"+ paintableId+ "'. Paintable='"+ paintableName+"'");		
			}
			
			if (this.debugEnabled) {
				var renderEndTime = (new Date()).getTime();
				this.debug("Change " + i + " Id='"+ paintableId+ "'. Paintable='"+ paintableName +"' rendered in " + (renderEndTime-renderStartTime) + "ms");
			}
		}
	} catch (e) {
		// Print out the exception
		if (this.debugEnabled) {
        	this.error("Could not process changes: "+e.message,e);
 		} else {
			alert("Failed to process all changes. \n Please enable debug logging to get detailed error description");
		}
	}
	
	this.processAllLayoutFunctions();
	
    var endTime = (new Date()).getTime();
    if (this.debugEnabled && this.requestStartTime > 0 ) {
		this.debug("Total time for update " + (endTime-this.requestStartTime) + "ms");
	}	
	if (this.waitElement) {
		this.waitElement.style.display = "none";
	}

}

/** Render the given UIDL to target.
 *
 *  If no renderer is specified the the internal renderer registry is 
 *  looked up for matching renderer.
 *
 *  @param uidl The UIDL node that is rendered.
 *  @param target The targer element where the result should be appended.
 *  @param renderer The specific renderer instance that should be used (optional)
 *  @return This function returns whatever the utilized renderer returns.
 *
 *  @author Oy IT Mill Ltd / Sami Ekblad
 *
 */
ITMillToolkitClient.prototype.renderUIDL = function (uidl, target, renderer) {

	// Sanity check
	if (uidl == null || uidl.nodeType != Node.ELEMENT_NODE) return;

	// Render the UIDL using the given renderer  
	if (renderer != null) {
	
		// Invoke renderer and return whatever it returns.
		
		// Function argument pass-through: 
		// Create arguments array and add all callers extra parameters 
		// to the end of arguments.
		var args = new Array();
		args[args.length] = renderer;
		args[args.length] = uidl;
		args[args.length] = target;
		for(var i=3; i<arguments.length; i++) {
            args[args.length] = arguments[i];
        }
        if (this.debugEnabled) {
	        this.debug("Theme '"+ renderer.theme.themeName + "' rendering '"+ uidl.nodeName + "' into '"+target.nodeName+"' (id="+target.id+")");
	    }
        try {      
			var res = renderer.renderFunction.apply(this,args);
			return res;
		} catch (e) {
			// Print out the exception
        	this.error("Could not render "+ uidl.nodeName +" using '"+ renderer.theme.themeName + "': "+e.message,e);
		}
		
		
	} else {
	
		// Lookup for renderer
		var style = uidl.getAttribute("style");
		var tag = uidl.nodeName;
		var renderer = this.findRenderer(tag,style);

		// Render the UIDL using the found renderer  
		if (renderer != null) {
			
			// Function argument pass-through: 
			// Create arguments array and add all callers extra parameters 
			// to the end of arguments.
			var args = new Array();
			args[args.length] = uidl;
			args[args.length] = target;
			args[args.length] = renderer;
			for(var i=3; i<arguments.length; i++) {
	            args[args.length] = arguments[i];
	        }
			return this.renderUIDL.apply(this,args);
		}
	}

	// If no renderer is specified, render the UIDL as-is.
	return this.renderHTML(uidl, target);  
}

/** Search the internal renderer registry for matching renderer. 
 *
 *  The matching process first looks up for exact tag and componentStyle
 *  match, but if no renderer is found it uses only the tag name matching.
 *  If still no renderer is found returns null.
 *
 *  @param tag UIDL tag name.
 *  @param componentStyle The style attribute of the component (optional)
 *  @return A matching renderer instance
 *  @type Object
 *	
 *  @author Oy IT Mill Ltd / Sami Ekblad
 * 
 */
ITMillToolkitClient.prototype.findRenderer = function (tag, componentStyle) {
	var renderer = null;
	var rendererId = tag + (componentStyle == null ? "" : "__" + componentStyle);

	// Try to find with specific style
	if (componentStyle != null) {
        renderer = this.renderers[rendererId];
	}
	
	// Try to find a renderer only using tag
	if (renderer == null) {
		renderer = this.renderers[tag];
	}
	
	return renderer;
}


/** Renders given XML as redable HTML.
 *
 *  @param xml The XML node to be rendered as readable HTML.
 *  @param target The node where the result should be appended as child.
 *  @return The rendered element
 *  @type Node
 *	
 *  @author Oy IT Mill Ltd / Sami Ekblad
 * 
 */
ITMillToolkitClient.prototype.renderHTML = function (xml, target) {

	var n = this.createElement("div",target);
	target.appendChild(n);
	n.setAttribute("style", "padding-left:10px;background-color:white;z-index:9999;position:relative;");
	var tn = this.createTextNode("<" + xml.nodeName, target);
	n.appendChild(tn);	
	if (xml.attributes.length > 0)
		for(var i=0; i<xml.attributes.length; i++) {
		var a = xml.attributes.item(i);
		n.appendChild(this.createTextNode(" " + a.name + "=\"" + a.value+"\"",target));
	}
	n.appendChild(this.createTextNode(">",target));
	if (xml.hasChildNodes())
	for (var i=0; i<xml.childNodes.length; i++) { 
		var c = xml.childNodes.item(i);
		if (c.nodeType == Node.ELEMENT_NODE) {
			this.renderHTML(c,n);
		} else if (c.nodeType == Node.TEXT_NODE && c.data != null) {
			n.appendChild(this.createTextNode(c.data, target));
		}
	}
	n.appendChild(this.createTextNode("</"+xml.nodeName+">",target));  
  return n;
}
/** Returns given XML as text.
 *
 *  @param xml The XML node to be rendered as HTML.
 *  @return The XML as text
 *  @type String
 *	
 *  @author Oy IT Mill Ltd / Sami Ekblad
 * 
 */
ITMillToolkitClient.prototype.getXMLtext = function(xml) {
	var n = "";
	n += "<" + xml.nodeName;
	if (xml.attributes.length > 0)
		for(var i=0; i<xml.attributes.length; i++) {
		var a = xml.attributes.item(i);
		n += " " + a.name + "=\"" + a.value+"\"";
	}
	n += ">";
	if (xml.hasChildNodes())
	for (var i=0; i<xml.childNodes.length; i++) { 
		var c = xml.childNodes.item(i);
		if (c.nodeType == Node.ELEMENT_NODE) {
			n += this.getXMLtext(c);
		} else if (c.nodeType == Node.TEXT_NODE && c.data != null) {
			n += c.data;
		}
	}
	n += "</"+xml.nodeName+">";  
  return n;
}

/** Send a change variable event to server.
 *
 *	Changes a variable value and if 'immediate' is true invokes the
 *	processVariableChanges function.
 *
 *  @param name The name of the variable to change.
 *  @param value New value of the variable.
 *  @param immediate True if the variable change should immediately propagate to server.
 *  @param nowait True if the wait-window should not be shown
 * 
 *  @author Oy IT Mill Ltd / Sami Ekblad
 * 
 */
ITMillToolkitClient.prototype.changeVariable = function (name, value, immediate, nowait) {
	this.debug("variableChange('" + name + "', '" + value + "', " + immediate + ");");

	this.variableStates[name] = escape(value);

	if (immediate) 
		this.processVariableChanges(false,nowait);
}

/** Create new containing element for a paintable (component).
 *
 *  This function creates new containing element for a single 
 *  paintable object, typically component.
 *
 *  @param uidl The UIDL node of the paintable.
 *  @param target The target node where the new containing element should be 
 *			appended to as child.
 *  @return The newly created element node.
 *  @type Node
 *
 *  @author Oy IT Mill Ltd / Sami Ekblad
 * 
 */
ITMillToolkitClient.prototype.createPaintableElement = function (uidl, target) {

	// Create DIV as container to right document.
	var div = this.createElement("div", target);

	// Append to parent, if 'target' parameter specified
	if (target != null) {
		target.appendChild(div);
	}
	
	// Add ID attribute	
	var pid = uidl.getAttribute("id");
	if (target != null && pid != null) {
		div.setAttribute("id",pid);
	}
	
	// Set visibility
	var invisible = uidl.getAttribute("invisible");
	if (target != null && invisible == "true") {
		div.style.display = "none";
	} else {
		div.style.display = "";
	}
	
	// Return reference to newly created div
	return div;	
}


/** Assigns given CSS class to element.
 *  Cross-browser function for assigning CSS class attribute to
 *  an element.
 *  @param element The element where the class should be applied.
 *  @param className The CSS class name to apply.
 *  @return element.
 *  @type Node
 * 
 *  @author Oy IT Mill Ltd / Sami Ekblad
 *
 */
ITMillToolkitClient.prototype.setElementClassName = function(element,className) {
	if (element == null) { return; }
		element.style.className = className;
	
}

/**
 *   Add event listener function to an element.
 *  
 *   @param element      The element
 *   @param type         Type of event to listen for [click|mouseover|mouseout|...]
 *   @param func         The function to call. Called with single parameter: event.
 *  
 *   @return the listener function added
 */
ITMillToolkitClient.prototype.addEventListener = function(element,type,func) {
	if (element.addEventListener) {
		element.addEventListener(type, func, false);
		
	} else if (element.attachEvent) {
		element.attachEvent("on" + type, func);
		
	} else {
		element['on'+type] =  func;
	}
		
	//  TODO add only to paintable?
		
	if (!element.eventMap) element.eventMap = new Object();
	if (!element.eventMap[type]) element.eventMap[type] = new Array();
	element.eventMap[type][element.eventMap[type].length] = func;
	
	return func;
}
/**
 *   Remove event listener function from a element. The parameters should match addEventListener()
 *  
 *   @param element      The element
 *   @param type         Type of event to listen for [click|mouseover|mouseout|...]
 *   @param func         The listener function to remove.
 *  
 */
ITMillToolkitClient.prototype.removeEventListener = function(element,type,func) {
	if (element.removeEventListener) {
		element.removeEventListener(type, func, false);
		
	} else if (element.detachEvent) {
		element.detachEvent("on" + type, func);
		
	} else {
		element['on'+type] =  null;
	}
	if (element.eventMap && element.eventMap[type]) {
		for (var f in element.eventMap[type]) {
			if (element.eventMap[type][f]==func) {
				element.eventMap[type][f] = null;
				break;
			}
		}
	}
}

/**
 *   Remove all event listener functions from a element.
 *  
 *   @param element      The element
 *   @param type         Type of event to listen for [click|mouseover|mouseout|...]
 *   @param func         The listener function to remove.
 *  
 */
 ITMillToolkitClient.prototype.removeAllEventListeners = function(element) {
 	var removed = 0;
	if (element.eventMap) {
		for (var t in element.eventMap) {
			var i = element.eventMap[t].length;
			while (i--) {
				this.removeEventListener(element,t,element.eventMap[t][i]);
				removed++;
			}
		}
		
		element.eventMap = null;
	}
	// TODO eventMAp -> paintable & only get DIV:s
	var childs = element.getElementsByTagName("*");
	if (childs) {
		var i = childs.length;
		while (i--) {
			element = childs[i];
			if (element.eventMap) {
				for (var t in element.eventMap) {
					var j = element.eventMap[t].length;
					while (j--) {
						this.removeEventListener(element,t,element.eventMap[t][j]);
						removed++;
					}
				}
				
				element.eventMap = null;
			}
		}
	}
	
	return removed;
}

ITMillToolkitClient.prototype.registerLayoutFunction = function (paintableElement,func) {
	if (!paintableElement || !func) {
		this.error("Invalid layout function registration; paintableElement:"+paintableElement+" func:"+func);
		return;
	}
	
	var pid = paintableElement.id;
	if (!pid) {
		this.error("Register layout function; paintableElement pid not found!" + paintableElement);
		return;
	}
	
	if (!this.layoutFunctionsOrder) {
		this.layoutFunctionsOrder = new Object();
		this.layoutFunctions = new Array();
	}
		
	var idx = this.layoutFunctionsOrder[pid];
	if (typeof(idx) == "undefined") idx = this.layoutFunctions.length;
	
	this.layoutFunctionsOrder[pid] = idx;
	this.layoutFunctions[idx] = func;

	this.debug("Registered layout function for ("+paintableElement.nodeName+") pid " + pid + " as number " + idx);
}
ITMillToolkitClient.prototype.unregisterLayoutFunction = function (paintableElement) {
	if (!paintableElement) {
		this.error("unregisterLayoutFunction(): NULL paintableElement!");
		return false;
	}
	
	if (!this.layoutFunctionsOrder) {
		// no functions at all
		return false;
	}
	
	var pid = paintableElement.id;
	if (!pid) {
		this.error("unregisterLayoutFunction(): paintableElement pid not found!" + paintableElement);
		return false;
	}
	
	var idx = this.layoutFunctionsOrder[pid];
	if (typeof(idx) == "undefined") {
		// no registered function
		return false;
	}
	
	this.layoutFunctions[idx] = null;
	delete this.layoutFunctionsOrder[pid];

	this.debug("Unregistered layout function " + pid);

	return true;
}
ITMillToolkitClient.prototype.unregisterAllLayoutFunctions = function (paintableElement) {
	var removed = 0;
	if (!paintableElement) {
		removed = (this.layoutFunctions?this.layoutFunctions.length:0);
		this.layoutFunctions = null;
		this.layoutFunctionsOrder = null;
		this.debug("Unregistered ALL layout functions!");
		return removed;
	}
	
	
	
	if (paintableElement.id) {
		if (this.unregisterLayoutFunction(paintableElement)) removed++
	}
	var cn = paintableElement.getElementsByTagName("div");
	if (cn) {
		var len = cn.length;
		for (var i=0;i<len;i++) {
			if (cn[i].id) {
				if (this.unregisterLayoutFunction(cn[i])) removed++
			}
		}
	}
	
	return removed;
}
ITMillToolkitClient.prototype.processAllLayoutFunctions = function() {
	if (this.layoutFunctions) {
		this.debug("Processing layout functions...");
		var lf = this.layoutFunctions;
		var lfo = this.layoutFunctionsOrder;
		var cnt = 0;
		for (var pid in lfo) {
			var idx = lfo[pid];
			var func = lf[idx];
			try {
				func();
				cnt++;
			} catch (e) {
				this.error("Layout function "+pid+" failed; "+ e + " Removing.");
				delete this.layoutFunctionsOrder[pid];
				this.layoutFunctions[idx] = null;
			}
		}
		this.debug("...processed " + cnt + " successfully");
	}
}


/** Returns a cross-browser object with useful event properties.
 * e: 					the ('raw') event  
 * type:				event type
 * target:				the target element
 * targetX:				X-position of the target element
 * targetY:				Y-position of the target element
 * key:					pressed key character
 * alt:					true if ALT -key was held
 * shift:				true if SHIFT -key was held
 * ctrl:				true if CTRL -key was held
 * rightclick:			true if the right mousebutton was clicked, or ctrl held while clicking
 * mouseX:				X-position of the mouse
 * mouseY:				Y-position of the mouse
 *
 *  @param e			The event, null for window.event (IE)
 *
 *	@return Properties object.  
 */
ITMillToolkitClient.prototype.getEvent = function(e) {
	var props = new Object()

	if (!e) var e = window.event;
	props.e = e;
	props.type = e.type;
	
	var targ;	
	if (e.target) { 
		targ = e.target;
	} else if (e.srcElement) { 
		targ = e.srcElement;
	}
	if (targ.nodeType == 3) {
		targ = targ.parentNode;
	}
	props.target = targ;
	var p = this.getElementPosition(targ);
	props.targetX = p.x;
	props.targetY = p.y;
	
	var code;
	if (e.keyCode) {
	 code = e.keyCode;
	} else if (e.which) {
		code = e.which;
	}
	if (code) {
		props.key = String.fromCharCode(code);
	}
	
	props.alt = e.altKey;
	props.ctrl = e.ctrlKey;
	props.shift = e.shiftKey;
	
	var rightclick;
	if (e.which) {
		rightclick = (e.which == 3 || (props.ctrl));
	} else if (e.button) {
		rightclick = (e.button == 2|| (props.ctrl));
	}
	props.rightclick = rightclick;
	
	props.mouseX = e.pageX||e.clientX;
	props.mouseY = e.pageY||e.clientY;
	
	props.stop = function() {
		e.cancelBubble = true;
		if (e.stopPropagation) e.stopPropagation();
		if (e.preventDefault) e.preventDefault();
		return false;
	}
	
	return props;
}
ITMillToolkitClient.prototype.getElementPosition = function(element) {
	var props = new Object();
// TODO scroll offsets testing in IE
	var obj = element;
	var x = obj.offsetLeft + (obj.scrollLeft||0);
	var y = obj.offsetTop + (obj.scrollTop||0);
	if (obj.parentNode||obj.offsetParent) {
		while (obj.offsetParent||obj.parentNode) {
            obj = obj.offsetParent||obj.parentNode;
			if (obj.nodeName == "TBODY") continue;
			x += (obj.offsetLeft||0) - (obj.scrollLeft||0);
			y += (obj.offsetTop||0) - (obj.scrollTop||0);
		}
	} else if (obj.x) {
		x += obj.x;
		y += obj.y;
	}
	props.x = x;
	props.y = y;
	props.h = element.offsetHeight;
	props.w = element.offsetWidth;
	
	return props;
}

/** Prints objects properties into separate window.
 *
 *  @obj Object to be printed
 *  @level recursion level
 */
ITMillToolkitClient.prototype.debugObjectWindow = function(obj,level) {

	// Default level
	if (level == null) {
		level = 2;
	}
	
	//print into string
	var str = this.printObject(obj,level);
	
	// open a window for debug
	var win = window.open("", "ms_ajax_debug");
	win.document.open();
	win.document.write("<html><body>"+str+"+</body></html>");
	win.document.close();
}

/** Print a object instace as html string.
 *  Prints (recursively) the objects properties into a html table.
 *  
 *  @obj Object to be printed
 *  @level recursion level
 */
ITMillToolkitClient.prototype.printObject = function(obj,level) {
	if (level == null || level < 1) {
		level = 1;
	}

	var str = "<table border=\"0\"><tr><td colspan=\"3\">Object: "+obj+"<hr /></td></tr>";
	try {	
		for (var prop in obj) { 
			str += "<tr><td valign=\"top\">"+prop+"</td><td valign=\"top\"> = </td><td valign=\"top\">";
			try { 
				if (typeof obj[prop] == "object" && level > 1) {
					str += this.printObject(obj[prop],level-1);
				} else {
					str += obj[prop];
				}
			} catch(ignored) {
				str += "[EVAL FAILED]";
			} 
			str += "</td></tr>";
		}
	} catch (e) {
		str += "<tr><td colspan=\"3\">[Failed to list object properties: "+e.message+"]</td></tr>"
	}
	str += "</table>";
	return str;
}

/** Creates a text node to the same document as target.
 *  If target is null or not given the document reference is 
 *  used instead.
 *  
 *  @target Target element
 *  @text Textnode content
 */
ITMillToolkitClient.prototype.createTextNode = function(text, target) {
	if (target != null && target.ownerDocument != null) {
		return target.ownerDocument.createTextNode(text);
	} else {
		return document.createTextNode(text);
	}
}

/** Creates a element node to the same document as target.
 *  If target is null or not given the document reference is 
 *  used instead.
 *  
 *  @nodeName Element nodeName
 *  @text Textnode content
 */
ITMillToolkitClient.prototype.createElement = function(nodeName, target) {
	
	if (target != null && target.ownerDocument != null) {
		return target.ownerDocument.createElement(nodeName);
	} else {
		return document.createElement(nodeName);
	}
}
