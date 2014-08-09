/*
 * PropagateType.java
 *
 * Created on October 15, 2007, 5:20 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */



public class PropagateType {
	private String stxt; // st start text

	private String etxt; // et end text

	private String scprefix;// sp shortcut prefix

	public PropagateType(String stxt, String etxt, String scprefix) {
		this.stxt = stxt;
                this.etxt = etxt;
		this.scprefix = scprefix;
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
}
