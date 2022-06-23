package org.paperplane.conference.contoller;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.paperplane.conference.WithMockCustomUser;
import org.paperplane.conference.api.request.CreateConferenceParticipantRequest;
import org.paperplane.conference.api.response.ConferenceParticipantResponse;
import org.paperplane.conference.api.response.UserResponse;
import org.paperplane.conference.exception.ConferenceIsFullException;
import org.paperplane.conference.model.Conference;
import org.paperplane.conference.model.Role;
import org.paperplane.conference.model.User;
import org.paperplane.conference.security.CustomAuthentication;
import org.paperplane.conference.service.AuthorizationService;
import org.paperplane.conference.service.ConferenceParticipantService;
import org.paperplane.conference.service.ConferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ConferenceParticipantControllerTests {
    MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @MockBean
    ConferenceParticipantService conferenceParticipantService;

    @MockBean
    ConferenceService conferenceService;

    @MockBean
    AuthorizationService authorizationService;

    ConferenceParticipantResponse conferenceParticipantResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        RestAssuredMockMvc.mockMvc(mockMvc);
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
        RestAssuredMockMvc.postProcessors(csrf().asHeader());

        conferenceParticipantResponse = ConferenceParticipantResponse.builder()
                .id(UUID.randomUUID().toString())
                .name("Test")
                .user(UserResponse.builder()
                        .id(UUID.randomUUID().toString())
                        .displayedName("Test")
                        .build()
                )
                .build();

        when(authorizationService.requireCurrentUserDetails()).thenReturn(
                Optional.ofNullable((CustomAuthentication) SecurityContextHolder.getContext().getAuthentication())
                        .map(CustomAuthentication::getUserDetails)
                        .orElse(null)
        );
    }

    @Nested
    @DisplayName("createConferenceParticipant() tests")
    class CreateConferenceParticipantTests {

        @Test
        @WithMockCustomUser(id = "user-1", username = "user", roles = Role.USER)
        @DisplayName("It creates conference participant when current user is conference creator")
        void createConferenceParticipant_whenCurrentUserIsConferenceCreator() {
            when(conferenceParticipantService.createConferenceParticipant(anyString(), any())).thenReturn(conferenceParticipantResponse);
            when(conferenceService.getConferenceEntityById(anyString())).thenReturn(setUpConference("user-1"));

            var createConferenceParticipantRequest = CreateConferenceParticipantRequest.builder()
                    .name("Test")
                    .userId("user-2")
                    .build();

            RestAssuredMockMvc
                    .given()
                        .contentType("application/json")
                        .body(createConferenceParticipantRequest)
                    .when()
                        .post("/api/v1/conferences/123/participants")
                    .then()
                        .status(HttpStatus.CREATED)
                        .body("id", equalTo(conferenceParticipantResponse.getId()))
                        .body("name", equalTo(conferenceParticipantResponse.getName()));
        }

        @Test
        @WithMockCustomUser(id = "user-2", username = "user", roles = Role.ADMIN)
        @DisplayName("It creates conference participant when current user is admin")
        void createConferenceParticipant_whenCurrentUserIsAdmin() {
            when(conferenceParticipantService.createConferenceParticipant(anyString(), any())).thenReturn(conferenceParticipantResponse);
            when(conferenceService.getConferenceEntityById(anyString())).thenReturn(setUpConference("user-1"));

            var createConferenceParticipantRequest = CreateConferenceParticipantRequest.builder()
                    .name("Test")
                    .userId("user-3")
                    .build();

            RestAssuredMockMvc
                    .given()
                        .contentType("application/json")
                        .body(createConferenceParticipantRequest)
                    .when()
                        .post("/api/v1/conferences/123/participants")
                    .then()
                        .status(HttpStatus.CREATED)
                        .body("id", equalTo(conferenceParticipantResponse.getId()))
                        .body("name", equalTo(conferenceParticipantResponse.getName()));
        }

        @Test
        @WithMockCustomUser(id = "user-2", username = "user", roles = Role.USER)
        @DisplayName("It returns 403 error when current user is neither admin nor conference creator")
        void creteConferenceParticipant_whenCurrentUserIsNeitherAdminNorConferenceCreator() {
            when(conferenceService.getConferenceEntityById(anyString())).thenReturn(setUpConference("user-1"));

            var createConferenceParticipantRequest = CreateConferenceParticipantRequest.builder()
                    .name("Test")
                    .userId("user-3")
                    .build();

            RestAssuredMockMvc
                    .given()
                        .contentType("application/json")
                        .body(createConferenceParticipantRequest)
                    .when()
                        .post("/api/v1/conferences/123/participants")
                    .then()
                        .status(HttpStatus.FORBIDDEN);
        }

        @Test
        @WithMockCustomUser(id = "user-1", username = "user", roles = Role.USER)
        @DisplayName("It returns 423 error when conference is full")
        void createConferenceParticipant_whenConferenceIsFull() {
            when(conferenceService.getConferenceEntityById(anyString())).thenReturn(setUpConference("user-1"));
            when(conferenceParticipantService.createConferenceParticipant(anyString(), any())).thenThrow(new ConferenceIsFullException());

            var createConferenceParticipantRequest = CreateConferenceParticipantRequest.builder()
                    .name("Test")
                    .userId("user-3")
                    .build();

            RestAssuredMockMvc
                    .given()
                        .contentType("application/json")
                        .body(createConferenceParticipantRequest)
                    .when()
                        .post("/api/v1/conferences/123/participants")
                    .then()
                        .status(HttpStatus.LOCKED);
        }
    }

    @Nested
    @DisplayName("deleteConferenceParticipant() tests")
    class DeleteConferenceParticipantTests {

        @Test
        @WithMockCustomUser(id = "user-1", username = "user", roles = Role.USER)
        @DisplayName("It deletes conference participant when current user is conference creator")
        void deleteConferenceParticipant_whenCurrentUserIsConferenceCreator() {
            when(conferenceService.getConferenceEntityById(anyString())).thenReturn(setUpConference("user-1"));

            RestAssuredMockMvc
                    .given()
                    .when()
                        .delete("/api/v1/conferences/123/participants/1")
                    .then()
                        .status(HttpStatus.NO_CONTENT);
        }

        @Test
        @WithMockCustomUser(id = "user-1", username = "user", roles = Role.ADMIN)
        @DisplayName("It deletes conference participant when current user is admin")
        void deleteConferenceParticipant_whenCurrentUserIsAdmin() {
            when(conferenceService.getConferenceEntityById(anyString())).thenReturn(setUpConference("user-2"));

            RestAssuredMockMvc.given()
                    .when()
                        .delete("/api/v1/conferences/123/participants/2")
                    .then()
                        .status(HttpStatus.NO_CONTENT);
        }

        @Test
        @WithMockCustomUser(id = "user-1", username = "user", roles = Role.USER)
        @DisplayName("It deletes conference participant when current user is admin")
        void deleteConferenceParticipant_whenCurrentUserIsNeitherAdminNorConferenceCreator() {
            when(conferenceService.getConferenceEntityById(anyString())).thenReturn(setUpConference("user-2"));

            RestAssuredMockMvc.
                    when()
                        .delete("/api/v1/conferences/123/participants/2")
                    .then()
                        .status(HttpStatus.FORBIDDEN);
        }
    }

    private Conference setUpConference(String creatorId) {
        var creator = User.builder().id(creatorId).build();

        return Conference.builder()
                .id(UUID.randomUUID().toString())
                .createdAt(ZonedDateTime.now())
                .createdBy(creator)
                .canceled(false)
                .build();
    }
}
