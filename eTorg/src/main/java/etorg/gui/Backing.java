package etorg.gui;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.validator.ValidatorException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Generalized abstrack backing bean that includes convenience methods and so on.
 * 
 * @author Hans Reier Sigmond
 *
 */
public abstract class Backing {
	
	/**
	 * 
	 * Return the backing bean given the name  of the bean.
	 * e.g. getBackingBean(Bean.class); 
	 * 
	 * Note: This method assumes that there is a fixed relationship between the bean class and bean name:
	 * Bean.class => bean (name).
	 * 
	 * This is not always true!?
	 * 
	 * Note: If no such bean exists, calling the bean will create it.
	 * If the bean is not a session or application bean, special care must be taken.
	 * E.g. Do not assign values to a request bean if not used in context of a JSF page.
	 * 
	 * @param <T>		Any bean class.
	 * @param beanClass The class of the bean.
	 * @return T 		Backing bean instance of any type.
	 */	

	/**
	 * This string stores the most recent error message from Javascript
	 * Note: A JSF hidden field must be used to get values back from Javascript
	 */
	private String javaScriptError="";
	
	protected String getJavaScriptError() {
		return javaScriptError;
	}

	protected void setJavaScriptError(String javaScriptError) {
		this.javaScriptError = javaScriptError;
	}

	/**
	 * Include logback
	 */
	protected static final Logger log = LoggerFactory.getLogger(Backing.class);
	
	protected <T> T getBackingBean(Class<T> beanClass) {
		FacesContext fc= FacesContext.getCurrentInstance();
		String s= beanClass.getSimpleName();
		s=s.substring(0,1).toLowerCase()+s.substring(1);
		return fc.getApplication().evaluateExpressionGet(fc,"#{"+s+"}", beanClass); 
	}
	
	
	/**
	 * 
	 * Return the backing bean given the name and class of the bean.
	 * E.g. getBackingBean("bean", Bean.class). 
	 * 
	 * Note: If no such bean exists, calling the bean will create it.
	 * If the bean is not a session or application bean, special care must be taken.
	 * E.g. Do not assign values to a request bean if not used in context of a JSF page.
	 * 
	 * @param <T>			Any bean class.
	 * @param beanName		Name of the bean.
	 * @param beanClass		The class of the bean.
	 * @return T			Backing bean instance of any type.
	 */
	protected <T> T getBackingBean(String beanName, Class<T> beanClass) {
		FacesContext fc= FacesContext.getCurrentInstance();
		return fc.getApplication().evaluateExpressionGet(fc,"#{"+beanName+"}", beanClass); 
	}
	
	/**
	 *
	 * Check if there is a rendered response from the current request.
	 *
	 * @return 			true if rendered response 
	 */
	protected boolean getRenderResponse() {
		return FacesContext.getCurrentInstance().getRenderResponse();
	}
	
	
	/**
	 * 
	 * Get the JSF Flash. Example of usage: User can be any object instance
	 *  
	 * 	getFlash().put("user",user);
	 *	getFlash().keep("user"); Often required after put.
	 *  getFlash().get("user");
	 *  <h:outputText value="#{flash.user.userId}"	/>	
	 *  
	 *  HRS: 25092010 Note: Do not use this unless you are 100% certain of what you are doing.
	 *  I am not the only one complaining about this. Read on the internet..
	 *  I tried to use the Flash to keep "logged in user" boolean values through several request/response cycles 
	 *  using a request scoped bean. I  used more than 4 hours and gave it up.
	 *  Flash requires that you have 100% understanding of the JSF request/response phases:
	 *  - What happens in each JSF phase.
	 *  - When you can retrieve values from the Flash.
	 *  - How the values are kept (using keep...)
	 *  - When the Flash is flushed.
	 *  
	 * @return		Flash.
	 */
	protected Flash getFlash() {
		return FacesContext.getCurrentInstance().getExternalContext().getFlash();
	}
	
	/**
	 * 
	 * Return message according to locale (language) from the message files.
	 * 
	 * @param key	Enter key to be found in localized message files.
	 * @param parameters	{n} where n is a number will be substituted with parameters
	 * @return		Returns message or message not found message.
	 */
	protected String getMessage(String key, String ... parameters) {
		FacesContext fc= FacesContext.getCurrentInstance();
		//get resource bundle with current locale (as defined in faces-config)
		ResourceBundle bundle = ResourceBundle.getBundle("messages",fc.getViewRoot().getLocale());
		try	{
			String msg = bundle.getString(key);	
			return MessageFormat.format(msg, (Object[]) parameters); 
		}
		catch(MissingResourceException e){
			return "message with key: " + key + " not found";		
		}
	}
	
