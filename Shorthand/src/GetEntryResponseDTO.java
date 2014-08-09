public class GetEntryResponseDTO {

	// Secondary Header Text
	private String sechdrtxt;

	// Left option text
	private byte lopttxt = -1;

	// Entry Type
	private byte entrytype;

	// Mask
	private String mask;

	// Banner Text
	private String bannerText;

	// Minimum value
	private float minvalue;

	private float maxvalue;

	private int maxChar;

	private int minChar;

	private boolean isMultiLineEnabled;
        
        private boolean isEntryBoxEnabled = true;

	/**
	 * Banner style 0 - not scrollable and no selectable 1 - scrollable but not
	 * selectable 2 - not scrollable but selectable 3 - scrollable and
	 * selectable
	 */
	private byte bannerStyle = 0;

	// private String[] itemName;
	private String[] entryitems;
        
        private short letterCount=-1;
        
        /** 
         * entry Action ItemId 
         *  <li> positivate value - entry Items </li>
         *  <li> -1 value - Escape Items </li>
         */
        private int[] itemId = null;
        
        private boolean isNotQuery = true;
        
        private boolean isNative = false;
        
        private boolean isShowNative = false;

        //CR 13059
        private boolean isBold = false;

        //CR 12118
        private int totalItemCount = 0;

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

        public int getTotalItemCount() {
            return totalItemCount;
        }

        public void setTotalItemCount(int totalCount) {
            this.totalItemCount = totalCount;
        }

        public boolean isIsBold() {
            return isBold;
        }

        public void setIsBold(boolean isBold) {
            this.isBold = isBold;
        }

        /** Get the Native textbox is Shown or not */
        public boolean isIsShowNative() {
            return isShowNative;
        }

        public void setIsShowNative(boolean isShowNative) {
            this.isShowNative = isShowNative;
        }

        /** Get the Native Textbox byte */
        public boolean isIsNative() {
            return isNative;
        }

        /** Set the Native Textbox byte */
        public void setIsNative(boolean isNative) {
            this.isNative = isNative;
        }
        
        public void setIsNotQuery(boolean isNotQuery) {
            this.isNotQuery = isNotQuery;
        }

        public boolean isIsNotQuery() {
            return isNotQuery;
        }
        
        /** Get ItemId */
        public int[] getItemId() {
            return itemId;
        }

        /** Set ItemId */
        public void setItemId(int[] itemId) {
            this.itemId = itemId;
        }

        public short getLetterCount() {
            return letterCount;
        }

        public void setLetterCount(short letterCount) {
            this.letterCount = letterCount;
        }
        
	/**
	 * constructor method for initialization
	 */
	public GetEntryResponseDTO() {
		entrytype = 0;
		minvalue = 0;
		maxvalue = Float.MAX_VALUE;
		//maxChar = Byte.MAX_VALUE;
                maxChar = UISettings.MAX_COUNT;
		minChar = 0;
	}

	// private MenuItems[] menuItems;
	public byte getBannerStyle() {
		return bannerStyle;
	}

	public void setBannerStyle(byte bannerStyle) {
		this.bannerStyle = bannerStyle;
	}

	public String getBannerText() {
		return bannerText;
	}

	public void setBannerText(String bannerText) {
		this.bannerText = bannerText;
	}

	public byte getEntryType() {
		return entrytype;
	}

	public void setEntryType(byte entryType) {
		entrytype = entryType;
	}

	public boolean isMultiLineEnabled() {
		return isMultiLineEnabled;
	}

	public void setMultiLineEnabled(boolean isMultiLineEnabled) {
		this.isMultiLineEnabled = isMultiLineEnabled;
	}

	public byte getLeftOptionText() {
		return lopttxt;
	}

	public void setLeftOptionText(byte leftOptionText) {
		lopttxt = leftOptionText;
	}

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public int getMaxChar() {
		return maxChar;
	}

	public void setMaxChar(int maxChar) {
            if(maxChar<=UISettings.MAX_COUNT)
		this.maxChar = maxChar;
	}

	public float getMaxValue() {
		return maxvalue;
	}

	public void setMaxValue(float maxValue) {
            if(maxValue>0)
		maxvalue = maxValue;
	}

	public int getMinChar() {
		return minChar;
	}

	public void setMinChar(int minChar) {
		this.minChar = minChar;
	}

	public float getMinValue() {
		return minvalue;
	}

	public void setMinValue(float minValue) {
		minvalue = minValue;
	}

	public String getSecondaryHeaderText() {
		return sechdrtxt;
	}

	public void setSecondaryHeaderText(String secondaryHeaderText) {
		sechdrtxt = secondaryHeaderText;
	}

	public String[] getEntryItems() {
		return entryitems;
	}

	public void setEntryItems(String[] itemValue) {
		entryitems = itemValue;
	}

    public boolean isIsEntryBoxEnabled() {
        return isEntryBoxEnabled;
    }

    public void setIsEntryBoxEnabled(boolean isEntryBoxEnabled) {
        this.isEntryBoxEnabled = isEntryBoxEnabled;
    }

}
