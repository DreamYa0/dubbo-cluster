package com.ntocc.dubbo.cluster;

import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.cluster.Cluster;
import com.alibaba.dubbo.rpc.cluster.Directory;

/**
 * @author dreamyao
 * @title 针对网货特殊用户或特殊接口继续动态隔离集群
 * @date 2020/11/18 11:07 PM
 * @since 1.0.0
 */
public class DynamicRoutingCluster implements Cluster {

    public final static String NAME = "routing";

    @Override
    public <T> Invoker<T> join(Directory<T> directory) throws RpcException {
        return new DynamicRoutingClusterInvoker<>(directory);
    }
}
