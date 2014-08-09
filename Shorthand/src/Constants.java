/**
 * Constants Class to do initial Form Settings and to hold the Fixed Options
 *
 * @author - Hakunamatata
 * @version - v1.00.15
 * @copyright (c) John Mcdonnough 
 **/


public class Constants {
	
	public static final byte OPTIONS = 41;
        
        public static byte SIZE = 56;

        public static final String aName = ChannelData.getClientName();
        
        public static final String appName = "Shorthand";
        
        public static boolean isJG = false;
        
        /*
         * 1. Jan
         * 2. Feb
         * 3. Mar
         * 4. Apr
         * 5. May
         * 6. Jun
         * 7. July
         * 8. Aug
         * 9. Sep
         * 10 Oct
         * 11. Nov
         * 12. Dec
         * */
        public static String[] months = null;
        
        /**
         *  1. Settings
            2. Manage App
            3. App Name
            4. Get Info
            5. About Shorthand
            6. Exit Shorthand
            7. Security
            8. Ok
            9. Cancel
            10. Inbox
            11. Smart Search
            12. Remind me
            13. Dismiss
            14. My Shortcuts
            15. goto
            16. Accept
            17. Reply Stop
            18. Sort By App Name
            19. Delete
            20. Delete All
            21. Reply
            22. Forward
            23. Back
            24. Sort By Date
            25. New
            26. Send
            27. Previous Message
            28. Next Message
            29. Make Entry Shortcut
            30. Prompt For Input
            31. Edit
            32. Rename
            33. Hide
            34. UnHide
            35. Main Menu
            36. Save
            37. Shorthand home
            38. Shortcut this action
            39. Shorthand Inbox
            40. Goto
            41. Dismiss
            42. Options
            43. Clear
            44. Yes
            45. No
            46. Now
            47. Later
            48. Symbols
         *  49. Message Counter
         *  50. Retry
         *  51. Chat
         *  52. Login
         *  53. Upgrade
         *  54. Send Image
         *  55. Upload Image
         *  56. Capture
         *  57. Discard
         *  58. Record Audio
         *  59. Record
         *  60. Record Movie
         *  61. Use
         */
        public static String[] options = null;
        
