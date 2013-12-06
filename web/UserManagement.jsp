<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>User Management</title>
</head>
<body>
<form id="frm" action="AdminServlet" method="post">
<div style="margin-left: 2em">
<p><b>Admin Login</b></p>
<table>
<tr>
<td align="left" valign="top">Enter ID:&nbsp;&nbsp;&nbsp;  <input type="text" id="admin" name="admin" size=20/>
</td>
</tr>
<tr></tr>
<tr><td align="left" valign="top" >Password:&nbsp;&nbsp;<input type="password" id="adminpw" name="adminpw" size=20/></td></tr>
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
if(request.getAttribute("Invalid")!=null){out.println(request.getAttribute("Invalid"));}
 	
 %>
</span><br>
</html>