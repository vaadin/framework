/** Construct example theme that extends some other theme
 *  typically the DefaultTheme.
 *
 *  @param themeRoot The base URL of theme resources.
 *  @param defaultTheme Theme to be extended.
 *  
 */


function ExampleTheme(themeRoot, defaultTheme) {
	this.themeName = "ExampleTheme";
	this.root = themeRoot;
	this.parent = defaultTheme;
	
	// Tell the parent where to look for theme icons
	this.parent.iconRoot = this.root;	
}

/** Register all renderers to a ajax client.
 *
 * @param client The ajax client instance.
 */
ExampleTheme.prototype.registerTo = function(client) {

	// We register our own customlayout handler.
	// This way the layouts can be in different place.
	client.registerRenderer(this,"customlayout",null,this.renderCustomLayout);
	
}

ExampleTheme.prototype.renderCustomLayout = function(renderer,uidl,target,layoutInfo) {

	// Shortcuts
	var theme = renderer.theme;
	var parentTheme = theme.parent;
	
	// Get style
    var style = uidl.getAttribute("style");    
    if (style == null) return null;
    
    // Load the layout
    var url = theme.root + style + ".html";   
    var text = renderer.client.loadDocument(url,false); 
    if (text == null) return null; 

	// Create containing element
	var main = parentTheme.createPaintableElement(renderer,uidl,target);		
    
    var n = parentTheme.createElementTo(main, "div");
    n.setAttribute("id",uidl.getAttribute("id"));
    n.innerHTML=text;
    var divs = n.getElementsByTagName("div");
    for (var i=0; i<divs.length; i++) {
      var div = divs.item(i);
      var name = div.getAttribute("location");      
      if (name != null) {
        for (var j=0; j < uidl.childNodes.length; j++) {
          var c = uidl.childNodes.item(j);
          if (c.nodeType == Node.ELEMENT_NODE 
          		&& c.nodeName == "location" 
          		&& c.getAttribute("name") == name) {   
          		           
            for (var k=0; k<c.childNodes.length; k++) {
              var cc = c.childNodes.item(k); 
              if (cc.nodeType == Node.ELEMENT_NODE) {
                var parent = div.parentNode;
                
                // TODO
                if (parent != null) {
                	parentTheme.removeAllChildNodes(div);
                	var newNode = renderer.client.renderUIDL(cc,div);
                }
              }
            }
            
          }
        }
      }
    }    
}
