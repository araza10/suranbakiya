
package org.irdresearch.tbreach2.server;

import java.util.ArrayList;
import java.util.Date;

import org.irdresearch.tbreach2.server.HibernateUtil;
import org.irdresearch.tbreach2.server.SMSUtil;
import org.irdresearch.tbreach2.shared.model.GeneXpertResults;
import org.irdresearch.tbreach2.shared.TBR;
import org.irdresearch.tbreach2.shared.model.*;

/**
 * The server side implementation of the RPC service.
 * 
 * @author owais.hussain@irdresearch.org
 */
public class ServerServiceImpl
{

	private String arrangeFilter (String filter)
	{
		if (filter.trim ().equalsIgnoreCase (""))
			return "";
		return (filter.toUpperCase ().contains ("WHERE") ? "" : " where ") + filter;
	}

	/**
	 * Get full name (first name + middle name + last name + surname) of any
	 * Person
	 * 
	 * @param Person
	 *            ID as String
	 * @return full name as String
	 */

	public String getFullName (String pid)
	{
		if (pid.equals (""))
			return "";
		return HibernateUtil.util
				.selectObject ("select LTRIM(RTRIM(IFNULL(FirstName, '') + ' ' + IFNULL(MiddleName, '') + IFNULL(LastName, '') + IFNULL(Surname, ''))) from person where PID='" + pid + "'")
				.toString ().toUpperCase ();
	}

	/**
	 * Get Mobile phone number of any Person
	 * 
	 * @param Person
	 *            ID as String
	 * @return Mobile number as String
	 */

	public String getMobileNumber (String pid)
	{
		if (pid.equals (""))
			return "";
		return HibernateUtil.util.selectObject ("select Mobile from contact where PID='" + pid + "'").toString ();
	}
	
	public GeneXpertResults[] findGeneXpertResults(String sputumTestID, String patientID) throws Exception
	{
		return (GeneXpertResults[]) HibernateUtil.util.findObject("from GeneXpertResults where SputumTestID='" + sputumTestID + "' and PatientID='"+patientID+"'");
	}
	
	public GeneXpertResults findGeneXpertResultsByCatridge(String cID) throws Exception
	{
		return (GeneXpertResults)HibernateUtil.util.findObject("from GeneXpertResults where CartridgeID='"+cID+"'");
	}
	
	public GeneXpertResults findGeneXpertResultsByPatient(String patientID) throws Exception
	{
		return (GeneXpertResults)HibernateUtil.util.findObject("from GeneXpertResults where PatientID='"+patientID+"'");
	}
	
	public Boolean updateGeneXpertResults(GeneXpertResults geneXpertResults) throws Exception
	{
		Boolean result = HibernateUtil.util.update(geneXpertResults);
		/*if (isTBPositive != null)
			SMSUtil.util.sendAlertsOnGXPResults(geneXpertResults);*/
		return result;
	}
	
	public Boolean saveGeneXpertResults(GeneXpertResults geneXpertResults) throws Exception
	{
		return HibernateUtil.util.save(geneXpertResults);
	}
	

	/**
	 * Get Mobile phone number of any Person
	 * 
	 * @param Person
	 *            ID as String
	 * @return Mobile number as String
	 */

	public String getAge (String pid)
	{
		if (pid.equals (""))
			return "";
		return HibernateUtil.util.selectObject ("select year(curdate()) - year(DOB) from person where PID='" + pid + "'").toString ();
	}

	/**
	 * Save method for server-side code
	 * 
	 * @param obj
	 *            Hibernate object
	 * @return
	 */
	public Boolean save (Object obj)
	{
		return HibernateUtil.util.save (obj);
	}

	/**
	 * Update method for server-side code
	 * 
	 * @param obj
	 *            Hibernate object
	 * @return
	 */
	public Boolean update (Object obj)
	{
		return HibernateUtil.util.update (obj);
	}

	/**
	 * Delete method for server-side code
	 * 
	 * @param obj
	 *            Hibernate object
	 * @return
	 */
	public Boolean delete (Object obj)
	{
		return HibernateUtil.util.delete (obj);
	}

	/**
	 * Sends an SMS
	 * 
	 * @param sms
	 */

	public void sendSMSAlert (Sms sms)
	{
		if (!sms.getTargetNumber ().equals (""))
			HibernateUtil.util.save (sms);
	}

	/**
	 * User authentication: Checks whether user exists, then match his password
	 * 
	 * @return Boolean
	 */

