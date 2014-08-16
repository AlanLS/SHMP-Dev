
/**
 *
 * @author alan
 */
abstract public class LWDTO
{

    private int barHeights = 20;
    private String hdrText = "Message+";
    private String hdrIconName = "Message+header";
    private String hdrDataSMS = null;
    //
    private String secHdrText = null;
    private String secHdrIconName = null;
    // 
    private byte[] optID = null;
    private byte[] optIDEsc = null;
    private byte[] optIDEntry = null;
    //

//
    //These can be provided elsewhere possibly as statics if need be   
    private int hdrBGColor = 0xB8B8B8;   // or any other default color
    private int hdrTextColor = 0x554344;   // text color or any other default color
//
    private int sHdrBGColor = hdrBGColor;   // or any other default color
    private int sHdrTextColor = hdrTextColor;   // text color or any other default color
//
    private int FormBGColor = 0xFFFFFF;   // text color or any other default color
    //private int FormBGImageID = 1;   // resource id# (RecordStore Storage?)
    private String FormBGImageName = "Message+bg";   // resource id# (RecordStore Storage?)
    //

    private byte backID = -1;   // resource id# (RecordStore Storage?)
//
    private int menuBarBGColor = hdrBGColor;   // or any other default color
    private int menuBarFGColor = hdrTextColor;   // text color or any other default color
    private int highlightColor = 0x8CC63F; // box around selection (list & escape)
    private int menuHighlightColor = 0xEEBA41; // box around selection (list & escape)
//      public static void drawBanner(String bannerText, byte style, boolean isSelected, Graphics g,boolean isBannerMove) {

    private int bannerBGColor = hdrBGColor;   // or any other default color
    private int bannerTextColor = hdrTextColor;

    private int escTextColor = 0xefefef;   // text color or any other default color  
    private int listTextColor = 0xefefef;

    /**
     * @return the hdrText
     */
    public String getHdrText()
    {
        return hdrText;
    }

    /**
     * @param hdrText the hdrText to set
     */
    public void setHdrText(String hdrText)
    {
        this.hdrText = hdrText;
    }

    /**
     * @return the hdrDataSMS
     */
    public String getHdrDataSMS()
    {
        return hdrDataSMS;
    }

    /**
     * @param hdrDataSMS the hdrDataSMS to set
     */
    public void setHdrDataSMS(String hdrDataSMS)
    {
        this.hdrDataSMS = hdrDataSMS;
    }

    /**
     * @return the secHdrText
     */
    public String getSecHdrText()
    {
        return secHdrText;
    }

    /**
     * @param secHdrText the secHdrText to set
     */
    public void setSecHdrText(String secHdrText)
    {
        this.secHdrText = secHdrText;
    }

    /**
     * @return the hdrBGColor
     */
    public int getHdrBGColor()
    {
        return hdrBGColor;
    }

    /**
     * @param hdrBGColor the hdrBGColor to set
     */
    public void setHdrBGColor(int hdrBGColor)
    {
        this.hdrBGColor = hdrBGColor;
    }

    /**
     * @return the hdrTextColor
     */
    public int getHdrFGColor()
    {
        return hdrTextColor;
    }

    /**
     * @param hdrFGColor the hdrTextColor to set
     */
    public void setHdrFGColor(int hdrFGColor)
    {
        this.hdrTextColor = hdrFGColor;
    }

    /**
     * @return the shdrBGColor
     */
    public int getSHdrBGColor()
    {
        return sHdrBGColor;
    }

    /**
     * @param sHdrBGColor the sHdrBGColor to set
     */
    public void setSHdrBGColor(int sHdrBGColor)
    {
        this.sHdrBGColor = sHdrBGColor;
    }

    /**
     * @return the sHdrTextColor
     */
    public int getSHdrFGColor()
    {
        return sHdrTextColor;
    }

    /**
     * @param sHdrFGColor the sHdrTextColor to set
     */
    public void setSHdrFGColor(int sHdrFGColor)
    {
        this.sHdrTextColor = sHdrFGColor;
    }

    /**
     * @return the FormBGColor
     */
    public int getFormBGColor()
    {
        return FormBGColor;
    }

    /**
     * @param FormBGColor the FormBGColor to set
     */
    public void setFormBGColor(int FormBGColor)
    {
        this.FormBGColor = FormBGColor;
    }

    /**
     * @return the highlightColor
     */
    public int getHighlightColor()
    {
        return highlightColor;
    }

    /**
     * @param highlightColor the highlightColor to set
     */
    public void setHighlightColor(int highlightColor)
    {
        this.highlightColor = highlightColor;
    }

