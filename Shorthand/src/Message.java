/**
 * @author Hakuna Matata
 * @version 1.0
 * @copyright (c) John Mcdonnough
 */

/**
 *
 */
public class Message {

        /** Message Sender Name */
        private String senderName = null;


    //CR 10687

//    /** Match Words for the use of Message receive association */
//    private String matchWords = null;
//
//        /** MisMathch Words for the use of Message receive Association */
//	private String misMatchWords = null;

        /** Received Message fileName */
	private String fName = null;

        /** Message Query type */
	private String queryType = null;

        /** PartIndex */
	private byte partIndex;

        /** MaxCount */
	private byte maxCount = 0;

        /** Message Sender Short Code */
	private String shortcode = null;

        /** Receive Messgaes */
        private String[] rMsg =null;

        /** acctual Receive Message Count */
        private byte rmsgCount =0;

        /** Message concatination Format */
        private String mscf = "";

        /** Currently Received Message */
        private String curRMsg = null;

        /** Currently Receive Message MaxCount */
        private byte tempmaxCount;

        /** Message Receive round Trip delay Time */
        private int rtDelay;

        /** Message Receive/Sending Profile Id */
        private String pId = null;

        /** Dont Send Message */
        private boolean isDSend =false;

        /**Dont Wait REsponse */
        private boolean isDWRes =false;

        /** No received message saved in inbox **/
        private boolean dontSaveInbox = false;

        private boolean isNotNewMsg = false;

        /** Sender Full Name */
        private String sFullName = null;

        //bug id 6716 Without Sequence Number need to set empty not null
        /** temp Message concadenation format */
        private String tempConFormat = "";

        private long populatedTime = 0;

        private boolean isRTDASend = false;


        //CR 11975
        private String errorMessage = null;
        
        private boolean inboxFunc = false;//0 sms 1 data

        private String abberVation = null;

        private boolean  isNotChatMessage = true;

        private String chatId = null;

        private String chatName = null;

        private boolean isInboxReply = false;

        private String channelData = null;

        private String logMessage = null;

        private boolean isSendQueueEmpty = true;

        //CR 14139
        private boolean isSMSSEND = false;

        //CR 14134
        private String messagePlus = " ";

        //CR 14324
        private boolean isDateTimeSend = false;

        //Cr 14326
        private String chatSequence = "00000";

        //Cr 14326
        private String chatDate = "20140218";

        public String getChatSequence() {
            return chatSequence;
        }

        public void setChatSequence(String chatSequence) {
            this.chatSequence = chatSequence;
        }

        public String getChatDate() {
            return chatDate;
        }

        public void setChatDate(String chatDate) {
            this.chatDate = chatDate;
        }

        public boolean isIsDateTimeSend() {
            return isDateTimeSend;
        }

        public void setIsDateTimeSend(boolean isDateTimeSend) {
            this.isDateTimeSend = isDateTimeSend;
        }

        public boolean isIsSMSSEND() {
            return isSMSSEND;
        }

        public void setIsSMSSEND(boolean isSMSSEND) {
            this.isSMSSEND = isSMSSEND;
        }

        public String getMessagePlus() {
            return messagePlus;
        }

        public void setMessagePlus(String messagePlus) {
            if(null == messagePlus || messagePlus.length() == 0)
                messagePlus = " ";
            this.messagePlus = messagePlus;
        }

        public void setIsSendQueueEmpty(boolean isSendQueueEmpty) {
            this.isSendQueueEmpty = isSendQueueEmpty;
        }

        public boolean isIsSendQueueEmpty() {
            return isSendQueueEmpty;
        }


    public String getChannelData() {
        return channelData;
    }

    public void setChannelData(String channelData) {
        this.channelData = channelData;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }

        

    public boolean isIsInboxReply() {
        return isInboxReply;
    }

    public void setIsInboxReply(boolean isInboxReply) {
        this.isInboxReply = isInboxReply;
    }


    

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

        

    public boolean isIsNotChatMessage() {
        return isNotChatMessage;
    }

    public void setIsNotChatMessage(boolean isNotChatMessage) {
        this.isNotChatMessage = isNotChatMessage;
    }

        


    public String getAbberVation() {
        return abberVation;
    }

    public void setAbberVation(String abberVation) {
        this.abberVation = abberVation;
    }


    

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
        //This variable is used to indetify the request is for inbox functions like reply or forward
        // if it s reply or forward. this should be handled as SMS request not as data request
        // if inboxFunc is TRUE means DATA. FALSE means SMS

        void setInboxFunc(boolean inboxFunc) {
            this.inboxFunc = inboxFunc;
        }

        boolean getInboxFunc() {
            return inboxFunc;
        }


        //<!-- CR 11975 -->
    public boolean isIsRTDASend() {
        return isRTDASend;
    }

    public void setIsRTDASend(boolean isRTDASend) {
        this.isRTDASend = isRTDASend;
    }

        

