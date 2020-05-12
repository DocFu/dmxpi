package de.plasmawolke.dmxpi.qlc;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.atomic.AtomicBoolean;

public class VirtualConsoleButton {

	public static final String CHANGE_PROPERTY_NAME = "VirtualConsoleButtonState";

	private PropertyChangeSupport support;

	private Integer id;

	private String name;

	private AtomicBoolean state = new AtomicBoolean(false);

	public VirtualConsoleButton() {
		support = new PropertyChangeSupport(this);
	}

	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		support.addPropertyChangeListener(pcl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		support.removePropertyChangeListener(pcl);
	}

	public void updateState(boolean newState) {

		if (state.compareAndSet(!newState, newState)) {
			support.firePropertyChange(CHANGE_PROPERTY_NAME, !newState, newState);
		}

	}

	/**
	 * @return the state
	 */
	public boolean getState() {
		return state.get();
	}

	/**
	 * @return the id
	 */
	public final Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public final void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public final void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VirtualConsoleButton other = (VirtualConsoleButton) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "VirtualConsoleButton (" + id + ", '" + name + "')";
	}

}
