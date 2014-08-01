package com.tv.xeeng.workflow;

import com.tv.xeeng.base.common.ConfigProperties;
import com.tv.xeeng.base.common.ServerException;

public class WorkflowConfig {

    private final ConfigProperties mConfig;
    final String DEFAULT_WORKFLOW_CONFIG = "conf/workflow-config.xml";
    @SuppressWarnings("unused")
	private final String WF_APPNAME = "workflow.appname";
    @SuppressWarnings("unused")
    private final String WF_DB_ENABLE = "workflow.db.enable";
    @SuppressWarnings("unused")
    private final String WF_BUSINESS_PROPERTIES_FACTORY = "workflow.business.properties.factory";
    @SuppressWarnings("unused")
    private final String WF_SERVER_NAME = "workflow.server.name";
    @SuppressWarnings("unused")
    private final String WF_SERVER_PORT = "workflow.server.port";
    @SuppressWarnings("unused")
    private final String WF_SERVER_CONNECTTIMEOUT = "workflow.server.connecttimeout";
    @SuppressWarnings("unused")
    private final String WF_SERVER_SESSIONTIMEOUT = "workflow.server.sessiontimeout";
    @SuppressWarnings("unused")
    private final String WF_SERVER_RECEIVEBUFFERSIZE = "workflow.server.receivebuffersize";
    @SuppressWarnings("unused")
    private final String WF_SERVER_REUSEADDRESS = "workflow.server.reuseaddress";
    @SuppressWarnings("unused")
    private final String WF_SERVER_TCPNODELAY = "workflow.server.tcpnodelay";

    WorkflowConfig()
            throws ServerException {
        this.mConfig = new ConfigProperties();
        this.mConfig.load(DEFAULT_WORKFLOW_CONFIG);
    }

    public String appName() {
        return this.mConfig.getString("workflow.appname");
    }

    public boolean enableDB() {
        return this.mConfig.getBoolean("workflow.db.enable");
    }

    public String getDBFactoryName() {
        return this.mConfig.getString("workflow.db.factory-name");
    }

    public String getDBModelName() {
        return this.mConfig.getString("workflow.db.modelname");
    }

    public String getBusinessPropertiesFactory() {
        return this.mConfig.getString("workflow.business.properties.factory");
    }

    public String getServerName() {
        return this.mConfig.getString("workflow.server.name");
    }

    public int getServerPort() {
        return this.mConfig.getInt("workflow.server.port");
    }

    public int getServerConnectTimeout() {
        return this.mConfig.getInt("workflow.server.connecttimeout");
    }

    public int getSessionTimeout() {
        return this.mConfig.getInt("workflow.server.sessiontimeout");
    }

    public int getServerReceiveBufferSize() {
        return this.mConfig.getInt("workflow.server.receivebuffersize");
    }

    public String getDBAccount() {
        return this.mConfig.getString("workflow.db.account");
    }
    public String getDBPassword() {
        return this.mConfig.getString("workflow.db.pass");
    }
    public String getDBUrl() {
        return this.mConfig.getString("workflow.db.url");
    }


    public boolean getReuseAddress() {
        return this.mConfig.getBoolean("workflow.server.reuseaddress");
    }

    public boolean getTcpNoDelay() {
        return this.mConfig.getBoolean("workflow.server.tcpnodelay");
    }

    public boolean enableScheduler() {
        return this.mConfig.getBoolean("workflow.scheduler.enable");
    }
    
     public String getCenterServerName() {
        return this.mConfig.getString("workflow.center-server.hostname");
    }

    public int getCenterServerPort() {
        return this.mConfig.getInt("workflow.center-server.port");
    }
    
    public boolean isLoadQuestion() {
        return this.mConfig.getBoolean("workflow.loadQuestion");
    }

    public boolean isLoadMonthlyEvent() {
        return this.mConfig.getBoolean("workflow.loadMonthlyEvent");
    }
    
    public boolean isUseCache() {
        return this.mConfig.getBoolean("workflow.useCached");
    }
    
    public double getRealGotMoney() {
        return Double.parseDouble(this.mConfig.getString("workflow.realGotMoney"));
    }

    public int getMoneyTransferDayLimit() {
        return Integer.parseInt(this.mConfig.getString("workflow.moneyTransferDayLimit"));
    }

    public int getMoneyTransferTax() {
        return Integer.parseInt(this.mConfig.getString("workflow.moneyTransferTax"));
    }        
}
