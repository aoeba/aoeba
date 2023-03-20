package com.devsss.aoeba.service.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Options {

    @Id
    String optKey;
    String optValue;
}
