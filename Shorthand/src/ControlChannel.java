
import generated.Build;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Administrator
 */
public class ControlChannel {

    private String cData = null;
    private RecordStoreParser rStoreParser = null;
    public String updateUrl = null;
    public String versionNumber = null;
    private boolean isDTTMNotSent = false;

    public ControlChannel() {
        opendRecordStore();
    }

    private void opendRecordStore() {
        rStoreParser = new RecordStoreParser();
        if (rStoreParser.openRecordStore(RecordManager.controlChannelName, true, false, false)) {
            rStoreParser = null;
        } else if (rStoreParser.getNumRecords() == 0) {
            byte[] rByte = new byte[0];
            rStoreParser.addRecord(rByte, 0, rByte.length, true); //High QUEUE
            rStoreParser.addRecord(rByte, 0, rByte.length, true); //MEDIUM QUEUE
            rStoreParser.addRecord(rByte, 0, rByte.length, true); //LOW QUEUE
            rStoreParser.addRecord(rByte, 0, rByte.length, true); //PROV QUEUE
            rStoreParser.addRecord(rByte, 0, rByte.length, true); //UIDA QUEUE
        } else if (rStoreParser.getNumRecords() < 5) { //Need to support the Old Client
            byte[] rByte = new byte[0];
            rStoreParser.addRecord(rByte, 0, rByte.length, true); //UIDA QUEUE
        }
    }

    public void addProfileDelete(String pId) {
        StringBuffer stbuf = new StringBuffer(ChannelData.getServerName()).append(" ").append(ChannelData.SDChar).append("PDEL").append(pId).append(ChannelData.EDChar);
        pId = getPacket(stbuf.toString(), false);
        //CR 3595
        //bug 12209
        ObjectBuilderFactory.GetKernel().sendMessage(getMessageDto(pId));
    }

    private Message getMessageDto(String msg) {
        Message message = new Message();
        message.setSFullName(Constants.aName);
        message.setRMsg(new String[]{msg});
        message.setShortcode(ChannelData.getShortcode());
        message.setInboxFunc(true);
        message.setIsNotChatMessage(true);
        message.setAbberVation(Constants.appName);
        return message;
    }

    public void addSelected(String pId, String adId) {
        StringBuffer stbuf = new StringBuffer("AEVT").append(pId).append(adId).append("1");
        createPacket(ChannelData.LOW, stbuf.toString());
        stbuf = null;
    }

    public void sendReplyStop(String pName) {
        //CR 3516
        int index = pName.indexOf(" ");
        if (index > -1) {
            pName = pName.substring(0, index).trim();
        }
        StringBuffer stbuf = new StringBuffer(ChannelData.getServerName()).append(" ").append("(").append(pName).append(") STOP");
        pName = getPacket(stbuf.toString(), false);
        //bug 12209
        ObjectBuilderFactory.GetKernel().sendMessage(getMessageDto(pName));
    }

    public void addDisplayed(String pId, String adId) {
        StringBuffer stbuf = new StringBuffer("AEVT").append(pId).append(adId).append("0");
        createPacket(ChannelData.MEDIUM, stbuf.toString());
        stbuf = null;
    }

    //Cr 14333
    //bug 14557
    public void addChatMessageReceived(String chatId, String chatSequence) {
        if(null != chatSequence && chatSequence.length()>0){
            StringBuffer stbuf = new StringBuffer("DRC2").append(chatId).append("(").append(chatSequence).append(")");
            createPacket(ChannelData.HIGH, stbuf.toString());
            stbuf = null;
        }
    }

    //Cr 14333
    //bug 14557
    public void addChatMessageDisplayed(String chatId, String chatSequence) {
        if(null != chatSequence && chatSequence.length()>0){
            StringBuffer stbuf = new StringBuffer("DRC3").append(chatId).append("(").append(chatSequence).append(")");
            createPacket(ChannelData.HIGH, stbuf.toString());
            stbuf = null;
        }
    }

