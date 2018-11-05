package com.jjcosare.parser.repository;

import com.jjcosare.parser.model.Block;

import java.time.LocalDateTime;
import java.util.stream.Stream;

/**
 * Created by jjcosare on 10/28/18.
 */
public interface BlockRepositoryCustom {

    Stream<Block> getBlockList(LocalDateTime startDate, LocalDateTime endDate, int threshold);

}
