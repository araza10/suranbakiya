
package org.irdresearch.tbreach2.mobilevent.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import org.irdresearch.tbreach2.mobileevent.DateTimeUtil;
import org.irdresearch.tbreach2.server.*;

public class DFRUpdater
{

	private BufferedReader	br;
	PrintWriter				out;

	String					findMaxEncounterID		= "select MAX(EncounterID) from Encounter where PID1=? AND PID2=?";
	String					insertEncounter			= "insert into Encounter (EncounterID,PID1,PID2,EncounterType,DateEncounterStart,DateEncounterEnd) VALUES (?,?,?,'DFR',?,?)";
	String					insertEncounterResult	= "insert into EncounterResults (EncounterID,PID1,PID2,Element,Value) VALUES (?,?,?,?,?)";
	String					dbUrl					= "jdbc:mysql://project2.irdresearch.org:3306/tbreach";
	String					dbClass					= "com.mysql.jdbc.Driver";

	PreparedStatement		pst1					= null;
	PreparedStatement		pst2					= null;
	PreparedStatement		pst3					= null;

	ResultSet				rs1						= null;
	Connection				conn					= null;

	public DFRUpdater ()
	{

	}

	public static void main (String args[])
	{
		DFRUpdater dfru = new DFRUpdater ();

		try
		{

			dfru.getFileConn ();
			dfru.getDBConn ();
			dfru.doUpdate ();
			dfru.closeDB ();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace ();
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace ();
		}
	}

