/*
Copyright (C) 2022 Cardiff

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.

*/

package org.dcom.core.servicehelper;
import java.util.HashMap;
import org.dcom.core.DCOM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.text.WordUtils;

/**
*
*This is a programmatic representation of the basic metadata a service holds about itself.
*/

public class ServiceBaseInfo {

	public static String NAME = "DCOM_SERVICE_NAME";
	public static String DESCRIPTION = "DCOM_SERVICE_DESCRIPTION";
	public static String OPERATOR = "DCOM_SERVICE_OPERATOR";
	public static String SECURITY_SERVICE_TYPE = "DCOM_SERVICE_SECURITY_SERVICE_TYPE";
	public static String SECURITY_SERVICE_URI = "DCOM_SERVICE_SECURITY_SERVICE_URI";
	public static String HOSTNAME = "DCOM_SERVICE_HOSTNAME";
	public static String PORT = "DCOM_SERVICE_PORT";

	private HashMap<String,String> properties;
	
	private static final Logger LOGGER = LoggerFactory.getLogger( ServiceBaseInfo.class );

	public ServiceBaseInfo(String... requiredItems){
			this();
			for (String item: requiredItems) {
						if (DCOM.existsEnvironmentVariable(item)) {
								LOGGER.info("Loading "+item);
								properties.put(translateName(item),DCOM.getEnvironmentVariable(item));
						} else {
								LOGGER.error("Environment Variable "+item+" not set!");
								System.exit(1);
						}
			}
	}
	
	public ServiceBaseInfo() {
			properties=new HashMap<String,String>();
	}
	
	public void setProperty(String name,String value) {
			properties.put(translateName(name),value);
	}
	
	private String translateName(String extName) {
		String name=extName.replace("DCOM_SERVICE","");
		name=WordUtils.capitalizeFully(name, '_').replaceAll("_", "");
		return name;
	}
	
	public String getProperty(String name) {
		return properties.get(translateName(name));
	}
	
	public String toJSON() {
			StringBuffer str=new StringBuffer();
			str.append("{");
			str.append(toJSONContent());
			str.append("}");
			return str.toString();
	}
	
	
	public String toXMLContent() {
		StringBuffer str=new StringBuffer();
		for (String item : properties.keySet()) {
				str.append("<").append(item).append(">").append(properties.get(item)).append("</").append(item).append(">");
		}
		return str.toString();
		
	}
	
	public String toJSONContent() {
		StringBuffer str=new StringBuffer();
		boolean first=true;
		for (String item : properties.keySet()) {
				if (first) first=false;
				else str.append(",");
				str.append("\"").append(item).append("\":\"").append(properties.get(item)).append("\"");
		}
		return str.toString();
	}
	
	public String toXML() {
		StringBuffer str=new StringBuffer();
		str.append("<ServerIdentity>");
		str.append(toXMLContent());
		str.append("</ServerIdentity>");
		return str.toString();
	}
}