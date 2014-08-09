public class AdData {
  
        // Variable to hold the Advertisement Text
        private String adText = null;

        // Variable to holf the Advertisement Style
	private byte style = 2;

        // Variable is having the fixed Advertisement Header Size 
        private final byte HEADER_SIZE = 1;
        
        // Varible is having the fixed Advertisement Landing Page Size
        private final byte LANDPAGE_SIZE = 2;
        
        // Variable to hold the Advertisement Landing Pages 
        private boolean[] lPag;
        
        // Variable to hold the Phone Number 
        private String pNo;
        
        // Variable to hold the Advertisement Id
        private String adId;
        
        // Variable to hold the Advertisement URL
        private String url;

        //
        private String cAdText1;
        
        private String cAdText2;

        // Variable to hold the Profile Id
        private String prId;
        
        /** Imediate Ad Action */
        private boolean isImdAct = false;

        private byte lLength = LANDPAGE_SIZE * 8;
        
        //Ad Display Chennal Data Send
        private boolean isDCDSend = false;

        public boolean isIsDCDSend() {
            return isDCDSend;
        }

        public void setIsDCDSend(boolean isDCDSend) {
            this.isDCDSend = isDCDSend;
        }

        /**
         *
         * @return
         */
        public byte getLLength() {
            return lLength;
        }

        /** Get the Imediate Ad Action */
        public boolean isIsImdAct() {
            return isImdAct;
        }

        /** Set the Imediate Ad Action */
        public void setIsImdAct(boolean isImdAct) {
            this.isImdAct = isImdAct;
        }
        
        // Method to Get the landing Page Size
        
        public byte getLANDPAGE_SIZE() {
            return LANDPAGE_SIZE;
        }
        
        // Method to Set and Get the Alternative Advertisement Text
        
        public void setCAdText1(String cAdText1) {
            this.cAdText1 = cAdText1;
        }
         
        public String getCAdText1() {
            return cAdText1;
        }

        // Method to Set and Get the Alternative Advertisement Text
        
        public void setCAdText2(String cAdText2) {
            this.cAdText2 = cAdText2;
        }
        
        public String getCAdText2() {
            return cAdText2;
        }      

        // Method to Set and Get the Advertisement URL
        
        public void setUrl(String url) {
            this.url = url;
        }
        
        public String getUrl() {
            return url;
        }

        // Method to Set and Get the Profile Id

        public void setPrId(String prId) {
            this.prId = prId;
        }
        
        public String getPrId() {
            return prId;
        }

        //Method to Set and Get the Advertisement Id

        public void setAdId(String adId) {
            this.adId = adId;
        }
        
        public String getAdId() {
            return adId;
        }
        
        // Method to Set and Get the Advertisement Landing Pages
        
        public void setLPag(boolean[] lPag) {
            this.lPag = lPag;
        }
        
        public boolean[] getLPag() {
            return lPag;
        }

        // Method to Set and Get the Phone Number
        
        public void setPNo(String pNo) {
            this.pNo = pNo;
        }
        
        public String getPNo() {
            return pNo;
        }

        //Method to Get the Advertisement Header Size
        
        public byte getHEADER_SIZE() {
            return HEADER_SIZE;
        }

        // Method to Set and Get the Advertisement Text
        
        public void setAdText(String adText) {
		this.adText = adText;
	}
    
        public String getAdText() {
		return adText;
	}

	// Method to Set and Get the Advertisement Style
        
        public void setStyle(byte style) {
		this.style = style;
	}
        
        public byte getStyle() {
		return style;
	}	
}
