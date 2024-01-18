package com.ntocc.dubbo.cluster;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.cluster.Directory;
import com.alibaba.dubbo.rpc.cluster.LoadBalance;
import com.alibaba.dubbo.rpc.cluster.support.FailoverClusterInvoker;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author dreamyao
 * @title
 * @date 2020/11/18 11:09 PM
 * @since 1.0.0
 */
public class DynamicRoutingClusterInvoker<T> extends FailoverClusterInvoker<T> {

    // dubbo provider 服务注册地址属性
    private static final String PROVIDER_URL_FIELD = "providerUrl";
    private static final Logger logger = LoggerFactory.getLogger(DynamicRoutingClusterInvoker.class);

    public DynamicRoutingClusterInvoker(Directory<T> directory) {
        super(directory);
    }

    @Override
    public Result doInvoke(Invocation invocation, List<Invoker<T>> invokers, LoadBalance loadbalance)
            throws RpcException {

        // 从本地ThreadLocal中获取
        String owner = DynamicRoutingTheadLocal.get();
        if ((owner == null || "".equals(owner))) {
            // 设置默认的命名空间
            owner = OwnerEnum.NTOCC.getName();
        }

        // 如果默认owner是All标记，则不走下面动态路由规则
        if (OwnerEnum.ALL.getName().equalsIgnoreCase(owner)) {
            return super.doInvoke(invocation, invokers, loadbalance);
        }

        // 按照所有者进行分组
        Map<String, List<Invoker<T>>> owner2Invokers = invokers.stream().collect(
                Collectors.groupingBy(
                        invoker -> {
                            try {
                                Field providerUrl = FieldUtils.getField(invoker.getClass(),
                                        PROVIDER_URL_FIELD, true);
                                URL url = (URL) FieldUtils.readField(providerUrl, invoker,
                                        true);
                                return url.getParameter(Constants.OWNER);
                            } catch (Exception e) {
                                logger.error("read dubbo provider owner failed use ntocc owner", e);
                            }

                            return OwnerEnum.NTOCC.getName();
                        }
                )
        );

        List<Invoker<T>> ownerInvokers = owner2Invokers.get(owner);

        // 优先选用owner对应集群的服务提供者
        if (CollectionUtils.isNotEmpty(ownerInvokers)) {
            return super.doInvoke(invocation, ownerInvokers, loadbalance);
        }

        // 第一级降级处理：如果没有对应owner集群的服务提供者，则选择网货标准的ntocc集群的服务提供者
        List<Invoker<T>> ntoccInvokers = owner2Invokers.get(OwnerEnum.NTOCC.getName());
        if (CollectionUtils.isNotEmpty(ntoccInvokers)) {
            return super.doInvoke(invocation, ntoccInvokers, loadbalance);
        }

        // 第二降级处理：如果对应owner集群和标准ntocc集群都没有服务提供者，则选择用剩下集群的服务提供者
        return super.doInvoke(invocation, invokers, loadbalance);
    }
}
