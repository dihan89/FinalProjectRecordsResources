package recordToResource.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import recordToResource.daoAndServiceUtils.Generator.GeneratorFromFile;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.File;

@org.springframework.context.annotation.Configuration
@ComponentScan("recordRoResource.dao")
@ComponentScan("recordRoResource.controllers")
@EntityScan("recordToResource.model")
@EnableWebMvc
public class Configuration implements WebMvcConfigurer {
    private static boolean constructed = false;
    private final ApplicationContext applicationContext;

    public Configuration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private GeneratorFromFile generatorFromFile;

    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        // SpringResourceTemplateResolver automatically integrates with Spring's own
        // resource resolution infrastructure, which is highly recommended.
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(this.applicationContext);
        templateResolver.setPrefix("/WEB-INF/views/");
        templateResolver.setSuffix(".html");
        // HTML is the default value, added here for the sake of clarity.
        templateResolver.setTemplateMode(TemplateMode.HTML);
        // Template cache is true by default. Set to false if you want
        // templates to be automatically updated when modified.
        templateResolver.setCacheable(true);
        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        // SpringTemplateEngine automatically applies SpringStandardDialect and
        // enables Spring's own MessageSource message resolution mechanisms.
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        // Enabling the SpringEL compiler with Spring 4.2.4 or newer can
        // speed up execution in most scenarios, but might be incompatible
        // with specific cases when expressions in one template are reused
        // across different data types, so this flag is "false" by default
        // for safer backwards compatibility.
        templateEngine.setEnableSpringELCompiler(true);
        return templateEngine;
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine());
        registry.viewResolver(resolver);
    }

    @PostConstruct
    @Autowired
    public void makeInit() {
        if (!constructed) {
            JdbcTestUtils.deleteFromTables(
                    new JdbcTemplate(dataSource),
                    "Record", "User", "Resource");

            generatorFromFile.generateResourceFromDir(
                    new File("resourcesMy"));
            generatorFromFile.generateResourceNullRecordsFromDir(
                    new File("schedule"));
            constructed = true;
        }
    }
    // https://www.pvsm.ru/java/304158vv
}
