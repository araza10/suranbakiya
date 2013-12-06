<%-- <%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
 --%><%@page import="javax.xml.crypto.AlgorithmMethod"%>
<%@ page contentType="text/html; charset=UTF-8" 
 import="java.io.*, java.util.*, org.irdresearch.tbreach2.shared.model.Users"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<script type='text/JavaScript' src='datetimepicker_css.js'>
</script>
<%! public ArrayList<String> a = new ArrayList<String>(); %>
<script type="text/javascript">
var rowelementarray; 
function somefunction()
{//alert("PC");
	 rowelementarray = new Array();
	var tabletid = document.getElementById("tever");
	 //for (var r = 0, n = tabletid.rows.length; r < n; r++) {
		 for (var r = 0, n = 1; r < n; r++) {
		 //alert("SD");
		 //alert(tabletid.rows[r].cells.length);
         for (var c = 0, m = tabletid.rows[r].cells.length; c < m; c++) {
            //alert(tabletid.rows[r].cells[c].innerText);
         // alert(tabletid.rows[r].cells[0].getElementsByTagName("input")[0].value);
        // alert(c);
         if(c==4)
        	 {//alert("Inside");
        	 	var selc = document.getElementById("sino");
        	 	var selc1 = document.getElementById("pino");
        	 	var selc2 = document.getElementById("tino");
        	 	var pop = selc.options[selc.selectedIndex].text;
        	 	var pop1 = selc1.options[selc1.selectedIndex].text;
        	 	var pop2 = selc2.options[selc2.selectedIndex].text;
        	 	
        	 	var pops = pop+" "+pop1+" "+pop2;
        	 	 rowelementarray[c]=pops;
        	 	
        	 }
         else{
        	
         
             rowelementarray[c] = tabletid.rows[r].cells[c].children[0].value;
             
             }
         
         
			
            
         }
     }
		 var chw = document.getElementById("wat").value;
		// alert(chw);
		 var version = "130130621";
		 var type = "spresf";
		 //alert(rowelementarray[0]+" "+rowelementarray[1]+" "+rowelementarray[2]+" "+rowelementarray[3]+" "+rowelementarray[4]+version+" "+"spresf");
		 document.getElementById("fomry").action='tbrmobile.jsp?id='+rowelementarray[0]+'&labid='+rowelementarray[1]+'&date='+rowelementarray[2]+'&facility='+rowelementarray[3]
		 +'&result='+rowelementarray[4]+'&appver='+version+'&type='+type+'&sd='+rowelementarray[2]+'&st='+rowelementarray[2]+'&et='+rowelementarray[2]+'&chwid='+chw;
			document.getElementById("fomry").submit();
		// alert("till here!!");
	 addingrows();
	 document.getElementById("date1").value="";
	 document.getElementById("1").value="";
	 document.getElementById("2").value="";
	 document.getElementById("3").value="";
	 document.getElementById("sino").value="";
	 document.getElementById("pino").value="";
	 document.getElementById("tino").value="";
	 
}
function addingrows()
{
	
	//while(paju<rowelementarray.length){
	tabBody=document.getElementById("tever");
    row=document.createElement("TR");
    cell1 = document.createElement("TD");
    cell2 = document.createElement("TD");
    cell3 = document.createElement("TD");
    cell4 = document.createElement("TD");
    cell5 = document.createElement("TD");
   /*  cell6 = document.createElement("TD");
    cell7 = document.createElement("TD"); */
    /* cell6 = document.createElement("TD");
    cell7 = document.createElement("TD");
    cell8 = document.createElement("TD");
    cell9 = document.createElement("TD"); */
    textnode1=document.createTextNode(rowelementarray[0]);
    textnode2=document.createTextNode(rowelementarray[1]);
    textnode3=document.createTextNode(rowelementarray[2]);
    textnode4=document.createTextNode(rowelementarray[3]);
    textnode5=document.createTextNode(rowelementarray[4]);
    /* textnode6=document.createTextNode(rowelementarray[5]);
    textnode7=document.createTextNode(rowelementarray[6]);
    textnode8=document.createTextNode(rowelementarray[7]);
    textnode9=document.createTextNode(rowelementarray[8]); */
    cell1.appendChild(textnode1);
    cell2.appendChild(textnode2);
    cell3.appendChild(textnode3);
    cell4.appendChild(textnode4);
    cell5.appendChild(textnode5);
    /* cell6.appendChild(textnode6);
    cell7.appendChild(textnode7);
    cell8.appendChild(textnode8);
    cell9.appendChild(textnode9); */
    row.appendChild(cell1);
    row.appendChild(cell2);
    row.appendChild(cell3);
    row.appendChild(cell4);
    row.appendChild(cell5);
    /* row.appendChild(cell6);
    row.appendChild(cell7);
    row.appendChild(cell8);
    row.appendChild(cell9); */
    tabBody.appendChild(row);//}
  /*  var entireTable = document.getElementById("chav");
   var rowss = entireTable.rows.length; */
   
}
</script>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Registration Book</title>
</head>
<body>
<% Users u =(Users)session.getAttribute("users");

