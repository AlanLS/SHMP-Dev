
import com.sun.lwuit.Button;
import com.sun.lwuit.Component;
import com.sun.lwuit.Image;
import com.sun.lwuit.list.CellRenderer;
import com.sun.lwuit.plaf.Style;

public class LWRendererEATGrid extends Button implements CellRenderer
{
    private int focusColor = 0x00FF00;

//    private Label focus = null;

    private int gridItemSz = 30;

    //private int gridItemSize = 25;
    // private KlinkMenuCell focus = new KlinkMenuCell();
    public LWRendererEATGrid(int cellSz, LWFormEAT frm)
    {
        super();
        this.setUIID("Label");
        addActionListener(frm);
        gridItemSz = ((cellSz/2) *2);
        setPreferredW(gridItemSz);
        setPreferredH(gridItemSz);
        
        Style s = new Style(getUnselectedStyle());
        s.setMargin(0, 0, 0, 0);
        s.setPadding(0, 0, 0, 0);
        s.setAlignment(Component.CENTER);
        s.setFont(LWFonts.getFont(LWFonts.StandardFontID));
        setUnselectedStyle(s);
        //
        s = new Style(getSelectedStyle());
        s.setMargin(0, 0, 0, 0);
        s.setPadding(0, 0, 0, 0);
        s.setAlignment(Component.CENTER);
        s.setBgTransparency(255);
        setSelectedStyle(s);
        //
 /*       focus = new Label();
        focus.setHeight(cellSz);
        focus.setPreferredH(cellSz);
        //
        s = new Style(focus.getSelectedStyle());
        s.setMargin(0, 0, 0, 0);
         s.setPadding(5, 5, 5, 5);
        s.setBgTransparency(250);
        focus.setSelectedStyle(s);
        focus.setUnselectedStyle(s);
   */     setFocusColor(focusColor);
    }

    public void setFocusColor(int color)
    {
        Style s = getSelectedStyle();
        s.setBgColor(color);
        s.setBackgroundGradientEndColor(color);
   
        setSelectedStyle(s);
    }

  

    
    public void refreshTheme()
    {
        super.refreshTheme();
    }

    /*
     * Overridden to do nothing and remove a performance issue where renderer
     * changes perform needless repaint calls
     */
    
    public void repaint()
    {
    }

    //////////////////////////////////////
    public Component getCellRendererComponent(Component list, Object model, Object value, int index, boolean isSelected)
    {
        setFocus(isSelected);
        Object[] o = (Object[]) value;
        if (o.length > 1)
        {
      
            //    setText(o[0].toString());
            if (o.length > 2) // may have Image data
            {
                if (o[2] != null) // there's an image resource path
                {
                    String imageName = (String) o[2];
                    if (imageName.isEmpty() == false)
                    {
                        javax.microedition.lcdui.Image img = RecordManager.getImage(imageName);
                        if (img != null)
                        {
                            Image icon = null;
                            int sz = (gridItemSz - 10) &  0xFFFE;
                            icon = Image.createImage(img).scaledSmallerRatio(sz, sz);
                            //icon = Image.createImage(img);
                            if (icon != null)
                            {
                                setIcon(icon);
                            }
                        }
                    }
                }
            }
            if (o.length > 3) // may have face data
            {
                /*
                 if (o[3] != null)
                 {
                 boolean isBold = ((Boolean) (o[3]));
                 if (isBold)
                 {
                 getStyle().setFont(LWFonts.getFont(LWFonts.StandardFontBoldID));
                 }
                 else
                 {
                 getStyle().setFont(LWFonts.getFont(LWFonts.StandardFontID));
                 }
                 }
                 */
            }
        }
        return this;
    }

    public Component getFocusComponent(Component list)
    {
        return this;
    }
}
