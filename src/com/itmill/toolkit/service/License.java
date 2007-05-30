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

package com.itmill.toolkit.service;

import java.io.ByteArrayInputStream;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Iterator;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class License {

	/**
	 * IT Mill License Manager certificate.
	 */
	private static String certificate = "-----BEGIN CERTIFICATE-----\n"
			+ "MIIDJjCCAuQCBEVqxNwwCwYHKoZIzjgEAwUAMHkxCzAJBgNVBAYTAkZJMRAwDgYDVQQIEwdVbmtu\n"
			+ "b3duMQ4wDAYDVQQHEwVUdXJrdTEUMBIGA1UEChMLSVQgTWlsbCBMdGQxEDAOBgNVBAsTB1Vua25v\n"
			+ "d24xIDAeBgNVBAMTF0lUIE1pbGwgTGljZW5zZSBNYW5hZ2VyMB4XDTA2MTEyNzEwNTgzNloXDTQ3\n"
			+ "MTIyMjEwNTgzNloweTELMAkGA1UEBhMCRkkxEDAOBgNVBAgTB1Vua25vd24xDjAMBgNVBAcTBVR1\n"
			+ "cmt1MRQwEgYDVQQKEwtJVCBNaWxsIEx0ZDEQMA4GA1UECxMHVW5rbm93bjEgMB4GA1UEAxMXSVQg\n"
			+ "TWlsbCBMaWNlbnNlIE1hbmFnZXIwggG3MIIBLAYHKoZIzjgEATCCAR8CgYEA/X9TgR11EilS30qc\n"
			+ "Luzk5/YRt1I870QAwx4/gLZRJmlFXUAiUftZPY1Y+r/F9bow9subVWzXgTuAHTRv8mZgt2uZUKWk\n"
			+ "n5/oBHsQIsJPu6nX/rfGG/g7V+fGqKYVDwT7g/bTxR7DAjVUE1oWkTL2dfOuK2HXKu/yIgMZndFI\n"
			+ "AccCFQCXYFCPFSMLzLKSuYKi64QL8Fgc9QKBgQD34aCF1ps93su8q1w2uFe5eZSvu/o66oL5V0wL\n"
			+ "PQeCZ1FZV4661FlP5nEHEIGAtEkWcSPoTCgWE7fPCTKMyKbhPBZ6i1R8jSjgo64eK7OmdZFuo38L\n"
			+ "+iE1YvH7YnoBJDvMpPG+qFGQiaiD3+Fa5Z8GkotmXoB7VSVkAUw7/s9JKgOBhAACgYB2wjpuZXqK\n"
			+ "Ldgw1uZRlNCON7vo4m420CSna0mhETqzW9UMFHmZfn9edD0B1dDh6NwmRIDjljf8+ODuhwZKkzl8\n"
			+ "DHUq3HPnipEsr0C3g1Dz7ZbjcvUhzsPDElpKBZhHRaoqfAfWiNxeVF2Kh2IlIMwuJ2xZeSaUH7Pj\n"
			+ "LwAkKye6dzALBgcqhkjOOAQDBQADLwAwLAIUDgvWt7ItRyZfpWNEeJ0P9yaxOwoCFC21LRtwLi1t\n"
			+ "c+yomHtX+mpxF7VO\n" + "-----END CERTIFICATE-----\n";

	/**
	 * License XML Document.
	 */
	private Document licenseXML = null;

	private boolean hasBeenRead;

	/**
	 * License properties
	 */
	private String number;

	private String product;

	private String edition;

	private String versionDescription;

	private String licenseeName;

	private String purpose;

	private int maxConcurrentUsers;

	private int maxJVMs;

	private String validUntilYear;

	private String validUntilMonth;

	private String validUntilDay;

	private String classPrefix;

	private int equalsToMajor;

	private int equalsToOrIsLessThanMajor;

	private int equalsToOrIsMoreThanMajor;

	private int equalsToMinor;

	private int equalsToOrIsLessThanMinor;

	private int equalsToOrIsMoreThanMinor;

	private boolean printLimitsOnInit;

	/**
	 * The signature has already been checked and is valid.
	 */
	private boolean signatureIsValid = false;

	/**
	 * Reads the license-file from input stream.
	 * 
	 * License file can only ne read once, after it has been read it stays.
	 * 
	 * @param is
	 *            Input stream where the license file is read from.
	 * @throws SAXException
	 *             Error parsing the license file
	 * @throws IOException
	 *             Error reading the license file
	 * @throws LicenseSignatureIsInvalid
	 * @throws LicenseFileHasNotBeenRead
	 * @throws InvalidLicenseFile
	 */
	public void readLicenseFile(InputStream is) throws SAXException,
			IOException, InvalidLicenseFile, LicenseFileHasNotBeenRead,
			LicenseSignatureIsInvalid {

		// Once the license has been read, it stays
		if (hasBeenRead())
			return;

		// Parse XML
		DocumentBuilder db;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			licenseXML = db.parse(is);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}

		// Check validity of the signature
		if (!isSignatureValid())
			throw new LicenseSignatureIsInvalid();

		// Read license contents to Java properties
		number = this.getLicenseNumber();
		product = this.getProductName();
		edition = this.getProductEdition();
		versionDescription = this.getVersionDescription();
		licenseeName = this.getLicenseeName();
		purpose = this.getPurpose();
		maxConcurrentUsers = this.getMaxConcurrentUsers();
		maxJVMs = this.getMaxJVMs();
		classPrefix = getClassPrefix();
		printLimitsOnInit = getPrintLimitsOnInit();
		parseValidUntil();
		parseVersion();

		hasBeenRead = true;
	}

	/**
	 * Is the license file already been read.
	 * 
	 * @return true if the license-file has already been read.
	 */
	public boolean hasBeenRead() {
		return hasBeenRead;
	}

	/** Should the license description be printed on (first) application init. */
	public boolean shouldLimitsBePrintedOnInit() {
		return printLimitsOnInit;
	}

	public boolean getPrintLimitsOnInit() throws LicenseFileHasNotBeenRead,
			LicenseSignatureIsInvalid, InvalidLicenseFile {
		// default value
		printLimitsOnInit = true;

		NodeList lL = licenseXML.getElementsByTagName("limits");
		if (lL == null || lL.getLength() == 0)
			throw new InvalidLicenseFile("limits not found from license-file");

		Element e = (Element) lL.item(0);
		String print = e.getAttribute("print-limits-on-init");

		return "true".equalsIgnoreCase(print);
	}

	/**
	 * Gets the description for this license.
	 * 
	 * @return the description.
	 * @throws LicenseFileHasNotBeenRead
	 *             if the license file has not been read.
	 * @throws InvalidLicenseFile
	 *             if the license file is not of correct XML format.
	 * @throws LicenseSignatureIsInvalid
	 *             if the license file has been changed or signature is
	 *             otherwise invalid.
	 */
	public String getDescription(String target)
			throws LicenseFileHasNotBeenRead, InvalidLicenseFile,
			LicenseSignatureIsInvalid {

		StringBuffer d = new StringBuffer();
		d.append("------------------ License Info -----------------------\n");

		d.append("Product: " + product);
		if (edition != null)
			d.append("  Edition: " + edition);
		d.append("\n");

		d.append("Target: " + target + "\n");

		d.append("License number: " + number + "\n");

		// Print version info
		if (versionDescription != null)
			d.append("Version: " + versionDescription + "\n");

		if (this.licenseeName != null)
			d.append("Licensed to: " + licenseeName + "\n");

		if (purpose != null)
			d.append("Use is limited to: " + purpose + "\n");

		if (maxConcurrentUsers >= 0)
			d.append("Maximum number of concurrent (active) users allowed: "
					+ maxConcurrentUsers + "\n");

		if (maxJVMs >= 0)
			d.append("Maximum number of JVM:s this license"
					+ " can be used concurrently: " + maxJVMs + "\n");

		// Print valid-until date
		NodeList vuL = licenseXML.getElementsByTagName("valid-until");
		if (vuL != null && vuL.getLength() > 0) {
			Element e = (Element) vuL.item(0);
			String year = e.getAttribute("year");
			String month = e.getAttribute("month");
			String day = e.getAttribute("day");
			d.append("License is valid until: " + year + "-" + month + "-"
					+ day + "\n");
		}

		// Print application info
		NodeList aL = licenseXML.getElementsByTagName("application");
		if (aL != null && aL.getLength() > 0) {
			Element e = (Element) aL.item(0);
			String app = e.getAttribute("name");
			String purpose = e.getAttribute("purpose");
			String prefix = e.getAttribute("classPrefix");
			if (app != null && app.length() > 0)
				d.append("For use with this application only: " + app + "\n");
			if (app != null && app.length() > 0)
				d.append("Application usage purpose is limited to: " + purpose
						+ "\n");
			if (app != null && app.length() > 0)
				d.append("Application class name must match prefix: " + prefix
						+ "\n");
		}

		d.append("-------------------------------------------------------\n");

		return d.toString();
	}

	/**
	 * Checks if the license valid for given usage?
	 * 
	 * Checks that the license is valid for specified usage. Throws an exception
	 * if there is something wrong with the license or use.
	 * 
	 * @param applicationClass
	 *            Class of the application this license is used for
	 * @param concurrentUsers
	 *            Number if users concurrently using this application
	 * @param majorVersion
	 *            Major version number (for example 4 if version is 4.1.7)
	 * @param minorVersion
	 *            Minor version number (for example 1 if version is 4.1.7)
	 * @param productName
	 *            The name of the product
	 * @param productEdition
	 *            The name of the product edition
	 * @throws LicenseFileHasNotBeenRead
	 *             if the license file has not been read
	 * @throws LicenseSignatureIsInvalid
	 *             if the license file has been changed or signature is
	 *             otherwise invalid
	 * @throws InvalidLicenseFile
	 *             License if the license file is not of correct XML format
	 * @throws LicenseViolation
	 * 
	 */
	public void check(Class applicationClass, int majorVersion,
			int minorVersion, String productName, String productEdition)
			throws LicenseFileHasNotBeenRead, LicenseSignatureIsInvalid,
			InvalidLicenseFile, LicenseViolation {
		// Check usage
		checkProductNameAndEdition(productName, productEdition);
		checkVersion(majorVersion, minorVersion);
		checkApplicationClass(applicationClass);
		checkValidUntil();
	}

	private void parseValidUntil() {
		NodeList vuL = licenseXML.getElementsByTagName("valid-until");
		if (vuL != null && vuL.getLength() > 0) {
			Element e = (Element) vuL.item(0);
			validUntilYear = e.getAttribute("year");
			validUntilMonth = e.getAttribute("month");
			validUntilDay = e.getAttribute("day");
		}
	}

	private void parseVersion() {
		// by default version check is not active
		equalsToMajor = -1;
		equalsToOrIsLessThanMajor = -1;
		equalsToOrIsMoreThanMajor = -1;
		equalsToMinor = -1;
		equalsToOrIsLessThanMinor = -1;
		equalsToOrIsMoreThanMinor = -1;

		// parse version
		NodeList verL = licenseXML.getElementsByTagName("version");
		if (verL != null && verL.getLength() > 0) {
			NodeList checks = verL.item(0).getChildNodes();
			for (int i = 0; i < checks.getLength(); i++) {
				Node n = checks.item(i);
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) n;
					String tag = e.getTagName();
					String eq = e.getAttribute("equals-to");
					String eqol = e.getAttribute("equals-to-or-is-less-than");
					String eqom = e.getAttribute("equals-to-or-is-more-than");
					if ("major".equalsIgnoreCase(tag)) {
						if (eq != null && eq.length() > 0) {
							equalsToMajor = Integer.parseInt(eq);
						} else if (eqol != null && eqol.length() > 0) {
							equalsToOrIsLessThanMajor = Integer.parseInt(eqol);
						} else if (eqom != null && eqom.length() > 0) {
							equalsToOrIsMoreThanMajor = Integer.parseInt(eqom);
						}
					} else if ("minor".equalsIgnoreCase(tag)) {
						if (eq != null && eq.length() > 0) {
							equalsToMinor = Integer.parseInt(eq);
						} else if (eqol != null && eqol.length() > 0) {
							equalsToOrIsLessThanMinor = Integer.parseInt(eqol);
						} else if (eqom != null && eqom.length() > 0) {
							equalsToOrIsMoreThanMinor = Integer.parseInt(eqom);
						}
					}
				}
			}
		}
	}

	private String getClassPrefix() {
		NodeList appL = licenseXML.getElementsByTagName("application");
		if (appL != null && appL.getLength() > 0) {
			return ((Element) appL.item(0)).getAttribute("classPrefix");
		}
		return null;
	}

	private int getMaxConcurrentUsers() {
		NodeList cuL = licenseXML
				.getElementsByTagName("concurrent-users-per-server");
		if (cuL == null && cuL.getLength() == 0)
			return -1;
		String limit = ((Element) cuL.item(0)).getAttribute("limit");
		if (limit != null && limit.length() > 0
				&& !limit.equalsIgnoreCase("unlimited"))
			return Integer.parseInt(limit);
		return -1;
	}

	private int getMaxJVMs() {
		NodeList cuL = licenseXML.getElementsByTagName("concurrent-jvms");
		if (cuL == null && cuL.getLength() == 0)
			return -1;
		Element e = (Element) cuL.item(0);
		String limit = e == null ? null : e.getAttribute("limit");
		if (limit != null && limit.length() > 0
				&& !limit.equalsIgnoreCase("unlimited"))
			return Integer.parseInt(limit);
		return -1;
	}

	private String getVersionDescription() {

		StringBuffer v = new StringBuffer();

		NodeList verL = licenseXML.getElementsByTagName("version");
		if (verL != null && verL.getLength() > 0) {
			NodeList checks = verL.item(0).getChildNodes();
			for (int i = 0; i < checks.getLength(); i++) {
				Node n = checks.item(i);
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) n;
					String tag = e.getTagName();
					appendVersionDescription(e.getAttribute("equals-to"), v,
							tag, "=");
					appendVersionDescription(e
							.getAttribute("equals-to-or-is-less-than"), v, tag,
							"<=");
					appendVersionDescription(e
							.getAttribute("equals-to-or-is-more-than"), v, tag,
							">=");
				}
			}
		}

		if (v.length() == 0)
			return null;
		return v.toString();
	}

	private void appendVersionDescription(String num, StringBuffer v,
			String tag, String relation) {
		if (num == null || num.length() == 0)
			return;
		if (v.length() > 0)
			v.append(" and ");
		v.append(tag + " version " + relation + " " + num);
	}

	private String getProductEdition() throws InvalidLicenseFile {
		Element prod = (Element) licenseXML.getElementsByTagName("product")
				.item(0);
		if (prod == null)
			throw new InvalidLicenseFile("product not found in license-file");
		NodeList editionE = (NodeList) prod.getElementsByTagName("edition");
		if (editionE == null || editionE.getLength() == 0)
			return null;
		return getTextContent(editionE.item(0));
	}

	private String getProductName() throws InvalidLicenseFile {
		Element prod = (Element) licenseXML.getElementsByTagName("product")
				.item(0);
		if (prod == null)
			throw new InvalidLicenseFile("product not found in license-file");
		String name = getTextContent(((Element) prod.getElementsByTagName(
				"name").item(0)));
		if (name == null || name.length() == 0)
			throw new InvalidLicenseFile(
					"product name not found in license-file");
		return name;
	}

	private String getTextContent(Node n) {
		String val = "";
		if (n.getChildNodes().getLength() > 0) {
			for (int i = 0; i < n.getChildNodes().getLength(); i++)
				if (n.getChildNodes().item(i).getNodeType() == Node.TEXT_NODE)
					val += n.getChildNodes().item(i).getNodeValue();
		}
		return val;
	}

	private String getLicenseeName() {
		NodeList licenseeL = licenseXML.getElementsByTagName("licensee");
		if (licenseeL == null || licenseeL.getLength() == 0)
			return null;
		NodeList nameL = ((Element) licenseeL.item(0))
				.getElementsByTagName("name");
		if (nameL == null || nameL.getLength() == 0)
			return null;
		String name = getTextContent(nameL.item(0));
		if (name == null || name.length() == 0)
			return null;
		return name;
	}

	/**
	 * Gets the purpose for this license.
	 * 
	 * @return the purpose.
	 */
	private String getPurpose() {
		NodeList purposeL = licenseXML.getElementsByTagName("purpose");
		if (purposeL == null || purposeL.getLength() == 0)
			return null;
		return getTextContent(purposeL.item(0));
	}

	/**
	 * Gets the license number for this license.
	 * 
	 * @return the license number of this license, or <code>null</code> if not
	 *         set.
	 * @throws InvalidLicenseFile
	 *             if the license file is not of correct XML format.
	 */
	private String getLicenseNumber() throws InvalidLicenseFile {
		Element lic = (Element) licenseXML.getElementsByTagName("license")
				.item(0);
		if (lic == null)
			throw new InvalidLicenseFile(
					"license element not found in license-file");
		return lic.getAttribute("number");
	}

	private String getNormalizedLicenseData() throws InvalidLicenseFile,
			LicenseFileHasNotBeenRead {

		// License must be read before
		if (licenseXML == null)
			throw new LicenseFileHasNotBeenRead();

		// Initialize result
		CharArrayWriter sink = new CharArrayWriter();

		// Serialize document to sink
		try {
			serialize(licenseXML, sink);
		} catch (IOException e) {
			throw new InvalidLicenseFile("Can not serialize the license file.");
		}

		return new String(sink.toCharArray());
	}

	private static void serialize(Node node, Writer sink) throws IOException {

		// Do not serialize comments and processing instructions
		if (node.getNodeType() == Node.COMMENT_NODE
				|| node.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE)
			return;

		// Do not serialize whitespace text-nodes
		if (node.getNodeType() == Node.TEXT_NODE) {
			String value = node.getNodeValue();
			if (value.matches("\\A\\s*\\z"))
				return;
		}

		// Do not serialize signature
		if (node.getNodeType() == Node.ELEMENT_NODE
				&& "signature".equals(node.getNodeName()))
			return;

		// Serialize node name
		sink.write(node.getNodeName().toLowerCase());

		// Serialize value of the node
		String value = node.getNodeValue();
		if (value != null)
			sink.write("='" + value + "'");

		// Serialize attributes if it has any, in sorted order
		NamedNodeMap attrs = node.getAttributes();
		if (attrs != null) {
			TreeSet names = new TreeSet();
			for (int i = 0; i < attrs.getLength(); i++)
				names.add(attrs.item(i).getNodeName());
			for (Iterator i = names.iterator(); i.hasNext();)
				serialize(attrs.getNamedItem((String) i.next()), sink);
		}

		// Serialize child nodes (other than attributes)
		Node child = node.getFirstChild();
		if (child != null && node.getNodeType() != Node.ATTRIBUTE_NODE) {
			sink.write("{");
			while (child != null) {
				if (child.getNodeType() != Node.ATTRIBUTE_NODE)
					serialize(child, sink);
				child = child.getNextSibling();
			}
			sink.write("}");
		}

	}

	private byte[] getSignature() throws InvalidLicenseFile {

		if (licenseXML == null)
			return null;

		// Get the base64 encoded signature from license-file
		NodeList nl = licenseXML.getElementsByTagName("signature");
		if (nl == null || nl.getLength() != 1)
			throw new InvalidLicenseFile("Signature element not found");
		Node text = nl.item(0).getFirstChild();
		if (text == null || text.getNodeType() != Node.TEXT_NODE)
			throw new InvalidLicenseFile("Invalid signature element");
		String base64 = text.getNodeValue();

		return base64_decode(base64);

	}

	/**
	 * Returns whether the signature is valid or not.
	 * 
	 * @return <code>true</code> if the signature is valid otherwise
	 *         <code>false</code> .
	 * @throws InvalidLicenseFile
	 *             if the license file is not of correct XML format.
	 * @throws LicenseFileHasNotBeenRead
	 *             if the license file has not been read.
	 * @throws LicenseSignatureIsInvalid
	 *             if the license file has been changed or signature is
	 *             otherwise invalid.
	 */
	private synchronized boolean isSignatureValid() throws InvalidLicenseFile,
			LicenseFileHasNotBeenRead, LicenseSignatureIsInvalid {

		if (signatureIsValid)
			return true;

		try {
			// Get X.509 factory implementation
			CertificateFactory x509factory = CertificateFactory
					.getInstance("X.509");

			// Decode statically linked X.509 certificate
			X509Certificate cert = (X509Certificate) x509factory
					.generateCertificate(new ByteArrayInputStream(certificate
							.getBytes()));

			PublicKey publicKey = cert.getPublicKey();

			// Verify signature with DSA
			Signature dsa = Signature.getInstance("SHA1withDSA");
			dsa.initVerify(publicKey);
			dsa.update(getNormalizedLicenseData().getBytes("UTF-8"));
			if (dsa.verify(getSignature())) {
				signatureIsValid = true;
				return true;
			}
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (InvalidKeyException e) {
			throw new RuntimeException(e);
		} catch (SignatureException e) {
			throw new LicenseSignatureIsInvalid();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} catch (CertificateException e) {
			throw new RuntimeException(e);
		}

		// Verification failed
		return false;
	}

	private void checkApplicationClass(Class applicationClass)
			throws LicenseViolation {
		if (classPrefix != null && classPrefix.length() > 0
				&& !applicationClass.getName().startsWith(classPrefix))
			throw new LicenseViolation(
					"License limits application class prefix to '"
							+ classPrefix
							+ "' but requested application class is '"
							+ applicationClass.getName() + "'");
	}

	public void checkConcurrentUsers(int concurrentUsers)
			throws TooManyConcurrentUsers {
		if (maxConcurrentUsers >= 0 && concurrentUsers > maxConcurrentUsers)
			throw new TooManyConcurrentUsers(
					"Currently "
							+ concurrentUsers
							+ " concurrent users are connected, while license sets limit to "
							+ maxConcurrentUsers);

	}

	private void checkValidUntil() throws LicenseViolation {
		Calendar cal = Calendar.getInstance();
		if ((validUntilYear != null && validUntilYear.length() > 0 && Integer
				.parseInt(validUntilYear) < cal.get(Calendar.YEAR))
				|| (validUntilMonth != null && validUntilMonth.length() > 0 && Integer
						.parseInt(validUntilMonth) < (1 + cal
						.get(Calendar.MONTH)))
				|| (validUntilDay != null && validUntilDay.length() > 0 && Integer
						.parseInt(validUntilDay) < cal
						.get(Calendar.DAY_OF_MONTH)))
			throw new LicenseViolation("The license is valid until "
					+ validUntilYear + "-" + validUntilMonth + "-"
					+ validUntilDay);
	}

	private void checkVersion(int majorVersion, int minorVersion)
			throws LicenseViolation {
		// check minor version
		if ((equalsToMinor != -1) && (equalsToMinor != minorVersion))
			throw new LicenseViolation("Product minor version is "
					+ minorVersion + " but license requires it to be "
					+ equalsToMinor);

		if ((equalsToOrIsLessThanMinor != -1)
				&& (equalsToOrIsLessThanMinor < minorVersion))
			throw new LicenseViolation("Product minor version is "
					+ minorVersion
					+ " but license requires it to be equal or less than"
					+ equalsToOrIsLessThanMinor);

		if ((equalsToOrIsMoreThanMinor != -1)
				&& (equalsToOrIsMoreThanMinor > minorVersion))
			throw new LicenseViolation("Product minor version is "
					+ minorVersion
					+ " but license requires it to be equal or more than"
					+ equalsToOrIsMoreThanMinor);

		// check major version
		if ((equalsToMajor != -1) && (equalsToMajor != majorVersion))
			throw new LicenseViolation("Product major version is "
					+ majorVersion + " but license requires it to be "
					+ equalsToMajor);

		if ((equalsToOrIsLessThanMajor != -1)
				&& (equalsToOrIsLessThanMajor < majorVersion))
			throw new LicenseViolation("Product major version is "
					+ majorVersion
					+ " but license requires it to be equal or less than"
					+ equalsToOrIsLessThanMajor);

		if ((equalsToOrIsMoreThanMajor != -1)
				&& (equalsToOrIsMoreThanMajor > majorVersion))
			throw new LicenseViolation("Product major version is "
					+ majorVersion
					+ " but license requires it to be equal or more than"
					+ equalsToOrIsMoreThanMajor);

	}

	private void checkProductNameAndEdition(String productName,
			String productEdition) throws InvalidLicenseFile, LicenseViolation {
		// Check product name
		if (productName == null || productName.length() == 0)
			throw new IllegalArgumentException(
					"productName must not be empty or null");
		if (productEdition != null && productEdition.length() == 0)
			throw new IllegalArgumentException(
					"productEdition must either be null (not present) or non-empty string");
		if (!product.equals(productName))
			throw new LicenseViolation("The license file is for product '"
					+ product + "' but it was requested to be used with '"
					+ productName + "'");

		// Check product edition
		if (productEdition != null || edition != null)
			if (edition == null || !edition.equals(productEdition))
				throw new LicenseViolation("Requested edition '"
						+ productEdition + "', but license-file is for '"
						+ edition + "'");

	}

	public class LicenseViolation extends Exception {

		private static final long serialVersionUID = -1907168254265902098L;

		public LicenseViolation(String msg) {
			super(msg);
		}
	}

	public class LicenseFileHasNotBeenRead extends Exception {
		private static final long serialVersionUID = -2298284163422128284L;
	}

	public class LicenseSignatureIsInvalid extends Exception {
		private static final long serialVersionUID = -1931663861678614927L;
	}

	public class TooManyConcurrentUsers extends LicenseViolation {
		private static final long serialVersionUID = -2388655757006023599L;

		public TooManyConcurrentUsers(String msg) {
			super(msg);
		}
	}

	public class LicenseHasExpired extends LicenseViolation {

		private static final long serialVersionUID = -8631335939895587668L;

		public LicenseHasExpired(String msg) {
			super(msg);
		}
	}

	public class ApplicationClassNameDoesNotMatch extends LicenseViolation {

		private static final long serialVersionUID = 5837570791695513847L;

		public ApplicationClassNameDoesNotMatch(String msg) {
			super(msg);
		}
	}

	public class InvalidLicenseFile extends Exception {

		private static final long serialVersionUID = -1624980115082095017L;

		InvalidLicenseFile(String message) {
			super(message);
		}
	}

	public class LicenseFileCanNotBeRead extends Exception {

		private static final long serialVersionUID = -362237440802937818L;

		LicenseFileCanNotBeRead(String message) {
			super(message);
		}

	}

	/* ****** BASE64 implementation created by Robert Harder ****** */
	/**
	 * The equals sign (=) as a byte.
	 */
	private final static byte Base64_EQUALS_SIGN = (byte) '=';

	/**
	 * Preferred encoding.
	 */
	private final static String Base64_PREFERRED_ENCODING = "UTF-8";

	/**
	 * Translates a Base64 value to either its 6-bit reconstruction value or a
	 * negative number indicating some other meaning.
	 */
	private final static byte[] Base64_DECODABET = { -9, -9, -9, -9, -9, -9,
			-9, -9, -9, // Decimal 0 - 8
			-5, -5, // Whitespace: Tab and Linefeed
			-9, -9, // Decimal 11 - 12
			-5, // Whitespace: Carriage Return
			-9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, // Decimal 14 -
			// 26
			-9, -9, -9, -9, -9, // Decimal 27 - 31
			-5, // Whitespace: Space
			-9, -9, -9, -9, -9, -9, -9, -9, -9, -9, // Decimal 33 - 42
			62, // Plus sign at decimal 43
			-9, -9, -9, // Decimal 44 - 46
			63, // Slash at decimal 47
			52, 53, 54, 55, 56, 57, 58, 59, 60, 61, // Numbers zero through nine
			-9, -9, -9, // Decimal 58 - 60
			-1, // Equals sign at decimal 61
			-9, -9, -9, // Decimal 62 - 64
			0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, // Letters 'A'
			// through 'N'
			14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, // Letters 'O'
			// through 'Z'
			-9, -9, -9, -9, -9, -9, // Decimal 91 - 96
			26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, // Letters 'a'
			// through 'm'
			39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, // Letters 'n'
			// through 'z'
			-9, -9, -9, -9 // Decimal 123 - 126
	};

	// I think I end up not using the BAD_ENCODING indicator.
	// private final static byte BAD_ENCODING = -9; // Indicates error in
	// encoding
	private final static byte Base64_WHITE_SPACE_ENC = -5; // Indicates white

	// space in encoding

	private final static byte Base64_EQUALS_SIGN_ENC = -1; // Indicates equals

	// sign in encoding
	/**
	 * Decodes four bytes from array <var>source</var> and writes the resulting
	 * bytes (up to three of them) to <var>destination</var>. The source and
	 * destination arrays can be manipulated anywhere along their length by
	 * specifying <var>srcOffset</var> and <var>destOffset</var>.
	 * <p>
	 * This method does not check to make sure your arrays are large enough to
	 * accomodate <var>srcOffset</var> + 4 for the <var>source</var> array or
	 * <var>destOffset</var> + 3 for the <var>destination</var> array. This
	 * method returns the actual number of bytes that were converted from the
	 * Base64 encoding.
	 * </p>
	 * 
	 * @param source
	 *            the array to convert.
	 * @param srcOffset
	 *            the index where conversion begins.
	 * @param destination
	 *            the array to hold the conversion.
	 * @param destOffset
	 *            the index where output will be put.
	 * @return the number of decoded bytes converted
	 * @since 1.3
	 */
	private static int base64_decode4to3(byte[] source, int srcOffset,
			byte[] destination, int destOffset) {
		// Example: Dk==
		if (source[srcOffset + 2] == Base64_EQUALS_SIGN) {
			// Two ways to do the same thing. Don't know which way I like best.
			// int outBuff = ( ( DECODABET[ source[ srcOffset ] ] << 24 ) >>> 6
			// )
			// | ( ( DECODABET[ source[ srcOffset + 1] ] << 24 ) >>> 12 );
			int outBuff = ((Base64_DECODABET[source[srcOffset]] & 0xFF) << 18)
					| ((Base64_DECODABET[source[srcOffset + 1]] & 0xFF) << 12);

			destination[destOffset] = (byte) (outBuff >>> 16);
			return 1;
		}

		// Example: DkL=
		else if (source[srcOffset + 3] == Base64_EQUALS_SIGN) {
			// Two ways to do the same thing. Don't know which way I like best.
			// int outBuff = ( ( DECODABET[ source[ srcOffset ] ] << 24 ) >>> 6
			// )
			// | ( ( DECODABET[ source[ srcOffset + 1 ] ] << 24 ) >>> 12 )
			// | ( ( DECODABET[ source[ srcOffset + 2 ] ] << 24 ) >>> 18 );
			int outBuff = ((Base64_DECODABET[source[srcOffset]] & 0xFF) << 18)
					| ((Base64_DECODABET[source[srcOffset + 1]] & 0xFF) << 12)
					| ((Base64_DECODABET[source[srcOffset + 2]] & 0xFF) << 6);

			destination[destOffset] = (byte) (outBuff >>> 16);
			destination[destOffset + 1] = (byte) (outBuff >>> 8);
			return 2;
		}

		// Example: DkLE
		else {
			try {
				// Two ways to do the same thing. Don't know which way I like
				// best.
				// int outBuff = ( ( DECODABET[ source[ srcOffset ] ] << 24 )
				// >>> 6 )
				// | ( ( DECODABET[ source[ srcOffset + 1 ] ] << 24 ) >>> 12 )
				// | ( ( DECODABET[ source[ srcOffset + 2 ] ] << 24 ) >>> 18 )
				// | ( ( DECODABET[ source[ srcOffset + 3 ] ] << 24 ) >>> 24 );
				int outBuff = ((Base64_DECODABET[source[srcOffset]] & 0xFF) << 18)
						| ((Base64_DECODABET[source[srcOffset + 1]] & 0xFF) << 12)
						| ((Base64_DECODABET[source[srcOffset + 2]] & 0xFF) << 6)
						| ((Base64_DECODABET[source[srcOffset + 3]] & 0xFF));

				destination[destOffset] = (byte) (outBuff >> 16);
				destination[destOffset + 1] = (byte) (outBuff >> 8);
				destination[destOffset + 2] = (byte) (outBuff);

				return 3;
			} catch (Exception e) {
				System.out.println("" + source[srcOffset] + ": "
						+ (Base64_DECODABET[source[srcOffset]]));
				System.out.println("" + source[srcOffset + 1] + ": "
						+ (Base64_DECODABET[source[srcOffset + 1]]));
				System.out.println("" + source[srcOffset + 2] + ": "
						+ (Base64_DECODABET[source[srcOffset + 2]]));
				System.out.println("" + source[srcOffset + 3] + ": "
						+ (Base64_DECODABET[source[srcOffset + 3]]));
				return -1;
			} // e nd catch
		}
	} // end decodeToBytes

	/**
	 * Very low-level access to decoding ASCII characters in the form of a byte
	 * array. Does not support automatically gunzipping or any other "fancy"
	 * features.
	 * 
	 * @param source
	 *            the Base64 encoded data.
	 * @param off
	 *            the offset of where to begin decoding.
	 * @param len
	 *            the length of characters to decode.
	 * @return decoded data
	 * @since 1.3
	 */
	private static byte[] base64_decode(byte[] source, int off, int len) {
		int len34 = len * 3 / 4;
		byte[] outBuff = new byte[len34]; // Upper limit on size of output
		int outBuffPosn = 0;

		byte[] b4 = new byte[4];
		int b4Posn = 0;
		int i = 0;
		byte sbiCrop = 0;
		byte sbiDecode = 0;
		for (i = off; i < off + len; i++) {
			sbiCrop = (byte) (source[i] & 0x7f); // Only the low seven bits
			sbiDecode = Base64_DECODABET[sbiCrop];

			if (sbiDecode >= Base64_WHITE_SPACE_ENC) // White space, Equals
			// sign or better
			{
				if (sbiDecode >= Base64_EQUALS_SIGN_ENC) {
					b4[b4Posn++] = sbiCrop;
					if (b4Posn > 3) {
						outBuffPosn += base64_decode4to3(b4, 0, outBuff,
								outBuffPosn);
						b4Posn = 0;

						// If that was the equals sign, break out of 'for' loop
						if (sbiCrop == Base64_EQUALS_SIGN)
							break;
					} // end if: quartet built

				} // end if: equals sign or better

			} // end if: white space, equals sign or better
			else {
				System.err.println("Bad Base64 input character at " + i + ": "
						+ source[i] + "(decimal)");
				return null;
			} // end else:
		} // each input character

		byte[] out = new byte[outBuffPosn];
		System.arraycopy(outBuff, 0, out, 0, outBuffPosn);
		return out;
	} // end decode

	/**
	 * Decodes data from Base64 notation, automatically detecting
	 * gzip-compressed data and decompressing it.
	 * 
	 * @param s
	 *            the string to decode.
	 * @return the decoded data
	 * @since 1.4
	 */
	private static byte[] base64_decode(String s) {
		byte[] bytes;
		try {
			bytes = s.getBytes(Base64_PREFERRED_ENCODING);
		} // end try
		catch (java.io.UnsupportedEncodingException uee) {
			bytes = s.getBytes();
		} // end catch
		// </change>

		// Decode
		bytes = base64_decode(bytes, 0, bytes.length);

		// Check to see if it's gzip-compressed
		// GZIP Magic Two-Byte Number: 0x8b1f (35615)
		if (bytes != null && bytes.length >= 4) {

			int head = ((int) bytes[0] & 0xff) | ((bytes[1] << 8) & 0xff00);
			if (java.util.zip.GZIPInputStream.GZIP_MAGIC == head) {
				java.io.ByteArrayInputStream bais = null;
				java.util.zip.GZIPInputStream gzis = null;
				java.io.ByteArrayOutputStream baos = null;
				byte[] buffer = new byte[2048];
				int length = 0;

				try {
					baos = new java.io.ByteArrayOutputStream();
					bais = new java.io.ByteArrayInputStream(bytes);
					gzis = new java.util.zip.GZIPInputStream(bais);

					while ((length = gzis.read(buffer)) >= 0) {
						baos.write(buffer, 0, length);
					} // end while: reading input

					// No error? Get new bytes.
					bytes = baos.toByteArray();

				} // end try
				catch (java.io.IOException e) {
					// Just return originally-decoded bytes
				} // end catch
				finally {
					try {
						baos.close();
					} catch (Exception e) {
					}
					try {
						gzis.close();
					} catch (Exception e) {
					}
					try {
						bais.close();
					} catch (Exception e) {
					}
				} // end finally

			} // end if: gzipped
		} // end if: bytes.length >= 2

		return bytes;
	} // end decode

}
