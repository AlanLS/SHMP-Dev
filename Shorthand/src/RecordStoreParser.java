import java.io.ByteArrayOutputStream;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotOpenException;

/**
 *
 * @author sasi
 */
public class RecordStoreParser {

    private RecordStore recordStore = null;

    private String recordName = null;

    private boolean isOpen = false;

    //CR 13294
    public static boolean clearAppRMS(){
        String[] recordStore = RecordStore.listRecordStores();
        int count = 0;
        if(null != recordStore && (count=recordStore.length)>0){
            for(int i=0;i<count;i++){
                try{
                    //CR 13416 13332
                 //CR 13562
                    if(recordStore[i].compareTo(RecordManager.setttingsRMS) == 0){
                        Settings.setIsPCNF(false);
                    } else if(recordStore[i].compareTo(RecordManager.globalMemName) != 0 &&
                            recordStore[i].compareTo(RecordManager.msContact) != 0){
                        RecordStore.deleteRecordStore(recordStore[i]);
                    }
                    //13562>>
                }catch(Exception e){

                }
            }
        }
        return true;
    }

    public boolean createRecordStore(String rName,boolean isOpen){
        boolean isOpened = false;
        try{
            recordStore = RecordStore.openRecordStore(rName, isOpen);
            recordName = rName;
            this.isOpen = isOpen;
            isOpened = true;
        }catch(Exception e){ recordStore = null; }
        return isOpened;

    }

    private void flushRMS(){
        if(ChannelData.isRMSAlwaysClose()){
            closeRecordStore();
            openRecordStore(recordName, isOpen, false, false);
        }
    }

    public boolean isOpen(){
        if(null != recordStore)
            return true;
        return false;
    }

    public boolean openRecordStore(String rName, boolean isOpen, boolean isAnyRead, boolean isAnyWrite){
        boolean isNotOpened = true;
        try{
            if(isAnyRead)
                recordStore = RecordStore.openRecordStore(rName, isOpen, RecordStore.AUTHMODE_ANY, isAnyWrite);
            else recordStore = RecordStore.openRecordStore(rName, isOpen);
            recordName = rName;
            this.isOpen = isOpen;
            isNotOpened = false;
        }catch(Exception e){ recordStore = null;}
        return isNotOpened;
    }

    public static String[] getAllRecordStoreName(){
        String[] name = null;
        name = RecordStore.listRecordStores();
        return name;
    }

    public String[] getRecordsValue()throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException{
        String[] rValues = null;
        int count = 0;
        String temp = null;
        if(null != recordStore && (count = recordStore.getNumRecords())>0){
            rValues = new String[count];
            byte[] rByte = null;
            for(int i=1;i<=count;i++){
                rByte = recordStore.getRecord(i);
                if(rByte.length>0){
                    temp = new String(rByte);
                } else temp = "";
                rValues[i-1] = temp;
            }
        }
        return rValues;
    }

    public void closeRecordStore(){
        try{
            if(null != recordStore){
                recordStore.closeRecordStore();
                recordStore = null;
            }
        }catch(Exception e){}
    }

    public byte[] getRecord(int rPostion){
        byte[] rByte = null;
        if(null != recordStore){
            try{
                rByte = recordStore.getRecord(rPostion);
            }catch(Exception e){}
        }
        return rByte;
    }

    public static byte[] getRecordStore(String rName){
        byte[] rByte = null;
        int count = 0;
        try{
            RecordStore tempRecordStore = RecordStore.openRecordStore(rName,false);
            if(null != tempRecordStore){
                if((count = tempRecordStore.getNumRecords())>0){
                    int j = 0;
                    for(int i=1;i<=count;i++){
                        j+= tempRecordStore.getRecordSize(i);
                    }
                    if(j>0)
                        rByte = new byte[j];
                    j = 0;
                    for(int i=1;i<=count;i++){
                        j += tempRecordStore.getRecord(i, rByte, j);
                    }
                }
                tempRecordStore.closeRecordStore();
                tempRecordStore = null;
            }
        }catch(Exception e){
            rByte = null;
        }
        return rByte;
    }

