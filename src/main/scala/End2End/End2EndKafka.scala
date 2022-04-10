package End2End

import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}

import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.Properties
import java.util.Arrays
import scala.Console.in
import java.util.Collections
import scala.language.postfixOps



object End2EndKafka{
  def main(args: Array[String]): Unit = {

    var totalTime = 0.0
    val numOfMessage = 30
    val latencies = new Array[Double](numOfMessage)
    val timeout = 3000
    val TOPIC = "end2end"

    val producerProperties: Properties = new Properties()
    producerProperties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092")
    producerProperties.put(ProducerConfig.LINGER_MS_CONFIG,"0")
    producerProperties.put(ProducerConfig.ACKS_CONFIG,"all")
    producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer")
    producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringSerializer")
    producerProperties.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, Long.MaxValue.toString)

    val consumerProperties: Properties = new Properties()
    consumerProperties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092")
    consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG,"end2")
    consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,"false")
    consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
    consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringDeserializer")
    consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,"org.apache.kafka.common.serialization.StringDeserializer")
    consumerProperties.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, "0")


    val producer = new KafkaProducer[String,String](producerProperties)
    val consumer = new KafkaConsumer[String,String](consumerProperties)
    consumer.subscribe(Collections.singletonList(TOPIC))


    def finalise(): Unit = {
      consumer.commitSync()
      producer.close()
      consumer.close()
    }

    for(i <- 0 until numOfMessage){
      val msg = s"Message -> $i"
      val begin = System.nanoTime()
      // Send 1 record
      producer.send(new ProducerRecord[String,String](TOPIC,msg)).get()
      // Pull 1 time
      val recordIter = consumer.poll(Duration.ofMillis(timeout)).iterator
      // Calculate elapsed
      val elapsed = System.nanoTime()-begin


      if (!recordIter.hasNext) {
        finalise()
        throw new RuntimeException(s"poll() timed out before finding a result (timeout:[$timeout] ms)")
      }

      val read = recordIter.next().value()
      if(!read.equals(msg)){
        finalise()
        throw new RuntimeException(s"The message read [$read] did not match the message [$msg]")
      }

      if (recordIter.hasNext) {
        while(recordIter.hasNext){
          println(s"Data $i = ${recordIter.next.value()}")
        }
        finalise()
        throw new RuntimeException(s"Only one result was expected during this test. We found > 1 msg")
      }

      // Report progress
      if (i % 10 == 0)
        println(i.toString + "\t" + elapsed / 1000.0 / 1000.0)
      if (i != 0)
        totalTime += elapsed

      latencies(i) = elapsed / 1000.0 / 1000.0
    }
    // Results
    for(i <- latencies.indices)
      println(f"Latency of msg ${i+1}%s = ${latencies(i)}%.4f ms")

    println("Avg latency: %.4f ms\n".format(totalTime / (numOfMessage-1) / 1000.0 / 1000.0))
    scala.util.Sorting.quickSort(latencies)

    val p50 = latencies((latencies.length * 0.5).toInt)
    val p99 = latencies((latencies.length * 0.99).toInt)
    val p999 = latencies((latencies.length * 0.999).toInt)
    println("Percentiles: 50th = %.4f, 99th = %.4f, 99.9th = %.4f".format(p50, p99, p999))

    finalise()
  }
}
