// IE DOM Node support
if(document.all) {
	Node = new Object();
	Node.ELEMENT_NODE = 1; 
	Node.ATTRIBUTE_NODE = 2; 
	Node.TEXT_NODE = 3; 
	Node.CDATA_SECTION_NODE = 4; 
	Node.ENTITY_REFERENCE_NODE = 5; 
	Node.ENTITY_NODE = 6; 
	Node.PROCESSING_INSTRUCTION_NODE = 7; 
	Node.COMMENT_NODE = 8; 
	Node.DOCUMENT_NODE = 9; 
	Node.DOCUMENT_TYPE_NODE = 10; 
	Node.DOCUMENT_FRAGMENT_NODE = 11; 
	Node.NOTATION_NODE = 12;
}

/** Default theme constructor.
 *
 *  @param themeRoot The base URL for theme resources.
 *  @constructor
 *
 */
function DefaultTheme(themeRoot) {
	this.themeName = "DefaultTheme";

	// Store the the root URL
	this.root = themeRoot;
}

/** Register all renderers to a ajax client.
 *
 * @param client The ajax client instance.
 */
DefaultTheme.prototype.registerTo = function(client) {

	// This hides all integer, string, etc. variables
	client.registerRenderer(this,"integer",null,function() {});
	client.registerRenderer(this,"string",null,function() {});
	// and special tags
	client.registerRenderer(this,"description",null,function() {});
	client.registerRenderer(this,"error",null,function() {});
	client.registerRenderer(this,"actions",null,function() {});
		
	
	// Register renderer functions
	client.registerRenderer(this,"component",null,this.renderComponent);
	client.registerRenderer(this,"label",null,this.renderLabel);
	client.registerRenderer(this,"data",null,this.renderData);
	client.registerRenderer(this,"pre",null,this.renderData);
	client.registerRenderer(this,"link",null,this.renderLink);
	client.registerRenderer(this,"button",null,this.renderButton);
	client.registerRenderer(this,"textfield",null,this.renderTextField);
	client.registerRenderer(this,"datefield",null,this.renderDateField);
	client.registerRenderer(this,"datefield","calendar",this.renderDateFieldCalendar);
	client.registerRenderer(this,"select",null,this.renderSelect);
	client.registerRenderer(this,"select","optiongroup",this.renderSelectOptionGroup);
	client.registerRenderer(this,"upload",null,this.renderUpload);
	client.registerRenderer(this,"embedded",null,this.renderEmbedded);

	client.registerRenderer(this,"window",null,this.renderWindow);
	client.registerRenderer(this,"framewindow",null,this.renderFramewindow);
	client.registerRenderer(this,"open",null,this.renderOpen);
	
	client.registerRenderer(this,"panel",null,this.renderPanel);
	client.registerRenderer(this,"orderedlayout",null,this.renderOrderedLayout);
	client.registerRenderer(this,"customlayout",null,this.renderCustomLayout);
	client.registerRenderer(this,"gridlayout",null,this.renderGridLayout);
	client.registerRenderer(this,"tabsheet",null,this.renderTabSheet);

	client.registerRenderer(this,"table",null,this.renderScrollTable);
	client.registerRenderer(this,"tree",null,this.renderTree);
	client.registerRenderer(this,"tree","coolmenu",this.renderTreeMenu);
	//client.registerRenderer(this,"tree","menu",this.renderTreeMenu);
};

/* 
#### DOM functions ########################################################
*/

DefaultTheme.prototype.createElementTo = function (target, tagName, cssClass) {

	if (target == null) return null;

	// Create the requested element
	var e = target.ownerDocument.createElement(tagName);
	
	// Set CSS class if specified
	if (cssClass) {
		this.setCSSClass(e,cssClass);	
	}
	
	// Append to parent
	target.appendChild(e);
	
	return e;
}

DefaultTheme.prototype.createTextNodeTo = function (target,text) {

	// Sanity check
	if (text == null || target == null) return null;

	// Create DIV as container
	var tn = target.ownerDocument.createTextNode(text);

	// Append to parent
	target.appendChild(tn);
		
	return tn;
}

DefaultTheme.prototype.getFirstElement = function(parent, elementName) {
	if (parent && parent.childNodes) {
		for (var i=0;i<parent.childNodes.length;i++) {
			if (parent.childNodes[i].nodeName == elementName) {
				return parent.childNodes[i];
			}
		}
	}
	return null;
}

DefaultTheme.prototype.getFirstTextNode = function(parent) {
	if (parent == null || parent.childNodes == null) return null;
	
	var cns = parent.childNodes;
	var len = cns.length;
	for (var i=0; i<len; i++) {
		var child = cns[i];
		if (child.nodeType == Node.TEXT_NODE) {
			return child;
		}
	}
	
}

/**
 *   Removes all children of an element an element.
 *  
 *   @param element      Remove children of this element. 
 *  
 *   @return the element with children removed
 */
DefaultTheme.prototype.removeAllChildNodes = function(element) {
	//TODO event listener leakage prevention, verify
	// MOVED to client
	//this.removeAllEventListeners(element);
	while (element.childNodes&&element.childNodes.length > 0) {
		element.removeChild(element.childNodes[0]);
	}
	return element;
}

DefaultTheme.prototype.getElementContent = function(parent, elementName) {
	if (elementName != null) {
		// Find element and return its content		
		var n = this.getFirstElement(parent,elementName);
		if (n == null) return null;
		var tn = this.getFirstTextNode(n);
		if (tn != null && tn.data != null) {
			return tn.data;
		}
		return "";	
	} else {
		// If no element name is given return
		// content of parent
		var tn = this.getFirstTextNode(parent);
		if (tn != null && tn.data != null) {
			return tn.data;
		}
		return "";	
	}
}

DefaultTheme.prototype.getChildElements = function(parent, tagName) {
	
	if (parent == null || parent.childNodes == null || tagName == null) return null;

	// Iterate all child nodes
	var res = new Array();
	for (var i=0; i < parent.childNodes.length; i++) {
		var n = parent.childNodes[i];
		if (n.nodeType == Node.ELEMENT_NODE && n.nodeName == tagName) {
			res[res.length++] = n;
		}
	}
	return res;	
}
DefaultTheme.prototype.nodeToString = function(node, deep) {

	if (node == null) {
		return "";
	} else if (node.nodeType == Node.TEXT_NODE) {
		// Render text nodes.
		if (node.data) {
			return node.data;
		} else {
			return "";
		}
	
	} else if (node.nodeType == Node.ELEMENT_NODE) {	
		
		// Renderer element nodes.
		var txt = "<" + node.nodeName;
		if (node.attributes.length > 0)
			for(var i=0; i<node.attributes.length; i++) {
			var a = node.attributes.item(i);
			txt += " " + a.name + "=\"" + a.value+"\"";
		}
		if (deep && node.childNodes != null && node.childNodes.length >0) {
			txt += ">";
			for (var i=0; i<node.childNodes.length; i++) { 
				var c = node.childNodes.item(i);
				txt += this.nodeToString(c,deep);			
			}
			txt += "</"+node.nodeName+">";  
		} else {
			txt += "/>";  
		}
	  	return txt;
	  }
	  
	  return ""+node.nodeName + "-node";
}
DefaultTheme.prototype.createInputElementTo = function(target,type,className,focusid) {
	
	var input = null;
	if (document.all) {
		// IE only
		input = this.createElementTo(target,"<input type='"+type+"'>");
	} else {
		// Other browsers
		input = this.createElementTo(target,"input");
		input.type = type;
	}
	
	// Assign class
	if (className != null && className != "") {
		this.setCSSClass(input,className);
	}
	
	if (focusid) input.focusid = focusid;
	
	return input;
}



/* 
#### CSS functions ###################################################### 
*/

DefaultTheme.prototype.addCSSClass = function(element, className) {
	if (element == null) return element;
	if (element.className) {
		var classArray = element.className.split(" ");
		for (var i in classArray) {
			if (classArray[i]==className) {
				// allready in className
				return element;
			}
		} 
	}
	element.className = (element.className?element.className:"") + " " + className;
	return element;	
}

DefaultTheme.prototype.removeCSSClass = function(element, className) {
	if (element == null) return element;
	var classArray = new Array();
	if (element.className) {
		classArray = element.className.split(" ");
	}
	var newArray = new Array();
	for (var i in classArray) {
		if (classArray[i]!=className) {
			newArray[newArray.length] = classArray[i];
		}
	} 
	element.className = newArray.join(" ");
	return element;	
}
DefaultTheme.prototype.toggleCSSClass = function(element, className) {
	if (element == null) return element;

	var classArray = new Array();
	if (element.className) {
		classArray = element.className.split(" ");
	}
	for (var i=0;i<classArray.length;i++) {
		if (classArray[i]==className) {
			this.removeCSSClass(element, className);
			return;
		}
	}	
	this.addCSSClass(element, className);
	
	return element;	
}
DefaultTheme.prototype.setCSSClass = function(element, className) {
	if (element == null) return element;
	element.className = className;
	return element;	
}
DefaultTheme.prototype.setCSSDefaultClass = function(renderer,element,uidl) {
	if (element == null) return element;
	var cn = this.styleToCSSClass(renderer.tag,uidl.getAttribute("style"));
	element.className = cn;
	return element;	
}
DefaultTheme.prototype.styleToCSSClass = function(prefix,style) {

	var s = "";
	if (prefix != null) {
		s = prefix;
	}
  	if (style != null) {
  		if (s.length > 0) {
  			s = s + "-";
  		}
  		s = s + style;
  	}
  	return s
}

/* 
#### Generic JS helpers ##################################################
*/

/**
 *   Check if integer list contains a number.
 *  
 *   @param list         Comma separated list of integers
 *   @param number       Number to be tested
 *  
 *   @return true iff the number can be found in the list
 */

DefaultTheme.prototype.listContainsInt = function(list,number) {
  if (!list) return false;
  a = list.split(",");

  for (i=0;i<a.length;i++) {
    if (a[i] == number) return true;
  }
  
  return false;
}
/** Add number to integer list, if it does not exit before.
 *  
 *  
 *  @param list         Comma separated list of integers
 *  @param number       Number to be added
 *  
 *  @return new list
 */

DefaultTheme.prototype.listAddInt = function(list,number) {

  if (this.listContainsInt(list,number)) 
    return list;
    
  if (list == "") return number;
  else return list + "," + number;
}
/** Remove number from integer list.
 *  
 *  @param list         Comma separated list of integers
 *  @param number       Number to be removed
 *  
 *  @return new list
 */
DefaultTheme.prototype.listRemoveInt = function(list,number) {
	if (!list) return "";
	retval = "";
	a = list.split(',');

	for (i=0;i<a.length;i++) {
		if (a[i] != number) {
  			if (i == 0) retval += a[i];
  			else retval += "," + a[i];
    	}
  	}
	return retval;
}
/* 
#### Variable helpers #############################################
*/
DefaultTheme.prototype.getVariableElement = function(uidl,type,name) {

	if (uidl == null) return;
	
	var nodes = this.getChildElements(uidl,type);
	if (nodes != null) {
		for (var i=0; i < nodes.length; i++) {
			if (nodes[i].getAttribute("name") == name) {				
				return nodes[i];
			}
		}
	}
	return null;	
}
DefaultTheme.prototype.createVariableElementTo = function(target,variableElement) {
	if (!variableElement) {
		return null;
	}
	/* TODO FF kludge try, does not work - how to prevent flashing hiddens?
	var d = this.createElementTo(target,"div");
	d.style.border = "none";
	d.style.background = "none";
	d.style.padding = "0px";
	d.style.margin = "0px;"	
	d.style.width = "0px";
	d.style.height = "0px";
	d.style.overflow = "hidden";
	*/
	var input = this.createInputElementTo(target,"hidden");
	input.variableId = variableElement.getAttribute("id");
	input.variableName = variableElement.getAttribute("name");
	if (variableElement.nodeName == "array") {
	    input.variableId = "array:"+input.variableId;
		input.value = this.arrayToList(variableElement);
	} else if (variableElement.nodeName == "string") {
		var node = this.getFirstTextNode(variableElement);
		input.value = (node?node.data:"");
	} else {
		input.value = variableElement.getAttribute("value");
	}
	return input;
}
DefaultTheme.prototype.getVariableElementValue = function(variableElement) {
	if ( variableElement == null) {
		return null;
	}
	
	if (variableElement.nodeName == "array") {
		return this.arrayToList(variableElement);
	} else if (variableElement.nodeName == "string") {
		var node = this.getFirstTextNode(variableElement);
		return (node?node.data:"");
	} else {
		return variableElement.getAttribute("value");
	}
	return null;
}
DefaultTheme.prototype.setVariable = function(client, variableNode, newValue, immediate) {
	if (variableNode == null) return;
	variableNode.value = newValue;
	client.changeVariable(variableNode.variableId, newValue, immediate);
}
DefaultTheme.prototype.addArrayVariable = function(client, variableNode, newValue, immediate) {
	if (variableNode == null) return;
	variableNode.value = this.listAddInt(variableNode.value,newValue);
	client.changeVariable(variableNode.variableId, variableNode.value, immediate);
}
DefaultTheme.prototype.toggleArrayVariable = function(client, variableNode, value, immediate) {
	if (variableNode == null) return;
	if (this.listContainsInt(variableNode.value,value)) {
		variableNode.value = this.listRemoveInt(variableNode.value,value);
	} else {
		variableNode.value = this.listAddInt(variableNode.value,value);
	}
	client.changeVariable(variableNode.variableId, variableNode.value, immediate);
}
DefaultTheme.prototype.removeArrayVariable = function(client, variableNode, value, immediate) {
	if (variableNode == null) return;
	variableNode.value = this.listRemoveInt(variableNode.value,value);
	client.changeVariable(variableNode.variableId, variableNode.value, immediate);
}
DefaultTheme.prototype.arrayToList = function(arrayVariableElement) {

  var list = "";
  if (arrayVariableElement == null || arrayVariableElement.childNodes == null) return list;
  
  var items = arrayVariableElement.getElementsByTagName("ai");
  if (items == null) return list;
  
  for (var i=0; i <items.length;i++) {
  	var v = this.getFirstTextNode(items[i]); 
  	if (v != null && v.data != null) {
  		if (list.length >0) list += ",";
  		list += v.data;
  	}
  }	
  
  return list;
}


/* 
#### Generic component functions #############################################
*/
DefaultTheme.prototype.renderChildNodes = function(renderer, uidl, to) {
	for (var i=0; i<uidl.childNodes.length; i++) {
		var child = uidl.childNodes.item(i);
		if (child.nodeType == Node.ELEMENT_NODE) {
			renderer.client.renderUIDL(child,to);
		} else if (child.nodeType == Node.TEXT_NODE) {
			to.appendChild(to.ownerDocument.createTextNode(child.data));
		}
	}
}
DefaultTheme.prototype.applyWidthAndHeight = function(uidl,target) {
	if (target == null || uidl == null) return;

	// Width
	var widthEl = this.getVariableElement(uidl,"integer","width");
	if (widthEl) {
		var w = widthEl.getAttribute("value");
		if (w > 0) {
			target.style.width = ""+w+"px";
		}
	}
	
	// Height
	var heightEl = this.getVariableElement(uidl,"integer","height");
	if (heightEl) {
		var h = heightEl.getAttribute("value");
		if (h > 0) {
			target.style.height = ""+h+"px";
		}
	}	
}
DefaultTheme.prototype.createPaintableElement = function (renderer, uidl, target,layoutInfo) {

	// And create DIV as container
	var div = null;
	var pid = uidl.getAttribute("id");
	var li = layoutInfo||target.layoutInfo;
	if (pid != null && target.getAttribute("id") == pid){
		div = target;
	} else {
		//TODO: Remove this if the statement below works.
		// div = renderer.theme.createElementTo(target,"div");
		div = renderer.client.createPaintableElement(uidl,target);
	}
	div.layoutInfo = li;
	
	// Remove possible previous content from target
	/* TODO remove when tested
	while (div.firstChild != null) {
		div.removeChild(div.firstChild);
	}
	*/
	div.innerHTML = "";
	if (li&&li.captionNode) {
		// caption placed elsewhere (form); see renderDefaultComponentHeader()
		li.captionNode.innerHTML = "";
	}
		
	// Assign CSS class
	this.setCSSDefaultClass(renderer,div,uidl);
	if ("true"==uidl.getAttribute("disabled")) {
		this.addCSSClass(div,"disabled");
	}
	if (this.getFirstElement(uidl,"error")) {
		this.addCSSClass(div,"error");
	}
	
	return div;	
}

DefaultTheme.prototype.renderDefaultComponentHeader = function(renderer,uidl,target, layoutInfo) {
	var theme = renderer.theme;
	var doc = renderer.doc;

	var captionText = uidl.getAttribute("caption");
	var error = this.getFirstElement(uidl,"error");
	var descriptionText = this.getElementContent(uidl,"description");
	var icon = uidl.getAttribute("icon");
	
	if (!captionText && !error && !descriptionText && !icon) {
		return null;
	}
	
	if (!layoutInfo) {
		layoutInfo = target.layoutInfo;
	}
	
	// If layout info contains caption node, use it as caption position
	if (layoutInfo != null && layoutInfo.captionNode) {
		target = layoutInfo.captionNode;
		target.innerHTML = "";
	}
	
	// Caption container	
	var caption = this.createElementTo(target,"div");
	// Create debug-mode UIDL div
	if (renderer.client.debugEnabled) {
		var uidlDebug = this.createElementTo(caption,"div","uidl minimized");
		renderer.client.renderHTML(uidl,uidlDebug);
		var t = this;
		client.addEventListener(uidlDebug,"click", function (e) {
				if (uidlDebug.className.indexOf("minimized") >=0) {
					t.removeCSSClass(uidlDebug,"minimized"); 
				} else {
					t.addCSSClass(uidlDebug,"minimized");
				}
			}
		);	
	}
	if (captionText||error||descriptionText||icon) {
		this.addCSSClass(caption,"caption");
	} else {
		return caption;
	}
	if (descriptionText || error) {
		this.addCSSClass(caption,"clickable");
	}

	if (error||descriptionText) {
		var popup = this.renderDescriptionPopup(renderer,uidl,(captionText?caption:target));
	}

	var iconUrl = uidl.getAttribute("icon");	
	
	if (error) {
		var icon = this.createElementTo(caption,"img","icon");
		icon.src = theme.root+"/img/icon/error-mini.gif";
		if (iconUrl) {
			/* overlay icon */
			this.setCSSClass(icon,"overlay");
		} else {
			this.setCSSClass(icon,"error");
		}
	} else if (descriptionText) {
		var icon = this.createElementTo(caption,"img","icon");
		icon.src = theme.root+"/img/icon/info-mini.gif";
		if (iconUrl) {
			/* overlay icon */
			this.setCSSClass(icon,"overlay");
		} else {
			this.setCSSClass(icon,"error");
		}
	}

	if (iconUrl) {
    	if (iconUrl.indexOf("theme://") == 0) {
    		iconUrl = (theme.iconRoot != null ? theme.iconRoot : theme.root) 
    					+ iconUrl.substring(8);
    	}
		var icon = this.createElementTo(caption,"img","icon");
		icon.src = iconUrl;
	}
	// Caption text
	this.createTextNodeTo(caption,captionText);

	return caption;
}

