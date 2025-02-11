package com.telus.credit.migration.thrd.Runnable;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.secretmanager.SecretManagerTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.telus.framework.crypto.Crypto;
import com.telus.framework.crypto.EncryptionUtil;
import com.telus.framework.crypto.impl.jce.AlgorithmParamSpecGenerator;
import com.telus.framework.crypto.impl.jce.IvParamSpecGenerator;
import com.telus.framework.crypto.impl.jce.JceCryptoImpl;
import com.telus.framework.crypto.impl.pilot.PilotCryptoImpl;

@Configuration
public class RunnableConfig {

	/*
	 * @Bean public ThreadPoolTaskExecutor taskExecutor() { return new
	 * ThreadPoolTaskExecutor(); }
	 */
    
}
