<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html" %>
<%@page import="com.hogdev.enterprise.beans.Resource"%>
<%@page import="java.text.DateFormat"%>
<script type="text/javascript"
	src="<html:rewrite page="/js/dojo-release-1.2.3/dojo/dojo.js"/>"></script>


<SCRIPT language="JavaScript1.2">
	dojo.require("dojox.timing._base");
	
	function refreshImage()
	{
	  dojo.xhrGet ({
	  url: '<html:rewrite page="/randomimagerurl"/>',	
	  load: function (data) {
			var imgs = document.getElementById("imagecontainer").getElementsByTagName("img");
		    for(i=0; i < imgs.length; i++) 
		    {
			    var next = imgs[i].src.substr(0,imgs[i].src.indexOf("<%=request.getContextPath()%>")) + "<%=request.getContextPath()%>" + data;
				imgs[i].src =  next + "?size=400";
		    }
			var ref = document.getElementById("randomhref");
			var mySplitResult = next.split("/imager/");
			var nexthref = mySplitResult[0] + "/viewer/" + mySplitResult[1];
			ref.href=nexthref;
	  }
	  });
	}
	
    function homeinit()
    {
    // do it one time to load first image;
      refreshImage();
	  var hometimer = new dojox.timing.Timer(10000);
      hometimer.onTick = refreshImage;      
      hometimer.start();
    }

    dojo.addOnLoad(homeinit);
	

</SCRIPT>
<div id="homecontent">
<table width="100%" align="center">
	<TR valign="top">
		<TD>
		<H2><bean:message key="home.heading" /></H2>
		</TD>
	</TR>
</table>
<div class="groupbox">
<div align="center" class="infotext"><bean:message
	key="home.info.heading" /></div>
<marquee direction="up" loop="true" scrolldelay="400" height="100">
<table>
	<logic:iterate id="element" name="resource_list" indexId="index">
		<logic:notEqual name="element" property="path" value="/docs/WebStats">
			<%
			String str2 = "/viewer" + ((Resource) element).getPath();
			%>
			<tr>
				<td><%=DateFormat.getDateInstance(DateFormat.MEDIUM)
				    .format(((Resource) element).getLastUpdate())%>
				</td>
				<td></td>
				<td><html:link page="<%=str2%>">
					<bean:write name="element" property="path" />
				</html:link></td>
			</tr>
		</logic:notEqual>
	</logic:iterate>
</table>
</marquee>
</div>
<BR>
<BR>

<div id="imagecontainer"><A id="randomhref" href="javascript:#;"><html:img
	alt="Randomly seleted picture" title="Randomly seleted picture"
	page="/images/blank.gif" /></A> <br>
<div class="infotext" align="center"><bean:message
	key="home.image.caption" /></div>
</div>
<div id="copy">
We have this web site so that people can stay up to date with our lives
in Liberty Hill, Texas. Jess and I met in July of 2000 through Jess'
cousin, Alli, who happened to be my next door neighbor. In December
2000, we were engaged in New York City and married in St. John, USVI on
May 26, 2001. We have two boys, Nicholas (Nick) and Benjamin (Ben) who
continue to teach us lessons daily and a chocolate lab named Killian. If
you visit the remainder of the site you are sure to see many photos of
our happy family.
<BR>
<BR>
Liberty Hill is about an hour driving distance from Austin and is a
fantastic place to live and we enjoy it tremendously.
<A href="javascript:popup('http://www.austintexas.org');">Austin</A>
is the capital of the great state of Texas but it is also known as the
"Live music capital of the world". Every year Austin hosts a music
festival called South by Southwest where over a thousand bands play in
the local clubs. Liberty Hill is country living and seems years away
from the big city hustle and bustle. We live on 16 acres and have
numerous goats which help to trim up things.
<BR>
<BR>
I work for
<A href="javascript:popup('http://www.paypal.com');">PayPal</A>
, the online payment service owned by
<A href="javascript:popup('http://www.ebay.com');">eBay</A>. 
Jess is a registered nurse, who loves her post partum position at
<A href="javascript:popup('http://stdavids.com/sdrrmc.aspx');">Round
Rock Medical Center</A>.
<BR>
<BR>
Thanks for visiting,
<BR>
Rick, Jess, Nick and Ben!
</div>
</div>