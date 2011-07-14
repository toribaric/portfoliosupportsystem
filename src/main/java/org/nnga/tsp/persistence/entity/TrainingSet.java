package org.nnga.tsp.persistence.entity;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TrainingSet")
public class TrainingSet implements PersistenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
    @Column(name = "Name")
    private String name;
    @Column(name = "NumInputs")
    private int numInputs;
    @Column(name = "NumOutputs")
    private int numOutputs;

    @OneToMany(mappedBy = "trainingSet", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    @OrderBy("id ASC")
    @Fetch(FetchMode.SUBSELECT)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private List<TrainingSetIO> trainingSetIOs;

    public TrainingSet() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumInputs() {
        return numInputs;
    }

    public void setNumInputs(int numInputs) {
        this.numInputs = numInputs;
    }

    public int getNumOutputs() {
        return numOutputs;
    }

    public void setNumOutputs(int numOutputs) {
        this.numOutputs = numOutputs;
    }

    public List<TrainingSetIO> getTrainingSetIOs() {
        return trainingSetIOs;
    }

    public void setTrainingSetIOs(List<TrainingSetIO> trainingSetIOs) {
        for( TrainingSetIO trainingSetIO : trainingSetIOs ) {
            trainingSetIO.setTrainingSet(this);
        }
        this.trainingSetIOs = trainingSetIOs;
    }

    public void addTrainingSetIO(TrainingSetIO trainingSetIO) {
        trainingSetIO.setTrainingSet(this);
        trainingSetIOs.add(trainingSetIO);
    }
}
