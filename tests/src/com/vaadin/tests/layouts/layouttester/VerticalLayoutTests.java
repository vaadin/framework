package com.vaadin.tests.layouts.layouttester;

import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.SystemError;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Link;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Select;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

public class VerticalLayoutTests extends AbstractLayoutTests {

	private Application application;

	public VerticalLayoutTests(Application application) {
		super();
		this.application = application;
	}

	
	
	@Override
	protected Layout getAlignmentTests() {
		VerticalLayout vlo = getTestLaytout();
		AbstractComponent[] components = new AbstractComponent[9];
		Alignment[] alignments = new Alignment[] { Alignment.BOTTOM_CENTER,
				Alignment.BOTTOM_LEFT, Alignment.BOTTOM_RIGHT,
				Alignment.MIDDLE_CENTER, Alignment.MIDDLE_LEFT,
				Alignment.MIDDLE_RIGHT, Alignment.TOP_CENTER,
				Alignment.TOP_LEFT, Alignment.TOP_RIGHT };

		vlo.addComponent(new Label("<hr />", Label.CONTENT_XHTML));
		for (int i = 0; i < components.length; i++) {
			components[i] = new TextField();
			((TextField) components[i]).setValue("FIELD " + i);
			vlo.addComponent(components[i]);
			vlo.setComponentAlignment(components[i], alignments[i]);
			vlo.addComponent(new Label("<hr />", Label.CONTENT_XHTML));
		}
		return vlo;
	}

	@Override
	protected Layout getCaptionsTests() {
		VerticalLayout vlo = getTestLaytout();
		AbstractComponent component = null;

		String[] captions = new String[] {
				"",
				"abcdefghijklmnopq",
				"abc def hij klm nop qrs tuv xyz qaz wsx edc rfv tgb yhn ujm mko nji bhu vgy cft cde"};

		vlo.addComponent(new Label("<hr />", Label.CONTENT_XHTML));
		for (int i = 0; i < captions.length; i++) {
			component = new TextField();
			((TextField) component).setValue("FIELD " + i);
			component.setCaption(captions[i]);
			vlo.addComponent(component);
		}
		vlo.addComponent(new Label("<hr />", Label.CONTENT_XHTML));
		for (int i = 0; i < captions.length; i++) {
			component = new Label();
			((Label) component).setValue("Label " + i);
			component.setCaption(captions[i]);
			vlo.addComponent(component);
		}
		vlo.addComponent(new Label("<hr />", Label.CONTENT_XHTML));
		for (int i = 0; i < captions.length; i++) {
			component = new Select();
			component.setCaption(captions[i]);
			component.setIcon(new ClassResource("help.png", application));
			vlo.addComponent(component);
		}
		vlo.addComponent(new Label("<hr />", Label.CONTENT_XHTML));
		for (int i = 0; i < captions.length; i++) {
			component = getTestTabsheet();
			component.setCaption(captions[i]);
			component.setComponentError(new UserError("component error, user error"));
			vlo.addComponent(component);
		}
		return vlo;
	}

