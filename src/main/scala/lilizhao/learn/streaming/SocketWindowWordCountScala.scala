package lilizhao.learn.streaming

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.time.Time


object SocketWindowWordCountScala {
  def main(args: Array[String]): Unit = {

    //获取socket端口号
    val port: Int = try {
      ParameterTool.fromArgs(args).getInt("port")
    } catch {
      case e: Exception => {
        System.err.println("No port set. use default port 9000--scala")
      }
        9000
    }

    //获取运行环境
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment

    //链接socket获取输入数据
    val text = env.socketTextStream("127.0.0.1", port, '\n');

    import org.apache.flink.streaming.api.scala._

    val windowCounts = text.flatMap(line => line.split("\\s"))//打平，把每一行单词都切开
      .map(w => WordWithCount(w,1))//把单词转成word , 1这种形式
      .keyBy("word")//分组
      .timeWindow(Time.seconds(2),Time.seconds(1))//指定窗口大小，指定间隔时间
      .sum("count");

    windowCounts.print().setParallelism(1)

    env.execute("Socket window count")
  }

  case class WordWithCount(word: String, count: Long)
}
