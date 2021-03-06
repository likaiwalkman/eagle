/*
 * Copyright 2017 eagle.jfaster.org.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package eagle.jfaster.org.task;

import eagle.jfaster.org.client.NettyResponseFuture;
import eagle.jfaster.org.logging.InternalLogger;
import eagle.jfaster.org.logging.InternalLoggerFactory;
import eagle.jfaster.org.rpc.ResponseFuture;
import eagle.jfaster.org.rpc.support.TraceContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * Created by fangyanpeng on 2017/8/22.
 */
@RequiredArgsConstructor
public class AsyncCallbackTask implements Runnable{

    private final static InternalLogger logger = InternalLoggerFactory.getInstance(AsyncCallbackTask.class);

    @Getter
    private final ResponseFuture responseFuture;

    @Override
    public void run() {
        try {
            Map<String,String> attachments = ((NettyResponseFuture)responseFuture).getAttachments();
            if(attachments != null){
                TraceContext.setTraceId(attachments.get(TraceContext.TRACE_KEY));
            }
            responseFuture.executeCallback();
        } catch (Throwable e) {
            logger.info("execute callback in executor exception, and callback throw", e);
        }finally {
            TraceContext.clear();
        }
    }
}