	public Boolean authenticate (String userName, String password)
	{
		if (!UserAuthentication.userExsists (userName))
			return false;
		else if (!UserAuthentication.validatePassword (userName, password))
			return false;
		TBR.setCurrentUser (userName.toUpperCase ());
		return true;
	}

	/**
	 * Checks if a user exists in the database
	 * 
	 * @return Boolean
	 */

	public Boolean authenticateUser (String userName)
	{
		if (!UserAuthentication.userExsists (userName))
			return false;
		return true;
	}

	/**
	 * Verifies secret answer against stored secret question
	 * 
	 * @return Boolean
	 */

/*	public Boolean verifySecretAnswer (String userName, String secretAnswer)
	{
		if (!UserAuthentication.validateSecretAnswer (userName, secretAnswer))
			return false;
		return true;
	}*/

	/**
	 * Get number of records in a table, given appropriate filter
	 * 
	 * @return Long
	 */

	public Long count (String tableName, String filter) throws Exception
	{
		Object obj = HibernateUtil.util.selectObject ("select count(*) from " + tableName + " " + arrangeFilter (filter));
		return Long.parseLong (obj.toString ());
	}
	
	public Long locationCount(String tableName, String locID) throws Exception
	{
		Object obj = HibernateUtil.util.selectObject("select count(PatientID) from "+ tableName+" where substring(PatientID,1,2)='"+locID+"'");
		return Long.parseLong(obj.toString());
		
	}
	
	public Long countMaxPerLocation(String tableName, String locID) throws Exception
	{
		Object obj = HibernateUtil.util.selectObject("select max(substring(PatientID,3,6)) from "+ tableName+" where substring(PatientID,1,2)='"+locID+"'");
		return Long.parseLong(obj.toString());
	}
	
	public Long workingDays() throws Exception
	{
		Object obj = HibernateUtil.util.selectObject("select DATEDIFF(now(),'2013-07-31 23:59:59')");
		return Long.parseLong(obj.toString());
	}


	/**
	 * Checks existence of data by counting number of records in a table, given
	 * appropriate filter
	 * 
	 * @return Boolean
	 */

	public Boolean exists (String tableName, String filter) throws Exception
	{
		long count = count (tableName, filter);
		return count > 0;
	}

	/**
	 * Generate report on server side and return the path it was created to
	 * 
	 * @param Path
	 *            of report as String Report parameters as Parameter[] Report to
	 *            be exported in csv format as Boolean
	 * @return String
	 */

	/*
	 * public String generateReport (String fileName, Parameter[] params,
	 * boolean export) { return ReportUtil.generateReport (fileName, params,
	 * export); }
	 */

	public String[] getColumnData (String tableName, String columnName, String filter) throws Exception
	{
		Object[] data = HibernateUtil.util.selectObjects ("select distinct " + columnName + " from " + tableName + " " + arrangeFilter (filter));
		String[] columnData = new String[data.length];
		for (int i = 0; i < data.length; i++)
			columnData[i] = data[i].toString ();
		return columnData;
	}

	public String getCurrentUser ()
	{
		return TBR.getCurrentUser ();
	}

 	public String[][] getLists () throws Exception
	{
		String[][] lists = null;
		lists = getTableData ("definition", new String[] {"DefinitionType", "DefinitionKey"}, "");
		return lists;
	}

	public String getObject (String tableName, String columnName, String filter)
	{
		return HibernateUtil.util.selectObject ("select " + columnName + " from " + tableName + arrangeFilter (filter)).toString ();
	}

	/*
	 * public String[][] getReportsList () { return ReportUtil.getReportList ();
	 * }
	 */

	public String[] getRowRecord (String tableName, String[] columnNames, String filter)
	{
		return getTableData (tableName, columnNames, filter)[0];
	}

/*	public String getSecretQuestion (String userName)
	{
		Users user = (Users) HibernateUtil.util.findObject ("from Users where UserName = '" + userName + "'");
		return user.getSecretQuestion ();
	}*/

	public String[][] getTableData (String sqlQuery)
	{
		Object[][] data = HibernateUtil.util.selectData (sqlQuery);
		String[][] stringData = new String[data.length][];
		for (int i = 0; i < data.length; i++)
		{
			stringData[i] = new String[data[i].length];
			for (int j = 0; j < data[i].length; j++)
			{
				if (data[i][j] == null)
					data[i][j] = "";
				String str = data[i][j].toString ();
				stringData[i][j] = str;
			}
		}
		return stringData;
	}