DefaultTheme.prototype.renderActionPopup = function(renderer, uidl, to, actions, actionVar, id, popupEvent) {
	// Shortcuts
	var theme = renderer.theme;
	var client = renderer.client;
	var evtName = popupEvent||"rightclick";

	var ak = uidl.getElementsByTagName("ak");
	var len = ak.length;
	if (len < 1) return;

	var popup = theme.createElementTo((to.nodeName=="TR"?to.firstChild:to),"div", "popup outset hide");
	theme.addHidePopupListener(theme,client,popup,"click");
	theme.addStopListener(theme,client,popup,"click");
	
	var inner = theme.createElementTo(popup,"div", "border");	
	var item = theme.createElementTo(inner,"div", "item pad clickable");
	
	for (var k=0;k<len;k++) {
		var key = theme.getFirstTextNode(ak[k]).data;
		var item = theme.createElementTo(inner,"div", "item pad clickable");
		theme.createTextNodeTo(item,actions[key]);
		item.style.color = "black";
		theme.addAddClassListener(theme,client,item,"mouseover","over");
		theme.addRemoveClassListener(theme,client,item,"mouseout","over");
		theme.addSetVarListener(theme,client,item,"click",actionVar,id+","+key,true);
		theme.addHidePopupListener(theme,client,item,"click");
		theme.addStopListener(theme,client,item,"click");
	}					
	theme.addStopListener(theme,client,to,"contextmenu");
	//theme.addStopListener(theme,client,to,evtName);
	theme.addTogglePopupListener(theme,client,to,evtName,popup);
}	

DefaultTheme.prototype.renderDescriptionPopup = function (renderer,uidl,target) {
	var theme = renderer.theme;
	var doc = renderer.doc;
	
	var captionText = uidl.getAttribute("caption");
	var desc = this.getFirstElement(uidl,"description");
	
	var error = this.getFirstElement(uidl,"error");
	var iconUrl = uidl.getAttribute("icon");		
	if (!iconUrl&&desc) {
		iconUrl = theme.root+"/img/icon/info.gif";
	}
	
	// Caption container
	var popup = this.createElementTo(target,"div","outset popup hide");
	var inner = this.createElementTo(popup,"table","border pad");
	inner = this.createElementTo(inner,"tbody");
	var tr = this.createElementTo(inner,"tr");
	var td = this.createElementTo(tr,"td");
	if (iconUrl) {
		if (iconUrl.indexOf("theme://") == 0) {
    		iconUrl = (theme.iconRoot != null ? theme.iconRoot : theme.root) 
    					+ iconUrl.substring(8);
    	}
		var icon = this.createElementTo(td,"img","icon");
		icon.src = iconUrl;
	}
	td = this.createElementTo(tr,"td");
	var caption = this.createElementTo(td,"div","caption");
	this.createTextNodeTo(caption,captionText);
	
	if (desc) {
		var description = this.createElementTo(td,"div","content");	
		description.innerHTML = renderer.client.getXMLtext(desc);
		description.style.whiteSpace ="normal";
	}
	if (error) {
		tr = this.createElementTo(inner,"tr");
		td = this.createElementTo(tr,"td");
		icon = this.createElementTo(td,"img","icon");
		icon.src = theme.root+"/img/icon/error.gif";
		td = this.createElementTo(tr,"td");
		var errorDiv = this.createElementTo(td,"div","error pad");
		this.renderData(renderer,error,errorDiv);
		var ew = errorDiv.ownerDocument.getElementById("error-window");
		if (ew) {
			ew.innerHTML += "<DIV><B>"+captionText+":</B> "+errorDiv.innerHTML+"</DIV><BR/>";
			ew.style.display = "inline";
		}
	}
	if (desc||error) {
        this.addTogglePopupListener(theme,client,target,"mouseover",popup,1000,500,target);
        //theme.addTogglePopupListener(theme,client,target,"click",popup,0,500);
        this.addHidePopupListener(theme,client,popup,"click",popup);
        this.addHidePopupListener(theme,client,target,"mouseout",popup);
        this.addHidePopupListener(theme,client,popup,"mouseout",popup);
	}
			
	return popup;
}

/** Show popup at specified position.
 *  Hides previous popup.
 *  
 *  @param popup		The element to popup
 *  @param x			horizontal popup position
 *  @param y			vertical popup position
 *  @param delay		delay before popping up
 *  @param defWidth		(optional) default width for the popup
 *  
 */
DefaultTheme.prototype.showPopup = function(client,popup, x, y, delay, defWidth) {
	if (this.popupTimeout) {
		clearTimeout(this.popupTimeout);
		delete this.popupTimeout;
	}
	if (!popup) { 
		var popup = this.popup;
		this.popupShowing = true;
		var scrollTop = (document.documentElement.scrollTop ? document.documentElement.scrollTop : document.body.scrollTop);
		var scrollLeft = (document.documentElement.scrollLeft ? document.documentElement.scrollLeft : document.body.scrollLeft);
		var docWidth = document.body.clientWidth;
		var docHeight = document.body.clientHeight;
		this.removeCSSClass(popup,"hide");
		
		var ua = navigator.userAgent.toLowerCase();
        if (ua.indexOf("msie")>=0) {
			var sels = popup.ownerDocument.getElementsByTagName("select");
			if (sels) {
				var len = sels.length;
				var hidden = new Array();
				for (var i=0;i<len;i++) {
					var sel = sels[i];
					if (sel.style&&sel.style.display!="none") {
						sel.style.visibility = "hidden";
						hidden[hidden.length] = sel;
					}
				}		
				this.popupSelectsHidden = hidden;
			}
		}
		/* TODO fix popup width & position */
		return;		
	}
	if (!delay) var delay = 0;
	if (this.popup && this.popup != popup) {
		this.hidePopup();
	} 
	this.popup = popup;
/*	THIS CODE IS NOT NEEDED IF WE CAN POSITION THE POPUP BEFOREHAND

	popup.style.left = 0+"px";
	popup.style.top = 0+"px";
	this.removeCSSClass(popup,"hide");

    var p = client.getElementPosition(popup);
    this.addCSSClass(popup,"hide");
    // TODOO!!! width not working properly
	if (p.w > document.body.clientWidth/2) {
		popup.style.width = Math.round(document.body.clientWidth/2)+"px";
		p.w = Math.round(document.body.clientWidth/2);
	}

    var posX = x||p.x;
    var posY = y||p.y;
    if (posX+p.w>document.body.clientWidth) {
    	posX = document.body.clientWidth-p.w;
    	if (posX<0) posX=0;
    }
    if (posY+p.h>document.body.clientHeight) {
    	posY = document.body.clientHeight-p.h;
    	if (posY < 0) posY =0;
    }
    
    if (p.h > document.body.clientHeight -20) {
		popup.style.height = document.body.clientHeight -20 + "px";
		popup.style.overflow = "auto";
		posX -= 20;
	}
    
    
	popup.style.left = posX+"px";
	popup.style.top = posY+"px";
*/
	if (delay > 0) {
		with ({theme:this}) {
			theme.popupTimeout = setTimeout(function(){
					theme.showPopup(client);
				}, delay);
		}
	} else {
		this.showPopup(client);
	}
}
/** Hides previous popup.
 */
DefaultTheme.prototype.hidePopup = function() {
	if (this.popupSelectsHidden) {
		var len = this.popupSelectsHidden.length;
		for (var i=0;i<len;i++) {
			var sel = this.popupSelectsHidden[i];
			sel.style.visibility = "visible";
		}
		this.popupSelectsHidden = null;
	}

	if (this.popup) {
		this.addCSSClass(this.popup,"hide");
		this.popupShowing = false;
	}
	if (this.popupTimeout) {
		clearTimeout(this.popupTimeout);
		delete this.popupTimeout;
	}
}
/** Shows the popup if it's not currently shown,
 *  hides the popup otherwise.
 *  Hides previous popup.
 *  
 *  @param popup		The element to popup
 *  @param x			horizontal popup position
 *  @param y			vertical popup position
 *  @param delay		delay before popping up
 *  @param defWidth		(optional) default width for the popup
 *  
 */
DefaultTheme.prototype.togglePopup = function(popup, x, y, delay, defWidth) {
	if (this.popup == popup && this.popupShowing) {
		this.hidePopup();
	} else {
		this.showPopup(client,popup,x,y,delay,defWidth);
	}
}


/*
#### Generic event handlers ######################################################
*/
DefaultTheme.prototype.addAddClassListener = function(theme,client,element,event,className,target,current) {
	client.addEventListener(element,event, function(e) {
			if (current) {
				if (current.length) {
					var length = current.length;
					while (length--) {
						theme.removeCSSClass(current[length],className);
						delete current[length];
					}
				} else {
					for (e in current) {
						theme.removeCSSClass(current[e],className);
						delete current[e];						
					}
				}
			}
			theme.addCSSClass((target?target:element),className);
			if (current) {
				current[current.length] = (target?target:element);
			}
		}
	);
}
DefaultTheme.prototype.addRemoveClassListener = function(theme,client,element,event,className,target) {
	client.addEventListener(element,event, function(e) {
			theme.removeCSSClass((target?target:element),className);
		}
	);
}
DefaultTheme.prototype.addToggleClassListener = function(theme,client,element,event,className,target) {
	client.addEventListener(element,event, function(e) {
			theme.toggleCSSClass((target?target:element),className);
		}
	);
}
DefaultTheme.prototype.addStopListener = function(theme,client,element,event) {
	client.addEventListener(element, event, function(e) { 
			var evt = client.getEvent(e);
			evt.stop();
			return false;				
		}
	);
}
DefaultTheme.prototype.addSetVarListener = function(theme,client,element,event,variable,key,immediate) {
	client.addEventListener(element,event, function(e) {
			var value = "";
			if (typeof(key)=="string") {
				value = key;
			} else if (key.type=="checkbox"||key.type=="radio") {
				value = key.checked;
			} else if (key.type=="select-multiple") {
				var s = new Array();
				for (var i = 0; i < key.options.length; i++) {
					if (key.options[i].selected) {
						s[s.length] = key.options[i].value;
					}
				}		
				value = s.join(',');		
			} else {
				value = key.value;
			}
			if (typeof(variable) == "string") {
				client.changeVariable(variable,value,immediate);
			} else {
				theme.setVariable(client,variable,value,immediate);
			}
		}
	);
}
DefaultTheme.prototype.addRemoveVarListener = function(theme,client,element,event,variable,key,immediate) {
	client.addEventListener(element,event, function(e) {
			theme.removeArrayVariable(client,variable,key,immediate);
		}
	);
}
DefaultTheme.prototype.addAddVarListener = function(theme,client,element,event,variable,key,immediate) {
	client.addEventListener(element,event, function(e) {
			theme.addArrayVariable(client,variable,key,immediate);
		}
	);
}
DefaultTheme.prototype.addToggleVarListener = function(theme,client,element,event,variable,key,immediate) {
	client.addEventListener(element,event, function(e) {
			theme.toggleArrayVariable(client,variable,key,immediate);
		}
	);
}
DefaultTheme.prototype.addExpandNodeListener = function(theme,client,img,event,subnodes,expandVariable,collapseVariable,key,immediate,target) {
		client.addEventListener((target?target:img), event, function(e) { 
				if (img.expanded == "true") {
					theme.removeArrayVariable(client,expandVariable,key,false);
					theme.addArrayVariable(client,collapseVariable,key,immediate);
					img.src = theme.root + "img/tree/off.gif";
					img.expanded = "false";
				} else {
					theme.removeArrayVariable(client,collapseVariable,key,false);
					theme.addArrayVariable(client,expandVariable,key,immediate || 
						!img.expanded || !subnodes.childNodes || subnodes.childNodes.length <= 0);
					img.src = theme.root + "img/tree/on.gif";
					img.expanded = "true";
				}
			}
		);
}

DefaultTheme.prototype.addTogglePopupListener = function(theme,client,element,event,popup,delay,defWidth,popupAt) {
	client.addEventListener(element,(event=="rightclick"?"mouseup":event), function(e) {
			var evt = client.getEvent(e);
			if (event=="rightclick"&&!evt.rightclick) return;
			if(evt.target.nodeName == "INPUT" || evt.target.nodeName == "SELECT") return;
            if (evt.alt) return;
            if (popupAt) {
            	var p = client.getElementPosition(popupAt);
 				theme.togglePopup(popup,p.x,(p.y+p.h),(delay?delay:0),(defWidth?defWidth:100));           	
            } else {
				theme.togglePopup(popup,evt.mouseX,evt.mouseY,(delay?delay:0),(defWidth?defWidth:100));
			}
			evt.stop();
		}
	);
}
DefaultTheme.prototype.addShowPopupListener = function(theme,client,element,event,popup,delay,defWidth) {
	client.addEventListener(element,(event=="rightclick"?"click":event), function(e) {
			var evt = client.getEvent(e);
			if (event=="rightclick"&&!evt.rightclick) return;

			theme.showPopup(client,popup,evt.mouseX,evt.mouseY,(delay?delay:0),(defWidth?defWidth:100));
			evt.stop();
		}
	);
}
// TODO dontstop -> stop in all listeners
DefaultTheme.prototype.addHidePopupListener = function(theme,client,element,event,dontstop) {
	client.addEventListener(element,(event=="rightclick"?"click":event), function(e) {
			var evt = client.getEvent(e);
            if (evt.alt) return;
			if (event=="rightclick"&&!evt.rightclick) return;
			theme.hidePopup();
			if (!dontstop) {
				evt.stop();
			}
		}
	);
}

/**
* Adds a hidden button with a tabindex; adds .over to hoverTarget when focused
*/
DefaultTheme.prototype.addTabtoHandlers = function(client,theme,target,hoverTarget,tabindex,defaultButton) {
	
	var d = this.createElementTo(target,"div");
	d.style.border = "none";
	d.style.background = "none";
	d.style.padding = "0px";
	d.style.margin = "0px;"	
	d.style.width = "0px";
	d.style.height = "0px";
	d.style.overflow = "hidden";

	var b = this.createInputElementTo(d,(defaultButton?"submit":"button"));

	if (tabindex) b.tabIndex = tabindex;

	client.addEventListener(b,"focus", function() {
		theme.addCSSClass(hoverTarget,"over");
	});
	client.addEventListener(b,"blur", function() {
		theme.removeCSSClass(hoverTarget,"over");
	});
}

/*
#### Component renderers ######################################################
*/
DefaultTheme.prototype.renderComponent = function(renderer,uidl,target,layoutInfo) {

	// Create containing element
	var div = renderer.theme.createPaintableElement(renderer,uidl,target,layoutInfo);
	if (uidl.getAttribute("invisible")) return; // Don't render content if invisible
	
	// Render default header
	renderer.theme.renderDefaultComponentHeader(renderer,uidl,div,layoutInfo);
	
	// Render children to div
	renderer.theme.renderChildNodes(renderer, uidl, div);
}


DefaultTheme.prototype.renderWindow = function(renderer,uidl,target,layoutInfo) {
	var div = renderer.theme.createPaintableElement(renderer,uidl,target,layoutInfo);
	if (uidl.getAttribute("invisible")) return; // Don't render content if invisible
	
	var theme = renderer.theme;
	
    theme.addHidePopupListener(theme,renderer.client,div,"click",true);
	// Render children to div
	theme.renderChildNodes(renderer, uidl, div);
	
	// Apply width and height
	theme.applyWidthAndHeight(uidl,div);
	
	// Focusing
	var focused = theme.getVariableElement(uidl,"string","focused");
	var focusid = theme.getVariableElementValue(focused);
	if (focusid) { 
		var found = false;
		var els = div.getElementsByTagName("input");
		var len = (els?els.length:0);
		for (var i=0;i<len;i++) {
			var el = els[i];
			if (focusid == el["focusid"]) {
				el.focus();
				found = true;
				break;
			}
		}
		if (!found) {
			els = div.getElementsByTagName("select");
			var len = (els?els.length:0);
			for (var i=0;i<len;i++) {
				var el = els[i];
				if (focusid == el["focusid"]) {
					el.focus();
					found = true;
					break;
				}
			}		
		}
		if (!found) {
			els = div.getElementsByTagName("textarea");
			var len = (els?els.length:0);
			for (var i=0;i<len;i++) {
				var el = els[i];
				if (focusid == el["focusid"]) {
					el.focus();
					found = true;
					break;
				}
			}		
		}
	}
}

DefaultTheme.prototype.renderOpen = function(renderer,uidl,target,layoutInfo) {
	var theme = renderer.theme;
 	
 	var src = uidl.getAttribute("src");
 	var name = uidl.getAttribute("name");
 	
 	if (name) {
 		window.open(src,name);
 	} else {
 		var div = theme.createPaintableElement(renderer,uidl,target,layoutInfo);
 		div.innerHTML = "<IFRAME name=\""+name+"\" id=\""+name+"\" width=100% height=100% style=\"border:none;margin:0px;padding:0px;background:none;\" src=\""+src+"\"></IFRAME>";
	}
}

DefaultTheme.prototype.renderFramewindow = function(renderer,uidl,target,layoutInfo) {	
	var theme = renderer.theme;
	var client = renderer.client;
	
	// TODO: Should we unregister all previous child windows?
	
	// We just reinitialize the window
	var win = target.ownerDocument.ownerWindow;
	client.initializeNewWindow(win,uidl,theme);
}

