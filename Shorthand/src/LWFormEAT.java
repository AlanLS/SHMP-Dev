
import com.sun.lwuit.Button;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Display;
import com.sun.lwuit.List;
import com.sun.lwuit.TextField;
import com.sun.lwuit.VirtualKeyboard;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.events.DataChangedListener;
import com.sun.lwuit.events.FocusListener;
import com.sun.lwuit.events.SelectionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;
import com.sun.lwuit.layouts.GridLayout;
import com.sun.lwuit.list.ContainerList;
import com.sun.lwuit.list.DefaultListModel;
import com.sun.lwuit.list.ListModel;
import com.sun.lwuit.plaf.Style;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 *
 * @author alan
 */
public class LWFormEAT extends LWForm implements ActionListener, SelectionListener, FocusListener
{

    static final String LISTNAME_ITEMS = "ITM";
    static final String LISTNAME_ESCAPE = "ESC";

    private LWEATActionDTO act;
    private boolean validateValue = false;
    private boolean validateCharCnt = false;

    private Object[] currentItem = null;
    private Object[] currentEsc = null;
    private String currentEntry = "";
    private Component currentFocus = null;

    static private String _bannerText = "To be, or not to be, that is the question:"
            + " Whether 'tis nobler in the mind to suffer"
            + " The slings and arrows of outrageous fortune,"
            + " Or to take arms against a sea of troubles"
            + " And by opposing end them.";

    private byte _bannerID = -1;
    private byte _bannerStyle = LWDTO.BANNER_STYLE_SCROLL;
    private byte _bannerOperation = LWDTO.BANNER_OPERATIONS_HIGHLIGHT_SELECT;// BANNER_OPERATIONS_NO_HIGHLIGHT;

    static Timer timer;
    static PopupTimerTask putt;
    private boolean startedOnce = false;

    /**
     *
     * @param _dto
     */
    public LWFormEAT(LWDTO _dto)
    {
        super(_dto);
        if (_dto.getClass() != LWEATActionDTO.class)
        {
            //throw new Exception();
        }
        act = (LWEATActionDTO) _dto;
        //
        act.setBanner(_bannerText, _bannerStyle, _bannerOperation, _bannerID);
        //
        addListAndEscape();
        setEntryAndBanner();
        //
        addCommandListener(this);
    }

    public void show()
    {
        if (startedOnce == false)
        {
            startedOnce = true;
            super.show();
            if (timer == null)
            {
                timer = new Timer();
                putt = new PopupTimerTask();
                timer.schedule(putt, 60000);
            }
        }
    }

    private void addListAndEscape()
    {
        //Container c = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        Container c = new Container(new BorderLayout());
        Component cmpLI = createItemsList();
        if (cmpLI != null)
        {
            c.addComponent(BorderLayout.CENTER, cmpLI);
        }
        //
        Component cmpET = createEscapeList();//getEscapeTexts();
        if (cmpET != null)
        {
            c.addComponent(BorderLayout.SOUTH, cmpET);
        }
        addComponent(BorderLayout.CENTER, c);
    }

    private void setEntryAndBanner()
    {
        Container c = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        Component cmpEB = createLineEntryBox();
        if (cmpEB != null)
        {
            c.addComponent(cmpEB);
        }
        Component cmpBN = createBanner();
        if (cmpBN != null)
        {
            c.addComponent(cmpBN);
        }
        addComponent(BorderLayout.SOUTH, c);
    }

    private Component createEscapeList()
    {
        if (act.getEscapeText() == null)
        {
            if (act.getOptIDEsc() != null)
            {
                act.setEscapeText(new String[0]);
            }
        }
        if (act.getEscapeText() != null)
        {
            int lngth = act.getEscapeText().length;
            Vector vec = new Vector(lngth);
            for (int i = 0; i < lngth; ++i)
            {
                vec.addElement(new Object[]
                {
                    act.getEscapeText()[i], (act.getEscapeIDs()[i])
                });
            }
            Component c = createListFromModel(new DefaultListModel(vec));
            c.setName("ESC");
            return c;
        }
        return null;
    }

