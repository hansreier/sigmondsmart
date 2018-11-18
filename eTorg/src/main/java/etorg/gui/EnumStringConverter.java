package etorg.gui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * This converter adds the possibility to output language dependent enums.
 * The enums must be stored in the messages*.properties files.
 * The keys must defined in capital letters like "RECEIVED" "SENT".
 * 
 * In this version only output is supported. 
 * 
 * @author Hans Reier Sigmond
 *
 */
@FacesConverter(value = "enumStringConverter")
public class EnumStringConverter implements Converter {
	
	/* 
	 * Output converter of enums.
	 */
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (context == null) {
		throw new NullPointerException("context");
		}
		if (component == null) {
		throw new NullPointerException("component");
		}
        if (value == null) {
            return "";
        }
		String key = ((Enum<?>)value).name();
		//get resource bundle with current locale (as defined in faces-config)
		ResourceBundle bundle=ResourceBundle.getBundle("resources",context.getViewRoot().getLocale());
		try	{
			return bundle.getString(key);		
		}
		catch(MissingResourceException e){
			return key;	
		}
}
	/* 
	 * Input converter of enums (not implemented, but have to be provided).
	 */
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		return null;
	}
}