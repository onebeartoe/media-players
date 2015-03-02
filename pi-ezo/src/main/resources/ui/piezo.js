
function hideElement(id)
{
    var element = document.getElementById(id);
    element.style.display = 'none';
}

function loadSongList()
{
    var url = "/songs";

    var elementName = "songList";
    
    logEvent("loading " + elementName + "...");
    
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange=function()
    {
        if (xmlhttp.readyState==4 && xmlhttp.status==200)      
        {
            var list = xmlhttp.responseText;
            
            var songList = list.split("-+-");

            for(var n in songList)
            {
                var songEntry = songList[n].trim();

                var songData = songEntry.split(":");
                
                var index = songData[0];
                var title = songData[1];

//TODO: rename x to selectElement
                var x = document.getElementById(elementName);
                var option = document.createElement("option");
                option.value = index;
                option.text = title;
                x.add(option);
            }

            logEvent("done loading " + elementName);
        }
    };
    
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

function logServerResponse(xmlhttp)
{
    if (xmlhttp.readyState==4 && xmlhttp.status==200)      
    {
        var s = xmlhttp.responseText;
        logEvent(s);
    }
}

function playRtttl(rtttlData)
{
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange=function()
    {
        logServerResponse(xmlhttp);
    };
    
    var url = "/rtttl" + "?" + rtttlData;
    
    xmlhttp.open("POST", url, true);
    xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
    xmlhttp.send("&p=3");    
}

function playSong(id)
{
    var xmlhttp = new XMLHttpRequest();
    xmlhttp.onreadystatechange=function()
    {
        logServerResponse(xmlhttp);
    };
    
    var url = "/play/" + id;
    
    xmlhttp.open("POST", url, true);
    xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
    xmlhttp.send("&p=3");
}

function showBuiltInControls()
{
    hideElement('onTheFlyControls');
    
    showElement('songListControls');
}

function showOnTheFlyControls()
{
    hideElement('songListControls');
    
    showElement('onTheFlyControls');
}

function showElement(id)
{
    var element = document.getElementById(id);
    element.style.display = 'block';
}
