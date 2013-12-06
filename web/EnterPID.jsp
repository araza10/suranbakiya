<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<script type="text/javascript">
/* function smefunction()
{
	var usr = document.getElementById("hw");
	var pss = document.getElementById("pw");
	document.getElementById("frm").action='loginServlet?usr='+usr+'&=pass'+pss+'&appver='+"130130621"+'&type='+"lg";
	document.getElementById("frm").submit();
	
} */
</script>
<html>
<body>
<form id="frm" action="loginServlet" method="post">
<div style="margin-left: 2em">
<p><b>Lab Tech Login</b></p>
<table>
<tr>
<td align="left" valign="top">Enter User ID:  <input type="text" id="hw" name="hw" size=20/>
</td>
</tr>
<tr></tr>
<tr><td align="left" valign="top" >Password:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="password" id="pw" name="pw" size=20/></td></tr>
<tr>
<td align="left" valign="top">
<input type="submit" value="Submit" onclick="smefunction();"/>
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
<a href="UserManagement.jsp">User Management</a>
</html>
