package com.ntocc.dubbo.cluster;

/**
 * @author dreamyao
 * @title k8s的namespace名称后缀
 * @date 2020/11/18 11:14 PM
 * @since 1.0.0
 */
public enum OwnerEnum {

    /**
     * 所有
     */
    ALL("All"),

    /**
     * 默认命名空间 k8s ntocc 命名空间 此namespace下的所有应用dubbo所有者配置 如 -Ddubbo.application.owner=ntocc
     */
    NTOCC("ntocc"),

    /**
     * k8s ntocc-job 命名空间 此namespace下的所有应用dubbo所有者配置 如 -Ddubbo.application.owner=ntocc-job
     */
    NTOCC_JOB("ntocc-job"),

    /**
     * k8s ntocc-isolation 命名空间 此namespace下的所有应用dubbo所有者配置 如 -Ddubbo.application.owner=ntocc-isolation
     */
    NTOCC_ISOLATION("ntocc-isolation"),
    ;

    private String name;

    OwnerEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
