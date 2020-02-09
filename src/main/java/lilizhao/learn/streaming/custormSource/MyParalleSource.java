package lilizhao.learn.streaming.custormSource;

import org.apache.flink.streaming.api.functions.source.ParallelSourceFunction;

public class MyParalleSource implements ParallelSourceFunction<Long> {
    private long count = 1L;

    private boolean isRunning = true;

    @Override
    public void run(SourceContext<Long> ctx) throws Exception {
        while(isRunning){
            ctx.collect(count);
            count++;
            //每秒产生一条数据
            Thread.sleep(1000);
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}
