package ru.site.web.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.io.PrintWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class RideFlowIntegrationTest {

  private static String authData =
      """
      {
      	"login": "dfvdfv",
      	"password": "12345"
      }
      """;

  private static String authData2 =
      """
      {
      	"login": "fvdvazsdcxd",
      	"password": "12345"
      }
      """;

  private final String clientData =
      """
      {
      	"firstName": "Иван",
      	"lastName": "Петров",
      	"phoneNumber": "+79001234567",
      	"email": "ivan.petrov@example.com"
      }
      """;

  private final String driverData =
      """
      {
      	"carModel": "Toyota Camry",
      	"carNumber": "А123ВС777",
      	"status": "FREE",
      	"latitude": 59.9311,
      	"longitude": 30.3609
      }
      """;

  private final String rideData =
      """
             {
                 "rideStatus": "REQUESTED",
                 "startLatitude": 59.9342802,
                 "startLongitude": 30.3350986,
                 "endLatitude": 59.9700000,
                 "endLongitude": 30.4100000
             }
      """;

  @Autowired private MockMvc mockMvc;

  @Test
  @Transactional
  @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
  public void shouldRegisterClientWhenAuthorized() throws Exception {
    mockMvc.perform(
        post("/register").contentType(MediaType.APPLICATION_JSON).content(authData).with(csrf()));

    MvcResult authResult =
        mockMvc
            .perform(post("/auth").contentType(MediaType.APPLICATION_JSON).content(authData))
            .andReturn();

    String responseBody = authResult.getResponse().getContentAsString();
    String token = JsonPath.read(responseBody, "$.accessToken");

    mockMvc
        .perform(
            post("/clients")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(clientData))
        .andExpect(status().isOk());
  }

  @Test
  @Transactional
  @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
  public void shouldCreateDriverWhenAuthorized() throws Exception {
    mockMvc.perform(
        post("/register").contentType(MediaType.APPLICATION_JSON).content(authData).with(csrf()));

    MvcResult authResult =
        mockMvc
            .perform(post("/auth").contentType(MediaType.APPLICATION_JSON).content(authData))
            .andReturn();
    String responseBody = authResult.getResponse().getContentAsString();
    String token = JsonPath.read(responseBody, "$.accessToken");

    mockMvc
        .perform(
            post("/drivers")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(driverData))
        .andExpect(status().isOk());
  }

  @Test
  @Transactional
  @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
  public void shouldCreateRideAndVerifyStatusWhenKafkaProcessing() throws Exception {

    mockMvc.perform(
        post("/register").contentType(MediaType.APPLICATION_JSON).content(authData).with(csrf()));

    MvcResult authResult =
        mockMvc
            .perform(post("/auth").contentType(MediaType.APPLICATION_JSON).content(authData))
            .andReturn();

    String responseBody = authResult.getResponse().getContentAsString();
    String token = JsonPath.read(responseBody, "$.accessToken");

    mockMvc
        .perform(
            post("/clients/rides")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .content(rideData))
        .andExpect(status().isOk());

    Thread.sleep(30000);

    MvcResult finalResult =
        mockMvc
            .perform(
                get("/clients/rides/current")
                    .header("Authorization", "Bearer " + token)
                    .contentType("application/json"))
            .andExpect(status().isOk())
            .andReturn();

    String finalStatusBody = finalResult.getResponse().getContentAsString();
    String finalStatus = JsonPath.read(finalStatusBody, "$.status");
    assertTrue(finalStatus.equals("REQUESTED"));
  }

  @Test
  @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
  public void shouldCreateDriverAndRideThenVerifyStatusAfterKafka() throws Exception {

    mockMvc.perform(
        post("/register").contentType(MediaType.APPLICATION_JSON).content(authData2).with(csrf()));

    MvcResult authResult =
        mockMvc
            .perform(post("/auth").contentType(MediaType.APPLICATION_JSON).content(authData2))
            .andReturn();

    String responseBody = authResult.getResponse().getContentAsString();
    String token = JsonPath.read(responseBody, "$.accessToken");

    mockMvc
        .perform(
            post("/drivers")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(driverData))
        .andExpect(status().isOk());

    mockMvc
        .perform(
            post("/clients/rides")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .content(rideData))
        .andExpect(status().isOk());

    Thread.sleep(50000);

    MvcResult finalResult =
        mockMvc
            .perform(
                get("/clients/rides/current")
                    .header("Authorization", "Bearer " + token)
                    .contentType("application/json"))
            .andExpect(status().isOk())
            .andReturn();

    String finalStatusBody = finalResult.getResponse().getContentAsString();
    String finalStatus = JsonPath.read(finalStatusBody, "$.status");

    PrintWriter out = new PrintWriter("dsc.txt");
    out.print(finalStatus);
    out.close();
    assertTrue(finalStatus.equals("ASSIGNED") || finalStatus.equals("IN_PROGRESS"));
  }

  @Test
  @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
  public void shouldCreateRideThenAssignDriverAndVerifyStatusAfterKafka() throws Exception {

    mockMvc.perform(
        post("/register").contentType(MediaType.APPLICATION_JSON).content(authData2).with(csrf()));

    MvcResult authResult =
        mockMvc
            .perform(post("/auth").contentType(MediaType.APPLICATION_JSON).content(authData2))
            .andReturn();

    String responseBody = authResult.getResponse().getContentAsString();
    String token = JsonPath.read(responseBody, "$.accessToken");

    mockMvc
        .perform(
            post("/clients/rides")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .content(rideData))
        .andExpect(status().isOk());

    Thread.sleep(50000);

    mockMvc
        .perform(
            post("/drivers")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(driverData))
        .andExpect(status().isOk());

    mockMvc
        .perform(
            post("/drivers/rides").header("Authorization", "Bearer " + token).content(rideData))
        .andExpect(status().isOk());

    Thread.sleep(50000);

    MvcResult finalResult =
        mockMvc
            .perform(
                get("/clients/rides/current")
                    .header("Authorization", "Bearer " + token)
                    .contentType("application/json"))
            .andExpect(status().isOk())
            .andReturn();

    String finalStatusBody = finalResult.getResponse().getContentAsString();
    String finalStatus = JsonPath.read(finalStatusBody, "$.status");
    assertTrue(finalStatus.equals("ASSIGNED") || finalStatus.equals("IN_PROGRESS"));
  }
}
