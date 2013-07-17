<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>

<%@page import="org.apache.struts.Globals"%>

<logic:present name="<%=Globals.ERROR_KEY%>">
	<TABLE class="groupbox" border="0" width="100%" cellpadding="0"
		cellspacing="0">
		<TR>
			<td align="left" width="34"><html:img page="/images/info.gif"
				height="34" width="34" border="0"/></td>
			<TD align="left">
			<div class="error"><html:messages message="false" id="message">
				<LI><bean:write name="message" /></LI>
			</html:messages></div>
			</TD>
		</TR>
	</TABLE>
</logic:present>
