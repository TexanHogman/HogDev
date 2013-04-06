<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>

<table width="100%" align="center">
	<TR>
		<TD>
		<H2><bean:message key="upload.heading" /></H2>
		</TD>
	</TR>
</table>
<html:form action="fileupload" enctype="multipart/form-data">
	<html:file property="file" />
	<html:submit value="Upload" />
</html:form>
