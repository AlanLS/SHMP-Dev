

/*
 * ProfileParser.java
 *
 * Created on September 21, 2007, 1:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
import java.util.Calendar;
import java.util.Date;
import java.util.Stack;

public class ProfileParser {

	// Global members
	// private String floc = null;
	private ByteArrayReader dis = null;

	// private ProfileHeader infoobj;
	// private Menu currmobj;
	// private IObjPersistor curraobj;
	private Object currobj = null;

	// logical variables
	private int initialid = -1;

        //first Level Goto Id
        private int fGotoId = -1;

        private int eGotoId = -1;

	private Stack actionseq;

	private MemorizedVariable memvar = null;

        private ProfileProperty ppro = null;

	// to identify the type of action
	private byte currtype; // Type specified in ProfileTypeConstant Class

	private String folderloc; // profile folder location

        private String memLoc = null;

        private int curPos = 0;

        boolean[] header = null;

        private RecordStoreParser rStoreParser = null;

        private int lastSMSSendActionId = -1;        
        
	/**
	 *
	 */
	public ProfileHeader initialize(String loc,boolean isNotChatLoad ) throws Exception {
            folderloc = loc;
            ProfileHeader _phobj = null;
            if (initializeStream(loc)) {
                _phobj = (ProfileHeader)getProfileTagObject(0);
                if(null != _phobj){
                    memLoc = _phobj.getName();
                    fGotoId = _phobj.getFgotoId();
                    eGotoId = _phobj.getEGotoid();
                    _phobj = getProfileProperty(_phobj,true);
                    setMemorizedValue();
                    actionseq = new Stack();
                    initialid = _phobj.getPinitid();
                    if(fGotoId == -1 && isNotChatLoad)//bug 12767
                        fGotoId = _phobj.getSGotoId();
                }
            }
            return _phobj;
	}

	/**
	 *
	 */
	public void shutDownParser() throws Exception {
            deInitializeInputStream();
            if(null != memvar){
                memvar.deinitialize();
                memvar = null;
            }
            if(null != actionseq){
                actionseq.removeAllElements();
                actionseq = null;
            }
            fGotoId  = -1;
            eGotoId = -1;
            ppro = null;
            currobj = null;
	}

	/**
	 *
	 */
	public SMSProfileHeader getProfile(String loc){
            SMSProfileHeader _sphobj = null;
            try {
                if (initializeStream(loc)) {
                    ProfileHeader _phobj = (ProfileHeader)getProfileTagObject(0);
                    if(_phobj != null)
                    {
                         memLoc = _phobj.getName();
                        _phobj = getProfileProperty(_phobj,false);
                        ppro = null;
                        _sphobj = new SMSProfileHeader();
                        _sphobj.setName(_phobj.getName());
                        _sphobj.setProfileLocation(loc);
                        if(null != _phobj.getScode() && _phobj.getScode().length>0)
                            _sphobj.setSC(_phobj.getScode()[0]);
                        _sphobj.setMCFormat(_phobj.getMSCF());
                        _sphobj.setLaunchIcon(RecordManager.getLogoImageName(RecordManager.getRecordStoreName(loc)));
                        _sphobj.setTildIcon(RecordManager.getTileImageName(RecordManager.getRecordStoreName(loc)));
                        _sphobj.setPDes(_phobj.getDesc());
                        _sphobj.setPId(_phobj.getPId());
                        _sphobj.setPUsg(_phobj.getPuc());
                        _sphobj.setQCount(_phobj.getQCount());
                        _sphobj.setAcount(_phobj.getACount());
                        _sphobj.setROption(_phobj.getROption());
                        _sphobj.setRecords(_phobj.getRecords());
                        _sphobj.setCEntryScprefix(_phobj.getCScprefixName());
                        _sphobj.setVersion(_phobj.getVersion());
                        _sphobj.setIsDynamicAd(_phobj.isIsDynamicAd());
                        _sphobj.setIsInboxReply(_phobj.isIsInboxReply());
                        _sphobj.setIsReportIEVT(_phobj.isIsReportIEVT());
                        _sphobj.setChatId(_phobj.getChatId());
                        _sphobj.setAbbreviation(_phobj.getAbbreviation());
                        _phobj = null;
                    }
                }
            }catch (Exception e){}
            deInitializeInputStream();
            return _sphobj;
	}

        /**
         *Method to retrieve the Exit Node Id Object
         *
         * @return
         */
        public Object GetExitNodeObject(){
            try{
                if(eGotoId>0){
                    int nextActionID=eGotoId;
                    eGotoId=-1;
                    return getProfileTagObject(nextActionID);
                }
            }catch(Exception e){
                Logger.loggerError("Profile Parser->GetExitNodeObject "+e.toString());
            }
            return null;
        }

        public boolean isExitNode(){
            if(eGotoId>0)
                return true;
            return false;
        }


        	/**
	 *
	 */
	public SMSProfileHeader getFeatureApp(String appName) {
            return getProfile(appName);
	}


	/**
	 *
	 */
	public SMSProfileHeader[] getNewProfileList(boolean isNew) {
            int len;

            String[] loc = null;
            if(isNew)
                loc = RecordManager.getNewAppNames();
            else
                loc = RecordManager.getOrdinaryAppsList();

            SMSProfileHeader[] _smPhs = null;
            if(null !=loc && (len=loc.length)>0)
            {
                _smPhs = new SMSProfileHeader[len];
                int j=0;
                for(int i=0;i<len;i++)
                {
                    _smPhs[j] = getProfile(loc[i]);
                    if(null != _smPhs[j])
                        j++;
                }
                if(j>0)
                {
                    if(j<len)
                    {
                        SMSProfileHeader[] temp = new SMSProfileHeader[j];
                        System.arraycopy(_smPhs,0,temp,0,j);
                        _smPhs = temp;
                        temp = null;
                    }
                }
                else _smPhs = null;
                loc = null;
            }

            return _smPhs;
	}

