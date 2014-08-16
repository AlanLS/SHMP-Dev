
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.list.ListCellRenderer;
import com.sun.lwuit.plaf.Border;
import com.sun.lwuit.plaf.Style;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author alan
 */
public class LWRendererMenuCell extends Label implements ListCellRenderer
{

    private final Label focus;
    private int focusColor = 0xCC862A;

    // private KlinkMenuCell focus = new KlinkMenuCell();
    public LWRendererMenuCell()
    {
        super();
        Style s = new Style(getStyle());
        s.setMargin(1, 1);
        s.setBorder(null);
        s.setBgTransparency(0);
        s.setFont(LWFonts.getFont(LWFonts.StandardFontID));
        setSelectedStyle(s);
        setUnselectedStyle(s);
        focus = new Label();
        Style ss = new Style(s);
        //ss.setBackgroundGradientStartColor(0xFFFFFF);
        //ss.setBackgroundType(Style.BACKGROUND_GRADIENT_LINEAR_VERTICAL);
        ss.setMargin(0, 0);
        ss.setBgTransparency(200);
        focus.setSelectedStyle(ss);
        focus.setUnselectedStyle(ss);
        setFocusColor(focusColor);
    }

    public void setFocusColor(int color)
    {
        focusColor = color;
        Style s = focus.getStyle();
        s.setBgColor(focusColor);
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
        focus.refreshTheme();
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
        Command c = (Command) value;
        // if (c.getCommandName().equalsIgnoreCase("Back"))
        // {  
        //     setText(null);
        // } 
        // else
        // {
        setText(c.getCommandName());
        // }
        return this;
    }

    public Component getListFocusComponent(final List arg0)
    {
        return focus;
    }
}
