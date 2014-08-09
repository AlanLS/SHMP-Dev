    /*
 * SMSSendReceive.java
 *
 * Created on October 4, 2007, 11:32 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 * @author Hakuna Matata
 * @version 1.0
 * @copyright (c) Sasikumar
 */
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.PushRegistry;
import javax.wireless.messaging.BinaryMessage;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessageListener;
import javax.wireless.messaging.TextMessage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Timer;
import java.util.TimerTask;
import javax.microedition.io.SocketConnection;

/**
 *
 */
public class SMSSendReceive {

    private ReceiveSMSParser receiveSms = new ReceiveSMSParser();
    private Thread msgTherad = null;
    private static Vector msgSendQueue = new Vector();
    private static Vector msgReceiveQueue = new Vector();
    private boolean isNotDead = true;
    private String[] numbers = null;
    private String[] pNames = null;
    private String[] abbreviation = null;
    private String[] originalAbberviation = null;
    private short tnum = 0;
    private String allNumbers = "^";
    private static Message reSend = null;

    private SMSHandler smsHandler = null;
    private DataHandler dataHandler = null;
    private Timer pollTimer = null;
    //CR 13248
//    private int pollTime = 60000;


    public void setMessageListener() {
        smsHandler = new SMSHandler();
        dataHandler = new DataHandler();
    }

    public void deRegisterPushRegistry() {
        smsHandler.deRegisterPushRegister();
    }

    public void addNumber(String num, String name, String mCf, String abbreviat) {
        int tem;

        if (null != num) {
            int len = 0;
            if (allNumbers.indexOf("^" + num + "-" + name + "-" + abbreviat + "^") == -1) {
                if (null == numbers) {
                    numbers = new String[10];
                    pNames = new String[10];
                    abbreviation = new String[10];
                    originalAbberviation = new String[10];
                    tnum++;
                } else {
                    tnum++;
                    if (tnum > numbers.length) {
                        String[] temp = numbers;
                        len = temp.length;
                        numbers = new String[len + 10];
                        System.arraycopy(temp, 0, numbers, 0, len);
                        temp = pNames;
                        pNames = new String[len + 10];
                        System.arraycopy(temp, 0, pNames, 0, len);
                        temp = abbreviation;
                        abbreviation = new String[len + 10];
                        System.arraycopy(temp, 0, abbreviation, 0, len);

                        temp = originalAbberviation;
                        originalAbberviation = new String[len + 10];
                        System.arraycopy(temp, 0, originalAbberviation, 0, len);
                        temp = null;
                    }
                }

                if (ChannelData.indireShortCodeName.compareTo(num.toLowerCase()) == 0) {
                    num = ChannelData.getShortcode();
                }
                allNumbers += num + "-" + name + "-" + abbreviat + "^";
                numbers[tnum - 1] = num;
                pNames[tnum - 1] = name;
                originalAbberviation[tnum - 1] = abbreviat;
                if ((tem = abbreviat.indexOf(")>")) > -1)//CR 11909//(fbc)>
                {
                    abbreviat = abbreviat.substring(1, tem);
                }
                abbreviation[tnum - 1] = abbreviat;//save it as "FBC"
            }
        }
    }

    /**
     * Clear the Widget Name list
     */
    public void clearWidgetArray() {
        tnum = 0;
        numbers = null;
        pNames = null;
        abbreviation = null;
        allNumbers = "^";

    }

    /**
     * Method to remvoe the already store widgetName
     * @param wName remove widget name
     */
    public void removeWidgetName(String wName) {
        for (int i = 0; i < tnum; i++) {
            if (pNames[i].compareTo(wName) == 0) {
                String[] temp = numbers;
                tnum--;
                System.arraycopy(temp, 0, numbers, 0, i);
                System.arraycopy(temp, i + 1, numbers, i, (tnum - i));
                temp = pNames;
                System.arraycopy(temp, 0, pNames, 0, i);
                System.arraycopy(temp, i + 1, pNames, i, (tnum - i));
                temp = abbreviation;
                System.arraycopy(temp, 0, abbreviation, 0, i);
                System.arraycopy(temp, i + 1, abbreviation, i, (tnum - i));

                temp = originalAbberviation;
                System.arraycopy(temp, 0, originalAbberviation, 0, i);
                System.arraycopy(temp, i + 1, originalAbberviation, i, (tnum - i));
                temp = null;
            }
        }
    }