//        /**
//         * Future Purpose
//         **/
//        public SMSProfileHeader[] getNewProfileList()
//        {
//            SMSProfileHeader[] _smPhs = null;
//            int len;
//
//            String[] loc = FileManager.getSMSNewProfilesLoc();
//
//            if(null !=loc && (len=loc.length)>0)
//            {
//                _smPhs = new SMSProfileHeader[len];
//                int j=0;
//                for(int i=0;i<len;i++)
//                {
//                    _smPhs[j] = getProfile(loc[i]);
//                    if(null != _smPhs[j])
//                        j++;
//                }
//
//                if(j>0)
//                {
//                    if(j<len){
//                        SMSProfileHeader[] temp = new SMSProfileHeader[j];
//                        System.arraycopy(_smPhs,0,temp,0,j);
//                        _smPhs =temp;
//                        temp = null;
//                    }
//                } else _smPhs = null;
//
//                loc = null;
//            }
//
//            return _smPhs;
//        }

	/**
	 *
	 */
	public Object getInitialMenu() {
		// Storing the sequence of actions
                actionseq.removeAllElements();
                if (fGotoId>0){
                    int id = fGotoId;
                    fGotoId = -1;
                    return getProfileTagObject(id);
                }
                return getProfileTagObject(initialid);
	}

        public void changeInitialScreenId(){
            fGotoId = -1;
        }

        /**
         *
         **/
        public boolean isInitialMenu()
        {
            if(currtype == ProfileTypeConstant.MENU)
            {
                MenuItemList list = (MenuItemList)currobj;
                if(initialid == list.getId())
                    return true;
            }

            return false;
        }

        /**
         *
         **/
        public byte getType()
        {
            return currtype;
        }

        /**
         *
         **/
        public Object getCurrentObjject()
        {
            return currobj;
        }

        /**
         *
         **/
        public MenuItemList getMenuItems(Menu _menu)throws Exception
        {
            MenuItemList _list = new MenuItemList();
            _list.setId(_menu.getId());
            _list.setBack(_menu.isBack());
            _list.setPlacead(_menu.isPlacead());
            _list.setMemvarname(_menu.getMemvarname());
            _list.setName(_menu.getName());
            _list.setSechdrtxt(_menu.getSechdrtxt());
            _list.setIscitemName(_menu.isCItem());
            _list.setEsTxt(_menu.getEsText());
            _list.setLOString(_menu.getLOString());
            _list.setLOGotoId(_menu.getLOGotoId());
            _list.setSmartBack(_menu.isSmartBack());

            int iCount = _menu.getNitems();
            if(iCount>0)
            {
                int[] iId = new int[iCount];
                String[] iName = new String[iCount];
                String[] ismsValue = new String[iCount];
                int[] gotoId = new int[iCount];
                String[] widggetId = new String[iCount];
                Item _item =null;
                short countOfBytesToRead = 0;
                for(int i=0;i<iCount;i++)
                {
                    iId[i] = curPos;
                    countOfBytesToRead = setReadPosition(curPos);
                    _item = (Item)getItemObject(iId[i],countOfBytesToRead);
                    iName[i] = _item.getTxt();
                    ismsValue[i] = _item.getSmsval();
                    gotoId[i] = _item.getGotoAddr();
                    widggetId[i] = _item.getWidgetId();
                    _item =null;
                }
                _list.setItemidlist(iId);
                _list.setStylelist(new int[iCount]);
                _list.setSmsvaluelist(ismsValue);
                _list.setItemnamelist(iName);
                _list.setGotoid(gotoId);
                _list.setGotoWidgetName(widggetId);
                iId =null;
                iName =null;
                ismsValue =null;
                gotoId =null;
                widggetId =null;
            }
            return _list;
        }

	/**
	 *
	 */
	public Object getActionForItemSelection(int itemid) {

            if(currtype == ProfileTypeConstant.MENU)
            {
                MenuItemList _list = (MenuItemList)currobj;
                int[] itemId = _list.getItemidlist();
                int len = itemId.length;
                for(int i=0;i<len;i++)
                {
                    if(itemid == itemId[i])
            		return  getProfileTagObject(_list.getGotoid()[i]);
                }
            }
            return null;
	}

	/**
	 *
	 */
	public Object getAction(int actionid) {
		return getProfileTagObject(actionid);
	}

	/**
	 *
	 */
	public Object getNextAction(int itemid) {
		int nextactionid = -1;
                if(ProfileTypeConstant.MENU == currtype){
                    nextactionid = itemid;
                } else if (ProfileTypeConstant.ENTRYACTION == currtype) {
			nextactionid = ((EntryAction) currobj).getGotoid();
		} else if (ProfileTypeConstant.DISPLAYACTION == currtype) {
			nextactionid = ((DisplayAction) currobj).getGotoid();
		} else if (ProfileTypeConstant.SMSSENDACTION == currtype) {
			nextactionid = ((SMSSendAction) currobj).getGotoid();
		} else if(ProfileTypeConstant.CALLACTION == currtype){
                        nextactionid = ((CallAction) currobj).getGoId();
                } else if(ProfileTypeConstant.URLACTION == currtype){
                        nextactionid = ((UrlAction) currobj).getGoId();
                } else if(ProfileTypeConstant.GENERALACTION == currtype){
                        nextactionid = ((GeneralAction) currobj).getGotoid();
                } else if(ProfileTypeConstant.DOWNLOADACTION == currtype){
                    nextactionid = ((DownloadAction) currobj).getGotoId();
                }
		return  getProfileTagObject(nextactionid);
	}


        public int getItemNextActionId(int itemid){
            MenuItemList _list = (MenuItemList)currobj;
            int[] itemId = _list.getItemidlist();
            int len = itemId.length;
            for(int i=0;i<len;i++)
            {
                if(itemid == itemId[i]){
                    return _list.getGotoid()[i];
                }
            }
            return -1;
        }
        /**
         *
         **/
        public EscapeText[] getEscapeTxt()
        {
            EscapeText[] esTxt = null;

            if(currtype == ProfileTypeConstant.MENU)
                esTxt = ((MenuItemList)currobj).getEsTxt();
            else if(currtype == ProfileTypeConstant.ENTRYACTION)
                esTxt = ((EntryAction)currobj).getEsTxt();
            else if(currtype == ProfileTypeConstant.DISPLAYACTION)
                esTxt = ((DisplayAction)currobj).getEsTxt();

            return esTxt;
        }

        /**
         *
         **/
        public String[] getEscapeMenu()
        {
            String[] esMenu = null;
            EscapeText[] esTxt = getEscapeTxt();
            if(null != esTxt)
            {
                int len = esTxt.length;
                String[] temp = new String[len];
                int j=0;
                for(int i=0;i<len;i++)
                {
                    if(!esTxt[i].getIsOpt())
                        temp[j++]=esTxt[i].getEsText();
                }
                if(j>0)
                {
                    if(j<len){
                        esMenu = new String[j];
                        System.arraycopy(temp,0,esMenu,0,j);
                    }else esMenu = temp;
                }
                esTxt = null;
            }
            return esMenu;
        }

        public boolean isEscapeMenu(String mitem){
            EscapeText[] esText = getEscapeTxt();
            if(null != esText){
                int len = esText.length;
                for(int i=0;i<len;i++){
                    if(!esText[i].getIsOpt() && 0 == esText[i].getEsText().compareTo(mitem))
                        return true;
                }
                esText = null;
            }
            return false;
        }

        /**
         *
         **/
        public String[] getEscapeOption()
        {
            String[] esMenu = null;
            EscapeText[] esTxt = getEscapeTxt();
            if(null != esTxt)
            {
                int len = esTxt.length;
                String[] temp = new String[len];
                int j=0;
                for(int i=0;i<len;i++)
                {
                    if(esTxt[i].getIsOpt())
                        temp[j++]=esTxt[i].getEsText();
                }
                if(j>0)
                {
                    if(j<len){
                        esMenu = new String[j];
                        System.arraycopy(temp,0,esMenu,0,j);
                    }else esMenu = temp;
                }
                esTxt = null;
            }
            return esMenu;
        }


        public Object getPreviousAction(int id){
            if(!actionseq.isEmpty()){
                int nId = -1;
                while(actionseq.size()>0){
                    nId = ((Integer) actionseq.pop()).intValue();
                    if(id == nId)
                        break;
                }
                //return getProfileTagObject(id);
            }
            return getProfileTagObject(id); //bug 11246
        }

     	/**
	 * 
	 */
	public Object getPreviousAction(boolean isRightKey) {
            int nextactionId = -1;
            if(!actionseq.isEmpty()){
                if(currtype == ProfileTypeConstant.DISPLAYACTION){
                    nextactionId =((Integer) actionseq.pop()).intValue(); // current Action
                }else if(actionseq.size()>1){
                    if(isRightKey) //CR 12165
                        actionseq.pop(); // current Action
                    nextactionId = ((Integer) actionseq.pop()).intValue(); // Previous Action
                }
                return getProfileTagObject(nextactionId);
            }
            return null;
	}

    public int getLastSMSSendAction(){
        return lastSMSSendActionId;
    }

        /**
         *
         * @param _id
         * @return
         */
	private Object getProfileTagObject(int _id) {
            Object _obj =null;
            if(_id < 0)
                return _obj;

            try {
                //retrieve the Number of bytes to be read
                short readByteCount = setReadPosition(_id);

                //Store the Accessed Id
                if(currtype == ProfileTypeConstant.MENU || currtype == ProfileTypeConstant.ENTRYACTION)
                {
                    if(initialid == _id)
                        actionseq.removeAllElements();
                    actionseq.push(new Integer(_id));
                }

                if(currtype == ProfileTypeConstant.PROFILEHEADER)
                    _obj = getProfileHeaderObject(readByteCount);
                else if(currtype == ProfileTypeConstant.MENU)
                    _obj = getMenuObject(_id,readByteCount);
                else if(currtype == ProfileTypeConstant.ENTRYACTION)
                    _obj = getEntryActionObject(_id,readByteCount);
                else if(currtype == ProfileTypeConstant.DISPLAYACTION)
                    _obj = getDisplayAction(_id,readByteCount);
                else if(currtype == ProfileTypeConstant.SMSSENDACTION){
                    lastSMSSendActionId = _id;
                    _obj = getSMSSendActionObject(_id,readByteCount);
                } else if(currtype == ProfileTypeConstant.CALLACTION)
                    _obj = getCallActionObject(_id,readByteCount);
                else if(currtype == ProfileTypeConstant.URLACTION)
                    _obj = getUrlActionObject(_id,readByteCount);
                else if(currtype == ProfileTypeConstant.GENERALACTION)
                    _obj = getGeneralActionObject(_id,readByteCount);
                else if(currtype == ProfileTypeConstant.DOWNLOADACTION)
                    _obj = getDownloadAction(_id,readByteCount);
            } catch (Exception e){
                Logger.loggerError("ProfileParser Load Action "+e.getMessage());
            }

            currobj = null;
            currobj = _obj;
            _obj = null;
           return currobj;
	}

        /**
         *
         **/
        private Object getProfileHeaderObject(short readByteCount) throws Exception
        {
            ProfileHeader pHeader = new ProfileHeader();

            ByteArrayReader localDis = new ByteArrayReader(getStream(readByteCount));

            header = getBitArray(ProfileTypeConstant.PROFILEHEADER_SIZE, localDis);

               //Read the Profile Id
            pHeader.setPId(localDis.readUTF());

            //Read Profile Initial Id
            pHeader.setPinitid(localDis.readInt());

            //Read Profile Usage Count
            pHeader.setPuc(localDis.readInt());

            //Read Profile Length
            localDis.readInt();

            //Read the profile Creation Date
            localDis.readUTF();

            //Read the Profile Name
            pHeader.setName(localDis.readUTF());

           //Read the Profile Version
            pHeader.setVersion(localDis.readUTF());

            //Read the Profile type
            localDis.readUTF();

            /** Remove Hiden Item */
            //header[0];

            /** Menu Reorder */
            //header[1];

            //Set the Boolean Profile Loop Back
            pHeader.setLBack(header[2]);

            //Read the shortCode
            if(header[3]){
                int len = localDis.readByte();
                if(len>0){
                    String[] tsc = new String[len];
                    for(int k=0;k<len;k++)
                        tsc[k] = localDis.readUTF();
                    pHeader.setScode(tsc);
                }
            }

            //Read Primary Header Text
            if(header[4])
                pHeader.setPhtxt(localDis.readUTF());

            //Read Banner Text
            if(header[5])
                pHeader.setBtxt(localDis.readUTF());

            //Read Alert Text
            if(header[6])
                pHeader.setAtxt(localDis.readUTF());

            //Read Profile Catagory
            if(header[7])
                pHeader.setCategory(localDis.readUTF());

            //Read Pay For
            if(header[8])
                pHeader.setPf(localDis.readUTF());

            //Read Profile Description
            if(header[9])
                pHeader.setDesc(localDis.readUTF());

            //Read Regions
            if(header[10])
                localDis.readUTF();

            //Desktop launchicon
            //header[11]

            //Multi SMS Format
            //if(header[12])
            pHeader.setMSCF(localDis.readUTF());

            /** Read the Profile count Send Interval */
            pHeader.setInterval(localDis.readByte());

            if(pHeader.getInterval()>0){
                /** Read the Profile Created Date */
                pHeader.setDate(localDis.readLong());
            }

            /** Read the Loop Back Delay time */
            pHeader.setLBdelay(localDis.readByte());

            /** Read the Records */
            if(header[12])
                pHeader.setRecords(localDis.readUTF());

            /** Read the Memorize Variable Warning message */
            if(header[13])
                pHeader.setMWMsg(localDis.readUTF());

            /** Read the Profile Level first Level goto Id */
            if(header[14])
                pHeader.setFgotoId(localDis.readInt());

            if(header[15]){
                header = getBitArray(ProfileTypeConstant.PROFILEHEADER_SIZE, localDis);

                /** Read the Command Scprefix Name */
                if(header[0])
                    pHeader.setCScprefixName(localDis.readUTF());

                /** Read the Second Goto Id */
                if(header[1])
                    pHeader.setSGotoId(localDis.readInt());

                /** Record Option */
                if(header[2])
                    pHeader.setROption(localDis.readUTF());

                /** Download Url **/
                 if(header[3])
                     pHeader.setDUrl(localDis.readUTF());

                /** Exiyt Node Id*/
                if(header[4])
                    pHeader.setEGotoid(localDis.readInt());

                /** Read the Memorize Variable Name */
                if(header[5])
                    pHeader.setMemVarName(localDis.readUTF());

                /** Read the Assign Table memorize varibale Name */
                if(header[6])
                    pHeader.setATableMemVarName(localDis.readUTF());

                /** Set the dynamic Ad should be display or Not */
                pHeader.setIsDynamicAd(header[7]);

                /** Set the Static Ad should be display or Not */
                pHeader.setIsStaticAd(header[8]);

                /** Set the inbox have the reply option */
                pHeader.setIsInboxReply(header[9]);

                /** Set the Report IEVT */
                pHeader.setIsReportIEVT(header[10]);

                if(header[11])
                    pHeader.setChatId(localDis.readInt());

                if(header[12]){
                    pHeader.setAbbreviation(localDis.readUTF());
                } else  {
                    String value = pHeader.getName();
                    //if(pHeader.getName().compareTo("Facebook Chat") == 0){
                    //    value = "FBC";
                    //} else {//CR 11909
                        int index = value.indexOf(" ");
                        if(index>-1){
                            value = value.substring(0,index);
                        }
                   // }
                    pHeader.setAbbreviation(value);
                }

            }
            header = null;

            localDis.close();
            localDis = null;

            return  pHeader;
        }

        /**
         *
         **/
        private Object getMenuObject(int id, short readByteCount) throws Exception
        {
            Menu menu = new Menu();

            ByteArrayReader localDis = new ByteArrayReader(getStream(readByteCount));

            header = getBitArray(ProfileTypeConstant.MENUHEADER_SIZE, localDis);

            menu.setId(id);

            //Set the Back Option
            menu.setBack(header[0]);

            //Set the PlaceAd option
            menu.setPlacead(header[1]);

            //Set the Change Item Name Option
            menu.setCItem(header[2]);

            //Read Menu Name
            if(header[3])
                menu.setName(localDis.readUTF());

            //Read Secondary Header Text
            if(header[4])
                menu.setSechdrtxt(localDis.readUTF());

            //Read Memorize variable Name
            if(header[5])
                menu.setMemvarname(localDis.readUTF());

            //Read EscapeText Object
            if(header[6])
                menu.setEsText(getEscapeTextObject(localDis));

            //Read the extra header byte
            if(header[7]){
                header = getBitArray(ProfileTypeConstant.MENUHEADER_SIZE, localDis);

                //Read the Left option String and Goto id
                if(header[0]){
                    menu.setLOString(localDis.readUTF());
                    menu.setLOGotoId(localDis.readInt());
                }

                /** Set the smart back */
                menu.setSmartBack(header[1]);
            }

            //Read Number of Menu Items Count
            menu.setNitems(localDis.readInt());

            header = null;

            localDis.close();
            localDis = null;

            return  getMenuItems(menu);
        }

        private byte[] getStream(short readByteCounts){
            byte[] readByte = new byte[readByteCounts];
            dis.read(readByte);
            return readByte;
        }

        /**
         *
         **/
        private Object getItemObject(int id, short readByteCounts) throws Exception
        {
            Item _item = new Item();

            ByteArrayReader localDis = new ByteArrayReader(getStream(readByteCounts));

            header = getBitArray(ProfileTypeConstant.ITEMHEADER_SIZE, localDis);

            //Set the Item Id
            _item.setId(id);

            //Set the Item Hiden option
            _item.setHide(header[0]);

            //Read the AccessCounter
            _item.setAccesscounter(localDis.readInt());

            //Read Item Text
            if(header[1])
                _item.setTxt(localDis.readUTF());

            //Read Item SMS Value
            if(header[2])
                _item.setSmsval(localDis.readUTF());

            //Read Goto Widget Id
            if(header[3])
                _item.setWidgetId(localDis.readUTF());

            _item.setGotoAddr(localDis.readInt());

            header =null;

            localDis.close();
            localDis = null;

            return _item;

        }

        /**
         *
         **/
        private Object getEntryActionObject(int id, short readByteCount) throws Exception
        {
            EntryAction _eAction = new EntryAction();

            ByteArrayReader localDis = new ByteArrayReader(getStream(readByteCount));

            header = getBitArray(ProfileTypeConstant.ENTRYACTIONHEADER_SIZE, localDis);

            //Set the Entry Action Id
            _eAction.setId(id);

            //Set the Back Option
            _eAction.setBack(header[0]);

            //Set Place Ad Option
            _eAction.setPlaceAd(header[1]);

            //Set Entry Item is Fixed of Varibale Items (isFixed) options
            _eAction.setIsfixed(header[2]);

            //Set the  Entry bos is MultiLine options
            _eAction.setMultiline(header[3]);

            //Set the Pershable Entry Option
            _eAction.setPerEntry(header[4]);

            //Set the Entry box Remove option
            _eAction.setIsebRemove(header[5]);

            //Read the Entry Name
            if(header[6])
                _eAction.setEName(localDis.readUTF());

            //Read ScPrefix
            if(header[7])
                _eAction.setScprefix(localDis.readUTF());

            //Read Mask
            if(header[8])
                _eAction.setMask(localDis.readUTF());

            //Set the Propagate option
            _eAction.setPpgt(header[9]);

            //Read Memorize Variable Name
            if(header[10])
                _eAction.setMvarname(localDis.readUTF());

            //Read Escape Text Object
            if(header[11])
                _eAction.setEsTxt(getEscapeTextObject(localDis));

            if(header[12]){
                _eAction.setMulValue(localDis.readUTF());
                _eAction.setSep(localDis.readUTF());
            }

            //Read Goto Id
            _eAction.setGotoid(localDis.readInt());

            //Read Entry Type
            _eAction.setEtype(localDis.readByte());

            //Read MinChar
            _eAction.setMinchar(localDis.readShort());

            //Read MaxChar
            _eAction.setMaxchar(localDis.readShort());

            //Read Length
            short length = localDis.readShort();

            if(length>0)
            {
                _eAction.setMaxchar(length);
                _eAction.setMinchar(length);
            } else if(_eAction.getMinchar() == 0 && _eAction.getMaxchar() == 0){
                _eAction.setMinchar((short)1);
            }

            //Native Textbox wondt accept the empty character so we have to send minchar 1 manually
            //This is for hot corded.
            if(_eAction.getMinchar() == 0 && header[20])
                _eAction.setMinchar((short)1);

            //Read MaxValue
            _eAction.setMaxvalue(localDis.readFloat());

            //Read Widget Id
            if(header[13])
                _eAction.setGotoWidgetName(localDis.readUTF());

            //Read NextAction query Type
            if(header[14])
                _eAction.setQueryType(localDis.readUTF());

            /** Set the Action Having the Record bit */
            _eAction.setIsRecord(header[15]);

            /** Read Record Display format */
            if(header[16])
                _eAction.setRDFormat(localDis.readUTF());

            /** Read Left option String and Left option goto id */
            if(header[17])
            {
                //Left Option String
                _eAction.setLOString(localDis.readUTF());

                //Left Option String goto id
                _eAction.setLOGotoId(localDis.readInt());
            }

            /** Delete Option is Enable of Not */
            _eAction.setIsRDel(header[18]);

            /** Set the smartBack Option bit*/
            _eAction.setSmartBack(header[19]);

            /** Set the Native Textbox byte*/
            _eAction.setIsNative(header[20]);

            /** Read Minimum value Bug 5560*/
            if(header[21])
                _eAction.setMinValue(localDis.readFloat());

            _eAction.setIsMsContacts(header[22]);

            // CR 13695
            if(header[23]){
                header = getBitArray(ProfileTypeConstant.ENTRYACTIONHEADER_SECOND, localDis);
                _eAction.setIsMsRefresh(header[0]);
            }

            header = null;

            localDis.close();
            localDis = null;

            return _eAction;


        }

        /**
         *
         **/
        private Object getDisplayAction(int id, short readByteCount) throws Exception
        {
            DisplayAction _dAction = new DisplayAction();

            ByteArrayReader localDis = new ByteArrayReader(getStream(readByteCount));

            header = getBitArray(ProfileTypeConstant.DISPLAYACTIONHEADER_SIZE, localDis);

            //Set DisplyAction Id
            _dAction.setId(id);

            //Read Display Image
            _dAction.setDispimage(localDis.readByte());

            //Read Display Time
            _dAction.setDisptime(localDis.readShort());

            //Set the Back Option
            _dAction.setBack(header[0]);

            //Set the Place Ad
            _dAction.setPlacead(header[1]);

            //Read Output Text
            if(header[2])
                _dAction.setOtxt(localDis.readUTF());

            //Read Message Query
            if(header[3])
                _dAction.setMqtype(localDis.readUTF());

            //Read Escape Text Object
            if(header[4])
                _dAction.setEsTxt(getEscapeTextObject(localDis));

            _dAction.setIsDReply(header[5]);

            //Information
            if(header[6])
                _dAction.setInfo(localDis.readUTF());

            //Read the Gotto Id
            _dAction.setGotoid(localDis.readInt());

            //Read the extra header byte
            if(header[7]){
                header = getBitArray(ProfileTypeConstant.DISPLAYACTIONHEADER_SIZE, localDis);

                //Read Widget gotoId
                if(header[0])
                    _dAction.setGotoWidgetName(localDis.readUTF());

                //Read the left option String
                if(header[1]){
                    _dAction.setLOString(localDis.readUTF());
                    _dAction.setLOGotoId(localDis.readInt());
                }

                /** Smart Back */
                _dAction.setSmartBack(header[2]);

                /** Display Information URL Highlight bit*/
                _dAction.setIsUrl(header[3]);

                if(header[4]){
                    _dAction.setBuddyName(localDis.readUTF());
                }

                _dAction.setIsReplyEnabled(header[5]);

                if(_dAction.getDispimage() == ProfileTypeConstant.Display.DISPLAY_CHAT){
                    _dAction.setMqtype(localDis.readUTF());
                }
  //bug no 9272
                _dAction.setIsNumber(header[6]);
                  //bug no 9272
            }

            header = null;

            localDis.close();
            localDis = null;

            return _dAction;

        }


        /**
         *
         **/
        private Object getSMSSendActionObject(int id, short readByteCount) throws Exception
        {
            SMSSendAction _sAction = new SMSSendAction();

            ByteArrayReader localDis = new ByteArrayReader(getStream(readByteCount));

            header = getBitArray(ProfileTypeConstant.SMSSENDACTIONHEADER_SIZE, localDis);

            //Set the SMSAction id
            _sAction.setId(id);

            //Set the Number Option
            _sAction.setChkforno(header[0]);

            //Set the URl Option
            _sAction.setChkforurl(header[1]);

            //Set the Query Format
            _sAction.setQfmt(localDis.readUTF());

            //Read the Query Type
            if(header[2])
                _sAction.setQtype(localDis.readUTF());

            //Read the shortCode
            if(header[3])
                _sAction.setSc(localDis.readUTF());

            //Read Match Words
            if(header[4])
                _sAction.setMwords(localDis.readUTF());

            //Read MisMatch Words
            if(header[5])
                _sAction.setMismwords(localDis.readUTF());

            //Read Goto Id
            _sAction.setGotoid(localDis.readInt());

            //Read Propagation Table
            if(header[6])
                _sAction.setPtarr(getPropagationObject(localDis));

            //Read KewordTable Object
            if(header[7])
                _sAction.setKdarr(getKeywordObject(localDis));

            //Read Intractive Table Object
            if(header[8])
                _sAction.setItarr(getIntTextObject(localDis));

            //Shortcode Propagate Name
            if(header[9])
                _sAction.setSCProName(localDis.readUTF());

            //Ignore Text
            if(header[10])
                _sAction.setITable(getIgnoreTextObject(localDis));

            //Read Goto Widget Name
            if(header[11])
                _sAction.setGotoWidgetName(localDis.readUTF());

            //Propagate Arrival time
            if(header[12])
                _sAction.setProArrivalTime(localDis.readUTF());

            //Read Intractive Imediate Goto Id enable byte
            if(header[13])
                _sAction = setIntractiveImediateEnable(_sAction, localDis);

            /** Dont Send Message */
            _sAction.setDSendMsg(header[14]);

            if(header[15]){
                header = getBitArray(ProfileTypeConstant.SMSSENDACTIONHEADER_SIZE, localDis);

                //No response message */
                _sAction.setDWResponse(header[0]);

                // No new Message Display
                _sAction.setNoNewMSG(header[1]);

                // No message saved in inbox
                _sAction.setDontSaveInbox(header[2]);

                //Internal Loop Back
                _sAction.setInternalLoopBack(header[3]);
            }

            header = null;

            localDis.close();
            localDis = null;

            return _sAction;

        }

        /**
         *
         * @param _sAction
         * @return
         */
        private SMSSendAction setIntractiveImediateEnable(SMSSendAction _sAction, ByteArrayReader localDis){
            try{
                int len = localDis.readByte();
                byte[] eByte = new byte[len];
                localDis.read(eByte);
                int k=0;
                int count = _sAction.getItarr().length;
                for(int i=0;i<len;i++)
                {
                    for(int j=0;j<8 && k<count ;j++)
                         _sAction.getItarr()[k++].setIsImediateGoto(((eByte[i] & (1 << j)) != 0));
                }
            }catch(Exception e){}
            return _sAction;
        }

        /**
         *
         **/
        private Object getCallActionObject(int id, short readByteCount) throws Exception {

            CallAction _cAction = new CallAction();

            //header = getBitArray(ProfileTypeConstant.CALLACTIONHEADER_SIZE);
            ByteArrayReader localDis = new ByteArrayReader(getStream(readByteCount));
            //Call Action Id
            _cAction.setId(id);

            //Call Number
            _cAction.setCallNum(localDis.readUTF());

            //GoId
            _cAction.setGoId(localDis.readInt());

//            header = null;

            localDis.close();
            localDis = null;

            return _cAction;
        }

        /**
         *
         **/
        private Object getUrlActionObject(int id, short readByteCount)throws  Exception{

            UrlAction _uAction = new UrlAction();

            ByteArrayReader localDis = new ByteArrayReader(getStream(readByteCount));
            //header = getBitArray(ProfileTypeConstant.URLACTIONHEADER_SIZE);

            //Url  Action Id
            _uAction.setId(id);

            //Read the Url
            _uAction.setUrl(localDis.readUTF());

            //Read the Goto Id
            _uAction.setGoId(localDis.readInt());

//            header = null;
            localDis.close();
            localDis = null;
            return _uAction;

        }

        /**
         *
         * @param id
         * @return
         * @throws java.io.IOException
         */
        private Object getGeneralActionObject(int id, short readByteCount) throws Exception {
            GeneralAction action = new GeneralAction();

            ByteArrayReader localDis = new ByteArrayReader(getStream(readByteCount));

            header = getBitArray(ProfileTypeConstant.GENERALACTIONHEADER_SIZE,localDis);

            // set id
            action.setid(id);

            //Set the clear single value bit
            action.setCSingleValue(header[0]);

            //Set the clear all value
            action.setClearAll(header[1]);

            //read the scprefic name
            if(header[2])
                action.setScPrefix(localDis.readUTF());

            //read the Logical Brach table value
            if(header[3])
                action.setLBTable(getLogicalTableObject(localDis));

            //Read the next action goto id
            action.setGotoid(localDis.readInt());

            //Read the Generic Assign Table Value
            if(header[4])
                action.setGTable(getGenericAssignTable(localDis));

            header = null;

            localDis.close();
            localDis = null;

            return action;
        }

        /**
         * Methos to read the Download Action Data
         * @param id
         * @return
         * @throws java.io.IOException
         */
        private Object getDownloadAction(int id, short readByteCount) throws Exception{
            DownloadAction downLoad = new DownloadAction();
            ByteArrayReader localDis =  new ByteArrayReader(getStream(readByteCount));
            downLoad.setActionId(id);  // Download Action Id
            downLoad.setType(localDis.readUTF()); // Read Type of Download
            downLoad.setwId(localDis.readUTF()); // Read Widget Id
            downLoad.setGotoId(localDis.readInt()); // Read Naxt Action Id
            localDis.close();
            localDis = null;
            return downLoad;
        }
        /**
         *
         * @return
         * @throws java.io.IOException
         */
        private GenericAssignTable[] getGenericAssignTable(ByteArrayReader localDis) throws Exception{

            byte count = localDis.readByte();
            GenericAssignTable[] gATable = new GenericAssignTable[count];
            for(int i=0;i<count;i++){
                gATable[i] = new GenericAssignTable();

                //REad the Variable Name
                gATable[i].setVarName(localDis.readUTF());

                // Set weather value will memorize or Not
                gATable[i].setIsMem(localDis.readBoolean());

                //Read the Variable Value
                gATable[i].setVarValue(localDis.readUTF());
            }
            return gATable;
        }

        /**
         *
         **/
        private KeywordDef[] getKeywordObject(ByteArrayReader localDis) throws Exception
        {
            byte count = localDis.readByte();
            KeywordDef[] kd = new KeywordDef[count];
            String tempValue =null;
            //CR 11799
            String repeadValue = "";
            int originalCount = 0;
            KeywordDef keyWordDef = null;
            int index =-1;
            String[] keywordString = null;
            for(int i=0;i<count;i++){
                keyWordDef = new KeywordDef(localDis.readUTF(),localDis.readUTF(),
                        localDis.readUTF(),localDis.readInt());
                tempValue = "^"+keyWordDef.getStText()+"|"+keyWordDef.getEndText()+"|"+
                        keyWordDef.getSCPrefix()+"|"+keyWordDef.getPgotoid()+"^";
                if((index= repeadValue.indexOf(tempValue)) == -1){
                    repeadValue += tempValue;
                    kd[originalCount++] = keyWordDef;
                } else {
                    //bug id 12108 and 12052
                    if(index>0){
                        keywordString = Utilities.split(repeadValue.substring(0,index), "^^");
                        if(null != keywordString){
                            kd[keywordString.length].increaseCount();
                        }
                    } else kd[0].increaseCount();
                }
            }

            if(count>originalCount){
                KeywordDef[] temp = kd;
                kd = new KeywordDef[originalCount];
                System.arraycopy(temp, 0, kd, 0, originalCount);
                temp = null;
            }

            return kd;
        }

        /**
         *
         */
        private LogicalBranchTable[] getLogicalTableObject(ByteArrayReader localDis) throws Exception{
            byte count = localDis.readByte();
            LogicalBranchTable[] lbt = new LogicalBranchTable[count];
            for(int i=0;i<count;i++){
                lbt[i] = new LogicalBranchTable();
                lbt[i].setScPrefix(localDis.readUTF());
                lbt[i].setIsEqual(localDis.readBoolean());
                lbt[i].setValue(localDis.readUTF());
                lbt[i].setGotoid(localDis.readInt());
            }
            return lbt;
        }

        /**
         *
         **/
        private IntText[] getIntTextObject(ByteArrayReader localDis) throws Exception
        {
            byte count = localDis.readByte();
            IntText[] intTxt =new IntText[count];
            for(int i=0;i<count;i++)
                intTxt[i] = new IntText(localDis.readUTF(),localDis.readInt());
            return intTxt;
        }

        /**
         *
         * @return Ignote Table Array
         * @throws java.io.IOException
         */
        private IgnoreTable[] getIgnoreTextObject(ByteArrayReader localDis) throws Exception
        {
            byte count = localDis.readByte();
            IgnoreTable[] iTable = new IgnoreTable[count];
            for(int i=0;i<count;i++)
                iTable[i] = new IgnoreTable(localDis.readUTF(), localDis.readUTF());
            return iTable;
        }

        /**
         *
         **/
        private PropagateType[] getPropagationObject(ByteArrayReader localDis) throws Exception
        {
            byte count = localDis.readByte();
            PropagateType[] ppgt =new PropagateType[count];
            for(int i=0;i<count;i++)
                ppgt[i] = new PropagateType(localDis.readUTF(),localDis.readUTF(),localDis.readUTF());
            return ppgt;
        }

        /**
         *
         **/
        private EscapeText[] getEscapeTextObject(ByteArrayReader localDis) throws Exception
        {
            byte count = localDis.readByte();
            EscapeText[] esTxt = new EscapeText[count];
            for(int i=0;i<count;i++)
                esTxt[i] = new EscapeText(localDis.readUTF(),localDis.readBoolean(),localDis.readInt());
            return esTxt;
        }

        /**
         *
         **/
        private boolean[] getBitArray(int len, ByteArrayReader localDis) throws Exception
        {
            byte[] eByte = new byte[len];
            localDis.read(eByte);
            boolean[] bitArray = new boolean[len*8];
            int k=0;
            for(int i=0;i<len;i++)
            {
                for(int j=0;j<8;j++)
                    bitArray[k++] = ((eByte[i] & (1 << j)) != 0);
            }
            return bitArray;
        }

        /**
         * Method to reinitialize the input Sream. When the new widget is initialized the application not consider the new widget
         * is initialized or not so the application default open the old widget.
         */
        private void reInitializeStream(){
            try{
                String floc = RecordManager.getOrdinaryAppName(folderloc);
                if (null != floc) {
                    dis = new ByteArrayReader();
                    if(dis.setReader(floc))
                       dis = null;
                }
            }catch(Exception e){
                //#if VERBOSELOGGING
                //|JG|                Logger.loggerError("Profile Parser-> Obj File Opeing Error "+e.toString());
                //#endif
            }
        }

        /**
         *
         **/
        private short setReadPosition(int mPos)
        {
            short countOfBytesToRead = 0;
//            //#if VERBOSELOGGING
//            //|JG|Logger.loggerError("Stream Current Position "+curPos +"\nMoving Position "+mPos);
//            //#endif //11801
            try{
                if(curPos <= mPos)
                {
                    curPos = mPos - curPos;
                    dis.skip(curPos);
                }
                else
                {
                    deInitializeInputStream();
                    reInitializeStream();
                    dis.skipBytes(mPos);
                }

                byte type = dis.readByte();

                if(type != ProfileTypeConstant.ITEM)
                    currtype =  type;

                //CurPos is Specified in the Stream Position
                // mPos is MovePossion, one byte is current tye, size is size off byte
                // 2 size of the short so we can add the this four values
                countOfBytesToRead = dis.readShort();

//                //#if VERBOSELOGGING
//                //|JG|                Logger.loggerError("Stream Skiped Position "+curPos+"\nCurrently Forming Dto Type "+currtype+"\nNumber of bytes to read "+countOfBytesToRead);
//                //#endif //11801
                curPos = countOfBytesToRead + mPos + 1 + 2;


            }catch (Exception e){ }
            return countOfBytesToRead;
        }

	/**
	 *
	 */
	private boolean initializeStream(String loc) throws Exception {
            boolean isInitialized = false;
            String floc = RecordManager.getOrdinaryAppName(loc);
            folderloc = loc;
            if (null != floc) {
                dis = new ByteArrayReader();
                if(dis.setReader(floc))
                    dis = null;
                else isInitialized = true;
            }
            return isInitialized;
	}

        /**
         *
         **/
        public String getMemorizedValue(String name,boolean isdis){
            if(null != memvar)
                return memvar.getValue(name,isdis);
            return null;
        }

        private void setMemorizedValue(){
            memvar = new MemorizedVariable();
            memvar.setMemorizeValue(RecordManager.getMemorizeName(memLoc));
        }

        /**
         *
         **/
        public void setMenuMemorizedValue(String memName,String itemValue,String smsvalue){
            if(null != itemValue){
                memvar.add(null,memName,itemValue);
            }
            if(null != smsvalue){
                memvar.add(null, memName+"."+"smsValue",smsvalue);
            }
        }

        /**
         *
         **/
        public void setEntryMemorizeValue(String memName,String value,String mask){
            memvar.add(getdisplayValue(value,mask),memName,value);
        }

        /**
         *
         * @param memNam
         */
        public void clearMemorizeValue(String memNam){
            if(null != memvar){
                memvar.removeValue(memNam);
            }
        }

        private String getdisplayValue(String value,String mask){
            int len;
            if(null != mask && (len=value.length())>0){
                StringBuffer stbuf = new StringBuffer();
                for(int i=0;i<len;i++){
                    if(0 == mask.compareTo("*"))
                        stbuf.append("*");
                    else if(0 == "*".compareTo(mask.charAt(i)+""))
                        stbuf.append("*");
                    else stbuf.append(value.charAt(i));
                }
                return stbuf.toString();
            }
            return null;
        }

        /**
         *
         *
         */
        private void updateProfileProperty(){
            if(null != ppro){
                ByteArrayWriter dOutStream = new ByteArrayWriter();
                dOutStream.writeBoolean(ppro.isIsPayForser());
                dOutStream.writeByte(ppro.getInterval());
                if(ppro.getInterval()>0){
                    dOutStream.writeInt(ppro.getPuCount());
                    dOutStream.writeLong((new Date().getTime()));
                }
                byte[] rBytes = dOutStream.toByteArray();
                dOutStream.close();
                dOutStream = null;
                RecordStoreParser.UpdateRecordStore(RecordManager.getProfileProperty(memLoc), rBytes, true);
            }
        }

        /**
         *
         */
        private void openRecordStore(boolean isCreate){
            try{
                rStoreParser = new RecordStoreParser();
                if(rStoreParser.openRecordStore(RecordManager.getProfileProperty(memLoc), isCreate,false,false))
                    rStoreParser = null;
            }catch(Exception e){
                rStoreParser = null;
            }
        }

        /**
         *
         */
        private void closeRecordStore(){
            try{
                if(null != rStoreParser){
                    rStoreParser.closeRecordStore();
                    rStoreParser = null;
                }
            }catch(Exception e){}
        }

        public boolean isPayForService(){
            if(null != ppro){
                return ppro.isIsPayForser();
            }
            return false;
        }

        public void setPayForService(){
            if(null != ppro){
                byte[] rbyte = RecordStoreParser.getRecordStore(RecordManager.getProfileProperty(memLoc));
                if(null != rbyte){
                    rbyte[0] = 0;
                    RecordStoreParser.UpdateRecordStore(RecordManager.getProfileProperty(memLoc), rbyte, true);
                }
                ppro.setIsPayForser(false);
            }
        }

        /**
         *
         */
        public void increaseProfileUsage(){
            if(null != ppro && ppro.getInterval()>0){
                ppro.setPuCount(ppro.getPuCount()+1);
                byte[] rbytes = RecordStoreParser.getRecordStore(RecordManager.getProfileProperty(memLoc));
                if(null != rbytes){
                    rbytes[2] = (byte)(ppro.getPuCount() >>> 24);
                    rbytes[3] = (byte)(ppro.getPuCount() >>> 16);
                    rbytes[4] = (byte)(ppro.getPuCount() >>> 8);
                    rbytes[5] = (byte)(ppro.getPuCount());
                    RecordStoreParser.UpdateRecordStore(RecordManager.getProfileProperty(memLoc), rbytes, true);
                }
            }
        }

        /**
         *
         * @param header
         * @return
         */
        private ProfileHeader getProfileProperty(ProfileHeader header,boolean isUpdate){
                ppro = new ProfileProperty();
                byte[] rBytes = RecordStoreParser.getRecordStore(RecordManager.getProfileProperty(memLoc));
                if(null != rBytes){
                    fGotoId = -1;
                    isUpdate = false;
                    ByteArrayReader dStream = new ByteArrayReader(rBytes);
                    ppro.setIsPayForser(dStream.readBoolean());
                    ppro.setInterval(dStream.readByte());
                    if(ppro.getInterval()>0){
                        ppro.setPuCount(dStream.readInt());
                        if(isSendProfileUsage(dStream.readLong(), ppro.getInterval())){
                            rBytes = RecordStoreParser.getRecordStore(RecordManager.getMessageCountLoc(memLoc));
                            if(null != rBytes){
                                dStream = new ByteArrayReader(rBytes);
                                header.setQCount(dStream.readShort());
                                header.setACount(dStream.readShort());
                            }
                            header.setPuc(ppro.getPuCount());
                            ppro.setPuCount(0);
                            isUpdate = true;
                        }
                    }
                    rBytes = null;
                    dStream.close();
                    dStream = null;
                } else {
                    ppro.setInterval(header.getInterval());
                    ppro.setPuCount(0);
                    ppro.setIsPayForser(true);
                }
            if(isUpdate)
                updateProfileProperty();
            return header;
        }

        private boolean isSendProfileUsage(long date,byte interval){
            Calendar curCal = Calendar.getInstance();
            Calendar sCal = Calendar.getInstance();
            Date dat = new Date();
            dat.setTime(date);
            sCal.setTime(dat);
            boolean isSend =false;
            if(Utilities.dateDiff(curCal.get(Calendar.DATE),curCal.get(Calendar.MONTH),curCal.get(Calendar.YEAR),sCal.get(Calendar.DATE),sCal.get(Calendar.MONTH),sCal.get(Calendar.YEAR)) >= interval)
                isSend = true;
            curCal = null;
            sCal = null;
            dat = null;
            return isSend;
        }

	/**
	 *
	 */
	private void deInitializeInputStream(){
            curPos = 0;
            try{
		if (null != dis) {
                    dis.close();
                    dis = null;
		}
            }catch (Exception e){
                //#if VERBOSELOGGING
                //|JG|                Logger.loggerError("Profile Parser-> Obj File Closing Error "+e.toString());
                //#endif

            }
	}
}
