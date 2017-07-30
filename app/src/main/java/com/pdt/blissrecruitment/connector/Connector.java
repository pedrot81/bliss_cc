package com.pdt.blissrecruitment.connector;

public class Connector {
    private static volatile IConnector sInstance;

    public static IConnector getInstance() {
        if (sInstance == null) {
            synchronized (Connector.class) {
                if (sInstance == null) {
                    sInstance = new ConnectorImpl();
                }
            }
        }

        return sInstance;
    }
}
