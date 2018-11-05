package com.jjcosare.parser.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;

/**
 * Created by jjcosare on 10/29/18.
 * basic tests and testing coverage
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AccessServiceImplTests {

    @Autowired
    @Qualifier("accessServiceImpl")
    AccessService accessService;

    @Test
    public void loadDataAccessLogEmpty(){
        String accessLog = "";
        accessService.loadData(accessLog);
    }

    @Test
    public void loadDataAccessLogNull(){
        String accessLog = null;
        accessService.loadData(accessLog);
    }

    @Test
    public void loadDataAccessLogLocationWrong(){
        String accessLog = "./wrongLocation/access_test.log";
        accessService.loadData(accessLog);
    }

    @Test
    public void loadDataAccessLogLocationCorrect() throws FileNotFoundException {
        String accessLog = ResourceUtils.getFile("classpath:access_test.log").getAbsolutePath();
        accessService.loadData(accessLog);
    }

    @Test
    public void blockIPArgsEmpty(){
        String startDate = "";
        String duration = "";
        String threshold = "";
        accessService.blockIP(startDate, duration, threshold);
    }

    @Test
    public void blockIPArgsNull(){
        String startDate = null;
        String duration = null;
        String threshold = null;
        accessService.blockIP(startDate, duration, threshold);
    }

    @Test
    public void blockIPArgsException(){
        String startDate = ".";
        String duration = ".";
        String threshold = ".";
        accessService.blockIP(startDate, duration, threshold);
    }

    @Test
    public void blockIPArgsThresholdLessThan3(){
        String startDate = ".";
        String duration = ".";
        String threshold = "2";
        accessService.blockIP(startDate, duration, threshold);
    }

    @Test
    public void blockIPArgsCorrectWithNoBlockIp(){
        String startDate = "2017-01-01.00:00:00";
        String duration = "hourly";
        String threshold = "200";
        accessService.blockIP(startDate, duration, threshold);
    }

    @Test
    public void blockIPArgsCorrectWithBlockIp(){
        String startDate = "2017-01-01.00:00:00";
        String duration = "daily";
        String threshold = "3";
        accessService.blockIP(startDate, duration, threshold);
    }

    @Test
    public void displayAccessByIpCorrect(){
        String ip = "192.168.234.82";
        accessService.displayAccessByIp(ip);
    }

    @Test
    public void displayAccessByIpWrong(){
        String ip = "192.168.000.000";
        accessService.displayAccessByIp(ip);
    }

    @Test
    public void loadDataAndBlockIpString() throws FileNotFoundException{
        String accessLog = ResourceUtils.getFile("classpath:access_test.log").getAbsolutePath();
        String startDate = "2017-01-01.00:00:00";
        String duration = "daily";
        String threshold = "3";
        accessService.loadDataAndBlockIP(accessLog, startDate, duration, threshold);
    }

}
