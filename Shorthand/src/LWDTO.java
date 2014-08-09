
/**
 *
 * @author alan
 */
abstract public class LWDTO
{
    private String hdrText = null;
    private int hdrIconID = -1;
    private String hdrDataSMS = null;
 //
    private String secHdrText = null;
    private int secHdrIconID = -1;
  // 
    private byte[] optID = null;
    private byte[] optIDEsc = null;
//
    //These can be provided elsewhere possibly as statics if need be   
    private int hdrBGColor = 0x0000FF;   // or any other default color
    private int hdrFGColor = 0xFFFFFF;   // text color or any other default color
//
    private int sHdrBGColor = hdrBGColor;   // or any other default color
    private int sHdrFGColor = hdrFGColor;   // text color or any other default color
//
    private int FormBGColor = 0xE0E0E0;   // text color or any other default color
    private int FormBGImageID = -1;   // resource id# (RecordStore Storage?)
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
     * @return the hdrIconID
     */
    public int getHdrIconID()
    {
        return hdrIconID;
    }

    /**
     * @param hdrIconID the hdrIconID to set
     */
    public void setHdrIconID(int hdrIconID)
    {
        this.hdrIconID = hdrIconID;
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
     * @return the secHdrIconID
     */
    public int getSecHdrIconID()
    {
        return secHdrIconID;
    }

    /**
     * @param secHdrIconID the secHdrIconID to set
     */
    public void setSecHdrIconID(int secHdrIconID)
    {
        this.secHdrIconID = secHdrIconID;
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
     * @return the FormBGImageID
     */
    public int getFormBGImageID()
    {
        return FormBGImageID;
    }

    /**
     * @param FormBGImageID the FormBGImageID to set
     */
    public void setFormBGImageID(int FormBGImageID)
    {
        this.FormBGImageID = FormBGImageID;
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
}
