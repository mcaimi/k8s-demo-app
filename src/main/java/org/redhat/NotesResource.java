package org.redhat;

import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.redhat.exceptions.MalformedNoteException;
import org.redhat.exceptions.NoteExistsException;
import org.redhat.exceptions.NoteNotExistsException;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;

@Path("/notes")
public class NotesResource {

    @Inject
    MeterRegistry registry;

    @Inject
    NotesService noteService;

    private static final Logger LOGGER = Logger.getLogger(NotesResource.class.getName());

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllNotes() {
        registry.counter("notes.resource.getallnotes.call.count").increment();

        List<Note> results;
        
        try {
            LOGGER.info("Getting all Notes from the database.");
            results = noteService.getAll();
        } catch(MalformedNoteException e) {
            return Response.status(500).build();
        }
        
        return Response.ok(results).status(200).build();
    }

    @GET
    @Path("/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam Long itemId) {
        registry.counter("notes.resource.getbyid.call.count", Tags.of("itemid", String.valueOf(itemId))).increment();

        LOGGER.info("Getting note with id [" + itemId + "] from the database.");
        Note foundNote = noteService.getNoteById(itemId);
        if (foundNote == null) {
            return Response.status(404).build();
        }
        return Response.ok(noteService.getNoteById(itemId)).status(200).build();
    }

    @POST
    @Path("/publish")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response publishNote(Note newNote) {
        registry.counter("notes.resource.publishnote.call.count").increment();

        if (newNote.getId() != null) {
            LOGGER.error("Got a non null ID, this note is malformed.");
            return Response.status(500).build();
        }
        
        Note inserted;

        try {
            LOGGER.info("Publishing new note: " + newNote.getName());
            inserted = noteService.publishNote(newNote);
        } catch (NoteExistsException e) {
            registry.counter("notes.resource.publishnote.exists.error.count").increment();
            LOGGER.error("Publish failed for note: " + newNote.getName() + "NOTE EXISTS.");
            return Response.status(500).build();
        } catch (MalformedNoteException e) {
            registry.counter("notes.resource.publishnote.malformed.error.count").increment();
            LOGGER.error("Publish failed for note: " + newNote.getName());
            return Response.status(500).build();
        }
        
        registry.counter("notes.resource.publishnote.call.ok.count").increment();
        return Response.ok(inserted.getId()).status(200).build();
    }

    @PUT
    @Path("/{itemId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateNote(@PathParam Long itemId, Note updatedNote) {
        registry.counter("notes.resource.updatenote.call.count").increment();
        if ((updatedNote.getContents() == null) || (updatedNote.getName() == null)) {
            registry.counter("notes.resource.updatenote.id.error.count", Tags.of("itemid", String.valueOf(itemId))).increment();
            LOGGER.error("Missing updated Name or Contents, this note is malformed.");
            return Response.status(500).build();
        }
        
        Note updated = noteService.updateNote(itemId, updatedNote);
        LOGGER.info("Updated note: " + updated.getId());

        registry.counter("notes.resource.updatenote.id.ok.count", Tags.of("itemid", String.valueOf(itemId))).increment();
        return Response.ok(updated.getId()).status(200).build();
    }

    @DELETE
    @Path("/{itemId}")
    public Response deleteNote(@PathParam Long itemId) {
        registry.counter("notes.resource.deletenote.call.count").increment();
        try {
            LOGGER.info("Deleting note: " + itemId);
            noteService.deleteNoteById(itemId);
        } catch(MalformedNoteException e) {
            registry.counter("notes.resource.deletenote.id.malformed.error.count", Tags.of("itemid", String.valueOf(itemId))).increment();
            LOGGER.error("Delete failed for note: " + itemId);
            return Response.status(500).build();
        } catch (NoteNotExistsException e) {
            registry.counter("notes.resource.deletenote.id.nonexisiting.id.count", Tags.of("itemid", String.valueOf(itemId))).increment();
            LOGGER.warn("Delete non-existing note failed for note: " + itemId);
            return Response.status(404).build();
        }

        return Response.ok(itemId).status(200).build();
    }
}