    /**
     * @return the menuBarBGColor
     */
    public int getMenuBarBGColor()
    {
        return menuBarBGColor;
    }

    /**
     * @param menuBarBGColor the menuBarBGColor to set
     */
    public void setMenuBarBGColor(int menuBarBGColor)
    {
        this.menuBarBGColor = menuBarBGColor;
    }

    /**
     * @return the menuBarFGColor
     */
    public int getMenuBarFGColor()
    {
        return menuBarFGColor;
    }

    /**
     * @param menuBarFGColor the menuBarFGColor to set
     */
    public void setMenuBarFGColor(int menuBarFGColor)
    {
        this.menuBarFGColor = menuBarFGColor;
    }

    /**
     * @return the optIDEsc
     */
    public byte[] getOptIDEsc()
    {
        return optIDEsc;
    }

    /**
     * @param optIDEsc the optIDEsc to set
     */
    public void setOptIDEsc(byte[] optIDEsc)
    {
        this.optIDEsc = optIDEsc;
    }

    /**
     * @return the optID
     */
    public byte[] getOptID()
    {
        return optID;
    }

    /**
     * @param optID the optID to set
     */
    public void setOptID(byte[] optID)
    {
        this.optID = optID;
    }

    /**
     * @return the backID
     */
    public byte getBackID()
    {
        return backID;
    }

    /**
     * @param backID the backID to set
     */
    public void setBackID(byte backID)
    {
        this.backID = backID;
    }

    /**
     * @return the optIDEntry
     */
    public byte[] getOptIDEntry()
    {
        return optIDEntry;
    }

    /**
     * @param optIDEntry the optIDEntry to set
     */
    public void setOptIDEntry(byte[] optIDEntry)
    {
        this.optIDEntry = optIDEntry;
    }

    /**
     * @return the FormBGImageName
     */
    public String getFormBGImageName()
    {
        return FormBGImageName;
    }

    /**
     * @param FormBGImageName the FormBGImageName to set
     */
    public void setFormBGImageName(String FormBGImageName)
    {
        this.FormBGImageName = FormBGImageName;
    }

    /**
     * @return the hdrIconName
     */
    public String getHdrIconName()
    {
        return hdrIconName;
    }

    /**
     * @param hdrIconName the hdrIconName to set
     */
    public void setHdrIconName(String hdrIconName)
    {
        this.hdrIconName = hdrIconName;
    }

    /**
     * @return the secHdrIconName
     */
    public String getSecHdrIconName()
    {
        return secHdrIconName;
    }

    /**
     * @param secHdrIconName the secHdrIconName to set
     */
    public void setSecHdrIconName(String secHdrIconName)
    {
        this.secHdrIconName = secHdrIconName;
    }

    /**
     * @return the menuHighlightColor
     */
    public int getMenuHighlightColor()
    {
        return menuHighlightColor;
    }

    /**
     * @param menuHighlightColor the menuHighlightColor to set
     */
    public void setMenuHighlightColor(int menuHighlightColor)
    {
        this.menuHighlightColor = menuHighlightColor;
    }

    /**
     * @return the bannerBGColor
     */
    public int getBannerBGColor()
    {
        return bannerBGColor;
    }

    /**
     * @param bannerBGColor the bannerBGColor to set
     */
    public void setBannerBGColor(int bannerBGColor)
    {
        this.bannerBGColor = bannerBGColor;
    }

    /**
     * @return the bannerTextColor
     */
    public int getBannerTextColor()
    {
        return bannerTextColor;
    }

    /**
     * @param bannerTextColor the bannerTextColor to set
     */
    public void setBannerTextColor(int bannerTextColor)
    {
        this.bannerTextColor = bannerTextColor;
    }

    /**
     * @return the listTextColor
     */
    public int getListTextColor()
    {
        return listTextColor;
    }

    /**
     * @param listTextColor the listTextColor to set
     */
    public void setListTextColor(int listTextColor)
    {
        this.listTextColor = listTextColor;
    }

    /**
     * @return the escTextColor
     */
    public int getEscTextColor()
    {
        return escTextColor;
    }

    /**
     * @param escTextColor the escTextColor to set
     */
    public void setEscTextColor(int escTextColor)
    {
        this.escTextColor = escTextColor;
    }

    /**
     * @return the barHeights
     */
    public int getBarHeights()
    {
        return barHeights;
    }

    /**
     * @param barHeights the barHeights to set
     */
    public void setBarHeights(int barHeights)
    {
        this.barHeights = barHeights;
    }
}
