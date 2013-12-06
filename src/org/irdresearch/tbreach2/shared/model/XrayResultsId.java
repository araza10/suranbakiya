// default package
// Generated Jan 6, 2011 10:24:45 AM by Hibernate Tools 3.4.0.Beta1

package org.irdresearch.tbreach2.shared.model;

/**
 * XrayResultsId generated by hbm2java
 */
public class XrayResultsId implements java.io.Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 3528906464370487265L;
	private int					irs;
	private String				patientId;

	public XrayResultsId ()
	{
		// Not implemented
	}

	public XrayResultsId (int xrayId, String patientId)
	{
		this.irs = xrayId;
		this.patientId = patientId;
	}

	public int getIrs ()
	{
		return this.irs;
	}

	public void setIrs (int irs)
	{
		this.irs = irs;
	}

	public String getPatientId ()
	{
		return this.patientId;
	}

	public void setPatientId (String patientId)
	{
		this.patientId = patientId;
	}

	@Override
	public String toString ()
	{
		return irs + ", " + patientId;
	}
}
