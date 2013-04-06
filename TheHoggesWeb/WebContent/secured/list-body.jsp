<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

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
	<TD><H2><bean:message key="list.heading"/></H2></TD>
	<TD align="right">	
	<html:img page="/images/list_disabled.gif" border="0" width="24" height="21"/>
<%
((java.util.HashMap)mappings).put("view", String.valueOf(Constants.DETAILS));
%>
		<html:link page="<%=str%>" name="mappings">
			<html:img page="/images/details.gif" border="0" width="24" height="21"/>
		</html:link>
<%
((java.util.HashMap)mappings).put("view", String.valueOf(Constants.THUMBNAILS));
%>
		<html:link page="<%=str%>" name="mappings">
			<html:img page="/images/thumbnails.gif" border="0" width="24" height="21"/>
		</html:link>
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
	<TH colspan="3"><bean:write name="resource" property="path"/></TH>
</TR>
<%
	ArrayList resource_list = (ArrayList)request.getAttribute("resource_list");
    MessageResources messages = (MessageResources) application.getAttribute(Globals.MESSAGES_KEY);
	resource_up.setName(messages.getMessage("list.goback"));
	resource_list.add(0, resource_up); 
	
	for (int index = 0; index < resource_list.size(); index++)
	{
		Resource element = (Resource)resource_list.get(index);
		pageContext.setAttribute("element", element);
		String str1 = "/viewer" + element.getPath();
%>		
<%if (index % 6 == 3) {%>
	<TR class="altrow">
	<%}else if (index % 6 == 0){%>
	<TR>
	<%}%>
	<TD>
	<html:link page="<%=str1%>">
		<logic:equal name="element" property="type" value="<%=String.valueOf(Constants.DIR)%>">
			<logic:present name="element" property="thumbPath">
				<%
				String str4 = "/cacheimager" + ((Resource)element).getThumbPath() + "?size=20";
				%>
				<html:img page="<%=str4%>" border="0" />
			</logic:present>
			<logic:notPresent name="element" property="thumbPath">
				<html:img page="/images/folder.gif" border="0"/>
			</logic:notPresent>
		</logic:equal>
		<logic:equal name="element" property="type" value="<%=String.valueOf(Constants.FILE)%>">
			<html:img page="/images/file.gif" border="0"/>
		</logic:equal>
		<logic:equal name="element" property="type" value="<%=String.valueOf(Constants.IMAGE)%>">
			<html:img page="/images/image.gif" border="0"/>
		</logic:equal>
		<logic:equal name="element" property="type" value="<%=String.valueOf(Constants.VIDEO)%>">
			<html:img page="/images/video.gif" border="0"/>
		</logic:equal>
		<logic:equal name="element" property="type" value="<%=String.valueOf(Constants.AUDIO)%>">
			<html:img page="/images/audio.gif" border="0"/>
		</logic:equal>
		<logic:equal name="element" property="type" value="<%=String.valueOf(Constants.UNKNOWN)%>">
			<html:img page="/images/unknown.gif" border="0"/>
		</logic:equal>
	</html:link>
	<html:link page="<%=str1%>">
		<bean:write name="element" property="name"/>
	</html:link>
	</TD>
	<%if (index % 3 == 2) {%>
		</TR>
	<%}
	}
	%>
	<%
	int i = resource_list.size() > 3 ? resource_list.size() % 3 : 0;
	if( i > 0 )
	{
		while(i++ < 3)
		{
		%><TD>&nbsp;</TD><%
		}
		out.println("</TR>");
	}
	%>
<TR>
	<TD colspan="3" class="TF">
	<logic:present name="resource" property="caption">
		<bean:write name="resource" property="caption"/>
	</logic:present>
	<logic:notPresent name="resource" property="caption">
		<DIV align="center"><bean:message key="common.sitename"/></DIV>
	</logic:notPresent>
	</TD>
</TR>
</table>
