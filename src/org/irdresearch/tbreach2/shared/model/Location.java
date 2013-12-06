/**
 * 
 */

package org.irdresearch.tbreach2.shared.model;

import java.io.Serializable;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public class Location implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1793174478195055422L;
	private String				locationId;
	private String				locationName;
	private String				locationType;
	private String				cellPhone1;
	private String				cellPhone2;

	public Location ()
	{
	}

	public Location (String locationId, String locationName, String locationType)
	{
		this.locationId = locationId;
		this.locationName = locationName;
		this.locationType = locationType;
	}

	public Location (String locationId, String locationName, String locationType, String cellPhone1, String cellPhone2)
	{
		this.locationId = locationId;
		this.locationName = locationName;
		this.locationType = locationType;
		this.cellPhone1 = cellPhone1;
		this.cellPhone2 = cellPhone2;
	}

	public String getLocationId ()
	{
		return locationId;
	}

	public void setLocationId (String locationId)
	{
		this.locationId = locationId;
	}

	public String getLocationName ()
	{
		return locationName;
	}

	public void setLocationName (String locationName)
	{
		this.locationName = locationName;
	}

	public String getLocationType ()
	{
		return locationType;
	}

	public void setLocationType (String locationType)
	{
		this.locationType = locationType;
	}

	public String getCellPhone1 ()
	{
		return cellPhone1;
	}

	public void setCellPhone1 (String cellPhone1)
	{
		this.cellPhone1 = cellPhone1;
	}

	public String getCellPhone2 ()
	{
		return cellPhone2;
	}

	public void setCellPhone2 (String cellPhone2)
	{
		this.cellPhone2 = cellPhone2;
	}

	@Override
	public String toString ()
	{
		return locationId + ", " + locationName + ", " + locationType + ", " + cellPhone1 + ", " + cellPhone2;
	}
}
