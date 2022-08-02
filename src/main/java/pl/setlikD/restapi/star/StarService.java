package pl.setlikD.restapi.star;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A class to work with {@link Star} collections.
 */
@Service
class StarService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StarService.class.getName());
    private final StarRepository starRepository;

    @Autowired
    public StarService(StarRepository starRepository) {
        this.starRepository = starRepository;
    }

    public List<Star> getAllStarts() {
        LOGGER.debug("Get all stars");
        return starRepository.findAll();
    }

    public Optional<Star> getStarById(Long id) {
        LOGGER.debug("Get Star by id:" + id);
        return starRepository.findById(id);
    }

    @Transactional
    public Star createStar(Star star) {
        LOGGER.info("Creating Star");
        LOGGER.debug("Model Star:" + star);
        Optional<Star> savedEmployee = starRepository.findByNameAndDistance(star.getName(), star.getDistance());
        if (savedEmployee.isPresent()) {
            throw new ResourceNotFoundException(String.format("Star already exist with given name:%s and distance:%s", star.getName(), star.getDistance()));
        }
        return starRepository.save(star);
    }

    public Star updateStar(Star star) {
        LOGGER.info("Update Star");
        LOGGER.debug("Model Star:" + star);
        return starRepository.save(star);
    }

    public void deleteStar(Long id) {
        LOGGER.info("Delete Star of if:" + id);
        starRepository.deleteById(id);
        LOGGER.info("Star deleted");
    }


    /**
     * The method accepts a collection of {@link Star} and checks if all Star's names are unique.
     *
     * @param stars unsorted collection of {@link Star} objects
     * @return true if there are no duplicates, otherwise false.
     * @throws IOException
     */
    public boolean areNamesUnique(List<Star> stars) throws IOException {
        LOGGER.info("Searching for unique star");
        LOGGER.debug("List size:" + stars.size());
        listValidator(stars);
        for (Star star : stars) {
            if (!(Collections.frequency(stars, star) == 1)) {
                return false;
            }
        }
        return true;
    }

    /**
     * The method searches and returns the Stars closest to the Sun.
     *
     * @param stars unsorted collection of {@link Star} objects
     * @param size  number of Stars to return
     * @return collection of {@link Star} objects
     * @throws IOException
     */
    public List<Star> findClosestStars(List<Star> stars, int size) throws IOException {
        LOGGER.info("Finding closest star");
        LOGGER.debug("Size List to return:" + size);
        listValidator(stars);
        if (size <= 0) {
            throw new ResourceNotFoundException("The size should be greater than 0");
        } else if (stars.size() < size) {
            size = stars.size();
        }
        return stars.stream().sorted(Comparator.comparingLong(Star::getDistance)).collect(Collectors.toList()).subList(0, size);
    }

    /**
     * It filters out the Stars with the name not matching the regular expression.
     *
     * @param stars   unsorted collection of {@link Star} objects
     * @param regExpr regular expression to match stars' names
     * @return collection of {@link Star} with the name matching the regular expression
     */
    public List<Star> filterByRegExpr(List<Star> stars, String regExpr) {
        LOGGER.info("Filtering List of Stars by regex");
        LOGGER.debug("Regex:" + regExpr);
        listValidator(stars);
        return stars.stream().filter(s -> s.getName().matches(regExpr)).collect(Collectors.toList());
    }

    protected static void listValidator(List<Star> stars) {
        if (stars == null || stars.isEmpty()) {
            throw new ResourceNotFoundException("Not implemented");
        }

    }


}
