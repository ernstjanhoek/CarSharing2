package carsharing.inherit;

import carsharing.DBClient;

public abstract class Dao {
    protected DBClient client;
    public Dao(DBClient client) {
        this.client = client;
    }
}
