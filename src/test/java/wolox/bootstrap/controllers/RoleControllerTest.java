package wolox.bootstrap.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import wolox.bootstrap.models.Role;
import wolox.bootstrap.repositories.RoleRepository;
import wolox.bootstrap.repositories.UserRepository;

@WebAppConfiguration
@WebMvcTest(value = RoleController.class, secure = false)
@RunWith(SpringRunner.class)
public class RoleControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    RoleRepository roleRepository;

    @MockBean
    UserRepository userRepository;

    private Role role;
    private String roleStr, roleUpdateStr;

    @Before
    public void setUp() {
        roleStr = "{\"name\": \"roleName\"}";
        roleUpdateStr = "{\"name\": \"newRoleName\"}";
        role = new Role();
        role.setName("roleName");
        given(roleRepository.findByNameContainingAllIgnoreCase(""))
            .willReturn(Arrays.asList(role));
        given(roleRepository.findById(1)).willReturn(role);
    }

    @Test
    public void givenCreatedRole_whenViewRoles_listIsNotEmpty() throws Exception {
        mvc.perform(post("/api/roles/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(roleStr))
            .andExpect(status().isOk());
        mvc.perform(get("/api/roles/find")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name", is(role.getName())));
    }

    @Test
    public void givenUpdatedRole_whenViewRoles_roleIsUpdated() throws Exception {
        role.setName("newRoleName");
        mvc.perform(put("/api/roles/update")
            .contentType(MediaType.APPLICATION_JSON)
            .param("id", "1")
            .content(roleUpdateStr))
            .andExpect(status().isOk());
        mvc.perform(get("/api/roles/find")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name", is(role.getName())));
    }

    @Test
    public void givenDeletedRole_whenViewRoles_listIsEmpty() throws Exception {
        given(roleRepository.findByNameContainingAllIgnoreCase(""))
            .willReturn(Arrays.asList());
        mvc.perform(delete("/api/roles/delete")
            .contentType(MediaType.APPLICATION_JSON)
            .param("id", "1"));
        mvc.perform(get("/api/roles/find")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

}