package com.homeki.core.triggers;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.Session;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import com.homeki.core.actions.Action;
import com.homeki.core.actions.ActionGroup;
import com.homeki.core.conditions.Condition;
import com.homeki.core.conditions.ConditionGroup;
import com.homeki.core.events.Event;


@Entity
public class Trigger {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@Cascade({ CascadeType.SAVE_UPDATE, CascadeType.DELETE })
	@JoinColumn(name = "condition_group_id")
	private ConditionGroup conditionGroup;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@Cascade({ CascadeType.SAVE_UPDATE, CascadeType.DELETE })
	@JoinColumn(name = "action_group_id")
	private ActionGroup actionGroup;
	
	@Column
	private String name;
	
	public Trigger() {
		this.conditionGroup = new ConditionGroup();
		this.actionGroup = new ActionGroup();
	}
	
	public Trigger(Condition condition, Action action) {
		this();
		this.conditionGroup.addCondition(condition);
		this.actionGroup.addAction(action);
	}

	public boolean update(Event e) {
		return conditionGroup.update(e);
	}
	
	public boolean isFulfilled() {
		return conditionGroup.isFulfilled();
	}
	
	public void execute(Session ses) {
		actionGroup.execute(ses);
	}
	
	public int getId() {
		return id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void addCondition(Condition condition) {
		conditionGroup.addCondition(condition);
	}
	
	public void deleteCondition(Condition condition) {
		conditionGroup.deleteCondition(condition);
	}
	
	public void addAction(Action action) {
		actionGroup.addAction(action);
	}
	
	public List<Condition> getConditions() {
		return conditionGroup.getConditions();
	}
	
	public List<Action> getActions() {
		return actionGroup.getActions();
	}

	public void deleteAction(Action action) {
		actionGroup.deleteAction(action);
	}
}
