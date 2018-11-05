package com.jjcosare.parser.service;

import com.jjcosare.parser.constant.Duration;
import com.jjcosare.parser.model.Access;
import com.jjcosare.parser.model.Block;
import com.jjcosare.parser.repository.AccessRepository;
import com.jjcosare.parser.repository.BlockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Created by jjcosare on 10/28/18.
 */
@Service
public class AccessServiceImpl implements AccessService {

    @Autowired
    private AccessRepository accessRepository;

    @Autowired
    private BlockRepository blockRepository;

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;

    @Override
    @Transactional
    public void loadData (String accessLog) {
        if(StringUtils.hasLength(accessLog)) {
            try {
                Path path = Paths.get(accessLog);
                try (Stream<String> lines = Files.lines(path)) {
                    List<Access> accessBatchList = new ArrayList(batchSize);
                    AtomicInteger atomicInteger = new AtomicInteger();
                    lines.forEach(line -> {
                        String[] data = line.split("\\|");

                        Access access = new Access();
                        access.setDate(LocalDateTime.parse(data[Access.DATA_DATE], Access.DATE_TIME_LOG_FORMAT));
                        access.setIp(data[Access.DATA_IP]);
                        access.setRequest(data[Access.DATA_REQUEST].replaceAll("\"", ""));
                        access.setStatus(Integer.parseInt(data[Access.DATA_STATUS]));
                        access.setUserAgent(data[Access.DATA_USER_AGENT].replaceAll("\"", ""));

                        accessBatchList.add(access);
                        if(accessBatchList.size() == batchSize) {
                            batchAccessSave(accessBatchList, atomicInteger);
                        }
                    });
                    // save below batch size
                    batchAccessSave(accessBatchList, atomicInteger);
                }
            } catch (IOException ex) {
                System.out.println("Please update \"accessLog\" to the correct file path");
            }
        }else{
            System.out.println("Required parameter \"accessLog\" loading data");
        }
    }

    @Override
    @Transactional
    public void blockIP (String startDate, String duration, String threshold) {
        boolean hasInputErrors = false;
        LocalDateTime startDateTime = null;
        Duration durationEnum = null;
        int thresholdInt = 0;

        if(!StringUtils.hasLength(startDate)){
            hasInputErrors = true;
            System.out.println("Required parameter \"startDate\" with format: \"yyyy-MM-dd HH:mm:ss.SSS\"");
        }else{
            try {
                startDateTime = LocalDateTime.parse(startDate, Access.DATE_TIME_INPUT_FORMAT);
            } catch (DateTimeParseException ex){
                hasInputErrors = true;
                System.out.println("Please update \"startDate\" with format: \"yyyy-MM-dd HH:mm:ss.SSS\"");
            }
        }


        if(!StringUtils.hasLength(duration)){
            hasInputErrors = true;
            System.out.println("Required parameter \"duration\" to either \"hourly\" or \"daily\"");
        }else{
            try {
                durationEnum = Duration.valueOf(duration.toUpperCase());
            } catch (IllegalArgumentException ex) {
                hasInputErrors = true;
                System.out.println("Please update \"duration\" to either \"hourly\" or \"daily\"");
            }
        }

        if(!StringUtils.hasLength(threshold)){
            hasInputErrors = true;
            System.out.println("Required parameter \"threshold\" with integer value not less than 3");
        }else{
            try {
                thresholdInt = Integer.parseInt(threshold);
                if(thresholdInt < 3){
                    hasInputErrors = true;
                    System.out.println("Please update \"threshold\" with integer value not less than 3");
                }
            } catch (NumberFormatException ex) {
                hasInputErrors = true;
                System.out.println("Please update \"threshold\" with integer value not less than 3");
            }
        }

        if(!hasInputErrors) {
            blockIP(startDateTime, durationEnum, thresholdInt);
        }
    }

    @Override
    @Transactional
    public void loadDataAndBlockIP (String accessLog, String startDate, String duration, String threshold) {
        loadData(accessLog);
        blockIP(startDate, duration, threshold);
    }

    @Override
    @Transactional
    public void displayAccessByIp (String ip) {
        Supplier<Stream<Access>> streamSupplier = () -> accessRepository.findByIp(ip);
        if(streamSupplier.get().iterator().hasNext()){
            AtomicInteger atomicInteger = new AtomicInteger();
            streamSupplier.get().forEach(access -> {
                System.out.println(atomicInteger.incrementAndGet() + " " + access.toString());
            });
        }else{
            System.out.println("No access logs found on ip \""+ ip +"\"");
        }
    }

    private void blockIP (LocalDateTime startDate, Duration duration, int threshold) {
        LocalDateTime endDate = getEndDate(startDate, duration);

        Supplier<Stream<Block>> streamSupplier = () -> blockRepository.getBlockList(startDate, endDate, threshold);
        List<Block> blockBatchList = new ArrayList(batchSize);
        if(streamSupplier.get().iterator().hasNext()) {
            AtomicInteger atomicInteger = new AtomicInteger();
            streamSupplier.get().forEach(block -> {
                String reason = getBlockReason(block.getIp(), startDate, endDate, threshold);
                block.setReason(reason);
                block.setDuration(duration);
                block.setStartDate(startDate);
                block.setEndDate(endDate);
                block.setThreshold(threshold);

                blockBatchList.add(block);
                if(blockBatchList.size() == batchSize) {
                    batchBlockSave(blockBatchList, atomicInteger);
                }
            });
            // save below batch size
            batchBlockSave(blockBatchList, atomicInteger);
        }else{
            System.out.println("All access logs are within the limits specified");
        }
    }

    private LocalDateTime getEndDate(LocalDateTime startDate, Duration duration){
        LocalDateTime endDate = startDate;
        switch (duration) {
            case HOURLY:
                endDate = endDate.plusMinutes(59).plusSeconds(59);
                break;
            case DAILY:
                endDate = endDate.plusHours(23).plusMinutes(59).plusSeconds(59);
                break;
            default:
                throw new IllegalArgumentException("unhandled enum value: " + duration);
        }
        return endDate;
    }

    private void batchBlockSave(List<Block> blockList, AtomicInteger atomicInteger){
        blockRepository.saveAll(blockList);
        blockList.stream().forEach(block -> {
            System.out.println(atomicInteger.incrementAndGet() + ": " + block.getReason());
        });
        blockList.clear();
    }

    private void batchAccessSave(List<Access> accessList, AtomicInteger atomicInteger){
        accessRepository.saveAll(accessList);
        accessList.stream().forEach(access -> {
            System.out.println(atomicInteger.incrementAndGet() + ": " + access.toString());
        });
        accessList.clear();
    }

    private String getBlockReason(String ip, LocalDateTime startDate, LocalDateTime endDate, int threshold){
        StringBuilder sb = new StringBuilder();
        sb.append("If you open the log file, ");
        sb.append(ip);
        sb.append(" has ");
        sb.append(threshold);
        sb.append(" or more requests between ");
        sb.append(startDate.format(Access.DATE_TIME_INPUT_FORMAT));
        sb.append(" and ");
        sb.append(endDate.format(Access.DATE_TIME_INPUT_FORMAT));
        return sb.toString();
    }

}