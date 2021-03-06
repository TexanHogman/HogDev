<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@ page import="java.util.*" %>
<script type="text/javascript"
	src="<html:rewrite page="/js/dojo-release-1.2.3/dojo/dojo.js"/>"></script>

<SCRIPT language="JavaScript1.2">
	dojo.require("dojox.timing._base");
	
	function reload()
	{
		 dojo.xhrGet ({
		 url: '<html:rewrite page="/automationA.do?action=status"/>',
		 handleAs: 'json',
		 load: function (json) 
		 {
			document.getElementById("time").innerHTML = json.time;
			document.getElementById("sunrise").innerHTML = json.sunrise;
			document.getElementById("sunset").innerHTML = json.sunset;
			document.getElementById("dawn").innerHTML = json.dawn;
			document.getElementById("dusk").innerHTML = json.dusk;

			var macrohtml ="";
			var top = 220;
			for (var i = 0; i < json.macros.length; i++)
			{
				macrohtml += "<input type=\"button\" value=\"" + json.macros[i].desc.toLowerCase() + "\" onclick=\"macro('" + json.macros[i].id + "')\" style=\"top:" + top + "px;\" class=\"automationbutton\" title=\"execute macro " + json.macros[i].desc.toLowerCase() + "\">\n";
				top += 30;
			}
			top += 20;
			for (var i = 0; i < json.macrosOnOff.length; i++)
			{
				macrohtml += "<input type=\"button\" value=\"" + json.macrosOnOff[i].desc.toLowerCase() + "\" onclick=\"macro('" + json.macrosOnOff[i].id + "')\" style=\"top:" + top + "px;\" class=\"automationbutton\" title=\"execute macro " + json.macrosOnOff[i].desc.toLowerCase() + "\">\n";
				top += 30;
			}
			document.getElementById("macros").innerHTML = macrohtml;
			
			for (var i = 0; i < json.devices.length; i++) 
			{
				var found = false;
				
				var images = document.getElementById("jets").getElementsByTagName("img");
			    for(var j=0; found == false && j < images.length; j++) 
			    {
			    	var image = images[j];
				    if(image.id == json.devices[i].id)
				    {
						if(json.devices[i].on)
						{
					    	image.src="images/jet_on.gif";
						}
						else
						{
					    	image.src="images/jet_off.gif";
						}
						found = true;
				    }
			    }
			    
				var images = document.getElementById("lights").getElementsByTagName("img");
			    for(var j=0; found == false && j < images.length; j++) 
			    {
			    	var image = images[j];
				    if(image.id == json.devices[i].id)
				    {
						if(json.devices[i].on)
						{
					    	image.src="images/light_on.gif";
						}
						else
						{
					    	image.src="images/light_off.gif";
						}
						found = true;
				    }
			    }
			    
				var images = document.getElementById("scents").getElementsByTagName("img");
			    for(var j=0; found == false && j < images.length; j++) 
			    {
			    	var image = images[j];
				    if(image.id == json.devices[i].id)
				    {
						if(json.devices[i].on)
						{
					    	image.src="images/scent_on.gif";
						}
						else
						{
					    	image.src="images/scent_off.gif";
						}
						found = true;
				    }
			    }
			    
				var images = document.getElementById("internets").getElementsByTagName("img");
			    for(var j=0; found == false && j < images.length; j++) 
			    {
			    	var image = images[j];
				    if(image.id == json.devices[i].id)
				    {
						if(json.devices[i].on)
						{
					    	image.src="images/internet_on.gif";
						}
						else
						{
					    	image.src="images/internet_off.gif";
						}
						found = true;
				    }
			    }

			}
		 }
		 });		
	}	
	
	 function automationinit()
	 {
		 //try now
		reload();
		
		 // and again in a few seconds
		setTimeout("reload()", 5000);
		
		 // then update every 30
		var reloadtimer = new dojox.timing.Timer(30 * 1000);
		reloadtimer.onTick = reload;      
		reloadtimer.start();

	 }

	function toggle(type, id) 
	{
		var img = document.getElementById(id);
		var cmd;
		if(img)
		{
			if(img.src.indexOf('_on') != -1)
			{
				cmd = 'Off';
			}
			else
			{
				cmd = 'On';
			}
		}
		
		document.AutomationForm.action.value = 'device'; 
		document.AutomationForm.id.value = id;
		document.AutomationForm.type.value = type;
		document.AutomationForm.cmd.value = cmd;
		document.AutomationForm.submit();
	}
	
	function macro(id) 
	{
		document.AutomationForm.action.value = 'macro'; 
		document.AutomationForm.id.value = id;
		document.AutomationForm.submit();
	}
	
	dojo.addOnLoad(automationinit);
</script>

