/*
 * AppParser.java
 *
 * @author - Hakunamatata
 * @version - v1.00.15.
 * @copyright (c) John Mcdonnough
 */


import java.util.Calendar;

public class AppParser {

        // Variable to hold the Profile Name
        private String pname = null;
        
        // Variable to hold the All profiles sequence shortcuts
        //private String[] favList = null;
        
        // Variable to hold the Profile Id
        private String pId = null;

        private RecordStoreParser rStoreParser = null;
        
        //private int qSTIndex = 1;
        
        /**
         * Method to Save the Profile Location, Name and Id.
         *
         * @param loc - Variable will contain the Profile Location
         * @param pName - Variable will contain the Profile Name.
         * @param pid - Variable will contain the Profile Id.de
         **/
        
	public void setProfileLocation(String loc,String pName,String pid) {
            pname = pName;
            pId = pid;
	}

        /**
         * Method to retrieve All the Profile Sequence Shortcuts.
         *
         * @param fLoc - Variable will contain the All Profiles Sequence Shortcuts Location.
         * @param fName - Variable will contain the All the Profile Name.
         *
         * @return favList - Variable will contain All the Sequence Shortcut Names.it may be Null.
         **/        
        
//        public void clearMyShortcuts(){
//            favList = null;
//        }
        
        
        /**
         * Method to retrieve All the Profile Sequence Shortcuts.
         *
         * @param fLoc - Variable will contain the All Profiles Sequence Shortcuts Location.
         * @param fName - Variable will contain the All the Profile Name.
         *
         * @return favList - Variable will contain All the Sequence Shortcut Names.it may be Null.
         **/        
        
//        public String[] getFavouriteProfileList(String[] fName){
//            if(null == favList){
//                int len;
//                if(null != fName && (len = fName.length)>0){
//                    int count;
//                    String loc = null;
//                    String[] list = null;
//                    int flen = 0;
//                    byte[] rBytes = null;
//                    for(int i=0;i<len;i++){
//                        loc = RecordManager.getQueryAndMemRecordLoc(fName[i]);
//                        try{
//                            openRecord(loc, false);
//                            if(null != rStoreParser && ((count= rStoreParser.getNumRecords()))>0){
//                                list = new String[(count/2)];
//                                flen = 0;
//                                for(int j=qSTIndex;j<=count;j+=2){
//                                    rBytes = rStoreParser.getRecord(j);
//                                    if(null != rBytes && rBytes.length>0){
//                                        loc = new String(rBytes);
//                                        if(!loc.startsWith("$"))
//                                            list[flen++] = fName[i] +":"+loc;
//                                    }
//                                }
//                                if(flen>0){
//                                    String[] temp = favList;
//                                    count = 0;
//                                    if(null != temp)
//                                        count  = temp.length;
//                                    favList = new String[flen+count];
//                                    if(null != temp)
//                                        System.arraycopy(temp,0,favList,0,count);
//                                    System.arraycopy(list,0,favList,count,flen);
//                                }
//                            }
//                        }catch(Exception e){}
//                        closeRecord();
//                    }
//                }
//            }
//            return favList;
//        }
        
