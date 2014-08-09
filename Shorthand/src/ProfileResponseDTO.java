

/**
 * @author Hakuna Matata
 * @version 2.0
 * @copyright (c) John Mcdonnough
 */

/**
 * Response DTO for Profile presenter
 */
public class ProfileResponseDTO {

	private String[] profilenames = null;

	private String[] logolinks = null;
        
        private String[] msgUnReadCount = null;

        private int[] chatUnReadCount = null;

	private byte lopttxt = -1;

        public int[] getChatUnReadCount() {
            return chatUnReadCount;
        }

        public void setChatUnReadCount(int[] chatUnReadCount) {
            this.chatUnReadCount = chatUnReadCount;
        }

        public String[] getMsgUnReadCount() {
            return msgUnReadCount;
        }

        public void setMsgUnReadCount(String[] msgUnReadCount) {
            this.msgUnReadCount = msgUnReadCount;
        }

	public byte getLeftOptionText() {
		return lopttxt;
	}

	public void setLeftOptionText(byte leftOptionText) {
		lopttxt = leftOptionText;
	}

	public String[] getLogoLink() {
		return logolinks;
	}

	public void setLogoLink(String[] logoLink) {
		logolinks = logoLink;
	}

	public String[] getProfileName() {
		return profilenames;
	}

	public void setProfileName(String[] profileName) {
		profilenames = profileName;
	}
}
