/*
 * Menu.java
 *
 * Created on October 12, 2007, 2:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */


public class Menu {
    
        /** Menu Id */
        private int id;
                
	private String name;

	private String sechdrtxt;

	private boolean back;

	private boolean placead;

	private String memvarname;

	private int nitems;

	private int[] iaddrptr; // will hold the item address array pointers
        
        /** Change Item Name */
        private boolean cItem;
        
        private EscapeText[] esText;

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

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        /** Get Escape Text Array */
        public EscapeText[] getEsText() {
            return esText;
        }

         /** Set Escape Text Array */
        public void setEsText(EscapeText[] esText) {
            this.esText = esText;
        }

        /** Get the Change Item Name Option */
        public boolean isCItem() {
            return cItem;
        }

        /** Set the Change Item Name Option */
        public void setCItem(boolean cItem) {
            this.cItem = cItem;
        }
        
	/** Creates a new instance of Menu */
	public Menu() {
		nitems = 0;
	}

//	public void initItemObjArray() {
//		if (null != iaddrptr)
//			iaddrptr = null;
//		if (nitems > 0) {
//			iaddrptr = new int[nitems];
//			for (int i = 0; i < nitems; i++)
//				iaddrptr[i] = -1;
//		}
//		ncounter = 0;
//	}

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

	public int getNitems() {
		return nitems;
	}

        public void setNitems(int nitems)
        {
            this.nitems = nitems;
        }

        public int getItemAt(int index) {
		if (index < nitems)
			return iaddrptr[index];
		return -1;
	}

}
