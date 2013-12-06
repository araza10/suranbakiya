package org.irdresearch.tbreach2.server;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.irdresearch.tbreach2.shared.model.Users;

/**
 * Servlet implementation class UpdateUserServlet
 */
public class UpdateUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	ServerServiceImpl ssl;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateUserServlet() {
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
		String upid = request.getParameter("upid");
		String upwd = request.getParameter("upwd");
		String ustatus = request.getParameter("ustatus");
		String adestination = "/UpdateUser.jsp";
		
		if(upid.isEmpty())
		{
			request.setAttribute("updateuserexception", "Please enter PID!");
            dispatcher=getServletContext().getRequestDispatcher(adestination);
            dispatcher.forward(request,response);
			return;
		}
		
		Users users = null;
		try
		{
			 users = ssl.findUser (upid);
			
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			
			
		}
		if(users == null)
		{
			request.setAttribute("updateuserexception", "This PID does not exist!");
            dispatcher=getServletContext().getRequestDispatcher(adestination);
            dispatcher.forward(request,response);
			return;
			
		}
		else
		{
			try
			{
			users.setPassword(upwd);
			users.setStatus(ustatus);
			ssl.updateUser(users);
			request.setAttribute("updateuserexception", "User Updated!");
            dispatcher=getServletContext().getRequestDispatcher(adestination);
            dispatcher.forward(request,response);
			return;
			}
			catch (Exception e) {
				e.printStackTrace();
				request.setAttribute("updateuserexception", "Error Occured. Could not update user. Please try again!");
	            dispatcher=getServletContext().getRequestDispatcher(adestination);
	            dispatcher.forward(request,response);
				return;
			}
		}

	}

}
