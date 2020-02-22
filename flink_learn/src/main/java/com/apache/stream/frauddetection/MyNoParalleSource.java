package com.apache.stream.frauddetection;

import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现一个没有并行度的数据源
 */
public class MyNoParalleSource implements SourceFunction<Transaction> {

    private int count = 1;

    private boolean isRunning = true;

    private static List<Transaction> transList = new ArrayList<>();

    static {
        transList.add(new Transaction("Txn_1", 13.01d));
        transList.add(new Transaction("Txn_1", 25.01d));
        transList.add(new Transaction("Txn_1", 0.09d));
        transList.add(new Transaction("Txn_1", 510d));
        transList.add(new Transaction("Txn_1", 102.62d));
        transList.add(new Transaction("Txn_1", 91.50d));
        transList.add(new Transaction("Txn_1", 0.02d));
        transList.add(new Transaction("Txn_1", 30.01d));
        transList.add(new Transaction("Txn_1", 730.01d));
        transList.add(new Transaction("Txn_1", 31.01d));
    }

    @Override
    public void run(SourceContext<Transaction> ctx) throws Exception {
        while(isRunning) {
            if (count < transList.size()) {
                ctx.collect(transList.get(count));
                count++;
            }

            //每秒产生一条数据
            Thread.sleep(1000);
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }
}