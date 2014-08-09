
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.lcdui.Graphics;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.RecordControl;
import javax.microedition.media.control.VideoControl;

/**
 *
 * @author sasi
 */
public class CaptureImageAudio implements ICaptureImage {

    private Player player = null;

    private VideoControl videoControl = null;
    
    private boolean isCurrentScreen = false;

    private Timer rotateImageTimer = null;

    private int imgrotType = 0;

    private byte rStart = 0;

    private boolean isUpload = false;

    private String chatId = null;

    private boolean isCamera = false;

    private IMenuHandler iMenuHandler = null;

    private RecordControl recordControl = null;

    private byte fileFormat = -1;

    private ByteArrayOutputStream byteArrayOutputStream = null;

    private boolean isAudio = false;

    private long totoalTime = 0;

    public CaptureImageAudio(IMenuHandler iMenuHandler) {
        this.iMenuHandler = iMenuHandler;
    }

    public void setChatId(String chatId){
        this.chatId = chatId;
    }

    public boolean isCurrentScreen(){
        return isCurrentScreen;
    }

    public boolean isCameraScreen(){
        return isCamera || (isAudio && (fileFormat == 1))?true:false;
    }

    private void getSupportContentType(){
        String[] value = Manager.getSupportedContentTypes("capture");
        if(null != value){
            for(int i=1;i<value.length;i++){
                value[0] += " "+value[i];
            }
            Logger.loggerError("Video Supported Format-> "+value[0]);
        } else Logger.loggerError("No Video Supported Format");

        value = Manager.getSupportedProtocols(null);
        if(null != value){
            for(int i=1;i<value.length;i++){
                value[0] += " "+value[i];
            }
            Logger.loggerError("SupportedProtocols-> "+value[0]);
        } else Logger.loggerError("No Protocols Supported ");

    }


    private boolean isCaptureImage(){
        boolean isVideo = true;
        try {
            String model = Utilities.getManufacture();
            if((null != model && model.toLowerCase().indexOf("samsung")>-1)
                    || Utilities.isCameraVideoSupport()){
                isVideo = false;
                player = Manager.createPlayer("capture://video");
            } else {
                player = Manager.createPlayer("capture://image");
            }
	} catch (MediaException mediaException) {
            Logger.loggerError("CaptureImage->isCapture->capture://video"+mediaException.toString());
	    try {
                if(isVideo){
                    player = Manager.createPlayer("capture://video");
                } else {
                    player = Manager.createPlayer("capture://image");
                }
	    } catch (Exception exception) {
                Logger.loggerError("CaptureImage->isCapture->capture://image"+exception.toString());
	    }
	} catch (Exception exception) {
            Logger.loggerError("CaptureImage->isCapture->"+exception.toString());
	}

        if(null != player){
            try {
                player.realize();
                videoControl = (VideoControl)player.getControl("VideoControl");
                
                reLoadFooterMenu();
                ShortHandCanvas.IsNeedPaint();
                return true;
            } catch(Exception exception){
                Logger.loggerError("CaptureImage->isCapture->Player Not Realize"+exception.toString());
            }
        }
        return false;
    }

    public boolean isCapture(int fileFormat){
        SoundManager.getInstance().deinitialize();
        deInitialize(false);
        this.fileFormat = (byte)fileFormat;
        boolean isStart = false;
        getSupportContentType();
        if(fileFormat == -1 || fileFormat == 2){ //Image CR 14694
            isStart =  isCaptureImage();
        } else if(fileFormat == 0){ //Audio
            isStart = isCaptureAudio();
        } else isStart = isCaptureVideo(); //Video
        
        return isStart;
    }

     private boolean  createPlayer(String value){
        try{
            player = Manager.createPlayer(value);
        }catch(MediaException mediaException){
            player=null;
          Logger.loggerError("CaptureImage->CreatePlayer media ex->"+mediaException.toString());
            return false;
        } catch(Exception exception){
            Logger.loggerError("CaptureImage->CreatePlayer->"+exception.toString());
            return false;
        }
        Logger.loggerError("CaptureImage->CreatePlayer-player format>"+value);
        return true;
    }

