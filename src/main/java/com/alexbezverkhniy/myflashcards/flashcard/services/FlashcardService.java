package com.alexbezverkhniy.myflashcards.flashcard.services;

import com.alexbezverkhniy.myflashcards.common.BaseService;
import com.alexbezverkhniy.myflashcards.flashcard.models.Flashcard;
import com.alexbezverkhniy.myflashcards.flashcard.repositories.FlashcardRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class FlashcardService extends BaseService<Flashcard, FlashcardRepository> {
    public FlashcardService() {
        this.entityType = Flashcard.class;
    }

    public Flashcard getRandom() {
        final List<Flashcard> res = repository.find("SELECT o FROM Flashcard o ORDER BY RAND()").list();
        return (res != null && !res.isEmpty()) ? res.get(0) : null;
    }
}
