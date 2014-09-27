package yachosan;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import net.sf.log4jdbc.sql.jdbcapi.DataSourceSpy;
import org.dozer.spring.DozerBeanMapperFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.web.HttpMapperProperties;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceArrayPropertyEditor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import yachosan.domain.model.Password;
import yachosan.domain.model.ProposedDate;
import yachosan.domain.model.ScheduleId;
import yachosan.infra.model.aikotoba.AikotobaMethodArgumentResolver;
import yachosan.infra.model.password.PasswordDeserializer;
import yachosan.infra.model.password.PasswordMethodArgumentResolver;
import yachosan.infra.model.password.PasswordSerializer;
import yachosan.infra.model.proposeddate.ProposedDateDeserializer;
import yachosan.infra.model.proposeddate.ProposedDateKeyDeserializer;
import yachosan.infra.model.proposeddate.ProposedDateSerializer;
import yachosan.infra.model.scheduleid.ScheduleIdConverter;
import yachosan.infra.model.scheduleid.ScheduleIdDeserializer;
import yachosan.infra.model.scheduleid.ScheduleIdKeyDeserializer;
import yachosan.infra.model.scheduleid.ScheduleIdSerializer;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Configuration
public class AppConfig {
    @Autowired
    DataSourceProperties dataSourceProperties;
    @Autowired
    HttpMapperProperties httpMapperProperties;
    DataSource dataSource;

    @ConfigurationProperties(prefix = DataSourceAutoConfiguration.CONFIGURATION_PREFIX)
    @Bean
    DataSource realDataSource() throws URISyntaxException {
        String url;
        String username;
        String password;

        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl != null) {
            URI dbUri = new URI(databaseUrl);
            url = "jdbc:postgresql://" + dbUri.getHost() + ":" + dbUri.getPort() + dbUri.getPath();
            username = dbUri.getUserInfo().split(":")[0];
            password = dbUri.getUserInfo().split(":")[1];
        } else {
            url = this.dataSourceProperties.getUrl();
            username = this.dataSourceProperties.getUsername();
            password = this.dataSourceProperties.getPassword();
        }

        DataSourceBuilder factory = DataSourceBuilder
                .create(this.dataSourceProperties.getClassLoader())
                .url(url)
                .username(username)
                .password(password);
        this.dataSource = factory.build();
        return this.dataSource;
    }

    @Bean
    DataSource dataSource() {
        return new DataSourceSpy(this.dataSource);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return Password.DEFAULT_PASSWORD_ENCODER;
    }

    @Bean
    JSR310Module jsr310Module() {
        return new JSR310Module();
    }

    @Bean
    Module yachosanModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(ScheduleId.class, new ScheduleIdSerializer());
        module.addDeserializer(ScheduleId.class, new ScheduleIdDeserializer());
        module.addKeyDeserializer(ScheduleId.class, new ScheduleIdKeyDeserializer());

        module.addSerializer(ProposedDate.class, new ProposedDateSerializer());
        module.addDeserializer(ProposedDate.class, new ProposedDateDeserializer());
        module.addKeyDeserializer(ProposedDate.class, new ProposedDateKeyDeserializer());

        module.addSerializer(Password.class, new PasswordSerializer());
        module.addDeserializer(Password.class, new PasswordDeserializer());
        return module;
    }

    @Bean
    ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        if (this.httpMapperProperties.isJsonSortKeys()) {
            objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS,
                    true);
        }
        // customize
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }

    @Bean
    DozerBeanMapperFactoryBean dozerMapper() throws Exception {
        DozerBeanMapperFactoryBean factoryBean = new DozerBeanMapperFactoryBean();
        ResourceArrayPropertyEditor editor = new ResourceArrayPropertyEditor();
        editor.setAsText("classpath*:/dozer/**/*.xml");
        factoryBean.setMappingFiles((Resource[]) editor.getValue());
        return factoryBean;
    }

    @Bean
    ScheduleIdConverter scheduleIdConverter() {
        return new ScheduleIdConverter();
    }


    @Configuration
    public static class WebConfig extends WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter {
        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
            argumentResolvers.add(new PasswordMethodArgumentResolver());
            argumentResolvers.add(new AikotobaMethodArgumentResolver());
        }
    }
}
