package de.conterra.babelfish.csv;

import de.conterra.babelfish.plugin.v10_02.feature.Popup;
import de.conterra.babelfish.plugin.v10_02.feature.PopupType;

import java.util.HashMap;
import java.util.Map;

/**
 * defines a very simple {@link Popup}
 *
 * @author ChrissW-R1
 * @version 0.1.0
 * @since 0.1.0
 */
public class SimplePopup
		implements Popup {
	/**
	 * the name of an object to show in the {@link Popup}
	 *
	 * @since 0.1.0
	 */
	private final String objectName;
	/**
	 * a {@link Map} of all attributes to show in a table
	 *
	 * @since 0.1.0
	 */
	private final Map<String, String> attributes = new HashMap<>();
	
	/**
	 * constructor, with given object name
	 *
	 * @param objectName the name of an object to show in the {@link Popup}
	 * @since 0.1.0
	 */
	public SimplePopup(String objectName) {
		this.objectName = objectName;
	}
	
	/**
	 * constructor, with given object name and attributes
	 *
	 * @param objectName the name of an object to show in the {@link Popup}
	 * @param attr       the attributes to show in a table
	 * @since 0.1.0
	 */
	public SimplePopup(String objectName, Map<? extends String, ? extends String> attr) {
		this(objectName);
		
		this.attributes.putAll(attr);
	}
	
	@Override
	public PopupType getType() {
		return PopupType.HtmlText;
	}
	
	@Override
	public String getContent() {
		String result = "<html>"
				+ " <body>"
				+ "  <h2>Object-Info</h2>"
				+ "  <p>Object: <b>" + this.objectName + "</b></p>"
				+ "  <table>"
				+ "   <tr>"
				+ "    <th>key</th>"
				+ "    <th>value</th>"
				+ "   </tr>";
		
		for (String key : this.attributes.keySet())
			result += "   <tr>"
					+ "    <td>" + key + "</td>"
					+ "    <td>" + this.attributes.get(key) + "</td>"
					+ "   </tr>";
		
		result += "  </table>"
				+ " </body>"
				+ "</html>";
		
		return result;
	}
}