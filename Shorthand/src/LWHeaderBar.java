
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Form;
import com.sun.lwuit.Image;
import com.sun.lwuit.Label;
import com.sun.lwuit.MenuBar;
import com.sun.lwuit.layouts.BorderLayout;
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
public class LWHeaderBar extends Container
{

    LWDTO dto;
    static public Style titleStyle;
    static final public byte BAR_TYPE_HEADER = 0;
    static final public byte BAR_TYPE_SECONDARY_HEADER = 1;
    static final public byte BAR_TYPE_MENUBAR = 2;

    private LWHeaderBar()
    {
        // prevent access
    }

    public LWHeaderBar(LWForm frm, byte barType)
    {
        dto = frm.getDto();
        BorderLayout bl = new BorderLayout();
        bl.setCenterBehavior(BorderLayout.CENTER_BEHAVIOR_CENTER_ABSOLUTE);
        setLayout(bl);
        int bgColor = dto.getHdrBGColor();
        int fgColor = dto.getHdrTextColor();
        if (titleStyle == null)
        {
            titleStyle = new Style();
            titleStyle.setMargin(0, 0, 0, 0);
            titleStyle.setBackgroundGradientStartColor(0xffffff);
            titleStyle.setBackgroundGradientEndColor(bgColor);
            titleStyle.setBackgroundType(Style.BACKGROUND_GRADIENT_LINEAR_VERTICAL);
            titleStyle.setBgColor(bgColor);
            titleStyle.setFgColor(fgColor);
        }
        setUnselectedStyle(titleStyle);
        setHeight(dto.getBarHeights());
        setPreferredH(dto.getBarHeights());
        setFocusable(false);
//
        switch (barType)
        {
            case BAR_TYPE_HEADER:
            {
                CreateHeaderBar();
                break;
            }
            case BAR_TYPE_SECONDARY_HEADER:
            {
                CreateSecondaryHeaderBar();
                break;
            }
            //static final public byte BAR_TYPE_MENUBAR = 2;
        }
    }

    void setThree(Object leftOb, Object cntrOb, Object rghtOb, Container c)
    {
        if (leftOb != null)
        {
            Component section = setTribarSection(leftOb);
            Style s = section.getStyle();
            s.setAlignment(LEFT);
            section.setUnselectedStyle(s);
            addComponent(BorderLayout.WEST, section);
        }
        if (cntrOb != null)
        {
            Component section = setTribarSection(cntrOb);
            Style s = section.getStyle();
            s.setAlignment(CENTER);
            section.setUnselectedStyle(s);
            addComponent(BorderLayout.CENTER, section);
        }
        if (rghtOb != null)
        {
            Component section = setTribarSection(rghtOb);
            Style s = section.getStyle();
            s.setAlignment(RIGHT);
            section.setUnselectedStyle(s);
            addComponent(BorderLayout.EAST, section);
        }
    }

    public Component setTribarSection(Object ob)
    {
        Label l = null;
        if (ob != null)
        {
            if (ob.getClass() == Image.class)
            {
                l = new Label((Image) ob);
            }
            else if (ob.getClass() == String.class)
            {
                l = new Label((String) ob);
            }
        }
        else
        {
            l = new Label();
        }
        setUnselectedStyle(new Style(titleStyle));
        return l;
    }

    private void CreateHeaderBar()
    {
        Image lwImg = null;
        String sIconName = dto.getHdrIconName();
        if ((sIconName != null) && (sIconName.isEmpty() == false))
        {
            javax.microedition.lcdui.Image img = RecordManager.getImage(sIconName);
            if (img != null)
            {
                int sz = dto.getBarHeights() - 2;
                lwImg = Image.createImage(img).scaledSmallerRatio(sz, sz);
            }
        }
        setThree(lwImg, dto.getHdrText(), dto.getHdrDataSMS(), this);
    }

    private void CreateSecondaryHeaderBar()
    {
        BorderLayout bl = new BorderLayout();
        bl.setCenterBehavior(BorderLayout.CENTER_BEHAVIOR_CENTER);
        Container c = new Container(bl);
        Image lwImg = null;
        String sIconName = dto.getSecHdrIconName();
        if ((sIconName != null) && (sIconName.isEmpty() == false))
        {
            javax.microedition.lcdui.Image img = RecordManager.getImage(sIconName);
            if (img != null)
            {
                int sz = dto.getBarHeights() - 2;
                lwImg = Image.createImage(img).scaledSmallerRatio(sz, sz);
            }
        }
        setThree(lwImg, dto.getSecHdrText(), null, c);
        addComponent(BorderLayout.CENTER, c);
    }

    static Style getTitleStyle()
    {
        return titleStyle;
    }
}
