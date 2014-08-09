public class MenuItemList {
    
        private int id;
    
	private String name;

	private String sechdrtxt;

	private boolean back;

	private boolean placead;

	private String memvarname;

	private String[] itemnamelist;

	private int[] itemidlist;

	private int[] stylelist;

	private String[] smsvaluelist;
        
        private int[] gotoid;
        
        private boolean iscitemName;
        
        private EscapeText[] esTxt;
        
        private String[] widgetName = null;

        /** Left Option Enable String */
        private String lOString = null;

        /** Left option Goto Id */
        private int lOGotoId = -1;

        /** smart Back */
        private boolean smartBack = false;

        /** Get the Smart Back */
        public boolean isSmartBack() {
            return smartBack;
        }

        /** Set the Smart Back */
        public void setSmartBack(boolean smartBack) {
            this.smartBack = smartBack;
        }

        /** Get Left Option Goto Id */
        public int getLOGotoId() {
            return lOGotoId;
        }

        /** Set Left Option Goto Id */
        public void setLOGotoId(int lOGotoId) {
            this.lOGotoId = lOGotoId;
        }

        /** Get Left option String */
        public String getLOString() {
            return lOString;
        }

        /** Set Left option String */
        public void setLOString(String lOString) {
            this.lOString = lOString;
        }

        public String[] getGotoWidgetName() {
            return widgetName;
        }

        public void setGotoWidgetName(String[] widgetName) {
            this.widgetName = widgetName;
        }

        public int getId() {
            return id;
        }
        
        public void setId(int id){
            this.id = id;
        }
        
        
        public EscapeText[] getEsTxt() {
            return esTxt;
        }

        public void setEsTxt(EscapeText[] esTxt) {
            this.esTxt = esTxt;
        }

        public boolean isIscitemName() {
            return iscitemName;
        }

        public void setIscitemName(boolean iscitemName) {
            this.iscitemName = iscitemName;
        }

        public int[] getGotoid() {
            return gotoid;
        }

        public void setGotoid(int[] gotoid) {
            this.gotoid = gotoid;
        }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSechdrtxt() {
		return sechdrtxt;
	}

	public void setSechdrtxt(String sechdrtxt) {
		this.sechdrtxt = sechdrtxt;
	}

	public boolean isBack() {
		return back;
	}

	public void setBack(boolean back) {
		this.back = back;
	}

	public boolean isPlacead() {
		return placead;
	}

	public void setPlacead(boolean placead) {
		this.placead = placead;
	}

	public String getMemvarname() {
		return memvarname;
	}

	public void setMemvarname(String memvarname) {
		this.memvarname = memvarname;
	}

	public String[] getItemnamelist() {
		return itemnamelist;
	}

	public void setItemnamelist(String[] itemnamelist) {
		this.itemnamelist = itemnamelist;
	}

	public int[] getItemidlist() {
		return itemidlist;
	}

	public void setItemidlist(int[] itemidlist) {
		this.itemidlist = itemidlist;
	}

	public int[] getStylelist() {
		return stylelist;
	}

	public void setStylelist(int[] stylelist) {
		this.stylelist = stylelist;
	}

	public String[] getSmsvaluelist() {
		return smsvaluelist;
	}

	public void setSmsvaluelist(String[] smsvaluelist) {
		this.smsvaluelist = smsvaluelist;
	}

}
