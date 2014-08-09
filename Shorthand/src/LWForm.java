
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Display;
import com.sun.lwuit.Form;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.plaf.Border;
import com.sun.lwuit.plaf.Style;
import jg.Resources;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author alan
 */
abstract public class LWForm extends Form
{
    LWDTO dto;
    Style titleStyle = null;

    protected Command[] options;
    protected Command[] escapeOptions;
    protected Command[] currentActiveOptions;

    public void initialize(LWDTO _dto)
    {
        dto = _dto;
        //
        getStyle().setBgColor(dto.getFormBGColor());
        //
        if (dto.getFormBGImageID() >= 0)
        {
            javax.microedition.lcdui.Image img = Resources.getImage(dto.getFormBGImageID());
            Image lwImg = Image.createImage(img);
            getStyle().setBgImage(lwImg);
            int[] splashRGB = new int[1];
            img.getRGB(CustomCanvas.splashRGB, 0, 1, 1, 1, 1, 1);
            getStyle().setBgColor(splashRGB[0]); // overrides
        }
        //
        setTitleComponent();
        //
        setSecondaryHeader();
        //
        setBackCommand(new Command(dto.getBackText()));
        //
        setOptions();
        //
        setMenuBarLAF();
        //
        
        //

    }

    public LWForm()
    {
        super();
        setLayout(new BorderLayout());
        setScrollable(false);
    }

    protected void setMenuBarLAF()
    {
        Style s = new Style(this.titleStyle);

        s.setBackgroundGradientStartColor(dto.getHdrBGColor());
        s.setBgColor(dto.getHdrBGColor());
        s.setFgColor(dto.getHdrFGColor());
        getMenuBar().setUnselectedStyle(s);

        Style ss = new Style(s);
        ss.setBackgroundGradientStartColor(ss.getBackgroundGradientEndColor());
        ss.setBackgroundGradientEndColor(dto.getHdrBGColor());
        getMenuBar().setPressedStyle(ss);

        Style sss = new Style(ss);
        
        getMenuBar().setSelectedStyle(sss);
        setMenuCellRenderer(new LWRendererMenuCell());
        
        
        LWRendererMenuCell rmc = new LWRendererMenuCell();
        rmc.setFocusColor(dto.getHighlightColor());
        getMenuBar().setMenuCellRenderer(rmc);
    }

    protected void setOptions()
    {
         if (dto.getOptID() != null)
        {
            int lngth = dto.getOptID().length;
            options = new Command[lngth];

            for (int i = 0; i < lngth; ++i)
            {
                Command cmd = new Command(Constants.options[dto.getOptID()[i]], dto.getOptID()[i]);
                options[lngth-i-1] = cmd;
            }
        }

        if (dto.getOptIDEsc() != null)
        {
            int lngth = dto.getOptIDEsc().length;
            escapeOptions = new Command[lngth];

            for (int i = 0; i < lngth; ++i)
            {
                Command cmd = new Command(Constants.options[dto.getOptIDEsc()[i]], dto.getOptIDEsc()[i]);
                escapeOptions[lngth-i-1] = cmd;
            }
        }
    }
    
    public void setCurrentOptionList(Command[] optList)
    {
        removeAllCommands();
        addCommand(getBackCommand());
          
        for (int i=0; i<optList.length; ++i)
        {
            addCommand(optList[i]);
        }
        currentActiveOptions = optList;
        revalidate();
        repaint();
    }
    
    public void setItemOptions()
    {
       setCurrentOptionList(options);  
    }
    
    public void setEscapeOptions()
    {
        setCurrentOptionList(escapeOptions); 
    }

    private void setSecondaryHeader()
    {
        Label secHdr = new Label(dto.getSecHdrText());
        Style s = new Style(titleStyle);
        s.setBgColor(dto.getSHdrBGColor());
        s.setFgColor(dto.getSHdrFGColor());
        s.setAlignment(LEFT);
        secHdr.setUnselectedStyle(s);

        Image lwImg = null;
        if (dto.getHdrIconID() >= 0)
        {
            javax.microedition.lcdui.Image img = Resources.getImage(dto.getHdrIconID());
            lwImg = Image.createImage(img);
            secHdr.setIcon(lwImg);
        }
        addComponent(BorderLayout.NORTH, secHdr);
    }

    private void setTitleComponent()
    {
        titleStyle = new Style(getTitleComponent().getStyle());
        titleStyle.setBackgroundGradientStartColor(dto.getHdrBGColor());
        titleStyle.setBgColor(dto.getHdrBGColor());
        titleStyle.setFgColor(dto.getHdrFGColor());

        getTitleArea().removeAll();
        Container c = new Container(new BorderLayout());
        getTitleArea().addComponent(BorderLayout.CENTER, c);
        Image lwImg = null;
        if (dto.getHdrIconID() >= 0)
        {
            javax.microedition.lcdui.Image img = Resources.getImage(dto.getHdrIconID());
            lwImg = Image.createImage(img);
        }
        setThree(lwImg, dto.getHdrText(), dto.getHdrDataSMS(), c);
    }

    void setThree(Object leftOb, Object cntrOb, Object rghtOb, Container c)
    {
        setLeft(leftOb, c);
        setCntr(cntrOb, c);
        setRght(rghtOb, c);
    }

    public void setLeft(Object leftOb, Container c)
    {
        if (leftOb != null) // || (leftOb.getClass()==String.class)))
        {
            Label l = null;

            if (leftOb.getClass() == Image.class)
            {
                l = new Label((Image) leftOb);
            }
            else if (leftOb.getClass() == String.class)
            {
                l = new Label((String) leftOb);
            }

            if (l != null)
            {
                Style s = new Style(titleStyle);
                s.setAlignment(LEFT);
                l.setUnselectedStyle(s);
                c.addComponent(BorderLayout.WEST, l);
            }
        }
    }

    public void setCntr(Object cntrOb, Container c)
    {
        if (cntrOb != null)
        {
            Label l = null;
            if (cntrOb.getClass() == Image.class)
            {
                l = new Label((Image) cntrOb);
            }
            else if (cntrOb.getClass() == String.class)
            {
                l = new Label((String) cntrOb);
            }

            if (l != null)
            {
                Style s = new Style(titleStyle);
                s.setAlignment(LEFT);
                l.setUnselectedStyle(s);
                c.addComponent(BorderLayout.CENTER, l);
            }
        }
    }

    public void setRght(Object RghtOb, Container c)
    {
        if (RghtOb != null)
        {
            Component l = null;
            if (RghtOb.getClass() == Image.class)
            {
                l = new Label((Image) RghtOb);
            }
            else if (RghtOb.getClass() == String.class)
            {
                l = new Label((String) RghtOb);
            }

            if (l != null)
            {
                Style s = new Style(titleStyle);
                s.setAlignment(RIGHT);
                l.setUnselectedStyle(s);
                c.addComponent(BorderLayout.EAST, l);
            }
        }
    }
}
