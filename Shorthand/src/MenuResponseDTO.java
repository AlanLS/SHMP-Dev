public class MenuResponseDTO {

	private String secHdrTxt;

	private String[] seqlist;

	private String[] itemnamelist;

	private int[] itemidlist;

	private int[] stylelist;

	private String bannertxt;

	private byte lopttext = -1;

	/**
	 * Banner style 0 - not scrollable and no selectable 1 - scrollable but not
	 * selectable 2 - not scrollable but selectable 3 - scrollable and
	 * selectable
	 */
	private byte bannerstyle;

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

	/**
	 * 
	 * @return
	 */
	public String getSecHdrTxt() {
            return secHdrTxt;
	}

	/**
	 * 
	 * @param secHdrTxt
	 */
	public void setSecHdrTxt(String secHdrTxt) {
            this.secHdrTxt = secHdrTxt;
	}

	/**
	 * 
	 * @return
	 */
	public String[] getSeqlist() {
            return seqlist;
	}

	/**
	 * 
	 * @param seqlist
	 */
	public void setSeqlist(String[] slist) {
            seqlist = slist;
	}

	/**
	 * 
	 * @return
	 */
	public String[] getItemnamelist() {
            return itemnamelist;
	}

	/**
	 * 
	 * @param itemnamelist
	 */
	public void setItemnamelist(String[] itemnamelist) {
            this.itemnamelist = itemnamelist;
	}

	/**
	 * 
	 * @return
	 */
	public int[] getItemidlist() {
            return itemidlist;
	}

	public void setItemidlist(int[] itemidlist) {
            this.itemidlist = itemidlist;
	}

	public int[] getStylelist() {
            return stylelist;
	}

	public void setStylelist(int[] stylelist) {
            this.stylelist = stylelist;
	}

	public String getBannerText() {
            return bannertxt;
	}

	public void setBannerText(String bannerText) {
            bannertxt = bannerText;
	}

	public byte getLopttext() {
            return lopttext;
	}

	public void setLopttext(byte lopttext) {
            this.lopttext = lopttext;
	}

	public byte getBannerStyle() {
            return bannerstyle;
	}

	public void setBannerStyle(byte bannerStyle) {
            bannerstyle = bannerStyle;
	}
}