    public void addProfileUsage(String pId, int pCount, short qCount, short aCount) { //bug 8311
        String tcount = "";
        if (pCount < 10) {
            tcount = "00" + pCount;
        } else if (pCount < 100) {
            tcount = "0" + pCount;
        }
        StringBuffer stbuf = new StringBuffer("PUSG").append(pId).append(tcount).append(Utilities.getCurrentMMDDYY()).append(":");
        if (qCount < 10) {
            stbuf.append("00");
        } else if (qCount < 100) {
            stbuf.append("0");
        }
        stbuf.append(qCount);
        if (aCount < 10) {
            stbuf.append("00");
        } else if (aCount < 100) {
            stbuf.append("0");
        }
        stbuf.append(aCount);
        createPacket(ChannelData.LOW, stbuf.toString());
        stbuf = null;
    }

    public void sendInteractiveAction(String pId, String url_Number) { //bug 8311
        StringBuffer stbuf = new StringBuffer("IEVT").append(pId).append(Utilities.getCurrentMMDDYY()).append(Utilities.getHHMM24HrsFormat()).append(url_Number);
        createPacket(ChannelData.LOW, stbuf.toString());
    }

    private String getProvData() {
        String model = Build.MANUFACTURER + "" + Build.MODEL;
        //String OS =  System.getProperty("os.name");
        //bu id 5107
        StringBuffer stbuf = new StringBuffer("PROV") //.append(Utilities.remove(ObjectBuilderFactory.GetKernel().getVersionNumber(true),".")) // for version (.) increment
                .append(ObjectBuilderFactory.GetKernel().getVersionNumber(true)).append(ChannelData.getProvenenceName()).append(":").append(model).append(":").append("J2ME").append(":");
        if (UISettings.GENERIC) { //This is only for Generic bug id 8440
            model = Utilities.getManufacture();
        } else {
            model = System.getProperty(Build.MOBILE_MODEL);
            if (null != model) {
                if (Build.SEPERATOR.length() > 0) {
                    int value = model.indexOf(Build.SEPERATOR);
                    if (value > 0) {
                        model = model.substring(0, value);
                    }
                }
            } else {
                model = Constants.appendText[24]; //No Code
            }
        }

        //Cr 8805
        if (model.length() > 20) {
            model = model.substring(0, 20);
        }
        //Hardcode model name to Nokia for SFC testing - only for 3.7.14.1
        if (ObjectBuilderFactory.GetKernel().getVersionNumber(false).equals(new String("3.7.14.1"))) {
            model = "NokiaTest";
        }
        stbuf.append(model).append(":").append(ChannelData.isPushEnabled()); //CR 7697
        return stbuf.toString();
    }

    public void sendProvenanceAction(boolean isSend) {
        //#if VERBOSELOGGING
        //|JG|Logger.loggerError("Creating PROV and adding to queue");
        //#endif

        //if(!isQueuedPROV())
        createPriorityPacket(getProvData(), ChannelData.PROV);

        //CR 7325
        if (isSend && !ChannelData.getProvQue()) {
            String prov = getPacket(ChannelData.getServerName() + " ", true);
            //bug 12209
            //CR 11975
            ObjectBuilderFactory.GetKernel().sendMessage(getMessageDto(prov));
            ObjectBuilderFactory.GetKernel().displayMessageSendSprit();
        }
    }

    //CR 14669
    public void sendContacts(){
        StringBuffer stbuf = new StringBuffer(ChannelData.getServerName()).append(" !*CTUL(")
                .append(Contacts.getUploadContacts(":")).append(")*!");
        ObjectBuilderFactory.GetKernel().sendMessage(getMessageDto(stbuf.toString()));
        ObjectBuilderFactory.GetKernel().displayMessageSendSprit();
    }

    //CR 12988
    private void createUserID() {
        createPriorityPacket("UIDA" + Settings.getUID(true), ChannelData.UIDA);
    }

    private void createPriorityPacket(String pStr, byte position) { //CR 8351
        try {
            StringBuffer fLoc = new StringBuffer(new Date().getTime() + "-").append(pStr);
            //rStoreParser.openRecordStore(RecordManager.getControlChanelName(), true,false,false);
            byte[] rByte = rStoreParser.getRecord(position);
            if (null == rByte || rByte.length == 0) {
                pStr = fLoc.toString();
            } else {
                return;
            }
            fLoc = null;
            rByte = pStr.getBytes();
            rStoreParser.setRecord(position, rByte, 0, rByte.length, true);
        } catch (Exception e) {
        }
    }

