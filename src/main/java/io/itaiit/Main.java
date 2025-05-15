package io.itaiit;


import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author itaiit
 * @date 2023/11/30 22:32
 */
@SpringBootApplication
public class Main implements CommandLineRunner, ApplicationRunner {
    public static void main(String[] args) throws Exception {
//        Map<String, String> getenv = System.getenv();
//        System.out.println("getenv = " + getenv);
//        System.out.println("----------------------------------------------");
        SpringApplication springApplication = new SpringApplicationBuilder(Main.class).logStartupInfo(true)
//                .addCommandLineProperties(false) // 设置是否将命令行参数也注册为属性
//                .environmentPrefix("springboot") // 设置系统变量的prefix
                .build();

        ConfigurableApplicationContext run = springApplication.run(args);
//        ConfigurableApplicationContext run = SpringApplication.run(Main.class, args);
//        MutablePropertySources propertySources = run.getEnvironment().getPropertySources();
//        for (PropertySource<?> propertySource : propertySources) {
//            System.out.println(propertySource.getName());
//        }
//
//        AnnotationMetadata introspect = AnnotationMetadata.introspect(Main.class);
//        MergedAnnotations annotations = introspect.getAnnotations();
//        for (MergedAnnotation<Annotation> annotation : annotations) {
//            System.out.println(annotation.getType());
//        }

//        MyService myService = run.getBean(MyService.class);
//        System.out.println("myService: " + myService);
//
//        String property = run.getEnvironment().getProperty("my.name");
//        System.out.println("property = " + property);
//        System.out.println("beanFactory started...");
//        MyService bean = run.getBean(MyService.class);
//        bean.asyncMethod("hello async");
//        System.out.println("start success");

    }

    @Override
    public void run(String... args) throws Exception {
//        System.out.println("args = " + Arrays.toString(args));
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        System.out.println("ApplicationArguments args = " + args);
    }
}