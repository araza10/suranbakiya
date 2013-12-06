<%@ page language="java" contentType="text/html; charset=UTF-8;"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" type="text/css" href="css/data_table.css" />
<link rel="stylesheet" href="css/jquery.ui.all.css" />
<link rel="stylesheet" type="text/css" href="css/demos.css" />


<!-- <script type="text/javascript" charset="utf-8"> </script> -->
<script src="js/jquery.js"></script>
<script src="js/jquery.ui.core.js"></script>
<script src="js/jquery.ui.datepicker.js"></script>
<script src="js/jquery.ui.widget.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Generate Screening CSV</title>
 <script type="text/javascript">
    $(document).ready(function(){
        $(function() {
        	$( "#datepicker" ).datepicker({ dateFormat: "yy-mm-dd" });
        	});
        
        $(function() {
        	$( "#datepicker2" ).datepicker({ dateFormat: "yy-mm-dd" });
        	});
        
        <%String mes = request.getParameter("ErrorMes");
			if (mes != null) {
				out.println("alert('" + mes + "');");
			}%>
    });
    </script>
</head>



<body>

	<form method="post" action="csvExport">
		Select Date: <input name="date" type="text" id="datepicker" /> 
		Select DateSecond: <input name="dateSecond" type="text" id="datepicker2" /> 
			<input type="submit" value="Submit">
	</form>


<%-- 	<%
		String toDate = request.getParameter("date");
		String tosDate = request.getParameter("dateSecond");
		DataTable d=null;
		//out.println("alert('" + toDate + "');");
		//getting the screening data filtered by Date here in html format
		if (toDate != null && toDate !="" && tosDate=="") {
			d= new DataTable();
			out.println(d.getTableData(toDate));
			//System.out.println(toDate);
		}
		
		else if (toDate != null && tosDate!=null) {
			d= new DataTable();
			out.println(d.getTableData(toDate, tosDate));
			//System.out.println(toDate);
		} 
	%> --%>


</body>
</html>