package com.allonsy.android.ftp;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FTP implements Serializable {
    private UUID mId;
    private String mConnectionName;
    private String mServerIP;
    private String mServerPort;
    private String mServerUsername;

    private List<String> mSources = new ArrayList<>();
    private String mDestination;

    public FTP() {
        // Generate unique identifier
        mId = UUID.randomUUID();
        mConnectionName="";
        mServerIP="";
        mServerPort="";
        mServerUsername="";
        mDestination="";
    }

    public FTP(UUID id) {
        mId = id;
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }


    public String getConnectionName() {
        return mConnectionName;
    }

    public void setConnectionName(String connectionName) {
        mConnectionName = connectionName;
    }

    public String getServerIP() {
        return mServerIP;
    }

    public void setServerIP(String serverIP) {
        mServerIP = serverIP;
    }

    public String getServerPort() {
        return mServerPort;
    }

    public void setServerPort(String serverPort) {
        mServerPort = serverPort;
    }

    public String getServerUsername() {
        return mServerUsername;
    }

    public void setServerUsername(String serverUsername) {
        mServerUsername = serverUsername;
    }

    public List<String> getSources() {
        return mSources;
    }

    public void setSources(List<String> sources) {
        mSources = sources;
    }

    public void addSource(String source) {
        mSources.add(source);
    }

    public String getDestination() {
        return mDestination;
    }

    public void setDestination(String destination) {
        mDestination = destination;
    }



}