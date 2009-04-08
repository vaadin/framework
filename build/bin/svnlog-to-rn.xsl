<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">

  <xsl:output method="html"/>

  <xsl:template match="logentry">
    <li>[<xsl:element name="a"><xsl:attribute name="href">http://dev.itmill.com/changeset/<xsl:value-of select="@revision"/></xsl:attribute><xsl:value-of select="@revision"/></xsl:element>]: <xsl:value-of select="msg"/></li>
  </xsl:template>

  <xsl:template match="/">
    <html>
      <body bgcolor="#FFFFFF">
        <xsl:apply-templates/>
      </body>
    </html>
  </xsl:template>

</xsl:stylesheet>
