package de.conterra.babelfish.csv.layers;

import de.conterra.babelfish.csv.CsvConfig;
import de.conterra.babelfish.csv.Group;
import de.conterra.babelfish.csv.SimpleFeature;
import de.conterra.babelfish.plugin.v10_02.feature.Feature;
import de.conterra.babelfish.plugin.v10_02.feature.Field;
import de.conterra.babelfish.plugin.v10_02.object.feature.GeometryFeatureObject;
import de.conterra.babelfish.plugin.v10_02.object.geometry.Multipoint;
import de.conterra.babelfish.plugin.v10_02.object.renderer.RendererObject;
import de.conterra.babelfish.plugin.v10_02.object.renderer.SimpleRenderer;
import de.conterra.babelfish.plugin.v10_02.object.symbol.PictureMarkerSymbol;
import org.geotools.geometry.iso.aggregate.MultiPointImpl;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * defines a {@link CsvLayer} of {@link Multipoint}s
 *
 * @author ChrissW-R1
 * @version 0.1.0
 * @since 0.1.0
 */
public class CsvMultiPointLayer
		extends CsvGroupedLayer<Multipoint, GeometryFeatureObject<Multipoint>> {
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
	public CsvMultiPointLayer(int id, File file)
			throws IOException, IllegalArgumentException {
		super(id, file);
		
		this.renderer = new SimpleRenderer(new PictureMarkerSymbol(this.getConfig().getPointImage()), "Content of " + this.getName());
	}
	
	@Override
	public Class<Multipoint> getGeometryType() {
		return Multipoint.class;
	}
	
	@Override
	public RendererObject getRenderer() {
		return this.renderer;
	}
	
	@Override
	public Set<? extends Feature<GeometryFeatureObject<Multipoint>>> getFeatures() {
		Set<Feature<GeometryFeatureObject<Multipoint>>> result = new LinkedHashSet<>();
		
		for (Group group : this.getGroups()) {
			GeometryFeatureObject<Multipoint> multiPoint = new GeometryFeatureObject<>(new Multipoint(new MultiPointImpl(group.getCrs(), new LinkedHashSet<>(group.getPoints()))));
			
			Map<? extends Field, ? extends String> attributes = group.getAttributes();
			for (Field field : attributes.keySet())
				multiPoint.addAttribute(field, attributes.get(field));
			
			result.add(new SimpleFeature<>(multiPoint));
		}
		
		return result;
	}
}