// https://github.com/yanatan16/nanoajax
!function(t,e){function n(t){return t&&e.XDomainRequest&&!/MSIE 1/.test(navigator.userAgent)?new XDomainRequest:e.XMLHttpRequest?new XMLHttpRequest:void 0}function o(t,e,n){t[e]=t[e]||n}var r=["responseType","withCredentials","timeout","onprogress"];t.ajax=function(t,a){function s(t,e){return function(){c||(a(void 0===f.status?t:f.status,0===f.status?"Error":f.response||f.responseText||e,f),c=!0)}}var u=t.headers||{},i=t.body,d=t.method||(i?"POST":"GET"),c=!1,f=n(t.cors);f.open(d,t.url,!0);var l=f.onload=s(200);f.onreadystatechange=function(){4===f.readyState&&l()},f.onerror=s(null,"Error"),f.ontimeout=s(null,"Timeout"),f.onabort=s(null,"Abort"),i&&(o(u,"X-Requested-With","XMLHttpRequest"),e.FormData&&i instanceof e.FormData||o(u,"Content-Type","application/x-www-form-urlencoded"));for(var p,m=0,v=r.length;v>m;m++)p=r[m],void 0!==t[p]&&(f[p]=t[p]);for(var p in u)f.setRequestHeader(p,u[p]);return f.send(i),f},e.nanoajax=t}({},function(){return this}());

nanoajax.ajax({url:'/api/pathFeedback?path=' + window.location.pathname}, function(code, responseText){
 if(responseText != undefined) document.getElementById("feedbackCount").innerHTML = responseText
})

var fedback = false
function feedback(elem, score){
    if(fedback) return;
    fedback = true;
    elem.style.opacity = 1
    document.getElementById("feedbackMessage").innerHTML = "Thanks.";
    if(elem.id == "plus1"){
        document.getElementById("minus1").style.display = "none";
        document.getElementById("plus1").style.cursor = "default";
    }else{
        document.getElementById("plus1").style.display = "none";
        document.getElementById("minus1").style.cursor = "default";
    }
    nanoajax.ajax({url:'/api/feedback?path=' + window.location.pathname + '&upVotes=' + score}, function (code, responseText) {

        if(responseText != undefined) document.getElementById("feedbackCount").innerHTML = responseText

    })
}
