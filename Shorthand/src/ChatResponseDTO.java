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
public class ChatResponseDTO {

	// Sender name
	private String buddyName = null;

	// Query Type
	//private String screenName = null;

	// Banner Text
	private String bannerTxt = null;

	// Banner Style
	private byte bannerstyle = 0;

	private byte lopttxt = -1;

        private boolean isHyperLinkEnabled = false;

        private boolean isPhoneNumberEnabled = false;

        private String[] mItems = null;

        //private boolean isEntryboxEnabled = false;
        
        private boolean isNative = false;
        
        private short letterCount=-1;

        private boolean isShowNative = false;

        private String appName = null;

//        private String queryFormat = null;

        private String chatId = null;

        private String chatName = null;

        private String abbervation = null;
        
        private String plusUser = null;
        
        public String getPlusUser(){
            return plusUser;
        }
        public void setPlusUser(String plusUser){
            this.plusUser= plusUser;
        }

        public String getAbbervation() {
            return abbervation;
        }

        public void setAbbervation(String abbervation) {
            this.abbervation = abbervation;
        }

        public String getChatName() {
            return chatName;
        }

        public void setChatName(String chatName) {
            this.chatName = chatName;
        }

        public String getChatId() {
            return chatId;
        }

        public void setChatId(String chatId) {
            this.chatId = chatId;
        }

//        public String getQueryFormat() {
//            return queryFormat;
//        }
//
//        public void setQueryFormat(String queryFormat) {
//            this.queryFormat = queryFormat;
//        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getAppName() {
            return appName;
        }

        /** Get the Native textbox is Shown or not */
        public boolean isIsShowNative() {
            return isShowNative;
        }

        public void setIsShowNative(boolean isShowNative) {
            this.isShowNative = isShowNative;
        }


         public short getLetterCount() {
            return letterCount;
        }

        public void setLetterCount(short letterCount) {
            this.letterCount = letterCount;
        }


        /** Get the Native Textbox byte */
        public boolean isIsNative() {
            return isNative;
        }

        /** Set the Native Textbox byte */
        public void setIsNative(boolean isNative) {
            this.isNative = isNative;
        }

//        public boolean isIsEntryboxEnabled() {
//            return isEntryboxEnabled;
//        }
//
//        public void setIsEntryboxEnabled(boolean isEntryboxEnabled) {
//            this.isEntryboxEnabled = isEntryboxEnabled;
//        }

        /** Get the Menu Item Names */
        public String[] getMItems() {
            return mItems;
        }

        /** Set the Menu Item Names */
        public void setMItems(String[] mItems) {
            this.mItems = mItems;
        }

	public String getBuddyName() {
		return buddyName;
	}

	public void setBuddyName(String senderName) {
		buddyName = senderName;
	}

//	public String getScreenName() {
//		return screenName;
//	}
//
//	public void setScreenName(String queryType) {
//		screenName = queryType;
//	}

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

    public boolean isIsHyperLinkEnabled() {
        return isHyperLinkEnabled;
    }

    public void setIsHyperLinkEnabled(boolean isHyperLinkEnabled) {
        this.isHyperLinkEnabled = isHyperLinkEnabled;
    }

    public boolean isIsPhoneNumberEnabled() {
        return isPhoneNumberEnabled;
    }

    public void setIsPhoneNumberEnabled(boolean isPhoneNumberEnabled) {
        this.isPhoneNumberEnabled = isPhoneNumberEnabled;
    }

}
