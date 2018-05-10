package com.service.layer.servicelayer;

import com.service.layer.servicelayer.model.StopWordsByLanguage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.json.JsonToObjectTransformer;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@SpringBootApplication
public class ServiceLayerApplication {

	private static Log logger = LogFactory.getLog(ServiceLayerApplication.class);

	public static void main(String[] args) throws IOException {

		SpringApplication.run(ServiceLayerApplication.class, args);
		ClassPathXmlApplicationContext context =
				new ClassPathXmlApplicationContext(new String[] {"messageBrokerContext.xml",
						                            "integrationContext.xml",
                                                    "configuration.xml"});

		MessageChannel requestChannel = context.getBean("request.channel", MessageChannel.class);
		requestChannel.send(MessageBuilder.withPayload("").build());

	}
}
