<!DOCTYPE>
<%@page import="com.vaadin.ui.UI"%>
<%@page import="com.vaadin.server.VaadinSession"%>
<HTML>
<HEAD>
<TITLE>JSP integration</TITLE>
<style>
table {
	background: #fff;
}

td {
	border: 1px solid black;
	padding: .5em;
}
</style>
</HEAD>
<BODY>
    <table>
        <tr>
            <th align="left" colspan=4>Available UIs:</th>
        </tr>
        <tr>
            <th>Service Name</th>
            <th>CSRF token</th>
            <th>UI id</th>
            <th>UI type</th>
            <th>Main content</th>
        </tr>
        <%
        	HttpSession httpSession = request.getSession(false);
        	for (VaadinSession vs : VaadinSession.getAllSessions(httpSession)) {
        		try {
        			vs.lock();
        			for (UI ui : vs.getUIs()) {
        				out.append("<tr class='uirow'>");
        				out.append("<td>" + vs.getService().getServiceName()
        						+ "</td>");
        				out.append("<td>" + vs.getCsrfToken() + "</td>");
        				out.append("<td>" + ui.getUIId() + "</td>");
        				out.append("<td>" + ui.getClass().getName() + "</td>");
        				out.append("<td>" + ui.getContent().getClass().getName() + "</td>");
        				out.append("</tr>");

        			}
        		} finally {
        			vs.unlock();
        		}

        	}
        %>
    </table>
</BODY>
</HTML>