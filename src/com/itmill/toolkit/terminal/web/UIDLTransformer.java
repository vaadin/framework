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

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.itmill.toolkit.terminal.PaintException;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.Iterator;

import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.OutputKeys;

/** 
 * Class implementing the UIDLTransformer.
 *
 * The transformer should not be created directly; it should be contructed
 * using <code>getTransformer</code> provided by <code>UIDLTransformerFactory</code>. 
 * 
 * After the transform has been done, the transformer can be recycled with
 * <code>releaseTransformer</code> by <code>UIDLTransformerFactory</code>.
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */

public class UIDLTransformer {

	/** 
	 * XSLT factory. 
	 */
	protected static javax.xml.transform.TransformerFactory xsltFactory;
	static {
		xsltFactory = javax.xml.transform.TransformerFactory.newInstance();
		if (xsltFactory == null)
			throw new RuntimeException(
				"Could not instantiate "
					+ "transformer factory. Maybe XSLT processor is "
					+ "not included in classpath.");
	}

	/** 
	 * Source of the transform containing UIDL. 
	 */
	private WebPaintTarget paintTarget;

	/** 
	 * Holds the type of the transformer. 
	 */
	private UIDLTransformerType transformerType;

	/** 
	 * Prepared XSLT transformer for UIDL transformations. 
	 */
	private javax.xml.transform.Transformer uidlTransformer;

	/** 
	 * Error handled used. 
	 */
	private TransformerErrorHandler errorHandler;

	/** 
	 * Theme repository used for late error reporting. 
	 */
	private ThemeSource themeSource;

	private ApplicationServlet webAdapterServlet;

	/** 
	 * UIDLTransformer constructor.
	 * @param type the Type of the transformer.
	 * @param themes the theme implemented by the transformer.
	 * @param webAdapterServlet the Adapter servlet.
	 * @throws UIDLTransformerException UIDLTransformer exception is thrown, 
	 * 									if the transform can not be created.
	 */
	public UIDLTransformer(
		UIDLTransformerType type,
		ThemeSource themes,
		ApplicationServlet webAdapterServlet)
		throws UIDLTransformerException {
		this.transformerType = type;
		this.themeSource = themes;
		this.webAdapterServlet = webAdapterServlet;

		// Registers the error handler
		errorHandler = new TransformerErrorHandler();
		xsltFactory.setErrorListener(errorHandler);

		try {

			// Creates XML Reader to be used by
			// XSLReader as the actual parser object.
			XMLReader parser = XMLReaderFactory.createXMLReader();

			// Creates XML reader for concatenating
			// multiple XSL files as one.

			XMLReader xmlReader =
				new XSLReader(
					parser,
					themes.getXSLStreams(
						type.getTheme(),
						type.getWebBrowser()));

			xmlReader.setErrorHandler(errorHandler);

			// Creates own SAXSource using a dummy inputSource.
			SAXSource source = new SAXSource(xmlReader, new InputSource());
			uidlTransformer = xsltFactory.newTransformer(source);

			if (uidlTransformer != null) {

				// Registers transformer error handler
				uidlTransformer.setErrorListener(errorHandler);

				// Ensures HTML output
				uidlTransformer.setOutputProperty(OutputKeys.METHOD, "html");

				// Ensures no indent
				uidlTransformer.setOutputProperty(OutputKeys.INDENT, "no");
			}

			// Checks if transform itself failed, meaning either
			// UIDL error or error in XSL/T semantics (like XPath)
			if (errorHandler.hasFatalErrors()) {
				throw new UIDLTransformerException(
					"XSL Transformer creation failed",
					errorHandler.getFirstFatalError(),
					errorHandler.getUIDLErrorReport()
						+ "<br /><br />"
						+ errorHandler.getXSLErrorReport(
							themeSource,
							transformerType));
			}

		} catch (Exception e) {
			// Pass the new XHTML coded error forwards
			throw new UIDLTransformerException(
				e.toString(),
				e,
				errorHandler.getXSLErrorReport(themeSource, transformerType));
		}
	}

	/** 
	 * Gets the type of the transformer.
	 * @return the Type of the transformer.
	 */
	public UIDLTransformerType getTransformerType() {
		return this.transformerType;
	}

	/** 
	 * Attaches the output stream to transformer and get corresponding UIDLStream for 
	 * writing UI description language trough transform to given output.
	 * @param variableMap the variable map used for UIDL creation.
	 * @return returns UI description language stream, that can be used for writing UIDL to
	 *  transformer.
	 */
	public WebPaintTarget getPaintTarget(HttpVariableMap variableMap) {

		try {
			paintTarget =
				new WebPaintTarget(
					variableMap,
					transformerType,
					webAdapterServlet,
					transformerType.getTheme());
		} catch (PaintException e) {
			throw new IllegalArgumentException(
				"Failed to instantiate new WebPaintTarget: " + e);
		}
		return paintTarget;
	}

