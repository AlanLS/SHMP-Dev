
import generated.Build;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;
import javax.microedition.io.file.FileConnection;

/**
 *
 * @author Tomm
 */
public class DownloadHandler {

    private byte prState = 0;
    private HttpConnection hConnection = null;
    private DataInputStream dIn = null;
    private DataOutputStream dout = null;
    private Timer downloadTimer = null;
    private Hashtable urlTable = new Hashtable();
    private Stack dappUrls = new Stack();
    private boolean isNotStarted = true;
    /** Trap link to store the link value in rms at run time*/
    private String tLink = null;

    private final String BOUNDARY = "-----------------------------10511280232634";


    /**
     *  <li> 1. initial Download </li>
     *  <li> 2. DAPP download </li>
     *  <li> 3. Single App download </li>
     *  <li> 4. Multi App doenload </li>
     *  <li> 5. Log file upload </li>
     */
    private byte downloadProcesstype = 0;
    private String singleWidgetUrl = null;
    private String singleWidgetId = null;

    private String imageId = null;

    private String fileLocation = null;

    private static DownloadHandler downloadHandler = null;

    private static boolean isContactUpdate = false;

    private Vector gridVector = new Vector();

    public static DownloadHandler getInstance(){
        if(null == downloadHandler){
            downloadHandler =  new DownloadHandler();
        }
        return downloadHandler;
    }

    private DownloadHandler() {
        buildDappUrl();
        buildAppsUrl();
    }




    private void loadDownloadScreen(String[] dText) {
        ObjectBuilderFactory.GetKernel().initializeScreen(KernelConstants.FRENDEND_DOWNLOAD);
        ObjectBuilderFactory.GetKernel().displayScreen(dText, false);
    }

    /**
     * Method to download the initial Apps, at the starting stage, if the initial download is interepted again the client reattempt the download
     * @param pState currently display screen state, and which handle did handle this screen
     */
    public void startInitilAppdownload(int pState) {
        // bug id 6307
        if (!isPendingAppToDownload()) {
            String resourcesApp = ChannelData.getResourcesApps();
            if(null != resourcesApp)
                setHttpRequest(pState, 1, new String[]{"", Constants.headerText[21]});
            else setHttpRequest(pState, 1, new String[]{"", Constants.headerText[18]});
        } else {
            setHttpRequest(pState, 1, new String[]{"", Constants.headerText[18]});
        }
//        FOR KABONN HEADER TEST DURING LODAING
//        // bug id 6307
//        if (!isPendingAppToDownload()) {
//            String resourcesApp = ChannelData.getResourcesApps();
//            if(null != resourcesApp)
//                setHttpRequest(pState, 1, new String[]{Constants.headerText[29], Constants.headerText[21]});
//            else setHttpRequest(pState, 1, new String[]{Constants.headerText[29], Constants.headerText[18]});
//        } else {
//            setHttpRequest(pState, 1, new String[]{Constants.headerText[29], Constants.headerText[18]});
//        }
        //setHttpRequest(pState, 1, new String[]{"", Constants.headerText[18]});
    }

    /**
     * Method to Download the Apps form pushed Url, the Url will comes from server thorugh message called DAPP chennal Data
     * @param pState currently display screen state, and which handle did handle this screen
     */
    public void startDappPushDownload(int pState) {
        setHttpRequest(pState, 2, new String[]{"", Constants.headerText[18]});
    }

    /**
     * Method to Download the Apps form the give Url/Pushed Url, the Url will comes from server/Widget catalog
     * @param pState currently display screen state, and which handle did handle this screen
     */
    public void startMultiappDownload(int pState) {
        setHttpRequest(pState, 4, new String[]{"", Constants.headerText[18]});
    }

    /**
     * Method to upload the file from the client to server
     * @param pState previous running screen
     */
    public void upload(int pState) {
        setHttpRequest(pState, 5, new String[]{"", Constants.headerText[19]}); //bug no 4897
    }

    /**
     * Method to download the App from the widget catalog, for given url and App id,
     * @param pState currently display screen state, and which handle did handle this screen
     * @param url app download from this url
     * @param wId which app to be download from the server
     */
    public void startSingleAppdownload(int pState, String url, String wId, String headerDisplayName) {
        if (pState > -1) {
            prState = (byte) pState;
        }
        singleWidgetId = wId;
        singleWidgetUrl = getLink(3, url, wId, null);
        setHttpRequest(pState, 3, (new String[]{headerDisplayName, Constants.headerText[20]}));
    }

    /**
     * Method to set the Download Request Parameter,
     * @param pState currently display screen state, and which handle did handle this screen
     * @param type which type of download to be start
     *      <li> 1. Initial Download </li>
     *      <li> 2. DAPP download </li>
     *      <li> 4. Multi App Download </li>
     *      <li> 5. Log File Upload <li>
     * @param dText, this text to be displayed into the ui screen
     */
    private void setHttpRequest(int pState, int type, String[] dText) {
        if (isNotStarted) {
            isNotStarted = false;
            downloadProcesstype = (byte) type;
            if (pState > -1) {
                prState = (byte) pState;
            }
            loadDownloadScreen(dText);
            stopDownloadTimer();
            startDownloadTimer();
        }
    }

    //CR 14291
    public void downloadImage(String fileLocation, String imageId, int fileFormat){
        if(fileFormat == 0){
            if(null != UiGlobalVariables.byteArrayInputStream){
                downloadProcesstype = 8;
            } else {
                downloadProcesstype = 9;
            }
        } else if(fileFormat == 1){
            if(null != UiGlobalVariables.byteArrayInputStream){
                downloadProcesstype = 10;
            } else {
                downloadProcesstype = 11;
            }
        } else if(fileFormat == 3){ //CR 14727
            downloadProcesstype = 13;
        } else if(null == UiGlobalVariables.imagefile){
            downloadProcesstype = 6;
        } else {
            if(fileFormat == 2){ //CR 14694
                downloadProcesstype = 12;
            } else if(fileFormat == -1){
                downloadProcesstype = 7;
            }
        }
        this.imageId = imageId;
        this.fileLocation = fileLocation;
        //Cr 13981
//        loadDownloadScreen(new String[]{"", Constants.headerText[32]});
        stopDownloadTimer();
        startDownloadTimer();
    }

    //CR 14741, 14742
    public void contactThumubUpdate(){
        if(isNotStarted && (Settings.isIsContactGridUpdate()||Settings.isIsGroupGridUpdate())){
            stopDownloadTimer();
            startDownloadTimer();
        }
    }

    /**
     *
     */
    public String getConfigDetails(String fileLocation){
        String value = null;
        byte[] rbyte = getResourcesBytes("", fileLocation);
        if(null != rbyte){
            value = new String(rbyte);
        }
        return value;
    }

