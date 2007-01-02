package com.itmill.toolkit.demo;

import java.io.*;
import java.util.*;
import java.lang.ref.WeakReference;

import com.itmill.toolkit.ui.*;
import com.itmill.toolkit.data.*;
import com.itmill.toolkit.demo.features.PropertyPanel;

/** Chat example application.
 * 
 * <p>This example application implements Internet chatroom with the 
 * following features:
 * <ul>  
 * </ul>
 * </p>
 *
 * @see com.itmill.toolkit.Application
 * @see com.itmill.toolkit.ui.FrameWindow
 * @see com.itmill.toolkit.terminal.StreamResource
 */

public class AjaxChat
	extends com.itmill.toolkit.Application
	implements Button.ClickListener {

	/** Login name / Alias for chat */
	private TextField loginName = new TextField("Your name?", "");

	/** Login button */
	private Button loginButton = new Button("Enter chat");

	/** Text to be said to discussion */
	private TextField sayText = new TextField();

	/** Button for sending the sayTest to discussion */
	private Button say = new Button("Say");

	private static Tree userlist = new Tree("Users");
	
	private static Panel messagePane;
	
	private ProgressIndicator connectionstatus;
	
	private static int msgCount = 1;
	
	private static final int MSG_PER_PAGE = 10;
	
	private Window main = new Window("Ajax chat example",
			new OrderedLayout(OrderedLayout.ORIENTATION_VERTICAL));

	private Item userObject;

	private Button leaveButton;
	
	/** Initialize the chat application */
	public void init() {
        setMainWindow(main);
        
        if(messagePane == null) {
        	messagePane = new Panel();
        	messagePane.setCaption("Messages:");
        	messagePane.addComponent(new ChatMessage("SERVER", "Chat started"));
        }
        
        
		// Initialize user interface
		say.dependsOn(sayText);
		say.addListener((Button.ClickListener) this);
		Panel controls =
			new Panel(
				"",
				new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL));
		controls.addComponent(sayText);
		sayText.setColumns(40);
		controls.addComponent(say);
		controls.addComponent(loginName);
		loginName.focus();
		controls.addComponent(loginButton);
		loginButton.dependsOn(loginName);
		loginButton.addListener(this);
		leaveButton = new Button("Leave", this, "leave");
		controls.addComponent(leaveButton);
		say.setVisible(false);
		sayText.setVisible(false);
		
		this.connectionstatus = new ProgressIndicator();
		this.connectionstatus.setIndeterminate(true);
		this.connectionstatus.setPollingInterval(2000); // check for new messages every 2 seconds
		main.addComponent(this.connectionstatus);
        main.addComponent(controls);
        main.addComponent(messagePane);
        main.addComponent(userlist);

	}

	/** Handle button actions for login, user listing and saying */
	public void buttonClick(Button.ClickEvent event) {

		// Say something in discussion
		if (event.getSource() == say && sayText.toString().length() > 0) {

			// Say something to chatstream
			say(sayText.toString());

			// Clear the saytext field
			sayText.setValue("");
			sayText.focus();
		}

		// Login to application
		else if (
			event.getSource() == loginButton
				&& loginName.toString().length() > 0) {

			// Set user name
			setUser(loginName.toString());
			//TODO remove elements older than ten posts
			
			userlist.addItem(loginName.toString());

			// Hide logins controls
			loginName.setVisible(false);
			loginButton.setVisible(false);

			// Show say controls
			say.setVisible(true);
			sayText.setVisible(true);
			sayText.focus();
			say("[Joining chat]");

		}
	}


	/** Add message to messages table */
	private void say(String text) {
		if(text != "") {
			synchronized(messagePane) {
				msgCount++;
				messagePane.addComponent(new ChatMessage(this.loginName.toString(), text));
				if(msgCount > MSG_PER_PAGE) {
					Label firstMessage = (Label) messagePane.getComponentIterator().next();
					messagePane.removeComponent(firstMessage);
				}
			}
		}
	}


	
	/** Leave the chat */
	public void leave() {
		say("[Leaving chat]");
		userlist.removeItem(loginName.toString());
		this.loginName.setVisible(true);
		this.loginButton.setVisible(true);
		
		this.sayText.setVisible(false);
		this.say.setVisible(false);
		this.leaveButton.setVisible(false);
	}

	/** Make sure that everybody leaves the chat */
	public void finalize() {
		leave();
	}

	private class ChatMessage extends Label {
		ChatMessage(String s, String m) {
			super( "<small style=\"font-size:x-small;\">"+(new Date()).toString()+
					"</small><div><strong style=\"font-weight:bold; \">"+
					s+": </strong>" +
					m +"</div>");
			this.setContentMode(Label.CONTENT_XHTML);
		}
	}
}
