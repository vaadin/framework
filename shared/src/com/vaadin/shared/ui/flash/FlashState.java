package com.vaadin.shared.ui.flash;

import java.util.Map;

import com.vaadin.shared.ui.AbstractEmbeddedState;

public class FlashState extends AbstractEmbeddedState {

    protected String classId;

    protected String codebase;

    protected String codetype;

    protected String archive;

    protected String standby;

    protected Map<String, String> embedParams;

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getCodebase() {
        return codebase;
    }

    public void setCodebase(String codeBase) {
        codebase = codebase;
    }

    public String getCodetype() {
        return codetype;
    }

    public void setCodetype(String codetype) {
        this.codetype = codetype;
    }

    public String getArchive() {
        return archive;
    }

    public void setArchive(String archive) {
        this.archive = archive;
    }

    public String getStandby() {
        return standby;
    }

    public void setStandby(String standby) {
        this.standby = standby;
    }

    public Map<String, String> getEmbedParams() {
        return embedParams;
    }

    public void setEmbedParams(Map<String, String> embedParams) {
        this.embedParams = embedParams;
    }
}
