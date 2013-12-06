package org.irdresearch.tbreach2.server;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.irdresearch.tbreach2.shared.model.LabMapping;
import org.irdresearch.tbreach2.shared.model.LabMappingId;
import org.irdresearch.tbreach2.shared.model.Location;
import org.irdresearch.tbreach2.shared.model.Users;

/**
 * Servlet implementation class AddUserServlet
 */
public class AddUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	ServerServiceImpl ssl;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddUserServlet() {
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher dispatcher;
		ssl = new ServerServiceImpl ();
		String apid = request.getParameter("apid");
		String afirstName = request.getParameter("afname");
		String alastName = request.getParameter("alname");
		String acurrentLocation = request.getParameter("acurrentLocation");
		String arole = request.getParameter("arole");
		String adestination = "/AddUser.jsp";
		
		if(apid.isEmpty())
		{
			request.setAttribute("adduserexception", "Please enter PID!");
            dispatcher=getServletContext().getRequestDispatcher(adestination);
            dispatcher.forward(request,response);
			return;
		}
		
		Users users = null;
		try
		{
			 users = ssl.findUser (apid);
			
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			
			
		}
		if(users != null)
		{
			request.setAttribute("adduserexception", "This PID already exists. Please enter a different PID!");
            dispatcher=getServletContext().getRequestDispatcher(adestination);
            dispatcher.forward(request,response);
			return;
			
		}
		else
		{
			// adding a user
			try{
			users = new Users();
			users.setFirstName(afirstName.toUpperCase());
			users.setLastName(alastName.toUpperCase());
			users.setUserName(apid);
			users.setPid(apid);
			users.setStatus("ACTIVE");
			users.setPassword(apid);
			users.setRole(arole);
			ssl.saveUser(users);
			
			//finding id for new location
			Location location = ssl.findLocationIDByName(acurrentLocation);
			String newLocationName =  location.getLocationName();
			String locationID = location.getLocationId();
			
			LabMapping mapp = new LabMapping();
			LabMappingId lmi = new LabMappingId(locationID, apid);
			
			mapp.setId(lmi);
			mapp.setPidrole("LabTech");
			ssl.saveLabMapping(mapp);
			request.setAttribute("adduserexception", "User Saved!");
            dispatcher=getServletContext().getRequestDispatcher(adestination);
            dispatcher.forward(request,response);
			return;
			
			}
			catch (Exception e) {
				e.printStackTrace();
				request.setAttribute("adduserexception", "Error Occured. Could not save user. Please try again!");
	            dispatcher=getServletContext().getRequestDispatcher(adestination);
	            dispatcher.forward(request,response);
				return;
				
			}
			
		}
		
	}

}