    private Component createItemsList()
    {
        if (act.getListItems() == null)
        {
            if (act.getOptID() != null)
            {
                act.setListItems(new String[0]);
            }
        }
        if (act.getListItems() != null)
        {
            int lngth = act.getListItems().length;
            Vector vec = new Vector(lngth);
            boolean hasImages = (act.getListImages() != null) && (act.getListImages().length == lngth);
            boolean hasFaces = (act.getListItemFaces() != null) && (act.getListItemFaces().length == lngth);
            for (int i = 0; i < lngth; ++i)
            {
                vec.addElement(new Object[]
                {
                    act.getListItems()[i],
                    (int) (act.getListItemIds()[i]),
                    hasImages ? act.getListImages()[i] : null,
                    hasFaces ? (Boolean) (act.getListItemFaces()[i] == 1) : null,
                });
            }
            Container c = new Container(new BoxLayout(BoxLayout.Y_AXIS));
            //
            final DefaultListModel listModel = new DefaultListModel(vec);
            listModel.addSelectionListener(this);
            //
            Component listComponent = null;
            if (act.isGridLayout() && hasImages)
            {
                listComponent = createListFromModel(listModel);
                //listComponent = createGridListFromModel(listModel);
            }
            else
            {
                listComponent = createListFromModel(listModel);
            }
            listComponent.setName("ITM");
            c.addComponent(listComponent);
            return c;
        }
        return null;
    }

    private Component createListFromModel(ListModel listModel)
    {
        LWRendererEAT _lwRendererEAT = new LWRendererEAT(act.getBarHeights());
        _lwRendererEAT.setFocusColor(act.getHighlightColor());
        _lwRendererEAT.setTextColor(act.getListTextColor());
        //
        List list = new List(listModel);
        list.setRenderer(_lwRendererEAT);
        list.addActionListener(this);
        list.addSelectionListener(this);
        list.addFocusListener(this);
        list.setSmoothScrolling(true);
        list.setPaintFocusBehindList(true);
        list.setFixedSelection(List.FIXED_NONE);
        list.setScrollVisible(true);
        list.setScrollToSelected(true);
        list.setItemGap(0);
        list.setFocusable(true);
        list.requestFocus();
        return list;
    }

    private Component createGridListFromModel(ListModel listModel)
    {
        int mrgn = getContentPane().getStyle().getMargin(Component.LEFT) * 2;
        int dsplyW = Display.getInstance().getDisplayWidth();
        int cpSize = dsplyW - (mrgn * 2);
        int sz = cpSize / 5; // gimme a five!
        final GridLayout gl = new GridLayout(2, 5);
        gl.setAutoFit(false);
        //
        LWRendererEATGrid _lwRendererEAT = new LWRendererEATGrid(sz, this);
        _lwRendererEAT.setFocusColor(act.getHighlightColor());
        //
        final ContainerList list = new ContainerList(listModel);
        list.setLayout(gl);
        list.setRenderer(_lwRendererEAT);//(act.isGridLayout() && hasImages)));
        list.addActionListener(this);
        list.addFocusListener(this);
        list.setSmoothScrolling(true);
        list.setScrollVisible(false);
        list.setFocusable(true);
        return list;
    }

