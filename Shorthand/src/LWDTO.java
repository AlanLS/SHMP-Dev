
/**
 *
 * @author alan
 */
abstract public class LWDTO
{

    private String hdrText = null;
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
    private int hdrBGColor = 0x0000FF;   // or any other default color
    private int hdrFGColor = 0xFFFFFF;   // text color or any other default color
//
    private int sHdrBGColor = hdrBGColor;   // or any other default color
    private int sHdrFGColor = hdrFGColor;   // text color or any other default color
//
    private int FormBGColor = 0xE0E0E0;   // text color or any other default color
    //private int FormBGImageID = 1;   // resource id# (RecordStore Storage?)
    private String FormBGImageName = "Message+bg";   // resource id# (RecordStore Storage?)
    //
    private String backText = "Back";   // resource id# (RecordStore Storage?)
    private byte backID = -1;   // resource id# (RecordStore Storage?)
//
    private int menuBarBGColor = hdrBGColor;   // or any other default color
    private int menuBarFGColor = hdrFGColor;   // text color or any other default color
    private int highlightColor = 0xFF0000; // box around selection (list & escape)
//     

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
     * @return the hdrFGColor
     */
    public int getHdrFGColor()
    {
        return hdrFGColor;
    }

    /**
     * @param hdrFGColor the hdrFGColor to set
     */
    public void setHdrFGColor(int hdrFGColor)
    {
        this.hdrFGColor = hdrFGColor;
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
     * @return the sHdrFGColor
     */
    public int getSHdrFGColor()
    {
        return sHdrFGColor;
    }

    /**
     * @param sHdrFGColor the sHdrFGColor to set
     */
    public void setSHdrFGColor(int sHdrFGColor)
    {
        this.sHdrFGColor = sHdrFGColor;
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
     * @return the backText
     */
    public String getBackText()
    {
        return backText;
    }

    /**
     * @param backText the backText to set
     */
    public void setBackText(String backText)
    {
        this.backText = backText;
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

    class LWBannerClass
    {

    }

}
