public class DisplayResponseDTO {

	// Secondary Header Text
	private String sechdrtxt;

	// Display Image
	private byte dispimg;

	// left option text
	private byte lopttxt = -1;

	// Banner Text
	private String bannertxt;

	/**
	 * Banner style 0 - not scrollable and no selectable 1 - scrollable but not
	 * selectable 2 - not scrollable but selectable 3 - scrollable and
	 * selectable
	 */
	private byte bannerstyle;

	// Display Time
	private short disptime;

        private String[] mItems = null;
        
        //Display Screen Running in the front of the Donload Process
        private boolean isDownload = false;
        
        private boolean isDATWait = false;

        private boolean isAppWait = false;

        private boolean isSmsWaitBeforeLoad = false;

         //CR 14694
        private String userNumber = null;

        private byte profileImageType = -1;

        public byte getProfileImageType() {
            return profileImageType;
        }

        public void setProfileImageType(byte profileImageType) {
            this.profileImageType = profileImageType;
        }

        //CR 14789
        private String uploadId = null;

        public String getUploadId() {
            return uploadId;
        }

        public void setUploadId(String uploadId) {
            this.uploadId = uploadId;
        }

        public String getUserNumber() {
            return userNumber;
        }

        public void setUserNumber(String userNumber) {
            this.userNumber = userNumber;
        }


    public boolean isIsSmsWaitBeforeLoad() {
        return isSmsWaitBeforeLoad;
    }

    public void setIsSmsWaitBeforeLoad(boolean isSmsWaitBeforeLoad) {
        this.isSmsWaitBeforeLoad = isSmsWaitBeforeLoad;
    }

        

    public void setIsAppWait(boolean isAppWait) {
        this.isAppWait = isAppWait;
    }

    public boolean isIsAppWait() {
        return isAppWait;
    }

        

        public boolean isIsDATWait() {
            return isDATWait;
        }

        public void setIsDATWait(boolean isDATWait) {
            this.isDATWait = isDATWait;
        }
        
        public boolean isIsDownload() {
            return isDownload;
        }

        public void setIsDownload(boolean isDownload) {
            this.isDownload = isDownload;
        }

        /** Get the Menu Item Names */
        public String[] getMItems() {
            return mItems;
        }

        /** Set the Menu Item Names */
        public void setMItems(String[] mItems) {
            this.mItems = mItems;
        }

	/**
	 * Constructor method
	 */
	public DisplayResponseDTO() {
		bannerstyle = 0;
		disptime = 0;
	}

	/**
	 * @return sechdrtxt
	 */
	public String getSecondaryHeaderText() {
		return sechdrtxt;
	}

	public void setSecondaryHeaderText(String secondaryHeaderText) {
		sechdrtxt = secondaryHeaderText;
	}

	public byte getDisplayImage() {
		return dispimg;
	}

	public void setDisplayImage(byte displayImage) {
		dispimg = displayImage;
	}

	public byte getLeftOptionText() {
		return lopttxt;
	}

	public void setLeftOptionText(byte leftOptionText) {
		lopttxt = leftOptionText;
	}

	public String getBannerText() {
		return bannertxt;
	}

	public void setBannerText(String bannerText) {
		bannertxt = bannerText;
	}

	public byte getBannerStyle() {
		return bannerstyle;
	}

	public void setBannerStyle(byte bannerStyle) {
		bannerstyle = bannerStyle;
	}

	public short getDisplayTime() {
		return disptime;
	}

	public void setDisplayTime(short displayTime) {
		disptime = displayTime;
	}

}