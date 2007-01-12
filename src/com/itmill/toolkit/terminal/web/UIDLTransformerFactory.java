/* *************************************************************************
 
                               IT Mill Toolkit 

               Development of Browser User Interfaces Made Easy

                    Copyright (C) 2000-2006 IT Mill Ltd
                     
   *************************************************************************

   This product is distributed under commercial license that can be found
   from the product package on license.pdf. Use of this product might 
   require purchasing a commercial license from IT Mill Ltd. For guidelines 
   on usage, see licensing-guidelines.html

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:   +358 2 4802 7181
   20540, Turku                          email:  info@itmill.com
   Finland                               company www: www.itmill.com
   
   Primary source for information and releases: www.itmill.com

   ********************************************************************** */

package com.itmill.toolkit.terminal.web;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Iterator;

/** Class implementing the UIDLTransformer Factory.
 * The factory creates and maintains a pool of transformers that are used
 * for transforming UIDL to HTML.
 * 
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */

public class UIDLTransformerFactory {

	/** Time between repository modified queries. */
	private static final int CACHE_CHECK_INTERVAL_MILLIS = 5 * 1000;

	/** The time transformers are cached by default*/
	private static final long DEFAULT_TRANSFORMER_CACHETIME = 60 * 60 * 1000;

	/** Maximum number of transformers in use */
	private int maxConcurrentTransformers = 1;

	/** Last time theme modification time was checked */
	private long lastModificationCheckTime = 0;

	/** Last time theme source was modified */
	private long themeSourceModificationTime = 0;

	/** How long to cache transformers. */
	private long cacheTime = DEFAULT_TRANSFORMER_CACHETIME;

	/** Spool manager thread */
	private SpoolManager spoolManager;

	private Map transformerSpool = new HashMap();
	private ThemeSource themeSource;
	private ApplicationServlet webAdapterServlet;
	private int transformerCount = 0;
	private int transformersInUse = 0;

	/** Constructor for transformer factory.
	 * Method UIDLTransformerFactory.
	 * @param themeSource Theme source to be used for themes.
	 * @param webAdapterServlet The Adapter servlet.
	 * @param maxConcurrentTransformers Maximum number of concurrent themes in use.
	 * @param cacheTime Time to cache the transformers.
	 */
	public UIDLTransformerFactory(
		ThemeSource themeSource,
		ApplicationServlet webAdapterServlet,
		int maxConcurrentTransformers,
		long cacheTime) {
		this.webAdapterServlet = webAdapterServlet;
		if (themeSource == null)
			throw new NullPointerException();
		this.themeSource = themeSource;
		this.themeSourceModificationTime = themeSource.getModificationTime();
		this.maxConcurrentTransformers = maxConcurrentTransformers;
		if (cacheTime >= 0)
			this.cacheTime = cacheTime;
		this.spoolManager = new SpoolManager(this.cacheTime);
		this.spoolManager.setDaemon(true);
		//Enable manager only if time > 0
		if (this.cacheTime > 0)
			this.spoolManager.start();
	}

