package com.jjcosare.parser.service;

/**
 * Created by jjcosare on 10/28/18.
 */
public interface AccessService {

    void displayAccessByIp (String ip);

    void loadData (String accessLogPath);

    void blockIP (String startDate, String duration, String threshold);

    void loadDataAndBlockIP (String accessLog, String startDate, String duration, String threshold);

}
