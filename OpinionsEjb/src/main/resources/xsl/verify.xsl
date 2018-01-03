<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="user">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
  <p><xsl:value-of select="email" /></p>
  <p>Please click on the following link to verify your email address at <a href="http://op-cacode.rhcloud.com">Court Opinions</a>.</p>
  <h4><xsl:element name="a">
    <xsl:attribute name="href">http://op-cacode.rhcloud.com/verify?email=<xsl:value-of select="email"/>&quot;key=<xsl:value-of select="verifyKey"/></xsl:attribute>
    <span>Verify Me!</span>
    </xsl:element>
  </h4>
  <p>If your account is not verified within <xsl:value-of select="3 - verifyCount" /> days then your account will be deleted.</p>
  <p>
    Regards, <br /><br />
    Court Opinions.
  </p>
</body>
</html>
</xsl:template>
</xsl:stylesheet>