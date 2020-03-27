package org.redhat;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.redhat.exceptions.NoteExistsException;
import org.redhat.exceptions.MalformedNoteException;

@ApplicationScoped
public class NotesService {
    @Inject
    EntityManager em;

    @Transactional
    public void publishNote(Note newNote) throws NoteExistsException, MalformedNoteException {
        try {
            createNoteWithData(newNote.getName(), newNote.getContents());
        } catch (NoteExistsException e) {
            throw new NoteExistsException("Called method createNoteWithData() throwed exception:" + e.getMessage());
        } catch (MalformedNoteException e) {
            throw new MalformedNoteException("Called method createNoteWithData() throwed exception:" + e.getMessage());
        }
    }

    @Transactional 
    public void createNoteWithData(String noteName, String noteContents) throws NoteExistsException, MalformedNoteException {
        try {
            Note newNote = new Note();
            newNote.setName(noteName);
            newNote.setContents(noteContents);
            em.persist(newNote);
        } catch (EntityExistsException e) {
            throw new NoteExistsException("Note exists on the database." + e.getMessage());
        } catch(IllegalArgumentException e) {
            throw new MalformedNoteException("NoteService.createNote(): Illegal argument received." + e.getMessage());
        }
    }

    @Transactional
    public void deleteNote(Note item) throws NoteExistsException, MalformedNoteException {
        try {
            em.remove(item);
        } catch (EntityExistsException e) {
            throw new NoteExistsException("Note exists on the database." + e.getMessage());
        } catch(IllegalArgumentException e) {
            throw new MalformedNoteException("NoteService.deleteNote(): Illegal argument received." + e.getMessage());
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
    public void deleteNoteById(Long id) throws NoteExistsException, MalformedNoteException {
        Note fetched = getNoteById(id);
        if (fetched != null) {
            try {
                deleteNote(fetched);
            } catch (NoteExistsException e) {
                throw new NoteExistsException("Called method deleteNote() throwed exception:" + e.getMessage());
            } catch (MalformedNoteException e) {
                throw new MalformedNoteException("Called method deleteNote() throwed exception:" + e.getMessage());
            }
        }
        return;
    }

    public List<Note> getAll() throws MalformedNoteException {
        try {
            return em.createNamedQuery("Notes.AllNotes", Note.class).getResultList();
        } catch (IllegalArgumentException e) {
            throw new MalformedNoteException("NoteService.getAll(): Illegal argument received." + e.getMessage());
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