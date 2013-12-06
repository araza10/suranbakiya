
package org.irdresearch.tbreach2.shared.model;

import java.io.Serializable;

public class Users implements Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private String				pid;
	private String				userName;
	private String				password;
	private String				role;
	private String				status;
	private String 				user_id;
	private String				firstName;
	private String 				lastName;

	public Users ()
	{

	}

	public Users (String pid, String userName, String password, String role, String status, String user_id, String firstName, String lastName)
	{
		this.pid = pid;
		this.userName = userName;
		this.password = password;
		this.role = role;
		this.status = status;
		this.user_id = user_id;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPid ()
	{
		return pid;
	}

	public void setPid (String pid)
	{
		this.pid = pid;
	}

	public String getUserName ()
	{
		return userName;
	}

	public void setUserName (String userName)
	{
		this.userName = userName;
	}

	public String getPassword ()
	{
		return password;
	}

	public void setPassword (String password)
	{
		this.password = password;
	}

	public String getRole ()
	{
		return role;
	}

	public void setRole (String role)
	{
		this.role = role;
	}

	public String getStatus ()
	{
		return status;
	}

	public void setStatus (String status)
	{
		this.status = status;
	}

}
