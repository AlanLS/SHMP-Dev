/*
 * MemorizedVariable.java
 *
 * Created on October 18, 2007, 7:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author 
 */
public class GlobalMemorizeVariable  {

	private static String[] memvarname = null;

	private static String[] memvarvalue = null;

	private static int index;

        private static boolean isNotSet = true;

        public static String globalDataMode = null;

        public static String globalMaxDataMode = null;

        public static String globalPereDataMode = null;

        private static int chatCharacterSequenceIndex = -1;

        private static String globalProfileMode = null;

        private static boolean isRefreshContacts = false;

        private static void setValue(){
            if(isNotSet){
                byte[] rbyte = RecordStoreParser.getRecordStore(RecordManager.globalMemName);
                if(null != rbyte){
                    ByteArrayReader dIn = new ByteArrayReader(rbyte);
                    index = dIn.readByte();
                    memvarname = new String[index];
                    memvarvalue = new String[index];
                    for(int i=0;i<index;i++){
                        memvarname[i] = dIn.readUTF();
                        memvarvalue[i] = dIn.readUTF();
                        setValue(memvarname[i],memvarvalue[i],i,false);
                    }
                    dIn.close();
                    dIn = null;
                } else {
                    index = 0;
                    memvarname = new String[4];
                    memvarvalue = new String[4];
                }
                setBootValue();
                isNotSet = false;
            }
        }

        //CR 14327
        private static void setBootValue(){
            if(chatCharacterSequenceIndex == -1){
                increaseArray();
                chatCharacterSequenceIndex =  index;
                memvarname[index] = ChannelData.globalChatSequence;
                memvarvalue[index] = "00000";
                index++;
            }
            
        }

        public static String getChatSequenceNumber(){
            String value = "00000";
            if(chatCharacterSequenceIndex>-1){
                value = memvarvalue[chatCharacterSequenceIndex];
            }
            return value;
        }

        //CR 14823
        public static void setContactType(String value){
            if(value.charAt(0) == 'G'){
                add(ChannelData.globalContactType, "group");
            } else if(value.charAt(0) == 'S'){
                add(ChannelData.globalContactType, "shout");
            } else if(Contacts.isContactMsisdn(value)){
                add(ChannelData.globalContactType, "phonecontact");
            } else{
                add(ChannelData.globalContactType, "newcontact");
            }

        }

        //CR 14327
        public static void updateChatCharacterSequence(int value){
            if(chatCharacterSequenceIndex>-1){
                value =  (Integer.parseInt(memvarvalue[chatCharacterSequenceIndex]) + value);
                if(value<10){
                    memvarvalue[chatCharacterSequenceIndex] = "0000"+value;
                } else if(value<100){
                    memvarvalue[chatCharacterSequenceIndex] = "000"+value;
                } else if(value<1000){
                    memvarvalue[chatCharacterSequenceIndex] = "00"+value;
                } else if(value<10000){
                    memvarvalue[chatCharacterSequenceIndex] = "0"+value;
                } else {
                    memvarvalue[chatCharacterSequenceIndex] = value+"";
                }
                updateMemorize();
            }
        }

        //<-- CR 13678
        private static void setValue(String name, String value, int index, boolean isNew){
            if(name.compareTo(ChannelData.globalMaxDataMode) == 0){
                globalMaxDataMode = value;
            } else if(name.compareTo(ChannelData.globalBearerProtocol) == 0){
                globalPereDataMode = value;
            } else if(name.compareTo(ChannelData.globalDataMode) == 0){
                globalDataMode = value;
            } else if(name.compareTo(ChannelData.globalChatSequence) == 0){
                chatCharacterSequenceIndex = index;
            } else if(isNew && name.compareTo(ChannelData.globalProfilePictureTag) == 0){ //CR 14694
                globalProfileMode = value;
            } else if(isNew && name.compareTo(ChannelData.globalContctRefresh) == 0){
                isRefreshContacts = true;
            } 
        }
        // CR 13678 -->

        //CR 14733
        public static boolean isRefreshContacts(){
            boolean isRefresh =  isRefreshContacts;
            isRefreshContacts = false;
            return isRefresh;
        }

        //CR 14694
        public static byte getProfileMode(){
            byte profileMode = -1;
            if(null != globalProfileMode){
                if(globalProfileMode.compareTo("TakePicture") == 0){
                    if(Utilities.isVideoCapture()){
                        profileMode = 1;
                    }
                } else if(globalProfileMode.compareTo("SelectPicture") == 0){
                    profileMode = 0;
                }
                globalProfileMode = null;
            }
            return profileMode;
        }

