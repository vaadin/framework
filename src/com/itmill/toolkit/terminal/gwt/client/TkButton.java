package com.itmill.toolkit.terminal.gwt.client;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

public class TkButton extends Component {

	private Button b;
	private BooleanVariable state;

	public TkButton(Node uidl, GwtClient c) {
		super(getIdFromUidl(uidl), c);
		b = new Button();
		updateFromUidl(uidl);
		b.addClickListener(new ButtonClickListener());
	}

	public void updateFromUidl(Node n) {
		
		String text = "";
		String description = null;
		NamedNodeMap attributes = n.getAttributes();
		Node caption = attributes.getNamedItem("caption");
		if(caption != null)
			text = caption.getNodeValue();

		NodeList children = n.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			String nName = child.getNodeName();
			if(nName.equals("description"))
				description  = child.getFirstChild().toString();
			if(nName.equals("boolean")) {
				state = (BooleanVariable) VariableFactory.getVariable(child,this);
				state.setImmediate(true); // button always immediate
			}
		}
		b.setText(text);
		if(description != null)
			b.setTitle(description);
	}

	public Widget getWidget() {
		return b;
	}
	
	public class ButtonClickListener implements ClickListener {

		public void onClick(Widget sender) {
			TkButton.this.state.setValue(true);
			TkButton.this.state.update();
		}
		
	}
	
}
