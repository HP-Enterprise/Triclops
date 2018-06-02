package com.hp.triclops.service;

import com.alibaba.fastjson.JSON;
import com.hp.triclops.entity.*;
import com.hp.triclops.redis.SocketRedis;
import com.hp.triclops.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author: chenz
 * @Project: BriAir
 * @date: 2018/06/02 11:35
 * @version: v1.0
 */
@Component
public class SaveToDbService {
    @Autowired
    GpsDataRepository gpsDataRepository;
    @Autowired
    RegularReportDataRespository regularReportDataRespository;
    @Autowired
    RealTimeReportDataRespository realTimeReportDataRespository;
    @Autowired
    WarningMessageDataRespository warningMessageDataRespository;
    @Autowired
    FailureMessageDataRespository failureMessageDataRespository;
    @Autowired
    DrivingBehaviorDataRepository drivingBehaviorDataRepository;
    @Autowired
    DrivingBehaviorOriginalDataRepository drivingBehaviorOriginalDataRepository;

    @Autowired
    SocketRedis socketRedis;

    private Logger _logger = LoggerFactory.getLogger(SaveToDbService.class);

    ArrayBlockingQueue<GpsData> gpsDataQueue = new ArrayBlockingQueue<>(30);
    ArrayBlockingQueue<RegularReportData> regularReportDataQueue = new ArrayBlockingQueue<>(30);
    ArrayBlockingQueue<RealTimeReportData> realTimeReportDataQueue = new ArrayBlockingQueue<>(30);
    ArrayBlockingQueue<WarningMessageData> warningMessageDataQueue = new ArrayBlockingQueue<>(30);
    ArrayBlockingQueue<FailureMessageData> failureMessageDataQueue = new ArrayBlockingQueue<>(30);
    ArrayBlockingQueue<DrivingBehaviorData> drivingBehaviorDataQueue = new ArrayBlockingQueue<>(30);
    ArrayBlockingQueue<DrivingBehavioOriginalData> drivingBehaviorOriginalDataQueue = new ArrayBlockingQueue<>(30);


    /**
     * 保存车辆位置信息
     *
     * @param gpsData
     */
    public void saveGpsData(GpsData gpsData) {
        if (!gpsDataQueue.offer(gpsData)) {
            synchronized (gpsDataQueue) {
                if (!gpsDataQueue.offer(gpsData)) {
                    List<GpsData> gpsDataList = new ArrayList<>(30);
                    gpsDataQueue.drainTo(gpsDataList);
                    long startTime = System.currentTimeMillis();
                    gpsDataRepository.save(gpsDataList);
                    long endTime = System.currentTimeMillis();
                    _logger.warn("saveGpsData Analysis data time" + (endTime - startTime));
                    gpsDataQueue.offer(gpsData);
                }
            }
        }

    }

    /**
     * 额定数据表
     *
     * @param reportData
     */
    public void saveRegularReportData(RegularReportData reportData) {
        if (!regularReportDataQueue.offer(reportData)) {
            synchronized (regularReportDataQueue) {
                if (!regularReportDataQueue.offer(reportData)) {
                    List<RegularReportData> regularReportDataList = new ArrayList<>(30);
                    regularReportDataQueue.drainTo(regularReportDataList);
                    long startTime = System.currentTimeMillis();
                    regularReportDataRespository.save(regularReportDataList);
                    long endTime = System.currentTimeMillis();
                    _logger.warn("saveRegularReportData Analysis data time" + (endTime - startTime));
                    regularReportDataQueue.offer(reportData);
                }
            }
        }

    }

    /**
     * 实时数据表
     *
     * @param realTimeReportData
     */
    public void saveRealTimeReportData(RealTimeReportData realTimeReportData) {
        if (!realTimeReportDataQueue.offer(realTimeReportData)) {
            synchronized (realTimeReportDataQueue) {
                if (!realTimeReportDataQueue.offer(realTimeReportData)) {
                    List<RealTimeReportData> realTimeReportDataList = new ArrayList<>(30);
                    realTimeReportDataQueue.drainTo(realTimeReportDataList);
                    long startTime = System.currentTimeMillis();
                    realTimeReportDataRespository.save(realTimeReportDataList);
                    long endTime = System.currentTimeMillis();
                    _logger.warn("saveRealTimeReportData Analysis data time" + (endTime - startTime));
                    realTimeReportDataQueue.offer(realTimeReportData);
                }
            }
        }
    }