    private Component createBanner()
    {
        while (act.getBannerText() != null) // gives me a break!;
        {
            Button b = new Button(act.getBannerText());
            b.setUIID("Label");
            b.setHeight(act.getBarHeights());
            b.setPreferredH(act.getBarHeights());
            // button to look like label
            //
            b.setName((((Byte) act.getBannerID()).toString()));
            byte oprtn = act.getBannerOperation();
            if (oprtn == LWDTO.BANNER_OPERATIONS_HIGHLIGHT)// static final byte BANNER_OPERATIONS_HIGHLIGHT = 1;
            {
                b.removeActionListener(this);
                b.setFocusable(true);
                b.addFocusListener(this);
            }
            else if (oprtn == LWDTO.BANNER_OPERATIONS_HIGHLIGHT_SELECT)// static final byte BANNER_OPERATIONS_HIGHLIGHT_SELECT = 2;
            {
                b.addActionListener(this);
                b.setFocusable(true);
                b.addFocusListener(this);
            }
            else if (oprtn == LWDTO.BANNER_OPERATIONS_NO_HIGHLIGHT)// static final byte BANNER_OPERATIONS_NO_HIGHLIGHT = 3;
            {
                b.removeActionListener(this);
                b.setFocusable(false);
                b.removeFocusListener(this);
            }
            else
            {
                break;
            }
            byte stl = act.getBannerStyle();
            if (stl == LWDTO.BANNER_STYLE_SCROLL)// static final byte BANNER_STYLE_SCROLL = 1;
            {
                b.setTickerEnabled(true);
            }
            else if (stl == LWDTO.BANNER_STYLE_FIXED)//static final byte BANNER_STYLE_FIXED = 2;
            {
                b.setTickerEnabled(false);
            }
            else
            {
                break;
            }
            Style s = new Style(b.getUnselectedStyle());
            s.setBgColor(act.getBannerBGColor());
            s.setFgColor(act.getBannerTextColor());
            s.setMargin(0, 0, 5, 5);
            s.setBgTransparency(255);
            b.setUnselectedStyle(s);
            //
            Style ss = new Style(b.getUnselectedStyle());
            ss.setBgTransparency(200);
            ss.setBackgroundGradientStartColor(0xFFFFFF);
            ss.setBackgroundType(Style.BACKGROUND_GRADIENT_LINEAR_VERTICAL);
            ss.setBackgroundGradientEndColor(act.getHighlightColor());
            ss.setFgColor(act.getListTextColor());
            b.setSelectedStyle(ss);
            b.setPressedStyle(ss);
            return b;
        }
        return null;
    }

    private Component createLineEntryBox()
    {
        /*
         if(entryType == 0){
         entryString = "NUMERIC";
         } else if(entryType == 1){
         entryString = "ALPHA";
         } else if(entryType == 2){
         entryString = "ALPHANUMERIC";
         } else if(entryType == 3){
         entryString = "DECIMAL";
         } else if(entryType == 4){
         entryString = "DOLLARCENTS";
         } else if(entryType == 5){
         entryString = "DATE";
         } else if(entryType == 6){
         entryString = "PHONENUMBER";
         }
         */
        if (act.isEntryBoxEnabled())
        {
            final TextField tf = new TextField();
            //tf.setSingleLineTextArea(true);
            tf.getStyle().setBgColor(0xffffff);
            tf.setHint(act.getEntryBoxHint());
            tf.addFocusListener(this);
            //tf.addActionListener(this);
            VirtualKeyboard vkb = new VirtualKeyboard();
            switch (act.getEntryBoxConstraint())
            {
                case -1:
                {
                    tf.setInputModeOrder(new String[]
                    {
                        "Abc", "123"
                    });
                    tf.setConstraint(TextField.ANY);
                    vkb.setInputModeOrder(new String[]
                    {
                        VirtualKeyboard.QWERTY_MODE
                    });
                }
                case 0://numeric
                {
                    tf.setInputModeOrder(new String[]
                    {
                        "123"
                    });
                    tf.setConstraint(TextField.NUMERIC);
                    vkb.setInputModeOrder(new String[]
                    {
                        VirtualKeyboard.NUMBERS_MODE
                    });
                }
                break;
                case 1://Alpha
                {
                    tf.setInputModeOrder(new String[]
                    {
                        "Abc"
                    });
                    tf.setConstraint(TextField.ANY);
                    vkb.setInputModeOrder(new String[]
                    {
                        VirtualKeyboard.QWERTY_MODE
                    });
                }
                break;
                case 2://AlphaNumeric
                {
                    tf.setInputModeOrder(new String[]
                    {
                        "Abc", "123"
                    });
                    tf.setConstraint(TextField.ANY);
                    vkb.setInputModeOrder(new String[]
                    {
                        VirtualKeyboard.QWERTY_MODE
                    });
                }
                break;
                case 3://Decimal
                {
                    tf.setInputModeOrder(new String[]
                    {
                        "123"
                    });
                    tf.setConstraint(TextField.DECIMAL);
                    vkb.setInputModeOrder(new String[]
                    {
                        VirtualKeyboard.NUMBERS_MODE
                    });
                }
                case 4://DollarCents
                {
                    tf.setInputModeOrder(new String[]
                    {
                        "123"
                    });
                    tf.setConstraint(TextField.DECIMAL);
                    vkb.setInputModeOrder(new String[]
                    {
                        VirtualKeyboard.NUMBERS_SYMBOLS_MODE
                    });
                }
                case 5://Date
                {
                    tf.setInputModeOrder(new String[]
                    {
                        "123", "Abc"
                    });
                    tf.setConstraint(TextField.ANY);
                    vkb.setInputModeOrder(new String[]
                    {
                        VirtualKeyboard.NUMBERS_SYMBOLS_MODE
                    });
                }
                case 6://PhoneNumber
                {
                    tf.setInputModeOrder(new String[]
                    {
                        "123", "Abc"
                    });
                    tf.setConstraint(TextField.NUMERIC);
                    vkb.setInputModeOrder(new String[]
                    {
                        VirtualKeyboard.NUMBERS_MODE
                    });
                }
                break;
            }
            //tf.setConstraint(TextField.NUMERIC);
            //tf.setInputModeOrder(new String[]{"123","ABC","abc","Abc"});
            VirtualKeyboard.bindVirtualKeyboard(tf, vkb);
            tf.addDataChangeListener(new DataChangedListener()
            {
                public void dataChanged(int i, int i1)
                {
                    entryFieldChanged(i, tf);
                }
            });
            float[] mmv = act.getMinMaxValue();
            validateValue = ((mmv[0] != Float.NaN) || (mmv[1] != Float.NaN));
            //
            int[] mmc = act.getMinMaxChar();
            validateCharCnt = ((mmv[0] >= 0) || (mmv[1] >= 0));
            return tf;
        }
        return null;
    }

