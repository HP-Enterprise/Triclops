package com.hp.triclops;

import com.hp.triclops.entity.TBox;
import com.hp.triclops.manager.TBoxMgr;
import com.hp.triclops.utils.Page2;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by liz on 2015/10/23.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class TboxTest {

    @Autowired
    ApplicationContext appContext;


    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * 增加TBox
     */
    @Test
    public void addTBox(){

        TBoxMgr tBoxMgr = new TBoxMgr();
        tBoxMgr.setAppCtxAndInit(this.appContext);
        TBox tbox = new TBox();
        tbox.setT_sn("FG099213");
        tbox.setVin("VIN00009");
        tBoxMgr.addTBox(tbox);
    }

    /**
     * 修改TBox
     */
    @Test
    public void updateTBox(){
        TBoxMgr tBoxMgr = new TBoxMgr();
        tBoxMgr.setAppCtxAndInit(this.appContext);

        //先新增一条tbox信息
        TBox insertTbox = new TBox();
        insertTbox.setT_sn("FG099212");
        insertTbox.setVin("VIN000012");
        tBoxMgr.addTBox(insertTbox);


        //修改tbox信息
        TBox updateTbox = tBoxMgr.fingTboxByVin("VIN000012");
        updateTbox.setImei("911192");
        updateTbox.setIs_activated(0);
        updateTbox.setActivation_time(new Date());
        updateTbox.setMobile("13932321211");
        tBoxMgr.updateTBox(updateTbox);


    }

    /**
     * 删除TBox
     */
    @Test
    public void deleteTBox(){
        TBoxMgr tBoxMgr = new TBoxMgr();
        tBoxMgr.setAppCtxAndInit(this.appContext);

       //先新增一条tbox信息
        TBox insertTbox = new TBox();
        insertTbox.setT_sn("FG099111");
        insertTbox.setVin("VIN00111");
        tBoxMgr.addTBox(insertTbox);

        //删除tbox信息
        TBox deleteTbox = tBoxMgr.fingTboxByVin("VIN00111");
        tBoxMgr.deleteTbox(deleteTbox);

    }

    /**
     * 查找TBox
     */
    @Test
    public void findTBox(){
        TBoxMgr tBoxMgr = new TBoxMgr();
        tBoxMgr.setAppCtxAndInit(this.appContext);
        Page2<TBox> page2 = tBoxMgr.findTboxByKeys(0, null,0, null, 0, null, null, 0, 1, 0, 0);
    }
}
