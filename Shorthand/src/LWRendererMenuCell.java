
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
    private final Label focus = new Label();

    // private KlinkMenuCell focus = new KlinkMenuCell();
    public LWRendererMenuCell()
    {
        super();
        
        Style s = new Style(getStyle());
        s.setMargin(1, 1);
        s.setBorder(null);
        s.setBgTransparency(0);
       // s.setBgTransparency(0x7F);
        //s.setBgColor(0x3F003F);
        setSelectedStyle(s);
        setUnselectedStyle(s);

        focus.getStyle().setBgTransparency(0);
        focus.getStyle().setBorder(Border.createLineBorder(3, 0x00ff00));
    }
    
     public void setFocusColor(int color)
     {
         focus.getStyle().setBorder(Border.createLineBorder(3, color));
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
        Command c = (Command)value;
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
