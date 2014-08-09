public class Item {
	// pointer variables
	private int gotoid; // used to locate next goto action

	// private member variables
	private String txt;

	private String otxt;

	private String smsval;

	private int acc; // limitation

	private boolean hide;

	private int id;
        
        private String widgetId =null;

        public String getWidgetId() {
            return widgetId;
        }

        public void setWidgetId(String widgetId) {
            this.widgetId = widgetId;
        }

	/** Creates a new instance of Item */
	public Item() {
		init();
	}

	public void init() {
		hide = false;
		gotoid = -1;
		acc = 1;
	}

	public int getGotoAddr() {
		return gotoid;
	}

	public void setGotoAddr(int pgoto) {
		this.gotoid = pgoto;
	}

	public boolean isHide() {
		return hide;
	}

	public void setHide(boolean hide) {
		this.hide = hide;
	}

	public String getTxt() {
		if (null != txt)
			return txt;
		return otxt;
	}

	public void setTxt(String txt) {
		this.txt = txt;
	}

	public String getSmsval() {
		return smsval;
	}

	public void setSmsval(String smsval) {
		this.smsval = smsval;
	}

	public String getOtxt() {
		return otxt;
	}

	public void setOtxt(String otxt) {
		this.otxt = otxt;
	}

	public int getAccesscounter() {
		return acc;
	}

	public void setAccesscounter(int accesscounter) {
		this.acc = accesscounter;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
