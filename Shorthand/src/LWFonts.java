
import com.sun.lwuit.Font;

/**
 *
 * @author alan
 */
final public class LWFonts
{

    static final public int StandardFontID = 1;
    static final public int StandardFontBoldID = LWFonts.StandardFontID + 1;
    static final public int SmallFontID = LWFonts.StandardFontID + 2;
    static final public int SmallFontBoldID = LWFonts.StandardFontID + 3;
    final public static Font SmallFont = Font.createSystemFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);
    final public static Font SmallBoldFont = Font.createSystemFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL);
    final public static Font StandardFont = Font.createSystemFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
    final public static Font StandardBoldFont = Font.createSystemFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_MEDIUM);

    private LWFonts()
    {
    }

    public static Font getFont(int fntID)
    {
        if (fntID == LWFonts.StandardFontBoldID)
        {
            return LWFonts.StandardBoldFont;
        }
        else if (fntID == LWFonts.SmallFontID)
        {
            return LWFonts.SmallFont;
        }
        else if (fntID == LWFonts.SmallFontBoldID)
        {
            return LWFonts.SmallBoldFont;
        }
        return LWFonts.StandardFont;
    }
}
