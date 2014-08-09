/*
 * Sequence.java
 *
 * Created on October 3, 2007, 3:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */



public class Sequence {

	private String selectedName;

	private int id;

	private boolean isVariable;

	private String selectedValue;
        
        private boolean isNotMemorize = true;

        public boolean isIsNotMemorize() {
            return isNotMemorize;
        }

        public void setIsNotMemorize(boolean isNotMemorize) {
            this.isNotMemorize = isNotMemorize;
        }

	// private String menuId;

	public String getSelectedName() {
		return selectedName;
	}

	public void setSelectedName(String selectedName) {
		this.selectedName = selectedName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isIsVariable() {
		return isVariable;
	}

	public void setIsVariable(boolean isVariable) {
		this.isVariable = isVariable;
	}

	public String getSelectedValue() {
		return selectedValue;
	}

	public void setSelectedValue(String selectedValue) {
		this.selectedValue = selectedValue;
	}

}
