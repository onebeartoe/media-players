
function soundMeterModeChanged(mode)
{
//    alert('hiOI');
    
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange=function()
    {
        if (xmlhttp.readyState==4 && xmlhttp.status==200)      
        {
            var s = xmlhttp.responseText + "<br/>" + document.getElementById("logs").innerHTML;
            document.getElementById("logs").innerHTML = s;
        }
    }
    var param = "sound-meter-mode=" + mode;
    var url = "/?" + param;    
    xmlhttp.open("POST", url, true);
    xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
    xmlhttp.send("&p=3");
}
