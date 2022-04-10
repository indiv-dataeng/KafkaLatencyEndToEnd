package BasicProducerConsumer

import BasicProducerConsumer.Producer.props
import org.apache.kafka.clients.consumer.KafkaConsumer

import java.time.Duration
import scala.collection.JavaConverters.*
import java.util.Collections
import java.util.Properties

object Consumer extends App{
  val TOPIC="end2end"
  val props = new Properties()
  props.put("bootstrap.servers","localhost:9092")
  props.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer")
  props.put("group.id","end1")

  val consumer = new KafkaConsumer[String,String](props)
  consumer.subscribe(Collections.singletonList(TOPIC))
//  while(true) {
  val records = consumer.poll(Duration.ofMillis(1000))
  for (record <- records.asScala) {
    println(record.value())
  }
//  }
}