DefaultTheme.prototype.renderCustomLayout = function(renderer,uidl,target,layoutInfo) {
	// Shortcuts
	var theme = renderer.theme;
	
	// Get style
    var style = uidl.getAttribute("style");    
    if (style == null) return null;
    
    // Load the layout
    var url = theme.root + style;   
    var text = renderer.client.loadDocument(url,false); 
    if (text == null) {
    	client.debug("CustomLayout " + style + " NOT FOUND @ "+ url);
    	return null; 
    }

	// Create containing element
	var main = renderer.theme.createPaintableElement(renderer,uidl,target,layoutInfo);		
	if (uidl.getAttribute("invisible")) return; // Don't render content if invisible
    
    var locations = new Object();
    var unused = new Object();
    var cN = uidl.childNodes;
    var len = cN.length;
 	for (var j=0; j < len; j++) {
          var c = cN.item(j);
          if (c.nodeType == Node.ELEMENT_NODE 
          		&& c.nodeName == "location" 
          		&& c.getAttribute("name")) {
          		
          		locations[c.getAttribute("name")] = c;
          		unused[c.getAttribute("name")] = c;
          }
    }   
    
    
    var n = theme.createElementTo(main, "div");
    n.setAttribute("id",uidl.getAttribute("id"));
    n.innerHTML=text;
    var divs = n.getElementsByTagName("div");
    for (var i=0; i<divs.length; i++) {
      var div = divs.item(i);
      var name = div.getAttribute("location");      
      if (name != null) {
         var c = locations[name];
         if (c && c.getAttribute("name") == name) {   
          	delete unused[name];       
            for (var k=0; k<c.childNodes.length; k++) {
              var cc = c.childNodes.item(k); 
              if (cc.nodeType == Node.ELEMENT_NODE) {
                var parent = div.parentNode;               
                // TODO
                if (parent != null) {
                	theme.removeAllChildNodes(div);
                	var newNode = renderer.client.renderUIDL(cc,div);
                }
              }
            }  
        } else {
        	client.warn("Location " + name + " NOT USED in CustomLayout " + style);
        }
      }
    }
    if (unused.length>0) {
    	for (var k in usedLocations) {
    		client.error("Location " + k + " NOT FOUND in CustomLayout " + style);
    	}
    }
    
}

DefaultTheme.prototype.renderOrderedLayout = function(renderer,uidl,target,layoutInfo) {
	// Shortcuts
	var theme = renderer.theme;
	
	// Create containing element
	var div = renderer.theme.createPaintableElement(renderer,uidl,target,layoutInfo);		
	if (uidl.getAttribute("invisible")) return; // Don't render content if invisible

	// Render all children to table
	var vertical = uidl.getAttribute("orientation") != "horizontal";
	var table = null;
	var tr = null;
	var td = null;
	var style = uidl.getAttribute("style");
	var form = style == "form";
	
	for (var i=0; i<uidl.childNodes.length; i++) {
		var childUIDL = uidl.childNodes.item(i);
		td = null;
		if (childUIDL.nodeType == Node.ELEMENT_NODE) {
		
			// Ensure TABLE and TR
			if (tr == null || vertical) {
				if (table == null) {
					table = renderer.theme.createElementTo(div,"table","orderedlayout");
                    //table.width="100%";                    
					renderer.theme.addCSSClass(table,"layout");
					table = renderer.theme.createElementTo(table,"tbody","layout");
				}
				tr = renderer.theme.createElementTo(table,"tr","layout");
			}
			
			// Create extra TD for form style captions
			var layoutInfo = null;
			if (form) {
			 	layoutInfo = new Object()
				td = renderer.theme.createElementTo(tr,"td","layout");
				layoutInfo.captionNode = td;
			}
			
			// Force new TD for each child rendered
			td = renderer.theme.createElementTo(tr,"td","layout");			
			
			// Render the component to TD
			renderer.client.renderUIDL(childUIDL,td, null, layoutInfo);
			
		}
	}			
}


DefaultTheme.prototype.renderGridLayout = function(renderer,uidl,target,layoutInfo) {
	// NOTE TODO indenting might be off
	// Shortcuts
	var theme = renderer.theme;
		
	var h = uidl.getAttribute("h");	
	var w = uidl.getAttribute("w");
	// Create component element
	var div = theme.createPaintableElement(renderer,uidl,target,layoutInfo);
	if (uidl.getAttribute("invisible")) return; // Don't render content if invisible
	//var width = (div.offsetWidth||div.clientWidth)-20;
	//var px = Math.floor(width/parseInt(w));
	
	var table = theme.createElementTo(div,"table", "layout");
	table = renderer.theme.createElementTo(table,"tbody","layout");
	var tr = null;
	var td = null;
	for (var y=0; y<uidl.childNodes.length; y++) {
		var rowUidl = uidl.childNodes[y];
				
		if (rowUidl.nodeType == Node.ELEMENT_NODE || rowUidl.nodeName == "gr") {
		
			tr = theme.createElementTo(table,"tr","layout");
			tr.style.verticalAlign = "top";
			td = null;
			
			for (var x=0; x<rowUidl.childNodes.length; x++) {
				var cellUidl = rowUidl.childNodes[x];				
				
				// Add colspan and rowspan
				if (cellUidl.nodeType == Node.ELEMENT_NODE && cellUidl.nodeName == "gc") {							
					// Create new TD for each child rendered
					td = renderer.theme.createElementTo(tr,"td","layout");
										
					var w = cellUidl.getAttribute('w');
					var h = cellUidl.getAttribute('h');							
					//var cont = renderer.theme.createElementTo(td,"div");
					//cont.style.width = ((w?w:1)*px)+"px";
					if (w != null) {
						td.setAttribute('colSpan',w);
					}
					if (h != null) {
						td.setAttribute('rowSpan',h);
					}					
					// Render the component(s) to TD
					if (cellUidl.childNodes != null && cellUidl.childNodes.length >0) {
						var len = cellUidl.childNodes.length;
						for (var c=0;c<len;c++) {
							var el = cellUidl.childNodes[c];
							if (el.nodeType == Node.ELEMENT_NODE) {
								renderer.client.renderUIDL(el,td);
							}
						}
						//cont.style.width = "";
					}
				}
			}
		}
	}
}

DefaultTheme.prototype.renderPanel = function(renderer,uidl,target,layoutInfo) {
    // Supports styles "light" and "none"

			// Shortcuts
			var theme = renderer.theme;
			
			var style = uidl.getAttribute("style");
			
			var borderStyle = "border";
			
			// Create component element
			var outer = theme.createPaintableElement(renderer,uidl,target,layoutInfo);
			if (uidl.getAttribute("invisible")) return; // Don't render content if invisible
			
            if ("none"!=style) {
			    theme.addCSSClass(div,"outset");
            }
			if ("light"==style) {
				theme.addCSSClass(div,"light");
				//borderStyle += "light";
			}
			
			// Create extra DIV for visual layout
			var div = theme.createElementTo(outer,"div");
            if ("none"!=style) {
			    theme.setCSSClass(div,borderStyle);
            }

			// Create default header
			var caption = theme.renderDefaultComponentHeader(renderer,uidl,div);
			theme.addCSSClass(caption,"panelcaption");
            if ("light"==style) {
				theme.addCSSClass(caption,"panelcaptionlight");
			}

			// Create content DIV
			var content = theme.createElementTo(div,"div");
			theme.setCSSClass(content,"content");
			
			// Render children to div
			theme.renderChildNodes(renderer, uidl, content);

			// Apply width and height
			theme.applyWidthAndHeight(uidl,outer);
			
}

DefaultTheme.prototype.renderTabSheet = function(renderer,uidl,target,layoutInfo) {

			var theme = renderer.theme;
			
			// Create container element
			var div = renderer.theme.createPaintableElement(renderer,uidl,target,layoutInfo);
			if (uidl.getAttribute("invisible")) return; // Don't render content if invisible

			// Create default header
			var caption = renderer.theme.renderDefaultComponentHeader(renderer,uidl,div,layoutInfo);
			
			//  Render tabs
			var tabs = theme.createElementTo(div,"div","tabs");
			var varId = theme.getVariableElement(uidl,"string","selected").getAttribute("id");
			
			var tabNodes = theme.getChildElements(uidl,"tabs");
			if (tabNodes != null && tabNodes.length >0)  tabNodes = theme.getChildElements(tabNodes[0],"tab");
			var selectedTabNode = null;
			if (tabNodes != null && tabNodes.length >0) {
				for (var i=0; i< tabNodes.length;i++) {
					var tabNode = tabNodes[i];
					var tab = theme.createElementTo(tabs,"div");
					var key = tabNode.getAttribute("key");
					var iconUrl =  tabNode.getAttribute("icon");
					if (iconUrl && iconUrl.indexOf("theme://") == 0) {
		   				iconUrl = (theme.iconRoot != null ? theme.iconRoot : theme.root) 
		    				+ iconUrl.substring(8);
					}		
					if (tabNode.getAttribute("selected") == "true") {
						theme.addCSSClass(tab,"tab-on inline");
						selectedTabNode = tabNode;
					} else if (tabNode.getAttribute("disabled") == "true" 
								|| uidl.getAttribute("disabled") == "true"
								|| uidl.getAttribute("readonly") == "true") {
						theme.setCSSClass(tab,"tab disabled inline");
					} else {
						theme.setCSSClass(tab,"tab clickable inline");
						theme.addAddClassListener(theme,client,tab,"mouseover","over",tab);
						theme.addRemoveClassListener(theme,client,tab,"mouseout","over",tab);
						theme.addSetVarListener(theme,client,tab,"click",varId,key,true);
					}
					// Extra div in tab
					tab = theme.createElementTo(tab,"div","caption border pad inline");
					
					// Icon
					if (iconUrl) {
						tab.innerHTML = "<IMG src=\""+iconUrl+"\" class=\"icon\" />" + tabNode.getAttribute("caption");
					} else {
						tab.innerHTML = tabNode.getAttribute("caption");
					}
				
				}
			}
			
			// Render content (IE renderbug need three)
			var content = theme.createElementTo(div,"div","outset");
			content = theme.createElementTo(content,"div","border");
			content = theme.createElementTo(content,"div","content");
			if (selectedTabNode != null) {
				theme.renderChildNodes(renderer,selectedTabNode, content);
			}
}

DefaultTheme.prototype.renderTree = function(renderer,uidl,target,layoutInfo) {
			
	var theme = renderer.theme;
	
	// Create container element
	var div = renderer.theme.createPaintableElement(renderer,uidl,target,layoutInfo);
	if (uidl.getAttribute("invisible")) return; // Don't render content if invisible

	// Get tree attributes
	var style = uidl.getAttribute("style");
	var immediate = ("true" == uidl.getAttribute("immediate"));
	var disabled = ("true" == uidl.getAttribute("disabled"));
	var readonly = ("true" == uidl.getAttribute("readonly"));
	var selectMode = uidl.getAttribute("selectmode");
	var selectable = selectMode == "multi" || selectMode == "single";
	var selected;
	if (selectable) {
		selected = new Object();
	}
	var selectionVariable = theme.createVariableElementTo(div,theme.getVariableElement(uidl,"array","selected"));
	var expandVariable = theme.createVariableElementTo(div,theme.getVariableElement(uidl,"array","expand"));
	var collapseVariable = theme.createVariableElementTo(div,theme.getVariableElement(uidl,"array","collapse"));

	var actions = null;
	var actionVar = null;
	var alNode = theme.getFirstElement(uidl,"actions")
	if (alNode) {
		actionVar = theme.createVariableElementTo(div,theme.getVariableElement(alNode,"string","action"));
		actions = new Object();
		var ak = alNode.getElementsByTagName("action");
		for (var i=0;i<ak.length;i++) {
			actions[ak[i].getAttribute("key")] = ak[i].getAttribute("caption");
		}
	}
	delete alNode;

	// Create default header
	var caption = renderer.theme.renderDefaultComponentHeader(renderer,uidl,div,layoutInfo);

	// Content DIV
	var content = theme.createElementTo(div,"div","content"); 
	
	// Iterate all nodes
	for (var i = 0; i< uidl.childNodes.length;i++) {
		var node = uidl.childNodes[i];
		if (node.nodeName == "node" || node.nodeName == "leaf") {
			theme.renderTreeNode(renderer,node,content,selectable,selectMode,selected,selectionVariable,expandVariable,collapseVariable,actions,actionVar,immediate,disabled,readonly);
		} 	
	}
}

DefaultTheme.prototype.renderTreeNode = function(renderer,node,target,selectable,selectMode,selected,selectionVariable,expandVariable,collapseVariable,actions,actionVar,immediate,disabled,readonly) {

	var theme = renderer.theme;
	var client = renderer.client;

	var n = theme.createElementTo(target,"div","node clickable");
	
	// Expand/collapse/spacer button
	var img = theme.createElementTo(n,"img","icon");
	var key = node.getAttribute("key");	
	var icon = node.getAttribute("icon");
	if (icon) {
        var iconurl = theme.root+icon.split("theme:")[1];
        var iimg = theme.createElementTo(n,"img","icon");
	    iimg.src = iconurl;
    }
	
	// Caption
	var cap = theme.createElementTo(n,"div","caption inline");
	theme.createTextNodeTo(n,node.getAttribute("caption"));	
	
	// Hover effects
	if (!disabled&&!readonly) {
		theme.addAddClassListener(theme,client,n,"mouseover","over",n);
		theme.addRemoveClassListener(theme,client,n,"mouseout","over",n);
	}
	
	// Server-side selection
	if (selectable && node.getAttribute("selected") == "true") {
		theme.addCSSClass(n,"selected");
		selected[key] = n;
	}

	// Indicate selection	
	if (theme.listContainsInt(selectionVariable.value,key)) {
		theme.addCSSClass(n, "selected");
	}

	// Selection listeners
	if (selectable && !disabled) {
		if (!readonly) {		
			if (selectMode == "single") {
				theme.addAddClassListener(theme,client,n,"click","selected",n,selected);
				theme.addSetVarListener(theme,client,n,"click",selectionVariable,key,immediate);
			
			} else if (selectMode == "multi") {	
				theme.addToggleClassListener(theme,client,n,"click","selected");
				theme.addToggleVarListener(theme,client,n,"click",selectionVariable,key,immediate);
			
			}
		}
	} 
	
	// Actions
	if (!disabled && !readonly) {
		for (var i = 0; i< node.childNodes.length;i++) {
			var childNode = node.childNodes[i];
			if (childNode.nodeName == "al" ) {
				theme.renderActionPopup(renderer,childNode,n,actions,actionVar,key); // TODO check
			} 
		}	
	}
	
	// Render all sub-nodes
	if (node.nodeName == "node") {
		var subnodes = theme.createElementTo(target,"div","nodes");
		if (node.childNodes != null && node.childNodes.length >0) {
			img.src = theme.root + "img/tree/on.gif";
			img.expanded = "true";
		} else {
			img.src = theme.root + "img/tree/off.gif";
			img.expanded = "false";
		}
		for (var i = 0; i< node.childNodes.length;i++) {
			var childNode = node.childNodes[i];
			if (childNode.nodeName == "node" || childNode.nodeName == "leaf") {
				theme.renderTreeNode(renderer,childNode,subnodes,selectable,selectMode,selected,selectionVariable,expandVariable,collapseVariable,actions,actionVar,immediate,disabled,readonly);
			} 
		}	
		
		// Add event listener
		if (!disabled) {
			var target = (selectable&&!readonly?img:n);
			theme.addToggleClassListener(theme,client,target,"mouseup","hidden",subnodes);
			theme.addExpandNodeListener(theme,client,img,"mouseup",subnodes,expandVariable,collapseVariable,key,immediate,target);
			theme.addStopListener(theme,client,target,"mouseup");
			theme.addStopListener(theme,client,target,"click");
		}
		
	} else {
			img.src = theme.root + "img/tree/empty.gif";			
	}

}

DefaultTheme.prototype.renderTextField = function(renderer,uidl,target, layoutInfo) {

	var client = renderer.client;
	var theme = renderer.theme;
	var immediate = uidl.getAttribute("immediate") == "true";
	var readonly = uidl.getAttribute("readonly") == "true";
	var multiline = uidl.getAttribute("multiline") == "true";
	var secret = uidl.getAttribute("secret") == "true";
	var cols = uidl.getAttribute("cols");
	var rows = uidl.getAttribute("rows");
	var disabled = uidl.getAttribute("disabled") == "true";
	var focusid = uidl.getAttribute("focusid");
	var tabindex = uidl.getAttribute("tabindex");
	
	// Create containing element
	var div = renderer.theme.createPaintableElement(renderer,uidl,target,layoutInfo);
	if (uidl.getAttribute("invisible")) return; // Don't render content if invisible

	// Render default header
	renderer.theme.renderDefaultComponentHeader(renderer,uidl,div, layoutInfo);
	
	// Create border
	var border = renderer.theme.createElementTo(div,"div","border");
	
	// Create input
	var input = null;
	if (multiline) {
		input = renderer.theme.createElementTo(border,"textarea");	
		input.wrap = "off";	
		if (focusid) {
			input.focusid = focusid;
		}
	} else {
		input = renderer.theme.createInputElementTo(border,(secret?"password":"text"),null,focusid);	
	}
	if (tabindex) input.tabIndex = tabindex;
	if (disabled||readonly) {
		input.disabled = "true";
	}
	
	// Assign cols and rows
	if (cols >0) {
		if (multiline) {
			input.cols = cols;
		} else {
			input.size = cols;
			input.maxlength = cols;
		}
	}
	if (rows >0) {
		input.rows = rows;
	}
	
	// Find variable node
	var strNode = theme.getVariableElement(uidl,"string","text");
	var inputId = strNode.getAttribute("id");
	input.id = inputId;
	
	// Assign value	
	strNode= theme.getFirstTextNode(strNode);
	if (strNode != null && strNode.data != null) {
			input.value = strNode.data;
	}
		
	// Listener 
	theme.addSetVarListener(theme,client,input,"change",inputId,input,immediate);
}

