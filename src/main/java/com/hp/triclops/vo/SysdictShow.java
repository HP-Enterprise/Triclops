package com.hp.triclops.vo;

import com.hp.triclops.entity.Sysdict;


/**
 * Created by Administrator on 2015/9/10.
 */

/**
 * <table summary="SysdictShow" class="typeSummary">
 *     <thead>
 *         <tr>
 *             <th>字段</th>
 *             <th>数据类型</th>
 *             <th>说明</th>
 *         </tr>
 *     </thead>
 *     <tbody>
 *         <tr>
 *             <td>dictid</td>
 *             <td>int</td>
 *             <td>字典表ID</td>
 *         </tr>
 *         <tr>
 *             <td>type</td>
 *             <td>int</td>
 *             <td>字典类型</td>
 *         </tr>
 *         <tr>
 *             <td>dictname</td>
 *             <td>String</td>
 *             <td>字典名称</td>
 *         </tr>
 *         <tr>
 *             <td>remark</td>
 *             <td>String</td>
 *             <td>备注</td>
 *         </tr>
 *         <tr>
 *             <td></td>
 *             <td></td>
 *             <td></td>
 *         </tr>
 *     </tbody>
 * </table>
 */
public class SysdictShow {
    private int dictid;
    private int type;
    private String dictname;
    private String remark;

    public SysdictShow() {
    }
    public SysdictShow(Sysdict sysdict){
        this.dictid=sysdict.getDictid();
        this.type=sysdict.getType();
        this.dictname=sysdict.getDictname();
        this.remark=sysdict.getRemark();
    }

    public int getDictid() {
        return dictid;
    }

    public void setDictid(int dictid) {
        this.dictid = dictid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDictname() {
        return dictname;
    }

    public void setDictname(String dictname) {
        this.dictname = dictname;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
