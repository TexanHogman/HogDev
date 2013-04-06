var lastbg = '';

function doClick(id)
{
	but = document.getElementById(id);
	but.style.backgroundColor = 'blue';
	lastbg = but.style.backgroundImage;
	but.style.backgroundImage = 'none';
	setTimeout("doReset('" + id + "')", 150);		
}
function doReset(id)
{
	but = document.getElementById(id);
	but.style.backgroundImage = lastbg;
	but.style.backgroundColor = 'white';
}

window.addEventListener('load', function(){
	setTimeout(scrollTo, 0, 0, 1);
	}, false);