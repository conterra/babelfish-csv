package de.conterra.babelfish.csv.layers;

import de.conterra.babelfish.csv.CsvConfig;
import de.conterra.babelfish.csv.Group;
import de.conterra.babelfish.plugin.v10_02.feature.Field;
import de.conterra.babelfish.plugin.v10_02.object.feature.FeatureObject;
import de.conterra.babelfish.plugin.v10_02.object.feature.GeometryFeatureObject;
import de.conterra.babelfish.plugin.v10_02.object.geometry.GeometryObject;
import de.conterra.babelfish.plugin.v10_02.object.geometry.Point;
import de.conterra.babelfish.util.DataUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.geotools.geometry.iso.primitive.PointImpl;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

/**
 * defines a {@link CsvLayer}, which grouped all features by the {@link CsvConfig#getGroupColumn()}s
 *
 * @param <G> the geometry type
 * @param <F> the {@link FeatureObject} type
 * @author ChrissW-R1
 * @version 0.4.0
 * @since 0.1.0
 */
@Slf4j
public abstract class CsvGroupedLayer<G extends GeometryObject, F extends GeometryFeatureObject<G>>
		extends CsvLayer<G, F> {
	/**
	 * constructor, with given id and {@link File}
	 *
	 * @param id   the unique identifier
	 * @param file the {@link File} to parse the CSV data from
	 * @throws IOException              if no {@link CsvConfig} could loaded
	 * @throws IllegalArgumentException if the given {@link File} is a configuration {@link File} and no data {@link File}
	 * @since 0.1.0
	 */
	public CsvGroupedLayer(int id, File file)
	throws IOException, IllegalArgumentException {
		super(id, file);
	}
	
	/**
	 * gives a {@link Set} of all {@link Group}s in a {@link CsvGroupedLayer}
	 *
	 * @return a {@link Set} of all {@link Group}s in the given {@link CsvGroupedLayer}
	 *
	 * @since 0.1.0
	 */
	public Set<? extends Group> getGroups() {
		Set<Group> result = new LinkedHashSet<>();
		
		Map<String, Set<GeometryFeatureObject<Point>>> groups   = new HashMap<>();
		File                                           file     = this.getFile();
		String                                         fileName = file.getName();
		CsvConfig                                      config   = this.getConfig();
		
		Reader reader = null;
		try {
			reader = new FileReader(file);
			Iterator<CSVRecord> records = CSVFormat.EXCEL.parse(reader).iterator();
			
			if (config.isIgnoreFirstRow() && records.hasNext()) {
				records.next();
			}
			
			while (records.hasNext()) {
				CSVRecord record = records.next();
				String    group  = "";
				
				for (int col : config.getGroupColumn()) {
					group += record.get(col) + ",";
				}
				
				while (group.endsWith(",")) {
					group = group.substring(0, group.length() - 1);
				}
				
				Set<GeometryFeatureObject<Point>> groupSet;
				if (groups.containsKey(group)) {
					groupSet = groups.get(group);
				} else {
					groupSet = new LinkedHashSet<>();
				}
				
				try {
					groupSet.add(this.addAttributes(new GeometryFeatureObject<Point>(new Point(new PointImpl(this.getPositionFromRecord(record)))), record));
					
					groups.put(group, groupSet);
					
					log.debug("Added new feature to group " + group + ".");
				} catch (FactoryException e) {
					log.warn("Couldn't create CRS by decoding given string!", e);
				}
			}
		} catch (IOException e) {
			log.error("An error occurred on reading the CSV file " + fileName + "!", e);
		}
		
		DataUtils.closeStream(reader);
		
		for (String group : groups.keySet()) {
			Set<org.opengis.geometry.primitive.Point> points     = new LinkedHashSet<>();
			Map<Field, Set<String>>                   attributes = new HashMap<>();
			CoordinateReferenceSystem                 crs        = null;
			
			for (GeometryFeatureObject<Point> feature : groups.get(group)) {
				Point point = feature.getGeometry();
				points.add(point);
				
				Map<? extends Field, ?> attrs = feature.getAttributes();
				for (Field field : attrs.keySet()) {
					Object obj = attrs.get(field);
					if (obj != null && obj instanceof String) {
						String attr = (String) obj;
						
						if (!(attr.isEmpty())) {
							Set<String> set;
							
							if (attributes.containsKey(field)) {
								set = attributes.get(field);
							} else {
								set = new LinkedHashSet<>();
							}
							
							set.add(attr);
							
							attributes.put(field, set);
						}
					}
				}
				
				if (crs == null) {
					CoordinateReferenceSystem pointCrs = point.getCoordinateReferenceSystem();
					if (pointCrs != null) {
						crs = pointCrs;
					}
				}
			}
			
			Map<Field, String> attrMap = new HashMap<>();
			for (Field field : attributes.keySet()) {
				String attrValue = "";
				for (String attr : attributes.get(field)) {
					attrValue += attr + Group.DELIMITER;
				}
				
				while (attrValue.endsWith(Group.DELIMITER)) {
					attrValue = attrValue.substring(0, attrValue.length() - Group.DELIMITER.length());
				}
				
				attrMap.put(field, attrValue);
			}
			
			if (crs != null) {
				result.add(new Group(points, attrMap, crs));
			} else {
				log.warn("Couldn't create group, because no valid CRS was found!");
			}
		}
		
		return result;
	}
}