    public void sendAdMenuItemSelectAction(String pId, String adId, int actId) {
        StringBuffer stbuf = new StringBuffer(ChannelData.getServerName()).append(" ").append(ChannelData.SDChar).append("AEVT").append(pId).append(adId).append("2").append(Integer.toHexString(actId).toUpperCase()).append(Utilities.getCurrentDateHHMMDDYYFormat()).append(ChannelData.EDChar);
        String temp = getPacket(stbuf.toString(), false);
        stbuf = null;
        //bug 12209
        ObjectBuilderFactory.GetKernel().sendMessage(getMessageDto(temp));//CR 11975
        temp = null;
    }

    public void sendAdCallItemSelectAction(String pId, String adId, int actId) {
        StringBuffer stbuf = new StringBuffer("AEVT").append(pId).append(adId).append("2").append(Integer.toHexString(actId).toUpperCase()).append(Utilities.getCurrentDateHHMMDDYYFormat());
        createPacket(ChannelData.LOW, stbuf.toString());
    }

    private void createPacket(byte periority, String pStr) {
        try {//Bug 12679
            synchronized (rStoreParser) {
                StringBuffer fLoc = new StringBuffer(new Date().getTime() + "-").append(pStr);
                //rStoreParser.openRecordStore(RecordManager.getControlChanelName(), true,false,false);
                byte[] rByte = rStoreParser.getRecord(periority);
                if (null != rByte && rByte.length > 0) {
                    pStr = new String(rByte);
                    pStr += "," + fLoc.toString();
                } else {
                    pStr = fLoc.toString();
                }
                fLoc = null;
                rByte = pStr.getBytes();
                rStoreParser.setRecord(periority, rByte, 0, rByte.length, true);
//            flushRMS();
            }
        } catch (Exception e) {
        }
    }

    private String getPacket(String sMsg, boolean isProv) {
        int index = sMsg.indexOf(ChannelData.EDChar);
        int length = sMsg.length() + 4;
        if (index == -1) {
            length += 4;
        }
        length = UISettings.MAX_COUNT - (length + 3); //3 For sequnuce number
        if (length > 3) {

            //CR 12988
            //CR 12988
            if ((!Settings.isIsUCNF() || null == Settings.getUID(false)) && sMsg.indexOf("UIDA") == -1) {
                createUserID();
            }

            StringBuffer tempMsg = null;
            StringBuffer pMsg = new StringBuffer();
            if (length > 3) {
                //CR 12988, bug no 13291
                if (sMsg.indexOf("UIDA") == -1) {
                    tempMsg = makePackets(length, ChannelData.UIDA);
                    length -= tempMsg.length();
                    pMsg.append(tempMsg);
                }
                // CR 14324
                if (isDTTMNotSent && length > getDTTMChannelData().length()) {
                    isDTTMNotSent = false;
                    pMsg.append(getDTTMChannelData()).append(",");
                }
                if (length > 3) {
                    if (isProv) {
                        tempMsg = makePackets(length, ChannelData.PROV);
                        length -= tempMsg.length();
                        pMsg.append(tempMsg);
                    }
                    if (length > 3) {
                        tempMsg = makePackets(length, ChannelData.HIGH);
                        pMsg.append(tempMsg);
                        length -= tempMsg.length();
                        if (length > 3) {
                            tempMsg = makePackets(length, ChannelData.MEDIUM);
                            pMsg.append(tempMsg);
                            length -= tempMsg.length();
                            if (length > 3) {
                                if (!isProv) {
                                    tempMsg = makePackets(length, ChannelData.PROV);
                                    length -= tempMsg.length();
                                    pMsg.append(tempMsg);
                                }
                                if (length > 3) {
                                    tempMsg = makePackets(length, ChannelData.LOW);
                                    pMsg.append(tempMsg);
                                    length -= tempMsg.length();
                                }
                            }
                        }
                    }
                }
            }

            if (pMsg.length() > 0) {
                pMsg.deleteCharAt(pMsg.length() - 1);
                if (index > -1) {
                    if (sMsg.charAt(index) != ',') {
                        sMsg = sMsg.substring(0, index) + "," + pMsg.toString() + sMsg.substring(index);
                    } else {
                        sMsg = sMsg.substring(0, index) + pMsg.toString() + sMsg.substring(index);
                    }
                } else {
                    pMsg.insert(0, ChannelData.SDChar);
                    pMsg.insert(pMsg.length(), ChannelData.EDChar);
                    pMsg.insert(0, sMsg);
                    sMsg = pMsg.toString();
                }
            }
        }
        return sMsg;
    }

