package com.itmill.toolkit.terminal.web;

import java.util.Set;

import com.itmill.toolkit.terminal.PaintException;
import com.itmill.toolkit.terminal.Paintable;
import com.itmill.toolkit.terminal.Resource;
import com.itmill.toolkit.terminal.VariableOwner;

public interface AjaxPaintTarget {

	/**
	 * Gets the UIDL already printed to stream. Paint target must be closed
	 * before the <code>getUIDL</code> can be called.
	 * 
	 * @return the UIDL.
	 */
	public abstract String getUIDL();

	/**
	 * Closes the paint target. Paint target must be closed before the
	 * <code>getUIDL</code> can be called. Subsequent attempts to write to
	 * paint target. If the target was already closed, call to this function is
	 * ignored. will generate an exception.
	 * 
	 * @throws PaintException
	 *             if the paint operation failed.
	 */
	public abstract void close() throws PaintException;

	/**
	 * 
	 * @return
	 */
	public abstract boolean isTrackPaints();

	/**
	 * Gets the number of paints.
	 * 
	 * @return the number of paints.
	 */
	public abstract int getNumberOfPaints();

	/**
	 * Sets the tracking to true or false.
	 * 
	 * This also resets the number of paints.
	 * 
	 * @param enabled
	 *            is the tracking is enabled or not.
	 * @see #getNumberOfPaints()
	 */
	public abstract void setTrackPaints(boolean enabled);

	public abstract void setPreCachedResources(Set preCachedResources);

	public abstract Set getPreCachedResources() ;

}