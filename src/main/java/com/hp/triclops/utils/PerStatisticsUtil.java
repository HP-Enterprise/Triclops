package com.hp.triclops.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 性能计数工具类
 * Created by wh on 2015/11/17.
 */
public  class PerStatisticsUtil {


    private ApplicationContext appContext;
    private String[] preApiStr = {"apiCount:","code:"}; //api计数
    private String[] preTranStr = {"transactionCount:","code:"}; //事物计数
    private String apiSessionKey = "";   //api计数key
    private String tranSessionKey = ""; //事物计数key


    private StringRedisTemplate stringRedisTemplate;

    /**
     * 手动注入Repository
     * @param appContext ApplicationContext
     */
    public void setAppCtxAndInit(ApplicationContext appContext){
        this.appContext = appContext;
        this.stringRedisTemplate = this.appContext.getBean(StringRedisTemplate.class);

    }

    /**
     * API请求数计数
     */
    public void  statisticsApiCount(){

        apiSessionKey = setPreApiStrOfKey(0, "1");
        if(!this.stringRedisTemplate.hasKey(apiSessionKey)){
            this.stringRedisTemplate.opsForValue().set(apiSessionKey, "1");
        }
        String count = this.stringRedisTemplate.opsForValue().get(apiSessionKey);
        int apiCount = Integer.parseInt(count);
        apiCount += 1;
        this.stringRedisTemplate.opsForValue().set(apiSessionKey, apiCount+"");
    }


    /**
     * 获取API请求总数
     * @return API请求总数
     */
    public Integer getStatisticsApiCount(){
        apiSessionKey = setPreApiStrOfKey(0, "1");
        String count = this.stringRedisTemplate.opsForValue().get(apiSessionKey);
        int apiCount = Integer.parseInt(count);

        return apiCount;
    }


    /**
     *删除API计数
     */
    public void emptyApiCount(){
        apiSessionKey = setPreApiStrOfKey(0, "1");
        if(this.stringRedisTemplate.hasKey(apiSessionKey)){
            this.stringRedisTemplate.opsForValue().set(apiSessionKey, "0");
        }
    }



    /**
     * 数据库事物计数
     */
    public synchronized void  statisticsTranCount(){

        tranSessionKey = setPreTranStrOfKey(0, "1");
        if(!this.stringRedisTemplate.hasKey(tranSessionKey)){
            this.stringRedisTemplate.opsForValue().set(tranSessionKey, "1");
        }
        String count = this.stringRedisTemplate.opsForValue().get(tranSessionKey);
        int apiCount = Integer.parseInt(count);
        apiCount += 1;
        this.stringRedisTemplate.opsForValue().set(tranSessionKey, apiCount+"");
    }


    /**
     * 获取数据库事物总数
     * @return 数据库事物总数
     */
    public Integer getStatisticsTranCount(){
        tranSessionKey = setPreTranStrOfKey(0, "1");
        String count = this.stringRedisTemplate.opsForValue().get(tranSessionKey);
        int apiCount = Integer.parseInt(count);

        return apiCount;
    }


    /**
     *删除数据库事物总数
     */
    public void emptyTranCount(){
        tranSessionKey = setPreTranStrOfKey(0, "1");
        if(this.stringRedisTemplate.hasKey(tranSessionKey)){
            this.stringRedisTemplate.opsForValue().set(tranSessionKey, "0");
        }
    }


    /**
     * 获取redis记录总数
     * @return  redis记录总数
     */
    public Integer getRedisCount(){

        Integer redisCount = 1000;
        return redisCount;
    }



    public String setPreApiStrOfKey(int index,String sessionId){
        return this.preApiStr[index] + sessionId;
    }

    public String setPreTranStrOfKey(int index,String sessionId){
        return this.preTranStr[index] + sessionId;
    }

}
