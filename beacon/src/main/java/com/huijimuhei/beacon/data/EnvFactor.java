package com.huijimuhei.beacon.data;

import java.io.Serializable;

/**
 * 环境因子
 */
public class EnvFactor implements Serializable {

    private static final long serialVersionUID = 1L;

    /*房间id*/
    private Integer roomId;

    /* 高度补偿值*/
    private Double height;

    /*环境衰减因子*/
    private Double n;

    /*一米处接收到的rssi值*/
    private Double p0;

    public EnvFactor(Integer roomId, Double height, Double n, Double p0) {
        super();
        this.roomId = roomId;
        this.height = height;
        this.n = n;
        this.p0 = p0;
    }

    public EnvFactor() {
        super();
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getN() {
        return n;
    }

    public void setN(Double n) {
        this.n = n;
    }

    public Double getP0() {
        return p0;
    }

    public void setP0(Double p0) {
        this.p0 = p0;
    }

}