%>
<form id ="fomry" method="post" action="sputumResult">

 <%--  <%@ include file="grid.jsp"%>  --%>
 <table border="1" width="100%" id="chav">
    <thead>
    <tr>				<th>Раками шахсии иштирокчи</th>
						<th>№</th>
						<th>Сана</th>
						<th>Номгуи муассиса тибби</th>
						<!-- <th>Type of Analysis</th> -->
						<th>Натичаи ташхис</th>
						
						</tr>
						 <tr>
						<!--  <th style="border:0px"></th>
						 <th style="border:0px"></th>
						 <th style="border:0px"></th>
						 <th style="border:0px"></th> -->
						 <!-- <th>Diagnostic</th>
						 <th>Control</th>
						 <th>1</th>
						 <th>2</th>
						 <th>3</th> -->
						 </tr>
    

    </thead>
     
      <tbody class="rows" id="tever">
      
     <tr height="20">
    <td><input type="text" id="1" name="1"/></td>
    <td><input type="text" id="2"name="2" readonly="readonly" value=<%=session.getAttribute("currentuser")%>></td>
    <!-- <td><input id="date1" name="date1" onclick='scwShow(this,event);' readonly="readonly"  value=""/>
                    <input onclick="scwShow(scwID('date1'),event);" style="cursor:pointer;width: 16px;height:16px;background-image: url('images/calendar_icon.png');"/></td>
 -->               
    <td><input id="date1" name="date1" onclick="NewCssCal ('date1','yyyyMMdd','dropdown',true,'24', true)" readonly="readonly" value=""/>
                    <input id="date1" onclick="NewCssCal ('date1','yyyyMMdd','dropdown',true,'24', true);" style="cursor:pointer;width: 16px;height:16px;background-image: url('images/cal.gif');"/>
    </td>           					                
                    <td><input type="text" id="3" name="3"/></td>
                    
    <td><select id="sino" name="sino" >
 	<option>Манфй</option>
 	<option>1-9 AFB</option>
 	<option>Пусиш</option>
 	<option>1+</option>
 	<option>2+</option>
 	<option>3+</option>
    </select>
    <select id="pino" name="pino" >
 	<option>Манфй</option>
 	<option>1-9 AFB</option>
 	<option>Пусиш</option>
 	<option>1+</option>
 	<option>2+</option>
 	<option>3+</option>
    </select>
    <select id="tino" name="tino" >
 	<option>Манфй</option>
 	<option>1-9 AFB</option>
 	<option>Пусиш</option>
 	<option>1+</option>
 	<option>2+</option>
 	<option>3+</option>
    </select>
    </td>
     </tr>
     <%
     
     if(request.getAttribute("id")!=null && request.getAttribute("Error")==null){
     
    	 int lenghtoffacility = (Integer)request.getAttribute("facilityLength");
     	if(lenghtoffacility>=10)
     	{
     		//place some check in arraylist signifying true or false, if the length is greater than 10 or not
     		
     	
    	 String lines = "1"+request.getAttribute("id")+""+session.getAttribute("currentuser")+""+request.getAttribute("enteredDate")+""+request.getAttribute("facility")
    		 +request.getAttribute("smearResult")+""+request.getAttribute("smearResult1")+""+request.getAttribute("smearResult2")+""+request.getAttribute("facilityLength");
     
     
     a.add(lines);
     
     %>
     <%session.setAttribute("a", a);
     
     }
     
     	else{
     		 String lines = "0"+request.getAttribute("id")+""+session.getAttribute("currentuser")+""+request.getAttribute("enteredDate")+""+request.getAttribute("facility")
     	    		 +request.getAttribute("smearResult")+""+request.getAttribute("smearResult1")+""+request.getAttribute("smearResult2")+""+request.getAttribute("facilityLength");
     	     
     	     
     	     a.add(lines);
     	     
     	     %>
     	     <%session.setAttribute("a", a);
     	}
     %>
     
    
     
     <% for(int i=0;i<a.size();i++)
     {%><tr height="20">
    	 <td><%out.println(a.get(i).substring(1, 9)); %></td>
    	 <td><%out.println(a.get(i).substring(9, 11)); %></td>
    	  <td><%out.println(a.get(i).substring(11, 30)); %></td>
    	  <% 
    	  if(a.get(i).substring(0, 1).equals("1"))
    	  {%>
    	  <%String abb =  a.get(i).substring(a.get(i).length()-2, a.get(i).length());%>
    	   <td><%out.println(a.get(i).substring(30, 30+Integer.parseInt(abb))); %></td>
    	   <td><%out.println(a.get(i).substring(30+Integer.parseInt(abb), a.get(i).length()-2)); %></td>
    	   <%-- <td><%out.println(a.get(i).substring(a.get(i).length()-2, a.get(i).length())); %></td> --%>
    	  <%}
    	  else{
    	  %>
    	 	<%String avv = a.get(i).substring(a.get(i).length()-1, a.get(i).length()); %>
    	   <td><%out.println(a.get(i).substring(30, 30+Integer.parseInt(avv))); %></td>
    	   <td><%out.println(a.get(i).substring(30+Integer.parseInt(avv), a.get(i).length()-1)); %></td>
    	   <%-- <td><%out.println(a.get(i).substring(a.get(i).length()-1, a.get(i).length())); %></td> --%>
    	   <%} %>
    	   	  <%-- <td><%out.println(a.get(i)); %></td> --%>
    	   	  </tr>
     <%}
     }
     %>