        /**
         *  1. Would you like to place a call to
            2. Do you want to continue?
            3. Please check inbox later for response
            4. Shorthand is an App player with dozens of Apps. Find more Apps at www.shorthandmobile.
            5. or in the App catalog under the options menu. Our Apps provide access to text messaging systems of other companies. Unless otherwise noted, our Apps are not 
            authorized or endorsed by those companies. The use of the trademarks of those companies is only intended to identify text messaging systems and/or services 
            of those companies. This is a FREE Beta.
            6. I agree to the terms of service at www.shorthandmobile.
            7. /tos
            8. Re-attempt App download?
            9. This error is most likely due to your Internet settings not being properly set to allow for application downloads. Please check with your service provider 
            and then try again.
            10. Your client is not configured to install new apps yet. Please try again later.
            11. No Log message to send
            12. Log cleared
            13. Password Mismatch
            14. Do you want to delete this shortcut action?
            15. Are you sure that you would like to delete the App?
            16. Are you sure that you would like to delete all Apps?
            17. App is not currently installed. Please download the
            18. App using the App Catalog before using this feature.
            19. Welcome to Shorthand Mobile, BETA version V
            20. Copyright 2010 Shorthand Mobile, Inc.
            21. No Shorthand Apps are loaded. Please Click on Options>App Catalog to find Apps.
            22. No Shorthand Apps are loaded. Please exit Shorthand and open again
            23. No Shorthand apps are loaded
            24. A problem occurred during app download, this could be due to low signal strength in your area. Please try again later.
            25. We're sorry, your phone has insufficient memory to download this app.
            26. Unable to send. Please try again later
            27. texts sent and
            28. texts received during this session... Please see Options>Settings>Message Counter for monthly summary.
            29. Your message could not be sent. Please try again later
            30. No More Screens Available
            31. App is downloading please wait untill the download finish
            32. Do you want to save Password?
            33. Call Invoke not supported
            34. Call number Missing
            35. Url String is missing
            36. Browser not supported
            37. Do you want to delete this shortcut?
            38. Are you sure that you would like to delete the message?
            39. Name already exists, please select another name
            40. Value already exists
            41. Action Not Saved
            42. Action Saved
            43. Not Set Any Imediate Action
            44. Response is delayed. You will be alerted when it arrives.
            45. You have exceeded the recommended number of inbox messages
            46. Please set Phone number
            47. Are you sure that you would like to delete all inbox messages?
            48. Reply Sent
            49. Message Forwarded
            50. SMSClient File Missing
         *  51. Warning, you have sent
         *  52. text messages this month.
         *  53. Message could not be sent due to low coverage.
         *  54. (53)You said no to sending the message.
         *  55. (54)Log upload complete
         * 56. Unable to complete your request. Please select 'Retry' below and allow Shorthand to send message.
         * 57. Indiatimes SMS Browser is sending a SMS. This SMS will cost Rs. 3 and will be sent from SIM1.
         * 58. App download not completed fully. Please try again
         * 59. You said NO to connecting internet        
         * 60. KB uploaded and
         * 61. KB downloaded during this session... Please see Options>Settings>Data Sent And Received for monthly summary.
         * 62. Please wait until one SMS is received inside Shorthand
         * 63. Data setup is incomplete
         * 64. Are you sure that you want to clear the log?
         * 65. texts received,
         * 66.
         * 67.
         * 68. Your Connection  has been droopped. Click 'Reconnect below to re-establish it'.
         * 69. Do you want to log out of your chat services?
         * 70. Unable to save image to gallery
         */
        public static String[] popupMessage = null;

//200: The request has succeeded.
//201: The request has been fulfilled and resulted in a new resource being created.
//202: The request has been accepted for processing, but the processing has not been completed.
//203: The returned meta-information in the entity-header is not the definitive set as available from the origin server.
//204: The server has fulfilled the request but does not need to return an entity-body, and might want to return updated meta-information.
//205: The server has fulfilled the request and the user agent SHOULD reset the document view which caused the request to be sent.
//206: The server has fulfilled the partial GET request for the resource.
//300: The requested resource corresponds to any one of a set of representations, each with its own specific location, and agent- driven negotiation information is being provided so that the user (or user agent) can select a preferred representation and redirect its request to that location.
//301: The requested resource has been assigned a new permanent URI and any future references to this resource SHOULD use one of the returned URIs.
//302: The requested resource resides temporarily under a different URI. (Note: the name of this status code reflects the earlier publication of RFC2068, which was changed in RFC2616 from "moved temporalily" to "found". The semantics were not changed. The Location header indicates where the application should resend the request.)
//303: The response to the request can be found under a different URI and SHOULD be retrieved using a GET method on that resource.
//304: If the client has performed a conditional GET request and access is allowed, but the document has not been modified, the server SHOULD respond with this status code.
//305: The requested resource MUST be accessed through the proxy given by the Location field.
//307: The requested resource resides temporarily under a different URI.
//400: The request could not be understood by the server due to malformed syntax.
//401: The request requires user authentication. The response MUST include a WWW-Authenticate header field containing a challenge applicable to the requested resource.
//402: This code is reserved for future use.
//403: The server understood the request, but is refusing to fulfill it. Authorization will not help and the request SHOULD NOT be repeated.
//404: The server has not found anything matching the Request-URI. No indication is given of whether the condition is temporary or permanent.
//405: The method specified in the Request-Line is not allowed for the resource identified by the Request-URI.
//406: The resource identified by the request is only capable of generating response entities which have content characteristics not acceptable according to the accept headers sent in the request.
//407: This code is similar to 401 (Unauthorized), but indicates that the client must first authenticate itself with the proxy.
//408: The client did not produce a request within the time that the server was prepared to wait. The client MAY repeat the request without modifications at any later time.
//409: The request could not be completed due to a conflict with the current state of the resource.
//410: The requested resource is no longer available at the server and no forwarding address is known.
//411: The server refuses to accept the request without a defined Content- Length.
//412: The precondition given in one or more of the request-header fields evaluated to false when it was tested on the server.
//413: The server is refusing to process a request because the request entity is larger than the server is willing or able to process.
//414: The server is refusing to service the request because the Request-URI is longer than the server is willing to interpret.
//415: The server is refusing to service the request because the entity of the request is in a format not supported by the requested resource for the requested method.
//416: A server SHOULD return a response with this status code if a request included a Range request-header field , and none of the range-specifier values in this field overlap the current extent of the selected resource, and the request did not include an If-Range request-header field.
//417: The expectation given in an Expect request-header field could not be met by this server, or, if the server is a proxy, the server has unambiguous evidence that the request could not be met by the next-hop server.
//500: The server encountered an unexpected condition which prevented it from fulfilling the request.
//501: The server does not support the functionality required to fulfill the request.
//502: The server, while acting as a gateway or proxy, received an invalid response from the upstream server it accessed in attempting to fulfill the request.
//503: The server is currently unable to handle the request due to a temporary overloading or maintenance of the server.
//504: The server, while acting as a gateway or proxy, did not receive a timely response from the upstream server specified by the URI or some other auxiliary server it needed to access in attempting to complete the request.
//505: The server does not support, or refuses to support, the HTTP protocol version that was used in the request message.
          public static int[] httpErrorCode = new int[]{
          200,
          201,
          202,
          203,
          204,
          205,
          206,
          300,
          301,
          302,
          303,
          304,
          305,
          307,
          400,
          401,
          402,
          403,
          404,
          405,
          406,
          407,
          408,
409,410,411,412,413,414,415,416,
417,500,501,502,503,504,505};
          public static String[] httpErrorMsg = new String[] { " The request has succeeded.",
" The request has been fulfilled and resulted in a new resource being created.",
" The request has been accepted for processing, but the processing has not been completed.",
" The returned meta-information in the entity-header is not the definitive set as available from the origin server.",
" The server has fulfilled the request but does not need to return an entity-body, and might want to return updated meta-information.",
" The server has fulfilled the request and the user agent SHOULD reset the document view which caused the request to be sent.",
" The server has fulfilled the partial GET request for the resource.",
" The requested resource corresponds to any one of a set of representations, each with its own specific location, and agent- driven negotiation information is being provided so that the user (or user agent) can select a preferred representation and redirect its request to that location.",
" The requested resource has been assigned a new permanent URI and any future references to this resource SHOULD use one of the returned URIs.",
" The requested resource resides temporarily under a different URI. (Note: the name of this status code reflects the earlier publication of RFC2068, which was changed in RFC2616 from 'moved temporalily' to 'found'. The semantics were not changed. The Location header indicates where the application should resend the request.)",
" The response to the request can be found under a different URI and SHOULD be retrieved using a GET method on that resource.",
" If the client has performed a conditional GET request and access is allowed, but the document has not been modified, the server SHOULD respond with this status code.",
" The requested resource MUST be accessed through the proxy given by the Location field.",
" The requested resource resides temporarily under a different URI.",
" The request could not be understood by the server due to malformed syntax.",
" The request requires user authentication. The response MUST include a WWW-Authenticate header field containing a challenge applicable to the requested resource.",
" This code is reserved for future use.",
" The server understood the request, but is refusing to fulfill it. Authorization will not help and the request SHOULD NOT be repeated.",
" The server has not found anything matching the Request-URI. No indication is given of whether the condition is temporary or permanent.",
" The method specified in the Request-Line is not allowed for the resource identified by the Request-URI.",
" The resource identified by the request is only capable of generating response entities which have content characteristics not acceptable according to the accept headers sent in the request.",
" This code is similar to 401 (Unauthorized), but indicates that the client must first authenticate itself with the proxy.",
" The client did not produce a request within the time that the server was prepared to wait. The client MAY repeat the request without modifications at any later time.",
" The request could not be completed due to a conflict with the current state of the resource.",
" The requested resource is no longer available at the server and no forwarding address is known.",
" The server refuses to accept the request without a defined Content- Length.",
" The precondition given in one or more of the request-header fields evaluated to false when it was tested on the server.",
" The server is refusing to process a request because the request entity is larger than the server is willing or able to process.",
" The server is refusing to service the request because the Request-URI is longer than the server is willing to interpret.",
" The server is refusing to service the request because the entity of the request is in a format not supported by the requested resource for the requested method.",
" A server SHOULD return a response with this status code if a request included a Range request-header field , and none of the range-specifier values in this field overlap the current extent of the selected resource, and the request did not include an If-Range request-header field.",
" The expectation given in an Expect request-header field could not be met by this server, or, if the server is a proxy, the server has unambiguous evidence that the request could not be met by the next-hop server.",
" The server encountered an unexpected condition which prevented it from fulfilling the request.",
" The server does not support the functionality required to fulfill the request.",
" The server, while acting as a gateway or proxy, received an invalid response from the upstream server it accessed in attempting to fulfill the request.",
" The server is currently unable to handle the request due to a temporary overloading or maintenance of the server.",
" The server, while acting as a gateway or proxy, did not receive a timely response from the upstream server specified by the URI or some other auxiliary server it needed to access in attempting to complete the request.",
" The server does not support, or refuses to support, the HTTP protocol version that was used in the request message."
};

        
        /**
         *  1. About
            2. Home
            3. Ad Response
            4. All
            5. Off
            6. On
            7. Disabled
            8. EnabledNo Shorthand apps are loadedNo Shorthand apps are loaded
            9. App
            10. sent
            11. received
            12. Text Messages Sent and Received
            13. tel:
            14. http://
            15. The
            16. No sent and received Message(s)
            17. RX LOG
            18. TX LOG
            19. New Message From
            20. NewMessage
            21. Please Set
            22. App Catalog
            23. SMS msgs
            24. Improper Entry
         *  25. No Code
         *  26. Select
         *  27. New Chat Message From
         *  28. ChatMessage
         *  29. (ME)
         *  30. Respond now?
         *  31. Receiving message
         *  32. Of
         *  33. No sent and received Data(s);
         *  34. Data Uploaded and Downloaded
         *  35. KB uploaded
         *  36. KB downloaded
         *  37. SMS
         *  38. Data
         *  39. Chat From
         *  40. Message+ User
         *  41. Non-Message+ User
         *  42. You are the group owner
         *  43. You are a group member
         *  44. You are the shout owner
         *  45. You are a shout member
         *  46. Group owner:
         *  47. Shout owner:
         */
        public static String[] appendText = null;
        
