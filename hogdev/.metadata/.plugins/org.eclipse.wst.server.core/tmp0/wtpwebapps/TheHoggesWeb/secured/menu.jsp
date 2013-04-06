<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>

<div id="navbar">
<ul>
	<li><html:link action="/home.do"><bean:message key="menu.home"/></html:link></li>
	<li><html:link action="/racing.do"><bean:message key="menu.racing"/></html:link></li>
	<li><html:link action="/products.do"><bean:message key="menu.software"/></html:link></li>
	<li><a href="<html:rewrite page="/viewer/images?view=3"/>"><bean:message key="menu.pictures"/></a></li>
	<li><a href="<html:rewrite page="/viewer/music?view=1"/>"><bean:message key="menu.music"/></a></li>
	<li><a href="<html:rewrite page="/viewer/video?view=1"/>"><bean:message key="menu.video"/></a></li>
	<li><html:link action="/webstats.do"><bean:message key="menu.webstats"/></html:link></li>
	<li><html:link action="/automation.do"><bean:message key="menu.automation"/></html:link></li>
	<li><html:link action="/webcams.do"><bean:message key="menu.webcams"/></html:link></li>
	<li><a href="mailto:thehogges@gmail.com"><bean:message key="menu.email"/></a></li>
</ul>
</div>
