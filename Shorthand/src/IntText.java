public class IntText {
	String srctxt;// search text

	// action id - no need to store in byte array instead we store pointer
	int pgotoid; // pointer to next action

        private boolean isImediateGoto = false;

        public boolean isIsImediateGoto() {
            return isImediateGoto;
        }

        public void setIsImediateGoto(boolean isImediateGoto) {
            this.isImediateGoto = isImediateGoto;
        }

	public IntText(String srctxt, int pgotoid) {
		this.srctxt = srctxt;
		this.pgotoid = pgotoid;
	}

	public void setSrctxt(String srctxt) {
		this.srctxt = srctxt;
	}

	public String getSrctxt() {
		return this.srctxt;
	}

	public void setPgotoid(int pgotoid) {
		this.pgotoid = pgotoid;
	}

	public int getPgotoid() {
		return this.pgotoid;
	}

}