	public String[][] getTableData (String tableName, String[] columnNames, String filter)
	{
		StringBuilder columnList = new StringBuilder ();
		for (String s : columnNames)
		{
			columnList.append (s);
			columnList.append (",");
		}
		columnList.deleteCharAt (columnList.length () - 1);
		
		String sql = "select " + columnList.toString () + " from " + tableName + " " + arrangeFilter (filter);
		
		Object[][] data = HibernateUtil.util.selectData (sql);
		String[][] stringData = new String[data.length][columnNames.length];
		for (int i = 0; i < data.length; i++)
		{
			for (int j = 0; j < columnNames.length; j++)
			{
				if(data[i][j]!=null) {
					stringData[i][j] = data[i][j].toString ();
				} else {
					stringData[i][j] = "";
				}
			}
		}
		return stringData;
	}
	
	public Object[][] getTableObject (String tableName, String[] columnNames, String filter)
	{
		StringBuilder columnList = new StringBuilder ();
		for (String s : columnNames)
		{
			columnList.append (s);
			columnList.append (",");
		}
		columnList.deleteCharAt (columnList.length () - 1);

		Object[][] data = HibernateUtil.util.selectData ("select " + columnList.toString () + " from " + tableName + " " + arrangeFilter (filter));

		return data;
	}

	public Boolean[] getUserRgihts (String userName, String menuName)
	{
		String role = HibernateUtil.util.selectObject ("select Role from users where UserName='" + userName + "'").toString ();
		if (role.equalsIgnoreCase ("ADMIN"))
		{
			Boolean[] rights = {true, true, true, true, true};
			return rights;
		}
		UserRights userRights = (UserRights) HibernateUtil.util.findObject ("from UserRights where Role='" + role + "' and MenuName='" + menuName + "'");
		Boolean[] rights = {userRights.isSearchAccess (), userRights.isInsertAccess (), userRights.isUpdateAccess (), userRights.isDeleteAccess (), userRights.isPrintAccess ()};
		return rights;
	}

	public void recordLogin (String userName)
	{
		Users user = (Users) HibernateUtil.util.findObject ("from Users where UserName = '" + userName + "'");
		HibernateUtil.util.recordLog (LogType.LOGIN, user);
	}

	public void recordLogout (String userName)
	{
		Users user = (Users) HibernateUtil.util.findObject ("from Users where UserName = '" + userName + "'");
		HibernateUtil.util.recordLog (LogType.LOGOUT, user);
	}

	public int execute (String query)
	{
		return HibernateUtil.util.runCommand (query);
	}

	public Boolean execute (String[] queries)
	{
		for (String s : queries)
		{
			boolean result = execute (s) >= 0;
			if (!result)
				return false;
		}
		return true;
	}

	public Boolean executeProcedure (String procedure) throws Exception
	{
		return HibernateUtil.util.runProcedure (procedure);
	}

	public Boolean deleteContact (Contact contact)
	{
		return delete (contact);
	}

	public Boolean deleteDrugHistory (DrugHistory drugHistory)
	{
		return delete (drugHistory);
	}

	public Boolean deleteEncounter (Encounter encounter)
	{
		return delete (encounter);
	}

	public Boolean deleteEncounterResults (EncounterResults encounterResults)
	{
		return delete (encounterResults);
	}

	public Boolean deleteEncounterResults (EncounterResultsId encounterResultsId)
	{
		boolean result = false;
		EncounterResults er = (EncounterResults) HibernateUtil.util.findObject ("from EncounterResults where EncounterId=" + encounterResultsId.getEncounterId () + " and PID1='"
				+ encounterResultsId.getPid1 () + "' and PID2='" + encounterResultsId.getPid2 () + "' and Element='" + encounterResultsId.getElement () + "'");
		result = deleteEncounterResults (er);
		return result;
	}

