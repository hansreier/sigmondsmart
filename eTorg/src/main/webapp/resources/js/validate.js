/**
 * 
 */

// replace parameters with text
String.prototype.format = function() { 
	var args = arguments;
		return this.replace(/\{(\d+)\}/g, function() {
				return args[arguments[1]];
		});
}; 

//A field is changed
function changed() {
	notchanged = false;
}

//Remove leading and trailing blanks
function removeBlanks(fld) {
	var res = "";
	var c = 0;
	for (i=0; i<fld.length; i++) {
	  if (fld.charAt(i) != " " || c > 0) {
	    res += fld.charAt(i);
	    if (fld.charAt(i) != " ") c = res.length;
	    }
	  }
	return res.substr(0,c);
}


// Display message where the JSF Messages is put
function displayMessage(Message) {
	if (Message == null) Message = "";
	var msgs = document.getElementById("mss");
	var table = document.createElement("TABLE");
	var tbody = document.createElement("TBODY");
	var tr = document.createElement("TR");
	var td = document.createElement("TD");
	var tx = document.createTextNode(Message);
	td.appendChild(tx);
	tr.appendChild(td);
	tbody.appendChild(tr);
	table.appendChild(tbody);
	table.setAttribute("id","mss");
	table.className="message";
	msgs.parentNode.replaceChild(table,msgs);
}

// check if field is empty and display error message
function empty(value, fieldLabel) {
	var required = msgRequired;
	if (fieldLabel != "") required = required.format(fieldLabel);
	msg = "";
	if (value == "") {
		msg = required;
		displayMessage(msg);
		return true;
	}
	return false;
}

/****************************************************************
/ Validate a field using regex
/
/  validateField = id of field to validate
/  regex = regex string: Have to be surrounded by //: JSF example: /#{res.zipCodeRegex}/ )
/  errorString = error message (fetched from messages file)
/  fieldLabel = label of the field to validate
/  required = 'required'; field is required
/  errorField = id of the hidden field to be passed to Java/JSF validation, empty: No transfer of  error mesage 
/
/ Note: Must use double backslashes in file
/****************************************************************/
function validateRegex(validateField, regex, errorString, fieldLabel, required, errorField) {
	var field = document.getElementById(validateField);
	if (fieldLabel == null) fieldLabel="";
	if (required == null) required="";
	if (errorField == null) {
		errorField = "";
		// Allow for errorField to be entered in required, if no required is set
		if (required != "required") errorField = required;
	}
	if (errorField != "") {	
		errorField = document.getElementById(errorField);
		errorField.value = "";
	}
	field.value = removeBlanks(field.value);
	errorField.value = "";
	
	// check if required field is empty, display message and exit if it is
	if (required == "required") {
		if (empty(field.value, fieldLabel)) return;
	}
		
	// check if the value of the field matches the regex expression
	var fieldString = field.value;
	if (fieldLabel != "") errorString = errorString.format(fieldLabel);
	msg = "";
	if (fieldString.length > 0) {
		if (!fieldString.match(regex)) {
			msg = errorString;
			if (errorField != "") errorField.value = errorString;
		}
	}
	displayMessage(msg);
}

/****************************************************************
/ Validate long range (corresponds to the JSF validation)
/
/  validateField = id of field to validate
/  fieldLabel = label of the field to validate
/  minimum = minimum value
/  maximum = maximum value
/  errorString = error message (fetched from messages file)
/  required = 'required'; field is required
/
****************************************************************/
function validateLongRange(validateField, fieldLabel, minimum, maximum, required) {
	var field = document.getElementById(validateField);
	if (required == null) required = "";
	if (maximum == null) maximum = "";
	if (minimum == null) minimum = "";
	if (fieldLabel == null) fieldLabel="";;
	field.value = removeBlanks(field.value);
	var fieldString = field.value;
	var msg="";
	
	// check if required field is empty, display message and exit if it is
	if (required == "required") {
		if (empty(field.value, fieldLabel)) return;
	}
	
	// check for valid integer number (somewhat narrower check than JSF)
	if (!field.value.match(integerRegex)) {
		msg = msgInteger;
		if (fieldLabel != "") msg = msg.format(fieldLabel);
	}
	
	// check for minimum value
	if (minimum != "") {
		min = parseInt (minimum);
		if (field.value < min) {
			msg = msgMinimum;
			if (fieldLabel != "") msg = msg.format(minimum, fieldLabel);
		}
	}
	
	// check for maximum value
	if (maximum != "") {
		max = parseInt (maximum);
		if (field.value > max) {
			msg = msgMaximum;
			if (fieldLabel != "") msg = msg.format(maximum, fieldLabel);
		}
	}	
	displayMessage(msg);
}

/****************************************************************
/ Validate a required field
/
/  validateField = id of field to validate
/  fieldLabel = label of the field to validate
/
****************************************************************/
function validateRequired(validateField, fieldLabel) {
	if (fieldLabel == null) fieldLabel="";
	var field = document.getElementById(validateField);
	field.value = removeBlanks(field.value);
	if (!empty(field.value, fieldLabel)) displayMessage("");
}

/****************************************************************
/ Logg of function
/
****************************************************************/
function logoff() {
	var logoutbutton = msgLogoff;
	if (!notchanged) {
		if (!confirm(logoutbutton)) {
			return false;
		} 
	} 
	return true;
}

/****************************************************************
/ Display if javascript is enabled
/
/  jsEnabledText = text to output
/  If no javascript function is never called and JSF original text is kept
/  Note: The java variable order.javascriptEnabled does not work at initization time
/  of eTorg because a request has to be done to server to set it.
/
****************************************************************/
function displayJavascriptEnabled(jsEnabledText) {
	document.getElementById("jsenabled").innerHTML=jsEnabledText;
}
