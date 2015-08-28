package com.hp.triclops.utils;


import java.util.List;
/**
 * Page类  封装分页数据
 *
 * @author 柳明
 *
 */
public class Page {
    private Integer currentPage;
    private Integer pageSize;
    private Long recordCount;
    private List items;

    public Page(Integer currentPage, Integer pageSize, Long recordCount, List items) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.recordCount = recordCount;
        this.items = items;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(Long recordCount) {
        recordCount = recordCount;
    }

    public Integer getPageCount() {
        Long modResult=this.recordCount%this.pageSize;
        Long result=this.recordCount/this.pageSize;
        if(modResult==0){
            return result.intValue();
        }
        else{
            return result.intValue()+1;
        }
    }


    public List getItems() {
        return items;
    }

    public void setItems(List items) {
        this.items = items;
    }
}
