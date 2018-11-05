package com.jjcosare.parser;

import com.jjcosare.parser.constant.OptionalArgs;
import com.jjcosare.parser.service.AccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class LogParserApplication implements ApplicationRunner {

	//private static final Logger LOGGER = LoggerFactory.getLogger(LogParserApplication.class);

	@Autowired
	@Qualifier("${com.jjcosare.parser.service.impl}")
	private AccessService accessService;

	public static void main(String[] args) {
		SpringApplication.run(LogParserApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments applicationArguments) throws Exception {
		if(!ObjectUtils.isEmpty(applicationArguments)) {

			Map<String, String> argumentMap = new HashMap();
			applicationArguments.getNonOptionArgs().forEach(nonOption -> {
				switch (nonOption) {
					case "help":
						displayHelpMessage();
						break;
					default:
						System.out.println("Run \"java -jar parser-0.0.1-SNAPSHOT.jar help\" for more info.");
						break;
				}
			});
			if(!applicationArguments.getNonOptionArgs().contains("help") && !applicationArguments.getOptionNames().isEmpty()) {
				applicationArguments.getOptionNames().forEach(optionName -> {
					if (applicationArguments.getNonOptionArgs().contains("debug")) {
						System.out.println("arg: " + optionName + " = " + applicationArguments.getOptionValues(optionName));
					}
					argumentMap.put(optionName.toUpperCase(), applicationArguments.getOptionValues(optionName).get(0));
				});

				String accessLog = argumentMap.get(OptionalArgs.ACCESSLOG.name());
				String startDate = argumentMap.get(OptionalArgs.STARTDATE.name());
				String duration = argumentMap.get(OptionalArgs.DURATION.name());
				String threshold = argumentMap.get(OptionalArgs.THRESHOLD.name());
				String ip = argumentMap.get(OptionalArgs.IP.name());

				if (!StringUtils.isEmpty(ip)) {
					accessService.displayAccessByIp(ip);
				} else {
					if (!StringUtils.isEmpty(accessLog)) {
						accessService.loadDataAndBlockIP(accessLog, startDate, duration, threshold);
					} else {
						accessService.blockIP(startDate, duration, threshold);
					}
				}
			}
		}
	}

	private void displayHelpMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append("------------------------------------------------------------------------\n");
		sb.append("Access Log Parser <jjcosare@gmail.com>\n");
		sb.append("------------------------------------------------------------------------\n");
		sb.append("Loading data then block IP: \n\"java -jar parser-0.0.1-SNAPSHOT.jar --accesslog=access.log --startDate=2017-01-01.00:00:00 --duration=daily --threshold=200\"\n");
		sb.append("Loading data only: \n\"java -jar parser-0.0.1-SNAPSHOT.jar --accesslog=access.log\"\n");
		sb.append("Block IP only: \n\"java -jar parser-0.0.1-SNAPSHOT.jar --startDate=2017-01-01.00:00:00 --duration=daily --threshold=200\"\n");
		sb.append("Display access logs on IP: \n\"java -jar parser-0.0.1-SNAPSHOT.jar --ip=192.168.234.82\"\n");
		System.out.println(sb.toString());
	}

}
