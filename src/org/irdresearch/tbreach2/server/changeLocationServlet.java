package org.irdresearch.tbreach2.server;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.irdresearch.tbreach2.mobileevent.XmlUtil;
import org.irdresearch.tbreach2.shared.model.LabMapping;
import org.irdresearch.tbreach2.shared.model.LabMappingId;
import org.irdresearch.tbreach2.shared.model.Location;
import org.irdresearch.tbreach2.shared.model.Users;

/**
 * Servlet implementation class changeLocationServlet
 */
public class changeLocationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	ServerServiceImpl ssl;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public changeLocationServlet() {
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
        HttpSession session=request.getSession();
        String dest="/changeLocation.jsp";
		String username = request.getParameter("uw");
		String currLocation = request.getParameter("currentLocation");
		String newLocation = request.getParameter("newLocation");
		String pid;
		String role;
		ssl = new ServerServiceImpl ();
		
		try
		{
			Users users = ssl.findUser (username);
			pid = users.getPid ();
			role = users.getRole ();
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			session.setAttribute("usersattribute", "Cant find this user. Please try again!");
            dispatcher=getServletContext().getRequestDispatcher(dest);
            dispatcher.forward(request,response);
            return;
			
		}
		//find id for current location
		
		Location locationcurre =ssl.findLocationIDByName(currLocation);
		String locationIDCurr = locationcurre.getLocationId();
		
				
		//update current user in Labmapping to reflect blank/empty
		try{
		LabMapping lmapp = ssl.findMappingByPerson (pid, locationIDCurr);
		
		
		ssl.delete(lmapp);
		}
		catch (Exception e) {
		
			e.printStackTrace();
			session.setAttribute("usersattribute", "Current Location and User ID do not match!");
			dispatcher=getServletContext().getRequestDispatcher(dest);
            dispatcher.forward(request,response);
            return;
		}
		
		
		//find id for new location
		Location location = ssl.findLocationIDByName(newLocation);
		String newLocationName =  location.getLocationName();
		String locationID = location.getLocationId();
		
		
		//updating labmapping table by changing this user's current location to a new location
		LabMapping mapp = new LabMapping();
		LabMappingId lmi = new LabMappingId(locationID, pid);
		
		mapp.setId(lmi);
		mapp.setPidrole("LabTech");
		
		try{
			
				ssl.saveLabMapping(mapp);
				session.setAttribute("usersattribute", "Updated Successfully!");
	            dispatcher=getServletContext().getRequestDispatcher(dest);
	            dispatcher.forward(request,response);
	            return;
				
			}
		catch (Exception e) {
			
			e.printStackTrace();
			
		}
		
	}

}
