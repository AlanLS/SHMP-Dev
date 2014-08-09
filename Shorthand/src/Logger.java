/*
 * Logger Class to Write the Exceptions
 *
 * @author - Hakunamatata
 * @version - v1.00.15
 * @copyright (c) John Mcdonnough 
 */

public class Logger {

        private static RecordStoreParser rStoreParser = null;
    
        private static String loggerName = RecordManager.getLoggerName();

        private static void closeRecordStore(){
            if(null != rStoreParser){
                rStoreParser.closeRecordStore();
                rStoreParser = null;
            }
        }
        
	/**
	 * Method to Initialize the Logger File for given fileLocation
	 * 
         * @throws loggerInitialException
	 */
	public static void initializeLogger() {
            openRecordStore();
            loggerError("Application Started");
	}

        private static void openRecordStore(){
            rStoreParser =  new RecordStoreParser();
            try{
                rStoreParser.openRecordStore(loggerName, true,true,true);
            }catch(Exception e){
               rStoreParser = null;
            }
        }

//        private static void flushRMS(){
//            if(ChannelData.isRMSAlwaysClose()){
//                closeRecordStore();
//                openRecordStore();
//            }
//        }

	/**
	 * Method to write the Error Message to the Logger file
	 * 
	 * @param errMsg - Variable will contain the Error message
         *
         * @throws writeLoggerException
	 */
	public static void loggerError(String errMsg) {
            try {
                //CR 10681
                if(Settings.getIsDebug()){
                    if(null != rStoreParser){
                        synchronized(rStoreParser){
                            errMsg = "\n"+Utilities.getcurrentMMDDYYHHMMSSFormat()+" "+errMsg;
                            byte[] uByte = errMsg.getBytes("UTF-8");
                            rStoreParser.addRecord(uByte, 0, uByte.length,false);
//                            flushRMS();
                        }
                    }
                }
            } catch (Exception e) {}
	}

//        public static void debugTestingOnError(String errMsg){
//        //   debugOnError(errMsg);
//        }

        public static void debugOnError(byte[] uByte){
            if (Settings.getIsDebug()) {
                try {
                    if(null != rStoreParser){
                        synchronized(rStoreParser){
                            rStoreParser.addRecord(uByte, 0, uByte.length,false);
                        }
                    }
                } catch (Exception e) {}
            }
        }
        
        public static void debugOnError(String errMsg){
            if (Settings.getIsDebug()) {
                try {
                    if(null != rStoreParser){
                        synchronized(rStoreParser){
                            errMsg = "\n"+Utilities.getcurrentMMDDYYHHMMSSFormat()+" "+errMsg;
                            byte[] uByte = errMsg.getBytes("UTF-8");
                            rStoreParser.addRecord(uByte, 0, uByte.length,false);
//                            flushRMS();
                        }
                    }
                } catch (Exception e) {}
            }
        }
        
        public static void writeOldData(byte[] rByte){
            try{
                if(null != rStoreParser){
                    synchronized(rStoreParser){
                        if(rStoreParser.getNumRecords()>0){
                            byte[] rb =rStoreParser.getRecordStore();
                            closeRecordStore();
                            RecordStoreParser.deleteRecordStore(loggerName,false);
                            openRecordStore();
                            rStoreParser.addRecord(rByte, 0, rByte.length,false);
                            rStoreParser.addRecord(rb, 0, rb.length,false);
                        } else {
                            rStoreParser.addRecord(rByte, 0, rByte.length,false);
                        }
//                        flushRMS();
                    }
                }
            }catch(Exception e){}
        }
        
        public static byte[] getUploadLog(){
            byte[] rbyte = null;
            try{
                if(null != rStoreParser){
                    synchronized(rStoreParser){
                        if(rStoreParser.getNumRecords()>0){
                            rbyte = rStoreParser.getRecordStore();
                            closeRecordStore();
                            RecordStoreParser.deleteRecordStore(loggerName,false);
                            openRecordStore();
                        }
                    }
                }
            }catch(Exception e){}
            return rbyte;
        }
        
        public static boolean isLogNotEmpty(){
            boolean isNoLog = false;
            if(null != rStoreParser)
                synchronized(rStoreParser){
                    if(rStoreParser.getNumRecords()>0){
                        isNoLog = true;
                    }
                }
            return isNoLog;
        }
        
        public static void clearLog(){
            try{
                if(null != rStoreParser){
                    synchronized(rStoreParser){
                        if(rStoreParser.getNumRecords()>0){
                            closeRecordStore();
                            RecordStoreParser.deleteRecordStore(loggerName,false);
                            openRecordStore();
                        } 
                    }
                }
            }catch(Exception e){}
        }
        
	/**
	 * Method to Close the Logger file
         *
         * @throws closeLoggerException 
	 */
	public static void clossLogger() {
            if(null != rStoreParser){
                synchronized(rStoreParser){
                    loggerError("Application End Successfully");
                    closeRecordStore();
                }
            }
	}
}
