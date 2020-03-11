package com.alexbezverkhniy.myflashcards.flashcard.repositories;

import com.alexbezverkhniy.myflashcards.flashcard.models.Flashcard;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FlashcardRepository implements PanacheRepository<Flashcard> {
}
