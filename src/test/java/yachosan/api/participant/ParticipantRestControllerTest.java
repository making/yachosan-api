package yachosan.api.participant;

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
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
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
    @Autowired
    ObjectMapper objectMapper;

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
        hanako.setPassword(new Password.UnmaskedPassword("12345678").encode().get());

        participantRepository.save(Arrays.asList(tarou, hanako));

        apiEndpoint = "http://localhost:" + port + "/api/v1/schedules/" + scheduleId.getValue() + "/participants";

        ((MappingJackson2HttpMessageConverter) restTemplate.getMessageConverters()
                .stream()
                .filter(x -> x instanceof MappingJackson2HttpMessageConverter)
                .findFirst()
                .get()).setObjectMapper(objectMapper);
        hanako.setPassword(Password.MASKED);
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
    public void testPostParticipants_withPassword() throws Exception {
        YParticipant yamada = new YParticipant();
        yamada.setComment("多分いけます");
        yamada.setParticipantPk(new ParticipantPk(null, "yamada"));
        yamada.setReplies(new LinkedHashMap<ProposedDate, Reply>() {{
            put(ProposedDate.fromString("2014-08-01"), Reply.MAYBE);
            put(ProposedDate.fromString("2014-08-02"), Reply.MAYBE);
            put(ProposedDate.fromString("2014-08-03"), Reply.MAYBE);
        }});
        yamada.setPassword(new Password.UnmaskedPassword("hogehoge"));

        ResponseEntity<YParticipant> response = restTemplate.exchange(apiEndpoint,
                HttpMethod.POST, new HttpEntity<>(yamada), YParticipant.class);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        YParticipant created = response.getBody();
        yamada.getParticipantPk().setScheduleId(scheduleId);
        yamada.setPassword(Password.MASKED); // password will be hidden

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
    public void testPutParticipant_withAuthorization() throws Exception {
        YParticipant newHanako = new YParticipant();
        newHanako.setComment("楽しみです！");
        newHanako.setParticipantPk(new ParticipantPk(scheduleId, "hanako"));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer 12345678");
        ResponseEntity<YParticipant> response = restTemplate.exchange(
                apiEndpoint + "/{nickname}", HttpMethod.PUT, new HttpEntity<>(newHanako, headers),
                YParticipant.class, Collections.singletonMap("nickname", "hanako"));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        YParticipant updated = response.getBody();

        newHanako.setEmail(hanako.getEmail());
        newHanako.setPassword(hanako.getPassword());
        newHanako.setReplies(hanako.getReplies());

        assertThat(updated, is(newHanako));
    }


    @Test
    public void testPutParticipant_withAuthorizationTwice() throws Exception {
        {
            YParticipant newHanako = new YParticipant();
            newHanako.setComment("楽しみです！");
            newHanako.setParticipantPk(new ParticipantPk(scheduleId, "hanako"));

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer 12345678");
            ResponseEntity<YParticipant> response = restTemplate.exchange(
                    apiEndpoint + "/{nickname}", HttpMethod.PUT, new HttpEntity<>(newHanako, headers),
                    YParticipant.class, Collections.singletonMap("nickname", "hanako"));
            assertThat(response.getStatusCode(), is(HttpStatus.OK));
            YParticipant updated = response.getBody();

            newHanako.setEmail(hanako.getEmail());
            newHanako.setPassword(hanako.getPassword());
            newHanako.setReplies(hanako.getReplies());

            assertThat(updated, is(newHanako));
        }

        {
            YParticipant newHanako = new YParticipant();
            newHanako.setComment("楽しみです！");
            newHanako.setParticipantPk(new ParticipantPk(scheduleId, "hanako"));

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer 12345678");
            ResponseEntity<YParticipant> response = restTemplate.exchange(
                    apiEndpoint + "/{nickname}", HttpMethod.PUT, new HttpEntity<>(newHanako, headers),
                    YParticipant.class, Collections.singletonMap("nickname", "hanako"));
            assertThat(response.getStatusCode(), is(HttpStatus.OK));
            YParticipant updated = response.getBody();

            newHanako.setEmail(hanako.getEmail());
            newHanako.setPassword(hanako.getPassword());
            newHanako.setReplies(hanako.getReplies());

            assertThat(updated, is(newHanako));
        }
    }


    @Test
    public void testPutParticipant_withAuthorizationAndPasswordChange() throws Exception {
        {
            YParticipant newHanako = new YParticipant();
            newHanako.setComment("楽しみです！");
            newHanako.setParticipantPk(new ParticipantPk(scheduleId, "hanako"));
            newHanako.setPassword(new Password.UnmaskedPassword("abcdefgh"));

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer 12345678");
            ResponseEntity<YParticipant> response = restTemplate.exchange(
                    apiEndpoint + "/{nickname}", HttpMethod.PUT, new HttpEntity<>(newHanako, headers),
                    YParticipant.class, Collections.singletonMap("nickname", "hanako"));
            assertThat(response.getStatusCode(), is(HttpStatus.OK));
            YParticipant updated = response.getBody();

            newHanako.setEmail(hanako.getEmail());
            newHanako.setPassword(hanako.getPassword());
            newHanako.setReplies(hanako.getReplies());

            assertThat(updated, is(newHanako));
        }

        {
            YParticipant newHanako = new YParticipant();
            newHanako.setComment("楽しみです！");
            newHanako.setParticipantPk(new ParticipantPk(scheduleId, "hanako"));

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer 12345678");
            ResponseEntity<YParticipant> response = restTemplate.exchange(
                    apiEndpoint + "/{nickname}", HttpMethod.PUT, new HttpEntity<>(newHanako, headers),
                    YParticipant.class, Collections.singletonMap("nickname", "hanako"));
            assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
        }

        {
            YParticipant newHanako = new YParticipant();
            newHanako.setComment("楽しみです！");
            newHanako.setParticipantPk(new ParticipantPk(scheduleId, "hanako"));

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer abcdefgh");
            ResponseEntity<YParticipant> response = restTemplate.exchange(
                    apiEndpoint + "/{nickname}", HttpMethod.PUT, new HttpEntity<>(newHanako, headers),
                    YParticipant.class, Collections.singletonMap("nickname", "hanako"));
            assertThat(response.getStatusCode(), is(HttpStatus.OK));
            YParticipant updated = response.getBody();

            newHanako.setEmail(hanako.getEmail());
            newHanako.setPassword(hanako.getPassword());
            newHanako.setReplies(hanako.getReplies());

            assertThat(updated, is(newHanako));
        }
    }

    @Test
    public void testPutParticipant_withWrongAuthorization() throws Exception {
        YParticipant newHanako = new YParticipant();
        newHanako.setComment("楽しみです！");
        newHanako.setParticipantPk(new ParticipantPk(scheduleId, "hanako"));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer abcdefg");
        ResponseEntity<YParticipant> response = restTemplate.exchange(
                apiEndpoint + "/{nickname}", HttpMethod.PUT, new HttpEntity<>(newHanako, headers),
                YParticipant.class, Collections.singletonMap("nickname", "hanako"));
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }


    @Test
    public void testPutParticipant_withoutAuthorization() throws Exception {
        YParticipant newHanako = new YParticipant();
        newHanako.setComment("楽しみです！");
        newHanako.setParticipantPk(new ParticipantPk(scheduleId, "hanako"));

        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<YParticipant> response = restTemplate.exchange(
                apiEndpoint + "/{nickname}", HttpMethod.PUT, new HttpEntity<>(newHanako, headers),
                YParticipant.class, Collections.singletonMap("nickname", "hanako"));
        assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
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

    @Test
    public void testDeleteParticipant() throws Exception {
        {
            ResponseEntity<YParticipant> response = restTemplate.exchange(
                    apiEndpoint + "/{nickname}", HttpMethod.DELETE, null /* body,header */,
                    YParticipant.class, Collections.singletonMap("nickname", "tarou"));
            assertThat(response.getStatusCode(), is(HttpStatus.NO_CONTENT));
        }

        assertThat(restTemplate.exchange(
                apiEndpoint, HttpMethod.GET, null /* body,header */,
                new ParameterizedTypeReference<List<YParticipant>>() {
                }).getBody().size(), is(1));

        {
            ResponseEntity<YParticipant> response = restTemplate.exchange(
                    apiEndpoint + "/{nickname}", HttpMethod.DELETE, null /* body,header */,
                    YParticipant.class, Collections.singletonMap("nickname", "tarou"));
            assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
        }
    }

    @Test
    public void testDeleteParticipant_NotFound() throws Exception {
        ResponseEntity<YParticipant> response = restTemplate.exchange(
                apiEndpoint + "/{nickname}", HttpMethod.DELETE, null /* body,header */,
                YParticipant.class, Collections.singletonMap("nickname", "foobar"));
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }


    @Test
    public void testDeleteParticipant_withAuthorization() throws Exception {
        {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer 12345678");
            ResponseEntity<YParticipant> response = restTemplate.exchange(
                    apiEndpoint + "/{nickname}", HttpMethod.DELETE, new HttpEntity<>(headers),
                    YParticipant.class, Collections.singletonMap("nickname", "hanako"));

            assertThat(response.getStatusCode(), is(HttpStatus.NO_CONTENT));
        }

        assertThat(restTemplate.exchange(
                apiEndpoint, HttpMethod.GET, null /* body,header */,
                new ParameterizedTypeReference<List<YParticipant>>() {
                }).getBody().size(), is(1));

        {
            ResponseEntity<YParticipant> response = restTemplate.exchange(
                    apiEndpoint + "/{nickname}", HttpMethod.DELETE, null /* body,header */,
                    YParticipant.class, Collections.singletonMap("nickname", "hanako"));
            assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
        }
    }

    @Test
    public void testDeleteParticipant_withWrongAuthorization() throws Exception {
        {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer abcdef");
            ResponseEntity<YParticipant> response = restTemplate.exchange(
                    apiEndpoint + "/{nickname}", HttpMethod.DELETE, new HttpEntity<>(headers),
                    YParticipant.class, Collections.singletonMap("nickname", "hanako"));

            assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
        }

        assertThat(restTemplate.exchange(
                apiEndpoint, HttpMethod.GET, null /* body,header */,
                new ParameterizedTypeReference<List<YParticipant>>() {
                }).getBody().size(), is(2));
    }

    @Test
    public void testDeleteParticipant_withoutAuthorization() throws Exception {
        {
            HttpHeaders headers = new HttpHeaders();
            ResponseEntity<YParticipant> response = restTemplate.exchange(
                    apiEndpoint + "/{nickname}", HttpMethod.DELETE, new HttpEntity<>(headers),
                    YParticipant.class, Collections.singletonMap("nickname", "hanako"));

            assertThat(response.getStatusCode(), is(HttpStatus.UNAUTHORIZED));
        }

        assertThat(restTemplate.exchange(
                apiEndpoint, HttpMethod.GET, null /* body,header */,
                new ParameterizedTypeReference<List<YParticipant>>() {
                }).getBody().size(), is(2));
    }

}