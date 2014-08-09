
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.RecordControl;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sasi
 */
public class CaptureAudio {

    private Timer timer = null;
    private Player player = null;
    private RecordControl recordControl = null;
    private ByteArrayOutputStream byteArrayOutputStream = null;


    private boolean  createPlayer(String value){
        try{
            player = Manager.createPlayer(value);
        }catch(MediaException mediaException){
            return false;
        } catch(Exception exception){
        }
        return true;
    }

    public boolean isAudioStart(){
        boolean isStarted = false;
         String[] value = new String[]{
                "capture://audio?encoding=pcm&rate=8000",
                "capture://audio?encoding=audio/amr&rate=8000",
                "capture://audio?encoding=audio/x-amr&rate=8000",
                "capture://audio?encoding=audio/au&rate=8000",
                "capture://audio?encoding=audio/wav&rate=8000",
                "capture://audio?encoding=audio/x-wav&rate=8000",
                "capture://audio?encoding=audio/basic&rate=8000",

                "capture://audio?encoding=pcm",
                "capture://audio?encoding=audio/amr",
                "capture://audio?encoding=audio/x-amr",
                "capture://audio?encoding=audio/au",
                "capture://audio?encoding=audio/wav",
                "capture://audio?encoding=audio/x-wav",
                "capture://audio?encoding=audio/basic",
                "capture://audio"
            };
        for(int i=0;i<value.length;i++){
            if(createPlayer(value[i])){
                break;
            }
        }
         try{
            if(null != player){

                player.realize();
                recordControl = (RecordControl)player.getControl("RecordControl");
                byteArrayOutputStream = new ByteArrayOutputStream();
                Logger.debugOnError("recording capture aud class");
                recordControl.setRecordStream(byteArrayOutputStream);
                
//                  String location = System.getProperty("fileconn.dir.recordings");
//                  if(null == location){
//                    location  = "file:///c:/photos/";
//                  }
//
//                if(null != location){
//                 location += System.currentTimeMillis() + ".wav";
//                }          
//                recordControl.setRecordLocation(location);

                recordControl.startRecord();
                player.start();
                timer = new Timer();
                timer.schedule(new StopPlayerTimer(), 20*1000);
                isStarted = true;
            } else {
                Logger.loggerError("Player Not supported ");
            }
        }catch(Exception exception){
Logger.debugOnError("capture audio err:"+ exception);
        }
         return isStarted;
    }

    private void stopAudio(){
        try{
        if(null != timer){
                timer.cancel();
                timer = null;
            }
            String extension = ".amr";
            if(recordControl.getContentType().indexOf("wav")>-1){
                extension = ".wav";
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

            String location = System.getProperty("fileconn.dir.recordings");
            if(null == location){
                location  = "file:///c:/photos/";
            }

            if(null != location){
                location += System.currentTimeMillis() + extension;
                FileConnection fileConnection = (FileConnection)Connector.open(location);
                if(!fileConnection.exists()){
                    fileConnection.create();
                    DataOutputStream dataOutputStream = fileConnection.openDataOutputStream();
                    dataOutputStream.write(byteArrayOutputStream.toByteArray());
                    Logger.debugOnError("Saved file size="+dataOutputStream.size());
                    dataOutputStream.close();
                    dataOutputStream = null;
                    fileConnection.close();
                    fileConnection = null;
                }
            }
        }catch(Exception exception){
            Logger.loggerError("Capture Audio - Stop Player "+exception.toString());
        }
    }

    private void releaseStream(){
        try{
        if(null != byteArrayOutputStream){
            byteArrayOutputStream.close();
            byteArrayOutputStream = null;
        }
        }catch(Exception exception){
            Logger.loggerError("CaptureAudio-releaseStream->"+exception.toString());
        }
    }

    private void stopPlayer(){
        try{
            if(null != player){
                player.stop();
                player.deallocate();
                player.close();
                player = null;
            }
        }catch(Exception exception){}
    }

    class StopPlayerTimer extends TimerTask{

        public void run() {
            stopAudio();
        }

    }

}

