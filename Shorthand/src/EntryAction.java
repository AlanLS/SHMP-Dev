public class EntryAction {

        /** Variable to hold the this action id */
        private int id;
    
        /** Variable to hold the next action id */
	private int gotoid;

        /** Variable to hold the entry name */
	private String ename;

        /** Variable to hold the Entry Action Scprefix */
	private String scprefix;

        /** Varibale to hold the mask String */
	private String mask;

        /** Variable to hold the memorize variable name */
	private String mvarname; // MV

        /** Variable to hold the entry type */
	private byte etype;

        /** Variable to hold the maximum character */
	private int maxchar = UISettings.MAX_COUNT;

        /** Variable to hold the minimum value */
	private short minchar;

        /** Vairable to hold the maximum value */
	private float  maxvalue;

        /** Varibale to hold the entrybox length */
	private short length;

        /** Variable to hold the entyr action is fixed or Variable */
	private boolean isfixed;

        /** Varibale to hold the back option is present or not */
	private boolean back;

        /** Varibale to hold the propagate present or not */
	private boolean ppgt;

        /** Varibale to hold the Place ad present or not */
	private boolean placead;

        /** MultiLine Present or Not Variable */
	private boolean multiline;
        
        /** Entry box Present Or Not Variable */
        private boolean isentrybox;
        
        /** Pershable Entry */
        private boolean perEntry;
        
        /** Escape Text */
        private EscapeText[] esTxt;
        
        //MultiValue
        private String mulValue;
        
        //Seperator
        private String sep = null;
        
        // Goto Widget Name
        private String gotoWidgetName = null;
        
        /** Next SMSAction queryType */
        private String queryType = null;

        /** Scprefix Record */
        private boolean isRecord = false;
        
        /** Record Display Format */
        private String rDFormat = null;

        /** Left Option Enable String */
        private String lOString = null;

        /** Left option Goto Id */
        private int lOGotoId = -1;

        /** Delete option is Enable of Not */
        private boolean isRDel = false;

        /** smart Back */
        private boolean smartBack = false;
        
        private boolean isNative = false;

        private float minValue =0;

        private boolean isMsContacts = false;

        private boolean isMsRefresh = false;

        

        public boolean isIsMsContacts() {
            return isMsContacts;
        }

        public void setIsMsContacts(boolean isMsContacts) {
            this.isMsContacts = isMsContacts;
        }

        public boolean isIsMSRefresh() {
            return isMsRefresh;
        }

        public void setIsMsRefresh(boolean isMsRefresh) {
            this.isMsRefresh = isMsRefresh;
        }

        public float getMinValue() {
            return minValue;
        }

        public void setMinValue(float minValue) {
            this.minValue = minValue;
        }

        /** Retrieve the Native textbox Bit */
        public boolean isIsNative() {
            return isNative;
        }

        /** Set the Native Textbox bit */
        public void setIsNative(boolean isNative) {
            this.isNative = isNative;
        }

        /** Encrypt **/
//        private boolean encrypt = false;
        
        /** Get isEntrySet **/
//        public boolean isEncrypt(){
//            return encrypt;
//        }
//        
//        /** Set Encrypt **/
//        public void setEncrypt(boolean isEncr){
//            encrypt = isEncr;
//        }
        
        /** Get the Smart Back */
        public boolean isSmartBack() {
            return smartBack;
        }

        /** Set the Smart Back */
        public void setSmartBack(boolean smartBack) {
            this.smartBack = smartBack;
        }

        /** Get Delete option is enable or Not */
        public boolean isIsRDel() {
            return isRDel;
        }

        /** Set Delete option is enable or Not */
        public void setIsRDel(boolean isRDel) {
            this.isRDel = isRDel;
        }

        /** Get Left Option Goto Id */
        public int getLOGotoId() {
            return lOGotoId;
        }

        /** Set Left Option Goto Id */
        public void setLOGotoId(int lOGotoId) {
            this.lOGotoId = lOGotoId;
        }

        /** Get Left option String */
        public String getLOString() {
            return lOString;
        }

        /** Set Left option String */
        public void setLOString(String lOString) {
            this.lOString = lOString;
        }

        /** Get Record Display format */
        public String getRDFormat() {
            return rDFormat;
        }

        /** Set Record Display Format */
        public void setRDFormat(String rDFormat) {
            this.rDFormat = rDFormat;
        }

        /** Set Record Bit if its ture action having the record otherwise not having*/
        public void setIsRecord(boolean isRecord) {
            this.isRecord = isRecord;
        }

        /** Get Record Bit if its ture action having the record otherwise not having*/
        public boolean isIsRecord() {
            return isRecord;
        }
        
        /** Get widgetName for the use of jump one widget to another widget*/
        public String getGotoWidgetName() {
            return gotoWidgetName;
        }

        /** Set widgetName for the use of jump one widget to another widget*/
        public void setGotoWidgetName(String widgetName) {
            this.gotoWidgetName = widgetName;
        }

        /** Get Next SMSmAction query Type for the use of to display the letter count */
        public String getQueryType() {
            return queryType;
        }

        /** Set Net SMSAction Query Type for the Use of to display the Letter count*/
        public void setQueryType(String qType) {
            this.queryType = qType;
        }
        
        /** Set MultiValue Seperator */
        public void setSep(String sep) {
            this.sep = sep;
        }

        /** Get Multivalue Seperator */
        public String getSep() {
            return sep;
        }

        /** Get Multi Value Name */
        public String getMulValue() {
            return mulValue;
        }

        /** Set Multi Value Naem */
        public void setMulValue(String mulValue) {
            this.mulValue = mulValue;
        }
        
//        /** Get Multiple Value bit */
//        public boolean isIsMValue() {
//            return isMValue;
//        }
//
//        /** Set Multi value Name */
//        public void setIsMValue(boolean isMValue) {
//            this.isMValue = isMValue;
//        }

        /** Get the Action Id it Prepresent the Widget offset */
        public int getId() {
            return id;
        }

        /** Set Action Id it represent the Widget Offset */
        public void setId(int id) {
            this.id = id;
        }
        
        /** Get the Escape Text Object */
        public EscapeText[] getEsTxt() {
            return esTxt;
        }

        /** Set the Escape Text Object */
        public void setEsTxt(EscapeText[] esTxt) {
            this.esTxt = esTxt;
        }
        
        /** Get the Pershable Entry Item Option */
        public boolean isPlaceAd() {
            return placead;
        }

        /** Set the Pershable Entry Item Option */
        public void setPerEntry(boolean perEntry) {
            this.perEntry = perEntry;
        }

        /** Get the Entry Box is Display or Not Option */
        public boolean isIsebRemove() {
            return isentrybox;
        }

        /** Set the Entry Box is Display or Not Option */
        public void setIsebRemove(boolean isentrybox) {
            this.isentrybox = isentrybox;
        }
        
        /** Get Entry Name */
	public String getEName() {
		return ename;
	}

        /** Set Entry Name */
	public void setEName(String ename) {
		this.ename = ename;
	}

        /** Get Scprefix Name */
	public String getScprefix() {
		return scprefix;
	}

        /** Set Scprefix Name */
	public void setScprefix(String scprefix) {
		this.scprefix = scprefix;
	}

        /** Get EntryBox Entry Type */
	public byte getEtype() {
		return etype;
	}

        /** Set EntryBox Entry Type */
	public void setEtype(byte etype) {
		this.etype = etype;
	}

        /** Get Back Option Bit if its true Back Option Present otherwise nnot Present */
	public boolean isBack() {
		return back;
	}

        /** Set Back Option Bit if its true Back Option Present otherwise nnot Present */
	public void setBack(boolean back) {
		this.back = back;
	}

        /** Get Mask Value */
	public String getMask() {
		return mask;
	}

        /** Set Mask Value */
	public void setMask(String mask) {
		this.mask = mask;
	}
        /** Get Propagate bit if its true to entry value to be propagate other widgets otherwise not propagate */
	public boolean isPpgt() {
		return ppgt;
	}

        /** Set Propagate bit if its true to entry value to be propagate other widgets otherwise not propagate */
	public void setPpgt(boolean ppgt) {
		this.ppgt = ppgt;
	}

        /** Get Perishable Entry if its true action is perishable entry otherwise not perishable */
	public boolean isPerentry() {
		return perEntry;
	}

        /** Set Place Ad Bit if its true ad to be place otherwise not place */
	public void setPlaceAd(boolean placeAd) {
		this.placead = placeAd;
	}

        /** Get MultiLine enable bit if its true multiline to Place otherwise not place*/
	public boolean isMultiline() {
		return multiline;
	}

        /** Set Multiline Enable bit it its true multiline to be place otherwise not place*/
	public void setMultiline(boolean multiline) {
		this.multiline = multiline;
	}

        /** Get Memorize Variable Name */
	public String getMvarname() {
		return mvarname;
	}

        /** Set Memorize variable Name */
	public void setMvarname(String mvarname) {
		this.mvarname = mvarname;
	}

        /** Get Goto Id */
	public int getGotoid() {
		return gotoid;
	}

        /** Set goto Id */
	public void setGotoid(int gotoid) {
		this.gotoid = gotoid;
	}

        /** Get Maximum Character */
	public int getMaxchar() {
		return maxchar;
	}

        /** Set Maximum Character */
	public void setMaxchar(short maxchar) {
            if(maxchar > 0)
		this.maxchar = maxchar;
	}

        /** Get Minimum Character */
	public short getMinchar() {
		return minchar;
	}

        /** Set Minimum Character */
	public void setMinchar(short minchar) {
		this.minchar = minchar;
	}

        /** Get Maximum Value */
	public float getMaxvalue() {
		return maxvalue;
	}
        
        /** Set Maximum value */
	public void setMaxvalue(float maxvalue) {
		this.maxvalue = maxvalue;
	}

        /** Get Entrybox Length */
	public short getLength() {
		return length;
	}

        /** Set Entry box length */
	public void setLength(short length) {
		this.length = length;
	}

        /** Get Entry Action Value is fixed or Variable */
	public boolean isIsfixed() {
		return isfixed;
	}

        /** Set the Entry Value is fixed or Variable */
	public void setIsfixed(boolean isfixed) {
		this.isfixed = isfixed;
	}
}
