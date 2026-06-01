package com.pgc.service;

import com.pgc.db.DBConnection;

public class MatchService {
    private OpenDotaService openDotaService;
    private DBConnection dbConnection;

    public MatchService() {
        this.openDotaService = new OpenDotaService();
        this.dbConnection = DBConnection.getInstance();
    }


}
