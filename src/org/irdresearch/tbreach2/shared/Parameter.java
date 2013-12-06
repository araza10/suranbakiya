/**
 * Represents a parameter for queries
 */

package org.irdresearch.tbreach2.shared;

/**
 * @author owais.hussain@irdresearch.org
 * 
 */
public final class Parameter implements java.io.Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 5615107890916594325L;
	String						name;
	String						value;
	DataType					type;

	public Parameter ()
	{
		// Not implemented
	}

	public Parameter (String name, String value, DataType type)
	{
		this.name = name;
		this.value = value;
		this.type = type;
	}

	// /**
	// * Get appropriate object of the current value which is in string. The
	// value is parsed and boxed into an object
	// * @return Object
	// */
	// @SuppressWarnings("deprecation")
	// public Object toObject()
	// {
	// try
	// {
	// switch (type)
	// {
	// case BOOLEAN:
	// return Boolean.parseBoolean(value);
	// case BYTE:
	// return Byte.parseByte(value);
	// case CHAR:
	// return value.charAt(0);
	// case DATE:
	// return Date.parse(value);
	// case DOUBLE:
	// return Double.parseDouble(value);
	// case FLOAT:
	// return Float.parseFloat(value);
	// case INT:
	// return Integer.parseInt(value);
	// case LONG:
	// return Long.parseLong(value);
	// case SHORT:
	// return Short.parseShort(value);
	// case STRING:
	// return value;
	// default:
	// return null;
	// }
	// }
	// catch (NumberFormatException e)
	// {
	// e.printStackTrace();
	// return null;
	// }
	// }

	/**
	 * Converts a comma separated string into Parameter
	 * 
	 * @param csvParams
	 * @return Parameter
	 */
	public Parameter fromString (String csvParams)
	{
		String[] params = csvParams.split (",");
		return new Parameter (params[0], params[1], DataType.valueOf (params[2]));
	}

	@Override
	public String toString ()
	{
		return name + ", " + value + ", " + type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode ()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode ());
		result = prime * result + ((type == null) ? 0 : type.hashCode ());
		result = prime * result + ((value == null) ? 0 : value.hashCode ());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals (Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass () != obj.getClass ())
			return false;
		Parameter other = (Parameter) obj;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals (other.name))
			return false;
		if (type == null)
		{
			if (other.type != null)
				return false;
		}
		else if (!type.equals (other.type))
			return false;
		if (value == null)
		{
			if (other.value != null)
				return false;
		}
		else if (!value.equals (other.value))
			return false;
		return true;
	}

	/**
	 * @return the name
	 */
	public String getName ()
	{
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName (String name)
	{
		this.name = name;
	}

	/**
	 * @return the value
	 */
	public String getValue ()
	{
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue (String value)
	{
		this.value = value;
	}

	/**
	 * @return the type
	 */
	public DataType getType ()
	{
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType (DataType type)
	{
		this.type = type;
	}

}
