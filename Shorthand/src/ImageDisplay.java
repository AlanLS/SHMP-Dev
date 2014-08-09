
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VideoControl;
import javax.microedition.media.control.VolumeControl;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sasi
 */
public class ImageDisplay implements PlayerListener{

    private Timer rotateImageTimer = null;

    private int imgrotType = 0;

    private byte rStart = 0;
    
    private String sHeader = null;

    private String imageId = null;

    private String fileLocation = null;

    private boolean isImageSend =  false;

    private byte fileformat = -1;

    private Player player = null;

    private boolean playAudio = false;

    private VideoControl videoControl = null;

    private boolean isNotExit = true;

    public boolean setHeadetText(String hText,
            String imageId, String fileLocation, byte screenType){
        boolean isShow = false;
        fileformat = -1;
        playAudio = false;
        byte downloadStart = 0;
        isNotExit = true;
        if(screenType == 0 || screenType == 2 || 
                screenType == 3 || screenType == 5){ //Download Image/Audio From imageId and show
            if(screenType == 0){
                if(screenType>0){ //CR 14727
                    fileformat = (byte)(screenType-2);
                }
                downloadStart = setImage(fileLocation,false, screenType);
            } else {
                fileformat = (byte)(screenType-2);
                downloadStart = setAudio(fileLocation);
            }
            if(downloadStart == 0){
                if(screenType == 5){
                    DownloadHandler.getInstance().downloadImage(fileLocation,imageId,fileformat);
                    fileLocation = null;
                } else {
                    DownloadHandler.getInstance().downloadImage(null,imageId,fileformat);
                }
            }
        } else if(screenType == 1 || screenType == 4){ //Show image from fileLocation
            //Show image and upload image from fileLocation
            //CR 14694
            fileformat = (byte)(screenType-2);
            downloadStart = setImage(fileLocation, (screenType == 4)?true:false, screenType);
        }  
        if(downloadStart  > -1){
            isShow = true;
            sHeader = hText;
            deInitialize();
            if(downloadStart == 0){
                startRotateImateTimer();
            }
            this.imageId = imageId;
            this.fileLocation = fileLocation;
            CustomCanvas.sHeader = CustomCanvas.getSecondaryHeader(sHeader, "",0);
            ShortHandCanvas.IsNeedPaint();
        }
        return isShow;
    }

    //CR 14492
    private byte setAudio(String fileLocation){
        byte downloadStart = -1;
        if(null != fileLocation){
            boolean isNotSecurity = CustomCanvas.setAudioFile(fileLocation);
            if(isNotSecurity){
                if(null != UiGlobalVariables.byteArrayInputStream){
                    downloadStart = 1;
                }  else {
                    downloadStart = 0;
                }
            }
        } else {
            UiGlobalVariables.imagefile = null;
            downloadStart = 0;
        }
        return downloadStart;
    }

    //Cr 14423
    private byte setImage(String fileLocation, boolean isSquare, byte screenType){
        byte downloadStart = -1;
        if(null != fileLocation){
            boolean isNotSecurity = CustomCanvas.getFileImage(fileLocation, isSquare);
            if(isNotSecurity){
                if(null != UiGlobalVariables.imagefile){
                    downloadStart = 1;
                }  else {
                    //bug 14771
                    if(screenType != 1){
                        downloadStart = 0;
                    }
                }
            } 
        } else {
            UiGlobalVariables.imagefile = null;
            downloadStart = 0;
        }
        return downloadStart;
    }

    public boolean isWait(){
        return isImageSend;
    }

    private void rotateImage(){
        rStart++;
        if(rStart >= 5){
            imgrotType++;
            if (imgrotType > 3) {
                imgrotType = 0;
            }
            rStart = 0;
        }
    }

    public byte isBack(int keyCode){
        byte back = 0;
        if(UISettings.RIGHTOPTION == keyCode ){
            if(UISettings.rOByte>-1){
                //bug 14640
                stopPlayer();
                ShortHandCanvas.IsNeedPaint();
                back = 1;
            }
        } else if(UISettings.LEFTOPTION == keyCode){
            if(UISettings.lOByte == 54 || UISettings.lOByte == 61){
                back = 2;
                isImageSend = true;
                startRotateImateTimer();
                DownloadHandler.getInstance().downloadImage(fileLocation,imageId , fileformat);
            }
        }
        return back;
    }

    public String getFileLocation(){
        return fileLocation;
    }

    /**
     * Method to unload the canvas
     */
    public void unLoad() {
        deInitialize();
    }

