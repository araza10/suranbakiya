<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <% response.setHeader("Cache-Control","no-cache"); //HTTP 1.1 
 response.setHeader("Pragma","no-cache"); //HTTP 1.0 
 response.setDateHeader ("Expires", 0); //prevents caching at the proxy server  
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Add New User</title>
</head>
<body>
<form id="frm" action="AddUserServlet" method="post">
<div style="margin-left: 2em">
<p><b>Add New User</b></p>
<table>
<tr>
<td align="left" valign="top">Enter PID&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="text" id="apid" name="apid" size=20/>
</td>
</tr>
<tr>
<td align="left" valign="top">First Name&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="text" id="afname" name="afname" size=20/>
</td>
</tr>
<tr>
<td align="left" valign="top">Last Name&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="text" id="alname" name="alname" size=20/>
</td>
</tr>
<tr></tr>
<tr><td align="left" valign="top" >Location of User&nbsp;&nbsp; 
<select id="acurrentLocation" name="acurrentLocation" >
 	<option>CentralPolyClinic</option>
 	<option>PolyDushanbe2</option>
 	<option>PolyDushanbe3</option>
 	<option>PolyDushanbe4</option>
 	<option>PolyDushanbe5</option>
 	<option>PolyDushanbe6</option>
 	<option>PolyDushanbe7</option>
 	<option>PolyDushanbe8</option>
 	<option>PolyDushanbe9</option>
 	<option>PolyDushanbe10</option>
 	<option>PolyDushanbe11</option>
 	<option>PolyDushanbe12</option>
 	<option>PolyDushanbe13</option>
 	<option>PolyDushanbe14</option>
 	<option>PolyTursunzade1</option>
 	<option>PolyTursunzade2</option>
 	<option>DiabetesDushanbe1</option>
 	<option>DiabetesDushanbe2</option>
 	<option>Prisonsystem</option>
 	<option>Rudaki</option>
 	<option>PolyDushanbe1</option>
    </select></td></tr>
    
<tr><td align="left" valign="top" >Role of User&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<select id="arole" name="arole">
<option>LabTech</option>
<option>ADMIN</option>
</select>
</td>

</tr>
<tr>

<td align="left" valign="top">
<input type="submit" value="Submit"/>
</td>
</tr>
</table>
</div>
</form>
</body>
<span style ="color:red"> <%
if(request.getAttribute("adduserexception")!=null){%>
	<%=request.getAttribute("adduserexception") %>
	<%}
 	
 %>
</span><br>
</html>