package de.conterra.babelfish.csv.layers;

import de.conterra.babelfish.csv.CsvConfig;
import de.conterra.babelfish.csv.Group;
import de.conterra.babelfish.csv.SimpleFeature;
import de.conterra.babelfish.plugin.v10_02.feature.Feature;
import de.conterra.babelfish.plugin.v10_02.feature.Field;
import de.conterra.babelfish.plugin.v10_02.object.feature.GeometryFeatureObject;
import de.conterra.babelfish.plugin.v10_02.object.geometry.Polygon;
import de.conterra.babelfish.plugin.v10_02.object.renderer.RendererObject;
import de.conterra.babelfish.plugin.v10_02.object.renderer.SimpleRenderer;
import de.conterra.babelfish.plugin.v10_02.object.symbol.SimpleFillSymbol;
import de.conterra.babelfish.plugin.v10_02.object.symbol.SimpleLineSymbol;
import de.conterra.babelfish.plugin.v10_02.object.symbol.style.SFSStyle;
import de.conterra.babelfish.plugin.v10_02.object.symbol.style.SLSStyle;
import de.conterra.babelfish.util.GeoUtils;
import lombok.extern.slf4j.Slf4j;
import org.geotools.geometry.iso.coordinate.PolygonImpl;
import org.geotools.geometry.iso.primitive.SurfaceBoundaryImpl;
import org.opengis.geometry.primitive.Point;
import org.opengis.geometry.primitive.Ring;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * defines a {@link CsvLayer} of {@link Polygon}s
 *
 * @author ChrissW-R1
 * @version 0.4.0
 * @since 0.1.0
 */
@Slf4j
public class CsvPolygonLayer
		extends CsvGroupedLayer<Polygon, GeometryFeatureObject<Polygon>> {
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
	public CsvPolygonLayer(int id, File file)
			throws IOException, IllegalArgumentException {
		super(id, file);
		
		CsvConfig config = this.getConfig();
		this.renderer = new SimpleRenderer(new SimpleFillSymbol(SFSStyle.Solid, config.getFillColor(), new SimpleLineSymbol(SLSStyle.Solid, config.getLineColor(), config.getLineStroke())), "Content of " + this.getName());
	}
	
	@Override
	public Class<Polygon> getGeometryType() {
		return Polygon.class;
	}
	
	@Override
	public RendererObject getRenderer() {
		return this.renderer;
	}
	
	@Override
	public Set<? extends Feature<GeometryFeatureObject<Polygon>>> getFeatures() {
		Set<Feature<GeometryFeatureObject<Polygon>>> result = new LinkedHashSet<>();
		
		for (Group group : this.getGroups()) {
			Set<? extends Point> points = group.getPoints();
			if (points.size() >= 3) {
				GeometryFeatureObject<Polygon> polygon = new GeometryFeatureObject<>(new Polygon(new PolygonImpl(new SurfaceBoundaryImpl(group.getCrs(), GeoUtils.createRing(points.toArray(new Point[points.size()])), new LinkedList<Ring>()))));
				
				Map<? extends Field, ? extends String> attributes = group.getAttributes();
				for (Field field : attributes.keySet())
					polygon.addAttribute(field, attributes.get(field));
				
				result.add(new SimpleFeature<>(polygon));
			} else
				log.warn("The group has less than 3 points! However, at least three points are needed for a polygon!");
		}
		
		return result;
	}
}