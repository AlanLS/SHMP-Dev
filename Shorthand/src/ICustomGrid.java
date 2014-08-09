
import javax.microedition.lcdui.Graphics;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sasi
 */
public interface ICustomGrid {

    void setGridValues(String[] name, String[] displayName);

    void drawScreen(Graphics g, byte itemFocused, byte lastItemfocused,
            boolean isMessagebox, String letterCount);

    boolean pointerPressed(int xPosition, int yPosition,
        boolean isNotDrag, boolean isDragEnd, boolean isPressed);

    String getSelectedGridValue();

    String getSelectedDisplayGridValue();

    void handleGrid(int keyCode);

    void rotateScreen(boolean isLandScape);

    int getGridPosition();

    boolean isGridPresent();

    void deInitialize();

    void stratImageDisplay(boolean isImageCreateNotInterept);

}
