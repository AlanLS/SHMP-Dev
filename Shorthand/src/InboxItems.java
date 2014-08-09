public class InboxItems {

	private boolean readflag = false;

	private String sender = null;

	private String messageid = null;

	private String message = null;
        
        private String fLineText = null;

        private String queryType = null;

        private String shortCode = null;

        private String chennalData = null;

        private boolean isReply = false;

        private int rmsIndex =-1;

    public int getRmsIndex() {
        return rmsIndex;
    }

    public void setRmsIndex(int rmsIndex) {
        this.rmsIndex = rmsIndex;
    }

    public boolean isIsReply() {
        return isReply;
    }

    public void setIsReply(boolean isReply) {
        this.isReply = isReply;
    }

    



    public String getChennalData() {
        return chennalData;
    }

    public void setChennalData(String chennalData) {
        this.chennalData = chennalData;
    }

        

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

        

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

        

        public String getFLineText() {
            return fLineText;
        }

        public void setFLineText(String fLineText) {
            this.fLineText = fLineText;
        }

	public boolean isReadFlag() {
		return readflag;
	}

	public void setReadFlag(boolean readFlag) {
		readflag = readFlag;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getMessageId() {
		return messageid;
	}

	public void setMessageId(String messageId) {
		messageid = messageId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