    private StringBuffer makePackets(int length, int periority) {
        StringBuffer pMsg = new StringBuffer();
        if (null != rStoreParser) {
            try {
                byte[] rbyte = rStoreParser.getRecord(periority);
                if (null != rbyte && rbyte.length > 0) {
                    String data = new String(rbyte);
                    String[] datas = Utilities.split(data, ",");
                    int count = datas.length;
                    int index = 0;
                    String uValue = "";
                    data = "";
                    for (int i = 0; i < count; i++) {
                        uValue = datas[i];
                        index = datas[i].indexOf("-");
                        datas[i] = datas[i].substring(index + 1);
                        if (length > datas[i].length()) {
                            pMsg.append(datas[i]).append(",");
                            length -= (datas[i].length() + 1);
                        } else {
                            data += uValue + ",";
                        }
                    }
                    if (data.length() > 0) {
                        data = data.substring(0, data.length() - 1);
                    }
                    rbyte = data.getBytes();
                    rStoreParser.setRecord(periority, rbyte, 0, rbyte.length, true);
                }
            } catch (Exception e) {
            }
        }
        return pMsg;
    }

    public void reorderQueue() {
        StringBuffer pMsg = searchQueue(ChannelData.HIGH, 1);
        pMsg.append(searchQueue(ChannelData.MEDIUM, 5));
        //if(null == Settings.getPhoneNumber())
        pMsg.append(searchQueue(ChannelData.PROV, 21));
//        else {
//            byte[] rbyte = new byte[0];
//            rStoreParser.setRecord(ChannelData.PROV, rbyte, 0, rbyte.length);
//        }
        pMsg.append(searchQueue(ChannelData.LOW, 21));
    }

    private StringBuffer searchQueue(int periority, int daydiff) {
        StringBuffer pMsg = new StringBuffer("");
        if (null != rStoreParser) {
            try {
                byte[] rbyte = rStoreParser.getRecord(periority);
                if (null != rbyte && rbyte.length > 0) {
                    String data = new String(rbyte);
                    String[] datas = Utilities.split(data, ",");
                    int count = datas.length;
                    int index = 0;
                    String udata = "";
                    data = "";
                    long time;
                    Date date = new Date();
                    Calendar curCal = Calendar.getInstance();
                    Calendar cal = Calendar.getInstance();
                    for (int i = 0; i < count; i++) {
                        udata = datas[i];
                        index = datas[i].indexOf("-");
                        time = Long.parseLong(datas[i].substring(0, index));
                        datas[i] = datas[i].substring(index + 1);
                        date.setTime(time);
                        cal.setTime(date);
                        if (Utilities.dateDiff(curCal.get(Calendar.DATE), curCal.get(Calendar.MONTH), curCal.get(Calendar.YEAR),
                                cal.get(Calendar.DATE), cal.get(Calendar.MONTH), cal.get(Calendar.YEAR)) > daydiff) {
                            pMsg.append(datas[i]).append(",");
                        } else {
                            data += udata + ",";
                        }
                    }
                    if (pMsg.length() > 0) {
                        if (data.length() > 0) {
                            data = data.substring(0, data.length() - 1);
                        }
                        rbyte = data.getBytes();
                        rStoreParser.setRecord(periority, rbyte, 0, rbyte.length, true);
                    }
                }
            } catch (Exception e) {
            }
        }
        return pMsg;
    }

