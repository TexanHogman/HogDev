<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<table width="100%" align="center">
<TR>
	<TD><H2><bean:message key="products.heading"/></H2></TD>
</TR>
</table>
<div id="copy">
<html:img alt="me at work" page="/images/hogdev.gif" align="right"  border="0"/>
<P>
Ok I am just starting to compile the list of the many applications and tools I have written over the
last decade. The links below require Java Web Start to load and run the application on your system.  
Some of the functionality requires that the binaries be signed.  I am using a self signed certificate for now
so on your first install you will get a warning message.  This is completely expected and you can safely ignore
it.
</P>
The obvious first project is this website.
<P>
<SCRIPT LANGUAGE="JavaScript"> 
	var javawsInstalled = 0; 
	var javaws12Installed = 0; 
	var javaws142Installed=0; 
	isIE = "false"; 
	if (navigator.mimeTypes && navigator.mimeTypes.length)
	{ 
	   x = navigator.mimeTypes['application/x-java-jnlp-file']; 
	   if (x)
	   { 
	      javawsInstalled = 1; 
	      javaws12Installed=1; 
	      javaws142Installed=1; 
	   } 
	} 
	else
	{ 
	   isIE = "true"; 
	} 
</SCRIPT> 

<SCRIPT LANGUAGE="VBScript">
	on error resume next
	If isIE = "true" Then
	  If Not(IsObject(CreateObject("JavaWebStart.isInstalled"))) Then
	     javawsInstalled = 0
	  Else
	     javawsInstalled = 1
	  End If
	  If Not(IsObject(CreateObject("JavaWebStart.isInstalled.2"))) Then
	     javaws12Installed = 0
	  Else
	     javaws12Installed = 1
	  End If
	  If Not(IsObject(CreateObject("JavaWebStart.isInstalled.1.4.2.0"))) Then
	     javaws142Installed = 0
	  Else
	     javaws142Installed = 1
	  End If  
	End If
</SCRIPT>

<SCRIPT LANGUAGE="JavaScript">
/* Note that the logic below always launches the JNLP application
 *if the browser is Gecko based. This is because it is not possible
 *to detect MIME type application/x-java-jnlp-file on Gecko-based browsers. 
 */
if (javawsInstalled || (navigator.userAgent.indexOf("Gecko") !=-1))
{
	var u = "<html:rewrite page="/products/RaceControl.jnlp"/>";
	var x = "<html:rewrite page="/products/Toolbox.jnlp"/>";
    document.write("<a href=" + u + ">RaceControl</a> - Lap Timing Software");
    document.write("<BR>");
    document.write("<a href=" + x + ">ToolBox</a> - Set of seemingly useless utilities");
} else
{
	// got to be a better way to build the server names and port
	var u = "<%=request.getRequestURL().toString().substring(0, request.getRequestURL().toString().lastIndexOf("/"))%>";
	u = u + "/products.do";
    document.write("Click ");
    document.write("<a href=http://java.sun.com/PluginBrowserCheck?pass=" + u + "&fail=http://java.sun.com/j2se/1.4.2/download.html>here</a> ");
    document.write("to automatically install Java Web Start.");
}
</SCRIPT>
<P>
Stayed tuned for more to come.
</P>

<P><BR>Here is my professional <A href="javascript:popup('<html:rewrite page="/RickHoggeResume.htm"/>','resume', 'scrollbars=yes,height=600,width=640,resizable=yes,status=no,toolbar=no,menubar=no,location=no');">resume</A>.
</P>
</div>