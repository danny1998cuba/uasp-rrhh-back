/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uasp.hhrr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Tapanes
 */
@Entity
@Table(name = "nivel_escolar")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NivelEscolar implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;

    @Basic(optional = false)
    @Column(name = "relevancia")
    private int relevancia;

    @Basic(optional = false)
    @Column(name = "abreviado")
    private String abreviado;

    @OneToMany(mappedBy = "idEscolar")
    @JsonIgnore
    private List<Trabajador> trabajadorList;

    @OneToMany(mappedBy = "idEscolarMin")
    @JsonIgnore
    private List<Cargo> cargoList;

    public NivelEscolar(Integer id) {
        this.id = id;
    }

}