    /**
     * Method to push the dapp data
     * @param url
     */
    public void pushDappData(String url) {
        url = getLink(2, url, null, null);
        dappUrls.push(url);
        updateDappUrl();
    }

    /**
     * Method to retrive the proper url link depends upon the linkstate
     * @param linkState
     *          <li> 1. Initial Download Link </li>
     *          <li> 2. Dapp Chennal Data Link </li>
     *          <li> 3. Single App download link </li>
     *          <li> 4. Log File upload link </li>
     * @param url append url parameter(Initial Download not have this parameter)
     * @param wId Single app id(This parameter does not have other linktype) only for Single app download
     * @return full url link.
     */
    private String getLink(int linkState, String url, String id, String size) {
        String link = null;
        if (linkState == 1) { //Initial download Link
            //CR 13229
            link = ChannelData.getInitialDownloadUrl() + "&prov=" + ChannelData.getProvenenceName() + "&bundlename=" + ChannelData.getBundleName() + "&os=" + ChannelData.osName + "&pmodel=" + Build.MODEL +
                    "&cver=" + ObjectBuilderFactory.GetKernel().getVersionNumber(true) + "&height=" + ChannelData.getJadHeight() +
                    "&width=" + ChannelData.getJadWidth() + "&pmake=" + Build.MANUFACTURER + "&iconwidth=" + ChannelData.iConWidth + "&region=" + ChannelData.getRegion()
                    +"&AppCatalog="+ChannelData.getAppCatalogInclude();
        } else if (linkState == 2) { //Dapp Download Link
            //CR 13229
            link = url + "&prov=" + ChannelData.getProvenenceName() + "&bundlename=" + ChannelData.getBundleName() + "&os=" + ChannelData.osName + "&cver=" +
                    ObjectBuilderFactory.GetKernel().getVersionNumber(true) + "&height=" + ChannelData.getJadHeight() +
                    "&width=" + ChannelData.getJadWidth() + "&pmake=" + Build.MANUFACTURER + "&iconwidth=" + ChannelData.iConWidth + "&region=" + ChannelData.getRegion()
                    +"&AppCatalog="+ChannelData.getAppCatalogInclude();
        } else if (linkState == 3) { //Single Widget Download Link
            //CR 8353
            //CR 12989
            //CR 13229
            link = url + "senderUID=" + Settings.getUID(true) +
                    "&wid=" + id + "&prov=" + ChannelData.getProvenenceName() + "&bundlename=" + ChannelData.getBundleName() + "&os=" + ChannelData.osName +
                    "&pmodel=" + Build.MODEL + "&cver=" + ObjectBuilderFactory.GetKernel().getVersionNumber(true) +
                    "&height=" + ChannelData.getJadHeight() + "&width=" + ChannelData.getJadWidth() +
                    "&pmake=" + Build.MANUFACTURER + "&iconwidth=" + ChannelData.iConWidth + "&region=" + ChannelData.getRegion()
                    + "&AppCatalog="+ChannelData.getAppCatalogInclude();
        } else if (linkState == 4) { //Log File Upload Link
            //CR 12990
            //CR 6724
//            url = Utilities.replace(url, "mplusapphandler", "doodad");
            link = url + "Log_" + (new Date().getTime()) + "_UID" + Settings.getUID(true) +
                        "_" + Build.MODEL + "_" + ObjectBuilderFactory.GetKernel().getVersionNumber(false) + ".txt";
        } else if(linkState == 5){
            //CR 13900
            //CR 14001,
            link = url+id+"?height="+(UISettings.formHeight-
                    (UISettings.headerHeight + UISettings.secondaryHeaderHeight
                    +UISettings.footerHeight + UISettings.secondaryHeaderHeight))
                    +"&width="+(UISettings.formWidth-20)+"&UID="+Settings.getUID(true);

//            link = "http://doodad.shorthandmobile.com/trials/j2me/test/san/unsigned/IMG_6223.JPG";
        } else if(linkState == 6){
            //CR 14111, 14325, 14330, 14465
            link = url+"UID="+Settings.getUID(true)+"&destination="+id
                    +"&chatSequence="+GlobalMemorizeVariable.getChatSequenceNumber()
                    +"&date="+Utilities.getCurrentDateYYYYMMDDFormat();
            //link = "http://115.115.65.123:8881/image.aspx";
        } else if(linkState == 7){
            //CR 14694
            link = url+"UID="+Settings.getUID(true)+"&msisdn="+id+"&height="+(UISettings.formHeight-
                    (UISettings.headerHeight + UISettings.secondaryHeaderHeight
                    +UISettings.footerHeight + UISettings.secondaryHeaderHeight))
                    +"&width="+(UISettings.formWidth-20);
            if(null != size){
                link += "&version="+fileLocation+"&size="+size;
            }
//            link = "http://mplusimg.shorthandmobile.com/downloadprofileimage.aspx?UID=0000008041&msisdn=918754415795&height=220&width=220&version=00&size=profile";
        } else if(linkState == 8){ //CR 14741, 14742, 14791, 14803
            link = url + "UID=" + Settings.getUID(true)+"&type="+size;
        }
        System.out.println(link);
        return link;

    }

    /**
     * Method to start the download timer
     */
    private void startDownloadTimer() {
        downloadTimer = new Timer();
        downloadTimer.schedule(new widgetDownloading(), 0);
    }

    /**
     * Method to start the download timer
     */
    private void stopDownloadTimer() {
        if (downloadTimer != null) {
            downloadTimer.cancel();
            downloadTimer = null;
        }
    }

    /**
     * Method to load the previous screen
     */
    private void loadPreviousScreen(boolean status) {
        isNotStarted = true;
        byte state = prState;
        prState = -1;
        if (state == -1) {
            state = 0;
        }
        ObjectBuilderFactory.GetKernel().loadCurrentScreen(state, status);
    }

    /**
     * Methdo to handle Messagebox options
     * @param isStaus
     */
    public void handleMessageBox(boolean isStaus) {
        loadPreviousScreen(false);
    }

    /**
     * Method to load the messagebox for the give the text and type
     * @param msgType loading messagebox type
     * @param msg displaying text
     * @param msgst handleing message state
     * @param hText messagebox header text
     */
    private void loadMessageBox(int msgType, String msg, int msgst, String hText) {
        ObjectBuilderFactory.GetKernel().displayMessageBox(msgType, msg, hText);
    }