    //CR 13900
    //CR 13981
    public boolean drawDisplayImage(Graphics graphics) {
        if(fileformat>-1 && fileformat <2){
            if(!playAudio){
                if(null == UiGlobalVariables.byteArrayInputStream){
                    CustomCanvas.drawProcessImage(graphics,imgrotType);
                } else {
                    Logger.debugOnError("Player initialize");
                    playAudio = true;
                    if(null != UiGlobalVariables.extension){
                        playFile();
                    } else Logger.debugOnError("Extension null");
                }
            } 
        } else {
            if(null == UiGlobalVariables.imagefile || isImageSend){
                CustomCanvas.drawProcessImage(graphics,imgrotType);
            } else {
                int yPosition = (UISettings.headerHeight+UISettings.secondaryHeaderHeight);
                int height = UISettings.formHeight - (yPosition+UISettings.footerHeight+graphics.getFont().getHeight()+4);
                if(null != UiGlobalVariables.imagefile){
                    if(UiGlobalVariables.imagefile.getHeight()<height)
                        yPosition += (height-UiGlobalVariables.imagefile.getHeight())/2;
                    graphics.drawImage(UiGlobalVariables.imagefile, (UISettings.formWidth-
                            UiGlobalVariables.imagefile.getWidth())/2, yPosition, Graphics.TOP|Graphics.LEFT);
                } else{
                    graphics.setColor(0xd3d3d3);
                    graphics.fillRect(10, yPosition, UISettings.formWidth-20,height);
                }
            }
        }
        return isNotExit;
    }

    private void playFile(){
        Logger.debugOnError("PlayFile Format "+UiGlobalVariables.extension);
        SoundManager.getInstance().deinitialize();
        UiGlobalVariables.byteArrayInputStream.reset();
        if(fileformat == 1){
            if(!playVideo3gp()){
                playVideoMp4();
            }
        } else {
            if(!playAudioAmr()){
               playAudioWav();
           }
        }
        try {
            UiGlobalVariables.byteArrayInputStream.close();
             UiGlobalVariables.byteArrayInputStream = null;
        } catch (IOException ex) { }
    }

    private boolean playVideoMp4(){
        try {
            player= Manager.createPlayer(UiGlobalVariables.byteArrayInputStream, "video/mp4");
            Logger.debugOnError("playing from stream: mp4");
            player.addPlayerListener(this);//maliHEMA
            player.realize();

            videoControl = (VideoControl)player.getControl("VideoControl");
            videoControl.initDisplayMode(VideoControl.USE_DIRECT_VIDEO, ObjectBuilderFactory.getPCanvas());
            try {
                videoControl.setDisplaySize(UISettings.formWidth-4, UISettings.formHeight -
                          (UISettings.headerHeight+UISettings.footerHeight+UISettings.secondaryHeaderHeight));
                videoControl.setDisplayLocation(2, UISettings.headerHeight+UISettings.secondaryHeaderHeight);
            } catch (MediaException mediaException) {
                Logger.loggerError("ImageDisplay->Video->>mediaException->"+mediaException.toString());
            }
            Logger.debugOnError("ImageDisplay->VideoSize-> "+videoControl.getDisplayWidth()
                    +"X"+videoControl.getDisplayHeight()+ " "+videoControl.getSourceWidth()+"X"+videoControl.getSourceHeight());
            VolumeControl volumeControl = (VolumeControl) player.getControl("VolumeControl");
            if(null != volumeControl){
                volumeControl.setLevel(100);
            }
            player.start();
            videoControl.setVisible(true);
            return true;
        }
        catch(Exception exc){
            Logger.loggerError("playing input stram fail and trying direct server link "+exc.toString());
        }
        return false;
    }

    private boolean playVideo3gp(){
        try {
           
            player = Manager.createPlayer(UiGlobalVariables.byteArrayInputStream, "video/3gpp");
            Logger.debugOnError("playing from stream: 3gpp");
            player.addPlayerListener(this);//maliHEMA
            player.realize();

            videoControl = (VideoControl)player.getControl("VideoControl");
            videoControl.initDisplayMode(VideoControl.USE_DIRECT_VIDEO, ObjectBuilderFactory.getPCanvas());
            try {
                videoControl.setDisplaySize(UISettings.formWidth-4, UISettings.formHeight -
                          (UISettings.headerHeight+UISettings.footerHeight+UISettings.secondaryHeaderHeight));
                videoControl.setDisplayLocation(2, UISettings.headerHeight+UISettings.secondaryHeaderHeight);
            } catch (MediaException mediaException) {
                Logger.loggerError("ImageDisplay->Video->>mediaException->"+mediaException.toString());
            }
            Logger.debugOnError("ImageDisplay->VideoSize-> "+videoControl.getDisplayWidth()
                    +"X"+videoControl.getDisplayHeight()+ " "+videoControl.getSourceWidth()+"X"+videoControl.getSourceHeight());
            VolumeControl volumeControl = (VolumeControl) player.getControl("VolumeControl");
            if(null != volumeControl){
                volumeControl.setLevel(100);
            }
            player.start();
            videoControl.setVisible(true);
            return true;
        }
        catch(Exception exc){
            Logger.loggerError("playing input stram fail and trying direct server link "+exc.toString());
        }
        return false;
    }

