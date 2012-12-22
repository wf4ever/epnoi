package epnoi.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class ActionsContext {
	private ArrayList <Action> actions;
	public ArrayList<Action> getActions() {
		return actions;
	}

	public void setActions(ArrayList<Action> actions) {
		this.actions = actions;
	}

	public ActionsContext() {
		this.actions = new ArrayList<Action>();
	
	}
	
	public void addAction(Action action){
		this.actions.add(action);
	}
	@Override
	public String toString(){
		return "AC["+this.actions+"]";
	}

}
