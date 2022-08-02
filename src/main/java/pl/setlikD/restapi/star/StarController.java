package pl.setlikD.restapi.star;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
class StarController {

    public static final Long EMPTY_ID = null;
    private final StarService starService;

    @Autowired
    StarController(StarService starService) {
        this.starService = starService;
    }

    @GetMapping("/stars")
    public List<Star> getStars() {
        return starService.getAllStarts();
    }

    @GetMapping("/stars/{id}")
    public ResponseEntity<Star> getStarById(@PathVariable("id") Long id) {
        return starService.getStarById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/stars")
    @ResponseStatus(HttpStatus.CREATED)
    public Star createStar(@RequestBody StarDto starDto) {
        return starService.createStar(new Star(EMPTY_ID, starDto.getName(), starDto.getDistance()));
    }

    @PutMapping("/stars/{id}")
    public ResponseEntity<Star> updateStar(@PathVariable("id") Long id, @RequestBody StarDto starDto) {
        return starService.getStarById(id).map(savedStar -> {
            savedStar.setName(starDto.getName());
            savedStar.setDistance(starDto.getDistance());
            Star updatedStar = starService.updateStar(savedStar);
            return new ResponseEntity<>(updatedStar, HttpStatus.OK);

        }).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @DeleteMapping("/stars/{id}")
    public ResponseEntity<String> deleteStar(@PathVariable("id") Long id) {
        Optional<Star> star = Optional.ofNullable(starService.getStarById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
        starService.deleteStar(star.get().getId());
        return new ResponseEntity<>("Star deleted successfully!.", HttpStatus.OK);
    }

    @GetMapping("/stars/unique")
    public boolean areNamesUnique() throws IOException {
        return starService.areNamesUnique(starService.getAllStarts());
    }

    @GetMapping("/stars/closest/{size}")
    public List<Star> findClosestStars(@PathVariable("size") int size) throws IOException {
        return starService.findClosestStars(starService.getAllStarts(), size);
    }

    @GetMapping("/stars/regex")
    public List<Star> filterByRegExpr(@RequestParam("regex") String regex) {
        String replaceRegex = regex.replaceAll(" ", "+");
        return starService.filterByRegExpr(starService.getAllStarts(), replaceRegex);
    }


}
