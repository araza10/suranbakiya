/**
 * This class contains constants and project-specific methods which will be used throughout the System
 */

package org.irdresearch.tbreach2.shared;

import java.util.ArrayList;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public final class TBR
{
	private static final String	resourcesPath			= "/var/lib/tomcat6/webapps/tbreach/";
	private static final String	alternateResourcesPath	= "c:\\workspace\\TBR\\trunk\\tbreach2\\";
	// private static final String resourcesPath =
	// "/home/owais/workspace/TBR/trunk/tbreach/war/";
	private static String		currentUser				= "";
	private static String		passCode				= "";
	public static final String	hashingAlgorithm		= "SHA";
	public static final String	packageName				= "org.irdresearch.tbreach";
	public static final String	projectTitle			= "TB REACH";
	public static final char	separatorChar			= ',';
	public static final int		sessionLimit			= 15 * 60 * 1000;
	public static String[][]	lists;

	public static void fillLists (String[][] lists)
	{
		TBR.lists = lists;
	}

	/**
	 * Concatenate an Array of Strings into single String
	 * 
	 * @param array
	 * @return string
	 */
	public static String concatenateArray (String[] array)
	{
		StringBuilder concatenated = new StringBuilder ();
		for (String s : array)
		{
			concatenated.append (s);
			concatenated.append (TBR.separatorChar);
		}
		concatenated.deleteCharAt (concatenated.length () - 1); // Remove
																// additional
																// separator
		return concatenated.toString ();
	}

	/**
	 * Get a list of values which will be constant throughout the application
	 * 
	 * @param listType
	 * @return array
	 */
	public static String[] getDefinitionValues (String definitionType)
	{
		ArrayList<String> list = new ArrayList<String> ();
		for (int i = 0; i < lists.length; i++)
			if (lists[i][0] != null && definitionType.equalsIgnoreCase (lists[i][0]))
				list.add (lists[i][1]);
		String[] prototype = new String[] {};
		return list.toArray (prototype);
	}

	/**
	 * Get secret question
	 * 
	 * @return array
	 */
	public static String[] getSecretQuestions ()
	{
		String[] questions = {"WHO IS YOUR FAVOURITE NATIONAL HERO?", "WHAT PHONE MODEL ARE YOU PLANNING TO PURCHASE NEXT?", "WHERE WAS YOUR MOTHER BORN?", "WHEN DID YOU BUY YOUR FIRST CAR?",
				"WHAT WAS YOUR CHILDHOOD NICKNAME?", "WHAT IS YOUR FAVOURITE CARTOON CHARACTER?"};
		return questions;
	}

	/**
	 * Get current User Name (saved in cookies on client-side)
	 * 
	 * @return currentUser
	 */
	public static String getCurrentUser ()
	{
		return currentUser;
	}

	/**
	 * Set current user
	 * 
	 * @param currentUser
	 */
	public static void setCurrentUser (String currentUser)
	{
		TBR.currentUser = currentUser.toUpperCase ();
	}

	/**
	 * Get pass code (first 4 characters of User's password)
	 * 
	 * @return passCode
	 */
	public static String getPassCode ()
	{
		return passCode;
	}

	/**
	 * Set pass code for current user
	 * 
	 * @param passCode
	 */
	public static void setPassCode (String passCode)
	{
		TBR.passCode = passCode;
	}

	/**
	 * @return the reportPath
	 */
	public static String getReportPath ()
	{
		return resourcesPath + "rpt/";
	}

	/**
	 * @return the staticFilePath
	 */
	public static String getStaticFilePath ()
	{
		return resourcesPath + "StaticData.xml";
	}

	/**
	 * @return the alternateStaticFilePath
	 */
	public static String getAlternateStaticFilePath ()
	{
		return alternateResourcesPath + "StaticData.xml";
	}

	/**
	 * @return the resourcesPath
	 */
	public static String getResourcesPath ()
	{
		return resourcesPath;
	}
}
