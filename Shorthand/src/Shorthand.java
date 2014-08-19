
import com.sun.lwuit.Display;
import com.sun.lwuit.Form;
import com.sun.lwuit.List;
import com.sun.lwuit.TextField;
import com.sun.lwuit.plaf.UIManager;
import java.io.IOException;
import javax.microedition.midlet.MIDlet;
import jg.JgCanvas;

/**
 * DO NOT HAND MODIFY THIS CLASS, IT WILL BE OVERWRITTEN FOR EACH BUILD.
 */
public class Shorthand extends MIDlet
{

    private JgCanvas canvas;
    boolean isLWUIT = true;

    public void startApp()
    {
        if (isLWUIT)
        {
            if (Display.isInitialized() == false)
            {
                Display.init(this);
                com.sun.lwuit.util.Resources res = null;
                try
                {
                    res = com.sun.lwuit.util.Resources.open("/Style.res");
                    UIManager.getInstance().setThemeProps(res.getTheme(res.getThemeResourceNames()[0]));
                    UIManager.getInstance().setResourceBundle(res.getL10N("Lang", "en"));  
                }
                catch (final IOException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    res = null;
                    UIManager.getInstance().getLookAndFeel().setReverseSoftButtons(true);
                    Display.getInstance().setCommandBehavior(Display.COMMAND_BEHAVIOR_SOFTKEY);
                    Display.getInstance().setThirdSoftButton(false);
                    Display.getInstance().setPureTouch(false);
                    Display.getInstance().setTouchScreenDevice(false);
                    
                // KNetworkManager.getInstance().start();
                    // various Object static settings
                    //
                   // TextField.setReplaceMenuDefault(false);
                   // TextField.setUseNativeTextInput(false);
                   // TextField.setQwertyAutoDetect(true);
                    //
                    List.setDefaultIgnoreFocusComponentWhenUnfocused(true);
                }
                //
            }
        }
        if (canvas == null)
        {
            canvas = new ShortHandCanvas(this);
            javax.microedition.lcdui.Display.getDisplay(this).setCurrent(canvas);
        }
        else
        {
            canvas.showNotify();
        }
    }

    public void destroyApp(boolean unconditional)
    {
        //#if VERBOSELOGGING
        //|JG|Logger.loggerError("Shorthand->destroyApp");
        //#endif
        //CR 13278
//       byte exitPressed = ObjectBuilderFactory.GetKernel().exitShorthand();
//       if(exitPressed == 2){
        Logger.loggerError("Shorthand->destroyApp " + unconditional);
//        ShortHandCanvas.currentDate = Long.parseLong(Utilities.getCurrentDateHHMMDDYYFormat());
        canvas.postSystemEvent(JgCanvas.SYSTEM_EVENT_EXIT);
//       } else canvas.showNotify();
    }

    public void pauseApp()
    {
//        canvas.postSystemEvent(JgCanvas.SYSTEM_EVENT_INTERRUPT);
        canvas.hideNotify();
    }

}
