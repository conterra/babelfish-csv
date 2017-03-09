package de.conterra.babelfish.csv;

import de.conterra.babelfish.plugin.v10_02.feature.Field;
import de.conterra.babelfish.plugin.v10_02.object.feature.GeometryFeatureObject;
import org.opengis.geometry.primitive.Point;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * defines a group of {@link GeometryFeatureObject}s to create a geometry of its {@link Point}s
 *
 * @author ChrissW-R1
 * @version 0.4.0
 * @since 0.1.0
 */
public class Group {
	/**
	 * the delimiter of attribute values
	 *
	 * @since 0.1.0
	 */
	public static final String DELIMITER = ";";
	
	/**
	 * the {@link Point}s to create a geometry from
	 *
	 * @since 0.1.0
	 */
	private final Set<Point> points = new LinkedHashSet<>();
	/**
	 * the attributes add to the geometry
	 *
	 * @since 0.1.0
	 */
	private final Map<Field, String> attributes = new HashMap<>();
	/**
	 * the {@link CoordinateReferenceSystem} of the geometry
	 *
	 * @since 0.1.0
	 */
	private final CoordinateReferenceSystem crs;
	
	/**
	 * standard constructor
	 *
	 * @param points     the {@link Point}s to create a geometry from
	 * @param attributes the attributes add to the geometry
	 * @param crs        the {@link CoordinateReferenceSystem} of the geometry
	 * @since 0.1.0
	 */
	public Group(Set<? extends Point> points, Map<? extends Field, ? extends String> attributes, CoordinateReferenceSystem crs) {
		this.points.addAll(points);
		this.attributes.putAll(attributes);
		this.crs = crs;
	}
	
	/**
	 * gives the {@link Point}s to create a geometry from
	 *
	 * @return a set of all {@link Point}s to create a geometry from
	 *
	 * @since 0.1.0
	 */
	public Set<? extends Point> getPoints() {
		return new LinkedHashSet<>(this.points);
	}
	
	/**
	 * gives the attributes add to the geometry
	 *
	 * @return the attributes add to the geometry
	 *
	 * @since 0.1.0
	 */
	public Map<? extends Field, ? extends String> getAttributes() {
		return new HashMap<>(this.attributes);
	}
	
	/**
	 * gives the {@link CoordinateReferenceSystem} of the geometry
	 *
	 * @return the {@link CoordinateReferenceSystem} of the geometry
	 *
	 * @since 0.1.0
	 */
	public CoordinateReferenceSystem getCrs() {
		return this.crs;
	}
}