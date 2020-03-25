package org.redhat;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@ApplicationScoped
public class NotesService {
    @Inject
    EntityManager em;

    @Transactional
    public void publishNote(Note newNote) throws Exception {
        try {
            createNoteWithData(newNote.getName(), newNote.getContents());
        } catch (Exception e) {
            throw e;
        }
    }

    @Transactional 
    public void createNoteWithData(String noteName, String noteContents) throws Exception {
        try {
            Note newNote = new Note();
            newNote.setName(noteName);
            newNote.setContents(noteContents);
            em.persist(newNote);
        } catch (EntityExistsException e) {
            return;
        } catch(IllegalArgumentException e) {
            throw new Exception("NoteService.createNote(): Illegal argument received." + e.getMessage());
        }
    }

    @Transactional
    public void deleteNote(Note item) throws Exception {
        try {
            em.remove(item);
        } catch (EntityExistsException e) {
            return;
        } catch(IllegalArgumentException e) {
            throw new Exception("NoteService.createNote(): Illegal argument received." + e.getMessage());
        }
    }

    @Transactional
    public Note updateNote(Long id, Note updatedNote) {
        Note persistedNote = getNoteById(id);
        if (persistedNote == null) {
            return null;
        }
        persistedNote.setContents(updatedNote.getContents());
        persistedNote.setName(updatedNote.getName());

        return persistedNote;
    }

    @Transactional
    public void deleteNoteById(Long id) {
        Note fetched = getNoteById(id);
        if (fetched != null) {
            try {
                deleteNote(fetched);
            } catch (Exception e) {
                return;
            }
        }
        return;
    }

    public List<Note> getAll() throws Exception {
        try {
            return em.createNamedQuery("Notes.AllNotes", Note.class).getResultList();
        } catch (IllegalArgumentException e) {
            throw new Exception("NoteService.getAll(): Illegal argument received." + e.getMessage());
        }
    }

    public Note getNoteById(Long id) {
        Note result = em.find(Note.class, id);
        if (result != null) {
            return result;
        }
        return null;
    }
}