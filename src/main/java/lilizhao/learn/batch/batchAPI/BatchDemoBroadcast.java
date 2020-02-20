package lilizhao.learn.batch.batchAPI;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.MapOperator;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BatchDemoBroadcast {
    public static void main(String[] args) throws Exception {
        //获取运行环境
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        //1：准备需要广播的数据
        ArrayList<Tuple2<String, Integer>> broadData = new ArrayList<>();
        broadData.add(new Tuple2<>("zs",18));
        broadData.add(new Tuple2<>("ls",20));
        broadData.add(new Tuple2<>("ww",17));
        DataSet<Tuple2<String, Integer>> tupleData = env.fromCollection(broadData);

        //1.1:处理需要广播的数据,把数据集转换成map类型，map中的key就是用户姓名，value就是用户年龄
        DataSet<HashMap<String, Integer>> toBroadcast = tupleData.map(new MapFunction<Tuple2<String, Integer>, HashMap<String, Integer>>() {
            @Override
            public HashMap<String, Integer> map(Tuple2<String, Integer> value) throws Exception {
                HashMap<String, Integer> res = new HashMap<>();
                res.put(value.f0, value.f1);
                return res;
            }
        });

        //源数据
        DataSource<String> data = env.fromElements("zs", "ls", "ww");

        MapOperator<String, String> result = data.map(new RichMapFunction<String, String>() {
            List<HashMap<String, Integer>> broadCastMap = new ArrayList<HashMap<String, Integer>>();
            HashMap<String, Integer> allMap = new HashMap<String, Integer>();

            @Override
            public String map(String value) throws Exception {
                Integer age = allMap.get(value);
                return value + "," + age;
            }

            @Override
            public void open(Configuration parameters) throws Exception {
                //3:获取广播数据
                this.broadCastMap = getRuntimeContext().getBroadcastVariable("broadCastMapName");
                for (HashMap map : broadCastMap) {
                    allMap.putAll(map);
                }

                super.open(parameters);
            }
        }).withBroadcastSet(toBroadcast, "broadCastMapName");//2：执行广播数据的操作

        result.print();
    }
}






















