package com.hp.triclops.service;

import com.alibaba.fastjson.JSON;
import com.hp.triclops.entity.*;
import com.hp.triclops.redis.SocketRedis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
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
    JdbcTemplate jdbcTemplate;

    @Autowired
    SocketRedis socketRedis;


    private Logger _logger = LoggerFactory.getLogger(SaveToDbService.class);

    private ArrayBlockingQueue<GpsData> gpsDataQueue = new ArrayBlockingQueue<>(30);
    private ArrayBlockingQueue<RegularReportData> regularReportDataQueue = new ArrayBlockingQueue<>(30);
    private ArrayBlockingQueue<RealTimeReportData> realTimeReportDataQueue = new ArrayBlockingQueue<>(30);
    private ArrayBlockingQueue<DrivingBehaviorData> drivingBehaviorDataQueue = new ArrayBlockingQueue<>(30);
    private ArrayBlockingQueue<DrivingBehavioOriginalData> drivingBehaviorOriginalDataQueue = new ArrayBlockingQueue<>(30);


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
                    String sql = "insert into t_data_gps(vin,imei,application_id,message_id,sending_time,is_location,north_south,east_west,latitude,longitude,speed,heading) " +
                        "values(?,?,?,?,?,?,?,?,?,?,?,?)";


                    jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            GpsData gpsData = gpsDataList.get(i);
                            ps.setString(1, gpsData.getVin());
                            ps.setString(2, gpsData.getImei());
                            ps.setInt(3, gpsData.getApplicationId());
                            ps.setInt(4, gpsData.getMessageId());
                            ps.setTimestamp(5, new Timestamp(gpsData.getSendingTime().getTime()));
                            ps.setInt(6, gpsData.getIsLocation());
                            ps.setString(7, gpsData.getNorthSouth());
                            ps.setString(8, gpsData.getEastWest());
                            ps.setDouble(9, gpsData.getLatitude());
                            ps.setDouble(10, gpsData.getLongitude());
                            ps.setFloat(11, gpsData.getSpeed());
                            ps.setInt(12, gpsData.getHeading());
                        }

                        @Override
                        public int getBatchSize() {
                            return gpsDataList.size();
                        }
                    });

                    long endTime = System.currentTimeMillis();
                    _logger.warn("=====saveGpsData Analysis data time=====" + (endTime - startTime));
                    gpsDataQueue.offer(gpsData);
                }
            }
        }

    }

    /**
     * 额定数据表 18
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
                    String sql = "INSERT INTO t_data_regular_report(`vin`, `imei`, `application_id`, `message_id`, `sending_time`, " +
                        "`frequency_for_realtime_report`, `frequency_for_warning_report`, `frequency_heartbeat`, " +
                        "`timeout_for_terminal_search`, `timeout_for_server_search`, `vehicle_type`, `vehicle_models`, " +
                        "`max_speed`, `hardware_version`, `software_version`, `frequency_save_local_media`, " +
                        "`enterprise_broadcast_address`, `enterprise_broadcast_port`) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


                    jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            RegularReportData reportData = regularReportDataList.get(i);
                            ps.setString(1, reportData.getVin());
                            ps.setString(2, reportData.getImei());
                            ps.setInt(3, reportData.getApplicationId());
                            ps.setInt(4, reportData.getMessageId());
                            ps.setTimestamp(5, new Timestamp(reportData.getSendingTime().getTime()));
                            ps.setInt(6, reportData.getFrequencyForRealTimeReport());
                            ps.setInt(7, reportData.getFrequencyForWarningReport());
                            ps.setInt(8, reportData.getFrequencyHeartbeat());
                            ps.setInt(9, reportData.getTimeOutForTerminalSearch());
                            ps.setInt(10, reportData.getTimeOutForServerSearch());
                            ps.setInt(11, reportData.getVehicleType());
                            ps.setInt(12, reportData.getVehicleModels());
                            ps.setInt(13, reportData.getMaxSpeed());
                            ps.setString(14, reportData.getHardwareVersion());
                            ps.setString(15, reportData.getSoftwareVersion());
                            ps.setInt(16, reportData.getFrequencySaveLocalMedia());
                            ps.setString(17, reportData.getEnterpriseBroadcastAddress());
                            ps.setInt(18, reportData.getEnterpriseBroadcastPort());
                        }

                        @Override
                        public int getBatchSize() {
                            return regularReportDataList.size();
                        }
                    });
                    long endTime = System.currentTimeMillis();
                    _logger.warn("=====saveRegularReportData Analysis data time=====" + (endTime - startTime));
                    regularReportDataQueue.offer(reportData);
                }
            }
        }

    }

    /**
     * 实时数据表43
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

                    String sql = "INSERT INTO t_data_realtime_report(`vin`, `imei`, `application_id`, `message_id`, `sending_time`, `driving_time`, " +
                        "`trip_id`, `oil_life`, `fuel_oil`, `avg_oil_a`, `avg_oil_b`, `driving_range`, `mileage_range`, `service_intervall`, " +
                        "`left_front_tire_pressure`, `left_rear_tire_pressure`, `right_front_tire_pressure`, `right_rear_tire_pressure`, " +
                        "`left_front_window_information`, `left_rear_window_information`, `right_front_window_information`, " +
                        "`right_rear_window_information`, `vehicle_temperature`, `vehicle_outer_temperature`, `left_front_door_information`, " +
                        "`left_rear_door_information`, `right_front_door_information`, `right_rear_door_information`, `engine_cover_state`, " +
                        "`trunk_lid_state`, `skylight_state`, `parking_state`, `voltage`, `average_speed_a`, `average_speed_b`, " +
                        "`mt_gear_postion`, `engine_state`, `lf_lock_state`, `lr_lock_state`, `rf_lock_state`, `rr_lock_state`, `blow`, `ac_state`)" +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


                    jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            RealTimeReportData realTimeReportData = realTimeReportDataList.get(i);
                            ps.setString(1, realTimeReportData.getVin());
                            ps.setString(2, realTimeReportData.getImei());
                            ps.setInt(3, realTimeReportData.getApplicationId());
                            ps.setInt(4, realTimeReportData.getMessageId());
                            ps.setTimestamp(5, new Timestamp(realTimeReportData.getSendingTime().getTime()));
                            ps.setInt(6, realTimeReportData.getDrivingTime());
                            ps.setInt(7, realTimeReportData.getTripId());
                            ps.setInt(8, realTimeReportData.getOilLife());
                            ps.setFloat(9, realTimeReportData.getFuelOil());
                            ps.setFloat(10, realTimeReportData.getAvgOilA());
                            ps.setFloat(11, realTimeReportData.getAvgOilB());
                            ps.setInt(12, realTimeReportData.getDrivingRange());
                            ps.setInt(13, realTimeReportData.getMileageRange());
                            ps.setInt(14, realTimeReportData.getServiceIntervall());
                            ps.setFloat(15, realTimeReportData.getLeftFrontTirePressure());
                            ps.setFloat(16, realTimeReportData.getLeftRearTirePressure());
                            ps.setFloat(17, realTimeReportData.getRightFrontTirePressure());
                            ps.setFloat(18, realTimeReportData.getRightRearTirePressure());
                            ps.setString(19, realTimeReportData.getLeftFrontWindowInformation());
                            ps.setString(20, realTimeReportData.getLeftRearWindowInformation());
                            ps.setString(21, realTimeReportData.getRightFrontWindowInformation());
                            ps.setString(22, realTimeReportData.getRightRearWindowInformation());
                            ps.setFloat(23, realTimeReportData.getVehicleTemperature());
                            ps.setFloat(24, realTimeReportData.getVehicleOuterTemperature());
                            ps.setString(25, realTimeReportData.getLeftFrontDoorInformation());
                            ps.setString(26, realTimeReportData.getLeftRearDoorInformation());
                            ps.setString(27, realTimeReportData.getRightFrontDoorInformation());
                            ps.setString(28, realTimeReportData.getRightRearDoorInformation());
                            ps.setString(29, realTimeReportData.getEngineCoverState());
                            ps.setString(30, realTimeReportData.getTrunkLidState());
                            ps.setString(31, realTimeReportData.getSkylightState());
                            ps.setString(32, realTimeReportData.getParkingState());
                            ps.setFloat(33, realTimeReportData.getVoltage());
                            ps.setInt(34, realTimeReportData.getAverageSpeedA());
                            ps.setInt(35, realTimeReportData.getAverageSpeedB());
                            ps.setString(36, realTimeReportData.getMtGearPostion());
                            ps.setInt(37, realTimeReportData.getEngineState());
                            ps.setInt(38, realTimeReportData.getLfLockState());
                            ps.setInt(39, realTimeReportData.getLrLockState());
                            ps.setInt(40, realTimeReportData.getRfLockState());
                            ps.setInt(41, realTimeReportData.getRrLockState());
                            ps.setInt(42, realTimeReportData.getBlow());
                            ps.setInt(43, realTimeReportData.getAcState());
                        }

                        @Override
                        public int getBatchSize() {
                            return realTimeReportDataList.size();
                        }
                    });
                    long endTime = System.currentTimeMillis();
                    _logger.warn("=====saveRealTimeReportData Analysis data time=====" + (endTime - startTime));
                    realTimeReportDataQueue.offer(realTimeReportData);
                }
            }
        }
    }

    /**
     * 报警数据表18
     *
     * @param warningMessageData
     */
    public void saveWarningMessageData(WarningMessageData warningMessageData) {

        long startTime = System.currentTimeMillis();
        String sql = "INSERT INTO t_data_warning_message (`vin`, `imei`, `application_id`, `message_id`, `sending_time`, " +
            "`receive_time`, `is_location`, `north_south`, `east_west`, `latitude`, `longitude`, " +
            "`speed`, `heading`, `srs_warning`, `crash_warning`, `ata_warning`, `safety_belt_count`, `vehicle_hit_speed`) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


        jdbcTemplate.update(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, warningMessageData.getVin());
                ps.setString(2, warningMessageData.getImei());
                ps.setInt(3, warningMessageData.getApplicationId());
                ps.setInt(4, warningMessageData.getMessageId());
                ps.setTimestamp(5, new Timestamp(warningMessageData.getSendingTime().getTime()));
                ps.setTimestamp(6, new Timestamp(warningMessageData.getReceiveTime().getTime()));
                ps.setInt(7, warningMessageData.getIsLocation());
                ps.setString(8, warningMessageData.getNorthSouth());
                ps.setString(9, warningMessageData.getEastWest());
                ps.setDouble(10, warningMessageData.getLatitude());
                ps.setDouble(11, warningMessageData.getLongitude());
                ps.setFloat(12, warningMessageData.getSpeed());
                ps.setInt(13, warningMessageData.getHeading());
                ps.setInt(14, warningMessageData.getSrsWarning());
                ps.setInt(15, warningMessageData.getCrashWarning());
                ps.setInt(16, warningMessageData.getAtaWarning());
                ps.setInt(17, warningMessageData.getSafetyBeltCount());
                ps.setInt(18, warningMessageData.getVehicleHitSpeed());
            }
        });

        long endTime = System.currentTimeMillis();
        _logger.warn("=====saveWarningMessageData Analysis data time=====" + (endTime - startTime));
    }

    /**
     * 故障数据14
     *
     * @param failureMessageData
     */
    public void saveFailureMessageData(FailureMessageData failureMessageData) {
        long startTime = System.currentTimeMillis();
        String sql = "INSERT INTO t_data_failure_message (`vin`, `imei`, `application_id`, `message_id`, `sending_time`, `receive_time`, `is_location`, `north_south`, " +
            "`east_west`, `latitude`, `longitude`, `speed`, `heading`, `info`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


        jdbcTemplate.update(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, failureMessageData.getVin());
                ps.setString(2, failureMessageData.getImei());
                ps.setInt(3, failureMessageData.getApplicationId());
                ps.setInt(4, failureMessageData.getMessageId());
                ps.setTimestamp(5, new Timestamp(failureMessageData.getSendingTime().getTime()));
                ps.setTimestamp(6, new Timestamp(failureMessageData.getReceiveTime().getTime()));
                ps.setInt(7, failureMessageData.getIsLocation());
                ps.setString(8, failureMessageData.getNorthSouth());
                ps.setString(9, failureMessageData.getEastWest());
                ps.setDouble(10, failureMessageData.getLatitude());
                ps.setDouble(11, failureMessageData.getLongitude());
                ps.setFloat(12, failureMessageData.getSpeed());
                ps.setInt(13, failureMessageData.getHeading());
                ps.setString(14, failureMessageData.getInfo());
            }
        });


        long endTime = System.currentTimeMillis();
        _logger.warn("=====saveFailureMessageData Analysis data time=====" + (endTime - startTime));

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
        FailureMessageData data = JSON.parseObject(failureStr, FailureMessageData.class);
        return data;
    }

    /**
     * 驾驶行为原始报文数据表 27
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
                    String sql = "INSERT INTO `t_data_driving_behavior` (`vin`, `imei`, `application_id`, `message_id`, `trip_id`, `sending_time`, `receive_time`, `speed_up`, `speed_down`, `speed_turn`, `trip_a`, `trip_b`, `seatbelt_fl`, `seatbelt_fr`, `seatbelt_rl`, `seatbelt_rm`, `seatbelt_rr`, `driving_range`, `fuel_oil`, `avg_oil_a`, `avg_oil_b`, `speed_1_count`, `speed_1_45_count`, `speed_45_90_count`, `speed_90_count`, `speed_up_count`, `max_speed`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                    jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            DrivingBehaviorData drivingBehaviorData = drivingBehaviorDataList.get(i);
                            ps.setString(1, drivingBehaviorData.getVin());
                            ps.setString(2, drivingBehaviorData.getImei());
                            ps.setInt(3, drivingBehaviorData.getApplicationId());
                            ps.setInt(4, drivingBehaviorData.getMessageId());
                            ps.setInt(5, drivingBehaviorData.getTripId());
                            ps.setTimestamp(5, new Timestamp(drivingBehaviorData.getSendingTime().getTime()));
                            ps.setTimestamp(6, new Timestamp(drivingBehaviorData.getReceiveTime().getTime()));
                            ps.setInt(8, drivingBehaviorData.getSpeedUp());
                            ps.setInt(9, drivingBehaviorData.getSpeedDown());
                            ps.setInt(10, drivingBehaviorData.getSpeedTurn());
                            ps.setFloat(11, drivingBehaviorData.getTripA());
                            ps.setFloat(12, drivingBehaviorData.getTripB());
                            ps.setString(13, drivingBehaviorData.getSeatbeltFl());
                            ps.setString(14, drivingBehaviorData.getSeatbeltFr());
                            ps.setString(15, drivingBehaviorData.getSeatbeltRl());
                            ps.setString(16, drivingBehaviorData.getSeatbeltRm());
                            ps.setString(17, drivingBehaviorData.getSeatbeltRr());
                            ps.setInt(18, drivingBehaviorData.getDrivingRange());
                            ps.setFloat(19, drivingBehaviorData.getFuelOil());
                            ps.setFloat(20, drivingBehaviorData.getAvgOilA());
                            ps.setFloat(21, drivingBehaviorData.getAvgOilB());
                            ps.setInt(22, drivingBehaviorData.getSpeed_1_count());
                            ps.setInt(23, drivingBehaviorData.getSpeed_1_45_count());
                            ps.setInt(24, drivingBehaviorData.getSpeed_45_90_count());
                            ps.setInt(25, drivingBehaviorData.getSpeed_90_count());
                            ps.setInt(26, drivingBehaviorData.getSpeed_up_count());
                            ps.setInt(27, drivingBehaviorData.getMax_speed());
                        }

                        @Override
                        public int getBatchSize() {
                            return drivingBehaviorDataList.size();
                        }
                    });
                    long endTime = System.currentTimeMillis();
                    _logger.warn("=====saveDrivingBehaviorData Analysis data time=====" + (endTime - startTime));
                    drivingBehaviorDataQueue.offer(drivingBehaviorData);
                }
            }
        }
    }

    /**
     * 驾驶行为数据表4
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
                    String sql = "INSERT INTO `t_data_original_driving_behavior` (`vin`, `imei`, `hex_string`, `receive_time`) VALUES (?, ?, ?, ?)";

                    jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            DrivingBehavioOriginalData drivingBehavioOriginalData = drivingBehavioOriginalDataList.get(i);
                            ps.setString(1, drivingBehavioOriginalData.getVin());
                            ps.setString(2, drivingBehavioOriginalData.getImei());
                            ps.setString(3, drivingBehavioOriginalData.getHexString());
                            ps.setTimestamp(4, new Timestamp(drivingBehavioOriginalData.getReceiveTime().getTime()));
                        }

                        @Override
                        public int getBatchSize() {
                            return drivingBehavioOriginalDataList.size();
                        }
                    });
                    long endTime = System.currentTimeMillis();
                    _logger.warn("=====saveDrivingBehaviorOriginalData Analysis data time=====" + (endTime - startTime));
                    drivingBehaviorOriginalDataQueue.offer(drivingBehavioOriginalData);
                }
            }
        }
    }
}
