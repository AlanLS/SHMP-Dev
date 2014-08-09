
import generated.Build;

public class Advertisement {

    private byte preState = -1;

        // Variable is having the Advertisement default Menu items
//	private final String[] landingMenu = new String[] {"SMS Opt-in","Call Advertiser",
//                "Have Advertiser Call","Go to Advertiser","Send Coupon","Get more info",
//                "Find Location" };

        //Variable to hold the message state
	private byte msgst;

        // Variable to hold the Current Advertisement state
	private byte currst;

        // Object to hold the Advertisement Informations
        private AdData selAd = null;

        // Variable to hold the Landing page index
        private int landPageIndex = 12;

        /**
         * Method to retrieve the Advertisement Informations based on the requested category.
         *
         * @param adcat - Variable to hold the Profile Category.(Not Null)
         *
         * @return selAd - Variable to hold the selected Advertisement Informations.
         */
	public AdData GetAdvertisement(String profileCat) {
            AdvertisementParser adParser = new AdvertisementParser();
            selAd = adParser.getAdvertisement(profileCat);
            adParser =null;
            return selAd;
	}

    /**
     * Method to Load Menu items or previous State
     *
     * @param selAdObj - Object will contain the Selected Advertisement Informations.
     * @param pState - Variable to hold the Previous State.
     */

	public void LoadInitialMenu(AdData selAdObj,byte pState) {
            if(pState>-1)
                preState = pState;
            selAd = selAdObj;
            if (null != selAd){
                setPresenterDTO();
                load(0);
            } else{
                loadPreviousScreen();
            }
	}

        private void setPresenterDTO(){
            PresenterDTO.setHdrLogo(RecordManager.getSTLogoLocation());
            PresenterDTO.setBgImage(RecordManager.getSTBgLocation());
            PresenterDTO.setHdrtxt(Constants.aName);
        }
        /**
         * Method to load the Current Advertisement State
         */
	public void loadCurrentState() {
            setPresenterDTO();
	    load(0);
	}

        /**
         * Method to Load the Advertisement Menu items.
         *
         * @param item - Variable will contain the Ad menu items.(Not Null)
         */
	private void loadMenu(String[] items) {
            ObjectBuilderFactory.GetKernel().initializeScreen(KernelConstants.FRENDEND_MENU);
		PresenterDTO.setLOptByte((byte)-1);
		MenuResponseDTO _responseDto = new MenuResponseDTO();
                PresenterDTO.setHdrtxt(Constants.aName);
		_responseDto.setSecHdrTxt(Constants.headerText[2]); //Select an Option
		_responseDto.setLopttext((byte)40);
		_responseDto.setItemnamelist(items);
                _responseDto.setBannerText(selAd.getAdText());
                _responseDto.setBannerStyle((byte)0);
		_responseDto.setItemidlist(getItemId(items.length));
		/** TBR - Need to load menu items* */
                ObjectBuilderFactory.GetKernel().displayScreen(_responseDto,true);
		_responseDto = null;
	}

        /**
         * Method to Menu items Id.
         *
         * @param len - Variable will contain the number of Menu items.(Not Null)
         *
         * @return ids - Array Variable will contain the Menu items Id.(Not NUll)
         */
	private int[] getItemId(int len)
	{
            boolean[] lPageIndex = selAd.getLPag();
            int length = lPageIndex.length;
            int j=0;
            int[] ids = new int[len];
            for(int i=0;i<length;i++)
                if(lPageIndex[i] && Constants.landingMenu.length>i)
                    ids[j++] = i;
            if(null != selAd.getCAdText1())
                ids[j++] = 10;
            if(null != selAd.getCAdText2())
                ids[j++] = 11;
            return ids;
	}

        /**
         * Method to Load the Previous Screen
         */
        private void loadPreviousScreen(){
            byte pState =preState;
            preState = -1;
            if(pState<0)
                pState = KernelConstants.BACKEND_PROFILEHANDLER;
            ObjectBuilderFactory.GetKernel().setPreviousBkState(pState);
            ObjectBuilderFactory.GetKernel().deInitializeHandlers(KernelConstants.BACKEND_ADDHANDLER);
        }

        /**
         * Method to Load the Previous Screen
         *
         * @param itemId - Variable will contain the selected Menu Id (Future Purpose).
         * @param optText - Variable will contain the Selected Option.
         */
	public void handleOptionSelected(int itemId, byte optText) {
		if (40 == optText)
                    loadPreviousScreen();
	}

