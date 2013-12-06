package org.irdresearch.tbreach2.server;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.Date;
import java.util.zip.ZipOutputStream;
import org.irdresearch.tbreach2.mobileevent.CSVUtil;
import org.irdresearch.tbreach2.mobileevent.DateUtils;
import au.com.bytecode.opencsv.*;

/**
 * Servlet implementation class csvExport
 */
public class csvExport extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static ServerServiceImpl ssl = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public csvExport() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		createCSVAll(request, response);
	}
	
/*	public static void createCSVAll(HttpServletRequest request, HttpServletResponse response)
	{
		
		ssl = new ServerServiceImpl ();
		String date = request.getParameter("date");
		String dateSecond = request.getParameter("dateSecond");
		
        try{

        	String[] s={"ScreeningID","PatientID","screening.FirstName","screening.LastName","Age","Gender","Suspect","Cough","CoughDuration","ProductiveCough","Fever","DateEntered","LocationName"};
    		String record[][] = ssl.getTableData("screening, location", s, "DateEntered between '"+date+"%' and '"+dateSecond+" 23:59:59"+"' and " +
    				"screening.ScreenLocation=location.LocationID and Suspect=true");
    		
    		response.setContentType("application/octet-stream; charset=UTF-8");
    		response.setHeader("Content-Disposition", "attachment;filename=this.csv"); 	// response.setHeader("Content-Disposition", "attachment; filename=PIFExport_"+DateUtils.convertToString(new Date()).substring(0,10)+".zip");
    		
    		String csvOutput = new String();

    		String[][] stringData = new String[record.length][];//[row][column]
			
    		for (int i = 0; i < record.length; i++)//first row length
    		{
    			stringData[i] = new String[record[i].length];//row length which is 10
    			for (int j = 0; j < record[i].length; j++)
    			{
    				if (record[i][j] == null)
    					record[i][j] = "";
    				
    				csvOutput+= "\""+record[i][j].toString ()+"\"";
    				csvOutput+=",";
    				
    			}
    			csvOutput+="\n";
    		}
    		
	       
	       ServletOutputStream out = response.getOutputStream();
	   
	       
	       out.write(csvOutput.getBytes("ISO-8859-1"), 0, 4096);
	       
	       
	       out.flush();
	       out.close();
    		
    		
    		
    		

    		
 			
        } catch (Exception e) {
			e.printStackTrace();
		}
		
	}*/
	
	

	
	public static void createCSVAll(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		
		ssl = new ServerServiceImpl ();
		String date = request.getParameter("date");
		String dateSecond = request.getParameter("dateSecond");
		
        try{

        	String[] s={"ScreeningID","PatientID","screening.FirstName","screening.LastName","CHWID","Age","Gender","Suspect","Cough","CoughDuration","ProductiveCough","Fever","DateEntered","LocationName"};
    		String screeningData[][] = ssl.getTableData("screening, location", s, "DateEntered between '"+date+"%' and '"+dateSecond+" 23:59:59"+"' and " +
    				"screening.ScreenLocation=location.LocationID");
    		
    		
    		
			response.setContentType("application/zip"); 
			
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Content-Disposition", "attachment; filename=ScreeningExport_"+DateUtils.convertToString(new Date()).substring(0,10)+".zip"); 


			ZipOutputStream zip = null;
			try {
				zip = new ZipOutputStream(response.getOutputStream());
				
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
			CSVUtil.makeCsv(zip,screeningData);
 			
        }
        catch (Exception e) {
			e.printStackTrace();
		}
		
	}


}
