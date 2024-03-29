<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="emailInformation">
<html>
<head>
</head>
<body>
  <p><xsl:value-of select="email" /></p>
  <p>Please click on the following link to verify your email address at <xsl:element name="a">
    <xsl:attribute name="href"><xsl:value-of select="opRoute"/></xsl:attribute><span>Court Opinions</span></xsl:element>.</p>
  <h4><xsl:element name="a">
    <xsl:attribute name="href"><xsl:value-of select="opRoute"/>/views/verify.xhtml?email=<xsl:value-of select="email"/>&amp;verifyKey=<xsl:value-of select="verifyKey"/></xsl:attribute>
    <span>Verify Me!</span>
    </xsl:element>
  </h4>
  <p>
    Regards, <br /><br />
    Court Opinions.
  </p>
</body>
</html>
</xsl:template>
</xsl:stylesheet>