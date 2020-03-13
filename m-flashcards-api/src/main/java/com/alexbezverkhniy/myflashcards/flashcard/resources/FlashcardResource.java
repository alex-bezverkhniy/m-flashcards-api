package com.alexbezverkhniy.myflashcards.flashcard.resources;

import com.alexbezverkhniy.myflashcards.flashcard.models.Flashcard;
import com.alexbezverkhniy.myflashcards.flashcard.services.FlashcardService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/api/flashcard")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class FlashcardResource {
    @Inject
    private FlashcardService flashcardService;

    private Flashcard flashcard;

    @GET
    public List<Flashcard> list() {
        return flashcardService.findAll();
    }

    @GET
    @Path("/random")
    public Flashcard getRandom() {
        return flashcardService.getRandom();
    }

    @POST
    public Flashcard add(final Flashcard flashcard) {
        return flashcardService.create(flashcard);
    }

    @PUT
    @Path("{id}")
    public Flashcard update(@PathParam("id") final Long id, final Flashcard flashcard) {
        return flashcardService.update(id, flashcard);
    }

    @GET
    @Path("{id}")
    public Flashcard getById(@PathParam("id") final Long id) {
        return flashcardService.findById(id);
    }

    @DELETE
    @Path("{id}")
    public void delete(@PathParam("id") final Long id) {
        flashcardService.delete(id);
    }
}
