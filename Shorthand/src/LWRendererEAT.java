
import com.sun.lwuit.Component;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.List;
import com.sun.lwuit.list.ListCellRenderer;
import com.sun.lwuit.plaf.Border;
import com.sun.lwuit.plaf.Style;

public class LWRendererEAT extends Label implements ListCellRenderer
{
    private final Label focus = new Label();

    // private KlinkMenuCell focus = new KlinkMenuCell();
    public LWRendererEAT()
    {
        super();
        
        Style s = new Style(getStyle());
        s.setMargin(1, 1);
        s.setBorder(null);
        s.setBgTransparency(0);
        setSelectedStyle(s);
        setUnselectedStyle(s);
        focus.getStyle().setBgTransparency(0);
        focus.getStyle().setBorder(Border.createLineBorder(3, 0x00ff00));
        
    }
    
     public void setFocusColor(int color)
     {
         focus.getStyle().setBorder(Border.createLineBorder(3, color));
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
        Object[] o = (Object[]) value;
        setHeight(25);
        setText(o[0].toString());
        if (o[1] != null) // there's an image resource path
        {
            // get image here
            
            //if ((System.currentTimeMillis() & 0xF) > 0x7) 
            //{
            //    Image img = Image.createImage(25, 25, 0xFF00FF);
            //    setIcon(img);
            //}
        }
        else
        {
            
        }
            
           
        return this;
    }

    public Component getListFocusComponent(final List arg0)
    {
        return focus;
    }
}
