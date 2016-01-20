package de.conterra.babelfish.csv.layers;

import java.awt.Image;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.geotools.geometry.GeneralDirectPosition;
import org.opengis.referencing.FactoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.conterra.babelfish.csv.CsvConfig;
import de.conterra.babelfish.csv.SimpleField;
import de.conterra.babelfish.plugin.ServiceContainer;
import de.conterra.babelfish.plugin.v10_02.feature.FeatureLayer;
import de.conterra.babelfish.plugin.v10_02.feature.Field;
import de.conterra.babelfish.plugin.v10_02.feature.FieldType;
import de.conterra.babelfish.plugin.v10_02.feature.PopupType;
import de.conterra.babelfish.plugin.v10_02.feature.Query;
import de.conterra.babelfish.plugin.v10_02.feature.Template;
import de.conterra.babelfish.plugin.v10_02.feature.Type;
import de.conterra.babelfish.plugin.v10_02.feature.wrapper.LayerWrapper;
import de.conterra.babelfish.plugin.v10_02.object.feature.FeatureObject;
import de.conterra.babelfish.plugin.v10_02.object.feature.GeometryFeatureObject;
import de.conterra.babelfish.plugin.v10_02.object.geometry.GeometryObject;
import de.conterra.babelfish.plugin.v10_02.object.labeling.LabelingInfo;
import de.conterra.babelfish.util.GeoUtils;

/**
 * defines {@link FeatureLayer} with represents the content of a CSV
 * {@link File}
 * 
 * @version 0.2.4
 * @author chwe
 * @since 0.1
 * 
 * @param <G> the geometry type
 * @param <F> the {@link FeatureObject} type
 */
