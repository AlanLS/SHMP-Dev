/*
 * SMSSendAction.java
 *
 * Created on October 12, 2007, 2:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 * 
 * @author Rajesh
 */
public class SMSSendAction {

        private int id;
    
	private int gotoid; // instead of this pgotoid will be written to file

	private String qtype;

	private String mwords;

	private String mismwords;

	private boolean chkforno;

	private boolean chkforurl;

	private String sc;

	private String qfmt; // query format

	// internal variables
	PropagateType[] ptarr; // propagate array

	// int ptoffset;
	IntText[] itarr; // interactive text array

	KeywordDef[] kdarr; // keyword def array
        
        IgnoreTable[] iTable; //Ignore Table Text

	String[] intactionid;

	String[] kdactionid;
        
        private String sCProName =null;
        
        private String gotoWidgetName =null;
        
        private String proArrivalTime =null;

        /** Dont Send Message */
        private boolean dSendMsg = false;

        /** Don't wait Response Message */
        private boolean dWResponse = false;

        /** No New Message Shown **/
        private boolean noNewMSG = false;
        
        /** No received message saved in inbox **/
        private boolean dontSaveInbox = false;
        
        /** Set the boolean for Internal LoopBack**/
        private boolean intLoop = false;
        
        public void setInternalLoopBack(boolean isInternal){
            intLoop = isInternal;
        }
        
        public boolean getInternalLoopBack(){
            return intLoop;
        }
        
        public boolean getDontSaveInbox(){
            return dontSaveInbox;
        }
        
        public void setDontSaveInbox(boolean dsinbox){
            dontSaveInbox = dsinbox;
        }
        
        public boolean getNoNewMSG(){
            return noNewMSG;
        }
        
        public void setNoNewMSG(boolean nnmsg){
            noNewMSG = nnmsg;
        }
        
        /** Get Dont Send Message boolean */
        public boolean isDSendMsg() {
            return dSendMsg;
        }

        /** Set Dont Send Message boolean */
        public void setDSendMsg(boolean dSendMsg) {
            this.dSendMsg = dSendMsg;
        }

        /** Get Dont Wait Response Message */
        public boolean isDWResponse() {
            return dWResponse;
        }

        /** Set Dont Wait Response Message */
        public void setDWResponse(boolean dWResponse) {
            this.dWResponse = dWResponse;
        }

        public void setProArrivalTime(String proArrivalTime) {
            this.proArrivalTime = proArrivalTime;
        }

        public String getProArrivalTime() {
            return proArrivalTime;
        }

        public void setGotoWidgetName(String widgetName) {
            this.gotoWidgetName = widgetName;
        }

        public String getGotoWidgetName() {
            return gotoWidgetName;
        }

        public String getSCProName() {
            return sCProName;
        }

        public void setSCProName(String sCProName) {
            this.sCProName = sCProName;
        }
 
        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
        
        /** Set the Intractive Text Object */
        public void setItarr(IntText[] itarr) {
            this.itarr = itarr;
        }

        /** Get the Intractive Text Object */
        public IntText[] getItarr() {
            return itarr;
        }

        /** Set the Keword Table Objext */
        public void setKdarr(KeywordDef[] kdarr) {
            this.kdarr = kdarr;
        }

        /** Get the Keword Table Objext */
        public KeywordDef[] getKdarr() {
            return kdarr;
        }

        /** Set Propagation Table Object */
        public void setPtarr(PropagateType[] ptarr) {
            this.ptarr = ptarr;
        }

        /** Get Propagation Table Object */
        public PropagateType[] getPtarr() {
            return ptarr;
        }

        public IgnoreTable[] getITable() {
            return iTable;
        }

        public void setITable(IgnoreTable[] iTable) {
            this.iTable = iTable;
        }

        public String getQtype() {
		return qtype;
	}

	public void setQtype(String qtype) {
		this.qtype = qtype;
	}

	public String getQfmt() {
		return qfmt;
	}

	public void setQfmt(String qfmt) {
		this.qfmt = qfmt;
	}

	public String getSc() {
		return sc;
	}

	public void setSc(String sc) {
		this.sc = sc;
	}

	public String getMwords() {
		return mwords;
	}

	public void setMwords(String mwords) {
		this.mwords = mwords;
	}

	public String getMismwords() {
		return mismwords;
	}

	public void setMismwords(String mismwords) {
		this.mismwords = mismwords;
	}

	public boolean isChkforno() {
		return chkforno;
	}

	public void setChkforno(boolean chkforno) {
		this.chkforno = chkforno;
	}

	public boolean isChkforurl() {
		return chkforurl;
	}

	public void setChkforurl(boolean chkforurl) {
		this.chkforurl = chkforurl;
	}

	public int getGotoid() {
		return gotoid;
	}

	public void setGotoid(int gotoid) {
		this.gotoid = gotoid;
	}

}
