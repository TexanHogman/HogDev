<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<table width="100%" align="center">
<TR>
	<TD><H2><bean:message key="webstats.heading"/></H2></TD>
</TR>
</table>
<IFRAME SRC='<html:rewrite page="/retriever/docs/WebStats/stats.html"/>' TITLE="The Hogge's Web Stats" frameborder="0" width="100%" height="600px">
</IFRAME>