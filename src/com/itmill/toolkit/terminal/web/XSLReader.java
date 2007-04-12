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

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/** 
 * Class implementing XMLReader for the UIDLTransformer.
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */

public class XSLReader implements XMLReader, ContentHandler {

	static protected final int XSLT_UNKNOWN = 0;
	static protected final int XSLT_XALAN = 1;
	static protected final int XSLT_SAXON6 = 2;
	static protected final int XSLT_SAXON7 = 3;
	static protected final int XSLT_RESIN = 4;
	static protected final int XSLT_WEBLOGIC = 5;
	static protected int xsltProcessor = XSLT_UNKNOWN;
	static {
		String transformerName =
			UIDLTransformer.xsltFactory.getClass().getName();

		// Saxon 7.x
		if ("net.sf.saxon.TransformerFactoryImpl".equals(transformerName))
			xsltProcessor = XSLT_SAXON7;

		// Saxon 6.x
		else if (
			"com.icl.saxon.TransformerFactoryImpl".equals(transformerName))
			xsltProcessor = XSLT_SAXON6;

		// Xalan
		else if (
			"org.apache.xalan.processor.TransformerFactoryImpl".equals(
				transformerName) || 
				"com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl".equals(
						transformerName))
			xsltProcessor = XSLT_XALAN;
		// Resin
		else if ("com.caucho.xsl.Xsl".equals(transformerName))
			xsltProcessor = XSLT_RESIN;
		
		else if ("weblogic.xml.jaxp.RegistrySAXTransformerFactory".equals(transformerName))
			xsltProcessor = XSLT_WEBLOGIC;
		else {
			throw new RuntimeException(
				"\nThis version of IT Mill Toolkit "
					+ " does not support the selected XSLT-processer:\n  "
					+ transformerName
					+ "\n"
					+ "You can specify the used XSLT processor with JVM "
					+ "parameter like: \n"
					+ "  -Djavax.xml.transform.TransformerFactory=net.sf.saxon.TransformerFactoryImpl\n"
					+ "  -Dorg.xml.sax.driver=org.apache.crimson.parser.XMLReaderImpl\n");
		}
	}

	private static final String[] JAVA_PREFIX = {"java://", "millstone://"};
	private Collection streams;
	private boolean startTagHandled = false;
	private String xslNamespace = "";
	private ContentHandler handler;
	private XMLReader reader;

	private XSLStreamLocator locator = null;
	private Locator streamLocator = null;
	private int streamStartLineNumber = 0;

	public XSLReader(XMLReader reader, Collection streams) {
		this.reader = reader;
		reader.setContentHandler(this);
		this.streams = streams;
	}

	/** 
	 * Parses all streams given for constructor parameter.
	 * The input parameter is ignored.
	 * @see org.xml.sax.XMLReader#parse(InputSource)
	 */
	public synchronized void parse(InputSource input)
		throws IOException, SAXException {

		startTagHandled = false;
		handler.startDocument();
		// Parse all files
		for (Iterator i = streams.iterator(); i.hasNext();) {
			ThemeSource.XSLStream xslStream = (ThemeSource.XSLStream) i.next();
			this.locator = new XSLStreamLocator(xslStream.getId());
			InputStream in = (xslStream).getStream();
			
			// Parse the stream
			reader.parse(new InputSource(in));

		}
		handler.endElement(xslNamespace, "stylesheet", "xsl:stylesheet");
		handler.endDocument();
	}
	
	/**
	 * @see org.xml.sax.ContentHandler#endElement(String, String, String)
	 */
	public void endElement(String namespaceURI, String localName, String qName)
		throws SAXException {
		if (localName.equals("stylesheet")) {
			return; //Skip
		}
		handler.endElement(namespaceURI, localName, qName);
	}

	/**
	 * @see org.xml.sax.ContentHandler#processingInstruction(String, String)
	 */
	public void processingInstruction(String target, String data)
		throws SAXException {
		handler.processingInstruction(target, data);
	}

	/**
	 * @see org.xml.sax.ContentHandler#startElement(String, String, String, Attributes)
	 */
	public void startElement(
		String namespaceURI,
		String localName,
		String qName,
		Attributes atts)
		throws SAXException {

		// Only the first stylesheet is used
		if (startTagHandled && localName.equals("stylesheet"))
			return; //skip

		// Get the namespace that will be used for closing the theme
		if (localName.equals("stylesheet")) {
			startTagHandled = true;
			this.xslNamespace = namespaceURI;

			// Manage calls to external functions in XSLT-processor independent
			// way, but still using XSLT 1.0
			handler.startElement(
				namespaceURI,
				localName,
				qName,
				new AttributeMapper(atts));
		} else

			// Handle the element in superclass directly
			handler.startElement(namespaceURI, localName, qName, atts);
	}

