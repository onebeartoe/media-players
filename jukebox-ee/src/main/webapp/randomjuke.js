
function initializeRandomJuke()
{   
    var xmlhttp = new XMLHttpRequest();

    xmlhttp.onreadystatechange=function(whw)
    {
        if (xmlhttp.readyState==4 && xmlhttp.status==200)      
        {
            var score = xmlhttp.responseText + "<br/>" + "Home";
            document.getElementById("homeScore").innerHTML = score;
                        
            updateLog(xmlhttp);
        }
    }
    var url = "../frame-buffer-screen";
    sendGetRequest(xmlhttp, url);
}

function nextSong()
{   
    var xmlhttp = new XMLHttpRequest();

    xmlhttp.onreadystatechange=function(whw)
    {
        if (xmlhttp.readyState==4 && xmlhttp.status==200)      
        {
            var score = xmlhttp.responseText + "<br/>" + "Home";
            document.getElementById("homeScore").innerHTML = score;
                        
            updateLog(xmlhttp);
        }
    }
    var url = "../controls/song/next";
    sendGetRequest(xmlhttp, url);
}

function sendGetRequest(xmlhttp, url)
{
    method = "GET";
    
    sendRequest(method, xmlhttp, url);
}

function sendPostRequest(xmlhttp, url)
{
    method = "POST";
    
    sendRequest(method, xmlhttp, url);
}

function sendRequest(method, xmlhttp, url)
{
    xmlhttp.open(method, url, true);
    xmlhttp.setRequestHeader("Content-type","application/x-www-form-urlencoded");
    xmlhttp.send("p1=a&p2=b");    
}

function updateLog(xmlhttp)
{
    var logEntry = xmlhttp.responseText + "<br/>" + document.getElementById("logs").innerHTML;
    
    document.getElementById("logs").innerHTML = logEntry;
}
