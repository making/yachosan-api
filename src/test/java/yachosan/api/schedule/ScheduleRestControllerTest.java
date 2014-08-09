package yachosan.api.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;
import yachosan.App;
import yachosan.domain.model.*;
import yachosan.domain.repository.participant.ParticipantRepository;
import yachosan.domain.repository.schedule.ScheduleRepository;
import yachosan.domain.repository.schedule.ScheduleSummary;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@WebAppConfiguration
@IntegrationTest
public class ScheduleRestControllerTest {
    @Autowired
    ParticipantRepository participantRepository;
    @Autowired
    ScheduleRepository scheduleRepository;
    @Autowired
    ObjectMapper objectMapper;

    @Value("${local.server.port}")
    int port;
    String apiEndpoint;
    RestTemplate restTemplate = new TestRestTemplate();

    ScheduleId scheduleId;
    YSchedule nomikai;
    YParticipant tarou;
    YParticipant hanako;

    @Before
    public void setUp() {
        scheduleRepository.deleteAll();

        nomikai = new YSchedule();
        nomikai.setScheduleName("飲み会");
        nomikai.setScheduleDescription("打ち上げ");
        nomikai.setProposedDates(Arrays.asList(
                ProposedDate.fromString("2014-08-01"),
                ProposedDate.fromString("2014-08-02"),
                ProposedDate.fromString("2014-08-03")));
        nomikai.setCreatedAt(LocalDateTime.now());
        nomikai.setUpdatedAt(LocalDateTime.now());
        scheduleRepository.save(nomikai);
        scheduleId = nomikai.getScheduleId();

        tarou = new YParticipant();
        tarou.setComment("よろしくお願いします！");
        tarou.setEmail("tarou@example.com");
        tarou.setParticipantPk(new ParticipantPk(scheduleId, "tarou"));
        tarou.setReplies(new LinkedHashMap<ProposedDate, Reply>() {{
            put(ProposedDate.fromString("2014-08-01"), Reply.OK);
            put(ProposedDate.fromString("2014-08-02"), Reply.OK);
            put(ProposedDate.fromString("2014-08-03"), Reply.MAYBE);
        }});

        hanako = new YParticipant();
        hanako.setParticipantPk(new ParticipantPk(scheduleId, "hanako"));
        hanako.setReplies(new LinkedHashMap<ProposedDate, Reply>() {{
            put(ProposedDate.fromString("2014-08-01"), Reply.NG);
            put(ProposedDate.fromString("2014-08-02"), Reply.OK);
            put(ProposedDate.fromString("2014-08-03"), Reply.NG);
        }});

        participantRepository.save(Arrays.asList(tarou, hanako));

        scheduleId = nomikai.getScheduleId();
        apiEndpoint = "http://localhost:" + port + "/api/v1/schedules";

        ((MappingJackson2HttpMessageConverter) restTemplate.getMessageConverters()
                .stream()
                .filter(x -> x instanceof MappingJackson2HttpMessageConverter)
                .findFirst()
                .get()).setObjectMapper(objectMapper);
    }

    @Test
    public void testGetSchedules() throws Exception {
        ResponseEntity<List<ScheduleSummary>> response = restTemplate.exchange(
                apiEndpoint, HttpMethod.GET, null /* body,header */,
                new ParameterizedTypeReference<List<ScheduleSummary>>() {
                });
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().size(), is(1));

        ScheduleSummary s0 = response.getBody().get(0);
        ScheduleSummary nomikaiSummary = new ScheduleSummary(nomikai.getScheduleId(),
                nomikai.getScheduleName(),
                nomikai.getScheduleDescription(),
                nomikai.getCreatedAt(),
                nomikai.getUpdatedAt());

        assertThat(s0, is(nomikaiSummary));
    }


    @Test
    public void testGetSchedule() throws Exception {
        ResponseEntity<YSchedule> response = restTemplate.exchange(
                apiEndpoint + "/{scheduleId}", HttpMethod.GET, null /* body,header */,
                YSchedule.class, Collections.singletonMap("scheduleId", scheduleId));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        nomikai.setParticipants(Arrays.asList(hanako, tarou));
        assertThat(response.getBody(), is(nomikai));
    }

}