        /**
         *  SMS Opt-in
            Call Advertiser
            Have Advertiser Call
            Go to Advertiser
            Send Coupon
            Get more info
            Find Location
         */
        public static String[] landingMenu = null;
        
        /**
         *  Revert to Original App
            Delete App
            Delete All Apps
         */
        public static String[] advanceEditOptionsForSMS = null;
        
        /**
         *  Security
            Audio Alerts
            Message Counter
            Debug
         *  Grid
         *  List
         *  Switch to SMS
         *  Switch to Data
         *  Data Sent And Received
         */
        public static String[] settingsMenu = null;
        
        /**
         *  Enter Shorthand PIN^
            Shorthand PIN
         */
        public static String[] securityMenu = null;
        
        /**
         *  Upload Log
            Empty Log
            Debug
         */
        public static String[] debugMenu = null;
        
        /**
         *  1. Application Loadding Error
            2. User Profile Loading Error
            3. Favorite Profile Loading Error
            4. Item Handle Error
            5. Option Handle Error
            6. Handle Rename Error
            7. Message Option Handle Error
            8. Renaming Error
            9. Message Receive Error
            10. Launching Error
            11. Initial Menu Loading Error
            12. Entry Screen Display Error
            13. Entry Handle Error
            14. Display Screen Display Error
            15. View Screen Loading Error
            16. Message Sending Error
            17. Display Handle Error
            18. Inbox Loading Error
         */
        public static String[] errorMessage = null;
        
