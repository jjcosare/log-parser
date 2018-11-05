package com.jjcosare.parser.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by jjcosare on 10/27/18.
 */
@Data
@NoArgsConstructor
@Entity
public class Access implements Serializable {

    public static final DateTimeFormatter DATE_TIME_LOG_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    public static final DateTimeFormatter DATE_TIME_INPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd.HH:mm:ss");
    public static final int DATA_DATE = 0;
    public static final int DATA_IP = 1;
    public static final int DATA_REQUEST = 2;
    public static final int DATA_STATUS = 3;
    public static final int DATA_USER_AGENT = 4;

    private static final long serialVersionUID = 689546226390252947L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private Long version;

    private LocalDateTime date;

    private String ip;

    private String request;

    private int status;

    private String userAgent;

}
