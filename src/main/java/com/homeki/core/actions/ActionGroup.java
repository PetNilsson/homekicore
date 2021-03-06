package com.homeki.core.actions;

import com.homeki.core.triggers.Trigger;
import org.hibernate.Session;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
public class ActionGroup extends Action {
	@OneToMany(mappedBy = "action", orphanRemoval = true)
	@Cascade({ CascadeType.SAVE_UPDATE, CascadeType.DELETE })
	@LazyCollection(LazyCollectionOption.EXTRA)
	private List<Action> actions;
	
	@OneToMany(mappedBy = "actionGroup")
	@LazyCollection(LazyCollectionOption.EXTRA)
	private Set<Trigger> triggers;

	@Column
	private String name;

	@Column
	private String description;
	
	@Column
	private boolean explicit;
	
	public ActionGroup() {
		this.actions = new ArrayList<>();
		this.description = "";
	}
	
	public void addAction(Action action) {
		action.setParent(this);
		actions.add(action);
	}
	
	public void deleteAction(Action action) {
		action.setParent(null);
		actions.remove(action);
	}
	
	@Override
	public void execute(Session ses) {
		for (Action a : actions)
			a.execute(ses);
	}
	
	public List<Action> getActions() {
		return actions;
	}

	@Override
	public String getType() {
		return "actiongroup";
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public boolean isExplicit() {
		return explicit;
	}
	
	public void setExplicit(boolean explicit) {
		this.explicit = explicit;
	}
}