        /**
         * Method to make the different operations based on the Menu id
         *  1. Make the call
         *  2. Open the Browser
         *  3. Laod the previous Screen
         *
         * @param id - Variable will contain the Menu id.(Not Null)
         *
         * @throws handleItemException.
         */
        public void handleItemSelection(int id) {
            try{
             if(id == 1){
                    ObjectBuilderFactory.getControlChanel().sendAdCallItemSelectAction(selAd.getPrId(),selAd.getAdId(),id);
                    if(Build.CALL_INVOKE){
                        loadMessageBox(7, Constants.popupMessage[0] + " "+selAd.getPNo()+"?", 3, Constants.headerText[8]);
                    } else invokeCall(true);
                }
            else if(id == 3){
             
              try{
                        String url = ChannelData.geturl(selAd.getUrl());
                        loadPreviousScreen();
                        if(null != url){ //  1.www.google.com   , 2. mobile.google.com   ,  3. http://www.google.com
                            if(!url.startsWith(Constants.appendText[13])){
                                //#if VERBOSELOGGING
                                //|JG|Logger.debugOnError("Invoke URL: http://"+url);
                                //#endif
                                ObjectBuilderFactory.getPCanvas().platformRequest(Constants.appendText[13]+url);
                            }else {
                                //#if VERBOSELOGGING
                                //|JG|Logger.debugOnError("Invoke URL:"+url);
                                //#endif
                                ObjectBuilderFactory.getPCanvas().platformRequest(url);
                            }
                        } else loadMessageBox(0, "Url Missing", 0,null);
                    }catch(Exception handleMsgException){}
//                    loadMessageBox(5, Constants.popupMessage[1], 2,Constants.headerText[9]);
               } //CR 12815
             else{
                    ObjectBuilderFactory.getControlChanel().sendAdMenuItemSelectAction(selAd.getPrId(),selAd.getAdId(),id);
                    loadPreviousScreen();
                    loadMessageBox(0,Constants.popupMessage[2], 0,null);
                }
            }catch(Exception handleItemException){}
        }

        private void invokeCall(boolean invoke){
            try{
                String pNo = Constants.appendText[12] + selAd.getPNo();
                loadPreviousScreen();
                if(invoke)
                    ObjectBuilderFactory.GetProgram().platformRequest(pNo);
//                    ObjectBuilderFactory.getPCanvas().platformRequest(pNo);
            }catch(Exception e){}
        }

        /**
         * Method to Load the Message Box to display
         *
         * @param msgType - Variable will contain the message Type.
         * @param msg - Variable will contain the message to display.
         * @param msgst - Variable will contain the message status.
         */
	private void loadMessageBox(int msgType, String msg, int msgst, String hText) {
		this.msgst =(byte)msgst;
		ObjectBuilderFactory.GetKernel().displayMessageBox(msgType, msg,hText);
	}

        /**
         * Method to handle different operations based on the MessageBox options.
         *  1. Laod previous Screen
         *  2. Open the Browser.
         *
         * @param status - if the variable will true , different operations will work.
         *
         * @throws handleMsgException
         */
        public void handleMessageBox(boolean status) {
            if (status) {
                if (1 == msgst){ // Error state
                        loadPreviousScreen();
                } else if(2 == msgst){
                    try{
                        String url = ChannelData.geturl(selAd.getUrl());
                        loadPreviousScreen();
                        if(null != url){ //  1.www.google.com   , 2. mobile.google.com   ,  3. http://www.google.com
                            if(!url.startsWith(Constants.appendText[13])){
                                //#if VERBOSELOGGING
                                //|JG|Logger.debugOnError("Invoke URL: http://"+url);
                                //#endif
                                ObjectBuilderFactory.getPCanvas().platformRequest(Constants.appendText[13]+url);
                            }else {
                                //#if VERBOSELOGGING
                                //|JG|Logger.debugOnError("Invoke URL:"+url);
                                //#endif
                                ObjectBuilderFactory.getPCanvas().platformRequest(url);
                            }
                        } else loadMessageBox(0, "Url Missing", 0,null);
                    }catch(Exception handleMsgException){}
                } else if(3 == msgst){
                    invokeCall(true);
                }
            } else if(3 == msgst){
                invokeCall(false);
            }
        }

        /**
         * Method to Load the Error MessageBox.
         *
         * @param exp - Variable will contain the Exception Text.
         * @param message - Variable will contain the User error message.
         */
	public void LoadErrorMessageScreen(Exception exp,String message) {
            Logger.loggerError("SMSAdvertisement-> "+exp.toString());
            loadMessageBox(4, message, 1,null);
	}

        /**
         * Method to Load the Menu items
         *
         * @param curload - Variable to keep the current state.(Future purpose).
         */
	private void load(int curload) {
            currst = (byte)curload;
            if (0 == curload) {
                String[] items = GetInitialMenu();
                if (null != items && items.length > 0)
                    loadMenu(items);
                else
                    loadPreviousScreen();
            }
	}

        /**
         * Method to retrieve Menu items for the selected Advertisement.
         *
         * @return items - Variable will contain the Menu items.It may be null.
         */
	private String[] GetInitialMenu() {
            String[] items = null;
            if (null != selAd) {
                boolean[] lPage = selAd.getLPag();
                int len;
                if(null != lPage && (len = lPage.length)>0)
                {
                    String[] temp = new String[landPageIndex];
                    int j=0;
                    for(int i=0;i<len;i++){
                        if(lPage[i]){
                            if(i<Constants.landingMenu.length)
                                temp[j++] = Constants.landingMenu[i];
                        }
                    }
                    if(null != selAd.getCAdText1())
                        temp[j++] = selAd.getCAdText1();
                    if(null != selAd.getCAdText2())
                        temp[j++] = selAd.getCAdText2();
                    if(j>0){
                        if(j<landPageIndex){
                            items = new String[j];
                            System.arraycopy(temp,0,items,0,j);
                        }else items = temp;
                    }
                    temp =null;
                }
            }
            return items;
	}

        /**
         * Method to Null the Objects and Collect the garbages.
         */
	public void DeInitialize() {
            selAd=null;
            Runtime.getRuntime().gc();
	}
}
