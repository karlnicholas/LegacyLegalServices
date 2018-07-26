<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="emailInformation">
<html>
<head>
</head>
<body>
  <p><xsl:value-of select="email" /></p>
  <p>New Opinions at Court Reports</p>
  <table border="1">
    <tr bgcolor="#9acd32">
      <th>Title</th>
      <th>Artist</th>
    </tr>
    <xsl:for-each select="emailInformation/opinionCases">
    <tr>
      <td><xsl:value-of select="opinionView/name"/></td>
      <td><xsl:value-of select="opinionView/opinionDate"/></td>
    </tr>
    </xsl:for-each>
  </table>
  <p>
    Regards, <br /><br />
    Court Opinions.
  </p>
</body>
</html>
</xsl:template>
</xsl:stylesheet>