        public static void updateMemMaxdataMode(String value, boolean isUpdate){
            boolean isSet = false;
            setValue();
            if (null != memvarname && index>0) {
                for (int i = 0; i < index; i++) {
                    if ((0 == ChannelData.globalMaxDataMode.toLowerCase().compareTo(memvarname[i].toLowerCase()))) {
                        isSet = true;
                        value = memvarvalue[i];
                        break;
                    }
                }
            }
            if(!isSet || isUpdate){
                add(ChannelData.globalMaxDataMode, value);
            }
        }

        //CR 12319
        public static int getChatUnreadCount(String abbervation){
            if(abbervation.indexOf(")>")>-1){
                abbervation = abbervation.substring(1,abbervation.length()-2);
                abbervation ="memGLOBAL"+abbervation+"Pending";
                String value = getValue(abbervation);
                if(null != value && value.length()>0){
                    return Integer.parseInt(value);
                }
            }
            return 0;
        }

        //CR 12319
        public static void clearChatUnReadCount(String abbervation){
            if(abbervation.indexOf(")>")>-1){
                abbervation = abbervation.substring(1,abbervation.length()-2);
                abbervation ="memGLOBAL"+abbervation+"Pending";
                removeValue(abbervation);
            }
        }

        //CR 12319
        public static void updateChatUnreadCount(String abbervation, int count, boolean isReset){
            if(abbervation.indexOf(")>")>-1){
                abbervation = abbervation.substring(1,abbervation.length()-2);
                String _varname ="memGLOBAL"+abbervation+"Pending";
                String value = null;
                setValue();
                int position = -1;
                if (null != memvarname && index>0) {
                    for (int i = 0; i < index; i++) {
                        if ((0 == _varname.toLowerCase().compareTo(memvarname[i].toLowerCase()))) {
                            value = memvarvalue[i];
                            position = i;
                            break;
                        }
                    }
                }
                if(null != value){
                    if(isReset){
                        memvarvalue[position] = count+"";
                    } else {
                        if(value.length()>0){
                            if(count != 0){
                                if(Integer.parseInt(value)+count >= 0) { //bug 13203
                                    memvarvalue[position] = (Integer.parseInt(value)+count) +"";
                                }
                            } else {
                                memvarvalue[position] = "0";
                            }
                        } else {
                            memvarvalue[position] = "1";
                        }
                    }
                    updateMemorize();
                } else {
                    increaseArray();
                    memvarname[index] = _varname;
                    if(isReset){
                        memvarvalue[index] = count+"";
                    } else {
                        memvarvalue[index] = "1";
                    }
                    index++;
                    updateNewMemorize();
                }
            }
        }

	public static String getValue(String _varname) {
            String value = null;
            setValue();
            if(ChannelData.globalClientState.compareTo(_varname) == 0) {//CR 12548
                if(Settings.isAliveState){
                    value = "Alive";
                } else value = "Stopped";
            } else if (null != memvarname && index>0) {
                for (int i = 0; i < index; i++) {
                    if ((0 == _varname.toLowerCase().compareTo(memvarname[i].toLowerCase()))) {
                            value = memvarvalue[i];
                        break;
                    }
                }
            }
            return value;
	}

        //CR 12548
        private static boolean isNotClientState(String name, String value){
            if(ChannelData.globalClientState.compareTo(name) == 0) {
                if(null != value){
                    if(value.toLowerCase().compareTo("alive") == 0)
                        Settings.isAliveState = true;
                    else Settings.isAliveState = false;
                } else Settings.isAliveState = false;
                return false;
            }
            return true;
        }

