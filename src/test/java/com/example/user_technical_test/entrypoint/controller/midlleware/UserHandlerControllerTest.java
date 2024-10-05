package com.example.user_technical_test.entrypoint.controller.midlleware;

import com.example.user_technical_test.infrastructure.repositories.UserRepository;
import com.example.user_technical_test.infrastructure.repositories.entities.UserEntity;
import com.example.user_technical_test.validator.UserValidator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class UserHandlerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository repository;

    private final UserEntity userTest = UserEntity.builder()
            .id(1L)
            .name("User Test")
            .email("emailteste@gmail.com")
            .password("senhateste123")
            .dateRegister(LocalDateTime.of(2024, 10, 5, 12, 23, 42, 414851900))
            .build();

    private final String USUARIO_JSON = "{\"name\": \"User Test\", \"email\":\"emailteste@gmail.com\", \"password\":\"senhateste123\"}";


    @Test
    void testeExceptionUserAlreadyRegistered() throws Exception {
        Mockito.when(repository.findByEmail(Mockito.any())).thenReturn(Optional.of(userTest));
        Mockito.when(repository.save(Mockito.any())).thenReturn(userTest);

        ResultActions resultadoRequisicao = mockMvc
                .perform(MockMvcRequestBuilders.post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(USUARIO_JSON));


        resultadoRequisicao.andExpect(MockMvcResultMatchers.status().isBadRequest());

        resultadoRequisicao.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Usuário com este email já cadastrado"));
    }

    @Test
    void testeExceptionUserNotFound() throws Exception {
        Mockito.when(repository.findById(Mockito.any())).thenReturn(Optional.empty());

        ResultActions resultadoRequisicao = mockMvc.perform(MockMvcRequestBuilders.get("/users/consult/{id}", userTest.getId()))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));


        resultadoRequisicao.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Usuário não encontrado"));
    }
}
