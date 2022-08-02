package pl.setlikD.restapi.star;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class StarControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StarService starService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<Star> prepirMockData() {
        List<Integer> numbers = IntStream.range(1, 100)
                .boxed()
                .collect(Collectors.toList());

        Collections.shuffle(numbers);
        return numbers.stream()
                .map(i -> new Star("STAR_" + i, i))
                .collect(Collectors.toList());
    }

    @DisplayName("Controller JUnit test for createStar method")
    @Test
    public void givenStarObj_whenCreateStar_thenReturnSavedStar() throws Exception {
        //given
        Star star = Star.builder().id(1L).name("STAR_100").distance(42).build();
        given(starService.createStar(any(Star.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));
        //when
        ResultActions response = mockMvc.perform(post("/api/v1/stars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(star)));
        //then
        response.andDo(print()).
                andExpect(status().isCreated())
                .andExpect(jsonPath("$.name",
                        is(star.getName())))
                .andExpect(jsonPath("$.distance",
                        is(star.getDistance()), Long.class));
    }

    @DisplayName("Controller JUnit test for getAllStarts method")
    @Test
    public void givenListOfStars_whenGetAllStars_thenReturnStarsList() throws Exception {
        //given
        List<Star> list = List.copyOf(prepirMockData());
        given(starService.getAllStarts()).willReturn(prepirMockData());
        //when
        ResultActions response = mockMvc.perform(get("/api/v1/stars"));
        //then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()",
                        is(list.size())));

    }

    @DisplayName("Controller JUnit test for getStarById method")
    @Test
    public void givenStarId_whenGetStarById_thenReturnStarObject() throws Exception {
        //given
        Long starId = 1L;
        Star star = Star.builder().name("STAR_122").distance(32).build();
        given(starService.getStarById(starId)).willReturn(Optional.of(star));
        //when
        ResultActions response = mockMvc.perform(get("/api/v1/stars/{id}", starId));
        //then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.name", is(star.getName())))
                .andExpect(jsonPath("$.distance", is(star.getDistance()), Long.class));
    }

    @DisplayName("Controller JUnit test for getStarById method negative scenario")
    @Test
    public void givenInvalidStarId_whenGetStarById_thenReturnEmpty() throws Exception {
        //given
        Long starId = 1L;
        given(starService.getStarById(starId)).willReturn(Optional.empty());
        //when
        ResultActions response = mockMvc.perform(get("/api/v1/stars/{id}", starId));
        //then
        response.andExpect(status().isNotFound())
                .andDo(print());
    }

    @DisplayName("Controller JUnit test for updateStar method")
    @Test
    public void givenUpdatedStar_whenUpdateStar_thenReturnUpdateStarObject() throws Exception {
        //given
        Long starId = 1L;
        Star savedStar = Star.builder().name("STAR_122").distance(32).build();
        Star updatedStar = Star.builder().name("Star_QWERTy").distance(67).build();
        given(starService.getStarById(starId)).willReturn(Optional.of(savedStar));
        given(starService.updateStar(any(Star.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));
        //when
        ResultActions response = mockMvc.perform(put("/api/v1/stars/{id}", starId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedStar)));
        //then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.name", is(updatedStar.getName())))
                .andExpect(jsonPath("$.distance", is(updatedStar.getDistance()), Long.class));
    }

    @DisplayName("Controller JUnit test for updateStar method negative scenario")
    @Test
    public void givenUpdatedStar_whenUpdateStar_thenReturnReturn404() throws Exception {
        //given
        Long starId = 1L;
        Star updatedStar = Star.builder().name("Star_QWERTy").distance(67).build();
        given(starService.getStarById(starId)).willReturn(Optional.empty());
        given(starService.updateStar(any(Star.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));
        //when
        ResultActions response = mockMvc.perform(put("/api/v1/stars/{id}", starId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedStar)));
        //then
        response.andExpect(status().isNotFound())
                .andDo(print());
    }

    @DisplayName("Controller JUnit test for deleteStar method")
    @Test
    public void givenStarId_whenDeleteStar_thenReturn200() throws Exception {
        //given
        Long starId = 1L;
        Star savedStar = Star.builder().name("STAR_122").distance(32).build();
        given(starService.getStarById(starId)).willReturn(Optional.of(savedStar));
        willDoNothing().given(starService).deleteStar(starId);
        //when
        ResultActions response = mockMvc.perform(delete("/api/v1/stars/{id}", starId));
        //then
        response.andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("Controller JUnit test for areNamesUnique method")
    @Test
    public void givenListOfStars_whenAreNamesUnique_thenReturnTrueOrFalse() throws Exception {
        //given
        List<Star> list = new ArrayList<>(List.copyOf(prepirMockData()));
        boolean b = starService.areNamesUnique(list);
        //when
        ResultActions response = mockMvc.perform(get("/api/v1/stars/unique"));
        //then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().string(String.valueOf(b)));

        list.add(new Star("STAR_1", 1));
        boolean c = starService.areNamesUnique(list);
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().string(String.valueOf(c)));

    }

    @DisplayName("Controller JUnit test findClosestStars for method")
    @Test
    public void givenListOfStars_whenFindClosestStars_thenReturnStarsListWithSizeIndicated() throws Exception {
        //given
        int size = 3;
        List<Star> list = List.copyOf(prepirMockData());
        List<Star> closestStars = starService.findClosestStars(list, size);
        given(starService.findClosestStars(list, size)).willReturn(closestStars);
        //when
        ResultActions response = mockMvc.perform(get("/api/v1/stars/closest/{size}", size));
        //then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()",
                        is(closestStars.size())));

    }

    @DisplayName("Controller JUnit test filterByRegExpr for method")
    @Test
    public void givenStarsList_whenFilterByRegExpr_thenReturnStarsListMatchedToRegex() throws Exception {
        //given
        String regex = "[A-Z]+_[0-9]{1}";
        List<Star> list = List.copyOf(prepirMockData());
        List<Star> filterByRegExpr = starService.filterByRegExpr(list, regex);
        given(starService.filterByRegExpr(list, regex)).willReturn(filterByRegExpr);
        //when
        ResultActions response = mockMvc.perform(get("/api/v1/stars/regex").param("regex", regex));
        //then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()",
                        is(filterByRegExpr.size())));
    }



}