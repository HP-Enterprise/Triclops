package com.hp.triclops.utils;

import java.util.List;

/**
 * Page2带有泛型
 */
public class Page2<T> {
    private Integer currentPage;
    private Integer pageSize;
    private Long recordCount;
    private List<T> items;

    public Page2() {
    }

    public Page2(Integer currentPage, Integer pageSize, Long recordCount, List<T> items) {
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
        this.recordCount = recordCount;
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


    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
