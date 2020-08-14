function checkFields() {
	if(document.getElementById('user').value == "" || document.getElementById('pass').value == ""){
		window.alert("Campos vazios.");
		return false;
	}else 
	{
		return true;
	}
}
function myIP() {  
    if (window.XMLHttpRequest) xmlhttp = new XMLHttpRequest();  
    else xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");  
  
    xmlhttp.open("GET","http://api.hostip.info/get_html.php",false);  
    xmlhttp.send();  
  
    hostipInfo = xmlhttp.responseText.split("\n");  
  
    for (i=0; hostipInfo.length >= i; i++) {  
        ipAddress = hostipInfo[i].split(":");  
        if ( ipAddress[0] == "IP" ) return ipAddress[1];  
    }  
  
    return false;  
}
function getData() {
	var hoje = new Date()
	var dia = hoje.getDate()
	var mes = hoje.getMonth()
	mes = mes+1
	var ano = hoje.getFullYear()
	if (dia < 10)
	dia = '0' + dia
	if (mes < 10)
	mes = '0' + mes

	var dtatual = dia+'/'+mes+'/'+ano
	return (dtatual);
}