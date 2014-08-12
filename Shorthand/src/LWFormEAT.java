
import com.sun.lwuit.Button;
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.Label;
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
import com.sun.lwuit.list.ListCellRenderer;
import com.sun.lwuit.plaf.Border;
import com.sun.lwuit.plaf.Style;
import java.util.Vector;

/**
 *
 * @author alan
 */
public class LWFormEAT extends LWForm implements ActionListener, SelectionListener, FocusListener
{

    static final String LISTNAME_ITEMS = "ITM";
    static final String LISTNAME_ESCAPE = "ESC";

    protected boolean listOnly = true;
    protected boolean gridView = false;
    private LWEATActionDTO act;
    private boolean validateValue = false;
    private boolean validateCharCnt = false;

    private Object[] currentItem = null;
    private Object[] currentEsc = null;

    public LWFormEAT()
    {
        super();
    }

    public void initialize(LWDTO _dto)
    {
        super.initialize(_dto);
        if (_dto.getClass() != LWEATActionDTO.class)
        {
            //throw new Exception();
        }
        act = (LWEATActionDTO) _dto;
        //responseDTO = new LWEATResponseDTO();
        //
        setListAndEscape();
        setEntry();
        //
        addCommandListener(this);
    }

    public void showCompleted()
    {
        super.onShowCompleted();
        //if (list != null)
        //{
        //    list.requestFocus();
        //    list.setSelectedIndex(0);
        //}
        revalidate();
    }

    private void setListAndEscape()
    {
        //Container c = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        Container c = new Container(new BorderLayout());
        Component cmpLI = getItemsList();
        if (cmpLI != null)
        {
            c.addComponent(BorderLayout.CENTER, cmpLI);
        }
        //
        Component cmpET = getEscapeList();//getEscapeTexts();
        if (cmpET != null)
        {
            c.addComponent(BorderLayout.SOUTH, cmpET);
        }
        addComponent(BorderLayout.CENTER, c);
    }

    private void setEntry()
    {
        Component cmpEB = getLineEntryBox();
        if (cmpEB != null)
        {
            addComponent(BorderLayout.SOUTH, cmpEB);
        }
    }
    /*
     private Component setEscTextLabel(String txt, String nm)
     {
     Label l = new Label();
     l.setText(txt);
     l.setName(nm);
     l.addFocusListener(this);
     l.setFocusable(true);
     Style s = new Style(l.getStyle());
     s.setMargin(0, 0, 0, 0);
     s.setFgColor(act.getEscTextColor());
     s.setBgTransparency(0);
     s.setBorder(null);
     //
     l.setUnselectedStyle(s);
     s = new Style(l.getStyle());
     s.setBorder(Border.createLineBorder(3, act.getHighlightColor()));
     l.setSelectedStyle(s);
     l.setPressedStyle(s);
     return l;
     }

     private Component setEscTextButton(String txt, String nm)
     {
     Button b = new Button();
     b.setUIID("Label");
     b.setText(txt);
     b.setName(nm);
     b.addFocusListener(this);
     b.setFocusable(true);
     b.addActionListener(this);
     Style s = new Style(b.getStyle());
     s.setMargin(0, 0, 0, 0);
     s.setFgColor(act.getEscTextColor());
     s.setBgTransparency(0);
     s.setBorder(null);
     //
     b.setUnselectedStyle(s);
     s = new Style(b.getStyle());
     s.setBorder(Border.createLineBorder(3, act.getHighlightColor()));
     b.setSelectedStyle(s);
     b.setPressedStyle(s);
     return b;
     }

     private Component getEscapeTexts()
     {
     String[] escText = act.getEscapeText();
     if (escText != null)
     {
     Container c = new Container(new BoxLayout(BoxLayout.Y_AXIS));
     int cnt = escText.length;
     if (cnt > 0)
     {
     String et0 = escText[0];
     if (et0 != null)
     {
     c.addComponent(setEscTextButton(et0, Integer.toString(act.getEscapeIDs()[0])));
     }
     }
     if (cnt > 1)
     {
     String et1 = escText[1];
     if (et1 != null)
     {
     c.addComponent(setEscTextButton(et1, Integer.toString(act.getEscapeIDs()[1])));
     }
     }
     return c;
     }
     return null;
     }
     */

    private Component getEscapeList()
    {
        if (act.getEscapeText() != null)
        {
            int lngth = act.getEscapeText().length;
            Vector vec = new Vector(lngth);
            for (int i = 0; i < lngth; ++i)
            {
                vec.addElement(new Object[]
                {
                    act.getEscapeText()[i], new Integer(act.getEscapeIDs()[i]), null
                });
            }
            final LWListModel listModel = new LWListModel(vec);
            listModel.addDataChangedListener(new DataChangedListener()
            {
                public void dataChanged(int i, int i1)
                {
                    listModelChanged(i, i1, listModel);
                }
            });
            List list = new List(listModel);
            list.setName("ESC");
            list.setRenderer((ListCellRenderer) new LWRendererEAT());
            ((LWRendererEAT) list.getRenderer()).setFocusColor(act.getHighlightColor());
            ((LWRendererEAT) list.getRenderer()).setTextColor(act.getListTextColor());
            list.addActionListener(this);
            list.addSelectionListener(this);
            list.addFocusListener(this);
            list.setSmoothScrolling(true);
            list.setPaintFocusBehindList(true);
            list.setFixedSelection(List.FIXED_NONE);
            list.setScrollVisible(true);
            list.setScrollToSelected(true);
            list.setItemGap(0);
            list.requestFocus();
            return list;
        }
        return null;
    }

