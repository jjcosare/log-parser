package com.jjcosare.parser.repository;

import com.jjcosare.parser.model.Access;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

/**
 * Created by jjcosare on 10/28/18.
 */
@Repository
public interface AccessRepository extends CrudRepository<Access, Long> {

    Stream<Access> findByIp (String ip);

}
