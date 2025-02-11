package com.telus.credit.migration;

import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.telus.credit.migration.JceCryptoImplLocal;
 
@Service
public class CryptoService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CryptoService.class);

  private JceCryptoImplLocal crypto;

  public CryptoService(JceCryptoImplLocal jceCryptoImpl) {
    this.crypto = jceCryptoImpl;
  }

  public String encrypt(String data) throws Exception {
    Assert.isTrue(StringUtils.isNotBlank(data), "Invalid input data=" + data);
    return new String(crypto.encrypt(data.getBytes(StandardCharsets.UTF_8)));
  }

  public String encryptOrNull(String data) throws Exception {
    return StringUtils.isBlank(data) ? null : encrypt(data);
  }

  public String decrypt(String data) throws Exception {
    Assert.isTrue(StringUtils.isNotBlank(data), "Invalid input data=" + data);
    return new String(crypto.decrypt(data.getBytes(StandardCharsets.UTF_8)));
  }

  public String decryptAndIgnoreError(String data) {
    try {
      return StringUtils.trimToNull(data) != null ? decrypt(data) : null;
    } catch (Exception e) {
    	e.printStackTrace();
    	return data;
    }
  }

  public String decryptAndNullOnError(String data) {
    try {
      return StringUtils.trimToNull(data) != null ? decrypt(data) : null;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
