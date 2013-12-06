package org.irdresearch.tbreach2.server;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.irdresearch.tbreach2.shared.model.LabMapping;
import org.irdresearch.tbreach2.shared.model.Screening;

/**
 * Servlet implementation class UpdateScreeningServlet
 */
public class UpdateScreeningServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Screening s  = null;
	ServerServiceImpl ssl = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateScreeningServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String screeningID 	= 	request.getParameter("screeningID");
		String suspect 		= 	request.getParameter("suspect");
		ssl = new ServerServiceImpl ();
		String labmappingCHWID = null;
		LabMapping lmpping = null;
		long locationCurrentCount;
		String uniqueID = null;
		RequestDispatcher dispatcher;
		String date= request.getParameter("date");
		String destination ="/ScreeningWeb.jsp";
		
		try{
		s = ssl.findScreeningByScreeningID(screeningID);
		if(s == null)
		{
			//return patient not found, which is not possible
		}
		else//Caution, don't make a real non suspect a suspect
		{//People who are wrongly registered as non suspects but are suspects
			if((s.getTbhistory().equalsIgnoreCase("yes") || s.getFamilyTbhistory().equalsIgnoreCase("yes") || (s.getCough().equalsIgnoreCase("yes") && 
					s.getCoughDuration().equalsIgnoreCase("more than 3 weeks")) || (s.getCough().equalsIgnoreCase("yes") 
					&& s.getCoughDuration().equalsIgnoreCase("2-3 weeks") && s.getProductiveCough().equalsIgnoreCase("yes")) || (s.getCough().equalsIgnoreCase("yes") &&
							s.getCoughDuration().equalsIgnoreCase("don't know"))) && s.getSuspect().equals(false))
			{
				s.setSuspect(true);
				labmappingCHWID = s.getChwid();
				lmpping = ssl.findMappingByPerson(labmappingCHWID);
				String locationID = lmpping.getId().getLocationId();
				Boolean b = ssl.exists ("screening", "substring(PatientID,1,2)='"+locationID+"'");
				if(b==false)
				{
					locationCurrentCount=ssl.locationCount("screening", locationID);
					locationCurrentCount++;
					String formattedString =  String.format("%06d", locationCurrentCount);
					uniqueID = locationID+""+formattedString;
					
				}
				else
				{
				locationCurrentCount = ssl.countMaxPerLocation("screening",locationID);
				locationCurrentCount++;
				String formattedString =  String.format("%06d", locationCurrentCount);
				uniqueID = locationID+""+formattedString;
				
				//System.out.println(date+ " Date Printing");
				s.setPatientId(uniqueID);
				ssl.update(s);
				
				response.sendRedirect(request.getContextPath()+destination+"?date="+date);
				
				/*dispatcher=getServletContext().getRequestDispatcher(destination);
	            dispatcher.forward(request,response);
			*/	return;
				}
			}
			//Don't make a real suspect a non suspect, scenario: yes less than 2 weeks 
			//Who should be non suspects but make sure this condition does not fulfill any condition which will update a correct entry/ patient id
			else if(((s.getCough().equalsIgnoreCase("no") && s.getTbhistory().equalsIgnoreCase("no") && s.getFamilyTbhistory().equalsIgnoreCase("no"))
					|| (s.getCough().equalsIgnoreCase("yes") && s.getCoughDuration().equalsIgnoreCase("less than 2 weeks") &&
							s.getTbhistory().equalsIgnoreCase("no") && s.getFamilyTbhistory().equalsIgnoreCase("no")) ||
					(s.getCough().equalsIgnoreCase("yes") && s.getCoughDuration().equalsIgnoreCase("2-3 weeks") && s.getProductiveCough().equalsIgnoreCase("no")) &&
					s.getTbhistory().equalsIgnoreCase("no") && s.getFamilyTbhistory().equalsIgnoreCase("no"))
					&& (s.getSuspect().equals(true)))
			{
								
				s.setSuspect(false);
				s.setPatientId(null);
				ssl.update(s);
				response.sendRedirect(request.getContextPath()+destination+"?date="+date);
				
				return;
				
			}
			
			else
			{
				response.sendRedirect(request.getContextPath()+destination+"?date="+date+"&ErrorMes="+"This patient is correctly entered! No need to update!");
				
				return;
			}
		}
		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println(screeningID + " " + suspect);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
	}

}