        /**
         *  1. Confirm Enter PIN
            2. 4-Digit Enter PIN
            3. Select an Option
            4. Settings
            5. Security
            6. Select Action Shortcut
            7. Entry SmartSearch Value
            8. Message Counter
            9. Alert
            10. Data charges may apply
            11. Welcome
            12. Terms of Service
            13. Warning
            14. Success
            15. Deleting App, notifying server
            16. Sending message problem occurred during app download, this c
            17. Message Box
            18. Select Symbol
            19. Retrieving Apps
            20. Log uploading, please wait
            21. Retrieving App
            22. Preparing Apps, please wait
            23. No Messages
            24. Message Sent and Received Counter
            25. Downloading
            26. Enter Shortcut Name
            27. Failed
            28. Enter Message
            29. Enter Phone Number
         *  30. It take arround 2 mins...
         *  31. 
         *  32. Reconnect to Shorthand?
         *  33. Downloading Image
         *  34. Record Audio
         *  35. Record Movie
         *  36. Select Picture
         *  37. Contact Profile
         */
        public static String[] headerText = null;
        
//	/**
//	 * Constant Char Two Dimensional Array Options.Common for the UI And BackEnd
//	 */
//	public static char[][] options = new char[][] { 
//		{'S','e','t','t','i','n','g','s'},                                  //Settings 0
//		{'M','a','n','a','g','e',' ','A','p','p'},                          //Manage App 1
//		{' '},                                                              //reserved 2 // Left Options string Profilehandler
//		{'G','e','t',' ','I','n','f','o'},                                  //Get Info 3
//		("About "+ChannelData.getClientName()).toCharArray(),                 //AboutShorthand 4
//		("Exit "+ChannelData.getClientName()).toCharArray(),                     // ExitShorthand 5
//		{'S','e','c','u','r','i','t','y'},                                  //Security 6 
//		{'O','k'},                                                          //Ok 7
//		{'C','a','n','c','e','l'},                                          //Cancel 8
//		{'I','n','b','o','x'},                                              //DebugEnabled 9
//		{'S','m','a','r','t',' ','S','e','a','r','c','h'},                  //Smart Search 10
//		{'R','e','m','i','n','d',' ','m','e'},                              //Remind Me 11
//		{'D','i','s','m','i','s','s'},                                      //Dismiss 12
//		{'M','y',' ','S','h','o','r','t','c','u','t','s'},                  //My Shortcuts 13
//		{'G','o','t','o'},                                                  //GoTo Using for View Screen option  14(Goto + ProfileName)
//		{'A','c','c','e','p','t'},                                          //Accept 15
//		{'R','e','p','l','y',' ','S','t','o','p'},                             //Reply stop 16
//		{'S','o','r','t',' ','B','y',' ','A','p','p',' ','N','a','m','e'}, //Sort By Profile Name 17
//		{'D','e','l','e','t','e'},                                          //Delete 18
//		{'D','e','l','e','t','e',' ','A','l','l'},                          //DeleteAll 19
//		{'R','e','p','l','y'},                                              //Reply 20
//		{'F','o','r','w','a','r','d'},                                      //Forward 21
//		{'B','a','c','k'},                                                  //Back 22
//		{'S','o','r','t',' ','B','y',' ','D','a','t','e'},                  //SortbyDate 23
//		{'N','e','w'},                                                      //New 24
//		{'S','e','n','d'},                                                  //Send 25
//		{'P','r','e','v','i','o','u','s',' ','M','e','s','s','a','g','e'},  //ViewPreviousSMS 26
//		{'N','e','x','t',' ','M','e','s','s','a','g','e'},                  //ViewNextSMS 27
//		{'M','a','k','e',' ','E','n','t','r','y',' ','S','h','o','r','t','c','u','t'}, //MakeEntryShortcut 28
//		{'P','r','o','m','p','t',' ','F','o','r',' ','I','n','p','u','t'},  //PromptForInput 29
//		{'E','d','i','t'},                                                  //Edit 30
//		{'R','e','n','a','m','e'},                                          //Rename 31
//		{'H','i','d','e'},                                                  //Hide 32
//		{'U','n','H','i','d','e'},                                          //UnHide 33
//		{'M','a','i','n',' ','M','e','n','u'},                              //MainMenu 34
//		{'S','a','v','e'},                                                 //save 35
//                (ChannelData.getClientName()+" Home").toCharArray(),               //Shorthand home 36 
//		{'S','h','o','r','t','c','u','t',' ','t','h','i','s',' ','a','c','t','i','o','n'}, //SaveQuery 37
//		(ChannelData.getClientName()+" Inbox").toCharArray(),                 //Shorthand Inbox 38
//		{'G','o','t','o'},                                                  //Goto 39
//		{'D','i','s','m','i','s','s'},                                      //Dismiss 40
//		{'O','p','t','i','o','n','s'},                                      //Options 41
//		{'C','l','e','a','r'},                                              // Clear 42
//                {'Y','e','s'},                                                      //Yes 43
//                {'N','o'},                                                          //No 44
//                {'N','o','w'},                                                      //Now 45
//                {'L','a','t','e','r'},                                              //Later 46
//                {'S','y','m','b','o','l','s'},                                      //Symbols 47
//                {'Message Counter '},                                                 //48
//                {'Retry '},                                                           ///49
//                {'Chat '},                                                            //50
//                {'ReConnect '},                                                       //51
//                {' '},
//                {' '},
//                {' '},
//                {' '},
//                {' '},
//                {' '},
//                {' '},
//                {' '},
//                {' '},
//                {' '},
//                {' '}
//	};
//        
        /**
         * Method to set the Dynamic Options
         *
         * @param dopt - Variable will contain Dynamic options
         *
         * @return temp - Variable will contain the Index of the dynamic Options
         **/
        public static byte[] setDOpt(String[] dopt,boolean isAppCatalog, boolean isTrunCate){ // CR 7230
            byte[] temp =null;
            int count =0;
            if(null != dopt && (count=dopt.length)>0){
                temp = new byte[count];
                for(int i=0;i<count;i++){
                    if(isTrunCate)
                        Constants.options[SIZE+i] = dopt[i].substring(0,dopt[i].length()-2);//.toCharArray();
                    else Constants.options[SIZE+i] = dopt[i];
                    if(isAppCatalog && null != Settings.getAppCatalogName() && (dopt[i].compareTo(Settings.getAppCatalogName()+"-d") == 0
                            || dopt[i].compareTo(Settings.getAppCatalogName()+"-j") == 0)){ // cr 7230
                        temp[i] = temp[0];
                        temp[0] = (byte)(SIZE+i);
                    } else
                    temp[i] =(byte)(SIZE+i);
                }
            }
            dopt = null;
            return temp;
        }

