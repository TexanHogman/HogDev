<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>

<script type="text/javascript"	src="<html:rewrite page="/js/dojo-release-1.2.3/dojo/dojo.js"/>"></script>

<SCRIPT language="JavaScript1.2">
	dojo.require("dojox.timing._base");
	
	function refreshWebCams()
	{
		var now =  new Date();
		var imgs = document.getElementById("wc1").getElementsByTagName("img");
	    for(i=0; i < imgs.length; i++) 
	     	imgs[i].src = imgs[i].src.substr(0,imgs[i].src.indexOf("?")) + "?time=" + now.getTime();
	    
		var imgs = document.getElementById("wc2").getElementsByTagName("img");
	    for(i=0; i < imgs.length; i++) 
	     	imgs[i].src = imgs[i].src.substr(0,imgs[i].src.indexOf("?")) + "?time=" + now.getTime();
	    
		var imgs = document.getElementById("wc3").getElementsByTagName("img");
	    for(i=0; i < imgs.length; i++) 
	     	imgs[i].src = imgs[i].src.substr(0,imgs[i].src.indexOf("?")) + "?time=" + now.getTime();
	}
	
    function pageinit()
    {
    // do it one time to load first image;
      refreshWebCams();
	  var timer = new dojox.timing.Timer(5000);
      timer.onTick = refreshWebCams;      
      timer.start();
    }

    dojo.addOnLoad(pageinit);
</SCRIPT>


<span id="wc1">
	<html:img imageName="webcam1" alt="Web Camera #1" page="/webcams/camera1.jpg?time=0" width="330"/>
</span>
<span id="wc2">
	<html:img imageName="webcam2" alt="Web Camera #2" page="/webcams/camera2.jpg?time=0" width="330"/>
</span>
<span id="wc3">
	<html:img imageName="webcam3" alt="Web Camera #3" page="/webcams/camera3.jpg?time=0" width="330"/>
</span>
