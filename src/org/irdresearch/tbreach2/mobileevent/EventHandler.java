
package org.irdresearch.tbreach2.mobileevent;

import java.math.BigInteger;
import java.text.Collator;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.swing.text.DateFormatter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.irdresearch.tbreach2.server.HibernateUtil;
import org.irdresearch.tbreach2.server.ServerServiceImpl;
import org.irdresearch.tbreach2.shared.TBR;
import org.irdresearch.tbreach2.shared.model.Contact;
import org.irdresearch.tbreach2.shared.model.DrugHistory;
import org.irdresearch.tbreach2.shared.model.DrugHistoryId;
import org.irdresearch.tbreach2.shared.model.Encounter;
import org.irdresearch.tbreach2.shared.model.EncounterId;
import org.irdresearch.tbreach2.shared.model.EncounterResults;
import org.irdresearch.tbreach2.shared.model.EncounterResultsId;
import org.irdresearch.tbreach2.shared.model.Gp;
import org.irdresearch.tbreach2.shared.model.LabMapping;
import org.irdresearch.tbreach2.shared.model.LabMappingId;
import org.irdresearch.tbreach2.shared.model.Location;
import org.irdresearch.tbreach2.shared.model.TreatmentRefusal;
import org.irdresearch.tbreach2.shared.model.XrayResults;
import org.irdresearch.tbreach2.shared.model.Patient;
import org.irdresearch.tbreach2.shared.model.Person;
import org.irdresearch.tbreach2.shared.model.Screening;
import org.irdresearch.tbreach2.shared.model.SputumResults;
import org.irdresearch.tbreach2.shared.model.Users;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import com.mysql.jdbc.StringUtils;
import org.irdresearch.tbreach2.shared.model.GeneXpertResults;

public class EventHandler
{
	private static final int	LAB_ID_LN	= 2;
	private HttpServletRequest	request;
	private ServerServiceImpl	ssl;
	private Date				encounterStartDate;
	private Date				encounterEndDate;

	public EventHandler ()
	{
		ssl = new ServerServiceImpl ();
	}

	public void setRequest (HttpServletRequest request)
	{
		this.request = request;
	}

	public HttpServletRequest getRequest ()
	{
		return request;
	}

	public String handleEvent ()
	{

		// String username = request.getParameter("un");

		/*
		 * TODO uncomment this line else security blunder if
		 * (!UserAuthentication.userExsists(username)) { return XmlUtil
		 * .createErrorXml("Authentcation Failure. Please try again!"); }
		 */

		String xmlResponse = null;

/*	try
		{
		int version = 0;
			if(request.getParameter("appver")!=null)
			{
			version = Integer.parseInt (request.getParameter ("appver"));
			if (version != Tbr2Version.VERSION)
			{
				return XmlUtil.createErrorXml ("Version Error: The version you are currently using (" + version + ") is old. Press download New version (" + Tbr2Version.VERSION
						+ ")");
			}
			}

		}
		catch (Exception e)
		{
			return XmlUtil.createErrorXml ("Error occurred while finding version from request.");
		}*/

		String reqType = request.getParameter ("type");
		System.out.println ("----->" + reqType);

		if (reqType.equals (RequestType.LOGIN))
		{
			return doLogin ();
		}
		else if (reqType.equals (RequestType.FORM_QUERY))
		{
			return handleFormQuery ();
		}
		else if (reqType.equals (RequestType.LAB_QUERY))
		{
			return handleLabFormQuery ();
		}
		else if (reqType.equals (RequestType.REPORT_QUERY))
		{
			return handleReportFormQuery ();
		}
		else if (reqType.equals (RequestType.QUERY_INIT_DATA))
		{
			return handleInitDataQuery ();
		}
		else
		{
			String chwId = request.getParameter ("chwid");
/*			if (chwId != null)
			{
				Boolean uCheck = null;
				try
				{
					uCheck = ssl.exists ("users", "where PID = '" + chwId.toUpperCase () + "'");
				}
				catch (Exception e)
				{
					e.printStackTrace ();
				}

				if (uCheck == null || !uCheck.booleanValue ())
				{
					return XmlUtil.createErrorXml ("Aap ne ghalat CHW ID darj kia hai. Sahih ID enter karain aur dobara koshish karain");
				}
			}*/
			String labId = request.getParameter ("loc");
/*			if (!StringUtils.isEmptyOrWhitespaceOnly (labId))
			{
				Location labloc = null;
				try
				{
					labloc = ssl.findLocation (labId);
				}
				catch (Exception e4)
				{
					e4.printStackTrace ();
				}

				if (labloc == null)
				{
					return XmlUtil.createErrorXml ("Lab with id " + labId + " does not exist. Please enter a valid Lab ID and try again.");
				}
			}*/
			
			String startDate = request.getParameter ("sd");
			String startTime = request.getParameter ("st");
			String endTime = request.getParameter ("et");
			
			try
			{
				if(!reqType.equals(RequestType.GXP_AUTO))
				{
				encounterStartDate = DateTimeUtil.getDateFromString (startDate + " " + startTime, DateTimeUtil.FE_FORMAT);
				encounterEndDate = DateTimeUtil.getDateFromString (startDate + " " + endTime, DateTimeUtil.FE_FORMAT);
				}

			}
			catch (ParseException e2)
			{
				return XmlUtil.createErrorXml ("Invalid Date Format. Please contact technical support!");
			}
			
			
			
			if (reqType.equals (RequestType.SUSPECT_ID))
			{
				return doSuspectIdentification ();
			}
			else if (reqType.equals (RequestType.NON_SUSPECT_ID))
			{
				return doLogNonSuspectScreening ();
			}
			else if (reqType.equals (RequestType.PATIENT_INFO))
			{
				return doPatientInfo ();
			}
			else if (reqType.equals (RequestType.PATIENT_INFO_OLD))
			{
				return doPatientOldInfo ();
			}
			else if (reqType.equals (RequestType.CONTACT_INFO))
			{
				return doContactInfo ();
			}
			else if (reqType.equals (RequestType.GENEXPERT_ORDER))
			{
				return doGeneXpertOrder ();
			}
			else if (reqType.equals (RequestType.SPUTUM_BARCODE_LINKAGE))
			{
				return doSputumBarcodeLink ();
			}
			else if (reqType.equals (RequestType.PATIENT_GPS_INFO))
			{
				return doPatientGPSInfo ();
			}
			else if (reqType.equals (RequestType.SPUTUM_COLLECTION))
			{
				return doSputumCollection ();
			}
			else if (reqType.equals (RequestType.BASELINE_TX))
			{
				return doBaselineTreatment ();
			}
			else if (reqType.equals (RequestType.FOLLOWUP_TX))
			{
				return doFollowupTreatment ();
			}
			else if (reqType.equals (RequestType.DRUG_ADMIN))
			{
				return doDrugAdm ();
			}
			else if (reqType.equals (RequestType.END_FOLLOWUP))
			{
				return doEndFollowup ();
			}
			else if (reqType.equals (RequestType.REFUSAL))
			{
				return doRefusal ();
			}
			else if (reqType.equals (RequestType.GP_REGISTRATION))
			{
				return doGPRegistration ();
			}
			else if (reqType.equals (RequestType.GP_FOLLOWUP))
			{
				return doGPFollowup ();
			}
			else if (reqType.equals (RequestType.XRAY_RECV))
			{
				return doXrayReceiving ();
			}
			else if (reqType.equals (RequestType.XRAY_RESULTS))
			{
				return doXrayResults ();
			}
			else if (reqType.equals (RequestType.SPUTUM_RESULTS))
			{
				return doSputumResults ();
			}
			else if (reqType.equals (RequestType.SCR_LOG))
			{
				return doScreeningLog ();
			}
			else if (reqType.equals (RequestType.CDF))
			{
				return doCDF ();
			}
			else if(reqType.equals (RequestType.INDUS_MRN_REQUEST))
			{
				return doIndusMRN();
			}
			else if(reqType.equals (RequestType.ADVERTISING_GPS_REQUEST))
			{
				return doAdvertisingGPS();
			}
			else if (reqType.equals (RequestType.GXP_AUTO))
			{
				return doRemoteASTMResult ();
			}
			
		}
		return xmlResponse;
	}

	private String handleInitDataQuery ()
	{
		String xml = null;

		String requestType = request.getParameter ("qtype");
		System.out.println (requestType);

		if (requestType.equals (RequestType.QUERY_GP_REGISTRATION))
		{
			xml = getGPRegistrationInfo ();
		}
		
		else if (requestType.equals (RequestType.QUERY_ADVERTISING_GPS))
		{
			xml = getGPRegistrationInfo ();
		}
		return xml;
	}

	private String handleFormQuery ()
	{
		String xml = null;

		String requestType = request.getParameter ("qtype");
		System.out.println (requestType);
		String id = request.getParameter ("pid");
		System.out.println (id);

		if (requestType.equals (RequestType.QUERY_SPUTUM_COLLECTION))
		{
			xml = getSputumCollectInfo (id);
		}
		else if (requestType.equals (RequestType.QUERY_BASELINE_TX))
		{
			xml = getBaselineTreatmentInfo (id);
		}
		else if (requestType.equals (RequestType.QUERY_FOLLOWUP_TX))
		{
			xml = getFollowupTreatmentInfo (id);
		}
		else if (requestType.equals (RequestType.QUERY_DRUG_ADMIN))
		{
			xml = getDrugAdmInfo (id);
		}
		else if (requestType.equals (RequestType.QUERY_END_FOL))
		{
			xml = getFollowupTreatmentInfo (id);
		}
		else if (requestType.equals (RequestType.QUERY_SEARCH))
		{
			xml = getSearchInfo (id);
		}
		else if (requestType.equals (RequestType.QUERY_SPUTUM_SEARCH))
		{
			xml = getSputumSearchInfo ();
		}
		else if (requestType.equals (RequestType.QUERY_LAB))
		{
			xml = getLabResultsData (id);
		}
		return xml;
	}

	private String handleLabFormQuery ()
	{
		String xml = null;

		String requestType = request.getParameter ("qtype");
		String labid = request.getParameter ("labId");
		String labbarcode = request.getParameter ("lbc");

		if (requestType.equals (RequestType.QUERY_GENEXPERT))
		{
			xml = getGeneXpertInfo (labid, labbarcode);
		}
		else if (requestType.equals (RequestType.QUERY_SPUTUM_RESULTS))
		{
			xml = getSputumResultsInfo (labid, labbarcode);
		}
		else if (requestType.equals (RequestType.QUERY_XRAY_RESULTS))
		{
			xml = getXrayResultsInfo (labid, labbarcode);
		}
		return xml;
	}

	private String handleReportFormQuery ()
	{
		String xml = null;

		String requestType = request.getParameter ("qtype");

		if (requestType.equals (RequestType.REPORT_MISSING_SMEAR))
		{
			xml = getMissingSmearReport ();
		}
		else if (requestType.equals (RequestType.REPORT_MISSING_CXR_RESULT))
		{
			xml = getMissingCXRReport ();
		}
		else if (requestType.equals (RequestType.REPORT_MISSING_GXP_RESULT))
		{
			xml = getMissingXpertResultsReport ();
		}
		else if (requestType.equals (RequestType.REPORT_MISSING_GXP_ORDER))
		{
			xml = getMissingXpertOrderReport();
		}
		else if (requestType.equals (RequestType.REPORT_MISSING_CXR_RECIEVING))
		{
			xml = getMissingCXRRecvReport ();
		}
		else if (requestType.equals (RequestType.REPORT_TX_NOT_STARTED))
		{
			xml = getTxNotStartedReport ();
		}
		else if (requestType.equals (RequestType.REPORT_PATIENT_DRUGS_DUE_DATE))
		{
			xml = getPatientDrugsDueDateReport ();
		}
		else if (requestType.equals (RequestType.REPORT_PENDING_FOLLOWUP_MONTH2))
		{
			xml = getPendingFollowup2Report ();
		}
		else if (requestType.equals (RequestType.REPORT_PENDING_FOLLOWUP_MONTH3))
		{
			xml = getPendingFollowup3Report ();
		}
		else if (requestType.equals (RequestType.REPORT_MISSING_GPS_LOCATION))
		{
			xml = getMissingGPSLocationReport ();
		}
		return xml;
	}
	
	private String doIndusMRN(){
		
		String chwID=request.getParameter("healthWorkerID");
		
		String patientID=request.getParameter("pid");
		
		String indusMRN=request.getParameter("indusMRN");
		String dateField=request.getParameter("dateField");
		String district=request.getParameter("district");
		String sd= request.getParameter("sd");
		String st= request.getParameter("st");
		String et= request.getParameter("et");
		String dreg= request.getParameter("dreg");
		
		Patient pat = null;

		try
		{
			pat = ssl.findPatient (patientID);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
		if (pat == null)
		{
			return XmlUtil.createErrorXml ("Patient with id " + patientID + " does not exist. Please recheck ID and try again.");
		}
		
	
		Boolean exists = null;

		try
		{
			exists = ssl.exists ("encounter", " where PID1='" + patientID + "'" + " AND EncounterType='" + EncounterType.INDUS_MRN + "'");
		}
		catch (Exception e3)
		{
			e3.printStackTrace ();
		}

		
		if (exists)
		{
			return XmlUtil.createErrorXml ("Patient " + patientID
					+ " ka MR Number Assign Kur Dia Gaya Hai");
		}
		
		if(pat.getPatientId()!=null && pat.getMrno() != null)//Assigned
		{
			return XmlUtil.createErrorXml("Patient " + patientID + "ka MR Number Assign Kur Dia Gaya Hai");
		}
		
		Boolean isMRNAssigned=null;
		
		try{
			
			isMRNAssigned=ssl.exists("patient", " where MRNo='" +indusMRN + "'");
			
		}
		
		catch(Exception e3){
		
			e3.printStackTrace ();
		}
		if (isMRNAssigned == null)
		{
			System.out.println(indusMRN+"There is the MR Number!!");
			return XmlUtil.createErrorXml ("Error tracking MR Number");
			
		}
		if(isMRNAssigned){
			
			return XmlUtil.createErrorXml("Yea MR Number Assign Ho Chuka Hai");			
		}

		
		pat.setMrno(indusMRN);
		String labid=pat.getLaboratoryId();

		Boolean mrnsaved = null;
		try
		{
			mrnsaved = ssl.updatePatient(pat);
		}
		catch (Exception e3)
		{
			e3.printStackTrace ();
		}

		if (mrnsaved == null || !mrnsaved)
		{
			return XmlUtil.createErrorXml ("Error saving MRNo. Try again");
		}
		
		
		EncounterId encId = new EncounterId ();
		encId.setPid1 (patientID.toUpperCase());
		encId.setPid2 (chwID.toUpperCase());

		Encounter e = new Encounter ();
		e.setId (encId);
		e.setEncounterType (EncounterType.INDUS_MRN);
		e.setLocationId(labid);
		e.setDateEncounterStart (encounterStartDate);
		e.setDateEncounterEnd (encounterEndDate);
		e.setDetail("");
		Date encounterdateentered = null;
		try
		{
			e.setDateEncounterEntered(DateTimeUtil.getDateFromString (dateField, DateTimeUtil.FE_FORMAT));
			
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			return XmlUtil.createErrorXml ("Bad entered date. Please try again");
		}
				

		try
		{
			ssl.saveEncounter (e);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			XmlUtil.createErrorXml ("Could not save encounter! Please try again!");
		}
		
		ArrayList<EncounterResults> encounters=new ArrayList<EncounterResults>();
		
		EncounterResults enteredDate=ModelUtil.createEncounterResult(e, "Entered_Date".toUpperCase() , dateField);
		encounters.add(enteredDate);
		
		EncounterResults dateRegistered=ModelUtil.createEncounterResult(e, "Date_Registered".toUpperCase(), dreg);
		encounters.add(dateRegistered);
		
		EncounterResults districtType=ModelUtil.createEncounterResult(e, "District".toUpperCase(),district);
		encounters.add(districtType);
		
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
				return XmlUtil.createErrorXml ("ERROR: Please try again");
			}
		}
		
			String xml=XmlUtil.createSuccessXml();
			return xml;
	}
	
	private String doAdvertisingGPS()
	{
		String chwId = request.getParameter ("chwid");
		String location= request.getParameter("location");
		String locationspspec =request.getParameter("locationspspec");//other
		String locationWhere="";
		
		if(location.equalsIgnoreCase("gp office"))
		{
			locationWhere = location;
		}
		else if(location.equalsIgnoreCase("pharmacy"))
		{
			locationWhere = location;
		}
		
		else if(location.equalsIgnoreCase("masjid"))
		{
			locationWhere = location;
		}
		else
		{
			locationWhere = locationspspec;
		}
		
		String advertisingType= request.getParameter("adv");
		String latitude = request.getParameter ("lat");
		
		
		String longitude = request.getParameter ("lng");		
		String startDate = request.getParameter ("sd");
		String startTime = request.getParameter ("st");
		String endTime = request.getParameter ("et");
		String enteredDate = request.getParameter ("ed");
		String labpreferedname = request.getParameter ("nearlab");
		String labpreferedid = request.getParameter ("labprefid");

		System.out.println (startDate);
		System.out.println (startTime);
		System.out.println (endTime);
		System.out.println (enteredDate);
		int posterNumber=0;
		int pamphletNumber=0;
		int bannerNumber=0;
		String locationIDDB="";
		Boolean locationExists=null;
		String lastLocationID="";
		
		try{
			
			String posterResult[]=ssl.getColumnData("location"," LocationID", "LocationType='"+advertisingType+"' and LocationName='" + labpreferedname.toUpperCase () + "'");
				
			int max=0;
			for(int i=0;i<posterResult.length;i++)
				{
					System.out.println(posterResult[i]+"POster Results Values for SIND LAB");
					if(posterResult[i].length()==5){
					int j=Integer.parseInt(posterResult[i].substring(4, 5));
					
					System.out.println(j+""+max);
					
					if(j>max)
					{ 
					max=j;
					System.out.println(max);
					}
					
				}
					
					else if(posterResult[i].length()==6){
						System.out.println(posterResult[i]+"POster Results Values for SIND LAB");
						int j=Integer.parseInt(posterResult[i].substring(4, 6));
						
						System.out.println(j+""+max);
						
						if(j>max)
						{ 
						max=j;
						System.out.println(max);
						}
					}
					
					else if(posterResult[i].length()==7){
						System.out.println(posterResult[i]+"POster Results Values for SIND LAB");
						int j=Integer.parseInt(posterResult[i].substring(4, 7));
						
						System.out.println(j+""+max);
						
						if(j>max)
						{ 
						max=j;
						System.out.println(max);
						}
					}
					
					else if(posterResult[i].length()==8){
						System.out.println(posterResult[i]+"POster Results Values for SIND LAB");
						int j=Integer.parseInt(posterResult[i].substring(4, 8));
						
						System.out.println(j+""+max);
						
						if(j>max)
						{ 
						max=j;
						System.out.println(max);
						}
					}
					
					else if(posterResult[i].length()==9){
						System.out.println(posterResult[i]+"POster Results Values for SIND LAB");
						int j=Integer.parseInt(posterResult[i].substring(4, 9));
						
						System.out.println(j+""+max);
						
						if(j>max)
						{ 
						max=j;
						System.out.println(max);
						}
					}
				}
			System.out.println("max value is"+max);
			posterNumber=max;
			pamphletNumber=max;
			bannerNumber=max;
			
			}catch(Exception e){}
		try
		{
			locationExists=ssl.exists ("location", "LocationType='"+advertisingType+"' and LocationName='" + labpreferedname.toUpperCase () + "'");
			
		}
		catch (Exception e)
		{
			return XmlUtil.createErrorXml ("Exception Location Exist");
		}
		
		if(locationExists==null)
		{
			return XmlUtil.createErrorXml ("Error tracking MR Number");
		}
		
		if(locationExists && advertisingType.equals("Poster"))
		{
			
			posterNumber++;
			locationIDDB= labpreferedid+""+advertisingType.substring(0, 2)+""+posterNumber;
		}
		
		else if(locationExists && advertisingType.equals("Pamphlet"))
		{
			pamphletNumber++;
			locationIDDB= labpreferedid+""+advertisingType.substring(0, 2)+""+pamphletNumber;
		}
		
		else if(locationExists && advertisingType.equals("Banner"))
		{
			bannerNumber++;
			locationIDDB= labpreferedid+""+advertisingType.substring(0, 2)+""+bannerNumber;
		}
		
		if(!locationExists && advertisingType.equals("Poster"))
		{
			posterNumber++;
			locationIDDB= labpreferedid+""+advertisingType.substring(0, 2)+""+posterNumber;
		}
		
		else if(!locationExists && advertisingType.equals("Pamphlet"))
		{
			pamphletNumber++;
			locationIDDB= labpreferedid+""+advertisingType.substring(0, 2)+""+pamphletNumber;
		}
		
		else if(!locationExists && advertisingType.equals("Banner"))
		{
			bannerNumber++;
			locationIDDB= labpreferedid+""+advertisingType.substring(0, 2)+""+bannerNumber;
		}
		
		Location locationObject=new Location();
		locationObject.setLocationId(locationIDDB.toUpperCase());
		
		locationObject.setLocationName(labpreferedname);
		locationObject.setLocationType(advertisingType.toUpperCase());
		//locationObject.setAddressTown(locationWhere);
		try{
		Float f=new Float(latitude);
	//	locationObject.setAddressLocationLat(f);
		Float f1=new Float(longitude);
		//locationObject.setAddressLocationLon(f1);
		}catch(Exception e){}
		//locationObject.setCity("KARACHI");
		//locationObject.setCountry("PAKISTAN");
		
		try
		{
			ssl.saveLocation(locationObject);
		}
		
		catch (Exception e1)
		{
			e1.printStackTrace ();
			XmlUtil.createErrorXml ("Could not save location! Please try again!");
		}
		
		EncounterId encId = new EncounterId ();
		encId.setPid1 (locationIDDB.toUpperCase());
		encId.setPid2 (chwId.toUpperCase());

		Encounter e = new Encounter ();
		e.setId (encId);
		e.setEncounterType (EncounterType.ADVERTISING_GPS);
		e.setLocationId(labpreferedid);
		e.setDateEncounterStart (encounterStartDate);
		e.setDateEncounterEnd (encounterEndDate);
		e.setDetail("");
		Date encounterdateentered = null;
		try
		{
			e.setDateEncounterEntered(DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.FE_FORMAT));
			
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			return XmlUtil.createErrorXml ("Bad entered date. Please try again");
		}
		
		try
		{
			ssl.saveEncounter (e);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			XmlUtil.createErrorXml ("Could not save encounter! Please try again!");
		}
		
		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults> ();

		EncounterResults dateResult = ModelUtil.createEncounterResult (e, "entered_date".toUpperCase (), enteredDate);
		encounters.add (dateResult);

		EncounterResults advType = ModelUtil.createEncounterResult (e, "advertising_type".toUpperCase (), advertisingType.toUpperCase ());
		encounters.add (advType);
		
		EncounterResults locationResult = ModelUtil.createEncounterResult (e, "location".toUpperCase (), location.toUpperCase ());
		encounters.add (locationResult);

		EncounterResults locationotherResult = ModelUtil.createEncounterResult (e, "location_other".toUpperCase (), (locationspspec == null ? "" : locationspspec.toUpperCase ()));
		encounters.add (locationotherResult);

		EncounterResults latitudeResult = ModelUtil.createEncounterResult (e, "lat".toUpperCase (), latitude.toUpperCase ());
		encounters.add (latitudeResult);

		EncounterResults longitudeResult = ModelUtil.createEncounterResult (e, "long".toUpperCase (), longitude.toUpperCase ());
		encounters.add (longitudeResult);

		EncounterResults labpreferednameResult = ModelUtil.createEncounterResult (e, "lab_prefered".toUpperCase (), labpreferedid.toUpperCase ());
		encounters.add (labpreferednameResult);
		
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
				return XmlUtil.createErrorXml ("ERROR: Please try again");
			}
		}
	
		/*System.out.println(locationObject.getLocationId());
		lastLocationID=locationObject.getLocationId();
		if(lastLocationID.substring(2,4).equals(("PO")))
		{
			posterNumber=Integer.parseInt(lastLocationID.substring(4, 5));
			System.out.println(posterNumber+"-------------");
			
		}*/
		return XmlUtil.createSuccessXml();
		
	}
	


	private String doBaselineTreatment ()
	{

		String id = request.getParameter ("id");
		String chwId = request.getParameter ("chwid");
		String labId = request.getParameter ("labid");
		// String gpId = request.getParameter("gpid").toUpperCase();

		// String height = request.getParameter("ht");
		String weight = request.getParameter ("wt");
		String pregnancy = request.getParameter ("pregn");

		// String phase = request.getParameter("pt");
		String patientType = request.getParameter ("ptp");
		String patientCategory = request.getParameter ("pc");
		// String diseaseSite = request.getParameter("ds");
		String regimen = request.getParameter ("reg");
		String tablets = request.getParameter ("tab");
		String trtSuppName = request.getParameter ("tsName");
		String trtrelation = request.getParameter ("tsRel");
		String trtRelationOther = null;
		if (trtrelation.equalsIgnoreCase ("other"))
		{
			trtRelationOther = request.getParameter ("tsOtherRel");
		}
		String trtPhone = request.getParameter ("tsPhn");
		String streptoDose = request.getParameter ("str");

		String startDate = request.getParameter ("sd");
		String startTime = request.getParameter ("st");
		String endTime = request.getParameter ("et");
		String enteredDate = request.getParameter ("ed");

		Patient pat = null;
		Screening scr = null;
	
		Boolean rifCheck = null;
		

		try
		{
			rifCheck = ssl.exists ("sputumresults", "where PatientID='" + id + "' AND GeneXpertResistance='RIF RESISTANT'");
		}
		catch (Exception e4)
		{
			e4.printStackTrace ();
			return XmlUtil.createErrorXml ("Error tracking Patient. Please try again");
		}
		try
		{
			scr = ssl.findScreeningByPatientID (id);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
		if(scr.getAge()<15 && Integer.parseInt(weight)<35)
		{
			return XmlUtil.createErrorXml ("Patient is less than 15 years old and weight is less than 35 kg.");
		}
		try
		{
			pat = ssl.findPatient (id);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
		if (pat == null)
		{
			return XmlUtil.createErrorXml ("Patient with id " + id + " does not exist. Please recheck ID and try again.");
		}

		Boolean exists = null;

		try
		{
			exists = ssl.exists ("encounter", " where PID1='" + id + "'" + " AND EncounterType='" + EncounterType.BASELINE + "'");
		}
		catch (Exception e3)
		{
			e3.printStackTrace ();
		}

		if (exists == null)
		{
			return XmlUtil.createErrorXml ("Error tracking Patient ID");
		}
		else if (exists)
		{
			return XmlUtil.createErrorXml ("Patient " + id
					+ " ka Baseline Treatment Form pehlay bhara ja chuka hai. Agar aap se form bharnay mein koi ghalati huwi hai to TB Reach team se rujoo karain");
		}

		/*
		 * pat.setBloodGroup( bloodGroup ); pat.setChwid( chwid );
		 * pat.setCompletedPreviousTreatment( completedPreviousTreatment );
		 * pat.setDateRegistered( dateRegistered ); pat.setDiseaseCategory(
		 * diseaseCategory ); pat.setDiseaseConfirmed( diseaseConfirmed );
		 * pat.setDiseaseHistory( diseaseHistory ); pat.setDiseaseSite(
		 * diseaseSite ); pat.setDiseaseSuspected( diseaseSuspected );
		 * pat.setDoseCombination( doseCombination ); pat.setExternalMrno(
		 * externalMrno ); pat.setFullDescription( fullDescription );
		 * pat.setGpid( gpid ); pat.setHeight( height ); pat.setLaboratoryId(
		 * laboratoryId ); pat.setMrno( mrno ); pat.setPatientId( patientId );
		 * pat.setPatientStatus( patientStatus ); pat.setPatientType(
		 * patientType ); pat.setPreviousTreatmentDuration(
		 * previousTreatmentDuration ); pat.setRegimen( regimen );
		 * pat.setSeverity( severity ); pat.setStreptomycin( streptomycin );
		 * pat.setTreatedPreviously( treatedPreviously );
		 * pat.setTreatmentCenter( treatmentCenter ); pat.setTreatmentPhase(
		 * treatmentPhase ); pat.setTreatmentSupporter( treatmentSupporter );
		 * pat.setTreatmentSupporterContact( treatmentSupporterContact );
		 * pat.setTreatmentSupporterRelationship( treatmentSupporterRelationship
		 * ); pat.setTswid( tswid ); pat.setWeight( weight );
		 */

		pat.setDiseaseCategory (patientCategory.toUpperCase ());
		// pat.setDiseaseSite( diseaseSite.toUpperCase() );
		pat.setDoseCombination (Float.parseFloat (tablets));
		// pat.setHeight( height );
		pat.setPatientType (patientType.toUpperCase ());
		pat.setRegimen (regimen.toUpperCase ());
		int streptoInt = 0;
		if (!StringUtils.isEmptyOrWhitespaceOnly (streptoDose))
		{
			try
			{
				streptoInt = Integer.parseInt (streptoDose);
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace ();
				return XmlUtil.createErrorXml ("Invalid Strepto Dose. Please contact tbreach team");
			}
		}

		pat.setStreptomycin (streptoInt);
		// pat.setTreatmentCenter( treatmentCenter );
		// pat.setTreatmentPhase( phase.toUpperCase() );
		if (!StringUtils.isEmptyOrWhitespaceOnly (trtSuppName))
		{
			pat.setTreatmentSupporter (trtSuppName.toUpperCase ());
			pat.setTreatmentSupporterContact (trtPhone);
			if (trtRelationOther == null)
			{// should be null check as it will only be null if relation is not
				// OTHER
				pat.setTreatmentSupporterRelationship (trtrelation.toUpperCase ());
			}
			else
			{
				pat.setTreatmentSupporterRelationship (trtRelationOther.toUpperCase ());
			}
		}
		pat.setWeight (Float.parseFloat (weight));

		Boolean isupdated = null;
		try
		{
			isupdated = ssl.updatePatient (pat);
		}
		catch (Exception e2)
		{
			e2.printStackTrace ();
		}

		if (isupdated == null || !isupdated)
		{
			return XmlUtil.createErrorXml ("Could not update Patient. Please try again");
		}
		EncounterId encId = new EncounterId (0, id.toUpperCase (), chwId.toUpperCase ());

		Date encounterdateentered = null;
		try
		{
			encounterdateentered = DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.FE_FORMAT);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			return XmlUtil.createErrorXml ("Bad entered date. Please try again");
		}
		Encounter e = new Encounter (encId, EncounterType.BASELINE, labId, encounterdateentered, encounterStartDate, encounterEndDate, "");

		try
		{
			ssl.saveEncounter (e);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			XmlUtil.createErrorXml ("Could not save encounter! Please try again!");
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults> ();

		EncounterResults dateResult = ModelUtil.createEncounterResult (e, "entered_date".toUpperCase (), enteredDate);
		encounters.add (dateResult);

		EncounterResults typeResult = ModelUtil.createEncounterResult (e, "patient_type".toUpperCase (), patientType.toUpperCase ());
		encounters.add (typeResult);

		EncounterResults categoryResult = ModelUtil.createEncounterResult (e, "patient_category".toUpperCase (), patientCategory.toUpperCase ());
		encounters.add (categoryResult);

		EncounterResults pregnancyResult = ModelUtil.createEncounterResult (e, "pregnancy".toUpperCase (), (pregnancy == null ? "" : pregnancy.toUpperCase ()));
		encounters.add (pregnancyResult);

		/*
		 * EncounterResults heightResult = ModelUtil.createEncounterResult(e,
		 * "height".toUpperCase(), height); encounters.add(heightResult);
		 */

		EncounterResults weightResult = ModelUtil.createEncounterResult (e, "weight".toUpperCase (), weight.toUpperCase ());
		encounters.add (weightResult);

		EncounterResults regimenResult = ModelUtil.createEncounterResult (e, "regimen".toUpperCase (), regimen.toUpperCase ());
		encounters.add (regimenResult);

		EncounterResults tabletResult = ModelUtil.createEncounterResult (e, "fdc_tablets".toUpperCase (), tablets.toUpperCase ());
		encounters.add (tabletResult);

		EncounterResults streptoResult = ModelUtil.createEncounterResult (e, "streptomycin".toUpperCase (), (streptoDose == null ? "" : streptoDose.toUpperCase ()));
		encounters.add (streptoResult);

		/*
		 * EncounterResults phaseResult = ModelUtil.createEncounterResult(e,
		 * "treatment_phase".toUpperCase(), phase.toUpperCase());
		 * encounters.add(phaseResult);
		 * 
		 * EncounterResults diseaseSiteResult =
		 * ModelUtil.createEncounterResult(e, "disease_site".toUpperCase(),
		 * diseaseSite.toUpperCase()); encounters.add(diseaseSiteResult);
		 */

		EncounterResults tswNameResult = ModelUtil.createEncounterResult (e, "ts_name".toUpperCase (), (trtSuppName == null ? "" : trtSuppName.toUpperCase ()));
		encounters.add (tswNameResult);

		EncounterResults tswRelationResult = ModelUtil.createEncounterResult (e, "ts_relation".toUpperCase (), (trtrelation == null ? "" : trtrelation.toUpperCase ()));
		encounters.add (tswRelationResult);

		EncounterResults tswRelationOtherResult = ModelUtil.createEncounterResult (e, "ts_relation_other".toUpperCase (), (trtRelationOther == null ? "" : trtRelationOther.toUpperCase ()));
		encounters.add (tswRelationOtherResult);

		EncounterResults tswPhoneNumResult = ModelUtil.createEncounterResult (e, "ts_phone".toUpperCase (), (trtPhone == null ? "" : trtPhone.toUpperCase ()));
		encounters.add (tswPhoneNumResult);

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
				return XmlUtil.createErrorXml ("ERROR: Please try again");
			}
		}
		// TODO: Send Alert/Incentive
		if (rifCheck.booleanValue () == true)
		{
			String xml= XmlUtil.createRIFSuccessXml();
			return xml;
		}
		else 
		{
		String xml = XmlUtil.createSuccessXml ();
		return xml;
		}
	}

	// TODO: TEST
	private String doCDF ()
	{
		String xml = null;
		String id = request.getParameter ("id");
		String chwId = request.getParameter ("chwid");
		String labId = request.getParameter ("labid");
		String diagnosis = request.getParameter ("cd");
		String otherDiagnosis = request.getParameter ("otd");
		if (otherDiagnosis == null)
		{
			otherDiagnosis = "";
		}
		String clinicalDiagnosis = request.getParameter ("clinTB");
		String xraySuggestive = request.getParameter ("xray");
		String pastHistory = request.getParameter ("phist");
		String contactHistory = request.getParameter ("chist");
		String antibioticTrial = request.getParameter ("anti");
		String largeLymph = request.getParameter ("lymph");
		String lymphBiopsy = request.getParameter ("lbiop");

		String mantoux = request.getParameter ("mt");

		String other = request.getParameter ("other");

		String otherDiagnostic = request.getParameter ("otherdiag");
		if (otherDiagnostic == null)
		{
			otherDiagnostic = "";
		}

		String notes = request.getParameter ("notes");
		if (notes == null)
		{
			notes = "";
		}

		String startDate = request.getParameter ("sd");
		String startTime = request.getParameter ("st");
		String endTime = request.getParameter ("et");
		String enteredDate = request.getParameter ("ed");

		try
		{
			Boolean check = ssl.exists ("encounter", " where PID1='" + id + "' AND EncounterType='" + EncounterType.CLINICAL_DIAGNOSIS + "'");

			if (check == null)
			{
				return XmlUtil.createErrorXml ("Could not save data. Please try again");
			}

			else if (check.booleanValue ())
			{
				return XmlUtil.createErrorXml (" Clinical Diagnosis for this patient is already complete");
			}
		}

		catch (Exception e)
		{
			return XmlUtil.createErrorXml ("Could not access encounter record. Please try again");
		}

		Patient pat = null;

		try
		{
			pat = ssl.findPatient (id);
		}
		catch (Exception e2)
		{
			e2.printStackTrace ();
			return XmlUtil.createErrorXml ("Could not access patient record. Please try again");
		}

		if (pat == null)
		{
			return XmlUtil.createErrorXml ("Could find patient. Please try again");
		}

		Boolean patientPositive = pat.getDiseaseConfirmed ();

		if (patientPositive.booleanValue ())
		{
			return XmlUtil.createErrorXml ("This patient has already been diagnosed with TB");
		}

		else if (!diagnosis.equalsIgnoreCase ("other than tb"))
		{
			pat.setPatientStatus("PATIENT");
			pat.setDiseaseConfirmed (new Boolean (true));
			try
			{
				boolean check = ssl.updatePatient (pat);
			}
			catch (Exception e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace ();
				return XmlUtil.createErrorXml ("Error updating Patient. Please try again");
			}

		}

		EncounterId encId = new EncounterId ();
		encId.setPid1 (id.toUpperCase ());
		encId.setPid2 (chwId.toUpperCase ());

		Encounter e = new Encounter ();
		e.setId (encId);
		e.setEncounterType (EncounterType.CLINICAL_DIAGNOSIS);
		e.setDateEncounterStart (encounterStartDate);
		e.setDateEncounterEnd (encounterEndDate);
		e.setLocationId (labId);
		try
		{
			e.setDateEncounterEntered (DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.FE_FORMAT));
		}

		catch (Exception e1)
		{
			e1.printStackTrace ();
			return XmlUtil.createErrorXml ("Bad entered date. Please try again");
		}

		try
		{
			ssl.saveEncounter (e);
		}
		catch (Exception e1)
		{
			// Auto-generated catch block
			e1.printStackTrace ();
			return XmlUtil.createErrorXml ("Error occurred. Please try again");
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults> ();

		EncounterResults dateResult = ModelUtil.createEncounterResult (e, "entered_date".toUpperCase (), enteredDate);
		encounters.add (dateResult);

		EncounterResults diagnosisResult = ModelUtil.createEncounterResult (e, "diagnosis".toUpperCase (), diagnosis.toUpperCase ());
		encounters.add (diagnosisResult);

		EncounterResults otherDiagnosisResult = ModelUtil.createEncounterResult (e, "other_than_tb_detail".toUpperCase (), otherDiagnosis.toUpperCase ());
		encounters.add (otherDiagnosisResult);

		EncounterResults clinicalResult = ModelUtil.createEncounterResult (e, "clinical_symp_TB".toUpperCase (), clinicalDiagnosis.toUpperCase ());
		encounters.add (clinicalResult);

		EncounterResults xrayResult = ModelUtil.createEncounterResult (e, "xray_suggestive".toUpperCase (), xraySuggestive.toUpperCase ());
		encounters.add (xrayResult);

		EncounterResults pastHistoryResult = ModelUtil.createEncounterResult (e, "past_history".toUpperCase (), pastHistory.toUpperCase ());
		encounters.add (pastHistoryResult);

		EncounterResults contactHistoryResult = ModelUtil.createEncounterResult (e, "contact_history".toUpperCase (), contactHistory.toUpperCase ());
		encounters.add (contactHistoryResult);

		EncounterResults antibioticResult = ModelUtil.createEncounterResult (e, "antibiotic_trial".toUpperCase (), antibioticTrial.toUpperCase ());
		encounters.add (antibioticResult);

		EncounterResults largeLymphResult = ModelUtil.createEncounterResult (e, "large_lymph_nodes".toUpperCase (), largeLymph.toUpperCase ());
		encounters.add (largeLymphResult);

		EncounterResults lymphBiopsyResult = ModelUtil.createEncounterResult (e, "lymph_node_biopsy".toUpperCase (), lymphBiopsy.toUpperCase ());
		encounters.add (lymphBiopsyResult);

		EncounterResults mantouxResult = ModelUtil.createEncounterResult (e, "mantoux".toUpperCase (), mantoux.toUpperCase ());
		encounters.add (mantouxResult);

		EncounterResults otherResult = ModelUtil.createEncounterResult (e, "other_diagnostic".toUpperCase (), other.toUpperCase ());
		encounters.add (otherResult);

		EncounterResults otherDetailResult = ModelUtil.createEncounterResult (e, "other_diagnostic_detail".toUpperCase (), otherDiagnostic.toUpperCase ());
		encounters.add (otherDetailResult);

		EncounterResults notesResult = ModelUtil.createEncounterResult (e, "notes".toUpperCase (), notes.toUpperCase ());
		encounters.add (notesResult);

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
				return XmlUtil.createErrorXml ("Error occurred. Please try again!");
			}
		}

		xml = XmlUtil.createSuccessXml ();
		return xml;
	}

	private String doContactInfo ()
	{
		String id = request.getParameter ("id");
		String chwid = request.getParameter ("chwid");
		String labid = request.getParameter ("labid");

		String phone1 = request.getParameter ("phn1");
		String whosephone1 = request.getParameter ("whophn1");
		String phone2 = request.getParameter ("phn2");
		String whosephone2 = request.getParameter ("whophn2");

		String addhouseNumber = request.getParameter ("hn");
		String addstreetName = request.getParameter ("sn");
		String addsectorName = request.getParameter ("sec");
		String addcolonyName = request.getParameter ("cn");
		String addtownName = request.getParameter ("tn");
		String addlandmark = request.getParameter ("lm");
		String adduc = request.getParameter ("uc");

		String startDate = request.getParameter ("sd");
		String startTime = request.getParameter ("st");
		String endTime = request.getParameter ("et");
		String enteredDate = request.getParameter ("ed");

		System.out.println (startDate);
		System.out.println (startTime);
		System.out.println (endTime);
		System.out.println (enteredDate);

		Screening scr = null;
		try
		{
			scr = ssl.findScreeningByPatientID (id);
		}
		catch (Exception e4)
		{
			e4.printStackTrace ();
		}

		if (scr == null)
		{
			return XmlUtil.createErrorXml ("Patient does not exist. Please confirm ID and try again");
		}

		Patient pat = null;
		try
		{
			pat = ssl.findPatient (id);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
		}

		if (pat == null)
		{
			return XmlUtil
					.createErrorXml ("Di gai id ki Screening hui hay lekin disease confirm nahi ho saki. Agar Registration-A form fill nahi hua hay tu pehlay us ki data entry karen. Baraey meherbani tamam forms tarteeb say fill karen");
		}

		Boolean exists = null;

		try
		{
			exists = ssl.exists ("encounter", " " + "where PID1='" + id + "' " + " AND EncounterType='" + EncounterType.CONTACT_INFO + "'");
		}
		catch (Exception e3)
		{
			e3.printStackTrace ();
		}

		if (exists == null)
		{
			return XmlUtil.createErrorXml ("Error tracking Patient ID");
		}
		else if (exists.booleanValue () == true)
		{
			return XmlUtil.createErrorXml ("Patient " + id + " ka Registration-B Form pehlay bhara ja chuka hai. Agar aap se form bharnay mein koi ghalati huwi hai to TB Reach team se rujoo karain");
		}

		Contact c = null;
		try{
			c = ssl.findContact(id);
		}
		catch (Exception e) {
			return XmlUtil.createErrorXml ("Error tracking Patient Contact");
		}
		if (c == null)
		{
			return XmlUtil.createErrorXml ("Contact for Patient ID was not found. Please fill Registration-B form.");
		}
		
		c.setAddressColony(addcolonyName.toUpperCase ());
		c.setAddressHouse(addhouseNumber.toUpperCase ());
		c.setAddressLandMark(addlandmark.toUpperCase ());
		c.setAddressSector(addsectorName.toUpperCase ());
		c.setAddressStreet(addstreetName.toUpperCase ());
		c.setAddressTown(addtownName.toUpperCase ());
		c.setAddressUc(adduc.toUpperCase ());
		c.setPhone(phone1.toUpperCase ());
		c.setPhoneOwner(whosephone1.toUpperCase ());
		c.setMobile(phone2.toUpperCase ());
		c.setMobileOwner(whosephone2.toUpperCase ());

		Boolean issaved = null;
		try
		{
			issaved = ssl.updateContact(c);
		}
		catch (Exception e3)
		{
			e3.printStackTrace ();
		}

		if (issaved == null || !issaved)
		{
			return XmlUtil.createErrorXml ("Error saving Contact. Try again");
		}

		EncounterId encId = new EncounterId (0, id.toUpperCase (), chwid.toUpperCase ());

		Date encounterdateentered = null;
		try
		{
			encounterdateentered = DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.FE_FORMAT);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			return XmlUtil.createErrorXml ("Bad entered date. Please try again");
		}

		Encounter e = new Encounter (encId, EncounterType.CONTACT_INFO, labid, encounterdateentered, encounterStartDate, encounterEndDate, "");

		try
		{
			ssl.saveEncounter (e);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			XmlUtil.createErrorXml ("Could not save encounter! Please try again!");
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults> ();

		EncounterResults dateResult = ModelUtil.createEncounterResult (e, "entered_date".toUpperCase (), enteredDate);
		encounters.add (dateResult);

		EncounterResults phone1Result = ModelUtil.createEncounterResult (e, "phone1".toUpperCase (), phone1.toUpperCase ());
		encounters.add (phone1Result);

		EncounterResults whosephone1Result = ModelUtil.createEncounterResult (e, "phone1_owner".toUpperCase (), whosephone1.toUpperCase ());
		encounters.add (whosephone1Result);

		EncounterResults phone2Result = ModelUtil.createEncounterResult (e, "phone2".toUpperCase (), phone2);
		encounters.add (phone2Result);

		EncounterResults whosephone2Result = ModelUtil.createEncounterResult (e, "phone2_owner".toUpperCase (), whosephone2.toUpperCase ());
		encounters.add (whosephone2Result);

		EncounterResults addhouseResult = ModelUtil.createEncounterResult (e, "add_house".toUpperCase (), addhouseNumber.toUpperCase ());
		encounters.add (addhouseResult);

		EncounterResults addstreetResult = ModelUtil.createEncounterResult (e, "add_street".toUpperCase (), addstreetName.toUpperCase ());
		encounters.add (addstreetResult);

		EncounterResults addsectorResult = ModelUtil.createEncounterResult (e, "add_sector".toUpperCase (), addsectorName.toUpperCase ());
		encounters.add (addsectorResult);

		EncounterResults addcolonyResult = ModelUtil.createEncounterResult (e, "add_colony".toUpperCase (), addcolonyName.toUpperCase ());
		encounters.add (addcolonyResult);

		EncounterResults addtownResult = ModelUtil.createEncounterResult (e, "add_town".toUpperCase (), addtownName.toUpperCase ());
		encounters.add (addtownResult);

		EncounterResults adducResult = ModelUtil.createEncounterResult (e, "add_uc".toUpperCase (), adduc.toUpperCase ());
		encounters.add (adducResult);

		EncounterResults addlandmarkResult = ModelUtil.createEncounterResult (e, "add_landmark".toUpperCase (), addlandmark.toUpperCase ());
		encounters.add (addlandmarkResult);

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
				return XmlUtil.createErrorXml ("ERROR: Please try again");
			}
		}

		// TODO: Send Alert/Incentive

		return XmlUtil.createSuccessXml ();
	}

	/*
	 * private String doContactSputumCollection() { String xml = null;
	 * 
	 * String id = request.getParameter("id");
	 * 
	 * String mid = request.getParameter("mid");
	 * 
	 * String patientStatus = request.getParameter("ps"); String mdrContact =
	 * request.getParameter("mdrc"); String sputumMonth =
	 * request.getParameter("cm"); String sampleNumber =
	 * request.getParameter("ws"); String sputumCollected =
	 * request.getParameter("sc"); String barCode = request.getParameter("sbc");
	 * 
	 * String startDate = request.getParameter("sd"); String startTime =
	 * request.getParameter("st"); String endTime = request.getParameter("et");
	 * String enteredDate = request.getParameter("ed");
	 * 
	 * Patient pat = null;
	 * 
	 * try { pat = ssl.findPatient(id); } catch (Exception e) {
	 * e.printStackTrace(); }
	 * 
	 * if(pat==null) { return XmlUtil.createErrorXml("Patient with id " + id +
	 * " does not exist. Please recheck ID and try again."); }
	 * 
	 * if(barCode!=null && barCode.length()!=0) { Boolean exists = null;
	 * 
	 * try { exists = ssl.exists("SputumResults", " where SputumTestID='" +
	 * barCode + "'"); } catch (Exception e2) { e2.printStackTrace(); }
	 * 
	 * if(exists==null) { System.out.println("null"); return
	 * XmlUtil.createErrorXml
	 * ("Error tracking Bar Code Number. Please try again!"); }
	 * 
	 * else if(exists.booleanValue()==true) { System.out.println("true"); return
	 * XmlUtil.createErrorXml(
	 * "This Bar Code has already been collected. Please recheck Bar Code and try again"
	 * ); }
	 * 
	 * //if not found in SputumResults check in ContactSputumResults
	 * if(exists.booleanValue()==false) { try { exists =
	 * ssl.exists("ContactSputumResults", " where SputumTestID='" + barCode +
	 * "'"); } catch (Exception e2) { e2.printStackTrace(); }
	 * 
	 * if(exists==null) { System.out.println("null"); return
	 * XmlUtil.createErrorXml
	 * ("Error tracking Bar Code Number. Please try again!"); } else
	 * if(exists.booleanValue()==true) { System.out.println("true"); return
	 * XmlUtil.createErrorXml(
	 * "This Bar Code has already been collected. Please recheck Bar Code and try again"
	 * ); } } }
	 * 
	 * EncounterId encId = new EncounterId(); encId.setPid1(id);
	 * encId.setPid2(mid);
	 * 
	 * Encounter e = new Encounter(); e.setId(encId);
	 * e.setEncounterType("CS_COLL");
	 * e.setDateEncounterStart(encounterStartDate);
	 * e.setDateEncounterEnd(encounterEndDate); try {
	 * e.setDateEncounterEntered(DateTimeUtil.getDateFromString(enteredDate,
	 * DateTimeUtil.FE_FORMAT)); } catch(Exception e1) { e1.printStackTrace();
	 * return XmlUtil.createErrorXml("Bad entered date. Please try again"); }
	 * 
	 * try { boolean eCreated = ssl.saveEncounter(e); } catch (Exception e1) {
	 * // Auto-generated catch block e1.printStackTrace(); return
	 * XmlUtil.createErrorXml("Error occurred. Please try again"); }
	 * 
	 * ArrayList<EncounterResults> encounters = new
	 * ArrayList<EncounterResults>();
	 * 
	 * EncounterResults dateResult = ModelUtil.createEncounterResult(e,
	 * "entered_date".toUpperCase(), enteredDate); encounters.add(dateResult);
	 * 
	 * EncounterResults patientStatusResult = ModelUtil.createEncounterResult(
	 * e, "patient_status".toUpperCase(), patientStatus.toUpperCase());
	 * encounters.add(patientStatusResult);
	 * 
	 * EncounterResults mdrContactResult = ModelUtil.createEncounterResult( e,
	 * "mdr_contact".toUpperCase(), mdrContact.toUpperCase());
	 * encounters.add(mdrContactResult);
	 * 
	 * EncounterResults collectionMonthResult = ModelUtil
	 * .createEncounterResult(e, "collection_month".toUpperCase(),
	 * sputumMonth.toUpperCase()); encounters.add(collectionMonthResult);
	 * 
	 * EncounterResults suspectSampleNumberResult = ModelUtil
	 * .createEncounterResult(e, "suspect_sample".toUpperCase(),
	 * sampleNumber.toUpperCase()); encounters.add(suspectSampleNumberResult);
	 * 
	 * EncounterResults sputumCollectedResult = ModelUtil
	 * .createEncounterResult(e, "sputum_collected".toUpperCase(),
	 * sputumCollected.toUpperCase()); encounters.add(sputumCollectedResult);
	 * 
	 * if (barCode != null) { EncounterResults barcodeResult =
	 * ModelUtil.createEncounterResult(e, "sample_barcode".toUpperCase(),
	 * barCode.toUpperCase()); encounters.add(barcodeResult); }
	 * 
	 * boolean resultSave = true;
	 * 
	 * for (int i = 0; i < encounters.size(); i++) { try { resultSave =
	 * ssl.saveEncounterResults(encounters.get(i)); } catch (Exception e1) { //
	 * Auto-generated catch block e1.printStackTrace(); break; }
	 * 
	 * if (!resultSave) { return
	 * XmlUtil.createErrorXml("Error occurred. Please try again"); } }
	 * 
	 * // //TODO: Modify for three tables: //If baseline: Add to SputumResults,
	 * GXPert Results, Chest Xray //Else: Just add to SputumResults
	 * if(barCode!=null && barCode.length()!=0) { //SputumResultsId sri1 = new
	 * SputumResultsId(); int month = -1;
	 * 
	 * if(sputumMonth.equals("Baseline")) { month = 0; }
	 * 
	 * else if(sputumMonth.equals("2nd")) { month = 2; }
	 * 
	 * else if(sputumMonth.equals("3rd")) { month = 3; }
	 * 
	 * else if(sputumMonth.equals("5th")) { month = 5; }
	 * 
	 * else if(sputumMonth.equals("7th")) { month = 7; }
	 * 
	 * //sri1.setPatientId(id);
	 * //sri1.setSputumTestId(Integer.parseInt(barCode)); ContactSputumResults
	 * csr = new ContactSputumResults();
	 * csr.setSputumTestId(Integer.parseInt(barCode)); csr.setPatientId(id);
	 * csr.setMonth(month); csr.setIrs(0);
	 * 
	 * //confirm this with Owais if(mdrContact.equalsIgnoreCase("Yes")) {
	 * csr.setIsTestPending(new Boolean(true)); }
	 * 
	 * else { csr.setIsTestPending(new Boolean(false)); }
	 * //sr.setDateSubmitted(encounterStartDate);
	 * 
	 * try { ssl.saveContactSputumResults(csr); } catch (Exception e1) {
	 * e1.printStackTrace(); }
	 * 
	 * //if(month==0) {
	 * 
	 * //GXP GeneXpertResults gxp = new GeneXpertResults();
	 * gxp.setIsPositive(new Boolean(false)); gxp.setPatientId(id);
	 * gxp.setSputumTestId(Integer.parseInt(barCode)); gxp.setIrs(0);
	 * 
	 * //ssl.saveGeneXpertResults(gxp); //} } xml = XmlUtil.createSuccessXml();
	 * return xml; }
	 */

	/*
	 * private String doDFR() { String xml = null; String id =
	 * request.getParameter("id").toUpperCase();
	 * 
	 * String location = request.getParameter("loc"); String locationDetail =
	 * request.getParameter("locd"); String gpId = request.getParameter("gpid");
	 * String attempted = request.getParameter("att"); String screened =
	 * request.getParameter("scr"); String refused =
	 * request.getParameter("ref"); String missed = request.getParameter("mis");
	 * String suspects = request.getParameter("sus");
	 * 
	 * 
	 * String startDate = request.getParameter("sd"); String startTime =
	 * request.getParameter("st"); String endTime = request.getParameter("et");
	 * String enteredDate = request.getParameter("ed");
	 * 
	 * if(gpId==null) { return
	 * XmlUtil.createErrorXml("Phone update karain aur dobara form save karain"
	 * ); }
	 * 
	 * //start CHW ID validity check Boolean cCheck = null; if((cCheck =
	 * ModelUtil.isValidCHWID(id))!=null) { if(!cCheck.booleanValue()) { return
	 * XmlUtil.createErrorXml(
	 * "Ye CHW ID sahih nahin. Sahih CHW ID darj karain aur dobara koshish karain"
	 * ); } }
	 * 
	 * else { return XmlUtil.createErrorXml("ERROR: Please try again"); } //end
	 * GP ID validity check
	 * 
	 * //start GP ID validity check Boolean gCheck = null; if((gCheck =
	 * ModelUtil.isValidGPID(gpId))!=null) { if(!gCheck.booleanValue()) { return
	 * XmlUtil.createErrorXml(
	 * "Ye GP ID sahih nahin. Sahih GP ID darj karain aur dobara koshish karain"
	 * ); } }
	 * 
	 * else { return XmlUtil.createErrorXml("ERROR: Please try again"); } //end
	 * GP ID validity check
	 * 
	 * //begin dup DFR check //String query =
	 * "select EncounterID from Encounter where EncounterType='DFR' AND DateEnounterEntered IS NOT NULL AND DateEnounterEntered LIKE '"
	 * + DateTimeUtil.convertFromSlashFormatToSQL(enteredDate.split(" ")[0]) +
	 * "%'";
	 * 
	 * String results[] = null;
	 * 
	 * try { results = ssl.getColumnData("Encounter", "EncounterID",
	 * " where PID1='" + id +
	 * "' AND EncounterType='DFR' AND DateEncounterEntered IS NOT NULL AND DateEncounterEntered LIKE '"
	 * + DateTimeUtil.convertFromSlashFormatToSQL(enteredDate.split(" ")[0]) +
	 * "%'"); } catch (Exception e2) { e2.printStackTrace(); }
	 * 
	 * String gp = ""; String er[] = null; if(results!=null) { for(int i=0;
	 * i<results.length;i++) { System.out.println((String)(results[i])); }
	 * 
	 * for(int i=0; i<results.length;i++) {
	 * 
	 * try { er = ssl.getColumnData("EncounterResults", "Value",
	 * " where PID1='"+ id + "' AND Element='GP_ID' AND EncounterID='" +
	 * (String)(results[i]) + "'"); } catch (Exception e) { e.printStackTrace();
	 * }
	 * 
	 * if(er!=null && er.length>0) { gp = er[0]; if(gp!=null &&
	 * gp.equalsIgnoreCase(gpId)) { return XmlUtil.createErrorXml(
	 * "Aap ne aaj is GPID ka ek DFR pehlay hi bhar diya hai. Agar form bharnay mein koi ghalati huwi hai to TB Reach team se rujoo karain."
	 * ); } }
	 * 
	 * }
	 * 
	 * }
	 * 
	 * //end dup DFR check
	 * 
	 * 
	 * EncounterId encId = new EncounterId(); encId.setPid1(id);
	 * encId.setPid2(id);
	 * 
	 * Encounter e = new Encounter(); e.setId(encId); e.setEncounterType("DFR");
	 * e.setDateEncounterStart(encounterStartDate);
	 * e.setDateEncounterEnd(encounterEndDate); try {
	 * e.setDateEncounterEntered(DateTimeUtil.getDateFromString(enteredDate,
	 * DateTimeUtil.FE_FORMAT)); }
	 * 
	 * catch(Exception e1) { e1.printStackTrace(); return
	 * XmlUtil.createErrorXml("Bad entered date. Please try again"); }
	 * 
	 * try { boolean eCreated = ssl.saveEncounter(e); } catch (Exception e1) {
	 * // Auto-generated catch block e1.printStackTrace(); return
	 * XmlUtil.createErrorXml("Error occurred. Please try again"); }
	 * 
	 * ArrayList<EncounterResults> encounters = new
	 * ArrayList<EncounterResults>();
	 * 
	 * EncounterResults dateResult = ModelUtil.createEncounterResult(e,
	 * "entered_date".toUpperCase(), enteredDate); encounters.add(dateResult);
	 * 
	 * EncounterResults locationResult = ModelUtil.createEncounterResult( e,
	 * "location".toUpperCase(), location.toUpperCase());
	 * encounters.add(locationResult);
	 * 
	 * EncounterResults locationDetailResult = ModelUtil
	 * .createEncounterResult(e, "location_detail".toUpperCase(),
	 * locationDetail.toUpperCase()); encounters.add(locationDetailResult);
	 * 
	 * EncounterResults gpIdResult = ModelUtil .createEncounterResult(e,
	 * "gp_id".toUpperCase(), gpId.toUpperCase()); encounters.add(gpIdResult);
	 * 
	 * EncounterResults attemptedResult = ModelUtil .createEncounterResult(e,
	 * "attempted".toUpperCase(), attempted.toUpperCase());
	 * encounters.add(attemptedResult);
	 * 
	 * EncounterResults screenedResult = ModelUtil .createEncounterResult(e,
	 * "screened".toUpperCase(), screened.toUpperCase());
	 * encounters.add(screenedResult);
	 * 
	 * EncounterResults missedResult = ModelUtil.createEncounterResult(e,
	 * "missed".toUpperCase(), missed.toUpperCase());
	 * encounters.add(missedResult);
	 * 
	 * EncounterResults refusedResult = ModelUtil.createEncounterResult(e,
	 * "refused".toUpperCase(), refused.toUpperCase());
	 * encounters.add(refusedResult);
	 * 
	 * EncounterResults suspectsResult = ModelUtil.createEncounterResult(e,
	 * "suspects".toUpperCase(), suspects.toUpperCase());
	 * encounters.add(suspectsResult);
	 * 
	 * boolean resultSave = true;
	 * 
	 * for (int i = 0; i < encounters.size(); i++) { try { resultSave =
	 * ssl.saveEncounterResults(encounters.get(i)); } catch (Exception e1) { //
	 * Auto-generated catch block e1.printStackTrace(); break; }
	 * 
	 * if (!resultSave) { return
	 * XmlUtil.createErrorXml("Error occurred. Please try again"); }
	 * 
	 * } xml = XmlUtil.createSuccessXml(); return xml; }
	 */

	private String doDrugAdm ()
	{
		String xml = null;

		String id = request.getParameter ("id");
		String chwId = request.getParameter ("chwid");
		String labid = request.getParameter ("labid");

		String whereGiven = request.getParameter ("where");
		String regimen = request.getParameter ("reg");

		String pilldays = request.getParameter ("pilldays");
		String pillnum = request.getParameter ("pillnum");
		String pillleft = request.getParameter ("pillleft");
		String pillnumleft = request.getParameter ("pillnumleft");
		String pilllost = request.getParameter ("pilllost");
		String pillnumlost = request.getParameter ("pillnumlost");
		String pillshown = request.getParameter ("pillshown");

		String strepdays = request.getParameter ("strepdays");
		String strepnum = request.getParameter ("strepnum");
		String strepleft = request.getParameter ("strepleft");
		String strepnumleft = request.getParameter ("strepnumleft");
		String streplost = request.getParameter ("streplost");
		String strepnumlost = request.getParameter ("strepnumlost");
		String strepshown = request.getParameter ("strepshown");

		String startDate = request.getParameter ("sd");
		String startTime = request.getParameter ("st");
		String endTime = request.getParameter ("et");
		String enteredDate = request.getParameter ("ed");

		int pilldaysint = 0;
		int pillnumint = 0;
		int pillnumleftint = 0;
		int pillnumlostint = 0;

		int strepdaysint = 0;
		int strepnumint = 0;
		int strepnumleftint = 0;
		int strepnumlostint = 0;
		try
		{// it will be empty only if no value has not been sent from mobile
			pilldaysint = Integer.parseInt (pilldays);
			pillnumint = Integer.parseInt (pillnum);
			pillnumleftint = Integer.parseInt (StringUtils.isEmptyOrWhitespaceOnly (pillnumleft) ? "0" : pillnumleft);
			pillnumlostint = Integer.parseInt (StringUtils.isEmptyOrWhitespaceOnly (pillnumlost) ? "0" : pillnumlost);

			if (strepdays != null || strepnum != null || strepnumleft != null || strepnumlost != null)
			{
				strepdaysint = Integer.parseInt (strepdays);
				strepnumint = Integer.parseInt (strepnum);
				strepnumleftint = Integer.parseInt (StringUtils.isEmptyOrWhitespaceOnly (strepnumleft) ? "0" : strepnumleft);
				strepnumlostint = Integer.parseInt (StringUtils.isEmptyOrWhitespaceOnly (strepnumlost) ? "0" : strepnumlost);
			}
		}
		catch (NumberFormatException e)
		{
			e.printStackTrace ();
			return XmlUtil.createErrorXml ("Error reading data values. Invalid numeric argument given");
		}
		boolean pillshownbool;
		boolean strepshownbool = false;

		pillshownbool = pillshown.equalsIgnoreCase ("yes");
		if (strepshown != null)
		{
			strepshownbool = strepshown.equalsIgnoreCase ("yes");
		}

		Patient pat = null;

		try
		{
			pat = ssl.findPatient (id);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}

		if (pat == null)
		{
			return XmlUtil.createErrorXml ("Patient with id " + id + " does not exist. Please recheck ID and try again.");
		}

		Boolean results = null;

		try
		{
			results = ssl.exists (
					"encounter",
					" where PID1='" + id + "'" + " AND EncounterType='" + EncounterType.DRUG_ADMINISTRATION + "'" + " AND DateEncounterEntered='"
							+ DateTimeUtil.convertToSQL (enteredDate, DateTimeUtil.FE_FORMAT) + "'" + " ORDER BY DateEncounterEntered DESC");
		}
		catch (Exception e3)
		{
			e3.printStackTrace ();
		}

		if (results != null && results)
		{
			return XmlUtil.createErrorXml ("Patient ka " + enteredDate
					+ " ka Drug Dispensal From bhara ja chuka hay . Agar aap say Form bharnay may koi ghalti hui hay tu TBReach team say rujoo karen.");
		}
		DrugHistoryId dhid = new DrugHistoryId (id, 0);

		Date encounterdateentered = null;
		try
		{
			encounterdateentered = DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.FE_FORMAT);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			return XmlUtil.createErrorXml ("Bad entered date. Please try again");
		}

		DrugHistory dh = new DrugHistory (dhid, encounterdateentered, chwId.toUpperCase (), whereGiven.toUpperCase (), regimen.toUpperCase (), pilldaysint, pillnumint, pillnumleftint, pillnumlostint,
				strepdaysint, strepnumint, strepnumleftint, strepnumlostint, pillshownbool, strepshownbool, "");

		if (!ssl.saveDrugHistory (dh))
		{
			return XmlUtil.createErrorXml ("Error!! Drug history data not saved. Please try again");
		}

		EncounterId encId = new EncounterId (0, id.toUpperCase (), chwId.toUpperCase ());

		Encounter e = new Encounter (encId, EncounterType.DRUG_ADMINISTRATION, labid, encounterdateentered, encounterStartDate, encounterEndDate, "");

		try
		{
			ssl.saveEncounter (e);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			XmlUtil.createErrorXml ("Could not save encounter! Please try again!");
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults> ();

		EncounterResults dateResult = ModelUtil.createEncounterResult (e, "entered_date".toUpperCase (), enteredDate);
		encounters.add (dateResult);

		EncounterResults whereDispensedResult = ModelUtil.createEncounterResult (e, "dispensal_location".toUpperCase (), whereGiven.toUpperCase ());
		encounters.add (whereDispensedResult);

		EncounterResults regimenResult = ModelUtil.createEncounterResult (e, "regimen".toUpperCase (), regimen.toUpperCase ());
		encounters.add (regimenResult);

		EncounterResults pilldaysResult = ModelUtil.createEncounterResult (e, "pills_days_worth".toUpperCase (), pilldays.toUpperCase ());
		encounters.add (pilldaysResult);

		EncounterResults pillnumResult = ModelUtil.createEncounterResult (e, "pills_num".toUpperCase (), pillnum.toUpperCase ());
		encounters.add (pillnumResult);

		EncounterResults pillleftResult = ModelUtil.createEncounterResult (e, "pills_left".toUpperCase (), pillleft.toUpperCase ());
		encounters.add (pillleftResult);

		EncounterResults pillnumleftResult = ModelUtil.createEncounterResult (e, "pills_left_num".toUpperCase (), (pillnumleft == null ? "" : pillnumleft.toUpperCase ()));
		encounters.add (pillnumleftResult);

		EncounterResults pilllostResult = ModelUtil.createEncounterResult (e, "pills_lost".toUpperCase (), pilllost.toUpperCase ());
		encounters.add (pilllostResult);

		EncounterResults pillnumlostResult = ModelUtil.createEncounterResult (e, "pills_lost_num".toUpperCase (), (pillnumlost == null ? "" : pillnumlost.toUpperCase ()));
		encounters.add (pillnumlostResult);

		EncounterResults pillshownResult = ModelUtil.createEncounterResult (e, "pills_container_shown".toUpperCase (), pillshown.toUpperCase ());
		encounters.add (pillshownResult);

		EncounterResults strepdaysResult = ModelUtil.createEncounterResult (e, "strepto_days_worth".toUpperCase (), (strepdays == null ? "" : strepdays.toUpperCase ()));
		encounters.add (strepdaysResult);

		EncounterResults strepnumResult = ModelUtil.createEncounterResult (e, "strepto_num".toUpperCase (), (strepnum == null ? "" : strepnum.toUpperCase ()));
		encounters.add (strepnumResult);

		EncounterResults strepleftResult = ModelUtil.createEncounterResult (e, "strepto_left".toUpperCase (), (strepleft == null ? "" : strepleft.toUpperCase ()));
		encounters.add (strepleftResult);

		EncounterResults strepnumleftResult = ModelUtil.createEncounterResult (e, "strepto_left_num".toUpperCase (), (strepnumleft == null ? "" : strepnumleft.toUpperCase ()));
		encounters.add (strepnumleftResult);

		EncounterResults streplostResult = ModelUtil.createEncounterResult (e, "strepto_lost".toUpperCase (), (streplost == null ? "" : streplost.toUpperCase ()));
		encounters.add (streplostResult);

		EncounterResults strepnumlostResult = ModelUtil.createEncounterResult (e, "strepto_lost_num".toUpperCase (), (strepnumlost == null ? "" : strepnumlost.toUpperCase ()));
		encounters.add (strepnumlostResult);

		EncounterResults strepshownResult = ModelUtil.createEncounterResult (e, "strepto_container_shown".toUpperCase (), (strepshown == null ? "" : strepshown.toUpperCase ()));
		encounters.add (strepshownResult);

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
				return XmlUtil.createErrorXml ("ERROR: Please try again");
			}
		}

		xml = XmlUtil.createSuccessXml ();
		return xml;
	}

	private String doEndFollowup ()
	{
		String id = request.getParameter ("id");

		String chwId = request.getParameter ("chwid");
		String labid = request.getParameter ("labid");

		String reason = request.getParameter ("rsn");
		String otherReason = request.getParameter ("otrrsn");

		String startDate = request.getParameter ("sd");
		String startTime = request.getParameter ("st");
		String endTime = request.getParameter ("et");
		String enteredDate = request.getParameter ("ed");

		Patient pat = null;
		try
		{
			pat = ssl.findPatient (id);
		}
		catch (Exception e2)
		{
			e2.printStackTrace ();
		}

		if (pat == null)
		{
			return XmlUtil.createErrorXml ("Patient with id " + id + " does not exist. Please recheck ID and try again.");
		}

		Boolean exists = null;

		try
		{
			exists = ssl.exists ("encounter", " where PID1='" + id + "'" + " AND EncounterType='" + EncounterType.END_OF_FOLLOWUP + "'");
		}
		catch (Exception e3)
		{
			e3.printStackTrace ();
		}

		if (exists == null)
		{
			return XmlUtil.createErrorXml ("Error tracking Patient ID");
		}
		else if (exists.booleanValue () == true)
		{
			return XmlUtil.createErrorXml ("Patient " + id + " ka End of Followup Form pehlay bhara ja chuka hai. Agar aap se form bharnay mein koi ghalati huwi hai to TB Reach team se rujoo karain");
		}

		Boolean patUpdated = null;
		try
		{
			pat.setPatientStatus ("CLOSED");
			patUpdated = ssl.updatePatient (pat);
		}
		catch (Exception e2)
		{
			e2.printStackTrace ();
		}

		if (patUpdated == null || !patUpdated)
		{
			return XmlUtil.createErrorXml ("Error updating patient followup status. Try again");
		}

		Date encounterdateentered = null;
		try
		{
			encounterdateentered = DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.FE_FORMAT);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			return XmlUtil.createErrorXml ("Bad entered date. Please try again");
		}

		EncounterId encId = new EncounterId (0, id.toUpperCase (), chwId.toUpperCase ());

		Encounter e = new Encounter (encId, EncounterType.END_OF_FOLLOWUP, labid, encounterdateentered, encounterStartDate, encounterEndDate, "");

		try
		{
			ssl.saveEncounter (e);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			XmlUtil.createErrorXml ("Could not save encounter! Please try again!");
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults> ();

		EncounterResults dateResult = ModelUtil.createEncounterResult (e, "entered_date".toUpperCase (), enteredDate);
		encounters.add (dateResult);

		EncounterResults reasonResult = ModelUtil.createEncounterResult (e, "reason".toUpperCase (), reason.toUpperCase ());
		encounters.add (reasonResult);

		EncounterResults otrReasonResult = ModelUtil.createEncounterResult (e, "other_reason".toUpperCase (), (otherReason == null ? "" : otherReason.toUpperCase ()));
		encounters.add (otrReasonResult);

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
				return XmlUtil.createErrorXml ("ERROR: Please try again");
			}
		}
		/*
		 * boolean resultSave = true;
		 * 
		 * for (int i = 0; i < encounters.size(); i++) { try {
		 * System.out.println(i); resultSave =
		 * ssl.saveEncounterResults(encounters.get(i)); } catch (Exception e1) {
		 * e1.printStackTrace(); break; }
		 * 
		 * if (!resultSave) { return
		 * XmlUtil.createErrorXml("ERROR! Please try again"); }
		 * 
		 * }
		 */

		// send incentive and alerts
		//

		/*
		 * boolean isCured = false; boolean isTreatmentCompleted = false;
		 * 
		 * 
		 * if(reason.equals("Cured")) isCured = true; else
		 * if(reason.equals("Tx Completed")) isTreatmentCompleted = true;
		 * 
		 * 
		 * 
		 * 
		 * if (isCured || isTreatmentCompleted) {
		 * 
		 * 
		 * IncentiveId gpIncentiveId = new IncentiveId(); IncentiveId
		 * chwIncentiveId = new IncentiveId();
		 * 
		 * gpIncentiveId.setPid(gpId); chwIncentiveId.setPid(pat.getChwid());
		 * 
		 * if(isCured) { gpIncentiveId.setIncentiveId("GP_CURED");
		 * chwIncentiveId.setIncentiveId("CHW_CURED"); }
		 * 
		 * else if(isTreatmentCompleted) {
		 * gpIncentiveId.setIncentiveId("GP_TX_CMP");
		 * chwIncentiveId.setIncentiveId("CHW_TX_CMP"); }
		 * 
		 * Incentive gpIncent = new Incentive(); gpIncent.setId(gpIncentiveId);
		 * 
		 * Incentive chwIncent = new Incentive();
		 * chwIncent.setId(chwIncentiveId);
		 * 
		 * try { ssl.saveIncentive(gpIncent); }
		 * 
		 * catch (Exception ex) { ex.printStackTrace();
		 * System.out.println("Could not add END FOLLOWUP " + reason +
		 * " incentive for GP: " + gpId + "/Patient " + id); }
		 * 
		 * try { ssl.saveIncentive(chwIncent); }
		 * 
		 * catch(Exception ex2) { ex2.printStackTrace();
		 * System.out.println("Could not add END FOLLOWUP " + reason +
		 * " incentive for CHW: " + pat.getChwid() + "/Patient " + id); }
		 * 
		 * 
		 * //send alerts SetupIncentive gpIncentive = null; SetupIncentive
		 * chwIncentive = null;
		 * 
		 * try { if (isCured) { gpIncentive =
		 * ssl.findSetupIncentive("GP_CURED"); chwIncentive =
		 * ssl.findSetupIncentive("CHW_CURED"); }
		 * 
		 * else if (isTreatmentCompleted) { gpIncentive =
		 * ssl.findSetupIncentive("GP_TX_CMP"); chwIncentive =
		 * ssl.findSetupIncentive("CHW_TX_CMP"); }
		 * 
		 * 
		 * ssl.sendAlertsOnEndFollowUp(encId, gpIncentive, chwIncentive,
		 * isCured, isTreatmentCompleted); }
		 * 
		 * catch(Exception e7) { e7.printStackTrace(); System.out.println(new
		 * Date(System.currentTimeMillis()).toString() +
		 * ": Could not send GP Confirmation incentive for Encounter: " +
		 * encId.getEncounterId() + "|" + encId.getPid1() + "|" +
		 * encId.getPid2()); } }
		 */

		return XmlUtil.createSuccessXml ();
	}

	private String doFollowupTreatment ()
	{

		String id = request.getParameter ("id");
		String chwId = request.getParameter ("chwid");
		String labid = request.getParameter ("labid");
		// String gpId = request.getParameter("gpid");

		// String height = request.getParameter("ht");
		String weight = request.getParameter ("wt");
		String phase = request.getParameter ("pt");
		String pregnant = request.getParameter ("pregn");

		String month = request.getParameter ("mon");
		String regimen = request.getParameter ("reg");
		String tablets = request.getParameter ("tab");
		String streptoDose = request.getParameter ("str");

		String haemp = request.getParameter ("hemp");
		String cough = request.getParameter	("coug");
		String fever = request.getParameter ("fev");
		String nightsweats = request.getParameter ("ns");
		String weightloss = request.getParameter ("wl");

		String startDate = request.getParameter ("sd");
		String startTime = request.getParameter ("st");
		String endTime = request.getParameter ("et");
		String enteredDate = request.getParameter ("ed");

		if (month == null)
		{
			return XmlUtil.createErrorXml ("Month not found with request. Phone update karain aur dobara form save karain");
		}

		Patient pat = null;
		try
		{
			pat = ssl.findPatient (id);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}

		if (pat == null)
		{
			return XmlUtil.createErrorXml ("Patient with id " + id + " does not exist. Please recheck ID and try again.");
		}

		// begin dup followup check
		String results[] = null;

		try
		{
			results = ssl.getColumnData ("encounter", "EncounterID", " where PID1='" + id + "'" + " AND EncounterType='" + EncounterType.FOLLOW_UP_VISIT + "'");
		}
		catch (Exception e2)
		{
			e2.printStackTrace ();
		}

		String filledMonth = "";
		String er[] = null;
		if (results != null)
		{
			for (int i = 0; i < results.length; i++)
			{
				System.out.println ((String) (results[i]));
			}

			for (int i = 0; i < results.length; i++)
			{

				try
				{
					er = ssl.getColumnData ("encounterresults", "Value", " where PID1='" + id + "' " + " AND Element='MONTH' " + " AND EncounterID='" + (String) (results[i]) + "'");
				}
				catch (Exception e)
				{
					e.printStackTrace ();
				}

				if (er != null && er.length > 0)
				{
					filledMonth = er[0];
					if (filledMonth != null && filledMonth.equalsIgnoreCase (month))
					{
						return XmlUtil.createErrorXml ("Patient " + id + " ka month " + month
								+ " ka Follow Up Form bhara ja chuka hai. Agar form bharnay mein koi ghalati huwi hai to TB Reach team se rujoo karain");
					}
				}
			}
		}

		// end dup Followup check

		// pat.setHeight(Float.parseFloat(height));
		pat.setWeight (Float.parseFloat (weight));
		pat.setTreatmentPhase (phase.toUpperCase ());

		pat.setRegimen (regimen.toUpperCase ());
		pat.setDoseCombination (Float.parseFloat (tablets));

		int streptoInt = 0;
		if (!StringUtils.isEmptyOrWhitespaceOnly (streptoDose))
		{
			try
			{
				streptoInt = Integer.parseInt (streptoDose);
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace ();
				return XmlUtil.createErrorXml ("Invalid Strepto Dose. Please contact tbreach team");
			}
		}

		pat.setStreptomycin (streptoInt);

		Boolean isupdated = null;
		try
		{
			isupdated = ssl.updatePatient (pat);
		}
		catch (Exception e2)
		{
			e2.printStackTrace ();
		}

		if (isupdated == null || !isupdated)
		{
			return XmlUtil.createErrorXml ("Could not update Patient. Please try again.");
		}

		Date encounterdateentered = null;
		try
		{
			encounterdateentered = DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.FE_FORMAT);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			return XmlUtil.createErrorXml ("Bad entered date. Please try again");
		}

		EncounterId encId = new EncounterId (0, id.toUpperCase (), chwId.toUpperCase ());

		Encounter e = new Encounter (encId, EncounterType.FOLLOW_UP_VISIT, labid, encounterdateentered, encounterStartDate, encounterEndDate, "");

		try
		{
			ssl.saveEncounter (e);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			XmlUtil.createErrorXml ("Could not save encounter! Please try again!");
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults> ();

		EncounterResults dateResult = ModelUtil.createEncounterResult (e, "entered_date".toUpperCase (), enteredDate);
		encounters.add (dateResult);

		/*
		 * EncounterResults heightResult = ModelUtil.createEncounterResult(e,
		 * "height".toUpperCase(), height); encounters.add(heightResult);
		 */

		EncounterResults weightResult = ModelUtil.createEncounterResult (e, "weight".toUpperCase (), weight);
		encounters.add (weightResult);

		EncounterResults pregnantResult = ModelUtil.createEncounterResult (e, "pregnant".toUpperCase (), (pregnant == null ? "" : pregnant.toUpperCase ()));
		encounters.add (pregnantResult);

		EncounterResults phaseResult = ModelUtil.createEncounterResult (e, "treatment_phase".toUpperCase (), phase.toUpperCase ());
		encounters.add (phaseResult);

		EncounterResults monthResult = ModelUtil.createEncounterResult (e, "month".toUpperCase (), month.toUpperCase ());
		encounters.add (monthResult);

		EncounterResults regimenResult = ModelUtil.createEncounterResult (e, "regimen".toUpperCase (), regimen.toUpperCase ());
		encounters.add (regimenResult);

		EncounterResults tabletResult = ModelUtil.createEncounterResult (e, "fdc_tablets".toUpperCase (), tablets.toUpperCase ());
		encounters.add (tabletResult);

		EncounterResults streptoResult = ModelUtil.createEncounterResult (e, "strepomycin".toUpperCase (), (streptoDose == null ? "" : streptoDose.toUpperCase ()));
		encounters.add (streptoResult);

		EncounterResults coughResult = ModelUtil.createEncounterResult (e, "heamptosis".toUpperCase (), haemp.toUpperCase ());
		encounters.add (coughResult);
		
		EncounterResults coughWeekResult = ModelUtil.createEncounterResult (e, "cough".toUpperCase (), cough.toUpperCase ());
		encounters.add (coughWeekResult);

		EncounterResults feverResult = ModelUtil.createEncounterResult (e, "fever".toUpperCase (), fever.toUpperCase ());
		encounters.add (feverResult);

		EncounterResults weightlossResult = ModelUtil.createEncounterResult (e, "weight_loss".toUpperCase (), weightloss.toUpperCase ());
		encounters.add (weightlossResult);

		EncounterResults nightsweatsResult = ModelUtil.createEncounterResult (e, "night_sweats".toUpperCase (), nightsweats.toUpperCase ());
		encounters.add (nightsweatsResult);

		EncounterResults[] encResults = new EncounterResults[] {};
		encResults = encounters.toArray (encResults);

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
				return XmlUtil.createErrorXml ("ERROR: Please try again");
			}
		}
		// TODO: Send Alert/Incentive
		/*
		 * IncentiveId gpIncentive = new IncentiveId();
		 * gpIncentive.setPid(gpId); gpIncentive.setIncentiveId("GP_FU_VST");
		 * 
		 * Incentive gpInc = new Incentive(); gpInc.setId(gpIncentive);
		 * 
		 * 
		 * try { ssl.saveIncentive(gpInc); } catch (Exception e1) {
		 * 
		 * e1.printStackTrace(); System.out.println(
		 * "ERROR: Could not save Followup Visit Incentive for GP " + gpId +
		 * "/ Patient " + id); }
		 * 
		 * 
		 * SetupIncentive gpVisitSetupIncentive = null;
		 * 
		 * try { gpVisitSetupIncentive = ssl.findSetupIncentive("GP_FU_VST"); }
		 * catch (Exception e1) { e1.printStackTrace(); }
		 * 
		 * 
		 * ssl.sendAlertsOnGPVisit(e, gpVisitSetupIncentive);
		 */

		return XmlUtil.createSuccessXml ();
	}

	private String doGeneXpertOrder ()
	{
		String id = request.getParameter ("id");
		String chwid = request.getParameter ("chwid");
		String labid = request.getParameter ("labid");
		String labbarcode = request.getParameter ("lbc");

		String gxpTest = request.getParameter ("gxptest");

		String startDate = request.getParameter ("sd");
		String startTime = request.getParameter ("st");
		String endTime = request.getParameter ("et");
		String enteredDate = request.getParameter ("ed");

		System.out.println (startDate);
		System.out.println (startTime);
		System.out.println (endTime);
		System.out.println (enteredDate);

		Patient pt = null;

		try
		{
			pt = ssl.findPatient (id);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
		if (pt == null)
		{
			return XmlUtil.createErrorXml ("Patient with id " + id + " does not exist. Please recheck ID and try again.");
		}

		SputumResults sr = null;
		try
		{
			sr = ssl.findSputumResultsBySputumLabID (labid + labbarcode);
		}
		catch (Exception e2)
		{
			e2.printStackTrace ();
		}
		if (sr == null)
		{
			return XmlUtil.createErrorXml ("Sputum Sample with given LabId + LabBarcode was not found. Please recheck values and try again");
		}

/*		if (sr.getGeneXpertOrderDate () != null || !StringUtils.isEmptyOrWhitespaceOnly (sr.getGeneXpertResult ()))
		{
			return XmlUtil.createErrorXml ("GeneXpert on given sample have already been ordered.\nDetails are:" + "\nPatientId: " + sr.getPatientId () + "\nGeneXpertOrderDate: "
					+ sr.getGeneXpertOrderDate () + "\nGeneXpertResult: " + sr.getGeneXpertResult () + "\nGeneXpertResistance: " + sr.getGeneXpertResistance () + "\nMonth: " + sr.getMonth ()
					+ "\nGeneXpertOrderedBy: " + sr.getGeneXpertOrderedBy ());
		}
*/
		String results[] = null;

		try
		{
			results = ssl.getColumnData ("encounter", "EncounterID", " where PID1='" + id + "'" + " AND EncounterType='" + EncounterType.GENEXPERT_ORDER + "' ");
		}
		catch (Exception e2)
		{
			e2.printStackTrace ();
			return XmlUtil.createErrorXml ("Error tracking Patient ID. Try again");
		}

		if (results != null && results.length > 0)
		{
			return XmlUtil.createErrorXml ("Patient " + id + " ka Genexpert Form bhara ja chuka hai. Agar form bharnay mein koi ghalati huwi hai to TB Reach team se rujoo karain");
		}
		/*
		 * String er[] = null; if(results != null) { for(int i=0;
		 * i<results.length;i++) { System.out.println((String)(results[i])); }
		 * 
		 * for(int i=0; i<results.length;i++) {
		 * 
		 * try { er = ssl.getColumnData("encounterresults" , "Value" ,
		 * " where PID1='"+ id + "' " + " AND Element='LAB_BARCODE' " +
		 * " AND EncounterID='" + (String)(results[i]) + "'"); } catch
		 * (Exception e) { e.printStackTrace(); } String encLabbarcode =
		 * (er==null || er.length==0)? "" : er[0]; try { er =
		 * ssl.getColumnData("encounterresults" , "Value" , " where PID1='"+ id
		 * + "' " + " AND Element='LAB_ID' " + " AND EncounterID='" +
		 * (String)(results[i]) + "'"); } catch (Exception e) {
		 * e.printStackTrace(); } String encLabId = (er==null || er.length==0) ?
		 * "" : er[0];
		 * 
		 * if(encLabbarcode != null && encLabId != null &&
		 * encLabbarcode.equalsIgnoreCase(labbarcode) &&
		 * encLabId.equalsIgnoreCase(labid)) { return
		 * XmlUtil.createErrorXml("Patient " + id +
		 * " kay Lab Id "+labid+" kay Sputum Barcode "+labbarcode+
		 * " ka Genexpert Form bhara ja chuka hai. Agar form bharnay mein koi ghalati huwi hai to TB Reach team se rujoo karain"
		 * ); } } }
		 */

		// sr.setSputumTestId(Integer.parseInt(barCode));

//		sr.setGeneXpertOrderedBy (chwid.toUpperCase ());

		if (gxpTest.equalsIgnoreCase ("yes"))
		{
/*			try
			{
//				sr.setGeneXpertOrderDate (DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.FE_FORMAT));
			}
			catch (ParseException e1)
			{
				e1.printStackTrace ();
				return XmlUtil.createErrorXml ("Error occurred. Please try again");
			}*/
		}

		Boolean isUpdated = null;

		try
		{
			isUpdated = ssl.update (sr);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}

		if (isUpdated == null || !isUpdated)
		{
			return XmlUtil.createErrorXml ("Error occurred while updating. Please try again");
		}

		EncounterId encId = new EncounterId (0, id.toUpperCase (), chwid.toUpperCase ());

		Date encounterdateentered = null;
		try
		{
			encounterdateentered = DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.FE_FORMAT);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			return XmlUtil.createErrorXml ("Bad entered date. Please try again");
		}

		Encounter e = new Encounter (encId, EncounterType.GENEXPERT_ORDER, labid, encounterdateentered, encounterStartDate, encounterEndDate, "");

		try
		{
			ssl.saveEncounter (e);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			XmlUtil.createErrorXml ("Could not save encounter! Please try again!");
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults> ();

		EncounterResults dateResult = ModelUtil.createEncounterResult (e, "entered_date".toUpperCase (), enteredDate);
		encounters.add (dateResult);

		EncounterResults labbarcodeResult = ModelUtil.createEncounterResult (e, "lab_barcode".toUpperCase (), (labbarcode).toUpperCase ());
		encounters.add (labbarcodeResult);

		EncounterResults labidResult = ModelUtil.createEncounterResult (e, "lab_id".toUpperCase (), labid.toUpperCase ());
		encounters.add (labidResult);

		EncounterResults gxpTestResult = ModelUtil.createEncounterResult (e, "gxp_order".toUpperCase (), gxpTest.toUpperCase ());
		encounters.add (gxpTestResult);

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
				return XmlUtil.createErrorXml ("ERROR: Please try again");
			}
		}

		// TODO: Send Alert/Incentive

		return XmlUtil.createSuccessXml ();
	}

	private String doGPRegistration ()
	{
		String gpidEntered = request.getParameter ("gpid");

		String chwId = request.getParameter ("chwid");
		// String labid = request.getParameter("labid");

		String firstName = request.getParameter ("gpfname");
		String fnameCleaned = firstName;
		fnameCleaned = fnameCleaned.toLowerCase ().replace ("dr ", "").trim ();
		fnameCleaned = (fnameCleaned.toLowerCase ().replace ("dr.", "")).trim ().toUpperCase ();

		String lastName = request.getParameter ("gplname");

		String idFnameStr = fnameCleaned.replaceAll ("[^\\w\\s\\.]+", "");
		idFnameStr = idFnameStr.replace (" ", "_");
		idFnameStr = idFnameStr.replace (".", "_");
		while (idFnameStr.indexOf ("__") != -1)
		{
			idFnameStr = idFnameStr.replace ("__", "_");
		}

		idFnameStr = (idFnameStr.length () <= 5 ? idFnameStr : idFnameStr.substring (0, 5));
		// TODO how to deal with GP ids-
		String idinit = "G-" + idFnameStr + "-";
		String gpIdComplete = idinit + gpidEntered;
		
		String chestSpc = request.getParameter ("chestsp");
		String chestSpcSpecified = request.getParameter ("chestspspec");
		String gpSpecializtion = "";
		if (chestSpc.equalsIgnoreCase ("yes"))
		{
			gpSpecializtion = "Pulmonologist";
		}
		else
		{
			gpSpecializtion = chestSpcSpecified;
		}

		String practiceName = request.getParameter ("pracname");
		String labpreferedname = request.getParameter ("labprefname");
		String labpreferedid = request.getParameter ("labprefid");

		String latitude = request.getParameter ("lat");
		String longitude = request.getParameter ("lng");

		String phone = request.getParameter ("phn");
		String addhouseNumber = request.getParameter ("hn");
		String addstreetName = request.getParameter ("sn");
		String addsectorName = request.getParameter ("sec");
		String addcolonyName = request.getParameter ("cn");
		String addtownName = request.getParameter ("tn");
		String addlandmark = request.getParameter ("lm");
		String adduc = request.getParameter ("uc");

		String startDate = request.getParameter ("sd");
		String startTime = request.getParameter ("st");
		String endTime = request.getParameter ("et");
		String enteredDate = request.getParameter ("ed");

		System.out.println (startDate);
		System.out.println (startTime);
		System.out.println (endTime);
		System.out.println (enteredDate);

		// Transaction t = HibernateUtil.util.getSession().beginTransaction();
		Boolean isExists = null;
		try
		{
			isExists = ssl.exists ("person", " PID like LOWER('%-" + gpidEntered.trim ().toLowerCase () + "') " + " and RoleInSystem='GP'");
		}
		catch (Exception e2)
		{
			e2.printStackTrace ();
		}

		if (isExists == null)
		{
			return XmlUtil.createErrorXml ("Error while verifying GP ID. Please try again.");
		}

		if (isExists)
		{
			return XmlUtil.createErrorXml ("A GP with given ID already exists");
		}

		String[] similarIds = null;
		try
		{
			similarIds = ssl.getColumnData ("person", "PID", " FirstName= LOWER('" + fnameCleaned.trim ().toLowerCase () + "') " + " and LastName=LOWER('" + lastName.toLowerCase () + "') "
					+ " and PID like LOWER('" + idinit.toLowerCase () + "%') " + " and RoleInSystem='GP'");
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
		}
		for (String id : similarIds)
		{// also check in contact
			try
			{
				if (ssl.exists ("gp", " WorkplaceID=LOWER('" + practiceName.toLowerCase () + "') " + " and Specialization=LOWER('" + gpSpecializtion.toLowerCase () + "') "))
				{
					if (ssl.exists ("contact", " PID='" + id + "' " + " AND Phone='" + phone + "' "))
					{
						return XmlUtil.createErrorXml ("GP with similar demographics already exists with ID:" + id + "\nDetails are:" + "\nFirst name:" + fnameCleaned + "\nLast name :"
								+ lastName.trim () + "\nPractice name:" + practiceName + "\nSpecialization:" + gpSpecializtion + "\nPhone:" + phone);
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace ();
			}
		}

		Person p = new Person (gpidEntered.toUpperCase (), fnameCleaned.trim ().toUpperCase (), ' ');
		p.setLastName (lastName.trim ().toUpperCase ());
		p.setRoleInSystem ("GP");

		// p.setGender(sex.toUpperCase().charAt(0));
		// p.setDob(dob);

		/*
		 * boolean pCreated = true; try { pCreated = ssl.savePerson(p); } catch
		 * (Exception e) { // Auto-generated catch block e.printStackTrace(); }
		 * 
		 * if (!pCreated) { return XmlUtil
		 * .createErrorXml("Could not create Person. Please try again"); }
		 */

		Contact contact = new Contact ();
		contact.setAddressColony (addcolonyName.toUpperCase ());
		contact.setAddressHouse (addhouseNumber.toUpperCase ());
		contact.setAddressLandMark (addlandmark.toUpperCase ());
		contact.setAddressSector (addsectorName.toUpperCase ());
		contact.setAddressStreet (addstreetName.toUpperCase ());
		contact.setAddressTown (addtownName.toUpperCase ());
		contact.setAddressUc (adduc.toUpperCase ());
		// contact.setCity( city );
		// contact.setCountry( country );
		// contact.setEmail( email );
		// contact.setIrdstructureNo( irdstructureNo );
		try
		{
			contact.setLocationLat (Float.parseFloat (latitude));
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
		try
		{
			contact.setLocationLon (Float.parseFloat (longitude));
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
		// contact.setMobile( mobile );
		// contact.setMeterNo( meterNo );
		// contact.setMobileOwner( mobileOwner );
		contact.setPhone (phone);
		// contact.setPhoneOwner( phoneOwner );
		contact.setPid (gpidEntered.toUpperCase ());
		// contact.setRegion( region );

/*		Users user = new Users (gpidEntered.toUpperCase (), gpidEntered.toUpperCase (), "GP", "ACTIVE", gpidEntered.toLowerCase () // lower
																																		// as
																																		// it
																																		// is
																																		// password
				, TBR.getSecretQuestions ()[0], "M.A. Jinah");
*/
		// TODO gpid by counting n then assigning
		// Long gpcnt = ssl.count("gp", "");
		Gp gp = new Gp ();
		try
		{
			gp.setDateRegistered (DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.FE_FORMAT));
		}
		catch (ParseException e)
		{
			e.printStackTrace ();
		}
		gp.setGpid (gpidEntered.toUpperCase ());
		// gp.setQualification(qualification);
		gp.setSpecialization (gpSpecializtion.toUpperCase ());
		gp.setWorkplaceId (practiceName.toUpperCase ());

		// TODO ask OVAIS to throw exception and then send its error as message
		/*if (!ssl.saveGP (gp, p, contact, user))
		{
			return XmlUtil.createErrorXml ("GP not saved. Try again");
		}*/

		try
		{
			if (!ssl.exists ("location", "LocationType='LABORATORY' and LocationID='" + labpreferedid.toUpperCase () + "'"))
			{
				return XmlUtil.createErrorXml ("Lab selected as prefered was not found in database. Please enter a valid lab.");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}

		LabMappingId labmapid = new LabMappingId ( /* TODO */labpreferedid.toUpperCase (), gpidEntered.toUpperCase ());
		LabMapping labmap = new LabMapping (labmapid, "GP");

		Boolean islabSaved = null;
		try
		{
			islabSaved = ssl.saveLabMapping (labmap);
		}
		catch (Exception e3)
		{
			e3.printStackTrace ();
		}

		if (islabSaved == null || !islabSaved)
		{
			return XmlUtil.createErrorXml ("Lab For GP was not mapped. Please try again");
		}

		Date encounterdateentered = null;
		try
		{
			encounterdateentered = DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.FE_FORMAT);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			return XmlUtil.createErrorXml ("Bad entered date. Please try again");
		}

		EncounterId encId = new EncounterId (0, gpidEntered.toUpperCase (), chwId.toUpperCase ());

		Encounter e = new Encounter (encId, EncounterType.GP_REGISTRATION, ""// labid
				, encounterdateentered, encounterStartDate, encounterEndDate, "");

		try
		{
			ssl.saveEncounter (e);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			XmlUtil.createErrorXml ("Could not save encounter! Please try again!");
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults> ();

		EncounterResults dateResult = ModelUtil.createEncounterResult (e, "entered_date".toUpperCase (), enteredDate);
		encounters.add (dateResult);

		EncounterResults gpidResult = ModelUtil.createEncounterResult (e, "gp_id".toUpperCase (), gpidEntered.toUpperCase ());
		encounters.add (gpidResult);

		EncounterResults firstNameResult = ModelUtil.createEncounterResult (e, "first_name".toUpperCase (), firstName.toUpperCase ());
		encounters.add (firstNameResult);

		EncounterResults lastNameResult = ModelUtil.createEncounterResult (e, "last_name".toUpperCase (), lastName.toUpperCase ());
		encounters.add (lastNameResult);

		EncounterResults pillnumResult = ModelUtil.createEncounterResult (e, "chest_specialist".toUpperCase (), chestSpc.toUpperCase ());
		encounters.add (pillnumResult);

		EncounterResults pillleftResult = ModelUtil.createEncounterResult (e, "chest_specialist_other".toUpperCase (), (chestSpcSpecified == null ? "" : chestSpcSpecified.toUpperCase ()));
		encounters.add (pillleftResult);

		EncounterResults practiceNameResult = ModelUtil.createEncounterResult (e, "gp_practice_name".toUpperCase (), practiceName.toUpperCase ());
		encounters.add (practiceNameResult);

		EncounterResults phone1Result = ModelUtil.createEncounterResult (e, "phone".toUpperCase (), phone.toUpperCase ());
		encounters.add (phone1Result);

		/*
		 * EncounterResults whosephone1Result =
		 * ModelUtil.createEncounterResult(e, "phone1_owner".toUpperCase(),
		 * whosephone1.toUpperCase()); encounters.add(whosephone1Result);
		 * 
		 * EncounterResults phone2Result = ModelUtil.createEncounterResult(e,
		 * "phone2".toUpperCase(), phone2); encounters.add(phone2Result);
		 * 
		 * EncounterResults whosephone2Result =
		 * ModelUtil.createEncounterResult(e, "phone2_owner".toUpperCase(),
		 * whosephone2.toUpperCase()); encounters.add(whosephone2Result);
		 */

		EncounterResults addhouseResult = ModelUtil.createEncounterResult (e, "add_house".toUpperCase (), addhouseNumber.toUpperCase ());
		encounters.add (addhouseResult);

		EncounterResults addstreetResult = ModelUtil.createEncounterResult (e, "add_street".toUpperCase (), addstreetName.toUpperCase ());
		encounters.add (addstreetResult);

		EncounterResults addsectorResult = ModelUtil.createEncounterResult (e, "add_sector".toUpperCase (), addsectorName.toUpperCase ());
		encounters.add (addsectorResult);

		EncounterResults addcolonyResult = ModelUtil.createEncounterResult (e, "add_colony".toUpperCase (), addcolonyName.toUpperCase ());
		encounters.add (addcolonyResult);

		EncounterResults addtownResult = ModelUtil.createEncounterResult (e, "add_town".toUpperCase (), addtownName.toUpperCase ());
		encounters.add (addtownResult);

		EncounterResults adducResult = ModelUtil.createEncounterResult (e, "add_uc".toUpperCase (), adduc.toUpperCase ());
		encounters.add (adducResult);

		EncounterResults addlandmarkResult = ModelUtil.createEncounterResult (e, "add_landmark".toUpperCase (), addlandmark.toUpperCase ());
		encounters.add (addlandmarkResult);

		EncounterResults latitudeResult = ModelUtil.createEncounterResult (e, "lat".toUpperCase (), latitude.toUpperCase ());
		encounters.add (latitudeResult);

		EncounterResults longitudeResult = ModelUtil.createEncounterResult (e, "long".toUpperCase (), longitude.toUpperCase ());
		encounters.add (longitudeResult);

		EncounterResults labpreferednameResult = ModelUtil.createEncounterResult (e, "lab_prefered".toUpperCase (), labpreferedid.toUpperCase ());
		encounters.add (labpreferednameResult);

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
				return XmlUtil.createErrorXml ("ERROR: Please try again");
			}
		}

		return XmlUtil.createSuccessXml ();
	}
	
	private String doGPFollowup ()
	{
		String gpidEntered = request.getParameter ("gpid");

		String chwId = request.getParameter ("chwid");
		// String labid = request.getParameter("labid");

		String firstName = request.getParameter ("gpfname");
		String lastName = request.getParameter ("gplname");

		String latitude = request.getParameter ("lat");
		String longitude = request.getParameter ("lng");

		String startDate = request.getParameter ("sd");
		String startTime = request.getParameter ("st");
		String endTime = request.getParameter ("et");
		String enteredDate = request.getParameter ("ed");

		System.out.println (startDate);
		System.out.println (startTime);
		System.out.println (endTime);
		System.out.println (enteredDate);

		// Transaction t = HibernateUtil.util.getSession().beginTransaction();
		Boolean isExists = null;
		try
		{
			//isExists = ssl.exists ("person", " substring(PID,-4) = '"+gpidEntered+"' and RoleInSystem='GP'");
			isExists = ssl.exists ("gp", " GPID = '"+gpidEntered+"'");
		}
		catch (Exception e2)
		{
			e2.printStackTrace ();
		}

		if (isExists == null)
		{
			return XmlUtil.createErrorXml ("Error while finding GP ID. Please try again.");
		}

		if (!isExists)
		{
			return XmlUtil.createErrorXml ("Person not found.");
		}

		Boolean results = null;
		try
		{
			results = ssl.exists ("encounter",
					" where PID1='" + gpidEntered + "'" + " AND EncounterType='" + EncounterType.GP_FOLLOWUP + "'" + " AND DateEncounterEntered='"
							+ DateTimeUtil.convertToSQL (enteredDate, DateTimeUtil.FE_FORMAT) + "'" + " ORDER BY DateEncounterEntered DESC");
		}
		catch (Exception e3)
		{
			e3.printStackTrace ();
		}

		if (results != null && results)
		{
			return XmlUtil.createErrorXml (enteredDate + " per di gai GP ID ka  ek GP Follow up Form bhara ja chuka hay . Agar aap say Form bharnay may koi ghalti hui hay tu TBReach team say rujoo karen.");
		}
		
		Date encounterdateentered = null;
		try
		{
			encounterdateentered = DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.FE_FORMAT);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			return XmlUtil.createErrorXml ("Bad entered date. Please try again");
		}

		EncounterId encId = new EncounterId (0, gpidEntered.toUpperCase (), chwId.toUpperCase ());

		Encounter e = new Encounter (encId, EncounterType.GP_FOLLOWUP, ""// labid
				, encounterdateentered, encounterStartDate, encounterEndDate, "");
		try
		{
			ssl.saveEncounter (e);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			XmlUtil.createErrorXml ("Could not save encounter! Please try again!");
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults> ();

		EncounterResults dateResult = ModelUtil.createEncounterResult (e, "entered_date".toUpperCase (), enteredDate);
		encounters.add (dateResult);

		EncounterResults gpidResult = ModelUtil.createEncounterResult (e, "gp_id".toUpperCase (), gpidEntered.toUpperCase ());
		encounters.add (gpidResult);

		EncounterResults firstNameResult = ModelUtil.createEncounterResult (e, "first_name".toUpperCase (), firstName.toUpperCase ());
		encounters.add (firstNameResult);

		EncounterResults lastNameResult = ModelUtil.createEncounterResult (e, "last_name".toUpperCase (), lastName.toUpperCase ());
		encounters.add (lastNameResult);

		EncounterResults latitudeResult = ModelUtil.createEncounterResult (e, "lat".toUpperCase (), latitude.toUpperCase ());
		encounters.add (latitudeResult);

		EncounterResults longitudeResult = ModelUtil.createEncounterResult (e, "long".toUpperCase (), longitude.toUpperCase ());
		encounters.add (longitudeResult);

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
				return XmlUtil.createErrorXml ("ERROR: Please try again");
			}
		}

		return XmlUtil.createSuccessXml ();
	}

	private String doLogin ()
	{
		String xml = null;

		String username = request.getParameter ("username");
		String password = request.getParameter ("password");
		String role = "";
		String pid = "";
		String labid = "";
		String status = "";
		// START TIME CHECK
		String phoneTime = request.getParameter ("phoneTime");

		Date phoneDate = null;
		try
		{
			phoneDate = DateTimeUtil.getDateFromString (phoneTime, DateTimeUtil.FE_FORMAT);
		}
		catch (ParseException e1)
		{
			e1.printStackTrace ();
			return XmlUtil.createErrorXml ("Bad date format. Contact Program team");
		}

		DateTime serverdt = new DateTime();
		final long phoneMillis = phoneDate.getTime();
		final long currentMillis = serverdt.getMillis();
		final long milliDiff = Math.abs (phoneMillis - currentMillis);

		System.out.println ("phonedate: " + phoneDate.toString () +":"+phoneMillis);
		System.out.println ("serverdate: " + serverdt.toString ()+":"+currentMillis);
		System.out.println ("diff: "+milliDiff);

		// long milliDiff = Math.abs(i.toDuration().getMillis());
		final double secDiff = milliDiff / 1000;
		final double hrDiff = secDiff / 3600;

		System.out.println ("hrdiff: "+hrDiff);

		if (hrDiff > 0.25)
		{
			//return XmlUtil.createErrorXml ("Aap ke phone par date ya time ghalat hai. Sahih date aur time set kar ke phir koshish karain");
			return XmlUtil.tajikErrorXml ("TajikError");
		}

		if (ssl.authenticate (username, password))
		{

			try
			{
				Users users = ssl.findUser (username);
				pid = users.getPid ();
				role = users.getRole ();
				status = users.getStatus();
				
			}
			catch (Exception e)
			{
				e.printStackTrace ();
				//return XmlUtil.createErrorXml ("Воридшави нодуруст аст. Такрор кунед.");
				return XmlUtil.tajikErrorLoggingXml ("ErrorLogging");
			}
			
			if(status.equalsIgnoreCase("suspended"))
			{
				
				return XmlUtil.tajikAccountSuspendedXml ("AccountSuspended");
			}
		}
		else
		{
			
			return XmlUtil.invalidUserXml ("InvalidUser");
		}

		LabMapping lab = null;
		try
		{
			lab = ssl.findMappingByPerson (pid);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
		}
		// lab should not be mendatory here as person not mapped on lab will
		// fail to login like admin etc
		if (lab != null)
		{
			labid = lab.getId ().getLocationId ();
		}

		//ssl.recordLogin (username);

		Document doc = null;

		try
		{
			doc = DocumentBuilderFactory.newInstance ().newDocumentBuilder ().newDocument ();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace ();
			return "";
		}
		Element responseNode = doc.createElement (XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element statusNode = doc.createElement ("role");
		Text statusValue = doc.createTextNode (role.toUpperCase ());
		statusNode.appendChild (statusValue);

		responseNode.appendChild (statusNode);

		Element uidNode = doc.createElement ("uid");
		Text uidValue = doc.createTextNode (pid);
		uidNode.appendChild (uidValue);

		responseNode.appendChild (uidNode);

		Element labidNode = doc.createElement ("labid");
		Text labidValue = doc.createTextNode (labid);
		labidNode.appendChild (labidValue);

		responseNode.appendChild (labidNode);

		doc.appendChild (responseNode);

		xml = XmlUtil.docToString (doc);

		return xml;
	}

	public String doLogNonSuspectScreening ()
	{

		String xml = null;

		String chwId = request.getParameter ("chwid");
		String labid = request.getParameter ("labid");
		String labtest = request.getParameter ("labtest");
		String labother = request.getParameter ("labother");
		// String gpId = request.getParameter("gpid");
		// String firstName = request.getParameter("fn");
		// String lastName = request.getParameter("ln");
		String sex = request.getParameter ("sex");
		String age = request.getParameter ("age");
		String cough = request.getParameter ("cough");
		String coughDuration = request.getParameter ("cd");
		if (coughDuration != null)
			coughDuration = coughDuration.toUpperCase ();
		String productiveCough = request.getParameter ("pc");
		if (productiveCough != null)
			productiveCough = productiveCough.toUpperCase ();
		String tbHistory = request.getParameter ("tbh");
		String tbMedication = request.getParameter ("tbmed");
		String tbTreatment = request.getParameter ("tbtrt");
		String tbFamilyHistory = request.getParameter ("ftbh");

		// these questions will not be asked to a Non Suspect..
		/*
		 * String fever = request.getParameter("fev"); if(fever!=null) { fever =
		 * fever.toUpperCase(); } String nightSweat =
		 * request.getParameter("ns"); if(nightSweat!=null) { nightSweat =
		 * nightSweat.toUpperCase(); } String weightLoss =
		 * request.getParameter("wl"); if(weightLoss!=null) { weightLoss =
		 * weightLoss.toUpperCase(); } String haemoptysis =
		 * request.getParameter("ha"); if(haemoptysis!=null) { haemoptysis =
		 * haemoptysis.toUpperCase(); }
		 */
		String startDate = request.getParameter ("sd");
		String startTime = request.getParameter ("st");
		String endTime = request.getParameter ("et");
		String enteredDate = request.getParameter ("ed");
		String conclusion = request.getParameter ("conc");
		System.out.println (startDate);
		System.out.println (startTime);
		System.out.println (endTime);
		System.out.println (enteredDate);

		// Transaction t = HibernateUtil.util.getSession().beginTransaction();
		if (cough == null)
		{
			return XmlUtil.createErrorXml ("Phone update karain aur dobara form save karain");
		}

		Date dob = getDOBFromAge (Integer.parseInt (age));
		java.sql.Date sqd = new java.sql.Date (dob.getTime ());

		System.out.println ("SQL TOSTRING-> " + sqd.toString ());

		Boolean exists = null;

		// TODO how to check it as we have no identification params ????

		Screening scr = new Screening ();
		scr.setAge (Integer.parseInt (age));
		scr.setChwid (chwId.toUpperCase ());
		scr.setCough (cough.toUpperCase ());
		if (coughDuration != null || productiveCough != null)
		{
			scr.setCoughDuration (coughDuration.toUpperCase ());
			scr.setProductiveCough (productiveCough.toUpperCase ());
		}
		scr.setDateEnded (encounterEndDate);
		try
		{
			scr.setDateEntered (DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.FE_FORMAT));
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			return XmlUtil.createErrorXml ("Ye date sahih nahin hai! Date dobara darj karain aur form phir submit karain");
		}
		scr.setDateStarted (encounterStartDate);
		scr.setFamilyTbhistory (tbFamilyHistory.toUpperCase ());
		scr.setGender (sex.toUpperCase ().charAt (0));

		// scr.setFever(fever);
		// scr.setHaemoptysis(haemoptysis);
		// scr.setNightSweat(nightSweat);
		// scr.setPatientId(patientId);
		// scr.setWeightLoss(weightLoss);

		// scr.setScreeningId(screeningId);

		scr.setScreenLocation (labid.toUpperCase ());

		if (conclusion.equalsIgnoreCase ("suspect"))
			return XmlUtil.createErrorXml ("Patient was detected as suspect while called for Non suspect logging. Contact Vendor.");

		scr.setSuspect (false);
		// scr.setTbawareness(tbawareness);
		scr.setTbhistory (tbHistory.toUpperCase ());
		/*if (tbMedication != null || tbTreatment != null)
		{
			scr.setMedication (tbMedication.equalsIgnoreCase ("yes"));
			scr.setMedicationDuration (tbTreatment.toUpperCase ());
		}
		if (labtest.trim ().equalsIgnoreCase ("other"))
		{
			scr.setVisitedLabForTest (labother.toUpperCase ());
		}
		else
		{
			scr.setVisitedLabForTest (labtest.toUpperCase ());
		}*/

		Boolean isSaved = null;
		try
		{
			isSaved = ssl.saveScreening (scr);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}

		if (isSaved == null || !isSaved.booleanValue ())
		{
			return XmlUtil.createErrorXml ("Screening not saved. Try Again");
		}
		// No encounter will be saved as it doesnt have person id
		xml = XmlUtil.createSuccessXml ();
		return xml;
	}

	private String doMRAssign ()
	{
		String xml = null;

		String id = request.getParameter ("id");

		String mid = request.getParameter ("mid");

		String mrNum = request.getParameter ("mr");

		String startDate = request.getParameter ("sd");
		String startTime = request.getParameter ("st");
		String endTime = request.getParameter ("et");
		String enteredDate = request.getParameter ("ed");

		Patient pat = null;

		try
		{
			pat = ssl.findPatient (id);
		}
		catch (Exception e2)
		{
			// Auto-generated catch block
			e2.printStackTrace ();
		}

		if (pat == null)
		{
			return XmlUtil.createErrorXml ("Patient with id " + id + " does not exist. Please recheck ID and try again.");
		}

		if (pat.getMrno () != null && pat.getMrno ().length () != 0)
		{
			return XmlUtil.createErrorXml ("Patient already has MR Number");
		}

		Boolean mrAssigned = null;

		try
		{
			mrAssigned = ssl.exists ("Patient", " where MrNo='" + mrNum + "'");
		}
		catch (Exception e3)
		{
			// TODO Auto-generated catch block
			e3.printStackTrace ();
		}

		if (mrAssigned == null)
		{
			return XmlUtil.createErrorXml ("Error tracking MR Number");
		}

		else if (mrAssigned.booleanValue () == true)
		{
			return XmlUtil.createErrorXml ("MR Number " + mrNum + " has already been assigned.");
		}

		pat.setMrno (mrNum);

		boolean patUpdated = true;

		try
		{
			patUpdated = ssl.updatePatient (pat);
		}
		catch (Exception e2)
		{
			// TODO Auto-generated catch block
			e2.printStackTrace ();
		}

		if (!patUpdated)
		{
			return XmlUtil.createErrorXml ("Could not update Patient! Please try again!");
		}
		EncounterId encId = new EncounterId ();
		encId.setPid1 (id);
		encId.setPid2 (mid);

		Encounter e = new Encounter ();
		e.setId (encId);
		e.setEncounterType ("MR_ASSIGN");
		e.setDateEncounterStart (encounterStartDate);
		e.setDateEncounterEnd (encounterEndDate);
		try
		{
			e.setDateEncounterEntered (DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.FE_FORMAT));
		}

		catch (Exception e1)
		{
			e1.printStackTrace ();
			return XmlUtil.createErrorXml ("Bad entered date. Please try again");
		}

		try
		{
			ssl.saveEncounter (e);
		}
		catch (Exception e1)
		{
			// Auto-generated catch block
			e1.printStackTrace ();
			return XmlUtil.createErrorXml ("Could not create Encounter. Please try again.");
		}

		return XmlUtil.createSuccessXml ();
	}

	/*
	 * private String doPaedClinicalDiagnosis() { String xml = null;
	 * 
	 * String id = request.getParameter("id"); String gpId =
	 * request.getParameter("gpid"); String afb = request.getParameter("afb");
	 * String ppd = request.getParameter("ppd"); String ppdr =
	 * request.getParameter("ppdr"); if(ppdr==null) ppdr = ""; String cxr =
	 * request.getParameter("cxr"); String granuloma =
	 * request.getParameter("gran"); String ppaDone =
	 * request.getParameter("ppa"); String initPPA =
	 * request.getParameter("initppa"); if(initPPA==null) initPPA =""; String
	 * finalPPA = request.getParameter("finalppa"); if(finalPPA==null) {
	 * finalPPA = ""; } String conclusion = request.getParameter("conc"); String
	 * antiFollowup = request.getParameter("antitime"); if(antiFollowup==null)
	 * antiFollowup = ""; String otherDisease =
	 * request.getParameter("otherdis"); if(otherDisease==null) otherDisease =
	 * ""; String otherDetails = request.getParameter("other");
	 * if(otherDetails==null) otherDetails = "";
	 * 
	 * String startDate = request.getParameter("sd"); String startTime =
	 * request.getParameter("st"); String endTime = request.getParameter("et");
	 * String enteredDate = request.getParameter("ed");
	 * 
	 * String un = request.getParameter("un");
	 * 
	 * if(un==null) { return
	 * XmlUtil.createErrorXml("Error! Please contact technical support"); }
	 * 
	 * un = un.toUpperCase(); Users users = null; try { users =
	 * ssl.findUser(un); } catch (Exception e4) { e4.printStackTrace(); return
	 * XmlUtil.createErrorXml("Bad username. Please try again"); } String pid2 =
	 * users.getPid();
	 * 
	 * Patient pat = null;
	 * 
	 * boolean incentFlag = false; try { pat = (Patient) (ssl.findPatient(id));
	 * } catch (Exception e3) { // Auto-generated catch block
	 * e3.printStackTrace(); }
	 * 
	 * if(pat==null) { return XmlUtil.createErrorXml("Patient with id " + id +
	 * " does not exist. Please recheck ID and try again."); }
	 * 
	 * Boolean exists = null;
	 * 
	 * try { exists = ssl.exists("Encounter"," where PID1='" + id +
	 * "' AND DateEncounterEntered LIKE '" +
	 * DateTimeUtil.convertFromSlashFormatToSQL(enteredDate.split(" ")[0]) +
	 * "%' AND EncounterType='PAED_DIAG'"); } catch (Exception e3) {
	 * e3.printStackTrace(); }
	 * 
	 * if(exists==null) { return
	 * XmlUtil.createErrorXml("Error tracking Patient ID"); }
	 * 
	 * else if(exists.booleanValue()==true) { return
	 * XmlUtil.createErrorXml("Patient " + id +
	 * " ka P form aaj hi ki tareekh mein pehlay bhara ja chuka hai. Agar aap se form bharnay mein koi ghalati huwi hai to TB Reach team se rujoo karain"
	 * ); }
	 * 
	 * //set disease confirmed to true if not already true and if TB is
	 * diagnosed //if already true, do not send alert/incentive
	 * 
	 * //pat.setGpid(gpId.toUpperCase());
	 * if(!conclusion.equals("Given Antibiotic Followup") &&
	 * !conclusion.equals("No Follow up required (other disease)") &&
	 * !pat.getDiseaseConfirmed()) { pat.setDiseaseConfirmed(new Boolean(true));
	 * incentFlag = true; }
	 * 
	 * try { boolean patUpdate = ssl.updatePatient(pat); } catch (Exception e2)
	 * { // Auto-generated catch block e2.printStackTrace();
	 * 
	 * return
	 * XmlUtil.createErrorXml("Could not update Patient. Please try again.");
	 * 
	 * }
	 * 
	 * EncounterId encId = new EncounterId(); encId.setPid1(id);
	 * encId.setPid2(pid2);
	 * 
	 * Encounter e = new Encounter(); e.setId(encId);
	 * e.setEncounterType("PAED_DIAG");
	 * e.setDateEncounterStart(encounterStartDate);
	 * e.setDateEncounterEnd(encounterEndDate); try {
	 * e.setDateEncounterEntered(DateTimeUtil.getDateFromString(enteredDate,
	 * DateTimeUtil.FE_FORMAT)); }
	 * 
	 * catch(Exception e1) { e1.printStackTrace(); return
	 * XmlUtil.createErrorXml("Bad entered date. Please try again"); }
	 * 
	 * try { boolean eCreated = ssl.saveEncounter(e); } catch (Exception e1) {
	 * // Auto-generated catch block e1.printStackTrace(); return
	 * XmlUtil.createErrorXml
	 * ("ERROR: Could not save encounter. Please try again");
	 * 
	 * }
	 * 
	 * ArrayList<EncounterResults> encounters = new
	 * ArrayList<EncounterResults>();
	 * 
	 * EncounterResults dateResult = ModelUtil.createEncounterResult(e,
	 * "entered_date".toUpperCase(), enteredDate); encounters.add(dateResult);
	 * 
	 * EncounterResults gpidResult = ModelUtil.createEncounterResult(e,
	 * "gpid".toUpperCase(), gpId.toUpperCase()); encounters.add(gpidResult);
	 * 
	 * EncounterResults afbResult = ModelUtil.createEncounterResult(e,
	 * "afb".toUpperCase(), afb.toUpperCase()); encounters.add(afbResult);
	 * 
	 * EncounterResults ppdResult = ModelUtil.createEncounterResult(e,
	 * "ppd".toUpperCase(), ppd.toUpperCase()); encounters.add(ppdResult);
	 * 
	 * EncounterResults ppdrResult = ModelUtil.createEncounterResult(e,
	 * "ppdr".toUpperCase(), ppdr.toUpperCase()); encounters.add(ppdrResult);
	 * 
	 * EncounterResults cxrResult = ModelUtil.createEncounterResult(e,
	 * "cxr".toUpperCase(), cxr.toUpperCase()); encounters.add(cxrResult);
	 * 
	 * EncounterResults granResult = ModelUtil.createEncounterResult(e,
	 * "granuloma".toUpperCase(), granuloma.toUpperCase());
	 * encounters.add(granResult);
	 * 
	 * EncounterResults ppaResult = ModelUtil.createEncounterResult(e,
	 * "ppa_done".toUpperCase(), ppaDone.toUpperCase());
	 * encounters.add(ppaResult);
	 * 
	 * EncounterResults initPPAResult = ModelUtil.createEncounterResult(e,
	 * "init_ppa".toUpperCase(), initPPA.toUpperCase());
	 * encounters.add(initPPAResult);
	 * 
	 * EncounterResults finalPPAResult = ModelUtil.createEncounterResult(e,
	 * "final_ppa".toUpperCase(), finalPPA.toUpperCase());
	 * encounters.add(finalPPAResult);
	 * 
	 * EncounterResults concResult = ModelUtil.createEncounterResult(e,
	 * "conclusion".toUpperCase(), conclusion.toUpperCase());
	 * encounters.add(concResult);
	 * 
	 * EncounterResults antiResult = ModelUtil.createEncounterResult(e,
	 * "antibiotic_fo_time".toUpperCase(), antiFollowup.toUpperCase());
	 * encounters.add(antiResult);
	 * 
	 * EncounterResults otherResult = ModelUtil.createEncounterResult(e,
	 * "other_disease".toUpperCase(), otherDisease.toUpperCase());
	 * encounters.add(otherResult);
	 * 
	 * EncounterResults otherDetResult = ModelUtil.createEncounterResult(e,
	 * "other_detail".toUpperCase(), otherDetails.toUpperCase());
	 * encounters.add(otherDetResult);
	 * 
	 * boolean resultSave = true;
	 * 
	 * for (int i = 0; i < encounters.size(); i++) { try { resultSave =
	 * ssl.saveEncounterResults(encounters.get(i)); } catch (Exception e1) { //
	 * Auto-generated catch block e1.printStackTrace(); break; }
	 * 
	 * if (!resultSave) { return
	 * XmlUtil.createErrorXml("ERROR! Please try again"); }
	 * 
	 * }
	 * 
	 * //add incentive/alert here if incentFlag is true
	 * 
	 * xml = XmlUtil.createSuccessXml(); return xml; }
	 */

	/*
	 * private String doPaedConfirmation() { String xml = null;
	 * 
	 * String id = request.getParameter("id"); String gpId =
	 * request.getParameter("gpid"); String weight = request.getParameter("wt");
	 * String weightPercentile = request.getParameter("wp"); String cough =
	 * request.getParameter("cough"); String coughDuration =
	 * request.getParameter("cd"); String productiveCough =
	 * request.getParameter("pc"); String fever = request.getParameter("fev");
	 * String nightSweat = request.getParameter("ns"); String weightLoss =
	 * request.getParameter("wl"); String haemoptysis =
	 * request.getParameter("ha"); String appetite =
	 * request.getParameter("app"); String chestExam =
	 * request.getParameter("chest"); String lymphExam =
	 * request.getParameter("lymph"); String abdm =
	 * request.getParameter("abdm"); String otherExam =
	 * request.getParameter("otherEx"); String otherDet =
	 * request.getParameter("otherDet"); if(otherDet==null) { otherDet=""; }
	 * String bcgScar = request.getParameter("bcg"); String currentHistory =
	 * request.getParameter("cfh"); String numCurrentHistory =
	 * request.getParameter("numcfh");
	 * 
	 * String previousHistory = request.getParameter("pfh"); String
	 * numPreviousHistory = request.getParameter("numpfh"); String conclusion =
	 * request.getParameter("conc");
	 * 
	 * String startDate = request.getParameter("sd"); String startTime =
	 * request.getParameter("st"); String endTime = request.getParameter("et");
	 * String enteredDate = request.getParameter("ed"); String un =
	 * request.getParameter("un");
	 * 
	 * if(un==null) { return
	 * XmlUtil.createErrorXml("Error! Please contact technical support"); }
	 * 
	 * if(cough==null) { return
	 * XmlUtil.createErrorXml("Phone update karain aur dobara form save karain"
	 * ); }
	 * 
	 * un = un.toUpperCase(); Users users = null; try { users =
	 * ssl.findUser(un); } catch (Exception e4) { e4.printStackTrace(); return
	 * XmlUtil.createErrorXml("Bad username. Please try again"); } String pid2 =
	 * users.getPid();
	 * 
	 * 
	 * 
	 * Patient pat = null; try { pat = (Patient) (ssl.findPatient(id)); } catch
	 * (Exception e3) { // Auto-generated catch block e3.printStackTrace(); }
	 * 
	 * if(pat==null) { return XmlUtil.createErrorXml("Patient with id " + id +
	 * " does not exist. Please recheck ID and try again."); }
	 * 
	 * if(pat.getPatientStatus()!=null &&
	 * !pat.getPatientStatus().equals("SUSPECT")) { return
	 * XmlUtil.createErrorXml("Patient has already been confirmed!"); }
	 * 
	 * if (conclusion.equalsIgnoreCase("confirmed")) {
	 * pat.setDiseaseSuspected(new Boolean(true));
	 * //pat.setPatientStatus("GP_CONF"); }
	 * 
	 * else { pat.setDiseaseSuspected(new Boolean(false));
	 * //pat.setPatientStatus("GP_NO_CONF"); }
	 * 
	 * //pat.setGpid(gpId.toUpperCase());
	 * pat.setWeight(Float.parseFloat(weight));
	 * 
	 * try { boolean patUpdate = ssl.updatePatient(pat); } catch (Exception e2)
	 * { // Auto-generated catch block e2.printStackTrace();
	 * 
	 * return
	 * XmlUtil.createErrorXml("Could not update Patient. Please try again.");
	 * 
	 * }
	 * 
	 * EncounterId encId = new EncounterId(); encId.setPid1(id);
	 * encId.setPid2(pid2);
	 * 
	 * Encounter e = new Encounter(); e.setId(encId);
	 * e.setEncounterType("PAED_CONF");
	 * e.setDateEncounterStart(encounterStartDate);
	 * e.setDateEncounterEnd(encounterEndDate); try {
	 * e.setDateEncounterEntered(DateTimeUtil.getDateFromString(enteredDate,
	 * DateTimeUtil.FE_FORMAT)); }
	 * 
	 * catch(Exception e1) { e1.printStackTrace(); return
	 * XmlUtil.createErrorXml("Bad entered date. Please try again"); }
	 * 
	 * try { boolean eCreated = ssl.saveEncounter(e); } catch (Exception e1) {
	 * // Auto-generated catch block e1.printStackTrace(); return
	 * XmlUtil.createErrorXml
	 * ("ERROR: Could not save encounter. Please try again");
	 * 
	 * }
	 * 
	 * ArrayList<EncounterResults> encounters = new
	 * ArrayList<EncounterResults>();
	 * 
	 * EncounterResults dateResult = ModelUtil.createEncounterResult(e,
	 * "entered_date".toUpperCase(), enteredDate); encounters.add(dateResult);
	 * 
	 * EncounterResults weightResult = ModelUtil.createEncounterResult(e,
	 * "weight".toUpperCase(), weight.toUpperCase());
	 * encounters.add(weightResult);
	 * 
	 * EncounterResults phaseResult = ModelUtil.createEncounterResult(e,
	 * "weight_percentile".toUpperCase(), weightPercentile.toUpperCase());
	 * encounters.add(phaseResult);
	 * 
	 * EncounterResults coughResult = ModelUtil.createEncounterResult(e,
	 * "cough".toUpperCase(), cough.toUpperCase()); encounters.add(coughResult);
	 * 
	 * if(coughDuration!=null) { EncounterResults coughDurationResult =
	 * ModelUtil.createEncounterResult(e, "cough_duration".toUpperCase(),
	 * coughDuration.toUpperCase()); encounters.add(coughDurationResult);
	 * 
	 * if(productiveCough!=null) { EncounterResults productiveCoughResult =
	 * ModelUtil.createEncounterResult(e, "productive_cough".toUpperCase(),
	 * productiveCough.toUpperCase()); encounters.add(productiveCoughResult);
	 * 
	 * } }
	 * 
	 * if(coughDuration!=null && productiveCough!=null &&
	 * (coughDuration.equals("2 to 3 weeks") ||
	 * coughDuration.equals("more than 3 weeks") ) &&
	 * productiveCough.equals("Yes")) { EncounterResults twoWeeksProdCoughResult
	 * = ModelUtil.createEncounterResult(e, "two_weeks_cough".toUpperCase(),
	 * "YES"); encounters.add(twoWeeksProdCoughResult); }
	 * 
	 * else { EncounterResults twoWeeksProdCoughResult =
	 * ModelUtil.createEncounterResult(e, "two_weeks_cough".toUpperCase(),
	 * "NO"); encounters.add(twoWeeksProdCoughResult); }
	 * 
	 * EncounterResults feverResult = ModelUtil.createEncounterResult(e,
	 * "fever".toUpperCase(), fever.toUpperCase()); encounters.add(feverResult);
	 * 
	 * EncounterResults nsResult = ModelUtil.createEncounterResult(e,
	 * "night_sweats".toUpperCase(), nightSweat.toUpperCase());
	 * encounters.add(nsResult);
	 * 
	 * EncounterResults wlResult = ModelUtil.createEncounterResult(e,
	 * "weight_loss".toUpperCase(), weightLoss.toUpperCase());
	 * encounters.add(wlResult);
	 * 
	 * EncounterResults hResult = ModelUtil.createEncounterResult(e,
	 * "haemoptysis".toUpperCase(), haemoptysis.toUpperCase());
	 * encounters.add(hResult);
	 * 
	 * EncounterResults appResult = ModelUtil.createEncounterResult(e,
	 * "appetite".toUpperCase(), appetite.toUpperCase());
	 * encounters.add(appResult);
	 * 
	 * EncounterResults chestResult = ModelUtil.createEncounterResult(e,
	 * "chest_exam".toUpperCase(), chestExam.toUpperCase());
	 * encounters.add(chestResult);
	 * 
	 * EncounterResults lymphResult = ModelUtil.createEncounterResult(e,
	 * "lymph_exam".toUpperCase(), lymphExam.toUpperCase());
	 * encounters.add(lymphResult);
	 * 
	 * EncounterResults abdResult = ModelUtil.createEncounterResult(e,
	 * "abd_mass".toUpperCase(), abdm.toUpperCase()); encounters.add(abdResult);
	 * 
	 * EncounterResults otherResult = ModelUtil.createEncounterResult(e,
	 * "other_exam".toUpperCase(), otherExam.toUpperCase());
	 * encounters.add(otherResult);
	 * 
	 * EncounterResults otherDetailResult = ModelUtil.createEncounterResult(e,
	 * "other_exam_detail".toUpperCase(), otherDet.toUpperCase());
	 * encounters.add(otherDetailResult);
	 * 
	 * EncounterResults bcgResult = ModelUtil.createEncounterResult(e,
	 * "bcg_scar".toUpperCase(), bcgScar.toUpperCase());
	 * encounters.add(bcgResult);
	 * 
	 * EncounterResults currFamHistResult = ModelUtil.createEncounterResult(e,
	 * "current_history".toUpperCase(), currentHistory.toUpperCase());
	 * encounters.add(currFamHistResult);
	 * 
	 * if(numCurrentHistory!=null) { EncounterResults numCurrFamHistResult =
	 * ModelUtil.createEncounterResult(e, "num_current_history".toUpperCase(),
	 * numCurrentHistory.toUpperCase()); encounters.add(numCurrFamHistResult);
	 * 
	 * //loop for current history int numCurr =
	 * Integer.parseInt(numCurrentHistory);
	 * 
	 * for(int q=0;q<numCurr;q++) {
	 * encounters.add(ModelUtil.createEncounterResult(e,
	 * "current_rel_".toUpperCase() + (q+1), request.getParameter("crel" +
	 * (q+1)))); encounters.add(ModelUtil.createEncounterResult(e,
	 * "current_other_rel_".toUpperCase() + (q+1),
	 * request.getParameter("cothrel" + (q+1))));
	 * encounters.add(ModelUtil.createEncounterResult(e,
	 * "current_tb_form_".toUpperCase() + (q+1), request.getParameter("ctbf" +
	 * (q+1)))); encounters.add(ModelUtil.createEncounterResult(e,
	 * "current_tb_type_".toUpperCase() + (q+1), request.getParameter("ctbt" +
	 * (q+1)))); encounters.add(ModelUtil.createEncounterResult(e,
	 * "current_ss_".toUpperCase() + (q+1), request.getParameter("css" +
	 * (q+1)))); } } EncounterResults prevFamHistResult =
	 * ModelUtil.createEncounterResult(e, "previous_history".toUpperCase(),
	 * previousHistory.toUpperCase()); encounters.add(prevFamHistResult);
	 * 
	 * if(numPreviousHistory!=null) { EncounterResults numPrevFamHistResult =
	 * ModelUtil.createEncounterResult(e, "num_previous_history".toUpperCase(),
	 * numPreviousHistory.toUpperCase()); encounters.add(numPrevFamHistResult);
	 * 
	 * //loop for previous history int numPrev =
	 * Integer.parseInt(numPreviousHistory);
	 * 
	 * for(int q=0;q<numPrev;q++) {
	 * encounters.add(ModelUtil.createEncounterResult(e,
	 * "previous_rel_".toUpperCase() + (q+1), request.getParameter("prel" +
	 * (q+1)))); encounters.add(ModelUtil.createEncounterResult(e,
	 * "previous_other_rel_".toUpperCase() + (q+1),
	 * request.getParameter("pothrel" + (q+1))));
	 * encounters.add(ModelUtil.createEncounterResult(e,
	 * "previous_tb_form_".toUpperCase() + (q+1), request.getParameter("ptbf" +
	 * (q+1)))); encounters.add(ModelUtil.createEncounterResult(e,
	 * "previous_tb_type_".toUpperCase() + (q+1), request.getParameter("ptbt" +
	 * (q+1)))); encounters.add(ModelUtil.createEncounterResult(e,
	 * "previous_ss_".toUpperCase() + (q+1), request.getParameter("pss" +
	 * (q+1)))); } }
	 * 
	 * EncounterResults concResult = ModelUtil.createEncounterResult(e,
	 * "conclusion".toUpperCase(), conclusion.toUpperCase());
	 * encounters.add(concResult);
	 * 
	 * 
	 * boolean resultSave = true;
	 * 
	 * for (int i = 0; i < encounters.size(); i++) { try { resultSave =
	 * ssl.saveEncounterResults(encounters.get(i)); } catch (Exception e1) { //
	 * Auto-generated catch block e1.printStackTrace(); break; }
	 * 
	 * if (!resultSave) { return
	 * XmlUtil.createErrorXml("ERROR! Please try again"); }
	 * 
	 * }
	 * 
	 * xml = XmlUtil.createSuccessXml(); return xml; }
	 */

	private String doPatientGPSInfo ()
	{
		String xml = null;

		String id = request.getParameter ("id");
		String chwId = request.getParameter ("chwid");
		// String structNum = request.getParameter("sn");
		String lat = request.getParameter ("lat");
		String lng = request.getParameter ("lng");
		String encType = request.getParameter ("enc");

		String shop = request.getParameter ("shop");
		String mosque = request.getParameter ("mosque");
		String houseType = request.getParameter ("htype");
		String otherHouseType = request.getParameter ("ohtype");
		if (otherHouseType == null)
			otherHouseType = "";
		String houseMaterial = request.getParameter ("hmtype");
		String otherHouseMaterial = request.getParameter ("ohmtype");
		if (otherHouseMaterial == null)
			otherHouseMaterial = "";

		String startDate = request.getParameter ("sd");
		String startTime = request.getParameter ("st");
		String endTime = request.getParameter ("et");
		String enteredDate = request.getParameter ("ed");

		// /////////////////////

		Person person = null;
		Contact c = null;
		try
		{
			person = ssl.findPerson (id);

		}
		catch (Exception e3)
		{
			e3.printStackTrace ();
		}

		if (person == null)
		{
			return XmlUtil.createErrorXml ("Person with id " + id + " does not exist. Please recheck ID or check if Registration A & B forms have been filled.");
		}

		Float latFloat = null;
		Float lngFloat = null;

		if (!StringUtils.isEmptyOrWhitespaceOnly (lat))
			latFloat = Float.valueOf (lat);

		if (!StringUtils.isEmptyOrWhitespaceOnly (lng))
			lngFloat = Float.valueOf (lng);

		// gpsc.setLat(latFloat);
		// gpsc.setLng(lngFloat);

		try
		{
			c = ssl.findContact (id);
		}
		catch (Exception e)
		{
			// Auto-generated catch block
			e.printStackTrace ();
		}

		if (c == null)
		{
			return XmlUtil.createErrorXml ("Contact for patient with id " + id + " does not exist. "
					+ "Please recheck ID and try again. Agar Patient ka Registration-A or Registartion-B fill nahi hua tu pehlay wo fill karen.");
		}

		Boolean results = null;
		try
		{
			results = ssl.exists ("encounter",
					" where PID1='" + id + "'" + " AND EncounterType='" + EncounterType.GPS_COORDINATES + "'" + " AND DateEncounterEntered='"
							+ DateTimeUtil.convertToSQL (enteredDate, DateTimeUtil.FE_FORMAT) + "'" + " ORDER BY DateEncounterEntered DESC");
		}
		catch (Exception e3)
		{
			e3.printStackTrace ();
		}

		if (results != null && results)
		{
			return XmlUtil.createErrorXml ("Patient ka " + enteredDate + " ka  ek GPS Form bhara ja chuka hay . Agar aap say Form bharnay may koi ghalti hui hay tu TBReach team say rujoo karen.");
		}
		/*
		 * Boolean exists = null;
		 * 
		 * try { exists = ssl.exists("encounter" ," where PID1='" + id + "'" +
		 * " AND EncounterType='"+EncounterType.GPS_COORDINATES+"'"); } catch
		 * (Exception e3) { e3.printStackTrace(); }
		 * 
		 * if(exists==null) { return
		 * XmlUtil.createErrorXml("Error tracking Patient ID"); } else
		 * if(exists.booleanValue()==true) { return
		 * XmlUtil.createErrorXml("Patient " + id +
		 * " ka Patient GPS Form pehlay bhara ja chuka hai. Agar aap se form bharnay mein koi ghalati huwi hai to TB Reach team se rujoo karain"
		 * ); }
		 */

		c.setLocationLat (latFloat);
		c.setLocationLon (lngFloat);

		try
		{
			ssl.updateContact (c);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			return XmlUtil.createErrorXml ("Could not update Contact for Patient with id: " + id + "! Please try again!");
		}

		EncounterId encId = new EncounterId ();
		encId.setPid1 (id.toUpperCase ());
		encId.setPid2 (chwId.toUpperCase ());

		Date encounterdateentered = null;
		try
		{
			encounterdateentered = DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.FE_FORMAT);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			return XmlUtil.createErrorXml ("Bad entered date. Please try again");
		}

		Encounter e = new Encounter ();
		e.setId (encId);
		e.setEncounterType (EncounterType.GPS_COORDINATES);
		e.setDateEncounterEntered (encounterdateentered);
		e.setDateEncounterStart (encounterStartDate);
		e.setDateEncounterEnd (encounterEndDate);
		e.setLocationId ("");

		try
		{
			ssl.saveEncounter (e);
		}
		catch (Exception e1)
		{
			// Auto-generated catch block
			e1.printStackTrace ();
			XmlUtil.createErrorXml ("Could not save encounter! Please try again!");
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults> ();

		EncounterResults dateResult = ModelUtil.createEncounterResult (e, "entered_date".toUpperCase (), enteredDate);
		encounters.add (dateResult);

		EncounterResults latResult = ModelUtil.createEncounterResult (e, "lat".toUpperCase (), (latFloat == null ? "" : latFloat.toString ().toUpperCase ()));
		encounters.add (latResult);

		EncounterResults lngResult = ModelUtil.createEncounterResult (e, "lng".toUpperCase (), (lngFloat == null ? "" : lngFloat.toString ().toUpperCase ()));
		encounters.add (lngResult);

		if (mosque == null)
			mosque = "";

		if (shop == null)
			shop = "";

		if (otherHouseMaterial == null)
			otherHouseMaterial = "";

		if (otherHouseType == null)
			otherHouseType = "";

		EncounterResults mosqueResult = ModelUtil.createEncounterResult (e, "mosque".toUpperCase (), mosque.toUpperCase ());
		encounters.add (mosqueResult);

		EncounterResults shopResult = ModelUtil.createEncounterResult (e, "shop".toUpperCase (), shop.toUpperCase ());
		encounters.add (shopResult);

		EncounterResults typeofHouseResult = ModelUtil.createEncounterResult (e, "type_of_house".toUpperCase (), houseType.toUpperCase ());
		encounters.add (typeofHouseResult);

		EncounterResults otherTypeOfHouseResult = ModelUtil.createEncounterResult (e, "other_type_of_house".toUpperCase (), otherHouseType.toUpperCase ());
		encounters.add (otherTypeOfHouseResult);

		EncounterResults houseMaterialsResults = ModelUtil.createEncounterResult (e, "house_material".toUpperCase (), houseMaterial.toUpperCase ());
		encounters.add (houseMaterialsResults);

		EncounterResults otherHouseMaterialsResults = ModelUtil.createEncounterResult (e, "other_house_material".toUpperCase (), otherHouseMaterial.toUpperCase ());
		encounters.add (otherHouseMaterialsResults);

		/*
		 * if (structNum != null && structNum.length() != 0) { EncounterResults
		 * snResult = ModelUtil.createEncounterResult(e,
		 * "struct_number".toUpperCase(), structNum.toUpperCase());
		 * encounters.add(snResult); }
		 * 
		 * if (lat != null) { EncounterResults latResult =
		 * ModelUtil.createEncounterResult(e, "gps_lat".toUpperCase(),
		 * lat.toUpperCase()); encounters.add(latResult); }
		 * 
		 * if (lng != null) { EncounterResults lngResult =
		 * ModelUtil.createEncounterResult(e, "gps_long".toUpperCase(),
		 * lng.toUpperCase()); encounters.add(lngResult); }
		 * 
		 * EncounterResults etResult = ModelUtil.createEncounterResult(e,
		 * "enc_type".toUpperCase(), encType.toUpperCase());
		 * encounters.add(etResult);
		 */

		boolean resultSave = true;

		for (int i = 0; i < encounters.size (); i++)
		{
			try
			{
				resultSave = ssl.saveEncounterResults (encounters.get (i));
			}
			catch (Exception e1)
			{
				// Auto-generated catch block
				e1.printStackTrace ();
				break;
			}

			if (!resultSave)
			{
				return XmlUtil.createErrorXml ("ERROR: Please try again");
			}
		}

		xml = XmlUtil.createSuccessXml ();
		return xml;
	}
	
	public String doPatientOldInfo()
	{
		String xml = null;
		String id = request.getParameter ("id");
		String chwId = request.getParameter ("chwid");
		String labId = request.getParameter ("labid");
		String smokeAware = request.getParameter("saware");
		String smokeCurrent = request.getParameter("scurrent");
		String smokePast = request.getParameter("spast");
		String smokelessCurrent = request.getParameter("slesscurr");
		String smokelessPast = request.getParameter("slesspast");
		String smokesecond = request.getParameter("ssecond");
		String diabetesaware = request.getParameter("daware");
		String diabetesdx = request.getParameter("ddx");
		
		

		String startDate = request.getParameter ("sd");
		String startTime = request.getParameter ("st");
		String endTime = request.getParameter ("et");
		String enteredDate = request.getParameter ("ed");
		
		Patient patient = null;
		Person person =null;
		
		try
		{
			person = ssl.findPerson(id);

			
		}
		catch (Exception e4)
		{
			e4.printStackTrace ();
		}
		
		
		if (person == null)
		{
			return XmlUtil.createErrorXml ("Could not find patient id " + id + ". Please try again.");
		}
		try
		{
			patient = ssl.findPatient(id);

			
		}
		catch (Exception e4)
		{
			e4.printStackTrace ();
		}
		if (patient == null)
		{
			return XmlUtil.createErrorXml ("Could not find patient id " + id + ". Please try again.");
		}
		
		Boolean check = null;

		try
		{
			check = ssl.exists ("person", "where PID='" + id + "'");
		}
		catch (Exception e4)
		{
			e4.printStackTrace ();
		}

		if (check == null)
		{
			return XmlUtil.createErrorXml ("Error finding person data. Try Again.");
		}

		
		EncounterResults erforNewquestions= null;
		
		EncounterResultsId eidnewquestions=new EncounterResultsId();
		eidnewquestions.setPid1(id);
		eidnewquestions.setElement("SMOKE_AWARE");
		
		
		try{
		erforNewquestions=ssl.findEncounterResultsByElements(eidnewquestions);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		int EncounterID = 2;
		
		
		EncounterId encId = new EncounterId ();
		encId.setEncounterId(EncounterID);
		encId.setPid1 (id.toUpperCase ());
		encId.setPid2 (chwId.toUpperCase ());
		
		
		Encounter encounter=ssl.findEncountersbyType(encId);
		EncounterID=encounter.getId().getEncounterId();
		String pid1=encounter.getId().getPid1();
		String pid2=encounter.getId().getPid2();
		System.out.println("EncounterID "+EncounterID+" PID1 "+pid1+" PID2 "+pid2);
		
		encounter.setId(encId);
		
		
		if(erforNewquestions==null)
		{
			System.out.println("Yes this question is not there........");
			ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults> ();
			
			
			
			EncounterResults smokingques1 = ModelUtil.createEncounterResult (encounter, "SMOKE_AWARE".toUpperCase (), smokeAware.toUpperCase ());
			encounters.add (smokingques1);
			
			EncounterResults smokingques2 = ModelUtil.createEncounterResult (encounter, "SMOKE_CURRENT".toUpperCase (), smokeCurrent.toUpperCase ());
			encounters.add (smokingques2);
			
			EncounterResults smokingques3 = ModelUtil.createEncounterResult (encounter, "SMOKE_PAST".toUpperCase (), smokePast.toUpperCase ());
			encounters.add (smokingques3);
			
			EncounterResults smokingques4 = ModelUtil.createEncounterResult (encounter, "SMOKELESS_CURRENT".toUpperCase (), smokelessCurrent.toUpperCase ());
			encounters.add (smokingques4);
			
			EncounterResults smokingques5 = ModelUtil.createEncounterResult (encounter, "SMOKELESS_PAST".toUpperCase (), smokelessPast.toUpperCase ());
			encounters.add (smokingques5);
			
			EncounterResults smokingques6 = ModelUtil.createEncounterResult (encounter, "SMOKE_SECOND".toUpperCase (), smokesecond.toUpperCase ());
			encounters.add (smokingques6);
			
			EncounterResults diabetesques7 = ModelUtil.createEncounterResult (encounter, "DIABETES_AWARE".toUpperCase (), diabetesaware.toUpperCase ());
			encounters.add (diabetesques7);
			
			EncounterResults diabetesques8 = ModelUtil.createEncounterResult (encounter, "DIABETES_DX".toUpperCase (), diabetesdx.toUpperCase ());
			encounters.add (diabetesques8);
			
			
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
					return XmlUtil.createErrorXml ("ERROR");
				}
			}
			
		}
		else
		{
			return XmlUtil.createErrorXml("Iss patient ka data Enter ho chuka hai");
		}
		
		xml = XmlUtil.createSuccessXml ();
		return xml;
	}

	public String doPatientInfo ()
	{
		String xml = null;

		String id = request.getParameter ("id");
		String chwId = request.getParameter ("chwid");
		String labId = request.getParameter ("labid");
		String nic = request.getParameter ("nic");
		String nicType = request.getParameter ("nt");
		String nicOwner = request.getParameter ("no");
		String firstName = request.getParameter ("fname");
		String lastName = request.getParameter ("lname");
		String fatherFirstName = request.getParameter ("ffn");
		// String gFatherFirstName = request.getParameter("gffn");
		// String dob = request.getParameter("dob");
		// String age = request.getParameter("age");
		// String sex = request.getParameter("sex");
		String maritalStatus = request.getParameter ("ms");
		String ethnicity = request.getParameter ("eth");
		String oEthnicity = request.getParameter ("oeth");
		String relegion = request.getParameter ("relg");
		String oRelegion = request.getParameter ("orelg");
		String sentByGp = request.getParameter ("gpsent");
		String nameOfGP = request.getParameter ("gpname");
		String knewAboutFreeTest = request.getParameter ("free");
		String howFound = request.getParameter ("how");
		String otherHowFoundOut = request.getParameter ("otherHow");
		String smokeAware = request.getParameter("saware");
		String smokeCurrent = request.getParameter("scurrent");
		String smokePast = request.getParameter("spast");
		String smokelessCurrent = request.getParameter("slesscurr");
		String smokelessPast = request.getParameter("slesspast");
		String smokesecond = request.getParameter("ssecond");
		String diabetesaware = request.getParameter("daware");
		String diabetesdx = request.getParameter("ddx");
		
		

		String startDate = request.getParameter ("sd");
		String startTime = request.getParameter ("st");
		String endTime = request.getParameter ("et");
		String enteredDate = request.getParameter ("ed");

		String[] howfoundarr = howFound.split(":");
		/*
		 * if(pastTBDrugs==null) { return
		 * XmlUtil.createErrorXml("Phone update karain aur dobara form save karain"
		 * ); }
		 */

		Screening scr = null;

		try
		{
			scr = ssl.findScreeningByPatientID (id);
/*			scr.setTbawareness(knewAboutFreeTest.equalsIgnoreCase("yes") ? howFoundOut + ":" + otherHowFoundOut : "");
			ssl.update(scr);*/
			
		}
		catch (Exception e4)
		{
			e4.printStackTrace ();
		}

		if (scr == null)
		{
			return XmlUtil.createErrorXml ("Could not find Screening for patient id " + id + ". Please try again. Agar Screening form fill nahi hua tu pehlay wo fill karen.");
		}

		Boolean check = null;

		try
		{
			check = ssl.exists ("person", "where PID='" + id + "'");
		}
		catch (Exception e4)
		{
			e4.printStackTrace ();
		}

		if (check == null)
		{
			return XmlUtil.createErrorXml ("Error finding person data. Try Again.");
		}

		if (check.booleanValue () == true)
		{
			return XmlUtil.createErrorXml ("1- Patient already registered.");
		}

		check = null;

		try
		{
			check = ssl.exists ("patient", "where PatientID='" + id + "'");
		}
		catch (Exception e4)
		{
			e4.printStackTrace ();
		}

		if (check == null)
		{
			return XmlUtil.createErrorXml ("Error checking patient data");
		}

		if (check.booleanValue () == true)
		{
			return XmlUtil.createErrorXml ("2- Patient already registered.");
		}

		String sex = scr.getGender ().toString ();

		Person person = new Person ();
		person.setPid (id);
		person.setFirstName (firstName.toUpperCase ());
		person.setLastName (lastName.toUpperCase ());
		person.setNic (nic);
		if (nicOwner != null)
			person.setNicownerName (nicOwner.toUpperCase ());
		person.setGuardianName (fatherFirstName.toUpperCase ());
		person.setGender (sex.toUpperCase ().charAt (0));
		person.setMaritalStatus (maritalStatus.toUpperCase ());
		if(ethnicity != null && !ethnicity.equalsIgnoreCase("other")){
			person.setCaste (ethnicity.toUpperCase ());
		}
		else if(oEthnicity != null){
			person.setCaste (oEthnicity.toUpperCase ());
		}
		if(relegion != null && !relegion.equalsIgnoreCase("other")){
			person.setReligion(relegion.toUpperCase ());
		}
		else if(oRelegion != null){
			person.setReligion (oRelegion.toUpperCase ());
		}
		person.setRoleInSystem ("PATIENT");

		Patient pat = new Patient ();
		pat.setPatientId (id.toUpperCase ());
		pat.setChwid (chwId.toUpperCase ());
		pat.setDateRegistered (new Date ());
		pat.setLaboratoryId (labId);
		pat.setPatientStatus ("SUSPECT");
		pat.setDiseaseSuspected (new Boolean (true));
		pat.setDiseaseConfirmed (new Boolean (false));

		Boolean pCreated = null;
		try
		{
			pCreated = ssl.saveNewPatient(pat, person, new Contact(id));
		}
		catch (Exception e3)
		{
			e3.printStackTrace ();
		}

		if (pCreated == null || !pCreated)
		{
			return XmlUtil.createErrorXml ("Could not save patient data. Please try again");
		}

		EncounterId encId = new EncounterId ();
		encId.setPid1 (id.toUpperCase ());
		encId.setPid2 (chwId.toUpperCase ());

		Encounter e = new Encounter ();
		e.setId (encId);
		e.setEncounterType (EncounterType.PRIMARY_INFORMATION);
		e.setDateEncounterStart (encounterStartDate);
		e.setDateEncounterEnd (encounterEndDate);
		e.setLocationId (labId);
		try
		{
			e.setDateEncounterEntered (DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.FE_FORMAT));
		}

		catch (Exception e1)
		{
			e1.printStackTrace ();
			return XmlUtil.createErrorXml ("Bad entered date. Please try again");
		}
		try
		{
			ssl.saveEncounter (e);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults> ();

		EncounterResults dateResult = ModelUtil.createEncounterResult (e, "entered_date".toUpperCase (), enteredDate);
		encounters.add (dateResult);

		EncounterResults nicResult = ModelUtil.createEncounterResult (e, "nic".toUpperCase (), nic.toUpperCase ());
		encounters.add (nicResult);

		EncounterResults nicTypeResult = ModelUtil.createEncounterResult (e, "nic_type".toUpperCase (), nicType.toUpperCase ());
		encounters.add (nicTypeResult);

		EncounterResults nicOwnerResult = ModelUtil.createEncounterResult (e, "nic_owner".toUpperCase (), nicOwner.toUpperCase ());
		encounters.add (nicOwnerResult);

		EncounterResults fNameResult = ModelUtil.createEncounterResult (e, "first_name".toUpperCase (), firstName.toUpperCase ());
		encounters.add (fNameResult);

		EncounterResults lNameResult = ModelUtil.createEncounterResult (e, "last_name".toUpperCase (), lastName.toUpperCase ());
		encounters.add (lNameResult);

		EncounterResults ffNameResult = ModelUtil.createEncounterResult (e, "father_first_name".toUpperCase (), fatherFirstName.toUpperCase ());
		encounters.add (ffNameResult);

		/*
		 * EncounterResults gffNameResult = ModelUtil.createEncounterResult(e,
		 * "gfather_first_name".toUpperCase(), gFatherFirstName.toUpperCase());
		 * encounters.add(gffNameResult);
		 */

		/*
		 * EncounterResults dobResult = ModelUtil.createEncounterResult(e,
		 * "dob".toUpperCase(), dob.toUpperCase()); encounters.add(dobResult);
		 */

		// EncounterResults ageResult = ModelUtil.createEncounterResult(e,
		// "age".toUpperCase(), age.toUpperCase());
		// encounters.add(ageResult);

		EncounterResults sexResult = ModelUtil.createEncounterResult (e, "sex".toUpperCase (), sex.toUpperCase ());
		encounters.add (sexResult);

		EncounterResults mStatusResult = ModelUtil.createEncounterResult (e, "marital_status".toUpperCase (), maritalStatus.toUpperCase ());
		encounters.add (mStatusResult);

		EncounterResults ethnicityResult = ModelUtil.createEncounterResult (e, "ethnicity".toUpperCase (), ethnicity.toUpperCase ());
		encounters.add (ethnicityResult);

		if (oEthnicity == null)
		{
			oEthnicity = "";
		}

		EncounterResults oethnicityResult = ModelUtil.createEncounterResult (e, "oth_ethnicity".toUpperCase (), oEthnicity.toUpperCase ());
		encounters.add (oethnicityResult);
		
		EncounterResults religionResult = ModelUtil.createEncounterResult (e, "religion".toUpperCase (), relegion.toUpperCase ());
		encounters.add (religionResult);
		
		if (oRelegion == null)
		{
			oRelegion = "";
		}
		EncounterResults oReligionResult = ModelUtil.createEncounterResult (e, "oth_religion".toUpperCase (), oRelegion.toUpperCase ());
		encounters.add (oReligionResult);
		
		EncounterResults sentByGPResult = ModelUtil.createEncounterResult (e, "SENT_BY_GP".toUpperCase (), sentByGp.toUpperCase ());
		encounters.add (sentByGPResult);

		if (nameOfGP == null)
		{
			nameOfGP = "";
		}

		EncounterResults sendingGPResult = ModelUtil.createEncounterResult (e, "SENDING_GP".toUpperCase (), nameOfGP.toUpperCase ());
		encounters.add (sendingGPResult);

		EncounterResults knewFreeTestResult = ModelUtil.createEncounterResult (e, "KNEW_FREE_TEST".toUpperCase (), knewAboutFreeTest.toUpperCase ());
		encounters.add (knewFreeTestResult);

		for (String str : howfoundarr) {
			if(!StringUtils.isEmptyOrWhitespaceOnly(str) && !str.trim().equalsIgnoreCase("other")){
			EncounterResults howDidYouLearnResults = ModelUtil.createEncounterResult (e, FieldTextMaps.getByDisplayText(str).getElementName().toUpperCase(), FieldTextMaps.getByDisplayText(str).getElementValue().toUpperCase());
			encounters.add (howDidYouLearnResults);
			}
		}

		if (otherHowFoundOut == null)
		{
			otherHowFoundOut = "";
		}

		EncounterResults otherHowDidYouLearnResults = ModelUtil.createEncounterResult (e, "OTHER_HOW_FOUND_OUT".toUpperCase (), otherHowFoundOut.toUpperCase ());
		encounters.add (otherHowDidYouLearnResults);
		
		EncounterResults smokingques1 = ModelUtil.createEncounterResult (e, "SMOKE_AWARE".toUpperCase (), smokeAware.toUpperCase ());
		encounters.add (smokingques1);
		
		EncounterResults smokingques2 = ModelUtil.createEncounterResult (e, "SMOKE_CURRENT".toUpperCase (), smokeCurrent.toUpperCase ());
		encounters.add (smokingques2);
		
		EncounterResults smokingques3 = ModelUtil.createEncounterResult (e, "SMOKE_PAST".toUpperCase (), smokePast.toUpperCase ());
		encounters.add (smokingques3);
		
		EncounterResults smokingques4 = ModelUtil.createEncounterResult (e, "SMOKELESS_CURRENT".toUpperCase (), smokelessCurrent.toUpperCase ());
		encounters.add (smokingques4);
		
		EncounterResults smokingques5 = ModelUtil.createEncounterResult (e, "SMOKELESS_PAST".toUpperCase (), smokelessPast.toUpperCase ());
		encounters.add (smokingques5);
		
		EncounterResults smokingques6 = ModelUtil.createEncounterResult (e, "SMOKE_SECOND".toUpperCase (), smokesecond.toUpperCase ());
		encounters.add (smokingques6);
		
		EncounterResults diabetesques7 = ModelUtil.createEncounterResult (e, "DIABETES_AWARE".toUpperCase (), diabetesaware.toUpperCase ());
		encounters.add (diabetesques7);
		
		EncounterResults diabetesques8 = ModelUtil.createEncounterResult (e, "DIABETES_DX".toUpperCase (), diabetesdx.toUpperCase ());
		encounters.add (diabetesques8);

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
				return XmlUtil.createErrorXml ("ERROR");
			}
		}
		xml = XmlUtil.createSuccessXml ();
		return xml;
	}

	private String doRefusal ()
	{
		String xml = null;
		String id = request.getParameter ("id");
		String chwId = request.getParameter ("chwid");
		String labId = request.getParameter ("labid");
		/* String pStatus = request.getParameter("ps"); */
		String whatRefused = request.getParameter ("wr");
		/*
		 * String collectionMonth = request.getParameter("cm"); String
		 * whichSample = request.getParameter("ws");
		 */

		String reason = request.getParameter ("rr");
		if (reason == null)
		{
			reason = "";
		}

		String startDate = request.getParameter ("sd");
		String startTime = request.getParameter ("st");
		String endTime = request.getParameter ("et");
		String enteredDate = request.getParameter ("ed");

		Screening scr = null;

		try
		{
			scr = ssl.findScreeningByPatientID (id);
		}
		catch (Exception e3)
		{
			e3.printStackTrace ();
			return XmlUtil.createErrorXml ("Error finding Screening");
		}

		if (scr == null)
		{
			return XmlUtil.createErrorXml ("Could not find Screening");
		}
		try{
			if(ssl.exists("treatmentrefusal", "where patientid = '"+id+"'")){
				return XmlUtil.createErrorXml ("Treatment Refusal for given id has already been filled and Patient's case has been closed.");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		Patient pat = null;
		if(whatRefused.toLowerCase().indexOf("consent") == -1){
			try
			{
				pat = ssl.findPatient(id);
			}
			catch (Exception e3)
			{
				e3.printStackTrace ();
				return XmlUtil.createErrorXml ("Error finding Patient");
			}
			if (pat == null)
			{
				return XmlUtil.createErrorXml ("Could not find Patient");
			}
		}

		if(whatRefused.toLowerCase().indexOf("mapping") == -1)
		{
			TreatmentRefusal tr = new TreatmentRefusal ();
			tr.setPatientId (id);
			tr.setElementRefused (whatRefused.toUpperCase ());
	
			if (reason == null)
				reason = "";
			tr.setReason (reason.toUpperCase ());
			try
			{
				tr.setCaseClosedOnDate (DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.FE_FORMAT));
			}
			catch (ParseException e2)
			{
				e2.printStackTrace ();
				return XmlUtil.createErrorXml ("Bad entered date. Please try again");
			}
	
			boolean trCreated = false;
	
			trCreated = ssl.saveTreatmentRefusal (tr);
	
			if (!trCreated)
			{
				return XmlUtil.createErrorXml ("Could not save treatment refusal");
			}
			//if it is not consent then  close case in patient table too for not being it is being checked above
			if(whatRefused.toLowerCase().indexOf("consent") == -1){
				pat.setPatientStatus("CLOSED");
				if(!ssl.updatePatient(pat)){
					return XmlUtil.createErrorXml ("Could not update patient status to CLOSED");
				}
			}
		}

		EncounterId encId = new EncounterId ();
		encId.setEncounterId (-1);
		encId.setPid1 (id.toUpperCase ());
		encId.setPid2 (chwId.toUpperCase ());

		Encounter e = new Encounter ();
		e.setId (encId);
		e.setEncounterType (EncounterType.REFUSAL);
		e.setDateEncounterStart (encounterStartDate);
		e.setDateEncounterEnd (encounterEndDate);
		e.setLocationId (labId);
		try
		{
			e.setDateEncounterEntered (DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.FE_FORMAT));
		}

		catch (Exception e1)
		{
			e1.printStackTrace ();
			return XmlUtil.createErrorXml ("Bad entered date. Please try again");
		}

		try
		{
			ssl.saveEncounter (e);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			XmlUtil.createErrorXml ("Could not create Encounter! Please try again!");
		}

		// TODO: Confirm Encounter Results
		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults> ();

		EncounterResults dateResult = ModelUtil.createEncounterResult (e, "entered_date".toUpperCase (), enteredDate.toUpperCase ());
		encounters.add (dateResult);

		EncounterResults whatRefusedResult = ModelUtil.createEncounterResult (e, "what_refused".toUpperCase (), whatRefused.toUpperCase ());
		encounters.add (whatRefusedResult);

		if (reason != null)
		{

			EncounterResults reasonResult = ModelUtil.createEncounterResult (e, "reason".toUpperCase (), reason.toUpperCase ());
			encounters.add (reasonResult);
		}

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
				return XmlUtil.createErrorXml ("ERROR");
			}
		}

		xml = XmlUtil.createSuccessXml ();
		return xml;
	}

	public String doScreeningLog ()
	{
		String xml = null;

		String chwId = request.getParameter ("chwid");
		String labId = request.getParameter ("labid");
		String males = request.getParameter ("male");
		String females = request.getParameter ("female");

		String startDate = request.getParameter ("sd");
		String startTime = request.getParameter ("st");
		String endTime = request.getParameter ("et");
		String enteredDate = request.getParameter ("ed");

		EncounterId encId = new EncounterId ();
		encId.setEncounterId (-1);
		encId.setPid1 (chwId.toUpperCase ());
		encId.setPid2 (chwId.toUpperCase ());

		Encounter e = new Encounter ();
		e.setId (encId);
		e.setEncounterType (EncounterType.SCREENING_LOG);
		e.setDateEncounterStart (encounterStartDate);
		e.setDateEncounterEnd (encounterEndDate);
		e.setLocationId (labId);
		try
		{
			e.setDateEncounterEntered (DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.FE_FORMAT));
		}

		catch (Exception e1)
		{
			e1.printStackTrace ();
			return XmlUtil.createErrorXml ("Bad entered date. Please try again");
		}

		try
		{
			ssl.saveEncounter (e);
		}
		catch (Exception e1)
		{
			// Auto-generated catch block
			e1.printStackTrace ();
			XmlUtil.createErrorXml ("Could not create Encounter! Please try again!");
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults> ();

		EncounterResults dateResult = ModelUtil.createEncounterResult (e, "entered_date".toUpperCase (), enteredDate.toUpperCase ());
		encounters.add (dateResult);

		EncounterResults xRayCollectedResult = ModelUtil.createEncounterResult (e, "males".toUpperCase (), males.toUpperCase ());
		encounters.add (xRayCollectedResult);

		EncounterResults reasonResult = ModelUtil.createEncounterResult (e, "females".toUpperCase (), females.toUpperCase ());
		encounters.add (reasonResult);

		boolean resultSave = true;

		for (int i = 0; i < encounters.size (); i++)
		{
			try
			{
				resultSave = ssl.saveEncounterResults (encounters.get (i));
			}
			catch (Exception e1)
			{
				// Auto-generated catch block
				e1.printStackTrace ();
				break;
			}

			if (!resultSave)
			{
				return XmlUtil.createErrorXml ("ERROR");
			}

		}

		xml = XmlUtil.createSuccessXml ();
		return xml;
	}

	private String doSputumBarcodeLink ()
	{

		// TODO - check that parameter names are same as those from form
		String id = request.getParameter ("id");
		String chwId = request.getParameter ("chwid");
		String sputumMonth = request.getParameter ("cm");
		String labId = request.getParameter ("labid");
		String labBarcode = request.getParameter ("labbarcode");
		String startDate = request.getParameter ("sd");
		String startTime = request.getParameter ("st");
		String endTime = request.getParameter ("et");
		String enteredDate = request.getParameter ("ed");

		int month = 0;

		if (sputumMonth.equals ("Baseline"))
		{
			month = 0;
		}
		else if (sputumMonth.equals ("2nd"))
		{
			month = 2;
		}
		else if (sputumMonth.equals ("3rd"))
		{
			month = 3;
		}
		else if (sputumMonth.equals ("5th"))
		{
			month = 5;
		}
		else if (sputumMonth.equals ("6th"))
		{
			month = 6;
		}
		else if (sputumMonth.equals ("7th"))
		{
			month = 7;
		}
		else if (sputumMonth.equals ("8th"))
		{
			month = 8;
		}
		else if (sputumMonth.equals ("9th"))
		{
			month = 9;
		}
		else if (sputumMonth.equals ("10th"))
		{
			month = 10;
		}
		SputumResults sr = null;

		try
		{
/*			for (Object s : ssl.findSputumResultsByPatientID (id))
			{
				if (((SputumResults) s).getMonth () == month)
				{
					sr = ((SputumResults) s);
					break;
				}
			}*/
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			return XmlUtil.createErrorXml ("Error finding test. Please try again");
		}

		if (sr == null)
		{
			return XmlUtil.createErrorXml ("Invalid PatientID/Month combination. Please recheck Patient ID or make sure Sputum Collection Form for given Patient and specified Month has been filled ");
		}

/*		if (!StringUtils.isEmptyOrWhitespaceOnly (sr.getSputumLabId ()))
		{
			return XmlUtil.createErrorXml ("Lab barcode already assigned to this test");
		}*/

		Boolean isCodeassigned = null;
		try
		{
			isCodeassigned = ssl.exists ("sputumresults", "where SputumLabID='" + labId + labBarcode + "'");
		}
		catch (Exception e2)
		{
			e2.printStackTrace ();
			return XmlUtil.createErrorXml ("Error tracking sputum results");
		}

		if (isCodeassigned == null)
		{
			return XmlUtil.createErrorXml ("Error tracking sputum results");
		}
		if (isCodeassigned)
		{
			return XmlUtil.createErrorXml ("This Lab barcode has already been assigned to another Sputum result");
		}

		//sr.setSputumLabId (labId + labBarcode);
		Boolean uCheck = false;
		try
		{
			uCheck = ssl.updateSputumResults (sr, false);
		}
		catch (Exception e)
		{
			return XmlUtil.createErrorXml ("Error saving test. Please try again");
		}

		if (uCheck == null || uCheck.booleanValue () == false)
		{
			return XmlUtil.createErrorXml ("Error saving test. Please try again");
		}

		EncounterId encId = new EncounterId ();
		encId.setPid1 (id.toUpperCase ());
		encId.setPid2 (chwId.toUpperCase ());

		Encounter e = new Encounter ();
		e.setId (encId);
		e.setEncounterType (EncounterType.SPUTUM_BARCODE);
		e.setDateEncounterStart (encounterStartDate);
		e.setDateEncounterEnd (encounterEndDate);
		e.setLocationId (labId);
		try
		{
			e.setDateEncounterEntered (DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.FE_FORMAT));
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			return XmlUtil.createErrorXml ("Bad entered date. Please try again");
		}

		Boolean eCreated = null;
		try
		{
			eCreated = ssl.saveEncounter (e);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
		}
		if (eCreated == null || !eCreated)
		{
			return XmlUtil.createErrorXml ("Error occurred while saving Encounter. Please try again");
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults> ();

		EncounterResults dateResult = ModelUtil.createEncounterResult (e, "entered_date".toUpperCase (), enteredDate);
		encounters.add (dateResult);

		EncounterResults barcodeResult = ModelUtil.createEncounterResult (e, "barcode".toUpperCase (), (labId + labBarcode).toUpperCase ());
		encounters.add (barcodeResult);

		EncounterResults monthResult = ModelUtil.createEncounterResult (e, "month".toUpperCase (), (month + "").toUpperCase ());
		encounters.add (monthResult);

		boolean resultSave = false;

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
				return XmlUtil.createErrorXml ("Error occurred. Please try again");
			}
		}
		return XmlUtil.createSuccessXml ();

	}

	private String doSputumCollection ()
	{
		String xml = null;

		String id = request.getParameter ("id");

		String chwId = request.getParameter ("chwid");

		// String patientStatus = request.getParameter("ps");
		String sputumMonth = request.getParameter ("cm");
		// String sampleNumber = request.getParameter("ws");
		String sputumCollected = request.getParameter ("sc");
		String instructionsGiven = request.getParameter ("ig");

		String collectionTime = request.getParameter ("sct");
		String sputumQuality = request.getParameter ("sq");
		// String barCode = request.getParameter("scb");
		String labId = request.getParameter ("labid");
		// String labBarcode = request.getParameter("lbc");

		String doGeneXpert = request.getParameter ("gxp");

		String startDate = request.getParameter ("sd");
		String startTime = request.getParameter ("st");
		String endTime = request.getParameter ("et");
		String enteredDate = request.getParameter ("ed");

		Patient pat = null;

		try
		{
			pat = ssl.findPatient (id);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			return XmlUtil.createErrorXml ("Error finding patient with id " + id + ". Please recheck ID and try again.");
		}

		if (pat == null)
		{
			return XmlUtil.createErrorXml ("Patient with id " + id + " does not exist. Please recheck ID and try again.");
		}

		// TODO - fix duplicate check
		// if(labBarcode!=null && labBarcode.length()!=0 && labId!=null &&
		// labId.length()!=0) {
		Boolean exists = null;

		/*
		 * try { exists = ssl.exists("sputumresults", " where SputumLabID='" +
		 * labId + labBarcode + "'"); } catch (Exception e2) {
		 * e2.printStackTrace(); return XmlUtil.createErrorXml(
		 * "Error tracking Bar Code Number. Please try again!"); }
		 * 
		 * if(exists==null) { System.out.println("null"); return
		 * XmlUtil.createErrorXml
		 * ("Error tracking Bar Code Number. Please try again!"); }
		 * 
		 * else if(exists.booleanValue()==true) { System.out.println("true");
		 * return XmlUtil.createErrorXml(
		 * "This Sputum ID has already been collected. Please recheck Bar Code and try again"
		 * ); }
		 */

		// check for existing sputum collection form

		// SputumResultsId sri1 = new SputumResultsId();
		int month = -1;

		if (sputumMonth.equals ("0"))
		{
			month = 0;
		}
		else if (sputumMonth.equals ("1"))
		{
			month = 1;
		}
		else if (sputumMonth.equals ("2"))
		{
			month = 2;
		}
		else if (sputumMonth.equals ("3"))
		{
			month = 3;
		}
		else if (sputumMonth.equals ("4"))
		{
			month = 4;
		}
		else if (sputumMonth.equals ("5"))
		{
			month = 5;
		}
		else if (sputumMonth.equals ("6"))
		{
			month = 6;
		}
		else if (sputumMonth.equals ("7"))
		{
			month = 7;
		}
		else if (sputumMonth.equals ("8"))
		{
			month = 8;
		}
		else if (sputumMonth.equals ("9"))
		{
			month = 9;
		}
		else if (sputumMonth.equals ("10"))
		{
			month = 10;
		}

		try
		{
			exists = ssl.exists ("sputumresults", " where PatientID='" + id + "' AND Month=" + month);
		}
		catch (Exception e2)
		{
			e2.printStackTrace ();
			return XmlUtil.createErrorXml ("Error tracking test history");
		}

		if (exists == null)
		{
			return XmlUtil.createErrorXml ("Error tracking test history. Please try again!");
		}

		else if (exists.booleanValue () == true)
		{
			System.out.println ("true");
			return XmlUtil.createErrorXml ("Month " + month + " sputum for Patient " + id + " has already been collected.");
		}
		// }

		if(sputumCollected != null && !sputumCollected.equalsIgnoreCase ("yes")){
			Boolean results = null;
			try
			{
				results = ssl.exists ("encounter",
						" where PID1='" + id + "'" + " AND EncounterType='" + EncounterType.SPUTUM_COLLECTION + "'" + " AND DateEncounterEntered='"
								+ DateTimeUtil.convertToSQL (enteredDate, DateTimeUtil.FE_FORMAT) + "'" + " ORDER BY DateEncounterEntered DESC");
			}
			catch (Exception e3)
			{
				e3.printStackTrace ();
			}

			if (results != null && results)
			{
				return XmlUtil.createErrorXml ("Patient ka " + enteredDate + " ka  ek Sputum Collection Form bhara ja chuka hay . Agar aap say Form bharnay may koi ghalti hui hay tu TBReach team say rujoo karen.");
			}
		}
		EncounterId encId = new EncounterId ();
		encId.setPid1 (id.toUpperCase ());
		encId.setPid2 (chwId.toUpperCase ());

		Encounter e = new Encounter ();
		e.setId (encId);
		e.setEncounterType (EncounterType.SPUTUM_COLLECTION);
		e.setDateEncounterStart (encounterStartDate);
		e.setDateEncounterEnd (encounterEndDate);
		e.setLocationId (labId);
		try
		{
			e.setDateEncounterEntered (DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.FE_FORMAT));
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			return XmlUtil.createErrorXml ("Bad entered date. Please try again");
		}

		try
		{
			ssl.saveEncounter (e);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			return XmlUtil.createErrorXml ("Error occurred. Please try again");
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults> ();

		EncounterResults dateResult = ModelUtil.createEncounterResult (e, "entered_date".toUpperCase (), enteredDate);
		encounters.add (dateResult);

		EncounterResults collectionMonthResult = ModelUtil.createEncounterResult (e, "collection_month".toUpperCase (), sputumMonth.toUpperCase ());
		encounters.add (collectionMonthResult);

		EncounterResults sputumCollectedResult = ModelUtil.createEncounterResult (e, "sputum_collected".toUpperCase (), sputumCollected.toUpperCase ());
		encounters.add (sputumCollectedResult);

		EncounterResults instructonsGivenResult = ModelUtil.createEncounterResult (e, "inst_given".toUpperCase (), instructionsGiven.toUpperCase ());
		encounters.add (instructonsGivenResult);

		if (sputumCollected != null && !sputumCollected.equalsIgnoreCase ("yes"))
		{
			collectionTime = "";
			sputumQuality = "";
			// labBarcode = "";
		}
		EncounterResults collectionTimeResult = ModelUtil.createEncounterResult (e, "collect_time".toUpperCase (), collectionTime.toUpperCase ());
		encounters.add (collectionTimeResult);

		EncounterResults sputumQualityResult = ModelUtil.createEncounterResult (e, "sputum_quality".toUpperCase (), sputumQuality.toUpperCase ());
		encounters.add (sputumQualityResult);

		/*
		 * EncounterResults labBarCodeResult = ModelUtil
		 * .createEncounterResult(e, "lab_barcode".toUpperCase(), (labId +
		 * labBarcode).toUpperCase()); encounters.add(labBarCodeResult);
		 */

		EncounterResults doGxpResult = ModelUtil.createEncounterResult (e, "do_gxp".toUpperCase (), doGeneXpert == null ? "" : doGeneXpert.toUpperCase ());
		encounters.add (doGxpResult);

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
				return XmlUtil.createErrorXml ("Error occurred. Please try again");
			}
		}

		if (sputumCollected != null && sputumCollected.equalsIgnoreCase ("yes"))
		{
			// //TODO: Check!
			// If baseline and gxp checked, do both tests
			// else if baseline or not-baseline, do sputum only
			// if(labBarcode!=null && labBarcode.length()!=0 && labId!=null &&
			// labId.length()!=0) {
			// SputumResultsId sri1 = new SputumResultsId();
			int month1 = -1;

			if (sputumMonth.equals ("0"))
			{
				month1 = 0;
			}
			else if (sputumMonth.equals ("1"))
			{
				month1 = 1;
			}
			else if (sputumMonth.equals ("2"))
			{
				month1 = 2;
			}
			else if (sputumMonth.equals ("3"))
			{
				month1 = 3;
			}
			else if (sputumMonth.equals ("4"))
			{
				month1 = 4;
			}
			else if (sputumMonth.equals ("5"))
			{
				month1 = 5;
			}
			else if (sputumMonth.equals ("6"))
			{
				month1 = 6;
			}
			else if (sputumMonth.equals ("7"))
			{
				month1 = 7;
			}
			else if (sputumMonth.equals ("8"))
			{
				month1 = 8;
			}
			else if (sputumMonth.equals ("9"))
			{
				month1 = 9;
			}
			else if (sputumMonth.equals ("10"))
			{
				month1 = 10;
			}

			// sri1.setPatientId(id);
			// sri1.setSputumTestId(Integer.parseInt(barCode));
			SputumResults sr = new SputumResults ();
			// sr.setSputumTestId(Integer.parseInt(barCode));
			sr.setPatientId (id);
/*			sr.setMonth (month1);
			if (instructionsGiven.equalsIgnoreCase ("yes"))
			{
				sr.setInstructionsGiven (new Boolean (true));
			}
			else
			{
				sr.setInstructionsGiven (new Boolean (false));
			}

			sr.setSmearOrderedBy (chwId.toUpperCase ());
			try
			{
				sr.setSmearOrderDate (DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.FE_FORMAT));
			}
			catch (ParseException e2)
			{
				e2.printStackTrace ();
				return XmlUtil.createErrorXml ("Error occurred. Please try again");
			}
*/
			// sr.setSputumId(Integer.parseInt(barCode));
			// sr.setSputumLabId(labId + labBarcode);

/*			sr.setSputumQuality (sputumQuality.toUpperCase ());

			// sr.setSputumProduced(sputumCollected.toUpperCase());

			if (doGeneXpert != null && doGeneXpert.equalsIgnoreCase ("yes"))
			{
				try
				{
					sr.setGeneXpertOrderedBy (chwId);
					sr.setGeneXpertOrderDate (DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.FE_FORMAT));
				}
				catch (ParseException e1)
				{
					e1.printStackTrace ();
					return XmlUtil.createErrorXml ("Error occurred. Please try again");
				}
			}*/

			// sr.setIrs(0);
			// sr.setDateSubmitted(encounterStartDate);

			ssl.saveSputumResults (sr);// TODO no try catch or check here

			// if(month==0) {

			// GXP
			/*
			 * GeneXpertResults gxp = new GeneXpertResults();
			 * gxp.setIsPositive(new Boolean(false)); gxp.setPatientId(id);
			 * gxp.setSputumTestId(Integer.parseInt(barCode)); gxp.setIrs(0);
			 */

			// ssl.saveGeneXpertResults(gxp);
			// }
			// }
		}
		xml = XmlUtil.createSuccessXml ();
		return xml;
	}

	private String doSputumResults ()
	{
		String xml = null;

		String id = request.getParameter ("id");
		String chwId = request.getParameter ("chwid").toUpperCase ();

		String labId = request.getParameter ("labid");
		//String labBarcode = request.getParameter ("lbc");
		//String testType = request.getParameter ("test");
		String smearResult = request.getParameter ("result");
		//String geneXpertResult = request.getParameter ("gxp");
		//String geneXpertResistance = request.getParameter ("gxpr");
		String facility = request.getParameter("facility");
		String enteredDate = request.getParameter ("date");

		Patient pat = null;
		SputumResults sr =null;
		
		sr = new SputumResults();
		try {
			sr.setDateSmearTested(DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.BE_FORMAT));
			sr.setLabNumber(Integer.parseInt(labId));
			sr.setPatientId(id);
			sr.setTreatmentFacility(facility);
			ssl.saveSputumResults(sr);
			
		} catch (ParseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
/*		try
		{
			pat = ssl.findPatient (id);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			return XmlUtil.createErrorXml ("Error finding patient with id " + id + ". Please recheck ID and try again.");
		}

		if (pat == null)
		{
			return XmlUtil.createErrorXml ("Patient with id " + id + " does not exist. Please recheck ID and try again.");
		}

		String pStatus = pat.getPatientStatus ();
*/
		/*
		 * // Need function to find Sputum Results by lab ID, lab barcode String
		 * result[] = null; try { result = ssl.getRowRecord("sputumresults", new
		 * String[] {"PatientID","SputumTestID"}, "SputumLabID='" + labBarcode +
		 * "' AND PatientID LIKE '"+ labId + "%'"); } catch (Exception e1) {
		 * e1.printStackTrace(); return
		 * XmlUtil.createErrorXml("Error finding test. Please try again!"); }
		 * 
		 * if(result==null || result.length==0) { return
		 * XmlUtil.createErrorXml("Error finding test. Please try again!"); }
		 * 
		 * String pid = result[0]; String testId = result[1];
		 */

/*		SputumResults sr;
		try
		{
			sr = ssl.findSputumResultsBySputumLabID (labId + labBarcode);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			return XmlUtil.createErrorXml ("Error finding test. Please try again!");
		}

		if (sr == null)
		{
			System.out.println ("null");
			return XmlUtil.createErrorXml ("Error finding test. Please try again!");
		}
*/
/*		if ((geneXpertResult != null || geneXpertResistance != null) && sr.getGeneXpertOrderDate () == null)
		{
			return XmlUtil.createErrorXml ("GeneXpert not ordered for this sample");
		}
		else if (smearResult != null && sr.getSmearOrderDate () == null)
		{
			return XmlUtil.createErrorXml ("Smear not ordered for this sample");
		}*/

/*		if (smearResult != null)
		{
			// /////////////////////////////////Maimoona/////////////////////////////////
			// done to restrict overwriting of previously entered sputum results
			if (!StringUtils.isEmptyOrWhitespaceOnly (sr.getSmearResult ()))
			{
				return XmlUtil.createErrorXml ("iss sample ka sputum result pehlay bhi enter kia ja chuka hay. agar form bharnay may koi ghlati hui hay tu Tbreach team say rujoo karen."
						+ "\nPehlay bharay gaey form ki Details darj zayl hen" + "\nSmearResult: " + sr.getSmearResult () + "\nSmearEnteredBy: " + sr.getSmearEnteredBy ());
			}
			// //////////////////////
			if (smearResult.trim ().equals ("1") || smearResult.trim ().equals ("2") || smearResult.trim ().equals ("3"))
			{
				smearResult = smearResult.trim () + "+";
			}

			sr.setSmearResult (smearResult.toUpperCase ());
			sr.setSmearTestDone (true);
			sr.setSmearEnteredBy (chwId.toUpperCase ());
			try
			{
				sr.setDateSmearTested (DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.FE_FORMAT));
			}
			catch (ParseException e1)
			{
				e1.printStackTrace ();
				return XmlUtil.createErrorXml ("Error occurred. Please try again");
			}
			if (!smearResult.equalsIgnoreCase ("NEGATIVE") && !pStatus.equalsIgnoreCase ("PATIENT"))
			{
				pat.setPatientStatus ("PATIENT");
				pat.setDiseaseConfirmed (new Boolean (true));
				try
				{
					ssl.updatePatient (pat);
				}
				catch (Exception e)
				{
					e.printStackTrace ();
					return XmlUtil.createErrorXml ("Error updating patient");
				}
			}
		}*/
/*		else if (geneXpertResult != null && geneXpertResistance != null)
		{
			// /////////////////////////////////Maimoona/////////////////////////////////
			// done to restrict overwriting of previously entered 'genexpert
			// result' in sputumResults row
			if (!StringUtils.isEmptyOrWhitespaceOnly (sr.getGeneXpertResult ()))
			{
				return XmlUtil.createErrorXml ("iss sample ka gxp result pehlay bhi enter kia ja chuka hay. agar form bharnay may koi ghlati hui hay tu Tbreach team say rujoo karen."
						+ "\nPehlay bharay gaey form ki Details darj zayl hen" + "\nSmearResult: " + sr.getSmearResult () + "\nSmearEnteredBy: " + sr.getSmearEnteredBy () + "\nGeneXpertResult: "
						+ sr.getGeneXpertResult () + "\nGeneXpertResistance: " + sr.getGeneXpertResistance () + "\nGeneXpertEnteredBy: " + sr.getGeneXpertEnteredBy ());
			}
			// //////////////////////
			sr.setGeneXpertEnteredBy (chwId.toUpperCase ());
			sr.setGeneXpertResult (geneXpertResult.toUpperCase ());
			sr.setGeneXpertResistance (geneXpertResistance.toUpperCase ());
			sr.setGeneXpertTestDone (true);
			try
			{
				sr.setDateGeneXpertTested (DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.FE_FORMAT));
			}
			catch (ParseException e1)
			{
				e1.printStackTrace ();
				return XmlUtil.createErrorXml ("Error occurred. Please try again");
			}
			int indexofMtb = geneXpertResult.toLowerCase().indexOf("mtb positive");
			if (indexofMtb != -1)
			{
				pat.setPatientStatus ("PATIENT");
				pat.setDiseaseConfirmed (new Boolean (true));
				try
				{
					ssl.updatePatient (pat);
				}
				catch (Exception e)
				{
					e.printStackTrace ();
					return XmlUtil.createErrorXml ("Error updating patient");
				}
			}
		}*/

		// TODO: Check the isPositive
/*		try
		{
			ssl.updateSputumResults (sr, false);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			return XmlUtil.createErrorXml ("Error saving test results");
		}
*/		EncounterId encId = new EncounterId ();
		encId.setPid1 (id.toUpperCase ());
		encId.setPid2 (chwId.toUpperCase ());

		Encounter e = new Encounter ();
		e.setId (encId);
		e.setEncounterType (EncounterType.SPUTUM_RESULTS);
		e.setDateEncounterStart (encounterStartDate);
		e.setDateEncounterEnd (encounterEndDate);
		e.setLocationId (labId);
		try
		{
			e.setDateEncounterEntered (DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.BE_FORMAT));
		}

		catch (Exception e1)
		{
			e1.printStackTrace ();
			return XmlUtil.createErrorXml ("Bad entered date. Please try again");
		}

		try
		{
			ssl.saveEncounter (e);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			return XmlUtil.createErrorXml ("Error occurred. Please try again");
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults> ();

		EncounterResults dateResult = ModelUtil.createEncounterResult (e, "entered_date".toUpperCase (), enteredDate);
		encounters.add (dateResult);
		
		EncounterResults labResult = ModelUtil.createEncounterResult (e, "lab_result".toUpperCase (), labId);
		encounters.add (labResult);
		
		EncounterResults chwResult = ModelUtil.createEncounterResult (e, "chw_result".toUpperCase (), chwId);
		encounters.add (chwResult);
		
		EncounterResults patResult = ModelUtil.createEncounterResult (e, "pat_result".toUpperCase (), id);
		encounters.add (patResult);
		
		EncounterResults facilityResult = ModelUtil.createEncounterResult (e, "facility_result".toUpperCase (), facility);
		encounters.add (facilityResult);
		
		EncounterResults smearResult1 = ModelUtil.createEncounterResult (e, "smear_result".toUpperCase (), facility);
		encounters.add (smearResult1);

/*		EncounterResults testTypeResult = ModelUtil.createEncounterResult (e, "test_type".toUpperCase (), testType.toUpperCase ());
		encounters.add (testTypeResult);
*/
		if (smearResult == null)
		{
			smearResult = "";
		}

/*		if (geneXpertResult == null)
		{
			geneXpertResult = "";
		}

		if (geneXpertResistance == null)
		{
			geneXpertResistance = "";
		}
*/
		EncounterResults smearResultResult = ModelUtil.createEncounterResult (e, "smear_result1".toUpperCase (), smearResult.toUpperCase ().substring(0, 8));
		encounters.add (smearResultResult);
		
		EncounterResults smearResultResult2 = ModelUtil.createEncounterResult (e, "smear_result2".toUpperCase (), smearResult.toUpperCase ().substring(9, 17));
		encounters.add (smearResultResult2);
		
	//	EncounterResults smearResultResult3 = ModelUtil.createEncounterResult (e, "smear_result3".toUpperCase (), smearResult.toUpperCase ().substring(17,);
		//encounters.add (smearResultResult3);

/*		EncounterResults gxpResultResult = ModelUtil.createEncounterResult (e, "gxp_result".toUpperCase (), geneXpertResult.toUpperCase ());
		encounters.add (gxpResultResult);

		EncounterResults gxpResistanceResult = ModelUtil.createEncounterResult (e, "gxp_resistance".toUpperCase (), geneXpertResistance.toUpperCase ());
		encounters.add (gxpResistanceResult);

		EncounterResults barcodeResult = ModelUtil.createEncounterResult (e, "barcode".toUpperCase (), (labId + labBarcode).toUpperCase ());
		encounters.add (barcodeResult);
*/
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
				return XmlUtil.createErrorXml ("Error occurred. Please try again");
			}
		}
		xml = XmlUtil.createSuccessXml ();
		return xml;
	}

	/*
	 * private String doSuspectConfirm() { String xml = null;
	 * 
	 * String id = request.getParameter("id");
	 * 
	 * String gpId = request.getParameter("gpid");
	 * 
	 * String cough = request.getParameter("cough"); String coughDuration =
	 * request.getParameter("cd"); String productiveCough =
	 * request.getParameter("pc"); String tbHistory =
	 * request.getParameter("tbh"); String tbFamilyHistory =
	 * request.getParameter("ftbh"); String fever = request.getParameter("fev");
	 * String nightSweat = request.getParameter("ns"); String weightLoss =
	 * request.getParameter("wl"); String haemoptysis =
	 * request.getParameter("ha"); String conclusion =
	 * request.getParameter("conc"); String startDate =
	 * request.getParameter("sd"); String startTime =
	 * request.getParameter("st"); String endTime = request.getParameter("et");
	 * String enteredDate = request.getParameter("ed");
	 * 
	 * if(cough==null) { return
	 * XmlUtil.createErrorXml("Phone update karain aur dobara form save karain"
	 * ); }
	 * 
	 * Patient pat = null; try { pat = (Patient) (ssl.findPatient(id)); } catch
	 * (Exception e3) { // Auto-generated catch block e3.printStackTrace(); }
	 * 
	 * if(pat==null) { return XmlUtil.createErrorXml("Patient with id " + id +
	 * " does not exist. Please recheck ID and try again."); }
	 * 
	 * if(pat.getPatientStatus()!=null &&
	 * !pat.getPatientStatus().equals("SUSPECT")) { return
	 * XmlUtil.createErrorXml
	 * ("Patient not in SUSPECT state! Confirmation may already have been done!"
	 * ); }
	 * 
	 * if (conclusion.equalsIgnoreCase("confirmed")) {
	 * pat.setDiseaseSuspected(new Boolean(true));
	 * pat.setPatientStatus("GP_CONF"); }
	 * 
	 * else { pat.setDiseaseSuspected(new Boolean(false));
	 * pat.setPatientStatus("GP_NO_CONF"); }
	 * 
	 * pat.setGpid(gpId.toUpperCase());
	 * 
	 * 
	 * try { boolean patUpdate = ssl.updatePatient(pat); } catch (Exception e2)
	 * { // Auto-generated catch block e2.printStackTrace();
	 * 
	 * return
	 * XmlUtil.createErrorXml("Could not update Patient. Please try again.");
	 * 
	 * }
	 * 
	 * EncounterId encId = new EncounterId(); encId.setPid1(id);
	 * encId.setPid2(gpId);
	 * 
	 * Encounter e = new Encounter(); e.setId(encId);
	 * e.setEncounterType("SUSPECTCON");
	 * e.setDateEncounterStart(encounterStartDate);
	 * e.setDateEncounterEnd(encounterEndDate); try {
	 * e.setDateEncounterEntered(DateTimeUtil.getDateFromString(enteredDate,
	 * DateTimeUtil.FE_FORMAT)); }
	 * 
	 * catch(Exception e1) { e1.printStackTrace(); return
	 * XmlUtil.createErrorXml("Bad entered date. Please try again"); }
	 * 
	 * try { boolean eCreated = ssl.saveEncounter(e); } catch (Exception e1) {
	 * // Auto-generated catch block e1.printStackTrace(); return
	 * XmlUtil.createErrorXml
	 * ("ERROR: Could not save encounter. Please try again");
	 * 
	 * }
	 * 
	 * ArrayList<EncounterResults> encounters = new
	 * ArrayList<EncounterResults>();
	 * 
	 * EncounterResults dateResult = ModelUtil.createEncounterResult(e,
	 * "entered_date".toUpperCase(), enteredDate); encounters.add(dateResult);
	 * 
	 * EncounterResults coughResult = ModelUtil.createEncounterResult(e,
	 * "cough".toUpperCase(), cough.toUpperCase()); encounters.add(coughResult);
	 * 
	 * if(coughDuration!=null) { EncounterResults coughDurationResult =
	 * ModelUtil.createEncounterResult(e, "cough_duration".toUpperCase(),
	 * coughDuration.toUpperCase()); encounters.add(coughDurationResult);
	 * 
	 * if(productiveCough!=null) { EncounterResults productiveCoughResult =
	 * ModelUtil.createEncounterResult(e, "productive_cough".toUpperCase(),
	 * productiveCough.toUpperCase()); encounters.add(productiveCoughResult);
	 * 
	 * } }
	 * 
	 * if(coughDuration!=null && productiveCough!=null &&
	 * (coughDuration.equals("2 to 3 weeks") ||
	 * coughDuration.equals("more than 3 weeks") ) &&
	 * productiveCough.equals("Yes")) { EncounterResults twoWeeksProdCoughResult
	 * = ModelUtil.createEncounterResult(e, "two_weeks_cough".toUpperCase(),
	 * "YES"); encounters.add(twoWeeksProdCoughResult); }
	 * 
	 * else { EncounterResults twoWeeksProdCoughResult =
	 * ModelUtil.createEncounterResult(e, "two_weeks_cough".toUpperCase(),
	 * "NO"); encounters.add(twoWeeksProdCoughResult); }
	 * 
	 * EncounterResults tbHistoryResult = ModelUtil.createEncounterResult(e,
	 * "tb_history".toUpperCase(), tbHistory.toUpperCase());
	 * encounters.add(tbHistoryResult);
	 * 
	 * EncounterResults tbFamilyHistoryResult =
	 * ModelUtil.createEncounterResult(e, "tb_family_history".toUpperCase(),
	 * tbFamilyHistory.toUpperCase()); encounters.add(tbFamilyHistoryResult);
	 * 
	 * EncounterResults feverResult = ModelUtil.createEncounterResult(e,
	 * "fever".toUpperCase(), fever.toUpperCase()); encounters.add(feverResult);
	 * 
	 * EncounterResults nsResult = ModelUtil.createEncounterResult(e,
	 * "night_sweats".toUpperCase(), nightSweat.toUpperCase());
	 * encounters.add(nsResult);
	 * 
	 * EncounterResults wlResult = ModelUtil.createEncounterResult(e,
	 * "weight_loss".toUpperCase(), weightLoss.toUpperCase());
	 * encounters.add(wlResult);
	 * 
	 * EncounterResults hResult = ModelUtil.createEncounterResult(e,
	 * "haemoptysis".toUpperCase(), haemoptysis.toUpperCase());
	 * encounters.add(hResult);
	 * 
	 * 
	 * EncounterResults concResult = ModelUtil.createEncounterResult(e,
	 * "conclusion".toUpperCase(), conclusion.toUpperCase());
	 * encounters.add(concResult);
	 * 
	 * boolean resultSave = true;
	 * 
	 * for (int i = 0; i < encounters.size(); i++) { try { resultSave =
	 * ssl.saveEncounterResults(encounters.get(i)); } catch (Exception e1) { //
	 * Auto-generated catch block e1.printStackTrace(); break; }
	 * 
	 * if (!resultSave) { return
	 * XmlUtil.createErrorXml("ERROR! Please try again"); }
	 * 
	 * }
	 * 
	 * 
	 * 
	 * 
	 * 
	 * xml = XmlUtil.createSuccessXml(); return xml; }
	 */
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
	private String doSuspectIdentification ()
	{
		
		String xml = null;
		String id = request.getParameter ("id");
		String chwId = request.getParameter ("chwid");
		// String gpId = request.getParameter("gpid");
		//String labid = request.getParameter ("labid");
		//String labtest = request.getParameter ("labtest");
		//String labother = request.getParameter ("labother");
		String location = request.getParameter("loc");
		 String firstName = request.getParameter("fn");
		 String lastName = request.getParameter("ln");
		String sex = request.getParameter ("sex");
		String sexString = "";
		String pid = "";
		String role = "";
		String labid = "";
		String tbHistory = request.getParameter ("tbh");
		String tbFamilyHistory = request.getParameter ("ftbh");
		String age = request.getParameter ("age");
		String cough = request.getParameter ("cough");
		String agetype = request.getParameter("agetype");
		String coughDuration = request.getParameter ("cd");
		String productiveCough = request.getParameter ("pc");
		String fever = request.getParameter ("fev");
		System.out.println("Request Parameters: "+"FirstName "+firstName+" LastName "+lastName+" CHWID "+chwId+ " cough "+cough+" productivecough "+productiveCough+" tbFamilyHistory "+tbFamilyHistory+" tbHistory "+tbHistory);

		long locationCurrentCount;
		String uniqueID = null;
		boolean sx = areRussianStringsEqual(sex,"M");
		if(sx==true)
		{
			sexString ="MALE";
		}
		else{
			sexString ="FEMALE";
		}
		
		String coughString = "";
		
		String ageString = "";
		
		boolean ax  = areRussianStringsEqual(agetype,"Руз");
		boolean ax1 = areRussianStringsEqual(agetype,"Мох");
		boolean ax2 = areRussianStringsEqual(agetype,"Сол");
		
		if(ax == true)
		{
			ageString = "DAYS";
		}
		else if(ax1 == true)
		{
			ageString = "MONTHS";
		}
		else if(ax2 == true)
		{
			ageString = "YEARS";
		}
		
		if(cough!=null){
		boolean cx = areRussianStringsEqual(cough,"Не");
		if(cx == true)
		{
			coughString = "NO";
		}
		else
		{
			coughString = "YES";
		}
		}

		String coughDurationString = "";
		boolean cduration = areRussianStringsEqual(coughDuration,"Камтар аз 2");
		boolean cduration1 = areRussianStringsEqual(coughDuration,"2-3 хафта");
		boolean cduration2 = areRussianStringsEqual(coughDuration,"Зиёд аз 3 хафта");
		boolean cduration3 = areRussianStringsEqual(coughDuration,"Намедонам");
		boolean cduration4 = areRussianStringsEqual(coughDuration,"Чавоб нест");
		if(cduration == true)
		{
			coughDurationString = "LESS THAN 2 WEEKS";
		}
		
		
		else if(cduration1 == true)
		{
			coughDurationString = "2-3 WEEKS";
		}
		
		else if(cduration2 == true)
		{
			coughDurationString = "MORE THAN 3 WEEKS";
		}
		
		else if(cduration3 == true)
		{
			coughDurationString = "DON'T KNOW";
		}
		
		else if(cduration4 == true)
		{
			coughDurationString = "REFUSE";
		}
		
		
		
		
		String productiveString = null;
		if(productiveCough!=null){
		
		boolean px = areRussianStringsEqual(productiveCough,"Не");
		
		if(px == true)
		{
			productiveString = "NO";
		}
		else
		{
			productiveString = "YES";
		}
		}
		
		
		String tbHistoryString = null;
		if(tbHistory!=null){
		
		
		boolean tbx = areRussianStringsEqual(tbHistory,"Не");
		
		if(tbx == true)
		{
			tbHistoryString = "NO";
		}
		else if(tbx == false)
		{
			System.out.println(tbHistory +" was translated as Yes");
			tbHistoryString = "YES";
		}
		else {
			System.out.println(tbHistory +" I will never be reached");
			tbHistoryString = tbHistory+"NEITHER YES NOR NO";
		}
		
		}
		
		
		
		//String tbMedication = request.getParameter ("tbmed");
		//String tbTreatmentDuration = request.getParameter ("tbtrt");
		
		String tbFamilyHistoryString = null;
		if(tbFamilyHistory!=null){
		
		
		boolean tbfx = areRussianStringsEqual(tbFamilyHistory,"Не");
		
		if(tbfx == true)
		{
			tbFamilyHistoryString = "NO";
		}
		else if(tbfx == false)
		{
			tbFamilyHistoryString = "YES";
		}
		else {
			System.out.println(tbFamilyHistory +" I will never be reached");
			tbFamilyHistoryString = tbFamilyHistory+"NEITHER YES NOR NO";
		}
		}
		
		
		String feverString = null;
		if(fever!=null){
		
		
		boolean fx = areRussianStringsEqual(fever,"Не");
		
		if(fx == true)
		{
			feverString = "NO";
		}
		else
		{
			feverString = "YES";
		}
		}
		
		
		String nightSweat = request.getParameter ("ns");
		String nightSweatString = null;
		if(nightSweat!=null){
		
		
		boolean nsx = areRussianStringsEqual(nightSweat,"Не");
		
		if(nsx ==true)
		{
			nightSweatString = "NO";
		}
		else
		{
			nightSweatString = "YES";
		}
		}
		String weightLoss = request.getParameter ("wl");
		String weightLossString = null;
		if(weightLoss!=null){
		
		
		boolean wlx = areRussianStringsEqual(weightLoss,"Не");
		
		if(wlx == true)
		{
			weightLossString = "NO";
		}
		else
		{
			weightLossString = "YES";
		}
		}
		
		String haemoptysis = request.getParameter ("ha");
		String haemoptysisString = null ;
		if(haemoptysis!=null){
		
		
		boolean stx = areRussianStringsEqual(haemoptysis,"Не");
		
		if(stx == true)
		{
			haemoptysisString = "NO";
		}
		else
		{
			haemoptysisString = "YES";
		}
		}
		
		
		String startDate = request.getParameter ("sd");
		String startTime = request.getParameter ("st");
		String endTime = request.getParameter ("et");
		String enteredDate = request.getParameter ("ed");
		String conclusion = request.getParameter ("conc");
		String conclusionString = "";
		
		boolean ccs = areRussianStringsEqual(conclusion,"Шубха ба сил");
		boolean ccseng = conclusion.equalsIgnoreCase("suspect");
		if(ccs == true || ccseng == true)
		{
			conclusionString = "SUSPECT";
		}
		else
		{
			conclusionString = "NOT a SUSPECT";
		}
				
/*		boolean ccs = conclusion.equalsIgnoreCase("suspect");
		
		if(ccs == true)
		{
			conclusionString = "SUSPECT";
		}
		else
		{
			conclusionString = "NOT a SUSPECT";
		}*/
		
		
		//String injectMedicine = request.getParameter ("inject");
		System.out.println (startDate);
		System.out.println (startTime);
		System.out.println (endTime);
		System.out.println (enteredDate);
		
		try
		{
			Users users = ssl.findUser (chwId);
			pid = users.getPid ();
			role = users.getRole ();
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			return XmlUtil.createErrorXml ("Cant find this user. Please try again");
		}
		
		LabMapping lab = null;
		try
		{
			lab = ssl.findMappingByPerson (pid);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
		}
		// lab should not be mendatory here as person not mapped on lab will
		// fail to login like admin etc
		
		if (lab != null)
		{
			labid = lab.getId ().getLocationId ();
		}
		Screening scr = new Screening ();
		if(conclusionString.equals("SUSPECT")) {
			try {
				Boolean b = ssl.exists ("screening", "substring(PatientID,1,2)='"+labid+"'");
				if(b==false)
				{
					locationCurrentCount=ssl.locationCount("screening", labid);
					locationCurrentCount++;
					String formattedString =  String.format("%06d", locationCurrentCount);
					uniqueID = labid+""+formattedString;
					
				}
				else
				{
				locationCurrentCount = ssl.countMaxPerLocation("screening",labid);
				locationCurrentCount++;
					String formattedString =  String.format("%06d", locationCurrentCount);
					uniqueID = labid+""+formattedString;
				}
					
				
			} catch (Exception e2) {
				
				e2.printStackTrace();
			}		
			
			
			Screening s = null;
			try
			{
				s = ssl.findScreeningByPatientID (uniqueID);
			}
			catch (Exception e)
			{
				e.printStackTrace ();
			}
	
			if (s != null)
			{
				return XmlUtil.createErrorXml ("The screening form of this Suspect ID " + uniqueID + " has been filled.");
			}
			scr.setPatientId (uniqueID);
			scr.setSuspect (true);
		}

		
		
		else /*if (!conclusionString.equalsIgnoreCase ("suspect"))*/
		{
			
			scr.setSuspect(false);
		}
		
		scr.setScreenLocation(labid);
		scr.setAge (Integer.parseInt (age));
		scr.setChwid (chwId.toUpperCase());
		scr.setCough (coughString);
		if (!StringUtils.isEmptyOrWhitespaceOnly (coughDuration))
		{
			scr.setCoughDuration (coughDurationString);
		}
		scr.setDateEnded (encounterEndDate);
/*		try
		{
			scr.setDateEntered (DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.FE_FORMAT));
		}
		catch (ParseException e1)
		{
			e1.printStackTrace ();
		}*/
		scr.setDateStarted (encounterStartDate);
		if(tbFamilyHistory!=null){
		scr.setFamilyTbhistory (tbFamilyHistoryString);}
		if(firstName!=null){
		scr.setFirstName(firstName.toUpperCase());
		scr.setLastName(lastName.toUpperCase());}
		scr.setGender (sexString.toUpperCase ().charAt(0));
		scr.setFever (feverString);
		scr.setHaemoptysis (haemoptysisString);
		scr.setNightSweat (nightSweatString);
		scr.setWeightLoss (weightLossString);
		scr.setScreeningId (0);
		if (!StringUtils.isEmptyOrWhitespaceOnly (productiveCough))
		{
			scr.setProductiveCough (productiveString);
		}
		
		
		
		
		// scr.setTbawareness(tbawareness);
		scr.setTbhistory (tbHistoryString);

		/*if (!StringUtils.isEmptyOrWhitespaceOnly (tbMedication) && tbMedication.equalsIgnoreCase ("yes"))
		{
			scr.setMedication (true);
			scr.setMedicationDuration (tbTreatmentDuration.toUpperCase());
		}
		else
		{
			scr.setMedication (false);
		}
		
		if (!StringUtils.isEmptyOrWhitespaceOnly (injectMedicine) && injectMedicine.equalsIgnoreCase ("yes"))
		{
			scr.setMedicineInjectible (true);
		}
		else
		{
			scr.setMedicineInjectible (false);
		}
			
					
		if (labtest.equalsIgnoreCase ("other"))
		{
			scr.setVisitedLabForTest (labother.toUpperCase ());
		}
		else
		{
			scr.setVisitedLabForTest (labtest.toUpperCase ());
		}*/

		try
		{
			scr.setDateEntered (DateTimeUtil.getDateFromString (enteredDate+" "+startTime, DateTimeUtil.FE_FORMAT));
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			return XmlUtil.createErrorXml ("The date is not in correct format! Please enter the Data and submit the form again");
		}

		try
		{
			ssl.saveScreening (scr);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
		if (conclusionString.equalsIgnoreCase ("suspect")){
		Date encounterdateentered = null;
		try
		{
			encounterdateentered = DateTimeUtil.getDateFromString (enteredDate+" "+startTime, DateTimeUtil.FE_FORMAT);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			return XmlUtil.createErrorXml ("Таърих нодуруст. Такрор кунед");
		}
		
		EncounterId encId = new EncounterId (0, uniqueID.toUpperCase(), chwId.toUpperCase());
		

		Encounter e = new Encounter (encId, EncounterType.SUSPECT_IDENTIFICATION, labid.toUpperCase(), encounterdateentered, encounterStartDate, encounterEndDate, "");

		try
		{
			ssl.saveEncounter (e);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			XmlUtil.createErrorXml ("Could not save encounter! Please try again!");
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults> ();

		EncounterResults dateResult = ModelUtil.createEncounterResult (e, "entered_date".toUpperCase (), enteredDate+" "+startTime);
		encounters.add (dateResult);
		
		EncounterResults ageResult = ModelUtil.createEncounterResult (e, "age".toUpperCase (), age);
		encounters.add (ageResult);
		
		EncounterResults ageStringResult = ModelUtil.createEncounterResult (e, "age_type".toUpperCase (), ageString);
		encounters.add (ageStringResult);
		if(firstName!=null){
		EncounterResults firstNameResult = ModelUtil.createEncounterResult (e, "first_name".toUpperCase (), firstName.toUpperCase());
		encounters.add (firstNameResult);
		
		EncounterResults lastNameResult = ModelUtil.createEncounterResult (e, "last_name".toUpperCase (), lastName.toUpperCase());
		encounters.add (lastNameResult);
		}
		EncounterResults phoneResult = ModelUtil.createEncounterResult (e, "gender".toUpperCase (), sexString);
		encounters.add (phoneResult);

		EncounterResults coughResult = ModelUtil.createEncounterResult (e, "cough".toUpperCase (), coughString);
		encounters.add (coughResult);

		EncounterResults coughDurationResult = ModelUtil.createEncounterResult (e, "cough_duration".toUpperCase (), (coughDuration == null ? "" : coughDurationString));
		encounters.add (coughDurationResult);

		EncounterResults productiveCoughResult = ModelUtil.createEncounterResult (e, "productive_cough".toUpperCase (), (productiveCough == null ? "" : productiveString));
		encounters.add (productiveCoughResult);

/*		EncounterResults tbMedicationResult = ModelUtil.createEncounterResult (e, "previous_tb_medication".toUpperCase (), (tbMedication == null ? "" : tbMedication.toUpperCase ()));
		encounters.add (tbMedicationResult);
		
		EncounterResults injectMedicineResult = ModelUtil.createEncounterResult (e, "injectible_tb_medication".toUpperCase (), (injectMedicine == null ? "" : injectMedicine.toUpperCase ()));
		encounters.add (injectMedicineResult);

		EncounterResults tbTreatmentResult = ModelUtil.createEncounterResult (e, "previous_tb_treatment_duration".toUpperCase (),
				(tbTreatmentDuration == null ? "" : tbTreatmentDuration.toUpperCase ()));
		encounters.add (tbTreatmentResult);
*/
		EncounterResults tbHistoryResult = ModelUtil.createEncounterResult (e, "tb_history".toUpperCase (), tbHistoryString);
		encounters.add (tbHistoryResult);

		EncounterResults tbFamilyHistoryResult = ModelUtil.createEncounterResult (e, "tb_family_history".toUpperCase (), tbFamilyHistoryString);
		encounters.add (tbFamilyHistoryResult);

		EncounterResults feverResult = ModelUtil.createEncounterResult (e, "fever".toUpperCase (), (fever == null ? "" : feverString));
		encounters.add (feverResult);

		EncounterResults nsResult = ModelUtil.createEncounterResult (e, "night_sweats".toUpperCase (), (nightSweat == null ? "" : nightSweatString));
		encounters.add (nsResult);

		EncounterResults wlResult = ModelUtil.createEncounterResult (e, "weight_loss".toUpperCase (), (weightLoss == null ? "" : weightLossString));
		encounters.add (wlResult);

		EncounterResults hResult = ModelUtil.createEncounterResult (e, "haemoptysis".toUpperCase (), (haemoptysis == null ? "" : haemoptysisString));
		encounters.add (hResult);

		EncounterResults concResult = ModelUtil.createEncounterResult (e, "conclusion".toUpperCase (), (conclusion == null ? "" : conclusionString));
		encounters.add (concResult);

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
				return XmlUtil.createErrorXml ("Хато шудааст. Такрор кунед");
			}
		}

		// if(conclusion!=null)
		// ssl.sendAlertsOnGPConfirmation(encId);
		uniqueID = uniqueID.replaceAll("..", "$0 ").trim();
		xml = XmlUtil.createSuccessXml (uniqueID);
		return xml;
		}
		
		xml = XmlUtil.createSuccessXml();
		return xml;

	}

	/*
	 * private String doSuspectIdentificationCT() {
	 * 
	 * String xml = null; String id = request.getParameter("id"); String
	 * contactMr = request.getParameter("cmr"); String chwId =
	 * request.getParameter("chwid"); String gpId =
	 * request.getParameter("gpid"); String firstName =
	 * request.getParameter("fn"); String lastName = request.getParameter("ln");
	 * String sex = request.getParameter("sex"); String age =
	 * request.getParameter("age"); String cough =
	 * request.getParameter("cough"); String coughDuration =
	 * request.getParameter("cd"); String productiveCough =
	 * request.getParameter("pc"); String tbHistory =
	 * request.getParameter("tbh");
	 * 
	 * String fever = request.getParameter("fev"); String nightSweat =
	 * request.getParameter("ns"); String weightLoss =
	 * request.getParameter("wl"); String haemoptysis =
	 * request.getParameter("ha"); String diabetes =
	 * request.getParameter("diab"); String contactAgeLessThanFive =
	 * request.getParameter("cage"); String startDate =
	 * request.getParameter("sd"); String startTime =
	 * request.getParameter("st"); String endTime = request.getParameter("et");
	 * String enteredDate = request.getParameter("ed");
	 * 
	 * 
	 * System.out.println(startDate); System.out.println(startTime);
	 * System.out.println(endTime); System.out.println(enteredDate);
	 * 
	 * // Transaction t = HibernateUtil.util.getSession().beginTransaction();
	 * if(cough==null) { return
	 * XmlUtil.createErrorXml("Phone update karain aur dobara form save karain"
	 * ); }
	 * 
	 * 
	 * Person checkPerson = null;
	 * 
	 * try { checkPerson = ssl.findPerson(id); } catch (Exception e2) { //
	 * Auto-generated catch block e2.printStackTrace(); }
	 * 
	 * if(checkPerson!=null) { return XmlUtil.createErrorXml("Person with ID " +
	 * id + " already exists!"); }
	 * 
	 * Date dob = getDOBFromAge(Integer.parseInt(age));
	 * 
	 * Person p = new Person(); p.setPid(id);
	 * p.setFirstName(firstName.toUpperCase());
	 * p.setLastName(lastName.toUpperCase());
	 * p.setGender(sex.toUpperCase().charAt(0)); p.setDob(dob);
	 * 
	 * boolean pCreated = true; try { pCreated = ssl.savePerson(p); } catch
	 * (Exception e) { // Auto-generated catch block e.printStackTrace(); }
	 * 
	 * if (!pCreated) { return XmlUtil
	 * .createErrorXml("Could not create Person. Please try again"); }
	 * 
	 * Patient pat = new Patient(); pat.setPatientId(id);
	 * pat.setDateRegistered(new Date());
	 * 
	 * 
	 * pat.setPatientStatus("SUSPECT"); pat.setDiseaseSuspected(new
	 * Boolean(false)); pat.setDiseaseConfirmed(new Boolean(false));
	 * 
	 * pat.setChwid(chwId.toUpperCase());
	 * 
	 * pat.setGpid(gpId.toUpperCase());
	 * 
	 * try { boolean patCreated = ssl.savePatient(pat); } catch (Exception e1) {
	 * // Auto-generated catch block e1.printStackTrace(); }
	 * 
	 * EncounterId encId = new EncounterId(); encId.setEncounterId(-1);
	 * encId.setPid1(id.toUpperCase());
	 * 
	 * encId.setPid2(chwId.toUpperCase());
	 * 
	 * Encounter e = new Encounter(); e.setId(encId);
	 * 
	 * e.setEncounterType("CT_SUSPECT");
	 * 
	 * e.setDateEncounterStart(encounterStartDate);
	 * e.setDateEncounterEnd(encounterEndDate); try {
	 * e.setDateEncounterEntered(DateTimeUtil.getDateFromString(enteredDate,
	 * DateTimeUtil.FE_FORMAT)); }
	 * 
	 * catch(Exception e1) { e1.printStackTrace(); return
	 * XmlUtil.createErrorXml("Bad entered date. Please try again"); }
	 * 
	 * try { boolean eCreated = ssl.saveEncounter(e); } catch (Exception e1) {
	 * // Auto-generated catch block e1.printStackTrace(); }
	 * 
	 * ArrayList<EncounterResults> encounters = new
	 * ArrayList<EncounterResults>();
	 * 
	 * // EncounterResultsId erId = new //
	 * EncounterResultsId(e.getId().getEncounterId(), e.getId().getPid1(), //
	 * e.getId().getPid2(), "entered_date"); // EncounterResults er = new
	 * EncounterResults(erId, enteredDate);
	 * 
	 * EncounterResults dateResult = ModelUtil.createEncounterResult(e,
	 * "entered_date".toUpperCase(), enteredDate); encounters.add(dateResult);
	 * 
	 * EncounterResults cmrResult = ModelUtil.createEncounterResult(e,
	 * "contact_mr".toUpperCase(), contactMr); encounters.add(cmrResult);
	 * 
	 * EncounterResults gpIdResult = ModelUtil.createEncounterResult(e,
	 * "gp_id".toUpperCase(), gpId.toUpperCase()); encounters.add(gpIdResult);
	 * 
	 * EncounterResults ageResult = ModelUtil.createEncounterResult(e,
	 * "age".toUpperCase(), age); encounters.add(ageResult);
	 * 
	 * EncounterResults phoneResult = ModelUtil.createEncounterResult(e,
	 * "phone".toUpperCase(), phone); encounters.add(phoneResult);
	 * 
	 * EncounterResults coughResult = ModelUtil.createEncounterResult(e,
	 * "cough".toUpperCase(), cough.toUpperCase()); encounters.add(coughResult);
	 * 
	 * if(coughDuration!=null) { EncounterResults coughDurationResult =
	 * ModelUtil.createEncounterResult(e, "cough_duration".toUpperCase(),
	 * coughDuration.toUpperCase()); encounters.add(coughDurationResult);
	 * 
	 * if(productiveCough!=null) { EncounterResults productiveCoughResult =
	 * ModelUtil.createEncounterResult(e, "productive_cough".toUpperCase(),
	 * productiveCough.toUpperCase()); encounters.add(productiveCoughResult);
	 * 
	 * } }
	 * 
	 * if(coughDuration!=null && productiveCough!=null &&
	 * (coughDuration.equals("2 to 3 weeks") ||
	 * coughDuration.equals("more than 3 weeks") ) &&
	 * productiveCough.equals("Yes")) { EncounterResults twoWeeksProdCoughResult
	 * = ModelUtil.createEncounterResult(e, "two_weeks_cough".toUpperCase(),
	 * "YES"); encounters.add(twoWeeksProdCoughResult); }
	 * 
	 * else { EncounterResults twoWeeksProdCoughResult =
	 * ModelUtil.createEncounterResult(e, "two_weeks_cough".toUpperCase(),
	 * "NO"); encounters.add(twoWeeksProdCoughResult); }
	 * 
	 * EncounterResults tbHistoryResult = ModelUtil.createEncounterResult(e,
	 * "tb_history".toUpperCase(), tbHistory.toUpperCase());
	 * encounters.add(tbHistoryResult);
	 * 
	 * if(fever!=null) { EncounterResults feverResult =
	 * ModelUtil.createEncounterResult(e, "fever".toUpperCase(),
	 * fever.toUpperCase()); encounters.add(feverResult); }
	 * 
	 * if(nightSweat!=null) { EncounterResults nsResult =
	 * ModelUtil.createEncounterResult(e, "night_sweats".toUpperCase(),
	 * nightSweat.toUpperCase()); encounters.add(nsResult); }
	 * 
	 * if(weightLoss!=null) { EncounterResults wlResult =
	 * ModelUtil.createEncounterResult(e, "weight_loss".toUpperCase(),
	 * weightLoss.toUpperCase()); encounters.add(wlResult); }
	 * 
	 * if(haemoptysis!=null) { EncounterResults hResult =
	 * ModelUtil.createEncounterResult(e, "haemoptysis".toUpperCase(),
	 * haemoptysis.toUpperCase()); encounters.add(hResult); }
	 * 
	 * if(diabetes!=null) { EncounterResults hResult =
	 * ModelUtil.createEncounterResult(e, "diabetes".toUpperCase(),
	 * diabetes.toUpperCase()); encounters.add(hResult); }
	 * 
	 * if(contactAgeLessThanFive!=null) { EncounterResults hResult =
	 * ModelUtil.createEncounterResult(e,
	 * "contact_less_than_five".toUpperCase(),
	 * contactAgeLessThanFive.toUpperCase()); encounters.add(hResult); }
	 * 
	 * boolean resultSave = true;
	 * 
	 * for (int i = 0; i < encounters.size(); i++) { try { resultSave =
	 * ssl.saveEncounterResults(encounters.get(i)); } catch (Exception e1) { //
	 * Auto-generated catch block e1.printStackTrace(); break; }
	 * 
	 * if (!resultSave) { return XmlUtil.createErrorXml("ERROR"); }
	 * 
	 * }
	 * 
	 * xml = XmlUtil.createSuccessXml(); return xml; }
	 */// ******************************************

	/*
	 * private String doSuspectVerify() {
	 * 
	 * 
	 * String xml = null; String id = request.getParameter("id"); String chwId =
	 * request.getParameter("chwid"); String gpId =
	 * request.getParameter("gpid"); String seen = request.getParameter("seen");
	 * String whyNotSeen = request.getParameter("wns"); String conclusion =
	 * request.getParameter("conc"); String whyNotVerfied =
	 * request.getParameter("wnv");
	 * 
	 * String takenTreatment = request.getParameter("tt"); String whereTaken =
	 * request.getParameter("wt");
	 * 
	 * String diagBefore = request.getParameter("pd"); String whereDiagnosed =
	 * request.getParameter("wd");
	 * 
	 * String indusLocation = request.getParameter("il"); String whereAtIndus =
	 * request.getParameter("wl");
	 * 
	 * String startDate = request.getParameter("sd"); String startTime =
	 * request.getParameter("st"); String endTime = request.getParameter("et");
	 * String enteredDate = request.getParameter("ed");
	 * 
	 * Patient pat = null; try { pat = (Patient) (ssl.findPatient(id)); } catch
	 * (Exception e3) { // Auto-generated catch block e3.printStackTrace(); }
	 * 
	 * if(pat==null) { return XmlUtil.createErrorXml("Patient with id " + id +
	 * " does not exist. Please recheck ID and try again."); }
	 * 
	 * if(pat.getPatientStatus()!=null &&
	 * !pat.getPatientStatus().equals("GP_CONF")) { return
	 * XmlUtil.createErrorXml
	 * ("Patient not in CONFIRMED state! Verification may already have been done!"
	 * ); }
	 * 
	 * if (conclusion.equalsIgnoreCase("Verified")) {
	 * 
	 * pat.setPatientStatus("VERIFIED");
	 * 
	 * }
	 * 
	 * else {
	 * 
	 * pat.setPatientStatus("NO_VERIFY"); }
	 * 
	 * //pat.setMonitorId(chwId); boolean patUpdate = false; try { patUpdate =
	 * ssl.updatePatient(pat); } catch (Exception e2) { // Auto-generated catch
	 * block e2.printStackTrace(); }
	 * 
	 * if(!patUpdate) { return
	 * XmlUtil.createErrorXml("Could not update patient with ID: " + id); }
	 * 
	 * EncounterId encId = new EncounterId(); encId.setEncounterId(-1);
	 * encId.setPid1(id); encId.setPid2(chwId);
	 * 
	 * Encounter e = new Encounter(); e.setId(encId);
	 * e.setEncounterType("SUSPECTVER");
	 * e.setDateEncounterStart(encounterStartDate);
	 * e.setDateEncounterEnd(encounterEndDate); try {
	 * e.setDateEncounterEntered(DateTimeUtil.getDateFromString(enteredDate,
	 * DateTimeUtil.FE_FORMAT)); }
	 * 
	 * catch(Exception e1) { e1.printStackTrace(); return
	 * XmlUtil.createErrorXml("Bad entered date. Please try again"); }
	 * 
	 * try { boolean eCreated = ssl.saveEncounter(e); } catch (Exception e1) {
	 * e1.printStackTrace(); }
	 * 
	 * ArrayList<EncounterResults> encounters = new
	 * ArrayList<EncounterResults>();
	 * 
	 * EncounterResults dateResult = ModelUtil.createEncounterResult(e,
	 * "entered_date".toUpperCase(), enteredDate); encounters.add(dateResult);
	 * 
	 * EncounterResults gpIdResult = ModelUtil.createEncounterResult(e,
	 * "gp_id".toUpperCase(), gpId.toUpperCase()); encounters.add(gpIdResult);
	 * 
	 * EncounterResults seenResult = ModelUtil.createEncounterResult(e,
	 * "patient_seen".toUpperCase(), seen.toUpperCase());
	 * encounters.add(seenResult);
	 * 
	 * if (seen.equals("No")) { EncounterResults whyNotSeenResult = ModelUtil
	 * .createEncounterResult(e, "why_not_seen".toUpperCase(),
	 * whyNotSeen.toUpperCase()); encounters.add(whyNotSeenResult); }
	 * //----------------
	 * 
	 * else {
	 * 
	 * if(takenTreatment!=null) { EncounterResults takenTreatmentResult =
	 * ModelUtil .createEncounterResult(e, "taken_treatment".toUpperCase(),
	 * takenTreatment.toUpperCase()); encounters.add(takenTreatmentResult); } if
	 * (whereTaken != null) { EncounterResults whereTreatmentResult = ModelUtil
	 * .createEncounterResult(e, "where_treatment" .toUpperCase(),
	 * whereTaken.toUpperCase()); encounters.add(whereTreatmentResult); }
	 * 
	 * if(diagBefore!=null) { EncounterResults diagBeforeResult = ModelUtil
	 * .createEncounterResult(e, "previous_diagnosis" .toUpperCase(),
	 * diagBefore.toUpperCase()); encounters.add(diagBeforeResult); }
	 * 
	 * if (whereDiagnosed != null) { EncounterResults whereDiagnosedResult =
	 * ModelUtil .createEncounterResult(e, "where_diagnosed" .toUpperCase(),
	 * whereDiagnosed.toUpperCase()); encounters.add(whereDiagnosedResult); }
	 * 
	 * if(indusLocation != null) { EncounterResults indusEncounterResults =
	 * ModelUtil .createEncounterResult(e, "indus_location".toUpperCase(),
	 * indusLocation.toUpperCase()); encounters.add(indusEncounterResults); } if
	 * (whereAtIndus != null) { EncounterResults otherIndusResult = ModelUtil
	 * .createEncounterResult(e, "other_indus_location" .toUpperCase(),
	 * whereAtIndus.toUpperCase()); encounters.add(otherIndusResult); }
	 * 
	 * } //----------------
	 * 
	 * EncounterResults concResult = ModelUtil.createEncounterResult(e,
	 * "conclusion".toUpperCase(), conclusion.toUpperCase());
	 * encounters.add(concResult);
	 * 
	 * if (conclusion.equals("Not Verified")) { EncounterResults
	 * whyNotVerifiedResult = ModelUtil .createEncounterResult(e,
	 * "why_not_verified".toUpperCase(), whyNotVerfied.toUpperCase());
	 * encounters.add(whyNotVerifiedResult); }
	 * 
	 * boolean resultSave = true;
	 * 
	 * for (int i = 0; i < encounters.size(); i++) { try { resultSave =
	 * ssl.saveEncounterResults(encounters.get(i)); } catch (Exception e1) {
	 * e1.printStackTrace(); break; }
	 * 
	 * if (!resultSave) { return XmlUtil.createErrorXml("ERROR"); }
	 * 
	 * }
	 * 
	 * xml = XmlUtil.createSuccessXml(); return xml; }
	 */

	private String doXrayReceiving ()
	{
		String id = request.getParameter ("id");
		String chwId = request.getParameter ("chwid").toUpperCase ();
		String labId = request.getParameter ("labid");
		String labBarcode = request.getParameter ("lbc");
		String xrayRecv = request.getParameter ("xrcv");
		String reason = request.getParameter ("rsn");

		String enteredDate = request.getParameter ("ed");

		Patient pat = null;

		try
		{
			pat = ssl.findPatient (id);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}

		if (pat == null)
		{
			return XmlUtil.createErrorXml ("Patient with id " + id + " was not found. Please recheck ID and try again. Agar iss ID ka Registration-A form fill nahi hua tu pehlay wo fill karen.");
		}

		Object[] patXrayres = null;
		try
		{
			patXrayres = ssl.findXrayResultsByPatient (id);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}

		if (patXrayres != null && patXrayres.length > 0)
		{
			String details = "";
			for (Object obj : patXrayres)
			{
				XrayResults xx = (XrayResults) obj;
				details += "--------" + "\nXrayDate: " + (xx.getXrayDate () == null ? "" : new SimpleDateFormat ("yyyy-MMM-dd").format (xx.getXrayDate ())) + "\nLabId - Labbarcode: "
						+ xx.getXrayLabId ().substring (0, LAB_ID_LN) + "-" + xx.getXrayLabId ().substring (LAB_ID_LN) + "\nXrayResult: "
						+ (xx.getXrayResult () == null ? "NOT AVAILABLE" : xx.getXrayResult ()) + "\nXrayOrderedBy: " + (xx.getXrayOrderedBy () == null ? "" : xx.getXrayOrderedBy ());
			}
			return XmlUtil.createErrorXml ("Xray for given Patient ID have been ordered. Details are :\n" + details);
		}

		Boolean exists = null;

		try
		{
			exists = ssl.exists ("encounter", " where PID1='" + id + "'" + " AND EncounterType='" + EncounterType.XRAY_RECV + "'");
		}
		catch (Exception e3)
		{
			e3.printStackTrace ();
		}

		if (exists == null)
		{
			return XmlUtil.createErrorXml ("Error tracking Patient ID. Try again");
		}
		else if (exists)
		{
			return XmlUtil.createErrorXml ("Patient " + id + " ka Recieving Form pehlay bhara ja chuka hai. Agar aap se form bharnay mein koi ghalati huwi hai to TB Reach team se rujoo karain");
		}

		if (xrayRecv.equalsIgnoreCase ("yes"))
		{
			XrayResults existxray = null;
			try
			{
				existxray = ssl.findXrayResultsByXRayTestID (labId + labBarcode);
			}
			catch (Exception e)
			{
				e.printStackTrace ();
			}
			if (existxray != null)
			{
				return XmlUtil.createErrorXml ("An Xray with given lab barcode already exists. Details are :" + "\nPatientID: " + existxray.getPatientId () + "\nXrayDate: "
						+ (existxray.getXrayDate () == null ? "" : new SimpleDateFormat ("yyyy-MMM-dd").format (existxray.getXrayDate ())) + "\nLabId - Labbarcode: "
						+ existxray.getXrayLabId ().substring (0, LAB_ID_LN) + "-" + existxray.getXrayLabId ().substring (LAB_ID_LN) + "\nXrayResult: "
						+ (existxray.getXrayResult () == null ? "NOT AVAILABLE" : existxray.getXrayResult ()) + "\nXrayOrderedBy: "
						+ (existxray.getXrayOrderedBy () == null ? "" : existxray.getXrayOrderedBy ()));
			}

			XrayResults xr = new XrayResults ();
			xr.setPatientId (id);
			xr.setXrayOrderedBy (chwId.toUpperCase ());
			try
			{
				xr.setXrayDate (DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.FE_FORMAT));
			}
			catch (ParseException e)
			{
				e.printStackTrace ();
				return XmlUtil.createErrorXml ("Error handling date. Please try again");
			}

			xr.setXrayLabId (labId + labBarcode);

			try
			{
				ssl.saveXrayResults (xr);
			}
			catch (Exception e)
			{
				e.printStackTrace ();
				return XmlUtil.createErrorXml ("Error saving XRay. Please try again");
			}
		}

		EncounterId encId = new EncounterId ();
		encId.setEncounterId (-1);
		encId.setPid1 (id.toUpperCase ());
		encId.setPid2 (chwId.toUpperCase ());

		Encounter e = new Encounter ();
		e.setId (encId);
		e.setEncounterType (EncounterType.XRAY_RECV);
		e.setDateEncounterStart (encounterStartDate);
		e.setDateEncounterEnd (encounterEndDate);
		e.setLocationId (labId);
		try
		{
			e.setDateEncounterEntered (DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.FE_FORMAT));
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			return XmlUtil.createErrorXml ("Bad entered date. Please try again");
		}

		try
		{
			ssl.saveEncounter (e);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			XmlUtil.createErrorXml ("Could not create Encounter! Please try again!");
		}

		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults> ();

		EncounterResults dateResult = ModelUtil.createEncounterResult (e, "entered_date".toUpperCase (), enteredDate.toUpperCase ());
		encounters.add (dateResult);

		EncounterResults xRayCollectedResult = ModelUtil.createEncounterResult (e, "xray_done".toUpperCase (), xrayRecv.toUpperCase ());
		encounters.add (xRayCollectedResult);

		if (reason == null)
		{
			reason = "";
		}

		EncounterResults reasonResult = ModelUtil.createEncounterResult (e, "reason".toUpperCase (), reason.toUpperCase ());
		encounters.add (reasonResult);

		if (labBarcode == null)
		{
			labBarcode = "";
		}
		else
		{
			labBarcode = labId + labBarcode;
		}
		EncounterResults lbcResult = ModelUtil.createEncounterResult (e, "lab_bar_code".toUpperCase (), (labBarcode).toUpperCase ());
		encounters.add (lbcResult);

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
				return XmlUtil.createErrorXml ("ERROR");
			}
		}

		String xml = XmlUtil.createSuccessXml ();
		return xml;
	}

	private String doXrayResults ()
	{
		String xml = null;

		String chwId = request.getParameter ("chwid").toUpperCase ();
		String labId = request.getParameter ("labId");
		String labBarcode = request.getParameter ("lbc");
		String technician = request.getParameter ("tech");
		String remarks = request.getParameter ("remarks");
		String conclusion = request.getParameter ("conc");
		String enteredDate = request.getParameter ("ed");

		String patientId = "";
		/*
		 * try { pat = ssl.findPatient(id); } catch (Exception e) {
		 * e.printStackTrace(); return
		 * XmlUtil.createErrorXml("Error finding patient with id " + id +
		 * ". Please recheck ID and try again."); }
		 * 
		 * if(pat==null) { return XmlUtil.createErrorXml("Patient with id " + id
		 * + " does not exist. Please recheck ID and try again."); }
		 */

		XrayResults xr = null;

		try
		{
			xr = ssl.findXrayResultsByXRayTestID (labId + labBarcode);
		}
		catch (Exception e2)
		{
			e2.printStackTrace ();
			return XmlUtil.createErrorXml ("Could not find XRay");
		}

		if (xr == null)
		{
			return XmlUtil.createErrorXml ("Could not find XRay");
		}

		if (!StringUtils.isEmptyOrWhitespaceOnly (xr.getXrayResult ()))
		{
			return XmlUtil
					.createErrorXml ("Iss lab barcode ka Xray Result Form bhara ja chuka hay. Agar form bharnay may koi ghalti hui hay tu Tbreach team say rujoo karen. Mojooda Xray Result ki Details darj zayl hen"
							+ "\nPatientID: "
							+ xr.getPatientId ()
							+ "\nXrayResult: "
							+ (xr.getXrayResult () == null ? "NOT AVAILABLE" : xr.getXrayResult ())
							+ "\nXrayDate: "
							+ (xr.getXrayDate () == null ? "" : new SimpleDateFormat ("yyyy-MMM-dd").format (xr.getXrayDate ()))
							+ "\nXrayOrderedBy: "
							+ (xr.getXrayOrderedBy () == null ? "" : xr.getXrayOrderedBy ())
							+ "\nDateReported: "
							+ xr.getDateReported ()
							+ "\nRemarks: "
							+ xr.getRemarks ()
							+ "\nTestedBy: "
							+ xr.getTestedBy ());
		}

		if (technician == null)
		{
			technician = "";
		}
		if (remarks == null)
		{
			remarks = "";
		}
		if (conclusion == null)
		{
			conclusion = "";
		}

		try
		{
			xr.setDateReported (DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.FE_FORMAT));
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			return XmlUtil.createErrorXml ("Error updating XRay Result. Invalid Date.");
		}

		xr.setXrayResult (conclusion.toUpperCase ());
		xr.setRemarks (remarks.toUpperCase ());
		xr.setTestedBy (technician.toUpperCase ());
		patientId = xr.getPatientId ();
		try
		{
			ssl.updateXrayResults (xr);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			return XmlUtil.createErrorXml ("Could not save X Ray. Please try again.");
		}

		EncounterId encId = new EncounterId ();
		encId.setEncounterId (-1);
		encId.setPid1 (patientId.toUpperCase ());
		encId.setPid2 (chwId.toUpperCase ());

		Encounter e = new Encounter ();
		e.setId (encId);
		e.setEncounterType (EncounterType.XRAY_RESULTS);
		e.setDateEncounterStart (encounterStartDate);
		e.setDateEncounterEnd (encounterEndDate);
		e.setLocationId (labId);
		try
		{
			e.setDateEncounterEntered (DateTimeUtil.getDateFromString (enteredDate, DateTimeUtil.FE_FORMAT));
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			return XmlUtil.createErrorXml ("Bad entered date. Please try again");
		}

		try
		{
			ssl.saveEncounter (e);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			XmlUtil.createErrorXml ("Could not create Encounter! Please try again!");
		}
		ArrayList<EncounterResults> encounters = new ArrayList<EncounterResults> ();

		EncounterResults dateResult = ModelUtil.createEncounterResult (e, "entered_date".toUpperCase (), enteredDate.toUpperCase ());
		encounters.add (dateResult);

		EncounterResults technicianResult = ModelUtil.createEncounterResult (e, "technician".toUpperCase (), technician.toUpperCase ());
		encounters.add (technicianResult);

		EncounterResults remarksResult = ModelUtil.createEncounterResult (e, "remarks".toUpperCase (), (remarks.length () < 50 ? remarks.toUpperCase () : remarks.substring (0, 49).toUpperCase ()));
		encounters.add (remarksResult);

		EncounterResults conclusionResult = ModelUtil.createEncounterResult (e, "conclusion".toUpperCase (), conclusion.toUpperCase ());
		encounters.add (conclusionResult);

		EncounterResults lbcResult = ModelUtil.createEncounterResult (e, "lab_bar_code".toUpperCase (), (labBarcode).toUpperCase ());
		encounters.add (lbcResult);

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
				return XmlUtil.createErrorXml ("ERROR");
			}
		}

		xml = XmlUtil.createSuccessXml ();

		return xml;
	}

	/*
	 * private long getAgeInYears(Date dob) { long age = -1;
	 * 
	 * GregorianCalendar gc = (GregorianCalendar)
	 * GregorianCalendar.getInstance(); gc.setTime(dob);
	 * 
	 * GregorianCalendar gcNow = (GregorianCalendar)
	 * GregorianCalendar.getInstance();
	 * gcNow.setTimeInMillis(System.currentTimeMillis());
	 * 
	 * long diff = gcNow.getTimeInMillis() - gc.getTimeInMillis();
	 * System.out.println("DIFF:" + diff);
	 * 
	 * double diffInSeconds = diff/1000; System.out.println(diffInSeconds);
	 * double diffInMinutes = diffInSeconds/60;
	 * System.out.println(diffInMinutes); double diffInHours = diffInMinutes/60;
	 * System.out.println(diffInHours); double diffInDays = diffInHours/24;
	 * System.out.println(diffInDays); double diffInYears = diffInDays/365;
	 * System.out.println(diffInYears);
	 * 
	 * Double ageDbl = new Double(diffInYears);
	 * 
	 * return ageDbl.longValue(); }
	 */

	public Date getDOBFromAge (int age)
	{
		Calendar c = Calendar.getInstance ();
		c.setTime (new Date (System.currentTimeMillis ()));
		c.set (Calendar.YEAR, c.get (Calendar.YEAR) - age);
		c.set (Calendar.MONTH, 5);
		c.set (Calendar.DATE, 30);
		return c.getTime ();
	}

	public String getBaselineTreatmentInfo (String id)
	{
		String xml = null;

		Screening scr = null;
		try
		{
			scr = ssl.findScreeningByPatientID (id);
		}
		catch (Exception e4)
		{
			e4.printStackTrace ();
		}

		if (scr == null)
		{
			return XmlUtil.createErrorXml ("Patient does not exist. Please confirm ID and try again");
		}

		Patient pat = null;
		try
		{
			pat = ssl.findPatient (id);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
		}

		if (pat == null)
		{
			return XmlUtil.createErrorXml ("Di gai id ki Screening hui hay lekin disease confirmed nahi ho saki hay. Baraey meherbani tamam forms tarteeb say fill karen");
		}

		if (pat.getDiseaseConfirmed () == null || !pat.getDiseaseConfirmed ())
		{
			return XmlUtil.createErrorXml ("Di gai ID ka status " + pat.getPatientStatus ().toLowerCase ()
					+ " hay or TB confirm nahin hui. Baraey meherbani tamam forms tarteeb say fill karen or agar Patient k Tests k results k form fill nahi huay tu pehlay wo fill karen.");
		}

		Boolean exists = null;

		try
		{
			exists = ssl.exists ("encounter", " where PID1='" + id + "'" + " AND EncounterType='" + EncounterType.BASELINE + "'");
		}
		catch (Exception e3)
		{
			e3.printStackTrace ();
		}

		if (exists == null)
		{
			return XmlUtil.createErrorXml ("Error tracking Patient ID");
		}
		else if (exists)
		{
			return XmlUtil.createErrorXml ("Patient " + id
					+ " ka Baseline Treatment Form pehlay bhara ja chuka hai. Agar aap se form bharnay mein koi ghalati huwi hai to TB Reach team se rujoo karain");
		}

		// ADDED BY ALI - RIF RESISTANCE CHECK
		Boolean rifCheck = null;
		

		try
		{
			rifCheck = ssl.exists ("sputumresults", "where PatientID='" + id + "' AND GeneXpertResistance='RIF RESISTANT'");
		}
		catch (Exception e4)
		{
			e4.printStackTrace ();
			return XmlUtil.createErrorXml ("Error tracking Patient. Please try again");
		}
		
		
//ADD HISTORY OF INJECTIBLE MEDICINE, IF RESISTANT AND INJECTED, BASELINE DISBALED
	/*	if (rifCheck.booleanValue () == true)
		{
			return XmlUtil.createErrorXml ("Ye Patient RIF Resistant hai. Patient ko Indus Hospital refer karain.");
		}*/
		
		
		// END RIF RESISTANCE CHECK

		// get base sputum results, gxp results, gxp resistance, chest x-ray
		//

		
		String[] sputumResults = null;
		int numResults = -1;
		try
		{
			sputumResults = ssl.getColumnData ("sputumresults", "SmearResult", " where PatientID='" + id + "' " + " AND SmearTestDone=1" + " AND Month=0 " + " AND SmearResult IS NOT NULL "
					+ " AND SmearOrderDate IS NOT NULL " + " AND (Details IS NULL OR Details NOT LIKE '%REJECTED%') " + " ORDER BY SmearOrderDate ASC");
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			return XmlUtil.createErrorXml ("Error finding base Sputum Results for patient with id " + id + ". Please try again.");
		}

		/*
		 * if(sputumResults==null || sputumResults.length == 0) { return
		 * XmlUtil.
		 * createErrorXml("Could not find base Sputum Results for patient with id "
		 * + id + ". Please try again."); }
		 */
		String gender = "";
		String tbHIST = "";
		Boolean injectHIST = null;
		Integer ageLess=0;
		Person per = null;
		try
		{
			per = ssl.findPerson (id);
		}
		catch (Exception e3)
		{
			e3.printStackTrace ();
		}

		if (per == null)
		{
			return XmlUtil.createErrorXml ("Person not found. Please confirm ID exists and try again");
		}

		gender = Character.toString (per.getGender ());
		ageLess = scr.getAge();
		tbHIST = scr.getTbhistory().toString();
		//injectHIST = scr.getMedicineInjectible();
		System.out.println(injectHIST+ " Patient Injectible History.........");
		System.out.println(tbHIST+" Patient Dependency.........");
		String baseSmear = " ";
		String baseGxp = " ";
		String baseGxpRes = " ";
		String cxr = " ";
		String patYpe = " ";
		String patCategory = " "; 
		String ageChecker = " ";

		if (sputumResults != null)
		{
			numResults = sputumResults.length;

			for (int i = 0; i < numResults; i++)
			{
				baseSmear += sputumResults[i] + " ";
			}
		}

		// get base Gxp results

		String[][] gxpResults = null;
		try
		{
			gxpResults = ssl.getTableData ("sputumresults", new String[] {"GeneXpertTestDone",// not
																								// necessarily
																								// needed
					"GeneXpertResult", "GeneXpertResistance"}, " where PatientID='" + id + "' " + " AND GeneXpertTestDone=1" + " AND Month=0 " + " AND GeneXpertResult IS NOT NULL "
					+ " AND GeneXpertOrderDate IS NOT NULL " + " ORDER BY GeneXpertOrderDate ASC");
		}
		catch (Exception e2)
		{
			e2.printStackTrace ();
		}

		if (gxpResults != null)
		{
			for (int i = 0; i < gxpResults.length; i++)
			{
				if (!StringUtils.isEmptyOrWhitespaceOnly (gxpResults[i][1]))
				{
					baseGxp += gxpResults[i][1];// column: GeneXpertResult
				}
				else
					baseGxp += "N/A" + " ";

				if (!StringUtils.isEmptyOrWhitespaceOnly (gxpResults[i][2]))
					baseGxpRes += gxpResults[i][2] + " ";// column:
															// GeneXpertResistance
				else
					baseGxpRes += "N/A" + " ";
			}
		}

		String[] xRayResult = null;

		try
		{
			xRayResult = ssl.getColumnData ("xrayresults", "XRayResult", " where PatientId='" + id + "' " + " AND DateReported IS NOT NULL " + "ORDER BY DateReported ASC");
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
		}

		if (xRayResult != null)
		{
			for (int i = 0; i < xRayResult.length; i++)
			{
				if (!StringUtils.isEmptyOrWhitespaceOnly (xRayResult[i]))
					cxr += xRayResult[i] + " ";
			}
		}

		/*
		 * String[] er = null; try { er = ssl.getColumnData("EncounterResults",
		 * "Value", " where PID1='"+ id +
		 * "' AND Element='PAST_TB_DRUG_HISTORY_D1'"); } catch (Exception e) {
		 * e.printStackTrace(); }
		 * 
		 * String tbhist; if(er!=null && er.length > 0) tbhist = er[0]; else {
		 * try { er = ssl.getColumnData("EncounterResults", "Value",
		 * " where PID1='"+ id + "' AND Element='PAST_TB_DRUG_HISTORY'"); }
		 * catch (Exception e) { e.printStackTrace(); }
		 * 
		 * if(er!=null && er.length>0) tbhist = er[0]; else tbhist =
		 * "Not Entered"; }
		 */if(injectHIST!=null){
		if (rifCheck.booleanValue () == true && tbHIST.equalsIgnoreCase("yes") && injectHIST.booleanValue()== true)
		{
			return XmlUtil.createErrorXml ("Cat 2 failure. Contact program team. Refer to Indus for Treatment.");
		}
		if(rifCheck.booleanValue()== true && tbHIST.equalsIgnoreCase("no"))
		{
			patYpe = "NEW" ;
			patCategory = "CAT 1"; 
		}
		
		else if (rifCheck.booleanValue()== true && tbHIST.equalsIgnoreCase("yes") && injectHIST.booleanValue()== false)
		{
			patYpe = "Treatment after Failure" ;
			patCategory = "CAT 2" ;
		}}else if(rifCheck.booleanValue()== true && tbHIST.equalsIgnoreCase("no"))
		{
			patYpe = "NEW" ;
			patCategory = "CAT 1"; 
		}
		if(ageLess.intValue()<15)
		{
			ageChecker = "LESS" ;
			System.out.println("This patient is less than 15 years old...");
		}

		Document doc = null;

		try
		{
			doc = DocumentBuilderFactory.newInstance ().newDocumentBuilder ().newDocument ();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace ();
			return "";
		}
		Element responseNode = doc.createElement (XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element smearNode = doc.createElement ("basesmear");
		Text smearValue = doc.createTextNode (baseSmear);
		smearNode.appendChild (smearValue);

		responseNode.appendChild (smearNode);

		Element gxpNode = doc.createElement ("basegxp");
		Text gxpValue = doc.createTextNode (baseGxp);
		gxpNode.appendChild (gxpValue);

		responseNode.appendChild (gxpNode);

		Element genderNode = doc.createElement ("gender");
		Text genderValue = doc.createTextNode (gender);
		genderNode.appendChild (genderValue);

		responseNode.appendChild (genderNode);

		Element dstNode = doc.createElement ("basedst");
		Text dstValue = doc.createTextNode (baseGxpRes);
		dstNode.appendChild (dstValue);

		responseNode.appendChild (dstNode);

		Element cxrNode = doc.createElement ("cxr");
		Text cxrValue = doc.createTextNode (cxr);
		cxrNode.appendChild (cxrValue);

		responseNode.appendChild (cxrNode);
		
		Element patYpeNode = doc.createElement("patYpe");
		Text patYpeValue = doc.createTextNode(patYpe);
		patYpeNode.appendChild(patYpeValue);
		
		responseNode.appendChild(patYpeNode);
		
		Element patCategoryNode = doc.createElement("patCate");
		Text patCategoryValue = doc.createTextNode(patCategory);
		patCategoryNode.appendChild(patCategoryValue);
		
		responseNode.appendChild(patCategoryNode);
		
		Element ageYpeNode = doc.createElement("ageChecker");
		Text ageYpeValue = doc.createTextNode(ageChecker);
		ageYpeNode.appendChild(ageYpeValue);
		
		responseNode.appendChild(ageYpeNode);

		/*
		 * Element tbHistNode = doc.createElement("tbhist"); Text tbHistValue =
		 * doc.createTextNode(tbhist); tbHistNode.appendChild(tbHistValue);
		 * 
		 * responseNode.appendChild(tbHistNode);
		 */

		/*
		 * Element typeNode = doc.createElement("type"); Text typeValue =
		 * doc.createTextNode("New"); typeNode.appendChild(typeValue);
		 * 
		 * responseNode.appendChild(typeNode);
		 * 
		 * Element catNode = doc.createElement("cat"); Text catValue =
		 * doc.createTextNode("2"); catNode.appendChild(catValue);
		 * 
		 * responseNode.appendChild(catNode);
		 */

		doc.appendChild (responseNode);

		xml = XmlUtil.docToString (doc);

		return xml;
	}

	/*
	 * public String getContactSputumCollectInfo(String id) {
	 * 
	 * return getSputumCollectInfo(id); }
	 */

	public String getDrugAdmInfo (String id)
	{
		String xml = null;

		Patient pat = null;

		try
		{
			pat = ssl.findPatient (id);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
		}

		if (pat == null)
		{
			return XmlUtil.createErrorXml ("Could not find Patient with id " + id + ". Please try again.");
		}
		if (pat.getDiseaseConfirmed () == null || !pat.getDiseaseConfirmed ())
		{
			return XmlUtil
					.createErrorXml ("Di gai ID ka status "
							+ pat.getPatientStatus ()
							+ " hay or Disease Confirmed false hay. Baraey meherbani tamam forms tarteeb say fill karen or agar Patient k Tests k results k form fill nahi huay tu pehlay wo fill karen. Aur agar patient ka Baseline From fill nahi hua tu pehlay wo fill karen");
		}
		// ADDED BY ALI - RIF RESISTANCE CHECK
		Boolean rifCheck = null;

		try
		{
			rifCheck = ssl.exists ("sputumresults", "where PatientID='" + id + "' AND GeneXpertResistance='RIF RESISTANT'");
		}
		catch (Exception e4)
		{
			e4.printStackTrace ();
			return XmlUtil.createErrorXml ("Error tracking Patient. Please try again");
		}

		/*if (rifCheck.booleanValue () == true)
		{
			return XmlUtil.createErrorXml ("Ye Patient RIF Resistant hai. Patient ko Indus Hospital refer karain.");
		}*/
		// END RIF RESISTANCE CHECK
		Boolean isBaselined = null;
		try
		{
			isBaselined = ssl.exists ("encounter", " where PID1 ='" + id + "' and EncounterType='" + EncounterType.BASELINE + "'");
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			return XmlUtil.createErrorXml ("Error Finding Baseline info. Try again.");
		}

		if (isBaselined == null || !isBaselined)
		{
			return XmlUtil.createErrorXml ("Di gai ID ka Baseline From fill nahi hua pehlay wo fill karen. Baraey meherbani tamam forms tarteeb say fill karen.");
		}

		// String phase = pat.getTreatmentPhase();
		String regimen = pat.getRegimen ();
		Integer strptomy = pat.getStreptomycin ();
		// Float fixedDose = pat.getDoseCombination();
		// String fixedDoseString = null;

		/*
		 * if(fixedDose==null) { fixedDoseString = " "; } else { fixedDoseString
		 * = fixedDose.toString(); }
		 */
		/* String otherDose = pat.getOtherDoseDescription(); */

		/*
		 * if(otherDose==null) { otherDose = " "; }
		 */

		// String[] colData = null;
		/*
		 * String [] rowRecord = null;
		 * 
		 * String treatmentStartDate = ""; try { colData =
		 * ssl.getColumnData("Encounter",
		 * "CONCAT(encounterId, '|', pid2) AS column1", " where pid1='" + id +
		 * "' AND encounterType='BASELINE' ORDER BY dateEncounterStart ASC");
		 * 
		 * rowRecord = ssl.getRowRecord("Encounter", new String[]{"EncounterID",
		 * "PID2"}, " where PID1= '" + id +
		 * "'AND encounterType='BASELINE' ORDER BY dateEncounterStart ASC"); }
		 * catch (Exception e1) { e1.printStackTrace(); }
		 * 
		 * if(rowRecord==null || rowRecord.length!=2) { treatmentStartDate =
		 * "N/A"; } else { System.out.println(rowRecord[0]);
		 * System.out.print(rowRecord[1]); int encId =
		 * Integer.parseInt(rowRecord[0]); String pid2 = rowRecord[1];
		 * EncounterResultsId eri = new
		 * EncounterResultsId(encId,id,pid2,"entered_date"); EncounterResults er
		 * = null; try { er = ssl.findEncounterResultsByElement(eri); } catch
		 * (Exception e) { e.printStackTrace(); }
		 * 
		 * if(er!=null) { treatmentStartDate = er.getValue(); } else {
		 * treatmentStartDate = "N/A"; } }
		 */

		Document doc = null;

		try
		{
			doc = DocumentBuilderFactory.newInstance ().newDocumentBuilder ().newDocument ();
		}
		catch (ParserConfigurationException e)
		{
			// Auto-generated catch block
			e.printStackTrace ();
			return "";
		}
		Element responseNode = doc.createElement (XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		/*
		 * Element phaseNode = doc.createElement("phase"); Text phaseValue =
		 * doc.createTextNode(phase); phaseNode.appendChild(phaseValue);
		 * 
		 * responseNode.appendChild(phaseNode);
		 */

		Element regimenNode = doc.createElement ("regimen");
		Text regimenValue = doc.createTextNode ((regimen == null ? "" : regimen));
		regimenNode.appendChild (regimenValue);

		responseNode.appendChild (regimenNode);

		/*
		 * Element fdctNode = doc.createElement("fdct"); Text fdctValue =
		 * doc.createTextNode(fixedDoseString); fdctNode.appendChild(fdctValue);
		 * 
		 * responseNode.appendChild(fdctNode);
		 */

		Element streptNode = doc.createElement ("strepto");
		Text streptValue = doc.createTextNode ((strptomy == null || strptomy <= 0 ? "" : strptomy.toString ()));
		streptNode.appendChild (streptValue);

		responseNode.appendChild (streptNode);

		doc.appendChild (responseNode);

		xml = XmlUtil.docToString (doc);

		System.out.println (xml);
		return xml;
	}

	public String getFollowupTreatmentInfo (String id)
	{
		String xml = null;

		Screening scr = null;
		try
		{
			scr = ssl.findScreeningByPatientID (id);
		}
		catch (Exception e4)
		{
			e4.printStackTrace ();
		}

		if (scr == null)
		{
			return XmlUtil.createErrorXml ("Patient does not exist. Please confirm ID and try again");
		}

		Patient pat = null;
		try
		{
			pat = ssl.findPatient (id);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
		}

		if (pat == null)
		{
			return XmlUtil.createErrorXml ("Di gai id ki Screening hui hay lekin disease confirmed nahi ho saki hay. Baraey meherbani tamam forms tarteeb say fill karen");
		}

		if (!pat.getDiseaseConfirmed ())
		{
			return XmlUtil.createErrorXml ("Di gai ID ka status " + pat.getPatientStatus () + " hay or Disease Confirmed false hay. Baraey meherbani tamam forms tarteeb say fill karen");
		}

		Boolean exists = null;

		try
		{
			exists = ssl.exists ("encounter", " where PID1='" + id + "'" + " AND EncounterType='" + EncounterType.BASELINE + "'");
		}
		catch (Exception e3)
		{
			e3.printStackTrace ();
		}

		if (exists == null)
		{
			return XmlUtil.createErrorXml ("Error tracking Patient Baseline Info. Try again");
		}
		else if (!exists)
		{
			return XmlUtil.createErrorXml ("Patient " + id + " ka Baseline Treatment Form nahi bhara gaya hay. Baraey meherbani pehlay Baseline Form fill karen");
		}
		
		String diseaseSite = pat.getDiseaseSite ();
		if (diseaseSite == null)
		{
			diseaseSite = " ";
		}
		String type = pat.getPatientType ();
		if (type == null)
		{
			type = " ";
		}

		String category = pat.getDiseaseCategory ();
		if (category == null)
		{
			category = " ";
		}

		String gender = "";

		Person per = null;
		try
		{
			per = ssl.findPerson (id);
		}
		catch (Exception e3)
		{
			e3.printStackTrace ();
		}

		if (per == null)
		{
			return XmlUtil.createErrorXml ("Person not found. Please confirm ID exists and try again");
		}

		gender = Character.toString (per.getGender ());
		// get base sputum results, gxp results, gxp resistance, chest x-ray
		// TODO: Modify for three tables
		String[] sputumResults = null;
		int numResults = -1;
		try
		{
			sputumResults = ssl.getColumnData ("sputumresults", "SmearResult", " where PatientID='" + id + "' " + " AND SmearTestDone=1" + " AND Month=0 " + " AND SmearResult IS NOT NULL "
					+ " AND SmearOrderDate IS NOT NULL " + " AND (Details IS NULL OR Details NOT LIKE '%REJECTED%') " + " ORDER BY SmearOrderDate ASC");
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			return XmlUtil.createErrorXml ("Error finding base Sputum Results for patient with id " + id + ". Please try again.");
		}

		/*
		 * if(sputumResults==null || sputumResults.length == 0) { return
		 * XmlUtil.
		 * createErrorXml("Could not find base Sputum Results for patient with id "
		 * + id + ". Please try again."); }
		 */

		boolean isAnyBaseSmearPositive = false;
		boolean isAnySmear3Positive = false;

		String baseSmear = " ";
		String baseGxp = " ";
		String baseGxpRes = " ";
		String cxr = " ";

		if (sputumResults != null)
		{
			numResults = sputumResults.length;

			for (int i = 0; i < numResults; i++)
			{
				if (!sputumResults[i].trim ().equalsIgnoreCase ("negative"))
				{// is +ve
					isAnyBaseSmearPositive = true;
				}
				baseSmear += sputumResults[i] + " ";
			}
		}
		// follow up smear 2
		String[] sputum2Results = null;
		int numSmear2Results = -1;
		try
		{
			sputum2Results = ssl.getColumnData ("sputumresults", "SmearResult", " where PatientID='" + id + "' " + " AND SmearTestDone=1 " + " AND Month=2 " + " AND SmearResult IS NOT NULL "
					+ " AND SmearOrderDate IS NOT NULL " + " AND (Details IS NULL OR Details NOT LIKE '%REJECTED%') " + " ORDER BY SmearOrderDate ASC");
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
		}

		String smear2 = " ";

		if (sputum2Results != null)
		{
			numSmear2Results = sputum2Results.length;

			for (int i = 0; i < numSmear2Results; i++)
			{
				smear2 += sputum2Results[i] + " ";
			}
		}

		// follow up smear 3
		String[] sputum3Results = null;
		int numSmear3Results = -1;
		try
		{
			sputum3Results = ssl.getColumnData ("sputumresults", "SmearResult", " where PatientID='" + id + "' " + " AND SmearTestDone=1 " + " AND Month=3 " + " AND SmearResult IS NOT NULL "
					+ " AND SmearOrderDate IS NOT NULL " + " AND (Details IS NULL OR Details NOT LIKE '%REJECTED%') " + " ORDER BY SmearOrderDate ASC");
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
		}

		String smear3 = " ";

		if (sputum3Results != null)
		{
			numSmear3Results = sputum3Results.length;

			for (int i = 0; i < numSmear3Results; i++)
			{
				if (!sputum3Results[i].trim ().equalsIgnoreCase ("negative"))
				{// is +ve
					isAnySmear3Positive = true;
				}
				smear3 += sputum3Results[i] + " ";
			}
		}

		// follow up smear 5
		String[] sputum5Results = null;
		int numSmear5Results = -1;
		try
		{
			sputum5Results = ssl.getColumnData ("sputumresults", "SmearResult", " where PatientID='" + id + "' " + " AND SmearTestDone=1 " + " AND Month=5 " + " AND SmearResult IS NOT NULL "
					+ " AND SmearOrderDate IS NOT NULL " + " AND (Details IS NULL OR Details NOT LIKE '%REJECTED%') " + " ORDER BY SmearOrderDate ASC");
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
		}

		String smear5 = " ";

		if (sputum5Results != null)
		{
			numSmear5Results = sputum5Results.length;

			for (int i = 0; i < numSmear5Results; i++)
			{
				smear5 += sputum5Results[i] + " ";
			}
		}

		// follow up smear 7
		String[] sputum7Results = null;
		int numSmear7Results = -1;
		try
		{
			sputum7Results = ssl.getColumnData ("sputumresults", "SmearResult", " where PatientID='" + id + "' " + " AND SmearTestDone=1 " + " AND Month=7 " + " AND SmearResult IS NOT NULL "
					+ " AND SmearOrderDate IS NOT NULL " + " AND (Details IS NULL OR Details NOT LIKE '%REJECTED%') " + " ORDER BY SmearOrderDate ASC");
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
		}

		String smear7 = " ";

		if (sputum7Results != null)
		{
			numSmear7Results = sputum7Results.length;

			for (int i = 0; i < numSmear7Results; i++)
			{
				smear7 += sputum7Results[i] + " ";
			}
		}
		// chest xray results
		String[] xRayResult = null;

		try
		{
			xRayResult = ssl.getColumnData ("xrayresults", "XRayResult", " where PatientId='" + id + "' " + " AND DateReported IS NOT NULL " + "ORDER BY DateReported ASC");

		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
		}

		if (xRayResult != null)
		{
			for (int i = 0; i < xRayResult.length; i++)
			{
				if (!StringUtils.isEmptyOrWhitespaceOnly (xRayResult[i]))
					cxr += xRayResult[i] + " ";
			}
		}

		// get base Gxp results

		String[][] gxpResults = null;
		try
		{
			gxpResults = ssl.getTableData ("sputumresults", new String[] {"GeneXpertTestDone",// not
																								// necessarily
																								// needed
					"GeneXpertResult", "GeneXpertResistance"}, " where PatientID='" + id + "' " + " AND GeneXpertTestDone=1" + " AND Month=0 " + " AND GeneXpertResult IS NOT NULL "
					+ " AND GeneXpertOrderDate IS NOT NULL " + " ORDER BY GeneXpertOrderDate ASC");
		}
		catch (Exception e2)
		{
			e2.printStackTrace ();
		}

		if (gxpResults != null)
		{
			for (int i = 0; i < gxpResults.length; i++)
			{
				if (!StringUtils.isEmptyOrWhitespaceOnly (gxpResults[i][1]))
				{
					baseGxp += gxpResults[i][1];// column: GeneXpertResult
				}
				else
					baseGxp += "N/A" + " ";

				if (!StringUtils.isEmptyOrWhitespaceOnly (gxpResults[i][2]))
					baseGxpRes += gxpResults[i][2] + " ";// column:
															// GeneXpertResistance
				else
					baseGxpRes += "N/A" + " ";
			}
		}

		String[] rowRecord = null;

		String treatmentStartDate = "";
		String formattedtreatmentdate= "";
		Date formatteddate = null;
		String treatmentStatus="";
		DateFormat dff = null;
		try
		{
			rowRecord = ssl.getRowRecord ("encounter", new String[] {"EncounterID", "PID2"}, " where PID1= '" + id + "'" + " AND EncounterType='" + EncounterType.BASELINE + "'"
					+ " ORDER BY DateEncounterStart ASC");
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
		}

		if (rowRecord == null || rowRecord.length != 2)
		{
			treatmentStartDate = "N/A";
			System.out.println (rowRecord == null ? "null" : rowRecord.length);
		}
		else
		{
			System.out.println (rowRecord[0]);
			System.out.print (rowRecord[1]);
			int encId = Integer.parseInt (rowRecord[0]);
			String pid2 = rowRecord[1];
			EncounterResultsId eri = new EncounterResultsId (encId, id, pid2, "entered_date");
			EncounterResults er = null;
			try
			{
				er = ssl.findEncounterResultsByElement (eri);
			}
			catch (Exception e)
			{
				e.printStackTrace ();
			}

			if (er != null)
			{
				treatmentStartDate = er.getValue ();
			}
			else
			{
				treatmentStartDate = "N/A";
			}
			dff=new SimpleDateFormat("dd/MM/yyyy");
			
			try {
				formatteddate = dff.parse(treatmentStartDate);
				System.out.println("this is the teatment ... "+treatmentStartDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Date d=new Date();
			long d1=d.getTime();
			long d2=formatteddate.getTime();
			
			long dys=(d1-d2)/(1000 * 60 * 60 * 24);
			//formattedtreatmentdate=dff.format(formatteddate);
			formattedtreatmentdate=Integer.toString((int)dys);
			
		}
		//treatement start date on May 01
		try{
		if(formatteddate.after(dff.parse("18/05/2012")))
		{
			treatmentStatus="yes";
		}
		}catch(ParseException e)
		{
			e.printStackTrace();
		}
		// TODO should return error of 'first baseline form' instead of allowing
		// it to reach form??

		/*
		 * String[] er = null; try { er = ssl.getColumnData("EncounterResults",
		 * "Value", " where PID1='"+ id +
		 * "' AND Element='PAST_TB_DRUG_HISTORY_D1'"); } catch (Exception e) {
		 * e.printStackTrace(); }
		 */

		/*
		 * String tbhist; if(er!=null && er.length > 0) tbhist = er[0]; else {
		 * try { er = ssl.getColumnData("EncounterResults", "Value",
		 * " where PID1='"+ id + "' AND Element='PAST_TB_DRUG_HISTORY'"); }
		 * catch (Exception e) { e.printStackTrace(); }
		 * 
		 * if(er!=null && er.length>0) tbhist = er[0]; else tbhist =
		 * "Not Entered"; }
		 */
		String[] tbhistcoldata = null;
		String tbhist = null;
		try
		{
			tbhistcoldata = ssl.getColumnData ("screening", "TBHistory", "where PatientID=" + id);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}

		if (tbhistcoldata == null || tbhistcoldata.length == 0)
		{
			tbhist = "Not Found";
		}
		else
		{
			tbhist = tbhistcoldata[0];
		}
		boolean allowMonth4 = false;
		if (isAnyBaseSmearPositive && isAnySmear3Positive && pat.getDiseaseCategory () != null && pat.getDiseaseCategory ().equalsIgnoreCase ("category 2"))
		{
			allowMonth4 = true;
		}

		boolean allowMonth3 = false;
		if (pat.getDiseaseCategory () != null 
				&& ( pat.getDiseaseCategory ().equalsIgnoreCase ("category 2")
						|| (pat.getDiseaseCategory ().equalsIgnoreCase ("category 1") 
								&& (!smear2.trim().equals("") && !smear2.equalsIgnoreCase("negative"))
							) 
					)
		){
			allowMonth3 = true;
		}

		boolean allowPregnQ = false;
		if (pat.getDiseaseCategory () != null && pat.getDiseaseCategory ().equalsIgnoreCase ("category 2") && gender.trim ().toLowerCase ().startsWith ("f"))
		{
			allowPregnQ = true;
		}

		Document doc = null;

		try
		{
			doc = DocumentBuilderFactory.newInstance ().newDocumentBuilder ().newDocument ();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace ();
			return "";
		}
		
		Element responseNode = doc.createElement (XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element dateNode = doc.createElement ("txstart");
		Text dateValue = doc.createTextNode (treatmentStartDate);
		dateNode.appendChild (dateValue);

		responseNode.appendChild (dateNode);
		
		Element treatmentstatusNode = doc.createElement ("treatmentStatus");
		Text treatmentstatusValue = doc.createTextNode (treatmentStatus);
		treatmentstatusNode.appendChild (treatmentstatusValue);
		
		responseNode.appendChild(treatmentstatusNode);
		
		Element treatmentdateNode = doc.createElement ("txstartdate");
		Text treatmentdateValue = doc.createTextNode (formattedtreatmentdate);
		treatmentdateNode.appendChild (treatmentdateValue);
		
		responseNode.appendChild(treatmentdateNode);
		
		Element smearNode = doc.createElement ("basesmear");
		Text smearValue = doc.createTextNode (baseSmear);
		smearNode.appendChild (smearValue);

		responseNode.appendChild (smearNode);

		Element gxpNode = doc.createElement ("basegxp");
		Text gxpValue = doc.createTextNode (baseGxp);
		gxpNode.appendChild (gxpValue);

		responseNode.appendChild (gxpNode);

		Element dstNode = doc.createElement ("basedst");
		Text dstValue = doc.createTextNode (baseGxpRes);
		dstNode.appendChild (dstValue);

		responseNode.appendChild (dstNode);

		Element cxrNode = doc.createElement ("cxr");
		Text cxrValue = doc.createTextNode (cxr);
		cxrNode.appendChild (cxrValue);

		responseNode.appendChild (cxrNode);

		Element genderNode = doc.createElement ("gender");
		Text genderValue = doc.createTextNode (gender);
		genderNode.appendChild (genderValue);

		responseNode.appendChild (genderNode);

		Element allowMonth4Node = doc.createElement ("allow4");
		Text allowMonth4Value = doc.createTextNode (allowMonth4 ? "yes" : "no");
		allowMonth4Node.appendChild (allowMonth4Value);

		responseNode.appendChild (allowMonth4Node);

		Element allowMonth3Node = doc.createElement ("allow3");
		Text allowMonth3Value = doc.createTextNode (allowMonth3 ? "yes" : "no");
		allowMonth3Node.appendChild (allowMonth3Value);

		responseNode.appendChild (allowMonth3Node);

		Element allowPregnQNode = doc.createElement ("allowpreg");
		Text allowPregnQValue = doc.createTextNode (allowPregnQ ? "yes" : "no");
		allowPregnQNode.appendChild (allowPregnQValue);

		responseNode.appendChild (allowPregnQNode);

		Element fosmear2Node = doc.createElement ("fosmear2");
		Text fosmear2Value = doc.createTextNode (smear2);
		fosmear2Node.appendChild (fosmear2Value);

		responseNode.appendChild (fosmear2Node);

		Element fosmear3Node = doc.createElement ("fosmear3");
		Text fosmear3Value = doc.createTextNode (smear3);
		fosmear3Node.appendChild (fosmear3Value);

		responseNode.appendChild (fosmear3Node);

		Element fosmear5Node = doc.createElement ("fosmear5");
		Text fosmear5Value = doc.createTextNode (smear5);
		fosmear5Node.appendChild (fosmear5Value);

		responseNode.appendChild (fosmear5Node);

		Element fosmear7Node = doc.createElement ("fosmear7");
		Text fosmear7Value = doc.createTextNode (smear7);
		fosmear7Node.appendChild (fosmear7Value);

		responseNode.appendChild (fosmear7Node);

		Element siteNode = doc.createElement ("site");
		Text siteValue = doc.createTextNode (diseaseSite);
		siteNode.appendChild (siteValue);

		responseNode.appendChild (siteNode);

		Element typeNode = doc.createElement ("type");
		Text typeValue = doc.createTextNode (type);
		typeNode.appendChild (typeValue);

		responseNode.appendChild (typeNode);

		Element catNode = doc.createElement ("cat");
		Text catValue = doc.createTextNode (category);
		catNode.appendChild (catValue);

		responseNode.appendChild (catNode);

		Element tbHistNode = doc.createElement ("tbhist");
		Text tbHistValue = doc.createTextNode (tbhist);
		tbHistNode.appendChild (tbHistValue);

		responseNode.appendChild (tbHistNode);

		doc.appendChild (responseNode);

		xml = XmlUtil.docToString (doc);

		return xml;
	}

	private String getFormCount ()
	{
		Hashtable<String, String> types = new Hashtable<String, String> ();

		types.put ("SUSPECT_ID", "Form A");
		types.put ("SUSPECTCON", "Form B");
		types.put ("SUSPECTVER", "Form C");
		types.put ("P_INFO", "Form D1");
		types.put ("TB_HISTORY", "Form D2");
		types.put ("GPS", "Form D3");
		types.put ("REFUSAL", "Form E");
		types.put ("SPUTUM_COL", "Form F");
		types.put ("BASELINE", "Form I");
		types.put ("FOLLOW_UP", "Form J");
		types.put ("DRUG_ADM", "Form K");
		types.put ("END_FOL", "Form L");
		types.put ("MR_ASSIGN", "MR Assigned");
		types.put ("GP_NEW", "GP Screened Suspect");
		types.put ("CDF", "Form Q");

		String xml = null;
		String id = request.getParameter ("mid");
		String date = request.getParameter ("date");

		date = DateTimeUtil.convertFromSlashFormatToSQL (date);

		System.out.println (id);
		System.out.println (date);
		ssl = new ServerServiceImpl ();

		Object[][] encounters = null;

		try
		{
			encounters = HibernateUtil.util.selectData ("select count(*),EncounterType from Encounter where PID2='" + id.toUpperCase () + "' AND DateEncounterStart >= '" + date
					+ " 00:00:01' AND DateEncounterStart <= '" + date + " 23:59:59' GROUP BY EncounterType ORDER BY EncounterType");
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}

		if (encounters == null)
		{
			return XmlUtil.createErrorXml ("Error while performing search. Please try again");
		}

		else if (encounters.length == 0)
		{
			return XmlUtil.createErrorXml ("No results found");
		}

		String data = "EncounterType:  Submitted\n";
		String encType = "";
		BigInteger count = null;

		for (int i = 0; i < encounters.length; i++)
		{
			count = (BigInteger) encounters[i][0];
			encType = (String) encounters[i][1];

			if (types.get (encType) != null)
			{
				encType = (String) (types.get (encType));
			}
			data += encType + ": " + count + "\n";
		}

		Document doc = null;

		try
		{
			doc = DocumentBuilderFactory.newInstance ().newDocumentBuilder ().newDocument ();
		}
		catch (ParserConfigurationException e)
		{
			// Auto-generated catch block
			e.printStackTrace ();
			return "";
		}
		Element responseNode = doc.createElement (XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element dataNode = doc.createElement ("data");
		Text dataValue = doc.createTextNode (data);
		dataNode.appendChild (dataValue);

		responseNode.appendChild (dataNode);

		doc.appendChild (responseNode);

		xml = XmlUtil.docToString (doc);

		return xml;

	}

	private String getGeneXpertInfo (String labid, String labbarcode)
	{

		String[] rowData = null;

		try
		{
			rowData = ssl.getRowRecord ("sputumresults", new String[] {"SmearResult", "SputumQuality", "PatientID", "SmearOrderDate"}, " where SputumLabID='" + labid + labbarcode + "'");
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}

		if (rowData == null || rowData.length == 0)
		{
			return XmlUtil.createErrorXml ("No Entry for Sputum Result found for Given Lab id and Barcode " + labid + " : " + labbarcode
					+ ". Please recheck IDs and try again and make sure that Sputum Results for this test have been submitted.");
		}

		// First check if No test have been completed i.e. The Samples still
		// waiting for smear result
		boolean isTestCompleted = false;

		// check if any result is a result in field SmearResult
		if (!StringUtils.isEmptyOrWhitespaceOnly (rowData[0]))
		{
			isTestCompleted = true;
		}

		// if no test completed return error.
		if (!isTestCompleted)
		{
			return XmlUtil.createErrorXml ("No Sputum Results found for Sample with Given Lab id and Barcode " + labid + ":" + labbarcode
					+ ". Seems sample is still waiting for smear results from lab.");
		}

		// Now check if any test of Patient shows Positive result.
		boolean isResultPositive = false;

		// check if any result is non-NEGATIVE in field SmearResult
		if (!StringUtils.isEmptyOrWhitespaceOnly (rowData[0]) && !rowData[0].equalsIgnoreCase ("negative"))
		{
			isResultPositive = true;
		}
		String patientId = rowData[2];
		String basesmear = rowData[0];
		String spQuality = rowData[1];

		// if any result is positive just show test history and error Alert.
		if (isResultPositive)
		{
			String testReport = "PATIENT ID : " + patientId + "\nA Smear Test Result was found to be +ve hence geneXpert Test can not be ordered." + " A detailed report of tests is given below\n\n";
			testReport += "-Smear Result : " + basesmear;
			testReport += "\n-Smear Order Date : " + rowData[3];
			testReport += "\n-Sputum Quality : " + spQuality;
			testReport += "\n\n";
			return XmlUtil.createErrorXml (testReport);
		}

		// till here it is sure that SPutum result for test will surely be
		// negative
		boolean allowGxpTest = false;
		String cxr = "";
		try
		{
			String[] xrayr = ssl.getColumnData ("xrayresults", "XRayResult", " PatientID='" + patientId + "'" + " AND XRayResult IS NOT NULL");

		//	String[] xrayr = ssl.getColumnData ("xrayresults", "XRayResult", " PatientID='"+ patientId+"'");
			//String[] xrayr = ssl.getColumnData ("xrayresults", "XRayResult", " PatientID='" + patientId + "'");
			
			if (xrayr == null || xrayr.length > 0)
			//if (xrayr != null && xrayr.length > 0)
			{
				for (int i = 0; i < xrayr.length; i++)
				{
					if (!StringUtils.isEmptyOrWhitespaceOnly (xrayr[i]))

					//if (StringUtils.isEmptyOrWhitespaceOnly (xrayr[i]))
					{
						/*if (xrayr[i].trim ().equalsIgnoreCase ("highly suggestive of tb") || xrayr[i].trim ().equalsIgnoreCase ("possibility of tb"))
						{
							allowGxpTest = true;// GXP TEST
						}*/
						cxr += xrayr[i] + " ";
						//allowing GXP Test here because of Andrew's email regarding filtering on sputum which has to be negative, not filtering on 
						//CXR being suggestive, now only sputum has to be negative, regardless of whatever CXR is
						//allowGxpTest = true;// GXP TEST
					}
				}
			}allowGxpTest = true;// GXP TEST
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
		}

		/*if (StringUtils.isEmptyOrWhitespaceOnly (cxr.replaceAll ("[^A-Za-z\\d]", "")))
		{
			return XmlUtil
					.createErrorXml ("Iss patient ka koi Xray Result mojood nahi hay jis kay baghair GeneXpert ki entry nahi ki ja sakti. Agar patient ka Xray nahi karwaya ja sakta tu Tbreach team say rujoo karen");
		}*/

		if (spQuality.equalsIgnoreCase ("saliva"))
		{
			allowGxpTest = false;
			return XmlUtil.createErrorXml ("Sputum Quality 'Saliva' hay jis pay GeneXpert nahi kia ja sakta");
		}
		// String[] data = rowData[0].split("\\|");
		// System.out.println(data[0]);
		// System.out.println(data[1]);

		Document doc = null;

		try
		{
			doc = DocumentBuilderFactory.newInstance ().newDocumentBuilder ().newDocument ();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace ();
			return "";
		}
		Element responseNode = doc.createElement (XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element smearNode = doc.createElement ("basesmear");
		Text smearValue = doc.createTextNode (basesmear);
		smearNode.appendChild (smearValue);

		responseNode.appendChild (smearNode);

		Element pidNode = doc.createElement ("pid");
		Text pidValue = doc.createTextNode (patientId);
		pidNode.appendChild (pidValue);

		responseNode.appendChild (pidNode);

		Element xrayNode = doc.createElement ("cxr");
		Text xrayValue = doc.createTextNode (cxr);
		xrayNode.appendChild (xrayValue);

		responseNode.appendChild (xrayNode);

		Element spqualNode = doc.createElement ("spqual");
		Text spqualValue = doc.createTextNode (spQuality);
		spqualNode.appendChild (spqualValue);

		responseNode.appendChild (spqualNode);

		Element allowGxpTestNode = doc.createElement ("allowgxp");
		Text allowGxpTestValue = doc.createTextNode (allowGxpTest ? "Yes" : "No");
		allowGxpTestNode.appendChild (allowGxpTestValue);

		responseNode.appendChild (allowGxpTestNode);

		/*
		 * Element lNameNode = doc.createElement("lname"); Text lNameValue =
		 * doc.createTextNode(data[1]); lNameNode.appendChild(lNameValue);
		 * 
		 * responseNode.appendChild(lNameNode);
		 */

		doc.appendChild (responseNode);

		String xml = XmlUtil.docToString (doc);

		return xml;
	}

	public String getGPRegistrationInfo ()
	{
		String[][] result = null;
		try
		{
			result = ssl.getTableData ("location", new String[] {"LocationName", "LocationID"}, " where LocationType='LABORATORY'");
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
		if (result == null || result.length <= 0)
		{
			return XmlUtil.createErrorXml ("Labs were not retrieved. Try Later");
		}

		String labsArray = "";
		for (String[] strings : result)
		{
			labsArray += strings[0] + "=" + strings[1] + ";";
		}

		String totalGpsInDb = "Not Found";
		Long count = null;
		try
		{
			count = ssl.count ("gp", "");
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
		}
		if (count != null)
		{
			totalGpsInDb = count.toString ();
		}

		Document doc = null;

		try
		{
			doc = DocumentBuilderFactory.newInstance ().newDocumentBuilder ().newDocument ();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace ();
			return "";
		}
		Element responseNode = doc.createElement (XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element labsArrayNode = doc.createElement ("labsarray");
		Text labsArrayValue = doc.createTextNode (labsArray);
		labsArrayNode.appendChild (labsArrayValue);

		responseNode.appendChild (labsArrayNode);

		Element totalGpsInDbNode = doc.createElement ("gpcnt");
		Text totalGpsInDbValue = doc.createTextNode (totalGpsInDb);
		totalGpsInDbNode.appendChild (totalGpsInDbValue);

		responseNode.appendChild (totalGpsInDbNode);

		doc.appendChild (responseNode);

		String xml = XmlUtil.docToString (doc);

		return xml;
	}

	private String getLabResultsData (String id)
	{
		String xml = null;

		ssl = new ServerServiceImpl ();

		Object[] results = null;
		String baseSmear = " ";
		String baseGxp = " ";
		String baseGxpRes = " ";
		String cxr = " ";
		String smear2 = "";
		String smear3 = "";
		String smear5 = "";
		String smear7 = "";
		// String barcode = "";
		String smearRes = "";
		String gxpResult = "";
		String gxpResistance = "";
		String cxrResult = "";
		// String remarks = "";
		int numResults = -1;
		try
		{
			results = HibernateUtil.util.selectObjects ("select SputumLabID from sputumresults where PatientID='" + id + "' AND Month=0 AND SmearOrderDate IS NOT NULL ORDER BY SmearOrderDate ASC");

		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
		}

		if (results == null)
		{
			return XmlUtil.createErrorXml ("Could not find base Sputum Results for patient with id " + id + " Please try again.");
		}

		SputumResults temp = null;
		String tempBarcode = null;
		numResults = results.length;

		for (int i = 0; i < numResults; i++)
		{
			temp = null;

			tempBarcode = (String) (results[i]);

			try
			{
				temp = ssl.findSputumResultsBySputumLabID (tempBarcode);
			}
			catch (Exception e)
			{
				e.printStackTrace ();
				temp = null;
			}

			if (temp != null)
			{
				smearRes = temp.getSmearResult ();
				// remarks = temp.getRemarks();

				if (smearRes != null)
				{
					baseSmear += "\n" + tempBarcode + ": " + smearRes;
				}
				else
				{
					baseSmear += "\n" + tempBarcode + ": " + "Pending";
				}
			}
		}

		/*
		 * if(baseSmear.length()>0 &&
		 * baseSmear.charAt(baseSmear.length()-1)==',') { baseSmear =
		 * baseSmear.substring(0, baseSmear.length()-1); }
		 */

		// get base Gxp results

		try
		{
			results = HibernateUtil.util.selectObjects ("select SputumLabID from sputumresults where PatientID='" + id
					+ "' AND Month=0 AND GeneXpertOrderDate IS NOT NULL ORDER BY GeneXpertOrderDate ASC");

		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
		}

		if (results == null)
		{
			return XmlUtil.createErrorXml ("Could not find base GeneXpert Results for patient with id " + id + " Please try again.");
		}

		temp = null;
		tempBarcode = null;
		numResults = results.length;

		for (int i = 0; i < numResults; i++)
		{
			temp = null;

			tempBarcode = (String) (results[i]);

			try
			{
				temp = ssl.findSputumResultsBySputumLabID (tempBarcode);
			}
			catch (Exception e)
			{
				e.printStackTrace ();
				temp = null;
			}

/*			if (temp != null)
			{
				gxpResult = temp.getGeneXpertResult ();
				gxpResistance = temp.getGeneXpertResistance ();

				if (gxpResult != null)
				{
					baseGxp += "\n" + tempBarcode + ": " + gxpResult;
				}
				else
				{
					baseGxp += "\n" + tempBarcode + ": " + "Pending";
				}

				if (gxpResistance != null)
				{
					baseGxpRes += "\n" + tempBarcode + ": " + gxpResistance;
				}
				else
				{
					baseGxpRes += "\n" + tempBarcode + ": " + "Pending";
				}
			}*/
		}

		/*
		 * if(baseGxp.length()>0 && baseGxp.charAt(baseGxp.length()-1)==',') {
		 * baseGxp = baseGxp.substring(0, baseGxp.length()-1); }
		 * 
		 * if(baseGxpRes.length()>0 &&
		 * baseGxpRes.charAt(baseGxpRes.length()-1)==',') { baseGxpRes =
		 * baseGxpRes.substring(0, baseGxpRes.length()-1); }
		 */

		// get CXR results

		try
		{
			results = HibernateUtil.util.selectObjects ("select XRayLabID from xrayresults where PatientID='" + id + "' AND XRayDate IS NOT NULL ORDER BY XRayDate ASC");
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
		}

		if (results == null)
		{
			return XmlUtil.createErrorXml ("Could not find XRay Results for patient with id " + id + " Please try again.");
		}

		temp = null;
		tempBarcode = null;
		numResults = results.length;
		XrayResults tempX = null;
		for (int i = 0; i < numResults; i++)
		{
			temp = null;

			tempBarcode = (String) (results[i]);

			try
			{
				tempX = ssl.findXrayResultsByXRayTestID (tempBarcode);
			}
			catch (Exception e)
			{
				e.printStackTrace ();
				tempX = null;
			}

			if (tempX != null)
			{
				cxrResult = tempX.getXrayResult ();

				if (cxrResult != null)
				{
					cxr += "\n" + tempBarcode + ": " + cxrResult;
				}
				else
				{
					cxr += "\n" + tempBarcode + ": " + "Pending";
				}
			}
		}

		/*
		 * if(cxr.length()>0 && cxr.charAt(cxr.length()-1)==',') { cxr =
		 * cxr.substring(0, cxr.length()-1); }
		 */

		// smear 2
		try
		{
			results = HibernateUtil.util.selectObjects ("select SputumLabID from sputumresults where PatientID='" + id + "' AND Month=2 AND SmearOrderDate IS NOT NULL ORDER BY SmearOrderDate ASC");

		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
		}

		if (results == null)
		{
			return XmlUtil.createErrorXml ("Could not find Sputum Results for patient with id " + id + " Please try again.");
		}

		temp = null;
		tempBarcode = null;
		numResults = results.length;

		for (int i = 0; i < numResults; i++)
		{
			temp = null;

			tempBarcode = (String) (results[i]);

			try
			{
				temp = ssl.findSputumResultsBySputumLabID (tempBarcode);
			}
			catch (Exception e)
			{
				e.printStackTrace ();
				temp = null;
			}

			if (temp != null)
			{
				smearRes = temp.getSmearResult ();
				// remarks = temp.getRemarks();

				if (smearRes != null)
				{
					smear2 += "\n" + tempBarcode + ": " + smearRes;
				}
				else
				{
					smear2 += "\n" + tempBarcode + ": " + "Pending";
				}
			}
		}

		/*
		 * if(smear2.length()>0 && smear2.charAt(smear2.length()-1)==',') {
		 * smear2 = smear2.substring(0, smear2.length()-1); }
		 */

		// smear 3

		try
		{
			results = HibernateUtil.util.selectObjects ("select SputumLabID from sputumresults where PatientID='" + id + "' AND Month=3 AND SmearOrderDate IS NOT NULL ORDER BY SmearOrderDate ASC");

		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
		}

		if (results == null)
		{
			return XmlUtil.createErrorXml ("Could not find Sputum Results for patient with id " + id + " Please try again.");
		}

		temp = null;
		tempBarcode = null;
		numResults = results.length;

		for (int i = 0; i < numResults; i++)
		{
			temp = null;

			tempBarcode = (String) (results[i]);

			try
			{
				temp = ssl.findSputumResultsBySputumLabID (tempBarcode);
			}
			catch (Exception e)
			{
				e.printStackTrace ();
				temp = null;
			}

			if (temp != null)
			{
				smearRes = temp.getSmearResult ();
				// remarks = temp.getRemarks();

				if (smearRes != null)
				{
					smear3 += "\n" + tempBarcode + ": " + smearRes;
				}
				else
				{
					smear3 += "\n" + tempBarcode + ": " + "Pending";
				}
			}
		}

		/*
		 * if(smear3.length()>0 && smear3.charAt(smear3.length()-1)==',') {
		 * smear3 = smear3.substring(0, smear3.length()-1); }
		 */

		// smear 5

		try
		{
			results = HibernateUtil.util.selectObjects ("select SputumLabID from sputumresults where PatientID='" + id + "' AND Month=5 AND SmearOrderDate IS NOT NULL ORDER BY SmearOrderDate ASC");

		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
		}

		if (results == null)
		{
			return XmlUtil.createErrorXml ("Could not find Sputum Results for patient with id " + id + " Please try again.");
		}

		temp = null;
		tempBarcode = null;
		numResults = results.length;

		for (int i = 0; i < numResults; i++)
		{
			temp = null;

			tempBarcode = (String) (results[i]);

			try
			{
				temp = ssl.findSputumResultsBySputumLabID (tempBarcode);
			}
			catch (Exception e)
			{
				e.printStackTrace ();
				temp = null;
			}

			if (temp != null)
			{
				smearRes = temp.getSmearResult ();
				// remarks = temp.getRemarks();

				if (smearRes != null)
				{
					smear5 += "\n" + tempBarcode + ": " + smearRes;
				}
				else
				{
					smear5 += "\n" + tempBarcode + ": " + "Pending";
				}
			}
		}

		/*
		 * if(smear5.length()>0 && smear5.charAt(smear5.length()-1)==',') {
		 * smear5 = smear5.substring(0, smear5.length()-1); }
		 */

		// smear 7
		try
		{
			results = HibernateUtil.util.selectObjects ("select SputumLabID from sputumresults where PatientID='" + id + "' AND Month=7 AND SmearOrderDate IS NOT NULL ORDER BY SmearOrderDate ASC");

		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
		}

		if (results == null)
		{
			return XmlUtil.createErrorXml ("Could not find Sputum Results for patient with id " + id + " Please try again.");
		}

		temp = null;
		tempBarcode = null;
		numResults = results.length;

		for (int i = 0; i < numResults; i++)
		{
			temp = null;

			tempBarcode = (String) (results[i]);

			try
			{
				temp = ssl.findSputumResultsBySputumLabID (tempBarcode);
			}
			catch (Exception e)
			{
				e.printStackTrace ();
				temp = null;
			}

			if (temp != null)
			{
				smearRes = temp.getSmearResult ();
				// remarks = temp.getRemarks();

				if (smearRes != null)
				{
					smear7 += "\n" + tempBarcode + ": " + smearRes;
				}
				else
				{
					smear7 += "\n" + tempBarcode + ": " + "Pending";
				}
			}
		}

		/*
		 * if(smear7.length()>0 && smear7.charAt(smear7.length()-1)==',') {
		 * smear7 = smear7.substring(0, smear7.length()-1); }
		 */

		/*
		 * String[][] gxpResults=null; try { gxpResults =
		 * ssl.getTableData("GeneXpertResults", new String[] {"IsPositive",
		 * "DrugResistance"}, " where PatientID='" + id +
		 * "' AND DateTested IS NOT NULL ORDER BY DateTested ASC"); } catch
		 * (Exception e2) { e2.printStackTrace(); }
		 * 
		 * if(gxpResults!=null) { for(int i=0; i<gxpResults.length; i++) {
		 * if(gxpResults[i][0]!=null) { if(gxpResults[i][0].equals("false"))
		 * baseGxp += "Negative "; else if(gxpResults[i][0].equals("true"))
		 * baseGxp += "Positive "; } else baseGxp += "N/A" + " ";
		 * 
		 * if(gxpResults[i][1]!=null) baseGxpRes += gxpResults[i][1] + " "; else
		 * baseGxpRes += "N/A" + " "; } }
		 * 
		 * String diseaseSite = pat.getDiseaseSite(); if(diseaseSite==null) {
		 * diseaseSite = " "; } String type = pat.getPatientType();
		 * if(type==null) { type = " "; }
		 * 
		 * String category = pat.getDiseaseCategory(); if(category==null) {
		 * category = " "; }
		 * 
		 * if(category==null) { category = " "; }
		 * 
		 * String [] rowRecord = null;
		 * 
		 * String treatmentStartDate = ""; try { colData =
		 * ssl.getColumnData("Encounter",
		 * "CONCAT(encounterId, '|', pid2) AS column1", " where pid1='" + id +
		 * "' AND encounterType='BASELINE' ORDER BY dateEncounterStart ASC");
		 * 
		 * rowRecord = ssl.getRowRecord("Encounter", new String[]{"EncounterID",
		 * "PID2"}, " where PID1= '" + id +
		 * "'AND EncounterType='BASELINE' ORDER BY DateEncounterStart ASC"); }
		 * catch (Exception e1) { e1.printStackTrace(); }
		 * 
		 * if(rowRecord==null || rowRecord.length!=2) { treatmentStartDate =
		 * "N/A"; if(rowRecord==null) System.out.println("null"); else
		 * System.out.println(rowRecord.length); }
		 * 
		 * else {
		 * 
		 * System.out.println(rowRecord[0]); System.out.print(rowRecord[1]); int
		 * encId = Integer.parseInt(rowRecord[0]); String pid2 = rowRecord[1];
		 * EncounterResultsId eri = new
		 * EncounterResultsId(encId,id,pid2,"entered_date"); EncounterResults er
		 * = null; try { er = ssl.findEncounterResultsByElement(eri); } catch
		 * (Exception e) { e.printStackTrace(); }
		 * 
		 * if(er!=null) { treatmentStartDate = er.getValue(); treatmentStartDate
		 * = treatmentStartDate.split(" ")[0]; }
		 * 
		 * else { treatmentStartDate = "N/A"; } }
		 * 
		 * ////////CHECK FOR END OF FOLLOW-UP
		 * 
		 * //GET FOLLOW-UP INFO
		 * 
		 * String followUpData = "";
		 * 
		 * String[] months = null;
		 * 
		 * try { months = ssl.getColumnData("EncounterResults", "Value",
		 * " where PID1='" + id + "' AND Element='MONTH' ORDER BY Value ASC");
		 * 
		 * if(months==null) followUpData = "<Error getting data>"; else
		 * if(months.length==0) { followUpData = "N/A"; } else { for(int i=0;
		 * i<months.length;i++) { followUpData += months[i] + ", "; }
		 * 
		 * followUpData = followUpData.trim(); if(followUpData.endsWith(",")) {
		 * followUpData = followUpData.substring(0, followUpData.length()-1); }
		 * } } catch (Exception e2) { e2.printStackTrace(); followUpData =
		 * "<Error getting data>"; }
		 * 
		 * ChartWalker cw = new ChartWalker(id); String flowPosition= "";
		 * 
		 * try { flowPosition = cw.walk(); } catch (Exception e1) {
		 * e1.printStackTrace(); flowPosition = "ERROR"; }
		 */
		String data = "Lab results for Patient: " + id;

		/*
		 * data += "\nFirst Name: " + fname; data += "\nLast Name: " + lname;
		 * data += "\nMR: " + mrNum; data += "\nTx Start: " +
		 * treatmentStartDate;
		 */
		data += "\nBase Smear: " + baseSmear;
		data += "\n\nXpert Result: " + baseGxp;
		data += "\n\nRIF Resistance: " + baseGxpRes;
		data += "\n\nCXR: " + cxr;
		data += "\n\nSmear - 2m: " + smear2;
		data += "\n\nSmear - 3m: " + smear3;
		data += "\n\nSmear - 5m: " + smear5;
		data += "\n\nSmear - 7m: " + smear7;
		/*
		 * data += "\nBase GXP: " + baseGxp; data += "\nRIF Resistance: " +
		 * baseGxpRes; data += "\nDisease Site: " + diseaseSite; data +=
		 * "\nPatient Type: " + type; data += "\nCategory: " + category; data +=
		 * "\nHistory of TB Drugs: " + tbhist; data += "\nSmear - 2m: " +
		 * smear2; data += "\nSmear - 3m: " + smear3; data += "\nSmear - 5m: " +
		 * smear5; data += "\nSmear - 7m: " + smear7; data += "\n" + diagType +
		 * "Clinical Diagnosis: " + diag; data += "\n" + diagType +
		 * "Clinical Diagnosis Date: " + diagDate; data += "\nFollow-ups: " +
		 * followUpData; data += "\nEnd of Followup: " + endOfFollowup;
		 * if(request.getParameter("un").equalsIgnoreCase("G-QUACK-99")) { data
		 * += "\nNext Steps: " + flowPosition; }
		 */
		Document doc = null;

		try
		{
			doc = DocumentBuilderFactory.newInstance ().newDocumentBuilder ().newDocument ();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace ();
			return "";
		}
		Element responseNode = doc.createElement (XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element dataNode = doc.createElement ("data");
		Text dataValue = doc.createTextNode (data);
		dataNode.appendChild (dataValue);

		responseNode.appendChild (dataNode);

		doc.appendChild (responseNode);

		xml = XmlUtil.docToString (doc);

		return xml;
	}

	/*
	 * public String getPaedClinicalDiagInfo(String id) { String xml = null;
	 * 
	 * 
	 * ssl = new ServerServiceImpl(); Person p = null; Patient pat = null;
	 * 
	 * Date dob = null;
	 * 
	 * try { p = (Person)ssl.findPerson(id); } catch (Exception e2) {
	 * e2.printStackTrace(); }
	 * 
	 * if(p==null) { return
	 * XmlUtil.createErrorXml("Could not find Person with id " + id +
	 * "Please try again."); }
	 * 
	 * long age = 0; dob = p.getDob(); if(dob!=null) { age = getAgeInYears(dob);
	 * System.out.println("AGE:->>>" + age); }
	 * 
	 * else { age = -1; System.out.println("AGE:->>>" + age); }
	 * 
	 * try { pat = ssl.findPatient(id); } catch (Exception e1) {
	 * e1.printStackTrace(); return
	 * XmlUtil.createErrorXml("Could not find Patient with id " + id +
	 * ". Please try again."); }
	 * 
	 * if(pat==null) { return
	 * XmlUtil.createErrorXml("Could not find Patient with id " + id +
	 * "Please try again."); }
	 * 
	 * 
	 * String firstname = p.getFirstName(); String lastname = p.getLastName();
	 * String gender = ""; if(p.getGender()=='M') { gender = "Male"; }
	 * 
	 * else { gender = "Female"; }
	 * 
	 * //get other data here String weight = ""; if(pat.getWeight()!=null)
	 * weight = pat.getWeight().toString();
	 * 
	 * //get confirmation encounter String rowRecord[] = null;
	 * 
	 * try { rowRecord = ssl.getRowRecord("Encounter", new
	 * String[]{"EncounterID", "PID2"}, " where PID1= '" + id +
	 * "'AND encounterType='PAED_CONF'"); } catch (Exception e1) {
	 * e1.printStackTrace(); }
	 * 
	 * if(rowRecord==null || rowRecord.length==0) { return
	 * XmlUtil.createErrorXml(
	 * "No paediatric confirmation found. Please fill in O form first and then try again."
	 * ); }
	 * 
	 * String encounterId = rowRecord[0]; int encId =
	 * Integer.parseInt(encounterId); String pid2 = rowRecord[1];
	 * 
	 * 
	 * String weightPercentile = null; String chestExam = null; String lymphExam
	 * = null; String abdMass = null; String otherExam = null; String
	 * otherExamDetail = null; String bcgScar = null; String currentHistory =
	 * null; String previousHistory = null; String numCurrentHistory = null;
	 * String numPreviousHistory = null; int numCurr = 0; int numPrev = 0;
	 * 
	 * String currentHistoryDetails = ""; String previousHistoryDetails = "";
	 * 
	 * EncounterResultsId eri = null; String data = "";
	 * 
	 * try { //get weight percentile eri = new
	 * EncounterResultsId(encId,id,pid2,"WEIGHT_PERCENTILE"); EncounterResults
	 * er = null;
	 * 
	 * er = ssl.findEncounterResultsByElement(eri); if(er!=null) {
	 * weightPercentile = er.getValue(); }
	 * 
	 * //get various exam results eri.setElement("CHEST_EXAM"); er =
	 * ssl.findEncounterResultsByElement(eri); if(er!=null) { chestExam =
	 * er.getValue(); }
	 * 
	 * eri.setElement("LYMPH_EXAM"); er =
	 * ssl.findEncounterResultsByElement(eri); if(er!=null) { lymphExam =
	 * er.getValue(); }
	 * 
	 * eri.setElement("ABD_MASS"); er = ssl.findEncounterResultsByElement(eri);
	 * if(er!=null) { abdMass = er.getValue(); }
	 * 
	 * eri.setElement("OTHER_EXAM"); er =
	 * ssl.findEncounterResultsByElement(eri); if(er!=null) { otherExam =
	 * er.getValue(); }
	 * 
	 * eri.setElement("OTHER_EXAM_DETAIL"); er =
	 * ssl.findEncounterResultsByElement(eri); if(er!=null) { otherExamDetail =
	 * er.getValue(); }
	 * 
	 * //get bcg result eri.setElement("BCG_SCAR"); er =
	 * ssl.findEncounterResultsByElement(eri); if(er!=null) { bcgScar =
	 * er.getValue(); }
	 * 
	 * //get current family history results eri.setElement("CURRENT_HISTORY");
	 * er = ssl.findEncounterResultsByElement(eri); if(er!=null) {
	 * currentHistory = er.getValue(); }
	 * 
	 * eri.setElement("NUM_CURRENT_HISTORY"); try { er =
	 * ssl.findEncounterResultsByElement(eri); }
	 * 
	 * catch(ArrayIndexOutOfBoundsException ao) { er = null; } if(er!=null) {
	 * numCurrentHistory = er.getValue(); if(numCurrentHistory!=null) { numCurr
	 * = Integer.parseInt(numCurrentHistory); currentHistoryDetails +=
	 * "\nNo. with current TB: " + numCurr;
	 * 
	 * "current_rel_"; "current_other_rel_" current_tb_form_" "current_tb_type_"
	 * "current_ss_"
	 * 
	 * for(int i=0; i<numCurr; i++) { currentHistoryDetails += "\nFam. member "
	 * + (i+1); eri.setElement("CURRENT_REL_" + (i+1)); er =
	 * ssl.findEncounterResultsByElement(eri); if(er!=null) {
	 * currentHistoryDetails += "\nRelationship: " + er.getValue(); }
	 * 
	 * eri.setElement("CURRENT_OTHER_REL_" + (i+1)); er =
	 * ssl.findEncounterResultsByElement(eri); if(er!=null) {
	 * currentHistoryDetails += "\nOther Relationship: " + er.getValue(); }
	 * 
	 * eri.setElement("CURRENT_TB_FORM_" + (i+1)); er =
	 * ssl.findEncounterResultsByElement(eri); if(er!=null) {
	 * currentHistoryDetails += "\nTB Form: " + er.getValue(); }
	 * 
	 * eri.setElement("CURRENT_TB_TYPE_" + (i+1)); er =
	 * ssl.findEncounterResultsByElement(eri); if(er!=null) {
	 * currentHistoryDetails += "\nTB Type: " + er.getValue(); }
	 * 
	 * eri.setElement("CURRENT_SS_" + (i+1)); er =
	 * ssl.findEncounterResultsByElement(eri); if(er!=null) {
	 * currentHistoryDetails += "\nSS +ve: " + er.getValue(); } } } }
	 * 
	 * //get previous family history results eri.setElement("PREVIOUS_HISTORY");
	 * er = ssl.findEncounterResultsByElement(eri); if(er!=null) {
	 * previousHistory = er.getValue(); }
	 * 
	 * eri.setElement("NUM_PREVIOUS_HISTORY"); try { er =
	 * ssl.findEncounterResultsByElement(eri); }
	 * 
	 * catch(ArrayIndexOutOfBoundsException ao) { er = null; } if(er!=null) {
	 * numPreviousHistory = er.getValue(); if(numPreviousHistory!=null) {
	 * numPrev = Integer.parseInt(numPreviousHistory); previousHistoryDetails +=
	 * "\nNo. with previous TB: " + numPrev;
	 * 
	 * for(int i=0; i<numPrev; i++) { previousHistoryDetails += "\nFam. member "
	 * + (i+1); eri.setElement("PREVIOUS_REL_" + (i+1)); er =
	 * ssl.findEncounterResultsByElement(eri); if(er!=null) {
	 * previousHistoryDetails += "\nRelationship: " + er.getValue(); }
	 * 
	 * eri.setElement("PREVIOUS_OTHER_REL_" + (i+1)); er =
	 * ssl.findEncounterResultsByElement(eri); if(er!=null) {
	 * previousHistoryDetails += "\nOther Relationship: " + er.getValue(); }
	 * 
	 * eri.setElement("PREVIOUS_TB_FORM_" + (i+1)); er =
	 * ssl.findEncounterResultsByElement(eri); if(er!=null) {
	 * previousHistoryDetails += "\nTB Form: " + er.getValue(); }
	 * 
	 * eri.setElement("PREVIOUS_TB_TYPE_" + (i+1)); er =
	 * ssl.findEncounterResultsByElement(eri); if(er!=null) {
	 * previousHistoryDetails += "\nTB Type: " + er.getValue(); }
	 * 
	 * eri.setElement("PREVIOUS_SS_" + (i+1)); er =
	 * ssl.findEncounterResultsByElement(eri); if(er!=null) {
	 * previousHistoryDetails += "\nSS +ve: " + er.getValue(); } } } }
	 * 
	 * } catch (Exception e1) { e1.printStackTrace(); return
	 * XmlUtil.createErrorXml("Error retrieving Patient data"); }
	 * 
	 * data += "Patient ID: " + id; data += "\nMR Number: "+ pat.getMrno(); data
	 * += "\nFirst Name: " + firstname; data += "\nLast Name: " + lastname; data
	 * += "\nAge: " + age; data += "\nGender: " + gender; data += "\nWeight: " +
	 * weight; data += "\nWeight Percentile: " + weightPercentile; data +=
	 * "\nChest Exam: " + chestExam; data += "\nLymph Node Exam: " + lymphExam;
	 * data += "\nAbd. Mass: " + abdMass; data += "\nOther Exam: " + otherExam;
	 * data += "\nOther Exam Details: " + otherExamDetail; data +=
	 * "\nBCG + Scar: " + bcgScar; data += "\nCurrent Family History: " +
	 * currentHistory; data += currentHistoryDetails; data +=
	 * "\nPrevious Family History: " + previousHistory; data +=
	 * previousHistoryDetails;
	 * 
	 * Document doc = null;
	 * 
	 * try { doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
	 * .newDocument(); } catch (ParserConfigurationException e) { //
	 * Auto-generated catch block e.printStackTrace(); return ""; }
	 * 
	 * Element responseNode = doc.createElement(XmlStrings.RESPONSE);
	 * 
	 * Element dataNode = doc.createElement("data"); Text dataValue =
	 * doc.createTextNode(data); dataNode.appendChild(dataValue);
	 * 
	 * responseNode.appendChild(dataNode);
	 * 
	 * doc.appendChild(responseNode);
	 * 
	 * xml = XmlUtil.docToString(doc);
	 * 
	 * return xml; }
	 */

	/*
	 * public String getPaedConfirmationInfo(String id) { String xml = null;
	 * 
	 * 
	 * ssl = new ServerServiceImpl(); Person p = null; Patient pat = null;
	 * 
	 * Date dob = null;
	 * 
	 * try { p = (Person)ssl.findPerson(id); } catch (Exception e2) {
	 * e2.printStackTrace(); }
	 * 
	 * if(p==null) { return
	 * XmlUtil.createErrorXml("Could not find Person with id " + id +
	 * "Please try again."); }
	 * 
	 * long age = 0; dob = p.getDob(); if(dob!=null) { age = getAgeInYears(dob);
	 * System.out.println("AGE:->>>" + age); }
	 * 
	 * else { age = -1; System.out.println("AGE:->>>" + age); }
	 * 
	 * try { pat = ssl.findPatient(id); } catch (Exception e1) {
	 * e1.printStackTrace(); return
	 * XmlUtil.createErrorXml("Could not find Patient with id " + id +
	 * ". Please try again."); }
	 * 
	 * if(pat==null) { return
	 * XmlUtil.createErrorXml("Could not find Patient with id " + id +
	 * "Please try again."); }
	 * 
	 * 
	 * String firstname = p.getFirstName(); String lastname = p.getLastName();
	 * String gender = ""; if(p.getGender()=='M') { gender = "Male"; }
	 * 
	 * else { gender = "Female"; } Document doc = null;
	 * 
	 * try { doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
	 * .newDocument(); } catch (ParserConfigurationException e) { //
	 * Auto-generated catch block e.printStackTrace(); return ""; } Element
	 * responseNode = doc.createElement(XmlStrings.RESPONSE); //
	 * responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);
	 * 
	 * Element fNameNode = doc.createElement("fname"); //Text fNameValue =
	 * doc.createTextNode("Test"); Text fNameValue =
	 * doc.createTextNode(firstname); fNameNode.appendChild(fNameValue);
	 * 
	 * responseNode.appendChild(fNameNode);
	 * 
	 * Element lNameNode = doc.createElement("lname"); //Text lNameValue =
	 * doc.createTextNode("Patient"); Text lNameValue =
	 * doc.createTextNode(lastname); lNameNode.appendChild(lNameValue);
	 * 
	 * responseNode.appendChild(lNameNode);
	 * 
	 * Element mrNode = doc.createElement("mr"); //Text lNameValue =
	 * doc.createTextNode("Patient"); Text mrValue =
	 * doc.createTextNode(pat.getMrno()); mrNode.appendChild(mrValue);
	 * 
	 * responseNode.appendChild(mrNode);
	 * 
	 * Element genderNode = doc.createElement("gender"); //Text lNameValue =
	 * doc.createTextNode("Patient"); Text genderValue =
	 * doc.createTextNode(gender); genderNode.appendChild(genderValue);
	 * 
	 * responseNode.appendChild(genderNode);
	 * 
	 * Element ageNode = doc.createElement("age"); //Text lNameValue =
	 * doc.createTextNode("Patient"); Text ageValue = doc.createTextNode(new
	 * Long(age).toString()); ageNode.appendChild(ageValue);
	 * 
	 * responseNode.appendChild(ageNode);
	 * 
	 * doc.appendChild(responseNode);
	 * 
	 * xml = XmlUtil.docToString(doc);
	 * 
	 * return xml;
	 * 
	 * 
	 * }
	 */

	public String getPatientEncounterHistory (String id)
	{
		String xml = null;

		ssl = new ServerServiceImpl ();

		String[][] encounters = null;

		try
		{
			encounters = ssl.getTableData ("Encounter", new String[] {"EncounterType", "DATE_FORMAT(DateEncounterStart,'%d%/%m%/%Y')"}, " where PID1='" + id + "'");
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			return XmlUtil.createErrorXml ("Error performing search. Please try again!");
		}

		if (encounters == null)
		{
			return XmlUtil.createErrorXml ("Error performing search. Please try again!");
		}
		else if (encounters.length == 0)
		{
			return XmlUtil.createErrorXml ("No results found!");
		}

		String type = "";
		String date = "";
		String data = "EncounterType - Date";
		for (int i = 0; i < encounters.length; i++)
		{
			type = encounters[i][0];
			date = encounters[i][1];

			data += "\n" + type + " - " + date;
		}

		Document doc = null;

		try
		{
			doc = DocumentBuilderFactory.newInstance ().newDocumentBuilder ().newDocument ();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace ();
			return "";
		}
		Element responseNode = doc.createElement (XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element dataNode = doc.createElement ("data");
		Text dataValue = doc.createTextNode (data);
		dataNode.appendChild (dataValue);

		responseNode.appendChild (dataNode);

		doc.appendChild (responseNode);

		xml = XmlUtil.docToString (doc);

		return xml;
	}

	/*
	 * public String getScreeningInfo() { result = ssl.getTableData(
	 * "definition" , new String[]{"DefinitionKey","Value"} , "where" ) String
	 * labsArray=""; for (String[] strings : result) { labsArray +=
	 * strings[0]+"="+strings[1]+";"; } }
	 */

	public String getSearchInfo (String id)
	{
		String xml = null;

		ssl = new ServerServiceImpl ();

		// String id = request.getParameter("id");
		// String mr = request.getParameter("mr");
		// String iType = request.getParameter("itype");
		Screening scr = null;
		Person per = null;
		Contact c = null;
		Patient pat = null;

		try
		{
			scr = ssl.findScreeningByPatientID (id);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}

		if (scr == null)
		{
			return XmlUtil.createErrorXml ("Patient with ID: " + id + " not found.");
		}

		int age = scr.getAge ();

		String fName = "";
		String fathersName = "";
		/*
		 * Date dobDate = p.getDob(); String dob = null; if(dobDate!=null) dob =
		 * dobDate.toString();
		 */
		String mStatus = "";
		/*
		 * long age = 0;
		 * 
		 * if(dob!=null) { age = getAgeInYears(p.getDob()); }
		 */
		String tbhist = scr.getTbhistory ();
/*		String tbmed = (scr.getMedication () == null || !scr.getMedication () ? "NO" : "YES");
		String meddur = (scr.getMedicationDuration () == null ? " " : scr.getMedicationDuration ());
*/		String tbfamhist = scr.getFamilyTbhistory ();

		try
		{
			per = ssl.findPerson (id);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}

		String nic = "";
		char gender = scr.getGender ();
		String genderString = "";
		genderString = "" + gender;

		if (per != null)
		{
			fName = per.getFirstName ();
			fathersName = per.getGuardianName ();
			/*
			 * Date dobDate = p.getDob(); String dob = null; if(dobDate!=null)
			 * dob = dobDate.toString();
			 */
			mStatus = per.getMaritalStatus ();
			/*
			 * long age = 0;
			 * 
			 * if(dob!=null) { age = getAgeInYears(p.getDob()); }
			 */

			nic = per.getNic ();
		}
		// String state = " ";
		// String mrNum = " ";
		String houseNum = " ";
		String street = " ";
		String sector = " ";
		String town = " ";
		String colony = " ";
		String uc = " ";
		String landmark = " ";
		String phone = " ";
		// String numAdults = " ";
		// String numChildren = " ";

		if (fName == null)
			fName = " ";
		if (fathersName == null)
			fathersName = " ";
		/*
		 * if(dob==null) dob = " ";
		 */
		if (nic == null)
			nic = " ";
		if (mStatus == null)
			mStatus = " ";

		try
		{
			c = ssl.findContact (id);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}

		if (c != null)
		{
			if (!c.getAddressHouse ().equals ("N/A"))
			{
				houseNum = c.getAddressHouse ();
			}

			if (!c.getAddressStreet ().equals ("N/A"))
			{
				street = c.getAddressStreet ();
			}

			if (!c.getAddressSector ().equals ("N/A"))
			{
				sector = c.getAddressSector ();
			}

			if (!c.getAddressColony ().equals ("N/A"))
			{
				colony = c.getAddressColony ();
			}

			if (!c.getAddressTown ().equals ("N/A"))
			{
				town = c.getAddressTown ();
			}

			if (!c.getAddressUc ().equals ("N/A"))
			{
				uc = c.getAddressUc ();
			}

			if (!c.getAddressLandMark ().equals ("N/A"))
			{
				landmark = c.getAddressLandMark ();
			}

			if (c.getPhone () != null)
			{
				phone = c.getPhone ();
			}

			/*
			 * if(c.getHouseHoldAdults()!=null) { numAdults =
			 * c.getHouseHoldAdults().toString(); }
			 * 
			 * if(c.getHouseHoldChildren()!=null) { numChildren =
			 * c.getHouseHoldChildren().toString(); }
			 */
		}

		String regimen = " ";
		String fdctab = " ";
		String strepto = " ";

		try
		{
			pat = ssl.findPatient (id);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}

		if (pat != null)
		{
			regimen = pat.getRegimen () == null ? " " : pat.getRegimen ();
			fdctab = pat.getDoseCombination () == null ? " " : Float.toString (pat.getDoseCombination ());
			strepto = pat.getStreptomycin () == null ? " " : Integer.toString (pat.getStreptomycin ());
		}
		/*
		 * if(pat!=null) {
		 * 
		 * if(pat.getMrno()!=null) mrNum = pat.getMrno();
		 * 
		 * if(pat.getPatientStatus()!=null) { state = pat.getPatientStatus(); }
		 * 
		 * }
		 * 
		 * if(age<=0) { String[] er = null; try { er =
		 * ssl.getColumnData("EncounterResults", "Value", " where PID1='"+ id +
		 * " ' AND Element='AGE'"); } catch (Exception e) { e.printStackTrace();
		 * }
		 * 
		 * String ageString = er[0]; try { age = Long.parseLong(ageString); }
		 * 
		 * catch(Exception e) { e.printStackTrace(); age = 0; } }
		 */

		Document doc = null;

		try
		{
			doc = DocumentBuilderFactory.newInstance ().newDocumentBuilder ().newDocument ();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace ();
			return "";
		}
		Element responseNode = doc.createElement (XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element idNode = doc.createElement ("pid");
		Text idValue = doc.createTextNode (id);
		idNode.appendChild (idValue);

		responseNode.appendChild (idNode);

		Element fnameNode = doc.createElement ("fname");
		Text fnameValue = doc.createTextNode (fName);
		fnameNode.appendChild (fnameValue);

		responseNode.appendChild (fnameNode);

		Element fatNameNode = doc.createElement ("fatname");
		Text fatNameValue = doc.createTextNode (fathersName);
		fatNameNode.appendChild (fatNameValue);

		responseNode.appendChild (fatNameNode);

		Element genderNode = doc.createElement ("gender");
		Text genderValue = doc.createTextNode (genderString);
		genderNode.appendChild (genderValue);

		responseNode.appendChild (genderNode);

		Element nicNode = doc.createElement ("nic");
		Text nicValue = doc.createTextNode (nic);
		nicNode.appendChild (nicValue);

		responseNode.appendChild (nicNode);

		/*
		 * Element dobNode = doc.createElement("dob"); Text dobValue =
		 * doc.createTextNode(dob); dobNode.appendChild(dobValue);
		 * 
		 * responseNode.appendChild(dobNode);
		 */

		Element ageNode = doc.createElement ("age");
		Text ageValue = doc.createTextNode (new Long (age).toString ());
		ageNode.appendChild (ageValue);

		responseNode.appendChild (ageNode);

		Element tbhistNode = doc.createElement ("tbhist");
		Text tbhistValue = doc.createTextNode (tbhist);
		tbhistNode.appendChild (tbhistValue);

		responseNode.appendChild (tbhistNode);

		/*Element tbmedNode = doc.createElement ("tbmed");
		Text tbmedValue = doc.createTextNode (tbmed);
		tbmedNode.appendChild (tbmedValue);

		responseNode.appendChild (tbmedNode);

		Element meddurNode = doc.createElement ("meddur");
		Text meddurValue = doc.createTextNode (meddur);
		meddurNode.appendChild (meddurValue);
*/
		//responseNode.appendChild (meddurNode);

		Element tbfamhistNode = doc.createElement ("tbfamhist");
		Text tbfamhistValue = doc.createTextNode (tbfamhist);
		tbfamhistNode.appendChild (tbfamhistValue);

		responseNode.appendChild (tbfamhistNode);

		Element regimenNode = doc.createElement ("regimen");
		Text regimenValue = doc.createTextNode (regimen);
		regimenNode.appendChild (regimenValue);

		responseNode.appendChild (regimenNode);

		Element fdctabNode = doc.createElement ("fdctab");
		Text fdctabValue = doc.createTextNode (fdctab);
		fdctabNode.appendChild (fdctabValue);

		responseNode.appendChild (fdctabNode);

		Element streptoNode = doc.createElement ("strepto");
		Text streptoValue = doc.createTextNode (strepto);
		streptoNode.appendChild (streptoValue);

		responseNode.appendChild (streptoNode);

		Element phoneNode = doc.createElement ("phone");
		Text phoneValue = doc.createTextNode (phone);
		phoneNode.appendChild (phoneValue);

		responseNode.appendChild (phoneNode);

		Element mStatusNode = doc.createElement ("mstatus");
		Text mStatusValue = doc.createTextNode (mStatus);
		mStatusNode.appendChild (mStatusValue);

		responseNode.appendChild (mStatusNode);

		Element houseNumNode = doc.createElement ("housenum");
		Text houseNumValue = doc.createTextNode (houseNum);
		houseNumNode.appendChild (houseNumValue);

		responseNode.appendChild (houseNumNode);

		Element streetNode = doc.createElement ("street");
		Text streetValue = doc.createTextNode (street);
		streetNode.appendChild (streetValue);

		responseNode.appendChild (streetNode);

		Element sectorNode = doc.createElement ("sector");
		Text sectorValue = doc.createTextNode (sector);
		sectorNode.appendChild (sectorValue);

		responseNode.appendChild (sectorNode);

		Element colonyNode = doc.createElement ("colony");
		Text colonyValue = doc.createTextNode (colony);
		colonyNode.appendChild (colonyValue);

		responseNode.appendChild (colonyNode);

		Element townNode = doc.createElement ("town");
		Text townValue = doc.createTextNode (town);
		townNode.appendChild (townValue);

		responseNode.appendChild (townNode);

		Element ucNode = doc.createElement ("uc");
		Text ucValue = doc.createTextNode (uc);
		ucNode.appendChild (ucValue);

		responseNode.appendChild (ucNode);

		Element landmarkNode = doc.createElement ("landmark");
		Text landmarkValue = doc.createTextNode (landmark);
		landmarkNode.appendChild (landmarkValue);

		responseNode.appendChild (landmarkNode);

		doc.appendChild (responseNode);

		xml = XmlUtil.docToString (doc);

		return xml;
	}

	public String getSputumCollectInfo (String id)
	{
		String xml = null;

		ssl = new ServerServiceImpl ();
		Person p = null;
		Patient pat = null;

		try
		{
			p = (Person) ssl.findPerson (id);
		}
		catch (Exception e2)
		{
			e2.printStackTrace ();
			return XmlUtil.createErrorXml ("Could not find Person with id " + id + ". Please try again.");
		}

		if (p == null)
		{
			return XmlUtil.createErrorXml ("Could not find Person with id " + id + ". Please try again.");
		}

		// ////////////////////////////////////////////////TODO///////////////////////////////////////////////
		// seems that as patient id has been moved to screening so only way to
		// get/verify suspect id is screening table
		// so we will check in screening table if person doesnt exists. and will
		// return true if it exists in...
		Screening scr = null;

		try
		{
			scr = ssl.findScreeningByPatientID (id);
		}
		catch (Exception e3)
		{
			e3.printStackTrace ();
			return XmlUtil.createErrorXml ("Could not find Person/Screening data for patient-id " + id + ". Please make sure patient has been screened and all required form have been filled.");
		}
		if (scr == null)
		{
			return XmlUtil.createErrorXml ("Could not find Person/Screening data for patient-id " + id + ". Please make sure patient has been screened and all required form have been filled.");
		}

		try
		{
			pat = ssl.findPatient (id);
		}
		catch (Exception e3)
		{
			e3.printStackTrace ();
			return XmlUtil.createErrorXml ("Could not find Patient data for patient-id " + id + ". Please make sure all required form have been filled.");
		}


	/*	Boolean tbMedicationTaken = scr.getMedication ();
		String tbMedicationDuration = scr.getMedicationDuration ();
*/		String allowGxp = "No";
		/*if (tbMedicationTaken == true && tbMedicationDuration != null && tbMedicationDuration.equalsIgnoreCase ("> 1 month"))
		{
			allowGxp = "Yes";
		}
*/		String[] sputumResults = null;
		int numResults = -1;
		try
		{
			sputumResults = ssl.getColumnData ("sputumresults", "SmearResult", " where PatientID='" + id + "' " + " AND SmearTestDone=1" + " AND Month=0 " + " AND SmearResult IS NOT NULL "
					+ " AND SmearOrderDate IS NOT NULL " + " AND (Details IS NULL OR Details NOT LIKE '%REJECTED%') " + " ORDER BY SmearOrderDate ASC");
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
			return XmlUtil.createErrorXml ("Error finding base Sputum Results for patient with id " + id + ". Please try again.");
		}
		boolean isAnyBaseSmearPositive = false;
		boolean isAnySmear3Positive = false;

		String baseSmear = " ";

		if (sputumResults != null)
		{
			numResults = sputumResults.length;

			for (int i = 0; i < numResults; i++)
			{
				if (!sputumResults[i].trim ().equalsIgnoreCase ("negative"))
				{// is +ve
					isAnyBaseSmearPositive = true;
				}
				baseSmear += sputumResults[i] + " ";
			}
		}
		// follow up smear 2
		String[] sputum2Results = null;
		int numSmear2Results = -1;
		try
		{
			sputum2Results = ssl.getColumnData ("sputumresults", "SmearResult", " where PatientID='" + id + "' " + " AND SmearTestDone=1 " + " AND Month=2 " + " AND SmearResult IS NOT NULL "
					+ " AND SmearOrderDate IS NOT NULL " + " AND (Details IS NULL OR Details NOT LIKE '%REJECTED%') " + " ORDER BY SmearOrderDate ASC");
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
		}

		String smear2 = " ";

		if (sputum2Results != null)
		{
			numSmear2Results = sputum2Results.length;

			for (int i = 0; i < numSmear2Results; i++)
			{
				smear2 += sputum2Results[i] + " ";
			}
		}

		// follow up smear 3
		String[] sputum3Results = null;
		int numSmear3Results = -1;
		try
		{
			sputum3Results = ssl.getColumnData ("sputumresults", "SmearResult", " where PatientID='" + id + "' " + " AND SmearTestDone=1 " + " AND Month=3 " + " AND SmearResult IS NOT NULL "
					+ " AND SmearOrderDate IS NOT NULL " + " AND (Details IS NULL OR Details NOT LIKE '%REJECTED%') " + " ORDER BY SmearOrderDate ASC");
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
		}

		String smear3 = " ";
		String[] rowRecord = null;

		String treatmentStartDate = "";
		String formattedtreatmentdate= "";
		try
		{
			rowRecord = ssl.getRowRecord ("encounter", new String[] {"EncounterID", "PID2"}, " where PID1= '" + id + "'" + " AND EncounterType='" + EncounterType.BASELINE + "'"
					+ " ORDER BY DateEncounterStart ASC");
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
		}

		if (rowRecord == null || rowRecord.length != 2)
		{
			treatmentStartDate = "N/A";
			System.out.println (rowRecord == null ? "null" : rowRecord.length);
		}
		else
		{
			System.out.println (rowRecord[0]);
			System.out.print (rowRecord[1]);
			int encId = Integer.parseInt (rowRecord[0]);
			String pid2 = rowRecord[1];
			EncounterResultsId eri = new EncounterResultsId (encId, id, pid2, "entered_date");
			EncounterResults er = null;
			try
			{
				er = ssl.findEncounterResultsByElement (eri);
			}
			catch (Exception e)
			{
				e.printStackTrace ();
			}

			if (er != null)
			{
				treatmentStartDate = er.getValue ();
			}
			else
			{
				treatmentStartDate = "N/A";
			}
			DateFormat dff=new SimpleDateFormat("dd/MM/yyyy");
			Date formatteddate = null;
			try {
				formatteddate = dff.parse(treatmentStartDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (sputum3Results != null)
		{
			numSmear3Results = sputum3Results.length;

			for (int i = 0; i < numSmear3Results; i++)
			{
				if (!sputum3Results[i].trim ().equalsIgnoreCase ("negative"))
				{// is +ve
					isAnySmear3Positive = true;
				}
				smear3 += sputum3Results[i] + " ";
			}
		}
		boolean allowMonth4 = false;
		if (isAnyBaseSmearPositive && isAnySmear3Positive && pat.getDiseaseCategory () != null && pat.getDiseaseCategory ().equalsIgnoreCase ("category 2"))
		{
			allowMonth4 = true;
		}
		boolean allowMonthThree = false;
		if (pat.getDiseaseCategory () != null 
				&& ( pat.getDiseaseCategory ().equalsIgnoreCase ("category 2")|| (pat.getDiseaseCategory ().equalsIgnoreCase ("category 1") 
								&& (!smear2.trim().equals("") && !smear2.equalsIgnoreCase("negative"))
							) 
					)
		){
			allowMonthThree = true;
		}
		
		Document doc = null;
		try
		{
			doc = DocumentBuilderFactory.newInstance ().newDocumentBuilder ().newDocument ();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace ();
			return "";
		}
		Element responseNode = doc.createElement (XmlStrings.RESPONSE);

		Element allowGxpNode = doc.createElement ("gxp");
		Text allowGxpValue = doc.createTextNode (allowGxp);
		allowGxpNode.appendChild (allowGxpValue);

		responseNode.appendChild (allowGxpNode);
		
		Element treatmentstart = doc.createElement ("txstart");
		Text treatstart = doc.createTextNode (treatmentStartDate);
		treatmentstart.appendChild (treatstart);

		responseNode.appendChild (treatmentstart);

		Element allowMonthThreeNode = doc.createElement ("month3");
		Text allowMonthThreeValue = doc.createTextNode ((allowMonthThree?"yes":"no"));
		allowMonthThreeNode.appendChild (allowMonthThreeValue);

		responseNode.appendChild (allowMonthThreeNode);
		
		Element allowMonth4Node = doc.createElement ("month4");
		Text allowMonth4Value = doc.createTextNode ((allowMonth4?"yes":"no"));
		allowMonth4Node.appendChild (allowMonth4Value);

		responseNode.appendChild (allowMonth4Node);
		
		doc.appendChild (responseNode);

		xml = XmlUtil.docToString (doc);

		return xml;

		// ///////////////////////////////////////WARNING only for testing
		// purpose. LOGIC needs to be
		// verified/////////////////////////////////////////////////////////
		/*
		 * long age = 0; dob = p.getDob(); if(dob!=null) { age =
		 * getAgeInYears(dob); System.out.println("AGE:->>>" + age); }
		 * 
		 * else { age = -1; System.out.println("AGE:->>>" + age); }
		 * 
		 * try { pat = ssl.findPatient(id); } catch (Exception e1) {
		 * e1.printStackTrace(); return
		 * XmlUtil.createErrorXml("Could not find Patient with id " + id +
		 * ". Please try again."); }
		 * 
		 * if(pat==null) { return
		 * XmlUtil.createErrorXml("Could not find Patient with id " + id +
		 * "Please try again."); }
		 * 
		 * try { c = ssl.findContact(id); } catch (Exception e1) {
		 * e1.printStackTrace(); return
		 * XmlUtil.createErrorXml("Could not find Contact for patient with id "
		 * + id + " Please try again."); }
		 * 
		 * if(c==null) {
		 * 
		 * return
		 * XmlUtil.createErrorXml("Could not find Contact for patient with id "
		 * + id + " Please try again.");
		 * 
		 * }
		 * 
		 * String address = ""; if(!c.getAddressHouse().equals("N/A")) { address
		 * += c.getAddressHouse() + ","; }
		 * 
		 * if(!c.getAddressStreet().equals("N/A")) { address +=
		 * c.getAddressStreet() + ","; }
		 * 
		 * if(!c.getAddressSector().equals("N/A")) { address += "Sector " +
		 * c.getAddressSector() + ","; }
		 * 
		 * if(!c.getAddressTown().equals("N/A")) { address += c.getAddressTown()
		 * + ","; }
		 * 
		 * if(!c.getAddressUc().equals("N/A")) { address += "UC " +
		 * c.getAddressUc() + ","; }
		 * 
		 * if(!c.getAddressLandMark().equals("N/A")) { address += "near " +
		 * c.getAddressLandMark(); }
		 * 
		 * String firstname = p.getFirstName(); String lastname =
		 * p.getLastName(); String diseaseCategory = pat.getDiseaseCategory();
		 * 
		 * if(diseaseCategory==null) { diseaseCategory = ""; }
		 * 
		 * String treatmentStartDate = ""; EncounterResults er = null;
		 * EncounterResultsId eri = null;
		 * 
		 * String [] rowRecord = null;
		 * 
		 * //String treatmentStartDate = ""; try { colData =
		 * ssl.getColumnData("Encounter",
		 * "CONCAT(encounterId, '|', pid2) AS column1", " where pid1='" + id +
		 * "' AND encounterType='BASELINE' ORDER BY dateEncounterStart ASC");
		 * 
		 * rowRecord = ssl.getRowRecord("Encounter", new String[]{"EncounterID",
		 * "PID2"}, " where PID1= '" + id +
		 * "' AND encounterType='BASELINE' ORDER BY dateEncounterStart ASC"); }
		 * catch (Exception e1) { e1.printStackTrace(); }
		 * 
		 * if(rowRecord==null || rowRecord.length!=2) { treatmentStartDate =
		 * "N/A"; }
		 * 
		 * else {
		 * 
		 * System.out.println(rowRecord[0]); System.out.print(rowRecord[1]); int
		 * encId = Integer.parseInt(rowRecord[0]); String pid2 = rowRecord[1];
		 * eri = new EncounterResultsId(encId,id,pid2,"entered_date"); er =
		 * null; try { er = ssl.findEncounterResultsByElement(eri); } catch
		 * (Exception e) { e.printStackTrace(); }
		 * 
		 * if(er!=null) { treatmentStartDate = er.getValue(); }
		 * 
		 * else { treatmentStartDate = "N/A"; } }
		 * 
		 * String[] er1 = null; try { er1 =
		 * ssl.getColumnData("EncounterResults", "Value", " where PID1='"+ id +
		 * "' AND Element='PAST_TB_DRUG_HISTORY_D1'"); } catch (Exception e) {
		 * e.printStackTrace(); }
		 * 
		 * String tbhist; if(er!=null && er1.length > 0) tbhist = er1[0]; else {
		 * try { er1 = ssl.getColumnData("EncounterResults", "Value",
		 * " where PID1='"+ id + "' AND Element='PAST_TB_DRUG_HISTORY'"); }
		 * catch (Exception e) { e.printStackTrace(); }
		 * 
		 * if(er1!=null && er1.length>0) tbhist = er1[0]; else tbhist =
		 * "Not Entered"; } Encounter e = findEncounter where encounter type =
		 * baseline treatment and pid1=patient ID. //EncounterResult = get
		 * encounter result where element is Entered date and encounter id is
		 * from above //
		 * 
		 * Document doc = null;
		 * 
		 * try { doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
		 * .newDocument(); } catch (ParserConfigurationException e) { //
		 * Auto-generated catch block e.printStackTrace(); return ""; } Element
		 * responseNode = doc.createElement(XmlStrings.RESPONSE); //
		 * responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);
		 * 
		 * Element fNameNode = doc.createElement("fname"); //Text fNameValue =
		 * doc.createTextNode("Test"); Text fNameValue =
		 * doc.createTextNode(firstname); fNameNode.appendChild(fNameValue);
		 * 
		 * responseNode.appendChild(fNameNode);
		 * 
		 * Element lNameNode = doc.createElement("lname"); //Text lNameValue =
		 * doc.createTextNode("Patient"); Text lNameValue =
		 * doc.createTextNode(lastname); lNameNode.appendChild(lNameValue);
		 * 
		 * responseNode.appendChild(lNameNode);
		 * 
		 * Element ageNode = doc.createElement("age"); //Text lNameValue =
		 * doc.createTextNode("Patient"); Text ageValue = doc.createTextNode(new
		 * Long(age).toString()); ageNode.appendChild(ageValue);
		 * 
		 * responseNode.appendChild(ageNode);
		 * 
		 * Element addrNode = doc.createElement("address"); Text addrValue =
		 * doc.createTextNode(address); addrNode.appendChild(addrValue);
		 * 
		 * responseNode.appendChild(addrNode);
		 * 
		 * Element txStartNode = doc.createElement("trstart"); //Text
		 * txStartValue = doc.createTextNode("29/11/10"); Text txStartValue =
		 * doc.createTextNode(treatmentStartDate);
		 * txStartNode.appendChild(txStartValue);
		 * 
		 * responseNode.appendChild(txStartNode);
		 * 
		 * Element catNode = doc.createElement("cat"); Text catValue =
		 * doc.createTextNode(diseaseCategory); catNode.appendChild(catValue);
		 * 
		 * responseNode.appendChild(catNode);
		 * 
		 * Element tbHistNode = doc.createElement("tbhist"); Text tbHistValue =
		 * doc.createTextNode(tbhist); tbHistNode.appendChild(tbHistValue);
		 * 
		 * responseNode.appendChild(tbHistNode);
		 * 
		 * doc.appendChild(responseNode);
		 */
	}

	private String getSputumResultsInfo (String labid, String labbarcode)
	{
		String xml = null;
		// String id = request.getParameter("id");
		// String chwId = request.getParameter("chwid").toUpperCase();

		/*
		 * String labId = request.getParameter("labId"); String labBarcode =
		 * request.getParameter("lbc");
		 */

		String patientId = "";
		String gxpOrdered = "Yes";
		String testid = labid + labbarcode;

		SputumResults sr;
		try
		{
			sr = ssl.findSputumResultsBySputumLabID (testid);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			return XmlUtil.createErrorXml ("Error finding test. Please try again!");
		}

		if (sr == null)
		{
			System.out.println ("null");
			return XmlUtil.createErrorXml ("Error finding test. Please try again!");
		}

/*		if (sr.getGeneXpertOrderDate () == null)
		{
			gxpOrdered = "No";
		}*/

		patientId = sr.getPatientId ();

		Person p = null;
		try
		{
			p = ssl.findPerson (patientId);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			return XmlUtil.createErrorXml ("Could not find Patient. Please try again");
		}

		if (p == null)
		{
			return XmlUtil.createErrorXml ("Could not find Patient. Please try again");
		}

		String firstName = p.getFirstName ();
		String lastName = p.getLastName ();

		Document doc = null;

		try
		{
			doc = DocumentBuilderFactory.newInstance ().newDocumentBuilder ().newDocument ();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace ();
			return "";
		}

		Element responseNode = doc.createElement (XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element pidNode = doc.createElement ("pid");
		Text pidValue = doc.createTextNode (patientId);
		pidNode.appendChild (pidValue);

		responseNode.appendChild (pidNode);

		Element fNameNode = doc.createElement ("fname");
		Text fNameValue = doc.createTextNode (firstName);
		fNameNode.appendChild (fNameValue);

		responseNode.appendChild (fNameNode);

		Element lNameNode = doc.createElement ("lname");
		Text lNameValue = doc.createTextNode (lastName);
		lNameNode.appendChild (lNameValue);

		responseNode.appendChild (lNameNode);

		Element gxpOrderedNode = doc.createElement ("gxp");
		Text gxpOrderedValue = doc.createTextNode (gxpOrdered);
		gxpOrderedNode.appendChild (gxpOrderedValue);

		responseNode.appendChild (gxpOrderedNode);

		doc.appendChild (responseNode);

		xml = XmlUtil.docToString (doc);

		return xml;
	}

	public String getSputumSearchInfo ()
	{
		String xml = null;
		String monitorId = request.getParameter ("mid");
		String date = request.getParameter ("date");

		date = DateTimeUtil.convertFromSlashFormatToSQL (date);

		System.out.println (monitorId);
		System.out.println (date);
		ssl = new ServerServiceImpl ();

		String[][] encounters = null;

		try
		{
			encounters = ssl.getTableData ("encounter", new String[] {"PID1", "EncounterID"}, " where PID2='" + monitorId.toUpperCase () + "' AND EncounterType='" + EncounterType.SPUTUM_BARCODE
					+ "' AND DateEncounterStart >= '" + date + " 00:00:01' AND DateEncounterStart <= '" + date + " 23:59:59'");
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}

		if (encounters == null)
		{
			return XmlUtil.createErrorXml ("Error while performing search. Please try again");
		}
		else if (encounters.length == 0)
		{
			return XmlUtil.createErrorXml ("No results found");
		}

		String data = "PatientID - Barcode - Month\n";
		String pid1 = "";
		String encId = "";
		EncounterResultsId encResultId = null;
		EncounterResults result = null;
		String bc = "";
		String month = "";

		for (int i = 0; i < encounters.length; i++)
		{
			pid1 = encounters[i][0];
			encId = encounters[i][1];
			// for barcode
			encResultId = new EncounterResultsId (Integer.parseInt (encId), pid1, monitorId.toUpperCase (), "BARCODE");

			try
			{
				result = ssl.findEncounterResultsByElement (encResultId);
			}
			catch (Exception e)
			{
				e.printStackTrace ();
				return XmlUtil.createErrorXml ("Error while performing search. Please try again");
			}

			if (result != null)
			{
				bc = result.getValue ();
				if (bc == null)
				{
					bc = "";
				}
			}
			// for Month
			encResultId = new EncounterResultsId (Integer.parseInt (encId), pid1, monitorId.toUpperCase (), "MONTH");
			result = null;
			try
			{
				result = ssl.findEncounterResultsByElement (encResultId);
			}
			catch (Exception e)
			{
				e.printStackTrace ();
				return XmlUtil.createErrorXml ("Error while performing search. Please try again");
			}

			if (result != null)
			{
				month = result.getValue ();
				if (month == null)
				{
					month = "";
				}
			}
			data += pid1 + "-" + bc + "-" + month + "\n";
		}

		Document doc = null;

		try
		{
			doc = DocumentBuilderFactory.newInstance ().newDocumentBuilder ().newDocument ();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace ();
			return "";
		}
		Element responseNode = doc.createElement (XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element dataNode = doc.createElement ("data");
		Text dataValue = doc.createTextNode (data);
		dataNode.appendChild (dataValue);

		responseNode.appendChild (dataNode);

		doc.appendChild (responseNode);

		xml = XmlUtil.docToString (doc);

		return xml;
	}

	/*
	 * public String getSuspectConfInfo(String id) { String xml = null;
	 * 
	 * ssl = new ServerServiceImpl(); String[] rowData = null;
	 * 
	 * try { rowData = ssl.getColumnData("Person",
	 * "CONCAT(FirstName, '|',LastName)", "where pid='" + id + "'");
	 * 
	 * rowData = ssl.getRowRecord("Person", new String[] {"firstName",
	 * "lastName"}, " where pid = '" + id + "'"); }
	 * 
	 * catch (Exception e) { e.printStackTrace(); return
	 * XmlUtil.createErrorXml("Error! Please try again"); }
	 * 
	 * if (rowData.length==0) { return XmlUtil.createErrorXml("Person with id "
	 * + id + " does not exist. Please recheck id and try again."); }
	 * 
	 * String[] data = rowData[0].split("\\|"); System.out.println(data[0]);
	 * System.out.println(data[1]);
	 * 
	 * Document doc = null;
	 * 
	 * try { doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
	 * .newDocument(); } catch (ParserConfigurationException e) {
	 * e.printStackTrace(); return ""; } Element responseNode =
	 * doc.createElement(XmlStrings.RESPONSE); //
	 * responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);
	 * 
	 * Element fNameNode = doc.createElement("fname"); Text fNameValue =
	 * doc.createTextNode(data[0]); fNameNode.appendChild(fNameValue);
	 * 
	 * responseNode.appendChild(fNameNode);
	 * 
	 * Element lNameNode = doc.createElement("lname"); Text lNameValue =
	 * doc.createTextNode(data[1]); lNameNode.appendChild(lNameValue);
	 * 
	 * responseNode.appendChild(lNameNode);
	 * 
	 * doc.appendChild(responseNode);
	 * 
	 * xml = XmlUtil.docToString(doc);
	 * 
	 * return xml; }
	 */

	private String getXrayResultsInfo (String labid, String labbarcode)
	{
		String patientId = "";
		XrayResults xr = null;
		try
		{
			xr = ssl.findXrayResultsByXRayTestID (labid + labbarcode);
		}
		catch (Exception e1)
		{
			e1.printStackTrace ();
		}

		if (xr == null)
		{
			return XmlUtil.createErrorXml ("Could not find XRay. Please try again");
		}

		patientId = xr.getPatientId ();

		Person p = null;
		try
		{
			p = ssl.findPerson (patientId);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}

		if (p == null)
		{
			return XmlUtil.createErrorXml ("Could not find Patient. Please try again.");
		}

		String firstName = p.getFirstName ();
		String lastName = p.getLastName ();
		Document doc = null;

		try
		{
			doc = DocumentBuilderFactory.newInstance ().newDocumentBuilder ().newDocument ();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace ();
			return "";
		}

		Element responseNode = doc.createElement (XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element pidNode = doc.createElement ("pid");
		Text pidValue = doc.createTextNode (patientId);
		pidNode.appendChild (pidValue);

		responseNode.appendChild (pidNode);

		Element fNameNode = doc.createElement ("fname");
		Text fNameValue = doc.createTextNode (firstName);
		fNameNode.appendChild (fNameValue);

		responseNode.appendChild (fNameNode);

		Element lNameNode = doc.createElement ("lname");
		Text lNameValue = doc.createTextNode (lastName);
		lNameNode.appendChild (lNameValue);

		responseNode.appendChild (lNameNode);

		doc.appendChild (responseNode);

		String xml = XmlUtil.docToString (doc);

		return xml;
	}

	public String getMissingSmearReport ()
	{
		String xml = null;

		String data = "";
		String labId = request.getParameter ("labId");

		String results[][] = null;

		results = ssl.getTableData ("sputumresults", new String[] {"PatientID", "SmearOrderDate"}, "where SmearOrderDate IS NOT NULL AND SmearResult IS NULL AND substring(PatientID,1,2)='" + labId
				+ "' ORDER BY SmearOrderDate ASC");

		if (results == null)
		{
			return XmlUtil.createErrorXml ("Error creating report");
		}

		/*
		 * if(results.length > 0 ){ data += "-"+String.format("%10s",
		 * "PatientID") + " , " + String.format("%11s", "SmearOrderDate") +
		 * "\n"; }
		 */
		for (int i = 0; i < results.length; i++)
		{
			data += "-" + String.format ("%10s", results[i][0]) + " , ";
			data += String.format ("%11s", results[i][1].substring (0, 10)) + "\n";
		}

		Document doc = null;

		try
		{
			doc = DocumentBuilderFactory.newInstance ().newDocumentBuilder ().newDocument ();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace ();
			return "";
		}
		Element responseNode = doc.createElement (XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element dataNode = doc.createElement ("data");
		Text dataValue = doc.createTextNode (data);
		dataNode.appendChild (dataValue);

		responseNode.appendChild (dataNode);

		doc.appendChild (responseNode);

		xml = XmlUtil.docToString (doc);

		return xml;
	}

	public String getMissingCXRReport ()
	{
		String xml = null;

		String data = "";

		String labId = request.getParameter ("labId");

		String results[][] = null;

		results = ssl.getTableData ("xrayresults", new String[] {"PatientID", "XRayDate"}, "where XRayDate IS NOT NULL AND XRayResult IS NULL AND substring(XRayLabID,1,2)='" + labId
				+ "' ORDER BY XRayDate ASC");

		if (results == null)
		{
			return XmlUtil.createErrorXml ("Error creating report");
		}
		/*
		 * if(results.length > 0 ){ data += "-"+String.format("%10s",
		 * "PatientID") + " , " + String.format("%11s", "XRayDate") + "\n"; }
		 */
		for (int i = 0; i < results.length; i++)
		{
			data += "-" + String.format ("%10s", results[i][0]) + " , ";
			data += String.format ("%11s", results[i][1].substring (0, 10)) + "\n";

		}

		Document doc = null;

		try
		{
			doc = DocumentBuilderFactory.newInstance ().newDocumentBuilder ().newDocument ();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace ();
			return "";
		}
		Element responseNode = doc.createElement (XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element dataNode = doc.createElement ("data");
		Text dataValue = doc.createTextNode (data);
		dataNode.appendChild (dataValue);

		responseNode.appendChild (dataNode);

		doc.appendChild (responseNode);

		xml = XmlUtil.docToString (doc);

		return xml;
	}

	public String getMissingXpertResultsReport ()
	{
		String xml = null;

		String data = "";

		String labId = request.getParameter ("labId");

		String results[][] = null;

		results = ssl.getTableData ("sputumresults", new String[] {"PatientID", "GeneXpertOrderDate"},
				"where GeneXpertOrderDate IS NOT NULL AND GeneXpertResult IS NULL AND substring(SputumLabID,1,2)='" + labId + "' ORDER BY GeneXpertOrderDate ASC");

		if (results == null)
		{
			return XmlUtil.createErrorXml ("Error creating report");
		}
		/*
		 * if(results.length > 0 ){ data += "-"+String.format("%10s",
		 * "PatientID") + " , " + String.format("%11s", "GeneXpertOrderDate") +
		 * "\n"; }
		 */
		for (int i = 0; i < results.length; i++)
		{
			data += "-" + String.format ("%10s", results[i][0]) + " , ";
			data += String.format ("%11s", results[i][1].substring (0, 10)) + "\n";

		}

		Document doc = null;

		try
		{
			doc = DocumentBuilderFactory.newInstance ().newDocumentBuilder ().newDocument ();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace ();
			return "";
		}
		Element responseNode = doc.createElement (XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element dataNode = doc.createElement ("data");
		Text dataValue = doc.createTextNode (data);
		dataNode.appendChild (dataValue);

		responseNode.appendChild (dataNode);

		doc.appendChild (responseNode);

		xml = XmlUtil.docToString (doc);

		return xml;
	}
	
	public String getMissingXpertOrderReport ()
	{
		String xml = null;

		String data = "";

		String labId = request.getParameter ("labId");

		String results[][] = null;

		results = ssl.getTableData ("SELECT sp.patientid, sp.smearorderdate ,substring(sp.sputumlabid,3) FROM sputumresults sp, xrayresults xr where sp.patientid=xr.patientid and sp.month = 0 and sp.smearresult = 'negative' and (xr.xrayresult like '%suggestive%' or xr.xrayresult like '%possib%tb%' )and sp.genexpertorderdate is null and substring(sp.sputumlabid,1,2)='"+labId+"' order by sp.smearorderdate");

		if (results == null)
		{
			return XmlUtil.createErrorXml ("Error creating report");
		}
		/*
		 * if(results.length > 0 ){ data += "-"+String.format("%10s",
		 * "PatientID") + " , " + String.format("%11s", "GeneXpertOrderDate") +
		 * "\n"; }
		 */
		for (int i = 0; i < results.length; i++)
		{
			data += "-" + String.format ("%10s", results[i][0]) + " , ";
			data += String.format ("%11s", results[i][1].substring (0, 10)) + "\n";

		}

		Document doc = null;

		try
		{
			doc = DocumentBuilderFactory.newInstance ().newDocumentBuilder ().newDocument ();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace ();
			return "";
		}
		Element responseNode = doc.createElement (XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element dataNode = doc.createElement ("data");
		Text dataValue = doc.createTextNode (data);
		dataNode.appendChild (dataValue);

		responseNode.appendChild (dataNode);

		doc.appendChild (responseNode);

		xml = XmlUtil.docToString (doc);

		return xml;
	}

	public String getMissingCXRRecvReport ()
	{
		String xml = null;

		String data = "";

		String labId = request.getParameter ("labId");

		String results[][] = null;

		results = ssl
				.getTableData ("patient", new String[] {"PatientID", "DateRegistered"},
						"where PatientID NOT IN (select PatientID from treatmentrefusal) AND PatientID NOT IN (select PatientID from xrayresults) AND LaboratoryID='" + labId
								+ "' ORDER BY DateRegistered ASC");

		if (results == null)
		{
			return XmlUtil.createErrorXml ("Error creating report");
		}
		/*
		 * if(results.length > 0 ){ data += "-"+String.format("%10s",
		 * "PatientID") + " , " + String.format("%11s", "DateRegistered") +
		 * "\n"; }
		 */
		for (int i = 0; i < results.length; i++)
		{
			data += "-" + String.format ("%10s", results[i][0]) + " , ";
			data += String.format ("%11s", results[i][1].substring (0, 10)) + "\n";

		}

		Document doc = null;

		try
		{
			doc = DocumentBuilderFactory.newInstance ().newDocumentBuilder ().newDocument ();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace ();
			return "";
		}
		Element responseNode = doc.createElement (XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element dataNode = doc.createElement ("data");
		Text dataValue = doc.createTextNode (data);
		dataNode.appendChild (dataValue);

		responseNode.appendChild (dataNode);

		doc.appendChild (responseNode);

		xml = XmlUtil.docToString (doc);

		return xml;
	}

	public String getTxNotStartedReport ()
	{
		String xml = null;

		String data = "";

		String labId = request.getParameter ("labId");

		String results[][] = null;

		results = ssl.getTableData ("patient", new String[] {"PatientID", "DateRegistered"},
				"where DiseaseConfirmed=true AND Regimen IS NULL AND PatientID NOT IN (select PatientID from treatmentrefusal)AND LaboratoryID='" + labId + "' ORDER BY DateRegistered ASC");

		if (results == null)
		{
			return XmlUtil.createErrorXml ("Error creating report");
		}
		/*
		 * if(results.length > 0 ){ data += "-"+String.format("%10s",
		 * "PatientID") + " , " + String.format("%11s", "DateRegistered") +
		 * "\n"; }
		 */
		for (int i = 0; i < results.length; i++)
		{
			data += "-" + String.format ("%10s", results[i][0]) + " , ";
			data += String.format ("%11s", results[i][1].substring (0, 10)) + "\n";

		}

		Document doc = null;

		try
		{
			doc = DocumentBuilderFactory.newInstance ().newDocumentBuilder ().newDocument ();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace ();
			return "";
		}
		Element responseNode = doc.createElement (XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element dataNode = doc.createElement ("data");
		Text dataValue = doc.createTextNode (data);
		dataNode.appendChild (dataValue);

		responseNode.appendChild (dataNode);

		doc.appendChild (responseNode);

		xml = XmlUtil.docToString (doc);

		return xml;
	}

	public String getPatientDrugsDueDateReport ()
	{
		String xml = null;

		String data = "";

		String labId = request.getParameter ("labId");

		String results[][] = null;

		results = ssl.getTableData ("select P.PatientID, datediff(date(D.DateDispensed), curdate()) + D.PillsQuotaDelivered as Difference from patient P " 
				+ "inner join drughistory D using (PatientID) "
				+ "where P.LaboratoryID = '" + labId + "' and P.PatientID not in (select PID1 from encounter where EncounterType = 'END_FOL') "
				+ "and D.DispensalNo = (select max(DispensalNo) from drughistory where PatientID = P.PatientID) "
				+ "having Difference <= 5");

		if (results == null)
		{
			return XmlUtil.createErrorXml ("Error creating report");
		}
		/*
		 * if(results.length > 0 ){ data += "-"+String.format("%10s",
		 * "PatientID") + " , " + String.format("%11s", "DateRegistered") +
		 * "\n"; }
		 */
		for (int i = 0; i < results.length; i++)
		{
			data += "-" + results[i][0] + ", ";
			data += results[i][1] + "\n";

		}

		Document doc = null;

		try
		{
			doc = DocumentBuilderFactory.newInstance ().newDocumentBuilder ().newDocument ();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace ();
			return "";
		}
		Element responseNode = doc.createElement (XmlStrings.RESPONSE);
		// responseNode.setAttribute(XmlStrings.TYPE, XmlStrings.LOGIN_TYPE);

		Element dataNode = doc.createElement ("data");
		Text dataValue = doc.createTextNode (data);
		dataNode.appendChild (dataValue);

		responseNode.appendChild (dataNode);

		doc.appendChild (responseNode);

		xml = XmlUtil.docToString (doc);

		return xml;
	}
	
	public String getPendingFollowup2Report ()
	{
		String xml = null;

		String data = "";

		String labId = request.getParameter ("labId");

		String results[][] = null;

		results = ssl.getTableData ("select distinct e.pid1, DATE_ADD(e.dateencounterentered, interval 60 day ) as datedue ,DATEDIFF(DATE_ADD(e.dateencounterentered, interval 60 day),CURDATE())as daysdue " +
				"from encounter e where e.pid1 in (select patientid from patient where diseasecategory = 'category 1' and patientstatus <> 'closed') " +
				"and e.pid1 in (select distinct patientid from sputumresults where substring(SputumLabID,1,2)='"+labId+"') " +
				"and e.pid1 not in (select distinct e.pid1 from encounter e, encounterresults er where er.encounterid = e.encounterid and er.pid1=e.pid1 and er.pid2=e.pid2 and e.encountertype = 'follow_up' and er.element = 'month' and er.value = '2') " +
				"and e.encountertype = 'baseline' order by daysdue");

		if (results == null)
		{
			return XmlUtil.createErrorXml ("Error creating report");
		}
		/*
		 * if(results.length > 0 ){ data += "-"+String.format("%10s",
		 * "PatientID") + " , " + String.format("%11s", "DateRegistered") +
		 * "\n"; }
		 */
		for (int i = 0; i < results.length; i++)
		{
			data += "-" + results[i][0] + ", ";
			data += results[i][1].substring (0, 10) + ", ";
			data += results[i][2] + "\n";

		}

		Document doc = null;

		try
		{
			doc = DocumentBuilderFactory.newInstance ().newDocumentBuilder ().newDocument ();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace ();
			return "";
		}
		Element responseNode = doc.createElement (XmlStrings.RESPONSE);

		Element dataNode = doc.createElement ("data");
		Text dataValue = doc.createTextNode (data);
		dataNode.appendChild (dataValue);

		responseNode.appendChild (dataNode);

		doc.appendChild (responseNode);

		xml = XmlUtil.docToString (doc);

		return xml;
	}
	
	public String getPendingFollowup3Report ()
	{
		String xml = null;

		String data = "";

		String labId = request.getParameter ("labId");

		String results[][] = null;

		results = ssl.getTableData ("select e.pid1, DATE_ADD(e.dateencounterentered, interval 30 day ) as datedue ,DATEDIFF(DATE_ADD(e.dateencounterentered, interval 30 day),CURDATE())as daysdue from encounter e where e.pid1 in (select patientid from patient where diseasecategory = 'category 1' and patientstatus <> 'closed') and e.pid1 in (select patientid from sputumresults where month = 2 and smearresult is not null and smearresult <> 'negative' and  substring(SputumLabID,1,2)='"+labId+"') and e.pid1 not in (select distinct e.pid1 from encounter e, encounterresults er where er.encounterid = e.encounterid and er.pid1=e.pid1 and er.pid2=e.pid2 and e.encountertype = 'follow_up' and er.element = 'month' and er.value like '%3%') and e.encountertype = 'follow_up' order by daysdue");

		if (results == null)
		{
			return XmlUtil.createErrorXml ("Error creating report");
		}
		/*
		 * if(results.length > 0 ){ data += "-"+String.format("%10s",
		 * "PatientID") + " , " + String.format("%11s", "DateRegistered") +
		 * "\n"; }
		 */
		for (int i = 0; i < results.length; i++)
		{
			data += "-" + results[i][0] + ", ";
			data += results[i][1].substring (0, 10) + ", ";
			data += results[i][2] + "\n";

		}

		String results2[][] = null;

		results2 = ssl.getTableData ("select distinct e.pid1, DATE_ADD(e.dateencounterentered, interval 90 day ) as datedue ,DATEDIFF(DATE_ADD(e.dateencounterentered, interval 90 day),CURDATE())as daysdue " +
				"from encounter e where e.pid1 in (select patientid from patient where diseasecategory = 'category 2' and patientstatus <> 'closed') " +
				"and e.pid1 in (select distinct patientid from sputumresults where substring(SputumLabID,1,2)='"+labId+"') " +
				"and e.pid1 not in (select distinct e.pid1 from encounter e, encounterresults er where er.encounterid = e.encounterid and er.pid1=e.pid1 and er.pid2=e.pid2 and e.encountertype = 'follow_up' and er.element = 'month' and er.value = '3') " +
				"and e.encountertype = 'baseline' order by daysdue");

		if (results2 == null)
		{
			return XmlUtil.createErrorXml ("Error creating report");
		}
		/*
		 * if(results.length > 0 ){ data += "-"+String.format("%10s",
		 * "PatientID") + " , " + String.format("%11s", "DateRegistered") +
		 * "\n"; }
		 */
		for (int i = 0; i < results2.length; i++)
		{
			data += "-" + results2[i][0] + ", ";
			data += results2[i][1].substring (0, 10) + ", ";
			data += results2[i][2] + "\n";

		}
		Document doc = null;

		try
		{
			doc = DocumentBuilderFactory.newInstance ().newDocumentBuilder ().newDocument ();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace ();
			return "Errrrrrrorr";
		}
		Element responseNode = doc.createElement (XmlStrings.RESPONSE);

		Element dataNode = doc.createElement ("data");
		Text dataValue = doc.createTextNode (data);
		dataNode.appendChild (dataValue);

		responseNode.appendChild (dataNode);

		doc.appendChild (responseNode);

		xml = XmlUtil.docToString (doc);

		return xml;
	}
	
	public String getMissingGPSLocationReport ()
	{
		String xml = null;

		String data = "";

		//String labId = request.getParameter ("labId");

		String results[][] = null;

		results = ssl.getTableData ("SELECT c.pid,p.dateregistered,p.LaboratoryId FROM contact c, patient p where c.pid = p.patientid and p.diseaseconfirmed = true and p.patientstatus <> 'closed' and  (c.locationlat is null || c.locationlon is null) and c.pid not in (select distinct er.pid1 from encounter e, encounterresults er where er.encounterid = e.encounterid and er.pid1=e.pid1 and er.pid2=e.pid2 and e.encountertype = 'refusal' and er.element = 'what_refused' and er.value = 'mapping') order by p.dateregistered");

		if (results == null)
		{
			return XmlUtil.createErrorXml ("Error creating report");
		}
		/*
		 * if(results.length > 0 ){ data += "-"+String.format("%10s",
		 * "PatientID") + " , " + String.format("%11s", "DateRegistered") +
		 * "\n"; }
		 */
		for (int i = 0; i < results.length; i++)
		{
			data += "-" + results[i][0] + ", ";
			data += results[i][1].substring (0, 10) + ", ";
			data += results[i][2] + "\n";

		}

		Document doc = null;

		try
		{
			doc = DocumentBuilderFactory.newInstance ().newDocumentBuilder ().newDocument ();
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace ();
			return "";
		}
		Element responseNode = doc.createElement (XmlStrings.RESPONSE);

		Element dataNode = doc.createElement ("data");
		Text dataValue = doc.createTextNode (data);
		dataNode.appendChild (dataValue);

		responseNode.appendChild (dataNode);

		doc.appendChild (responseNode);

		xml = XmlUtil.docToString (doc);

		return xml;
	}
	
	private String doRemoteASTMResult() {
		String xml = null;
		boolean insert = false;
		boolean update = false;
		/*
		 * MTB DETECTED (HIGH|LOW|MEDIUM|VERY LOW); RIF Resistance (DETECTED|NOT DETECTED|INDETERMINATE)
MTB NOT DETECTED
NO RESULT
ERROR
INVALID
		 */
		String patientId = request.getParameter("pid");
		//String[] splitting = patientIdOriginal.split("-");
		//String patientId = splitting[0];
		
		
		
		String sampleId = request.getParameter("sampleid");
		String mtb = request.getParameter("mtb");
			
		String rif = request.getParameter("rif");
		if(rif!=null && rif.equalsIgnoreCase("null"))
			rif = null;
		
		String rFinal = request.getParameter("final");
		String rPending = request.getParameter("pending");
		String rError = request.getParameter("error");
		String rCorrected = request.getParameter("correction");
		String resultDate = request.getParameter("enddate");
		String errorCode = request.getParameter("errorcode");
		
		String operatorId = request.getParameter("operatorid");//=" + operatorId;// e.g Karachi Xray
		String pcId = request.getParameter("pcid");
		String instrumentSerial = request.getParameter("instserial");
		String moduleId = request.getParameter("moduleid");
		String cartridgeId = request.getParameter("cartrigeid");
		String reagentLotId = request.getParameter("reagentlotid");
		String expDate = request.getParameter("expdate");
		System.out.println("------>" + operatorId);
		String probeResultA = request.getParameter("probea");
		String probeResultB = request.getParameter("probeb");
		String probeResultC = request.getParameter("probec");
		String probeResultD = request.getParameter("probed");
		String probeResultE = request.getParameter("probee");
		String probeResultSPC = request.getParameter("probespc");
		
		String probeCtA = request.getParameter("probeact");
		String probeCtB = request.getParameter("probebct");
		String probeCtC = request.getParameter("probecct");
		String probeCtD = request.getParameter("probedct");
		String probeCtE = request.getParameter("probeect");
		String probeCtSPC = request.getParameter("probespcct");
		
		String probeEndptA = request.getParameter("probeaendpt");
		String probeEndptB = request.getParameter("probebendpt");
		String probeEndptC = request.getParameter("probecendpt");
		String probeEndptD = request.getParameter("probedendpt");
		String probeEndptE = request.getParameter("probeeendpt");
		String probeEndptSPC = request.getParameter("probespcendpt");
		Date resultDateObj = null;
		if(resultDate!=null) {
			System.out.println("handling time");
			String year = resultDate.substring(0,4);
			String month = resultDate.substring(4,6);
			String date = resultDate.substring(6,8);
			String hour = null;
			String minute = null;
			String second = null;
		
			if(resultDate.length()==14) {
				hour = resultDate.substring(8,10);
				minute = resultDate.substring(10,12);
				second = resultDate.substring(12,14);
				
			}
			
			GregorianCalendar cal = new GregorianCalendar();
			cal.set(Calendar.YEAR, Integer.parseInt(year));
			cal.set(Calendar.MONTH, Integer.parseInt(month)-1);
			cal.set(Calendar.DATE, Integer.parseInt(date));
			if(hour!=null)
				cal.set(Calendar.HOUR, Integer.parseInt(hour));
			else
				cal.set(Calendar.HOUR,0);
			if(minute!=null)
				cal.set(Calendar.MINUTE, Integer.parseInt(minute));
			else
				cal.set(Calendar.MINUTE,0);
			if(second!=null)
				cal.set(Calendar.SECOND, Integer.parseInt(second));
			cal.set(Calendar.SECOND,0);
			
			cal.set(Calendar.MILLISECOND,0);
			
			resultDateObj = cal.getTime();
			System.out.println("TIME" + resultDateObj.getTime());
			
			
			
		}
		
		//if(rPending!=null) {
			
			ssl = new ServerServiceImpl();
			GeneXpertResults gxpCart = null;
			GeneXpertResults gxpPatient = null;
			GeneXpertResults gxpNew = null;
			
//			GeneXpertResultsAuto gxp = null;
			System.out.println(patientId + "  " + sampleId);
			//if(sampleId!=null && patientId!=null && sampleId.length()!=0 && patientId.length()!=0) {
				try {
					//gxp = ssl.findGeneXpertResults(sampleId,patientId);//remove this check and find by catridge id
					
					
					gxpCart = ssl.findGeneXpertResultsByCatridge(cartridgeId);
					
					
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();  
				}
				try{
					gxpPatient = ssl.findGeneXpertResultsByPatient(patientId);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			//}
			
			//catridge and patient don't exist
			if(gxpCart==null && gxpPatient==null) 
			{
				GeneXpertResults gxpU = createGeneXpertResults(patientId,sampleId, mtb, rif, resultDate,instrumentSerial,moduleId,cartridgeId,reagentLotId, expDate,operatorId,pcId,probeResultA,probeResultB,probeResultC,probeResultD,probeResultE,probeResultSPC,probeCtA,probeCtB,probeCtC,probeCtD,probeCtE,probeCtSPC,probeEndptA,probeEndptB,probeEndptC,probeEndptD,probeEndptE,probeEndptSPC,errorCode);
				try {
					//ssl.updateGeneXpertResultsAuto(gxp, gxp.getIsPositive(),operatorId,pcId,instrumentSerial,moduleId,cartridgeId,reagentLotId);
					ssl.saveGeneXpertResults(gxpU);//, gxp.getIsPositive(),operatorId,pcId,instrumentSerial,moduleId,cartridgeId,reagentLotId);
					return XmlUtil.createSuccessXml();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return XmlUtil.createErrorXml("Could not save data for Sample ID " + gxpU.getSputumTestId());
				}
			}
			if(gxpPatient!=null && gxpCart==null){//catridge id does not exist but patient does, add a new record that means it is the second result of an existing patient
				//System.out.println("NOT FOUND");
				//System.out.println(gxpPatient.getPatientId()+" "+gxpCart.getCartridgeId());
				GeneXpertResults gxpU = createGeneXpertResults(patientId,sampleId, mtb, rif, resultDate,instrumentSerial,moduleId,cartridgeId,reagentLotId, expDate,operatorId,pcId,probeResultA,probeResultB,probeResultC,probeResultD,probeResultE,probeResultSPC,probeCtA,probeCtB,probeCtC,probeCtD,probeCtE,probeCtSPC,probeEndptA,probeEndptB,probeEndptC,probeEndptD,probeEndptE,probeEndptSPC,errorCode);
				try {
					//ssl.updateGeneXpertResultsAuto(gxp, gxp.getIsPositive(),operatorId,pcId,instrumentSerial,moduleId,cartridgeId,reagentLotId);
					ssl.saveGeneXpertResults(gxpU);//, gxp.getIsPositive(),operatorId,pcId,instrumentSerial,moduleId,cartridgeId,reagentLotId);
					return XmlUtil.createSuccessXml();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return XmlUtil.createErrorXml("Could not save data for Sample ID " + gxpU.getSputumTestId());
				}
				
			}

			
			//if {
				//System.out.println(gxp.length);
				if(gxpCart!=null && gxpPatient!=null) {
				System.out.println(gxpCart.getCartridgeId()+" "+gxpPatient.getPatientId());
			//	for(int i=0;i<gxp.length;i++) {
					
					//System.out.println("STORED TIME:" + gxp[i].getDateTested().getTime());
				//System.out.println("STORED TIME:" + gxp.getDateTested().getTime());
						//if(resultDateObj.getTime()== gxp[i].getDateTested().getTime()){
				      //  if(resultDateObj.getTime()== gxp.getDateTested().getTime()){
				        	//if(cartridgeId.equals(gxp.getCartridgeId()) && patientId.equals(gxp.getPatientId())){	
								//gxpNew = gxp[i];
								gxpNew = gxpPatient;
							//	update = true;
								//System.out.println("date match");
								//break;
						//}
								if(gxpPatient.getPatientId().equals(gxpCart.getPatientId()))
								{
									gxpNew.setSputumTestId(sampleId);
				
				if(mtb != null) {
					//System.out.println("----MTB----" + mtb);
					int index = mtb.indexOf("MTB DETECTED");
					String mtbBurden = null;
					if(index!=-1) {
						mtbBurden = mtb.substring(index+"MTB DETECTED".length()+1);
					
						gxpNew.setGeneXpertResult("MTB DETECTED");
						gxpNew.setIsPositive(new Boolean(true));
						gxpNew.setMtbBurden(mtbBurden);
						gxpNew.setErrorCode(0);
					}
				
					else {
						index = mtb.indexOf("MTB NOT DETECTED");
						//System.out.println("mtb :" + index + " " + mtb);
						if(index!=-1) {
							gxpNew.setGeneXpertResult("MTB NOT DETECTED");
							gxpNew.setIsPositive(new Boolean(false));
							mtbBurden = null;
					}
					
					else {
						gxpNew.setGeneXpertResult(mtb);
						mtbBurden = null;
					}
				}
			}
			
			if(rif != null) {
				int index = rif.indexOf("NOT DETECTED");
				String rifResult = null;
				if(index!=-1) {
					rifResult = "NOT DETECTED";
				}
				
				else if(rif.indexOf("DETECTED")!=-1){
					rifResult = "DETECTED";
				}
				
				else {
					rifResult = rif.toUpperCase();
				}
				
				gxpNew.setDrugResistance(rifResult);
			}
			
			
			gxpNew.setDateTested(resultDateObj);
			gxpNew.setInstrumentSerial(instrumentSerial);
			gxpNew.setModuleId(moduleId);
			gxpNew.setReagentLotId(reagentLotId);
			gxpNew.setCartridgeId(cartridgeId);
			gxpNew.setPcId(pcId);
			gxpNew.setOperatorId(operatorId);
			if(errorCode!=null)
				gxpNew.setErrorCode(Integer.parseInt(errorCode));
			//Probes
			gxpNew.setProbeResultA(probeResultA);
			gxpNew.setProbeResultB(probeResultB);
			gxpNew.setProbeResultC(probeResultC);
			gxpNew.setProbeResultD(probeResultD);
			gxpNew.setProbeResultE(probeResultE);
			gxpNew.setProbeResultSPC(probeResultSPC);
			
			if(probeCtA!=null)
				gxpNew.setProbeCtA(Double.parseDouble(probeCtA));
			if(probeCtB!=null)
				gxpNew.setProbeCtB(Double.parseDouble(probeCtB));
			if(probeCtC!=null)
				gxpNew.setProbeCtC(Double.parseDouble(probeCtC));
			if(probeCtD!=null)
				gxpNew.setProbeCtD(Double.parseDouble(probeCtD));
			if(probeCtE!=null)
				gxpNew.setProbeCtE(Double.parseDouble(probeCtE));
			if(probeCtSPC!=null)
				gxpNew.setProbeCtSPC(Double.parseDouble(probeCtSPC));
			
			if(probeEndptA!=null)
				gxpNew.setProbeEndptA(Double.parseDouble(probeEndptA));
			if(probeEndptB!=null)
				gxpNew.setProbeEndptB(Double.parseDouble(probeEndptB));
			if(probeEndptC!=null)
				gxpNew.setProbeEndptC(Double.parseDouble(probeEndptC));
			if(probeEndptD!=null)
				gxpNew.setProbeEndptD(Double.parseDouble(probeEndptD));
			if(probeEndptE!=null)
				gxpNew.setProbeEndptE(Double.parseDouble(probeEndptE));
			if(probeEndptSPC!=null)
				gxpNew.setProbeEndptSPC(Double.parseDouble(probeEndptSPC));
			
			 
			try {
				//ssl.updateGeneXpertResultsAuto(gxp, gxp.getIsPositive(),operatorId,pcId,instrumentSerial,moduleId,cartridgeId,reagentLotId);
				ssl.updateGeneXpertResults(gxpNew);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
								}
						
					}
				
				
				if(gxpCart!=null && gxpPatient==null && gxpCart.getPatientId().equals(""))
				{
					gxpNew = gxpCart;
					
					if(gxpCart.getCartridgeId().equals(cartridgeId))
					{
						gxpNew.setSputumTestId(sampleId);
	
	if(mtb != null) {
		//System.out.println("----MTB----" + mtb);
		int index = mtb.indexOf("MTB DETECTED");
		String mtbBurden = null;
		gxpNew.setPatientId(patientId);
		if(index!=-1) {
			mtbBurden = mtb.substring(index+"MTB DETECTED".length()+1);
			
			gxpNew.setGeneXpertResult("MTB DETECTED");
			gxpNew.setIsPositive(new Boolean(true));
			gxpNew.setMtbBurden(mtbBurden);
			gxpNew.setErrorCode(0);
		}
	
		else {
			index = mtb.indexOf("MTB NOT DETECTED");
			//System.out.println("mtb :" + index + " " + mtb);
			if(index!=-1) {
				gxpNew.setGeneXpertResult("MTB NOT DETECTED");
				gxpNew.setIsPositive(new Boolean(false));
				mtbBurden = null;
		}
		
		else {
			gxpNew.setGeneXpertResult(mtb);
			mtbBurden = null;
		}
	}
}

if(rif != null) {
	int index = rif.indexOf("NOT DETECTED");
	String rifResult = null;
	if(index!=-1) {
		rifResult = "NOT DETECTED";
	}
	
	else if(rif.indexOf("DETECTED")!=-1){
		rifResult = "DETECTED";
	}
	
	else {
		rifResult = rif.toUpperCase();
	}
	
	gxpNew.setDrugResistance(rifResult);
}


gxpNew.setDateTested(resultDateObj);
gxpNew.setInstrumentSerial(instrumentSerial);
gxpNew.setModuleId(moduleId);
gxpNew.setReagentLotId(reagentLotId);
gxpNew.setCartridgeId(cartridgeId);
gxpNew.setPcId(pcId);
gxpNew.setOperatorId(operatorId);
if(errorCode!=null)
	gxpNew.setErrorCode(Integer.parseInt(errorCode));
//Probes
gxpNew.setProbeResultA(probeResultA);
gxpNew.setProbeResultB(probeResultB);
gxpNew.setProbeResultC(probeResultC);
gxpNew.setProbeResultD(probeResultD);
gxpNew.setProbeResultE(probeResultE);
gxpNew.setProbeResultSPC(probeResultSPC);

if(probeCtA!=null)
	gxpNew.setProbeCtA(Double.parseDouble(probeCtA));
if(probeCtB!=null)
	gxpNew.setProbeCtB(Double.parseDouble(probeCtB));
if(probeCtC!=null)
	gxpNew.setProbeCtC(Double.parseDouble(probeCtC));
if(probeCtD!=null)
	gxpNew.setProbeCtD(Double.parseDouble(probeCtD));
if(probeCtE!=null)
	gxpNew.setProbeCtE(Double.parseDouble(probeCtE));
if(probeCtSPC!=null)
	gxpNew.setProbeCtSPC(Double.parseDouble(probeCtSPC));

if(probeEndptA!=null)
	gxpNew.setProbeEndptA(Double.parseDouble(probeEndptA));
if(probeEndptB!=null)
	gxpNew.setProbeEndptB(Double.parseDouble(probeEndptB));
if(probeEndptC!=null)
	gxpNew.setProbeEndptC(Double.parseDouble(probeEndptC));
if(probeEndptD!=null)
	gxpNew.setProbeEndptD(Double.parseDouble(probeEndptD));
if(probeEndptE!=null)
	gxpNew.setProbeEndptE(Double.parseDouble(probeEndptE));
if(probeEndptSPC!=null)
	gxpNew.setProbeEndptSPC(Double.parseDouble(probeEndptSPC));

 
try {
	//ssl.updateGeneXpertResultsAuto(gxp, gxp.getIsPositive(),operatorId,pcId,instrumentSerial,moduleId,cartridgeId,reagentLotId);
	ssl.updateGeneXpertResults(gxpNew);
} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
					}
					

				}
			//	}
		
/*				if(!update) {
				//	for(int i=0;i<gxp.length;i++) {
						//if(gxp[i].getGeneXpertResult()==null){//F form filled
						if(gxp.getGeneXpertResult()==null){
							update = true;
							gxpNew = gxp;
							System.out.println("ID match null result");
							//break;
						
						}
				//	}
				}
				
		//	}
			

			//set mtb
			if(update==true) {

		}*/
			
	
			
		
		xml = XmlUtil.createSuccessXml();
		return xml;
	}
	
	public GeneXpertResults createGeneXpertResults(String patientId, String SampleID, String mtb, String rif, String resultDate,String instrumentSerial,String moduleId,String cartridgeId,String reagentLotId,String expDate,String operatorId,String pcId,String probeResultA,String probeResultB,String probeResultC,String probeResultD,String probeResultE,String probeResultSPC,String probeCtA,String probeCtB,String probeCtC,String probeCtD,String probeCtE,String probeCtSPC,String probeEndptA,String probeEndptB,String probeEndptC,String probeEndptD,String probeEndptE,String probeEndptSPC,String errorCode) {
		GeneXpertResults gxp = new GeneXpertResults();
		gxp.setSputumTestId(SampleID);
		gxp.setPatientId(patientId);
		
		if(rif!=null && rif.equalsIgnoreCase("null"))
			rif = null;
		
		if(mtb != null) {
			//System.out.println("----MTB----" + mtb);
			int index = mtb.indexOf("MTB DETECTED");
			String mtbBurden = null;
			if(index!=-1) {
				mtbBurden = mtb.substring(index+"MTB DETECTED".length()+1);
				
				gxp.setGeneXpertResult("MTB DETECTED");
				gxp.setIsPositive(new Boolean(true));
				gxp.setMtbBurden(mtbBurden);
				gxp.setErrorCode(0);
			}
			
			else {
				index = mtb.indexOf("MTB NOT DETECTED");
				//System.out.println("mtb :" + index + " " + mtb);
				if(index!=-1) {
					gxp.setGeneXpertResult("MTB NOT DETECTED");
					gxp.setIsPositive(new Boolean(false));
					mtbBurden = null;
				}
				
				else {
					gxp.setGeneXpertResult(mtb);
					mtbBurden = null;
				}
			}
		}
		
		if(rif != null) {
			int index = rif.indexOf("NOT DETECTED");
			String rifResult = null;
			if(index!=-1) {
				rifResult = "NOT DETECTED";
			}
			
			else if(rif.indexOf("DETECTED")!=-1){
				rifResult = "DETECTED";
			}
			
			else {
				rifResult = rif.toUpperCase();
			}
			
			gxp.setDrugResistance(rifResult);
		}
		
		if(resultDate!=null) {
			String year = resultDate.substring(0,4);
			String month = resultDate.substring(4,6);
			String date = resultDate.substring(6,8);
			String hour = null;
			String minute = null;
			String second = null;
		
			if(resultDate.length()==14) {
				hour = resultDate.substring(8,10);
				minute = resultDate.substring(10,12);
				second = resultDate.substring(12,14);
				
			}
			
			GregorianCalendar cal = new GregorianCalendar();
			cal.set(Calendar.YEAR, Integer.parseInt(year));
			cal.set(Calendar.MONTH, Integer.parseInt(month)-1);
			cal.set(Calendar.DATE, Integer.parseInt(date));
			if(hour!=null)
				cal.set(Calendar.HOUR, Integer.parseInt(hour));
			else
				cal.set(Calendar.HOUR,0);
			if(minute!=null)
				cal.set(Calendar.MINUTE, Integer.parseInt(minute));
			cal.set(Calendar.MINUTE,0);
			if(second!=null)
				cal.set(Calendar.SECOND, Integer.parseInt(second));
			cal.set(Calendar.SECOND,0);
			cal.set(Calendar.MILLISECOND,0);
			gxp.setDateTested(cal.getTime());
			
		}
		
		gxp.setInstrumentSerial(instrumentSerial);
		gxp.setModuleId(moduleId);
		gxp.setReagentLotId(reagentLotId);
		gxp.setCartridgeId(cartridgeId);
		gxp.setPcId(pcId);
		gxp.setExpDate(expDate);
		gxp.setOperatorId(operatorId);
		if(errorCode!=null)
			gxp.setErrorCode(Integer.parseInt(errorCode));
		
		
		//Probes
		gxp.setProbeResultA(probeResultA);
		gxp.setProbeResultB(probeResultB);
		gxp.setProbeResultC(probeResultC);
		gxp.setProbeResultD(probeResultD);
		gxp.setProbeResultE(probeResultE);
		gxp.setProbeResultSPC(probeResultSPC);
		
		if(probeCtA!=null)
			gxp.setProbeCtA(Double.parseDouble(probeCtA));
		if(probeCtB!=null)
			gxp.setProbeCtB(Double.parseDouble(probeCtB));
		if(probeCtC!=null)
			gxp.setProbeCtC(Double.parseDouble(probeCtC));
		if(probeCtD!=null)
			gxp.setProbeCtD(Double.parseDouble(probeCtD));
		if(probeCtE!=null)
			gxp.setProbeCtE(Double.parseDouble(probeCtE));
		if(probeCtSPC!=null)
			gxp.setProbeCtSPC(Double.parseDouble(probeCtSPC));
		
		if(probeEndptA!=null)
			gxp.setProbeEndptA(Double.parseDouble(probeEndptA));
		if(probeEndptB!=null)
			gxp.setProbeEndptB(Double.parseDouble(probeEndptB));
		if(probeEndptC!=null)
			gxp.setProbeEndptC(Double.parseDouble(probeEndptC));
		if(probeEndptD!=null)
			gxp.setProbeEndptD(Double.parseDouble(probeEndptD));
		if(probeEndptE!=null)
			gxp.setProbeEndptE(Double.parseDouble(probeEndptE));
		if(probeEndptSPC!=null)
			gxp.setProbeEndptSPC(Double.parseDouble(probeEndptSPC));
		
		return gxp;
	}
}
