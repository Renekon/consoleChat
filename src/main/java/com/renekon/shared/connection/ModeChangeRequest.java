package com.renekon.shared.connection;

public class ModeChangeRequest {
    public NioSocketConnection connection;
    int ops;

    ModeChangeRequest(NioSocketConnection connection, int ops) {
        this.connection = connection;
        this.ops = ops;
    }
}
