package de.conterra.babelfish.csv;

import de.conterra.babelfish.plugin.Plugin;
import de.conterra.babelfish.plugin.PluginAdapter;
import de.conterra.babelfish.plugin.RestService;
import de.conterra.babelfish.plugin.ServiceContainer;
import de.conterra.babelfish.plugin.v10_02.feature.FeatureService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;

/**
 * defines a {@link Plugin}, which represents CSV files as a {@link FeatureService}
 *
 * @author ChrissW-R1
 * @version 0.4.0
 * @since 0.1.0
 */
@Slf4j
public class CsvPlugin
		implements Plugin {
	/**
	 * the only instance of a {@link CsvPlugin}<br>
	 * (singleton pattern)
	 *
	 * @since 0.1.0
	 */
	public static final CsvPlugin INSTANCE = new CsvPlugin();
	
	/**
	 * standard constructor
	 *
	 * @since 0.1.0
	 */
	public CsvPlugin() {
	}
	
	@Override
	public String getName() {
		return "CSV";
	}
	
	@Override
	public boolean init() {
		boolean result = true;
		
		try {
			File pluginFolder = new File(PluginAdapter.getPluginFolder(CsvPlugin.INSTANCE).toURI());
			
			for (File file : pluginFolder.listFiles()) {
				String fileName = file.getName();
				
				Reader reader = null;
				try {
					reader = new FileReader(file);
					CSVFormat.EXCEL.parse(reader);
					
					try {
						if (!(ServiceContainer.registerService(new CsvService(file))))
							result = false;
					} catch (IllegalArgumentException e) {
						log.debug("Ignore the file " + fileName + ", because it is a config file and no CSV data file.", e);
					} catch (IOException e) {
						log.warn("The configuration of " + fileName + " couldn't be loaded!", e);
						
						result = false;
					}
				} catch (IOException e) {
					log.debug("The file " + fileName + " is not a valid CSV.", e);
				}
				
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						log.warn("Couldn't close reader of file: " + fileName, e);
					}
				}
			}
		} catch (URISyntaxException e) {
			String msg = "Exception occurred: " + e.getMessage();
			log.error(msg, e);
			throw new NullPointerException(msg);
		}
		
		return result;
	}
	
	@Override
	public boolean shutdown() {
		boolean result = true;
		
		for (RestService service : ServiceContainer.getServices(this.getName())) {
			if (!(ServiceContainer.unregisterService(service)))
				result = false;
		}
		
		return result;
	}
}