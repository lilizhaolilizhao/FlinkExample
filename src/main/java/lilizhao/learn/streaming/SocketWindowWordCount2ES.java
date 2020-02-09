package lilizhao.learn.streaming;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.apache.flink.streaming.connectors.elasticsearch5.ElasticsearchSink;
import org.apache.flink.util.Collector;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 滑动窗口测试 & 写入ES
 */
public class SocketWindowWordCount2ES {
    public static void main(String[] args) throws Exception {
        //获取需要的端口号
        int port;
        try {
            ParameterTool parameterTool = ParameterTool.fromArgs(args);
            port = parameterTool.getInt("port");
        } catch (Exception e) {
            System.err.println("No port set. use default port 9000--java");
            port = 9000;
        }

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        String delimiter = "\n";
        //连接socket获取输入的数据
        DataStreamSource<String> text = env.socketTextStream("127.0.0.1", port, delimiter);

        DataStream<WordWithCount> sum = text.flatMap(new FlatMapFunction<String, WordWithCount>() {
            @Override
            public void flatMap(String value, Collector<WordWithCount> collector) throws Exception {
                String[] splits = value.split("\\s");
                for (String word : splits) {
                    collector.collect(new WordWithCount(word, 1L));
                }
            }
        }).keyBy("word")
                .timeWindow(Time.seconds(2), Time.seconds(1))
                .sum("count");

        sum.print().setParallelism(1);

        Map<String, String> config = new HashMap<>();
        config.put("cluster.name", "elasticsearch");
        // This instructs the sink to emit after every element, otherwise they would be buffered
        config.put("bulk.flush.max.actions", "1");

        List<InetSocketAddress> transportAddresses = new ArrayList<>();
        transportAddresses.add(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 9300));

        sum.addSink(new ElasticsearchSink<>(config, transportAddresses, new ElasticsearchSinkFunction<WordWithCount>() {
            public IndexRequest createIndexRequest(WordWithCount wordWithCount) {
                Map<String, Object> json = new HashMap<>();
                json.put("word", wordWithCount.word);
                json.put("count", wordWithCount.count);

                return Requests.indexRequest()
                        .index("llz-index")
                        .type("llz-type")
                        .source(json);
            }

            @Override
            public void process(WordWithCount wordWithCount, RuntimeContext ctx, RequestIndexer indexer) {
                indexer.add(createIndexRequest(wordWithCount));
            }
        }));

        env.execute("Socket window count");
    }

    public static class WordWithCount {
        public String word;
        public long count;

        public WordWithCount() {
        }

        public WordWithCount(String word, long count) {
            this.word = word;
            this.count = count;
        }

        @Override
        public String toString() {
            return "WordWithCount{" +
                    "word='" + word + '\'' +
                    ", count=" + count +
                    '}';
        }
    }
}
