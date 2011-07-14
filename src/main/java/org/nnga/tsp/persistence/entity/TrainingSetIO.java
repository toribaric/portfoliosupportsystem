package org.nnga.tsp.persistence.entity;

import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "TrainingSetIO")
public class TrainingSetIO implements PersistenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
    @Column(name = "Inputs")
    private String inputs;
    @Column(name = "Outputs")
    private String outputs;

    @ManyToOne
    @JoinColumn(name = "TrainingSetId")
    private TrainingSet trainingSet;

    public TrainingSetIO() {

    }

    @JsonIgnore
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInputs() {
        return inputs;
    }

    public void setInputs(String inputs) {
        this.inputs = inputs;
    }

    public String getOutputs() {
        return outputs;
    }

    public void setOutputs(String outputs) {
        this.outputs = outputs;
    }

    @JsonIgnore
    public TrainingSet getTrainingSet() {
        return trainingSet;
    }

    public void setTrainingSet(TrainingSet trainingSet) {
        this.trainingSet = trainingSet;
    }
}
