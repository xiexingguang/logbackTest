package com.spencer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.repository.ConstructorRepository;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws  Exception{
       Logger logback = LoggerFactory.getLogger(App.class);
       logback.info(".....normal get  of the log .....");
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger) createLogbackInstance();

        logger.info("........reflect for get the log ..........");
        logger.debug("....reflect for debug log");
        logger.error("........reflect for error log ..........");
        logger.warn("........reflect for warn log ..........");
    }

    private static String getLogbackFileName() {

        //

        return null;
    }


    public static Object createLogbackInstance() throws  Exception{


        //获取logback class
        Class<?> logbackClass = Class.forName("ch.qos.logback.classic.Logger");

        //获取loggerContext class
        Class<?> loggerContextClass = Class.forName("ch.qos.logback.classic.LoggerContext");

        //获取了logContext实例,该实例默认维护了rootLogger实例,
        Object loggerContextClassContext = loggerContextClass.newInstance();

        //获取rootLooger
        Field root = loggerContextClass.getDeclaredField("root");
        root.setAccessible(true);
        Object rootLogger = root.get(loggerContextClassContext);  // 可以在这里修改rootLogger 输出级别信息等

        Class<?> levelClass = Class.forName("ch.qos.logback.classic.Level");
        Field INFO_INT = levelClass.getDeclaredField("INFO");
        Object levelInfo = INFO_INT.get(rootLogger);

        Method setLevelMethod = logbackClass.getDeclaredMethod("setLevel", new Class<?>[]{levelClass});
        setLevelMethod.invoke(rootLogger, levelInfo);

        Class<?> appendClass = Class.forName("ch.qos.logback.core.Appender");
        Method addAppenderMethod = logbackClass.getDeclaredMethod("addAppender", new Class<?>[]{appendClass});

        //添加appender

        addAppenderMethod.invoke(rootLogger, getConsoleAppender(loggerContextClassContext));
        addAppenderMethod.invoke(rootLogger, getDailyRollingFileAppender(loggerContextClassContext));

        return rootLogger;
    }

    private static Object getConsoleAppender(Object logContext) throws  Exception {

        Class<?> consoleAppender = Class.forName("ch.qos.logback.core.ConsoleAppender");
        Object consoleObject = consoleAppender.newInstance();

        Class<?> contextClass = Class.forName("ch.qos.logback.core.Context");


        //set logcontext 给consoleObject
        Method setContextMethod = consoleAppender.getSuperclass().getSuperclass().getSuperclass().getDeclaredMethod("setContext", new Class<?>[]{contextClass});

        setContextMethod.invoke(consoleObject, logContext);

        //生成 layout 实例

        Class<?> layoutClass = Class.forName("ch.qos.logback.core.Layout");

        Class<?> patterLayout = Class.forName("ch.qos.logback.classic.PatternLayout");

        Object paterLayoutObject = patterLayout.newInstance();


       //setContext
        Method setContextMe = patterLayout.getSuperclass().getSuperclass().getSuperclass().getDeclaredMethod("setContext", new Class<?>[]{contextClass});
        setContextMe.invoke(paterLayoutObject, logContext);

        //设置patternLayout
        Method setPatternMethod = patterLayout.getSuperclass().getDeclaredMethod("setPattern", new Class<?>[]{String.class});
        setPatternMethod.invoke(paterLayoutObject, "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");

        //start layout 标记为启动
        Method startMethod1 = patterLayout.getSuperclass().getSuperclass().getDeclaredMethod("start");
        startMethod1.invoke(paterLayoutObject);


      //给console 设置 patternLayout
        Method setLayOutMethod = consoleAppender.getSuperclass().getDeclaredMethod("setLayout", new Class<?>[]{layoutClass});
        setLayOutMethod.invoke(consoleObject, paterLayoutObject);

        // 调用console start方法 激活该appender
        Method startMethod = consoleAppender.getSuperclass().getSuperclass().getDeclaredMethod("start");
        startMethod.invoke(consoleObject);




        return consoleObject;
    }

    private static Object getDailyRollingFileAppender(Object logContext) throws Exception {

        //获取dailFileAppenderClass
        Class<?> dailFileAppenderClass = Class.forName("ch.qos.logback.core.rolling.RollingFileAppender");

        //实例化dailFileAppender
        Object dailyFileAppenderObject = dailFileAppenderClass.newInstance();

        //set logcontext
        Class<?> contextClass = Class.forName("ch.qos.logback.core.Context");

        System.out.println("method :" + dailFileAppenderClass.getSuperclass().getSuperclass().getSuperclass().getSuperclass().getName());


        Method setLoggerContext = dailFileAppenderClass.
                getSuperclass().
                getSuperclass().
                getSuperclass().
                getSuperclass().getDeclaredMethod("setContext", new Class<?>[]{contextClass});

        setLoggerContext.invoke(dailyFileAppenderObject, logContext);



        Method setFileMethod = dailFileAppenderClass.getDeclaredMethod("setFile", new Class<?>[]{String.class});
        setFileMethod.invoke(dailyFileAppenderObject, "d:/logback.log");


        //设置rollingPolicy 方案
        Class<?> rollingPolicyClass = Class.forName("ch.qos.logback.core.rolling.TimeBasedRollingPolicy");

        Object rollingObject = rollingPolicyClass.newInstance();

        //给rollingPolicy 设置context
        Method setContextForRolling = rollingPolicyClass.getSuperclass().getSuperclass().getDeclaredMethod("setContext", new Class<?>[]{contextClass});
        setContextForRolling.invoke(rollingObject, logContext);

        //给rollingpolicy 设置filePattern
        Method setPateenString = rollingPolicyClass.getSuperclass().getDeclaredMethod("setFileNamePattern", new Class<?>[]{String.class});
        setPateenString.invoke(rollingObject, "logs/stdout.%d{yyyy-MM-dd}.log");

        Class<?> fileappender = Class.forName("ch.qos.logback.core.FileAppender");
        Method setParente = rollingPolicyClass.getSuperclass().getDeclaredMethod("setParent", fileappender);
        setParente.invoke(rollingObject, dailyFileAppenderObject);

        //启动rollingPolicy start
        Method startRollingPolicy = rollingPolicyClass.getDeclaredMethod("start");
        startRollingPolicy.invoke(rollingObject);

        //给dailFileRollingAppende  设置 Policy
        Class<?> rollingClass = Class.forName("ch.qos.logback.core.rolling.RollingPolicy");
        Method setPolicy = dailFileAppenderClass.getDeclaredMethod("setRollingPolicy", new Class<?>[]{rollingClass});
        setPolicy.invoke(dailyFileAppenderObject, rollingObject);

        //需要设置encode
        Class<?> encodeClass = Class.forName("ch.qos.logback.core.encoder.Encoder");
        Method setEncode = dailFileAppenderClass.getSuperclass().getSuperclass().getDeclaredMethod("setEncoder", new Class<?>[]{encodeClass});


        Class<?> patternLayoutEncoderClass = Class.forName("ch.qos.logback.classic.encoder.PatternLayoutEncoder");
        Object patternLayoutEncodeObject = patternLayoutEncoderClass.newInstance();

        //给encode 设置pattern

        Method setPatternMethod = patternLayoutEncoderClass.getSuperclass().getDeclaredMethod("setPattern", new Class<?>[]{String.class});
        setPatternMethod.invoke(patternLayoutEncodeObject, "%m%n");

        //给encode 设置charset
        Method setCharsetMethod = patternLayoutEncoderClass.getSuperclass().getSuperclass().getDeclaredMethod("setCharset", new Class<?>[]{Charset.class});
        setCharsetMethod.invoke(patternLayoutEncodeObject, Charset.forName("UTF-8"));

        //给encode设置是否立即刷新
        Method setImmediateFlush = patternLayoutEncoderClass.getSuperclass().getSuperclass().getDeclaredMethod("setImmediateFlush", new Class<?>[]{boolean.class});
        setImmediateFlush.invoke(patternLayoutEncodeObject, true);
       //给encode 设置context
        Method setContext = patternLayoutEncoderClass.getSuperclass()
                .getSuperclass()
                .getSuperclass()
                .getSuperclass().getDeclaredMethod("setContext", new Class<?>[]{contextClass});
        setContext.invoke(patternLayoutEncodeObject, logContext);

        //给encode设置layout 开始
        Class<?> layoutClass = Class.forName("ch.qos.logback.core.Layout");

        Class<?> patterLayout = Class.forName("ch.qos.logback.classic.PatternLayout");

        Object paterLayoutObject = patterLayout.newInstance();


        //setContext
        Method setContextMe = patterLayout.getSuperclass().getSuperclass().getSuperclass().getDeclaredMethod("setContext", new Class<?>[]{contextClass});
        setContextMe.invoke(paterLayoutObject, logContext);

        //设置patternLayout
        Method setPatternMethods= patterLayout.getSuperclass().getDeclaredMethod("setPattern", new Class<?>[]{String.class});
        setPatternMethods.invoke(paterLayoutObject, "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");

        //start layout 标记为启动
        Method startMethod1 = patterLayout.getSuperclass().getSuperclass().getDeclaredMethod("start");
        startMethod1.invoke(paterLayoutObject);

       // Method setPatterlayout = patternLayoutEncoderClass.getSuperclass().getSuperclass().getDeclaredMethod("setLayout", new Class<?>[]{layoutClass});
       // setPatterlayout.invoke(patternLayoutEncodeObject, paterLayoutObject);// 设置layout 结束

        //将encode 标记启动
        Method startEncode = patternLayoutEncoderClass.getSuperclass().getSuperclass().getSuperclass().getDeclaredMethod("start");
        startEncode.invoke(patternLayoutEncodeObject);



        //将enocde 加入到dailyRollingAppender中
        setEncode.invoke(dailyFileAppenderObject, patternLayoutEncodeObject);


        //设置start
        Method startMethod = dailFileAppenderClass.getSuperclass().getSuperclass().getDeclaredMethod("start");
        startMethod.setAccessible(true);
        startMethod.invoke(dailyFileAppenderObject);

        return dailyFileAppenderObject;
    }










}
