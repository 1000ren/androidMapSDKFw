package com.ctfo.mvapi.entities;

import java.io.Serializable;

/**
 * @author fangwei
 * 
 * PoiData 兴趣点对象定义
 * 
 */
public class PoiItem implements Serializable
{
    private static final long serialVersionUID = 100L;
    
     /**
     * // DEFAULTPOIKIND
     */
    public static final int DEFAULTPOIKIND = 0xBF00;
    /**
     * // 名字
     */
    public String szName;
    /**
     * // 行政区划名称
     */
    public String szAdminName;
    /**
     * // 电话
     */
    public String szPhone;
    /**
     * // 地址
     */
    public String szAddress;
    /**
     * // 行政区划码
     */
    public int dwAdminCode;
    /**
     * // 图幅ID
     */
    public int dwMapId;
    /**
     * // 类别
     */
    public int wKind = DEFAULTPOIKIND;
    /**
     * // 坐标
     */
    public int dwX;
    public int dwY;
    /**
     * // 邮编
     */
    public int dwZipCode;
    /**
     * // 距离
     */
    public double dDistance;



    public PoiItem(int dwX, int dwY, int dwAdminCode, int dwMapId, int wKind,
            String szName, String szAdminName, int dwZipCode, String szAddress,
            String szPhone, double dDistance) {
        super();
        this.dwX = dwX;
        this.dwY = dwY;
        this.dwAdminCode = dwAdminCode;
        this.dwMapId = dwMapId;
        this.wKind = wKind;
        this.szName = szName;
        this.szAdminName = szAdminName;
        this.dwZipCode = dwZipCode;
        this.szAddress = szAddress;
        this.szPhone = szPhone;
        this.dDistance = dDistance;
    }

    /**
     * PoiData结构赋值拷贝
     * 
     * @param from
     *            传进的PoiData结构类型数据
     */
    public void copy(PoiItem from) {
        if (null == from) {
            return;
        }
        this.dwX = from.dwX;
        this.dwY = from.dwY;
        this.dwAdminCode = from.dwAdminCode;
        this.dwMapId = from.dwMapId;
        this.wKind = from.wKind;
        this.szName = from.szName;
        this.szAdminName = from.szAdminName;
        this.dwZipCode = from.dwZipCode;
        this.szAddress = from.szAddress;
        this.szPhone = from.szPhone;
        this.dDistance = from.dDistance;
    }


}
