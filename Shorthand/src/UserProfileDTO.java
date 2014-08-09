/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Sasikumar
 */
public class UserProfileDTO {

    private String userPhoneNumber = null;

    private String name = null;

    private String plusUser = null;

    private String bannerText = null;

    /**
	 * Banner style 0 - not scrollable and no selectable 1 - scrollable but not
	 * selectable 2 - not scrollable but selectable 3 - scrollable and
	 * selectable
	 */
	private byte bannerstyle;

        // left option text
	private byte lopttxt = -1;

        private String[] mItems = null;

        private String information = null;

        private String secondaryHeader = null;

        private String imageVersion = null;

        //CR 14743
        private int gridThumbNailIndex = -1;

        //CR 14801
        private byte displayType = -1;

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

    public byte getDisplayType() {
        return displayType;
    }

    public void setDisplayType(byte displayType) {
        this.displayType = displayType;
    }

    public int getGridThumbNailIndex() {
        return gridThumbNailIndex;
    }

    public void setGridThumbNailIndex(int gridThumbNailIndex) {
        this.gridThumbNailIndex = gridThumbNailIndex;
    }

    public String getImageVersion() {
        return imageVersion;
    }

    public void setImageVersion(String imageVersion) {
        this.imageVersion = imageVersion;
    }

    public String getSecondaryHeader() {
        return secondaryHeader;
    }

    public void setSecondaryHeader(String secondaryHeader) {
        this.secondaryHeader = secondaryHeader;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public byte getLopttxt() {
        return lopttxt;
    }

    public void setLopttxt(byte lopttxt) {
        this.lopttxt = lopttxt;
    }

        public String[] getmItems() {
            return mItems;
        }

        public void setmItems(String[] mItems) {
            this.mItems = mItems;
        }

        

    public String getBannerText() {
        return bannerText;
    }

    public void setBannerText(String bannerText) {
        this.bannerText = bannerText;
    }

    public byte getBannerstyle() {
        return bannerstyle;
    }

    public void setBannerstyle(byte bannerstyle) {
        this.bannerstyle = bannerstyle;
    }

        

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlusUser() {
        return plusUser;
    }

    public void setPlusUser(String plusUser) {
        this.plusUser = plusUser;
    }

}