    // CR 11730
    public void addRoundTripTime(long delayTime, String sequenceNumber) {
        if (delayTime > 0 && null != sequenceNumber && sequenceNumber.length() == 1) {
            if (ChannelData.isRoundTripTime()) {
                String time = Utilities.getRoundTripTime(delayTime) + "";
                if (time.length() == 1) {
                    time = "00" + time;
                } else if (time.length() == 2) {
                    time = "0" + time;
                }
                StringBuffer stbuf = new StringBuffer("RTDA").append(time).append(sequenceNumber);
                createPacket(ChannelData.LOW, stbuf.toString());
            }
        }
    }

    public String removePacket(String rMsg) {
        String temp = null;
        if (rMsg != null) {
            //#if VERBOSELOGGING
            //|JG| if (rMsg.length() > 20) {
            //|JG|  Logger.debugOnError("Starting to remove control channel data from message " + rMsg.substring(0, 20));
            //|JG| } else {
            //|JG|  Logger.debugOnError("Starting to remove control channel data from message " + rMsg);
            //|JG| }
            //#endif//11801
            int stInd = rMsg.indexOf(ChannelData.SDChar);//Bug 0012270
            if (stInd > -1) {
                //Bug id
                int edInd = rMsg.indexOf(ChannelData.EDChar, stInd + ChannelData.SDChar.length());
                if (edInd > -1) {
                    if (rMsg.indexOf("DTTM", stInd) > -1) {
                        isDTTMNotSent = true;
                    }
                    temp = rMsg.substring(stInd, edInd + 2);

                    //#if VERBOSELOGGING
                    //|JG|Logger.debugOnError("Message contains channel data - " + temp);
                    //#endif
                }
            }
        }
        return temp;
    }

