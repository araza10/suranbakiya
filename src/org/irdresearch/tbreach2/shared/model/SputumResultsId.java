// default package
// Generated Jan 2, 2011 1:21:18 PM by Hibernate Tools 3.4.0.Beta1

package org.irdresearch.tbreach2.shared.model;

/**
 * SputumResultsId generated by hbm2java
 */
public class SputumResultsId implements java.io.Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -2967927067077169072L;
	private String				patientId;
	private int					sputumTestId;

	public SputumResultsId ()
	{
		// Not implemented
	}

	public SputumResultsId (String patientId, int sputumTestId)
	{
		this.patientId = patientId;
		this.sputumTestId = sputumTestId;
	}

	public String getPatientId ()
	{
		return this.patientId;
	}

	public void setPatientId (String patientId)
	{
		this.patientId = patientId;
	}

	public int getSputumTestId ()
	{
		return this.sputumTestId;
	}

	public void setSputumTestId (int sputumTestId)
	{
		this.sputumTestId = sputumTestId;
	}

	@Override
	public String toString ()
	{
		return patientId + ", " + sputumTestId;
	}

}
