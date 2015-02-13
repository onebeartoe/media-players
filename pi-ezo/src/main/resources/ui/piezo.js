
function loadSongList()
{
    var url = "songs";
    
    var elementName = "songList";
elementName = "logs";

    logEvent("loading " + elementName + "...");
    
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange=function()
    {
        if (xmlhttp.readyState==4 && xmlhttp.status==200)      
        {
            var list = xmlhttp.responseText;
            
            var names = list.split("-+-");
            
            var html = "";
            
            for(var n in names)
            {
                var name = names[n].trim();

                html += name + "<br>";
            }

            var e = document.getElementById(elementName);
            e.innerHTML = html;
            
            logEvent("done loading " + elementName);
        }
    }
    
    xmlhttp.open("POST", url, true);
    xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
    xmlhttp.send("&p=3");    
}

function logEvent(message)
{
    var e = document.getElementById("logs");
    
    var logs = message + "<br/>" + e.innerHTML;
    
    e.innerHTML = logs;
}
