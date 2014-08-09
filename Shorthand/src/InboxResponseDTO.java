public class InboxResponseDTO {

	private String secondaryHeaderText;

	private byte lopttxt = -1;

	private InboxItems[] messages;

	/**
	 * Banner style 0 - not scrollable and no selectable 1 - scrollable but not
	 * selectable 2 - not scrollable but selectable 3 - scrollable and
	 * selectable
	 */
	private byte bannerstyle;

	private String bannertxt;

	/**
	 * 
	 * @return
	 */
	public String getSecondaryHeaderText() {
		return secondaryHeaderText;
	}

	/**
	 * 
	 * @param secondaryHeaderText
	 */
	public void setSecondaryHeaderText(String secondaryHeaderText) {
		this.secondaryHeaderText = secondaryHeaderText;
	}

	public byte getLeftOptionText() {
		return lopttxt;
	}

	public void setLeftOptionText(byte leftOptionText) {
		lopttxt = leftOptionText;
	}

	public InboxItems[] getMessages() {
		return messages;
	}

	public void setMessages(InboxItems[] messages) {
		this.messages = messages;
	}

	public byte getBannerStyle() {
		return bannerstyle;
	}

	public void setBannerStyle(byte bannerStyle) {
		bannerstyle = bannerStyle;
	}

	public String getBannerText() {
		return bannertxt;
	}

	public void setBannerText(String bannerText) {
		bannertxt = bannerText;
	}
}
