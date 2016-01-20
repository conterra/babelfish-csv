package de.conterra.babelfish.csv;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;

import org.apache.commons.csv.CSVFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.conterra.babelfish.plugin.Plugin;
import de.conterra.babelfish.plugin.PluginAdapter;
import de.conterra.babelfish.plugin.RestService;
import de.conterra.babelfish.plugin.ServiceContainer;
import de.conterra.babelfish.plugin.v10_02.feature.FeatureService;

/**
 * defines a {@link Plugin}, which represents CSV files as a
 * {@link FeatureService}
 * 
 * @version 0.3
 * @author chwe
 * @since 0.1
 */
public class CsvPlugin
implements Plugin
{
	/**
	 * the {@link Logger} of this class
	 * 
	 * @since 0.1
	 */
	public static final Logger LOGGER = LoggerFactory.getLogger(CsvPlugin.class);
	
	/**
	 * the only instance of a {@link CsvPlugin}<br>
	 * (singleton pattern)
	 * 
	 * @since 0.1
	 */
	public static final CsvPlugin INSTANCE = new CsvPlugin();
	
	/**
	 * standard constructor
	 * 
	 * @since 0.1
	 */
	public CsvPlugin()
	{
	}
	
	@Override
	public String getName()
	{
		return "CSV";
	}
	
	@Override
	public boolean init()
	{
		boolean result = true;
		
		try
		{
			File pluginFolder = new File(PluginAdapter.getPluginFolder(CsvPlugin.INSTANCE).toURI());
			
			for (File file : pluginFolder.listFiles())
			{
				String fileName = file.getName();
				
				Reader reader = null;
				try
				{
					reader = new FileReader(file);
					CSVFormat.EXCEL.parse(reader);
					
					try
					{
						if ( ! (ServiceContainer.registerService(new CsvService(file))))
							result = false;
					}
					catch (IllegalArgumentException e)
					{
						CsvPlugin.LOGGER.debug("Ignore the file " + fileName + ", because it is a config file and no CSV data file.", e);
					}
					catch (IOException e)
					{
						CsvPlugin.LOGGER.warn("The configuration of " + fileName + " couldn't be loaded!", e);
						
						result = false;
					}
				}
				catch (IOException e)
				{
					CsvPlugin.LOGGER.debug("The file " + fileName + " is not a valid CSV.", e);
				}
				
				if (reader != null)
				{
					try
					{
						reader.close();
					}
					catch (IOException e)
					{
						CsvPlugin.LOGGER.warn("Couldn't close reader of file: " + fileName, e);
					}
				}
			}
		}
		catch (URISyntaxException e)
		{
			String msg = "Exception occurred: " + e.getMessage();
			CsvPlugin.LOGGER.error(msg, e);
			throw new NullPointerException(msg);
		}
		
		return result;
	}
	
	@Override
	public boolean shutdown()
	{
		boolean result = true;
		
		for (RestService service : ServiceContainer.getServices(this.getName()))
		{
			if ( ! (ServiceContainer.unregisterService(service)))
				result = false;
		}
		
		return result;
	}
}