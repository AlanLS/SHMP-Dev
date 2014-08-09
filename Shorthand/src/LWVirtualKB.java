
import com.sun.lwuit.Component;
import com.sun.lwuit.Container;
import com.sun.lwuit.VirtualKeyboard;
import com.sun.lwuit.layouts.BorderLayout;
import com.sun.lwuit.layouts.BoxLayout;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author alan
 */
public class LWVirtualKB extends VirtualKeyboard
{
    public void show()
    {
        this.setPreferredH(640);
        this.setHeight(640);
        revalidate();
        repaint();
        
        
         super.showPacked(BorderLayout.CENTER, true);
       
    }
    
    public void onShow()
    {
       
    }

   

}