        /**
         *
         * @param _disval
         * @param _varname
         * @param _value
         */
	public static void add(String _varname, String _value) {
            setValue();
            if(isNotClientState(_varname, _value)) {
                setValue(_varname, _value,chatCharacterSequenceIndex,true);
                if (null != memvarname) {
                    //CR 11974
                    if(null != _value){
                        if (ChannelData.globalShortCode.compareTo(_varname) == 0) { //10669
                            Settings.setLongCode(_value);
                        } else if (ChannelData.globalBearerProtocol.compareTo(_varname) == 0) {
                            if (_value.toLowerCase().compareTo("data") == 0) {
                                Settings.setIsGPRS(true);
                            } else {
                                Settings.setIsGPRS(false);
                            }
                        } else if(ChannelData.globalUserPhone.compareTo(_varname) == 0){ //CR 12069
                            Settings.setPhoneNumber(_value);
                        } else if(ChannelData.globalUserId.compareTo(_varname) == 0){ //CR 12988
                            Settings.setUID(_value);
                        }
                        //<-- CR 13618
//                        else if(ChannelData.globalDataMode.compareTo(_varname) == 0){ //CR 13237
//                            Settings.setIsModeNotChanged(true);
//                        }
                        // CR 13618 -->
                    }

                    //bug id 6014
                    for (int i = 0; i < index; i++) {
                        if (0 == _varname.toLowerCase().compareTo(memvarname[i].toLowerCase())) {
                            if(memvarvalue[i].compareTo(_value) != 0){
                                memvarvalue[i] = _value;
                                updateMemorize();
                            }
                            return;
                        }
                    }
                }

                increaseArray();
                memvarname[index] = _varname;
                memvarvalue[index] = _value;
    //            disvalue[index] = _disval;
                index++;
                updateNewMemorize();
            }
	}


        private static void increaseArray(){
            if (memvarname.length <= index) {
                String[] _tmp = memvarname;
                memvarname = new String[index + 4];
                System.arraycopy(_tmp, 0, memvarname, 0, index);
                _tmp = memvarvalue;
                memvarvalue = new String[index + 4];
                System.arraycopy(_tmp, 0, memvarvalue, 0, index);
//                _tmp = disvalue;
//                disvalue = new String[index + 4];
//                System.arraycopy(_tmp,0,disvalue,0,index);
                _tmp =null;
            }

        }
        /**
         *
         * @param memName
         * @return
         */
        public static boolean removeValue(String memName){
            setValue();
            if(isNotClientState(memName, null)){
                //bug no 13351
                setValue(memName, null,chatCharacterSequenceIndex,false);
                if(memName.toLowerCase().compareTo(ChannelData.globalUserPhone.toLowerCase()) == 0){
                    Settings.setPhoneNumber(null);
                }
                if(null != memvarname){
                    for(int i=0;i<index;i++){
                        if(memvarname[i].toLowerCase().compareTo(memName.toLowerCase()) == 0){
                            String[] temp = memvarname;
                            memvarname = new String[temp.length];
                            System.arraycopy(temp, 0, memvarname, 0, i);
                            System.arraycopy(temp, i+1, memvarname, i, (index-(i+1)));
                            temp = memvarvalue;
                            memvarvalue = new String[temp.length];
                            System.arraycopy(temp, 0, memvarvalue, 0, i);
                            System.arraycopy(temp, i+1, memvarvalue, i, (index-(i+1)));
                            temp = null;
                            index--;
                            updateMemorize();
                            return true;
                        }
                    }
                }
            } else return true;
            return false;
        }

        private static void updateMemorize(){
            if(index>0){
                ByteArrayWriter dOut = new ByteArrayWriter();
                dOut.writeByte(index);
                for(int i=0;i<index;i++){
//                    if(null != disvalue[i]){
//                       dOut.writeBoolean(true);
//                       dOut.writeUTF(disvalue[i]);
//                    } else dOut.writeBoolean(false);
                    dOut.writeUTF(memvarname[i]);
                    dOut.writeUTF(memvarvalue[i]);
                }
                byte[] rbyte = dOut.toByteArray();
                dOut.close();
                dOut = null;
                RecordStoreParser.UpdateRecordStore(RecordManager.globalMemName, rbyte, true);
            } else RecordStoreParser.deleteRecordStore(RecordManager.globalMemName, true);
        }

        /**
         *
         * @param _disval
         * @param _varName
         * @param _value
         */
        private static void updateNewMemorize(){
            byte[] rbyte = RecordStoreParser.getRecordStore(RecordManager.globalMemName);
            ByteArrayWriter dOut = new ByteArrayWriter();
            if(null != rbyte){
                rbyte[0] += 1;
                dOut.write(rbyte);
            } else {
                dOut.writeByte(1);
            }
//            if(null != disvalue[index-1]){
//               dOut.writeBoolean(true);
//               dOut.writeUTF(disvalue[index-1]);
//            } else dOut.writeBoolean(false);
            dOut.writeUTF(memvarname[index-1]);
            dOut.writeUTF(memvarvalue[index-1]);
            rbyte = dOut.toByteArray();
            dOut.close();
            dOut = null;
            RecordStoreParser.UpdateRecordStore(RecordManager.globalMemName, rbyte, true);
        }


        /**
         *
         */
        public static void deinitialize(){
//            disvalue = null;
            memvarname = null;
            memvarvalue = null;
            index = 0;
        }

}
