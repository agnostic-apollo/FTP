package com.allonsy.android.ftp.database;


public class FTPDbSchema {
    public static final class ConnectionTable {
        public static final String NAME = "connection";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String CONNECTION_NAME = "connectionName";
            public static final String Server_IP = "serverIP";
            public static final String Server_PORT = "serverPort";
            public static final String Server_USERNAME = "serverUsername";
            public static final String Server_DESTINATION = "serverDestination";
        }
    }

    public static final class SourceTable {
        public static final String NAME = "sources";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String SOURCE = "source";
        }
    }
}
