<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:b="http://www.ftn.uns.ac.rs/xpath/examples" 
    xmlns:fo="http://www.w3.org/1999/XSL/Format" version="2.0">
    
     <xsl:template match="/">
     	<fo:root>
	     	<fo:layout-master-set>
	            <fo:simple-page-master master-name="regulations-page">
	                <fo:region-body margin="1in"/>
	            </fo:simple-page-master>
	        </fo:layout-master-set>
	        
	        <fo:page-sequence master-reference="regulations-page">
      			 <fo:flow flow-name="xsl-region-body">
                    <fo:block font-family="sans-serif" font-size="18px" font-weight="bold" padding="15px" text-indent="55px">
                       TEST korisnici...
                    </fo:block>
                    
                    <fo:block font-family="sans-serif" font-size="12px" text-indent="30px">
                    	<xsl:value-of select="korisnik/@atr" />
                    </fo:block>
                    
                    
                    <fo:block font-family="sans-serif" font-size="12px" text-indent="30px">
                    	<xsl:value-of select="korisnik/ime" />
                    </fo:block>
                 </fo:flow>
              </fo:page-sequence>
	     </fo:root>
	 </xsl:template>
</xsl:stylesheet>