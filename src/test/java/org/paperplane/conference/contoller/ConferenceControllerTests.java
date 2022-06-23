package org.paperplane.conference.contoller;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.paperplane.conference.WithMockCustomUser;
import org.paperplane.conference.api.request.ConferenceSeatsAvailabilityResponse;
import org.paperplane.conference.api.request.CreateConferenceRequest;
import org.paperplane.conference.api.response.ConferenceResponse;
import org.paperplane.conference.api.response.UserResponse;
import org.paperplane.conference.exception.NotFoundException;
import org.paperplane.conference.model.Conference;
import org.paperplane.conference.model.Role;
import org.paperplane.conference.model.User;
import org.paperplane.conference.security.CustomAuthentication;
import org.paperplane.conference.service.AuthorizationService;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ConferenceControllerTests {
    MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @MockBean
    ConferenceService conferenceService;

    @MockBean
    AuthorizationService authorizationService;

    ConferenceResponse conferenceResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        RestAssuredMockMvc.mockMvc(mockMvc);
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
        RestAssuredMockMvc.postProcessors(csrf().asHeader());

        conferenceResponse = ConferenceResponse.builder()
                .id(UUID.randomUUID().toString())
                .capacity(30)
                .participantsCount(10)
                .createdAt(ZonedDateTime.now())
                .createdBy(
                        UserResponse.builder()
                                .id(UUID.randomUUID().toString())
                                .displayedName("Admin")
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
    @DisplayName("createConference() tests")
    class CreateConferenceTests {

        @Test
        @WithMockCustomUser(id = "user-1", username = "user", roles = Role.USER)
        @DisplayName("It creates conference")
        void createConference() {
            when(conferenceService.createConference(any())).thenReturn(conferenceResponse);
            var createConferenceRequest = CreateConferenceRequest.builder()
                    .name("Test conference")
                    .capacity(10)
                    .usersIds(List.of())
                    .build();

            RestAssuredMockMvc
                    .given()
                        .contentType("application/json")
                        .body(createConferenceRequest)
                    .when()
                        .post("/api/v1/conferences")
                    .then()
                        .status(HttpStatus.CREATED)
                        .body("id", equalTo(conferenceResponse.getId()))
                        .body("capacity", equalTo(conferenceResponse.getCapacity()))
                        .body("participantsCount", equalTo(conferenceResponse.getParticipantsCount()));
        }

        @Test
        @WithMockCustomUser(id = "user-1", username = "user", roles = Role.USER)
        @DisplayName("It returns 400 error when request is invalid")
        void createConference_whenRequestIsInvalid() {
            var createConferenceRequest = CreateConferenceRequest.builder()
                    .capacity(null)
                    .name("")
                    .build();

            RestAssuredMockMvc
                    .given()
                        .contentType("application/json")
                        .body(createConferenceRequest)
                    .when()
                        .post("/api/v1/conferences")
                    .then()
                        .status(HttpStatus.BAD_REQUEST);
        }

        @Test
        @DisplayName("It returns 401 error when no authentication supplied")
        void createConference_withNoAuthentication() {
            var createConferenceRequest = CreateConferenceRequest.builder()
                    .name("Test conference")
                    .capacity(10)
                    .usersIds(List.of())
                    .build();

            RestAssuredMockMvc
                    .given()
                        .contentType("application/json")
                        .body(createConferenceRequest)
                    .when()
                        .post("/api/v1/conferences")
                    .then()
                        .status(HttpStatus.UNAUTHORIZED);
        }
    }

    @Nested
    @DisplayName("cancelConference() tests")
    class CancelConferenceTests {

        @Test
        @WithMockCustomUser(id = "user-1", username = "user", roles = Role.USER)
        @DisplayName("It cancels conference when current user is creator of conference")
        void cancelConference_whenUserIsConferenceCreator() {
            when(conferenceService.getConferenceEntityById(anyString())).thenReturn(setUpConference("user-1"));
            doNothing().when(conferenceService).cancelConference(anyString());

            RestAssuredMockMvc
                    .given()
                    .when()
                        .delete("/api/v1/conferences/conference-id")
                    .then()
                        .status(HttpStatus.NO_CONTENT);
        }

        @Test
        @WithMockCustomUser(id = "user-1", username = "user", roles = {Role.USER, Role.ADMIN})
        @DisplayName("It cancels conference when current user is admin")
        void cancelConference_whenCurrentUserIsAdmin() {
            when(conferenceService.getConferenceEntityById(anyString())).thenReturn(setUpConference("user-2"));
            doNothing().when(conferenceService).cancelConference(anyString());

            RestAssuredMockMvc
                    .given()
                    .when()
                        .delete("/api/v1/conferences/conference-id")
                    .then()
                        .status(HttpStatus.NO_CONTENT);
        }

        @Test
        @WithMockCustomUser(id = "user-1", username = "user", roles = Role.USER)
        @DisplayName("It returns 403 error when current user is neither admin nor creator of conference")
        void cancelConference_returnsForbidden_whenCurrentUserIsNeitherAdminNorConferenceCreator() {
            when(conferenceService.getConferenceEntityById(anyString())).thenReturn(setUpConference("user-2"));

            RestAssuredMockMvc
                    .given()
                    .when()
                        .delete("/api/v1/conferences/conference-id")
                    .then()
                        .status(HttpStatus.FORBIDDEN);
        }

        @Test
        @WithMockCustomUser(id = "user-1", username = "user", roles = Role.USER)
        @DisplayName("It returns 404 error when conference does not exists")
        void cancelConference_returnsNotFound_whenConferenceDoesNotExists() {
            when(conferenceService.getConferenceEntityById(anyString())).thenThrow(new NotFoundException());

            RestAssuredMockMvc
                    .given()
                    .when()
                        .delete("/api/v1/conferences/conference-id")
                    .then()
                        .status(HttpStatus.NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("checkConferenceSeatsAvailability() tests")
    class CheckConferenceSeatsAvailabilityTests {

        @Test
        @DisplayName("It returns conference seats availability")
        void checkConferenceSeatsAvailability() {
            when(conferenceService.checkConferenceSeatsAvailability(anyString())).thenReturn(new ConferenceSeatsAvailabilityResponse(true));

            RestAssuredMockMvc
                    .given()
                    .when()
                        .get("/api/v1/conferences/conference-id/has-seats")
                    .then()
                        .status(HttpStatus.OK)
                        .body("available", equalTo(true));
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
