public class ProfileHeader {
    
	private String name;

	private String category;

	private String[] scode = null; // shortcut code

	private String phtxt; // primary header text

	private String desc; // desc

	private String btxt; // banner text

	private String atxt; // alert text

	private String pf; // pay for

	private int pinitid; // it refers to address of menu in the initial id

    	private int pmemvarid; // pointer to memorized variable address
        
        /** Profile Loop Back */
        private boolean lBack; 
        
        /** Profile Usage Count */
        private int puc;
        
        /** Multi SMS Format */
        private String msf;
        
        /** Profile Id 3 digits */
        private String pId;
        
        private byte interval;
        
        private long date=-1;
        
        //LoopBack Delay 
        private byte lBdelay;
        
        /** Memorize Variable Warning Message */
        private String mWMsg = null;
        
        /** Records Name and Element Name */
        private String records = null;
        
        /** Profile Level Goto Id */
        private int fgotoId = -1;

        private String cScprefixName = null;

         /** Second Initial Menu Goto Id*/
        private int sGotoId = -1;

        /** Record Option String */
        private String rOption = null;

        //Alart Count
        private short aCount = 0;

        //Response Query Count
        private short qCount = 0;

        private String pVersion = "0";
        
        private String dUrl = null;
        
        private int eGotoid = -1;
        
        private String memVarName = null;
        
        private String aTableMemVarName = null;
        
        private boolean isDynamicAd = false;

        private boolean isStaticAd = false;
        
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

        public boolean isIsStaticAd() {
            return isStaticAd;
        }

        public void setIsStaticAd(boolean isStaticAd) {
            this.isStaticAd = isStaticAd;
        }
        
        public boolean isIsDynamicAd() {
            return isDynamicAd;
        }

        public void setIsDynamicAd(boolean isDynamicAd) {
            this.isDynamicAd = isDynamicAd;
        }

        public String getATableMemVarName() {
            return aTableMemVarName;
        }

        public void setATableMemVarName(String aTableMemVarName) {
            this.aTableMemVarName = aTableMemVarName;
        }

        public String getMemVarName() {
            return memVarName;
        }

        public void setMemVarName(String memVarName) {
            this.memVarName = memVarName;
        }

        public int getEGotoid() {
            return eGotoid;
        }

        public void setEGotoid(int eGotoid) {
            this.eGotoid = eGotoid;
        }
        
        public void setDUrl(String url){
            dUrl = url;
        }
        
        public String getUrl(){
            return dUrl;
        }
        public String getVersion(){
            return pVersion;
        }
        
        public void setVersion(String version){
            pVersion = version;
        }
        public short getACount() {
            return aCount;
        }

        public void setACount(short aCount) {
            this.aCount = aCount;
        }

        public short getQCount() {
            return qCount;
        }

        public void setQCount(short qCount) {
            this.qCount = qCount;
        }

        /** Get the Record Optional String */
        public String getROption() {
            return rOption;
        }

        /** Set the Record Optional String */
        public void setROption(String rOption) {
            this.rOption = rOption;
        }


        /** Get the Second Action goto Id */
        public int getSGotoId() {
            return sGotoId;
        }

        /** Set the Second Action goto Id */
        public void setSGotoId(int sGotoId) {
            this.sGotoId = sGotoId;
        }

        /** Get the Common Entry Scprefix Name */
        public String getCScprefixName() {
            return cScprefixName;
        }

        /** Set the Common Entry Scprefix Name */
        public void setCScprefixName(String cScprefixName) {
            this.cScprefixName = cScprefixName;
        }

        /** Get the FirstLevel goto Id */
        public int getFgotoId() {
            return fgotoId;
        }

        /** Set the First Level Goto Id */
        public void setFgotoId(int fgotoId) {
            this.fgotoId = fgotoId;
        }
                
        /** Set the Records */
        public void setRecords(String records) {
            this.records = records;
        }

        /** Get the Records */
        public String getRecords() {
            return records;
        }

        /** Set the Memorize Variable Warning Message */
        public void setMWMsg(String mWMsg) {
            this.mWMsg = mWMsg;
        }

