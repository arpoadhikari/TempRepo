var path;
var defaultTargetValue = "";

document.onmouseover = trackElement;
document.onkeydown = getElementInfo;
document.onmouseout = disableTracking;

function trackElement(event) {

    if (event===undefined) event= window.event;
    var target = 'target' in event? event.target : event.srcElement;

    var root = document.compatMode==='CSS1Compat'? document.documentElement : document.body;
    var mxy = [event.clientX+root.scrollLeft, event.clientY+root.scrollTop];

    path = getPathTo(target);
    if (path.substring(0, 4) == "body") {
        path = "html/"+path;
    }
    var txy = getPageXY(target);
    enableHighlight(target);

    if (target.hasAttribute("title")) {
        if(target.title.substring(0, 2) != "//") {
            defaultTargetValue = target.title;
        }
        else if(target.title.substring(0, 4) != "html") {
            defaultTargetValue = target.title;
        }
        else {
            defaultTargetValue = "";
        }
    }
    target.title = 'Xpath -> '+path+'\nOffset -> '+(mxy[0]-txy[0])+', '+(mxy[1]-txy[1]);

}

function getElementInfo(event) {
    
    if (event===undefined) event = window.event;
    var target = 'target' in event? event.target : event.srcElement;
    
    const keyName = event.key;

    if (keyName === 'Control') {
        // do not alert when only Control key is pressed.
        return;
    }

    if (event.ctrlKey && event.altKey) {
        if (keyName === 'C' || keyName === 'c') { 
            resetTargetValue(target);
            //alert(path);
            prompt("Xpath", path)
        }
    }
}

function getPathTo(element) {
    if (element.id!=='')
        return "//"+element.tagName.toLowerCase()+"[@id='"+element.id+"']";
    if (element===document.body)
        return element.tagName.toLowerCase();

    var ix = 0;
    var siblings = element.parentNode.childNodes;
    for (var i = 0; i<siblings.length; i++) {
        var sibling= siblings[i];
        if (sibling===element)
            return getPathTo(element.parentNode).toLowerCase()+'/'+element.tagName.toLowerCase()+'['+(ix+1)+']';
        if (sibling.nodeType===1 && sibling.tagName===element.tagName)
            ix++;
    }
}

function getPageXY(element) {
    var x = 0, y = 0;
    while (element) {
        x+= element.offsetLeft;
        y+= element.offsetTop;
        element = element.offsetParent;
    }
    return [x, y];
}

function enableHighlight(element) {
    element.style.border ='2px solid green';
}

function disableHighlight(element) {
    element.style.border ='';
}

function disableTracking(event) {
    if (event===undefined) event = window.event;
    var target = 'target' in event? event.target : event.srcElement;
    disableHighlight(target);

    resetTargetValue(target);
}

function resetTargetValue(target) {
    if(target.title.substring(0, 2) == "//" && defaultTargetValue != "") {
            target.title = defaultTargetValue;
    }
    else if (target.title.substring(0, 4) == "html" && defaultTargetValue != "") {
            target.title = defaultTargetValue;
    }
    else {
        target.title = "";
        //target.removeAttribute("title");
    }
}
