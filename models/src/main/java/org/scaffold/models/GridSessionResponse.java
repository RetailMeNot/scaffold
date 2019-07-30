package io.github.kgress.scaffold.models;

public class GridSessionResponse {
    private String internalKey;
    private String session;
    private int inactivityTime;
    private String proxyId;
    private boolean success;
    private String msg;

    public String getInternalKey() {
        return internalKey;
    }

    public String getSession() {
        return session;
    }

    public int getInactivityTime() {
        return inactivityTime;
    }

    public String getProxyId() {
        return proxyId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMsg() {
        return msg;
    }

    public void setInternalKey(String internalKey) {
        this.internalKey = internalKey;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public void setInactivityTime(int inactivityTime) {
        this.inactivityTime = inactivityTime;
    }

    public void setProxyId(String proxyId) {
        this.proxyId = proxyId;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
