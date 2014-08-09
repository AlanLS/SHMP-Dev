
/**
 *
 * @author Sasikumar
 */
public class GenericAssignTable {

    /** Variable Name */
    private String varName = null;

    /** Variable value */
    private String varValue = null;

    /** Status to save in memory or not **/
    private boolean isMem=false;
     
    public void setIsMem(boolean isM){
        isMem = isM;
    }
    
    public boolean getIsMem(){
        return isMem;
    }
    
    /** Get the Variable Name */
    public String getVarName() {
        return varName;
    }

    /** set the Variable Name */
    public void setVarName(String varName) {
        this.varName = varName;
    }

    /** Get the Variable Value */
    public String getVarValue() {
        return varValue;
    }

    /** Set the Variable Value */
    public void setVarValue(String varValue) {
        this.varValue = varValue;
    }



}
