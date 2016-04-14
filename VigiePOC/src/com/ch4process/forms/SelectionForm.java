package com.ch4process.forms;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

import com.CH4Process.PiSenseHat.SenseHat;
import com.ch4process.beans.BDDRequest;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import com.yoctopuce.YoctoAPI.YAPI;
import com.yoctopuce.YoctoAPI.YAPI_Exception;
import com.yoctopuce.YoctoAPI.YTemperature;


public class SelectionForm 
{
	private static final String DATABASE_ADDRESS = "192.168.0.7";
	private static final String DATABASE_USER = "pi";
	private static final String DATABASE_PASSWORD = "Crepitus";
	private static final String DATABASE_NAME = "CH4Process_DB";
	private static final String CHAMP_CAPTEUR = "capteurs";
	
	private Integer temperature;
	private String erreur = "";
	private String selection = "";
	private String selectionName = "";
	private ArrayList<Long[]> requestResult = new ArrayList<Long[]>();
	private Long[][] datas;
	
	private BDDRequest bddrequest;
	private SenseHat sensehat;
	private I2CBus bus;
	
	private String getValeurChamp( HttpServletRequest request, String nomChamp ) 
	{
		String valeur = request.getParameter(nomChamp);
		if (valeur == null || valeur.trim().length() == 0 ) 
		{
			return null;
		} 
		else 
		{
			return valeur.trim();
		}
	}
	
	private void validateSelection(String selection)
	{
		try
		{
			if (selection == null)
			{
				setErreur("Aucun capteur n'a été sélectionné !");
			}
			else
			{
				bddrequest = new BDDRequest(DATABASE_ADDRESS, DATABASE_NAME, DATABASE_USER, DATABASE_PASSWORD);
				bddrequest.makeRequest("SELECT libelle, modele, numeroserie FROM capteur WHERE capteur_id =" + selection + ";");
				
				if (bddrequest.nextResult())
				{
					String typeCapteur = bddrequest.getString("modele");
					String numeroDeSerie = bddrequest.getString("numeroserie");
					selectionName = bddrequest.getString("libelle");
					
					switch(typeCapteur)
					{
					case "YoctoTemperature": temperature = getTemperatureFromYoctoTemp(numeroDeSerie); break;
					case "SenseHat": temperature = getTemperatureFromSenseHat(); break;
					default: setErreur("Type de capteur inconnu : prévenir Alex !"); break;
					}
					bddrequest.LogTemperature(temperature, selection);
				}
				else
				{
					setErreur("Aucun capteur lié à la sélection. C'est pas normal faut le dire à Alex");
				}
				
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			setErreur("Exception levée pendant le test de type de capteur");
		}
		finally
		{
			bddrequest.closeResultset();
			bddrequest.closeStatement();
		}
	}
	
	private void setErreur(String message)
	{
		erreur = message;
	}
	
	public String getErreur()
	{
		return erreur;
	}
	
	public SelectionForm(HttpServletRequest request)
	{
		try
		{
			selection = getValeurChamp(request, CHAMP_CAPTEUR);
			validateSelection(selection);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	private Integer getTemperatureFromYoctoTemp(String serialNumber)
	{
		try
		{
			YAPI.RegisterHub(serialNumber);
			YTemperature temperatureSensor = YTemperature.FirstTemperature();
			return (int) (temperatureSensor.get_currentValue() *100);
		}
		catch(YAPI_Exception ex)
		{
			ex.printStackTrace();
			System.out.println(ex.getMessage());
			return 0;
		}
		finally
		{
			try
			{
				YAPI.FreeAPI();
			}
			catch (Exception ex)
			{
				System.out.println(ex.getMessage());
			}
		}
	}
	
	private Integer getTemperatureFromSenseHat()
	{	
		try
		{
			bus = I2CFactory.getInstance(I2CBus.BUS_1);

			sensehat = new SenseHat(bus);
			Float f = sensehat.getTemperatureFromHumidity();
			bus.close();
			bus = null;
			sensehat.close();
			sensehat = null;
			
			return (int) ( f * 100);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return 0;
		}
	}

	public void fetchDatas()
	{
		String capteur_id = selection;
		bddrequest.makeRequest("SELECT valeur, datetime FROM mesure WHERE capteur_id =" + capteur_id +" ORDER BY datetime ASC;");
		
		while(bddrequest.nextResult())
		{
			Long[] dataset = new Long[2];

			dataset[0] = bddrequest.getDate("datetime");
			dataset[1] = (long) (bddrequest.getInteger("valeur"));
			
			requestResult.add(dataset);
		}
		bddrequest.closeResultset();
		
		if (requestResult.size() > 0)
		{
			// Transfering the datas into a list because you can't mix datatypes in a Java array
			datas = new Long[requestResult.size()][];
			requestResult.toArray(datas);
		}
	}
	
	public Long[][] getDatas()
	{
		return datas;
	}
	
	public String getSelectionName()
	{
		return selectionName;
	}

}