        public static int[] setDtOptInt(String[] dopt,boolean isAppCatalog, boolean isTrunCate){ // CR 7230
            int[] temp =null;
            int count =0;
            if(null != dopt && (count=dopt.length)>0){
                temp = new int[count];
                for(int i=0;i<count;i++){
                    if(isTrunCate)
                        Constants.options[SIZE+i] = dopt[i].substring(0,dopt[i].length()-2);//.toCharArray();
                    else Constants.options[SIZE+i] = dopt[i];
                    if(isAppCatalog && null != Settings.getAppCatalogName() && (dopt[i].compareTo(Settings.getAppCatalogName()+"-d") == 0
                            || dopt[i].compareTo(Settings.getAppCatalogName()+"-j") == 0)){ // cr 7230
                        temp[i] = temp[0];
                        temp[0] = (SIZE+i);
                    } else
                    temp[i] =(SIZE+i);
                }
            }
            dopt = null;
            return temp;
        }

        /**
         * Method to set the Mobile Basic keys and set the Form Height and Width
         **/
        public static void setApplicationFilePath(){
           //setModelKeys();
            if (UISettings.formWidth == 0) {
                UISettings.formWidth = (short) ObjectBuilderFactory.getPCanvas().getWidth();
                UISettings.formHeight = (short) (ObjectBuilderFactory.getPCanvas().getHeight());
            }
            setUiSettings();
            if(null == options)
                setLanguage();
        }
        
