package etorg.gui;

import java.util.Map;

import javax.faces.context.FacesContext;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

/**
 * 
 * The purpose of this is to implement a view scope in Spring 3.0 as there is in JSF 2.0.
 * This seems to work. Note: HRS: This is not my code, seems to work after testing.
 * 
 * ETorg backing beens works in Session, View and Request scope.
 * Note: The shopping basket (order) must be in Session scope,
 * since this is set up to work without a database.
 * This bean are/can be used to store any kind of persistent data.
 * 
 * The alternative to use view scope is to rewrite eTorg to only use the
 * JSF container in presentation and the Spring container in the other layers.
 * This gives a cleaner separation of the layers, but adds complexity.
 * 
 * Note: Testing shows that @PREDESTROY is not called,
 * does that mean that the scope is not flushed as is should be?
 * or only that @PREDESTROY does not work in this case.
 * 
 * No guarantees!
 * 
 *  @author cagataycivici 
 *  @see <a href="http://cagataycivici.wordpress.com/2010/02/17/port-jsf-2-0s-viewscope-to-spring-3-0"> article</a>

 */
public class ViewScope implements Scope {
	@SuppressWarnings("rawtypes")
	public Object get(String name,  ObjectFactory objectFactory) {
		Map<String, Object> viewMap = FacesContext.getCurrentInstance()
				.getViewRoot().getViewMap();

		if (viewMap.containsKey(name)) {
			return viewMap.get(name);
		} else {
			Object object = objectFactory.getObject();
			viewMap.put(name, object);

			return object;
		}
	}

	public Object remove(String name) {
		return FacesContext.getCurrentInstance().getViewRoot().getViewMap()
				.remove(name);
	}

	public String getConversationId() {
		return null;
	}

	public void registerDestructionCallback(String name, Runnable callback) {
		// Not supported
	}

	public Object resolveContextualObject(String key) {
		return null;
	}
}