    private Component getLineEntryBox()
    {
        if (act.isEntryBoxEnabled())
        {
            final TextField tf = new TextField();
            //tf.setSingleLineTextArea(true);
            tf.getStyle().setBgColor(0xffffff);
            tf.setHint(act.getEntryBoxHint());
            tf.addFocusListener(this);
            tf.addActionListener(this);
            //tf.setConstraint(TextField.NUMERIC);
            //tf.setInputModeOrder(new String[]{"123","ABC","abc","Abc"});
            LWVirtualKB vkb = new LWVirtualKB();
            vkb.setInputModeOrder(new String[]
            {
                VirtualKeyboard.QWERTY_MODE
            });
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
            int[] mmc = act.getMinMaxChar();
            validateCharCnt = ((mmv[0] >= 0) || (mmv[1] >= 0));
            return tf;
        }
        return null;
    }

    private Component getItemsList()
    {
        if (act.getListItems() != null)
        {
            int lngth = act.getListItems().length;
            Vector vec = new Vector(lngth);
            boolean hasImages = (act.getListImages() != null) && (act.getListImages().length == lngth);
            for (int i = 0; i < lngth; ++i)
            {
                vec.addElement(new Object[]
                {
                    act.getListItems()[i], new Integer(act.getListItemIds()[i]), hasImages ? act.getListImages()[i] : null
                });
            }
            final LWListModel listModel = new LWListModel(vec);
            listModel.addDataChangedListener(new DataChangedListener()
            {
                public void dataChanged(int i, int i1)
                {
                    listModelChanged(i, i1, listModel);
                }
            });
            List list = new List(listModel);
            list.setName("ITM");
            list.setRenderer((ListCellRenderer) new LWRendererEAT());
            ((LWRendererEAT) list.getRenderer()).setFocusColor(act.getHighlightColor());
            ((LWRendererEAT) list.getRenderer()).setTextColor(act.getEscTextColor());
            list.addActionListener(this);
            list.addSelectionListener(this);
            list.addFocusListener(this);
            list.setSmoothScrolling(true);
            list.setPaintFocusBehindList(true);
            list.setFixedSelection(List.FIXED_NONE);
            list.setScrollVisible(true);
            list.setScrollToSelected(true);
            list.setItemGap(0);
            list.requestFocus();
            return list;
        }
        return null;
    }

    private void entryFieldChanged(int type, TextField tf)
    {
        System.out.println("Entryfield Changed " + "type= " + type + " entered= " + tf.getText());
    }

    private void listModelChanged(int type, int idx, LWListModel lm)
    {
        System.out.println("List Data Changed " + "type= " + type + " idx= " + idx + " LstMdl= " + lm);
    }

    public void actionPerformed(ActionEvent ae)
    {
        Component cmp = ae.getComponent();
        Command cmd = ae.getCommand();
        System.out.println("cmp= " + cmp + " cmd= " + cmd);
        if (cmp != null)
        {
            if (cmp.getClass() == List.class)
            {
                LWListModel lm = (LWListModel) ((List) cmp).getModel();
                Object[] item = (Object[]) lm.getSelectedItem();
                ObjectBuilderFactory.GetKernel().handleItemSelection(((Integer) item[1]).intValue(), (String) item[0]);
            }
        }
        else if (cmd != null)
        {
            if (cmd == getBackCommand())
            {
                ObjectBuilderFactory.GetKernel().handleItemSelection(act.getBackID(), act.getBackText());
            }
            else if ((options != null) && this.currentActiveOptions.equals(options))
            {
                if (currentItem != null)
                {
                    ObjectBuilderFactory.GetKernel().handleOptionSelection(((Integer) currentItem[1]).intValue(), (String) currentItem[0], (byte) cmd.getId());
                }
            }
            else if ((escapeOptions != null) && this.currentActiveOptions.equals(escapeOptions))
            {
                if (currentEsc != null)
                {
                    ObjectBuilderFactory.GetKernel().handleOptionSelection(((Integer) currentEsc[1]).intValue(), (String) currentEsc[0], (byte) cmd.getId());
                }
            }
        }
    }

    public void selectionChanged(int arg0, int arg1)
    {
        System.out.println("old= " + arg0 + " new= " + arg1);
        if (getFocused().getClass() == List.class)
        {
            List l = ((List) getFocused());
            if (l.getName().equals(LISTNAME_ITEMS))
            {
                currentItem = (Object[]) ((LWListModel) l.getModel()).getSelectedItem();
            }
            else if (l.getName().equals(LISTNAME_ESCAPE))
            {
                currentEsc = (Object[]) ((LWListModel) l.getModel()).getSelectedItem();
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
        if (cmpnt.getClass() == List.class)
        {
            if (((List) cmpnt).getName().equals(LISTNAME_ITEMS))
            {
                setItemOptions();
                currentItem = (Object[]) ((LWListModel) ((List) cmpnt).getModel()).getSelectedItem();
            }
            if (((List) cmpnt).getName().equals(LISTNAME_ESCAPE))
            {
                setEscapeOptions();
                currentEsc = (Object[]) ((LWListModel) ((List) cmpnt).getModel()).getSelectedItem();
            }
        }
    }

    public void focusLost(Component cmpnt)
    {
        System.out.println("focusLost: " + cmpnt);
//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
