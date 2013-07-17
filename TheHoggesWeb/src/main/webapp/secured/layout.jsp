<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-tiles" prefix="tiles" %>
<%-- Layout Tiles 
  This layout render a header, left menu, body and footer.
  @param title String use in page title
  @param header Header tile (jsp url or definition name)
  @param menu Menu 
  @param body Body
  @param footer Footer
--%>

<HTML>
  <HEAD>
    <link rel=stylesheet href="<html:rewrite page="/theme/new-10-look.css" />" type="text/css">
    <title><tiles:getAsString name="title"/></title>
	<meta http-equiv="Refresh" content="<tiles:getAsString name="refresh"/>">
	<meta name="Keywords" content="<tiles:getAsString name="keywords"/>">
	<META HTTP-EQUIV="Pragma" content="<tiles:getAsString name="pragma"/>">
    <SCRIPT language="JavaScript1.2" src="<html:rewrite page="/common.js"/>"></SCRIPT>
  </HEAD>

<body onLoad="doOnload()">
<div id="header">
	<tiles:insert attribute="header"/>
</div>
<div id="menu">
	<tiles:insert attribute="menu"/>
</div>
<div id="errors">
  	<tiles:insert page="/secured/errors.jsp" />
</div>
<div id="messages">
  	<tiles:insert page="/secured/messages.jsp" />
</div>
<div id="content">
	<tiles:insert attribute="body"/>
</div>
<div id="footer">
	<tiles:insert attribute="footer"/>
</div>
</body>
</html>