DefaultTheme.prototype.renderDateField = function(renderer,uidl,target,layoutInfo) {
	// TODO needs simplification
	// - jscalendar supports time! but not resolution?
	// - dynamic .js loading!

	var theme = renderer.theme;

	// Create containing element
	var div = renderer.theme.createPaintableElement(renderer,uidl,target,layoutInfo);

	if (uidl.getAttribute("invisible")) return; // Don't render content if invisible

	/* Styles:
	*	time	- only time selection (no date)
	*/
	var style = uidl.getAttribute("style");

	var immediate = uidl.getAttribute("immediate") == "true";
	var disabled = uidl.getAttribute("disabled") == "true";
	var readonly = uidl.getAttribute("readonly") == "true";
	
	/* locale, translate UI */
	var locale = uidl.getAttribute("locale")	
	if (locale && !disabled && !readonly) {
		locale = locale.toLowerCase().split("_")[0];
		var lang = renderer.client.loadDocument(theme.root+"jscalendar/lang/calendar-"+locale+".js",false);
		if (lang) {			
			try {
				window.eval(lang);
			} catch (e) {
				client.error("Could not eval DateField lang ("+locale+"):"+e );
			}
		}
	}		
	
	// Render default header
	renderer.theme.renderDefaultComponentHeader(renderer,uidl,div,layoutInfo);
		
	var yearVar = theme.getVariableElement(uidl,"integer","year");
    var monthVar = theme.getVariableElement(uidl,"integer","month"); 
    var dayVar = theme.getVariableElement(uidl,"integer","day");
    var hourVar = theme.getVariableElement(uidl,"integer","hour");
    var minVar = theme.getVariableElement(uidl,"integer","min");
    var secVar = theme.getVariableElement(uidl,"integer","sec");
    var msecVar = theme.getVariableElement(uidl,"integer","msec");

	var year = null;
    var month = null;
    var day = null;
    var hour = null;
    var min = null;
    var sec = null;
    var msec = null;
	var text = null;
        
    var inputId = yearVar.getAttribute("id") + "_input";
	var buttonId = yearVar.getAttribute("id") + "_button";
	
    // Assign the value to textfield
    var yearValue = yearVar != null? yearVar.getAttribute("value"): null;
    var monthValue = monthVar != null? monthVar.getAttribute("value"): null;
    var dayValue = dayVar != null? dayVar.getAttribute("value"): null;
    var hourValue = hourVar != null? hourVar.getAttribute("value"): null;
    var minValue = minVar != null? minVar.getAttribute("value"): null;
    var secValue = secVar != null? secVar.getAttribute("value"): null;
    var msecValue = msecVar != null? msecVar.getAttribute("value"): null;
    
    if (style != "time") {
		if (dayValue) {
			// Using calendar - create textfield
		    if (readonly) {
		    	text = theme.createTextNodeTo(div,dayValue+"."+monthValue+"."+yearValue);
		    } else {
		    	text = theme.createInputElementTo(div,"text");
				text.id = inputId;
		    	text.size = "10";
			    if (disabled) {
			    	text.disabled = true;
			    }	            
			    if (yearValue >0 && monthValue >0 && dayValue >0) {
				    text.value = dayValue+"."+monthValue+"."+yearValue;
				} else {
				    text.value ="";
				}
		    }
			
			// Create button
		    var button = theme.createInputElementTo(div,"button","btn clickable");
		    button.id =buttonId;
		    button.value = "...";
		    if (disabled||readonly) {
		    	button.disabled = true;
		    }
		} else {
			if (yearVar) {
				// Year select
				if (readonly) {
					theme.createTextNodeTo(div,yearValue);
				} else {
			    	var year = theme.createElementTo(div,"select");
			    	year.options[0] = new Option("",-1);
			    	for (var i=0;i<2000;i++) {
			    		year.options[i+1] = new Option(i+1900,i+1900);
			    		if (yearValue == (i+1900)) {
			    			year.options[i+1].selected = true;
			    		}
			    	}
				    if (disabled) {
				    	year.disabled = true;
				    }
			    	if (!readonly) theme.addSetVarListener(theme,client,year,"change",yearVar.getAttribute("id"),year,immediate);
		    	}
			}
			if (monthVar) {
				// Month select
				if (readonly) {
					theme.createTextNodeTo(div,"."+monthValue);
				} else {
			    	month = theme.createElementTo(div,"select");
			    	month.options[0] = new Option("",-1);
			    	for (var i=0;i<12;i++) {
			    		month.options[i+1] = new Option(i+2,i+2);
			    		if (monthValue == i+2) {
			    			month.options[i+1].selected = true;
			    		}
			    	}
				    if (disabled) {
				    	month.disabled = true;
				    }
			    	if (!readonly) theme.addSetVarListener(theme,client,month,"change",monthVar.getAttribute("id"),month,immediate);
			    }
			}
		}
	}
    if (hourVar) {
    	if (readonly) {
    		theme.createTextNodeTo(div," "+(hourValue<10?"0"+hourValue:hourValue));
    	} else {
	    	hour = theme.createElementTo(div,"select");
	    	hour.options[0] = new Option("",-1);
	    	for (var i=0;i<24;i++) {
	    		var cap = (i+1<10?"0"+(i+1):(i+1));
	    		if (!minVar) {
	    			// Append anyway, makes it easier to recognize as time
	    			cap = cap + ":00";
	    		}
	    		hour.options[i+1] = new Option(cap,i+1);
	    		if (hourValue == i+1) {
	    			hour.options[i+1].selected = true;
	    		}
	    	}
		    if (disabled) {
		    	hour.disabled = true;
		    }
	    	if (!readonly) theme.addSetVarListener(theme,client,hour,"change",hourVar.getAttribute("id"),hour,immediate);
	    }
    }
    if (minVar) {
    	// Minute select
    	if (readonly) {
    		theme.createTextNodeTo(div,":"+(minValue<10?"0"+minValue:minValue));
    	} else {
	    	theme.createTextNodeTo(div,":");
	    	min = theme.createElementTo(div,"select");
	    	min.options[0] = new Option("",-1);
	    	for (var i=0;i<60;i++) {
	    		min.options[i+1] = new Option((i<10?"0"+(i):(i)),i);
	    		if (minValue == i) {
	    			min.options[i+1].selected = true;
	    		}
	    	}
		    if (disabled) {
		    	min.disabled = true;
		    }
	    	if (!readonly) theme.addSetVarListener(theme,client,min,"change",minVar.getAttribute("id"),min,immediate);
	    }
    }
    if (secVar) {
    	// Second select
    	if (readonly) {
    		theme.createTextNodeTo(div,":"+(secValue<10?"0"+secValue:secValue));
    	} else {
	    	theme.createTextNodeTo(div,":");
	    	sec = theme.createElementTo(div,"select");
	    	sec.options[0] = new Option("",-1);
	    	for (var i=0;i<60;i++) {
	    		sec.options[i+1] = new Option((i<10?"0"+(i):(i)),i);
	    		if (secValue == i) {
	    			sec.options[i+1].selected = true;
	    		}
	    	}
		    if (disabled) {
		    	sec.disabled = true;
		    }
	    	if (!readonly) theme.addSetVarListener(theme,client,sec,"change",secVar.getAttribute("id"),sec,immediate);
	    }
    }
    if (msecVar) {
    	// Millisecond select
    	if (readonly) {
	    		var cap = msecValue;
	    		if (i+1 < 100) {
	    			cap = "0"+cap;
	    		}
	    		if (i+1 < 10) {
	    			cap = "0"+cap;
	    		}
    		theme.createTextNodeTo(div,"."+cap);
    	} else {
	    	theme.createTextNodeTo(div,".");
	    	msec = theme.createElementTo(div,"select");
	    	msec.options[0] = new Option("",-1);
	    	for (var i=0;i<1000;i++) {
	    		var cap = i;
	    		if (i < 100) {
	    			cap = "0"+cap;
	    		}
	    		if (i < 10) {
	    			cap = "0"+cap;
	    		}
	    		msec.options[i+1] = new Option(cap,i);
	    		if (msecValue == i) {
	    			msec.options[i+1].selected = true;
	    		}
	    	}
		    if (disabled) {
		    	msec.disabled = true;
		    }
	    	if (!readonly) theme.addSetVarListener(theme,client,msec,"change",msecVar.getAttribute("id"),msec,immediate);
	    }	    
   }
   
   if (!readonly) {
   		if (msec) theme.addDateFieldNullListener(client,msec,text,msec,sec,min,hour,day,month,year,yearVar,immediate);
   		if (sec) theme.addDateFieldNullListener(client,sec,text,msec,sec,min,hour,day,month,year,yearVar,immediate);
   		if (min) theme.addDateFieldNullListener(client,min,text,msec,sec,min,hour,day,month,year,yearVar,immediate);
   		if (hour) theme.addDateFieldNullListener(client,hour,text,msec,sec,min,hour,day,month,year,yearVar,immediate);
   		if (day) theme.addDateFieldNullListener(client,day,text,msec,sec,min,hour,day,month,year,yearVar,immediate);
   		if (month) theme.addDateFieldNullListener(client,month,text,msec,sec,min,hour,day,month,year,yearVar,immediate);
   		if (year) theme.addDateFieldNullListener(client,year,text,msec,sec,min,hour,day,month,year,yearVar,immediate);
   		 
   }
   
   var nullFunc = function () {
   			// TODO wierd when un-nulling
   			// -> serverside, examine
   			// + nulls in dropdowns!
   			text.value = "";
   			if (msec) {
   				msec.options[0].selected = true;
   				//client.changeVariable(msecVar.getAttribute("id"), -1, false);
   			}
   			if (sec) {
   				sec.options[0].selected = true;
    			//client.changeVariable(secVar.getAttribute("id"), -1, false);
   			}
   			if (min) {
   				min.options[0].selected = true;
   				//client.changeVariable(minVar.getAttribute("id"), -1, false);
   			}
   			if (hour) {
   				hour.options[0].selected = true;
   				//client.changeVariable(hourVar.getAttribute("id"), -1, false);
   			}
   			//client.changeVariable(dayVar.getAttribute("id"), -1, false);
   			//client.changeVariable(monthVar.getAttribute("id"), -1, false);
   			client.changeVariable(yearVar.getAttribute("id"), -1, immediate);
   }
    
    // Function that updates the datefield
    var updateFunc = function (event) { 
   		if (text.value == null || text.value == "") {
   			nullFunc();
   			return;
   		}
    	if (dayVar) {
         var d = text.value.split(".")[0];
         if (d == null || d < 1 || d > 31 ) alert("Error");
         client.changeVariable(dayVar.getAttribute("id"), d, false);
        }
        if (monthVar) {
         var m = text.value.split(".")[1];
         if (m == null || m < 1 || m > 12) alert("Error");
         client.changeVariable(monthVar.getAttribute("id"), m, false);
        }
         var y = text.value.split(".")[2];
         if (y == null || y < 0 || y > 5000) alert("Error");
         client.changeVariable(yearVar.getAttribute("id"), y, immediate);
         
         
 	};
 	
 	if (!readonly && !disabled && style != "time" && dayVar) {
	 	//  Create a unique temporary variable
	 	// Dont know if all this is needed, but its purpose is to avoid
	 	// javascript problems with event handlers scopes.
	 	var temp = "datefield_" + (new Date()).getTime();;
	    eval (temp + " = new Object();");
	    (eval (temp)).update = function () { updateFunc() };
	    var st = "Calendar.setup({onUpdate : function () { " + temp + 
	                    ".update(); } ,inputField : '"+inputId+"', firstDay : 1,"+
	                    " ifFormat : '%d.%m.%Y', button : '"+buttonId+"'});";
	    
	    // Assign update function to textfield
	    text.onchange = updateFunc;
	
		// TODO externalize:
	    // Assign initialization to button mouseover (lazy initialization)
	    client.addEventListener(button, "mouseover", function(event) { 
	 			if (!eval(temp).initialized) {
	 				eval(temp).initialized =true; 
	 				eval(st); 
	 			} 
	 		}
	 	); 
	}
}
DefaultTheme.prototype.addDateFieldNullListener = function (client,elm,text,msec,sec,min,hour,day,month,year,yearVar,immediate) {
	client.addEventListener(elm, "change", function(event) {

		if ( !elm || elm.value != -1) return;


   			if (text) text.value = "";
   			
   			if (msec) {
   				msec.options[0].selected = true;
   				//client.changeVariable(msecVar.getAttribute("id"), -1, false);
   			}
   			if (sec) {
   				sec.options[0].selected = true;
    			//client.changeVariable(secVar.getAttribute("id"), -1, false);
   			}
   			if (min) {
   				min.options[0].selected = true;
   				//client.changeVariable(minVar.getAttribute("id"), -1, false);
   			}
   			if (hour) {
   				hour.options[0].selected = true;
   				//client.changeVariable(hourVar.getAttribute("id"), -1, false);
   			}
   			if (day) {
   				hour.options[0].selected = true;
   				//client.changeVariable(hourVar.getAttribute("id"), -1, false);
   			}
   			if (month) {
   				hour.options[0].selected = true;
   				//client.changeVariable(hourVar.getAttribute("id"), -1, false);
   			}
   			if (year) {
   				hour.options[0].selected = true;
   				//client.changeVariable(hourVar.getAttribute("id"), -1, false);
   			}
   			//client.changeVariable(dayVar.getAttribute("id"), -1, false);
   			//client.changeVariable(monthVar.getAttribute("id"), -1, false);
   			client.changeVariable(yearVar.getAttribute("id"), -1, immediate);
   });
}

DefaultTheme.prototype.renderDateFieldCalendar = function(renderer,uidl,target,layoutInfo) {

	var theme = renderer.theme;
	
	// Create containing element
	var div = renderer.theme.createPaintableElement(renderer,uidl,target,layoutInfo);
	if (uidl.getAttribute("invisible")) return; // Don't render content if invisible
	
	// Render default header
	renderer.theme.renderDefaultComponentHeader(renderer,uidl,div,layoutInfo);
	
	var immediate = uidl.getAttribute("immediate") == "true";
	var enabled = uidl.getAttribute("enabled") == "true";
	var readonly = uidl.getAttribute("readonly") == "true";
	var yearVar = theme.getVariableElement(uidl,"integer","year");
    var monthVar = theme.getVariableElement(uidl,"integer","month"); 
    var dayVar = theme.getVariableElement(uidl,"integer","day");
    var hourVar = theme.getVariableElement(uidl,"integer","hour");
    var minVar = theme.getVariableElement(uidl,"integer","minutes");
    var secVar = theme.getVariableElement(uidl,"integer","seconds");
    var msecVar = theme.getVariableElement(uidl,"integer","millseconds");
    
    var showTime   = hourVar != null;
    var inputId = yearVar.getAttribute("id") + "_input";
	
	// Create container DIV
	var calDiv = theme.createElementTo(div,"div");
	calDiv.id = inputId;
        
    // Assign the value to textfield
    var yearValue = yearVar != null? yearVar.getAttribute("value"): -1;
    var monthValue = monthVar != null? monthVar.getAttribute("value"): -1;
    var dayValue = dayVar != null? dayVar.getAttribute("value"): -1;
    var hourValue = hourVar != null? hourVar.getAttribute("value"): -1;
    var minValue = minVar != null? minVar.getAttribute("value"): -1;
    var secValue = secVar != null? secVar.getAttribute("value"): -1;
    var msecValue = msecVar != null? msecVar.getAttribute("value"): -1;
    if (yearValue >0 && monthValue >0 && dayValue >0) {
	    //TODO Assign date
	} else {
	    //TODO Assign date
	}	    

 	
 	//  Create a unique temporary variable
 	// Dont know if all this is needed, but its purpose is to avoid
 	// javascript problems with event handlers scopes.
 	var temp = "datefield_" + (new Date()).getTime();;
    eval (temp + " = new Object();");
    
    // Function that updates the datefield
    var dateChanged = function (cal) {
         var y = cal.date.getFullYear();
         var m = cal.date.getMonth();
         var d = cal.date.getDate();
         if (d == null || y == null || m == null || d < 1 ||
                 d > 31 || m < 1 || m > 12 || y < 0 || y > 5000) alert("Error");
         client.changeVariable(dayVar.getAttribute("id"), d, false);
         client.changeVariable(monthVar.getAttribute("id"), m, false);
         client.changeVariable(yearVar.getAttribute("id"), y, immediate);
 	};
    
    // Calendar setup code
    (eval (temp)).update = dateChanged;
    var st = "Calendar.setup({flatCallback : function (cal) { " + temp + 
                    ".update(cal); } ,showsTime: "+showTime+", flat: '"+inputId+"', firstDay : 1,"+
                    " ifFormat : '%d.%m.%Y'});";
    
 	
    // Assign initialization to button mouseover (lazy initialization)
    client.addEventListener(div, "mouseover", function(event) { 
 			if (!eval(temp).initialized) {
 				eval(temp).initialized =true; 
 				eval(st); 
 				
 			} 
 		}
 	);
 	
}

DefaultTheme.prototype.renderUpload = function(renderer,uidl,target,layoutInfo) {

	var theme = renderer.theme;
	var client = renderer.client;
	var varNode = theme.getVariableElement(uidl,"uploadstream","stream");
	
	// Create containing element
	var div = renderer.theme.createPaintableElement(renderer,uidl,target,layoutInfo);
	if (uidl.getAttribute("invisible")) return; // Don't render content if invisible
	
	// Render default header
	renderer.theme.renderDefaultComponentHeader(renderer,uidl,div,layoutInfo);

	// Unique name for iframes
	var frameName = "upload_"+varNode.getAttribute("id")+"_iframe";
	
	var iframe = theme.createElementTo(div, "iframe");
	iframe.style.width = '300px';
	iframe.style.height = '30px';
	iframe.id = frameName;
	iframe.name = frameName;
	iframe.src = 'about:blank';	
	iframe.style.border = 'none';
	iframe.style.margin = '0px';
	iframe.style.padding = '0px';
	iframe.style.background = 'none';


	// Get the window object of the iframe		
	var ifr = window.frames[frameName];	
	
	// TODO: FF fix. The above does not work in FF, so we
	// have to work our way around it. Iterate all frames.
	if (ifr == null) {
		var fi = 0;
		while (fi < window.frames.length) {
			if (window.frames[fi].frameElement != null && window.frames[fi].frameElement.name == frameName) {
				ifr = window.frames[fi];
			}
			fi++;
		}
	} 
		
	if (ifr != null) {
	
		// TODO: Put some initial content to IFRAME.
		// Nasty, but without this the browsers fail 
		// to create any elements into window.
		var code="<HTML>"+"<BODY STYLE=\" overflow: hidden; border: none; margin: 0px; padding: 0px;\"><\/BODY><\/HTML>";			
	    ifr.document.open();
	    ifr.document.write(code);
	    ifr.document.close();
	    
	        
	    // Ok. Now we are ready render the actual upload form and 
	    // inputs.
	    var form = ifr.document.createElement('form');		    
		form.setAttribute("action",client.ajaxAdapterServletUrl);
		form.setAttribute("method", "post");
		form.setAttribute("enctype", "multipart/form-data");
		if (document.all) {
		    form = ifr.document.createElement('<form action="'+client.ajaxAdapterServletUrl+'" method="post" enctype="multipart/form-data">');		    
		}
		var upload  = theme.createInputElementTo(form, "file");		
		upload.id = varNode.getAttribute("id");
		upload.name = varNode.getAttribute("id");
		var submit  = theme.createInputElementTo(form, "submit");	
		submit.value = "Send";
		ifr.document.body.appendChild(form);

		// Attach event listeners for processing the chencges after upload.
		if (document.all) {
			iframe.onreadystatechange = function() {			
				if (iframe.readyState == "complete") {
					//TODO: Is there a better way? Cannot figure out a 
					// way to take the changes out of iframes document in IE.
					// FF seems to be working, but IE just renders the
					// XML as highlight HTML and looses the original XML.
					//div.ownerDocument.location.reload();
					div.ownerDocument.location.href = div.ownerDocument.location.href;
				}
			};
		} else {
			iframe.onload = function() {				
				if (ifr.document != null && (ifr.document.contentType == "application/xml")) {
					// TODO: Damn. This would be nice but seems to be unreliable:
					//client.processUpdates(ifr.document);
					//div.ownerDocument.location.reload();
					div.ownerDocument.location.href = div.ownerDocument.location.href;
					
				}
			};
		}
	
	}	
}