    /**
     * Method to open the Http Connection and return the pasitive or negative parameter
     * @param url Connection establish link
     * @return true-connection established successfuly otherwise not establish
     */
    private boolean openConnection(String url) {
        try {
            hConnection = (HttpConnection) Connector.open(url);
            //#if VERBOSELOGGING
            //|JG|Logger.debugOnError("Connection Opened");
            //#endif
            if (null != hConnection) {
                //#if VERBOSELOGGING
                //|JG|Logger.debugOnError("Connection code checking");
                //#endif                
                if (hConnection.getResponseCode() == HttpConnection.HTTP_OK) {
                    String temp= hConnection.getHeaderField("Content-Type");
                    Logger.loggerError("Http Connection content type="+temp);

                    if(downloadProcesstype == 9){
                        UiGlobalVariables.extension = "wav";
                        if(temp!= null && temp.indexOf("amr")>-1){
                            UiGlobalVariables.extension = "amr";
                        }
                    } else if(downloadProcesstype == 11){
                        UiGlobalVariables.extension = "3gp";
                        if(temp!= null && temp.indexOf("mp4")>-1){
                            UiGlobalVariables.extension = "mp4";
                        }
                    }
                    //#if VERBOSELOGGING
                    //|JG|Logger.debugOnError("Connection success "+url);
                    //#endif
                    return true;
                } else {
                    Logger.loggerError("Http Connection Response Code Error : "+hConnection.getResponseCode());
                }
            } else {
                Logger.loggerError("Http Connection not opened");
            }
        } catch (Exception e) {
            Logger.loggerError("DownloadHandler -> HTTP Download OpenConnection Error" + e.toString());
        }
        closeConnection();
        return false;
    }

    private boolean openUploadConnection(String url) {
        try {
            //errorString = "";
//            url ="http://doodad.smarttouchmobileinc.com/uploadlog.aspx?filename=Log_1384843469111_UID000001111_Unsigned_4.7.6.txt";
            Logger.debugOnError("Upload Url "+url);
            hConnection = (HttpConnection) Connector.open(url, Connector.READ_WRITE, true);
            if (null != hConnection) {
                return true;
            } 
        } catch (Exception e) {
            //errorString = e.toString();
            Logger.loggerError("DownloadHandler -> HTTP Upload OpenConnection Error" + e.toString());
        }
        closeConnection();
        return false;
    }

    /**
     * Method to close the http connection, if the connection is opened
     */
    private void closeConnection() {
        try {
            if (null != hConnection) {
                hConnection.close();
                hConnection = null;
            }
        } catch (Exception e) {
            Logger.loggerError("DownloadHandler -> Http CloseConnection Error " + e.toString());
        }
    }