	/**
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int length)
		throws SAXException {
		handler.characters(ch, start, length);
	}

	/**
	 * @see org.xml.sax.ContentHandler#startDocument()
	 */
	public void startDocument() throws SAXException {
		// Ignore document starts
	}

	/**
	 * @see org.xml.sax.ContentHandler#endDocument()
	 */
	public void endDocument() throws SAXException {
		//Ignore document ends, but add previous line numbers
		if (this.streamLocator != null) {
			this.streamStartLineNumber += this.streamLocator.getLineNumber();
		}
	}

	/**
	 * @see org.xml.sax.ContentHandler#endPrefixMapping(String)
	 */
	public void endPrefixMapping(String prefix) throws SAXException {
		handler.endPrefixMapping(prefix);
	}

	/**
	 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
	 */
	public void ignorableWhitespace(char[] ch, int start, int length)
		throws SAXException {
		handler.ignorableWhitespace(ch, start, length);
	}

	/**
	 * @see org.xml.sax.ContentHandler#setDocumentLocator(Locator)
	 */
	public void setDocumentLocator(Locator locator) {				
        this.streamLocator = locator;
		// create new locator combined streams/files
		if (!startTagHandled) {
			handler.setDocumentLocator(this.locator);
		}
	}

	/**
	 * @see org.xml.sax.ContentHandler#skippedEntity(String)
	 */
	public void skippedEntity(String name) throws SAXException {
		handler.skippedEntity(name);
	}

	/**
	 * @see org.xml.sax.ContentHandler#startPrefixMapping(String, String)
	 */
	public void startPrefixMapping(String prefix, String uri)
		throws SAXException {
		handler.startPrefixMapping(prefix, uri);
	}

	/** 
	 * Overrides the default content handler.
	 * @see org.xml.sax.XMLReader#getContentHandler()
	 */
	public ContentHandler getContentHandler() {
		return this.handler;
	}

	/** 
	 * Overrides the default content handler.
	 * @see org.xml.sax.XMLReader#setContentHandler(ContentHandler)
	 */
	public void setContentHandler(ContentHandler handler) {
		this.handler = handler;
	}
	
	/**
	 * @see org.xml.sax.XMLReader#getDTDHandler()
	 */
	public DTDHandler getDTDHandler() {
		return reader.getDTDHandler();
	}

	/**
	 * @see org.xml.sax.XMLReader#getEntityResolver()
	 */
	public EntityResolver getEntityResolver() {
		return reader.getEntityResolver();
	}

	/**
	 * @see org.xml.sax.XMLReader#getErrorHandler()
	 */
	public ErrorHandler getErrorHandler() {
		return reader.getErrorHandler();
	}

	/**
	 * @see org.xml.sax.XMLReader#getFeature(String)
	 */
	public boolean getFeature(String name)
		throws SAXNotRecognizedException, SAXNotSupportedException {
		return reader.getFeature(name);
	}

	/**
	 * @see org.xml.sax.XMLReader#getProperty(String)
	 */
	public Object getProperty(String name)
		throws SAXNotRecognizedException, SAXNotSupportedException {
		return reader.getProperty(name);
	}

	/** 
	 * Overrides the parse.
	 * @see org.xml.sax.XMLReader#parse(String)
	 */
	public void parse(String systemId) throws IOException, SAXException {
		this.parse((InputSource) null);
	}

	/**
	 * @see org.xml.sax.XMLReader#setDTDHandler(DTDHandler)
	 */
	public void setDTDHandler(DTDHandler handler) {
		reader.setDTDHandler(handler);
	}

	/**
	 * @see org.xml.sax.XMLReader#setEntityResolver(EntityResolver)
	 */
	public void setEntityResolver(EntityResolver resolver) {
		reader.setEntityResolver(resolver);
	}

	/**
	 * @see org.xml.sax.XMLReader#setErrorHandler(ErrorHandler)
	 */
	public void setErrorHandler(ErrorHandler handler) {
		reader.setErrorHandler(new SAXStreamErrorHandler(handler));
	}

	/**
	 * @see org.xml.sax.XMLReader#setFeature(String, boolean)
	 */
	public void setFeature(String name, boolean value)
		throws SAXNotRecognizedException, SAXNotSupportedException {
		reader.setFeature(name, value);
	}

	/**
	 * @see org.xml.sax.XMLReader#setProperty(String, Object)
	 */
	public void setProperty(String name, Object value)
		throws SAXNotRecognizedException, SAXNotSupportedException {
		reader.setProperty(name, value);
	}

	public class AttributeMapper implements Attributes {

		private Attributes original;
		
		/**
		 * 
		 * @param originalAttributes
		 */
		public AttributeMapper(Attributes originalAttributes) {
			original = originalAttributes;
		}

