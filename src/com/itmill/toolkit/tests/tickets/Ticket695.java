package com.itmill.toolkit.tests.tickets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class Ticket695 extends Application {

	private static final long serialVersionUID = 3803150085397590662L;

	@Override
	public void init() {
		final Window w = new Window("Serialization test #695");
		setMainWindow(w);
		Button b = new Button("Serialize ApplicationContext");
		w.addComponent(b);
		b.addListener(new Button.ClickListener() {

			public void buttonClick(ClickEvent event) {
				ByteArrayOutputStream buffer = new ByteArrayOutputStream();
				try {
					ObjectOutputStream oos = new ObjectOutputStream(buffer);
					oos.writeObject(getContext());
					w.showNotification("ApplicationContext serialized ("
							+ buffer.size() + "bytes)");
				} catch (IOException e) {
					e.printStackTrace();
					w
							.showNotification("ApplicationContext serialization failed - see console for stacktrace");
				}

			}
		});
	}

}
