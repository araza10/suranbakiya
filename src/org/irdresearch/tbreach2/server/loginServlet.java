package org.irdresearch.tbreach2.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.irdresearch.tbreach2.shared.model.LabMapping;
import org.irdresearch.tbreach2.shared.model.Users;
/*import org.irdresearch.tbreach2.util.*;*/

/**
 * Servlet implementation class loginServlet
 */
public class loginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public loginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String dest="/registrationBook.jsp";
		String dest2="/EnterPID.jsp";
		RequestDispatcher dispatcher;
        HttpSession session=request.getSession();
        ServerServiceImpl ssl = new ServerServiceImpl();
         String ssn=request.getParameter("hw");
         String pass=request.getParameter("pw");
         String userid = null;
         String role = null;
         Users users = null;
         String status = null;
         String currelocationid = null;
        
                
    		if (ssl.authenticate (ssn, pass))
    		{

    			try
    			{
    				users = ssl.findUser (ssn);
    				userid = users.getPid();
    				role = users.getRole();
    				status = users.getStatus();
    					
    				LabMapping lmp = ssl.findMappingByPerson(userid);
    				currelocationid = lmp.getId().getLocationId();
    				
    			}

    				
                    catch(Exception e){
                    	e.printStackTrace ();
                  
                    }
    			if(status.equalsIgnoreCase("suspend"))
    			{
    				request.setAttribute("Invalid", "Account Suspended");
    				dispatcher=getServletContext().getRequestDispatcher(dest2);
    				dispatcher.forward(request,response);
                    return;
    			}

    				session.setAttribute("users", users);
                    session.setAttribute("currentuser", currelocationid);
                    dispatcher=getServletContext().getRequestDispatcher(dest);
    			
    			
                    dispatcher.forward(request,response);
                    return;
    		}
            else{
            	
            request.setAttribute("Invalid","Invalid Username or Password!");
            														 
            dispatcher=getServletContext().getRequestDispatcher(dest2);
            dispatcher.forward(request, response);
            return;
            }
            
            
    			
    		}

	}


