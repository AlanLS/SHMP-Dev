public class DisplayAction {

        // Varible to hold the Display Action Id
        private int id;
        
        // 
        private String otxt;

	private byte dispimage;

	// private String id;
	private short disptime;

	private String mqtype;

	private int gotoid; // we will not be using this - instead we will use
							// pgotoid
	private boolean placead;

	private boolean back;
        
        /** Escape Text object */
        private EscapeText[] esTxt;
        
        //Disable Reply
        private boolean isDReply;
        
        //Display Information
        private String info;
        
        private String gotWidgetName = null;

        /** Left Option Enable String */
        private String lOString = null;

        /** Left option Goto Id */
        private int lOGotoId = -1;

        /** Delete option is Enable of Not */
        private boolean isRDel = false;

        /** smart Back */
        private boolean smartBack = false;
        
        private boolean isUrl = false;

        private String buddyName = null;

        //private String screenName = null;

        //private boolean isRemoveEntryBox = false;

        private boolean isReplyEnabled = false;

        //<bug 9272
        private boolean  isNumber = false;

        public void setIsNumber(boolean isNumber) {
        this.isNumber = isNumber;
    }

    public boolean isIsNumber() {
        return isNumber;
    }
    //bug9272>>

    public void setBuddyName(String buddyName) {
        this.buddyName = buddyName;
    }

    public String getBuddyName() {
        return buddyName;
    }

//    public void setScreenName(String screenName) {
//        this.screenName = screenName;
//    }
//
//    public String getScreenName() {
//        return screenName;
//    }

//    public boolean isIsRemoveEntryBox() {
//        return isRemoveEntryBox;
//    }
//
//    public void setIsRemoveEntryBox(boolean isRemoveEntryBox) {
//        this.isRemoveEntryBox = isRemoveEntryBox;
//    }

    public boolean isIsReplyEnabled() {
        return isReplyEnabled;
    }

    public void setIsReplyEnabled(boolean isReplyEnabled) {
        this.isReplyEnabled = isReplyEnabled;
    }



        public boolean isIsUrl() {
            return isUrl;
        }

        public void setIsUrl(boolean isUrl) {
            this.isUrl = isUrl;
        }

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

        public void setGotoWidgetName(String widgetName) {
            this.gotWidgetName = widgetName;
        }

        public String getGotoWidgetName() {
            return gotWidgetName;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public boolean isIsDReply() {
            return isDReply;
        }

        public void setIsDReply(boolean isDReply) {
            this.isDReply = isDReply;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
        
        /** set the Escape Txt Object */
        public void setEsTxt(EscapeText[] esTxt) {
            this.esTxt = esTxt;
        }

        /** Get the Escape Txt Object */
        public EscapeText[] getEsTxt() {
            return esTxt;
        }
        
	public String getOtxt() {
		return otxt;
	}

	public void setOtxt(String otxt) {
		this.otxt = otxt;
	}

	public byte getDispimage() {
		return dispimage;
	}

	public void setDispimage(byte dispimage) {
//            if(dispimage == 0){
//                dispimage = ProfileTypeConstant.Display.DISPLAY_GSPROFILE;
//            }
		this.dispimage = dispimage;
	}

	public short getDisptime() {
		return disptime;
	}

	public void setDisptime(short disptime) {
		this.disptime = disptime;
	}

	public String getMqtype() {
		return mqtype;
	}

	public void setMqtype(String mqtype) {
		this.mqtype = mqtype;
	}

	public boolean isPlacead() {
		return placead;
	}

	public void setPlacead(boolean placead) {
		this.placead = placead;
	}

	public int getGotoid() {
		return gotoid;
	}

	public void setGotoid(int gotoid) {
		this.gotoid = gotoid;
	}

	public boolean isBack() {
		return back;
	}

	public void setBack(boolean back) {
		this.back = back;
	}

}
