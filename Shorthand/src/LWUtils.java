
import com.sun.lwuit.Dialog;
import com.sun.lwuit.Display;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author alan
 */
public class LWUtils
{

    static private LWUtils theInstance = null;
    private boolean dialogShowing = false;

    

    private LWUtils()
    {
    }

    static public LWUtils getInstance()
    {
        if (theInstance == null)
        {
            theInstance = new LWUtils();
        }
        return theInstance;
    }
    
    private LWForm getCurrentForm()
    {
        return (LWForm)Display.getInstance().getCurrent(); 
    }
    
    
    public void showPopup(String title, String body, String btnTextL, String btnTextR) 
    {
       Dialog.setCommandsAsButtons(true);
       dialogShowing = true;
       boolean result = Dialog.show(title, body, btnTextL, btnTextR);
       if (result)
       {
           System.out.println("btnTextL Selected");
       }
       else
       {
            System.out.println("btnTextR Selected");
       } 
       dialogShowing = false;
    } 
    
    public boolean isDialogShowing()
    {
        return dialogShowing;
    }
      
        
     
    
    
    
    
    
}

