<%
  //
  // Demonstrates how Toolkit application can be integrated into jsp pages
  //
  String test = "This text comes from EmbeddedToolkit.jsp file";
  // Toolkit application name. This is servlet URL pattern, see web.xml
  String applicationName = "TableDemo";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
	"http://www.w3.org/TR/html4/loose.dtd">
 
 <html xmlns="http://www.w3.org/1999/xhtml" xmlns:ITMillToolkit40="http://itmill.com/toolkit">
 
 <head>
 <title>Embedding Toolkit to JSP pages</title>
 
	 <!-- Toolkit code block starts -->
	 
	 <NOSCRIPT><META http-equiv="refresh" content="0; url=?WA_NOSCRIPT=1" /></NOSCRIPT>
	 <link rel="stylesheet" href="<%=applicationName %>/RES/base/css/base-ajax.css" type="text/css" />
	 <link rel="stylesheet" href="<%=applicationName %>/RES/base/css/calendar-default.css" type="text/css" />
	 <script src="<%=applicationName %>/RES/base/script/ajax-client.js" type="text/javascript"></script>
	 <script src="<%=applicationName %>/RES/base/script/base-ajax-components.js" type="text/javascript"></script>
	 <script src="<%=applicationName %>/RES/base/ext/jscalendar/calendar.js" type="text/javascript"></script>
	 <script src="<%=applicationName %>/RES/base/ext/jscalendar/lang/calendar-en.js" type="text/javascript"></script>
	 <script src="<%=applicationName %>/RES/base/ext/jscalendar/calendar-setup.js" type="text/javascript"></script>
	 <script src="<%=applicationName %>/RES/base/ext/firebug/firebugx.js" type="text/javascript"></script>
	 <link rel="stylesheet" href="<%=applicationName %>/RES/corporate/css/corporate-ajax.css" type="text/css" />
	 <script src="<%=applicationName %>/RES/corporate/script/corporate-ajax-components.js" type="text/javascript"></script>
	 
	 <!-- Toolkit code block ends -->
	 
 </head>
 
 <!-- Toolkit code: for body set tag class with value itmtk -->
 <body class="itmtk">
 <center>
 <h3><%=test %> before Toolkit application.</h3>
 <hr />
 
	 <!-- Toolkit code block starts -->
	 
	 <div id="ajax-wait">Loading...</div>
	 <div id="ajax-window"></div>
	 <script language="JavaScript">
	 itmill.tmp = new itmill.Client(document.getElementById('ajax-window'),"<%=applicationName %>/UIDL/","<%=applicationName %>/RES/base/client/",document.getElementById('ajax-wait'));
	  (new itmill.themes.Base("<%=applicationName %>/RES/base/")).registerTo(itmill.tmp);
	 itmill.tmp.start();
	 delete itmill.tmp;
	 </script>
	 
	 <!-- Toolkit code block ends -->
	 
 </tr></td></table>
 
 <hr />
 <h3><%=test %> after Toolkit application.</h3>
 <br />
 <br />
 </center>
 </body>
 
 </html>
 