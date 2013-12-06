package org.irdresearch.tbreach2.server;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class StatusServlet
 */
public class StatusServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StatusServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("deprecation")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher;
		ServerServiceImpl ssl = new ServerServiceImpl ();
		String forwardurl = "/SystemStatus.jsp";
		String totalScreened = null;
		String totalSuspects = null;
		String totalNonSuspect = null;
		Date d = null;
		String completeTimeIn=null;
		String completeTime = null;
		String formattedDate = null;
		String locationType = request.getParameter("locationType");
		String polyLocationType = null;
		String polyLocationSuspects = null;
		String polyLocationNonSuspects = null;
		System.out.println("Printing Drop Down Value in Servlet "+locationType);
		/*String screenspercenter = null;
		String suspectspercenter = null;*/
		String CENTRALPOLYCLINIC= null;
		String POLYDUSHANBE2 = null;
		String POLYDUSHANBE3 = null;
		String POLYDUSHANBE4 = null;
		String POLYDUSHANBE5 = null;
		String POLYDUSHANBE6 = null;
		String POLYDUSHANBE7 = null;
		String POLYDUSHANBE8 = null;
		String POLYDUSHANBE9 = null;
		String POLYDUSHANBE10 = null;
		String POLYDUSHANBE11 = null;
		String POLYDUSHANBE12 = null;
		String POLYDUSHANBE14 = null;
		String POLYTURSUNZADE1 = null;
		String POLYTURSUNZADE2 = null;
		String DIABETESDUSHANBE1 = null;
		String DIABETESDUSHANBE2 = null;
		String PRISONSYSTEM = null;
		String RUDAKI = null;
		String POLYDUSHANBE1 = null;
		String wrongEntries = null;
		int totalCorrectSuspectEntries= 0;
		Integer numberofworkingdayssinceStart = 0;
		Integer screensperdayperCenter = 0;
		Integer screensperdayperOverall = 0;
		double percentofsuspectsTotalScreened = 0.0;
		double a=0;
		double b=0;
		String percentage = null;
		try{
		d = new Date();
		DateFormat df = new SimpleDateFormat("dd/MM/yy");
		formattedDate = df.format(new Date()); 
		
		completeTimeIn = d.getHours()+":"+d.getMinutes()+":"+d.getSeconds();
		//completeTime =  Integer.toString(completeTimeIn);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//get Screened Suspect Count
		try {
			totalScreened = String.valueOf(ssl.count("screening", ""));
			totalSuspects = String.valueOf(ssl.count("screening", "Suspect = true"));
			totalNonSuspect = String.valueOf(ssl.count("screening", "Suspect = false"));
			wrongEntries = String.valueOf(ssl.count("screening", "cough='NO' and tbhistory='NO' and familyTbhistory='NO' and Suspect=true"));
			totalCorrectSuspectEntries = Integer.valueOf(totalSuspects)-Integer.valueOf(wrongEntries);
			numberofworkingdayssinceStart = Integer.valueOf(String.valueOf(ssl.workingDays()));
			screensperdayperOverall = Integer.valueOf(totalScreened)/numberofworkingdayssinceStart;
			screensperdayperCenter = screensperdayperOverall/20;
			a = (double)(totalCorrectSuspectEntries);
			b = (double)Double.parseDouble(totalScreened);
			percentofsuspectsTotalScreened = (a/b)*100;
			percentage = String.format("%.2f", percentofsuspectsTotalScreened);
			/*screenspercenter = String.valueOf(Integer.parseInt(totalScreened)/20);
			suspectspercenter = String.valueOf(Integer.parseInt(totalSuspects)/20);*/
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		//get by each location
		try
		{
			CENTRALPOLYCLINIC = String.valueOf(ssl.count("screening", "ScreenLocation = 01"));
			POLYDUSHANBE2 = String.valueOf(ssl.count("screening", "ScreenLocation = 02"));
			POLYDUSHANBE3 = String.valueOf(ssl.count("screening", "ScreenLocation = 03"));
			POLYDUSHANBE4 = String.valueOf(ssl.count("screening", "ScreenLocation = 04"));
			POLYDUSHANBE5 = String.valueOf(ssl.count("screening", "ScreenLocation = 05"));
			POLYDUSHANBE6 = String.valueOf(ssl.count("screening", "ScreenLocation = 06"));
			POLYDUSHANBE7 = String.valueOf(ssl.count("screening", "ScreenLocation = 07"));
			POLYDUSHANBE8 = String.valueOf(ssl.count("screening", "ScreenLocation = 08"));
			POLYDUSHANBE9 = String.valueOf(ssl.count("screening", "ScreenLocation = 09"));
			POLYDUSHANBE10 = String.valueOf(ssl.count("screening", "ScreenLocation = 10"));
			POLYDUSHANBE11 = String.valueOf(ssl.count("screening", "ScreenLocation = 11"));
			POLYDUSHANBE12 = String.valueOf(ssl.count("screening", "ScreenLocation = 12"));
			POLYDUSHANBE14 = String.valueOf(ssl.count("screening", "ScreenLocation = 14"));
			POLYTURSUNZADE1 = String.valueOf(ssl.count("screening", "ScreenLocation = 15"));
			POLYTURSUNZADE2 = String.valueOf(ssl.count("screening", "ScreenLocation = 16"));
			DIABETESDUSHANBE1 = String.valueOf(ssl.count("screening", "ScreenLocation = 17"));
			DIABETESDUSHANBE2 = String.valueOf(ssl.count("screening", "ScreenLocation = 18"));
			PRISONSYSTEM = String.valueOf(ssl.count("screening", "ScreenLocation = 19"));
			RUDAKI = String.valueOf(ssl.count("screening", "ScreenLocation = 20"));
			POLYDUSHANBE1 = String.valueOf(ssl.count("screening", "ScreenLocation = 21"));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
		//get by Location Type
		try{
			if(locationType!=null){
			if(locationType.equalsIgnoreCase("All PolyClinics"))
					{
				polyLocationType = String.valueOf(ssl.count("screening, location", "screening.ScreenLocation = location.LocationID and location.LocationName like '%POLY%'"));
				polyLocationSuspects = String.valueOf(ssl.count("screening, location", "screening.ScreenLocation = location.LocationID and " +
						"location.LocationName like '%POLY%' and screening.Suspect=true"));
				polyLocationNonSuspects = String.valueOf(ssl.count("screening, location", "screening.ScreenLocation = location.LocationID and " +
						"location.LocationName like '%POLY%' and screening.Suspect=false"));

					}
			
			else if(locationType.equalsIgnoreCase("All Diabetes Centers"))
			{
		polyLocationType = String.valueOf(ssl.count("screening, location", "screening.ScreenLocation = location.LocationID and location.LocationName like '%DIABET%'"));
		polyLocationSuspects = String.valueOf(ssl.count("screening, location", "screening.ScreenLocation = location.LocationID and " +
				"location.LocationName like '%DIABET%' and screening.Suspect=true"));
		polyLocationNonSuspects = String.valueOf(ssl.count("screening, location", "screening.ScreenLocation = location.LocationID and " +
				"location.LocationName like '%DIABET%' and screening.Suspect=false"));

			}
			
			else if(locationType.equalsIgnoreCase("Prison Centers"))
			{
		polyLocationType = String.valueOf(ssl.count("screening, location", "screening.ScreenLocation = location.LocationID and location.LocationName like '%PRIS%'"));
		polyLocationSuspects = String.valueOf(ssl.count("screening, location", "screening.ScreenLocation = location.LocationID and " +
				"location.LocationName like '%PRIS%' and screening.Suspect=true"));
		polyLocationNonSuspects = String.valueOf(ssl.count("screening, location", "screening.ScreenLocation = location.LocationID and " +
				"location.LocationName like '%PRIS%' and screening.Suspect=false"));

			}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//setting attributes
		request.setAttribute("attributedate", formattedDate);
		request.setAttribute("attributetime", completeTimeIn);
		request.setAttribute("ScreeningTotal", totalScreened);
		request.setAttribute("SuspectTotal", totalSuspects);
		request.setAttribute("NonSuspectTotal", totalNonSuspect);
		request.setAttribute("allpoly", polyLocationType);
		request.setAttribute("allpolySuspects", polyLocationSuspects);
		request.setAttribute("allpolyNonSuspects", polyLocationNonSuspects);
		request.setAttribute("LocationName", locationType);
		
		//setting each location attribute in request
		request.setAttribute("CENTRALPOLYCLINIC", CENTRALPOLYCLINIC);
		request.setAttribute("POLYDUSHANBE2", POLYDUSHANBE2);
		request.setAttribute("POLYDUSHANBE3", POLYDUSHANBE3);
		request.setAttribute("POLYDUSHANBE4", POLYDUSHANBE4);
		request.setAttribute("POLYDUSHANBE5", POLYDUSHANBE5);
		request.setAttribute("POLYDUSHANBE6", POLYDUSHANBE6);
		request.setAttribute("POLYDUSHANBE7", POLYDUSHANBE7);
		request.setAttribute("POLYDUSHANBE8", POLYDUSHANBE8);
		request.setAttribute("POLYDUSHANBE9", POLYDUSHANBE9);
		request.setAttribute("POLYDUSHANBE10", POLYDUSHANBE10);
		request.setAttribute("POLYDUSHANBE11", POLYDUSHANBE11);
		request.setAttribute("POLYDUSHANBE12", POLYDUSHANBE12);
		request.setAttribute("POLYDUSHANBE14", POLYDUSHANBE14);
		request.setAttribute("POLYTURSUNZADE1", POLYTURSUNZADE1);
		request.setAttribute("POLYTURSUNZADE2", POLYTURSUNZADE2);
		request.setAttribute("DIABETESDUSHANBE1", DIABETESDUSHANBE1);
		request.setAttribute("DIABETESDUSHANBE2", DIABETESDUSHANBE2);
		request.setAttribute("PRISONSYSTEM", PRISONSYSTEM);
		request.setAttribute("RUDAKI", RUDAKI);
		request.setAttribute("POLYDUSHANBE1", POLYDUSHANBE1);
		request.setAttribute("WrongEntries", wrongEntries);
		request.setAttribute("totalCorrectSuspectEntries", totalCorrectSuspectEntries);
		request.setAttribute("workingDays", numberofworkingdayssinceStart);
		request.setAttribute("screensperdayperoverall", screensperdayperOverall);
		request.setAttribute("screensperdaypercenter", screensperdayperCenter);
		request.setAttribute("percentage", percentage);
		
		
		
		dispatcher=getServletContext().getRequestDispatcher(forwardurl);
	    dispatcher.forward(request, response);
	    return;
		
	}

}
