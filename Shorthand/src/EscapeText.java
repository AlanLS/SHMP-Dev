public class EscapeText {
    
    /** EscapeText */
    private String esText;
    
    /** Goto Id or File Offset */
    private int gotoId;
    
    /** isMenu Or Option */
    private boolean  isOpt;

    public String getEsText() {
        return esText;
    }

    public int getGotoId() {
        return gotoId;
    }

    public boolean  getIsOpt() {
        return isOpt;
    }
    
    public EscapeText(String estxt,boolean  isopt,int id)
    {
        this.esText = estxt;
        this.gotoId =id;
        this.isOpt = isopt;
    }
}
