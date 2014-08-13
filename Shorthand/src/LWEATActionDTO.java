
import java.util.Hashtable;

/**
 *
 * @author alan
 */
public class LWEATActionDTO extends LWDTO
{
//specific to EAT

    private boolean entryBoxEnabled = false;
    private String entryBoxHint = null;
    private byte entryBoxMode = IKeyHandler.MODE_Abc;
    /*
       IKeyHandler.
        public static final byte MODE_123 = 0;
        public static final byte MODE_abc = 1;
        public static final byte MODE_ABC = 2;
        public static final byte MODE_Abc = 3;
    */
    
    private float minValue = Float.NaN;
    private float maxValue = Float.NaN;
    private int maxChar = -1;
    private int minChar = -1;
    
    private byte entryBoxConstraint = 2;
     /*
        if(entryType == 0){
            entryString = "NUMERIC";
        } else if(entryType == 1){
            entryString = "ALPHA";
        } else if(entryType == 2){
            entryString = "ALPHANUMERIC";
        } else if(entryType == 3){
            entryString = "DECIMAL";
        } else if(entryType == 4){
            entryString = "DOLLARCENTS";
        } else if(entryType == 5){
            entryString = "DATE";
        } else if(entryType == 6){
            entryString = "PHONENUMBER";
        }
    */
    // these all need to be same length
    private int[] listItemIds = null;
    private String[] listItems = null;
    private byte[] listItemFaces = null;
    private String[] listImages = null;
    private int listTextColor = 0x3F3F00;

    private String[] escapeText = null;
    private byte[] escapeIDs = null;
    private int escTextColor = 0x007F4F;   // text color or any other default color  

    /**
     * @return the entryBoxEnabled
     */
    public boolean isEntryBoxEnabled()
    {
        return entryBoxEnabled;
    }

    /**
     * @param entryBoxEnabled the entryBoxEnabled to set
     */
    public void setEntryBoxEnabled(boolean entryBoxEnabled)
    {
        this.entryBoxEnabled = entryBoxEnabled;
    }

    /**
     * @return the entryBoxHint
     */
    public String getEntryBoxHint()
    {
        return entryBoxHint;
    }

    /**
     * @param entryBoxHint the entryBoxHint to set
     */
    public void setEntryBoxHint(String entryBoxHint)
    {
        this.entryBoxHint = entryBoxHint;
    }

    /**
     * @return the minValue
     */
    public float getMinValue()
    {
        return minValue;
    }

    /**
     * @param minValue the minValue to set
     */
    public void setMinValue(float minValue)
    {
        this.minValue = minValue;
    }

    /**
     * @return the maxValue
     */
    public float getMaxValue()
    {
        return maxValue;
    }

    /**
     * @param maxValue the maxValue to set
     */
    public void setMaxValue(float maxValue)
    {
        this.maxValue = maxValue;
    }

    public float[] getMinMaxValue()
    {
        return new float[]
        {
            minValue, maxValue
        };
    }

    /**
     * @return the maxChar
     */
    public int getMaxChar()
    {
        return maxChar;
    }

    /**
     * @param maxChar the maxChar to set
     */
    public void setMaxChar(int maxChar)
    {
        this.maxChar = maxChar;
    }

    /**
     * @return the minChar
     */
    public int getMinChar()
    {
        return minChar;
    }

    /**
     * @param minChar the minChar to set
     */
    public void setMinChar(int minChar)
    {
        this.minChar = minChar;
    }

    public int[] getMinMaxChar()
    {
        return new int[]
        {
            minChar, maxChar
        };
    }



    /**
     * @return the listItemIds
     */
    public int[] getListItemIds()
    {
        return listItemIds;
    }

    /**
     * @param listItemIds the listItemIds to set
     */
    public void setListItemIds(int[] listItemIds)
    {
        this.listItemIds = listItemIds;
    }

    /**
     * @return the listItems
     */
    public String[] getListItems()
    {
        return listItems;
    }

    /**
     * @param listItems the listItems to set
     */
    public void setListItems(String[] listItems)
    {
        this.listItems = listItems;
    }

    /**
     * @return the listImages
     */
    public String[] getListImages()
    {
        return listImages;
    }

    /**
     * @param listImages the listImages to set
     */
    public void setListImages(String[] listImages)
    {
        this.listImages = listImages;
    }

    /**
     * @return the escapeText
     */
    public String[] getEscapeText()
    {
        return escapeText;
    }

    /**
     * @param escapeText the escapeText to set
     */
    public void setEscapeText(String[] escapeText)
    {
        this.escapeText = escapeText;
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
     * @return the escapeIDs
     */
    public byte[] getEscapeIDs()
    {
        return escapeIDs;
    }

    /**
     * @param escapeIDs the escapeIDs to set
     */
    public void setEscapeIDs(byte[] escapeIDs)
    {
        this.escapeIDs = escapeIDs;
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
     * @return the listItemFaces
     */
    public byte[] getListItemFaces()
    {
        return listItemFaces;
    }

    /**
     * @param listItemFaces the listItemFaces to set
     */
    public void setListItemFaces(byte[] listItemFaces)
    {
        this.listItemFaces = listItemFaces;
    }

    /**
     * @return the entryBoxMode
     */
    public byte getEntryBoxMode()
    {
        return entryBoxMode;
    }

    /**
     * @param entryBoxMode the entryBoxMode to set
     */
    public void setEntryBoxMode(byte entryBoxMode)
    {
        this.entryBoxMode = entryBoxMode;
    }

    /**
     * @return the entryBoxConstraint
     */
    public byte getEntryBoxConstraint()
    {
        return entryBoxConstraint;
    }

    /**
     * @param entryBoxConstraint the entryBoxConstraint to set
     */
    public void setEntryBoxConstraint(byte entryBoxConstraint)
    {
        this.entryBoxConstraint = entryBoxConstraint;
    }
}
