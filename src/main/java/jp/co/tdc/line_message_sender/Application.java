package jp.co.tdc.line_message_sender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

import jp.co.tdc.line_message_sender.service.CommandHandleService;
import jp.co.tdc.line_message_sender.service.LoadLineChannelCredentialCommandHandleService;
import jp.co.tdc.line_message_sender.service.LoadLineMessageTemplateCommandHandleService;
import jp.co.tdc.line_message_sender.service.LoadLinePushMessageCommandHandleService;
import jp.co.tdc.line_message_sender.service.PushMessagesCommandHandleService;
import jp.co.tdc.line_message_sender.service.RefreshTokenCommandHandleService;

@SpringBootApplication
@EnableRetry
public class Application implements ApplicationRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(PushMessagesCommandHandleService.class);
	public static final String REFRESH_TOKEN_COMMAND_NAME = "refresh-token";
	public static final String PUSH_MESSAGES_COMMAND_NAME = "push-messages";
	public static final String LOAD_CHANNEL_CREDENTIAL_COMMAND_NAME = "load-channel-credential";
	public static final String LOAD_MESSAGE_TEMPLATE_COMMAND_NAME = "load-message-template";
	public static final String LOAD_PUSH_MESSAGE_COMMAND_NAME = "load-push-message";

	@Autowired
	private RefreshTokenCommandHandleService refreshTokenCommandHandler;

	@Autowired
	private PushMessagesCommandHandleService pushMessagesCommandHandler;

	@Autowired
	private LoadLineChannelCredentialCommandHandleService loadLineChannelCredentialsCommandHandleService;

	@Autowired
	private LoadLineMessageTemplateCommandHandleService loadLineMessageTemplateCommandHandleService;

	@Autowired
	private LoadLinePushMessageCommandHandleService loadLinePushMessageCommandHandleService;

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(Application.class);

		application.setBannerMode(Mode.OFF);
		application.setWebApplicationType(WebApplicationType.NONE);
		application.setLogStartupInfo(false);
		application.run(args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		Map<String, CommandHandleService> commandHandleServiceMap = createCommandHandleServiceMap();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("commandNames={}", commandHandleServiceMap.keySet());
		}

		List<String> nonOptionArgs = args.getNonOptionArgs();

		if (nonOptionArgs.isEmpty()) {
			throw new ApplicationException("Command not specified");
		}

		String command = nonOptionArgs.get(0);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("command={}", command);
		}

		CommandHandleService subCommandService = commandHandleServiceMap.get(command);

		if (subCommandService == null) {
			throw new ApplicationException("Command \"" + command + "\" not defined");
		}

		subCommandService.run(args);
	}

	private Map<String, CommandHandleService> createCommandHandleServiceMap() {
		Map<String, CommandHandleService> commandHandleServiceMap = new HashMap<>();

		commandHandleServiceMap.put(REFRESH_TOKEN_COMMAND_NAME, refreshTokenCommandHandler);
		commandHandleServiceMap.put(PUSH_MESSAGES_COMMAND_NAME, pushMessagesCommandHandler);
		commandHandleServiceMap.put(LOAD_CHANNEL_CREDENTIAL_COMMAND_NAME, loadLineChannelCredentialsCommandHandleService);
		commandHandleServiceMap.put(LOAD_MESSAGE_TEMPLATE_COMMAND_NAME, loadLineMessageTemplateCommandHandleService);
		commandHandleServiceMap.put(LOAD_PUSH_MESSAGE_COMMAND_NAME, loadLinePushMessageCommandHandleService);

		return commandHandleServiceMap;
	}
}