        /**
         * Method to delete the Selected Sequence Shortcut.
         *
         * @param floc - Variable will contain the selected Shortcut location.
         * @param itemName - Variable will contain the Selected Shortcut Profile Name.
         * @param seqName - Variable will contain the Selected Shortcut Name.
         **/
        
//        public void deleteFavouriteProfile(String pName, String itemName, String seqName){
//            int len;
//           if(null != favList && (len=favList.length)>0) {
//               if(len > 1){
//                   for(int i=0;i<len;i++){
//                       if(favList[i].compareTo(itemName) == 0){
//                            String[] temp = favList;
//                            len--;
//                            favList = new String[len];
//                            System.arraycopy(temp,0,favList,0,i);
//                            System.arraycopy(temp,i+1,favList,i,len-i);
//                            break;
//                       }
//                   }
//               } else favList =null;
//               try{
//                   if(null != pName){
//                       pName = RecordManager.getQueryAndMemRecordLoc(pName);
//                       openRecord(pName, false);
//                       if(null != rStoreParser){
//                           len = rStoreParser.getNumRecords();
//                           byte[] rByte = null;
//                           for(int i=qSTIndex;i<=len;i+=2){
//                               rByte = rStoreParser.getRecord(i);
//                               if(null != rByte && rByte.length>0){
//                                    pName = new String(rByte);
//                                    if(pName.compareTo(seqName) == 0)
//                                    {
//                                        pName = "$" + pName;
//                                        rByte = pName.getBytes();
//                                        rStoreParser.setRecord(i, rByte, 0, rByte.length, true);
//                                        break;
//                                    }
//                               }
//                           }
//                       }
//                   }
//               }catch(Exception e){}
//               closeRecord();
//           }
//        }

        
        
//        private void deleteWidgetMyShorts(){
//            int len;
//            if(null != favList && (len=favList.length)>0) {
//               if(len > 1){
//                   int stIndex = -1;
//                   int eIndex = 0;
//                   for(int i=0;i<len;i++){
//                       if(favList[i].indexOf(pname+":") == 0){
//                           if(stIndex ==-1)
//                               stIndex = i;
//                           eIndex++;
//                       }
//                   }
//                   if(stIndex != -1){
//                       if((len-eIndex)>0){
//                            String[] temp = favList;
//                            favList = new String[len-eIndex];
//                            System.arraycopy(temp,0,favList,0,stIndex);
//                            System.arraycopy(temp,stIndex+eIndex,favList,stIndex,len-eIndex);
//                       }else favList = null;
//                   }
//               } else favList =null;
//            }
//        }
        
        public String deleteWidgetRecords(){
//            deleteWidgetMyShorts();
            RecordManager.deleteRecords(pname);
            return pname;
        }
        

	/**
	 * Method to Delete the Selected Profile.
         *
	 * @return pname - Variable will contain the deleted profile Name.	 
	 **/
        
	public String deleteProfile() {
//            deleteWidgetMyShorts();            
            RecordManager.deleteSingleApp(pname);
            return pname;
	}
        
        /**
         * Method to get the Profile Id
         *
         * @return pId - Variable will contain the Selected Profile Id.
         **/
        
        public String getProfileId(){
            return pId;
        }
        
        
    /**
     * Method to retrieve the Total Number of Message Send And Receive form the Last 6 Month entried
     *  @param sMsgCount Currently Sent Message Couint
     *  @parm rMsgCount currently received message count
     * 
     *  @return Message counter
     */
       
        public String getMsgCount(){
            StringBuffer stbuf = new StringBuffer();
            openRecord(RecordManager.allMessageCountRMS, true);
            stbuf.append(Constants.appendText[11]).append(":\n"); // Text Messages Sent and Received
            byte mon =0;
            short yr=0;
            short sC = 0;
            short rC = 0;
            boolean isCurrentMonth = false;
            Calendar cal = Calendar.getInstance();
            if(rStoreParser!= null && rStoreParser.getNumRecords()>0){
                int i=1;
                if(rStoreParser.getNumRecords()>5)
                    i=rStoreParser.getNumRecords()-4;
                byte[] rbyte = null;
                for(;i<=rStoreParser.getNumRecords();i++){
                    rbyte = rStoreParser.getRecord(i);
                    if(null != rbyte){
                        ByteArrayReader din = new ByteArrayReader(rbyte);
                        mon= din.readByte();
                        yr = din.readShort();
                        sC = din.readShort();
                        rC = din.readShort();

                        din.close();
                        din=null;
                        if(cal.get(Calendar.MONTH) == mon && cal.get(Calendar.YEAR) == yr){
                            isCurrentMonth=true;
                        }
                        stbuf.append(Utilities.getMonth(mon)).append(" ")
                                    .append(yr).append(": ") /** Read Year */
                                    .append(sC).append(" ").append(Constants.appendText[9]).append("; ") /** Read Sent Message Count */
                                    .append(rC).append(" ").append(Constants.appendText[10]).append("\n"); /** Read Received Message Count */
                        if(isCurrentMonth)
                            break;
                    }
                }
            }
            if(!isCurrentMonth)
                stbuf.append(Utilities.getMonth((byte)cal.get(Calendar.MONTH))).append(" ")
                        .append(cal.get(Calendar.YEAR)).append(": ") /** Read Year */
                        .append(0).append(" ").append(Constants.appendText[9]).append("; ") /** Read Sent Message Count */
                        .append(0).append(" ").append(Constants.appendText[10]).append("\n"); /** Read Received Message Count */
            cal = null;
            closeRecord();
            Runtime.getRuntime().gc();
            return stbuf.toString();
        }

