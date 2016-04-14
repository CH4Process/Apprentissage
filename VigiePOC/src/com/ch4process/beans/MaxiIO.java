package com.ch4process.beans;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yoctopuce.YoctoAPI.*;

public class MaxiIO 
{
	
	
	private Integer portMapping;
	private Integer portState;
	private Integer portSize;
	private YDigitalIO io;
	
	private Map<Integer, String> channels = new HashMap<Integer, String>();
	private Map<Integer, Integer> channelsState = new HashMap<Integer, Integer>();
	private Map<String, String> erreurs = new HashMap<String, String>();
	private Integer temperature;
	
	public MaxiIO(String IPAddress)
	{
		try 
		{
			YAPI.RegisterHub(IPAddress);
			erreurs.put("Hub","Connection ok.");
		} 
		catch (YAPI_Exception ex) 
		{
			erreurs.put("Hub","Cannot contact VirtualHub (" + ex.getLocalizedMessage() + ")");
		}
		catch (NullPointerException ex)
		{
			
		}
		
		try
		{
			YTemperature temperatureSensor = YTemperature.FirstTemperature();
			temperature =  (int) (temperatureSensor.get_currentValue() *100);
			erreurs.put("Temperature","Reading ok.");
		}
		catch (YAPI_Exception ex)
		{
			erreurs.put("Temperature", "Error while retrieving temperature from sensor : " + ex.getLocalizedMessage());
		}
		
		try
		{
			io = YDigitalIO.FirstDigitalIO();
			portMapping = io.get_portDirection();
			portState = io.get_portState();
			portSize = io.get_portSize();
			
			erreurs.put("DigitalIO","Reading ok.");
		}
		catch (YAPI_Exception ex)
		{
			erreurs.put("DigitalIO", "Error while retrieving DigitalIO datas : " + ex.getLocalizedMessage());
		}
		
		sortChannels();
		sortChannelsState();
	}
	
	private void sortChannels()
	{
		for (int i=0; i <= (portSize -1); i++)
		{
			int mask = (int) Math.pow(2, i);
			
			if ((portMapping & mask) == 0)
			{
				channels.put(i, "Input");
			}
			else
			{
				channels.put(i, "Output");
			}
		}
	}
	
	private void sortChannelsState()
	{
		for (int i=0; i <= (portSize -1); i++)
		{
			int mask = (int) Math.pow(2, i);
			
			if ((portState & mask) == 0)
			{
				channelsState.put(i, 0);
			}
			else
			{
				channelsState.put(i, 1);
			}
		}
	}
	
	private void setBitState(int bitNumber)
	{
		try
		{
			io.toggle_bitState(bitNumber);
			erreurs.put("SetState", "Value successfully updated.");
		}
		catch (YAPI_Exception ex)
		{
			erreurs.put("SetState", "Error while trying to set bit value : " +ex.getLocalizedMessage());
		}
	}
	
	public void update(HttpServletRequest request)
	{		
		for (int i = 0; i < portSize; i++)
		{
			String bit = getValeurChamp(request, "cmd_"+i);
			if  (bit != null && bit.trim().length() > 0 )
			{
				setBitState(i);
			}	
		}
		
		try
		{
			portState = io.get_portState();
		}
		catch (YAPI_Exception ex)
		{
			erreurs.put("DigitalIO", "Error while retrieving DigitalIO datas : " + ex.getLocalizedMessage());
		}
		
		sortChannelsState();
		
	}
	
	private String getValeurChamp(HttpServletRequest request, String nomChamp) 
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
	
	public Map<String, String> getErreurs()
	{
		return erreurs;
	}
	
	public Map<Integer, String> getChannels()
	{
		return channels;
	}
	
	public Map<Integer, Integer> getChannelsState()
	{
		return channelsState;
	}
	
	public Integer getTemperature()
	{
		return temperature;
	}
	
}
