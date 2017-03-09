package de.conterra.babelfish.csv;

import de.conterra.babelfish.plugin.v10_02.feature.Attachment;
import de.conterra.babelfish.plugin.v10_02.feature.Feature;
import de.conterra.babelfish.plugin.v10_02.feature.Field;
import de.conterra.babelfish.plugin.v10_02.object.feature.FeatureObject;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * defines a very simple {@link Feature}
 *
 * @param <T> the type of the {@link FeatureObject}
 * @author ChrissW-R1
 * @version 0.1.0
 * @since 0.1.0
 */
public class SimpleFeature<T extends FeatureObject>
		implements Feature<T> {
	/**
	 * the {@link FeatureObject}
	 *
	 * @since 0.1.0
	 */
	private final T feature;
	
	/**
	 * constructor, with given {@link FeatureObject}
	 *
	 * @param feature the {@link FeatureObject}
	 * @since 0.1.0
	 */
	public SimpleFeature(T feature) {
		this.feature = feature;
	}
	
	@Override
	public T getFeature() {
		return this.feature;
	}
	
	@Override
	public Set<? extends Attachment> getAttachments() {
		return new LinkedHashSet<>();
	}
	
	@Override
	public SimplePopup getPopup() {
		T feature = this.getFeature();
		
		Map<? extends Field, ? extends Object> attr = feature.getAttributes();
		Map<String, String> attrString = new LinkedHashMap<>();
		for (Field key : attr.keySet())
			attrString.put(key.getName(), attr.get(key).toString());
		
		return new SimplePopup(feature.toString(), attrString);
	}
}