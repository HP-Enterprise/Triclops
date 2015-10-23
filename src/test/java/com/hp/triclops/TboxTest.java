package com.hp.triclops;

import com.hp.triclops.entity.TBox;
import com.hp.triclops.manager.TBoxMgr;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by liz on 2015/10/23.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class TBoxTest {

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

    }

    /**
     * 删除TBox
     */
    @Test
    public void deleteTBox(){

    }

    /**
     * 查找TBox
     */
    @Test
    public void findTBox(){

    }
}
