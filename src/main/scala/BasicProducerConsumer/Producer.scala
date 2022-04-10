package BasicProducerConsumer
import java.util.Properties
//import org.apache.kafka.clients.producer._
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
object Producer extends App{



  val props = new Properties()
  props.put("bootstrap.servers","localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer")


  val producer = new KafkaProducer[String,String](props)
  val TOPIC = "end2end"

  for(i<-1 to 10){
    val record = new ProducerRecord[String,String](TOPIC,s"hello $i")
    producer.send(record)
  }
  println("complete")
  producer.close()
}
