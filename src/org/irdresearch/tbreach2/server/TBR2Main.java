
package org.irdresearch.tbreach2.server;

import java.util.Date;

import org.irdresearch.tbreach2.shared.model.LabMapping;
import org.irdresearch.tbreach2.shared.model.LabMappingId;
import org.irdresearch.tbreach2.shared.model.Screening;
import org.irdresearch.tbreach2.shared.model.SputumResults;
import org.irdresearch.tbreach2.shared.model.Users;

public class TBR2Main
{
	public static void main (String[] args)
	{
		ServerServiceImpl impl = new ServerServiceImpl ();
		//SputumResults obj = new SputumResults();
		Users obj = new Users();
		obj.setFirstName("Makhfurat");
		obj.setLastName("Rakhmatova");
		obj.setUserName("94366");
		obj.setPid("94366");
		obj.setStatus("ACTIVE");
		obj.setPassword("94366");
		obj.setRole("LabTech");
		
		Users obj2 = new Users();
		obj2.setFirstName("Khuriyat");
		obj2.setLastName("Zhuraeva");
		obj2.setUserName("08520");
		obj2.setPid("08520");
		obj2.setStatus("ACTIVE");
		obj2.setPassword("08520");
		obj2.setRole("LabTech");
		
		Users obj3 = new Users();
		obj3.setFirstName("Bakhodir");
		obj3.setLastName("Amonkulov");
		obj3.setUserName("14906");
		obj3.setPid("14906");
		obj3.setStatus("ACTIVE");
		obj3.setPassword("14906");
		obj3.setRole("LabTech");
		
		Users obj4 = new Users();
		obj4.setFirstName("Zebuniso");
		obj4.setLastName("Zhabarova");
		obj4.setUserName("53608");
		obj4.setPid("53608");
		obj4.setStatus("ACTIVE");
		obj4.setPassword("53608");
		obj4.setRole("LabTech");
		
		Users obj5 = new Users();
		obj5.setFirstName("Rakhima");
		obj5.setLastName("Ganijonova");
		obj5.setUserName("24876");
		obj5.setPid("24876");
		obj5.setStatus("ACTIVE");
		obj5.setPassword("24876");
		obj5.setRole("LabTech");
		
		Users obj6 = new Users();
		obj6.setFirstName("Gulnozai");
		obj6.setLastName("Abdukhamid");
		obj6.setUserName("22847");
		obj6.setPid("22847");
		obj6.setStatus("ACTIVE");
		obj6.setPassword("22847");
		obj6.setRole("LabTech");
		
		Users obj7 = new Users();
		obj7.setFirstName("Sabokhat");
		obj7.setLastName("Zulunova ");
		obj7.setUserName("34275");
		obj7.setPid("34275");
		obj7.setStatus("ACTIVE");
		obj7.setPassword("34275");
		obj7.setRole("LabTech");
		
		Users obj8 = new Users();
		obj8.setFirstName("Khursheda");
		obj8.setLastName("Khakimova ");
		obj8.setUserName("81201");
		obj8.setPid("81201");
		obj8.setStatus("ACTIVE");
		obj8.setPassword("81201");
		obj8.setRole("LabTech");
		
		Users obj9 = new Users();
		obj9.setFirstName("Zebuniso");
		obj9.setLastName("Bidonova");
		obj9.setUserName("64560");
		obj9.setPid("64560");
		obj9.setStatus("ACTIVE");
		obj9.setPassword("64560");
		obj9.setRole("LabTech");
		
		Users obj10 = new Users();
		obj10.setFirstName("Gulsara");
		obj10.setLastName("Abdurakhimova");
		obj10.setUserName("57877");
		obj10.setPid("57877");
		obj10.setStatus("ACTIVE");
		obj10.setPassword("57877");
		obj10.setRole("LabTech");
		
		Users obj11 = new Users();
		obj11.setFirstName("Firuza");
		obj11.setLastName("Murodova");
		obj11.setUserName("43667");
		obj11.setPid("43667");
		obj11.setStatus("ACTIVE");
		obj11.setPassword("43667");
		obj11.setRole("LabTech");
		
		Users obj12 = new Users();
		obj12.setFirstName("Anzurat");
		obj12.setLastName("Shukarieva");
		obj12.setUserName("55694");
		obj12.setPid("55694");
		obj12.setStatus("ACTIVE");
		obj12.setPassword("55694");
		obj12.setRole("LabTech");
		
		Users obj13 = new Users();
		obj13.setFirstName("Mavlyuda");
		obj13.setLastName("Shamsova");
		obj13.setUserName("79047");
		obj13.setPid("79047");
		obj13.setStatus("ACTIVE");
		obj13.setPassword("79047");
		obj13.setRole("LabTech");
		
		Users obj14 = new Users();
		obj14.setFirstName("Nigina");
		obj14.setLastName("Begova");
		obj14.setUserName("62257");
		obj14.setPid("62257");
		obj14.setStatus("ACTIVE");
		obj14.setPassword("62257");
		obj14.setRole("LabTech");
		
		Users obj15 = new Users();
		obj15.setFirstName("Rano");
		obj15.setLastName("Abduraimova");
		obj15.setUserName("30681");
		obj15.setPid("30681");
		obj15.setStatus("ACTIVE");
		obj15.setPassword("30681");
		obj15.setRole("LabTech");
		
		Users obj16 = new Users();
		obj16.setFirstName("Inobat");
		obj16.setLastName("Alieva");
		obj16.setUserName("88271");
		obj16.setPid("88271");
		obj16.setStatus("ACTIVE");
		obj16.setPassword("88271");
		obj16.setRole("LabTech");
		
		Users obj17 = new Users();
		obj17.setFirstName("Kholmoh");
		obj17.setLastName("Akhunova");
		obj17.setUserName("44172");
		obj17.setPid("44172");
		obj17.setStatus("ACTIVE");
		obj17.setPassword("44172");
		obj17.setRole("LabTech");
		
		Users obj18 = new Users();
		obj18.setFirstName("Umeda");
		obj18.setLastName("Igamova");
		obj18.setUserName("08500");
		obj18.setPid("08500");
		obj18.setStatus("ACTIVE");
		obj18.setPassword("08500");
		obj18.setRole("LabTech");
		
		Users obj19 = new Users();
		obj19.setFirstName("Dilorom");
		obj19.setLastName("Murodova");
		obj19.setUserName("35253");
		obj19.setPid("35253");
		obj19.setStatus("ACTIVE");
		obj19.setPassword("35253");
		obj19.setRole("LabTech");
		
		Users obj20 = new Users();
		obj20.setFirstName("Mavzunai");
		obj20.setLastName("Mirshakar");
		obj20.setUserName("63183");
		obj20.setPid("63183");
		obj20.setStatus("ACTIVE");
		obj20.setPassword("63183 ");
		obj20.setRole("LabTech");
		
		Users obj21 = new Users();
		obj21.setFirstName("Matlyuba");
		obj21.setLastName("Bakhromova");
		obj21.setUserName("35293");
		obj21.setPid("35293");
		obj21.setStatus("ACTIVE");
		obj21.setPassword("35293");
		obj21.setRole("LabTech");
		
		Users obj22 = new Users();
		obj22.setFirstName("Khafiza");
		obj22.setLastName("Boboeva");
		obj22.setUserName("46758");
		obj22.setPid("46758");
		obj22.setStatus("ACTIVE");
		obj22.setPassword("46758");
		obj22.setRole("LabTech");
		
		Users obj23 = new Users();
		obj23.setFirstName("Kumri");
		obj23.setLastName("Tolibova");
		obj23.setUserName("63880");
		obj23.setPid("63880");
		obj23.setStatus("ACTIVE");
		obj23.setPassword("63880");
		obj23.setRole("LabTech");
		
		Users obj24 = new Users();
		obj24.setFirstName("Mamura");
		obj24.setLastName("Sobirova");
		obj24.setUserName("28217");
		obj24.setPid("28217");
		obj24.setStatus("ACTIVE");
		obj24.setPassword("28217");
		obj24.setRole("LabTech");
		
		Users obj25 = new Users();
		obj25.setFirstName("Tamanno");
		obj25.setLastName("Yatimova");
		obj25.setUserName("86244");
		obj25.setPid("86244");
		obj25.setStatus("ACTIVE");
		obj25.setPassword("86244");
		obj25.setRole("LabTech");
		
		Users obj26 = new Users();
		obj26.setFirstName("Takhmina");
		obj26.setLastName("Abdulloeva");
		obj26.setUserName("87054");
		obj26.setPid("87054");
		obj26.setStatus("ACTIVE");
		obj26.setPassword("87054");
		obj26.setRole("LabTech");
		
		Users obj27 = new Users();
		obj27.setFirstName("Fotima");
		obj27.setLastName("Mirzoeva");
		obj27.setUserName("47337");
		obj27.setPid("47337");
		obj27.setStatus("ACTIVE");
		obj27.setPassword("47337");
		obj27.setRole("LabTech");
		
		Users obj28 = new Users();
		obj28.setFirstName("Saida");
		obj28.setLastName("Khamidova");
		obj28.setUserName("81248");
		obj28.setPid("81248");
		obj28.setStatus("ACTIVE");
		obj28.setPassword("81248");
		obj28.setRole("LabTech");
		
		Users obj29 = new Users();
		obj29.setFirstName("Mavlyuda");
		obj29.setLastName("Solieva");
		obj29.setUserName("30013");
		obj29.setPid("30013");
		obj29.setStatus("ACTIVE");
		obj29.setPassword("30013");
		obj29.setRole("LabTech");
		
		Users obj30 = new Users();
		obj30.setFirstName("Intizor");
		obj30.setLastName("Ashurova");
		obj30.setUserName("97451");
		obj30.setPid("97451");
		obj30.setStatus("ACTIVE");
		obj30.setPassword("97451");
		obj30.setRole("LabTech");
		
		Users obj31 = new Users();
		obj31.setFirstName("Mukharama");
		obj31.setLastName("Izatulloeva");
		obj31.setUserName("81905");
		obj31.setPid("81905");
		obj31.setStatus("ACTIVE");
		obj31.setPassword("81905");
		obj31.setRole("LabTech");
		
		Users obj32 = new Users();
		obj32.setFirstName("Marjona");
		obj32.setLastName("Saydalieva");
		obj32.setUserName("63745");
		obj32.setPid("63745");
		obj32.setStatus("ACTIVE");
		obj32.setPassword("63745");
		obj32.setRole("LabTech");
		
		Users obj33 = new Users();
		obj33.setFirstName("Maidagul");
		obj33.setLastName("Valieva");
		obj33.setUserName("32669");
		obj33.setPid("32669");
		obj33.setStatus("ACTIVE");
		obj33.setPassword("32669");
		obj33.setRole("LabTech");
		
		Users obj34 = new Users();
		obj34.setFirstName("Nisso");
		obj34.setLastName("Boboeva");
		obj34.setUserName("60051");
		obj34.setPid("60051");
		obj34.setStatus("ACTIVE");
		obj34.setPassword("60051");
		obj34.setRole("LabTech");
		
		Users obj35 = new Users();
		obj35.setFirstName("Zamira");
		obj35.setLastName("Bakieva");
		obj35.setUserName("01224");
		obj35.setPid("01224");
		obj35.setStatus("ACTIVE");
		obj35.setPassword("01224");
		obj35.setRole("LabTech");
		
		Users obj36 = new Users();
		obj36.setFirstName("Lola");
		obj36.setLastName("Dodojonova");
		obj36.setUserName("76300");
		obj36.setPid("76300");
		obj36.setStatus("ACTIVE");
		obj36.setPassword("76300");
		obj36.setRole("LabTech");
		
		Users obj37 = new Users();
		obj37.setFirstName("Surayo");
		obj37.setLastName("Azizova");
		obj37.setUserName("37641");
		obj37.setPid("37641");
		obj37.setStatus("ACTIVE");
		obj37.setPassword("37641");
		obj37.setRole("LabTech");
		
		Users obj38 = new Users();
		obj38.setFirstName("Makhfirat");
		obj38.setLastName("Davlatova");
		obj38.setUserName("10908");
		obj38.setPid("10908");
		obj38.setStatus("ACTIVE");
		obj38.setPassword("10908");
		obj38.setRole("LabTech");
		
/*		Users dmurodova = new Users();
		dmurodova.setUserName("dmurodova");
		dmurodova.setPid("dmurodova");
		dmurodova.setStatus("ACTIVE");
		dmurodova.setPassword("cdvebr");
		dmurodova.setRole("LabTech");
		
		Users khakimova = new Users();
		khakimova.setUserName("khakimova");
		khakimova.setPid("khakimova");
		khakimova.setStatus("ACTIVE");
		khakimova.setPassword("4y6prk");
		khakimova.setRole("LabTech");
		
		Users zulunova = new Users();
		zulunova.setUserName("zulunova");
		zulunova.setPid("zulunova");
		zulunova.setStatus("ACTIVE");
		zulunova.setPassword("a3gjre");
		zulunova.setRole("LabTech");
		
		Users shamsova = new Users();
		shamsova.setUserName("shamsova");
		shamsova.setPid("shamsova");
		shamsova.setStatus("ACTIVE");
		shamsova.setPassword("zppubk");
		shamsova.setRole("LabTech");
		
		Users valieva = new Users();
		valieva.setUserName("valieva");
		valieva.setPid("valieva");
		valieva.setStatus("ACTIVE");
		valieva.setPassword("znzavz");
		valieva.setRole("LabTech");
		
		Users boboeva = new Users();
		boboeva.setUserName("boboeva");
		boboeva.setPid("boboeva");
		boboeva.setStatus("ACTIVE");
		boboeva.setPassword("gejgzr");
		boboeva.setRole("LabTech");
		
		Users zhuraeva = new Users();
		zhuraeva.setUserName("zhuraeva");
		zhuraeva.setPid("zhuraeva");
		zhuraeva.setStatus("ACTIVE");
		zhuraeva.setPassword("529axm");
		zhuraeva.setRole("LabTech");
		
		Users rakhmatova = new Users();
		zhuraeva.setUserName("rakhmatova");
		zhuraeva.setPid("rakhmatova");
		zhuraeva.setStatus("ACTIVE");
		zhuraeva.setPassword("espsjg");
		zhuraeva.setRole("LabTech");
		
		Users amonkulov = new Users();
		amonkulov.setUserName("amonkulov");
		amonkulov.setPid("amonkulov");
		amonkulov.setStatus("ACTIVE");
		amonkulov.setPassword("ny7vtf");
		amonkulov.setRole("LabTech");
		
		Users zhabarova = new Users();
		zhabarova.setUserName("zhabarova");
		zhabarova.setPid("zhabarova");
		zhabarova.setStatus("ACTIVE");
		zhabarova.setPassword("cftvuy");
		zhabarova.setRole("LabTech");
		
		Users dodojonova = new Users();
		dodojonova.setUserName("dodojonova");
		dodojonova.setPid("dodojonova");
		dodojonova.setStatus("ACTIVE");
		dodojonova.setPassword("u5gens");
		dodojonova.setRole("LabTech");
		
		Users bakieva = new Users();
		bakieva.setUserName("bakieva");
		bakieva.setPid("bakieva");
		bakieva.setStatus("ACTIVE");
		bakieva.setPassword("egmvr3");
		bakieva.setRole("LabTech");
		
		Users davlatova = new Users();
		davlatova.setUserName("davlatova");
		davlatova.setPid("davlatova");
		davlatova.setStatus("ACTIVE");
		davlatova.setPassword("egmvr3");
		davlatova.setRole("LabTech");
		
		Users azizova = new Users();
		azizova.setUserName("azizova");
		azizova.setPid("azizova");
		azizova.setStatus("ACTIVE");
		azizova.setPassword("a3gjre");
		azizova.setRole("LabTech");
		
		Users abdukhamid = new Users();
		abdukhamid.setUserName("abdukhamid");
		abdukhamid.setPid("abdukhamid");
		abdukhamid.setStatus("ACTIVE");
		abdukhamid.setPassword("h2pkcn");
		abdukhamid.setRole("LabTech");
		
		Users ganijonova = new Users();
		ganijonova.setUserName("ganijonova");
		ganijonova.setPid("ganijonova");
		ganijonova.setStatus("ACTIVE");
		ganijonova.setPassword("vbvusn");
		ganijonova.setRole("LabTech");
		
		Users zhuraeva = new Users();
		zhuraeva.setUserName("zhuraeva");
		zhuraeva.setPid("zhuraeva");
		zhuraeva.setStatus("ACTIVE");
		zhuraeva.setPassword("529axm");
		zhuraeva.setRole("LabTech");
		
		Users abdukhamid = new Users();
		abdukhamid.setUserName("abdukhamid");
		abdukhamid.setPid("abdukhamid");
		abdukhamid.setStatus("ACTIVE");
		abdukhamid.setPassword("h2pkcn");
		abdukhamid.setRole("LabTech");
*/		
		
		
	//	obj.setPatientId ("FAKE");
		//obj.setAge (27);
		//obj.setDateSmearTested(new Date());
		try
		{
/*			impl.saveUser(dmurodova);
			impl.saveUser(khakimova);
			impl.saveUser(zulunova);
			impl.saveUser(shamsova);
			impl.saveUser(valieva);
			impl.saveUser(boboeva);
			
			impl.saveUser(amonkulov);
			impl.saveUser(zhabarova);
			impl.saveUser(dodojonova);
			impl.saveUser(bakieva);
			impl.saveUser(davlatova);
			impl.saveUser(azizova);
			impl.saveUser(ganijonova);*/
			/*impl.saveUser(abdukhamid);
			impl.saveUser(zhuraeva);*/
			
/*			impl.saveUser(obj);
			impl.saveUser(obj2);
			impl.saveUser(obj3);
			impl.saveUser(obj4);
			impl.saveUser(obj5);
			impl.saveUser(obj6);
			impl.saveUser(obj7);
			impl.saveUser(obj8);
			impl.saveUser(obj9);
			impl.saveUser(obj10);
			impl.saveUser(obj11);
			impl.saveUser(obj12);
			impl.saveUser(obj13);*/
			impl.saveUser(obj15);
			impl.saveUser(obj16);
			impl.saveUser(obj17);
			impl.saveUser(obj18);
			impl.saveUser(obj19);
			impl.saveUser(obj20);
			impl.saveUser(obj21);
			impl.saveUser(obj22);
			impl.saveUser(obj23);
			impl.saveUser(obj24);
			impl.saveUser(obj25);
			impl.saveUser(obj26);
			impl.saveUser(obj27);
			impl.saveUser(obj28);
			impl.saveUser(obj29);
			impl.saveUser(obj30);
			impl.saveUser(obj31);
			impl.saveUser(obj32);
			impl.saveUser(obj33);
			impl.saveUser(obj34);
			impl.saveUser(obj35);
			impl.saveUser(obj36);
			impl.saveUser(obj37);
			impl.saveUser(obj38);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
		}
		
		
		
	}
}
