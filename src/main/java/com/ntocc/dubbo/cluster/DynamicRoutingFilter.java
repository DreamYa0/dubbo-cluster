package com.ntocc.dubbo.cluster;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;

/**
 * @author dreamyao
 * @title
 * @date 2019-08-24 14:14
 * @since 1.0.0
 */
@Activate(group = {com.alibaba.dubbo.common.Constants.PROVIDER, com.alibaba.dubbo.common.Constants.CONSUMER})
public class DynamicRoutingFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        RpcContext context = RpcContext.getContext();
        boolean consumerSide = context.isConsumerSide();

        if (consumerSide) {
            String owner = DynamicRoutingTheadLocal.get();
            if (StringUtils.isBlank(owner)) {

                owner = RpcContext.getContext().getAttachment(Constants.OWNER);
                if (StringUtils.isBlank(owner)) {

                    owner = invocation.getAttachment(Constants.OWNER);
                }

                if(StringUtils.isBlank(owner)) {
                    // 如果上游没有传这个标记，使用当前consumer 自己的owner
                    owner = context.getUrl().getParameter(Constants.OWNER);
                }
                DynamicRoutingTheadLocal.set(owner);
            }

            invocation.getAttachments().put(Constants.OWNER, owner);

            return invoker.invoke(invocation);

        } else {

            String owner = invocation.getAttachment(Constants.OWNER);

            DynamicRoutingTheadLocal.set(owner);

            return invoker.invoke(invocation);
        }
    }
}