    private void handleReceiveMessage(String[] receivedMessage) {
        Utilities.setCurrentTime();

        int index = -1;
        if (receivedMessage.length > 2) {
            Settings.increaseReceiveMsgcount();
            index = receivedMessage[0].toLowerCase().indexOf("sms://");
            if (index > -1) {
                receivedMessage[0] = receivedMessage[0].substring(6);
            }

            index = receivedMessage[0].indexOf(":");
            if (index > -1) {
                receivedMessage[0] = receivedMessage[0].substring(0, index);
            }

            index = receivedMessage[0].indexOf("-");
            if (index > -1) {
                receivedMessage[0] = receivedMessage[0].substring(index + 1);
            }
        }
        Utilities.updateMessage("Increment Count of Message Received ");
        if (receivedMessage[1].length() > 20) {
            //#if VERBOSELOGGING
            //|JG| Logger.debugOnError("Message reached from server " + receivedMessage[1].substring(0, 20));
            //#endif
        } else {
            //#if VERBOSELOGGING
            //|JG| Logger.debugOnError("Message reached from server " + receivedMessage[1]);
            //#endif
        }

        if (receivedMessage[1].indexOf(Constants.appendText[2]) > -1) {
            index = -1;
            receivedMessage[0] = ChannelData.getShortcode();
        } else {
            Utilities.setCurrentTime();
            index = findBestpNameIndex(receivedMessage[1]);
            Utilities.updateMessage("Identify Message Sender in Local Memory ");
        }


        String mCf = "(|/|)";
        String abbreviat = null;
        String sFullName = Constants.aName;
        if (index > -1) {
            receivedMessage[0] = numbers[index];
            sFullName = pNames[index];
            abbreviat = abbreviation[index];
        }

//        if(receivedMessage[1].indexOf("!*")>-1){
//            receivedMessage[1] = "!*USST911234567890:Online*!";
//        }

        //#if VERBOSELOGGING
        //|JG|Logger.debugOnError("Received Message Association Started");
        //#endif
        Message messageDto = receiveSms.receiveMessage(receivedMessage[0], abbreviat, receivedMessage[1], mCf, sFullName);
        //#if VERBOSELOGGING
        //|JG|Logger.debugOnError("Received Message Association End");
        //#endif
        if (messageDto != null) {

            if (messageDto.getCurRMsg().indexOf(Constants.appendText[2]) > -1) {
                messageDto.setSFullName(Constants.appendText[2]);
            } else if (Constants.aName.compareTo(messageDto.getSFullName()) == 0) {
                index = findAppName(messageDto.getCurRMsg());
                if (index > -1) {
                    messageDto.setSFullName(pNames[index]);
                } else {
                    messageDto.setSFullName(Constants.aName);
                }
            }
            if(msgSendQueue.size()>0)
                messageDto.setIsSendQueueEmpty(false);
            ObjectBuilderFactory.GetKernel().messageReceived(0, messageDto, true);
            Logger.debugOnError("Logging for Receive, association and Display " + Utilities.getLogginMessage());
            Utilities.resetMessage();

        } else {
            Logger.debugOnError("Logging for Received and not associated message " + Utilities.getLogginMessage());
            Utilities.resetMessage();
            Logger.debugOnError("Received Message Not associated");
            if (receivedMessage[1].length() > 18) {
                receivedMessage[1] = receivedMessage[1].substring(0, 18);
            }
            if (receivedMessage[1].charAt(0) == '(') {
                if ((index = receivedMessage[1].indexOf(')', 1)) > -1) {
                    int sep = -1;
                    String[] newMsg = new String[2];
                    if ((sep = receivedMessage[1].substring(0, index).indexOf('/')) > -1) {
                        if (receivedMessage[1].substring(sep + 1, index).length() > 1) {
                            newMsg[0] = receivedMessage[1].substring(index - 1, index);
                            newMsg[1] = receivedMessage[1].substring(sep + 1, index - 1);
                            ObjectBuilderFactory.GetKernel().multiPartMessage(newMsg, null);
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @param msg
     * @return
     */
    private int findBestpNameIndex(String msg) {
        byte index = -1;
        if (null != abbreviation && null != msg) {
            index = getChatAppIndex(msg);
            if (index > -1) {
                return index;
            }
            msg = msg.trim().toLowerCase();
            int len = getFindPosition(msg);
            if (len > 0) {
                if (msg.indexOf("(1/") != 0) //Bug Id 10956
                {
                    return index;
                }
                msg = msg.substring(len);
            }
            len = 0;
            for (byte i = 0; i < tnum; i++) {
                if (msg.toLowerCase().startsWith(abbreviation[i].toLowerCase()) && (len < abbreviation[i].length())) {
                    len = abbreviation[i].length();
                    index = i;
                }
            }
        }
        return index;
    }

    private byte getChatAppIndex(String msg) {
        if (msg.indexOf(">\n") < 7) {
            for (byte i = 0; i < tnum; i++) {
                if (originalAbberviation[i].indexOf(")>") > -1 && msg.toLowerCase().startsWith(abbreviation[i].toLowerCase())) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     *
     * @param msg
     * @return
     */
    private int findAppName(String msg) {
        int index = -1;
        int len = 0;
        msg = msg.trim().toLowerCase();
        for (byte i = 0; i < tnum; i++) {
            if (msg.toLowerCase().startsWith(abbreviation[i].toLowerCase()) && (len < abbreviation[i].length())) {
                len = abbreviation[i].length();
                index = i;
            }
        }
        return index;
    }

    /**
     *
     * @param msg
     * @return
     */
    private int getFindPosition(String msg) {
        int index = -1;
        if (msg.charAt(0) == '(') {
            index = msg.indexOf(" ");
            if (index > -1) {
                index = msg.substring(0, index).indexOf(")");
            } else {
                index = msg.indexOf(")");
            }
            if (index > -1 && msg.substring(0, index).indexOf("/") > -1) {
                index++;
            }
        }
        return index;
    }

    public void startSocketReader(){
        dataHandler.reScheduleReaderTimer();
    }

    public void receiveMessage(String msg, String sFullName, String sNo) {

        PushDataIntoReceiveQueue(msg.getBytes(),sNo,false,false);

//        Settings.increaseReceiveMsgcount();
//        sNo = "+91" + sNo;
//        int index = findBestpNameIndex(msg);
//        String mCf = "(|/|)";
//        String sName = null;
//        if (index > -1) {
//            sNo = numbers[index];
//            mCf = pNames[index];
//            sName = abbreviation[index];
//        }
//        synchronized (receiveSms) {
//            Message messageDto = receiveSms.receiveMessage(sNo, sName, msg, "(|/|)", mCf);
//
//            if (messageDto != null) {
//                if (messageDto.getRMsg()[0].indexOf(Constants.appendText[2]) > -1) {
//                    messageDto.setSFullName(Constants.appendText[2]);
//                } else if (Constants.aName.compareTo(messageDto.getSFullName()) == 0) {
//                    index = findAppName(messageDto.getRMsg()[0]);
//                    if (index > -1) {
//                        messageDto.setSFullName(pNames[index]);
//                    } else {
//                        messageDto.setSFullName(Constants.aName);
//                    }
//                }
//                ObjectBuilderFactory.GetKernel().messageReceived(0, messageDto, true);
//            }
//
//        }
    }

    public void pendingMsgSnd() {
        if(null != dataHandler){
            dataHandler.resumeConnection();
        }
        //stopTimer();
//            if(messageQueue.size()>0){
//                smsThread = new Timer();
//                smsThread.schedule(new TimerForMessageSend(), 0);
//            }
    }

    /**
     *
     * @param senderName
     * @param shortcode
     * @param message
     * @param queryType
     * @param matchWords
     * @param misMatchWords
     * @return
     *
     */
    public String[] populateSendMessageDto(Message senderBucket) {
        String[] returnVales = new String[]{null, null};
        try {
            //#if VERBOSELOGGING
            //|JG|Logger.debugOnError("Send Message data population started");
            //#endif
            String mscf = senderBucket.getMscf();
            String abbreviat = senderBucket.getAbberVation();
            String message = senderBucket.getRMsg()[0];

            int index = -1;//CR 11909
            if ((index = abbreviat.indexOf(")>")) > -1) {
                senderBucket.setSenderName(abbreviat.substring(1, index));
            } else {
                senderBucket.setSenderName(abbreviat);
            }
            senderBucket.setPopulatedTime(new Date().getTime()); //bug 10255
            returnVales[1] = senderBucket.getPopulatedTime() + "";
            //CR 9546
            //CR 11909 Changed Chat indetification logic

            if (null == senderBucket.getChatId() && senderBucket.isIsSendQueueEmpty()) {
                //CR Number 6645
                if (ChannelData.getServerName().length() > 0 && message.toLowerCase().startsWith(ChannelData.getServerName().toLowerCase())
                        || ChannelData.isWaterMark() && UISettings.SHORTHAND_WATER_MARK.length() > 0 && message.toLowerCase().startsWith(UISettings.SHORTHAND_WATER_MARK.toLowerCase())) {
                    mscf = Settings.getSequenceNumber();
                    senderBucket.setMscf(mscf);
                    if (ChannelData.isWaterMark() && message.toLowerCase().startsWith(UISettings.SHORTHAND_WATER_MARK.toLowerCase())) { // CR 8144
                        index = -1;
                        if ((index = message.indexOf(ChannelData.SDChar)) > -1) {
                            if (message.indexOf(ChannelData.EDChar) > index) {
                                message = message.substring(0, index) + " s" + mscf + Utilities.remove(message.substring(index), " ");
                            } else {
                                message += " s" + mscf;
                            }
                        } else {
                            message += " s" + mscf;
                        }
                    } else {
                        message += " s" + mscf;
                    }
                }
            }

            //#if VERBOSELOGGING
            //|JG|Logger.debugOnError("Send Messaeg data population End");
            //#endif
            //CR 14410
            senderBucket.setRMsg(new String[]{message});

            synchronized (msgSendQueue) {
                msgSendQueue.addElement(senderBucket);
            }

            if (null == msgTherad) {
                msgTherad = new Thread(new TimerForMessageSend());
                msgTherad.start();
            }

            returnVales[0] = mscf;
        } catch (Exception e) {
            Logger.loggerError("Sms send bucket Create " + e.toString());
        }
        return returnVales;
    }

    public String ConvertASCIIValue(String message){
        //#if VERBOSELOGGING
        //|JG|Logger.debugOnError("Message before Replace method " + message);
        //#endif
        message = Utilities.ConvertASCIIValue(message);//CR 11034
        //#if VERBOSELOGGING
        //|JG|Logger.debugOnError("Message after Replace method " + message);
        //#endif

        if (ChannelData.isSymbolConvert()) { //CR 7255
            message = Utilities.replace(message, "@", "?at?");
            message = Utilities.replace(message, "&", "?an?");
            message = Utilities.replace(message, "#", "?hh?");
            message = Utilities.replace(message, "*", "?sr?");
            message = Utilities.replace(message, "_", "?ue?");
            message = Utilities.replace(message, "$", "?dr?");
        } else if (UISettings.isConverSymbol) { //bug 6659
            message = Utilities.replace(message, "@", "?at?");
        }

        // if (UISettings.isUTF8Convert) // Samsung F480 sms send issue
        {
            try{
                message = new String(message.getBytes("UTF-8"), "UTF-8"); //CR Number Karbonn K1414
            }catch(Exception exception){
                Logger.loggerError("UTF-8 Convertion error "+exception.toString());
            }
            //#if VERBOSELOGGING
            //|JG|Logger.debugOnError("Message after UTF8 Conversion " + message);
            //#endif
        }
        return message;
    }

    //CR 11975
    //ConnectionSatus 1 = HTTP_ok
    //                2 = SecurityException
    //                3 = Other exception
    //                4 = Empty message
    //<!-- CR 11975 -->
    public void reSendMessage() {
        if(null != reSend){
            if(null != reSend.getCurRMsg())
                reSend.setRMsg(new String[]{reSend.getTempConFormat()});
            synchronized (msgSendQueue) {
                msgSendQueue.addElement(reSend);
            }
            reSend = null;
            if (null == msgTherad) {
                msgTherad = new Thread(new TimerForMessageSend());
                msgTherad.start();
            }
        }
    }

    public void setPOLLMessage(Message message){
        reSend = message;
    }

    private Message getSendFaildMessage(Message temp) {
        int index = temp.getRMsg()[0].indexOf(" s" + temp.getMscf());
        if (index > -1) {
            temp.setRMsg(new String[]{temp.getRMsg()[0].substring(0, index)});
        }
        return temp;
    }

    private void PushDataIntoReceiveQueue(byte[] receivedByte, String number, boolean isSMS, boolean isSocket) {
        try{
        String temp = new String(receivedByte, "UTF-8");
        if (temp.trim().length() > 0) {
//            temp += "<img=12345;Testing Image>";
//            temp += "!*CTON(918754415793\n)*!";
//            temp += "!*CTDL(Du:+918754415793:+:00:*)\n(HMS:918754415794:+: :*)\n(HMS2:918754415795:+: : )" +
//                    "\n(HMS7:918754415796: : : )\n(HMS4:918754415797: :00:*)\n(HMS3:918754415798: : :*)" +
//                    "\n(HMS6:918754415778:+:00: )\n(HMS5:918754415799: :00: )*!";
//            temp += "!*USST918754415793:OnlineFriends*!";
//            temp += "!*GSDL(sasikumar:G000000001:00:*:9841922425)\n(sasikumar1:S000000002:00:*:9841922426)" +
//                    "\n(sasikumar2:G000000002: :*:9841922427)\n(sasikumar3:S000000001: :*:9841922428)\n" +
//                    "(sasikumar4:G000000001: : :9841922429)\n(sasikumar5:S000000001: : :9841922430)*!";
            //CR<-0012014->
            if(!isSMS)
                Settings.increaseReceiveRequestcount(receivedByte.length);
            //CR<-0012014->
            Logger.debugOnError("Succesfully retrieved Number of byte ="+receivedByte.length+" Message = " + temp);
            System.out.println(temp);
            synchronized (msgReceiveQueue) {
                //CR 13238
                String[] receivedMessage = Utilities.split(temp, "!-!");
                int count = receivedMessage.length;
                String[] parsedMessage = null;
                for (int i = 0; i < count; i++) {
                    if (isSMS) {
                        parsedMessage = new String[]{number, receivedMessage[i], null};
                    } else {
                        parsedMessage = new String[]{number, receivedMessage[i]};
                    }
                    msgReceiveQueue.addElement(parsedMessage);
                }

                //CR 14660
                if(isSocket){
                    Message message = new Message();
                    message.setShortcode(ChannelData.getShortcode());
                    message.setSFullName(Constants.aName);
                    message.setIsSendQueueEmpty(false);
                    message.setInboxFunc(true);
                    message.setIsNotChatMessage(false);
                    message.setRtDelay(-1);
                    message.setAbberVation(Constants.appName);
                    message.setRMsg(new String[]{ChannelData.getServerName()+" !*ACKN*!"});
                    msgSendQueue.insertElementAt(message,0);
                }

                parsedMessage = null;
                receivedMessage = null;

                //#if VERBOSELOGGING
                //|JG|    Logger.debugOnError("Added message to Received Queue");
                //#endif
            }

            if (null == msgTherad) {
                msgTherad = new Thread(new TimerForMessageSend());
                msgTherad.start();
                //#if VERBOSELOGGING
                //|JG|    Logger.debugOnError("Started new thread for handling received message(Shorthand Invoked)");
                //#endif
            }
        } else Logger.debugOnError("Empty Data Received ");
        }catch(Exception e){
            Logger.loggerError("SMSSendReceive->PushDataIntoReceiveQueue->"+e.toString());
        }
    }

    public void changeMode(boolean isReset){
        if(null != dataHandler){
            if(isReset)
                reSchedulePollTimer(false,0);
            dataHandler.deinitialize(false);
        }
    }

    /**
     *
     *
     *
     */
    public boolean deinitialize() {
        boolean isUnloaded = true;
        isNotDead = false;
        msgTherad = null;
        //bug 12298

        reSchedulePollTimer(false,0);

        if(null != dataHandler){
            synchronized(dataHandler){
                dataHandler.unload();
                dataHandler = null;
            }
        }

        if(null != smsHandler){
            synchronized(smsHandler){
                smsHandler.deinitialize();
                smsHandler = null;
            }
        }

        if (null != receiveSms) {
            synchronized (receiveSms) {
                receiveSms.deInitialize();
                receiveSms = null;
            }
        }
        //#if VERBOSELOGGING
        //|JG|Logger.loggerError("Receiver Closed");
        //#endif
        pNames = null;
        abbreviation = null;
        numbers = null;
        allNumbers = "^";
        return isUnloaded;
    }

    class TimerForMessageSend implements Runnable {
        public void run() {
            Message thisMessage = null;
            String[] receiveMessage = null;
            while (isNotDead && (msgSendQueue.size() > 0 || msgReceiveQueue.size() > 0)) {
                while (isNotDead && msgReceiveQueue.size() > 0) {
                    synchronized (msgReceiveQueue) {
                        if (msgReceiveQueue.size() > 0) {
                            receiveMessage = (String[]) msgReceiveQueue.firstElement();
                            msgReceiveQueue.removeElementAt(0);
                        }
                    }
                    handleReceiveMessage(receiveMessage);
                }
                thisMessage = null;
                if (isNotDead && msgSendQueue.size() > 0) {
                    synchronized (msgSendQueue) {
                        thisMessage = (Message) msgSendQueue.firstElement();
                        while(msgSendQueue.size()>1 && thisMessage.getRMsg()[0].compareTo(
                                ChannelData.getServerName() +" !*POLL*!") == 0)
                        {
                            msgSendQueue.removeElementAt(0);
                            thisMessage = (Message) msgSendQueue.firstElement();
                        }
                        msgSendQueue.removeElementAt(0);
                    }
                    sendMessage(thisMessage);
                }
            }
            if (!isNotDead) {
                ObjectBuilderFactory.GetKernel().unLoad();
            } else {
                //CR 14531
                if(null != receiveSms){
                    if(receiveSms.isWaitingForResponse()){
                        Settings.poolTime = 2 * 1000;
                    } else {
                        Settings.poolTime = Settings.serverPoolTime * 1000;
                    }
                    reSchedulePollTimer(false, 1);
                }
            }
            msgTherad = null;
        }
    }

    private void sendMessage(Message thisMessage) {
        if (null != thisMessage) {
            //CR 12548
            Settings.isAliveState = true;
            //CR 11975
            //CR 12988
            //CR 13219
            //cr 13397
            try {
                if (!thisMessage.isIsDSend()) {
                    if (Settings.isIsGPRS() && thisMessage.getInboxFunc() &&
                        thisMessage.getShortcode().equals(ChannelData.getShortcode())) {
                        dataHandler.sendMessageOnData(thisMessage);
                    } else {
                        smsHandler.sendMessageOnSMS(thisMessage);
                    }
                } else {
                    //#if VERBOSELOGGING
                    //|JG|Logger.debugOnError("App Property Set as Do Not Send Message");
                    //#endif
                }
                pushDataIntoSendBucket(thisMessage);
            } catch (CustomException exception) {
                Logger.debugOnError("Send Request Exception "+exception.toString());
                reSend = thisMessage;
                reSend.setTempConFormat(reSend.getRMsg()[0]);
                //CR 14250
                if(thisMessage.getRMsg()[0].compareTo(ChannelData.getServerName()+" !*POLL*!") != 0){
                    showErrorMessage(exception, thisMessage);
                }
                else {
                    Settings.isAliveState = true;
                    reSchedulePollTimer(true, 0);
                }
            }
            thisMessage = null;
        }
    }

    private void pushDataIntoSendBucket(Message sendMessage) {
        Logger.debugOnError("Message before encode=" + sendMessage.getRMsg()[0]);
        //bug No 12670
//        String id = +null;
        if (null != sendMessage.getQueryType() && !sendMessage.isIsDWRes()) {
            byte[] rbyte = receiveSms.getBucketData(sendMessage);
            receiveSms.addBucket(rbyte, sendMessage.getSenderName(), null);
        }
//        return id;
    }

    private void showErrorMessage(CustomException exception, Message faildMessage){
        //SMS 1,2,3
        //Http 4,5,6, 8
        //Socket 7, 8
        //8 Reconnect Popup
        Logger.loggerError(exception.getErrorMessage());

        if(exception.getCode()>3){
           if(exception.getCode() == 5){ //http
               faildMessage.setErrorMessage(Constants.popupMessage[52]); //BUG CR 0012488
           } else if(exception.getCode() == 6){ //http
               faildMessage.setErrorMessage("HTTP Failure "+exception.getErrorMessage());
           }
        }

        ObjectBuilderFactory.GetKernel().messageReceived(exception.getCode(),
                   getSendFaildMessage(faildMessage), true);
    }

    public boolean isNotSocketMode(){
        if(1 == dataHandler.dataMode)
            return false;
        return true;
    }

    //CR 13248
    //CR 13346
    public void reSchedulePollTimer(boolean isNotStart,int pint){
        if(null != pollTimer){
            System.out.println("Poll timer stoped");
            pollTimer.cancel();
            pollTimer = null;
            pint += 1;
//            Logger.debugOnError("Pool Timer Stoped");
        }
        if(isNotStart || pint == 2){
            if(Settings.isAliveState){ //CR 12548
                System.out.println("Poll timer start "+Settings.poolTime);
                pollTimer = new Timer();
                pollTimer.schedule(new PollMessage(), Settings.poolTime);
//                Logger.debugOnError("Pool Timer Started With Interval : "+Settings.poolTime);
            } else{
//                Logger.debugOnError("Client user state is Stopped ");
            }
        }
    }

    private class PollMessage extends TimerTask{
        public void run() {
            //CR 13248
            if(Settings.isAliveState && Settings.isIsGPRS()){

                ObjectBuilderFactory.GetKernel().sendReconnectOrPollMessage("!*POLL*!",true);
//                Logger.debugOnError("Poll added to the Queue : "+ Settings.poolTime);
            } else reSchedulePollTimer(false,0);
        }
    }




    private class SMSHandler implements MessageListener {

        private MessageConnection msgConnection = null;
        private String smsPort = null;

        public SMSHandler() {
            smsPort = "sms://:" + ChannelData.getPortNumber();
            registerMessageListener();
        }

        private void registerMessageListener() {
            try {
                //#if VERBOSELOGGING
                //|JG|Logger.debugOnError("Receive Message Connection opening");
                //#endif
                msgConnection = (MessageConnection) Connector.open(smsPort);
                msgConnection.setMessageListener(this);
                //#if VERBOSELOGGING
                //|JG|Logger.debugOnError("Receive Message Connection opened");
                //#endif
            } catch (Exception e) {
                Logger.loggerError("SMSSendReceive-> addHandler " + e.toString());
            }
        }

        public void notifyIncomingMessage(MessageConnection messageConnection) {
            Logger.debugOnError("Message receiver thread invoked");
            if (msgConnection == messageConnection) {
                pulledNotifyMessage();
            } else {
                //#if VERBOSELOGGING
                //|JG|Logger.debugOnError("Invoked Connection is Not same as our receiver connection");
                //#endif
            }

            //#if VERBOSELOGGING
            //|JG|Logger.debugOnError("Message Receive Thread invoked by OS completed");
            //#endif
        }

        private void pulledNotifyMessage() {
            try {
                //#if VERBOSELOGGING
                //|JG|Logger.debugOnError("Invoking JSR 120/205 message pull API");
                //#endif
                Utilities.resetMessage();
                Utilities.setCurrentTime();
                javax.wireless.messaging.Message msg = msgConnection.receive();
                Utilities.updateMessage("JavaAPI connection getmsg time = ");
                //#if VERBOSELOGGING
                //|JG|Logger.debugOnError("JSR 120/205 message pull API returned success");
                //#endif

                String msgReceived = null;
                String addr = null;
                // Process the received message
                if (msg instanceof TextMessage) {
                    TextMessage tmsg = (TextMessage) msg;
                    Utilities.setCurrentTime();
                    msgReceived = tmsg.getPayloadText();
                    Utilities.updateMessage("JavaAPI getPayloadTxtTime = ");
                    Logger.debugOnError("Pulled out payload text: " + msgReceived);
                    addr = tmsg.getAddress();

                } else {
                    StringBuffer stringbuffer = new StringBuffer();
                    Utilities.setCurrentTime();
                    byte[] abyte0 = ((BinaryMessage) msg).getPayloadData();
                    for (int i = 0; i < abyte0.length; i++) {
                        int j = abyte0[i] & 0xff;
                        stringbuffer.append((char) j);
                    }
                    msgReceived = stringbuffer.toString();
                    Utilities.updateMessage("JavaAPI getPayloadTxtTime = ");
                    addr = msg.getAddress();
                }
                PushDataIntoReceiveQueue(msgReceived.getBytes(), addr, true,false);
            } catch (Exception e) {
                Logger.loggerError("SMSSendReceive MSG Error" + e.toString());
            }
        }

        /**
         *
         **/
        public void sendMessageOnSMS(Message aMessage) throws CustomException {
            if (null != msgConnection) {
                try{
                    if (!aMessage.isIsDSend()) {
                        String smsString = "sms://" + aMessage.getShortcode();

                        //#if VERBOSELOGGING
                        //|JG|Logger.debugOnError("Sms String=" + smsString);
                        //#endif

                        TextMessage tmsg = (TextMessage) msgConnection.newMessage(MessageConnection.TEXT_MESSAGE);
                        //#if VERBOSELOGGING
                        //|JG|Logger.debugOnError("Message=" + tmsg);
                        //#endif
                        tmsg.setAddress(smsString);
                        //CR 14410
                        tmsg.setPayloadText(ConvertASCIIValue(aMessage.getRMsg()[0]));
                        //#if VERBOSELOGGING
                        //|JG|Logger.debugOnError("Trying to send message :" + aMessage.getRMsg()[0] + "to number:" + aMessage.getShortcode());
                        //#endif
                        msgConnection.send(tmsg);
                        tmsg = null;
                        Logger.debugOnError("Sent Message App Name = " + 
                                aMessage.getSenderName() + " Shortcode = " + aMessage.getShortcode() +
                                " QueryType = " + aMessage.getQueryType() +
                                " sent Message = " + aMessage.getRMsg()[0] + " SequenceNumber = s"
                                + aMessage.getMscf());
                        ObjectBuilderFactory.GetKernel().messageReceived(3, getSendFaildMessage(aMessage), true); //CR 10203
                        Settings.increaseSendMsgCount();
                    }
                }catch(SecurityException securityException){
                    throw new CustomException(2, "Send Message Exception:Security exception " +
                            securityException.toString() + " " + securityException.getMessage());
                } catch(Exception exception){
                    throw new CustomException(1, "Send Message Exception: Other exceptions " + exception.toString() + " " + exception.getMessage());
                }
            } else {
                throw new CustomException(2, "User Not Allowed to send and receive sms through aplication");
            }
            aMessage = null;
        }

        private void unRegisterMessageListener() {
            try {
                if (null != msgConnection) {
                    synchronized (msgConnection) {
                        msgConnection.setMessageListener(null);
                        msgConnection.close();
                        msgConnection = null;
                        //#if VERBOSELOGGING
                        //|JG|Logger.loggerError("Message Listener Closed");
                        //#endif
                    }
                }
                //smsPort = null;
            } catch (Exception e) {
                Logger.loggerError("Port Close Error " + e.toString());
            }
        }

        public void deRegisterPushRegister() {
            try {
                if (UISettings.isRegistryNotRemove) {
                    String connectionString = "sms://:" + smsPort;
                    String mName = PushRegistry.getMIDlet(connectionString);
                    if (null == mName) {
                        PushRegistry.unregisterConnection(mName);
                    }
                }
            } catch (Exception e) {
            }
        }

        public void deinitialize() {
            unRegisterMessageListener();
            smsPort = null;
        }
    }

    private class DataHandler {

        private HttpHandler httpHandler = null;
        private SocketHandler socketHandler = null;
        public byte dataMode = 0;

        public DataHandler() {
            socketHandler = new SocketHandler();
            httpHandler = new HttpHandler();
        }

        private void reScheduleReaderTimer(){
            if(null != socketHandler && dataMode == 1){
                socketHandler.reScheduleReaderTimer(true);
            }
        }

        public void resumeConnection(){
            try{
                opendConnection();
            }catch(Exception e){}
        }

        private void opendConnection() throws CustomException {
            try {
                //CR 13237
                //CR 13618
             //CR 13237
                //CR 13618
                if(null == Settings.getUID(false) && (null == GlobalMemorizeVariable.globalDataMode ||
                        GlobalMemorizeVariable.globalDataMode.compareTo("Http") != 0)){
                    GlobalMemorizeVariable.add(ChannelData.globalDataMode, "Http");
                }

                if(GlobalMemorizeVariable.globalDataMode.compareTo("Http") == 0){
                    //< bug 13711
                     dataMode = 2;   // bug 13711>
                    if(null != socketHandler){
                        socketHandler.closeSocket();
                    }
                } else if(GlobalMemorizeVariable.globalDataMode.compareTo("Socket") == 0){
                    //< bug 13711
                       dataMode = 1; // bug 13711>
                    if(!socketHandler.isSocketOpen){
                        socketHandler.OpenConnection();
                    }

                }
            } catch(SecurityException securityException) {
                // Sam A767 13725.
                 if(dataMode  != 2){
                    GlobalMemorizeVariable.add(ChannelData.globalDataMode, "Http");
                }
                dataMode = 2;
                //Sam A767 13725
                Logger.loggerError("Socket open exception: Security exception " + securityException.toString() + " " + securityException.getMessage());
            } catch(Exception e){
                if(dataMode  != 2){
                    GlobalMemorizeVariable.add(ChannelData.globalDataMode, "Http");
                }
                dataMode = 2;
//                GlobalMemorizeVariable.sessionDataMode = "Http";
                Logger.loggerError("Socket Connection Not Open "+e.toString());
            }
        }

        private void sendMessageOnData(Message sendMessage) throws CustomException {
            opendConnection();
            int securityCode = 0;
            try{
                if (dataMode == 1 && socketHandler.isSocketOpen) {
                    securityCode = 2;
                    socketHandler.SendMessageOnSocket(sendMessage);
                } else {
                    securityCode = 4;
                    httpHandler.sendMessageOnHttp(sendMessage);
                }
            } catch(CustomException customException){
                Logger.loggerError("SMSSendReceive->sendMessageOnData->"+customException.getErrorMessage());
                //CR 14052
                if(customException.getCode() != securityCode && sendMessage.isIsSMSSEND()){
                    //Bug 14139
                    if(null != sendMessage.getChatId()){
                        sendMessage.setRMsg(new String[]{getTrimedChatMessage(sendMessage.getRMsg()[0])});
                    }
                    smsHandler.sendMessageOnSMS(sendMessage);
                    if(null == sendMessage.getChatId()){
                        Settings.setIsGPRS(false);
                    }
                } else {
                    throw  customException;
                }
            }
        }

        //CR 14052
        private String getTrimedChatMessage(String chatMessage){
            int index = chatMessage.indexOf("!*");
            int sIndex = chatMessage.indexOf("*!");
            String channeldata = "";
            if(index<sIndex){
                channeldata = chatMessage.substring(index+2);
                chatMessage = chatMessage.substring(0,index);
            }
            if(chatMessage.length()>152){
                chatMessage = chatMessage.substring(0,152) +"!*DERR*!";
            } else if(chatMessage.length() == 152){
                chatMessage += "!*DERR*!";
            } else {
                int previousIndex = 0;
                if(channeldata.length() >0){
                    index = 153 - chatMessage.length();
                    sIndex = 0;
                    while((sIndex=channeldata.indexOf(","))>(sIndex+1)){
                        if(sIndex>index){
                            sIndex =-1;
                            index = -1;
                            break;
                        }
                        previousIndex = sIndex;
                    }
                }
                if(previousIndex>0) {
                    chatMessage += channeldata.substring(0,previousIndex)+",DERR*!";
                } else {
                    chatMessage += "!*DERR*!";
                }
            }
            return chatMessage;
        }

        private void deinitialize(boolean isSHExit) {
            try {
//                GlobalMemorizeVariable.removeValue(ChannelData.globalDataMode);
                if (null != httpHandler) {
                    httpHandler.deinitialize();
                }
                if (null != socketHandler) {
                    socketHandler.deinitialize(isSHExit);
                }
            } catch (Exception exception) { }
            dataMode = 0;
        }

        private void unload(){
            deinitialize(true);
            httpHandler = null;
            socketHandler = null;
        }
    }


    private class HttpHandler {

        private HttpConnection hConnection = null;
        private DataInputStream dataInputStream = null;



        private void openRequestConnection(String url, boolean  isPoll, String uploadData) throws CustomException {

            byte noPoll = 5;
            if(isPoll)
                noPoll = 8;
            try {
                deinitialize();
            }catch(Exception e){}
            try {
                //CR 12062
                //#if VERBOSELOGGING
                //|JG|Logger.debugOnError("Invoking URL-" + url);
                //#endif

//                if(null != uploadData){
                    hConnection = (HttpConnection) Connector.open(url,Connector.READ_WRITE,true);

//                } else {
//                    hConnection = (HttpConnection) Connector.open(url,Connector.READ,true);
//                }
                Logger.debugOnError("Invoked -" + url);

                if (null != hConnection) {
                    //#if VERBOSELOGGING
                    //|JG|Logger.debugOnError("Data Request Connection Opened");
                    //#endif

                    hConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset=\"utf-8\"");
                    hConnection.setRequestProperty("User-Agent", "Profile/MIDP-2.0 Configuration/CLDC-1.1");
                    hConnection.setRequestProperty("Connection","keep-alive");
                    hConnection.setRequestProperty("Accept-Charset", "utf-8");
                    hConnection.setRequestMethod(HttpConnection.POST);

//                    hConnection.setRequestProperty("User-Agent", "Profile/MIDP-2.0 Configuration/CLDC-1.1");
//                    hConnection.setRequestProperty("Connection", "keep-alive");
//                    hConnection.setRequestProperty("Content-Type", "text/vnd.wap.wml");
//                    hConnection.setRequestProperty("wap-connection", "Stack-Type=HTTP");
//                    hConnection.setRequestProperty("Accept", "application/octet-stream");

                    System.out.println(url+" \n"+uploadData);
                    //CR 14035
//                    if(null != values){
                    byte[] values = uploadData.getBytes();

                    hConnection.setRequestProperty("Content-Length", Integer.toString(values.length));
                    DataOutputStream dout = hConnection.openDataOutputStream();
                    dout.write(values);
                    dout.flush();
                    dout.close();
                    dout = null;
//                    } else {
//                    //#if VERBOSELOGGING
//                    //|JG|Logger.debugOnError("Data Request Connection code checking");
//                    //#endif
//                        int code = hConnection.getResponseCode();
//                        if (code != HttpConnection.HTTP_OK) {
//                            Logger.debugOnError("Response received = " + code + " " + hConnection.getResponseMessage());
//                            if (code == 408) {
//                                throw new CustomException(6,"Server timed out");
//                            } else {
//                                throw new CustomException(6,"Server not available, Please try again later");
//                            }
//                        }
//                    }
                } else {
                    noPoll = 2;
                    throw new CustomException(2,"Http Connection not opened");
                }
            } catch (SecurityException securityException) {
                Logger.loggerError("SMSSendReceive->HttpHandler->OpenRequestConnection->Security"+securityException.toString());
                noPoll = 4;
                throw new CustomException(4,"Data Request Message Exception: Security exception " + securityException.toString() + " " + securityException.getMessage());
            } catch (Exception exception) {
                Logger.loggerError("SMSSendReceive->HttpHandler->OpenRequestConnection->"+exception.toString());
                throw new CustomException(noPoll, "Data Request Message Exception: Other exceptions " + exception.toString() + " " + exception.getMessage());
            }
        }

        //CR 11975
        private void sendMessageOnHttp(Message aMessage) throws CustomException {
            //#if VERBOSELOGGING
            //|JG|Logger.debugOnError("Sms String=" + aMessage.getShortcode());
            //|JG|Logger.debugOnError("Trying to request DATA message :" + aMessage.getRMsg()[0] + "to number:" + aMessage.getShortcode());
            //#endif

            boolean isPoll = false;
            
            //CR 14035
            //bug 14419
            String postData = "SenderUID=" + Settings.getUID(true) + "&msg=";
            try {
                postData +=  new String(aMessage.getRMsg()[0].getBytes("utf-8"));
            } catch(Exception exception){
                postData += aMessage.getRMsg()[0];
            }

            //CR 12988
            String requestUrl = ChannelData.getDataRequestUrl();
            openRequestConnection(requestUrl,isPoll,postData);
            if (null != hConnection) {
                Logger.debugOnError("Sent DATA Request App Name = " + aMessage.getSenderName()
                        + " Shortcode = " + aMessage.getShortcode()
                        + " QueryType = " + aMessage.getQueryType()
                        + " sent Message = " + aMessage.getRMsg()[0]
                        + " SequenceNumber = s" + aMessage.getMscf());
                //CR 0012014
                Settings.increaseSentRequestCount(aMessage.getRMsg()[0].getBytes().length);
                ObjectBuilderFactory.GetKernel().messageReceived(3, getSendFaildMessage(aMessage), true); //CR 10203
                pushDataIntoReceiveQueue(aMessage.getShortcode());
                deinitialize();
                reSchedulePollTimer(true,0);
            }
            aMessage = null;
        }

        private void pushDataIntoReceiveQueue(String number) {
            byte[] rByte = null;
            try {
                int len = 0;
                len = (int) hConnection.getLength();
                dataInputStream = hConnection.openDataInputStream();
                if (len <= 0) {
                    //#if VERBOSELOGGING
                    //|JG|Logger.loggerError("Required URl connection length is Zero");
                    //#endif
                    int ch;
                    ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                    while ((ch = dataInputStream.read()) != -1) {
                        byteArray.write(ch);
                    }
                    rByte = byteArray.toByteArray();
                } else {
                    Logger.debugOnError("Data Length1 " + len);
                    rByte = new byte[len];
                    dataInputStream.readFully(rByte);
                }
                PushDataIntoReceiveQueue(rByte, number, false,false);
            } catch (Exception e) {
                Logger.loggerError("Request Data retrive Error " + e.toString());
            }
            rByte = null;
        }

        //CR 11975
        private void deinitializeStream() throws Exception {
            if (null != dataInputStream) {
                synchronized (dataInputStream) {
                    dataInputStream.close();
                    dataInputStream = null;
                }
            }
        }

        //CR 11975
        private void deinitializeConnection() throws Exception {
            if (null != hConnection) {
                synchronized (hConnection) { //bug 12298
                    hConnection.close();
                    hConnection = null;
                }
            }
        }

        private void deinitialize() {
            try{
                deinitializeStream();
                deinitializeConnection();
            }catch(Exception e){

            }
        }
    }

    private class SocketHandler {

        private DataInputStream dataInputStream = null;
        private DataOutputStream dataOutPutStrem = null;
        private SocketConnection socketConnection = null;
        private boolean isSocketOpen = false;
        private boolean isNotReceivedAck = true;

        //private StringBuffer stringBuffer = null;
        private boolean isNotStop = false;
        private Timer receiverTimer = null;

        public void OpenConnection() throws Exception {
            try {
                //13348
                deinitialize(false);
            } catch(Exception excep){
                Logger.loggerError("Socket closing exception "+excep.toString());
            }
            Settings.isAliveState = true;
            //#if VERBOSELOGGING
            //|JG|Logger.debugOnError("Opening Socket "+ChannelData.getSocketUrl()+ " with port 5000");
            //#endif
            String socketUrl = "socket://"+ChannelData.getSocketUrl()+":"+ChannelData.getSocketPort();
            socketConnection = (SocketConnection) Connector.open(socketUrl);

//            socketConnection.setSocketOption(SocketConnection.DELAY, 1);
//            socketConnection.setSocketOption(SocketConnection.KEEPALIVE, 1);
//            socketConnection.setSocketOption(SocketConnection.LINGER, 0);
//            int bufferSize = socketConnection.getSocketOption(SocketConnection.RCVBUF);
//            Logger.debugOnError("Socket Receive Buffer Size "+bufferSize);
//            if (bufferSize < 8192)
//                socketConnection.setSocketOption(SocketConnection.RCVBUF, 8192);

            dataInputStream = socketConnection.openDataInputStream();

            dataOutPutStrem = socketConnection.openDataOutputStream();

            //#if VERBOSELOGGING
            //|JG|Logger.debugOnError("Socket opended with Reader and Writer");
            //#endif

            isSocketOpen = true;

            reScheduleReaderTimer(true);
        }

        private void reScheduleReaderTimer(boolean isStart){
            try{
                if(null != receiverTimer){
                    Logger.debugOnError("Reader Timer stop started");
                    receiverTimer.cancel();
                    receiverTimer = null;
                    Logger.debugOnError("Reader Timer stop end");
                }
                if(isStart){
                    Logger.debugOnError("Reader Timer start started");
                    isNotStop = true;
                    receiverTimer = new Timer();
                    receiverTimer.schedule(new SocketStreamReader(), 1);
                    Logger.debugOnError("Reader Timer start end");
                }
            }catch(Exception e){
                Logger.loggerError("Socket reScheduler timer Exception "+e.toString());
            }
        }

//        private boolean isConnectionNotAlive(){
//            return !isSocketOpen;
//        }

        private void SendMessageOnSocket(Message sendMessage) throws CustomException {
            if (isSocketOpen) {
                byte notPoll = 7;
                StringBuffer buffer = new StringBuffer();
                long sendTime = 0;
                try {

//                    if(sendMessage.getRMsg()[0].indexOf("!*POLL")>-1)// ||
//                            //sendMessage.getRMsg()[0].indexOf("!*KEEP")>-1 )
//                        notPoll = 8;
                    //String exitCommand = ChannelData.getServerName()+" !*EXIT*!";
                    //CR 14651
                    buffer.append("SenderUID=").append(Settings.getUID(true)).append("&msg=").
                            append(new String(sendMessage.getRMsg()[0].getBytes("utf-8"))).append("!-!");
                    synchronized(dataOutPutStrem){
                        sendTime = (new Date()).getTime();
                        isNotReceivedAck = true;
                        //#if VERBOSELOGGING
                        //|JG|Logger.debugOnError("Trying to request Socket message :" + buffer.toString());
                        //#endif
                        dataOutPutStrem.write(buffer.toString().getBytes(), 0, buffer.toString().getBytes().length);
                        dataOutPutStrem.flush();
                        //#if VERBOSELOGGING
                        //|JG|Logger.debugOnError("Sent Socket message ACKN Timer start "+sendTime);
                        //#endif
                        if(sendMessage.getRtDelay() != -1){
                            while(isNotReceivedAck && ((new Date()).getTime() - sendTime) < 10000){
                                Thread.sleep(100);
                            }
                            if(isNotReceivedAck){
                                notPoll = 11;
                                Logger.debugOnError("ACKN not received, Timer expired");
                                throw new Exception("ACKN Not Received");
                            }
                        }
                        //#if VERBOSELOGGING
                        //|JG|Logger.debugOnError("ACKN Timer end");
                        //#endif
                    }
                    //CR 0012014
                    Settings.increaseSentRequestCount(sendMessage.getRMsg()[0].getBytes().length);
                    ObjectBuilderFactory.GetKernel().messageReceived(3, getSendFaildMessage(sendMessage), true); //CR 10203
                    //CR 13431
                    reSchedulePollTimer(true,0);
                } catch(SecurityException securityException){
                   notPoll = 2;
                   throw new CustomException(2,"Socket request write message exception: Security exception " +
                           securityException.toString() + " " + securityException.getMessage());
                } catch(Exception exception){
                    Logger.debugOnError("Socket request write exception "+exception.toString());
                    try {
                        //13348
                        deinitialize(false);
                    } catch(Exception excep){
                        Logger.loggerError("Socket closing exception "+excep.toString());
                    }
                    throw new CustomException(notPoll,"Socket request write message exception " +
                           exception.toString() + " " + exception.getMessage());
                }
                buffer = null;
            }
        }

        private class SocketStreamReader extends TimerTask{
            public void run() {
                byte[] tempBytes = null;
                int lenght = 0;
                reScheduleReaderTimer(false);
                ByteArrayOutputStream byteArrayOutPutStream = new ByteArrayOutputStream();
                String temp, passText = null;
                int index, sencondIndex = -1;
                boolean isProcess = true;
                String ackn ="!*ACKN*!";
                Logger.debugOnError("Socket response reader started");
                while (isNotStop) {
                    try {
                        if(null != dataInputStream){
                            tempBytes = new byte[0];
                            synchronized(dataInputStream){
                                while(isNotStop && (lenght=dataInputStream.available()) == 0){
                                    Thread.sleep(50);
                                }
                                if(isNotStop){
                                    tempBytes = new byte[lenght];
                                    dataInputStream.readFully(tempBytes);
                                }
                            }
                            if(isNotStop){
                                if(tempBytes.length>0){
                                    //CR 13344
                                   byteArrayOutPutStream.write(tempBytes);
                                   tempBytes = byteArrayOutPutStream.toByteArray();
                                   temp = new String(tempBytes);
                                   //#if VERBOSELOGGING
                                   //|JG|Logger.debugOnError("Socket total response message "+temp);
                                   //#endif
                                   isProcess = true;
                                   while(isProcess && (index=temp.indexOf("!=-!"))>-1){
                                        if((sencondIndex=temp.indexOf("!-=!",index+4))>-1){
                                            passText = temp.substring(index+4,sencondIndex);
                                            temp = temp.substring(sencondIndex+4);
                                            if(ackn.compareTo(passText) == 0){
                                                isNotReceivedAck = false;
                                                Logger.debugOnError("ACKN Received "+passText);
                                            } else {
                                                PushDataIntoReceiveQueue(passText.getBytes(), ChannelData.getShortcode(), false,true);
                                            }
                                        } else isProcess = false;
                                   }
                                   byteArrayOutPutStream = new ByteArrayOutputStream();
                                   byteArrayOutPutStream.write(temp.getBytes());
                                   //#if VERBOSELOGGING
                                   //|JG|Logger.debugOnError("Socket balance response message "+temp);
                                   //#endif
                                }
                            } else Logger.debugOnError("Socket reader stream closed");
                        } else {
                            throw (new Exception("Socket reader closed without knowledge"));
                        }
                    } catch (Exception exception) {
                        Logger.loggerError("Socket reader exception "+exception.toString());
                        try{
                            deinitialize(false);
                        }catch(Exception e){}
                        ObjectBuilderFactory.GetKernel().messageReceived(9,null, true);
                    }
                }
                Logger.debugOnError("Socket Read End");
            }
        }

        private void closeSocket() throws Exception {
            if (null != socketConnection) {
                Logger.debugOnError("Socket stream close started");
                synchronized(socketConnection){
                    socketConnection.close();
                    socketConnection = null;
                }
                Logger.debugOnError("Socket stream close end");
            }
        }

        private void closeStreams(boolean isSHExit) {
            boolean isNotWriteException = true;
            isNotStop = false;
            try{
                if (null != dataOutPutStrem) {
                    Logger.debugOnError("Socket write stream close started");
                    synchronized(dataOutPutStrem){
                        //CR 13245, 14651
                        if(isSHExit){
                            StringBuffer buffer = new StringBuffer("SenderUID=").append(Settings.getUID(true)).append("&msg=").
                                    append(ChannelData.getServerName()).append(" !*EXIT*!!-!");
                            Logger.debugOnError("Socket close sending !*EXIT*! channel data");
                            dataOutPutStrem.write(buffer.toString().getBytes(),0,buffer.toString().getBytes().length);
                            dataOutPutStrem.flush();
                            Logger.debugOnError("Sent EXIT channer data");
                        }

                        dataOutPutStrem.close();
                        dataOutPutStrem = null;
                    }
                    Logger.debugOnError("Socket write stream close end");
                }
            } catch(Exception exception){
                isNotWriteException = false;
                Logger.loggerError("Socket writeStream connection close Exception "+exception.toString());
            }

            try {
                if (null != dataInputStream) {
                    Logger.debugOnError("Socket reader stream close started");
                    if(isNotWriteException){
                        synchronized(dataInputStream){
                            dataInputStream.close();
                            dataInputStream = null;
                        }
                        Logger.debugOnError("Socket reader stream close end");
                    } else {
                        dataInputStream = null;
                        Logger.debugOnError("Socket reader UnProper close end");
                    }
                }
            } catch(Exception exception){
              Logger.loggerError("Socket readerStream connection close Exception "+exception.toString());
            }
        }

        private void deinitialize(boolean isSHExit) throws Exception {
            isSocketOpen = false;
            //CR 12548
            Settings.isAliveState = false;
            reScheduleReaderTimer(false);
            closeStreams(isSHExit);
            try{
                closeSocket();
            }catch(Exception exception){
                if(!isSHExit){
                    throw exception;
                }
            }
        }
    }
}
