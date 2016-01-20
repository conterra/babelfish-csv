package de.conterra.babelfish.csv;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.opengis.geometry.primitive.Point;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.conterra.babelfish.plugin.v10_02.feature.Field;
import de.conterra.babelfish.plugin.v10_02.object.feature.GeometryFeatureObject;

/**
 * defines a group of {@link GeometryFeatureObject}s to create a geometry of its
 * {@link Point}s
 * 
 * @version 0.1
 * @author chwe
 * @since 0.1
 */
public class Group
{
	/**
	 * the {@link Logger} of this class
	 * 
	 * @since 0.1
	 */
	public static final Logger LOGGER = LoggerFactory.getLogger(Group.class);
	
	/**
	 * the delimiter of attribute values
	 * 
	 * @since 0.1
	 */
	public static final String DELIMITER = ";";
	
	/**
	 * the {@link Point}s to create a geometry from
	 * 
	 * @since 0.1
	 */
	private final Set<Point> points = new LinkedHashSet<>();
	/**
	 * the attributes add to the geometry
	 * 
	 * @since 0.1
	 */
	private final Map<Field, String> attributes = new HashMap<>();
	/**
	 * the {@link CoordinateReferenceSystem} of the geometry
	 * 
	 * @since 0.1
	 */
	private final CoordinateReferenceSystem crs;
	
	/**
	 * standard constructor
	 * 
	 * @since 0.1
	 * 
	 * @param points the {@link Point}s to create a geometry from
	 * @param attributes the attributes add to the geometry
	 * @param crs the {@link CoordinateReferenceSystem} of the geometry
	 */
	public Group(Set<? extends Point> points, Map<? extends Field, ? extends String> attributes, CoordinateReferenceSystem crs)
	{
		this.points.addAll(points);
		this.attributes.putAll(attributes);
		this.crs = crs;
	}
	
	/**
	 * gives the {@link Point}s to create a geometry from
	 * 
	 * @since 0.1
	 * 
	 * @return a set of all {@link Point}s to create a geometry from
	 */
	public Set<? extends Point> getPoints()
	{
		return new LinkedHashSet<>(this.points);
	}
	
	/**
	 * gives the attributes add to the geometry
	 * 
	 * @since 0.1
	 * 
	 * @return the attributes add to the geometry
	 */
	public Map<? extends Field, ? extends String> getAttributes()
	{
		return new HashMap<>(this.attributes);
	}
	
	/**
	 * gives the {@link CoordinateReferenceSystem} of the geometry
	 * 
	 * @since 0.1
	 * 
	 * @return the {@link CoordinateReferenceSystem} of the geometry
	 */
	public CoordinateReferenceSystem getCrs()
	{
		return this.crs;
	}
}