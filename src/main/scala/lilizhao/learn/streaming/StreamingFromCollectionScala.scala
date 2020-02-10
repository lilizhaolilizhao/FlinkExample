package lilizhao.learn.streaming

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

object StreamingFromCollectionScala {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    import org.apache.flink.streaming.api.scala._

    val data = List(10, 15, 20)
    val text = env.fromCollection(data)

    val num = text.map(_ + 1)
    num.print().setParallelism(1)

    env.execute("StreamingFromCollectionScala")
  }
}