    private void entryFieldChanged(int type, TextField tf)
    {
        System.out.println("Entryfield Changed " + "type= " + type + " entered= " + tf.getText());
        currentEntry = tf.getText();
    }

    private void listModelChanged(int type, int idx, DefaultListModel lm)
    {
        System.out.println("List Data Changed " + "type= " + type + " idx= " + idx + " LstMdl= " + lm);
    }

    /**
     *
     * @param ae
     */
    public void actionPerformed(ActionEvent ae)
    {
        Component cmp = ae.getComponent();
        Command cmd = ae.getCommand();
        if (cmp == null)
        {
            System.out.println("cmd= " + cmd.getClass().toString());
        }
        else
        {
            System.out.println("cmp= " + cmp.getClass().toString());
        }
        if (cmp != null)
        {
            System.out.println(cmp.getName());
            if (cmp.getClass() == Button.class)
            {
                ObjectBuilderFactory.GetKernel().handleItemSelection(act.getBannerID(), "Banner");
            }
            else
            {
                DefaultListModel lm = null;
                if (cmp.getClass() == List.class)
                {
                    lm = (DefaultListModel) ((List) cmp).getModel();
                }
                else if (cmp.getClass() == ContainerList.class)
                {
                    lm = (DefaultListModel) ((ContainerList) cmp).getModel();
                }
                if (lm != null)
                {
                    Object[] item = (Object[]) lm.getItemAt(lm.getSelectedIndex());
                    ObjectBuilderFactory.GetKernel().handleItemSelection(((Integer) item[1]).intValue(), (String) item[0]);
                }
            }
        }
        else if (cmd != null)
        {
            if (cmd == getBackCommand())
            {
                if (currentItem != null)
                {
                    int crrntItm = (Integer) currentItem[1];
                    ObjectBuilderFactory.GetKernel().handleOptionSelection(crrntItm, (String) currentItem[0], (byte) cmd.getId());
                }
                else
                {
                    ObjectBuilderFactory.GetKernel().handleOptionSelection(-1, "", (byte) cmd.getId());
                }
            }
            else if ((options != null) && currentActiveOptions.equals(options))
            {
                if (currentItem != null)
                {
                    int crrntItm = (Integer) currentItem[1];
                    ObjectBuilderFactory.GetKernel().handleOptionSelection(crrntItm, (String) currentItem[0], (byte) cmd.getId());
                }
                else
                {
                    ObjectBuilderFactory.GetKernel().handleOptionSelection(-1, "", (byte) cmd.getId());
                }
            }
            else if ((escapeOptions != null) && this.currentActiveOptions.equals(escapeOptions))
            {
                if (currentEsc != null)
                {
                    ObjectBuilderFactory.GetKernel().handleOptionSelection(((Integer) currentEsc[1]), (String) currentEsc[0], (byte) cmd.getId());
                }
                else
                {
                    ObjectBuilderFactory.GetKernel().handleOptionSelection(-1, "", (byte) cmd.getId());
                }
            }
        }
    }