	/**
	 * 
	 * Return resource according to locale (language) from the message files.
	 * 
	 * @param key	Enter key to be found in localized resource files.
	 * @param parameters	{n} where n is a number will be substituted with parameters
	 * @return		Returns resource or resource not found message.
	 */
	protected String getResource(String key, String ... parameters) {
		FacesContext fc= FacesContext.getCurrentInstance();
		//get resource bundle with current locale (as defined in faces-config)
		ResourceBundle bundle = ResourceBundle.getBundle("resources",fc.getViewRoot().getLocale());
		try	{
			String msg = bundle.getString(key);	
			return MessageFormat.format(msg, (Object[]) parameters); 
		}
		catch(MissingResourceException e){
			return "resource with key: " + key + " not found";		
		}
	}
	
	/**
	 * 
	 * Display message according to locale (language) from the message files.
	 * 
	 * @param key			Enter key to be found in localized message files.
	 * @param severity		The severity of the error
	 * @param parameters	{n} where n is a number will be substituted with parameters
	 */
	protected void displayMessage(String key, Severity severity, String ... parameters) {
		FacesContext fc= FacesContext.getCurrentInstance();
		String msg = getMessage(key, parameters);
	    
		if (severity == FacesMessage.SEVERITY_INFO) {
			log.info(msg);
			fc.addMessage(null, new FacesMessage(severity,getMessage("info"),msg));
		}
		if (severity == FacesMessage.SEVERITY_WARN) {
			log.warn(msg);
			fc.addMessage(null, new FacesMessage(severity,getMessage("warn"),msg));
		}
		if (severity == FacesMessage.SEVERITY_ERROR) {
			log.error(msg);
			fc.addMessage(null, new FacesMessage(severity,getMessage("error"),msg));
		}
		if (severity == FacesMessage.SEVERITY_FATAL) {
			log.error("JSF fatal error: "+ msg);
			fc.addMessage(null, new FacesMessage(severity,getMessage("fatal"),msg));
		}	
	}
	
	/**
	 * The purpose is to parse the error string returned by hibernate
	 * to be able to output localized and customized error messages.
	 * 
	 * @param s					The complete hibernate error string.
	 * @param params			The list of parameters extracted from the hibernate error string.
	 * 							The parameters to search for are enclosed in ' ' in the hibernate error string. 
	 * 							Parameters found in the error string are added to params.
	 * @return String			The error message string without the parameters.
	 */
	protected String parseHibernateError(String s, List<String> params) {
		
		if (params == null) params = new ArrayList<String>(); //Just in case forgotten
		int iStart, iEnd;
		String result="";
		iStart=s.indexOf("'");
		while (iStart >= 0) {
			if (iStart>0) result = result + s.substring(0,iStart-1);
			s = s.substring(iStart+1);
			iEnd =s.indexOf("'");
			if (iEnd >=0) {
				params.add(s.substring(0,iEnd));
				s = s.substring(iEnd+1);
			}
			iStart = s.indexOf("'");
		}
		result = result+s;
		return result;
	}
	
	/**
	 *	Stupid Java with no possibilites to pass more than one return parameter
	 */
	class HibernateError {
		private String hibernateErrorKey;
		private Severity severity;
	}
	
	/**
	 * The purpose is to search the parsed hibernate string for keywords
	 * and return the correct error message key including parameters.
	 * The key is used to obtain localized messages from the messages files.
	 * I am sorry to say that the only way to do this is to search for text.
	 * (Can the JDBC error numbers be used as an alternative)?
	 * This is not good in a maintenance perpective if the hibernate error messages changes.
	 * If several matches, the first is returned.
	 * 
	 * @param hibernateError						    The Hibernate error.
	 * @param params									The list of parameters returned to be used in the error message.
	 * 													Parameters found in the error string are added to params.
	 * @return	HibernateErr							The Hibernate error found: Containing error message key and severity.
	 */
	
