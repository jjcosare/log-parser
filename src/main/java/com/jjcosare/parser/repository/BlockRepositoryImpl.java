package com.jjcosare.parser.repository;

import com.jjcosare.parser.model.Block;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.stream.Stream;

/**
 * Created by jjcosare on 10/28/18.
 */
public class BlockRepositoryImpl implements BlockRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    @Override
    public Stream<Block> getBlockList (LocalDateTime startDate, LocalDateTime endDate, int threshold) {
        Query query = entityManager.unwrap(Session.class).createNamedQuery("getBlockListQuery");
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setParameter("threshold", threshold);
        return ((Query) query).stream();
    }

}
