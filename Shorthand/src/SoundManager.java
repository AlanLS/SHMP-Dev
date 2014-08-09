/*
 * SoundManager Class to Raise the Sound in the Application
 *
 * @author - Sasikumar
 * @version - v1.00.15
 * @copyright (c) John Mcdonnough
 */

import java.io.DataInputStream;
import javax.microedition.media.Player;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.PlayerListener;

public class SoundManager implements PlayerListener{

    private Player player = null;
    private DataInputStream dataInputStream = null;
    private static SoundManager soundManager = null;
    private static boolean isSoundPlay = true;

    public static SoundManager getInstance(){
        if(null == soundManager)
            soundManager = new SoundManager();
        return soundManager;
    }

    public void setData(int id){
        dataInputStream = ObjectBuilderFactory.getPCanvas().getResourceStream(id);
    }
    
    public void initialize(){
        try{
            if(null == player && isSoundPlay){
                String[] contentType = Manager.getSupportedContentTypes(null);
                String temp = "";
                if(null != contentType){
                    for(int i=0;i<contentType.length;i++){
                        temp += contentType[i]+",";
                    }
                }
                contentType = Manager.getSupportedContentTypes("audio");
                if(null != contentType){
                    for(int i=0;i<contentType.length;i++){
                        temp += contentType[i]+",";
                    }
                }
                //Logger.loggerError("SupportedContentType->"+temp);
                dataInputStream.reset();
                try {
                    player = Manager.createPlayer(dataInputStream, "audio/x-amr");
                } catch(MediaException mediaException){
                    player = Manager.createPlayer(dataInputStream, "audio/amr");
                }
                player.addPlayerListener(this);
                player.realize();
                player.prefetch();
            }
        } catch(MediaException mediaException){
            isSoundPlay = false;
            Logger.loggerError("SoundManager->initialize->MediaException "+mediaException.toString());
            deinitialize();
        } catch(Exception exception){
            isSoundPlay = false;
            Logger.loggerError("SoundManager->initialize->Exception "+exception.toString());
            deinitialize();
        }
    }

    public void deinitialize(){
        if(null != player){
            player.removePlayerListener(null);
            try{
                if(player.getState() == Player.STARTED){
                    player.stop();
                }
            }catch(Exception exception){
                Logger.loggerError("SoundManager->deinitialize-> "+exception.toString());
            }

            if(player.getState() == Player.PREFETCHED){
                player.deallocate();
            }

            if(player.getState() == Player.REALIZED  || player.getState() == Player.UNREALIZED){
                player.close();
            }
            player = null;
        }
    }

    /**
     * Method to Start to Play Sound
     *
     * @throws playSoundException
     **/
    public void playSound() {
        
        //#if VERBOSELOGGING
        //|JG|Logger.debugOnError("Before Playing");
        //#endif
        try{
            initialize();
            if(null != player){
                if(player.getState() != Player.STARTED){
                    player.start();
                } 
            }
        }catch(Exception exception){
            Logger.loggerError("SoundManager->playSount-> "+exception.toString());
        }
        //#if VERBOSELOGGING
        //|JG|Logger.debugOnError("After Playing");
        //#endif
    }

    public void playerUpdate(Player player, String event, Object o) {
        if(event.compareTo(PlayerListener.END_OF_MEDIA) == 0){
            try{
                if(null != player){
                    player.stop();
                }
            }catch(Exception exception){

            }
        }
    }
}