     private boolean isCaptureVideo(){
         try {
                player = Manager.createPlayer("capture://video");
		player.realize();
		// Grab the video control and set it to the current display.
                videoControl = (VideoControl)player.getControl("VideoControl");
                //setup recording
		recordControl = (RecordControl)player.getControl("RecordControl");
                byteArrayOutputStream = new ByteArrayOutputStream();
                recordControl.setRecordStream(byteArrayOutputStream);
                reLoadFooterMenu();
                ShortHandCanvas.IsNeedPaint();
                return true;
         } catch(Exception exception){
             Logger.loggerError("Video Not Working"+exception.toString());
         }
         return false;
     }

     //CR 14465
    private boolean isCaptureAudio(){
        
        boolean isStarted = false;
         String[] value = new String[]{               
               
                "capture://audio?encoding=audio/amr&rate=8000",//501,311
                "capture://audio?encoding=audio/amr",//210
                "capture://audio?encoding=pcm&rate=8000",//monte,star
                "capture://audio?encoding=audio/wav&rate=8000",
                "capture://audio?encoding=audio/x-wav&rate=8000",                
                "capture://audio?encoding=audio/x-amr&rate=8000",
                "capture://audio?encoding=audio/au&rate=8000",                
                "capture://audio?encoding=audio/basic&rate=8000",
                "capture://audio?encoding=pcm",
                "capture://audio?encoding=audio/wav",
                "capture://audio?encoding=audio/x-wav",                
                "capture://audio?encoding=audio/x-amr",
                "capture://audio?encoding=audio/au",                
                "capture://audio?encoding=audio/basic",
                "capture://audio"
            };
        for(int i=0;i<value.length;i++){    
            if(createPlayer(value[i])){
                  if(value[i].indexOf("amr") == -1)
                    UiGlobalVariables.extension  = "wav";
                  else
                    UiGlobalVariables.extension  = "amr";
                break;
            }
        }
         try{
            if(null != player){
                player.realize();
                recordControl = (RecordControl)player.getControl("RecordControl");
                Logger.debugOnError("recording type="+ recordControl.getContentType());

                byteArrayOutputStream = new ByteArrayOutputStream();
                recordControl.setRecordStream(byteArrayOutputStream);
                //Bug 14663
                reLoadFooterMenu();
                ShortHandCanvas.IsNeedPaint();
                isStarted = true;
            } else {
                Logger.loggerError("Player Not supported ");
            }
        }catch(Exception exception){
            Logger.loggerError("CaptureImage->isCaptureAudio->"+exception.toString());
        }
         return isStarted;
    }

    private boolean camera(){
        try{
            if(null != videoControl){
                CustomCanvas.sHeader = CustomCanvas.getSecondaryHeader(Constants.options[55], "",0);
                videoControl.initDisplayMode(VideoControl.USE_DIRECT_VIDEO, ObjectBuilderFactory.getPCanvas());
                try {
                    videoControl.setDisplaySize(UISettings.formWidth-2, UISettings.formHeight -
                              (UISettings.headerHeight+UISettings.footerHeight));
                    videoControl.setDisplayLocation(0, UISettings.headerHeight);
                } catch (MediaException mediaException) {
                    Logger.loggerError("CaptureImage->camera->mediaException->"+mediaException.toString());
                }
                player.start();
                videoControl.setVisible(true);
                //bug 14460
                isCurrentScreen = true;
                isCamera = true;
                //bug 14542
                reLoadFooterMenu();
                ShortHandCanvas.IsNeedPaint();
            }
        }catch(Exception exception){
            deInitialize(false);
            Logger.loggerError("CaptureImage->camera->"+exception.toString());
        }
        return isCamera;
    }

    //Cr 14465
    private boolean StartAudioOrVideo(){
        try{
              if(fileFormat == 1 && null != videoControl){
                    videoControl.initDisplayMode(VideoControl.USE_DIRECT_VIDEO, ObjectBuilderFactory.getPCanvas());
                    try {
                        videoControl.setDisplaySize(UISettings.formWidth-2, UISettings.formHeight -
                                  (UISettings.headerHeight+UISettings.footerHeight));
                        videoControl.setDisplayLocation(0, UISettings.headerHeight);
                    } catch (MediaException mediaException) {
                        Logger.loggerError("CaptureImage->Video->mediaException->"+mediaException.toString());
                    }
              }
              if(player.getState() != Player.STARTED){
                    player.start();
              }
              if(fileFormat == 1 && null != videoControl){
                  videoControl.setVisible(true);
              }
            totoalTime = 0;
            isCurrentScreen = true;
            isAudio = true;
        }catch(Exception exception){
            //bug 14663
            deInitialize(true);
            Logger.loggerError("CaptureImage->audio->"+exception.toString());
        }
        return isAudio;
    }

