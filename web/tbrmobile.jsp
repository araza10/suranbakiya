<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="org.irdresearch.tbreach2.mobileevent.EventHandler" %>
<%
EventHandler eh = new EventHandler();
eh.setRequest(request);
String xmlResponse = eh.handleEvent();
System.out.println(xmlResponse);
out.println(xmlResponse.substring(54,xmlResponse.length()).trim());
%>
