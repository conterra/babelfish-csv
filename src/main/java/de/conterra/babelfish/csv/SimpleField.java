package de.conterra.babelfish.csv;

import de.conterra.babelfish.plugin.v10_02.feature.Field;
import de.conterra.babelfish.plugin.v10_02.feature.FieldType;
import de.conterra.babelfish.plugin.v10_02.object.domain.DomainObject;

/**
 * defines a very simple {@link Field}
 *
 * @author ChrissW-R1
 * @version 0.1.0
 * @since 0.1.0
 */
public class SimpleField
		implements Field {
	/**
	 * describes the object type
	 *
	 * @since 0.1.0
	 */
	public static final Field TYPE_FIELD = new SimpleField("type", FieldType.String, "", false, 50, null);
	/**
	 * describes the object name
	 *
	 * @since 0.1.0
	 */
	public static final Field NAME_FIELD = new SimpleField("name", FieldType.String, "", false, 50, null);
	/**
	 * describes the display field
	 *
	 * @since 0.1.0
	 */
	public static final Field DISPLAY_FIELD = new SimpleField("req_id", FieldType.String, "Request ID", true, 20, null);
	/**
	 * describes the type field
	 *
	 * @since 0.1.0
	 */
	public static final Field REQ_TYPE_FIELD = new SimpleField("req_type", FieldType.String, "Request Type", true, 40, null);
	
	/**
	 * the name
	 *
	 * @since 0.1.0
	 */
	private final String name;
	/**
	 * the {@link FieldType}
	 *
	 * @since 0.1.0
	 */
	private final FieldType type;
	/**
	 * the alias
	 *
	 * @since 0.1.0
	 */
	private final String alias;
	/**
	 * is it editable?
	 *
	 * @since 0.1.0
	 */
	private final boolean editable;
	/**
	 * the field length
	 *
	 * @since 0.1.0
	 */
	private final int length;
	/**
	 * the {@link DomainObject}
	 *
	 * @since 0.1.0
	 */
	private final DomainObject domain;
	
	/**
	 * constructor, with all necessary attributes
	 *
	 * @param name     the name
	 * @param type     the {@link FieldType}
	 * @param alias    the alias
	 * @param editable is it editable?
	 * @param length   the field length
	 * @param domain   the {@link DomainObject}
	 * @since 0.1.0
	 */
	public SimpleField(String name, FieldType type, String alias, boolean editable, int length, DomainObject domain) {
		this.name = name;
		this.type = type;
		this.alias = alias;
		this.editable = editable;
		this.length = length;
		this.domain = domain;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public FieldType getType() {
		return this.type;
	}
	
	@Override
	public String getAlias() {
		return this.alias;
	}
	
	@Override
	public int getLength() {
		return this.length;
	}
	
	@Override
	public boolean isEditable() {
		return this.editable;
	}
	
	@Override
	public DomainObject getDomain() {
		return this.domain;
	}
}