    /**
     *
     * @return
     * @throws javax.microedition.rms.RecordStoreNotOpenException
     * @throws javax.microedition.rms.InvalidRecordIDException
     * @throws javax.microedition.rms.RecordStoreException
     */
    public byte[] getRecordStore() throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException{
        byte[] rByte = null;
        int count = 0;
        if(null != recordStore && (count = recordStore.getNumRecords())>0){
            int j = 0;
            for(int i=1;i<=count;i++){
                j+= recordStore.getRecordSize(i);
            }
            if(j>0)
                rByte = new byte[j];
            j = 0;
            for(int i=1;i<=count;i++){
                j += recordStore.getRecord(i, rByte, j);
            }
        }
        return rByte;
    }

    public int getNumRecords() {
        int rCount = 0;
        try{
            if(null != recordStore)
                rCount = recordStore.getNumRecords();
        }catch(Exception e){}
        return rCount;
    }

    public boolean addRecord(byte[] rByte,int sPos,int ePos, boolean isLogWrite){
        try{
            if(null != recordStore){
                recordStore.addRecord(rByte, 0, rByte.length);
                flushRMS();
//                //#if VERBOSELOGGING
//                //|JG|if(isLogWrite && UISettings.isRmsLogNotDisable)
//                //|JG|Logger.debugOnError( "Before Add Available Size"+(recordStore.getSizeAvailable()-rByte.length) +"\nAfter Add Available Size "+recordStore.getSizeAvailable());
//                    //#endif //11801
                return true;
            }
        }catch(Exception e){}
        return false;
    }

    public boolean addRecord(byte[] rByte){
        try{
            if(null != recordStore){
                recordStore.addRecord(rByte, 0, rByte.length);
                flushRMS();
//                //#if VERBOSELOGGING
//                //|JG|if(UISettings.isRmsLogNotDisable)
//                //|JG|Logger.debugOnError( "Before Add Available Size"+(recordStore.getSizeAvailable()-rByte.length) +"\nAfter Add Available Size "+recordStore.getSizeAvailable());
//                    //#endif //11801
                return true;
            }
        }catch(Exception e){}
        return false;
    }

    public boolean setRecord(int rPosition, byte[] rbyte,int sPos,int ePos, boolean isLogWrite){
        if(null != recordStore){
            try{
//                int count = recordStore.getSizeAvailable();
                recordStore.setRecord(rPosition, rbyte, sPos, ePos);
                flushRMS();
//                //#if VERBOSELOGGING
//                //|JG|if(isLogWrite && UISettings.isRmsLogNotDisable)
//                //|JG|Logger.debugOnError("Before Add Available Size"+count +"\nTotal Available Size "+recordStore.getSizeAvailable());
//                    //#endif//11801
            }catch(Exception e){}
        }
        return false;
   }

     public boolean setRecord(int rPosition, byte[] rbyte){
        if(null != recordStore){
            try{
//                int count = recordStore.getSizeAvailable();
                recordStore.setRecord(rPosition, rbyte, 0, rbyte.length);
                flushRMS();
//                //#if VERBOSELOGGING
//                //|JG|if(UISettings.isRmsLogNotDisable)
//                //|JG|Logger.debugOnError("Before Add Available Size"+count +"\nTotal Available Size "+recordStore.getSizeAvailable());
//                    //#endif//11801
            }catch(Exception e){
                Logger.debugOnError("Set record exception "+ e);
            }
        }
        return false;
   }

    public boolean deleteRecord(int rPosition, boolean isLogWrite) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException{
        if(null != recordStore){
//            int count = recordStore.getSizeAvailable();
            recordStore.deleteRecord(rPosition);
//            //#if VERBOSELOGGING
//            //|JG|if(isLogWrite && UISettings.isRmsLogNotDisable)
//            //|JG|Logger.debugOnError("Before Delete Available Size"+count +"\nAfter Delete Available Size "+recordStore.getSizeAvailable());
//                    //#endif //11801
            return true;
        }
        return false;
    }

    public static boolean deleteRecordStore(String rName, boolean isLogWrite){
        boolean isDeleted = false;
        try{
//            int count = getSizeAvailable();
            RecordStore.deleteRecordStore(rName);
//            //#if VERBOSELOGGING
//            //|JG|if(isLogWrite && UISettings.isRmsLogNotDisable)
//            //|JG|Logger.debugOnError("Before Delete Available Size"+count +"\n After Delete Available Size "+getSizeAvailable());
//            //#endif //11801
            isDeleted = true;
        }catch(Exception e){}
        return isDeleted;
    }