		/**
		 * @see org.xml.sax.Attributes#getIndex(String, String)
		 */
		public int getIndex(String uri, String localName) {
			return original.getIndex(uri, localName);
		}

		/**
		 * @see org.xml.sax.Attributes#getIndex(String)
		 */
		public int getIndex(String qName) {
			return original.getIndex(qName);
		}

		/**
		 * @see org.xml.sax.Attributes#getLength()
		 */
		public int getLength() {
			return original.getLength();
		}

		/**
		 * @see org.xml.sax.Attributes#getLocalName(int)
		 */
		public String getLocalName(int index) {
			return original.getLocalName(index);
		}

		/**
		 * @see org.xml.sax.Attributes#getQName(int)
		 */
		public String getQName(int index) {
			return original.getQName(index);
		}

		/**
		 * @see org.xml.sax.Attributes#getType(int)
		 */
		public String getType(int index) {
			return original.getType(index);
		}

		/**
		 * @see org.xml.sax.Attributes#getType(String, String)
		 */
		public String getType(String uri, String localName) {
			return original.getType(uri, localName);
		}

		/**
		 * @see org.xml.sax.Attributes#getType(String)
		 */
		public String getType(String qName) {
			return original.getType(qName);
		}

		/**
		 * @see org.xml.sax.Attributes#getURI(int)
		 */
		public String getURI(int index) {
			String uri = original.getURI(index);

			for (int i=0; i<JAVA_PREFIX.length; i++)
			if (uri != null && uri.startsWith(JAVA_PREFIX[i])) {

				System.out.print("DEBUG " + uri + " --> ");
				switch (xsltProcessor) {
					case XSLT_SAXON6 :
						uri =
							"saxon://"
								+ uri.substring(JAVA_PREFIX[i].length());
						break;
					case XSLT_SAXON7 :
						uri =
							"saxon://"
								+ uri.substring(JAVA_PREFIX[i].length());
						break;
					case XSLT_XALAN :
						uri =
							"xalan://"
								+ uri.substring(JAVA_PREFIX[i].length());
						break;
					default :
						uri =
							"xalan://"
								+ uri.substring(JAVA_PREFIX[i].length());
						break;
				}
				System.out.println(uri);
			}

			return uri;
		}

		/**
		 * @see org.xml.sax.Attributes#getValue(int)
		 */
		public String getValue(int index) {
			return original.getValue(index);
		}

		/**
		 * @see org.xml.sax.Attributes#getValue(String, String)
		 */
		public String getValue(String uri, String localName) {
			return original.getValue(uri, localName);
		}

		/**
		 * @see org.xml.sax.Attributes#getValue(String)
		 */
		public String getValue(String qName) {
			return original.getValue(qName);
		}
	}

	public class XSLStreamLocator implements Locator {

		private String id;
		
/**
 * 
 * @param id
 */
		public XSLStreamLocator(String id) {
			this.id = id;
		}
		
		/**
		 * 
		 * @see org.xml.sax.Locator#getPublicId()
		 */
		public String getPublicId() {
			return streamLocator.getPublicId();
		}
		
		/**
		 * 
		 * @see org.xml.sax.Locator#getSystemId()
		 */
		public String getSystemId() {
			return streamLocator.getSystemId()+""+id;
		}
		/**
		 * 
		 * @see org.xml.sax.Locator#getLineNumber()
		 */
		public int getLineNumber() {
			return streamLocator.getLineNumber();
		}
	
		public int getCombinedLineNumber() {
			return streamLocator.getLineNumber()+streamStartLineNumber;
		}
		
		/**
		 * @see org.xml.sax.Locator#getColumnNumber()
		 */
		public int getColumnNumber() {
			return streamLocator.getColumnNumber();
		}
		
		/**
		 * Gets the id.
		 * @return the id .
		 */
		public String getId() {
			return id;
		}
	}

	public class SAXStreamErrorHandler implements ErrorHandler {

		private ErrorHandler handler;
		
/**
 * 
 * @param origHandler
 */
		SAXStreamErrorHandler(ErrorHandler origHandler) {
			this.handler = origHandler;
		}
		
		/**
		 * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
		 */
		public void warning(SAXParseException exception) throws SAXException {
			handler.warning(
				new SAXParseException(
					"" + exception.getMessage()+" in "+locator.getId(),
					locator,
					exception));
		}
		
		/**
		 * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
		 */
		public void error(SAXParseException exception) throws SAXException {
			handler.error(
				new SAXParseException(
					"" + exception.getMessage()+" in "+locator.getId(),
					locator,
					exception));
		}
		
		/**
		 * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
		 */
		public void fatalError(SAXParseException exception)
			throws SAXException {
			handler.fatalError(
				new SAXParseException(
					"" + exception.getMessage()+" in "+locator.getId(),
					locator,
					exception));
		}
	}
}
