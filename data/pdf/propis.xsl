<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:p="http://www.parlament.gov.rs/propisi" 
    xmlns:fo="http://www.w3.org/1999/XSL/Format"

    version="2.0">
    
     <xsl:template match="/">
     	<fo:root>
	     	<fo:layout-master-set>
	            <fo:simple-page-master master-name="regulations-page">
	                <fo:region-body margin="1in"/>
	            </fo:simple-page-master>
	        </fo:layout-master-set>
            
     	   	<fo:page-sequence master-reference="regulations-page">
      			 <fo:flow flow-name="xsl-region-body">
                    <fo:block font-family="sans-serif" font-size="9px" font-weight="bold" padding="5px" text-indent="180px">
                        Skupština Grada Novog Sada
                    </fo:block>
                    
                     <fo:block text-indent="170px" font-size="16px"  padding="20px">
                     	 <fo:inline font-weight="bold">
                    	  <xsl:value-of select="p:propis/@naziv" />
                    	  </fo:inline>
                    </fo:block>
                    
                     <fo:block text-indent="190px" font-size="10px" padding="8px">
                     	 <fo:inline font-weight="bold">
                    	  <xsl:value-of select="p:propis/p:deo/@naziv" />
                    	  </fo:inline>
                    </fo:block>
                    
                 
                     <fo:block text-indent="190px" font-size="12px" font-weight="bold" padding="8px">
                     	GLAVA 
                     	 <fo:inline>
                    	  <xsl:value-of select="p:propis/p:deo/p:glava/@broj" />
                    	  </fo:inline>
                    </fo:block>
                    
                    <fo:block text-indent="170px" font-size="12px" padding="8px">  
                     	 <fo:inline font-weight="bold">
                    	  <xsl:value-of select="p:propis/p:deo/p:glava/@naslov" />
                    	  </fo:inline>
                       </fo:block>
                   
                       
                        <fo:block text-indent="100px" >
                     	 <fo:inline font-weight="bold">
                    	  <xsl:value-of select="p:propis/p:deo/p:glava/p:odeljak/p:clan/p:naslov" />
                    	  </fo:inline>
                       </fo:block>
                  
                  		 <fo:block text-indent="190px"  font-weight="bold" padding="12px">
                     	Član  
                     	 <fo:inline>
                    	  <xsl:value-of select="p:propis/p:deo/p:glava/p:odeljak/p:clan/@broj" />
                    	  </fo:inline>
                       </fo:block>
                  
                  
                  		<fo:block padding="12px">
                    	  <xsl:value-of select="p:propis/p:deo/p:glava/p:odeljak/p:clan/p:opis" />  
                       </fo:block>
                  	
                 
                    
      			</fo:flow>
      		</fo:page-sequence>
    	</fo:root>
     </xsl:template>
</xsl:stylesheet>