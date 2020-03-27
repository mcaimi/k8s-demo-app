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

@Path("/notes")
public class NotesResource {
    @Inject
    NotesService noteService;

    private static final Logger LOGGER = Logger.getLogger(NotesResource.class.getName());

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Note> getAllNotes() {
        List<Note> results;
        
        try {
            LOGGER.info("Getting all Notes from the database.");
            results = noteService.getAll();
        } catch(Exception e) {
            results = null;
        }
        
        return results;
    }

    @GET
    @Path("/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Note getById(@PathParam Long itemId) {
        LOGGER.info("Getting note with id [" + itemId + "] from the database.");
        return noteService.getNoteById(itemId);
    }

    @POST
    @Path("/publish")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response publishNote(Note newNote) {
        if (newNote.getId() != null) {
            LOGGER.error("Got a non null ID, this note is malformed.");
            return Response.status(500).build();
        }

        try {
            LOGGER.info("Publishing new note: " + newNote.getName());
            noteService.publishNote(newNote);
        } catch (Exception e) {
            LOGGER.error("Publish failed for note: " + newNote.getName());
            return Response.status(500).build();
        }
 
        return Response.status(200).build();
    }

    @PUT
    @Path("/{itemId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateNote(@PathParam Long itemId, Note updatedNote) {
        if ((updatedNote.getContents() == null) || (updatedNote.getName() == null)) {
            LOGGER.error("Missing updated Name or Contents, this note is malformed.");
            return Response.status(500).build();
        }
        
        Note updated = noteService.updateNote(itemId, updatedNote);
        LOGGER.info("Updated note: " + updated.getId());

        return Response.ok(updated.getId()).status(200).build();
    }

    @DELETE
    @Path("/{itemId}")
    public Response deleteNote(@PathParam Long itemId) {
        try {
            LOGGER.info("Deleting note: " + itemId);
            noteService.deleteNoteById(itemId);
        } catch(Exception e) {
            LOGGER.error("Delete failed for note: " + itemId);
            return Response.status(500).build();
        }

        return Response.status(200).build();
    }
}