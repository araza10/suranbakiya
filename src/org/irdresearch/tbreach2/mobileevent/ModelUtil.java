
package org.irdresearch.tbreach2.mobileevent;

import org.irdresearch.tbreach2.server.ServerServiceImpl;
import org.irdresearch.tbreach2.shared.model.Encounter;
import org.irdresearch.tbreach2.shared.model.EncounterResults;
import org.irdresearch.tbreach2.shared.model.EncounterResultsId;

public class ModelUtil
{

	public static EncounterResults createEncounterResult (Encounter e, String element, String value)
	{
		EncounterResultsId erId = new EncounterResultsId (e.getId ().getEncounterId (), e.getId ().getPid1 (), e.getId ().getPid2 (), element);
		EncounterResults er = new EncounterResults (erId, value);

		return er;
	}

	public static Boolean isValidGPID (String gpid)
	{
		Boolean result = false;

		ServerServiceImpl ssl = new ServerServiceImpl ();
		try
		{
			result = ssl.exists ("GP", "where GPID='" + gpid.toUpperCase () + "'");
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace ();
			return null;
		}

		return result;
	}

	public static Boolean isValidCHWID (String chwid)
	{
		Boolean result = false;

		ServerServiceImpl ssl = new ServerServiceImpl ();
		try
		{
			result = ssl.exists ("Worker", "where WorkerID='" + chwid.toUpperCase () + "'");
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace ();
			return null;
		}

		return result;
	}
}
