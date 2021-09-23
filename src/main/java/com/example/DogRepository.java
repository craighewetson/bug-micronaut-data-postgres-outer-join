package com.example;

import io.micronaut.data.annotation.Join;
import io.micronaut.data.annotation.Join.Type;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.jdbc.runtime.JdbcOperations;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import jakarta.inject.Inject;

import java.sql.ResultSet;
import java.util.Optional;

@JdbcRepository(dialect = Dialect.POSTGRES)
public abstract class DogRepository implements CrudRepository<Dog, Long> {

    @Inject
    JdbcOperations jdbcOperations;

    @Join("collar")
    public abstract Optional<Dog> findById(Long id);

    public Optional<Dog> explicitFindById(Long id) {
        String sql = """
        select dog_."id" as id, dog_."name" as name, dog_."collar_id" as collar_id, dog_collar_."description" AS collar_description 
        FROM "dog" dog_ FULL OUTER JOIN "collar" dog_collar_ ON dog_."collar_id"=dog_collar_."id" WHERE (dog_."id" = ?);
        """;
        return jdbcOperations.prepareStatement(sql, statement -> {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            Dog dog = null;
            if (resultSet.next()) {
                Long dogId = resultSet.getLong("id");
                String name = resultSet.getString("name");

                Long collarId = (Long)resultSet.getObject("collar_id");
                Collar collar = null;
                if(collarId != null) {
                    String collarDescription = resultSet.getString("collar_description");
                    collar = new Collar(collarId, collarDescription);
                }

                dog = new Dog(dogId, name, collar);
            }
            return Optional.ofNullable(dog);
        });
    }

}
