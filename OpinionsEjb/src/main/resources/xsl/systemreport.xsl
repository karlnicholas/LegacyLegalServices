<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="emailInformation">
<html>
<head/>
<body>
<p><xsl:value-of select="email" /></p>
<p>New Opinions at <xsl:element name="a"><xsl:attribute name="href"><xsl:value-of select="opRoute"/>Court Opinions</xsl:attribute></xsl:element></p>
<table>
  <tr bgcolor="#9acd32"><th>Measurement</th><th>Value</th></tr>
  <xsl:for-each select="memoryMap/entry">
  <tr>
    <td nowrap="true"><xsl:value-of select="key" /></td>
    <td><xsl:value-of select="value" /></td>
  </tr>
  </xsl:for-each>
</table>
<p>Regards,<br /><br />Court Opinions.</p>
</body>
</html>
</xsl:template>
</xsl:stylesheet>