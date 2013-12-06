package org.irdresearch.tbreach2.mobileevent;

import java.util.Scanner;

public class Example {
	
	public static void main(String []args)
	
	{
		Scanner sc=new Scanner(System.in);
		String kl = sc.next();
		int k = Integer.parseInt(kl);
		if(k%3==0 && k%5==0)
		{
			System.out.println("Carpet");
		}
		else if(k%3==0)
			{
				System.out.println("car");
			}
			else if(k%5==0)
			{
				System.out.println("pet");
			}
			 
		
	}

}
