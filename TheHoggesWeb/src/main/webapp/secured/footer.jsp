<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>

<bean:define id="stats" name="stats" scope="application" type="com.hogdev.enterprise.beans.Stats"/>
<div class="groupbox">
<marquee direction="left" loop="true" scrolldelay="400">
Thanks for visiting TheHogges.com. There have been <span id="hits"><bean:write name="stats" property="hitAndReturn"/></span> hits since <bean:write name="stats" property="startDate"/>.  Have a great day!
</marquee>
</div>
 