    private boolean playAudioWav(){
        try {
            player= Manager.createPlayer(UiGlobalVariables.byteArrayInputStream, "audio/wav");
            Logger.debugOnError("playing from stream: wav");
            player.addPlayerListener(this);//maliHEMA
            player.realize();
            VolumeControl volumeControl = (VolumeControl) player.getControl("VolumeControl");
            if(null != volumeControl){
                volumeControl.setLevel(100);
            }
            player.start();
            return true;
        }
        catch(Exception exc){
            Logger.loggerError("playing input stram fail and trying direct server link "+exc.toString());
        }
        return false;
    }

    private boolean  playAudioAmr(){
        try{
            player = Manager.createPlayer(UiGlobalVariables.byteArrayInputStream, "audio/amr");
            Logger.debugOnError("playing from stream: amr");
            player.addPlayerListener(this);//maliHEMA
            player.realize();
            VolumeControl volumeControl = (VolumeControl) player.getControl("VolumeControl");
            if(null != volumeControl){
                volumeControl.setLevel(100);
            }
            player.start();
            return true;
        }
        catch(Exception exc){
            Logger.loggerError("playing input stram fail and trying direct server link "+exc.toString());
        }
        return false;
    }

    private void stopRotateImageTimer(){
        if(null != rotateImageTimer){
            rotateImageTimer.cancel();
            rotateImageTimer = null;
        }
    }

    private void startRotateImateTimer(){
        rotateImageTimer = new Timer();
        rotateImageTimer.schedule(new DisplayImageTimer(), 0, 100);
    }

    public void rotateScreen(boolean isLandScape) {
        CustomCanvas.sHeader = CustomCanvas.getSecondaryHeader(sHeader, "",0);
    }


    /**
     * De Initialize method
     */
    public void deInitialize() {

        stopRotateImageTimer();

        rStart = 0;

        //Int
        imgrotType = 0;
        isImageSend = false;

    }


    //CR 14423
    public String  setImage(ByteArrayOutputStream byteArrayOutputStream, boolean isStore){
        String filLocation = null;
        if(fileformat>-1 && fileformat<2){
            if(isStore){
                deInitialize();                
                filLocation = Utilities.saveImage_Audio(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()),
                            imageId, fileformat, UiGlobalVariables.extension);
                UiGlobalVariables.byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            }
        } else {
            try {
                //CR 13981
                UiGlobalVariables.imagefile = Image.createImage(byteArrayOutputStream.toByteArray(), 0, byteArrayOutputStream.size());
                if(isStore){
                    filLocation = "";
                }
            } catch (Exception e) {
                Logger.debugOnError("Caption Image Create Error " + e.toString());
                try {
                    if(isStore){
                        filLocation = "";
                    }
                    UiGlobalVariables.imagefile = Image.createImage(byteArrayOutputStream.toByteArray(), 1, byteArrayOutputStream.size());
                } catch (Exception ex) {
                    Logger.debugOnError("Caption Image Create Error " + ex.toString());
                }
            }
            if(null != filLocation){
                deInitialize();
                if(isStore){
                    String image = Utilities.saveImage_Audio(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()),
                            imageId, -1, "jpeg");
                    if(null != image){
                        filLocation = image;
                    }
                }
            }
        }
        return filLocation;
    }

    private void stopPlayer(){
            try{
            if(null != player){
                if(player.getState() == Player.STARTED){
                    player.stop();
                }
//                if(player.getState() == Player.PREFETCHED){
                    player.deallocate();
//                }
                if(player.getState() == Player.REALIZED  || player.getState() == Player.UNREALIZED){
                    player.close();
                }
                player = null;
            }
        }catch(Exception exception){
            Logger.loggerError("CaptureImage->stopPlayer->Player"+exception.toString());
        }
        playAudio = false;
    }

    public void playerUpdate(Player player, String event, Object o) {
        if (event.compareTo(PlayerListener.END_OF_MEDIA) == 0) {
            stopPlayer();
            deInitialize();
            isNotExit = false;
            ShortHandCanvas.IsNeedPaint();

        }
    }
   

     class DisplayImageTimer extends TimerTask{
        public void run(){
            rotateImage();
            ShortHandCanvas.IsNeedPaint();
        }
    }
}
