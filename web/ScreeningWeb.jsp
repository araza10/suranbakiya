<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="ISO-8859-1"
    
 import="java.io.*,java.util.*,org.irdresearch.tbreach2.server.DataTable"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" type="text/css" href="css/data_table.css" />
<link rel="stylesheet" href="css/jquery.ui.all.css" />
<link rel="stylesheet" type="text/css" href="css/demos.css" />


<script type="text/javascript" charset="utf-8"> </script>
<script src="js/jquery.js"></script>
<script src="js/jquery.ui.core.js"></script>
<script src="js/jquery.ui.datepicker.js"></script>
<script src="js/jquery.ui.widget.js"></script>
<script type="text/javascript" charset="utf-8" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" charset="utf-8" src="media/js/ZeroClipboard.js"></script>
<script type="text/javascript" charset="utf-8" src="media/js/TableTools.js"></script>

<style type="text/css" title="currentStyle">
			@import "media/css/TableTools.css";
		</style>
		


 <script type='text/javascript'>
    function doSomething(id, sop){
        var scridd = document.getElementById(id).innerHTML;
        var scrid = document.getElementById(sop).value;
       
       // alert(scridd);
        if(!(scrid=="false" || scrid=="true"))
        	{
        	alert("Only allowed to enter true or false!!");
        	}
        
		else {
			 var date = document.getElementById('datepicker').value;
			//alert("Please enter dates");
			//alert(date);
			//assign patient ids to all such patients
			window.location = 'UpdateScreeningServlet?screeningID='+scridd +'&suspect='+scrid+'&date='+date;
			 }

		}
    

    
	</script> 
    <script type="text/javascript">
    $(document).ready(function(){
        $('#datatable').dataTable({
        	
            "iDisplayLength": 20,  
            "sPaginationType": "full_numbers", 
            "aLengthMenu": [[20, 50, 100, -1], [20, 50, 100, "All"]], 
          /*  "aLengthMenu": [[20, 50, 100,]], */
           "sEmptyTable": "No record found",
           "bDeferRender": true,
           "oLanguage": {
               "sSearch": "Filter: "
           },
           "sDom": 'T<"clear">lfrtip',
       	 "oTableTools": {
				/* "sSwfPath": "media/swf/xls.swf" */
				"aButtons": [
               {
                   "sExtends": "xls",
                   "sButtonText": "Excel",
                   "mColumns": [ 0, 1, 2, 3, 4, 5, 7, 8, 9, 10, 11, 12 ]
               },
               
           ]
		}
        });
        
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

<%

	String ss = request.getParameter("date");
	String ss1 = request.getParameter("dateSecond");
	String po = "";
	String po2 = "";
	if (ss != null && ss1 != null) {
		po = ss;
		po2 = ss1;
	}
%><!-- action="ScreeningWeb.jsp" -->
<body>
	<form name="dateForm" action="ScreeningWeb.jsp">
		Select Date: <input name="date" type="text" id="datepicker" value="<%=po%>" /> 
		Select DateSecond: <input name="dateSecond" type="text" id="datepicker2" value="<%=po2%>" /> 
			<input type="submit" value="Submit">
	</form>


	<%
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
	%>

</body>
</html>