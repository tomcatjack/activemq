package www.luqi.queuemodel;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * @author LUCCI
 * @date 2018/8/16 15:26
 * @Description: 消费者 (一对一模式)
 * @Modify:
 */
@Slf4j
public class AppReceive {

    private static final String url = "tcp://192.168.3.62:61616";

    private static final String queueName = "queue-test";

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
            Destination destination = session.createQueue(queueName);

            //6.创建一个消费者
            MessageConsumer  consumer = session.createConsumer(destination);

            //7.实现一个消息的监听器
            //实现这个监听器后，以后只要有消息，就会通过这个监听器接收到
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        String text = ((TextMessage) message).getText();
                        log.info("接收消息:{}",text);
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }

                }
            });

        }catch (Exception e){
            log.info("接收消息异常:{}",e);
        } finally {
            //connection.close();
        }
    }
}