	/** 
	 * Resets the transformer, before it can be used again. This also interrupts
	 * any ongoing transform and thus should not be called before the transform
	 * is ready. This is automatically called by the UIDLTransformFactory, when the UIDLTransformer
	 * has been released.
	 * @see UIDLTransformerFactory#releaseTransformer(UIDLTransformer)
	 */
	protected void reset() {
		if (paintTarget != null) {
			try {
				paintTarget.close();
			} catch (PaintException e) {
				// Ignore this exception
			}
			paintTarget = null;
		}
		if (errorHandler != null)
			errorHandler.clear();
	}

	/**
	 * Transforms the UIDL to HTML and output to the OutputStream.
	 * 
	 * @param outputStream the output stream to render to.
	 * @throws UIDLTransformerException UIDLTransformer exception is thrown, 
	 * 								if the transform can not be created.
	 */
	public void transform(OutputStream outputStream)
		throws UIDLTransformerException {

		StreamResult result =
			new StreamResult(new BufferedOutputStream(outputStream));

		// XSL Transform
		try {
			InputSource uidl =
				new InputSource(new StringReader(paintTarget.getUIDL()));
			XMLReader reader =
				org.xml.sax.helpers.XMLReaderFactory.createXMLReader();
			reader.setErrorHandler(this.errorHandler);

			// Validates if requested. We validate the UIDL separately,
			// toget the SAXExceptions instead of TransformerExceptions.
			// This is required to get the line numbers right.
			/* FIXME: Disable due abnormalities in DTD handling.
			if (webAdapterServlet.isDebugMode()) {
				reader.setFeature(
					"http://xml.org/sax/features/validation",
					true);
				reader.parse(uidl);
				uidl =
				new InputSource(new StringReader(paintTarget.getUIDL()));
				
			}
			*/
			SAXSource source = new SAXSource(reader, uidl);

			uidlTransformer.transform(source, result);
		} catch (Exception e) {
			// XSL parsing failed. Pass the new XHTML coded error forwards
			throw new UIDLTransformerException(
				e.toString(),
				e,
				errorHandler.getUIDLErrorReport());
		}

		// Checks if transform itself failed, meaning either
		// UIDL error or error in XSL/T semantics (like XPath)
		if (errorHandler.hasFatalErrors()) {
			throw new UIDLTransformerException(
				"UIDL Transform failed",
				errorHandler.getFirstFatalError(),
				errorHandler.getUIDLErrorReport()
					+ "<br /><br />"
					+ errorHandler.getXSLErrorReport(
						themeSource,
						transformerType));
		}
	}
	
/**
 * 
 * 
 *
 */
	protected class TransformerErrorHandler
		implements ErrorListener, org.xml.sax.ErrorHandler {

		LinkedList errors = new LinkedList();
		LinkedList warnings = new LinkedList();
		LinkedList fatals = new LinkedList();
		Hashtable rowToErrorMap = new Hashtable();
		Hashtable errorToRowMap = new Hashtable();
		
/**
 * 
 * @return
 */
		public boolean hasNoErrors() {
			return errors.isEmpty() && warnings.isEmpty() && fatals.isEmpty();
		}
		
/**
 * 
 * @return
 */
		public boolean hasFatalErrors() {
			return !fatals.isEmpty();
		}
		
/**
 * 
 *
 */
		public void clear() {
			errors.clear();
			warnings.clear();
			fatals.clear();
		}
		
		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return getHTMLErrors("Fatal Errors", fatals)
				+ "<br />"
				+ getHTMLErrors("Errors", errors)
				+ "<br />"
				+ getHTMLErrors("Warnings", warnings)
				+ "<br />";
		}
		
/**
 * 
 * @param title
 * @param l
 * @return
 */
		private String getHTMLErrors(String title, LinkedList l) {
			String r = "";
			r = "<b>" + title + "</b><br />";
			if (l.size() > 0) {
				for (Iterator i = l.iterator(); i.hasNext();) {
					Exception e = (Exception) i.next();
					if (e
						instanceof javax.xml.transform.TransformerException) {
						Integer line = (Integer) errorToRowMap.get(e);
						r += " - "
							+ WebPaintTarget.escapeXML(
								((javax.xml.transform.TransformerException) e)
									.getMessage());
						Throwable cause =
							((javax.xml.transform.TransformerException) e)
								.getException();

						// Append cause if available
						if (cause != null) {
							r += ": "
								+ WebPaintTarget.escapeXML(cause.getMessage());
						}
						r += line != null
							? " (line:" + line.intValue() + ")"
							: " (line unknown)";
						r += "<br />\n";
					} else {
						Integer line = (Integer) errorToRowMap.get(e);
						r += " - " + WebPaintTarget.escapeXML(e.toString());
						r += line != null
							? " (line:" + line.intValue() + ")"
							: " (line unknown)";
						r += "<br />\n";

					}
				}
			}
			return r;
		}

