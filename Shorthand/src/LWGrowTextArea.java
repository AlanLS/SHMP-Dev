
import com.sun.lwuit.TextArea;

public class LWGrowTextArea extends TextArea
{

    public LWGrowTextArea()
    {
        super();
        makeMeGrowable();
    }

    public LWGrowTextArea(String text)
    {
        super(text);
        makeMeGrowable();
    }

    public LWGrowTextArea(int rows, int columns)
    {
        super(rows, columns);
        makeMeGrowable();
    }

    public LWGrowTextArea(String text, int maxSize)
    {
        super(text, maxSize);
        makeMeGrowable();
    }

    public LWGrowTextArea(int rows, int columns, int constraint)
    {
        super(rows, columns, constraint);
        makeMeGrowable();
    }

    public LWGrowTextArea(String text, int rows, int columns)
    {
        super(text, rows, columns);
        makeMeGrowable();
    }

    public LWGrowTextArea(String text, int rows, int columns, int constraint)
    {
        super(text, rows, columns, constraint);
        makeMeGrowable();
    }

    private void makeMeGrowable()
    {
        setGrowByContent(true);
        setEditable(false);
        setRows(2);
    }

    
    protected boolean isSelectableInteraction()
    {
        return false;
    }
}