	@Override
	protected Layout getComponentAddReplaceMoveTests() {
		final VerticalLayout vlo = getTestLaytout();
		vlo.addComponent(new Label("<hr />", Label.CONTENT_XHTML));

		final HorizontalLayout source = new HorizontalLayout();
		source.addComponent(new Label("OTHER LABEL 1"));
		source.addComponent(new Label("OTHER LABEL 2"));

		final AbstractComponent c1 = new Button("BUTTON");
		final AbstractComponent c2 = new Label("<b>LABEL</b>", Label.CONTENT_XHTML);
		final AbstractComponent c3 = new Table("TABLE");
		c3.setHeight("100px");
		c3.setWidth("100%");

		final Button addButton = new Button("Test add");
		final Button replaceButton = new Button("Test replace");
		final Button moveButton = new Button("Test move");
		final Button removeButton = new Button("Test remove");

		replaceButton.setEnabled(false);
		moveButton.setEnabled(false);
		removeButton.setEnabled(false);

		addButton.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 7716267156088629379L;

			@Override
			public void buttonClick(ClickEvent event) {
				vlo.addComponent(new TextField());
				addButton.setEnabled(false);
				replaceButton.setEnabled(true);
			}
		});
		replaceButton.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 7716267156088629379L;

			@Override
			public void buttonClick(ClickEvent event) {
				vlo.replaceComponent(c1, c3);
				replaceButton.setEnabled(false);
				moveButton.setEnabled(true);
			}
		});
		moveButton.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 7716267156088629379L;

			@Override
			public void buttonClick(ClickEvent event) {
				vlo.moveComponentsFrom(source);
				moveButton.setEnabled(false);
				removeButton.setEnabled(true);
			}
		});
		removeButton.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 7716267156088629379L;

			@Override
			public void buttonClick(ClickEvent event) {
				vlo.removeComponent(c1);
				vlo.removeComponent(c2);
				removeButton.setEnabled(false);
			}
		});

		vlo.addComponent(addButton);
		vlo.addComponent(replaceButton);
		vlo.addComponent(moveButton);
		vlo.addComponent(removeButton);
		vlo.addComponent(new Label("<hr />", Label.CONTENT_XHTML));

		vlo.addComponent(c1);
		vlo.addComponent(c2);
		vlo.addComponent(c3);

		return vlo;
	}

	@Override
	protected Layout getComponentSizingTests() {
		final VerticalLayout vlo = getTestLaytout();
		vlo.addComponent(new Label("<hr />", Label.CONTENT_XHTML));

		final AbstractComponent c = getTestTable();

		final Button biggerButton = new Button("full size");
		final Button smallerButton = new Button("200 px width");
		final Button originalButton = new Button("undefined size and add");

		vlo.addComponent(biggerButton);
		vlo.addComponent(smallerButton);
		vlo.addComponent(originalButton);
		vlo.addComponent(c);

		biggerButton.setEnabled(true);
		smallerButton.setEnabled(false);
		originalButton.setEnabled(false);

		biggerButton.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 7716267156088629379L;

			@Override
			public void buttonClick(ClickEvent event) {
				c.setSizeFull();
				biggerButton.setEnabled(false);
				smallerButton.setEnabled(true);
			}
		});
		smallerButton.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 7716267156088629379L;

			@Override
			public void buttonClick(ClickEvent event) {
				c.setWidth("200px");
				smallerButton.setEnabled(false);
				originalButton.setEnabled(true);
			}
		});
		originalButton.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 7716267156088629379L;

			@Override
			public void buttonClick(ClickEvent event) {
				originalButton.setEnabled(false);
				c.setSizeUndefined();
				((Table) c)
						.addItem(
								new Object[] { "VYVTCTC VYVYV ECECCE NIDSD SDMPOM" },
								3);
			}
		});

		return vlo;
	}

	@Override
	protected Layout getLayoutSizingTests() {
		final VerticalLayout vlo = getTestLaytout();
		
		vlo.setSpacing(false);
		vlo.setMargin(false);
		vlo.addComponent(new Label("<hr />", Label.CONTENT_XHTML));
		final AbstractComponent c1 = getTestTable();
		c1.setSizeFull();
		final AbstractComponent c2 = getTestTable();
		c2.setSizeFull();

		final Button button1 = new Button("Set fixed height 350px");
		final Button button2 = new Button("Set undefined size and add component");
		final Button button3 = new Button("Set fixed width and height 75%");
		final Button button4 = new Button("Set size full");
		
		vlo.addComponent(button1);
		vlo.addComponent(button2);
		vlo.addComponent(button3);
		vlo.addComponent(button4);
		button1.setEnabled(true);
		button2.setEnabled(false);
		button3.setEnabled(false);
		button4.setEnabled(false);

		vlo.addComponent(new Label("<hr />", Label.CONTENT_XHTML));
		vlo.addComponent(c1);
		vlo.addComponent(new Label("<hr />", Label.CONTENT_XHTML));
		vlo.addComponent(c2);
		vlo.addComponent(new Label("<hr />", Label.CONTENT_XHTML));
		vlo.setExpandRatio(c1, 0.5f);
		vlo.setExpandRatio(c2, 0.5f);
		
		button1.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 7716267156088629379L;
			@Override
			public void buttonClick(ClickEvent event) {
				vlo.setHeight("350px");
				button1.setEnabled(false);
				button2.setEnabled(true);
			}
		});
		button2.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 7716267156088629379L;
			@Override
			public void buttonClick(ClickEvent event) {
				vlo.setSizeUndefined();
				vlo.addComponent(new Label("--- NEW LABEL ---"));
				button2.setEnabled(false);
				button3.setEnabled(true);
			}
		});
		button3.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 7716267156088629379L;
			@Override
			public void buttonClick(ClickEvent event) {
				vlo.setWidth("75%");
				vlo.setHeight("75%");
				button3.setEnabled(false);
				button4.setEnabled(true);
			}
		});
		button4.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 7716267156088629379L;
			@Override
			public void buttonClick(ClickEvent event) {
				vlo.setSizeFull();
				button4.setEnabled(false);
			}
		});

		return vlo;
	}

	@Override
	protected Layout getExpandRatiosTests() {
		final VerticalLayout vlo = getTestLaytout();
		vlo.addComponent(new Label("<hr />", Label.CONTENT_XHTML));
		final AbstractComponent c1 = getTestTable();
		c1.setSizeFull();
		final AbstractComponent c2 = getTestTable();
		c2.setSizeFull();

		final Button button1 = new Button("Expand to 1/undefined");
		final Button button2 = new Button("Expand to 0.5/0.5");
		final Button button3 = new Button("Expand to 0.75/0.25");

		vlo.addComponent(button1);
		vlo.addComponent(button2);
		vlo.addComponent(button3);
		button1.setEnabled(true);
		button2.setEnabled(false);
		button3.setEnabled(false);

		vlo.addComponent(c1);
		vlo.addComponent(c2);

		button1.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 7716267156088629379L;

			@Override
			public void buttonClick(ClickEvent event) {
				vlo.setExpandRatio(c1, 1.0f);
				button1.setEnabled(false);
				button2.setEnabled(true);
			}
		});
		button2.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 7716267156088629379L;

			@Override
			public void buttonClick(ClickEvent event) {
				vlo.setExpandRatio(c1, 0.5f);
				vlo.setExpandRatio(c2, 0.5f);
				button2.setEnabled(false);
				button3.setEnabled(true);
			}
		});
		button3.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 7716267156088629379L;

			@Override
			public void buttonClick(ClickEvent event) {
				vlo.setExpandRatio(c1, 0.75f);
				vlo.setExpandRatio(c2, 0.25f);
				button3.setEnabled(false);
			}
		});
		return vlo;
	}

	@Override
	protected Layout getIconsTests() {
		VerticalLayout vlo = getTestLaytout();
		AbstractComponent[] components = new AbstractComponent[2];

		Resource[] icons = new Resource[] {
				new ClassResource("alert.png", application),
				new ClassResource("help.png", application) };

		vlo.addComponent(new Label("<hr />", Label.CONTENT_XHTML));
		for (int i = 0; i < components.length; i++) {
			components[i] = new TextField();
			((TextField) components[i]).setValue("FIELD " + i);
			components[i].setIcon(icons[i]);
			components[i].setCaption("long test caption bewucbwuebco or bmort b cbwecubw wbeucwe asdasd asdasda asdasd");
			vlo.addComponent(components[i]);
		}
		vlo.addComponent(new Label("<hr />", Label.CONTENT_XHTML));
		for (int i = 0; i < components.length; i++) {
			components[i] = new Label();
			((Label) components[i]).setValue("Label " + i);
			components[i].setIcon(icons[i]);
			vlo.addComponent(components[i]);
		}
		vlo.addComponent(new Label("<hr />", Label.CONTENT_XHTML));
		for (int i = 0; i < components.length; i++) {
			components[i] = new Select();
			components[i].setIcon(icons[i]);
			vlo.addComponent(components[i]);
		}
		vlo.addComponent(new Label("<hr />", Label.CONTENT_XHTML));
		for (int i = 0; i < components.length; i++) {
			components[i] = new Button();
			components[i].setComponentError(new UserError("component error, user error"));
			components[i].setIcon(icons[i]);
			vlo.addComponent(components[i]);
		}
		vlo.addComponent(new Label("<hr />", Label.CONTENT_XHTML));
		for (int i = 0; i < components.length; i++) {
			components[i] = new Link("Link",null);
			components[i].setIcon(icons[i]);
			vlo.addComponent(components[i]);
		}
		return vlo;
	}

	@Override
	protected Layout getMarginSpacingTests() {
		final VerticalLayout vlo = getTestLaytout();
		vlo.setSpacing(false);
		vlo.setMargin(false);
		vlo.addComponent(new Label("<hr />", Label.CONTENT_XHTML));
		final AbstractComponent c1 = getTestTable();
		c1.setSizeFull();
		final AbstractComponent c2 = getTestTable();
		c2.setSizeFull();

		final Button button1 = new Button("Set margin on");
		final Button button2 = new Button("Set spacing on");
		final Button button3 = new Button("Set margin off");
		final Button button4 = new Button("Set spacing off");
		
		vlo.addComponent(button1);
		vlo.addComponent(button2);
		vlo.addComponent(button3);
		vlo.addComponent(button4);
		button1.setEnabled(true);
		button2.setEnabled(false);
		button3.setEnabled(false);
		button4.setEnabled(false);

		vlo.addComponent(new Label("<hr />", Label.CONTENT_XHTML));
		vlo.addComponent(c1);
		vlo.addComponent(new Label("<hr />", Label.CONTENT_XHTML));
		vlo.addComponent(c2);
		vlo.addComponent(new Label("<hr />", Label.CONTENT_XHTML));
		vlo.setExpandRatio(c1, 0.5f);
		vlo.setExpandRatio(c2, 0.5f);
		
		button1.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 7716267156088629379L;
			@Override
			public void buttonClick(ClickEvent event) {
				vlo.setMargin(true);
				button1.setEnabled(false);
				button2.setEnabled(true);
			}
		});
		button2.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 7716267156088629379L;
			@Override
			public void buttonClick(ClickEvent event) {
				vlo.setSpacing(true);
				button2.setEnabled(false);
				button3.setEnabled(true);
			}
		});
		button3.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 7716267156088629379L;
			@Override
			public void buttonClick(ClickEvent event) {
				vlo.setMargin(false);
				button3.setEnabled(false);
				button4.setEnabled(true);
			}
		});
		button4.addListener(new Button.ClickListener() {
			private static final long serialVersionUID = 7716267156088629379L;
			@Override
			public void buttonClick(ClickEvent event) {
				vlo.setSpacing(false);
				button4.setEnabled(false);
			}
		});
		
		return vlo;
	}

	@Override
	protected Layout getRequiredErrorIndicatorsTests() {
		VerticalLayout vlo = getTestLaytout();
		AbstractComponent[] components= new AbstractComponent[4];
		components[0] = new Label("LABEL");
		components[0].setSizeUndefined();
		components[1] = new Button("BUTTON");
		components[2] = getTestTable();
		components[3] = getTestTabsheet();
		components[3].setIcon(new ClassResource("help.png", application));
		
		AbstractField[] fields= new AbstractField[6];
		fields[0] = new TextField();
		fields[0].setRequired(true);
		fields[0].setValidationVisible(true);
		fields[0].setRequiredError("required error");
		
		fields[1] = new TextField();
		fields[1].setValue("TEXTFIELD2");
		fields[1].setComponentError(new UserError("component error, user error"));
		
		fields[2] = new Select();
		fields[2].setComponentError(new SystemError("component error, system error"));
		fields[3] = new DateField();
		fields[3].setComponentError(new SystemError("component error, system error"));
		
		fields[4] = new CheckBox();
		fields[4].setComponentError(new UserError("component error, user error"));
		
		fields[5] = new NativeSelect();
		fields[5].setRequired(true);
		fields[5].setValidationVisible(true);
		fields[5].setRequiredError("required error");
		fields[5].setComponentError(new UserError("component error, user error"));
		fields[5].setIcon(new ClassResource("alert.png", application));
		
		vlo.addComponent(new Label("<hr />", Label.CONTENT_XHTML));
		for (int i=0; i<components.length; i++) {
			components[i].setComponentError(new UserError("component error, user error"));
			vlo.addComponent(components[i]);
		}
		vlo.addComponent(new Label("<hr />", Label.CONTENT_XHTML));
		for (int i=0; i<fields.length; i++) {
			vlo.addComponent(fields[i]);
		}
		vlo.addComponent(new Label("<hr />", Label.CONTENT_XHTML));
		
		return vlo;
	}

	private VerticalLayout getTestLaytout() {
		VerticalLayout vlo = new VerticalLayout();
		vlo.setHeight("600px");
		vlo.setWidth("400px");
		return vlo;
	}
	
	
	private AbstractComponent getTestTabsheet() {
		TabSheet tabsheet = new TabSheet();
		tabsheet.setSizeUndefined();
		tabsheet.addTab(new Label("TAB1"), "TAB1", new ClassResource("alert.png", application));
		tabsheet.addTab(new Label("TAB2"), "TAB2", null);
		return tabsheet;
	}

	private Table getTestTable() {
		Table t = new Table();
		t.setSizeUndefined();
		t.setPageLength(5);
		t.addContainerProperty("test", String.class, null);
		t.addItem(new Object[] { "qwertyuiop asdfghjklöä zxccvbnm,m,." }, 1);
		t.addItem(new Object[] { "YGVYTCTCTRXRXRXRX" }, 2);
		return t;
	}
}