<%--       <td><% if(session.getAttribute("id")!=null){%><%=session.getAttribute("id")%><%}%></td>
     <td><% if(session.getAttribute("currentuser")!=null){%><%=session.getAttribute("currentuser")%><%}%></td>
     <td><% if(session.getAttribute("enteredDate")!=null){%><%=session.getAttribute("enteredDate")%><%}%></td>
     <td><% if(session.getAttribute("facility")!=null){%><%=session.getAttribute("facility")%><%}%></td>
     <td><% if(session.getAttribute("smearResult")!=null){%><%=session.getAttribute("smearResult")%> <%=session.getAttribute("smearResult1")%> <%=session.getAttribute("smearResult2")%><%}%></td>
 --%>	<%-- <%int count=0;
 			String[] a = new String[5];
 			a[0] = (String)session.getAttribute("id");
 			a[1] = (String)session.getAttribute("currentuser");
 			a[2] = (String)session.getAttribute("enteredDate");
 			a[3] = (String)session.getAttribute("facility");
 			a[4] = (String)session.getAttribute("smearResult")+" "+(String)session.getAttribute("smearResult2")+" "+(String)session.getAttribute("smearResult2");
		
 		%> --%>
 	    	    
     
      
     </tbody> 
    
    </table>
   <input type="submit" value="Save"/>
	<input type="hidden" id="wat" name="wat" value=<%=u.getUserName()%>>

</form>
</body>
<span style ="color:red"><% if(request.getAttribute("Error")!=null){%><%=request.getAttribute("Error")%><%}%></span>
</html>