<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ page import="com.hogdev.enterprise.Constants" %>
<%@ page import="javax.servlet.http.Cookie" %>

<jsp:useBean id="resource" scope="request" class="com.hogdev.enterprise.beans.Resource"/>
<jsp:useBean id="mappings" scope="request" class="java.util.HashMap"/>

<script>
function doSizeChange()
{
	var sel = document.getElementById("sizeselect");
	var img = document.getElementById("picture");
	var next = img.src.substr(0,img.src.indexOf("?")) + "?size=" + sel.value;
	img.src =  next;
	
	//set cookie with this preferred size
	setCookie("preferred_img_size", sel.value, 365);
}
</script>

<table width="100%" align="center">
<TR>
	<TD><H2><bean:message key="walker.heading"/></H2></TD>
	<logic:equal name="resource" property="type" value="<%=String.valueOf(Constants.IMAGE)%>">
	<TD align="right">
	<%
	String str = "/retriever" + resource.getPath();
	%>
	<html:link page="<%=str%>">
		<html:img page="/images/photo.png" title="click here for photo quality image" border="0"/>
	</html:link>
	</TD>
	</logic:equal>
</TR>
<TR>
<TD align="left">
<logic:present name="resource_back">
	<jsp:useBean id="resource_back" scope="request" class="com.hogdev.enterprise.beans.Resource"/>
	<%
	String strB = "/viewer" + resource_back.getPath();
	%>
	<html:link page="<%=strB%>">
		<html:img page="/images/nav_previous.gif" border="0" styleId="backButton" title="go back"/>
	</html:link>
</logic:present>
	<jsp:useBean id="resource_up" scope="request" class="com.hogdev.enterprise.beans.Resource"/>
	<%
	String strU = "/viewer" + resource_up.getPath();
	%>
	<html:link page="<%=strU%>">
	<html:img page="/images/nav_up.gif" border="0" styleId="upButton" title="return to parent"/>
</html:link>
<logic:present name="resource_forward">
	<jsp:useBean id="resource_forward" scope="request" class="com.hogdev.enterprise.beans.Resource"/>
	<%
	String strF = "/viewer" + resource_forward.getPath();
	%>
	<html:link page="<%=strF%>"><html:img page="/images/nav_next.gif" border="0" styleId="forwardButton" title="go forward"/></html:link>
</logic:present>
</TD>
</TR>
</TABLE>
<%
	Cookie[] cookies = request.getCookies();
	String size = "640"; //default
	for(int i = 0; i < cookies.length; i++)
	{
	    if(cookies[i].getName().equals("preferred_img_size"))
		{
			size = cookies[i].getValue();
	    	break;
		}
	}

    ((java.util.HashMap)mappings).put("size", size);
    
	String str1 = "/cacheimager" + resource.getPath();
	String str2 = "/retriever" + resource.getPath();
%>
<table width="100%" align="center" border="1">
<TR>
	<TH colspan="3"><bean:write name="resource" property="path"/></TH>
</TR>
<TR>
<TD>
<logic:equal name="resource" property="type" value="<%=String.valueOf(Constants.DIR)%>">
	<bean:message key="walker.directory.notSupported"/>
</logic:equal>
<logic:equal name="resource" property="type" value="<%=String.valueOf(Constants.FILE)%>">
	<html:link page="<%=str2%>"><bean:message key="walker.unknown.supported"/></html:link>
</logic:equal>
<logic:equal name="resource" property="type" value="<%=String.valueOf(Constants.IMAGE)%>">
	<div style="float:left;">
		<div class="groupbox" align="center">
			Select your picture size<bean:message key="common.fielddelim"/>
			<select onchange="doSizeChange();" id="sizeselect">
				<option value="1280" <%=size.equals("1280")? "selected" : ""%>>Xtra Large</option>
				<option value="1024" <%=size.equals("1024")? "selected" : ""%>>Large</option>
				<option value="800" <%=size.equals("800")? "selected" : ""%>>Medium</option>
				<option value="640" <%=size.equals("640")? "selected" : ""%>>Small</option>
				<option value="400" <%=size.equals("400")? "selected" : ""%>>Xtra Small</option>
			</select>
		</div>
		<div class="imageContainer">
		<html:link page="<%=str2%>">
		<html:img page="<%=str1%>" name="mappings" styleId="picture" alt="click for original size" title="click for original size"/>
		</html:link>
		</div>
		<div class="infotext" align="center"><bean:message key="walker.image.caption"/></div>
	</div>
</logic:equal>
<logic:equal name="resource" property="type" value="<%=String.valueOf(Constants.VIDEO)%>">
	<html:link page="<%=str2%>"><bean:message key="walker.click.video"/></html:link>
</logic:equal>
<logic:equal name="resource" property="type" value="<%=String.valueOf(Constants.AUDIO)%>">
	<html:link page="<%=str2%>"><bean:message key="walker.click.audio"/></html:link>
</logic:equal>
<logic:equal name="resource" property="type" value="<%=String.valueOf(Constants.UNKNOWN)%>">
	<html:link page="<%=str2%>"><bean:message key="walker.unknown.supported"/></html:link>
</logic:equal>
</TD>
</TR>
<TR>
	<TD class="TF">
	<logic:present name="resource" property="caption">
		<bean:write name="resource" property="caption"/>
	</logic:present>
	<logic:notPresent name="resource" property="caption">
		<DIV align="center"><bean:message key="common.sitename"/></DIV>
	</logic:notPresent>
	</TD>
</TR>
</table>


