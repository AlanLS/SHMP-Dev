/*
 * KeywordDef.java
 *
 * Created on October 15, 2007, 5:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */



public class KeywordDef {
	String stxt;// start text

	String etxt;// end text

	String scprefix;// shortcut prefix

	// action id - no need to store in byte array instead we store pointer
	int pgotoid; // pointer to next action

        String selText =null;

        //bug no 12108 and 12052
        int count = 0;
        
	public KeywordDef(String stxt, String etxt, String scprefix, int pgotoid) {
		this.stxt = stxt;
		this.etxt = etxt;
		this.scprefix = scprefix;
		this.pgotoid = pgotoid;
                count = 1;
	}

        public void setSelText(String selText) {
            this.selText = selText;
        }

        public String getSelText() {
            return selText;
        }

	public String getStText() {
		return this.stxt;
	}

	public String getEndText() {
		return this.etxt;
	}

	public String getSCPrefix() {
		return this.scprefix;
	}

	public int getPgotoid() {
		return this.pgotoid;
	}

        public void setStText(String sttxt){
            this.stxt = sttxt;
        }

        //bug no 12108 and 12052
        public int getCount() {
            return count;
        }

        public void increaseCount(){
            count++;
        }
        
}
