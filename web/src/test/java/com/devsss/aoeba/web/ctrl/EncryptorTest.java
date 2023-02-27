package com.devsss.aoeba.web.ctrl;

import lombok.extern.slf4j.Slf4j;
import org.jasypt.util.text.BasicTextEncryptor;
import org.junit.Test;

@Slf4j
public class EncryptorTest {

    @Test
    public void mm() {
        BasicTextEncryptor basicTextEncryptor = new BasicTextEncryptor();
        basicTextEncryptor.setPassword("");
        log.info(basicTextEncryptor.encrypt(""));
    }
}
