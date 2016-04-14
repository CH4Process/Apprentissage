package com.ch4process.beans;

import java.util.Map;
import java.util.LinkedHashMap;
import com.ch4process.beans.BDDRequest;

public class ListeCapteurs 
{
	private static final String POPULATE_REQUEST = "SELECT capteur_id, libelle, modele, numeroserie FROM capteur;";
	private static final String DATABASE_ADDRESS = "192.168.0.7";
	private static final String DATABASE_USER = "pi";
	private static final String DATABASE_PASSWORD = "Crepitus";
	private static final String DATABASE_NAME = "CH4Process_DB";
	
	private Map<Integer, String[]> liste = new LinkedHashMap<>();;
	private BDDRequest bddrequest;
	
	public ListeCapteurs()
	{
		try
		{
			bddrequest = new BDDRequest(DATABASE_ADDRESS, DATABASE_NAME, DATABASE_USER, DATABASE_PASSWORD);
			bddrequest.makeRequest(POPULATE_REQUEST);
			
			while(bddrequest.nextResult())
			{
				Integer key = bddrequest.getInteger("capteur_id");
				String[] value = new String[3];
				value[0] = bddrequest.getString("libelle");
				value[1] = bddrequest.getString("modele");
				value[2] = bddrequest.getString("numeroserie");
				liste.put(key, value);
			}
			
			bddrequest.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public Map<Integer, String[]> getListe()
	{
		return this.liste;
	}
	

}
