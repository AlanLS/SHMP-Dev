
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
    protected LWListModel listModel = null;
    protected boolean listOnly = true;
    protected boolean gridView = false;
    private LWEATActionDTO act;
    private boolean validateValue = false;
    private boolean validateCharCnt = false;

    private Component lastFocusedComponent = null;

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
        Component cmpLI = getListItems();
        if (cmpLI != null)
        {
            c.addComponent(BorderLayout.CENTER, cmpLI);
        }
        //
        Component cmpET = getEscapeTexts();
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
                    c.addComponent(setEscTextLabel(et0, Integer.toString(act.getEscapeIDs()[0])));
                }
            }
            if (cnt > 1)
            {
                String et1 = escText[1];
                if (et1 != null)
                {
                    c.addComponent(setEscTextLabel(et1, Integer.toString(act.getEscapeIDs()[1])));
                }
            }
            return c;
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

    private Component getListItems()
    {
        if (act.getListItems() != null)
        {
            int lngth = act.getListItems().length;
            Vector vec = new Vector(lngth);
            if ((act.getListImages() != null) && (act.getListImages().length == lngth))
            {
                for (int i = 0; i < lngth; ++i)
                {
                    vec.addElement(new Object[]
                    {
                        act.getListItems()[i], act.getListImages()[i]
                    });
                }
            }
            else
            {
                for (int i = 0; i < lngth; ++i)
                {
                    vec.addElement(new Object[]
                    {
                        act.getListItems()[i], null
                    });
                }
            }
            listModel = new LWListModel(vec);
            listModel.addDataChangedListener(new DataChangedListener()
            {
                public void dataChanged(int i, int i1)
                {
                    listModelChanged(i, i1, listModel);
                }
            });
            List list = new List(listModel);
            list.setRenderer((ListCellRenderer) new LWRendererEAT());
            ((LWRendererEAT) list.getRenderer()).setFocusColor(act.getHighlightColor());
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
                int idx = listModel.getSelectedIndex();
                ObjectBuilderFactory.GetKernel().handleItemSelection(act.getListItemIds()[idx], act.getListItems()[idx]);
             }
        }
        else if (cmd != null)
        {
            if ((options != null) && this.currentActiveOptions.equals(options))
            {
                if (getListItems() != null)
                {
                    int idx = listModel.getSelectedIndex();
                    if (idx >= 0)
                    {
                        ObjectBuilderFactory.GetKernel().handleOptionSelection(act.getListItemIds()[idx], act.getListItems()[idx], (byte) cmd.getId());
                    }
                }
            }
            else if ((escapeOptions != null) && this.currentActiveOptions.equals(escapeOptions))
            {
                ObjectBuilderFactory.GetKernel().handleOptionSelection(Integer.getInteger(lastFocusedComponent.getName()), lastFocusedComponent.getName(), (byte) cmd.getId());
            }
        }
    }

    public void selectionChanged(int arg0, int arg1)
    {
        System.out.println("old= " + arg0 + " new= " + arg1);
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
            setItemOptions();
        }
        else
        {
            // its escape
            setEscapeOptions();
        }
    }

    public void focusLost(Component cmpnt)
    {
        System.out.println("focusLost: " + cmpnt);
//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
