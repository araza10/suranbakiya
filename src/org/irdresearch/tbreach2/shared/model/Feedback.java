
package org.irdresearch.tbreach2.shared.model;

// Generated Aug 28, 2011 3:01:59 PM by Hibernate Tools 3.4.0.Beta1

/**
 * Feedback generated by hbm2java
 */
public class Feedback implements java.io.Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -4381243990392015578L;
	private Long				feedbackId;
	private String				userName;
	private String				feedbackType;
	private String				detail;

	public Feedback ()
	{
	}

	public Feedback (String userName, String feedbackType, String detail)
	{
		this.userName = userName;
		this.feedbackType = feedbackType;
		this.detail = detail;
	}

	public Long getFeedbackId ()
	{
		return this.feedbackId;
	}

	public void setFeedbackId (Long feedbackId)
	{
		this.feedbackId = feedbackId;
	}

	public String getUserName ()
	{
		return this.userName;
	}

	public void setUserName (String userName)
	{
		this.userName = userName;
	}

	public String getFeedbackType ()
	{
		return this.feedbackType;
	}

	public void setFeedbackType (String feedbackType)
	{
		this.feedbackType = feedbackType;
	}

	public String getDetail ()
	{
		return this.detail;
	}

	public void setDetail (String detail)
	{
		this.detail = detail;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString ()
	{
		return feedbackId + ", " + userName + ", " + feedbackType + ", " + detail;
	}

}