package com.telus.credit.migration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.integration.AckMode;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.MessageChannel;

@Configuration
public class PubSubConfig {

    public static final String SUBSCRIPTION_NAME_PROPERTY_KEY = "${target.pubsub.subscription}";

    @Bean
    public MessageChannel pubSubInputChannel() {
        return new PublishSubscribeChannel();
    }

    @Bean
    @ConditionalOnProperty("target.pubsub.subscription")
    public PubSubInboundChannelAdapter messageChannelAdapter(PubSubTemplate pubSubTemplate, @Value(SUBSCRIPTION_NAME_PROPERTY_KEY) String subscriptionName) {
        PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate, subscriptionName);
        adapter.setOutputChannel(pubSubInputChannel());
        adapter.setAckMode(AckMode.MANUAL);
        adapter.setPayloadType(String.class);
        return adapter;
    }
}
