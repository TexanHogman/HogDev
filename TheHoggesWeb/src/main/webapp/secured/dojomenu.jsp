<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<script type="text/javascript" src="<html:rewrite page="/js/dojo-release-1.2.3/dojo/dojo.js"/>"></script>

<style type="text/css">
.dojoxFisheyeListBar {
	margin: 15;
	text-align: center;
}
</style>

<script type="text/javascript">
		dojo.require("dojox.widget.FisheyeList");
		dojo.require("dojo.parser");	// scan page for widgets and instantiate them
        dojo.addOnLoad(function()
        {
        	dojo.parser.parse(dojo.byId('menuContainer'));
        });

	function load_app(id){
		document.location.href=id;
	}
	function load_webcam(){
		popup('<html:rewrite page="/webcam.jsp"/>','webcam','width=640,height=480,resizable=yes,status=no,toolbar=no,menubar=no,location=no,scrollbars=no');
	}
</script>

<div id="menuContainer">
<div dojoType="dojox.widget.FisheyeList" itemWidth="50" itemHeight="50"
	itemMaxWidth="150" itemMaxHeight="150" orientation="vertical"
	effectUnits="2" itemPadding="10" attachEdge="top" labelEdge="bottom"
	conservativeTrigger="false">

<div dojoType="dojox.widget.FisheyeListItem"
	onClick="load_app('<html:rewrite page="/blog.do"/>');"
	iconsrc='<html:rewrite page="/images/newspaper.png"/>' caption="News">
</div>

<div dojoType="dojox.widget.FisheyeListItem"
	onClick="load_app('<html:rewrite page="/racing.do"/>');"
	iconsrc='<html:rewrite page="/images/checkeredflag.png"/>'
	caption="Racing"></div>

<div dojoType="dojox.widget.FisheyeListItem"
	onClick="load_app('<html:rewrite page="/products.do"/>');"
	iconsrc='<html:rewrite page="/images/computer.png"/>'
	caption="Software"></div>

<div dojoType="dojox.widget.FisheyeListItem"
	onClick="load_app('<html:rewrite page="/viewer/images?view=3"/>');"
	iconsrc='<html:rewrite page="/images/camera.png"/>' caption="Pictures">
</div>

<div dojoType="dojox.widget.FisheyeListItem"
	onClick="load_app('<html:rewrite page="/viewer/music?view=1"/>');"
	iconsrc='<html:rewrite page="/images/music.png"/>' caption="Music">
</div>

<div dojoType="dojox.widget.FisheyeListItem" onClick="load_webcam();"
	iconsrc='<html:rewrite page="/images/webcam.png"/>'
	caption="Web Camera"></div>

<div dojoType="dojox.widget.FisheyeListItem"
	onClick="load_app('<html:rewrite page="/webstats.do"/>');"
	iconsrc='<html:rewrite page="/images/stats.png"/>' caption="Web Stats">
</div>

<div dojoType="dojox.widget.FisheyeListItem"
	onClick="load_app('<html:rewrite page="/home.do"/>');"
	iconsrc='<html:rewrite page="/images/home.png"/>' caption="Home">
</div>

<div dojoType="dojox.widget.FisheyeListItem"
	onClick="load_app('mailto:rick.hogge@gmail.com');"
	iconsrc='<html:rewrite page="/images/email.png"/>' caption="eMail us">
</div>
</div>
<br>
<br>
<br>
<br>
&nbsp;&nbsp;
<A
	href="javascript:popup('<html:rewrite page="/RickHoggeResume.htm"/>','resume', 'height=600,width=680,resizable=yes,status=no,toolbar=no,menubar=no,location=no,scrollbars=yes');">
<html:img imageName="resumeimage" alt="Resume"
	page="/images/JavaAni.gif" border="0" /> </A>
</div>

