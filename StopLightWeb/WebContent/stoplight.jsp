<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page import="com.hogdev.traffic.*"%>

<%
	StandardStopLight light = (StandardStopLight)application.getAttribute("light");

	String command = request.getParameter("command");
	if (command != null)
	{
		if (command.equals("On"))
		{
			light.turnOn();
		}
		else if (command.equals("Off"))
		{
			light.turnOff();
		}
		else if (command.equals("Flash"))
		{
			light.flashAll();
		}
		else if (command.equals("Green on"))
		{
			light.turnGreenOn();
		}
		else if (command.equals("Green off"))
		{
			light.turnGreenOff();
		}
		else if (command.equals("Green flash"))
		{
			light.flashGreen();
		}
		else if (command.equals("Yellow on"))
		{
			light.turnYellowOn();
		}
		else if (command.equals("Yellow off"))
		{
			light.turnYellowOff();
		}
		else if (command.equals("Yellow flash"))
		{
			light.flashYellow();
		}
		else if (command.equals("Red on"))
		{
			light.turnRedOn();
		}
		else if (command.equals("Red off"))
		{
			light.turnRedOff();
		}
		else if (command.equals("Red flash"))
		{
			light.flashRed();
		}
		else if (command.equals("Stage"))
		{
			light.stage();
		}
		else if (command.equals("Cycle"))
		{
			light.cycle();
		}
		else if (command.equals("Crazy"))
		{
			light.goCrazy();
		}
	}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>StopLight control</title>
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
	testwindow = window.open ("stoplight.jsp", "stoplight", "toolbar=0,resiazable=0,location=0,status=0,scrollbars=0,width=100,height=380");
	testwindow.moveTo(0,0);
}
</SCRIPT>
<body>
<form action="stoplight.jsp" name="control">
<input type="submit" name="command" value="On" class="button">
<br>
<input type="submit" name="command" value="Flash" class="button">
<br>
<input type="submit" name="command" value="Red on" class="button">
<br>
<input type="submit" name="command" value="Red flash" class="button">
<br>
<input type="submit" name="command" value="Red off" class="button">
<br>
<input type="submit" name="command" value="Yellow on" class="button">
<br>
<input type="submit" name="command" value="Yellow flash" class="button">
<br>
<input type="submit" name="command" value="Yellow off" class="button">
<br>
<input type="submit" name="command" value="Green on" class="button">
<br>
<input type="submit" name="command" value="Green flash" class="button">
<br>
<input type="submit" name="command" value="Green off" class="button">
<br>
<input type="submit" name="command" value="Cycle" class="button">
<br>
<input type="submit" name="command" value="Stage" class="button">
<br>
<input type="submit" name="command" value="Crazy" class="button">
<br>
<input type="submit" name="command" value="Off" class="button">
<br>
<br>
<input type="button" value="popup" onclick="openpopup()"></form>
</body>

</html>