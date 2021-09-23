package com.example;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.Relation;
import io.micronaut.data.annotation.Relation.Kind;

@MappedEntity
public record Dog(@Id @GeneratedValue Long id,
                  String name,
                  @Nullable @Relation(Kind.MANY_TO_ONE) Collar collar) {
}

