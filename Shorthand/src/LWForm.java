
import com.sun.lwuit.Command;
import com.sun.lwuit.Container;
import com.sun.lwuit.Display;
import com.sun.lwuit.Form;
import com.sun.lwuit.Image;
import com.sun.lwuit.MenuBar;
import com.sun.lwuit.geom.Dimension;
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
abstract public class LWForm extends Form
{

    private static Dimension screenDimen = null;

    public static Dimension getScreenDimen()
    {
        if (screenDimen == null)
        {
            screenDimen = new Dimension(Display.getInstance().getDisplayWidth(), Display.getInstance().getDisplayHeight());
        }
        return screenDimen;
    }

    private LWDTO dto;

    protected Command[] options;
    protected Command[] escapeOptions;
    protected Command[] entryOptions;
    protected Command[] currentActiveOptions;

    public LWForm(LWDTO _dto)
    {
        super();
        setLayout(new BorderLayout());
        setScrollable(false);
        dto = _dto;
        //
        Style s = getStyle();
        s.setBgColor(dto.getFormBGColor());
        //
        String bgImgName = dto.getFormBGImageName();
        if ((bgImgName != null) && (bgImgName.isEmpty() == false))
        {
            javax.microedition.lcdui.Image img = RecordManager.getImage(bgImgName);
            Image lwImg = Image.createImage(img);
            s.setBgImage(lwImg);
            s.setBackgroundType(Style.BACKGROUND_IMAGE_ALIGNED_CENTER);
            int[] splashRGB = new int[1];
            img.getRGB(CustomCanvas.splashRGB, 0, 1, 1, 1, 1, 1);
            s.setBgColor(splashRGB[0]);
        }
        //
        createAndSetHeaderComponent();
        //
        createSecondaryHeader();
        //
        setBackCommand(new Command(Constants.options[getDto().getBackID()], getDto().getBackID()));
        addCommand(getBackCommand());
        //
        setOptions();
        //
        setMenuBarLAF();
    }

    private LWForm()
    {
    }

    protected void setMenuBarLAF()
    {
        MenuBar m = this.getMenuBar();
        Style s = getMenuStyle();// getComponentForm().getStyle();
        //
        s = LWHeaderBar.getTitleStyle();
        if (s != null)
        {
            s = new Style(s);
        }
        else
        {
            s = m.getStyle();
        }
        m.setSelectedStyle(s);
        m.setUnselectedStyle(s);
        m.setPressedStyle(s);
        //m.getStyle().setMargin(0, 0, 0, 0);
        m.setHeight(getDto().getBarHeights());
        m.setPreferredH(getDto().getBarHeights());
        //
        LWRendererMenuCell rmc = new LWRendererMenuCell();
        setMenuCellRenderer(rmc);
        rmc.setFocusColor(getDto().getMenuHighlightColor());
        getMenuBar().setMenuCellRenderer(rmc);
    }

    protected void setOptions()
    {
        if (getDto().getOptID() != null)
        {
            int lngth = getDto().getOptID().length;
            options = new Command[lngth];
            for (int i = 0; i < lngth; ++i)
            {
                Command cmd = new Command(Constants.options[getDto().getOptID()[i]], getDto().getOptID()[i]);
                options[lngth - i - 1] = cmd;
            }
        }
        if (getDto().getOptIDEsc() != null)
        {
            int lngth = getDto().getOptIDEsc().length;
            escapeOptions = new Command[lngth];
            for (int i = 0; i < lngth; ++i)
            {
                Command cmd = new Command(Constants.options[getDto().getOptIDEsc()[i]], getDto().getOptIDEsc()[i]);
                escapeOptions[lngth - i - 1] = cmd;
            }
        }
        //
        if (getDto().getOptIDEntry() != null)
        {
            int lngth = getDto().getOptIDEntry().length;
            entryOptions = new Command[lngth];
            for (int i = 0; i < lngth; ++i)
            {
                Command cmd = new Command(Constants.options[getDto().getOptIDEntry()[i]], getDto().getOptIDEntry()[i]);
                entryOptions[lngth - i - 1] = cmd;
            }
        }
    }

    public void setCurrentOptionList(Command[] optList)
    {
        if (this.getCommandCount() > 0)
        {
            removeAllCommands();
        }
        addCommand(getBackCommand());
        if (optList == null)
        {
            optList = new Command[]
            {
            };
        }
        for (int i = 0; i < optList.length; ++i)
        {
            addCommand(optList[i]);
        }
        currentActiveOptions = optList;
    
        repaint();
    }

    private void createSecondaryHeader()
    {
        Container c = new LWHeaderBar(this, LWHeaderBar.BAR_TYPE_SECONDARY_HEADER);
        if (c.getComponentCount() > 0)
        {
            addComponent(BorderLayout.CENTER, new LWHeaderBar(this, LWHeaderBar.BAR_TYPE_SECONDARY_HEADER));
        }
    }

    private void createAndSetHeaderComponent()
    {
        Container ta = getTitleArea();
        ta.removeAll();
        ta.setHeight(dto.getBarHeights());
        ta.setPreferredH(dto.getBarHeights());
        ta.setFocusable(false);
        ta.addComponent(BorderLayout.CENTER, new LWHeaderBar(this, LWHeaderBar.BAR_TYPE_HEADER));
    }

    public int getScreenW()
    {
        return getScreenDimen().getWidth();
    }

    public int getScreenH()
    {
        return getScreenDimen().getHeight();
    }

    /**
     * @return the dto
     */
    public LWDTO getDto()
    {
        return dto;
    }

}