DefaultTheme.prototype.renderEmbedded = function(renderer,uidl,target,layoutInfo) {

    var theme = renderer.theme;
    
	// Create containing element
	var div = renderer.theme.createPaintableElement(renderer,uidl,target,layoutInfo);
	if (uidl.getAttribute("invisible")) return; // Don't render content if invisible
	
	// Render default header
	renderer.theme.renderDefaultComponentHeader(renderer,uidl,div,layoutInfo);
	
	if (uidl.getAttribute("type") == "image") {
	
		// Image mode
		var img = renderer.theme.createElementTo(div,"img","embedded");
		
		// SRC
		var val = uidl.getAttribute("src");
		if (val != null) img.src = val;
		
		// Width
		val = uidl.getAttribute("width");
		if (val != null && val > 0) img.width = val;
		
		// Height
		val = uidl.getAttribute("height");
		if (val != null && val > 0) img.height = val;
		
		// ALT-attribute
		img.alt = theme.getElementContent(uidl,"description");
	} else {
	
		/*
		// Object mode
		var embedded = renderer.theme.createElementTo(div,"object","embedded");
		// SRC
		var val = uidl.getAttribute("src");
		if (val) embedded.src = val;
		
		
		// Width
		val = uidl.getAttribute("width");
		if (val != null && val > 0) embedded.width = val;
		
		// Height
		val = uidl.getAttribute("height");
		if (val != null && val > 0) embedded.height = val;
		
		// Codebase
		val = uidl.getAttribute("codebase");
		if (val != null) embedded.codebase = val;
		
		// Standby
		val = uidl.getAttribute("standby");
		if (val != null) embedded.codebase = val;
		*/
		
		var html = "<object ";
		var val = uidl.getAttribute("src");
		if (val) html += " data=\""+val+"\" ";
		
		val = uidl.getAttribute("width");
		if (val) html += " width=\""+val+"\" ";
		
		val = uidl.getAttribute("height");
		if (val) html += " height=\""+val+"\" ";
		
		val = uidl.getAttribute("codebase");
		if (val) html += " codebase=\""+val+"\" ";
		
		val = uidl.getAttribute("standby");
		if (val) html += " standby=\""+val+"\" ";
		
		html += ">";
		
		// Add all parameters
		var params = theme.getChildElements(uidl,"embeddedparams");
		if (params != null) {
			var len = params.length;
			for (var i=0;i<len;i++) {
				html += "<param name=\""+params[i].getAttribute("name")+"\" value=\""+params[i].getAttribute("name")+"\" />"
			}
		}
		
		html += "</object>";
		
		div.innerHTML = html;		
	}
}

DefaultTheme.prototype.renderLink = function(renderer,uidl,target,layoutInfo) {
	// Shortcut variables
	var theme = renderer.theme;
	var client = renderer.client;

	var immediate = "true"==uidl.getAttribute("immediate");
	var disabled = "true"==uidl.getAttribute("disabled");
	var readonly = "true"==uidl.getAttribute("readonly");

	var targetName = uidl.getAttribute("name");
	var width = uidl.getAttribute("width");
	var height = uidl.getAttribute("height");
	var border = uidl.getAttribute("border");
	var src = uidl.getAttribute("src");
	if (src && src.indexOf("theme://") == 0) {
		src = theme.root + src.substring(8);
	}	

	// Create containing element
	var div = theme.createPaintableElement(renderer,uidl,target,layoutInfo);
	if (uidl.getAttribute("invisible")) return; // Don't render content if invisible

	var link = theme.createElementTo(div,"div", "link pad clickable");
	
	if (!disabled&&!readonly) {
		theme.addAddClassListener(theme,client,link,"mouseover","over");
		theme.addRemoveClassListener(theme,client,link,"mouseout","over");
		
		var feat;
		switch (border) {
			case "minimal":
				feat = "menubar=yes,location=no,status=no";
				break;
			case "none":
				feat = "menubar=no,location=no,status=no";
				break;
			default: 
				feat = "menubar=yes,location=yes,scrollbars=yes,status=yes";
				break;
		}
		if (width||height) {
			feat += ",resizable=no";
			feat += (width?",width="+width:"");
			feat += (height?",height="+height:"");
		} else {
			feat += ",resizable=yes";
		}
		theme.addLinkOpenWindowListener(theme,client,div,"click",src,targetName,feat);
	}
	/*
	with(props) {
		client.addEventListener(div,"mouseover", function(e) {
				theme.addCSSClass(div,"over");
			}
		);
		client.addEventListener(div,"mouseout", function(e) {
				theme.removeCSSClass(div,"over");
			}
		);
		client.addEventListener(div,"click", function(e) {
				theme.hidePopup();
				if (!target) {
					window.location = src;
				} else {
					var feat;
					switch (border) {
						case "minimal":
							feat = "menubar=yes,location=no,status=no";
							break;
						case "none":
							feat = "menubar=no,location=no,status=no";
							break;
						default: 
							feat = "menubar=yes,location=yes,scrollbars=yes,status=yes";
							break;
					}
					if (width||height) {
						feat += ",resizable=no"
					} else {
						feat += ",resizable=yes"
					}
					var win = window.open(src, target,
								feat
									+(width?",width="+width:"")
									+(height?",height="+height:"")
							);
					win.focus();
				}			
			}
		);
	}
	*/
	//var inner = theme.createElementTo(div,"div", "border pad");
	
	// Render default header
	theme.renderDefaultComponentHeader(renderer,uidl,link);
	
	// Description under link
	var descriptionText = theme.getElementContent(uidl,"description");
	if (descriptionText) {
		var desc = theme.createElementTo(link,"div", "description");
		theme.createTextNodeTo(desc,descriptionText);
	}
}
DefaultTheme.prototype.addLinkOpenWindowListener = function(theme,client,element,event,url,target,features) {
	client.addEventListener(element,(event=="rightclick"?"click":event), function(e) {
			var evt = client.getEvent(e);
			if (event=="rightclick"&&!evt.rightclick) return;
			if (!target) {
				window.location = url;
			} else {
				var win = window.open(url, target, features);
				win.focus();
			}
		}
	);
}
DefaultTheme.prototype.renderTable = function(renderer,uidl,target,layoutInfo) {
	// Shortcut variables
	var theme = renderer.theme;
	var client = renderer.client;
	
	// Create containing DIV
	var div = theme.createPaintableElement(renderer,uidl,target,layoutInfo);	
	if (uidl.getAttribute("invisible")) return; // Don't render content if invisible

	if ("list"==uidl.getAttribute("style")) {
		theme.removeCSSClass(div,"table");
		theme.addCSSClass(div,"list");
	}	
	// Create default header
	var caption = theme.renderDefaultComponentHeader(renderer,uidl,div,layoutInfo);
	
	// Get table attributes
	var rowheaders = ("true"==uidl.getAttribute("rowheaders"));
	var totalrows = parseInt(uidl.getAttribute("totalrows"));
	var pagelength = parseInt(uidl.getAttribute("pagelength"));
	var rowCount = parseInt(uidl.getAttribute("rows"));
	var firstvisible = theme.getVariableElementValue(theme.getVariableElement(uidl,"integer","firstvisible"))||1;
	var firstvisibleVar = theme.createVariableElementTo(div,theme.getVariableElement(uidl,"integer","firstvisible"));
	var immediate = ("true" == uidl.getAttribute("immediate"));
	var selectMode = uidl.getAttribute("selectmode");
	var selectable = selectMode == "multi" || selectMode == "single";
	var selected; // Selected map
	if (selectable) {
		selected = new Array();
	}
	var selectionVariable = theme.createVariableElementTo(div,theme.getVariableElement(uidl,"array","selected"));
	var visibleCols = theme.getFirstElement(uidl,"visiblecolumns");
	var collapseVariable = theme.createVariableElementTo(div,theme.getVariableElement(uidl,"array","collapsedcolumns"));
	var sortcolVar = theme.createVariableElementTo(div,theme.getVariableElement(uidl,"string","sortcolumn"));
	var sortkey = theme.getVariableElementValue(theme.getVariableElement(uidl,"string","sortcolumn"));
	var sortasc = theme.getVariableElement(uidl,"boolean","sortascending");
	var sortascVar = theme.createVariableElementTo(div,sortasc);
	sortasc = sortasc != null && "true"==sortasc.getAttribute("value");
	
	var actions = null;
	var actionVar = null;
	var alNode = theme.getFirstElement(uidl,"actions")
	if (alNode) {
		actionVar = theme.createVariableElementTo(div,theme.getVariableElement(alNode,"string","action"));
		actions = new Object();
		var ak = alNode.getElementsByTagName("action");
		for (var i=0;i<ak.length;i++) {
			actions[ak[i].getAttribute("key")] = ak[i].getAttribute("caption");
		}
	}
	delete alNode;
		
	// Create table for content
	div = theme.createElementTo(div,"div","outset");
	div = theme.createElementTo(div,"div","content border");
	
	var table = theme.createElementTo(div,"table");
	table = theme.createElementTo(table,"tbody");
	table.setAttribute("cellpadding","0");
	table.setAttribute("cellspacing","0");
	var tr = null;
	var td = null;

	
	// Column headers
	var cols = theme.getFirstElement(uidl,"cols");
	if (cols != null) {
		cols = cols.getElementsByTagName("ch");
	}
	if (cols != null && cols.length >0) {
		tr = theme.createElementTo(table,"tr","header");
		if (rowheaders) {
			theme.createElementTo(tr,"td","empty");
		}
		for (var i=0; i<cols.length;i++) {
			var sortable = cols[i].getAttribute("sortable");
			td = theme.createElementTo(tr,"td","cheader bg");
			// Sorting
			var key = cols[i].getAttribute("cid");
			if (sortable=="true") {
				theme.addCSSClass(td,"clickable");
				// Sorting always immediate
				theme.addSetVarListener(theme,client,td,"click",sortascVar,(key==sortkey?!sortasc:sortasc),false);
				theme.addSetVarListener(theme,client,td,"click",sortcolVar,key,true);
				
			}
			var ch = cols[i].getAttribute("caption");
			var cap = theme.createElementTo(td,"div","caption");
			theme.createTextNodeTo(cap,ch != null? ch : "");
			if (sortkey==key) {
				var icon = theme.createElementTo(cap,"IMG","icon");
				icon.src = theme.root+"img/table/"+(sortasc?"asc.gif":"desc.gif");
			}
		}
		
		// Collapsing
		td = theme.createElementTo(tr,"td","cheader scroll bg");
		if (visibleCols) {
			var iconDiv = theme.createElementTo(td,"div");
			var icon = theme.createElementTo(iconDiv,"img","icon");
			icon.src = theme.root+"/img/table/colsel.gif";
			var popup = theme.createElementTo(td,"div","outset popup hide");
			var inner = theme.createElementTo(popup,"div","border");
			// empty row to allow closing:
			var row = theme.createElementTo(inner,"div","item clickable pad border");

			theme.addHidePopupListener(theme,client,row,"click");
			theme.addToggleClassListener(theme,client,row,"mouseover","over");
			theme.addToggleClassListener(theme,client,row,"mouseout","over");		
			theme.addTogglePopupListener(theme,client,iconDiv,"click",popup);
			
			var cols = visibleCols.getElementsByTagName("column");
			for (var i=0;i<cols.length;i++) {
				var row = theme.createElementTo(inner,"div","item clickable pad border");
				var collapsed = "true"==cols[i].getAttribute("collapsed");
				icon = theme.createElementTo(row,"img","icon");
				icon.src = theme.root+"/img/table/"+(collapsed?"off.gif":"on.gif");				
				theme.createTextNodeTo(row,cols[i].getAttribute("caption"));

				theme.addToggleClassListener(theme,client,row,"mouseover","over");
				theme.addToggleClassListener(theme,client,row,"mouseout","over");
				theme.addToggleVarListener(theme,client,row,"click",collapseVariable,cols[i].getAttribute("cid"),true);
			}
			delete cols;
		}
	}
	delete cols;

	// Table rows
	var rows = theme.getFirstElement(uidl,"rows");
	if (rows != null) {
		rows = theme.getChildElements(rows,"tr");
	}
	if (rows != null && rows.length >0) {
		for (var i=0; i<rows.length;i++) {
			tr = theme.createElementTo(table,"tr");
			// TODO rowheader
			theme.setCSSClass(tr, (i % 2 == 0?"even":"odd"));

			if (selectable) theme.addCSSClass(tr, "clickable");
			var key = rows[i].getAttribute("key");
			
			if (selectable&&"true"==rows[i].getAttribute("selected")) {
				theme.addCSSClass(tr, "selected");
				selected[selected.length] = tr;
			}

			if (selectable) {
				if (selectMode == "multi") {
					theme.addToggleClassListener(theme,client,tr,"click","selected");
					theme.addToggleVarListener(theme,client,tr,"click",selectionVariable,key,immediate);
				} else {
					theme.addAddClassListener(theme,client,tr,"click","selected",tr,selected);
					theme.addSetVarListener(theme,client,tr,"click",selectionVariable,key,immediate);
				}
			}

			if (rowheaders) {
				var td = theme.createElementTo(tr,"td","rheader bg");
				var caption = theme.createElementTo(td,"div","caption");
				theme.createTextNodeTo(caption,rows[i].getAttribute("caption"));
			}
			if (rows[i].childNodes != null && rows[i].childNodes.length >0) {
				var al = null; 
				for (var j=0; j<rows[i].childNodes.length;j++) {
					if (rows[i].childNodes[j].nodeName == "al") {
						al = rows[i].childNodes[j];
					} else if (rows[i].childNodes[j].nodeType == Node.ELEMENT_NODE) {
						td = theme.createElementTo(tr,"td");
						renderer.client.renderUIDL(rows[i].childNodes[j],td);
						if (al) {
							theme.renderActionPopup(renderer,al,td,actions,actionVar,key);
						}	
					}
				}
			}	
			// SCROLLBAR
			if (i==0) {
				td = theme.createElementTo(tr,"td", "scroll border");
				// TODO:
				//theme.tableAddScrollEvents(theme,td);
				
				td.setAttribute("rowSpan",rows.length);
				var inner = theme.createElementTo(td,"div", "scroll");
			}		
		}
	}
	delete rows;
	
	var paging = theme.createElementTo(div,"div","nav pad");
	var button = theme.createElementTo(paging,"div","pad caption inline");
	if (firstvisible > 1) {
		theme.addCSSClass(button,"clickable");
		theme.addAddClassListener(theme,client,button,"mouseover","bg");
		theme.addRemoveClassListener(theme,client,button,"mouseout","bg");
		theme.addSetVarListener(theme,client,button,"click",firstvisibleVar,(parseInt(firstvisible)-parseInt(pagelength)),true);
	} else {
		theme.addCSSClass(button,"disabled");
	}
	theme.createTextNodeTo(button,"<<");
	
	button = theme.createElementTo(paging,"div","small pad inline");
	theme.createTextNodeTo(button,firstvisible+" - "+(firstvisible-1+parseInt(rowCount))+ " / " + totalrows);
	
	button = theme.createElementTo(paging,"div","pad caption inline");
	if (parseInt(firstvisible)+parseInt(pagelength)<=parseInt(totalrows)) {
		theme.addCSSClass(button,"clickable");
		theme.addAddClassListener(theme,client,button,"mouseover","bg");
		theme.addRemoveClassListener(theme,client,button,"mouseout","bg");
		theme.addSetVarListener(theme,client,button,"click",firstvisibleVar,(parseInt(firstvisible)+parseInt(pagelength)),true);
	} else {
		theme.addCSSClass(button,"disabled");
	}
	theme.createTextNodeTo(button,">>");
}