public abstract class CsvLayer<G extends GeometryObject, F extends GeometryFeatureObject<G>>
implements FeatureLayer<G, F>
{
	/**
	 * the {@link Logger} of this class
	 * 
	 * @since 0.1
	 */
	public static final Logger LOGGER = LoggerFactory.getLogger(CsvLayer.class);
	
	/**
	 * the unique identifier
	 * 
	 * @since 0.1
	 */
	private final int id;
	/**
	 * the {@link File} to get the CSV data from
	 * 
	 * @since 0.1
	 */
	private final File file;
	/**
	 * the configuration (loaded from configuration file)
	 * 
	 * @since 0.1
	 */
	private final CsvConfig config;
	/**
	 * header fields (from column headers)
	 * 
	 * @since 0.1
	 */
	private final Map<Integer, Field> headers = new HashMap<>();
	
	/**
	 * constructor, with given id and {@link File}
	 * 
	 * @since 0.1
	 * 
	 * @param file the {@link File} to parse the CSV data from
	 * @param id the unique identifier
	 * @throws IOException if no configuration could loaded
	 * @throws IllegalArgumentException if the given {@link File} is a
	 *         configuration {@link File} and no data {@link File}
	 */
	public CsvLayer(int id, File file)
	throws IOException, IllegalArgumentException
	{
		this.id = id;
		this.file = file;
		this.config = CsvConfig.getConfig(file);
		
		if (this.config.isIgnoreFirstRow())
		{
			Reader reader = null;
			
			try
			{
				reader = new FileReader(this.file);
				Iterator<CSVRecord> records = CSVFormat.EXCEL.parse(reader).iterator();
				
				if (records.hasNext())
				{
					CSVRecord record = records.next();
					
					int i = 0;
					for (String header : record)
					{
						if (i != this.config.getLongColumn()
						&& i != this.config.getLatColumn()
						&& i != this.config.getEleColumn()
						&& i != this.config.getCrsColumn())
							this.headers.put(i, new SimpleField(header, FieldType.String, "", false, 32767, null));
						
						i++;
					}
				}
			}
			catch (IOException e)
			{
			}
			
			try
			{
				reader.close();
			}
			catch (NullPointerException e)
			{
			}
			catch (IOException e)
			{
				CsvLayer.LOGGER.warn("Couldn't close reader of file: " + file.getName(), e);
			}
		}
	}
	
	@Override
	public int getId()
	{
		return this.id;
	}
	
	@Override
	public String getName()
	{
		return ServiceContainer.toUrlSaveString(this.file.getName());
	}
	
	@Override
	public String getDescription()
	{
		return this.getConfig().getDescription();
	}
	
	@Override
	public String getCopyrightText()
	{
		return this.getConfig().getCopyright();
	}
	
	@Override
	public PopupType getPopupType()
	{
		return PopupType.HtmlText;
	}
	
	@Override
	public Field getObjectIdField()
	{
		return null;
	}
	
	@Override
	public Field getGlobalIdField()
	{
		return null;
	}
	
	@Override
	public Field getDisplayField()
	{
		return SimpleField.DISPLAY_FIELD;
	}
	
	@Override
	public Field getTypeIdField()
	{
		return SimpleField.REQ_TYPE_FIELD;
	}
	
	@Override
	public Set<? extends Type<F>> getSubTypes()
	{
		return new HashSet<>();
	}
	
	@Override
	public Set<? extends Template<F>> getTemplates()
	{
		return new HashSet<>();
	}
	
	@Override
	public Query<F> getQuery()
	{
		return null;
	}
	
	@Override
	public Map<? extends String, ? extends Image> getImages()
	{
		return new HashMap<>();
	}
	
	@Override
	public int getMinScale()
	{
		return 0;
	}
	
	@Override
	public int getMaxScale()
	{
		return 0;
	}
	
	@Override
	public int getTranparency()
	{
		return 0;
	}
	
	@Override
	public LabelingInfo getLabelingInfo()
	{
		return null;
	}
	
	/**
	 * gives the {@link File} to get the CSV data from
	 * 
	 * @since 0.1
	 * 
	 * @return the {@link File} to get the CSV data from
	 */
	public File getFile()
	{
		return this.file;
	}
	
	/**
	 * gives the configuration
	 * 
	 * @since 0.1
	 * 
	 * @return the configuration
	 */
	public CsvConfig getConfig()
	{
		return this.config;
	}
	
	/**
	 * gives a {@link Map} of header fields (from column headers)
	 * 
	 * @since 0.1
	 * 
	 * @return a {@link Map} of header fields (from column headers)
	 */
	public Map<Integer, Field> getHeaders()
	{
		return this.headers;
	}
	
	/**
	 * extracts a {@link GeneralDirectPosition} from a given {@link CSVRecord}
	 * 
	 * @since 0.1
	 * 
	 * @param record the {@link CSVRecord} to get the
	 *        {@link GeneralDirectPosition} from
	 * @return the extracted {@link GeneralDirectPosition}
	 * @throws FactoryException if the CRS could decoded from the
	 *         {@link CSVRecord}
	 */
	public GeneralDirectPosition getPositionFromRecord(CSVRecord record)
	throws FactoryException
	{
		CsvConfig config = this.getConfig();
		
		GeneralDirectPosition position = new GeneralDirectPosition(GeoUtils.decodeCrs(record.get(config.getCrsColumn())));
		position.setOrdinate(0, Double.parseDouble(record.get(config.getLatColumn())));
		position.setOrdinate(1, Double.parseDouble(record.get(config.getLongColumn())));
		
		int z = config.getEleColumn();
		if (z >= 0)
			position.setOrdinate(2, Double.parseDouble(record.get(z)));
		
		return position;
	}
	
	/**
	 * adds all meta attributes to a {@link GeometryFeatureObject}
	 * 
	 * @since 0.1
	 * 
	 * @param <T> the geometry type
	 * @param feature the {@link GeometryFeatureObject} to add the attributes to
	 * @param record the {@link CSVRecord} to get the attribute values from
	 * @return the same {@link GeometryFeatureObject} with the added attributes
	 */
	public <T extends GeometryObject> GeometryFeatureObject<T> addAttributes(GeometryFeatureObject<T> feature, CSVRecord record)
	{
		int idColumn = this.getConfig().getIdColumn();
		
		int i = 0;
		for (String cell : record)
		{
			if (idColumn != i)
			{
				Field field = this.getHeaders().get(i);
				
				if (field != null)
					feature.addAttribute(field, cell);
			}
			else
			{
				try
				{
					feature.addAttribute(LayerWrapper.DEFAULT_OBJECT_ID_FIELD, Integer.parseInt(cell));
				}
				catch (NumberFormatException e)
				{
					CsvLayer.LOGGER.warn("Found a not valid object ID (" + cell + ") in column: " + idColumn, e);
				}
			}
			
			i++;
		}
		
		return feature;
	}
}