        public static void setLanguage(){
            String jaddetails = DownloadHandler.getInstance().getConfigDetails(RecordManager.languageConfig);
            String[] value = null;
            String[] tempoopt = null;
            options = new String[15];
            SIZE = 0;
            if(null != jaddetails){
                value = Utilities.split(jaddetails, "|||");
                int count = value.length;
                for(int i=0;i<count;i+=2){
                    if("options".compareTo(value[i].toLowerCase()) == 0){
                        tempoopt = Utilities.split(value[i+1],"^");
                        SIZE = (byte)tempoopt.length;
                        options = new String[tempoopt.length+15];
                        System.arraycopy(tempoopt, 0, options, 0, tempoopt.length);
                    } else if("appenedtext".compareTo(value[i].toLowerCase()) == 0) {
                        appendText = Utilities.split(value[i+1],"^");
                        UISettings.IMPROPERENTRY = Constants.appendText[23];
                    } else if("headertext".compareTo(value[i].toLowerCase()) == 0){
                        headerText = Utilities.split(value[i+1],"^");
                    } else if("popupmessage".compareTo(value[i].toLowerCase()) == 0){
                        popupMessage = Utilities.split(value[i+1],"^");
                    } else if("errormessage".compareTo(value[i].toLowerCase()) == 0){
                        errorMessage = Utilities.split(value[i+1],"^");
                    } else if("securitymenu".compareTo(value[i].toLowerCase()) == 0){
                        securityMenu = Utilities.split(value[i+1],"^");
                    } else if("settingsmenu".compareTo(value[i].toLowerCase()) == 0){
                        settingsMenu = Utilities.split(value[i+1],"^");
                    } else if("advancemenu".compareTo(value[i].toLowerCase()) == 0){
                        advanceEditOptionsForSMS = Utilities.split(value[i+1],"^");
                    } else if("debugmenu".compareTo(value[i].toLowerCase()) == 0){
                        debugMenu = Utilities.split(value[i+1],"^");
                    } else if("landingmenu".compareTo(value[i].toLowerCase()) == 0){
                        landingMenu = Utilities.split(value[i+1],"^");
                    } else if("month".compareTo(value[i].toLowerCase()) == 0){
                        months = Utilities.split(value[i+1], "^");
                    }
                }
            }
        }
        
//        private static void setModelKeys(){
//            if(Build.MANUFACTURER.indexOf("SONY")>-1){
//                UISettings.MAINMODE = Canvas.KEY_STAR;
//                UISettings.SUBMODE = 0;
//                UISettings.SPACEKEYCODE = Canvas.KEY_POUND;
//              //  UISettings.SYMBOLTEXT =".,-?!'@:;/()1";
//                
//            }else if(Build.MANUFACTURER.indexOf("MOTOROLA")>-1){
//                UISettings.MAINMODE = Canvas.KEY_POUND;
//                UISettings.SUBMODE = Canvas.KEY_NUM0;
//                UISettings.SPACEKEYCODE = Canvas.KEY_STAR;
////                UISettings.SYMBOLTEXT =".?!@,()#&/-:;'_%+=*[]~";
//                //UISettings.SPACETEXT =" ";
//            } else if(Build.MANUFACTURER.indexOf("NOKIA")>-1){
//                UISettings.SUBMODE = 0;
//                UISettings.MAINMODE = Canvas.KEY_POUND;
//                UISettings.SPACEKEYCODE = Canvas.KEY_NUM0;
//                UISettings.SPACETEXT = "0 ";
//                
////                UISettings.SUBMODE = Build.SUB_MODE;
////                UISettings.MAINMODE = Build.MAIN_MODE;
////                UISettings.SPACEKEYCODE = Build.SPACEKEY_CODE;
//                
//                //UISettings.SYMBOLTEXT = ".,'-?!1@:/";
//            } else if(Build.MANUFACTURER.indexOf("LG")>-1){
//                UISettings.SUBMODE = 0;
//                UISettings.MAINMODE = Canvas.KEY_POUND;
//                UISettings.SPACEKEYCODE = Canvas.KEY_NUM0;
//                // UISettings.SYMBOLTEXT = ".,'@?!-:/1";
//                UISettings.SPECIALSYMBOLSCODE = Canvas.KEY_STAR;
//                UISettings.SPECIALSYMBOLS = "@";
//                 UISettings.SPACETEXT = "0 ";
//            }else if(Build.MANUFACTURER.indexOf("SAMSUNG")>-1){
//                UISettings.SUBMODE = 0;
//                UISettings.MAINMODE = Canvas.KEY_STAR;
//                UISettings.SPACEKEYCODE = Canvas.KEY_POUND;
//               // UISettings.SYMBOLTEXT = ".,'-?!@:/1";
//                UISettings.SPECIALSYMBOLSCODE = Canvas.KEY_NUM0;
//                UISettings.SPECIALSYMBOLS = "+=<>$%&0";
//            }
//        }
        
        /**
         * Method to Set the Form Secondary header size, Footer size  and Number of Menu itesm will show in the form
         *
         **/
        private static void setUiSettings(){
            int height = ChannelData.getIconHeight();
            int bheight = UISettings.formHeight - (height*3);
            UISettings.numOfMenuItems =(byte)((bheight/height)+1);
            UISettings.itemHeight = (byte)height;
            UISettings.headerHeight = (byte)height;
            UISettings.secondaryHeaderHeight =(byte)height;
            UISettings.footerHeight = (byte)height;
        }

}