    public boolean loadCamera(){
        if(fileFormat == -1 || fileFormat == 2){ //Image CR 14694
            return camera();
        } else { //Audio/Video
            return StartAudioOrVideo();
        }  
    }

    public byte commandAction(byte priority) {
        byte rByte = 3;
        if(priority == 0){
            captureImage();
        } else if(priority == 1){
            deInitialize(true);
        }
        return rByte;
    }

    public void drawCaptureImage(Graphics graphics){
        if(fileFormat == -1 || fileFormat == 2){
            if(null != UiGlobalVariables.imagefile){
                if(isUpload){
                    CustomCanvas.drawProcessImage(graphics,imgrotType);
                } else {
                    int yPosition = UISettings.headerHeight;//+UISettings.secondaryHeaderHeight);
                    int height = UISettings.formHeight - (yPosition+UISettings.footerHeight+4);
                    if(UiGlobalVariables.imagefile.getHeight()<height)
                        yPosition += (height-UiGlobalVariables.imagefile.getHeight())/2;
                    graphics.drawImage(UiGlobalVariables.imagefile, (UISettings.formWidth-
                            UiGlobalVariables.imagefile.getWidth())/2, yPosition, Graphics.TOP|Graphics.LEFT);
                }
            }
        } else {
            //Cr 14465
            if(isUpload){
                 CustomCanvas.drawProcessImage(graphics,imgrotType);
            } else {
                if(totoalTime>0 ){
                    String value = Utilities.getElapsedTime(totoalTime);
                   // Logger.debugOnError("timer value="+ value);
                    graphics.drawString(value, (UISettings.formWidth-graphics.getFont().stringWidth(value))/2,
                            (UISettings.formHeight/2)-(graphics.getFont().getHeight()/2), Graphics.LEFT|Graphics.TOP);
                }
            }
        }
    }

    public void reLoadFooterMenu(){
        if(fileFormat == -1 || fileFormat == 2){
            if(null != UiGlobalVariables.imagefile){
                if(isUpload){
                    UISettings.lOByte = -1;
                    UISettings.rOByte = -1;
                } else {
                    if(fileFormat == -1){
                        UISettings.lOByte = 54; //Send Index
                    } else {
                        UISettings.lOByte = 61; //Use Index
                    }
                    UISettings.rOByte = 57; //Discard Index
                }
            } else {
                UISettings.lOByte = 56; //Capture Index
                UISettings.rOByte = 22; //Back Index
            }
        } else {
            if(isUpload){
                UISettings.lOByte = -1;
                UISettings.rOByte = -1;
            } else if(totoalTime>0){
                UISettings.lOByte = 54; //Send Index
                UISettings.rOByte = 57; //Discard Index
            } else {
                UISettings.lOByte =  59; //record Index
                UISettings.rOByte =  8; //Cancel Index
            }
        }
    }


    public boolean pointerPressed(int xPosition, int yPosition, boolean isNotDrag,
            boolean isDragEnd, boolean isPressed){
        if(isNotDrag){
            if(fileFormat == -1 || fileFormat == 2){
                captureImage();
            }
        }
        return false;
    }

    public void keyPressed(int keyCode){
        if(keyCode == UISettings.RIGHTOPTION){
            if(UISettings.rOByte >-1){ //Back and Discard Index
                if(UISettings.rOByte == 57){
                    isCapture(fileFormat);
                    loadCamera();
                } else {
                    deInitialize(true);
                }
            } 
        } else if(keyCode == UISettings.LEFTOPTION){
            if(UISettings.lOByte == 56){ // Capture Image 56
                capture();
            } else if(UISettings.lOByte == 59){ //Start Record; 59
              if(null != recordControl){
                 byte timeLimit = 20;
                  try{
                      if(fileFormat == 1 && null != videoControl){
                          timeLimit = 10;
                      }
                      recordControl.startRecord();
                      totoalTime = Calendar.getInstance().getTime().getTime();
                      reLoadFooterMenu();
                      startRotateImateTimer(timeLimit*1000);
                  }catch(Exception exception){
                      Logger.loggerError("CaptureImageAudio->StartRecord"+exception.toString());
                  }
                  ShortHandCanvas.IsNeedPaint();
              } else {
                  Logger.loggerError("CpatureImageAudio->Keypressed->RecordControl Not initialized");
              }
            } else if(UISettings.lOByte == 54 || UISettings.lOByte ==  61){
                //CR 14465  14694
                if(((fileFormat == -1 || fileFormat == 2) && null != UiGlobalVariables.imagefile) || (fileFormat>-1 && fileFormat<2)){
                    captureAudio();
                    isUpload = true;
                    reLoadFooterMenu();
                    startRotateImateTimer(100);
                    //CR 14465
                    DownloadHandler.getInstance().downloadImage(null,chatId, fileFormat);
                } else {
                    deInitialize(true);
                }
            } 
        } else if(keyCode == UISettings.FIREKEY){ //CR 14465
            if(null == UiGlobalVariables.imagefile && isCamera){
                capture();
            }
        }
    }