	protected HibernateError findHibernateError(String hibernateError, List<String> params) {
		HibernateError hibernateErr = new HibernateError();
		if (params == null) params = new ArrayList<String>(); //Just in case forgotten
		List<String> p = new ArrayList<String>();
		String parsed = parseHibernateError(hibernateError, p);  //Parse hibernate error	
		
		/* Duplicate user during logon */
		if (parsed.contains("Duplicate entry for key")) {
			if ((p != null) && (p.size() > 0)) {
				params.add(p.get(0)); //return user name
			}
			hibernateErr.hibernateErrorKey = "duplicateUser";
			hibernateErr.severity = FacesMessage.SEVERITY_ERROR;
			return hibernateErr;
		}
		
		/* Access denied for system user defined in hibernate_mysql.properties */
		if (parsed.contains("Access denied for user")) {
			hibernateErr.hibernateErrorKey = "systemAccessDenied";
			hibernateErr.severity = FacesMessage.SEVERITY_FATAL;
			return hibernateErr;
		}
		
		/* Cannot find database defined in hibernatge_mysql.properties */
		if (parsed.contains("Unknown database")) {
			if ((p != null) && (p.size() > 0)) {
				params.add(p.get(0)); //return database name
			}
			hibernateErr.hibernateErrorKey = "unknownDatabase";
			hibernateErr.severity = FacesMessage.SEVERITY_FATAL;
			return hibernateErr;
		}
		
		return null;
	}
	
