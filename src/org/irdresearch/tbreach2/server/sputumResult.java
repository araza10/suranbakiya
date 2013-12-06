package org.irdresearch.tbreach2.server;

import java.io.IOException;
import java.text.Collator;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.irdresearch.tbreach2.mobileevent.DateTimeUtil;
import org.irdresearch.tbreach2.mobileevent.EncounterType;
import org.irdresearch.tbreach2.mobileevent.ModelUtil;
import org.irdresearch.tbreach2.mobileevent.XmlUtil;
import org.irdresearch.tbreach2.shared.model.Encounter;
import org.irdresearch.tbreach2.shared.model.EncounterId;
import org.irdresearch.tbreach2.shared.model.EncounterResults;
import org.irdresearch.tbreach2.shared.model.Patient;
import org.irdresearch.tbreach2.shared.model.Screening;
import org.irdresearch.tbreach2.shared.model.SputumResults;

/**
 * Servlet implementation class sputumResult
 */
public class sputumResult extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public sputumResult() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String dest="/registrationBook.jsp";
		request.setCharacterEncoding("UTF-8");
        RequestDispatcher dispatcher;
        HttpSession session=request.getSession();
        String id = request.getParameter ("1");
		String chwId = request.getParameter ("wat").toUpperCase ();
		ServerServiceImpl ssl =new ServerServiceImpl();
		String labId = request.getParameter ("2");
		//String labBarcode = request.getParameter ("lbc");
		//String testType = request.getParameter ("test");
		String smearResult = request.getParameter ("sino");
		String smearResult1 = request.getParameter ("pino");
		String smearResult2 = request.getParameter ("tino");
		//String geneXpertResult = request.getParameter ("gxp");
		//String geneXpertResistance = request.getParameter ("gxpr");
		String facility = request.getParameter("3");
		String enteredDate = request.getParameter ("date1");
		String smearResult1Eng;
		String smearResult2Eng;
		String smearResult3Eng;
		Screening screen = null;
		Patient pat = null;
		SputumResults sr = null;
		
		try{
		screen = ssl.findScreeningByPatientID(id);
		}
		catch (Exception e) {
			request.setAttribute("Error", "Ин ракам вучуд надорад!");
			dispatcher=getServletContext().getRequestDispatcher(dest);
	        dispatcher.forward(request,response);
	        return;
		}
		
		if(screen !=null)
		{
			try{
			sr = ssl.findSputumResults(id);}
			catch (Exception e) {
				e.printStackTrace();
			}

		}
		
		if(sr!=null)
		{
			request.setAttribute("Error", "Натичаи балгами ин бемор ворид карда шудааст!");
			dispatcher=getServletContext().getRequestDispatcher(dest);
	        dispatcher.forward(request,response);
	        return;
		}
		

		
		
		
		boolean sx = areRussianStringsEqual(smearResult,"Манфй");
		boolean sx1 = areRussianStringsEqual(smearResult,"Пусиш");
		if(sx == true)
		{
			smearResult1Eng = "NEGATIVE";
		}
		
		else if(sx1 == true)
		{
			smearResult1Eng = "SCANTY";
		}
		else
		{
			smearResult1Eng = smearResult;
		}
		
		
		boolean sx2 = areRussianStringsEqual(smearResult1,"Манфй");
		boolean sx3 = areRussianStringsEqual(smearResult1,"Пусиш");
		
		if(sx2 == true)
		{
			smearResult2Eng = "NEGATIVE";
		}
		
		else if(sx3 == true)
		{
			smearResult2Eng = "SCANTY";
		}
		else
		{
			smearResult2Eng = smearResult1;
		}
		
		boolean sx4 = areRussianStringsEqual(smearResult2,"Манфй");
		boolean sx5 = areRussianStringsEqual(smearResult2,"Пусиш");
		
		if(sx4 == true)
		{
			smearResult3Eng = "NEGATIVE";
		}
		
		else if(sx5 == true)
		{
			smearResult3Eng = "SCANTY";
		}
		else
		{
			smearResult3Eng = smearResult2;
		}
		
		
		sr = new SputumResults();
		try {
			sr.setDateSmearTested(DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.BE_FORMAT));
			sr.setLabNumber(Integer.parseInt(labId));
			sr.setPatientId(id);
			sr.setTreatmentFacility(facility.toUpperCase());
			sr.setSmearResult(smearResult1Eng);
			sr.setSmearResult2(smearResult2Eng);
			sr.setSmearResult3(smearResult3Eng);
			ssl.saveSputumResults(sr);
			
		} catch (ParseException e2) {
			
			e2.printStackTrace();
			request.setAttribute("Error", "Таърихи тахлили балгамро ворид кунед");
			dispatcher=getServletContext().getRequestDispatcher(dest);
	        dispatcher.forward(request,response);
	        return;
		}
		
		EncounterId encId = new EncounterId ();
		encId.setPid1 (id.toUpperCase ());
		encId.setPid2 (chwId.toUpperCase ());

		Encounter e = new Encounter ();
		e.setId (encId);
		e.setEncounterType (EncounterType.SPUTUM_RESULTS);
		e.setDateEncounterStart (new Date());
		e.setDateEncounterEnd (new Date());
		e.setLocationId (labId);
		try
		{
			e.setDateEncounterEntered (DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.BE_FORMAT));
		}

		catch (Exception e1)
		{
			e1.printStackTrace ();
			request.setAttribute("Error", "Таърих нодуруст. Такрор кунед!");
			dispatcher=getServletContext().getRequestDispatcher(dest);
	        dispatcher.forward(request,response);
	        return;
		}

		try
		{
			ssl.saveEncounter (e);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			//return XmlUtil.createErrorXml ("Error occurred. Please try again");
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults> ();

		EncounterResults dateResult = ModelUtil.createEncounterResult (e, "entered_date".toUpperCase (), enteredDate);
		encounters.add (dateResult);
		
		EncounterResults labResult = ModelUtil.createEncounterResult (e, "lab_no".toUpperCase (), labId);
		encounters.add (labResult);
		
		EncounterResults chwResult = ModelUtil.createEncounterResult (e, "chw_id".toUpperCase (), chwId);
		encounters.add (chwResult);
		
		EncounterResults patResult = ModelUtil.createEncounterResult (e, "pat_id".toUpperCase (), id);
		encounters.add (patResult);
		
		EncounterResults facilityResult = ModelUtil.createEncounterResult (e, "facility_result".toUpperCase (), facility.toUpperCase());
		encounters.add (facilityResult);
		
		EncounterResults smearResultR = ModelUtil.createEncounterResult (e, "smear_result".toUpperCase (), smearResult1Eng.toUpperCase());
		encounters.add (smearResultR);
		
		EncounterResults smearResultResult = ModelUtil.createEncounterResult (e, "smear_result1".toUpperCase (), smearResult2Eng.toUpperCase ());
		encounters.add (smearResultResult);
		
		EncounterResults smearResultResult2 = ModelUtil.createEncounterResult (e, "smear_result2".toUpperCase (), smearResult3Eng.toUpperCase ());
		encounters.add (smearResultResult2);
		
		boolean resultSave = true;

		for (int i = 0; i < encounters.size (); i++)
		{
			try
			{
				resultSave = ssl.saveEncounterResults (encounters.get (i));
			}
			catch (Exception e1)
			{
				e1.printStackTrace ();
				break;
			}

			if (!resultSave)
			{
				request.setAttribute("Error", "Хато шудааст. Такрор кунед!");
				dispatcher=getServletContext().getRequestDispatcher(dest);
		        dispatcher.forward(request,response);
		        return;
			}
		}
		
		request.setAttribute("id",id);
		request.setAttribute("labid", labId);
		request.setAttribute("facility", facility);
		request.setAttribute("enteredDate", enteredDate);
		request.setAttribute("smearResult", smearResult1Eng);
		request.setAttribute("smearResult1", smearResult2Eng);
		request.setAttribute("smearResult2", smearResult3Eng);
		request.setAttribute("facilityLength", facility.length());
		dispatcher=getServletContext().getRequestDispatcher(dest);
        dispatcher.forward(request,response);
        return;


	}
	
	public static boolean areRussianStringsEqual(String s1, String s2) {
		boolean result = false;
		if(s1==null || s2==null)
		return false;
		if(s1.length()==0 || s2.length()==0)
		return false;
		Collator collator = Collator.getInstance();
		collator.setStrength(Collator.SECONDARY);
		int compResult = collator.compare(s1, s2);
		if(compResult==0)
			return true;
		
		return result;
		}

}