    public AdData getChannelData(String temp, boolean isSendQueueEmpty) {
        AdData adData = null;
        if (null != temp && temp.length() > 5) {
            temp = temp.substring(2, temp.length() - 2);
            String[] splitData = Utilities.split(temp, ",");
            String cmd = null;
            for (int i = 0; i < splitData.length; i++) {
                try {
                    temp = splitData[i];
                    cmd = temp.substring(0, 4);
                    temp = temp.substring(4);

                    if ("PINT".compareTo(cmd) == 0) {
                        //CR 13248, 14531
                        Settings.serverPoolTime = Integer.parseInt(temp);
                        Settings.poolTime = Settings.serverPoolTime * 1000;
                        ObjectBuilderFactory.GetKernel().ChangeModeOrTimer(false, false);
                    } //<-- CR 13617
                    else if ("PIPE".compareTo(cmd) == 0) {
                        GlobalMemorizeVariable.add(ChannelData.globalMaxDataMode, temp);
                        GlobalMemorizeVariable.add(ChannelData.globalDataMode, temp);
                    } // CR 13617 -->
                    else if ("STOP".compareTo(cmd) == 0) {
                        //CR 12550
                        if (isSendQueueEmpty) {
                            Settings.isAliveState = false;
                            //cr 12550   //ObjectBuilderFactory.GetKernel().ChangeMode(false);
                            ObjectBuilderFactory.GetKernel().displayMessageBox(22, Constants.popupMessage[67],
                                    Constants.headerText[31]);
                        }
                    } else if ("DROP".compareTo(cmd) == 0) {
                        //CR 13395
                        ObjectBuilderFactory.GetKernel().ChangeModeOrTimer(false, true);
                    } else if (0 == "ANEW".compareTo(cmd)) {
                        //#if VERBOSELOGGING
                        //|JG|Logger.loggerError("Found Ad in message");
                        //#endif
                        adData = getNewAd(temp);
                    } //<-CR 13618
                    else if (0 == "ULOC".compareTo(cmd)) {
                        String[] splitedText = Utilities.split(temp, ":");
                        GlobalMemorizeVariable.add(ChannelData.globalUserCity, splitedText[0]);
                        GlobalMemorizeVariable.add(ChannelData.globalUserCountry, splitedText[1]);
                    } // CR 13618 ->
                    //CR 13332 13721
                    else if (0 == "UPGD".compareTo(cmd)) {
                        String[] splitedText = Utilities.split(temp, ":");
                        if (!splitedText[1].toLowerCase().startsWith("http://")) {
                            updateUrl = "http://" + splitedText[1];
                        }
                        versionNumber = splitedText[0];
                    } //CR 13332
                    else if (0 == "SAPP".compareTo(cmd)) {
                        handleAppEnable_Disable(temp);
                    } else if (0 == "PVRQ".compareTo(cmd)) {
                        sendProvenanceAction(true); //CR 8351
                    } else if (0 == "UREQ".compareTo(cmd)) {
                        createUserID(); //CR 12988
                    } else if (0 == "PCNF".compareTo(cmd)) {
                        int index = temp.indexOf(":");
                        if (index > -1) {
                            temp = temp.substring(0, index);
                        }
                        //#if VERBOSELOGGING
                        //|JG|Logger.loggerError("PCNF received. Phone number is:" + temp);
                        //#endif
                        Settings.setPhoneNumber(temp);
                        //CR 12360
                        Settings.setIsPCNF(true);
                        if (null != cData) {
                            ObjectBuilderFactory.getPCanvas().DAPPDownload();
                        }
                    } else if (0 == "UCNF".compareTo(cmd)) { //CR 12988
                        //#if VERBOSELOGGING
                        //|JG|Logger.loggerError("UCNF received. Phone number is:" + temp);
                        //#endif
                        Settings.setUID(temp);
                        //CR 12988
                        Settings.setIsUCNF(true);
                        if (null != cData) {
                            ObjectBuilderFactory.getPCanvas().DAPPDownload();
                        }
                    } else if (0 == "DAPP".compareTo(cmd)) {
                        //#if VERBOSELOGGING
                        //|JG|Logger.loggerError("DAPP received. Downloading app.....");
                        //#endif
                        cData = temp;
                        ObjectBuilderFactory.GetKernel().startHttpProcess(6, cData, null, null);
                        cData = "";
                        ObjectBuilderFactory.getPCanvas().DAPPDownload();
                    } else if (0 == "DRS2".compareTo(cmd)) { //CR 14333
                        ObjectBuilderFactory.GetKernel().updateChatStatus(2, temp, null);
                    } else if (0 == "DRS3".compareTo(cmd)) { //CR 14333
                        ObjectBuilderFactory.GetKernel().updateChatStatus(3, temp, null);
                    } else if (0 == "DRS8".compareTo(cmd)) { //CR 14333
                        ObjectBuilderFactory.GetKernel().updateChatStatus(8, temp, null);
                    } else if (0 == "DRS9".compareTo(cmd)) { //CR 14333
                        ObjectBuilderFactory.GetKernel().updateChatStatus(9, temp, null);
                    } else if(0 == "USST".compareTo(cmd)){ //CR 14423/14441
                        int index = temp.indexOf(":");
                        ObjectBuilderFactory.GetKernel().updateChatStatus(0,temp.substring(index+1),
                                temp.substring(0,index));
                    } else if(0 == "CTRQ".compareTo(cmd)){ //CR 14667
                        sendContacts();
                    } else if(0 == "CTON".compareTo(cmd)){ //CR 14675
                        temp = temp.trim();
                        ObjectBuilderFactory.GetKernel().updateMessagePlusContacts(temp.substring(1,temp.length()-1).trim(),(byte)1);
                    } else if(0 == "CTDL".compareTo(cmd)){//CR 14675
                        temp = temp.trim();
                        ObjectBuilderFactory.GetKernel().updateMessagePlusContacts(temp.substring(1,temp.length()-1).trim(),(byte)0);
                    } else if(0 == "CTUD".compareTo(cmd)){ //CR 14698
                        ObjectBuilderFactory.GetKernel().updateMessagePlusContacts(temp,(byte)2);
                    } else if(0 == "GSDL".compareTo(cmd)) { //CR 14788
                        ObjectBuilderFactory.GetKernel().updateMessagePlusContacts(temp.substring(1,temp.length()-1).trim(),(byte)3);
                    }
                } catch (Exception e) {
                    Logger.loggerError("Chennal Data Manipulation Error" + e.toString() + " Chennal Data " + temp);
                }
            }
        }

        return adData;
    }

