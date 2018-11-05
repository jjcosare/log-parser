package com.jjcosare.parser.model;

import com.jjcosare.parser.constant.Duration;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by jjcosare on 10/28/18.
 */
@Data
@Entity
@NamedNativeQuery(
        name = "getBlockListQuery",
        query = "SELECT ip AS ip, count(ip) AS accessCount FROM access " +
                "WHERE date BETWEEN :startDate and :endDate " +
                "GROUP BY ip HAVING accessCount >= :threshold",
        resultSetMapping = "blockListResultSet"
)
@SqlResultSetMapping(name="blockListResultSet", classes = {
        @ConstructorResult(targetClass = Block.class,
                columns = {
                    @ColumnResult(name="ip", type = String.class),
                    @ColumnResult(name="accessCount", type = Integer.class)
        })
})
public class Block implements Serializable {

    private static final long serialVersionUID = 276832547888964856L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private Long version;

    private String ip;

    private int accessCount;

    private String reason;

    @Enumerated(EnumType.STRING)
    private Duration duration;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private int threshold;

    public Block(){}

    public Block (String ip, int accessCount){
        this.ip = ip;
        this.accessCount = accessCount;
    }

}
