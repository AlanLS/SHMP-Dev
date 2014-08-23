
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.list.ListCellRenderer;
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

    private Label focus = null;
    private int focusColor = 0xCC862A;

    // private KlinkMenuCell focus = new KlinkMenuCell();
    public LWRendererMenuCell()
    {
        super();
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

    
    public void refreshTheme()
    {
        super.refreshTheme();
        focus.refreshTheme();
    }

    /*
     * Overridden to do nothing and remove a performance issue where renderer
     * changes perform needless repaint calls
     */
    
    public void repaint()
    {
    }

    public Component getListCellRendererComponent(final List list, final Object value, final int index, final boolean isSelected)
    {
        setFocus(isSelected);
        Command c = (Command) value;
        setText(c.getCommandName());
        return this;
    }

    public Component getListFocusComponent(final List arg0)
    {
        return focus;
    }
}
