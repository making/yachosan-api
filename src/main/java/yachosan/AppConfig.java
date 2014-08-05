package yachosan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import net.sf.log4jdbc.Log4jdbcProxyDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.web.HttpMapperProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import yachosan.infra.converter.ScheduleIdConverter;

import javax.sql.DataSource;

@Configuration
public class AppConfig {
    @Autowired
    DataSourceProperties dataSourceProperties;
    @Autowired
    HttpMapperProperties httpMapperProperties;
    DataSource dataSource;

    @ConfigurationProperties(prefix = DataSourceAutoConfiguration.CONFIGURATION_PREFIX)
    @Bean
    DataSource realDataSource() {
        DataSourceBuilder factory = DataSourceBuilder
                .create(this.dataSourceProperties.getClassLoader())
                .url(this.dataSourceProperties.getUrl())
                .username(this.dataSourceProperties.getUsername())
                .password(this.dataSourceProperties.getPassword());
        this.dataSource = factory.build();
        return this.dataSource;
    }

    @Bean
    DataSource dataSource() {
        return new Log4jdbcProxyDataSource(this.dataSource);
    }

    @Bean
    JSR310Module jsr310Module() {
        return new JSR310Module();
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
    ScheduleIdConverter scheduleIdConverter() {
        return new ScheduleIdConverter();
    }
}
