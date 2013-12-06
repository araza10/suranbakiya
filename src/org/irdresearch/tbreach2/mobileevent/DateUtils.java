package org.irdresearch.tbreach2.mobileevent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateUtils {

	public enum TIME_INTERVAL{
		SECOND,
		MINUTE,
		HOUR,
		DATE,
		DAY,
		WEEK,
		MONTH,
		QUARTER_YEAR,
		HALF_YEAR,
		YEAR
	}
	//public static final String DATE_FORMAT_

	private static DateTimeFormatter yyyy_MM_dd_HH_mm_ss = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	private static final DateTimeFormatter dd_MM_yyyy = DateTimeFormat.forPattern("dd-MM-yyyy");
	
	public DateUtils()
	{
		
	}
	/**
	 * compare till dates only, i.e. precision is only till date/day not compare time values
	 * @param date
	 * @return
	 */
	public static boolean datesEqual(Date date1,Date date2){
		SimpleDateFormat sd=new SimpleDateFormat("dd-MM-yyyy");
		sd.setLenient(false);
		String dat1=sd.format(date1);
		String dat2=sd.format(date2);
		if(dat1.compareTo(dat2)==0){
			return true;
		}
		return false;
	}
	public static boolean validateDate(String date){
		try{
			dd_MM_yyyy.parseDateTime(date);
			return true;
		}
		catch (Exception e) {
			try{
				yyyy_MM_dd_HH_mm_ss.parseDateTime(date);
				return true;
			}catch (Exception e1) {
				return false;
			}
			
		}
	}
	/**
	 * compare till dates upto hour, i.e. precision is only till hour of day , 
	 * do not compare other time values (minutes, seconds...)
	 * @param date
	 * @return
	 */
	public static boolean datesEqualUptoHour(Date date1,Date date2){
		Calendar dt1=Calendar.getInstance();
		dt1.setTime(date1);
		Calendar dt2=Calendar.getInstance();
		dt2.setTime(date2);
		if(dt1.get(Calendar.YEAR)==dt2.get(Calendar.YEAR)&&dt1.get(Calendar.MONTH)==dt2.get(Calendar.MONTH)
				&&dt1.get(Calendar.DAY_OF_MONTH)==dt2.get(Calendar.DAY_OF_MONTH)&&dt1.get(Calendar.HOUR_OF_DAY)==dt2.get(Calendar.HOUR_OF_DAY)){
			return true;
		}
		return false;
	}
	/**
	 * compare till dates only, i.e. precision is only till date/day not compare time values
	 * @param date
	 * @return
	 */
	public static boolean afterTodaysDate(Date date){
		Calendar dt1=Calendar.getInstance();
		dt1.setTime(date);
		Calendar now=Calendar.getInstance();
		if(now.get(Calendar.YEAR)<dt1.get(Calendar.YEAR)){
			return true;
		}
		if(now.get(Calendar.YEAR)==dt1.get(Calendar.YEAR)){
			if(now.get(Calendar.MONTH)<dt1.get(Calendar.MONTH)){
				return true;
			}
			if(now.get(Calendar.MONTH)==dt1.get(Calendar.MONTH)){
				if(now.get(Calendar.DATE)<dt1.get(Calendar.DATE)){
					return true;
				}
				return false;
			}
		}
		return false;
	}
	
	public static Date subtractInterval(Date date,int amount,TIME_INTERVAL timeInterval){
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		
		switch (timeInterval) {
		case SECOND:
			cal.add(Calendar.SECOND, -amount);
			break;
			
		case MINUTE:
			cal.add(Calendar.MINUTE, -amount);
			break;
			
		case HOUR:
			cal.add(Calendar.HOUR_OF_DAY, -amount);	
			break;
			
		case DATE:
		case DAY:
			cal.add(Calendar.DATE, -amount);		
			break;
			
		case WEEK:
			cal.add(Calendar.DATE, -(amount*7));							
			break;
			
		case  MONTH:
			cal.add(Calendar.MONTH, -amount);			
			break;
			
		case QUARTER_YEAR:
			cal.add(Calendar.MONTH, -(amount*3));				
			break;
			
		case HALF_YEAR:
			cal.add(Calendar.MONTH, -(amount*6));							
			break;	
			
		case YEAR:
			cal.add(Calendar.YEAR, -amount);							
			break;
		}
		return cal.getTime();
	}
	
	public static Date addInterval(Date date,int amount,TIME_INTERVAL timeInterval){
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);

		switch (timeInterval) {
		case SECOND:
			cal.add(Calendar.SECOND, amount);
			break;
			
		case MINUTE:
			cal.add(Calendar.MINUTE, amount);
			break;
			
		case HOUR:
			cal.add(Calendar.HOUR_OF_DAY, amount);	
			break;
			
		case DATE:
		case DAY:
			cal.add(Calendar.DATE, amount);		
			break;
			
		case WEEK:
			cal.add(Calendar.DATE, (amount*7));							
			break;
			
		case  MONTH:
			cal.add(Calendar.MONTH, amount);			
			break;
			
		case QUARTER_YEAR:
			cal.add(Calendar.MONTH, (amount*3));				
			break;
			
		case HALF_YEAR:
			cal.add(Calendar.MONTH, (amount*6));							
			break;	
			
		case YEAR:
			cal.add(Calendar.YEAR, amount);							
			break;
		}
		
		return cal.getTime();
	}
	
	/** 
	 * @param dategreater (or date after)
	 * @param datelesser  (or date before)
	 * @param timeInterval
	 * @return the integer difference between given dates which will have unit TIME_INTERVAL timeInterval provided with parameter
	 * return value is 
	 * -ve if @param dategreater is before @param datelesser
	 * 0 if given time intervals are equal
	 * +ve if @param dategreater is after @param datelesser
	 */
	public static int differenceBetweenIntervals(Date dategreater,Date datelesser,TIME_INTERVAL timeInterval){
		long timegrt=truncateDatetoDate(dategreater).getTime();
		long timelsr=truncateDatetoDate(datelesser).getTime();
		double intervalDivider=0;
		
		////if dategreater is less than/after date lesser then negate the value returned
		//by months and years fields
		
		switch (timeInterval) {
		case SECOND:
			intervalDivider=1000;
			break;
			
		case MINUTE:
			intervalDivider=1000*60;
			break;
			
		case HOUR:
			intervalDivider=1000*60*60;
			break;
			
		case DATE:
		case DAY:
			Period prd=new Period(dategreater.getTime(), datelesser.getTime(), PeriodType.days());
			return -prd.getDays();
			
		case WEEK:
			Period prw=new Period(dategreater.getTime(), datelesser.getTime(), PeriodType.weeks());
			return -prw.getWeeks();
			
		case  MONTH:
			Period pr=new Period(dategreater.getTime(), datelesser.getTime(), PeriodType.months());
			return -pr.getMonths();
					
		case QUARTER_YEAR:
			Period prqy=new Period(timegrt, timelsr, PeriodType.yearMonthDay());
			int years=-prqy.getYears();
			int months=-prqy.getMonths();
			return (years*4)+(months/3);
			
		case HALF_YEAR:
			Period prhy=new Period(timegrt, timelsr, PeriodType.yearMonthDay());
			int yrs=-prhy.getYears();
			int mths=-prhy.getMonths();
			return (yrs*2)+(mths/6);
			
		case YEAR:
			Period pry=new Period(timegrt, timelsr, PeriodType.years());
			return -pry.getYears();
		}
		
		double diff=((timegrt-timelsr)/(intervalDivider));
		return (int) diff;
	}
	@Deprecated
	/**
	 * Use DateUtils.subtractInterval(Date date,int amount,TIME_INTERVAL timeInterval) instead
	 */
	public static Date subtractNumOfDays(Date date,int daysToSubttract){
		DateTime dt=new DateTime(date);
		return dt.minusDays(daysToSubttract).toDate();
	}
	@Deprecated
	/**
	 * Use DateUtils.subtractInterval(Date date,int amount,TIME_INTERVAL timeInterval) instead
	 */
	public static Date getPastDate(int yearsbefore){
		DateTime dt=new DateTime();
		return dt.minusYears(yearsbefore).toDate();
	}
	@Deprecated
	/**
	 * Use DateUtils.differenceBetweenIntervals(Date dategreater,Date datelesser,TIME_INTERVAL timeInterval) instead
	 */
	public static int getDifferenceOfHours(Date date1,Date date2){
		int diff=(int) ((date1.getTime()-date2.getTime())/(60*60*1000));
		return diff;
	}
	@Deprecated
	/**
	 * Use DateUtils.differenceBetweenIntervals(Date dategreater,Date datelesser,TIME_INTERVAL timeInterval) instead
	 */
	public static int getDaysPassed(Date date1,Date date2){
		int diff=(int) ((truncateDatetoDate(date1).getTime()-truncateDatetoDate(date2).getTime())/(24*60*60*1000));
		return diff;
	}
	@Deprecated
	/**
	 * Use DateUtils.differenceBetweenIntervals(Date @dategreater,Date datelesser,TIME_INTERVAL timeInterval) instead.
	 * To use give Date (future or past) whose difference is to be found as @dategreater 
	 * and now date (e.g. new Date()) as @datelesser 
	 * which would give the years that have been passed from now
	 */
	public static int getNumOfYears(Date pastDate){
		DateTime dt=new DateTime(pastDate);
		DateTime now=new DateTime();
		int n=now.getYear();
		int d=dt.getYear();
		int years=n-d;
		return years;
	}
	@Deprecated
	/**
	 * Use DateUtils.differenceBetweenIntervals(Date @dategreater,Date datelesser,TIME_INTERVAL timeInterval) instead.
	 * To use give Date (future or past) whose difference is to be found as @dategreater 
	 * and now date (e.g. new Date()) as datelesser 
	 * which would give the weeks that have been passed from now
	 */
	public static int getNumOfWeeksPassed(Date pastdate){
		long pastms=pastdate.getTime();
		long nowms=new Date().getTime();
		
		int weekdiff=(int) ((nowms-pastms)/(1000*60*60*24*7));
		return weekdiff;
	}
	public static String truncateDate(Date date){
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		Calendar daybgn=Calendar.getInstance();
		daybgn.clear();
		daybgn.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 0, 0, 0);
		DateTime dt=new DateTime(daybgn);
		return dt.toString(yyyy_MM_dd_HH_mm_ss);
	}
	public static String roundoffDate(Date date){
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		Calendar dayend=Calendar.getInstance();
		dayend.clear();
		dayend.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 23, 59, 59);
		DateTime dt=new DateTime(dayend);
		return dt.toString(yyyy_MM_dd_HH_mm_ss);
	}
	public static Date truncateDatetoDate(Date date){
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		Calendar daybgn=Calendar.getInstance();
		daybgn.clear();
		daybgn.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 0, 0, 0);
		return daybgn.getTime();
	}
	public static Date roundoffDatetoDate(Date date){
		Calendar cal=Calendar.getInstance();
		cal.setTime(date);
		Calendar dayend=Calendar.getInstance();
		dayend.clear();
		dayend.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH), cal.get(Calendar.DATE), 23, 59, 59);
		return dayend.getTime();
	}

	public static Date convertToDate(String date){
		try{
		return dd_MM_yyyy.parseDateTime(date).toDate();
		}catch (Exception e) {
			return yyyy_MM_dd_HH_mm_ss.parseDateTime(date).toDate();
		}
	}
	public static String convertToString(Date date){
		DateTime dt=new DateTime(date);
		try{
			return dt.toString(yyyy_MM_dd_HH_mm_ss);
			}catch (Exception e) {
				return dt.toString(dd_MM_yyyy);
			}
	}
	public static void main(String[] args) {
		Period prhm=new Period(new Date().getTime(),new Date(19,12,12).getTime(),   PeriodType.yearMonthDay());
		int years=prhm.getYears();
		int months=prhm.getMonths();
		
		System.out.println("y:"+years+" m:"+months);
		Period pr=new Period(new Date(19,12,12).getTime(), new Date().getTime(), PeriodType.months());
	//	Period p = new Period(new Date(), new Date() , PeriodType.yearMonthDay());
		System.out.println(new Date());
		System.out.println(new Date(19,12,12));

		System.out.println(pr.getYears());
		
		/*SimpleDateFormat sd=new SimpleDateFormat();
		System.out.println(sd.format(new Date())+":"+sd.parse("29-06-1999"));
		Calendar cal1=Calendar.getInstance();
		cal1.set(Calendar.YEAR, 2011);
		cal1.set(Calendar.MONTH, 4);
		cal1.set(Calendar.DATE, 1);
		System.out.println(cal1.getTime());

		System.out.println(getNumOfWeeksPassed(cal1.getTime()));

		 DateFormat dfMedium = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
		 DateFormat dfShort = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
		 DateFormat dfLarge = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
		 DateFormat dfLong = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
DateFormat df=DateFormat.getDateTimeInstance();
df.format(new Date());
Calendar cal=Calendar.getInstance();
cal.set(1929, 3, 2);
DateTime dt=new DateTime(cal);

DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MM-yyyy HH:mm:ss");
dt.toString(fmt);

//fmt.parseDateTime("29-03-2010");
//fmt.parseDateTime("29Nov2010");
		System.out.println(dfMedium.format(new Date()));
		System.out.println(dfShort.format(new Date()));
		System.out.println(dfLarge.format(new Date()));
		System.out.println(dfLong.format(new Date()));
		System.out.println(dd_MM_yyyy.parseDateTime("29-06-1999").toDate());
		System.out.println(new Date());
		try {
			System.out.print(df.parse("8-8-8"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	
}
