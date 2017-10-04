package de.conterra.babelfish.csv.layers;

import de.conterra.babelfish.csv.CsvConfig;
import de.conterra.babelfish.csv.SimpleField;
import de.conterra.babelfish.plugin.ServiceContainer;
import de.conterra.babelfish.plugin.v10_02.feature.*;
import de.conterra.babelfish.plugin.v10_02.feature.wrapper.LayerWrapper;
import de.conterra.babelfish.plugin.v10_02.object.feature.FeatureObject;
import de.conterra.babelfish.plugin.v10_02.object.feature.GeometryFeatureObject;
import de.conterra.babelfish.plugin.v10_02.object.geometry.GeometryObject;
import de.conterra.babelfish.plugin.v10_02.object.labeling.LabelingInfo;
import de.conterra.babelfish.util.DataUtils;
import de.conterra.babelfish.util.GeoUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.geotools.geometry.GeneralDirectPosition;
import org.opengis.referencing.FactoryException;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * defines {@link FeatureLayer} with represents the content of a CSV {@link File}
 *
 * @param <G> the geometry type
 * @param <F> the {@link FeatureObject} type
 * @author ChrissW-R1
 * @version 0.4.0
 * @since 0.1.0
 */
@Slf4j
public abstract class CsvLayer<G extends GeometryObject, F extends GeometryFeatureObject<G>>
		implements FeatureLayer<G, F> {
	/**
	 * the unique identifier
	 *
	 * @since 0.1.0
	 */
	private final int       id;
	/**
	 * the {@link File} to get the CSV data from
	 *
	 * @since 0.1.0
	 */
	private final File      file;
	/**
	 * the configuration (loaded from configuration file)
	 *
	 * @since 0.1.0
	 */
	private final CsvConfig config;
	/**
	 * header fields (from column headers)
	 *
	 * @since 0.1.0
	 */
	private final Map<Integer, Field> headers = new HashMap<>();
	
	/**
	 * constructor, with given id and {@link File}
	 *
	 * @param file the {@link File} to parse the CSV data from
	 * @param id   the unique identifier
	 * @throws IOException              if no configuration could loaded
	 * @throws IllegalArgumentException if the given {@link File} is a configuration {@link File} and no data {@link File}
	 * @since 0.1.0
	 */
	public CsvLayer(int id, File file)
	throws IOException, IllegalArgumentException {
		this.id = id;
		this.file = file;
		this.config = CsvConfig.getConfig(file);
		
		if (this.config.isIgnoreFirstRow()) {
			Reader reader = null;
			
			try {
				reader = new FileReader(this.file);
				Iterator<CSVRecord> records = CSVFormat.EXCEL.parse(reader).iterator();
				
				if (records.hasNext()) {
					CSVRecord record = records.next();
					
					int i = 0;
					for (String header : record) {
						if (i != this.config.getLongColumn()
						    && i != this.config.getLatColumn()
						    && i != this.config.getEleColumn()
						    && i != this.config.getCrsColumn()) {
							this.headers.put(i, new SimpleField(header, FieldType.String, "", false, 32767, null));
						}
						
						i++;
					}
				}
			} catch (IOException e) {
			}
			
			DataUtils.closeStream(reader);
		}
	}
	
	@Override
	public int getId() {
		return this.id;
	}
	
	@Override
	public String getName() {
		return ServiceContainer.toUrlSaveString(this.file.getName());
	}
	
	@Override
	public String getDescription() {
		return this.getConfig().getDescription();
	}
	
	@Override
	public String getCopyrightText() {
		return this.getConfig().getCopyright();
	}
	
	@Override
	public PopupType getPopupType() {
		return PopupType.HtmlText;
	}
	
	@Override
	public Field getObjectIdField() {
		return null;
	}
	
	@Override
	public Field getGlobalIdField() {
		return null;
	}
	
	@Override
	public Field getDisplayField() {
		return SimpleField.DISPLAY_FIELD;
	}
	
	@Override
	public Field getTypeIdField() {
		return SimpleField.REQ_TYPE_FIELD;
	}
	
	@Override
	public Set<? extends Type<F>> getSubTypes() {
		return new HashSet<>();
	}
	
	@Override
	public Set<? extends Template<F>> getTemplates() {
		return new HashSet<>();
	}
	
	@Override
	public Query<F> getQuery() {
		return null;
	}
	
	@Override
	public Map<? extends String, ? extends Image> getImages() {
		return new HashMap<>();
	}
	
	@Override
	public int getMinScale() {
		return 0;
	}
	
	@Override
	public int getMaxScale() {
		return 0;
	}
	
	@Override
	public int getTranparency() {
		return 0;
	}
	
	@Override
	public LabelingInfo getLabelingInfo() {
		return null;
	}
	
	/**
	 * gives the {@link File} to get the CSV data from
	 *
	 * @return the {@link File} to get the CSV data from
	 *
	 * @since 0.1.0
	 */
	public File getFile() {
		return this.file;
	}
	
	/**
	 * gives the configuration
	 *
	 * @return the configuration
	 *
	 * @since 0.1.0
	 */
	public CsvConfig getConfig() {
		return this.config;
	}
	
	/**
	 * gives a {@link Map} of header fields (from column headers)
	 *
	 * @return a {@link Map} of header fields (from column headers)
	 *
	 * @since 0.1.0
	 */
	public Map<Integer, Field> getHeaders() {
		return this.headers;
	}
	
	/**
	 * extracts a {@link GeneralDirectPosition} from a given {@link CSVRecord}
	 *
	 * @param record the {@link CSVRecord} to get the {@link GeneralDirectPosition} from
	 * @return the extracted {@link GeneralDirectPosition}
	 *
	 * @throws FactoryException if the CRS could decoded from the {@link CSVRecord}
	 * @since 0.1.0
	 */
	public GeneralDirectPosition getPositionFromRecord(CSVRecord record)
	throws FactoryException {
		CsvConfig config = this.getConfig();
		
		GeneralDirectPosition position = new GeneralDirectPosition(GeoUtils.decodeCrs(record.get(config.getCrsColumn())));
		position.setOrdinate(0, Double.parseDouble(record.get(config.getLatColumn())));
		position.setOrdinate(1, Double.parseDouble(record.get(config.getLongColumn())));
		
		int z = config.getEleColumn();
		if (z >= 0) {
			position.setOrdinate(2, Double.parseDouble(record.get(z)));
		}
		
		return position;
	}
	
	/**
	 * adds all meta attributes to a {@link GeometryFeatureObject}
	 *
	 * @param <T>     the geometry type
	 * @param feature the {@link GeometryFeatureObject} to add the attributes to
	 * @param record  the {@link CSVRecord} to get the attribute values from
	 * @return the same {@link GeometryFeatureObject} with the added attributes
	 *
	 * @since 0.1.0
	 */
	public <T extends GeometryObject> GeometryFeatureObject<T> addAttributes(GeometryFeatureObject<T> feature, CSVRecord record) {
		int idColumn = this.getConfig().getIdColumn();
		
		int i = 0;
		for (String cell : record) {
			if (idColumn != i) {
				Field field = this.getHeaders().get(i);
				
				if (field != null) {
					feature.addAttribute(field, cell);
				}
			} else {
				try {
					feature.addAttribute(LayerWrapper.DEFAULT_OBJECT_ID_FIELD, Integer.parseInt(cell));
				} catch (NumberFormatException e) {
					log.warn("Found a not valid object ID (" + cell + ") in column: " + idColumn, e);
				}
			}
			
			i++;
		}
		
		return feature;
	}
}
