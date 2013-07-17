<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@page import="com.hogdev.enterprise.Constants"%>
<%@page import="com.hogdev.enterprise.beans.Resource"%>
<%@page import="java.util.ArrayList"%>
<%@page import="org.apache.struts.util.MessageResources"%>
<%@page import="org.apache.struts.Globals"%>

<jsp:useBean id="mappings" scope="request" class="java.util.HashMap"/>
<jsp:useBean id="resource" scope="request" class="com.hogdev.enterprise.beans.Resource"/>
<jsp:useBean id="resource_up" scope="request" class="com.hogdev.enterprise.beans.Resource"/>

<%
String str = "/viewer" + resource.getPath();
String str0 = "/imager" + resource.getThumbPath();
%>
<table width="100%" align="center">
<TR>
	<TD><H2><bean:message key="thumbnails.heading"/></H2></TD>
	<TD align="right">	
<%
((java.util.HashMap)mappings).put("view", String.valueOf(Constants.LIST));
%>
		<html:link page="<%=str%>" name="mappings">
			<html:img page="/images/list.gif" border="0" width="24" height="21"/>
		</html:link>
<%
((java.util.HashMap)mappings).put("view", String.valueOf(Constants.DETAILS));
%>
		<html:link page="<%=str%>" name="mappings">
			<html:img page="/images/details.gif" border="0" width="24" height="21"/>
		</html:link>
		<html:img page="/images/thumbnails_disabled.gif" border="0" width="24" height="21"/>
	</TD>
	</TR>
<TR>
	<TD align="right">
	<logic:present name="resource" property="thumbPath">
		<html:img page="<%=str0%>" border="5"/>
	</logic:present>
	</TD>
</TR>
</table>
<table width="100%" align="center" border="1">
<TR>
	<TH colspan="6"><bean:write name="resource" property="path"/></TH>
</TR>
<%
	ArrayList resource_list = (ArrayList)request.getAttribute("resource_list");
    MessageResources messages = (MessageResources) application.getAttribute(Globals.MESSAGES_KEY);
	resource_up.setName(messages.getMessage("thumbnails.goback"));
	resource_list.add(0, resource_up); 
%>
<logic:iterate id="element" name="resource_list" indexId="index">
<%
((java.util.HashMap)mappings).put("size", "100");
((java.util.HashMap)mappings).put("quality", ".50");
String str1 = "/cacheimager" + ((Resource)element).getThumbPath();
String str2 = "/viewer" + ((Resource)element).getPath();
%>
<%if ((index.intValue()) % 12 == 6) {%>
	<TR class="altrow">
	<%}else if ((index.intValue()) % 12 == 0){%>
	<TR>
	<%}%>
	<TD align="center">
	<html:link page="<%=str2%>">
		<logic:equal name="element" property="type" value="<%=String.valueOf(Constants.DIR)%>">
			<logic:present name="element" property="thumbPath">
				<html:img page="<%=str1%>" name="mappings" border="0"/>
			</logic:present>
			<logic:notPresent name="element" property="thumbPath">
				<html:img page="/images/bigfolder.gif" border="0"/>
			</logic:notPresent>
		</logic:equal>
		<logic:equal name="element" property="type" value="<%=String.valueOf(Constants.FILE)%>">
			<html:img page="/images/bigfile.gif" border="0"/>
		</logic:equal>
		<logic:equal name="element" property="type" value="<%=String.valueOf(Constants.IMAGE)%>">
			<html:img page="<%=str1%>" name="mappings" border="0"/>
		</logic:equal>
		<logic:equal name="element" property="type" value="<%=String.valueOf(Constants.VIDEO)%>">
			<html:img page="/images/bigvideo.gif" border="0"/>
		</logic:equal>
		<logic:equal name="element" property="type" value="<%=String.valueOf(Constants.AUDIO)%>">
			<html:img page="/images/bigaudio.gif" border="0"/>
		</logic:equal>
		<logic:equal name="element" property="type" value="<%=String.valueOf(Constants.UNKNOWN)%>">
			<html:img page="/images/bigunknown.gif" border="0"/>
		</logic:equal>
	</html:link>
	<BR>
	<html:link page="<%=str2%>">
		<bean:write name="element" property="name"/>
	</html:link>
	</TD>
<%if ((index.intValue()) % 6 == 5) {%>
	</TR>
	<%}%>
</logic:iterate>
	<%
	int i = resource_list.size() > 6 ? resource_list.size() % 6 : 0;
	if( i > 0 )
	{
		while(i++ < 6)
		{
		%><TD>&nbsp;</TD><%
		}
		out.println("</TR>");
	}
	%>
<TR>
	<TD colspan="6" class="TF">
	<logic:present name="resource" property="caption">
		<bean:write name="resource" property="caption"/>
	</logic:present>
	<logic:notPresent name="resource" property="caption">
		<DIV align="center"><bean:message key="common.sitename"/></DIV>
	</logic:notPresent>
	</TD>
</TR>
</table>
