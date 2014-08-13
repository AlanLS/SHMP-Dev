
import com.sun.lwuit.Command;
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.List;
import com.sun.lwuit.TextField;
import com.sun.lwuit.VirtualKeyboard;
import com.sun.lwuit.events.ActionEvent;
import com.sun.lwuit.events.ActionListener;
import com.sun.lwuit.events.DataChangedListener;
import com.sun.lwuit.events.FocusListener;
import com.sun.lwuit.events.SelectionListener;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.list.ListCellRenderer;
import java.io.IOException;
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
  
    public LWFormEAT(LWDTO _dto)
    {
        super(_dto);
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
                    act.getEscapeText()[i], (int) act.getEscapeIDs()[i]
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

    private Component getItemsList()
    {
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
                    hasFaces ? (boolean) (act.getListItemFaces()[i] == 1) : null,
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
            LWVirtualKB vkb = new LWVirtualKB();
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
        backReturnText = tf.getText();
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
                ObjectBuilderFactory.GetKernel().handleItemSelection(act.getBackID(), backReturnText);
            }
            else if ((options != null) && currentActiveOptions.equals(options))
            {
                if (currentItem != null)
                {
                    ObjectBuilderFactory.GetKernel().handleOptionSelection(((Integer) currentItem[1]), (String) currentItem[0], (byte) cmd.getId());
                }
            }
            else if ((escapeOptions != null) && this.currentActiveOptions.equals(escapeOptions))
            {
                if (currentEsc != null)
                {
                    ObjectBuilderFactory.GetKernel().handleOptionSelection(((Integer) currentEsc[1]), (String) currentEsc[0], (byte) cmd.getId());
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
                setCurrentOptionList(options);
                currentItem = (Object[]) ((LWListModel) ((List) cmpnt).getModel()).getSelectedItem();
            }
            if (((List) cmpnt).getName().equals(LISTNAME_ESCAPE))
            {
                setCurrentOptionList(escapeOptions);
                currentEsc = (Object[]) ((LWListModel) ((List) cmpnt).getModel()).getSelectedItem();
            }
        }
        else if (cmpnt.getClass() == TextField.class)
        {
            setCurrentOptionList(entryOptions);
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
        System.out.println("focusLost: " + cmpnt);
//throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