    /**
     *
     * @param arg0
     * @param arg1
     */
    public void selectionChanged(int arg0, int arg1)
    {
        System.out.println("Object class= " + currentFocus + " old= " + arg0 + " new= " + arg1);
        if (LWUtils.getInstance().isDialogShowing() == false)
        {
            if (currentFocus != null)
            {
                DefaultListModel model = null;
                String name = null;
                if (currentFocus.getClass() == List.class)
                {
                    model = (DefaultListModel) ((List) currentFocus).getModel();
                    name = ((List) currentFocus).getName();
                }
                else if (currentFocus.getClass() == ContainerList.class)
                {
                    model = (DefaultListModel) ((ContainerList) currentFocus).getModel();
                    name = ((ContainerList) currentFocus).getName();
                }
                if (model != null)
                {
                    //String name = getFocused().getName();
                    if (name.equals(LISTNAME_ITEMS))
                    {
                        currentItem = (Object[]) model.getItemAt(model.getSelectedIndex());
                    }
                    else if (name.equals(LISTNAME_ESCAPE))
                    {
                        currentEsc = (Object[]) model.getItemAt(model.getSelectedIndex());
                    }
                }
            }
        }
    }

    /**
     * Overridden to do nothing and remove a performance issue where renderer
     * changes perform needless repaint calls
     */
    public void repaint()
    {
    }

    public void focusGained(Component cmpnt)
    {
        System.out.println("focusGained: " + cmpnt);
        currentFocus = cmpnt;
        if (currentFocus != null)
        {
            DefaultListModel model = null;
            String name = null;
            if (currentFocus.getClass() == List.class)
            {
                model = (DefaultListModel) ((List) currentFocus).getModel();
                name = ((List) currentFocus).getName();
            }
            else if (currentFocus.getClass() == ContainerList.class)
            {
                model = (DefaultListModel) ((ContainerList) currentFocus).getModel();
                name = ((ContainerList) currentFocus).getName();
            }
            if (model != null)
            {
                if (name.equals(LISTNAME_ITEMS))
                {
                    setCurrentOptionList(options);
                    currentItem = (Object[]) model.getItemAt(model.getSelectedIndex());
                }
                else if (name.equals(LISTNAME_ESCAPE))
                {
                    setCurrentOptionList(escapeOptions);
                    currentEsc = (Object[]) model.getItemAt(model.getSelectedIndex());
                }
            }
        }
        else if (cmpnt.getClass() == TextField.class)
        {
            setCurrentOptionList(this.entryOptions);
            currentEntry = ((TextField) cmpnt).getText();
        }
        else
        {
            if (this.getCommandCount() > 0)
            {
                removeAllCommands();
            }
            addCommand(getBackCommand());
        }
    }

    public void focusLost(Component cmpnt)
    {
        //System.out.println("focusLost: " + cmpnt);
    }

    private static class PopupTimerTask extends TimerTask
    {

        public PopupTimerTask()
        {
            super();
        }

        public void run()
        {
            timer.cancel();
            ShowAPopup();
        }
    }

    static private void ShowAPopup()
    {
        LWUtils.getInstance().showPopup("Hello", _bannerText, "Suffer", "Take Arms");
    }

}
