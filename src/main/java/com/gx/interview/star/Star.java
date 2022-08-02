package com.gx.interview.star;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

/**
 * A star object represnts distance of the star from the Sun.
 */
@Entity
@Getter
@Setter
@Builder
public class Star {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private long distance;

    public Star(String name, long distance) {
        this.name = name;
        this.distance = distance;
    }

    public Star(Long id, String name, long distance) {
        this.id = id;
        this.name = name;
        this.distance = distance;
    }

    public Star() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Star star = (Star) o;
        return name.equals(star.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, distance);
    }

    @Override
    public String toString() {
        return "Star{" +
                "name='" + name + '\'' +
                ", distance=" + distance +
                '}';
    }
}
