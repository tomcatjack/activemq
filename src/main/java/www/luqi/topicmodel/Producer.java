package www.luqi.topicmodel;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * @author LUCCI
 * @date 2018/8/16 16:02
 * @Description: 消息生产者  先消费者 在去开生产者
 * @Modify:
 * Topic方式每个消息都可以有多个订阅者（消费者），只要消费者订阅了这个主题(Topic),就可以获得这个消息。
 * 发布者和订阅者之间有时间上的依赖性。针对某个主题（Topic）的订阅者，它必须创建一个订阅者之后，才能消费发布者的消息，
 * 而且为了消费消息，订阅者必须保持运行的状态(也就是说在必须先启动消费者那方)。
 * 所以在代码中得先启动消费者的服务，再发送消息。
 */
@Slf4j
public class Producer {

    private static final String url = "tcp://192.168.3.72:61616";

    private static final String topicName = "FirstTopic3";

    public static void main(String[] args) throws JMSException {
        Connection connection = null;
        try {
            //1.创建ConnectionFactory
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);

            //2.创建connection
            connection = connectionFactory.createConnection();

            //3.启动连接
            connection.start();

            /**
             * 4.创建session
             * 第一个参数:是否支持事务，如果为true，则会忽略第二个参数，被jms服务器设置为SESSION_TRANSACTED
             * 第二个参数:
             */
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            //5.创建一个到达的目的地，其实想一下就知道了，activemq不可能同时只能跑一个队列吧，这里就是连接了一个名为"text-msg"的队列，这个会话将会到这个队列，当然，如果这个队列不存在，将会被创建
            Destination destination = session.createTopic(topicName);

            //6.从session中，获取一个消息生产者
            MessageProducer producer = session.createProducer(destination);

            //producer.setDeliveryMode(DeliveryMode.PERSISTENT);

            //8.创建一条消息，当然，消息的类型有很多，如文字，字节，对象等,可以通过session.create..方法来创建出来
            for (int i = 0; i < 10; i++) {
                TextMessage textMsg = session.createTextMessage("topic模式发送消息三" + i);
                //发送一条消息
                producer.send(textMsg);
                log.info("发送消息:{}",textMsg);
            }
            log.info("发送成功");
        } catch (Exception e) {
            log.info("发送消息异常:{}",e);
        } finally {
            connection.close();
        }
    }
}
