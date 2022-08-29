package flab.resellPlatform.controller.brand;

import com.fasterxml.jackson.databind.ObjectMapper;
import flab.resellPlatform.MessageConfig;
import flab.resellPlatform.SecurityConfig;
import flab.resellPlatform.common.TestUtil;
import flab.resellPlatform.common.response.StandardResponse;
import flab.resellPlatform.domain.brand.BrandDTO;
import flab.resellPlatform.service.brand.BrandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BrandController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))
@Import(MessageConfig.class)
@WithMockUser(roles = {"ADMIN"})
class BrandControllerTest {

    @Autowired
    MessageSourceAccessor messageSourceAccessor;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BrandService brandService;

    ObjectMapper mapper = new ObjectMapper();
    BrandDTO brandDTO;

    @BeforeEach
    void beforeEach() {
        brandDTO = BrandDTO.builder()
                .name("Nike")
                .build();
    }

    @DisplayName("브랜드 추가")
    @Test
    void addBrand() throws Exception {
        when(brandService.addBrand(any())).thenReturn(brandDTO);
        String brand = mapper.writeValueAsString(brandDTO);

        ResultActions resultActions = mockMvc.perform(post("/brands")
                .contentType(MediaType.APPLICATION_JSON)
                .content(brand)
                .with(csrf()));

        resultActions.andExpect(status().is2xxSuccessful());
    }

    @DisplayName("브랜드 업데이트")
    @Test
    void updateBrand() throws Exception {
        when(brandService.updateBrand(eq(1L), any())).thenReturn(true);

        String brand = mapper.writeValueAsString(brandDTO);

        ResultActions resultActions = mockMvc.perform(put("/brands/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(brand)
                .with(csrf()));

//        resultActions.andExpect(status().is2xxSuccessful());
        StandardResponse standardResponse = StandardResponse.builder()
                .message(messageSourceAccessor.getMessage("brand.update.success"))
                .build();

        TestUtil.expectDefaultResponse(mapper, standardResponse, status().is2xxSuccessful(), resultActions);
    }

    @DisplayName("브랜드 삭제")
    @Test
    void deleteBrand() throws Exception {
        when(brandService.deleteBrand(1L)).thenReturn(true);

        ResultActions resultActions = mockMvc.perform(delete("/brands/1"));

        resultActions.andExpect(status().is2xxSuccessful());
    }
}