<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Change Location</title>
</head>
<body>

<form id="frm" action="changeLocationServlet" method="post">
<div style="margin-left: 2em">
<p><b>Change User Location</b></p>
<table>
<tr>
<td align="left" valign="top">Enter User ID&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="text" id="uw" name="uw" size=20/>
</td>
</tr>
<tr></tr>
<tr><td align="left" valign="top" >Current Location&nbsp;&nbsp; 
<select id="currentLocation" name="currentLocation" >
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
    </select></tr>
    
    <tr><td align="left" valign="top" >New Location&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
<select id="newLocation" name="newLocation" >
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
    </select></tr>
<tr>
<td align="left" valign="top">
<input type="submit" value="Submit"/>
</td>
</tr>
</table>
</div>
</form>
<span style="margin-left: 2em; color: red"><% if(session.getAttribute("usersattribute")!=null){%><%=session.getAttribute("usersattribute")%><%}%></span>
</body>
</html>