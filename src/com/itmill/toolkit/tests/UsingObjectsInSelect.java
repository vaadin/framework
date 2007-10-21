package com.itmill.toolkit.tests;

import java.util.LinkedList;
import java.util.Random;

import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.data.Property.ValueChangeListener;
import com.itmill.toolkit.terminal.ClassResource;
import com.itmill.toolkit.ui.*;

public class UsingObjectsInSelect extends com.itmill.toolkit.Application
		implements ValueChangeListener {

	private Select select = new Select();
	private Label selectedTask = new Label("Selected task", Label.CONTENT_XHTML);

	public LinkedList exampleTasks = new LinkedList();

	public static Random random = new Random(1);

	public void init() {
		Window main = new Window("Select demo");
		setMainWindow(main);

		setTheme("corporate");

		Panel panel = new Panel("Select demo");
		panel.addComponent(select);
		Panel panel2 = new Panel("Selection");
		panel2.addComponent(selectedTask);

		select.setCaption("Select component");
		select.addListener((ValueChangeListener) this);
		select.setImmediate(true);

		main.addComponent(panel);
		main.addComponent(panel2);

		createExampleTasks();
	}

	public void createExampleTasks() {
		String[] assignedTo = new String[] { "John", "Mary", "Joe", "Sarah",
				"Jeff", "Jane", "Peter", "Marc", "Josie", "Linus" };
		String[] type = new String[] { "Enhancement", "Bugfix", "Testing",
				"Task" };
		for (int j = 0; j < 100; j++) {
			Task task = new Task(
					type[(int) (random.nextDouble() * (type.length - 1))],
					assignedTo[(int) (random.nextDouble() * (assignedTo.length - 1))],
					random.nextInt(100));
			select.addItem(task);
		}
	}

	public void valueChange(ValueChangeEvent event) {
		Task task = (Task) select.getValue();
		selectedTask.setValue("<b>Type:</b> " + task.getType()
				+ "<br /><b>Assigned to:</b> " + task.getAssignedTo()
				+ "<br /><b>Estimated hours: </b>" + task.getEstimatedHours());
	}

	/**
	 * Sample class which is bind in Toolkit components
	 * 
	 */
	public class Task {

		private String type;
		private String assignedTo;
		private int estimatedHours;

		public Task(String type, String assignedTo, int estimatedHours) {
			this.type = type;
			this.assignedTo = assignedTo;
			this.estimatedHours = estimatedHours;
		}

		public String toString() {
			return type + ", " + assignedTo;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getAssignedTo() {
			return assignedTo;
		}

		public void setAssignedTo(String assignedTo) {
			this.assignedTo = assignedTo;
		}

		public float getEstimatedHours() {
			return estimatedHours;
		}

		public void setEstimatedHours(int estimatedHours) {
			this.estimatedHours = estimatedHours;
		}
	}

}
