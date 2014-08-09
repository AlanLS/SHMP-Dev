/**
 *
 * @author Sasi
 */
public class GeneralAction {

    /** Clear All value */
    private boolean clearAll = false;

    /** Next action goto Id */
    private int gotoid = -1;

    /** Scprefix Name */
    private String scPrefix = null;

    /** Logical Branch table Value */
    private LogicalBranchTable[] LBTable = null;

    /** Clear single Value */
    private boolean cSingleValue = false;

    private GenericAssignTable[] gTable = null;

    private int id = -1;

    /** Get the Generic Action Table */
    public GenericAssignTable[] getGTable() {
        return gTable;
    }

    /** Set the Generic Action Table */
    public void setGTable(GenericAssignTable[] gTable) {
        this.gTable = gTable;
    }

    /** Get the action id */
    public int getid() {
        return id;
    }

    /** Set the action id */
    public void setid(int id) {
        this.id = id;
    }

    /** Get the Single Value Clear Byte */
    public boolean isCSingleValue() {
        return cSingleValue;
    }

    /** Set the Single Value Clear Byte */
    public void setCSingleValue(boolean cSingleValue) {
        this.cSingleValue = cSingleValue;
    }

    /** Set the Logical Branch table */
    public LogicalBranchTable[] getLBTable() {
        return LBTable;
    }

    /** Get the Logical Branch Table */
    public void setLBTable(LogicalBranchTable[] LBTable) {
        this.LBTable = LBTable;
    }

    /** Get the scprefix Name */
    public String getScPrefix() {
        return scPrefix;
    }

    /** Set the scprefix Name */
    public void setScPrefix(String scPrefix) {
        this.scPrefix = scPrefix;
    }


    /** Get Next action goto id */
    public int getGotoid() {
        return gotoid;
    }

    /** Set next action goto id */
    public void setGotoid(int gotoid) {
        this.gotoid = gotoid;
    }

    /** Is All value clear enable byte */
    public boolean isClearAll() {
        return clearAll;
    }

    /** Set All value Clear enable byte */
    public void setClearAll(boolean clearAll) {
        this.clearAll = clearAll;
    }



}