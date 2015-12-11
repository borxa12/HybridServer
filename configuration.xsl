<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:c="http://www.esei.uvigo.es/dai/hybridserver">

<xsl:output method="html" indent="yes" encoding="utf-8"/>
	<xsl:template match="/">
		<html>
			<body>
				<h4>numClients=<xsl:value-of select="c:configuration/c:connections/c:numClients"/></h4>
				<h4>port=<xsl:value-of select="c:configuration/c:connections/c:http"/></h4>
				<h4>db.url=<xsl:value-of select="c:configuration/c:database/c:url"/></h4>
				<h4>db.users=<xsl:value-of select="c:configuration/c:database/c:user"/></h4>
				<h4>db.password=<xsl:value-of select="c:configuration/c:database/c:password"/></h4>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