		/**
		 * @see javax.xml.transform.ErrorListener#error(TransformerException)
		 */
		public void error(javax.xml.transform.TransformerException exception) {
			if (exception != null) {
				errors.addLast(exception);
				SourceLocator l = exception.getLocator();
				if (l != null) {
					rowToErrorMap.put(
						new Integer(
							((XSLReader.XSLStreamLocator) l).getLineNumber()),
						exception);
					errorToRowMap.put(
						exception,
						new Integer(
							((XSLReader.XSLStreamLocator) l).getLineNumber()));
				}
			}
		}

		/**
		 * @see javax.xml.transform.ErrorListener#fatalError(TransformerException)
		 */
		public void fatalError(
			javax.xml.transform.TransformerException exception) {
			if (exception != null) {
				fatals.addLast(exception);
				SourceLocator l = exception.getLocator();
				if (l != null) {
					rowToErrorMap.put(
						new Integer(l.getLineNumber()),
						exception);
					errorToRowMap.put(
						exception,
						new Integer(l.getLineNumber()));
				}
			}
		}

		/**
		 * @see javax.xml.transform.ErrorListener#warning(TransformerException)
		 */
		public void warning(
			javax.xml.transform.TransformerException exception) {
			if (exception != null) {
				warnings.addLast(exception);
				SourceLocator l = exception.getLocator();
				if (l != null) {
					rowToErrorMap.put(
						new Integer(l.getLineNumber()),
						exception);
					errorToRowMap.put(
						exception,
						new Integer(l.getLineNumber()));
				}
			}
		}

		/** 
		 * Gets the formated error report on XSL. 
		 * @param themes
		 * @param type
		 */
		public String getXSLErrorReport(
			ThemeSource themes,
			UIDLTransformerType type) {

			// Recreates the XSL for error reporting
			StringBuffer readBuffer = new StringBuffer();
			try {
				Collection c =
					themes.getXSLStreams(type.getTheme(), type.getWebBrowser());
				for (Iterator i = c.iterator(); i.hasNext();) {

					java.io.InputStream is =
						((ThemeSource.XSLStream) i.next()).getStream();
					byte[] buffer = new byte[1024];
					int read = 0;
					while ((read = is.read(buffer)) >= 0)
						readBuffer.append(new String(buffer, 0, read));
				}
			} catch (IOException ignored) {

			} catch (ThemeSource.ThemeException ignored) {

			}

			String xsl = "XSL Source not avaialable";
			if (readBuffer != null)
				xsl = readBuffer.toString();

			StringBuffer sb = new StringBuffer();

			// Print formatted UIDL with errors embedded

			int row = 0;
			int prev = 0;
			int index = 0;
			int errornro = 0;
			boolean lastLineWasEmpty = false;

			sb.append(toString());
			sb.append(
				"<font size=\"+1\"><a href=\"#err1\">"
					+ "Go to first error</a></font>"
					+ "<table width=\"100%\" style=\"border-left: 1px solid black; "
					+ "border-right: 1px solid black; border-bottom: "
					+ "1px solid black; border-top: 1px solid black\""
					+ " cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>"
					+ "<th bgcolor=\"#ddddff\" colspan=\"2\">"
					+ "<font size=\"+2\">XSL</font><br />"
					+ "</th></tr>\n");

			while ((index = xsl.indexOf('\n', prev)) >= 0) {
				String line = xsl.substring(prev, index);
				prev = index + 1;
				row++;

				Exception exp = (Exception) rowToErrorMap.get(new Integer(row));
				line = WebPaintTarget.escapeXML(line);
				boolean isEmpty = (line.length() == 0 || line.equals("\r"));

				// Code beautification : Comment lines
				line = xmlHighlight(line);

				String head = "";
				String tail = "";

				if (exp != null) {
					errornro++;
					head =
						"<a name=\"err"
							+ String.valueOf(errornro)
							+ "\"><table width=\"100%\">"
							+ "<tr><th bgcolor=\"#ff3030\">"
							+ exp.getLocalizedMessage()
							+ "</th></tr>"
							+ "<tr><td bgcolor=\"#ffcccc\">";
					tail =
						"</tr><tr><th bgcolor=\"#ff3030\">"
							+ (errornro > 1
								? "<a href=\"#err"
									+ String.valueOf(errornro - 1)
									+ "\">Previous error</a>   "
								: "")
							+ "<a href=\"#err"
							+ String.valueOf(errornro + 1)
							+ "\">Next error</a>"
							+ "</th></tr></table></a>\n";
				}

				if (!(isEmpty && lastLineWasEmpty))
					sb.append(
						"<tr"
							+ ((row % 10) > 4 ? " bgcolor=\"#eeeeff\"" : "")
							+ "><td style=\"border-right: 1px solid gray\">&nbsp;"
							+ String.valueOf(row)
							+ "&nbsp;</td><td>"
							+ head
							+ "<nobr>"
							+ line
							+ "</nobr>"
							+ tail
							+ "</td></tr>\n");

				lastLineWasEmpty = isEmpty;

			}

			sb.append("</table>\n");

			return sb.toString();
		}