<div id="automationcontent">
	<html:form action="/automationA.do">
		<html:hidden property="action" value=""/>
		<html:hidden property="type" value=""/>
		<html:hidden property="cmd" value=""/>
		<html:hidden property="id" value=""/>
	</html:form>
	
	<img src="images/house.gif" style="left:0px; top:130px;">

	<img src="images/water.gif" style="left:650px; top:735px; height:30px; width:30px;" title="Zone 1 On" onclick="macro('IRZN1');">
	<img src="images/water.gif" style="left:800px; top:550px; height:30px; width:30px;" title="Zone 2 On" onclick="macro('IRZN2');">
	<img src="images/water.gif" style="left:510px; top:270px; height:30px; width:30px;" title="Zone 3 On" onclick="macro('IRZN3');">
	<img src="images/water.gif" style="left:200px; top:560px; height:30px; width:30px;" title="Zone 4 On" onclick="macro('IRZN4');">
	<img src="images/water.gif" style="left:250px; top:845px; height:30px; width:30px;" title="Zone 5 On" onclick="macro('IRZN5');">
	<img src="images/water.gif" style="left:790px; top:140px; height:30px; width:30px;" title="Zone 6 On" onclick="macro('IRZN6');">

	<img src="images/water.gif" style="left:100px; top:845px; height:30px; width:30px;" title="Zone 7 On" onclick="macro('IRZN7');">
	<img src="images/water.gif" style="left:05px; top:560px; height:30px; width:30px;" title="Zone 8 On" onclick="macro('IRZN8');">
	<img src="images/water.gif" style="left:75px; top:405px; height:30px; width:30px;" title="Zone 9 On" onclick="macro('IRZN9');">
	<img src="images/water.gif" style="left:320px; top:140px; height:30px; width:30px;" title="Zone 10 On" onclick="macro('IRZN10');">
	<img src="images/water.gif" style="left:255px; top:280px; height:30px; width:30px;" title="Zone 11 On" onclick="macro('IRZN11');">
		
	<div id="jets">
	<img id="PLDCKJTS" src="images/jet_off.gif" style="left:120px; top:250px;" title="Deck Jets"  onclick="toggle('5','PLDCKJTS');">
	</div>
	
	<div id="lights">
	<img id="PLLGHT" src="images/light_off.gif" style="left:60px; top:150px; height:28px; width:21px;" title="Pool" onclick="toggle('5','PLLGHT');">
	<img id="TRLGHT" src="images/light_off.gif" style="left:05px; top:415px; height:28px; width:21px;" title="Trees" onclick="toggle('5','TRLGHT');">
	<img id="RFLD" src="images/light_off.gif" style="left:465px; top:365px; height:28px; width:21px;" title="Rear Flood" onclick="toggle('5','RFLD');">
	<img id="SDFLD" src="images/light_off.gif" style="left:200px; top:430px; height:28px; width:21px;" title="Side Flood" onclick="toggle('5','SDFLD');">
	<img id="KTCHNCB" src="images/light_off.gif" style="left:373px; top:595px; height:28px; width:21px;" title="Kitchen Accent" onclick="toggle('5','KTCHNCB');">
	<img id="FYRRP" src="images/light_off.gif" style="left:565px; top:590px; height:28px; width:21px;" title="Foyer Accent" onclick="toggle('5','FYRRP');">
	<img id="LVFLLMP" src="images/light_off.gif" style="left:585px; top:430px; height:28px; width:21px;" title="Floor Lamp" onclick="toggle('0','LVFLLMP');">
	<img id="LVTBLMP" src="images/light_off.gif" style="left:475px; top:440px; height:28px; width:21px;" title="Table Lamp" onclick="toggle('0','LVTBLMP');">
	<img id="LVFRLMP" src="images/light_off.gif" style="left:465px; top:505px; height:28px; width:21px;" title="Furniture Lamp" onclick="toggle('0','LVFRLMP');">
	<img id="LVRP" src="images/light_off.gif" style="left:585px; top:505px; height:28px; width:21px;" title="Living Accent" onclick="toggle('5','LVRP');">
	<img id="LVCLNG_1" src="images/light_off.gif" style="left:530px; top:475px; height:28px; width:21px;" title="Living" onclick="toggle('5','LVCLNG_1');">
	<img id="FRTPRCH" src="images/light_off.gif" style="left:517px; top:636px; height:28px; width:21px;" title="Porch" onclick="toggle('5','FRTPRCH');">
	<img id="FRTGRG" src="images/light_off.gif" style="left:450px; top:727px; height:28px; width:21px;" title="Garage" onclick="toggle('5','FRTGRG');">
	<img id="FRTFLD_1" src="images/light_off.gif" style="left:580px; top:705px; height:28px; width:21px;" title="Front Flood" onclick="toggle('5','FRTFLD_1');">
	<img id="SHPLGHT" src="images/light_off.gif" style="left:35px; top:735px; height:28px; width:21px;" title="Shop" onclick="toggle('0','SHPLGHT');">
	</div>	
	
	<div id="cameras">
	<img src="images/webcam.gif" style="left:455px; top:630px; height:30px; width:30px;" title="Front Camera" onclick="window.location.href='webcams.do'">
	<img src="images/webcam.gif" style="left:0px; top:755px; height:30px; width:30px;" title="Shop Camera" onclick="window.location.href='webcams.do'">
	<img src="images/webcam.gif" style="left:295px; top:465px; height:30px; width:30px;" title="Back Camera" onclick="window.location.href='webcams.do'">
	</div>	
	
	<div id="scents">
	<img id="SCENTSY" src="images/scent_off.gif" style="left:485px; top:510px; height:25px; width:25px;" title="Scentsy" onclick="toggle('0','SCENTSY');"> 
	</div>	
	
	<div id="internets">
	<img id="INTRNT" src="images/internet_off.gif" style="left:0px; top:798px; height:100px;" title="Internet" onclick="toggle('0','INTRNT');"> 
	</div>	
	
	
	<div id="macros">
	</div>
		
	<div class="subtle" style="left:700px; top:840px;">Time:&nbsp;&nbsp;&nbsp;&nbsp;<span id="time"></span></div>
	<div class="subtle" style="left:700px; top:860px;">Sunrise:&nbsp;<span id="sunrise"></span></div>
	<div class="subtle" style="left:840px; top:860px;">Dawn:&nbsp;<span id="dawn"></span></div>
	<div class="subtle" style="left:700px; top:880px;">Sunset:&nbsp;&nbsp;<span id="sunset"></span></div>
	<div class="subtle" style="left:840px; top:880px;">Dusk:&nbsp;<span id="dusk"></span></div>
</div>


