package com.example;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import java.sql.Connection;

@MicronautTest
class OuterjoinTest {

    @Inject
    DogRepository dogRepository;

    @Inject
    CollarRepository collarRepository;

    @Inject
    Connection connection;

    @Test
    void testItWorks() throws Exception {
        Collar fleaCollar = collarRepository.save(new Collar(-1L, "flea collar"));
        Dog fido = dogRepository.save(new Dog(-1L, "fido", fleaCollar));
        connection.commit();

        // now confirm that the fido still has his collar:
        Dog fidoFromDb = dogRepository.findById(fido.id()).orElseThrow();
        Assertions.assertNotNull(fidoFromDb.collar());

        // now for dog without collar:
        Dog tramp = dogRepository.save(new Dog(-1L, "tramp", null));
        connection.commit();

        // if the outer join does not work then this query will fail.
        // Tramp must be retrieved without his collar
        Dog trampFromDb = dogRepository.findById(tramp.id()).orElseThrow();

        Assertions.assertNull(trampFromDb.collar());
    }

    @Test
    void testItExplicitQueryWorks() throws Exception {
        Collar fleaCollar = collarRepository.save(new Collar(-1L, "flea collar"));
        Dog fido = dogRepository.save(new Dog(-1L, "fido", fleaCollar));
        connection.commit();

        // now confirm that the fido still has his collar:
        Dog fidoFromDb = dogRepository.explicitFindById(fido.id()).orElseThrow();
        Assertions.assertNotNull(fidoFromDb.collar());

        // now for dog without collar:
        Dog tramp = dogRepository.save(new Dog(-1L, "tramp", null));
        connection.commit();

        // if the outer join does not work then this query will fail.
        // Tramp must be retrieved without his collar
        Dog trampFromDb = dogRepository.explicitFindById(tramp.id()).orElseThrow();

        Assertions.assertNull(trampFromDb.collar());
    }

}