    /**
     * 报警数据表
     *
     * @param warningMessageData
     */
    public void saveWarningMessageData(WarningMessageData warningMessageData) {
        if (!warningMessageDataQueue.offer(warningMessageData)) {
            synchronized (warningMessageDataQueue) {
                if (!warningMessageDataQueue.offer(warningMessageData)) {
                    List<WarningMessageData> warningMessageDataList = new ArrayList<>(30);
                    warningMessageDataQueue.drainTo(warningMessageDataList);
                    long startTime = System.currentTimeMillis();
                    warningMessageDataRespository.save(warningMessageDataList);
                    long endTime = System.currentTimeMillis();
                    _logger.warn("saveWarningMessageData Analysis data time" + (endTime - startTime));
                    warningMessageDataQueue.offer(warningMessageData);
                }
            }
        }
    }

    /**
     * 失效数据
     *
     * @param failureMessageData
     */
    public void saveFailureMessageData(FailureMessageData failureMessageData) {
        if (!failureMessageDataQueue.offer(failureMessageData)) {
            synchronized (failureMessageDataQueue) {
                if (!failureMessageDataQueue.offer(failureMessageData)) {
                    List<FailureMessageData> failureMessageDataList = new ArrayList<>(30);
                    failureMessageDataQueue.drainTo(failureMessageDataList);
                    long startTime = System.currentTimeMillis();
                    failureMessageDataRespository.save(failureMessageDataList);
                    long endTime = System.currentTimeMillis();
                    _logger.warn("saveFailureMessageData Analysis data time" + (endTime - startTime));
                    failureMessageDataQueue.offer(failureMessageData);
                }
            }
        }

        failureMessageDataQueue.offer(failureMessageData);
        //保存最新的故障信息
        socketRedis.saveValueString("failure:" + failureMessageData.getVin(), JSON.toJSONString(failureMessageData), -1);
    }

    /**
     * 根据vin码查询redis中最新的故障信息
     *
     * @param vin
     * @return
     */
    public FailureMessageData findLastFailureByVin(String vin) {
        String failureStr = socketRedis.getValueString("failure:" + vin);
        FailureMessageData data = (FailureMessageData) JSON.parse(failureStr);
        return data;
    }

    /**
     * 驾驶行为原始报文数据表
     *
     * @param drivingBehaviorData
     */
    public void saveDrivingBehaviorData(DrivingBehaviorData drivingBehaviorData) {
        if (!drivingBehaviorDataQueue.offer(drivingBehaviorData)) {
            synchronized (drivingBehaviorDataQueue) {
                if (!drivingBehaviorDataQueue.offer(drivingBehaviorData)) {
                    List<DrivingBehaviorData> drivingBehaviorDataList = new ArrayList<>(30);
                    drivingBehaviorDataQueue.drainTo(drivingBehaviorDataList);
                    long startTime = System.currentTimeMillis();
                    drivingBehaviorDataRepository.save(drivingBehaviorDataList);
                    long endTime = System.currentTimeMillis();
                    _logger.warn("saveDrivingBehaviorData Analysis data time" + (endTime - startTime));
                    drivingBehaviorDataQueue.offer(drivingBehaviorData);
                }
            }
        }
    }

    /**
     * 驾驶行为数据表
     *
     * @param drivingBehavioOriginalData
     */
    public void saveDrivingBehaviorOriginalData(DrivingBehavioOriginalData drivingBehavioOriginalData) {
        if (!drivingBehaviorOriginalDataQueue.offer(drivingBehavioOriginalData)) {
            synchronized (drivingBehaviorOriginalDataQueue) {
                if (!drivingBehaviorOriginalDataQueue.offer(drivingBehavioOriginalData)) {
                    List<DrivingBehavioOriginalData> drivingBehavioOriginalDataList = new ArrayList<>(30);
                    drivingBehaviorOriginalDataQueue.drainTo(drivingBehavioOriginalDataList);
                    long startTime = System.currentTimeMillis();
                    drivingBehaviorOriginalDataRepository.save(drivingBehavioOriginalDataList);
                    long endTime = System.currentTimeMillis();
                    _logger.warn("saveDrivingBehaviorOriginalData Analysis data time" + (endTime - startTime));
                    drivingBehaviorOriginalDataQueue.offer(drivingBehavioOriginalData);
                }
            }
        }
    }
}