DefaultTheme.prototype.renderScrollTable = function(renderer,uidl,target,layoutInfo) {
	// Shortcut variables
	var theme = renderer.theme;
	var client = renderer.client;
	var colWidths;
	if (target.colWidths) {
		colWidths = target.colWidths;
	} else {
		colWidths = new Object();
	}
	var wholeWidth = target.wholeWidth;
	var scrolledLeft = target.scrolledLeft;

	// Get attributes
	var pid = uidl.getAttribute("id");
	var immediate = uidl.getAttribute("immediate")||false;
	var selectmode = uidl.getAttribute("selectmode");
	var cols = parseInt(uidl.getAttribute("cols"));
	var rows = parseInt(uidl.getAttribute("rows"));
	var totalrows = parseInt(uidl.getAttribute("totalrows"));
	var pagelength = uidl.getAttribute("pagelength");
	var colheaders = uidl.getAttribute("colheaders")||false;
	var rowheaders = uidl.getAttribute("rowheaders")||false;
	var visiblecols = theme.getFirstElement(uidl,"visiblecolumns");
	var sortkey = theme.getVariableElementValue(theme.getVariableElement(uidl,"string","sortcolumn"));
	
	var colorder = new Array();
	var fv = parseInt(theme.getVariableElementValue(theme.getVariableElement(uidl,"integer","firstvisible"))||1);
	var selected; // Selected map
	if (selectmode) {
		selected = new Array();
	}
	
	// Create containing DIV
	var div = theme.createPaintableElement(renderer,uidl,target,layoutInfo);	
	div.colWidths = colWidths;
	if (uidl.getAttribute("invisible")) return; // Don't render content if invisible

	// Variables
	var fvVar = theme.createVariableElementTo(div,theme.getVariableElement(uidl,"integer","firstvisible"));
	var ccVar = theme.createVariableElementTo(div,theme.getVariableElement(uidl,"array","collapsedcolumns"));
	var coVar = theme.createVariableElementTo(div,theme.getVariableElement(uidl,"array","columnorder"));
	var selVar = theme.createVariableElementTo(div,theme.getVariableElement(uidl,"array","selected"));
	var sortVar = theme.createVariableElementTo(div,theme.getVariableElement(uidl,"string","sortcolumn"));
	var sortasc = theme.getVariableElement(uidl,"boolean","sortascending");
	var sortascVar = theme.createVariableElementTo(div,sortasc);
	sortasc = (sortasc != null && "true"==sortasc.getAttribute("value"));

	// Create default header
	var caption = theme.renderDefaultComponentHeader(renderer,uidl,div,layoutInfo);
	// column collapsing

	// main div
	var inner  = theme.createElementTo(div,"div","border");
	inner.innerHTML = "<TABLE width=\"100%\"><TR><TD></TD></TR></TABLE>";
	if (!wholeWidth) {
		wholeWidth = inner.offsetWidth||inner.clientWidth||300;
		wholeWidth -= 2; // Leave room for border, TODO: more dynamic
		if (wholeWidth<200) wholeWidth = 300;
	}
	div.wholeWidth = wholeWidth;
	var offsetLeft = client.getElementPosition(inner).x;
    	
	// Actions
	var actions = null;
	var actionVar = null;
	var alNode = theme.getFirstElement(uidl,"actions")
	if (alNode) {
		actionVar = theme.createVariableElementTo(div,theme.getVariableElement(alNode,"string","action"));
		actions = new Object();
		var ak = alNode.getElementsByTagName("action");
		for (var i=0;i<ak.length;i++) {
			actions[ak[i].getAttribute("key")] = ak[i].getAttribute("caption");
		}
	}
	delete alNode;

	inner.innerHTML = "<DIV id=\""+pid+"status\" align=\"center\" class=\"abs border pad\" style=\"width:"+(wholeWidth/2)+"px;background-color:white;display:none;\"></DIV><TABLE cellpadding=0 cellspacing=0 border=0 width=100%><TBODY><TR valign=top class=bg><TD></TD><TD width=16></TD></TR></TBODY></TABLE><TABLE>";
	//inner.style.width = wholeWidth+"px";
	var vcols = inner.childNodes[1].firstChild.firstChild.childNodes[1];
	if (visiblecols) {			
		vcols.innerHTML = "<IMG class=\"bg icon\" src=\""+theme.root+"/img/table/colsel.gif\"/>";
		var icon = vcols.firstChild; 
		vcols.id = pid+"vcols";
		var popup = theme.createElementTo(div,"div","border popup hide");
		theme.addTogglePopupListener(theme,client,icon,"click",popup);
		theme.addStopListener(theme,client,icon,"mouseover");
		theme.addStopListener(theme,client,icon,"mouseout");
		var row = theme.createElementTo(popup,"div","item clickable pad border");
		theme.addHidePopupListener(theme,client,row,"click");
		var cols = visiblecols.getElementsByTagName("column");
		for (var i=0;i<cols.length;i++) {
			var row = theme.createElementTo(popup,"div","item clickable pad border");
			var collapsed = "true"==cols[i].getAttribute("collapsed");
			icon = theme.createElementTo(row,"img","icon");
			icon.src = theme.root+"/img/table/"+(collapsed?"off.gif":"on.gif");				
			theme.createTextNodeTo(row,cols[i].getAttribute("caption"));
			theme.addAddClassListener(theme,client,row,"mouseover","over");
			theme.addRemoveClassListener(theme,client,row,"mouseout","over");
			theme.addToggleVarListener(theme,client,row,"click",ccVar,cols[i].getAttribute("cid"),true);
		}
		delete cols;		
	}


	// FIRST render the table 	
	var alignments = new Array();
	
	// headers
	var hout = theme.createElementTo(inner.childNodes[1].firstChild.firstChild.firstChild,"div","bg");
	hout.style.width = (wholeWidth-16)+"px";
	hout.style.paddingRight = "0px";
	hout.id = pid+"hout";
	hout.style.overflow = "hidden";	
	var html = "<TABLE id=\""+pid+"hin\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><TBODY><TR>";	
	if (rowheaders) {
				html += "<TD ";
				if (colWidths["heh"]) {
					html += "width=\""+colWidths["heh"]+"\" ";
				}
				html += "style=\"overflow:hidden\" cid=\"heh\" id=\""+pid+"heh\"><DIV class=\"padnr\" style=\"";
				if (colWidths["heh"]) {
					html += "width:"+colWidths["heh"]+"px;";
				}
				html += "overflow:hidden;height:100%;white-space:nowrap;border-right:1px solid gray;\"><IMG id=\""+pid+"hah\" align=\"right\" src=\""+theme.root+"/img/table/handle.gif\" border=\"0\" style=\"height:100%;width:2px;cursor:w-resize;\"></DIV></TD>";
	}	
	var chs = theme.getFirstElement(uidl, "cols").getElementsByTagName("ch");
	var len = chs.length;
	for (var i=0;i<len;i++) {
		var col = chs[i];
		var cap =  col.getAttribute("caption")||(visiblecols?"":"");
		var sort =  col.getAttribute("sortable");
		var cid =  col.getAttribute("cid");
		var iconUrl =  col.getAttribute("icon");
		if (iconUrl && iconUrl.indexOf("theme://") == 0) {
		    iconUrl = (theme.iconRoot != null ? theme.iconRoot : theme.root) 
		    				+ iconUrl.substring(8);
		}		
		alignments[i] = col.getAttribute("align");		
		colorder[i] = cid;
		html += "<TD ";
		if (colWidths[cid]) {
			html += "width=\""+colWidths[cid]+"\" ";
		}
		if (sortkey == cid) {
			html += "sorted=\"true\" ";
		}
		html += "style=\"overflow:hidden\" cid=\""+cid+"\" id=\""+pid+"he"+i+"\" >"
		html += "<DIV class=\"padnr\" ";
		if (alignments[i]) {
			switch (alignments[i]) {
				case "e":
					html += "align=\"right\"";
					break;
				case "c":
					html += "align=\"center\"";
					break;
				default:
			}
		}
		html += " style=\"";
		if (colWidths[cid]) {
			html += "width:"+colWidths[cid]+"px;";
		}
		html += "overflow:hidden;font-weight:bold;height:100%;white-space:nowrap;border-right:1px solid gray;\"><IMG id=\""+pid+"ha"+cid+"\" align=\"right\" src=\""+theme.root+"/img/table/handle.gif\" border=\"0\" style=\"height:100%;width:4px;cursor:w-resize;\">";
		html += (iconUrl?"<IMG src=\""+iconUrl+"\" class=\"icon\">":"")+cap+"</DIV></TD>";
	}
	html += "</TR></TBODY></TABLE>";
	hout.innerHTML = html;
	
	// content
	// scroll padding calculations 
	// TODO these need to be calculated better, perhaps updated after rendering content
	var prePad = (fv==1?1:fv*22);
	var postPad = (totalrows-fv-rows+1)*22;
	// html
	cout = theme.createElementTo(inner,"div");
	cout.style.width = wholeWidth+"px";
	cout.style.height = (18*rows)+"px";
	cout.id = pid+"cout";
	cout.style.overflow = "scroll";
	html = "<TABLE border=\"0\" cellpadding=\"0\" cellspacing=\"0\" id=\""+pid+"cin\"><TBODY><TR height=\""+prePad+"\"></TR>";
	var trs = theme.getFirstElement(uidl, "rows").getElementsByTagName("tr");
	len = trs.length;
	if (len==0) {
		html += "<TR id=\""+pid+"firstrow\"><TD style=\"overflow:hidden\">";
		html += "<DIV class=\"pad\" style=\"overflow:hidden;height:100%;white-space:nowrap;border-right:1px solid gray;\"></DIV></TD></TR>";
	}
	for (var i=0;i<len;i++) {
		var row = trs[i];
		var cap =  row.getAttribute("caption");
		var key =  row.getAttribute("key");
		var seld = row.getAttribute("selected");
		var iconUrl = row.getAttribute("icon");
		html += "<TR "+(i==0?"id=\""+pid+"firstrow\"":"");
		html += " key=\""+key+"\"";
		if (seld) {
			html += " selected=\"true\" class=\"selected\" ";
		}
		html += ">";	
		if (rowheaders) {
			html += "<TD ";
			if (colWidths["heh"]) {
				html += "width=\""+colWidths["heh"]+"\" ";
			}
			html += "style=\"overflow:hidden\"><DIV class=\"padnr\" style=\"";
			if (colWidths["heh"]) {
				html += "width:"+colWidths["heh"]+"px;";
			}
			html += "overflow:hidden;height:100%;white-space:nowrap;border-right:1px solid gray;\">";
			if (iconUrl) {
				if (iconUrl.indexOf("theme://") == 0) {
		    		iconUrl = (theme.iconRoot != null ? theme.iconRoot : theme.root) 
		    				+ iconUrl.substring(8);
				}
				html += "<IMG src=\""+iconUrl+"\" class=\"icon\" />";	
			}
			html += row.getAttribute("caption")+"</DIV></TD>";
		}	
		var comps = row.childNodes;
		var l = comps.length;
		if (l==0) {
			html += "<TD><DIV class=\"padnr\" style=\"overflow:hidden;height:100%;white-space:nowrap;border-right:1px solid gray;\"></DIV></TD>";
		}
		
		var colNum = -1;
		for (j=0;j<l;j++) {
			var comp = comps[j];
			if (comp.nodeName == "al"||comp.nodeName == "#text") continue;
			colNum++;
			// Placeholder TD, we'll render the content later
			html += "<TD "
			if (colWidths[colorder[colNum]]) {
				html += "width=\""+colWidths[colorder[colNum]]+"\" ";
			}
			if (alignments[colNum]) {
				switch (alignments[colNum]) {
					case "e":
						html += " align=\"right\" ";
						break;
					case "c":
						html += " align=\"center\" ";
						break;
					default:
				}
			}
			html += "style=\"overflow:hidden\"><DIV class=\"padnr\" style=\"";
			if (colWidths[colorder[colNum]]) {
				html += "width:"+colWidths[colorder[colNum]]+"px;";
			}
			html += "overflow:hidden;height:100%;white-space:nowrap;border-right:1px solid gray;\"></DIV></TD>";
		}	
		html += "</TR>";
	}
	html += "<TR id=\""+pid+"lastrow\" height=\""+postPad+"\"></TR></TBODY></TABLE>";	
	cout.innerHTML = html;

	// SECOND render the sub-components (TD content)
	var trs = cout.firstChild.firstChild.childNodes;
	var utrs = theme.getFirstElement(uidl, "rows").getElementsByTagName("tr");
	for (var i=0;i<len;i++) {
		var tr = trs[i+1];
		var key = tr.getAttribute("key");
		var comps = utrs[i].childNodes;
		var l = comps.length;
		var currentCol = (rowheaders?1:0);
		var al = null;
		for (j=0;j<l;j++) {
			var comp = comps[j];
			if (comp.nodeName == "#text") continue;
			if (comp.nodeName == "al") {
				al = comp;
				continue;
			}
			var trg = tr.childNodes[currentCol++].firstChild;
			client.renderUIDL(comp, trg);
		}
		
		if (al&&tr.firstChild) {
			theme.renderActionPopup(renderer,al,tr,actions,actionVar,key,"rightclick");
		}	
		
		// selection
		if (selectmode) {
			selected[selected.length] = tr;
			theme.addCSSClass(tr,"clickable");
			theme.addToggleClassListener(theme,client,tr,"mouseover","selectable");
			theme.addToggleClassListener(theme,client,tr,"mouseout","selectable");
			if (selectmode == "multi") {
				theme.addToggleClassListener(theme,client,tr,"click","selected");
				theme.addToggleVarListener(theme,client,tr,"click",selVar,key,immediate);
			} else {
				theme.addAddClassListener(theme,client,tr,"click","selected",tr,selected);
				theme.addSetVarListener(theme,client,tr,"click",selVar,key,immediate);
			}
		}
	}

	// THIRD do some initial sizing and scrolling
	var fr = target.ownerDocument.getElementById(pid+"firstrow").offsetTop;
   	var lr = target.ownerDocument.getElementById(pid+"lastrow").offsetTop;
	cout.style.height = (lr-fr+20)+"px";	
    cout.scrollTop = (fv>totalrows-rows?cout.scrollHeight:fr);
	div.recalc = theme.scrollTableRecalc;
	div.initialWidth = wholeWidth;
 	div.recalc(pid,target);
	cout.scrollLeft = scrolledLeft;
	hout.scrollLeft = scrolledLeft;

	var status = target.ownerDocument.getElementById(pid+"status");
	var p = client.getElementPosition(inner);
	status.style.top = (p.y + p.h/2) + "px";
	status.style.left = (p.x + p.w/2 - wholeWidth/4) +"px";
 	theme.scrollTableAddScrollHandler(client,theme,cout,div,status,lr,fr,rows,totalrows,fv,fvVar,immediate);	
 	theme.scrollTableAddScrollListener(theme,div,pid,lr,fr,rows,totalrows,fv);
 	
 	
 		// Column order drag & drop
 	var hin = target.ownerDocument.getElementById(pid+"hin");
    var h = hin.getElementsByTagName("td");
    var dragOrderGroup = new Object();
    for (var i = 0;i<h.length;i++) { 
    	var id = h[i].getAttribute("id");  
    	if (id==pid+"heh") {
	        var handle = target.ownerDocument.getElementById(pid+"hah");
	        if (handle) {
	        	theme.tableAddWidthListeners(client,theme,handle,"heh",div,pid);
	        }
    	}
 		if (!id||id.indexOf(pid+"he")<0) {
            continue;
        }   
        var cid = h[i].getAttribute("cid");
        var handle = target.ownerDocument.getElementById(pid+"ha"+cid);
        if (handle) {
        	theme.tableAddWidthListeners(client,theme,handle,cid,div,pid);
        }
        if (coVar||sortVar) {
        	theme.addCSSClass(h[i],"clickable");
        	theme.addToDragOrderGroup(client,theme,h[i],dragOrderGroup,coVar,sortVar,sortascVar,sortasc);
        }
    }
    
    var hin = target.ownerDocument.getElementById(pid+"hin");
    var cin = target.ownerDocument.getElementById(pid+"cin");
     theme.scrollTableRegisterLF(client,theme,div,inner,cout,hout,cin,hin);
}
// Header order drag & drop	
DefaultTheme.prototype.tableAddWidthListeners = function(client,theme,element,cid,table,pid) {
	
	var colWidths = table.colWidths;
	
	var mouseDragListener = function (e) {
			var evt = client.getEvent(e);
			evt.stop();
			element.ownerDocument.onselectstart = function(e) {return false;}
			var target = element.target;
			var div = target.parentNode;
			var td = div.parentNode;
			//target.style.position = "relative";
			target.style.zIndex = "99999";
			var offset = -(target.origX-evt.mouseX+10);
			var w = (target.origW+offset);
			if (w < 7) w = 7;
			try {
				target.style.left = offset+"px";			
				td.width = w;
				td.style.width = w+"px";
				td.firstChild.style.width = w+"px";
				colWidths[cid] = w;
			} catch (err) {
				client.debug("Failed: d&d target.style.left="+ offset+"px");
			}

	}
	
	var mouseUpListener = function(e) {
			client.removeEventListener(element.ownerDocument.body,"mousemove",mouseDragListener);
			client.removeEventListener(element.ownerDocument.body,"mouseup",arguments.callee);
			client.removeEventListener(element.ownerDocument.body,"drag",stopListener);
			var evt = client.getEvent(e);
			evt.stop();
			element.ownerDocument.onselectstart = null;
			element.dragging = false;
			// TODO apply for all rows
			table.recalc(pid,table);
			return false;
	};
	
	var stopListener = function (e) {
		var evt = client.getEvent(e);
		evt.stop();
		return false;
	}
	
	client.addEventListener(element,"mousedown", function(e) {
		var evt = client.getEvent(e);
		evt.stop();
		element.dragging = true;
		element.moved = false;
		element.target = evt.target;
		evt.target.origX = evt.mouseX;
		evt.target.origW = evt.target.parentNode.offsetWidth;
		client.addEventListener(element.ownerDocument.body,"mousemove", mouseDragListener);
		client.addEventListener(element.ownerDocument.body,"mouseup", mouseUpListener);
		client.addEventListener(element.ownerDocument.body,"drag",stopListener);
	});
}

DefaultTheme.prototype.scrollTableRegisterLF = function(client,theme,paintableElement,inner,cout,hout,cin,hin) {
	client.registerLayoutFunction(paintableElement,function() {
		var w = (inner.offsetWidth-2) +"px";
		cout.style.width = w;
		//cin.style.width = w;
		//hout.style.width = w;
		//hin.style.width = w;
		hout.style.width = hout.offsetParent.offsetWidth + "px";
		//div.recalc();
	});
}

DefaultTheme.prototype.scrollTableAddScrollListener = function (theme,target,pid,lr,fr,rows,totalrows,fv) {
	var hout = target.ownerDocument.getElementById(pid+"hout");
    var cout = target.ownerDocument.getElementById(pid+"cout"); 		
 	client.addEventListener(cout,"scroll", function (e) {
        if (cout.scrollTimeout) {
 			clearTimeout(cout.scrollTimeout);
		}
		hout.scrollLeft = cout.scrollLeft;	
		target.scrolledLeft = cout.scrollLeft;
		var status = target.ownerDocument.getElementById(pid+"status");
		var d = theme.scrollTableGetFV(cout,lr,fr,rows,totalrows,fv);
		if (d!=fv) {
 			status.innerHTML = d + "-" + (d+rows-1) + " / " + totalrows;
 			status.style.display = "";		
 		}
		cout.scrollTimeout = setTimeout(function () {
				var cout = target.ownerDocument.getElementById(pid+"cout");
				cout.scrollHandler();
			},500)	
 	});
}
DefaultTheme.prototype.scrollTableGetFV = function(cout,lr,fr,rows,totalrows,fv) {
 			var rh = (lr-fr)/rows;
 			if (cout.scrollTop >= (fr+rh/2) || cout.scrollTop <= (fr-rh/2)) {
 				var d = Math.round((cout.scrollTop-fr)/rh);
 				d = (fv+d);
 				if (d<1) d=1; // scrolled past begin
 				if (d>(totalrows-rows+1)) d=(totalrows-rows+1); // scrolled past last page
 				return d;
 			} else {
 				return fv;
 			}
 }
