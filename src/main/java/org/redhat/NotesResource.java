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
import org.jboss.resteasy.annotations.jaxrs.PathParam;

@Path("/notes")
public class NotesResource {
    @Inject
    NotesService noteService;

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Note> getAllNotes() {
        List<Note> results;
        
        try {
            results = noteService.getAll();
        } catch(Exception e) {
            results = null;
        }
        
        return results;
    }

    @GET
    @Path("/{item_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Note getById(@PathParam Long item_id) {
        return noteService.getNoteById(item_id);
    }

    @POST
    @Path("/publish")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response publishNote(Note newNote) {
        if (newNote.getId() != null) {
            return Response.status(500).build();
        }

        try {
            noteService.publishNote(newNote);
        } catch (Exception e) {
            return Response.status(500).build();
        }
 
        return Response.status(200).build();
    }

    @PUT
    @Path("/{item_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateNote(@PathParam Long item_id, Note updatedNote) {
        if ((updatedNote.getContents() == null) || (updatedNote.getName() == null)) {
            return Response.status(500).build();
        }
        
        Note updated = noteService.updateNote(item_id, updatedNote);

        return Response.ok(updated.getId()).status(200).build();
    }

    @DELETE
    @Path("/{item_id}")
    public Response deleteNote(@PathParam Long item_id) {
        try {
            noteService.deleteNoteById(item_id);
        } catch(Exception e) {
            return Response.status(500).build();
        }

        return Response.status(200).build();
    }
}