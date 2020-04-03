package lilizhao.learn.batch

import org.apache.flink.api.scala.ExecutionEnvironment

object BatchWordCountScala {
  def main(args: Array[String]): Unit = {
    val inputPath = "/Users/lilizhao/my_source/LearningDemo/flink_learn/src/main/scala/lilizhao/learn/batch/BatchWordCountScala.scala"
    val outPath = "/Users/lilizhao/my_source/LearningDemo/target/out.csv"

    val env = ExecutionEnvironment.getExecutionEnvironment
    val text = env.readTextFile(inputPath, "utf-8")

    import org.apache.flink.streaming.api.scala._

    val counts = text.flatMap(_.toLowerCase.split("\\W+"))
      .filter(_.nonEmpty)
      .map((_, 1))
      .groupBy(0)
      .sum(1)

    counts.writeAsCsv(outPath, "\n", " ").setParallelism(1)
    env.execute("batch word count")
  }
}
