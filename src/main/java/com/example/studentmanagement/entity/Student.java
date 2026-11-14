package com.example.studentmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String nom;

    @NotBlank @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String prenom;

    @NotNull @Past
    @Column(nullable = false)
    private LocalDate dateNaissance;

    @NotBlank @Size(max = 30)
    @Column(nullable = false, unique = true, length = 30)
    private String matricule;

    @NotBlank @Email @Size(max = 150)
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String filiere;

    @NotNull @Min(1) @Max(5)
    @Column(nullable = false)
    private Integer niveau;

    @Pattern(regexp = "^$|[+]?[0-9]{10,15}", message = "doit contenir 10 à 15 chiffres, avec un + facultatif")
    @Column(length = 16)
    private String telephone;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }
    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFiliere() { return filiere; }
    public void setFiliere(String filiere) { this.filiere = filiere; }
    public Integer getNiveau() { return niveau; }
    public void setNiveau(Integer niveau) { this.niveau = niveau; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
}
