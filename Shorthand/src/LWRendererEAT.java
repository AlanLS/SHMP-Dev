
import com.sun.lwuit.Component;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.list.CellRenderer;
import com.sun.lwuit.list.ListCellRenderer;
import com.sun.lwuit.plaf.Style;

public class LWRendererEAT extends Label implements ListCellRenderer, CellRenderer
{

    private int focusColor = 0x00FF00;
    private boolean isGrid = false;
    private Label focus = null;

    //private int gridItemSize = 25;
    // private KlinkMenuCell focus = new KlinkMenuCell();
    public LWRendererEAT(int height, boolean gridView)
    {
        super();
        isGrid = gridView;
        if (isGrid == false)
        {
            setHeight(height);
            setPreferredH(height);
        }
        Style s = new Style(getUnselectedStyle());
        s.setMargin(0, 0, 0, 0);
        s.setPadding(1, 1, 5, 5);
        s.setFont(LWFonts.getFont(LWFonts.StandardFontID));
        setUnselectedStyle(s);
        //
        s = new Style(getSelectedStyle());
        s.setMargin(0, 0, 0, 0);
        s.setPadding(1, 1, 5, 5);
        s.setFont(LWFonts.getFont(LWFonts.StandardFontID));
        s.setBackgroundType(Style.BACKGROUND_NONE);
        s.setBgTransparency(0);
        setSelectedStyle(s);
        //
        focus = new Label();
         if (isGrid == false)
        {
            focus.setHeight(height);
            focus.setPreferredH(height);
        }
       //s = new Style(focus.getUnselectedStyle());
       // s.setMargin(0, 0, 0, 0);
       // s.setPadding(1, 1, 5, 5);
       // focus.setUnselectedStyle(s);
        
        s = new Style(focus.getSelectedStyle());
        s.setMargin(0, 0, 0, 0);
        s.setPadding(1, 1, 5, 5);
        s.setBgTransparency(200);
        focus.setSelectedStyle(s);
        focus.setUnselectedStyle(s);
        setFocusColor(focusColor);
    }

    public void setFocusColor(int color)
    {
        Style s = focus.getSelectedStyle();
        s.setBgColor(color);
        s.setBackgroundGradientEndColor(color);
        focus.setUnselectedStyle(s);
       
    }

    public void setTextColor(int color)
    {
        Style s = getUnselectedStyle();
        s.setFgColor(color);
        s = getSelectedStyle();
        s.setFgColor(color);
    }

    @Override
    public void refreshTheme()
    {
        super.refreshTheme();
    }

    /*
     * Overridden to do nothing and remove a performance issue where renderer
     * changes perform needless repaint calls
     */
    @Override
    public void repaint()
    {
    }

    public Component getListCellRendererComponent(final List list, final Object value, final int index, final boolean isSelected)
    {
        setFocus(isSelected);
        Object[] o = (Object[]) value;
        if (o.length > 1)
        {
            setText(o[0].toString());
            //
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
                            int sz = this.getPreferredH() - 2;
                            Image icon = Image.createImage(img).scaledSmallerRatio(sz, sz);
                            setIcon(icon);
                        }
                    }
                }
            }
            if (o.length > 3) // may have face data
            {
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
            }
        }
        return this;
    }

    @Override
    public Component getListFocusComponent(final List arg0)
    {
        return focus;
    }

    //////////////////////////////////////
    @Override
    public Component getCellRendererComponent(Component list, Object model, Object value, int index, boolean isSelected)
    {
        setFocus(isSelected);
        //
        Object[] o = (Object[]) value;
        if (o.length > 1) // at least two element array
        {
            if (isGrid == false)
            {
                setText(o[0].toString());
            }//
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
                            if (isGrid)
                            {
                                /* int sz = getGridItemSize() - 2;
                                 if (img.getHeight() > sz)
                                 {
                                 if ((img.getHeight() > sz) && (img.getHeight() >= img.getWidth()))
                                 {
                                 icon = Image.createImage(img).scaledHeight(sz);
                                 }
                                 }
                                 if (img.getWidth() > sz) //&& (img.getWidth() >= img.getHeight()))
                                 {
                                 icon = Image.createImage(img).scaledWidth(sz);
                                 }
                                 else*/
                                icon = Image.createImage(img);
                                getStyle().setAlignment(Component.CENTER);
                            }
                            else
                            {
                                int sz = this.getPreferredH() - 2;
                                icon = Image.createImage(img).scaledSmallerRatio(sz, sz);
                            }
                            setIcon(icon);
                        }
                    }
                }
            }
            if (isGrid == false)
            {
                if (o.length > 3) // may have face data
                {
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
                }
            }
        }
        return this;
    }

    public Component getFocusComponent(Component list)
    {
        return this;
    }
}
