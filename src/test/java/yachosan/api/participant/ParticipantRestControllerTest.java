package yachosan.api.participant;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;
import yachosan.App;
import yachosan.domain.model.*;
import yachosan.domain.repository.participant.ParticipantRepository;
import yachosan.domain.repository.schedule.ScheduleRepository;

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
public class ParticipantRestControllerTest {
    @Autowired
    ParticipantRepository participantRepository;
    @Autowired
    ScheduleRepository scheduleRepository;

    @Value("${local.server.port}")
    int port;
    String apiEndpoint;
    RestTemplate restTemplate = new TestRestTemplate();

    YParticipant tarou;
    YParticipant hanako;
    ScheduleId scheduleId;

    @Before
    public void setUp() {
        scheduleRepository.deleteAll();
        participantRepository.deleteAll();

        YSchedule nomikai = new YSchedule();
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

        apiEndpoint = "http://localhost:" + port + "/api/v1/schedules/" + scheduleId.getValue() + "/participants";
    }

    @Test
    public void testGetParticipants() throws Exception {
        ResponseEntity<List<YParticipant>> response = restTemplate.exchange(
                apiEndpoint, HttpMethod.GET, null /* body,header */,
                new ParameterizedTypeReference<List<YParticipant>>() {
                });
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().size(), is(2));

        YParticipant p0 = response.getBody().get(0);
        YParticipant p1 = response.getBody().get(1);

        assertThat(p0, is(hanako));
        assertThat(p1, is(tarou));
    }

    @Test
    public void testPostParticipants() throws Exception {
        YParticipant yamada = new YParticipant();
        yamada.setComment("多分いけます");
        yamada.setParticipantPk(new ParticipantPk(null, "yamada"));
        yamada.setReplies(new LinkedHashMap<ProposedDate, Reply>() {{
            put(ProposedDate.fromString("2014-08-01"), Reply.MAYBE);
            put(ProposedDate.fromString("2014-08-02"), Reply.MAYBE);
            put(ProposedDate.fromString("2014-08-03"), Reply.MAYBE);
        }});

        ResponseEntity<YParticipant> response = restTemplate.exchange(apiEndpoint,
                HttpMethod.POST, new HttpEntity<>(yamada), YParticipant.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        YParticipant created = response.getBody();
        yamada.getParticipantPk().setScheduleId(scheduleId);

        assertThat(created, is(yamada));

        assertThat(restTemplate.exchange(
                apiEndpoint, HttpMethod.GET, null /* body,header */,
                new ParameterizedTypeReference<List<YParticipant>>() {
                }).getBody().size(), is(3));
    }

    @Test
    public void testPutParticipant_comment() throws Exception {
        YParticipant newTarou = new YParticipant();
        newTarou.setComment("楽しみです！");
        newTarou.setParticipantPk(new ParticipantPk(scheduleId, "tarou"));

        ResponseEntity<YParticipant> response = restTemplate.exchange(
                apiEndpoint + "/{nickname}", HttpMethod.PUT, new HttpEntity<>(newTarou),
                YParticipant.class, Collections.singletonMap("nickname", "tarou"));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        YParticipant updated = response.getBody();

        newTarou.setEmail(tarou.getEmail());
        newTarou.setPassword(tarou.getPassword());
        newTarou.setReplies(tarou.getReplies());

        assertThat(updated, is(newTarou));
        System.out.println(updated);

        assertThat(restTemplate.exchange(
                apiEndpoint, HttpMethod.GET, null /* body,header */,
                new ParameterizedTypeReference<List<YParticipant>>() {
                }).getBody().size(), is(2));
    }

    @Test
    public void testPutParticipant_comment_allReplies() throws Exception {
        YParticipant newTarou = new YParticipant();
        newTarou.setComment("やっぱ無理かも");
        newTarou.setParticipantPk(new ParticipantPk(scheduleId, "tarou"));
        newTarou.setReplies(new LinkedHashMap<ProposedDate, Reply>() {{
            put(ProposedDate.fromString("2014-08-01"), Reply.MAYBE);
            put(ProposedDate.fromString("2014-08-02"), Reply.NG);
            put(ProposedDate.fromString("2014-08-03"), Reply.NG);
        }});

        ResponseEntity<YParticipant> response = restTemplate.exchange(
                apiEndpoint + "/{nickname}", HttpMethod.PUT, new HttpEntity<>(newTarou),
                YParticipant.class, Collections.singletonMap("nickname", "tarou"));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        YParticipant updated = response.getBody();

        newTarou.setEmail(tarou.getEmail());
        newTarou.setPassword(tarou.getPassword());

        assertThat(updated, is(newTarou));
        System.out.println(updated);

        assertThat(restTemplate.exchange(
                apiEndpoint, HttpMethod.GET, null /* body,header */,
                new ParameterizedTypeReference<List<YParticipant>>() {
                }).getBody().size(), is(2));
    }

    @Test
    public void testPutParticipant_comment_partialReplies() throws Exception {
        YParticipant newTarou = new YParticipant();
        newTarou.setComment("厳しくなった");
        newTarou.setParticipantPk(new ParticipantPk(scheduleId, "tarou"));
        newTarou.setReplies(new LinkedHashMap<ProposedDate, Reply>() {{
            put(ProposedDate.fromString("2014-08-02"), Reply.NG);
            put(ProposedDate.fromString("2014-08-03"), Reply.NG);
        }});

        ResponseEntity<YParticipant> response = restTemplate.exchange(
                apiEndpoint + "/{nickname}", HttpMethod.PUT, new HttpEntity<>(newTarou),
                YParticipant.class, Collections.singletonMap("nickname", "tarou"));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        YParticipant updated = response.getBody();

        newTarou.setEmail(tarou.getEmail());
        newTarou.setPassword(tarou.getPassword());
        newTarou.setReplies(new LinkedHashMap<ProposedDate, Reply>() {{
            put(ProposedDate.fromString("2014-08-01"), Reply.OK);
            put(ProposedDate.fromString("2014-08-02"), Reply.NG);
            put(ProposedDate.fromString("2014-08-03"), Reply.NG);
        }});

        assertThat(updated, is(newTarou));
        System.out.println(updated);

        assertThat(restTemplate.exchange(
                apiEndpoint, HttpMethod.GET, null /* body,header */,
                new ParameterizedTypeReference<List<YParticipant>>() {
                }).getBody().size(), is(2));
    }

    @Test
    public void testGetParticipant() throws Exception {
        ResponseEntity<YParticipant> response = restTemplate.exchange(
                apiEndpoint + "/{nickname}", HttpMethod.GET, null /* body,header */,
                YParticipant.class, Collections.singletonMap("nickname", "tarou"));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(tarou));
    }

    @Test
    public void testGetParticipant_NotFound() throws Exception {
        ResponseEntity<YParticipant> response = restTemplate.exchange(
                apiEndpoint + "/{nickname}", HttpMethod.GET, null /* body,header */,
                YParticipant.class, Collections.singletonMap("nickname", "foobar"));
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

}