    public long getPopulatedTime() {
        return populatedTime;
    }

    public void setPopulatedTime(long populatedTime) {
        this.populatedTime = populatedTime;
    }



        public String getTempConFormat() {
            return tempConFormat;
        }

        public void setTempConFormat(String tempConFormat) {
            this.tempConFormat = tempConFormat;
        }

        /* Get the Sender Full Name */
        public String getSFullName() {
            return sFullName;
        }

        /* Set the Sender Full Name */
        public void setSFullName(String sFullName) {
            this.sFullName = sFullName;
        }

        /** Get the Message Notification is Not Displayed */
        public boolean isIsNotNewMsg() {
            return isNotNewMsg;
        }

        /** Set the Message Notification is Not Displayed */
        public void setIsNotNewMsg(boolean isNotNewMsg) {
            this.isNotNewMsg = isNotNewMsg;
        }

        public boolean getDontSaveInbox(){
            return dontSaveInbox;
        }

        public void setDontSaveInbox(boolean dsinbox){
            dontSaveInbox = dsinbox;
        }

        /** Get Dont Send Message boolean */
        public boolean isIsDSend() {
            return isDSend;
        }

        /** Set Dont Send Message boolean */
        public void setIsDSend(boolean isDSend) {
            this.isDSend = isDSend;
        }

        /** Get Dont Wait Response Message boolean */
        public boolean isIsDWRes() {
            return isDWRes;
        }

        /** Set Dont Wait Response Message boolean */
        public void setIsDWRes(boolean isDWRes) {
            this.isDWRes = isDWRes;
        }

    /** Get the Receive/sended Profile Id */
    public String getPId() {
        return pId;
    }

    /** Set the Receive/sended Profile Id */
    public void setPId(String pId) {
        this.pId = pId;
    }

    /** Get the Receive Message round trip Delay */
    public int getRtDelay() {
        return rtDelay;
    }

    /** Set the Receive Message Round trip delay */
    public void setRtDelay(int rtDelay) {
        this.rtDelay = rtDelay;
    }

    /** Get the currently Receive Message MaxCount */
    public byte getTempmaxCount() {
        return tempmaxCount;
    }

    /** Get Currently Receive Message */
    public String getCurRMsg() {
        return curRMsg;
    }

    /** Set Currently Receive Message */
    public void setCurRMsg(String curRMsg) {
        this.curRMsg = curRMsg;
    }

    /** Get Receive Messages array */
    public String[] getRMsg() {
        return rMsg;
    }

    /** Get Acctual Receive Message Count */
    public byte getRmsgCount() {
        return rmsgCount;
    }

    /** Set the Receive Messaeg Count */
    public void setRMsg(String[] rMsg) {
        this.rMsg = rMsg;
    }

    /** Set the Receive Message Count */
    public void setRmsgCount(byte rmsgCount) {
        //#if VERBOSELOGGING
        //|JG|Logger.debugOnError("Number of parts received yet= "+ rmsgCount);
        //#endif
        this.rmsgCount = rmsgCount;
    }

    /** Get the Received File Name */
    public String getFName() {
        return fName;
    }

    /** Set the Receive Message File Name */
    public void setFName(String fName) {
        this.fName = fName;
    }

        /** Multi Message Format */
        public String getMscf() {
            return mscf;
        }

        /** set Mulkti Message Format */
        public void setMscf(String mscf) {
            this.mscf = mscf;
        }

        /** Get Message Receive/Send sender Name */
            public String getSenderName() {
		return senderName;
	}

        /** Set Message Receive/Send Sender Name */
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

        /** Get Message query Type */
        public String getQueryType() {
		return queryType;
	}

        /** Set Query type */
	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

        /** Get currently Receive Message PartIndex */
        public byte getPartIndex() {
		return partIndex;
	}

        /** set currently receive Message PartIndex */
        public void setPartIndex(byte partIndex) {
            //#if VERBOSELOGGING
            //|JG|Logger.debugOnError("Current part index= "+ partIndex );
            //#endif
		this.partIndex = partIndex;
	}

        /** Get MaxCount */
        public byte getMaxCount() {
		return maxCount;
	}

        /** Set Maxcount */
        public void setMaxCount(byte maxCount) {
            //#if VERBOSELOGGING
            //|JG|Logger.debugOnError("Setting Max part count= "+ maxCount );
            //#endif
		this.maxCount = maxCount;
	}

        /** Get Send/Receive Message ShortCode */
        public String getShortcode() {
		return shortcode;
	}

        /** Set send/Receive Message Short Code */
	public void setShortcode(String shortcode) {
		this.shortcode = shortcode;
	}

        /** Set Currenty receive Message MaxCount */
        public void setTempmaxCount(byte tempmaxCount) {
            this.tempmaxCount = tempmaxCount;
        }

}
