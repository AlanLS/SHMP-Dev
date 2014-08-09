/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



public class LogicalBranchTable {

    /** Dynamic Variable Name if should be scprefix or memorize varibale name*/
   private String scPrefix = null;

   /** Comparison Operator Byte */
   private boolean isEqual = false;

   /** Commparison value */
   private String value = null;

   /** Action performed id */
   private int gotoid = -1;

   /** Get the action performed id */
    public int getGotoid() {
        return gotoid;
    }

    /** Set the action performed id */
    public void setGotoid(int gotoid) {
        this.gotoid = gotoid;
    }

   /** Get the Scprefix or Memorize variable name */
    public String getScPrefix() {
        return scPrefix;
    }

    /** Set the Scprefix or Memorize variable name */
    public void setScPrefix(String scPrefix) {
        this.scPrefix = scPrefix;
    }

    /** Get the Comparrison Value */
    public String getValue() {
        return value;
    }

    /** Set the Comparrison Value */
    public void setValue(String value) {
        this.value = value;
    }

    /** Get the Comparrison Operator byte */
    public boolean isIsEqual() {
        return isEqual;
    }

    /** Set the Comparrison Operator byte */
    public void setIsEqual(boolean isEqual) {
        this.isEqual = isEqual;
    }



}