	public void getDBConn ()
	{
		try
		{
			Class.forName (dbClass);
			conn = DriverManager.getConnection (dbUrl, "tbreachuser", "tbr1876");
		}
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace ();
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace ();
		}

	}

	public void closeDB ()
	{
		if (conn != null)
		{
			try
			{
				conn.close ();
			}
			catch (SQLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace ();
			}
		}
	}

	public void getFileConn () throws FileNotFoundException
	{
		File file = new File ("C:\\Users\\Ali\\Desktop\\DFR_yesterday.csv");
		InputStreamReader isr = new InputStreamReader (new FileInputStream (file));
		br = new BufferedReader (isr);

		File outfile = new File ("C:\\Users\\Ali\\Desktop\\sqlLog.txt");
		out = new PrintWriter (outfile);

	}

	public void doUpdate () throws IOException, ParseException
	{
		String line = "";
		String tokens[] = null;
		int lineCount = 0;
		/*
		 * Encounter e = null; EncounterId eid = null;
		 */
		int encounterID = -1;
		ServerServiceImpl ssi = new ServerServiceImpl ();
		String pid1 = null;
		String pid2 = null;
		String startDate = null;
		String endDate = null;
		String enteredDate = null;
		String location = null;
		String gpId = null;
		String locationDetail = null;
		String attempted = null;
		String screened = null;
		String missed = null;
		String refused = null;
		String suspects = null;
		br.readLine ();
		while ((line = br.readLine ()) != null)
		{
			encounterID = -1;
			tokens = line.split (",");

			if (tokens.length != 13)
			{
				out.println ("ERROR IN line: " + (lineCount + 1));
			}

			else
			{
				pid1 = tokens[0];
				pid2 = tokens[1];
				startDate = tokens[2];
				endDate = tokens[3];
				enteredDate = tokens[4];
				location = tokens[5];
				gpId = tokens[6];
				locationDetail = tokens[7];
				attempted = tokens[9];
				screened = tokens[9];
				missed = tokens[10];
				refused = tokens[11];
				suspects = tokens[12];

				try
				{
					pst1 = conn.prepareStatement (findMaxEncounterID);
					pst1.setString (1, pid1);
					pst1.setString (2, pid2);
					rs1 = pst1.executeQuery ();

					if (rs1.next ())
					{
						encounterID = rs1.getInt (1) + 1;
					}

					else
						encounterID = 1;

					pst2 = conn.prepareStatement (insertEncounter);
					pst2.setInt (1, encounterID);
					pst2.setString (2, pid1);
					pst2.setString (3, pid2);
					pst2.setDate (4, new java.sql.Date (DateTimeUtil.getDateFromString (startDate, DateTimeUtil.FE_FORMAT).getTime ()));
					pst2.setDate (5, new java.sql.Date (DateTimeUtil.getDateFromString (endDate, DateTimeUtil.FE_FORMAT).getTime ()));
					pst2.executeUpdate ();

					pst3 = conn.prepareStatement (insertEncounterResult);
					pst3.setInt (1, encounterID);
					pst3.setString (2, pid1);
					pst3.setString (3, pid2);
					pst3.setString (4, "ENTERED_DATE");
					pst3.setString (5, enteredDate);
					pst3.executeUpdate ();

					pst3.setString (4, "LOCATION");
					pst3.setString (5, location);
					pst3.executeUpdate ();

					pst3.setString (4, "LOCATION_DETAIL");
					pst3.setString (5, locationDetail);
					pst3.executeUpdate ();

					pst3.setString (4, "GP_ID");
					pst3.setString (5, gpId);
					pst3.executeUpdate ();

					pst3.setString (4, "ATTEMPTED");
					pst3.setString (5, attempted);
					pst3.executeUpdate ();

					pst3.setString (4, "SCREENED");
					pst3.setString (5, screened);
					pst3.executeUpdate ();

					pst3.setString (4, "MISSED");
					pst3.setString (5, missed);
					pst3.executeUpdate ();

					pst3.setString (4, "REFUSED");
					pst3.setString (5, refused);
					pst3.executeUpdate ();

					pst3.setString (4, "SUSPECTS");
					pst3.setString (5, suspects);
					pst3.executeUpdate ();

				}
				catch (SQLException e)
				{
					// TODO Auto-generated catch block
					out.println ("Could not save some results for line: " + (lineCount + 1) + "->" + line);
					e.printStackTrace ();
				}

				/*
				 * EncounterId encId = new EncounterId();
				 * encId.setPid1(pid1.toUpperCase());
				 * encId.setPid2(pid2.toUpperCase());
				 * 
				 * e = new Encounter(); e.setId(encId);
				 * e.setEncounterType("DFR");
				 * e.setDateEncounterStart(DateTimeUtil
				 * .getDateFromString(startDate, DateTimeUtil.FE_FORMAT));
				 * e.setDateEncounterEnd(DateTimeUtil.getDateFromString(endDate,
				 * DateTimeUtil.FE_FORMAT));
				 * 
				 * try { boolean eCreated = ssi.saveEncounter(e); } catch
				 * (Exception e1) { // Auto-generated catch block
				 * e1.printStackTrace(); out.println("Could not save line: " +
				 * (lineCount+1) + "->" + line); continue; }
				 * 
				 * ArrayList<EncounterResults> encounters = new
				 * ArrayList<EncounterResults>();
				 * 
				 * EncounterResults dateResult =
				 * ModelUtil.createEncounterResult(e,
				 * "entered_date".toUpperCase(), enteredDate);
				 * encounters.add(dateResult);
				 * 
				 * EncounterResults locationResult =
				 * ModelUtil.createEncounterResult( e, "location".toUpperCase(),
				 * location.toUpperCase()); encounters.add(locationResult);
				 * 
				 * EncounterResults locationDetailResult = ModelUtil
				 * .createEncounterResult(e, "location_detail".toUpperCase(),
				 * locationDetail.toUpperCase());
				 * encounters.add(locationDetailResult);
				 * 
				 * EncounterResults gpIdResult = ModelUtil
				 * .createEncounterResult(e, "gp_id".toUpperCase(),
				 * gpId.toUpperCase()); encounters.add(gpIdResult);
				 * 
				 * EncounterResults attemptedResult = ModelUtil
				 * .createEncounterResult(e, "attempted".toUpperCase(),
				 * attempted.toUpperCase()); encounters.add(attemptedResult);
				 * 
				 * EncounterResults screenedResult = ModelUtil
				 * .createEncounterResult(e, "screened".toUpperCase(),
				 * screened.toUpperCase()); encounters.add(screenedResult);
				 * 
				 * EncounterResults missedResult =
				 * ModelUtil.createEncounterResult(e, "missed".toUpperCase(),
				 * missed.toUpperCase()); encounters.add(missedResult);
				 * 
				 * EncounterResults refusedResult =
				 * ModelUtil.createEncounterResult(e, "refused".toUpperCase(),
				 * refused.toUpperCase()); encounters.add(refusedResult);
				 * 
				 * EncounterResults suspectsResult =
				 * ModelUtil.createEncounterResult(e, "suspects".toUpperCase(),
				 * suspects.toUpperCase()); encounters.add(suspectsResult);
				 * 
				 * boolean resultSave = true;
				 * 
				 * for (int i = 0; i < encounters.size(); i++) { try {
				 * resultSave = ssi.saveEncounterResults(encounters.get(i)); }
				 * catch (Exception e1) { // Auto-generated catch block
				 * e1.printStackTrace(); break; }
				 * 
				 * if (!resultSave) {
				 * out.println("Could not save some results for line: " +
				 * (lineCount+1) + "->" + line); continue; }
				 * 
				 * }
				 */

			}
			out.println ("Saved line " + (lineCount + 1) + "->" + line);
			lineCount++;
		}
		out.flush ();
	}

}
