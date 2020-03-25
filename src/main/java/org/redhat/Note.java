package org.redhat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "notes")
@NamedQuery(name = "Notes.AllNotes", query = "SELECT n FROM Note n ORDER BY n.id")
public class Note {
    @Id 
    @SequenceGenerator(name = "noteSeq", sequenceName = "notes_id_squential", allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="noteSeq")
    private Long id;

    @Column(unique = false)
    private String contents;

    @Column(unique = false)
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String newContent) {
        this.contents = newContent;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        this.name = newName;
    }
}

