package epnoi.model;

public class Parameter {
	String name;
	String value;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString(){
		return "P["+this.name+", "+this.value+"]";
	}

}
