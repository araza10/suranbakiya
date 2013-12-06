package org.irdresearch.tbreach2.server;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.irdresearch.tbreach2.shared.model.LabMapping;
import org.irdresearch.tbreach2.shared.model.Users;

/**
 * Servlet implementation class AdminServlet
 */
public class AdminServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AdminServlet() {
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
		String destadmin="/UserAdmin.jsp";
		String destadmin2="/UserManagement.jsp";
        RequestDispatcher dispatcher;
        HttpSession session=request.getSession();
        
        ServerServiceImpl ssl = new ServerServiceImpl();
        String ssn=request.getParameter("admin");
        String pass=request.getParameter("adminpw");
        Users user = null;
        String role = null;
        String status = null;
		if (ssl.authenticate (ssn, pass))
		{

			try
			{
				user = ssl.findUser (ssn);
				role = user.getRole();
				status = user.getStatus();
			}

				
                catch(Exception e){
                	e.printStackTrace ();
              
                }
			if(role.equalsIgnoreCase("admin")&&!(status.equalsIgnoreCase("suspend")) )
			{

				session.setAttribute("users", user);
                dispatcher=getServletContext().getRequestDispatcher(destadmin);
			}
			/*if(status.equalsIgnoreCase("suspend"))
			{
				request.setAttribute("Invalid", "Account Suspended!");
				dispatcher=getServletContext().getRequestDispatcher(destadmin2);
			}*/

			else
			{
				request.setAttribute("Invalid", "Only Admins Allowed or Account Suspended!");
				dispatcher=getServletContext().getRequestDispatcher(destadmin2);
			}
			
			
                dispatcher.forward(request,response);
                return;
		}
		
        else{
        	
        request.setAttribute("Invalid","Invalid Admin Username or Password!");
        														 
        dispatcher=getServletContext().getRequestDispatcher(destadmin2);
        dispatcher.forward(request, response);
        return;
        }

	}

}