    private void rotateImage(boolean stop){
        if(isUpload){
            rStart++;
            if(rStart >= 5){
                imgrotType++;
                if (imgrotType > 3) {
                    imgrotType = 0;
                }
                rStart = 0;
            }
        } else {
            //CR 14465
            if(!stop)
              capture();
        }
    }

    public void rotateScreen(){
        if(isCurrentScreen){
            if(fileFormat == -1 || fileFormat == 2){
                CustomCanvas.sHeader = CustomCanvas.getSecondaryHeader(Constants.options[55], "",0);
            } else if(fileFormat == 0) {
                CustomCanvas.sHeader = CustomCanvas.getSecondaryHeader(Constants.headerText[33], "",0);
            } else {
                CustomCanvas.sHeader = CustomCanvas.getSecondaryHeader(Constants.headerText[34], "",0);
            }
        }
    }

    //CR 14423
    public String SaveImage_Audio(String imageName){
        String fileLocation = null;
        try {
            UiGlobalVariables.byteArrayInputStream.reset();
            //Cr 14465, 14492
            if(fileFormat == -1 || fileFormat == 2){
                fileLocation = Utilities.saveImage_Audio(UiGlobalVariables.byteArrayInputStream,
                        imageName, -1, "jpeg");
            } else {
                fileLocation = Utilities.saveImage_Audio(UiGlobalVariables.byteArrayInputStream,
                        imageName, fileFormat, UiGlobalVariables.extension);
            }
        } catch(Exception exception){
            Logger.loggerError("CaptureImage-SaveImage->"+exception.toString());
        }
        return fileLocation;
    }

    public void deInitialize(boolean isReEnable) {
        //CR 14465
        try{
            if(null != byteArrayOutputStream){
                byteArrayOutputStream.close();
                byteArrayOutputStream = null;
            }
        }catch(Exception exception){

        }

        Runtime.getRuntime().gc();

        isCamera = false;

        isAudio = false;
        
        totoalTime = 0;//mali
        
        stopPlayer();

        UiGlobalVariables.imagefile = null;

        rStart = 0;

        //Int
        imgrotType = 0;

        isUpload = false;

        isCurrentScreen = false;

        try{
            if(null != UiGlobalVariables.byteArrayInputStream){
                UiGlobalVariables.byteArrayInputStream.close();
                UiGlobalVariables.byteArrayInputStream = null;
            }
        }catch(Exception exception){
            Logger.loggerError("CaptureImage->deinitialize->StreamClose"+exception.toString());
        }

        if(isReEnable){
            iMenuHandler.enableUpSelection();
        }
    }

    private void stopPlayer(){

        stopRotateImageTimer();

        isCamera = false;
        isAudio = false;
        videoControl = null;

        //CR 14465
        try{
            if(null != recordControl){
                recordControl.stopRecord();
                recordControl = null;
            }
        }catch(Exception exception){
            Logger.loggerError("CaptureImage->stopPlayer->recoreControl"+exception.toString());
        }
        
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
    }

    private void stopRotateImageTimer(){
       // Logger.debugOnError("timer stop");
        if(null != rotateImageTimer){
            rotateImageTimer.cancel();
            rotateImageTimer = null;
        }
    }


    private void startRotateImateTimer(int time){
        stopRotateImageTimer();
        rotateImageTimer = new Timer();
        rotateImageTimer.schedule(new DisplayImageTimer(), 0, time);
    }

