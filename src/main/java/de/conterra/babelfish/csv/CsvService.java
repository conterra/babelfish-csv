package de.conterra.babelfish.csv;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import de.conterra.babelfish.csv.layers.CsvLayer;
import de.conterra.babelfish.csv.layers.CsvMultiPointLayer;
import de.conterra.babelfish.csv.layers.CsvPointLayer;
import de.conterra.babelfish.csv.layers.CsvPolygonLayer;
import de.conterra.babelfish.csv.layers.CsvPolylineLayer;
import de.conterra.babelfish.plugin.Plugin;
import de.conterra.babelfish.plugin.v10_02.feature.FeatureLayer;
import de.conterra.babelfish.plugin.v10_02.feature.FeatureService;
import de.conterra.babelfish.plugin.v10_02.feature.Layer;
import de.conterra.babelfish.plugin.v10_02.feature.Relationship;
import de.conterra.babelfish.plugin.v10_02.feature.Table;
import de.conterra.babelfish.plugin.v10_02.object.feature.FeatureObject;
import de.conterra.babelfish.plugin.v10_02.object.feature.GeometryFeatureObject;
import de.conterra.babelfish.plugin.v10_02.object.geometry.GeometryObject;
import de.conterra.babelfish.plugin.v10_02.object.geometry.Multipoint;
import de.conterra.babelfish.plugin.v10_02.object.geometry.Polygon;
import de.conterra.babelfish.plugin.v10_02.object.geometry.Polyline;

/**
 * defines a {@link FeatureService}, with one {@link FeatureLayer} of a CSV
 * {@link File}
 * 
 * @version 0.1.1
 * @author chwe
 * @since 0.1
 */
public class CsvService
implements FeatureService
{
	/**
	 * the layer of the CSV {@link File}
	 * 
	 * @since 0.1
	 */
	private final CsvLayer<? extends GeometryObject, ? extends GeometryFeatureObject<? extends GeometryObject>> layer;
	
	/**
	 * standard constructor
	 * 
	 * @since 0.1
	 * 
	 * @param file the {@link File} to get the CSV data from
	 * @throws IOException if the configuration couldn't be loaded
	 * @throws IllegalArgumentException if the given {@link File} is a
	 *         configuration {@link File} and no data {@link File}
	 */
	public CsvService(File file)
	throws IOException, IllegalArgumentException
	{
		Class<? extends GeometryObject> geoType = CsvConfig.getConfig(file).getGeoType();
		
		if (Multipoint.class.isAssignableFrom(geoType))
			this.layer = new CsvMultiPointLayer(0, file);
		else if (Polygon.class.isAssignableFrom(geoType))
			this.layer = new CsvPolygonLayer(0, file);
		else if (Polyline.class.isAssignableFrom(geoType))
			this.layer = new CsvPolylineLayer(0, file);
		else
			this.layer = new CsvPointLayer(0, file);
	}
	
	@Override
	public Image getIcon()
	{
		return null;
	}
	
	@Override
	public String getServiceDescription()
	{
		return this.layer.getDescription();
	}
	
	@Override
	public Set<? extends Layer<? extends FeatureObject>> getLayers()
	{
		return this.getFeatureLayers();
	}
	
	@Override
	public Set<? extends FeatureLayer<?, ? extends GeometryFeatureObject<?>>> getFeatureLayers()
	{
		Set<CsvLayer<?, ? extends GeometryFeatureObject<?>>> result = new LinkedHashSet<>();
		
		CsvLayer<?, ? extends GeometryFeatureObject<?>> layer = this.layer;
		result.add(layer);
		
		return result;
	}
	
	@Override
	public Set<? extends Table<? extends FeatureObject>> getTables()
	{
		return new LinkedHashSet<>();
	}
	
	@Override
	public Set<? extends Relationship<? extends FeatureObject, ? extends FeatureObject>> getRelationships()
	{
		return new HashSet<>();
	}
	
	@Override
	public Plugin getPlugin()
	{
		return CsvPlugin.INSTANCE;
	}
	
	@Override
	public String getId()
	{
		return this.layer.getName();
	}
}