        /** Get the Memorize Variable Warning Message */
        public String getMWMsg() {
            return mWMsg;
        }
        
        /** Set Loop Back Delay Time Byte */
        public void setLBdelay(byte lBdelay) {
            this.lBdelay = lBdelay;
        }

        /** Get Loop Back Delay Time(byte) */
        public byte getLBdelay() {
            return lBdelay;
        }
 
        /** Get Interval Date  Interval is Greater the Zero the Interval Date is Present Otherwise Not Present*/
        public long getDate() {
            return date;
        }

        /** Get Profile Usage Send Interval */
        public byte getInterval() {
            return interval;
        }

        /** Set the Date (Profile Usage send Interval is Grater than Zero the Interval Date is Present otherwise Not Present) */
        public void setDate(long date) {
            this.date = date;
        }

        /** Set the Profile Usage Send Interval */
        public void setInterval(byte interval) {
            this.interval = interval;
        }

        /** Set the Profile Id */
        public void setPId(String pId) {
            this.pId = pId;
        }

        /** Get The Profile id */
        public String getPId() {
            return pId;
        }

        /** Get the Multi SMS Format */
        public String getMSCF() {
            return msf;
        }

        /** Set the Multi SMS Format */
        public void setMSCF(String msf) {
            this.msf = msf;
        }
        
        /** Get Profile Usage count */
        public int getPuc() {
            return puc;
        }

        /** Set Profile Usage count */
        public void setPuc(int puc) {
            this.puc = puc;
        }
        
        /** Get the LoopBack */
        public boolean isLBack() {
            return lBack;
        }

        /** Set the Loop Back */
        public void setLBack(boolean lBack) {
            this.lBack = lBack;
        }


        /** Get the Profile Name */
	public String getName() {
		return name;
	}

        /** Set the Profile Name */
	public void setName(String name) {
		this.name = name;
	}

        /** Get the Profile Catagory */
	public String getCategory() {
		return category;
	}

        /** Set the Profile Catagore */
	public void setCategory(String category) {
		this.category = category;
	}

        /** Get the Profile Level ShortCode (Profile is Having the MultiShortCode Each ShortCode Seperated by ",") */
	public String[] getScode() {
		return scode;
	}

        /** Set the ShortCode (String Array MultiPle ShortCode) */
	public void setScode(String[] scode) {
		this.scode = scode;
	}

        /** Get the Profile Header Text */
	public String getPhtxt() {
		return phtxt;
	}

        /** Set the Profile Header Text */
	public void setPhtxt(String phtxt) {
		this.phtxt = phtxt;
	}

        /** Get the Profile Description */
	public String getDesc() {
		return desc;
	}

        /** Set the profile Description */
	public void setDesc(String desc) {
		this.desc = desc;
	}

        /** Get the Profile Level Banner Text */
	public String getBtxt() {
		return btxt;
	}

        /** Set the Profile Level Banner Text */
	public void setBtxt(String btxt) {
		this.btxt = btxt;
	}

        /** Get the Profile Level Message Receive Alert Text */
	public String getAtxt() {
		return atxt;
	}

        /** Set the Profile Level Message Receive Alert Test */
	public void setAtxt(String atxt) {
		this.atxt = atxt;
	}
        
        /** Get the Profile Level PayFor Alert Text */
	public String getPf() {
		return pf;
	}
        
        /** Set the Profile Level Payfor Alert Test */
	public void setPf(String pf) {
		this.pf = pf;
	}

//	public String getSp() {
//		return sp;
//	}
//
//	public void setSp(String sp) {
//		this.sp = sp;
//	}

        /** Get the Profile Initial Id it's to be the offset of the Intial Menu Starting of the File) */
	public int getPinitid() {
		return pinitid;
	}

        /** Set the Initial Menu Id its to be offset of the filewhere the Initial Menu writen the file */
	public void setPinitid(int pinitid) {
		this.pinitid = pinitid;
	}

        /** Get the Original Length of the File (its used to be Store the Memorized values) */
	public int getPmemvarid() {
		return pmemvarid;
	}

        /** Set the Original Length of the file */
	public void setPmemvarid(int pmemvarid) {
		this.pmemvarid = pmemvarid;
	}
}
