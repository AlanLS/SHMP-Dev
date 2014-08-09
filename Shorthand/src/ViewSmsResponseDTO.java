/*
 * ViewSmsResponseDTO.java
 *
 * Created on September 5, 2007, 3:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */



/**
 * @author Hakuna Matata
 * @version 2.0
 * @copyright (c) John Mcdonnough
 */

/**
 * Response DTO for View sms presenter
 */
public class ViewSmsResponseDTO {

        //CR 12816
        //App name
        private String appName = null;

	// Sender name
	private String sendername = null;

	// Query Type
	private String querytype = null;

	// Message
	private String message = null;

	// Message Id
	private String mid = null;

	// Banner Text
	private String bannerTxt = null;

	// Banner Style
	private byte bannerstyle = 0;

	private byte lopttxt = -1;
        
        private String[] searchText;
        
        private boolean isPhoneNumberEnabled = true;

        private String[] mItems = null;
        
        //Number of Message Part
        private String fLineText = null;
        
        private boolean isUrlHeighlight = true;

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


        //CR 12816
        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public void setFLineText(String fLineText) {
            this.fLineText = fLineText;
        }

        public String getFLineText() {
            return fLineText;
        }

        /** Get the Menu Item Names */
        public String[] getMItems() {
            return mItems;
        }

        /** Set the Menu Item Names */
        public void setMItems(String[] mItems) {
            this.mItems = mItems;
        }

	public String getSenderName() {
		return sendername;
	}

	public void setSenderName(String senderName) {
		sendername = senderName;
	}

	public String getQueryType() {
		return querytype;
	}

	public void setQueryType(String queryType) {
		querytype = queryType;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMesssageId() {
		return mid;
	}

	public void setMesssageId(String messsageId) {
		mid = messsageId;
	}

	public String getBannerText() {
		return bannerTxt;
	}

	public void setBannerText(String bannerText) {
		bannerTxt = bannerText;
	}

	public byte getBannerStyle() {
		return bannerstyle;
	}

	public void setBannerStyle(byte bannerStyle) {
		bannerstyle = bannerStyle;
	}

	public byte getLeftOptionText() {
		return lopttxt;
	}

	public void setLeftOptionText(byte leftOptionText) {
		lopttxt = leftOptionText;
	}

    public String[] getSearchText() {
        return searchText;
    }

    public void setSearchText(String[] searchText) {
        this.searchText = searchText;
    }

    public boolean isIsPhoneNumberEnabled() {
        return isPhoneNumberEnabled;
    }

    public void setIsPhoneNumberEnabled(boolean isPhoneNumberEnabled) {
        this.isPhoneNumberEnabled = isPhoneNumberEnabled;
    }

    boolean isIsUrlHeighlight() {
        return isUrlHeighlight;
    }

    public void setIsUrlHeighlight(boolean isUrlHeighlight) {
        this.isUrlHeighlight = isUrlHeighlight;
    }
    
//      boolean isInternalLB() {
//        return internalLB;
//    } //cr121hema
//    public void setInternalLB(boolean internalLB){
//            this.internalLB = internalLB;
//    }
}