	/**
	 * Display a hibernate error caught in a JSF backing bean in the GUI.
	 * 
	 * This is a preliminary implementation.
	 * Look for hibernateError in the messages files and provide language support.
	 * Attempt is made to seach for specific text string and e.g. user information enclosed in ' '.
	 * If this text string is found, this will be translated to specific error message stored in the messages files.
	 * If the text is found, no more search is performed (recursion is aborted).
	 * If the text string is not found, the error is considered to be a generic (unrecoverable) hibernateErrror (as defined in the messsages files).
	 * Note: The hibernateError will always have english text.
	 * Multiple levels of errors are supported.
	 *  
	 * Error message levels in JSF 2.0 (Error, Warning, ..) are ignored in this implementation.
	 * 
	 * 
	 * @param e			Exception.
	 *
	 * @return			boolean(TRUE):   A specific error is not found in the messages files, display the entire hibernate message.
	 *                  boolean (FALSE): Display the specific error message(s) found.
	 *                  Note: The main purpose is transfer this flag when returning from a recursive call.
	 */
	protected boolean displayDaoError(Throwable e) {
		FacesContext fc= FacesContext.getCurrentInstance();
		String msg = getMessage("hibernateError");
		String hibernateError;
		boolean b = true; //The value of b is kept through the return from recursion
		List<String> params = new ArrayList<String>();
		HibernateError foundError = null;
		hibernateError = e.getMessage();
		if (!(e instanceof NullPointerException)) {
			if (hibernateError != null) {
				foundError = findHibernateError(hibernateError, params);
				if (foundError != null) b = false; // set to false if a specific error is found in the messages fils
			}	
			// No recursion if the error message are found or if no more message levels exist.
			// To allow for multiple error messages requires a rewrite:
			// E.g. inner recursive function and transfer b in the method call,
			// in addition to logic to prevent the same message to be displaid twice.
			if (b && e.getCause()!= null) {
				// return true if error message are found in messages files
				// this will prevent "hibernateError" to be displaid
				// and only print the error message found in messages file
				b = displayDaoError(e.getCause()); //recursive	
			}	
	    
			if (foundError == null)  {
				// display hibernate error as JSF message if no JSF message(s) are found
				// these errors will always be in english and are in principle unwanted (except for debugging)
				if (b) {
					if (e.getStackTrace()!= null) {
						log.error(msg+": "+hibernateError);
						for (StackTraceElement st: e.getStackTrace())   log.error(st.toString());
						fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, getMessage("fatal"), msg+": "+hibernateError));
					}
					else  {
						log.error(msg+": "+hibernateError);
						fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, getMessage("error"), msg + ": "+hibernateError));
					}	
				}
			}
			else  {
			// display JSF message
				displayMessage(foundError.hibernateErrorKey, foundError.severity, params.toArray(new String[params.size()]));
			}	
		}
		//Should really not happen, an unknown FATAL error is found and even hibernate cannot determine what it is
		if (hibernateError == null || e instanceof NullPointerException) {
			// display as JSF message
			hibernateError = e.getClass().toString();
			msg = getMessage("fatalHibernateError");
			log.error(msg+" " + hibernateError);
			if (e.getStackTrace()!= null) {
				for (StackTraceElement st: e.getStackTrace())   log.error(st.toString());
			}	
			fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, getMessage("fatal"), msg+": "+hibernateError));
		} 	
		return b;
	}
	
	/**
	 * Validate based on resulting error message from Javascript
	 * This error message must be put in hidden field for JSF to reach it.
	 * 
	 * @param context				State information for JSF request.
	 * @param toValidate			UI component to validat	
	 * @param errorMessage			Error message from Javascript, empty if none
	 * @throws ValidatorException	Validation failed.
	 */
	public void javascriptValidator(FacesContext context, UIComponent toValidate, 
		Object errorMessage) throws ValidatorException {
		String error = (String) errorMessage;
		if (error.length() != 0 ) {
			// Set javascript error for use by another validator
			javaScriptError = error;
		}
	}
	
	/**
	 * Validate using regular expressions
	 * 
	 * Note: I tried to use the regex validator in JSF directly, 
	 * but did not manage to get proper error handling using it.
	 * 
	 * Note: This validator can fetch error caught in Javascipt using a hidden field and a string fetched from the above javascriptValidator.
	 * In this demo code both Javascript and Java are using the same regular expression matching, 
	 * so it is really no point in doing it if not for showing how it could be done.
	 * 
	 * <h:inputHidden id="emailError" value="#{customerBacking.emailError}" validator="#{customerBacking.javascriptValidator}" />  
	 * <h:inputText id="email"	label="#{res.emailLabel}" value="#{customerBacking.customer.email}"  onchange="changed()"          
	 *		onblur="validateRegex('email',/#{res.emailRegex}/,'#{msg.emailError}', '#{res.emailLabel}', 'emailError')"             
	 *	    validator="#{customerBacking.regexValidator}">                                                                         
	 * 
	 * @param context	 			State information for JSF request.
	 * @param toValidate 			UI component to validate.
	 * @param value		 			Value to validate.
	 * @throws ValidatorException	Validation failed.
	 * 
	 **/
	public void regexValidator(FacesContext context, UIComponent toValidate, 
		Object value) throws ValidatorException {
		String field = (String) value;
		String javaScriptError = getJavaScriptError();
		Map<String,Object> attributes = toValidate.getAttributes();
		
		// get label or id if the label is not found
		String label = (String) attributes.get("label");
		if (label == null) label = toValidate.getId();
		// if no spesific error message attribute exists for the field
		// use the standard error message (with label inserted) 
		// else use the one found, eventually with label inserted as parameter in the message
		String pattern = (String) attributes.get("regex");
		String msg = (String) attributes.get("regexError");
		if (msg == null) 
			msg = (String) getMessage("regexValidationError", label);
		else
			msg = MessageFormat.format(msg, (Object) label);
		// find if the field matches the regular expression
		boolean javaError = !field.matches(pattern);
		// java error or javascript error detected
		// javascript error must be fetched in separate validator called before this validator
		if (javaError || javaScriptError != "") {
			List<FacesMessage> facesMessages = new ArrayList<FacesMessage>();
			log.error(msg);
			String err = getMessage("error");
			// do not output duplicate error messages
			if ((javaScriptError != "") && (!(err+" "+ msg).equals(javaScriptError) || !javaError)) {
				facesMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, err, javaScriptError));	
			}	
			if (javaError)
				facesMessages.add(new FacesMessage(FacesMessage.SEVERITY_ERROR, err, msg));
			setJavaScriptError("");
			throw new ValidatorException(facesMessages);
		}
		setJavaScriptError("");
	} 
	
	/**
	 * Validate because of problems with entering a too long string in h:inputTextarea
	 * Assumed max lenght: 255 characters (because of database storage issues)
	 * 
	 * @param context				State information for JSF request.
	 * @param toValidate			UI component to validate	.
	 * @param value					The value of the input text area field.
	 * @throws ValidatorException	Validation failed.
	 */
	public void inputTextareaValidator(FacesContext context, UIComponent toValidate, 
		Object value) throws ValidatorException {
		int length = value.toString().length();
		String err = getMessage("error");
		String detail = getMessage("stringTooLong", Integer.toString(length));
		if (length > 255) {
			FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, err, detail);
			throw new ValidatorException(facesMessage);
		}
	}
	
	
	/**
	 * This routine finds the current language setting.
	 * If done in constructor, this will be the default as defined by browser settings.
	 * 
	 * @return 	The current language setting.
	 */
	protected String getCurrentLocale() {
		FacesContext fc= FacesContext.getCurrentInstance();
		return fc.getViewRoot().getLocale().toString();
	}

}
