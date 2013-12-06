<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Update User</title>
</head>
<body>
<form id="frm" action="UpdateUserServlet" method="post">
<div style="margin-left: 2em">
<p><b>Update User</b></p>
<table>
<tr>
<td align="left" valign="top">Enter PID&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="text" id="upid" name="upid" size=20/>
</td>
</tr>
<tr>
<td align="left" valign="top">Password&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="text" id="upwd" name="upwd" size=20/>
</td>
</tr>
<tr><td align="left" valign="top" >Status of User&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<select id="ustatus" name="ustatus">
<option>ACTIVE</option>
<option>SUSPEND</option>
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
if(request.getAttribute("updateuserexception")!=null){%>
	<%=request.getAttribute("updateuserexception") %>
	<%}
 	
 %>
</span><br>
</html>