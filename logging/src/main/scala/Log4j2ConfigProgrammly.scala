import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.appender.ConsoleAppender
import org.apache.logging.log4j.core.config.{ConfigurationSource, LoggerConfig}
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory
import org.apache.logging.log4j.core.layout.PatternLayout

object Log4j2ConfigProgrammly extends App {
  val factory = new XmlConfigurationFactory
  val configurationSource = new ConfigurationSource(getClass.getResourceAsStream("log4j2.xml"))
  val context = new LoggerContext("JournalDevLoggerContext")
  val configuration = factory.getConfiguration(context, configurationSource)

  val appender = ConsoleAppender.createDefaultAppenderForLayout(PatternLayout.createDefaultLayout())
  configuration.addAppender(appender)

  val loggerConfig = new LoggerConfig("com",Level.FATAL,false)
  loggerConfig.addAppender(appender,null,null)

  configuration.addLogger("com",loggerConfig)

  context.start(configuration)

  val logger = context.getLogger("com")
  logger.log(Level.FATAL,"Logger Name :: "+logger.getName + ":: Passed Message ::")
  // LogEvent of Error message for Logger configured as FATAL// LogEvent of Error message for Logger configured as FATAL

  logger.log(Level.ERROR, "Logger Name :: " + logger.getName + " :: Not Passed Message ::")

  // LogEvent of ERROR message that would be handled by Root
  logger.getParent.log(Level.ERROR, "Root Logger :: Passed Message As Root Is Configured For ERROR Level messages")

}
