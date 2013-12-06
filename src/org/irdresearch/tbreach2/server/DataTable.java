package org.irdresearch.tbreach2.server;

import java.util.Date;

public class DataTable {
	ServerServiceImpl ssl = null;
	
	public DataTable()
	{
		
	}
	
	public String getTableData(String date)
	{
		ssl = new ServerServiceImpl ();
		String[] s={"ScreeningID","PatientID","FirstName","LastName","Age","Gender","Suspect","Cough","CoughDuration","ProductiveCough","Fever","DateEntered","LocationName"};
		Object screeningData[][] = ssl.getTableObject("screening, location", s, "DateEntered like '"+date+"%' and " +
				"screening.ScreenLocation=location.LocationID");
		//String columnData = null;
		/*
		for(int i=0;i<screeningData.length;i++)
		{
			for(int j=0; j<screeningData[i].length; j++)
			{
				columnData = screeningData[i][j];
				
			}
		}*/
		
		
		//html table
		String entireHTML = "<table id=\"datatable\" class=\"display\">" +
				"<thead> \n" +
				"<tr> \n" +
				"<th>Screening ID</th> \n" +
				"<th>Patient ID</th> \n" +
				"<th>First Name</th> \n" +
				"<th>Last Name</th> \n" +
				"<th>Age</th> \n" +
				"<th>Gender</th> \n" +
				"<th>Suspect</th> \n" +
				"<th>Cough</th> \n" +
				"<th>Cough Duration</th> \n" +
				"<th>Productive Cough</th> \n" +
				"<th>Fever</th> \n" +
				"<th>DateEntered</th> \n" +
				"<th>LocationName</th> \n" +
				
				"<th>Update</th> \n" +
				
				"</tr></thead> \n" +
				"<tbody>";
				for(int i=0;i<screeningData.length;i++) {
					if(screeningData[i][1]==null)
					{screeningData[i][1]="";}
					if(screeningData[i][2]==null)
					{screeningData[i][2]="";}
					if(screeningData[i][3]==null)
					{screeningData[i][3]="";}
					if(screeningData[i][8]==null)
					{screeningData[i][8]="";}
					if(screeningData[i][9]==null)
					{screeningData[i][9]="";}
					if(screeningData[i][10]==null)
					{screeningData[i][10]="";}
					if(screeningData[i][11]==null)
					{screeningData[i][11]="";}

					
					entireHTML+="<tr> \n"+				
					"<td id = '"+screeningData[i][0]+"'>"+screeningData[i][0]+"</td> \n"+
					"<td>"+screeningData[i][1]+"</td> \n"+
					"<td>"+screeningData[i][2]+"</td> \n"+
					"<td>"+screeningData[i][3]+"</td> \n"+
					"<td>"+screeningData[i][4]+"</td> \n"+
					"<td>"+screeningData[i][5]+"</td> \n"+
					"<td><input id = 'suspt"+screeningData[i][0]+"' type='text' value='"+screeningData[i][6]+"'/></td> \n"+
					
					
					"<td>"+screeningData[i][7]+"</td> \n"+
					"<td>"+screeningData[i][8]+"</td> \n"+
					"<td>"+screeningData[i][9]+"</td> \n"+
					"<td>"+screeningData[i][10]+"</td> \n"+
					"<td>"+screeningData[i][11]+"</td> \n"+
					"<td>"+screeningData[i][12]+"</td> \n"+
					
					"<td> <button onclick=\"doSomething('"+screeningData[i][0]+"', 'suspt"+screeningData[i][0]+"');\">Update</button></td> \n" +
					
					"</tr> \n";
				}
				
				entireHTML+="</tbody>"+
				"</table>";
		
		return entireHTML;

		
	}
	
	public String getTableData(String date, String date2)
	{
	
		ssl = new ServerServiceImpl ();
		String[] s={"ScreeningID","PatientID","FirstName","LastName","Age","Gender","Suspect","Cough","CoughDuration","ProductiveCough","Fever","DateEntered","LocationName"};
		String screeningData[][] = ssl.getTableData("screening, location", s, "DateEntered between '"+date+"%' and '"+date2+" 23:59:59"+"' "+
				"and screening.ScreenLocation=location.LocationID");
		//String columnData = null;
		/*
		for(int i=0;i<screeningData.length;i++)
		{
			for(int j=0; j<screeningData[i].length; j++)
			{
				columnData = screeningData[i][j];
				
			}
		}*/
		
		
		//html table
		String entireHTML = "<table id=\"datatable\" class=\"display\">" +
				"<thead> \n" +
				"<tr> \n" +
				"<th>Screening ID</th> \n" +
				"<th>Patient ID</th> \n" +
				"<th>First Name</th> \n" +
				"<th>Last Name</th> \n" +
				"<th>Age</th> \n" +
				"<th>Gender</th> \n" +
				"<th>Suspect</th> \n" +
				"<th>Cough</th> \n" +
				"<th>Cough Duration</th> \n" +
				"<th>Productive Cough</th> \n" +
				"<th>Fever</th> \n" +
				"<th>DateEntered</th> \n" +
				"<th>LocationName</th> \n" +
				
				"<th>Update</th> \n" +
				
				"</tr></thead> \n" +
				"<tbody>";
				for(int i=0;i<screeningData.length;i++) {
					if(screeningData[i][1]==null)
					{screeningData[i][1]="";}
					if(screeningData[i][2]==null)
					{screeningData[i][2]="";}
					if(screeningData[i][3]==null)
					{screeningData[i][3]="";}
					if(screeningData[i][8]==null)
					{screeningData[i][8]="";}
					if(screeningData[i][9]==null)
					{screeningData[i][9]="";}
					if(screeningData[i][10]==null)
					{screeningData[i][10]="";}
					if(screeningData[i][11]==null)
					{screeningData[i][11]="";}
					entireHTML+="<tr> \n"+				
					"<td id = '"+screeningData[i][0]+"'>"+screeningData[i][0]+"</td> \n"+
					"<td>"+screeningData[i][1]+"</td> \n"+
					"<td>"+screeningData[i][2]+"</td> \n"+
					"<td>"+screeningData[i][3]+"</td> \n"+
					"<td>"+screeningData[i][4]+"</td> \n"+
					"<td>"+screeningData[i][5]+"</td> \n"+
					"<td><input id = 'suspt"+screeningData[i][0]+"' type='text' value='"+screeningData[i][6]+"'/></td> \n"+
					
					
					"<td>"+screeningData[i][7]+"</td> \n"+
					"<td>"+screeningData[i][8]+"</td> \n"+
					"<td>"+screeningData[i][9]+"</td> \n"+
					"<td>"+screeningData[i][10]+"</td> \n"+
					"<td>"+screeningData[i][11]+"</td> \n"+
					/*"<td>"+screeningData[i][12]+"</td> \n"+*/
					
					"<td> <button onclick=\"doSomething('"+screeningData[i][0]+"', 'suspt"+screeningData[i][0]+"');\">Update</button></td> \n" +
					
					"</tr> \n";
					
				}
				
				entireHTML+="</tbody>"+
				"</table>";
		
		return entireHTML;

		
	}

}