	/** Get new transformer of the specified type
	 * @param type Type of the requested transformer.
	 * @param variableMap WebVariable map used by the transformer
	 * @return Created new transformer.
	 */
	public synchronized UIDLTransformer getTransformer(UIDLTransformerType type)
		throws UIDLTransformerException {

		while (transformersInUse >= maxConcurrentTransformers) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				return null;
			}
		}

		// Get list of transformers for this type
		TransformerList list =
			(TransformerList) this.transformerSpool.get(type);

		// Check the modification time between fixed intervals
		long now = System.currentTimeMillis();
		if (now - CACHE_CHECK_INTERVAL_MILLIS
			> this.lastModificationCheckTime) {

			this.lastModificationCheckTime = now;

			//  Check if the theme source has been modified and flush 
			//  list if necessary
			long lastmod = this.themeSource.getModificationTime();
			if (list != null && this.themeSourceModificationTime < lastmod) {
				if (webAdapterServlet.isDebugMode(null)) {
					Log.info(
						"Theme source modified since "
							+ new Date(this.themeSourceModificationTime)
								.toString()
							+ ". Reloading...");
				}
				// Force refresh by removing from spool
				this.transformerSpool.clear();
				list = null;
				this.transformerCount = 0;
				this.themeSourceModificationTime = lastmod;
			}
		}

		UIDLTransformer t = null;

		if (list != null && !list.isEmpty()) {
			// If available, return the first available transformer
			t = (UIDLTransformer) list.removeFirst();
			if (webAdapterServlet.isDebugMode(null)) {
				Log.info("Reserved existing transformer: " + type);
			}
		} else {

			// Create new transformer and return it. Transformers are added to
			// spool when they are released.
			t = new UIDLTransformer(type, themeSource, webAdapterServlet);
			transformerCount++;
			if (webAdapterServlet.isDebugMode(null)) {
				Log.info(
					"Created new transformer ("
						+ transformerCount
						+ "):"
						+ type);
			}

			// Create new list, if not found
			if (list == null) {
				list = new TransformerList();
				this.transformerSpool.put(type, list);
				if (webAdapterServlet.isDebugMode(null)) {
					Log.info("Created new type: " + type);
				}
			}

		}
		transformersInUse++;
		return t;
	}

	/** Recycle a used transformer back to spool.
	 * One must guarantee not to use the transformer after it have been released.
	 * @param transformer UIDLTransformer to be recycled
	 */
	public synchronized void releaseTransformer(UIDLTransformer transformer) {

		try {
			// Reset the transformer before returning it to spool
			transformer.reset();

			// Recycle the transformer back to spool
			TransformerList list =
				(TransformerList) this.transformerSpool.get(
					transformer.getTransformerType());
			if (list != null) {
				list.add(transformer);
				if (webAdapterServlet.isDebugMode(null)) {
					Log.info(
						"Released transformer: "
							+ transformer.getTransformerType()
							+ "(In use: "
							+ transformersInUse
							+ ",Spooled: "
							+ list.size()
							+ ")");
				}
				list.lastUsed = System.currentTimeMillis();
			} else {
				Log.info(
					"Tried to release non-existing transformer. Ignoring."
						+ " (Type:"
						+ transformer.getTransformerType()
						+ ")");
			}
		} finally {
			if (transformersInUse > 0)
				transformersInUse--;
			notifyAll();
		}
	}

	private class TransformerList {

		private LinkedList list = new LinkedList();
		private long lastUsed = 0;

		public void add(UIDLTransformer transformer) {
			list.add(transformer);
		}

		public UIDLTransformer removeFirst() {
			return (UIDLTransformer) ((LinkedList) list).removeFirst();
		}

		public boolean isEmpty() {
			return list.isEmpty();
		}

		public int size() {
			return list.size();
		}
	}

	private synchronized void removeUnusedTransformers() {
		long currentTime = System.currentTimeMillis();
		HashSet keys = new HashSet();
		keys.addAll(this.transformerSpool.keySet());
		for (Iterator i = keys.iterator(); i.hasNext();) {
			UIDLTransformerType type = (UIDLTransformerType) i.next();
			TransformerList l =
				(TransformerList) this.transformerSpool.get(type);
			if (l != null) {
				if (l.lastUsed > 0
					&& l.lastUsed < (currentTime - this.cacheTime)) {
					if (webAdapterServlet.isDebugMode(null)) {
						Log.info(
							"Removed transformer: "
								+ type
								+ " Not used since "
								+ new Date(l.lastUsed));
					}
					this.transformerSpool.remove(type);
				}
			}
		}
	}

	/** Class for periodically remove unused transformers from memory.
	 * @author IT Mill Ltd.
	 * @version @VERSION@
	 * @since 3.0
	 */
	protected class SpoolManager extends Thread {

		long refreshTime;

		public SpoolManager(long refreshTime) {
			super("UIDLTransformerFactory.SpoolManager");
			this.refreshTime = refreshTime;
		}

		public void run() {
			while (true) {
				try {
					sleep(refreshTime);
				} catch (Exception e) {
				}
				removeUnusedTransformers();
			}
		}
	}

}