DefaultTheme.prototype.scrollTableAddScrollHandler = function(client,theme,cout,target,status,lr,fr,rows,totalrows,fv,fvVar,immediate) {
 	cout.scrollHandler = function () {
 			var rh = (lr-fr)/rows;
			var d = theme.scrollTableGetFV(cout,lr,fr,rows,totalrows,fv);
 			if (d!=fv) {
 				// only submit if firstvisible changed
 				status.innerHTML = d + "-" + (d+rows-1) + " / " + totalrows + "...";
 				status.style.display = "";				
 				// always immediate
 				theme.setVariable(client, fvVar, d, true);
 			} else {
 				// else realign
 				status.style.display = "none";
 				cout.scrollTop = fr;
 			}
 	};
} 
DefaultTheme.prototype.scrollTableRecalc = function(pid,target) {
	var defPad = 7;
	var div = target.ownerDocument.getElementById(pid);
	var wholeWidth = div.initialWidth;
	var colWidths = div.colWidths;
	if (!colWidths) {
		colWidths = new Object();
		div.colWidths = colWidths;
	}
	var hout = target.ownerDocument.getElementById(pid+"hout");
    var cout = target.ownerDocument.getElementById(pid+"cout");
 	var hin = target.ownerDocument.getElementById(pid+"hin");
    var cin = target.ownerDocument.getElementById(pid+"cin");
    var h = hin.getElementsByTagName("td");
    var c = cin.getElementsByTagName("td");           
    var whole = 0;   
    var col = -1;
    for (var i = 0;i<h.length;i++) {    
        if (!h[i].getAttribute||!h[i].getAttribute("id")||h[i].getAttribute("id").indexOf(pid+"he")<0) {
            continue;
        }
        col++;
        // colWidth, or whole width if only one column
        var cw = (h.length>1?colWidths[h[i].getAttribute("cid")]:hout.clientWidth-20);
        var w1 = h[i].firstChild.clientWidth + defPad; 
        var w2 = (c[col]?c[col].firstChild.clientWidth + defPad:0);
                                
        var w = parseInt((cw?cw:(w1>w2?w1:w2)));                       
        h[i].width = w;
        h[i].style.width = w+"px";
        h[i].firstChild.style.width = w+"px";
        var rows = c.length/h.length;
        for (var j=0;j<rows;j++) {
        	var idx = j*h.length+col;
	        if (c[idx]) {
		        c[idx].width = w;
		        c[idx].firstChild.style.width = w+"px";
		        c[idx].style.width = w+"px";
		        colWidths[h[i].getAttribute("cid")] = w;
	        }
        }
    	whole += parseInt(w);        
    }
}
// Header order drag & drop	
DefaultTheme.prototype.addToDragOrderGroup = function (client,theme,element,group,variable,sortVar,sortascVar,sortasc) {
	element.dragGroup = group;
	if (!group.elements) {
		group.elements = new Array();
	}
	var idx = group.elements.length;
	group.elements[idx] = element;
	
	var mouseDragListener = function (e) {
			var evt = client.getEvent(e);
			evt.stop();
			element.ownerDocument.onselectstart = function() {return false;}
			var target = element.target;
			target.style.position = "relative";
			target.style.top = "5px";
			try {
				target.style.left = -(target.origX-evt.mouseX+10)+"px";
			} catch (err) {
				client.error("Failed: d&d target.style.left="+ (-(target.origX-evt.mouseX+10)+"px"));
			}
			var dragGroup = element.dragGroup;
			dragGroup.moved = true;
			var els = dragGroup.elements;
			for (var i=0;i<els.length;i++) {
				if (i==element.idx) continue;
				var el = els[i];
				var p = client.getElementPosition(el);
				if (i!=dragGroup.origIdx&&i-1!=dragGroup.origIdx&&p.x < evt.mouseX && p.x+p.w/2 > evt.mouseX) {
						dragGroup.targetIdx = i; 
						el.style.borderLeft = "1px solid black";
						el.style.borderRight = "";
						break;
				} else if (i!=dragGroup.origIdx&&i+1!=dragGroup.origIdx && p.x+p.w/2 < evt.mouseX && p.x+p.w > evt.mouseX) {
						dragGroup.targetIdx = i+1;
						el.style.borderRight = "1px solid black";
						el.style.borderLeft = "";
						break;
				} else {
					dragGroup.targetIdx = dragGroup.origIdx;
					el.style.borderRight = "";
					el.style.borderLeft = "";
				}	
			}
	}
	
	var mouseUpListener = function(e) {
			client.removeEventListener(element.ownerDocument.body,"mousemove",mouseDragListener);
			client.removeEventListener(element.ownerDocument.body,"mouseup",arguments.callee);
			var evt = client.getEvent(e);
			evt.stop();
			element.ownerDocument.onselectstart = null;
			element.target.style.background = "";
			element.dragGroup.dragging = false;
			if (element.dragGroup.dragTM) {
				clearTimeout(element.dragGroup.dragTM);
			}
			if (!element.dragGroup.moved) {
				if (sortVar) {
					var cid = element.getAttribute("cid");
					var sorted = element.getAttribute("sorted");
					//alert("sorting "+element.getAttribute("cid") + " " + sorted + " " + sortasc);
					if (sorted) {
						theme.setVariable(client, sortascVar, !sortasc, true);
					} else {
						theme.setVariable(client, sortVar, cid, true);
					}
				}
			}
			var origIdx = element.dragGroup.origIdx;
			var targetIdx = element.dragGroup.targetIdx;
			if (origIdx != targetIdx) {
				var els = element.dragGroup.elements;
				var neworder = new Array();
				for (var i=0;i<els.length;i++) {
					if (i==origIdx) continue;
					if (i==targetIdx) {
						neworder[neworder.length] = els[origIdx].getAttribute("cid");
					} 
					neworder[neworder.length] = els[i].getAttribute("cid");
				}
				theme.setVariable(client, variable, neworder, true);
			} else {
				element.target.style.left = "0px";
				element.target.style.top = "0px";
			}
	};
	
	client.addEventListener(element,"mousedown", function(e) {
		var evt = client.getEvent(e);
		evt.stop();
		element.dragGroup.dragging = true;
		element.dragGroup.moved = false;
		element.dragGroup.origIdx = idx;
		element.dragGroup.targetIdx = idx;
		element.target = evt.target;
		evt.target.dragGroup = element.dragGroup;
		evt.target.origX = evt.mouseX;
		evt.target.idx = idx;
		if (element.dragGroup.dragTM) {
			clearTimeout(element.dragGroup.dragTM);
		}
		client.addEventListener(element.ownerDocument.body,"mouseup", mouseUpListener);
		if (variable) {
			// column reordering allowed
			group.dragTM = setTimeout(function () {
				if(element.dragGroup.dragging) {
					evt.target.style.background = "white";
					client.addEventListener(element.ownerDocument.body,"mousemove",mouseDragListener);				
				}
			},100);
		}
	});
	//client.addEventListener(element,"mouseup", mouseUpListener);
}

DefaultTheme.prototype.renderSelect = function(renderer,uidl,target,layoutInfo) {
			
	var theme = renderer.theme;
	var client = renderer.client;
	
	// Create containing element
	var div = renderer.theme.createPaintableElement(renderer,uidl,target,layoutInfo);	
	if (uidl.getAttribute("invisible")) return; // Don't render content if invisible

	// Create selection variable
	var selectMode = uidl.getAttribute("selectmode");
	var selectable = selectMode == "multi" || selectMode == "single";
	var immediate = ("true" == uidl.getAttribute("immediate"));
	var disabled = ("true" == uidl.getAttribute("disabled"));
	var readonly = ("true" == uidl.getAttribute("readonly"));
	var newitem = ("true" == uidl.getAttribute("allownewitem"));
	var focusid = uidl.getAttribute("focusid");
	var tabindex = uidl.getAttribute("tabindex");
	
	var selectionVariable = theme.createVariableElementTo(div,theme.getVariableElement(uidl,"array","selected"));

	// Render default header
	theme.renderDefaultComponentHeader(renderer,uidl,div,layoutInfo);

	// Create select input	
	var select = theme.createElementTo(div,"select");
	if (focusid) select.focusid = focusid;
	if (tabindex) select.tabIndex = tabindex;
	if (selectMode == "multi") {
		select.setAttribute("multiple", "true");
		if (newitem) {
			theme.createElementTo(div,"br");
		} 
	} else {
		if (newitem) {
			theme.addCSSClass(div,"nobr");
		} 
	}
	var options = theme.getFirstElement(uidl,"options");
	if (options != null) {
		options = options.getElementsByTagName("so");
		if (options && options.length && selectMode == "multi") {
			select.size = (options.length>7?7:options.length);
		}
	}	
	if (disabled||readonly) {
		select.disabled = "true";
	} else {
		// Add change listener
		theme.addSetVarListener(theme,client,select,"change",selectionVariable,select,immediate);
	}
	// Empty selection for WA compatibility
	var optionNode = theme.createElementTo(select,"option");
	theme.createTextNodeTo(optionNode,"-");
	
	// Selected options
	if (options != null && options.length >0) {
		for (var i=0; i<options.length;i++) {
			var optionNode = theme.createElementTo(select,"option");
			optionNode.setAttribute("value", options[i].getAttribute("key"));	
			if (options[i].getAttribute("selected") == "true") {
				optionNode.selected="true";	
			}
			theme.createTextNodeTo(optionNode,options[i].getAttribute("caption"));
		}
	}
	
	if (newitem) {
		var input = theme.createInputElementTo(div,"text");
		var button = theme.createElementTo(div,"button");
		theme.createTextNodeTo(button,"+");
		var newitemVariable = theme.createVariableElementTo(div,theme.getVariableElement(uidl,"string","newitem"));
		theme.addSetVarListener(theme,client,input,"change",newitemVariable,input,true);
	}
}


DefaultTheme.prototype.renderSelectOptionGroup = function(renderer,uidl,target,layoutInfo) {
	// TODO: 
	// 	- newitem currently always immediate, change
	//	- optiongrouphorizontal style	
					
	var theme = renderer.theme;
	var client = renderer.client;
	
	// Create containing element
	var div = theme.createPaintableElement(renderer,uidl,target);	
	if (uidl.getAttribute("invisible")) return; // Don't render content if invisible
	
	// Create selection variable
	var selectMode = uidl.getAttribute("selectmode");
	var selectable = selectMode == "multi" || selectMode == "single";
	var immediate = ("true" == uidl.getAttribute("immediate"));
	var disabled = ("true" == uidl.getAttribute("disabled"));
	var readonly = ("true" == uidl.getAttribute("readonly"));
	var newitem = ("true" == uidl.getAttribute("allownewitem"));
	var selectionVariable = theme.createVariableElementTo(div,theme.getVariableElement(uidl,"array","selected"));
	
	// Render default header
	theme.renderDefaultComponentHeader(renderer,uidl,div,layoutInfo);

	// Create select input	
	var select = theme.createElementTo(div,"div");
	var options = theme.getFirstElement(uidl,"options");
	if (options != null) {
		options = options.getElementsByTagName("so");
	}	
	
	// Selected options
	if (options != null && options.length >0) {
		for (var i=0; i<options.length;i++) {
			var optionUidl = options[i];
			var iconUrl = optionUidl.getAttribute("icon");
			var div = theme.createElementTo(select,"div", "nobr");
			var key = optionUidl.getAttribute("key");
			
			// Create input
			var inputName = "input"+uidl.getAttribute("id");
			var inputId = inputName+i;
			var input = null;
			var caption =  optionUidl.getAttribute("caption");
			var html;
			if (selectMode == "multi") {
				html = "<input class=\"option\" type=checkbox name=\""+inputName+"\" id=\""+inputId+"\" ";
			} else {	
				html = "<input class=\"option\" type=radio name=\""+inputName+"\" id=\""+inputId+"\" ";			
			}
			if (disabled||readonly) html += " disabled=\"true\" "
			if (optionUidl.getAttribute("selected") == "true") {
				html += " checked=\"true\" "
			} 
			html += " ><label class=\"clickable\" for=\""+inputId+"\">";
			if (caption) html += caption;
			if (iconUrl) {
				if (iconUrl.indexOf("theme://") == 0) {
	    			iconUrl = (theme.iconRoot != null ? theme.iconRoot : theme.root) 
	    					+ iconUrl.substring(8);
	    		}
	    		html += "<IMG src=\""+iconUrl+"\" class=\"icon\">";
			}				
			html += "</label>";
			
			div.innerHTML = html;
			if (!(disabled||readonly)) {
				var input = div.firstChild;
				if (selectMode == "multi") {
					theme.addToggleVarListener(theme,client,input,"click",selectionVariable,key,immediate);
				} else {
					theme.addSetVarListener(theme,client,input,"click",selectionVariable,key,immediate);
				} 
			}
		}
	}
	if (newitem) {
		var ni = theme.createElementTo(div,"div","newitem");
		var input = theme.createInputElementTo(ni,"text");
		var button = theme.createElementTo(ni,"button");
		theme.createTextNodeTo(button,"+");
		var newitemVariable = theme.createVariableElementTo(ni,theme.getVariableElement(uidl,"string","newitem"));
		theme.addSetVarListener(theme,client,input,"change",newitemVariable,input,true);
	}
}

DefaultTheme.prototype.renderLabel = function(renderer,uidl,target,layoutInfo) {
			
			// Create container element
			var div = renderer.theme.createPaintableElement(renderer,uidl,target,layoutInfo);
			if (uidl.getAttribute("invisible")) return; // Don't render content if invisible

			// Create default header
			var caption = renderer.theme.renderDefaultComponentHeader(renderer,uidl,div,layoutInfo);

			// Render children to div
			if (uidl.childNodes.length>0) {
				div = renderer.theme.createElementTo(div,"div");
				renderer.theme.renderChildNodes(renderer, uidl, div);
			}
			if (div.innerHTML == "") div.innerHTML = "&nbsp;";
}

DefaultTheme.prototype.renderData = function(renderer,uidl,target) {

	var html = "";
	for (var i=0; i<uidl.childNodes.length; i++) {
		var child = uidl.childNodes.item(i);
		if (child.nodeType == Node.ELEMENT_NODE) {
			html += renderer.theme.nodeToString(child,true);
		} if (child.nodeType == Node.TEXT_NODE && child.data != null) {
			html += child.data;
		}
	}
	target.innerHTML = html;
				
}

DefaultTheme.prototype.renderPre = function(renderer,uidl,target) {

	// Create pre node
	var pre = renderer.theme.createElementTo(target,"pre");
	
	var html = "";
	for (var i=0; i<uidl.childNodes.length; i++) {
		var child = uidl.childNodes.item(i);
		if (child.nodeType == Node.ELEMENT_NODE) {
			html += renderer.theme.nodeToString(child,true);
		} if (child.nodeType == Node.TEXT_NODE && child.data != null) {
			html += child.data;
		}
	}
	pre.innerHTML = html;				
}


DefaultTheme.prototype.renderButton = function(renderer,uidl,target,layoutInfo) {
			// Branch for checkbox

			if (uidl.getAttribute("type") == "switch") {
				return renderer.theme.renderCheckBox(renderer,uidl,target,layoutInfo);
			}

			// Shortcuts
			var theme = renderer.theme;
			var client = renderer.client;
			
			var disabled = "true"==uidl.getAttribute("disabled");
			var readonly = "true"==uidl.getAttribute("readonly");
			var immediate = "true"==uidl.getAttribute("immediate");
			var tabindex = uidl.getAttribute("tabindex");
			
			var linkStyle = "link"==uidl.getAttribute("style");
			
			var div = theme.createPaintableElement(renderer,uidl,target,layoutInfo);
			if (uidl.getAttribute("invisible")) return; // Don't render content if invisible
			
			div = renderer.theme.createElementTo(div,"div",(linkStyle?"link clickable":"outset clickable"));;
			var inner = renderer.theme.createElementTo(div,"div",(linkStyle?"pad":"border pad bg"));
			
			var caption = theme.renderDefaultComponentHeader(renderer,uidl,inner);
			theme.addTabtoHandlers(client,theme,caption,div,tabindex,("default"==uidl.getAttribute("style")));
			
			if (!disabled&&!readonly) {
				// Handlers
				var v = theme.getVariableElement(uidl,"boolean", "state");
				if (v != null) {
					var varId = v.getAttribute("id");
					theme.addSetVarListener(theme,client,div,"click",varId,"true",immediate);
					theme.addAddClassListener(theme,client,div,"mouseover","over",div);
					theme.addRemoveClassListener(theme,client,div,"mouseout","over",div);
				}		
			}
				
}

DefaultTheme.prototype.renderCheckBox = function(renderer,uidl,target,layoutInfo) {
		// Shortcuts
		var theme = renderer.theme;
		var client = renderer.client;
		
		var div = theme.createPaintableElement(renderer,uidl,target,layoutInfo);
		if (uidl.getAttribute("invisible")) return; // Don't render content if invisible

		var immediate = (uidl.getAttribute("immediate") == "true");
		var disabled = (uidl.getAttribute("disabled") == "true");
		var readonly = (uidl.getAttribute("readonly") == "true");
		var tabindex = uidl.getAttribute("tabindex");
		
		// Create input
		var div = theme.createElementTo(div,"div","nocappad nobr");
		var input = theme.createInputElementTo(div,"checkbox");
		input.setAttribute("id", "input"+uidl.getAttribute("id"));
		if (tabindex) input.tabIndex = tabindex;
		if (disabled||readonly) {
			input.disabled = "true";
		}
		
		// Create label
		var label = theme.createElementTo(div,"label", "clickable");
		var cap = theme.renderDefaultComponentHeader(renderer,uidl,label);
		theme.addCSSClass(cap,"inline");
		label.setAttribute("for","input"+uidl.getAttribute("id"));
		// Value
		var v = theme.getVariableElement(uidl,"boolean", "state");
		if ( v!= null) {
			var varId = v.getAttribute("id");
			input.checked = (v.getAttribute("value") == "true");			
			// Attach listener
			theme.addSetVarListener(theme,client,input,(immediate?"click":"change"),varId,input,immediate);
		}
}


///////
/* TODO merge or delete the rest

/**
 *   Render tree as a menubar.
 *   NOTE:
 *   First level nodes are not selectable - menu opens with click. 
 *   If style == "coolmenu", immediate is forced.
 *  
 */

DefaultTheme.prototype.renderTreeMenu = function(renderer,uidl,target,layoutInfo) {
			
	var theme = renderer.theme;
	
	// Create container element
	var div = renderer.theme.createPaintableElement(renderer,uidl,target,layoutInfo);
	if (uidl.getAttribute("invisible")) return; // Don't render content if invisible

	// Get tree attributes
	var style = uidl.getAttribute("style");
	var immediate = ("true" == uidl.getAttribute("immediate")||style=="coolmenu");
	var disabled = ("true" == uidl.getAttribute("disabled"));
	var readonly = ("true" == uidl.getAttribute("readonly"));
	var selectMode = uidl.getAttribute("selectmode");
	var selectable = selectMode == "multi" || selectMode == "single";
	var selected;
	if (selectable) {
		selected = new Object();
	}
	var selectionVariable = theme.createVariableElementTo(div,theme.getVariableElement(uidl,"array","selected"));
	var expandVariable = theme.createVariableElementTo(div,theme.getVariableElement(uidl,"array","expand"));
	var collapseVariable = theme.createVariableElementTo(div,theme.getVariableElement(uidl,"array","collapse"));

	var actions = null;
	var actionVar = null;
	var alNode = theme.getFirstElement(uidl,"actions")
	if (alNode) {
		actionVar = theme.createVariableElementTo(div,theme.getVariableElement(alNode,"string","action"));
		actions = new Object();
		var ak = alNode.getElementsByTagName("action");
		for (var i=0;i<ak.length;i++) {
			actions[ak[i].getAttribute("key")] = ak[i].getAttribute("caption");
		}
	}
	delete alNode;

	// Create default header
	var caption = renderer.theme.renderDefaultComponentHeader(renderer,uidl,div,layoutInfo);

	// Content DIV
	var content = theme.createElementTo(div,"div","content menu"); 
	
	// Iterate all nodes
	for (var i = 0; i< uidl.childNodes.length;i++) {
		var node = uidl.childNodes[i];
		if (node.nodeName == "node" || node.nodeName == "leaf") {
			theme.renderTreeMenuNode(renderer,node,content,selectable,selectMode,selected,selectionVariable,expandVariable,collapseVariable,actions,actionVar,immediate,disabled,readonly,0);
		} 	
	}
}

