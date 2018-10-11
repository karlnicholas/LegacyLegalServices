<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="emailInformation">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<body>
  <p>Dear <xsl:value-of select="firstName" /> <xsl:value-of select="lastName" /></p>
  <p>Welcome to the <xsl:element name="a"><xsl:attribute name="href"><xsl:value-of select="opRoute"/>Court Opinions</xsl:attribute></xsl:element> application.</p>
  <p>This website is dedicated to serving attorneys in California. This website analyzes newly published California Court opinions (Slip Opinions), showing what statutes and cases are important for each Slip Opinion. Please take a moment to have a look.</p>
  <p>You have been registered with the email <xsl:value-of select="email" />. Use this email to login to the account. This will enable you to set account options. Court Opinions account options control which statute areas, such as Civil, you will receive email updates for.</p>
  <p>Currently your account does not require a password. If you would like to set a password please verify your email with the Verify button on your accounts page.</p>
  <p>If you wish to opt out of all further communications, click the link below.</p>
  <p><xsl:element name="a"><xsl:attribute name="href"><xsl:value-of select="opRoute"/>/views/optout/optout.xhtml?email=<xsl:value-of select="email"/>&quot;key=<xsl:value-of select="verifyKey"/></xsl:attribute>Opt out of further Court Opinions emails</xsl:element></p>
  <p>We hope you will find this effort useful.</p>
  <p>With Kind Regards, <br /><br />Court Opinions.
  </p>
</body>
</html>
</xsl:template>
</xsl:stylesheet>