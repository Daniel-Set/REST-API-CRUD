package pl.setlikD.restapi.star;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StarServiceTests {

    @Mock
    private StarRepository starRepository;
    @InjectMocks
    private StarService starService;
    private Star star;

    private List<Star> prepirMockData() {
        List<Integer> numbers = IntStream.range(1, 10000)
                .boxed()
                .collect(Collectors.toList());

        Collections.shuffle(numbers);
        return numbers.stream()
                .map(i -> new Star("STAR_" + i, i))
                .collect(Collectors.toList());
    }

    @DisplayName("JUnit test for createStar method")
    @Test
    public void givenStarObj_whenSaveStar_thenReturnStarObject() {
        // given
        star = Star.builder().id(112L).name("STAR_121").distance(22).build();
        given(starRepository.findByNameAndDistance(star.getName(), star.getDistance()))
                .willReturn(Optional.empty());
        given(starRepository.save(star)).willReturn(star);
        //when
        Star savedStar = starService.createStar(star);
        //then
        assertThat(savedStar).isNotNull();
    }

    @DisplayName("JUnit test for createStar method which throws exception")
    @Test
    public void givenExistingStar_whenSaveStar_thenThrowsException() {
        //given
        star = Star.builder().id(667L).name("STAR_171").distance(202).build();
        given(starRepository.findByNameAndDistance(star.getName(), star.getDistance()))
                .willReturn(Optional.of(star));
        //when
        org.junit.jupiter.api.Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            starService.createStar(star);
        });
        //then
        verify(starRepository, never()).save(any(Star.class));
    }


    @DisplayName("JUnit test for getAllStars method")
    @Test
    public void givenStarsList_whenGetAllStars_thenReturnStarsList() {
        //given
        given(starRepository.findAll()).willReturn(prepirMockData());
        //when
        List<Star> starsList = starRepository.findAll();
        //then
        assertThat(starsList).isNotNull();
        assertThat(starsList.size()).isEqualTo(9999);
    }

    @DisplayName("JUnit test for getAllStars method (negative scenario)")
    @Test
    public void givenEmptyStarsList_whenGetAllStars_thenReturnEmptyStarsList() {
        //given
        given(starRepository.findAll()).willReturn(Collections.emptyList());
        //when
        List<Star> employeeList = starService.getAllStarts();
        //then
        assertThat(employeeList).isEmpty();
        assertThat(employeeList.size()).isEqualTo(0);
    }

    @DisplayName("JUnit test for getStarById method")
    @Test
    public void givenStarId_whenGetStarById_thenReturnStarObject() {
        //given
        star = Star.builder().id(100L).name("STAR_100").distance(42).build();
        given(starRepository.findById(100L)).willReturn(Optional.of(star));
        //when
        Star getStar = starService.getStarById(star.getId()).get();
        //then
        assertThat(getStar).isNotNull();
    }

    @DisplayName("JUnit test for updateStar method")
    @Test
    public void givenStarObj_whenUpdateStar_thenReturnUpdatedStar() {
        //given
        star = Star.builder().id(10L).name("STAR_1").distance(2).build();
        given(starRepository.save(star)).willReturn(star);
        star.setName("StarXYZ");
        star.setDistance(12);
        //when
        Star updatedStar = starService.updateStar(star);
        //then
        assertThat(updatedStar.getName()).isEqualTo("StarXYZ");
        assertThat(updatedStar.getDistance()).isEqualTo(12);
    }

    @DisplayName("JUnit test for deleteStar method")
    @Test
    public void givenStarId_whenDeleteStar_thenNothing() {
        //given
        long starId = 1L;
        willDoNothing().given(starRepository).deleteById(starId);
        //when
        starService.deleteStar(starId);
        //then
        verify(starRepository, times(1)).deleteById(starId);
    }

    @DisplayName("JUnit test for areNamesUnique method")
    @Test
    public void givenStarsList_whenAreNamesUnique_thenReturnTrueOrFalse() throws IOException {
        //given
        given(starRepository.findAll()).willReturn(prepirMockData());
        List<Star> starsList = starRepository.findAll();
        //when+then
        assertThat(starService.areNamesUnique(starsList)).isEqualTo(true);
        starsList.add(new Star("STAR_100", 100));
        assertThat(starService.areNamesUnique(starsList)).isEqualTo(false);
    }

    @DisplayName("JUnit test for findClosestStars method")
    @Test
    public void givenStarsList_whenFindClosestStars_thenReturnStarsListWithSizeIndicated() throws IOException {
        //given
        given(starRepository.findAll()).willReturn(prepirMockData());
        List<Star> starsList = starRepository.findAll();
        //when
        List<Star> closestStars = starService.findClosestStars(starsList, 3);
        //then
        assertThat(closestStars.size()).isEqualTo(3);
        assertThat(new Star("STAR_1", 1)).isEqualTo(closestStars.get(0));
        assertThat(new Star("STAR_2", 2)).isEqualTo(closestStars.get(1));
        assertThat(new Star("STAR_3", 3)).isEqualTo(closestStars.get(2));
    }

    @DisplayName("JUnit test for filterByRegExpr method")
    @Test
    public void givenStarsList_whenFilterByRegExpr_thenReturnStarsListMatchedToRegex() {
        //given
        given(starRepository.findAll()).willReturn(prepirMockData());
        List<Star> starsList = starRepository.findAll();
        //when
        List<Star> filteredStars = starService.filterByRegExpr(starsList, "[A-Z]+_[0-9]{1}");
        //then
        assertEquals(9, filteredStars.size());
        filteredStars.forEach(s -> assertTrue(s.getDistance() < 10));
        filteredStars = starService.filterByRegExpr(starsList, "[A-Z]+_1[0-9]{1}");
        assertThat(filteredStars.size()).isEqualTo(10);
        filteredStars.forEach(s -> assertTrue(s.getDistance() > 9 && s.getDistance() < 20));
    }

    @DisplayName("JUnit test for listValidator method which throws exception")
    @Test
    public void givenStarsList_whenStarsListIsEmptyORNull_thenExceptionShouldBeThrown() {
        //given
        given(starRepository.findAll()).willReturn(Collections.emptyList());
        List<Star> starsList = starRepository.findAll();
        //when+then
        assertThrows(ResourceNotFoundException.class, () -> StarService.listValidator(starsList));
        assertThrows(ResourceNotFoundException.class, () -> StarService.listValidator(null));
    }


}