		/** 
		 * Gets the formated error report on UIDL. 
		 * @return the formatted error report.
		 */
		public String getUIDLErrorReport() {

			String uidl = "UIDL Source Not Available.";
			if (paintTarget != null)
				uidl = paintTarget.getUIDL();
			StringBuffer sb = new StringBuffer();

			// Prints the formatted UIDL with errors embedded
			int row = 0;
			int prev = 0;
			int index = 0;
			boolean lastLineWasEmpty = false;

			// Appends the error report
			sb.append(toString());

			// Appends UIDL
			sb.append(
				"<table width=\"100%\" style=\"border-left: 1px solid black; "
					+ "border-right: 1px solid black; border-bottom: "
					+ "1px solid black; border-top: 1px solid black\""
					+ " cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr>"
					+ "<th bgcolor=\"#ddddff\" colspan=\"2\">"
					+ "<font size=\"+2\">UIDL</font><br />"
					+ "</th></tr>\n");

			while ((index = uidl.indexOf('\n', prev)) >= 0) {
				String line = uidl.substring(prev, index);
				prev = index + 1;
				row++;

				line = WebPaintTarget.escapeXML(line);
				boolean isEmpty = (line.length() == 0 || line.equals("\r"));

				// Highlight source				
				// line = xmlHighlight(line);

				if (!(isEmpty && lastLineWasEmpty))
					sb.append(
						"<tr"
							+ ((row % 10) > 4 ? " bgcolor=\"#eeeeff\"" : "")
							+ "><td style=\"border-right: 1px solid gray\">&nbsp;"
							+ String.valueOf(row)
							+ "&nbsp;</td><td>"
							+ "<nobr>"
							+ line
							+ "</nobr>"
							+ "</td></tr>\n");

				lastLineWasEmpty = isEmpty;
			}

			sb.append("</table>\n");

			return sb.toString();
		}

		/** 
		 * Highlights the XML source.
		 * @param xmlSnippet
		 * @return  
		 */
		private String xmlHighlight(String xmlSnippet) {
			String res = xmlSnippet;

			// Code beautification : Comment lines			
			DebugWindow.replaceAll(
				res,
				"&lt;!--",
				"<SPAN STYLE=\"color: #00dd00\">&lt;!--");
			res = DebugWindow.replaceAll(res, "--&gt;", "--&gt;</SPAN>");

			// nbsp instead of blanks
			String l = "&nbsp;";
			while (res.startsWith(" ")) {
				l += "&nbsp;";
				res = res.substring(1, res.length());
			}
			res = l + res;

			return res;
		}

		/** 
		 * Gets the first fatal error.
		 * @return the fatal error. 
		 */
		public Throwable getFirstFatalError() {
			return (Throwable) fatals.iterator().next();
		}

		/**
		 * @see org.xml.sax.ErrorHandler#error(SAXParseException)
		 */
		public void error(SAXParseException exception) throws SAXException {
			errors.addLast(exception);
			rowToErrorMap.put(
				new Integer(exception.getLineNumber()),
				exception);
			errorToRowMap.put(
				exception,
				new Integer(exception.getLineNumber()));
		}

		/**
		 * @see org.xml.sax.ErrorHandler#fatalError(SAXParseException)
		 */
		public void fatalError(SAXParseException exception)
			throws SAXException {
			fatals.addLast(exception);
			rowToErrorMap.put(
				new Integer(exception.getLineNumber()),
				exception);
			errorToRowMap.put(
				exception,
				new Integer(exception.getLineNumber()));
		}

		/**
		 * @see org.xml.sax.ErrorHandler#warning(SAXParseException)
		 */
		public void warning(SAXParseException exception) throws SAXException {
			warnings.addLast(exception);
			rowToErrorMap.put(
				new Integer(exception.getLineNumber()),
				exception);
			errorToRowMap.put(
				exception,
				new Integer(exception.getLineNumber()));
		}

	}

}
