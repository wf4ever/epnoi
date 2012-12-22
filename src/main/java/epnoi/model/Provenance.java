package epnoi.model;

import java.util.ArrayList;
import java.util.Iterator;

public class Provenance {

	public static final String TECHNIQUE = "technique";
	public static final String TECHNIQUE_SOCIAL = "technique_social";
	public static final String TECHNIQUE_COLLABORATIVE = "technique_collaborative";
	public static final String TECHNIQUE_KEYWORD_CONTENT_BASED = "technique_keyword_content_based";
	public static final String TECHNIQUE_GROUP_CONTENT_BASED = "technique_group_content_based";
	public static final String TECHNIQUE_INFERRED = "technique_inferred";

	public static final String ITEM_TYPE = "item_type";
	public static final String ITEM_TYPE_USER = "item_type_user";
	public static final String ITEM_TYPE_WORKFLOW = "item_type_workflow";
	public static final String ITEM_TYPE_FILE = "item_type_file";
	public static final String ITEM_TYPE_PACK = "item_type_pack";
	public static final String ITEM_TYPE_EXTERNAL_RESOURCE="item_type_external_resource";

	private ArrayList<Parameter> parameters;

	// ----------------------------------------------------------------------------------------

	public Provenance() {
		this.parameters = new ArrayList<Parameter>();
	}

	// ----------------------------------------------------------------------------------------

	public ArrayList<Parameter> getParameters() {
		return parameters;
	}

	// ----------------------------------------------------------------------------------------
	
	public void setParameters(ArrayList<Parameter> parameters) {
		this.parameters = parameters;
	}
	
	// ----------------------------------------------------------------------------------------

	public String getParameterByName(String name) {
		boolean found = false;
		String parameterValue = null;
		Iterator<Parameter> parametersIt = this.parameters.iterator();
		while (parametersIt.hasNext()) {
			Parameter parameter = parametersIt.next();
			found = parameter.getName().equals(name);
			if (found) {
				parameterValue = parameter.getValue();
			}
		}
		return parameterValue;
	}
}