        public String getDataCount(){
            StringBuffer stbuf = new StringBuffer();
            openRecord(RecordManager.allGprsRequestCountRMS, true);
            stbuf.append(Constants.appendText[33]).append(":\n"); // Text Messages Sent and Received
            byte mon =0;
            short yr=0;
            double sC = 0;
            double rC = 0;
            
            boolean isCurrentMonth = false;
            Calendar cal = Calendar.getInstance();
            if(rStoreParser!= null && rStoreParser.getNumRecords()>0){
                int i=1;
                if(rStoreParser.getNumRecords()>5)
                    i=rStoreParser.getNumRecords()-4;
                byte[] rbyte = null;
                for(;i<=rStoreParser.getNumRecords();i++){
                    rbyte = rStoreParser.getRecord(i);
                    if(null != rbyte){
                        ByteArrayReader din = new ByteArrayReader(rbyte);
                        mon= din.readByte();
                        yr = din.readShort();
                        sC = din.readDouble();                     

                        rC = din.readDouble();
                        
                        din.close();
                        din=null;
                        if(cal.get(Calendar.MONTH) == mon && cal.get(Calendar.YEAR) == yr){
                            isCurrentMonth=true;
                        }
                        //CR<-0012014->
                        String temp1 = "0",temp2 ="0";
                        if(sC > 0)
                         temp1 = (sC/1024+"").substring(0,(sC/1024+"").indexOf(".")+3);
                        if(rC > 0)
                         temp2 = (rC/1024+"").substring(0,(rC/1024+"").indexOf(".")+3);


                        stbuf.append(Utilities.getMonth(mon)).append(" ")
                                    .append(yr).append(": ") /** Read Year */
                                    .append(temp1).append(" ").append(Constants.appendText[34]).append("; ") /** Read Sent Message Count */
                                    .append(temp2).append(" ").append(Constants.appendText[35]).append("\n"); /** Read Received Message Count */

                        //CR<-0012014->
                        if(isCurrentMonth)
                            break;
                    }
                }
            }
            if(!isCurrentMonth)
                stbuf.append(Utilities.getMonth((byte)cal.get(Calendar.MONTH))).append(" ")
                        .append(cal.get(Calendar.YEAR)).append(": ") /** Read Year */
                        .append(0).append(" ").append(Constants.appendText[34]).append("; ") /** Read Sent Message Count */
                        .append(0).append(" ").append(Constants.appendText[35]).append("\n"); /** Read Received Message Count */
            cal = null;
            closeRecord();
            Runtime.getRuntime().gc();
            return stbuf.toString();
        }
        
        private RecordStoreParser openRecord(String rName,boolean isOpend){
            try{
                rStoreParser = new RecordStoreParser();
                rStoreParser.openRecordStore(RecordManager.getRecordStoreName(rName), isOpend,false,false);
            }catch(Exception e){}
            return rStoreParser;
        }
        
        private void closeRecord(){
            try{
                if(rStoreParser!= null){
                    rStoreParser.closeRecordStore();
                    rStoreParser=null;
                }
                
            }catch(Exception e){}
        }

	/**
	 * Method to remove the Objects.
         *
         * @throws exception	 
	 * 
	 */
	public void deInitialize() {
            try {               
                if(null != rStoreParser){
                    rStoreParser.closeRecordStore();
                    rStoreParser = null;
                }
                pname = null;
//                favList = null;
                Runtime.getRuntime().gc();
            } catch (Exception exception) {	}
	}
}
