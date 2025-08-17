package ru.site.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerTest {

  private static String authData =
      """
      {
      	"login": "fdszsdcsd",
      	"password": "12345"
      }
      """;

  @Autowired private MockMvc mockMvc;

  @Test
  @Transactional
  @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
  public void testRegisterSuccess() throws Exception {
    mockMvc
        .perform(post("/register").contentType(MediaType.APPLICATION_JSON).content(authData))
        .andExpect(status().isCreated());
  }

  @Test
  @Transactional
  @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
  public void testRegisterFailUserAlreadyExists() throws Exception {

    mockMvc
        .perform(post("/register").contentType(MediaType.APPLICATION_JSON).content(authData))
        .andExpect(status().isCreated());

    mockMvc
        .perform(post("/register").contentType(MediaType.APPLICATION_JSON).content(authData))
        .andExpect(status().isInternalServerError());
  }
}
