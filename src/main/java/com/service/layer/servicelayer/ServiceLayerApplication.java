package com.service.layer.servicelayer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;

@SpringBootApplication
public class ServiceLayerApplication {

	private static Log logger = LogFactory.getLog(ServiceLayerApplication.class);

	public static void main(String[] args) {

		SpringApplication.run(ServiceLayerApplication.class, args);
		ClassPathXmlApplicationContext context =
				new ClassPathXmlApplicationContext(new String[] {"messageBrokerContext.xml",
						                            "integrationContext.xml",
                                                    "configuration.xml"});

		MessageChannel requestChannel = context.getBean("request.channel", MessageChannel.class);
		requestChannel.send(MessageBuilder.withPayload("").build());

	}
}