    /**
     * Method to upload the log file for the give Url
     * @param uByte uploading byte
     * @param uUrl uploading path
     * @return true upload is finished otherwise not finished
     */
    private ByteArrayOutputStream UploadData(String url) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            Logger.debugOnError("UploadFileData->"+fileLocation+" "+imageId+" "+UiGlobalVariables.extension);
            if (openUploadConnection(url)) {
                hConnection.setRequestMethod(HttpConnection.POST);
                hConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" +BOUNDARY);
                hConnection.setRequestProperty("User-Agent", "Profile/MIDP-2.0 Configuration/CLDC-1.1");
                hConnection.setRequestProperty("Content-Language", "en-US");
                //CR 14630
//                hConnection.setRequestProperty("Accept", "application/octet-stream");
                uploadContent();
                byteArrayOutputStream = getConnectionBytes();
                if(null != byteArrayOutputStream && byteArrayOutputStream.size()>0){
                    Logger.debugOnError("Byte Length "+byteArrayOutputStream.size()+" \nReturn value "+new String(byteArrayOutputStream.toByteArray()));
                } else Logger.debugOnError("Server not return any byts");
            }
        } catch (Exception er) {
            Logger.loggerError("DownloadHandler -> Image Uload error " + er.toString());
        }
        closeConnection();
        return byteArrayOutputStream;
    }

    private void uploadContent() throws CustomException{
        byte errorPosition = 0;
        DataInputStream dataInputStream = null;
        FileConnection fileConnection = null;
        int index = 5;

        try {
            //CR 14291
           //boolean dummy= true;
            String contType="image";
            long size = 0;
            boolean isSizeReduce = true;
            if(null == fileLocation){
                errorPosition = 1;
                dataInputStream = new DataInputStream(UiGlobalVariables.byteArrayInputStream);
                size = dataInputStream.available();

                if(null != UiGlobalVariables.extension){
                    isSizeReduce = false;
                    if(downloadProcesstype == 10){
                        contType = "video";
                    } else if(downloadProcesstype == 8){
                        contType = "audio";
                    }
                    fileLocation = "sasi."+UiGlobalVariables.extension;
                    index = UiGlobalVariables.extension.length()+1;
                } else {
                    fileLocation = "sasi.jpeg";
                }
            } else {
                errorPosition = 2;
                fileConnection = (FileConnection)Connector.open(fileLocation,Connector.READ);
                dataInputStream = new DataInputStream(fileConnection.openInputStream());
                size = fileConnection.fileSize();
//                if(downloadProcesstype == 12){ //CR 14694
//                    isSizeReduce = false;
//                }
            }
            errorPosition = 3;
            //Cr 14465
//            Logger.debugOnError("FILE SIZE:"+size);
            if(size>51200 && isSizeReduce){
                dataInputStream.close();
                dataInputStream = null;
                errorPosition = 4;
                JPGEncoder encoder = new JPGEncoder();
                if(null == fileConnection) {
                    UiGlobalVariables.byteArrayInputStream = new ByteArrayInputStream(encoder.encode(UiGlobalVariables.imagefile, 100));
                    dataInputStream = new DataInputStream(UiGlobalVariables.byteArrayInputStream);
                } else {
                    fileConnection.close();
                    fileConnection = null;
                    dataInputStream = new DataInputStream(new ByteArrayInputStream(encoder.encode(UiGlobalVariables.imagefile, 100)));
                }
                encoder.freeEncoder();
                encoder = null;
                size = dataInputStream.available();
            } else if(downloadProcesstype == 12){ //Bug 14774
                JPGEncoder encoder = new JPGEncoder();
                UiGlobalVariables.byteArrayInputStream = new ByteArrayInputStream(encoder.encode(UiGlobalVariables.imagefile, 100));
                dataInputStream = new DataInputStream(UiGlobalVariables.byteArrayInputStream);
                encoder.freeEncoder();
                encoder = null;
            }
                          
//            Logger.debugOnError("UPLOADING this file to server:"+fileLocation+",content type="+contType);
            errorPosition = 5;
            String contentType = "--"+BOUNDARY+"\r\n";
            index = fileLocation.indexOf(".",fileLocation.length()-index);
            contentType += "Content-Disposition: form-data; name=\"file\"; filename=\"sasi."+
                    fileLocation.substring(index+1)+"\"\r\n";
            contentType +="Content-Type: " + contType + "/"+fileLocation.substring(index+1)+"\r\n\r\n";
            long totalSize = ("\r\n--" + BOUNDARY + "--\r\n").getBytes().length+size+contentType.length();
            hConnection.setRequestProperty("Content-length", totalSize+"");
            hConnection.setRequestProperty("Content-Length", totalSize+"");
            errorPosition = 6;
            dout = hConnection.openDataOutputStream();
            dout.write(contentType.getBytes());
            errorPosition = 7;
            int nRead = -1;
            if(size>Runtime.getRuntime().freeMemory()){
                size = Runtime.getRuntime().freeMemory();
            } else {
                size = (size/2);
            }
            byte[] bytes = new byte[(int)size];
//            Logger.debugOnError("Upload WriteStarted ->"+size+" "+dataInputStream.available());
            
            while((nRead=dataInputStream.read(bytes)) != -1){
                dout.write(bytes,0,nRead);
            }
            
//            Logger.debugOnError("Data Write End");
            errorPosition = 8;
            bytes = null;
            dout.write(("\r\n--" + BOUNDARY + "--\r\n").getBytes());
            errorPosition = 9;
            try{
                dout.flush();
                errorPosition = 10;
            }catch(Exception exception){
                Logger.debugOnError("DownloadHander->UploadData->Flush-> "+exception.toString());
            }
            dout.close();
            errorPosition = 11;
            dout = null;
        } catch(SecurityException securityException){
            Logger.loggerError("DownloadHandler->uploadData->"+securityException.toString());
            throw (new CustomException(2, "User not allow to open file connection"));
        } catch(Exception exception){
            Logger.loggerError("DownloadHandler->uploadData-> "+exception.toString()+" \nErrorPosition "+errorPosition);
            throw  (new CustomException(3, "Image file write exception"));
        } catch(OutOfMemoryError outOfMemoryError){
            Logger.loggerError("DownloadHandler->uploadData-> "+outOfMemoryError.toString()+" \nErrorPosition "+errorPosition);
            throw  (new CustomException(3, "Image file write exception"));
        } finally{
            try{
                if(null != dataInputStream){
                    dataInputStream.close();
                    dataInputStream = null;
                }
            }catch(Exception exception){}
            try{
                if(null != fileConnection){
                    fileConnection.close();
                    fileConnection = null;
                }
            }catch(Exception exception){}
        }
    }

    /**
     * Method to upload the log file for the give Url
     * @param uByte uploading byte
     * @param uUrl uploading path
     * @return true upload is finished otherwise not finished
     */
    private boolean UploadLog() {
        boolean isNotSent = true;
        byte[] rbyte = null;
        try {
            if (openUploadConnection(getLink(4, ChannelData.getUploadUrl(), null, null))) {
                hConnection.setRequestMethod(HttpConnection.POST);
                hConnection.setRequestProperty("User-Agent", "Profile/MIDP-2.0 Configuration/CLDC-1.1");
                hConnection.setRequestProperty("Content-Language", "en-US");
//                hConnection.setRequestProperty("Accept", "application/octet-stream");
                rbyte = Logger.getUploadLog();
                byte[] version  = ("Application Version "+ObjectBuilderFactory.GetKernel().getVersionNumber(true)).getBytes();
                hConnection.setRequestProperty("Content-Length", Integer.toString(rbyte.length+version.length));
                dout = hConnection.openDataOutputStream();
                dout.write(rbyte);
                dout.write(version);
                dout.close();
                dout = null;
                isNotSent = false;
            }
        } catch (Exception er) {
            if(null != rbyte){
                Logger.writeOldData(rbyte);
            }
            Logger.loggerError("DownloadHandler -> Uload Log file error " + er.toString());
        }
        closeConnection();
        return isNotSent;
    }

    /**
     * Method to write the byte[] for the given location, and the file is already exit it should be delete and recreate the new file
     * @param wByte writing byte[]
     * @param loc fileCreation location
     * @param fLoc file storing foldre location
     * @return true-file successfully writen otherwise false
     */
    private byte writeFile(ByteArrayOutputStream byteArrayOutputStream, String rName) {
        byte isWriten = 0;
        //rName = RecordManager.getBackImage(rName);
        if (null != byteArrayOutputStream) {
            isWriten = RecordStoreParser.UpdateRecordStore(rName, byteArrayOutputStream, true);
        }
        return isWriten;
    }

    //CR 14741, 14742
    private void downloadContactGridThumb(){
        while(isContactUpdate);
        if(Settings.isIsContactGridUpdate() || Settings.isIsGroupGridUpdate()){
            isContactUpdate = true;
            try{
                ByteArrayOutputStream byteArrayOutputStream = null;
                if(Settings.isIsContactGridUpdate()){
                    byteArrayOutputStream = getConnectionBytes(getLink(8, ChannelData.getContactGridThumbNailDownloadUrl(), null, "profile"));
                    writeFile(byteArrayOutputStream, RecordManager.gridThumbnailImage);
                    Settings.setContactGridUpdate(false);
                    //bug 14832
                    ObjectBuilderFactory.GetKernel().setImage(null, true, true);
                }

                if(Settings.isIsGroupGridUpdate()){
                    byteArrayOutputStream = getConnectionBytes(getLink(8, ChannelData.getContactGridThumbNailDownloadUrl(), null, "groupandshout"));
                    writeFile(byteArrayOutputStream, RecordManager.groupGridThumbnailImage);
                    Settings.setGroupContactGridUpdate(false);
                    //bug 14832
                    ObjectBuilderFactory.GetKernel().setImage(null, true, true);
                }

                if(null != byteArrayOutputStream){
                    byteArrayOutputStream.close();
                    byteArrayOutputStream = null;
                }
            }catch(Exception exception){
                Logger.loggerError("DownloadHandler->dowloadContactGridThumb->"+exception.toString());
            }
            isContactUpdate = false;
        }
    }

    /**
     * Method to start the download depends upon the httprequest, when the timer is reached the dead state,
     */
    private void reStartDownload() {
        isNotStarted = false;
        Settings.setIsDownload(false);
        try {
            stopDownloadTimer();
            if (downloadProcesstype == 1 || downloadProcesstype == 2) { //Initial download and Dapp Download Process
                if (downloadProcesstype == 1 && !isPendingAppToDownload()) {
                    setResourcessToUserMemory();
                    if (ChannelData.isInitialAppsDownload()) {
                        //ObjectBuilderFactory.GetKernel().changeHearder(new String[]{Constants.headerText[29], Constants.headerText[18]});
                        ObjectBuilderFactory.GetKernel().changeHearder(new String[]{"", Constants.headerText[18]});
                        dappUrls.push(getLink(1, null, null, null));
                        updateDappUrl();
                    }
                }
                reBuildTable();
                boolean isNotMemory = startDownload();
                if (isPendingAppToDownload()) {
                    if (isNotMemory) {
                        loadMessageBox(4, Constants.popupMessage[23], 1, Constants.headerText[8]);
                    } else {
                        loadMessageBox(4, Constants.popupMessage[24], 1, Constants.headerText[8]);
                    }
                } else {
                    loadPreviousScreen(true);
                }
            } else if (downloadProcesstype == 3) { //Single App Download Process
                byte isMemory = downloadSingleApp();
                if(null == Settings.getPhoneNumber()){ //CR 8353
                    ObjectBuilderFactory.getControlChanel().sendProvenanceAction(true); //CR 8353
                }
                if (isMemory == 0) {
                    loadMessageBox(4, Constants.popupMessage[23], 1, Constants.headerText[8]);
                } else if(isMemory == 2){
                    loadMessageBox(4, Constants.popupMessage[24], 1, Constants.headerText[8]);
                } else if(isMemory == 3){
                    loadMessageBox(4, Constants.popupMessage[57], 1, Constants.headerText[8]);
                } else {
                    loadPreviousScreen(true);
                }
            } else if (downloadProcesstype == 4) { //Multi App download Process
            } else if (downloadProcesstype == 5) { //Log file Upload Process
                if (UploadLog()) {
                    loadMessageBox(4, Constants.popupMessage[25] , 1, Constants.headerText[8]);
                } else {
                    loadMessageBox(14, Constants.popupMessage[54], 1, Constants.headerText[8]);
                }
            } else if(downloadProcesstype == 6){
                ObjectBuilderFactory.GetKernel().setImage(getConnectionBytes(getLink(5, ChannelData.getImageUrl(), imageId, null)),
                        true, false);
            } else if(downloadProcesstype == 7){
                ObjectBuilderFactory.GetKernel().setImage(UploadData(getLink(6, ChannelData.getChatImageUrl(), imageId, null))
                        , false, false);
            } else if(downloadProcesstype == 8){ // CR 14465
                ObjectBuilderFactory.GetKernel().setImage(UploadData(getLink(6, ChannelData.getAudioUploadUrl(), imageId, null))
                        ,false, false);
            } else if(downloadProcesstype == 9){ //CR 14491
                ObjectBuilderFactory.GetKernel().setImage(getConnectionBytes(getLink(5, ChannelData.getAudioDownloadUrl(), imageId, null))
                        ,true, false);
            } else if(downloadProcesstype == 10){
                ObjectBuilderFactory.GetKernel().setImage(UploadData(getLink(6, ChannelData.getVideoUploadUrl(), imageId, null))
                        ,false, false);
            } else if(downloadProcesstype == 11){
                ObjectBuilderFactory.GetKernel().setImage(getConnectionBytes(getLink(5, ChannelData.getVideoDownloadUrl(), imageId, null))
                        ,true, false);
            } else if(downloadProcesstype == 12){ //CR 14694
                ObjectBuilderFactory.GetKernel().setImage(UploadData(getLink(7, ChannelData.getProfilePictureUploadUrl(), imageId, null))
                        ,false, false);
            } else if(downloadProcesstype == 13){ //CR 14727
                ObjectBuilderFactory.GetKernel().setImage(getConnectionBytes(getLink(7, ChannelData.getProfilePictureDownloadUrl(), imageId, "profile")), 
                        true, false);
            }
        } catch (Exception e) {
            Logger.loggerError("DownloadHandler reStreatDownload Exception " + e.toString());
            loadMessageBox(4, Constants.popupMessage[23], 1, Constants.headerText[8]);
        }
        Settings.setIsDownload(false);
        isNotStarted = true;
    }

    private void setResourcessToUserMemory(){
        String resourcesApp = ChannelData.getResourcesApps();
        if (null != resourcesApp) {
            ObjectBuilderFactory.GetKernel().changeHearder(new String[]{"", Constants.headerText[21]});
            byte[] rbyte = getResourcesBytes("Applist", resourcesApp + ".txt");
            String wName = null;
            if (null != rbyte) {
                String names = new String(rbyte);
                int index = -1;
                String tName = null;
                while (names.length() > 0) {
                    if ((index = names.indexOf(",")) > -1) {
                        tName = names.substring(0, index);
                        names = names.substring(index + 1);
                    } else {
                        tName = names;
                        names = "";
                    }
                    index = tName.indexOf("^");
                    boolean isOrdinary = true;
                    boolean isAppcatalog = false;
                    if (!tName.startsWith("Startup")) {
                        if (tName.startsWith("Feature")) {
                            isOrdinary = false;
                        } else if(tName.toLowerCase().startsWith("appcatalog") || tName.toLowerCase().startsWith("app catalog")){
                            isOrdinary = false;
                            isAppcatalog = true;
                        }
                        wName = tName.substring(index+1);
                        if (isOrdinary) {
                            updateAppName(RecordManager.newAppName, wName, isAppcatalog, true);
                        } else {
                            updateAppName(RecordManager.featureAppName, wName, isAppcatalog, true);
                        }
                    }
                }
            }
        }
    }

    private int getFirstLinkIndex(String value){
        int index = value.indexOf(":");
        index = value.indexOf(":",index+1);
        index = value.indexOf(":",index+1);
        index = value.indexOf(",",index+1);
        index = value.indexOf(",",index+1);
        index = value.indexOf(",",index+1);
        int lastIndex = value.indexOf(",",index+1);
        if(lastIndex>-1){
            if((lastIndex +1) != value.length())
                return lastIndex;
        }
        index = value.length();
        return index;
    }

    /**
     *
     * @param folderName
     * @param fileName
     * @return
     */
    private byte[] getResourcesBytes(String folderName, String fileName) {
        byte[] rbyte = null;
        try {
            if(folderName.length()>0){
                folderName = "/" + folderName + "/" + fileName;
            } else folderName = "/" + fileName;
            InputStream instream = Object.class.getResourceAsStream(folderName);
            rbyte = new byte[instream.available()];
            instream.read(rbyte);
            instream.close();
            instream = null;
        } catch (Exception ex) {
            Logger.loggerError("DownloadHandler->GetResourcesbytes " + ex.toString() + "Folder Location " + folderName + "/" + fileName);
        }
        return rbyte;
    }

    public String getResourcesAppsName(){
        String resourcesApp = ChannelData.getResourcesApps();
        if(null != resourcesApp){
            byte[] rbyte = getResourcesBytes("Applist", resourcesApp + ".txt");
            if(null != rbyte)
                resourcesApp = new String(rbyte);
            else resourcesApp = null;
        }
        return resourcesApp;
    }

    //Bug
    public String getStartupApp(){
        String name = ","+getResourcesAppsName();
        int index;
        if((index= name.indexOf(",Startup^"))>-1)
        {
            index+=9;
            int sIndex =-1;
            if((sIndex=name.indexOf(",",index))>-1){
                return name.substring(index,sIndex);
            } else {
                return name.substring(index);
            }
        }
        return null;
    }

    public byte[] getResourcesBytes(String fileName,boolean isObj){
        byte[] rbyte = null;
        try {
            if(isObj)
                fileName = "/" + fileName + "/" + fileName+".obj";
            else {
                if(fileName.charAt(0) == '/'){
                    fileName += ".png";
                } else {
                    fileName = "/" + fileName+".png";
                }
            }
            InputStream instream = Object.class.getResourceAsStream(fileName);
            rbyte = new byte[instream.available()];
            instream.read(rbyte);
            instream.close();
            instream = null;
        } catch (Exception ex) {
            Logger.loggerError("DownloadHandler->GetResourcesbytes " + ex.toString() + "Folder Location " + fileName);
        }
        return rbyte;
    }

    /**
     * Method to retrive the server link and prepare the hashtable,
     *  Hashtable id should be the App id
     *  Hashtable value should be other links
     *      <li> 1. App Catagory </li>
     *      <li> 2. App Name </li>
     *      <li> 3. App obj link </li>
     *      <li> 4. App bg link </li>
     *      <li> 5. App icon link <.li>
     */
    private void reBuildTable() {
        String link = null;
        Stack tempStack = new Stack();
        ByteArrayOutputStream byteArrayOutputStream = null;
        while (!dappUrls.isEmpty()) {
            link = (String) dappUrls.pop();
            try{
                 byteArrayOutputStream   = getConnectionBytes(link);
                    //#if VERBOSELOGGING
                    //|JG|Logger.debugOnError(new String(rbyte));
                //#endif
                if (null != byteArrayOutputStream) {
                    buildAppUrl(byteArrayOutputStream);
                    updateDappUrl();
                    updateAppsUrl();
                } else {
                    tempStack.push(link);
                }
            }catch(Exception e){
            }
        }
        dappUrls = tempStack;
    }

    /**
     * Method to download the single App for the give url, and create the proper folder
     * @return
     */
    private byte downloadSingleApp() {
        isNotStarted = false;
        byte isDownloadFinished = 0;
        if (null != singleWidgetUrl) {
            try{
                ByteArrayOutputStream byteArrayOutputStream = getConnectionBytes(singleWidgetUrl);
                if (null != byteArrayOutputStream) {
                    String wList = new String(byteArrayOutputStream.toByteArray(),"utf-8");
                    byteArrayOutputStream.close();
                    byteArrayOutputStream = null;
                    Logger.debugOnError(wList);
                    String wTemp = null;
                    int index = 0;
                    String aId = "000";
                    Object[] obj = null;
                    String id = null;
                    byte success = 0;
                    boolean isContinue = true;
                    while (isContinue && wList.length() > 0) {
                        index = getFirstLinkIndex(wList);
                        if(index == wList.length()){
                            wTemp = wList;
                            wList = "";
                        } else {
                            wTemp = wList.substring(0,index);
                            wList = wList.substring(index+1);
                        }
                        obj = getLinkValue(wTemp);
                        success = createApp(((String[]) obj[1]), ((String) obj[0]),false,true);
                        if (success == 1) {
                            id = (String) obj[0];
                            if (aId.compareTo(id) == 0 && singleWidgetId.compareTo(id) != 0) {
                                isDownloadFinished = 4;
                            } else {
                                ObjectBuilderFactory.GetKernel().isNewLoaded();
                                isDownloadFinished = 1;
                            }
                            urlTable.remove(id);
                        } else if (2 == success) {
                            isContinue = false;
                            isDownloadFinished = success;
                        } else if(success == 0){
                            isContinue = false;
                            isDownloadFinished = 3;
                        }
                    }
                }
            }catch(Exception e){

            }
        }
        isNotStarted = true;
        singleWidgetUrl = null;
        singleWidgetId = null;
        return isDownloadFinished;
    }

    /**
     * Method to create the SH App folder for the particular location
     * @param links should have 5 parameter
     *      <li> 1. App catagory </li>
     *      <li> 2. App Name </li>
     *      <li> 3. App obj link </li>
     *      <li> 4. App bg link <li>
     *      <li> 5. App icon link </li>
     * @param appAddName if alreay widget to be downloading again, the appAddName should be 1, otherwise it is Empty
     * @return The App obj file download is success, it returns true othherwise false
     */
    private byte createApp(String[] links, String id, boolean isJar, boolean isSingleApp) {
        String wName = null;
        boolean isOrdinary = true;
        boolean isAppCatalog = false;
        if (links[0].compareTo("Startup") != 0) {
            wName = links[1];
            if (links[0].compareTo("Feature") == 0) {
                isOrdinary = false;
            } else if(links[0].toLowerCase().compareTo("appcatalog") == 0 || links[0].toLowerCase().compareTo("app catalog") == 0) {
                isOrdinary = false;
                isAppCatalog = true;
            }
        } else {
            wName = RecordManager.startupAppName;
        }
        String fName = RecordManager.getRecordStoreName(wName);
        Object[] obj = new Object[4];
        obj[0] = getConnectionBytes(links[2]); //Obj File
        if(links[3].length()>0)
            obj[1] = getConnectionBytes(links[3]); //Bg File
        else obj[1] = new ByteArrayOutputStream();
        if(links[4].length()>0){
            obj[2] = getConnectionBytes(links[4]); //icon File
        } else obj[2] = new ByteArrayOutputStream();
        if(links[5].length()>0){
            obj[3] = getConnectionBytes(links[5]); //Tile File
        } else obj[3] = new ByteArrayOutputStream();

        byte isFinished = 0;
        if(null != obj[0]){
              if(isSingleApp && (null == obj[1] || null == obj[2] || null == obj[3])){
                return isFinished;
              }
            isFinished = writeFile((ByteArrayOutputStream)obj[0], fName);
            if(isFinished == 1){
                if(null != obj[1] && ((ByteArrayOutputStream)obj[1]).size()>0){
                    writeFile((ByteArrayOutputStream)obj[1], RecordManager.getBackImage(fName));
                }
                if(null != obj[2] && ((ByteArrayOutputStream)obj[2]).size()>0){
                    writeFile((ByteArrayOutputStream)obj[2], RecordManager.getLogoImageName(fName));
                }
                if(null != obj[3] && ((ByteArrayOutputStream)obj[3]).size()>0){
                    writeFile((ByteArrayOutputStream)obj[3], RecordManager.getTileImageName(fName));
                }
                if (wName.compareTo(RecordManager.startupAppName) != 0) {
                    if (isOrdinary) {
                        updateAppName(RecordManager.newAppName, wName, false,isJar);
                    } else {
                        updateAppName(RecordManager.featureAppName, wName, isAppCatalog,isJar);
                    }
                }
                updateTrapeLink(id, links[0]); // bug id  12080
            } else if (isFinished == 2) {
                updateTrapeLink(id, links[0]); // bug id  5827
                Logger.loggerError("DownloadHandler -> Phone not have the sufficient Memory");
            }
        }
        return isFinished;
    }



    private void updateTrapeLink(String id, String aCatagory) {
        if (null != tLink) {
            int index = tLink.indexOf(id);
            if (index > -1) {
                int sIndex = index + id.length() + 1;
                sIndex = tLink.indexOf(":", sIndex);
                sIndex = tLink.indexOf(",", sIndex + 1);
                sIndex = tLink.indexOf(",", sIndex + 1);
                sIndex = tLink.indexOf(",", sIndex + 1);
                if (tLink.indexOf(",", sIndex + 1) > -1) {
                    sIndex = tLink.indexOf(",", sIndex + 1) + 1;
                } else {
                    sIndex = tLink.length();
                }
                index -= (aCatagory.length() + 1);
                if (tLink.length() > sIndex) {
                    if (index > 0) {
                        tLink = tLink.substring(0, index) + tLink.substring(sIndex);
                    } else {
                        tLink = tLink.substring(sIndex);
                    }
                } else {
                    tLink = null;
                }
                updateAppsUrl();
            }
        }
    }

    private void updateAppName(String rName, String wName, boolean isAppCatalog, boolean isJar) {
        String name = RecordManager.getAppNames(rName, false);
        //String name = getAppNames(rName, false);
        if (null != name) {
            if(isAppCatalog){ //CR 7230
                if(null != Settings.getAppCatalogName()){
                    if(Settings.getAppCatalogName().compareTo(wName) != 0){
                        name = Utilities.replace(name, Settings.getAppCatalogName()+"-j" ,wName+"-d");
                        name = Utilities.replace(name, Settings.getAppCatalogName()+"-d",  wName+"-d");
                        Settings.setAppCatalogName(wName);
                    }
                    else if (("^^" + name + "^^").indexOf("^^" + wName + "-j^^") > -1 && !isJar) {
                        name = Utilities.replace(name, Settings.getAppCatalogName() + "-j", wName + "-d");
                    } //Bug 10938
                    else if (("^^" + name + "^^").indexOf("^^" + wName + "^^") == -1 && ("^^" + name + "^^").indexOf("^^" + wName + "-d^^") == -1) {
                        name += "^^" + wName;
                        if (isJar) {
                            name += "-j";
                        } else {
                            name += "-d";
                        }
                    }
                } else {
                    Settings.setAppCatalogName(wName);
                    if (("^^" + name + "^^").indexOf("^^" + wName + "^^") == -1) {
                        name += "^^" + wName;
                        if (isJar) {
                            name += "-j";
                        } else {
                            name += "-d";
                        }
                    }
                }
            } else { //issue 10615
                if(("^^" + name + "^^").indexOf("^^" + wName + "-j^^")>-1){
                    if(!isJar)
                        name = Utilities.replace(name, wName+"-j" ,wName+"-d");
                } else if(("^^" + name + "^^").indexOf("^^" + wName + "-d^^")>-1){
                    if(isJar)
                        name = Utilities.replace(name, wName+"-d" ,wName+"-j");
                } else if(("^^" + name + "^^").indexOf("^^" + wName + "^^") == -1){
                    name += "^^" + wName;
                    if(isJar) name += "-j";
                    else name += "-d";
                }
            }
        } else {
            if(isAppCatalog){
                Settings.setAppCatalogName(wName);
            }
            name = wName;
            if(isJar) name += "-j";
            else name += "-d";
        }
        RecordStoreParser.UpdateRecordStore(rName, name.getBytes(), true);
    }

    /**
     * Method to formet the link array for the give text.
     * @param wTemp This is the single App link, it should be concatenated to 5 paramters with ':' and '," symbols
     *      App Catagory:App ID:App Name:App obj link,App bg link,App icon link (this is the format of the wTemp text)
     * @return it returns the object array, it should be have two parameter,
     *      <li> 1. App id (Stirng) </li>
     *      <li> 2. (String[]) other app details and links
     */
    private Object[] getLinkValue(String wTemp) {
        Object[] obj = new Object[2];
        String[] value = new String[6];

        //App Catagory
        int index = wTemp.indexOf(":");
        value[0] = wTemp.substring(0, index);
        wTemp = wTemp.substring(index + 1);

        //App Id
        index = wTemp.indexOf(":");
        obj[0] = wTemp.substring(0, index);
        wTemp = wTemp.substring(index + 1);

        //App Name
        index = wTemp.indexOf(":");
        value[1] = RecordManager.getActualWidgetName(wTemp.substring(0, index));
        wTemp = wTemp.substring(index + 1);

        //App Obj link
        index = wTemp.indexOf(",");
        value[2] = wTemp.substring(0, index);
        wTemp = wTemp.substring(index + 1);

        //App bg image linnk
        index = wTemp.indexOf(",");
        value[3] = wTemp.substring(0, index);
        wTemp = wTemp.substring(index + 1);

        //App Icon Link
        index = wTemp.indexOf(",");
        value[4] = wTemp.substring(0, index);
        wTemp = wTemp.substring(index + 1);

        //App Tild Link
        if ((index = wTemp.indexOf(",")) > -1) {
            value[5] = wTemp.substring(0, index);
        } else {
            value[5] = wTemp;
        }
        obj[1] = value;
        value = null;
        return obj;
    }

    /**
     * Method to download the widget from the give link, the link to be stored into the hashtable.
     * HashTable structure
     *                  1. Id = App id
     *                  2. element = String[], String array have 5 value,
     *                      a. Widget Catagory
     *                      b. widget Name
     *                      c. App obj link
     *                      d. App bg image link
     *                      e. App header image link
     */
    private boolean startDownload() throws Exception {
        boolean isNotMemory = true;
        try {
            if (urlTable.size() > 0) {
               // isNotStarted = false;
                Enumeration enuer = urlTable.keys();
                String id = null;
                String[] links = null;
                byte success = 0;
                boolean isContinue = true;
                while (isContinue && enuer.hasMoreElements()) {
                    id = (String) enuer.nextElement();
                    links = (String[]) urlTable.get(id);
                    success = createApp(links, id,false,false);
                    if (success == 1) {
                        ObjectBuilderFactory.GetKernel().isNewLoaded();
                        urlTable.remove(id);
                    } else if (2 == success) {
                        urlTable.remove(id); // bug id 5827
                        //isContinue = false; // bug id 5827
                        isNotMemory = false; // bug id 5827
                    }
                }
                enuer = null;
                links = null;
                id = null;
            }
        } catch (Exception e) {
            Logger.loggerError("DownloadHandler -> startDownload Exception " + e.toString());
            throw e;
        }
        return isNotMemory;
    }

    /**
     * Method to check any download is app is still pending
     * @return true-pending app is present, false- no pending app
     */
    public boolean isPendingAppToDownload() {
        if (urlTable.size() > 0 || !dappUrls.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Method to check the Download is alreay started or not
     * @return if download is started it returns true, otherwise false
     */
    public boolean isDownloadNotStarted() {
        return isNotStarted;
    }

    /**
     * Method to update the Apps pending url, for the particular record store, If not pending app to be download it will clear the
     * record Store, otherwise maintain the balance pending links. when the application/client is uninstall, the Os automatically removed this
     * record store
     */
    private void updateAppsUrl() {
        if (null != tLink) {
            byte[] ubyte = tLink.getBytes();
            RecordStoreParser.UpdateRecordStore(RecordManager.downloadURLRMS, ubyte, true);
        } else {
            RecordStoreParser.deleteRecordStore(RecordManager.downloadURLRMS, true);
        }
    }

    private void buildAppsUrl() {
        byte[] ubyte = RecordStoreParser.getRecordStore(RecordManager.downloadURLRMS);
        if (null != ubyte) {
            try{
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byteArrayOutputStream.write(ubyte);
                buildAppUrl(byteArrayOutputStream);
                byteArrayOutputStream.close();
                byteArrayOutputStream = null;
            }catch(Exception exception){
                Logger.loggerError("DownloadHandler->BuildAppsUrl->"+exception.toString());
            }
        }
    }

    private void buildAppUrl(ByteArrayOutputStream byteArrayOutputStream) {
        try{
            String id = null;
            String wList = new String(byteArrayOutputStream.toByteArray(),"utf-8");
            String wTemp = null;
            int index = 0;
            while (wList.length() > 0) {
                index = getFirstLinkIndex(wList);
                if(index == wList.length()){
                    wTemp = wList;
                    wList = "";
                } else {
                    wTemp = wList.substring(0,index);
                    wList = wList.substring(index+1);
                }
                Object[] obj = getLinkValue(wTemp);
                id = (String) obj[0];
                if (urlTable.contains(id)) {
                    urlTable.remove(id);
                } else {
                    if (null == tLink) {
                        tLink = wTemp;
                    } else {
                        tLink = tLink + "," + wTemp;
                    }
                }
                urlTable.put(id, ((String[]) obj[1]));
            }
        }catch(Exception e){
            Logger.loggerError("DownloadHandler->buildAppUrl "+e.toString());
        }
    }

    private void buildDappUrl() {
        try {
            RecordStoreParser rStoreParser = new RecordStoreParser();
            if (rStoreParser.openRecordStore(RecordManager.dappDownloadURLRMA, false,false,false)) {
                rStoreParser = null;
            } else {
                int count = rStoreParser.getNumRecords();
                String link = null;
                byte[] rbyte = null;
                for (int i = 1; i <= count; i++) {
                    rbyte = rStoreParser.getRecord(i);
                    link = new String(rbyte);
                    dappUrls.push(link);
                }
                rStoreParser.closeRecordStore();
                rStoreParser = null;
            }
        } catch (Exception e) {        }
    }

    private void updateDappUrl() {
        try {
            RecordStoreParser.deleteRecordStore(RecordManager.dappDownloadURLRMA, true);
        } catch (Exception e) {
        }
        try {
            if (!dappUrls.isEmpty()) {
                RecordStoreParser rStoreParser = new RecordStoreParser();
                if (rStoreParser.openRecordStore(RecordManager.dappDownloadURLRMA, true,false,false)) {
                    rStoreParser = null;
                } else {
                    int count = dappUrls.size();
                    byte[] rbyte = null;
                    String link = null;
                    Stack tmp = new Stack();
                    for (int i = 0; i < count; i++) {
                        link = (String) dappUrls.pop();
                        tmp.push(link);
                        rbyte = link.getBytes();
                        rStoreParser.addRecord(rbyte, 0, rbyte.length, true);
                    }
                    dappUrls = tmp;
                    rStoreParser.closeRecordStore();
                    rStoreParser = null;
                }
            }
        } catch (Exception e) { }
    }

//    private String getActualWidgetName(String wName){
//        int index1 = wName.indexOf("(");
//        int index = wName.indexOf(")");
//        if(index>-1 && index1>-1)
//            wName = wName.substring(0,index1) + wName.substring(index+1);
//        return wName.trim();
//    }
    private ByteArrayOutputStream getConnectionBytes(String url) {
       ByteArrayOutputStream byteArrayOutputStream = null;
        Logger.loggerError("Http Start Url :" + url);
        if (openConnection(url)) {
            byteArrayOutputStream = getConnectionBytes();
            closeConnection();
        }
        //#if VERBOSELOGGING
        //|JG|Logger.loggerError("Http end");
        //#endif
        return byteArrayOutputStream;
    }

    private ByteArrayOutputStream getConnectionBytes(){
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {            
                dIn = hConnection.openDataInputStream();
                int ch = (int)hConnection.getLength();
                if(ch < 1 || ch>Runtime.getRuntime().freeMemory()/2){
                    ch = (int)Runtime.getRuntime().freeMemory()/2;
                }
                byte[] rByte = new byte[ch];
                byteArrayOutputStream = new ByteArrayOutputStream();
                while ((ch = dIn.read(rByte)) != -1) {
                    byteArrayOutputStream.write(rByte,0,ch);
                }
                rByte = null;
            }catch (Exception e) {
                byteArrayOutputStream = null;
                Logger.loggerError("Download Data retrive Error " + e.toString());
            }
            if(null != dIn){
                try{
                    dIn.close();
                    dIn = null;
                }catch(Exception e){}
            }
         return byteArrayOutputStream;
    }

    /**
     * Method to set the Dwonloading URL  link with the full parameter
     * @param url
     * @param item
     * @return
     */
    public void deinitialize() {
        try {

            stopDownloadTimer();

            closeConnection();

            if (null != dIn) {
                dIn.close();
                dIn = null;
            }

            if (null != dout) {
                dout.close();
                dout = null;
            }
        } catch (Exception e) {
        }
    }

    class widgetDownloading extends TimerTask {

        public void run() {
            //CR 14741, 14742
            
            if(downloadProcesstype>0){
                reStartDownload();
                //Bug 14671
                downloadProcesstype = 0;
            }
            //bug 14820
            downloadContactGridThumb();
        }
    }
}

