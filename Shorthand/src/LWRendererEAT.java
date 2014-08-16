
import com.sun.lwuit.Component;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.list.ListCellRenderer;
import com.sun.lwuit.plaf.Border;
import com.sun.lwuit.plaf.Style;

public class LWRendererEAT extends Label implements ListCellRenderer
{

    private int focusColor = 0x00FF00;
    private final Label focus;

    // private KlinkMenuCell focus = new KlinkMenuCell();
    public LWRendererEAT(int height)
    {
        super();
        Style s = new Style(getStyle());
        s.setMargin(1, 1);
        s.setBorder(null);
        s.setBgTransparency(0);
        s.setFont(LWFonts.getFont(LWFonts.StandardFontID));
        setSelectedStyle(s);
        setUnselectedStyle(s);
        setHeight(height);
        setPreferredH(height);
        focus = new Label();
        Style ss = new Style(s);
        ss.setBackgroundGradientStartColor(0xFFFFFF);
        ss.setBackgroundType(Style.BACKGROUND_GRADIENT_LINEAR_VERTICAL);
        ss.setMargin(0, 0);
        focus.setSelectedStyle(ss);
        focus.setUnselectedStyle(ss);
        focus.setHeight(height);
        focus.setPreferredH(height);
        setFocusColor(focusColor);
    }

    public void setFocusColor(int color)
    {
        focusColor = color;
        Style s = focus.getStyle();
        s.setBackgroundGradientEndColor(focusColor);
        focus.setSelectedStyle(s);
        focus.setUnselectedStyle(s);
    }

    public void setTextColor(int color)
    {
        Style s = getStyle();
        s.setFgColor(color);
        setSelectedStyle(s);
        setUnselectedStyle(s);
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
                        this.setIcon(icon);
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
        return this;
    }

    public Component getListFocusComponent(final List arg0)
    {
        return focus;
        //return null;
    }
}
