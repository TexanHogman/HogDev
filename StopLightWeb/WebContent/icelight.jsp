<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="light.StandardICELight"%>

<%
	StandardICELight light = (StandardICELight)application.getAttribute("icelight");

	String command = request.getParameter("command");
	if (command != null)
	{
		if (command.equals("ICE Project ID"))
		{
			String id = request.getParameter("projectid");
			light.iceProject(id.trim());
			application.setAttribute("icelight.mode", "ID-" + id.trim());
			response.sendRedirect("icelight.jsp");
		}
		else if (command.equals("ICE Queue"))
		{
			light.iceQueue();
			application.setAttribute("icelight.mode", "ICE Queue");
			response.sendRedirect("icelight.jsp");
		}
		else if (command.equals("Off"))
		{
			light.off();
			application.setAttribute("icelight.mode", "Ready");
			response.sendRedirect("icelight.jsp");
		}
	}
	else
	{
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>ICELight control</title>
<style>
BODY {
	BACKGROUND-COLOR: white;
	COLOR: #333366;
	FONT-FAMILY: Verdana, Tahoma, "Times New Roman";
	font-size: 10pt;
	list-style-type: disc;
	padding: 0px;
	margin-left: 0px;
	margin-right: 0px;
	margin-bottom: 0px;
	margin-top: 0px;
}

FORM {
	padding: 0px;
	margin-left: 0px;
	margin-right: 0px;
	margin-bottom: 0px;
	margin-top: 0px;
}

.button {
	width: 100px;
	height: 25px;
	font-size: 10pt;
}
</style>
</head>
<SCRIPT language="JavaScript1.2">
function openpopup()
{
	testwindow = window.open ("icelight.jsp", "icelight", "toolbar=0,resiazable=0,location=0,status=0,scrollbars=0,width=100,height=135");
	testwindow.moveTo(0,0);
}
</SCRIPT>
<body>
Mode:
<%=application.getAttribute("icelight.mode")%>
<br>
<br>
<form action="icelight.jsp" name="control"><input type="text"
	name="projectid" size="12"> <br>
<input type="submit" name="command" value="ICE Project ID"
	class="button"> <br>
<input type="submit" name="command" value="ICE Queue" class="button">
<br>
<input type="submit" name="command" value="Off" class="button">
<br>
<br>
<input type="button" value="popup" onclick="openpopup()"></form>
</body>

</html>

<%
	}
%>
