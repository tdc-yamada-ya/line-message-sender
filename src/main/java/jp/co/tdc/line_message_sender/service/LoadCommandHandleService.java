package jp.co.tdc.line_message_sender.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;

public abstract class LoadCommandHandleService implements CommandHandleService {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoadCommandHandleService.class);

	private static final String FILE_OPTION_NAME = "file";

	@Override
	public void run(ApplicationArguments args) {
		LOGGER.info("Load file start");

		List<String> fileOptionValues = args.getOptionValues(FILE_OPTION_NAME);

		if (fileOptionValues == null || fileOptionValues.isEmpty()) {
			throw new CommandHandleServiceException("\"--file=<file>\" is not specified");
		}

		String file = fileOptionValues.get(0);
		int count = 0;

		try (Reader reader = new InputStreamReader(new FileInputStream(file), "UTF-8")) {
			CSVParser parser = CSVFormat.EXCEL
					.withHeader(getHeader())
					.withIgnoreEmptyLines()
					.withFirstRecordAsHeader()
					.parse(reader);

			for (CSVRecord record : parser) {
				handleCSVRecord(record);
				count++;
			}
		} catch (IOException e) {
			LOGGER.error("CSV file load error", e);
		}

		LOGGER.info("Save records - count={}", count);
		LOGGER.info("Load file finished");
	}

	protected abstract void handleCSVRecord(CSVRecord record);

	protected abstract Class<? extends Enum<?>> getHeader();
}
