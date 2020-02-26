package com.yxf.setup.enums;

/**
 * 设备类型
 */
public enum DeviceType {
    /**
     * 儿童医院
     */
    CHILDREN_HOSPITAL("儿童医院"),
    /**
     * 奥朗底座
     */
    AO_LANG_PEDESTAL("医院版底座"),
    /**
     * 奥朗中继器
     */
    AO_LANG_REPEATER("医院版中继器"),
    /**
     * 配置静态IP
     */
    CONFIG_IP("配置静态IP");

    private String type;

    DeviceType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
