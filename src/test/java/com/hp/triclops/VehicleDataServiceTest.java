package com.hp.triclops;

import com.hp.triclops.acquire.DataTool;
import com.hp.triclops.entity.DiagnosticData;
import com.hp.triclops.entity.GpsData;
import com.hp.triclops.entity.Position;
import com.hp.triclops.entity.TBoxParmSet;
import com.hp.triclops.service.VehicleDataService;
import com.hp.triclops.vo.RealTimeDataShow;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.util.Date;

/**
 * Created by luj on 2015/10/12.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class VehicleDataServiceTest {
    @Autowired
    VehicleDataService vehicleDataService;
    @Autowired
    DataTool dataTool;
    private Logger _logger = LoggerFactory.getLogger(VehicleDataServiceTest.class);

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Ignore("Not suitable for travis-ci")
    @Test
    public void test_remoteWakeUp() {
       vehicleDataService.remoteWakeUp("123456");
    }


    @Test
    @Transactional
    @Rollback
    public void test_handleRemoteControl() {
        Position p=new Position();
        p.setLatitude(30.123555);
        p.setLongitude(117.123456);
        System.out.println("handleRemoteControl return:" + vehicleDataService.handleRemoteControl(1, "1443151834l", (short) 15, (short) 22, p));
        //单元测试无法唤醒 总是null
    }

    @Test
    @Transactional
    @Rollback
    public void test_handleParmSet() {
        TBoxParmSet tBoxParmSet=new TBoxParmSet();
        tBoxParmSet.setSendingTime(new Date());
        tBoxParmSet.setVin("12345678919991234");
        int eventId=1444812349;
        tBoxParmSet.setEventId((long) eventId);
        tBoxParmSet.setFrequencySaveLocalMedia(1000);
        tBoxParmSet.setFrequencyForReport(1000);
        tBoxParmSet.setFrequencyForWarningReport(1000);
        tBoxParmSet.setFrequencyHeartbeat((short) 10);
        tBoxParmSet.setTimeOutForTerminalSearch(10);
        tBoxParmSet.setTimeOutForServerSearch(10);
        tBoxParmSet.setLicensePlate("京A12345");
        tBoxParmSet.setUploadType((short) 1);
        tBoxParmSet.setEnterpriseBroadcastAddress1("192.168.1.1");
        tBoxParmSet.setEnterpriseBroadcastPort1(9000);
        tBoxParmSet.setEnterpriseBroadcastAddress2("192.168.1.2");
        tBoxParmSet.setEnterpriseBroadcastPort2(9000);
        tBoxParmSet.setEnterpriseDomainName("www.baidu.com");
        tBoxParmSet.setEnterpriseDomainNameSize((short) 13);
        tBoxParmSet.setVersion("1.0.0.1");
        vehicleDataService.handleParmSet(tBoxParmSet);
    }

    @Ignore("Not suitable for travis-ci")
    @Test
    @Transactional
    @Rollback
     public void test_handleDiag() {
        DiagnosticData diagnosticData=new DiagnosticData();
        diagnosticData.setVin("12345678919991234");
        int eventId=1438573144;
        diagnosticData.setEventId((long) eventId);

        vehicleDataService.handleDiag(diagnosticData);
    }

    @Test
    public void test_getRealTimeData() {
       String vin="12345678919991235";
        RealTimeDataShow realTimeDataShow= vehicleDataService.getRealTimeData(vin);
        if(realTimeDataShow!=null){
            System.out.println(realTimeDataShow.getSendingTime());
        }

    }

    @Test
    public void test_getLatestGpseData() {
        String vin="12345678919991234";
        GpsData gd=vehicleDataService.getLatestGpseData(vin);
        if(gd!=null){
            System.out.println(gd.getSendingTime());
        }

    }


}
