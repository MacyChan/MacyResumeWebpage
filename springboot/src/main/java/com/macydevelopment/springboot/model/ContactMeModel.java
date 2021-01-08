package com.macydevelopment.springboot.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "main_contact_me")
public class ContactMeModel extends AuditModel {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "contactMe_generator")
    @SequenceGenerator(
            name = "contactMe_generator",
            sequenceName = "contactMe_sequence",
            initialValue = 1000
    )
    private Long id;

    @NotBlank
    @Size(min = 1, max = 100)
    private String name;

    @Column(columnDefinition = "text")
    private String email;

    @Column(columnDefinition = "text")
    private String phone;

    @Column(columnDefinition = "text")
    private String message;

    @Column(columnDefinition = "text")
    private String read;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRead() {
        return this.read;
    }

    public void setRead(String read) {
        this.read = read;
    }

}