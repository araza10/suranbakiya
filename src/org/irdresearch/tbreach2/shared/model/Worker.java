
package org.irdresearch.tbreach2.shared.model;

// Generated Aug 28, 2011 3:01:59 PM by Hibernate Tools 3.4.0.Beta1

/**
 * Worker generated by hbm2java
 */
public class Worker implements java.io.Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 6504827084321406008L;
	private String				workerId;
	private String				workerRole;

	public Worker ()
	{
	}

	public Worker (String workerId, String workerRole)
	{
		this.workerId = workerId;
		this.workerRole = workerRole;
	}

	public String getWorkerId ()
	{
		return this.workerId;
	}

	public void setWorkerId (String workerId)
	{
		this.workerId = workerId;
	}

	public String getWorkerRole ()
	{
		return this.workerRole;
	}

	public void setWorkerRole (String workerRole)
	{
		this.workerRole = workerRole;
	}

}