    public static int getSizeAvailable(){
        int size = 0;
        try{
            RecordStore recordStore1 = RecordStore.openRecordStore("123", true);
            size = recordStore1.getSizeAvailable();
            recordStore1.closeRecordStore();
            RecordStore.deleteRecordStore("123");
        }catch(Exception e){}
        return size;
    }

    public int getTotalRecordStoresize(){
        int size = 0;
        try{
            if(null != recordStore){
                size = recordStore.getSizeAvailable();
                int count = recordStore.getNumRecords();
                for(int i=1;i<=count;i++){
                    size += recordStore.getRecordSize(i);
                }
            }
        }catch(Exception e){}
        return size;
    }

    public static boolean isRecordStoreExits(String rName){
        try{
            RecordStore tempStore = RecordStore.openRecordStore(rName, false);
            tempStore.closeRecordStore();
            return true;
        }catch(Exception e){}
        return false;
    }

    public static byte UpdateRecordStore(String rName,byte[] rByte, boolean isLogWrite){
        byte isUpdated = 0;
        if(null != rByte){
            deleteRecordStore(rName, isLogWrite);
            RecordStore tempRecordStore = null;
            try{
                tempRecordStore = RecordStore.openRecordStore(rName, true);
                if(null != tempRecordStore){
                    int count = tempRecordStore.getSizeAvailable();
                    if(count>rByte.length){
                        tempRecordStore.addRecord(rByte, 0, rByte.length);
//                        //#if VERBOSELOGGING
//                        //|JG|if(isLogWrite && UISettings.isRmsLogNotDisable)
//                        //|JG|Logger.debugOnError("Before Add Available Size"+count +"\nTotal Available Size "+tempRecordStore.getSizeAvailable());
//                        //#endif //11801
                        isUpdated = 1;
                    } else {
                        isUpdated = 2;
                        Logger.loggerError("Recorde Store is Not have sufficient size, Total Record Store Size "+count+" Try to Write "+rByte.length);
                    }
                }
            }catch(RecordStoreFullException rs){
                isUpdated = 2;
                Logger.loggerError("RecordStroe Parser -> "+rName +" RecordStore Full Error "+rs.toString());
            } catch(Exception e){
                Logger.loggerError("RecordStroe Parser -> "+rName +" Record Updateion Error "+e.toString());
            }
           finally {
                try {
                    if (null != tempRecordStore) {
                        tempRecordStore.closeRecordStore();
                        tempRecordStore = null;
                    }
                } catch (Exception e) {
                }
            }

        }

        if(isUpdated != 1)
            deleteRecordStore(rName, isLogWrite);
        return isUpdated;
    }

    public static byte UpdateRecordStore(String rName,ByteArrayOutputStream rByte, boolean isLogWrite){
        byte isUpdated = 0;
        if(null != rByte){
            deleteRecordStore(rName, isLogWrite);
            RecordStore tempRecordStore = null;
            try{
                tempRecordStore = RecordStore.openRecordStore(rName, true);
                if(null != tempRecordStore){
                    int count = tempRecordStore.getSizeAvailable();
                    if(count>rByte.size()){
                        tempRecordStore.addRecord(rByte.toByteArray(), 0, rByte.size());
//                        //#if VERBOSELOGGING
//                        //|JG|if(isLogWrite && UISettings.isRmsLogNotDisable)
//                        //|JG|Logger.debugOnError("Before Add Available Size"+count +"\nTotal Available Size "+tempRecordStore.getSizeAvailable());
//                        //#endif //11801
                        isUpdated = 1;
                    } else {
                        isUpdated = 2;
                        Logger.loggerError("Recorde Store is Not have sufficient size, Total Record Store Size "+count+" Try to Write "+rByte.size());
                    }
                }
            }catch(RecordStoreFullException rs){
                isUpdated = 2;
                Logger.loggerError("RecordStroe Parser -> "+rName +" RecordStore Full Error "+rs.toString());
            } catch(Exception e){
                Logger.loggerError("RecordStroe Parser -> "+rName +" Record Updateion Error "+e.toString());
            }
           finally {
                try {
                    if (null != tempRecordStore) {
                        tempRecordStore.closeRecordStore();
                        tempRecordStore = null;
                    }
                } catch (Exception e) {
                }
            }

        }

        if(isUpdated != 1)
            deleteRecordStore(rName, isLogWrite);
        return isUpdated;
    }
}
