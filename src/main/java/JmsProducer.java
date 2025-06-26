
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.RandomStringUtils;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;

/**
 * A JMS producer (sender or publisher) application that sends a simple message
 * to the named destination (queue or topic).
 * 
 */
public class JmsProducer {

	private static final int RECONNECT_TIMEOUT_SECONDS = 20;
	private static final String DEFAULT_CONNECTION_LIST = "localhost(1431)";
	private static final String DEFAULT_CHANNEL = "LPQAINT.DVLPR.CN";
	private static final String DEFAULT_QUEUEMANAGER = "LPQAINT";

	private static final String USER_NAME = null;

	private static final String PASSWORD = null;

	private static final String DESTINATION = "ADMI.INITADM";

	private static final boolean isTopic = false;

	private static final boolean CLIENT_TRANSPORT = true;
	private static final String DEFAULT_TESTDURATION = "5";

	/**
	 * Main method
	 *
	 * @param args
	 * @throws ParseException
	 */
	public static void main(final String[] args) throws ParseException {

		final CommandLineParser parser = new DefaultParser();
		final Options options = new Options();

		Option connectionListOption = Option.builder("connectionList").hasArg(true)
				.desc("default: " + DEFAULT_CONNECTION_LIST).optionalArg(true).build();

		options.addOption(connectionListOption);

		Option channelOption = Option.builder("channel").hasArg().desc("default: " + DEFAULT_CHANNEL).optionalArg(true)
				.build();
		options.addOption(channelOption);

		Option queueManagerOption = Option.builder("queuemanager").hasArg().desc("default: " + DEFAULT_QUEUEMANAGER)
				.optionalArg(true).build();
		options.addOption(queueManagerOption);

		Option durationOption = Option.builder("duration").hasArg()
				.desc("default: " + DEFAULT_TESTDURATION + " minutes").optionalArg(true).build();
		options.addOption(durationOption);

		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp(" ", options);
		System.out.println("--------------------------------------------------------------");

		final CommandLine commandLine = parser.parse(options, args);

		String connectionList = commandLine.getOptionValue(connectionListOption.getOpt(), DEFAULT_CONNECTION_LIST);
		System.out.println("using connectionList = " + connectionList);

		String channel = commandLine.getOptionValue(channelOption.getOpt(), DEFAULT_CHANNEL);
		System.out.println("using channel = " + channel);
		String queueManager = commandLine.getOptionValue(queueManagerOption.getOpt(), DEFAULT_QUEUEMANAGER);
		System.out.println("using queueManager = " + queueManager);

		final long testDurationMinutes = Long
				.parseLong(commandLine.getOptionValue(durationOption.getOpt(), DEFAULT_TESTDURATION));

		try {
			// Create a connection factory
			final JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
			final JmsConnectionFactory cf = ff.createConnectionFactory();

			cf.setIntProperty(WMQConstants.WMQ_CLIENT_RECONNECT_OPTIONS, WMQConstants.WMQ_CLIENT_RECONNECT);
			cf.setIntProperty(WMQConstants.WMQ_CLIENT_RECONNECT_TIMEOUT, RECONNECT_TIMEOUT_SECONDS);

			// Set the properties
			cf.setStringProperty(WMQConstants.WMQ_CONNECTION_NAME_LIST, connectionList);
			// cf.setIntProperty(WMQConstants.WMQ_PORT, PORT);
			cf.setStringProperty(WMQConstants.WMQ_CHANNEL, channel);
			if (CLIENT_TRANSPORT) {
				cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
			} else {
				cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_BINDINGS);
			}
			cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, queueManager);
			if (USER_NAME != null) {
				System.out.println("Using user name and password for authentication");
				cf.setStringProperty(WMQConstants.USERID, USER_NAME);
				cf.setStringProperty(WMQConstants.PASSWORD, PASSWORD);
				cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
			} else {
				System.out.println("Using anonymous authentication");
			}
			// Create JMS objects
			try (Connection connection = cf.createConnection()) {
				connection.setClientID("kuhu");
				connection.start();
				System.out.println(testDurationMinutes + " minutes send/read test");
				long startTime = System.currentTimeMillis();
				while ((System.currentTimeMillis() - startTime) < TimeUnit.MINUTES.toMillis(testDurationMinutes)) {
					sendAndRead(connection);
				}
			}

		} catch (final JMSException jmsex) {
			recordFailure(jmsex);
		}
		return;
	} // end main()

	private static void sendAndRead(Connection connection) throws JMSException {
		Destination destination;
		try (Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);) {
			if (isTopic) {
				destination = session.createTopic(DESTINATION);
			} else {
				destination = session.createQueue(DESTINATION);
			}
			try (MessageProducer producer = session.createProducer(destination)) {

				final TextMessage message = session
						.createTextMessage("random :" + RandomStringUtils.randomAlphanumeric(12));
				producer.send(message);
				try (MessageConsumer consumer = session.createConsumer(destination)) {
					do {
						Message receivedMessage = consumer.receive(TimeUnit.SECONDS.toMillis(1));
						if (receivedMessage != null) {
							System.out.println("Received sent message:" + receivedMessage.getBody(String.class));
						} else {
							break;
						}
					} while (true);
				}
			}
		}
	}

	/**
	 * Process a JMSException and any associated inner exceptions.
	 *
	 * @param jmsex
	 */
	private static void processJMSException(final JMSException jmsex) {
		System.out.println(jmsex);
		Throwable innerException = jmsex.getLinkedException();
		if (innerException != null) {
			System.out.println("Inner exception(s):");
		}
		while (innerException != null) {
			System.out.println(innerException);
			innerException = innerException.getCause();
		}
		return;
	}

	/**
	 * Record this run as failure.
	 *
	 * @param ex
	 */
	private static void recordFailure(final Exception ex) {
		if (ex != null) {
			if (ex instanceof JMSException) {
				processJMSException((JMSException) ex);
			} else {
				System.out.println(ex);
			}
		}
		return;
	}

} // end class
