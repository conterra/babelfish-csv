package de.conterra.babelfish.csv.layers;

import de.conterra.babelfish.csv.CsvConfig;
import de.conterra.babelfish.csv.SimpleFeature;
import de.conterra.babelfish.plugin.v10_02.feature.Feature;
import de.conterra.babelfish.plugin.v10_02.object.feature.GeometryFeatureObject;
import de.conterra.babelfish.plugin.v10_02.object.geometry.Point;
import de.conterra.babelfish.plugin.v10_02.object.renderer.RendererObject;
import de.conterra.babelfish.plugin.v10_02.object.renderer.SimpleRenderer;
import de.conterra.babelfish.plugin.v10_02.object.symbol.PictureMarkerSymbol;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.geotools.geometry.iso.primitive.PointImpl;
import org.opengis.referencing.FactoryException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * defines {@link CsvLayer} of {@link Point}s
 *
 * @author ChrissW-R1
 * @version 0.4.0
 * @since 0.1.0
 */
@Slf4j
public class CsvPointLayer
		extends CsvLayer<Point, GeometryFeatureObject<Point>> {
	/**
	 * the {@link RendererObject}
	 *
	 * @since 0.1.0
	 */
	private final SimpleRenderer renderer;
	
	/**
	 * constructor, with given id and {@link File}
	 *
	 * @param id   the unique identifier
	 * @param file the {@link File} to parse the CSV data from
	 * @throws IOException              if no {@link CsvConfig} could loaded
	 * @throws IllegalArgumentException if the given {@link File} is a configuration {@link File} and no data {@link File}
	 * @see CsvLayer#CsvLayer(int, File)
	 * @since 0.1.0
	 */
	public CsvPointLayer(int id, File file)
			throws IOException, IllegalArgumentException {
		super(id, file);
		
		this.renderer = new SimpleRenderer(new PictureMarkerSymbol(this.getConfig().getPointImage()), "Content of " + this.getName());
	}
	
	@Override
	public Class<Point> getGeometryType() {
		return Point.class;
	}
	
	@Override
	public RendererObject getRenderer() {
		return this.renderer;
	}
	
	@Override
	public Set<? extends Feature<GeometryFeatureObject<Point>>> getFeatures() {
		Set<Feature<GeometryFeatureObject<Point>>> result = new LinkedHashSet<>();
		
		File file = this.getFile();
		String fileName = file.getName();
		
		Reader reader = null;
		try {
			reader = new FileReader(file);
			Iterator<CSVRecord> records = CSVFormat.EXCEL.parse(reader).iterator();
			
			if (this.getConfig().isIgnoreFirstRow() && records.hasNext())
				records.next();
			
			while (records.hasNext()) {
				CSVRecord record = records.next();
				
				try {
					result.add(new SimpleFeature<GeometryFeatureObject<Point>>(this.addAttributes(new GeometryFeatureObject<Point>(new Point(new PointImpl(this.getPositionFromRecord(record)))), record)));
				} catch (FactoryException e) {
					log.warn("Couldn't create a point, because the CRS couldn't be decoded!", e);
				}
			}
		} catch (IOException e) {
			log.error("An error occurred on reading the CSV file " + fileName + "!", e);
		}
		
		try {
			reader.close();
		} catch (NullPointerException e) {
		} catch (IOException e) {
			log.warn("Couldn't close the reader of CSV file: " + fileName, e);
		}
		
		return result;
	}
}