<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">

<!-- ===================================================================== -->
<!--                                                                       -->
<!--  Log4j Configuration for DESK                                         -->
<!--                                                                       -->
<!-- ===================================================================== -->

<log4j:configuration xmlns:log4j="http://jakarta.apache.org/log4j/" debug="false">

    <!--
      - d=date
      - p=priority
      - c=category
      - C=fully qualified class name
      - n=line separator
      - m=app supplied message
      - M=method name
      -->

    <appender name="CONSOLE" class="org.apache.log4j.ConsoleAppender">
        <param name="Target" value="System.out"/>
        <param name="Threshold" value="DEBUG"/>

        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" value="%-4r [%d] [%t] %-5p %c {%x} - %m%n"/>
        </layout>
    </appender>
    
    <appender name="FILE" class="org.apache.log4j.DailyRollingFileAppender">
        <param name="File" value="${catalina.base}/logs/crp-component-showcase.log"/>
        <param name="Append" value="true"/>
        <param name="Threshold" value="DEBUG"/>
        <param name="DatePattern" value="'.'yyyy-MM-dd"/>

        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" value="%-4r [%d] [%t] %-5p %c {%x} - %m%n"/>
        </layout>
    </appender>
    
    <appender name="CAPTURE" class="org.apache.log4j.DailyRollingFileAppender">
        <param name="File" value="${catalina.base}/logs/capture.log"/>
        <param name="Append" value="true"/>
        <param name="Threshold" value="DEBUG"/>
        <param name="DatePattern" value="'.'yyyy-MM-dd"/>

        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" value="%n[%t] %m%n"/>
        </layout>
    </appender>
    
    
    <logger name="au.com.sensis.mobile" additivity="false">
        <level value="INFO" />
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </logger>
    
    <logger name="SERVLET_CAPTURE" additivity="false">
        <level value="INFO" />
        <appender-ref ref="CAPTURE"/>
    </logger>

    <root>
        <priority value="INFO"/>
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>

</log4j:configuration>
