package com.jjcosare.parser.repository;

import com.jjcosare.parser.model.Block;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by jjcosare on 10/28/18.
 */
@Repository
public interface BlockRepository extends CrudRepository<Block, Long>, BlockRepositoryCustom {

}
