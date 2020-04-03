package lilizhao.learn.streaming.streamAPI;

import org.apache.flink.streaming.api.collector.selector.OutputSelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SplitStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import xuwei.tech.streaming.custormSource.MyNoParalleSource;

import java.util.ArrayList;

public class StreamingDemoSplit {
    public static void main(String[] args) throws Exception {
        //获取Flink的运行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //获取数据源
        DataStreamSource<Long> text = env.addSource(new MyNoParalleSource()).setParallelism(4);//注意：针对此source，并行度只能设置为1

        SplitStream<Long> splitStream = text.split((OutputSelector<Long>) value -> {
            ArrayList<String> outPut = new ArrayList<>();

            if (value % 2 == 0) {
                outPut.add("even");//偶数
            } else {
                outPut.add("odd");//奇数
            }
            return outPut;
        });

        //选择一个或者多个切分后的流
        DataStream<Long> evenStream = splitStream.select("even");
        DataStream<Long> oddStream = splitStream.select("odd");

        DataStream<Long> moreStream = splitStream.select("odd","even");

        moreStream.shuffle();
        moreStream.rebalance();

        //打印结果
//        evenStream.print().setParallelism(1);
        moreStream.print().setParallelism(1);

        String jobName = StreamingDemoSplit.class.getSimpleName();
        env.execute(jobName);
    }
}
