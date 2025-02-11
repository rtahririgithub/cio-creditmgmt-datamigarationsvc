package com.telus.credit.migration;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.secretmanager.SecretManagerTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.telus.framework.crypto.Crypto;
import com.telus.framework.crypto.EncryptionUtil;
import com.telus.framework.crypto.impl.jce.AlgorithmParamSpecGenerator;
import com.telus.framework.crypto.impl.jce.IvParamSpecGenerator;
import com.telus.framework.crypto.impl.jce.JceCryptoImpl;
import com.telus.framework.crypto.impl.pilot.PilotCryptoImpl;

@Configuration
public class CryptoConfig {

  @Value("${crypto.key1}")
  private String key1;

  @Value("${crypto.key2}")
  private String key2;

  @Value("${crypto.key3}")
  private String key3;

  @Value("${crypto.keystoreUrl}")
  private String keystoreUrl;

  @Value("${crypto.keyAlias}")
  private String keyAlias;

  @Value("${crypto.keyPassword}")
  private String keyPassword;


  public AlgorithmParamSpecGenerator algorithmParamSpecGenerator() {
    return new IvParamSpecGenerator(Boolean.TRUE);
  }

  public Crypto pilotCrypto() {

    PilotCryptoImpl pilotCrypto = new PilotCryptoImpl();
    pilotCrypto.setKey1(key1);
    pilotCrypto.setKey2(key2);
    pilotCrypto.setKey3(key3);
    pilotCrypto.init();
    return pilotCrypto;
  }

  @Bean
  public JceCryptoImpl jceCryptoImpl(@Autowired SecretManagerTemplate secretManagerTemplate) throws Exception {
    EncryptionUtil.setCrypto(pilotCrypto());
    JceCryptoImpl bean = new JceCryptoImpl();
    bean.setAlgorithmParamSpecGenerator(algorithmParamSpecGenerator());
    bean.setEncodeBase64(Boolean.TRUE);
    bean.setKeyAlias(EncryptionUtil.decryptHex(keyAlias));
    bean.setKeyPassword(EncryptionUtil.decryptHex(keyPassword));
    Path f = Files.write(new File("CDA_keystore.jks").toPath(), secretManagerTemplate.getSecretBytes(keystoreUrl));
    URL fileUrl = f.toUri().toURL();
    bean.setKeystoreURL(fileUrl.toString());
    bean.init();
    return bean;
  }
}
