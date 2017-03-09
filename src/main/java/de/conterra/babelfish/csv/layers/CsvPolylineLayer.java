package de.conterra.babelfish.csv.layers;

import de.conterra.babelfish.csv.CsvConfig;
import de.conterra.babelfish.csv.Group;
import de.conterra.babelfish.csv.SimpleFeature;
import de.conterra.babelfish.plugin.v10_02.feature.Feature;
import de.conterra.babelfish.plugin.v10_02.feature.Field;
import de.conterra.babelfish.plugin.v10_02.object.feature.GeometryFeatureObject;
import de.conterra.babelfish.plugin.v10_02.object.geometry.Polyline;
import de.conterra.babelfish.plugin.v10_02.object.renderer.RendererObject;
import de.conterra.babelfish.plugin.v10_02.object.renderer.SimpleRenderer;
import de.conterra.babelfish.plugin.v10_02.object.symbol.SimpleLineSymbol;
import de.conterra.babelfish.plugin.v10_02.object.symbol.style.SLSStyle;
import lombok.extern.slf4j.Slf4j;
import org.geotools.geometry.iso.coordinate.LineStringImpl;
import org.opengis.geometry.coordinate.Position;
import org.opengis.geometry.primitive.Point;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * defines a {@link CsvLayer} of {@link Polyline}s
 *
 * @author ChrissW-R1
 * @version 0.4.0
 * @since 0.1.0
 */
@Slf4j
public class CsvPolylineLayer
		extends CsvGroupedLayer<Polyline, GeometryFeatureObject<Polyline>> {
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
	public CsvPolylineLayer(int id, File file)
			throws IOException, IllegalArgumentException {
		super(id, file);
		
		CsvConfig config = this.getConfig();
		this.renderer = new SimpleRenderer(new SimpleLineSymbol(SLSStyle.Solid, config.getLineColor(), config.getLineStroke()), "Content of " + this.getName());
	}
	
	@Override
	public Class<Polyline> getGeometryType() {
		return Polyline.class;
	}
	
	@Override
	public RendererObject getRenderer() {
		return this.renderer;
	}
	
	@Override
	public Set<? extends Feature<GeometryFeatureObject<Polyline>>> getFeatures() {
		Set<Feature<GeometryFeatureObject<Polyline>>> result = new LinkedHashSet<>();
		
		for (Group group : this.getGroups()) {
			Set<? extends Point> points = group.getPoints();
			if (points.size() >= 2) {
				GeometryFeatureObject<Polyline> polyline = new GeometryFeatureObject<>(new Polyline(new LineStringImpl(new LinkedList<Position>(points))));
				
				Map<? extends Field, ? extends String> attributes = group.getAttributes();
				for (Field field : attributes.keySet())
					polyline.addAttribute(field, attributes.get(field));
				
				result.add(new SimpleFeature<>(polyline));
			} else
				log.warn("The group has less than 2 points! However, at least two points are needed for a line!");
		}
		
		return result;
	}
}