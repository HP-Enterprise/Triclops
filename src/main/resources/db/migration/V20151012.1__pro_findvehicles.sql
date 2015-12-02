delimiter //

CREATE PROCEDURE pro_findvehicles(uid int,vin VARCHAR(50), tboxsn VARCHAR(50), vendor VARCHAR(100), fuzzy int, model VARCHAR(100),
t_flag int,displacement VARCHAR(20),license_plate VARCHAR(10),firstRcord int, pageSize int, orderByProperty VARCHAR(15), ascOrDesc VARCHAR(5),start_date datetime ,end_date datetime)
BEGIN
  DROP TABLE IF EXISTS the_vehicles;
--	CREATE TABLE IF NOT EXISTS the_vehicles
--(
--  id int(11) ,
--  vin varchar(50) ,
--  tboxsn varchar(50) ,
--  vendor varchar(100) ,
--  model varchar(100) ,
--  t_flag int ,
--  displacement varchar(20) ,
--  license_plate varchar(10) ,
--  product_date datetime ,
--  PRIMARY KEY (id),
--  UNIQUE key (vin)
-- -- UNIQUE key (tboxsn)
--) ENGINE = MEMORY;

CREATE TEMPORARY TABLE the_vehicles SELECT * FROM t_vehicle WHERE 1=2;
			INSERT INTO the_vehicles SELECT DISTINCT V.* FROM t_vehicle V LEFT JOIN t_organization_vehicle OV ON v.id = ov.vid WHERE ov.oid in
             (SELECT O.id FROM t_organization O 
                 LEFT JOIN t_authoritygroup AG ON O.id = AG.oid 
                 LEFT JOIN t_authoritygroup_relatived AGR ON AG.id = AGR.ag_id 
                 LEFT JOIN t_authoritygroup_user AGU ON AGR.ag_id = AGU.ag_id WHERE AGR.a_id = 0 AND AGU.u_id = uid);    
            SET @vin = vin;
            SET @tboxsn = tboxsn;
            SET @vendor = vendor;            
            SET @model = model;
            SET @t_flag = t_flag;
            SET @displacement = displacement;
            SET @license_plate = license_plate;
            SET @firstRcord = firstRcord;
            SET @pageSize = pageSize;
            SET @orderByProperty = orderByProperty;
            SET @ascOrDesc = ascOrDesc;
            SET @start_date = start_date;
            SET @end_date = end_date;
            SET @sql = "SELECT * FROM the_vehicles v ";
            SET @sql = CONCAT(@sql, " WHERE (@t_flag = 0 OR v.t_flag = ?) ");
            SET @sql = CONCAT(@sql, " AND (@vin is null OR v.vin like ?)");
            SET @sql = CONCAT(@sql, " AND (@tboxsn is null OR v.tboxsn like ?)");
            IF FUZZY = 1 THEN
                 SET @sql = CONCAT(@sql, " AND (@vendor is null OR v.vendor like ?)"); 
                 SET @sql = CONCAT(@sql, " AND (@model is null OR v.model like ?)"); 
                 SET @sql = CONCAT(@sql, " AND (@displacement is null OR v.displacement like ?)"); 
                 SET @sql = CONCAT(@sql, " AND (@license_plate is null OR v.license_plate like ?)");            
            ELSE
                 SET @sql = CONCAT(@sql, " AND (@vendor is null OR v.vendor = ?)");   
                 SET @sql = CONCAT(@sql, " AND (@model is null OR v.model = ?)");   
                 SET @sql = CONCAT(@sql, " AND (@displacement is null OR v.displacement = ?)");   
                 SET @sql = CONCAT(@sql, " AND (@license_plate is null OR v.license_plate = ?)");                           
            END IF;

            SET @sql = CONCAT(@sql, " AND (@start_date is null OR v.product_date >= ?)");
            SET @sql = CONCAT(@sql, " AND (@end_date is null OR v.product_date <= ?)");


            SET @sql = CONCAT(@sql, " ORDER BY v.",@orderByProperty," ",@ascOrDesc);
             IF @firstRcord != -1 AND @pageSize != -1 THEN            
                SET @sql = CONCAT(@sql, " limit ", @firstRcord, "," ,@pageSize);           
            END IF; 
            PREPARE sqlstr from @sql;
            EXECUTE sqlstr using @t_flag,@vin,@tboxsn,@vendor,@model,@displacement,@license_plate,@start_date,@end_date;
   		    DROP TABLE the_vehicles;
END//

delimiter ;