    private boolean setImageData(String encodeFormat){
        boolean isSecurity = false;
        try {
            UiGlobalVariables.byteArrayInputStream = new ByteArrayInputStream(videoControl.getSnapshot(encodeFormat));
            UiGlobalVariables.extension = null;//mali
        } catch(SecurityException securityException){
            Logger.loggerError("Encoding format security Exception "+securityException.toString());
            isSecurity = true;
        } catch(Exception e){
            Logger.loggerError("Encoding format not supported "+encodeFormat);
        }
        return isSecurity;
    }

    private void capture(){
        if(fileFormat == -1 || fileFormat == 2){ //Image CR 14694
            captureImage();
        } else { //Audio/Video
            captureAudio();
        }
    }

    private void captureAudio(){
        if(null != recordControl){
            try{
                Logger.debugOnError("timer stop after capture aud/img");
                totoalTime = 1;

                stopRotateImageTimer();                
                if(fileFormat == 1){
                    UiGlobalVariables.extension = "3gp";
                }
                    
                String manufacture =  Utilities.getManufacture();
                if(null != manufacture && manufacture.toLowerCase().indexOf("samsung") == -1){
                    recordControl.stopRecord();
                    recordControl.commit();
                } else {
                    recordControl.commit();
                    recordControl.stopRecord();
                }

                recordControl = null;

                stopPlayer();
                Logger.loggerError("Recorded Bytes"+byteArrayOutputStream.size());
                UiGlobalVariables.byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                if(null != byteArrayOutputStream){
                    byteArrayOutputStream.close();
                    byteArrayOutputStream = null;
                }

                reLoadFooterMenu();
                ShortHandCanvas.IsNeedPaint();
                return;

            }catch(Exception exception){
                Logger.loggerError("Capture Audio/Video - Stop Player FileFormat "+fileFormat+" "+exception.toString());
            }
            deInitialize(true);
            ShortHandCanvas.IsNeedPaint();
        }
    }

    private void captureImage(){
        if(null != videoControl){
            try {
                int exceptionPoint = 0;
                int height = (UISettings.formHeight-(UISettings.headerHeight+
                        UISettings.footerHeight));
                String encodings = System.getProperty("video.snapshot.encodings");
                exceptionPoint = 1;
                if(null != encodings){
                    boolean isSecurity = false;
                    String[] encodeFormat = new String[]{
                        "encoding=image/jpeg&quality=100&width="+(UISettings.formWidth-2)+"&height="+height,
                        "encoding=jpeg&quality=100&width="+(UISettings.formWidth-2)+"&height="+height,
                        "encoding=image/jpeg&width="+(UISettings.formWidth-2)+"&height="+height,
                        "encoding=jpeg&width="+(UISettings.formWidth-2)+"&height="+height,
                        "encoding=image/jpeg",
                        "encoding=jpeg",
                        null};
                    for(int i=0;i<encodeFormat.length;i++){
                        exceptionPoint++;
                        isSecurity = setImageData(encodeFormat[i]);
                        if(isSecurity || null != UiGlobalVariables.byteArrayInputStream){
                            break;
                        }
                    }
                }
                if(null != UiGlobalVariables.byteArrayInputStream){
                    exceptionPoint = 9;
                    //bug 14452, 14694
                    UiGlobalVariables.imagefile = ImageHelper.createThumbnail(UiGlobalVariables.byteArrayInputStream, 
                            UISettings.formWidth-2, height, (fileFormat == 2)?true:false);
                    exceptionPoint = 10;
                    stopPlayer();
                    exceptionPoint = 11;
                }
                if(null != UiGlobalVariables.imagefile){
                    exceptionPoint = 12;
                    UiGlobalVariables.byteArrayInputStream.reset();
                    exceptionPoint = 13;
                    reLoadFooterMenu();
                    exceptionPoint = 14;
                    ShortHandCanvas.IsNeedPaint();
                    exceptionPoint = 15;
                    return;
                } 
            } catch(Exception exception){
                Logger.loggerError("CaptureImage->captureImage->"+exception.toString());
            }
            deInitialize(true);
            ShortHandCanvas.IsNeedPaint();
        }
    }

    public boolean isAudioScreen() {
        return isAudio;
    }

    class DisplayImageTimer extends TimerTask{
                    boolean started = false;
        public void run(){
            if(!started)
                started = true; 
            else
                started= false;
            rotateImage(started);
            ShortHandCanvas.IsNeedPaint();
        }
    }
}
