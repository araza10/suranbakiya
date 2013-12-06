import java.util.Date;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.irdresearch.tbreach2.server.DataTable;
import org.irdresearch.tbreach2.server.HibernateUtil;
import org.irdresearch.tbreach2.server.ServerServiceImpl;


public class test {

	public static void main(String[] args) {
		/*ServerServiceImpl ssl = new ServerServiceImpl();
		Session session = HibernateUtil.util.getSession ();
		String SqlQuery = "SELECT count(*) FROM tbreach2.screening s, tbreach2.location l " +
				"where s.ScreenLocation = l.LocationID and l.LocationName like '%POLY%' and s.Suspect=true ";
		SQLQuery q = session.createSQLQuery (SqlQuery );
		List list = q.list ();
		list.get(0);*/
		
		DataTable dt = new DataTable();
		System.out.println("Start: "+new Date());
		String entireData = dt.getTableData("2013-10-01", "2013-10-10");
		System.out.println(entireData);
		System.out.println("Start: "+new Date());
	}
}