	public Boolean deleteEncounterWithResults (Encounter encounter)
	{
		boolean result = false;
		try
		{
			String[] elements = getColumnData ("EncounterResults", "Element", "EncounterID=" + encounter.getId ().getEncounterId () + " AND PID1='" + encounter.getId ().getPid1 () + "' AND PID2='"
					+ encounter.getId ().getPid2 () + "'");
			// Delete encounter results
			for (String s : elements)
			{
				EncounterResultsId id = new EncounterResultsId (encounter.getId ().getEncounterId (), encounter.getId ().getPid1 (), encounter.getId ().getPid2 (), s);
				result = deleteEncounterResults (id);
			}
			// Delete encounter
			result = deleteEncounter (encounter);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
		return result;
	}

	public Boolean deleteFeedback (Feedback feedback)
	{
		return delete (feedback);
	}

	public Boolean deleteGP (Gp gp, Person person, Contact contact, Users user)
	{
		return delete (user) && delete (gp) && delete (person) && delete (contact);
	}

	public Boolean deleteLabMapping (LabMapping labMapping)
	{
		return delete (labMapping);
	}

	public Boolean deleteLocation (Location location)
	{
		return delete (location);
	}

	public Boolean deletePatient (Patient patient)
	{
		return delete (patient);
	}

	public Boolean deletePerson (Person person)
	{
		return delete (person);
	}

	public Boolean deleteScreening (Screening screening)
	{
		return delete (screening);
	}

	public Boolean deleteSputumResults (SputumResults sputumResults)
	{
		return delete (sputumResults);
	}

	public Boolean deleteTreatmentRefusal (TreatmentRefusal treatmentRefusal)
	{
		return delete (treatmentRefusal);
	}

	public Boolean deleteUser (Users user)
	{
		return delete (user);
	}

	public Boolean deleteUserRights (UserRights userRights)
	{
		return delete (userRights);
	}

	public Boolean deleteWorker (Worker worker, Person person, Contact contact, Users user)
	{
		return delete (user) && delete (worker) && delete (person) && delete (contact);
	}

	public Boolean deleteXrayResults (XrayResults xrayResults)
	{
		return delete (xrayResults);
	}

	/* Find methods */

	public Contact findContact (String personID)
	{
		return (Contact) HibernateUtil.util.findObject ("from Contact where PID='" + personID + "'");
	}

	public DrugHistory findDrugHistory (String patientID)
	{
		return (DrugHistory) HibernateUtil.util.findObject ("from DrugHistory where PatientID='" + patientID + "'");
	}

	public Encounter findEncounter (EncounterId encounterID)
	{
		return (Encounter) HibernateUtil.util.findObject ("from Encounter where PID1='" + encounterID.getPid1 () + "' and PID2='" + encounterID.getPid2 () + "' and EncounterID='"
				+ encounterID.getEncounterId () + "'");
	}
	
	public Encounter findEncountersbyType (EncounterId encounterID)
	{
		return (Encounter) HibernateUtil.util.findObject ("from Encounter where PID1='" + encounterID.getPid1 () + "' and EncounterType='P_INFO'");
	}

	public EncounterResults[] findEncounterResults (EncounterResultsId encounterResultsID)
	{
		Object[] objs = (EncounterResults[]) HibernateUtil.util.findObjects ("from EncounterResults where PID1='" + encounterResultsID.getPid1 () + "' and PID2='" + encounterResultsID.getPid2 ()
				+ "' and EncounterID='" + encounterResultsID.getEncounterId () + "'");
		EncounterResults[] encounterResults = new EncounterResults[objs.length];
		for (int i = 0; i < objs.length; i++)
			encounterResults[i] = (EncounterResults) objs[i];
		return encounterResults;
	}

	public EncounterResults findEncounterResultsByElement (EncounterResultsId encounterResultsID)
	{
		return (EncounterResults) HibernateUtil.util.findObject ("from EncounterResults where PID1='" + encounterResultsID.getPid1 () + "' and PID2='" + encounterResultsID.getPid2 ()
				+ "' and EncounterID='" + encounterResultsID.getEncounterId () + "' and Element='" + encounterResultsID.getElement () + "'");
	}
	
	public EncounterResults findEncounterResultsByElements (EncounterResultsId encounterResultsID)
	{
		return (EncounterResults) HibernateUtil.util.findObject ("from EncounterResults where PID1='" + encounterResultsID.getPid1 () + "' and Element='" + encounterResultsID.getElement () + "'");
	}
	
																																

	public Gp findGP (String GPID)
	{
		return (Gp) HibernateUtil.util.findObject ("from Gp where GPID='" + GPID + "'");
	}

	public Location findLocation (String locationID)
	{
		return (Location) HibernateUtil.util.findObject ("from Location where LocationID='" + locationID + "'");
	}
	
	public Location findLocationIDByName (String locationName)
	{
		return (Location) HibernateUtil.util.findObject("from Location where LocationName='"+locationName+"'");
	}

	public LabMapping findMappingByPerson (String pid)
	{
		return (LabMapping) HibernateUtil.util.findObject ("from LabMapping where PID='" + pid + "'");
	}
	
	public LabMapping findMappingByPerson (String pid, String locationID)
	{
		return (LabMapping) HibernateUtil.util.findObject ("from LabMapping where PID='" + pid + "' and LocationID='"+locationID+"'");
	}

	public LabMapping[] findMappingByLocation (String locationID)
	{

		Object[] objs = HibernateUtil.util.findObjects ("from LabMapping where LocationID='" + locationID + "'");
		LabMapping[] labMapping = new LabMapping[objs.length];
		for (int i = 0; i < objs.length; i++)
			labMapping[i] = (LabMapping) objs[i];
		return labMapping;
	}

	public Patient findPatient (String patientID)
	{
		return (Patient) HibernateUtil.util.findObject ("from Patient where PatientID='" + patientID + "'");
	}

	public Person findPerson (String PID)
	{
		return (Person) HibernateUtil.util.findObject ("from Person where PID='" + PID + "'");
	}

	public Person[] findPersonsByName (String firstName, String lastName)
	{
		Object[] objs = HibernateUtil.util.findObjects ("from Person where FirstName LIKE '" + firstName + "%' and LastName LIKE '" + lastName + "%'");
		Person[] persons = new Person[objs.length];
		for (int i = 0; i < objs.length; i++)
			persons[i] = (Person) objs[i];
		return persons;
	}

	public Person findPersonsByNIC (String NIC)
	{
		return (Person) HibernateUtil.util.findObject ("from Person where NIC='" + NIC + "'");
	}

	public SputumResults findSputumResults (String patientID, String sputumLabID)
	{
		return (SputumResults) HibernateUtil.util.findObject ("from SputumResults where PatientID='" + patientID + "' and SputumLabID='" + sputumLabID + "'");
	}
	
	public SputumResults findSputumResults (String patientID)
	{
		return (SputumResults) HibernateUtil.util.findObject ("from SputumResults where PatientID='" + patientID + "'");
	}

	public SputumResults[] findSputumResultsByPatientID (String patientID)
	{
		Object[] objs = HibernateUtil.util.findObjects ("from SputumResults where PatientID='" + patientID + "'");

		SputumResults[] sputumResults = new SputumResults[objs.length];
		for (int i = 0; i < objs.length; i++)
			sputumResults[i] = (SputumResults) objs[i];
		return sputumResults;
	}

	public Screening findScreeningByPatientID (String patientID)
	{
		return (Screening) HibernateUtil.util.findObject ("from Screening where PatientID='" + patientID + "'");
	}
	
	public Screening findScreeningByScreeningID (String ScreeningID)
	{
		return (Screening) HibernateUtil.util.findObject ("from Screening where ScreeningID='" + ScreeningID + "'");
	}

	public SputumResults findSputumResultsBySputumID (Integer sputumID)
	{
		return (SputumResults) HibernateUtil.util.findObject ("from SputumResults where SputumID='" + sputumID + "'");
	}

	public SputumResults findSputumResultsBySputumLabID (String sputumLabID)
	{
		return (SputumResults) HibernateUtil.util.findObject ("from SputumResults where SputumLabID='" + sputumLabID + "'");
	}

	public TreatmentRefusal findTreatmentRefusal (String patientID)
	{
		return (TreatmentRefusal) HibernateUtil.util.findObject ("from TreatmentRefusal where PatientID='" + patientID + "'");
	}

	public Users findUser (String userName)
	{
		return (Users) HibernateUtil.util.findObject ("from Users where UserName='" + userName + "'");
	}

	public UserRights findUserRights (String roleName, String menuName)
	{
		return (UserRights) HibernateUtil.util.findObject ("from UserRights where Role='" + roleName + "' and MenuName='" + menuName + "'");
	}

	public Worker findWorker (String workerID)
	{
		return (Worker) HibernateUtil.util.findObject ("from Worker where WorkerID='" + workerID + "'");
	}

	public XrayResults[] findXrayResultsByPatient (String patientID)
	{
		Object[] objs = HibernateUtil.util.findObjects ("from XrayResults where PatientID='" + patientID + "'");

		XrayResults[] xrayResults = new XrayResults[objs.length];
		for (int i = 0; i < objs.length; i++)
			xrayResults[i] = (XrayResults) objs[i];
		return xrayResults;
	}

	public XrayResults findXrayResultsByXRayTestID (String xrayLabID)
	{
		return (XrayResults) HibernateUtil.util.findObject ("from XrayResults where XRayLabID='" + xrayLabID + "'");
	}

	/* Save methods */

	public Boolean saveContact (Contact contact)
	{
		return save (contact);
	}

	public Boolean saveDrugHistory (DrugHistory drugHistory)
	{
		DrugHistoryId id = drugHistory.getId ();
		try
		{
			Object obj = HibernateUtil.util.selectObject ("select max(DispensalNo) from drughistory where PatientID='" + id.getPatientId () + "'");
			id = new DrugHistoryId (id.getPatientId (), Integer.parseInt (obj.toString ()) + 1);
			drugHistory.setId (id);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
		return save (drugHistory);
	}

	public Boolean saveEncounter (Encounter encounter)
	{
		// Get the max encounter ID and add 1
		EncounterId currentID = encounter.getId ();
		Object[] max = HibernateUtil.util.selectObjects ("select max(encounterID) from encounter where pid1='" + currentID.getPid1 () + "' and pid2='" + currentID.getPid2 () + "'");

		Integer maxInt = (Integer) max[0];
		if (maxInt == null)
		{
			currentID.setEncounterId (1);
		}
		else
		{
			currentID.setEncounterId ((maxInt.intValue () + 1));
		}
		encounter.setId (currentID);
		return save (encounter);
	}

	public Boolean saveEncounterResults (EncounterResults encounterResults)
	{
		return save (encounterResults);
	}

	public Boolean saveEncounterWithResults (Encounter encounter, EncounterResults[] encounterResults)
	{
		boolean result = false;
		result = saveEncounter (encounter);
		Object[] maxObj = HibernateUtil.util.selectObjects ("select max(encounterID) from encounter where PID1='" + encounter.getId ().getPid1 () + "' and PID2='" + encounter.getId ().getPid2 ()
				+ "'");
		Integer max = (Integer) maxObj[0];
		EncounterId eId = encounter.getId ();
		EncounterResultsId erId = new EncounterResultsId (max, eId.getPid1 (), eId.getPid2 (), "");
		for (EncounterResults er : encounterResults)
		{
			erId = new EncounterResultsId (max, eId.getPid1 (), eId.getPid2 (), er.getId ().getElement ());
			er = new EncounterResults (erId, er.getValue ());
			er.setId (erId);
			result = saveEncounterResults (er);
		}
		return result;
	}

	public Boolean saveEncounterWithResults (Encounter encounter, ArrayList<String> encounterResults)
	{
		boolean result = false;
		// Save an encounter
		try
		{
			Long encounterID = HibernateUtil.util.count ("select IFNULL(max(EncounterID), 0) + 1 from Encounter where PID1='" + encounter.getId ().getPid1 () + "' and PID2='"
					+ encounter.getId ().getPid2 () + "' and EncounterType='" + encounter.getEncounterType () + "'");
			result = saveEncounter (encounter);

			for (String s : encounterResults)
			{
				String[] split = s.split ("=");
				EncounterResults encounterResult = new EncounterResults ();
				encounterResult.setId (new EncounterResultsId (encounterID.intValue (), encounter.getId ().getPid1 (), encounter.getId ().getPid2 (), split[0]));
				if (split.length == 2)
					encounterResult.setValue ((split[1]));
				else
					encounterResult.setValue ("");
				result = saveEncounterResults (encounterResult);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
		return result;
	}

	public Boolean saveFeedback (Feedback feedback)
	{
		// Send a text message to personal number if a Error/Bug is recorded
		if (feedback.getFeedbackType ().equalsIgnoreCase ("Error/Bug"))
		{
			try
			{
				sendSMSAlert (new Sms ("03453174270", feedback.toString (), new Date (), null, "PENDING", null, null));
			}
			catch (Exception e)
			{
				e.printStackTrace ();
			}
		}
		return save (feedback);
	}

	public Boolean saveGP (Gp gp, Person person, Contact contact, Users user)
	{
		boolean result = false;
		deleteGP (gp, person, contact, user);
	//	result = saveUser (user);
		result = save (gp);
		result = save (person);
		result = save (contact);
		return result;
	}

	public Boolean saveLabMapping (LabMapping labMapping)
	{
		return save (labMapping);
	}

	public Boolean saveLocation (Location location)
	{
		return save (location);
	}

	public Boolean saveNewPatient (Patient patient, Person person, Contact contact)
	{
		boolean result = false;
		result = save (patient);
		result = save (person);
		result = save (contact);
		if (!result) // In case of failure of any save, delete all 3
		{
			delete (patient);
			delete (person);
			delete (contact);
			return false;
		}
		return result;
	}

	public Boolean savePatient (Patient patient)
	{
		return save (patient);
	}

	public Boolean savePerson (Person person)
	{
		return save (person);
	}

	public Boolean saveScreening (Screening screening)
	{
		return save (screening);
	}

	public Boolean saveSputumResults (SputumResults sputumResults)
	{
		return save (sputumResults);
	}

	public Boolean saveTreatmentRefusal (TreatmentRefusal treatmentRefusal)
	{
		return save (treatmentRefusal);
	}

	public Boolean saveUser (Users user)
	{
		user.setPassword (MDHashUtil.getHashString (user.getPassword ()));
		//user.setSecretAnswer (MDHashUtil.getHashString (user.getSecretAnswer ()));
		return save (user);
	}

	public Boolean saveUserRights (UserRights userRights)
	{
		return save (userRights);
	}

	public Boolean saveWorker (Worker worker, Person person, Contact contact, Users user)
	{
		boolean result = false;
		deleteWorker (worker, person, contact, user);
		//result = saveUser (user);
		result = save (worker);
		result = save (person);
		result = save (contact);
		return result;
	}

	public Boolean saveXrayResults (XrayResults xrayResults)
	{
		return save (xrayResults);
	}

	/* Update methods */

	public Boolean updateContact (Contact contact)
	{
		return update (contact);
	}

	public Boolean updateDrugHistory (DrugHistory drugHistory)
	{
		return update (drugHistory);
	}

	public Boolean updateEncounter (Encounter encounter)
	{
		return update (encounter);
	}

	public Boolean updateEncounterResults (EncounterResults encounterResults)
	{
		return update (encounterResults);
	}

	public Boolean updateEncounterResults (EncounterResultsId encounterResultsId, String newValue)
	{
		EncounterResults encounterResults = (EncounterResults) HibernateUtil.util.findObject ("from EncounterResults where EncounterId=" + encounterResultsId.getEncounterId () + " and PID1='"
				+ encounterResultsId.getPid1 () + "' and PID2='" + encounterResultsId.getPid2 () + "' and Element='" + encounterResultsId.getElement () + "'");
		encounterResults.setValue (newValue);
		return update (encounterResults);
	}

	public Boolean updateFeedback (Feedback feedback)
	{
		return update (feedback);
	}

	public Boolean updateGP (Gp gp, Person person, Contact contact, Users user)
	{
		return update (user) && update (gp) && update (person) && update (contact);
	}

	public Boolean updateLocation (Location location)
	{
		return update (location);
	}

	public Boolean updatePassword (String userName, String newPassword)
	{
		Users user = (Users) HibernateUtil.util.findObject ("from Users where UserName = '" + userName + "'");
		user.setPassword (MDHashUtil.getHashString (newPassword));
		return update (user);
	}

	public Boolean updatePatient (Patient patient)
	{
		return update (patient);
	}

	public Boolean updatePerson (Person person)
	{
		return update (person);
	}

	public Boolean udpateScreening (Screening screening)
	{
		return update (screening);
	}

	public Boolean updateSputumResults (SputumResults sputumResults, Boolean isTBPositive)
	{
		Boolean result = update (sputumResults);
		return result;
	}

	public Boolean updateTreatmentRefusal (TreatmentRefusal treatmentRefusal)
	{
		return update (treatmentRefusal);
	}

	public Boolean updateUser (Users user)
	{
		user.setPassword (MDHashUtil.getHashString (user.getPassword ()));
	//	user.setSecretAnswer (MDHashUtil.getHashString (user.getSecretAnswer ()));
		return update (user);
	}

	public Boolean updateUserRights (UserRights userRights)
	{
		return update (userRights);
	}

	public Boolean updateWorker (Worker worker, Person person, Contact contact, Users user)
	{
		return update (user) && update (worker) && update (person) && update (contact);
	}

	public Boolean updateXrayResults (XrayResults xrayResults)
	{
		return update (xrayResults);
	}

}