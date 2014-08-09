public class SMSProfileHeader {

        //Profile Name
	private String name;

        //Profile Location
	private String ploc;

        //Profile Icon
	private String licon;

    private String tildIcon;
        
        //Profile Description
        private String pDes;

        /** Profile ShortCode */
	private String sc; // shortcode
        
        /** Profile Id*/
        private String pId; 
        
        private int pUsg = 0;
        
        private String mCFormat = null;

        //Variable to hold the common entry scprefix
        private String cEntryScprefix = null;

        /** Record Name */
        private String records = null;

        /** Record Option String */
        private String rOption = null;

        //query response Count
        private short qCount = 0;

        //Message receive Alart count
        private short Acount = 0;

        private String pVersion ="0";      
        
        private boolean isDynamicAd = false;
        
        private boolean isInboxReply = false;

        private boolean isReportIEVT = false;

        private int chatId = -1;

        private String abbreviation = null;

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

        

        public int getChatId() {
            return chatId;
        }

        public void setChatId(int chatId) {
            this.chatId = chatId;
        }

    public String getTildIcon() {
        return tildIcon;
    }

    public void setTildIcon(String tildIcon) {
        this.tildIcon = tildIcon;
    }



        public boolean isIsReportIEVT() {
            return isReportIEVT;
        }

        public void setIsReportIEVT(boolean isReportIEVT) {
            this.isReportIEVT = isReportIEVT;
        }

        /** Get the inbox reply enable option */
        public boolean isIsInboxReply() {
            return isInboxReply;
        }

        /** Set the inbox reply enable option */
        public void setIsInboxReply(boolean isInboxReply) {
            this.isInboxReply = isInboxReply;
        }

        public boolean isIsDynamicAd() {
            return isDynamicAd;
        }

        public void setIsDynamicAd(boolean isDynamicAd) {
            this.isDynamicAd = isDynamicAd;
        }
        
        public String getVersion(){
            return pVersion;
        }
        
        public void setVersion(String version){
            pVersion = version;
        }
        public short getQCount() {
            return qCount;
        }

        public void setQCount(short qCount) {
            this.qCount = qCount;
        }

        public short getAcount() {
            return Acount;
        }

        public void setAcount(short Acount) {
            this.Acount = Acount;
        }

        /** Get the Record Optional String */
        public String getROption() {
            return rOption;
        }

        /** Set the Record Optional String */
        public void setROption(String rOption) {
            this.rOption = rOption;
        }

        /** Get the Record Name and Element */
        public String getRecords() {
            return records;
        }

        /** Set the Record Name and Element */
        public void setRecords(String records) {
            this.records = records;
        }

        /** Set the common entry shortcut scprefix Name */
        public void setCEntryScprefix(String cEntryScprefix) {
            this.cEntryScprefix = cEntryScprefix;
        }

        /** Get the common entry shortcut scprefix Name */
        public String getCEntryScprefix() {
            return cEntryScprefix;
        }

        /** Set the Messae Concardnatiion Format ( Default format (|/|) ) */
        public void setMCFormat(String mCFormat) {
            this.mCFormat = mCFormat;
        }

        /** Set the Message Concardnation Format */
        public String getMCFormat() {
            return mCFormat;
        }

        /** Get the Profile Usage Count */
        public int getPUsg() {
            return pUsg;
        }

        /** Set the Profile Usage Count */
        public void setPUsg(int pUsg) {
            this.pUsg = pUsg;
        }
        
        /** Get the Profile Id */
        public String getPId(){
            return pId;
        }
        
        /** Set the Profile Id */
        public void setPId(String id){
            pId = id;
        }
        
        /** Get the Profile Launch Icon Location */
	public String getLaunchIcon() {
		return licon;
	}

        /** Set the Profile Launch Icon Locatiion (its used to display the icon in the Profile Presenter Screen) */
	public void setLaunchIcon(String licon) {
		this.licon = licon;
	}

        /** Get the Profile Name */
	public String getName() {
		return name;
	}

        /** Set the Profile Name */
	public void setName(String name) {
		this.name = name;
	}

        /** Get the Profile Stored or Presenter Location (This having only the root to folder path) */
	public String getProfileLocation() {
		return ploc;
	}

        /* Set the Profile Stored or Presenter Location */
	public void setProfileLocation(String ploc) {
		this.ploc = ploc;
	}

        /** Get the Profile Level First Shortcode */
	public String getSC() {
		return sc;
	}

        /** Set the Profile Level First shortcode */
	public void setSC(String sc) {
		this.sc = sc;
	}

        /** Get the Profile Descriptiion */
        public String getPDes() {
            return pDes;
        }

        /** Set the Profile Description */
        public void setPDes(String pDes) {
            this.pDes = pDes;
        }
}
