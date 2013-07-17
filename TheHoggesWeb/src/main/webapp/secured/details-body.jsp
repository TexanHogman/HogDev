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

<SCRIPT language="javascript">

	function selectAll() 
	{
		document.PackagerForm.selectedResources.checked = true;
		var i;
		for (i=0; i < document.PackagerForm.selectedResources.length; i++) 
		{
			document.PackagerForm.selectedResources[i].checked = true;
		}
	}
	function selectNone() 
	{
		document.PackagerForm.selectedResources.checked = false;
		var i;
		for (i=0; i < document.PackagerForm.selectedResources.length; i++) 
		{
			document.PackagerForm.selectedResources[i].checked = false;
		}
	}
	function changeAction() 
	{
		document.PackagerForm.action = document.PackagerForm.action.replace('.do','.m3u');
	}
</SCRIPT>
<%
String str = "/viewer" + resource.getPath();
String str0 = "/imager" + resource.getThumbPath();
%>
<table width="100%" align="center" >
<TR>
	<TD><H2><bean:message key="details.heading"/></H2></TD>
	<TD align="center">
		<table cellpadding="0" cellspacing="0">
		<TR><TD>
		<div class="groupbox">
		<html:form action="searcher">
			<bean:message key="search.criteria"/><bean:message key="common.fielddelim"/>
			<html:select property="criteria">
				<html:option value="title" key="search.title"></html:option>
				<html:option value="artist" key="search.artist"></html:option>
				<html:option value="album" key="search.album"></html:option>
			</html:select>
			<html:text property="value"/>
			<html:submit><bean:message key="search.perform"/></html:submit>
		</html:form>
		</div>
		</TD></TR>
		</table>
	</TD>
	<TD align="right">
<%
((java.util.HashMap)mappings).put("view", String.valueOf(Constants.LIST));
%>
		<html:link page="<%=str%>" name="mappings">
			<html:img page="/images/list.gif" border="0" width="24" height="21"/>
		</html:link>
		<html:img page="/images/details_disabled.gif" border="0" width="24" height="21"/>
<%
((java.util.HashMap)mappings).put("view", String.valueOf(Constants.THUMBNAILS));
%>
		<html:link page="<%=str%>" name="mappings">
			<html:img page="/images/thumbnails.gif" border="0" width="24" height="21"/>
		</html:link>
	</TD>
</TR>
<TR>
	<TD align="center" colspan="3">
	<logic:present name="resource" property="thumbPath">
		<html:img page="<%=str0%>" border="5"/>
	</logic:present>
	</TD>
</TR>
</table>

<table width="100%" align="center" border="1">
<html:form action="packager" onsubmit="changeAction();">
<html:hidden property="path" value="<%=resource.getPath()%>"/>
<TR>
	<TH colspan="4"><bean:write name="resource" property="path"/></TH>
</TR>
<TR class="altTH">
	<TD><bean:message key="details.col2"/></TD>
	<TD><bean:message key="details.col3"/></TD>
	<TD><bean:message key="details.col4"/></TD>
	<TD><bean:message key="details.col1"/></TD>
</TR>
<%
	ArrayList list = (ArrayList)request.getAttribute("resource_list");
    MessageResources messages = (MessageResources) application.getAttribute(Globals.MESSAGES_KEY);
	resource_up.setName(messages.getMessage("details.goback"));
	list.add(0, resource_up); 
%>
<logic:iterate id="element" name="resource_list" indexId="index">
	<%
	String str1 = "/viewer" + ((Resource)element).getPath();
	if (index.intValue() % 2 != 0) {%>
	<TR class="altrow">
	<%}else{%>
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
	<TD align="center">
		<bean:write name="element" property="size"/>
	</TD>
	<TD align="center">
		<bean:write name="element" property="lastUpdate"/>
	</TD>
	<TD align="center">
		<%if(element != resource_up)
		{%>
			<html:multibox property="selectedResources" value="<%=((Resource)element).getPath()%>"/>
		<%} else { %>
			<input type="button" class="smallbutton" value="<bean:message key="details.selectall"/>" onclick="selectAll();">
			<input type="button" class="smallbutton" value="<bean:message key="details.selectnone"/>" onclick="selectNone();">
			<html:submit styleClass="smallbutton"><bean:message key="details.package"/></html:submit>
		<%}%>
	</TD>
	</TR>
</logic:iterate>
<TR>
	<TD colspan="4" class="TF">
	<logic:present name="resource" property="caption">
		<bean:write name="resource" property="caption"/>
	</logic:present>
	<logic:notPresent name="resource" property="caption">
		<DIV align="center"><bean:message key="common.sitename"/></DIV>
	</logic:notPresent>
	</TD>
</TR>
</html:form>
</table>

