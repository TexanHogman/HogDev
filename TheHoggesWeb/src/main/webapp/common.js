// common.js
// bust some frames bee-otch
if(top.location!= document.location) 
 top.location = document.location; 


function popup(href, windowname)
{
	window.open(href, windowname);
}

function popup(href, windowname, params)
{
	window.open(href, windowname, params);
}

function doOnload()
{
}

function setCookie(c_name,value,expiredays)
{
	var exdate=new Date();
	exdate.setDate(exdate.getDate()+expiredays);
	document.cookie=c_name+ "=" +escape(value)+ ((expiredays==null) ? "" : "; expires="+exdate.toGMTString() + "; path=/");
}

