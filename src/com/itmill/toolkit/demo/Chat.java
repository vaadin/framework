package com.itmill.toolkit.demo;

import java.io.*;
import java.util.*;
import java.lang.ref.WeakReference;

import com.itmill.toolkit.terminal.StreamResource;
import com.itmill.toolkit.ui.*;

/** Chat example application.
 * 
 * <p>This example application implements Internet chatroom with the 
 * following features:
 * <ul>
 * 		<li>Continuosly streaming chat discussion. This is implemented 
 * 			with StreamResource that is kept open during the discussion.
 * 		<li>Dynamically changing frames.
 * 		<li>Chatroom that is implemented with static list of chatters 
 * 			referenced by weak references.
 *  </ul>
 * </p>
 *
 * @see com.itmill.toolkit.Application
 * @see com.itmill.toolkit.ui.FrameWindow
 * @see com.itmill.toolkit.terminal.StreamResource
 */

public class Chat
	extends com.itmill.toolkit.Application
	implements StreamResource.StreamSource, Button.ClickListener {

	/** Linked list of Chat applications who participate the discussion */
	private static LinkedList chatters = new LinkedList();

	/** Reference (to this application) stored in chatters list */
	private WeakReference listEntry = null;

	/** Writer for writing to open chat stream */
	private PrintWriter chatWriter = null;

	/** Login name / Alias for chat */
	private TextField loginName = new TextField("Your name?", "");

	/** Login button */
	private Button loginButton = new Button("Enter chat");

	/** Text to be said to discussion */
	private TextField sayText = new TextField();

	/** Button for sending the sayTest to discussion */
	private Button say = new Button("Say");

	/** Button for listing the people in the chatroom */
	private Button listUsers = new Button("List chatters");

	/** Last time this chat application said something */
	private long idleSince = (new Date()).getTime();

	/** framewindow for following the discussion and control */
	FrameWindow frames = new FrameWindow("Chat");

	/** Last messages */
	private static LinkedList lastMessages = new LinkedList();

	/** Initialize the chat application */
	public void init() {

		// Initialize user interface
		say.dependsOn(sayText);
		say.addListener((Button.ClickListener) this);
		listUsers.addListener((Button.ClickListener) this);
		StreamResource chatStream =
			new StreamResource(this, "discussion.html", this);
		chatStream.setBufferSize(1);
		chatStream.setCacheTime(0);
		frames.getFrameset().newFrame(chatStream, "chatDiscussion");
		Window controls =
			new Window(
				"",
				new OrderedLayout(OrderedLayout.ORIENTATION_HORIZONTAL));
		controls.setName("chatControls");
		controls.addComponent(sayText);
		sayText.setColumns(40);
		controls.addComponent(say);
		controls.addComponent(loginName);
		loginName.focus();
		controls.addComponent(loginButton);
		loginButton.dependsOn(loginName);
		loginButton.addListener(this);
		controls.addComponent(listUsers);
		Button leaveButton = new Button("Leave", this, "leave");
		controls.addComponent(leaveButton);
		say.setVisible(false);
		sayText.setVisible(false);
		frames.getFrameset().newFrame(controls).setAbsoluteSize(60);
		frames.getFrameset().setVertical(true);
		frames.setName("chatMain");
		setMainWindow(frames);

		// Register chat application
		synchronized (chatters) {
			chatters.add(listEntry = new WeakReference(this));
		}
	}

	/** Handle button actions for login, user listing and saying */
	public void buttonClick(Button.ClickEvent event) {

		// Say something in discussion
		if (event.getSource() == say && sayText.toString().length() > 0) {

			// Say something to chatstream
			say("<b>" + getUser() + ": </b>" + sayText + "<br>");

			// Clear the saytext field
			sayText.setValue("");
			sayText.focus();
		}

		// List the users
		else if (event.getSource() == listUsers)
			listUsers();

		// Login to application
		else if (
			event.getSource() == loginButton
				&& loginName.toString().length() > 0) {

			// Set user name
			setUser(loginName.toString());

			// Hide logins controls
			loginName.setVisible(false);
			loginButton.setVisible(false);

			// Show say controls
			say.setVisible(true);
			sayText.setVisible(true);
			sayText.focus();

			// Announce discussion joining
			say(
				"<i>"
					+ getUser()
					+ " joined the discussion ("
					+ (new Date()).toString()
					+ ")</i><br>");
		}
	}

	/** List chatters to chat stream */
	private void listUsers() {

		// Compose userlist 
		StringBuffer userlist = new StringBuffer();
		userlist.append(
			"<div style=\"background-color: #ffffd0;\"><b>Chatters ("
				+ (new Date())
				+ ")</b><ul>");
		synchronized (chatters) {
			for (Iterator i = chatters.iterator(); i.hasNext();) {
				try {
					Chat c = (Chat) ((WeakReference) i.next()).get();
					String name = (String) c.getUser();
					if (name != null && name.length() > 0) {
						userlist.append("<li>" + name);
						userlist.append(
							" (idle "
								+ ((new Date()).getTime() - c.idleSince) / 1000
								+ "s)");
					}
				} catch (NullPointerException ignored) {
				}
			}
		}
		userlist.append("</ul></div><script>self.scroll(0,71234);</script>\n");

		// Print the user list to chatstream
		printToStream(userlist.toString());
	}

	/** Print to chatstream and scroll the window */
	private void printToStream(String text) {
		if (chatWriter != null) {
			chatWriter.println(text);
			chatWriter.println("<script>self.scroll(0,71234);</script>\n");
			chatWriter.flush();
		}
	}

	/** Say to all chat streams */
	private void say(String text) {

		// Get all the listeners
		Object[] listener;
		synchronized (chatters) {
			listener = chatters.toArray();
		}

		// Put the saytext to listener streams
		// Remove dead listeners
		for (int i = 0; i < listener.length; i++) {
			Chat c = (Chat) ((WeakReference) listener[i]).get();
			if (c != null)
				c.printToStream(text);
			else
				chatters.remove(listener[i]);
		}

		// Update idle time
		idleSince = (new Date()).getTime();

		// Update last messages
		synchronized (lastMessages) {
			lastMessages.addLast(text);
			while (lastMessages.size() > 5)
				lastMessages.removeFirst();
		}
	}

	/** Open chat stream */
	public InputStream getStream() {

		// Close any existing streams
		if (chatWriter != null)
			chatWriter.close();

		// Create piped stream
		PipedOutputStream chatStream = new PipedOutputStream();
		chatWriter = new PrintWriter(chatStream);
		InputStream is = null;
		try {
			is = new PipedInputStream(chatStream);
		} catch (IOException ignored) {
			chatWriter = null;
			return null;
		};

		// Write headers
		printToStream(
			"<html><head><title>Discussion "
				+ (new Date())
				+ "</title>"
				+ "</head><body>\n");

		// Print last messages 
		Object[] msgs;
		synchronized (lastMessages) {
			msgs = lastMessages.toArray();
		}
		for (int i = 0; i < msgs.length; i++)
			printToStream(msgs[i].toString());

		// Allways list the users
		listUsers();

		return is;
	}

	/** Leave the chat */
	public void leave() {

		// If we have been logged in, say goodbye
		if (listEntry != null) {
			if (getUser() != null)
				say(
					"<i>"
						+ getUser()
						+ " left the chat ("
						+ (new Date())
						+ ")</i><br>");

			synchronized (chatters) {
				chatters.remove(listEntry);
				listEntry = null;
			}
		}
		if (chatWriter != null)
			chatWriter.close();

		// Close the chat frames
		if (frames != null) {
			frames.getFrameset().removeAllFrames();
			Window restartWin = new Window();
			frames.getFrameset().newFrame(restartWin);
			restartWin.addComponent(new Button("Restart chat", this, "close"));
			frames = null;
		}
	}

	/** Make sure that everybody leaves the chat */
	public void finalize() {
		leave();
	}

}
