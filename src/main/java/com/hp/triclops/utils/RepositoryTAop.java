package com.hp.triclops.utils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;

/**
 * Created by samsung on 2015/11/17.
 */
@Aspect
@Configuration
public class RepositoryTAop {


    @Pointcut("execution(* com.hp.*.repository.*.*(..))")
    public void excudeController(){}

    /**
     * 调用repository里面的类之前执行
     * @param joinPoint
     * 该方法要将repository里面类的调用者，URL全路径，方法名，参数列表，调用时间写入到log日志中
     */
    @Before(value = "excudeController()")
    public void BeforeMethodRun(JoinPoint joinPoint){
        System.out.println("----------------------");
        /*PerStatisticsUtil perStatisticsUtil = new PerStatisticsUtil();
        perStatisticsUtil.setAppCtxAndInit(appContext);
        //开始对API调用次数计数
        perStatisticsUtil.statisticsTranCount();
        System.out.println("事务总数=====" + perStatisticsUtil.getStatisticsTranCount());*/
    }
}