    private void handleAppEnable_Disable(String e_DMsg) {
        //#if VERBOSELOGGING
        //|JG|Logger.loggerError("Application must be disabled/enabled");
        //#endif
        char dChar = e_DMsg.charAt(0);
        if (dChar == '0') {
            Settings.setIsAppEnable(true);
        } else {
            Settings.setIsAppEnable(false);
        }
        e_DMsg = e_DMsg.substring(1);
        Settings.setAppE_DMsg(e_DMsg);
        StringBuffer stbuf = new StringBuffer(ChannelData.getServerName()).append(" ").append(ChannelData.SDChar).append("SAPC").append(dChar).append(ChannelData.EDChar);
        e_DMsg = getPacket(stbuf.toString(), false);
        //bug 12209
        ObjectBuilderFactory.GetKernel().sendMessage(getMessageDto(e_DMsg));////CR 11975
        ObjectBuilderFactory.GetKernel().handleApplicationMessage();
    }

    private AdData getNewAd(String temp) {
        AdData adData = new AdData();
        adData.setAdId(temp.substring(0, 3));
        String acItem = temp.substring(3, 6);
        int adType = Integer.parseInt(temp.substring(6, 7));
        adType += 3 - (2 * adType);
        adData.setStyle(Byte.parseByte(adType + ""));
        temp = temp.substring(7);
        String[] lPageInd = Utilities.split(temp, ":");
        byte noAction = 0;
        if (null != lPageInd && lPageInd.length > 0) {
            adData.setAdText(lPageInd[0]);
            int value = Integer.parseInt(acItem, 16);
            int pow = 1;
            boolean[] lPage = new boolean[adData.getLANDPAGE_SIZE() * 8];
            for (int i = 0, j = 1; i < 12; i++) {
                pow = 1;
                for (int k = 1; k <= i; k++) {
                    pow *= 2;
                }
                if ((value & pow) > 0) {
                    lPage[i] = true;
                    noAction += 1;
                    if (1 == i) {
                        adData.setPNo(Utilities.getformatedCallNumber(lPageInd[j++]));
                    } else if (3 == i) {
                        adData.setUrl(lPageInd[j++]);
                    } else if (10 == i) {
                        adData.setCAdText1(Utilities.replace(lPageInd[j++], "%-%", ":"));
                    } else if (11 == i) {
                        adData.setCAdText2(Utilities.replace(lPageInd[j++], "%-%", ":"));
                    } else if (8 == i) {
                        adData.setIsImdAct(true);
                    }
                }
            }
            adData.setLPag(lPage);
            if ((adData.isIsImdAct() && noAction == 1) || noAction == 0) {
                adData.setStyle((byte) 1);
            }
        } else {
            adData = null;
        }
        return adData;
    }

    //CR 14324
    private String getDTTMChannelData() {
        return "DTTM" + Utilities.getCurrentDateYYYYMMDDHHMMFormat();
    }

    public String addPacket(String sCode, String sMsg, boolean isDateTimeSend) {
        if (0 == ChannelData.getShortcode().compareTo(sCode)) {
            //CR 12988
//            if(!Settings.isIsUCNF()  && sMsg.indexOf("UIDA") == -1){
//                createUserID();
//            }

            //CR 14324
            if (isDateTimeSend) {
                isDTTMNotSent = true;
            }

            //CR 12360
            if (!Settings.isIsPCNF() && sMsg.indexOf("PROV") == -1) { //CR 8351
                sendProvenanceAction(false);
            }

            sMsg = getPacket(sMsg, false);
        }
        return sMsg;
    }

    private void closeRecordStore() {
        if (null != rStoreParser) {
            rStoreParser.closeRecordStore();
            rStoreParser = null;
        }
    }

    public void deinitialize() {
        closeRecordStore();
    }
}
