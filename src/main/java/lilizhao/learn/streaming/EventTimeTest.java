//package lilizhao.learn.streaming;
//
//import org.apache.flink.api.common.io.FilePathFilter;
//import org.apache.flink.streaming.api.TimeCharacteristic;
//import org.apache.flink.streaming.api.functions.source.FileProcessingMode;
//import org.apache.flink.streaming.api.scala.DataStream;
//import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment;
//import org.apache.flink.streaming.api.windowing.time.Time;
//
//public class EventTimeTest {
//    public static void main(String[] args) {
//        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
//
//        DataStream<MyEvent> stream = env.readFile(
//                myFormat, myFilePath, FileProcessingMode.PROCESS_CONTINUOUSLY, 100,
//                FilePathFilter.createDefaultFilter(), typeInfo);
//
//        DataStream<MyEvent> withTimestampAndWatermarks = stream
//                .filter(myEvent -> event.serverity() == WARNING)
//                .assignTimestampsAndWatermarks(new MyTimestampsAndWatermarks());
//
//        withTimestampAndWatermarks
//                .keyBy((event) -> evnet.getGroup())
//                .timeWindow(Time.seconds(10))
//                .reduce((a,b) -> (a.add(b)))
//                .addSink(...);
//    }
//}
