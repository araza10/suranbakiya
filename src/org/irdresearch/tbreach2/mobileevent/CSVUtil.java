package org.irdresearch.tbreach2.mobileevent;


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CSVUtil {

	
	public static void makeCsv(ZipOutputStream zip , String[][] record) throws IOException {
		//File inputFile = new File("utf-8-data.txt");
		//File outputFile = new File("latin-1-data.zip");

		ZipEntry entry = new ZipEntry("screeningdata.txt");

		//BufferedReader reader = new BufferedReader(new FileReader(inputFile));

		//ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(outputFile));
		BufferedWriter writer = new BufferedWriter(
		    new OutputStreamWriter(zip, Charset.forName("utf-8"))
		);

		zip.putNextEntry(entry);

		writer.append("\"ScreeningID\",\"PatientID\",\"FirstName\",\"LastName\",\"ScreenerID\",\"Age\",\"Gender\",\"Suspect\",\"Cough\",\"CoughDuration\",\"ProductiveCough\",\"Fever\",\"DateEntered\",\"LocationName\"");
		writer.append('\n');
		//Object[][] data = HibernateUtil.util.selectData (sqlQuery);
		String[][] stringData = new String[record.length][];//[row][column]
				
		for (int i = 0; i < record.length; i++)//first row length
		{
			stringData[i] = new String[record[i].length];//row length which is 10
			System.out.println("This is the string data......."+record[i].length);
			for (int j = 0; j < record[i].length; j++)
			{
				if (record[i][j] == null)
					record[i][j] = "";
				String str = record[i][j].toString ();
				stringData[i][j] = str;

				System.out.println(stringData[i][j]);
				writer.append(("\""+stringData[i][j]+"\""));
				
				writer.append(',');
				
			}
			writer.append('\n');
		}
		// this is the important part:
		// all character data is written via the writer and not the zip output stream
		/*String line = null;
		while ((line = reader.readLine()) != null) {
		    writer.append(line).append('\n');
		}*/
		writer.flush(); // i've used a buffered writer, so make sure to flush to the
		// underlying zip output stream

		zip.closeEntry();
		zip.finish();

		//reader.close(); 
		writer.close();
		//final FileOutputStream dest = new FileOutputStream(zipFileName);
		  //final ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
		  /*try {
		 int BUFFER = 2046;
		   byte[] data = new byte[BUFFER];
		  // final File folder = new File(filePath);
		  // final List< String > files = Arrays.asList(folder.list());
		   //for (String file : files) {
		   // final FileInputStream fi = new FileInputStream(filePath + PATH_SEP + file);
		   BufferedOutputStream fw = new BufferedOutputStream(new FileOutputStream("hello.csv"));
		   fw.write("\"ScreeningID\",\"PatientID\",\"FirstName\",\"LastName\",\"Age\",\"Gender\",\"Suspect\",\"Cough\",\"CoughDuration\",\"ProductiveCough\",\"Fever\",\"DateEntered\",\"LocationName\"".getBytes("utf-8"));
			fw.write('\n');
			//Object[][] data = HibernateUtil.util.selectData (sqlQuery);
			String[][] stringData = new String[record.length][];//[row][column]
					
			for (int i = 0; i < record.length; i++)//first row length
			{
				stringData[i] = new String[record[i].length];//row length which is 10
				System.out.println("This is the string data......."+record[i].length);
				for (int j = 0; j < record[i].length; j++)
				{
					if (record[i][j] == null)
						record[i][j] = "";
					String str = record[i][j].toString ();
					stringData[i][j] = str;

					System.out.println(stringData[i][j]);
					fw.write(("\""+stringData[i][j]+"\"").getBytes("utf-8"));
					
					fw.write(',');
					
				}
				fw.write('\n');
			}
		   zip.putNextEntry(new ZipEntry("hello.csv"));
		   ByteArrayOutputStream baos= new ByteArrayOutputStream();
		   baos.
		    //int count;
		   // while ((count = fw.read(data, 0, BUFFER)) != -1) {
		     zip.write(data, 0, count);
		   // }
		
		//ByteArrayOutputStream fw = new ByteArrayOutputStream();
		BufferedOutputStream fw = new BufferedOutputStream(new FileOutputStream("hello.csv"), "UTF-8");            
		
		fw.write("\"ScreeningID\",\"PatientID\",\"FirstName\",\"LastName\",\"Age\",\"Gender\",\"Suspect\",\"Cough\",\"CoughDuration\",\"ProductiveCough\",\"Fever\",\"DateEntered\",\"LocationName\"");
		fw.write('\n');
		//Object[][] data = HibernateUtil.util.selectData (sqlQuery);
		String[][] stringData = new String[record.length][];//[row][column]
				
		for (int i = 0; i < record.length; i++)//first row length
		{
			stringData[i] = new String[record[i].length];//row length which is 10
			System.out.println("This is the string data......."+record[i].length);
			for (int j = 0; j < record[i].length; j++)
			{
				if (record[i][j] == null)
					record[i][j] = "";
				String str = record[i][j].toString ();
				stringData[i][j] = str;

				System.out.println(stringData[i][j]);
				fw.write(("\""+stringData[i][j]+"\""));
				
				fw.write(',');
				
			}
			fw.write('\n');
		}
		String date=DateUtils.convertToString(new Date()).substring(0,10);
		byte[] b=fw.toString().getBytes();
		zip.putNextEntry(new ZipEntry("ScreeningData_"+date+".csv"));
		zip.write(b);
		zip.closeEntry();
		zip.close();*/

}
	
	public static void makeCsvPDF(ZipOutputStream zip , String[][] record) throws IOException {
		ByteArrayOutputStream fw = new ByteArrayOutputStream();
		fw.write("\"PatientID\",\"firstName\",\"lastName\",\"Gender\",\"LabSpecimenID\",\"DateRegistered\",\"Age\",\"CellPhone1\",\"CellPhone2\",\"CellPhone3\",\"ReferringFacilityCode\",\"GeneXpertSiteCode\",\"DateofSpecimen\",\"SpecimenCollection\",\"ResultStatus\",\"LC1Village\",\"SubCounty\",\"District\",\"DateTested\",\"DateResult\",\"MTBResults\",\"Error\",\"RIFResistant\",\"NTRL\",\"isPositive\",\"DateDOTSCBD\",\"TreatmentOutcome\",\"PlaceofTreatment\",\"TypeofDisease\",\"TreatmentStatus\",\"TypeofPatient\",\"Symptoms\",\"SymptomsOther\",\"HIVStatus\",\"ARV\",\"SputumSmearStatus\",\"XRayStatus\",\"DateEntered\",\"DateStarted\",\"DateEnded\",\"Cough\",\"Fever\",\"NightSweats\",\"WeightLoss\",\"Hemoptosis\"".getBytes());
		fw.write('\n');
		//Object[][] data = HibernateUtil.util.selectData (sqlQuery);
		String[][] stringData = new String[record.length][];//[row][column]
				
		for (int i = 0; i < record.length; i++)//first row length
		{
			stringData[i] = new String[record[i].length];//row length which is 10
			System.out.println("This is the string data......."+record[i].length);
			for (int j = 0; j < record[i].length; j++)
			{
				if (record[i][j] == null)
					record[i][j] = "";
				String str = record[i][j].toString ();
				stringData[i][j] = str;

				System.out.println(stringData[i][j]);
				fw.write(("\""+stringData[i][j]+"\"").getBytes());
				fw.write(',');
				
			}
			fw.write('\n');
		}
		String date=DateUtils.convertToString(new Date()).substring(0,10);
		byte[] b=fw.toString().getBytes("UTF-8");
		zip.putNextEntry(new ZipEntry("PIFExport_"+date+".csv"));
		zip.write(b);
		zip.closeEntry();
		zip.close();

}
	
	public static void makeMonthCsv(ZipOutputStream zip , String[][] record) throws IOException {
		ByteArrayOutputStream fw = new ByteArrayOutputStream();
		fw.write("\"Week\",\"# GXP tests performed\",\"# GXP +ve cases\",\"# Rif resistant cases\",\"# cases on treatment\",\"# cases successfully treated\"".getBytes());
		fw.write('\n');
		//Object[][] data = HibernateUtil.util.selectData (sqlQuery);
		String[][] stringData = new String[record.length][];//[row][column]
				
		for (int i = 0; i < record.length; i++)//first row length
		{
			stringData[i] = new String[record[i].length];//row length which is 10
			System.out.println("This is the string data......."+record[i].length);
			for (int j = 0; j < record[i].length; j++)
			{
				if (record[i][j] == null)
					record[i][j] = "";
				String str = record[i][j].toString ();
				stringData[i][j] = str;

				System.out.println(stringData[i][j]);
				fw.write(("\""+stringData[i][j]+"\"").getBytes());
				fw.write(',');
				
			}
			fw.write('\n');
		}
		String date=DateUtils.convertToString(new Date()).substring(0,10);
		byte[] b=fw.toString().getBytes();
		zip.putNextEntry(new ZipEntry("MonthCSV_"+date+".csv"));
		zip.write(b);
		zip.closeEntry();
		zip.close();

}
}