DefaultTheme.prototype.renderTreeMenuNode = function(renderer,node,target,selectable,selectMode,selected,selectionVariable,expandVariable,collapseVariable,actions,actionVar,immediate,disabled,readonly,level) {

	var theme = renderer.theme;
	var client = renderer.client;

	var n = theme.createElementTo(target,"div",(level==0?"inline clickable":"clickable"));
	
	
	// Caption
	var cap = theme.createElementTo(n,"div","inline caption pad");
	theme.createTextNodeTo(cap,node.getAttribute("caption"));	

	// Expand/collapse/spacer button
	var img = theme.createElementTo(n,"img","icon");
    img.align = "absbottom";
	var key = node.getAttribute("key");	
	var icon = node.getAttribute("icon");
    if (icon) {
        var iconurl = theme.root+icon.split("theme:")[1];
        var iimg = theme.createElementTo(n,"img","icon");
	    iimg.src = iconurl;
    }


	// Hover effects
	if (!disabled&&!readonly) {
		theme.addAddClassListener(theme,client,n,"mouseover","selected",n);
		theme.addRemoveClassListener(theme,client,n,"mouseout","selected",n);
	}
	
	// Server-side selection
	if (selectable && node.getAttribute("selected") == "true") {
		theme.addCSSClass(n,"selected");
		selected[key] = n;
	}

	// Indicate selection	
	if (theme.listContainsInt(selectionVariable.value,key)) {
		theme.addCSSClass(n, "selected");
	}

	// Selection listeners
	if (selectable && !disabled && (level != 0 || node.nodeName == "leaf")) {
		if (!readonly) {		
			if (selectMode == "single") {
				theme.addAddClassListener(theme,client,n,"click","selected",n,selected);
				theme.addSetVarListener(theme,client,n,"click",selectionVariable,key,immediate);
			
			} else if (selectMode == "multi") {	
				theme.addToggleClassListener(theme,client,n,"click","selected");
				theme.addToggleVarListener(theme,client,n,"click",selectionVariable,key,immediate);
			
			}
		}
	} 
	
	// Actions
	if (!disabled && !readonly) {
		for (var i = 0; i< node.childNodes.length;i++) {
			var childNode = node.childNodes[i];
			if (childNode.nodeName == "al" ) {
				theme.renderActionPopup(renderer,childNode,n,actions,actionVar,key,1); // TODO check
			} 
		}	
	}
	
	// Render all sub-nodes
	if (node.nodeName == "node") {
		var subnodes = theme.createElementTo(target,"div","hide popup");
        var inner = theme.createElementTo(subnodes,"div","border");
        theme.addTogglePopupListener(theme,client,n,(level==0?"click":"mouseover"),subnodes,0,null,n);
        //theme.addToggleClassListener(theme,client,n,(level==0?"click":"mouseover"),"hide",subnodes)
		if (node.childNodes != null && node.childNodes.length >0) {
			img.src = theme.root + "img/tree/empty.gif";
			img.expanded = "true";
		} else {
			img.src = theme.root + "img/tree/empty.gif";
			img.expanded = "false";
		}
		for (var i = 0; i< node.childNodes.length;i++) {
			var childNode = node.childNodes[i];
			if (childNode.nodeName == "node" || childNode.nodeName == "leaf") {
				theme.renderTreeMenuNode(renderer,childNode,inner,selectable,selectMode,selected,selectionVariable,expandVariable,collapseVariable,actions,actionVar,immediate,disabled,readonly,level+1);
			} 
		}	
		
		// Add event listener
		if (!disabled&&level!=0) {
			var target = (selectable&&!readonly?img:n);
			theme.addToggleClassListener(theme,client,target,"mouseup","hidden",subnodes);
			theme.addExpandNodeListener(theme,client,img,"mouseup",subnodes,expandVariable,collapseVariable,key,immediate,target);
			theme.addStopListener(theme,client,target,"mouseup");
			theme.addStopListener(theme,client,target,"click");
		}
		
	} else {
			img.src = theme.root + "img/tree/empty.gif";			
	}
}

/**
* 5.6.2006 - Jouni Koivuviita
* New innerHTML components
* RENAMED for testing both - marc
*/

DefaultTheme.prototype.renderNewPanel = function(renderer,uidl,target,layoutInfo) {
    // Shortcuts
    var theme = renderer.theme;
	var style = uidl.getAttribute("style");
	// Create component element
	var div = theme.createPaintableElement(renderer,uidl,target); 
    
	/* New panel theme, 8.6.2006 - Jouni Koivuviita */
	div.innerHTML = "<div class=\"top\"><div class=\"right\"></div><div class=\"left\"><div class=\"title\"></div></div></div><div class=\"middle\"></div><div class=\"bottom\"><div class=\"right\"></div><div class=\"left\"></div></div>";
	var cap = div.firstChild.firstChild.nextSibling.firstChild;
	var content = div.childNodes[1];
	theme.applyWidthAndHeight(uidl,div.childNodes[1],"height");
	theme.applyWidthAndHeight(uidl,div,"width");
	
	/*	
    div.innerHTML = "<TABLE width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><TR height=\"35\"><TD width=\"12\"><IMG onload=\"png(this);\" src=\""+theme.root+"img/top-left.png\"></TD><TD style=\"background: url('"+theme.root+"img/top.png') !important;background: none;background-position:right;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+theme.root+"img/top.png', sizingMethod='scale');\"></TD><TD  width=\"12\"><IMG onload=\"png(this);\" src=\""+theme.root+"img/top-right.png\"></TD></TR><TR><TD style=\"background: url('"+theme.root+"img/left.png') !important;background: none;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+theme.root+"img/left.png', sizingMethod='scale');\"></TD><TD bgcolor=white></TD><TD style=\"background: url('"+theme.root+"img/right.png') !important;background: none;background-position:right;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+theme.root+"img/right.png', sizingMethod='scale');\"></TD></TR><TR height=\"12\"><TD><IMG onload=\"png(this);\" src=\""+theme.root+"img/bottom-left.png\"></TD><TD style=\"background: url('"+theme.root+"img/bottom.png') !important;background: none;background-position:right;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+theme.root+"img/bottom.png', sizingMethod='scale');\"></TD><TD><IMG onload=\"png(this);\" src=\""+theme.root+"img/bottom-right.png\"></TD></TR></TABLE>";
    var cap = div.firstChild.firstChild.firstChild.childNodes[1];
    var content = div.firstChild.firstChild.childNodes[1].childNodes[1];
	*/
    
    theme.renderDefaultComponentHeader(renderer,uidl,cap);
    theme.renderChildNodes(renderer, uidl, content);
}

DefaultTheme.prototype.renderNewPanelModal = function(renderer,uidl,target,layoutInfo,alignment) {
    // Shortcuts
    var theme = renderer.theme;
    //var parentTheme = theme.parent;
	// Create component element
	var div = theme.createPaintableElement(renderer,uidl,target); 
    var html = "<IFRAME frameborder=\"0\" style=\"border:none;z-index:9997;position:absolute;top:0px;left:0px;width:100%;height:100%;background-color:white;filter: alpha(opacity=80);opacity:0.8;\"></IFRAME>";
    html += "<DIV align=\"center\" style=\"position:absolute;top:0px;width:100%;left:0px;z-index:9999;filter: alpha(opacity=100);opacity:1;\"><TABLE  cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><TR height=\"35\"><TD width=\"12\"><IMG onload=\"png(this);\" src=\""+theme.root+"img/top-left.png\"></TD><TD style=\"background: url('"+theme.root+"img/top.png') !important;background: none;background-position:right;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+theme.root+"img/top.png', sizingMethod='scale');\"></TD><TD  width=\"12\"><IMG onload=\"png(this);\" src=\""+theme.root+"img/top-right.png\"></TD></TR><TR><TD style=\"background: url('"+theme.root+"img/left.png') !important;background: none;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+theme.root+"img/left.png', sizingMethod='scale');\"></TD><TD bgcolor=white ></TD><TD style=\"background: url('"+theme.root+"img/right.png') !important;background: none;background-position:right;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+theme.root+"img/right.png', sizingMethod='scale');\"></TD></TR><TR height=\"12\"><TD><IMG onload=\"png(this);\" src=\""+theme.root+"img/bottom-left.png\"></TD><TD style=\"background: url('"+theme.root+"img/bottom.png') !important;background: none;background-position:right;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+theme.root+"img/bottom.png', sizingMethod='scale');\"></TD><TD><IMG onload=\"png(this);\" src=\""+theme.root+"img/bottom-right.png\"></TD></TR></TABLE><DIV>";
    div.innerHTML = html;
    var overlay = div.firstChild;
    overlay.style.width = div.ownerDocument.body.offsetWidth + "px";
    overlay.style.height = div.ownerDocument.body.offsetHeight + "px";
    var table = div.childNodes[1].firstChild;
    var cap = table.firstChild.firstChild.childNodes[1];
    var content = table.firstChild.childNodes[1].childNodes[1];
    
    theme.renderDefaultComponentHeader(renderer,uidl,cap);
    theme.renderChildNodes(renderer, uidl, content);
   
   	var ifrdiv = theme.createElementTo(div,"div");
   
   html = "<IFRAME frameborder=\"0\" style=\"border:none;z-index:9998;position:absolute;top:"+(div.childNodes[1].offsetTop+5)+"px;left:"+(table.offsetLeft+5)+"px;width:"+(table.offsetWidth-7)+"px;height:"+(table.offsetHeight-7)+"px;background-color:white;filter: alpha(opacity=100);opacity:1;\"></IFRAME>";
   ifrdiv.innerHTML += html;
}

DefaultTheme.prototype.renderNewPanelLight = function(renderer,uidl,target,layoutInfo) {
    // Shortcuts
    var theme = renderer.theme;
	var style = uidl.getAttribute("style");
	// Create component element
	var div = theme.createPaintableElement(renderer,uidl,target); 
                        
    div.innerHTML = "<TABLE width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><TR><TD width=\"12\"><IMG onload=\"png(this);\" src=\""+theme.root+"img/top-left-lite.png\"></TD><TD style=\"background: url('"+theme.root+"img/top-lite.png') !important;background: none;background-position:right;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+theme.root+"img/top-lite.png', sizingMethod='scale');\"></TD><TD width=\"12\"><IMG onload=\"png(this);\" src=\""+theme.root+"img/top-right-lite.png\"></TD></TR><TR><TD style=\"background: url('"+theme.root+"img/left.png') !important;background: none;background-position:right;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+theme.root+"img/left.png', sizingMethod='scale');\"></TD><TD bgcolor=white></TD><TD style=\"background: url('"+theme.root+"img/right.png') !important;background: none;background-position:right;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+theme.root+"img/right.png', sizingMethod='scale');\"></TD></TR><TR><TD><IMG onload=\"png(this);\" src=\""+theme.root+"img/bottom-left.png\"></TD><TD style=\"background: url('"+theme.root+"img/bottom.png') !important;background: none;background-position:right;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+theme.root+"img/bottom.png', sizingMethod='scale');\"></TD><TD><IMG onload=\"png(this);\" src=\""+theme.root+"img/bottom-right.png\"></TD></TR></TABLE>";

    var content = div.firstChild.firstChild.childNodes[1].childNodes[1];
    
    theme.renderDefaultComponentHeader(renderer,uidl,content);
    theme.renderChildNodes(renderer, uidl, content);
}

DefaultTheme.prototype.renderNewPanelNone = function(renderer,uidl,target,layoutInfo) {
    // Shortcuts
    var theme = renderer.theme;
	var style = uidl.getAttribute("style");
	// Create component element
	var div = theme.createPaintableElement(renderer,uidl,target); 
            
    var content = theme.createElementTo(div,"div");
   
    theme.renderDefaultComponentHeader(renderer,uidl,content);
    theme.renderChildNodes(renderer, uidl, content);
}

DefaultTheme.prototype.renderNewTabSheet = function(renderer,uidl,target,layoutInfo) {
    // Shortcuts
    var theme = renderer.theme;

	// Create component element
	var div = theme.createPaintableElement(renderer,uidl,target); 
    if (uidl.getAttribute("invisible")) return;  

	var style = uidl.getAttribute("style");
    var disabled  = ("true"==uidl.getAttribute("disabled"));
	
	var cdiv = theme.createElementTo(div,"div");
	var caption = theme.renderDefaultComponentHeader(renderer,uidl,cdiv,layoutInfo);
	div = theme.createElementTo(div,"div");
         
	// Tabs
	var tabNodes = theme.getChildElements(uidl,"tabs");
	if (tabNodes != null && tabNodes.length >0) tabNodes = theme.getChildElements(tabNodes[0],"tab");
	var selectedTabNode = null;
	if (tabNodes != null && tabNodes.length >0) {
	    var html = "<TABLE width=\"100%\" class=\"tabsheet-table\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><TBODY>";
		html += "<TR valign=\"bottom\"><TD></TD>";
		
		var posttabs = "<TR valign=\"top\"><TD><IMG  onload=\"png(this);\" src=\""+theme.root+"img/tabsheet/top-left-lite.png\"/></TD>";
		var len = tabNodes.length;
		for (var i=0; i<len;i++) {
			var tab = tabNodes[i];
			var caption = tab.getAttribute("caption");
			var icon = tab.getAttribute("icon");
			if (icon) icon = theme.root+icon.split("theme://")[1];
			var selected = ("true"==tab.getAttribute("selected"));
			var disabled = ("true"==tab.getAttribute("disabled"));
			var offset = (selected?6:4);
			
			var variant = "";
			if (disabled) {
				variant = "-dis";
			} else if (selected) {
				variant = "-on";
			}

			if (selected) selectedTabNode = tab;
			
   			html += "<TD width=\"1\" align=\"right\"><IMG onload=\"png(this);\" onload=\"png(this);\" src=\""+theme.root+"img/tabsheet/top-left"+variant+".png\"/></TD><TD class=\""+(disabled?"caption":"caption clickable")+"\" style=\"background-image: url('"+theme.root+"img/tabsheet/top"+variant+".png') !important;background: none;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+theme.root+"img/tabsheet/top"+variant+".png', sizingMethod='scale');\">"
   			html += "<DIV style=\"padding-top:0.5em;\" class=\"caption"+(selected&&!disabled?"":" clickable")+"\">";
   			if (icon) html += "<IMG onload=\"png(this);\" class=\"icon\" src=\""+icon+"\"/>";
   			html += caption+"</DIV>";
   			html += "</TD><TD><IMG  onload=\"png(this);\" src=\""+theme.root+"img/tabsheet/top-right"+variant+".png\"/></TD>";	
   			
   			
   			// POSTTABS		     
   			posttabs += "<TD align=\"right\" style=\"background-image: url('"+theme.root+"img/tabsheet/top-lite.png') !important;background: none;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+theme.root+"img/tabsheet/top-lite.png', sizingMethod='scale');\"><IMG  onload=\"png(this);\" height=\""+(selected?6:4)+"\" width=\"8\" src=\""+theme.root+"img/tabsheet/tab-left.png\"/></TD><TD "+(selected?"bgcolor=\"white\"":"style=\"background-image: url('"+theme.root+"img/tabsheet/top-lite.png') !important;background: white;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+theme.root+"img/tabsheet/top-lite.png', sizingMethod='scale');\"")+"></TD><TD style=\"background-image: url('"+theme.root+"img/tabsheet/top-lite.png') !important;background: none;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+theme.root+"img/tabsheet/top-lite.png', sizingMethod='scale');\"><IMG  onload=\"png(this);\" height=\""+(selected?6:4)+"\" width=\"8\" src=\""+theme.root+"img/tabsheet/tab-right.png\"/></TD>";			
		}
   		html += "<TD width=\"100%\"></TD></TR>"+posttabs+"<TD style=\"background-image: url('"+theme.root+"img/tabsheet/top-lite.png') !important;background: none;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+theme.root+"img/tabsheet/top-lite.png', sizingMethod='scale');\" ></TD><TD><IMG  onload=\"png(this);\" src=\""+theme.root+"img/tabsheet/top-right-lite.png\"/></TD></TR>";
   		
    	//Content
    	html +="</TBODY></TABLE><TABLE width=\"100%\" class=\"tabsheet-table\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><TBODY><TR valign=\"top\"><TD style=\"width:12px;background-image: url('"+theme.root+"img/tabsheet/left.png') !important;background: none;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+theme.root+"img/tabsheet/left.png', sizingMethod='scale');\"></TD><TD style=\"width:100% !important;width:auto;\" class=\"tabsheet-content\" bgcolor=\"white\" colspan=\""+(len*3+1)+"\"><DIV></DIV></TD><TD width=\"12\" style=\"background-image: url('"+theme.root+"img/tabsheet/right.png') !important;background: none;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+theme.root+"img/tabsheet/right.png', sizingMethod='scale');\"></TD></TR>";
		html += "<TR height=\"12\" valign=\"top\"><TD width=\"8\"><IMG onload=\"png(this);\" src=\""+theme.root+"img/tabsheet/bottom-left.png\"></TD><TD style=\"background-image: url('"+theme.root+"img/tabsheet/bottom.png') !important;background: none;filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+theme.root+"img/tabsheet/bottom.png', sizingMethod='scale');\" colspan=\""+(len*3+1)+"\"></TD><TD><IMG  onload=\"png(this);\" src=\""+theme.root+"img/tabsheet/bottom-right.png\"></TD></TR></TBODY></TABLE>";
		div.innerHTML = html;
		
		// TODO click listeners
		
		if (!disabled) {
			var varId = theme.getVariableElement(uidl,"string","selected").getAttribute("id");		
			for (var i=0; i<len;i++) {
				var tabNode = tabNodes[i];
				if (tabNode == selectedTabNode||("true"==tabNode.getAttribute("disabled"))) continue;
				var key = tabNode.getAttribute("key");
				var tab = div.firstChild.firstChild.firstChild.childNodes[2+i*3];
				theme.addAddClassListener(theme,client,tab,"mouseover","over",tab);
				theme.addRemoveClassListener(theme,client,tab,"mouseout","over",tab);
				theme.addSetVarListener(theme,client,tab,"click",varId,key,true);
			}		
		}
		
		var content = div.childNodes[1].firstChild.firstChild.childNodes[1];
		if (selectedTabNode) {
			theme.renderChildNodes(renderer,selectedTabNode, content);
		}
		
	}
	
}